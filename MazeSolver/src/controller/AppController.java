package controller;

import algorithm.MazeGenerator;
import algorithm.MazeSolver;
import model.Cell;
import model.Maze;
import model.SolveResult;
import view.ui.Theme;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;

/**
 * AppController - The brain of the application. Connects all UI panels
 * with the model and algorithm layers.
 *
 * WHY THIS CLASS EXISTS:
 *   In MVC, the Controller handles all user actions. The UI panels call
 *   methods on this controller. This keeps UI code separate from logic.
 *
 * JAVA CONCEPTS USED: OOP, Composition, javax.swing.Timer, File I/O,
 *                     Exception Handling, ArrayList
 */
public class AppController {

    // ---- Core Data ----
    private Maze maze;
    private SolveResult lastResult;
    private List<int[]> animationSteps; // steps recorded by MazeSolver
    private int currentStepIndex;

    // ---- Animation ----
    private Timer animationTimer;
    private int animationDelay = 40; // milliseconds between steps

    // ---- Display Grid (what the visualizer actually draws) ----
    // This is a copy of the maze grid updated step-by-step during animation
    private Cell[][] displayGrid;

    // ---- Reference to the main window (to update panels) ----
    private view.AppWindow appWindow;

    public AppController() {
        maze = new Maze(Maze.DEFAULT_ROWS, Maze.DEFAULT_COLS);
        lastResult = new SolveResult();
    }

