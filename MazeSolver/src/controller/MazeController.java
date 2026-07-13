package controller;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.Timer;
import algorithm.BacktrackingSolver;
import algorithm.RandomMazeGenerator;
import algorithm.SolvingStep;
import model.CellType;
import model.InvalidMazeException;
import model.Maze;
import model.Position;
import model.SolveStatistics;
import utils.FileHandler;
import utils.ImageExporter;
import utils.SystemPrinter;
import view.Theme;

/**
 * Controller class that coordinates between the Maze models, the solving/generation algorithms,
 * and the visualizer/editor views. Manages undo/redo history and animation playbacks.
 */
public class MazeController {
    private Maze maze;
    private Maze baseMazeState; // Clone of maze before solving started
    private final BacktrackingSolver solver;
    private SolveStatistics lastSolveStats;
    private List<SolvingStep> solvingSteps;
    
    // Playback control
    private Timer playbackTimer;
    private int currentStepIndex = -1;
    private int animationDelayMs = 50; // Speed of step animation

    // Undo/Redo stacks for editor
    private final Stack<Maze> undoStack;
    private final Stack<Maze> redoStack;

    // View components references for repaint notifications
    private final List<Component> viewsToNotify;

    /**
     * Interface for listening to controller state changes.
     */
    public interface ControllerListener {
        void onStateChanged();
        void onAnimationStep(int stepIndex, int totalSteps, SolvingStep step);
        void onAnimationFinished();
    }

    private final List<ControllerListener> listeners;

    /**
     * Constructs a MazeController with a default maze grid size.
     */
    public MazeController() {
        this.maze = new Maze();
        this.solver = new BacktrackingSolver();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.viewsToNotify = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.solvingSteps = new ArrayList<>();
        this.lastSolveStats = new SolveStatistics();

        initializeTimer();
    }

    private void initializeTimer() {
        playbackTimer = new Timer(animationDelayMs, e -> {
            if (currentStepIndex < solvingSteps.size() - 1) {
                currentStepIndex++;
                SolvingStep step = solvingSteps.get(currentStepIndex);
                
                // Apply step to active maze
                maze.setCell(step.getPosition(), step.getNewType());
                
                notifyAnimationStep(currentStepIndex, solvingSteps.size(), step);
                notifyViews();
            } else {
                stopAnimation();
                notifyAnimationFinished();
            }
        });
    }

    public void registerView(Component view) {
        if (view != null && !viewsToNotify.contains(view)) {
            viewsToNotify.add(view);
        }
    }

