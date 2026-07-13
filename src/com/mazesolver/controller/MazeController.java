package com.mazesolver.controller;

import com.mazesolver.view.panels.MazePanel;
import com.mazesolver.view.panels.StatsPanel;
import com.mazesolver.view.panels.LearningPanel;
import com.mazesolver.view.panels.StatusBar;
import com.mazesolver.model.Maze;
import com.mazesolver.model.Cell;
import com.mazesolver.model.SolverStats;
import com.mazesolver.model.MazeSolution;
import com.mazesolver.util.AnimationTimer;
import com.mazesolver.algorithm.MazeSolver;
import com.mazesolver.algorithm.MazeGenerator;
import com.mazesolver.view.ToastNotification;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import com.mazesolver.util.ThemeManager;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.FontMetrics;
import java.awt.Cursor;
import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;

public class MazeController implements MazeSolver.SolveCallback {
    private final MazePanel mazePanel;
    private final StatsPanel statsPanel;
    private final LearningPanel learningPanel;
    private final StatusBar statusBar;
    private Maze maze;
    private final SolverStats stats;
    private final AnimationTimer timer;
    private Thread solverThread;
    private boolean isSolvingRunning;
    private long lastUIUpdateTime;
    private final List<VisualStep> solveHistory;
    private int currentHistoryIndex;
    private boolean isReplayMode;
    private Timer replayTimer;
    private final Deque<int[]> undoStack;
    private final Deque<int[]> redoStack;
    private static final int MAX_UNDO = 80;
    private final List<MazeSolution> lastSolutions;
    private String activeTool;
    private int brushSize;
    private SolvingStateListener stateListener;

    // Player Mode state
    private boolean isPlayMode = false;
    private int playerRow = -1;
    private int playerCol = -1;
    private boolean[][] playerVisited;
    private int playerMoves = 0;
    private int playerWrongTurns = 0;
    private int playerHintsUsed = 0;
    private long playerStartTime = 0;
    private boolean playTimerStarted = false;
    private Timer playUIUpdateTimer;

    public interface SolvingStateListener {
        void onSolvingStateChanged(boolean running);
        void onRequestModeChange(String mode);
    }

    public static class VisualStep {
        public final int row;
        public final int col;
        public final Cell.State oldState;
        public final Cell.State newState;
        public final int visitedNodes;
        public final int recursiveCalls;
        public final int currentDepth;
        public final int maxDepth;
        public final int totalSolutions;
        public final int backtrackCount;
        public final String currentDirection;
        public final String currentDecision;
        public final List<String> stackSnapshot;

        public VisualStep(int r, int c, Cell.State oldState, Cell.State newState, SolverStats stats) {
            this.row = r;
            this.col = c;
            this.oldState = oldState;
            this.newState = newState;
            this.visitedNodes = stats.getVisitedNodes();
            this.recursiveCalls = stats.getRecursiveCalls();
            this.currentDepth = stats.getCurrentDepth();
            this.maxDepth = stats.getMaxDepth();
            this.totalSolutions = stats.getTotalSolutions();
            this.backtrackCount = stats.getBacktrackCount();
            this.currentDirection = stats.getCurrentDirection();
            this.currentDecision = stats.getCurrentDecision();
            synchronized (stats.getCurrentStack()) {
                this.stackSnapshot = new ArrayList<>(stats.getCurrentStack());
            }
        }

        public void applyForward(Maze maze, SolverStats stats) {
            if (row != -1 && col != -1) {
                maze.getCell(row, col).setState(newState);
            }
            restoreStats(stats);
        }

        public void applyBackward(Maze maze) {
            if (row != -1 && col != -1) {
                maze.getCell(row, col).setState(oldState);
            }
        }

        public void restoreStats(SolverStats stats) {
            stats.restoreFromSnapshot(visitedNodes, recursiveCalls, maxDepth, currentDepth, totalSolutions, backtrackCount, currentDirection, currentDecision, stackSnapshot);
        }
    }

    public MazeController(MazePanel mazePanel, StatsPanel statsPanel, LearningPanel learningPanel, StatusBar statusBar, int delayMs) {
        this.lastUIUpdateTime = 0;
        this.solveHistory = new ArrayList<>();
        this.currentHistoryIndex = -1;
        this.isReplayMode = false;
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
        this.lastSolutions = new ArrayList<>();
        this.activeTool = "brush";
        this.brushSize = 1;

        this.mazePanel = mazePanel;
        this.statsPanel = statsPanel;
        this.learningPanel = learningPanel;
        this.statusBar = statusBar;
        this.maze = mazePanel.getMaze();
        this.stats = new SolverStats();
        this.timer = new AnimationTimer(delayMs);
        this.mazePanel.setStats(this.stats);
    }