    /** Called once the AppWindow is ready, so we can update its panels. */
    public void setAppWindow(view.AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    // ================================================================
    //  MAZE EDITING
    // ================================================================

    /** Paints a single cell in the editor with the selected brush type. */
    public void paintCell(int row, int col, Cell cellType) {
        if (!maze.isInBounds(row, col)) return;
        // Don't paint over the very border walls
        if (row == 0 || row == maze.getRows() - 1 || col == 0 || col == maze.getCols() - 1) return;

        // If setting Start, clear old Start first
        if (cellType == Cell.START) {
            maze.setStart(row, col);
        } else if (cellType == Cell.END) {
            maze.setEnd(row, col);
        } else {
            maze.setCell(row, col, cellType);
        }
    }

    /** Clears all solver markings from the maze (keeps walls). */
    public void clearSolverMarkings() {
        maze.clearSolverMarkings();
    }

    /** Resets the entire maze to a blank slate. */
    public void resetMaze() {
        maze.resetAll();
        lastResult = new SolveResult();
        animationSteps = null;
        displayGrid = null;
        stopAnimation();
    }

    /** Resets only the visualizer state (keeps maze walls). */
    public void resetVisualization() {
        maze.clearSolverMarkings();
        lastResult = new SolveResult();
        animationSteps = null;
        displayGrid = null;
        stopAnimation();
    }

    /** Resizes the maze to a new grid size. */
    public void resizeMaze(int rows, int cols) {
        maze = new Maze(rows, cols);
        lastResult = new SolveResult();
        animationSteps = null;
        stopAnimation();
    }

    /** Generates a random perfect maze. */
    public void generateMaze() {
        MazeGenerator.generate(maze);
        lastResult = new SolveResult();
        animationSteps = null;
        stopAnimation();
    }

    // ================================================================
    //  SOLVING
    // ================================================================

    /**
     * Runs the DFS solver on the current maze.
     * Records all animation steps and computes statistics.
     * Returns true if a solution was found.
     */
    public boolean solveMaze() {
        // Clear old markings before solving
        maze.clearSolverMarkings();

        // Run solver on the maze
        MazeSolver solver = new MazeSolver();
        lastResult = solver.solve(maze);
        animationSteps = solver.getSteps();
        currentStepIndex = 0;

        // Prepare a clean display grid for animation
        initDisplayGrid();

        return lastResult.isSolved();
    }

    /** Instantly applies ALL steps to show the final solved state. */
    public void solveInstant() {
        if (!solveMaze()) return;
        // Apply every step at once
        for (int[] step : animationSteps) {
            applyStep(step);
        }
        currentStepIndex = animationSteps.size();
    }

    // ================================================================
    //  ANIMATION
    // ================================================================

    /** Prepares displayGrid as a fresh copy of the maze for animation. */
    private void initDisplayGrid() {
        int rows = maze.getRows();
        int cols = maze.getCols();
        displayGrid = new Cell[rows][cols];
        Cell[][] source = maze.getGrid();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                displayGrid[r][c] = source[r][c];
            }
        }
    }

    /** Starts the step-by-step animation using a Swing Timer. */
    public void startAnimation() {
        if (animationSteps == null || animationSteps.isEmpty()) return;
        stopAnimation();

        animationTimer = new Timer(animationDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStepIndex >= animationSteps.size()) {
                    stopAnimation();
                    if (appWindow != null) {
                        appWindow.getVisualizerPanel().onAnimationFinished();
                    }
                    return;
                }
                applyStep(animationSteps.get(currentStepIndex));
                currentStepIndex++;
                if (appWindow != null) {
                    appWindow.getVisualizerPanel().repaintGrid();
                    appWindow.getVisualizerPanel().updateStepLabel(currentStepIndex, animationSteps.size());
                }
            }
        });
        animationTimer.start();
    }

    /** Pauses the animation. */
    public void pauseAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    /** Stops and resets the animation. */
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
    }

    /** Moves one step forward manually. */
    public void stepForward() {
        if (animationSteps == null) return;
        if (currentStepIndex < animationSteps.size()) {
            applyStep(animationSteps.get(currentStepIndex));
            currentStepIndex++;
            if (appWindow != null) {
                appWindow.getVisualizerPanel().repaintGrid();
                appWindow.getVisualizerPanel().updateStepLabel(currentStepIndex, animationSteps.size());
            }
        }
    }

    /** Moves one step backward by rebuilding the displayGrid. */
    public void stepBackward() {
        if (animationSteps == null || currentStepIndex <= 0) return;
        currentStepIndex--;
        // Rebuild displayGrid from scratch up to currentStepIndex
        initDisplayGrid();
        for (int i = 0; i < currentStepIndex; i++) {
            applyStep(animationSteps.get(i));
        }
        if (appWindow != null) {
            appWindow.getVisualizerPanel().repaintGrid();
            appWindow.getVisualizerPanel().updateStepLabel(currentStepIndex, animationSteps.size());
        }
    }

    /** Applies a single animation step to the displayGrid. */
    private void applyStep(int[] step) {
        int row  = step[0];
        int col  = step[1];
        Cell cell = Cell.values()[step[2]];
        // Preserve START and END colors
        if (displayGrid[row][col] != Cell.START && displayGrid[row][col] != Cell.END) {
            displayGrid[row][col] = cell;
        }
    }

    /** Updates the animation speed (delay in ms between frames). */
    public void setAnimationDelay(int delayMs) {
        this.animationDelay = delayMs;
        if (animationTimer != null) {
            animationTimer.setDelay(delayMs);
        }
    }

    // ================================================================
    //  FILE OPERATIONS
    // ================================================================

    /** Saves the current maze to a plain-text file. */
    public void saveMaze(File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(maze.getRows() + " " + maze.getCols());
        writer.newLine();
        writer.write(maze.getStartRow() + " " + maze.getStartCol());
        writer.newLine();
        writer.write(maze.getEndRow() + " " + maze.getEndCol());
        writer.newLine();

        Cell[][] grid = maze.getGrid();
        for (int r = 0; r < maze.getRows(); r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < maze.getCols(); c++) {
                switch (grid[r][c]) {
                    case WALL:  sb.append('#'); break;
                    case START: sb.append('S'); break;
                    case END:   sb.append('E'); break;
                    default:    sb.append('.'); break;
                }
            }
            writer.write(sb.toString());
            writer.newLine();
        }
        writer.close();
    }

    /** Loads a maze from a plain-text file. */
    public void loadMaze(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String[] dims = reader.readLine().trim().split(" ");
        int rows = Integer.parseInt(dims[0]);
        int cols = Integer.parseInt(dims[1]);

        String[] startCoords = reader.readLine().trim().split(" ");
        int startRow = Integer.parseInt(startCoords[0]);
        int startCol = Integer.parseInt(startCoords[1]);

        String[] endCoords = reader.readLine().trim().split(" ");
        int endRow = Integer.parseInt(endCoords[0]);
        int endCol = Integer.parseInt(endCoords[1]);

        maze = new Maze(rows, cols);
        for (int r = 0; r < rows; r++) {
            String line = reader.readLine();
            for (int c = 0; c < cols; c++) {
                char ch = line.charAt(c);
                switch (ch) {
                    case '#': maze.setCell(r, c, Cell.WALL);  break;
                    case 'S': maze.setCell(r, c, Cell.START); break;
                    case 'E': maze.setCell(r, c, Cell.END);   break;
                    default:  maze.setCell(r, c, Cell.PATH);  break;
                }
            }
        }
        maze.setStart(startRow, startCol);
        maze.setEnd(endRow, endCol);
        reader.close();

        lastResult = new SolveResult();
        animationSteps = null;
        stopAnimation();
    }

    // ================================================================
    //  GETTERS
    // ================================================================

    public Maze getMaze()               { return maze; }
    public SolveResult getLastResult()  { return lastResult; }
    public Cell[][] getDisplayGrid()    { return displayGrid; }
    public List<int[]> getSteps()       { return animationSteps; }
    public int getCurrentStepIndex()    { return currentStepIndex; }
    public boolean isAnimating()        { return animationTimer != null && animationTimer.isRunning(); }
}