    public void addListener(ControllerListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void notifyViews() {
        for (Component view : viewsToNotify) {
            view.repaint();
        }
        for (ControllerListener l : listeners) {
            l.onStateChanged();
        }
    }

    private void notifyAnimationStep(int index, int total, SolvingStep step) {
        for (ControllerListener l : listeners) {
            l.onAnimationStep(index, total, step);
        }
    }

    private void notifyAnimationFinished() {
        for (ControllerListener l : listeners) {
            l.onAnimationFinished();
        }
    }

    public Maze getMaze() {
        return maze;
    }

    public SolveStatistics getLastSolveStats() {
        return lastSolveStats;
    }

    public List<SolvingStep> getSolvingSteps() {
        return solvingSteps;
    }

    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    public boolean isAnimating() {
        return playbackTimer.isRunning();
    }

    // --- MAZE EDITING ACTIONS (WITH UNDO/REDO) ---

    /**
     * Saves the current maze state to the undo stack. Called before any modifications are applied.
     */
    public void saveStateForUndo() {
        undoStack.push(maze.clone());
        redoStack.clear(); // Clear redo on new action
    }

    /**
     * Set a cell type at a position, supporting undo records.
     */
    public void editCell(Position p, CellType type) {
        if (!maze.isWithinBounds(p)) return;
        
        // If placing same cell type, skip
        if (maze.getCell(p) == type) return;

        saveStateForUndo();
        maze.setCell(p, type);
        notifyViews();
    }

    /**
     * Undoes the last grid editor modification.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(maze.clone());
            maze = undoStack.pop();
            notifyViews();
        }
    }

    /**
     * Redoes the previously undone grid editor modification.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(maze.clone());
            maze = redoStack.pop();
            notifyViews();
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Resizes the active maze. Clears history.
     */
    public void resizeMaze(int rows, int cols) {
        saveStateForUndo();
        maze.setDimensions(rows, cols);
        maze.clearAll();
        undoStack.clear();
        redoStack.clear();
        stopAnimationAndReset();
        notifyViews();
    }

    /**
     * Resets the solver indicators on the grid while retaining start, end, and wall configurations.
     */
    public void clearSolverVisuals() {
        stopAnimation();
        maze.clearSolverTags();
        currentStepIndex = -1;
        solvingSteps.clear();
        notifyViews();
    }

    /**
     * Clears all walls and paths, resetting to empty default maze.
     */
    public void resetMaze() {
        saveStateForUndo();
        maze.clearAll();
        stopAnimationAndReset();
        notifyViews();
    }

    // --- ALGORITHM ACTIONS ---

    /**
     * Generates a noise-based maze structure.
     */
    public void generateRandomNoise(double density) {
        saveStateForUndo();
        stopAnimationAndReset();
        RandomMazeGenerator.generateNoise(maze, density);
        notifyViews();
    }

    /**
     * Generates a perfect maze using Randomized DFS.
     */
    public void generatePerfectMaze() {
        saveStateForUndo();
        stopAnimationAndReset();
        RandomMazeGenerator.generatePerfectMaze(maze);
        notifyViews();
    }

    /**
     * Computes the solve paths, setting up visualization parameters.
     * Does not run animation yet.
     */
    public boolean solveMaze() throws InvalidMazeException {
        clearSolverVisuals();
        baseMazeState = maze.clone();

        // Solve and gather statistics
        lastSolveStats = solver.solveForVisualization(maze);
        solvingSteps = solver.getSteps();
        
        if (solvingSteps == null || solvingSteps.isEmpty()) {
            return false;
        }

        // Keep the maze in clean base state for animated playbacks
        maze = baseMazeState.clone();
        currentStepIndex = -1;
        return lastSolveStats.getShortestPathLength() > 0;
    }

    /**
     * Instantly shows the final shortest path solution without animation playback.
     */
    public void solveInstantly() throws InvalidMazeException {
        boolean pathExists = solveMaze();
        if (pathExists) {
            // Apply all solving steps instantly
            for (SolvingStep step : solvingSteps) {
                maze.setCell(step.getPosition(), step.getNewType());
            }
            currentStepIndex = solvingSteps.size() - 1;
            notifyViews();
        } else {
            throw new InvalidMazeException("No solution path found in the current maze.");
        }
    }

    // --- ANIMATION CONTROLS ---

    public void startAnimation() {
        if (solvingSteps == null || solvingSteps.isEmpty()) return;
        playbackTimer.start();
    }

    public void stopAnimation() {
        playbackTimer.stop();
    }

    public void stopAnimationAndReset() {
        stopAnimation();
        if (baseMazeState != null) {
            maze = baseMazeState.clone();
        } else {
            maze.clearSolverTags();
        }
        currentStepIndex = -1;
        notifyViews();
    }

    public void setAnimationDelay(int delayMs) {
        this.animationDelayMs = delayMs;
        playbackTimer.setDelay(delayMs);
    }

    /**
     * Advances the animation exactly one step forward.
     */
    public void stepForward() {
        if (solvingSteps == null || solvingSteps.isEmpty() || isAnimating()) return;
        
        if (currentStepIndex < solvingSteps.size() - 1) {
            currentStepIndex++;
            SolvingStep step = solvingSteps.get(currentStepIndex);
            maze.setCell(step.getPosition(), step.getNewType());
            notifyAnimationStep(currentStepIndex, solvingSteps.size(), step);
            notifyViews();
        }
    }

    /**
     * Rewinds the animation exactly one step backward.
     */
    public void stepBackward() {
        if (solvingSteps == null || solvingSteps.isEmpty() || isAnimating()) return;

        if (currentStepIndex >= 0) {
            currentStepIndex--;
            
            // Reapply steps from beginning up to currentStepIndex
            maze = baseMazeState.clone();
            for (int i = 0; i <= currentStepIndex; i++) {
                SolvingStep step = solvingSteps.get(i);
                maze.setCell(step.getPosition(), step.getNewType());
            }
            
            SolvingStep currentStep = currentStepIndex >= 0 ? solvingSteps.get(currentStepIndex) : null;
            notifyAnimationStep(currentStepIndex, solvingSteps.size(), currentStep);
            notifyViews();
        }
    }

    // --- FILE I/O ---

    public void saveMaze(File file) throws IOException {
        FileHandler.saveMaze(maze, file);
    }

    public void loadMaze(File file) throws IOException, InvalidMazeException {
        saveStateForUndo();
        stopAnimationAndReset();
        maze = FileHandler.loadMaze(file);
        undoStack.clear();
        redoStack.clear();
        notifyViews();
    }

    public void exportTextReport(File file) throws IOException {
        FileHandler.exportReport(file, baseMazeState != null ? baseMazeState : maze, lastSolveStats);
    }

    // --- MEDIA UTILS ---

    public void exportScreenshot(Component target, File file) throws IOException {
        ImageExporter.exportComponentToImage(target, file);
    }

    public void printVisualizer(Component target) {
        SystemPrinter.printComponent(target, "Maze Solver Grid");
    }
}