    public List<VisualStep> getSolveHistory() {
        return solveHistory;
    }

    public SolverStats getCurrentStats() {
        return stats;
    }

    public void generateRandomMaze() {
        generateRandomMaze("medium");
    }

    public void generateRandomMaze(String difficulty) {
        stopSolvingForcefully();
        clearHistory();
        maze.clearAll();
        MazeGenerator generator = new MazeGenerator(maze);
        generator.generate(difficulty);
        statusBar.setStatus("New random maze (" + difficulty + ") generated. Pathways carved.");
        mazePanel.repaint();
        statsPanel.resetStats();
        learningPanel.resetLearning();
        // Reset player state so playerVisited array matches new maze dimensions
        resetPlayerStateIfActive();
    }

    public void generateTemplateMaze(String template) {
        stopSolvingForcefully();
        clearHistory();
        maze.clearAll();
        MazeGenerator generator = new MazeGenerator(maze);
        if ("spiral".equals(template)) {
            generator.generateSpiral();
            statusBar.setStatus("Spiral maze template generated.");
        } else if ("corridor".equals(template)) {
            generator.generateCorridor();
            statusBar.setStatus("Long corridor maze template generated.");
        } else if ("checkerboard".equals(template)) {
            generator.generateCheckerboard();
            statusBar.setStatus("Checkerboard maze template generated.");
        } else {
            generator.generate();
            statusBar.setStatus("New random maze generated. Open cell pathways carved.");
        }
        mazePanel.repaint();
        statsPanel.resetStats();
        learningPanel.resetLearning();
        // Reset player state so playerVisited array matches new maze dimensions
        resetPlayerStateIfActive();
    }

    public void resetSolvingState() {
        stopSolvingForcefully();
        clearHistory();
        maze.resetSolvingState();
        mazePanel.repaint();
        statsPanel.resetStats();
        learningPanel.resetLearning();
        statusBar.setStatus("Solving visual states cleared. Maze structure preserved.");
    }

    public void toggleWallAt(int row, int col) {
        int radius = brushSize / 2;
        boolean isBrush = "brush".equals(activeTool);
        boolean modified = false;

        for (int r = row - radius; r <= row + brushSize - 1 - radius; r++) {
            for (int c = col - radius; c <= col + brushSize - 1 - radius; c++) {
                if (maze.isInBounds(r, c)) {
                    Cell cell = maze.getCell(r, c);
                    if (cell.isEndpoint()) {
                        continue;
                    }
                    boolean isWall = cell.isWall();
                    if (isBrush && !isWall) {
                        maze.setWall(r, c);
                        if (undoStack.size() >= MAX_UNDO) {
                            undoStack.pollFirst();
                        }
                        undoStack.push(new int[]{r, c});
                        modified = true;
                    } else if (!isBrush && isWall) {
                        maze.setOpen(r, c);
                        if (undoStack.size() >= MAX_UNDO) {
                            undoStack.pollFirst();
                        }
                        undoStack.push(new int[]{r, c});
                        modified = true;
                    }
                }
            }
        }

        if (modified) {
            redoStack.clear();
            mazePanel.repaint();
        }
    }

    public void undoWallEdit() {
        if (undoStack.isEmpty()) {
            statusBar.setStatus("Nothing to undo.");
            return;
        }
        int[] point = undoStack.pop();
        maze.toggleWall(point[0], point[1]);
        redoStack.push(point);
        mazePanel.repaint();
        statusBar.setStatus("Undo: wall edit at (" + point[0] + "," + point[1] + ")  [" + undoStack.size() + " undo(s) left]");
    }

    public void redoWallEdit() {
        if (redoStack.isEmpty()) {
            statusBar.setStatus("Nothing to redo.");
            return;
        }
        int[] point = redoStack.pop();
        maze.toggleWall(point[0], point[1]);
        undoStack.push(point);
        mazePanel.repaint();
        statusBar.setStatus("Redo: wall edit at (" + point[0] + "," + point[1] + ")  [" + redoStack.size() + " redo(s) left]");
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void resizeMaze(int rows, int cols) {
        stopSolvingForcefully();
        clearHistory();
        maze.resize(rows, cols);
        mazePanel.setMaze(maze);
        statsPanel.resetStats();
        learningPanel.resetLearning();
        statusBar.setGridSize(rows, cols);
        statusBar.setStatus("Grid size updated to " + rows + "x" + cols);
        // Player visited array must be reallocated to new dimensions
        resetPlayerStateIfActive();
    }

    public void solveMaze() {
        if (isSolvingRunning || isReplayMode) {
            return;
        }
        maze.resetSolvingState();
        mazePanel.repaint();
        statsPanel.resetStats();
        learningPanel.resetLearning();
        clearHistory();
        lastSolutions.clear();
        timer.reset();
        isSolvingRunning = true;
        if (stateListener != null) {
            stateListener.onSolvingStateChanged(true);
        }
        statusBar.setStatus("Solving... Tracing pathways via DFS Backtracking.");
        statusBar.setMode("Solving");
        solverThread = new Thread(() -> {
            MazeSolver solver = new MazeSolver(maze, stats, timer, this);
            solver.solve();
        });
        solverThread.start();
    }

    public void pauseSolving() {
        timer.pause();
        statusBar.setStatus("Solving paused.");
        statusBar.setMode("Paused");
    }

    public void resumeSolving() {
        timer.resume();
        statusBar.setStatus("Solving resumed...");
        statusBar.setMode("Solving");
    }

    public void setSpeed(int delayMs) {
        timer.setDelay(delayMs);
        if (replayTimer != null) {
            replayTimer.setDelay(Math.max(10, delayMs));
        }
    }

    public void stopSolvingForcefully() {
        if (isSolvingRunning) {
            timer.cancel();
            if (solverThread != null) {
                solverThread.interrupt();
            }
            isSolvingRunning = false;
            if (stateListener != null) {
                stateListener.onSolvingStateChanged(false);
            }
            statusBar.setMode("Idle");
            statusBar.setStatus("Solving terminated.");
        }
        stopReplay();
    }

    private void clearHistory() {
        solveHistory.clear();
        lastSolutions.clear();
        currentHistoryIndex = -1;
        isReplayMode = false;
        if (replayTimer != null) {
            replayTimer.stop();
            replayTimer = null;
        }
    }

    public void stepForward() {
        if (solveHistory.isEmpty()) {
            return;
        }
        if (currentHistoryIndex < solveHistory.size() - 1) {
            currentHistoryIndex++;
            VisualStep step = solveHistory.get(currentHistoryIndex);
            step.applyForward(maze, stats);
            mazePanel.repaint();
            statsPanel.updateStats(stats);
            learningPanel.updateLearning(stats);
            statusBar.setStatus("Step " + (currentHistoryIndex + 1) + " / " + solveHistory.size());
        } else {
            stopReplay();
            statusBar.setStatus("Reached end of solve history.");
        }
    }

    public void stepBackward() {
        if (solveHistory.isEmpty()) {
            return;
        }
        if (currentHistoryIndex >= 0) {
            VisualStep step = solveHistory.get(currentHistoryIndex);
            step.applyBackward(maze);
            currentHistoryIndex--;
            if (currentHistoryIndex >= 0) {
                VisualStep prevStep = solveHistory.get(currentHistoryIndex);
                prevStep.restoreStats(stats);
            } else {
                stats.restoreFromSnapshot(0, 0, 0, 0, 0, 0, "—", "Waiting for solver to start...", new ArrayList<>());
            }
            mazePanel.repaint();
            statsPanel.updateStats(stats);
            learningPanel.updateLearning(stats);
            statusBar.setStatus("Step " + (currentHistoryIndex + 1) + " / " + solveHistory.size());
        }
    }

    public void startReplay() {
        stopSolvingForcefully();
        if (solveHistory.isEmpty()) {
            statusBar.setStatus("No solve history recorded to replay.");
            return;
        }
        maze.resetSolvingState();
        mazePanel.repaint();
        isReplayMode = true;
        currentHistoryIndex = 0;
        statusBar.setMode("Solving");
        VisualStep firstStep = solveHistory.get(0);
        firstStep.applyForward(maze, stats);
        mazePanel.repaint();
        statsPanel.updateStats(stats);
        learningPanel.updateLearning(stats);
        int delay = Math.max(10, timer.getDelay());
        replayTimer = new Timer(delay, e -> stepForward());
        replayTimer.start();
        statusBar.setStatus("Replaying solve animation...");
    }

    public void stopReplay() {
        isReplayMode = false;
        if (replayTimer != null) {
            replayTimer.stop();
            replayTimer = null;
        }
        statusBar.setMode("Idle");
    }

    public boolean isReplayMode() {
        return isReplayMode;
    }

    @Override
    public void onCellVisited(int row, int col) {
        if (!maze.isInBounds(row, col)) {
            return;
        }
        if (row == maze.getStartRow() && col == maze.getStartCol()) {
            return;
        }
        if (row == maze.getEndRow() && col == maze.getEndCol()) {
            return;
        }
        Cell.State oldState = maze.getCell(row, col).getState();
        maze.getCell(row, col).setState(Cell.State.CURRENT);
        mazePanel.repaint();
        long now = System.currentTimeMillis();
        if (now - lastUIUpdateTime > 40) {
            statsPanel.updateStats(stats);
            learningPanel.updateLearning(stats);
            lastUIUpdateTime = now;
        }
        solveHistory.add(new VisualStep(row, col, oldState, Cell.State.CURRENT, stats));
    }

    @Override
    public void onBacktrack(int row, int col, boolean isDeadEnd) {
        if (!maze.isInBounds(row, col)) {
            return;
        }
        if (row == maze.getStartRow() && col == maze.getStartCol()) {
            return;
        }
        if (row == maze.getEndRow() && col == maze.getEndCol()) {
            return;
        }
        Cell.State oldState = maze.getCell(row, col).getState();
        Cell.State newState = isDeadEnd ? Cell.State.DEAD_END : Cell.State.BACKTRACK;
        maze.getCell(row, col).setState(newState);
        mazePanel.repaint();
        long now = System.currentTimeMillis();
        if (now - lastUIUpdateTime > 40) {
            statsPanel.updateStats(stats);
            learningPanel.updateLearning(stats);
            lastUIUpdateTime = now;
        }
        solveHistory.add(new VisualStep(row, col, oldState, newState, stats));
    }

    @Override
    public void onSolutionFound(MazeSolution solution) {
        List<int[]> path = solution.getPath();
        for (int[] step : path) {
            int r = step[0];
            int c = step[1];
            if (!maze.isInBounds(r, c)) {
                continue;
            }
            if (r == maze.getStartRow() && c == maze.getStartCol()) {
                continue;
            }
            if (r == maze.getEndRow() && c == maze.getEndCol()) {
                continue;
            }
            Cell cell = maze.getCell(r, c);
            Cell.State oldState = cell.getState();
            cell.setState(Cell.State.SOLUTION);
            solveHistory.add(new VisualStep(r, c, oldState, Cell.State.SOLUTION, stats));
        }
        mazePanel.repaint();
        statsPanel.updateStats(stats);
        learningPanel.updateLearning(stats);
        statusBar.setStatus("Solution #" + solution.getSolutionNumber() + "  found! Length: " + solution.getLength());
    }

    @Override
    public void onComplete(SolverStats stats, List<MazeSolution> solutions) {
        isSolvingRunning = false;
        lastSolutions.clear();
        lastSolutions.addAll(solutions);
        if (stateListener != null) {
            stateListener.onSolvingStateChanged(false);
        }
        statusBar.setMode("Idle");
        stats.setCurrentDirection("—");
        stats.setCurrentDecision("Solving completed! Found " + solutions.size() + " unique solution(s).");
        statsPanel.updateStats(stats);
        learningPanel.updateLearning(stats);

        if (solutions.isEmpty()) {
            statusBar.setStatus("Solved: No solution found.");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mazePanel);
            ToastNotification.showWarning(frame, "No solution path exists.");
        } else {
            statusBar.setStatus("Solving completed! Found " + solutions.size() + " unique path(s).");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mazePanel);
            ToastNotification.showSuccess(frame, "Maze Solved! Found " + solutions.size() + " paths.");

            MazeSolution shortest = null;
            for (MazeSolution sol : solutions) {
                if (shortest == null || sol.getLength() < shortest.getLength()) {
                    shortest = sol;
                }
            }

            if (shortest != null) {
                maze.resetSolvingState();
                for (int[] step : shortest.getPath()) {
                    int r = step[0];
                    int c = step[1];
                    if (!maze.isInBounds(r, c)) {
                        continue;
                    }
                    if (r == maze.getStartRow() && c == maze.getStartCol()) {
                        continue;
                    }
                    if (r == maze.getEndRow() && c == maze.getEndCol()) {
                        continue;
                    }
                    maze.getCell(r, c).setState(Cell.State.SOLUTION);
                }
                mazePanel.repaint();
            }
        }
        solveHistory.add(new VisualStep(-1, -1, null, null, stats));
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        this.mazePanel.setMaze(maze);
        // Player visited array must match the newly loaded maze dimensions
        resetPlayerStateIfActive();
    }

    public Maze getMaze() {
        return maze;
    }

    public void setActiveTool(String tool) {
        this.activeTool = tool;
    }

    public String getActiveTool() {
        return activeTool;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public void setStateListener(SolvingStateListener listener) {
        this.stateListener = listener;
    }

    public SolverStats getStats() {
        return stats;
    }

    public List<MazeSolution> getLastSolutions() {
        return lastSolutions;
    }

    public void setMode(String mode) {
        stopSolvingForcefully();
        resetSolvingState();
        
        this.isPlayMode = "Play Mode".equals(mode);
        this.isReplayMode = "Replay".equals(mode);
        
        statsPanel.setMode(mode);
        
        if (isPlayMode) {
            playerRow = maze.getStartRow();
            playerCol = maze.getStartCol();
            playerVisited = new boolean[maze.getRows()][maze.getCols()];
            playerVisited[playerRow][playerCol] = true;
            playerMoves = 0;
            playerWrongTurns = 0;
            playerHintsUsed = 0;
            playerStartTime = 0;
            playTimerStarted = false;
            
            mazePanel.setPlayerPosition(playerRow, playerCol);
            mazePanel.setPlayerVisited(playerVisited);
            mazePanel.setHintPosition(-1, -1);
            
            updatePlayerStatsUI();
            
            if (playUIUpdateTimer != null) {
                playUIUpdateTimer.stop();
            }
            playUIUpdateTimer = new Timer(1000, e -> {
                if (playTimerStarted && isPlayMode) {
                    updatePlayerStatsUI();
                }
            });
            playUIUpdateTimer.start();
            
            statusBar.setStatus("Play Mode enabled: Use WASD/Arrow keys to navigate Start (S) to End (E).");
        } else {
            if (playUIUpdateTimer != null) {
                playUIUpdateTimer.stop();
                playUIUpdateTimer = null;
            }
            playerRow = -1;
            playerCol = -1;
            mazePanel.setPlayerPosition(-1, -1);
            mazePanel.setPlayerVisited(null);
            mazePanel.setHintPosition(-1, -1);
            statsPanel.resetStats();
            statusBar.setStatus("Mode set to: " + mode);
        }
        
        mazePanel.repaint();
    }

    /**
     * If Play Mode is currently active, resets the player back to the start
     * cell and reallocates the playerVisited array to match the current maze
     * dimensions.  Call this whenever the maze grid changes (generate, resize,
     * load) so that playerVisited[nextR][nextC] never throws
     * ArrayIndexOutOfBoundsException.
     */
    private void resetPlayerStateIfActive() {
        if (!isPlayMode) return;
        playerRow = maze.getStartRow();
        playerCol = maze.getStartCol();
        playerVisited = new boolean[maze.getRows()][maze.getCols()];
        if (playerRow >= 0 && playerCol >= 0) {
            playerVisited[playerRow][playerCol] = true;
        }
        playerMoves = 0;
        playerWrongTurns = 0;
        playerHintsUsed = 0;
        playerStartTime = 0;
        playTimerStarted = false;
        mazePanel.setPlayerPosition(playerRow, playerCol);
        mazePanel.setPlayerVisited(playerVisited);
        mazePanel.setHintPosition(-1, -1);
        mazePanel.repaint();
        updatePlayerStatsUI();
    }

    public boolean isPlayMode() {
        return isPlayMode;
    }

    public void onPlayerMove(String direction) {
        if (!isPlayMode) return;
        
        int nextR = playerRow;
        int nextC = playerCol;
        
        switch (direction) {
            case "UP":    nextR--; break;
            case "DOWN":  nextR++; break;
            case "LEFT":  nextC--; break;
            case "RIGHT": nextC++; break;
        }
        
        if (!maze.isInBounds(nextR, nextC)) {
            return;
        }
        
        Cell cell = maze.getCell(nextR, nextC);
        if (cell.isWall()) {
            return;
        }
        
        if (!playTimerStarted) {
            playerStartTime = System.currentTimeMillis();
            playTimerStarted = true;
        }
        
        // Guard: playerVisited array may be null or sized for a different maze
        if (playerVisited == null
                || nextR < 0 || nextR >= playerVisited.length
                || nextC < 0 || nextC >= playerVisited[0].length) {
            return;
        }

        playerMoves++;

        if (playerVisited[nextR][nextC]) {
            playerWrongTurns++;
        } else {
            if (isDeadEndCell(nextR, nextC)) {
                playerWrongTurns++;
            }
            playerVisited[nextR][nextC] = true;
        }
        
        playerRow = nextR;
        playerCol = nextC;
        
        mazePanel.setPlayerPosition(playerRow, playerCol);
        mazePanel.setPlayerVisited(playerVisited);
        mazePanel.setHintPosition(-1, -1);
        
        updatePlayerStatsUI();
        
        if (playerRow == maze.getEndRow() && playerCol == maze.getEndCol()) {
            handlePlayerWin();
        }
    }

    public void givePlayerHint() {
        int startR = isPlayMode ? playerRow : maze.getStartRow();
        int startC = isPlayMode ? playerCol : maze.getStartCol();
        
        List<int[]> path = findShortestPath(startR, startC, maze.getEndRow(), maze.getEndCol());
        if (path == null || path.size() < 2) {
            statusBar.setStatus("Hint: No path to the destination exists.");
            return;
        }
        int[] nextStep = path.get(1);
        if (isPlayMode) {
            playerHintsUsed++;
            updatePlayerStatsUI();
        }
        mazePanel.setHintPosition(nextStep[0], nextStep[1]);
        statusBar.setStatus("💡 Hint: Move to (" + nextStep[0] + ", " + nextStep[1] + ")");
    }

    public List<int[]> findShortestPath(int startR, int startC, int endR, int endC) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        boolean[][] visited = new boolean[rows][cols];
        int[][][] parent = new int[rows][cols][2];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                parent[r][c][0] = -1;
                parent[r][c][1] = -1;
            }
        }

        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        queue.add(new int[]{startR, startC});
        visited[startR][startC] = true;

        int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        boolean found = false;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0];
            int c = curr[1];
            if (r == endR && c == endC) {
                found = true;
                break;
            }
            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (maze.isInBounds(nr, nc)) {
                    Cell cell = maze.getCell(nr, nc);
                    if (!cell.isWall() && !visited[nr][nc]) {
                        visited[nr][nc] = true;
                        parent[nr][nc][0] = r;
                        parent[nr][nc][1] = c;
                        queue.add(new int[]{nr, nc});
                    }
                }
            }
        }

        if (!found) return null;

        List<int[]> path = new ArrayList<>();
        int currR = endR;
        int currC = endC;
        while (currR != -1 && currC != -1) {
            path.add(0, new int[]{currR, currC});
            int pr = parent[currR][currC][0];
            int pc = parent[currR][currC][1];
            currR = pr;
            currC = pc;
        }
        return path;
    }

    private boolean isDeadEndCell(int r, int c) {
        if (r == maze.getStartRow() && c == maze.getStartCol()) return false;
        if (r == maze.getEndRow() && c == maze.getEndCol()) return false;
        
        int walkableNeighbors = 0;
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (maze.isInBounds(nr, nc) && !maze.getCell(nr, nc).isWall()) {
                walkableNeighbors++;
            }
        }
        return walkableNeighbors <= 1;
    }

    private double getPlayerScore() {
        long elapsedSecs = playTimerStarted ? (System.currentTimeMillis() - playerStartTime) / 1000 : 0;
        double score = 1000.0 - (playerMoves * 10) - (playerWrongTurns * 30) - (playerHintsUsed * 50) - elapsedSecs;
        return Math.max(0.0, score);
    }

    private double getPlayerEfficiency() {
        List<int[]> path = findShortestPath(maze.getStartRow(), maze.getStartCol(), maze.getEndRow(), maze.getEndCol());
        int shortestLength = path != null ? path.size() : 1;
        if (playerMoves == 0) return 100.0;
        return Math.min(100.0, (double) shortestLength / playerMoves * 100.0);
    }

    private void updatePlayerStatsUI() {
        long elapsed = playTimerStarted ? (System.currentTimeMillis() - playerStartTime) : 0;
        int visitedCount = 0;
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                if (playerVisited[r][c]) visitedCount++;
            }
        }
        
        List<int[]> path = findShortestPath(maze.getStartRow(), maze.getStartCol(), maze.getEndRow(), maze.getEndCol());
        int shortestLength = path != null ? path.size() : 1;
        
        statsPanel.updatePlayerStats(
            elapsed,
            visitedCount,
            playerMoves,
            playerWrongTurns,
            playerHintsUsed,
            getPlayerScore(),
            getPlayerEfficiency(),
            shortestLength
        );
    }

    private void handlePlayerWin() {
        playTimerStarted = false;
        if (playUIUpdateTimer != null) {
            playUIUpdateTimer.stop();
        }
        
        long elapsed = System.currentTimeMillis() - playerStartTime;
        long secs = elapsed / 1000;
        long mins = secs / 60;
        secs = secs % 60;
        
        double score = getPlayerScore();
        double efficiency = getPlayerEfficiency();
        double accuracy = playerMoves > 0 ? (double)(playerMoves - playerWrongTurns) / playerMoves * 100.0 : 100.0;
        accuracy = Math.max(0.0, accuracy);
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(mazePanel);
        
        JDialog dialog = new JDialog(parentFrame, "Victory!", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 360);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 400, 360, 16, 16));
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getBgSecondary());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        JLabel title = new JLabel("🏆  Congratulations!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.getAccentGreen());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel sub = new JLabel("You have successfully solved the maze!");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(ThemeManager.getTextSecondary());
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel statsGrid = new JPanel(new GridLayout(6, 2, 10, 8));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        addDialogStatRow(statsGrid, "Completion Time:", String.format("%02d:%02d", mins, secs), ThemeManager.getAccentBlue());
        addDialogStatRow(statsGrid, "Moves:", String.valueOf(playerMoves), ThemeManager.getAccentPurple());
        addDialogStatRow(statsGrid, "Wrong Turns:", String.valueOf(playerWrongTurns), ThemeManager.getAccentRed());
        addDialogStatRow(statsGrid, "Efficiency:", String.format("%.1f %%", efficiency), ThemeManager.getAccentGreen());
        addDialogStatRow(statsGrid, "Accuracy:", String.format("%.1f %%", accuracy), ThemeManager.getAccentBlue());
        addDialogStatRow(statsGrid, "Score:", String.format("%.0f pts", score), ThemeManager.getAccentYellow());
        
        // Options Panel: Play Again, Generate New Maze, Replay
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnPlayAgain = makeDialogBtn("Play Again", ThemeManager.getAccentBlue(), e -> {
            dialog.dispose();
            if (stateListener != null) {
                stateListener.onRequestModeChange("Play Mode");
            }
        });
        
        JButton btnGenerateNew = makeDialogBtn("New Maze", ThemeManager.getAccentPurple(), e -> {
            dialog.dispose();
            generateRandomMaze();
            if (stateListener != null) {
                stateListener.onRequestModeChange("Play Mode");
            }
        });
        
        JButton btnReplay = makeDialogBtn("Replay", ThemeManager.getAccentGreen(), e -> {
            dialog.dispose();
            if (solveHistory.isEmpty()) {
                // Solve instantly in background to populate solve history for replay
                maze.resetSolvingState();
                SolverStats dummyStats = new SolverStats();
                MazeSolver solver = new MazeSolver(maze, dummyStats, new AnimationTimer(0), this);
                solver.solve();
            }
            if (stateListener != null) {
                stateListener.onRequestModeChange("Replay");
                startReplay();
            }
        });
        
        btnPanel.add(btnPlayAgain);
        btnPanel.add(btnGenerateNew);
        btnPanel.add(btnReplay);
        
        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(sub);
        panel.add(Box.createVerticalStrut(10));
        panel.add(statsGrid);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JButton makeDialogBtn(String text, Color bgColor, java.awt.event.ActionListener al) {
        JButton btn = new JButton(text) {
            private boolean hov = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { hov = true; repaint(); }
                    public void mouseExited(java.awt.event.MouseEvent e)  { hov = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? bgColor.darker() : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(105, 34));
        btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(al);
        return btn;
    }

    private void addDialogStatRow(JPanel parent, String key, String val, Color valColor) {
        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        k.setForeground(ThemeManager.getTextSecondary());
        
        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
        v.setForeground(valColor);
        
        parent.add(k);
        parent.add(v);
    }
}