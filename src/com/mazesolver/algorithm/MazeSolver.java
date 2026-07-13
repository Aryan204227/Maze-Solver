package com.mazesolver.algorithm;

import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import com.mazesolver.model.MazeSolution;
import com.mazesolver.model.SolverStats;
import com.mazesolver.util.AnimationTimer;
import com.mazesolver.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * MazeSolver.java
 * ─────────────────────────────────────────────────────
 * THE HEART OF THE PROJECT — Core recursive DFS + Backtracking algorithm.
 *
 * WHY THIS CLASS EXISTS:
 *   This is what the project is about! It uses recursion and
 *   backtracking to explore every possible path through the maze
 *   and find ALL valid solutions.
 *
 * ╔═══════════════════════════════════════════════════╗
 * ║         HOW THE ALGORITHM WORKS                  ║
 * ╠═══════════════════════════════════════════════════╣
 * ║  solveRecursive(row, col):                       ║
 * ║    1. GUARD: out of bounds? → return             ║
 * ║    2. GUARD: wall? → return                      ║
 * ║    3. GUARD: already visited? → return           ║
 * ║    4. MARK cell as visited                       ║
 * ║    5. BASE CASE: reached END? → save solution    ║
 * ║    6. RECURSE: try Down, Right, Up, Left         ║
 * ║    7. BACKTRACK: unmark cell, remove from path   ║
 * ╚═══════════════════════════════════════════════════╝
 *
 * JAVA CONCEPTS USED:
 *   - Recursion (method calls itself)
 *   - Backtracking (undo step when no path found)
 *   - DFS (Depth-First Search)
 *   - Interface (callback to notify UI)
 *   - Thread + volatile (for animation)
 *   - SwingUtilities.invokeLater (safe UI updates)
 */
public class MazeSolver {

    // ─────────────────────────────────────────────────
    // Callback Interface
    // ─────────────────────────────────────────────────

    /**
     * The UI implements this interface to receive notifications
     * as the solver explores the maze step by step.
     *
     * Why interface? → Keeps algorithm separate from UI (MVC).
     */
    public interface SolveCallback {
        /** Called when a cell is being explored */
        void onCellVisited(int row, int col);

        /** Called when the solver backtracks from a cell */
        void onBacktrack(int row, int col, boolean isDeadEnd);

        /** Called when a complete path to END is found */
        void onSolutionFound(MazeSolution solution);

        /** Called when the entire solve process is complete */
        void onComplete(SolverStats stats, List<MazeSolution> solutions);
    }

    // ─────────────────────────────────────────────────
    // Direction Vectors
    // ─────────────────────────────────────────────────

    // DFS explores in order: Down → Right → Up → Left
    private static final int[] MOVE_ROW = {  1,  0, -1,  0 };
    private static final int[] MOVE_COL = {  0,  1,  0, -1 };
    private static final String[] DIRECTION_NAME = { "Down", "Right", "Up", "Left" };

    // ─────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────

    private final Maze          maze;
    private final SolverStats   stats;
    private final AnimationTimer timer;
    private final SolveCallback callback;

    private boolean[][]         visited;      // tracks visited cells per path
    private List<MazeSolution>  allSolutions; // all solutions found

    // ─────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────

    public MazeSolver(Maze maze, SolverStats stats, AnimationTimer timer, SolveCallback callback) {
        this.maze     = maze;
        this.stats    = stats;
        this.timer    = timer;
        this.callback = callback;
    }

    // ─────────────────────────────────────────────────
    // Public Entry Point
    // ─────────────────────────────────────────────────

    /**
     * Starts the maze solving process.
     * Called from a background Thread (NOT the Swing thread).
     * UI updates are sent via SwingUtilities.invokeLater().
     */
    public void solve() {
        visited      = new boolean[maze.getRows()][maze.getCols()];
        allSolutions = new ArrayList<>();

        stats.reset();
        stats.startTimer();

        // Start recursion from the START cell
        List<int[]> currentPath = new ArrayList<>();
        solveRecursive(maze.getStartRow(), maze.getStartCol(), currentPath, 0);

        stats.stopTimer();

        // Notify UI that solving is complete (on Swing thread)
        final List<MazeSolution> resultCopy = new ArrayList<>(allSolutions);
        notifyUI(() -> callback.onComplete(stats, resultCopy));
    }

    // ─────────────────────────────────────────────────
    // THE CORE RECURSIVE METHOD
    // ─────────────────────────────────────────────────

    /**
     * ★ THIS IS THE ALGORITHM ★
     *
     * Recursively explores the maze from (row, col).
     * Uses DFS to go as deep as possible, then backtracks
     * when a dead end is reached.
     *
     * @param row         current row position
     * @param col         current column position
     * @param currentPath list of cells in current exploration path
     * @param depth       current recursion depth (for stats)
     */
    private void solveRecursive(int row, int col, List<int[]> currentPath, int depth) {

        // ── STOP CONDITIONS ──────────────────────────────

        // Stop if user pressed Cancel/Reset
        if (timer.isCancelled()) return;

        // Stop if we found enough solutions (safety cap)
        if (allSolutions.size() >= Constants.MAX_SOLUTIONS) return;

        // Guard 1: Is (row, col) inside the maze grid?
        if (!maze.isInBounds(row, col)) return;

        // Guard 2: Is this cell a wall? Can't walk through walls.
        if (maze.getCell(row, col).isWall()) return;

        // Guard 3: Did we already visit this cell in current path?
        //          Prevents infinite loops (cycles).
        if (visited[row][col]) return;

        // ── VISIT THIS CELL ──────────────────────────────

        visited[row][col] = true;
        currentPath.add(new int[]{row, col});
        maze.getCell(row, col).setDepth(depth);

        // Update statistics
        stats.incrementCalls();
        stats.incrementVisited();
        stats.updateMaxDepth(depth);
        stats.setCurrentDepth(depth);
        stats.setCurrentCoords(row, col);
        stats.pushToStack(row, col);

        // Notify the UI: this cell is being explored (🔵 Blue)
        notifyUI(() -> callback.onCellVisited(row, col));

        // Handle pause: block here if user paused animation
        timer.waitIfPaused();

        // Wait for animation delay (makes it visible step-by-step)
        sleep(timer.getDelay());

        // ── BASE CASE: REACHED THE END ───────────────────

        if (row == maze.getEndRow() && col == maze.getEndCol()) {

            stats.incrementSolutions();
            stats.updateShortestPath(currentPath.size());

            // Save this complete path as a solution
            MazeSolution solution = new MazeSolution(stats.getTotalSolutions());
            for (int[] step : currentPath) {
                solution.addStep(step[0], step[1]);
            }
            allSolutions.add(solution);

            // Notify UI: solution found (🟢 Green highlight)
            final MazeSolution foundSol = solution;
            notifyUI(() -> callback.onSolutionFound(foundSol));

            // Pause to let user see the solution before backtracking
            sleep(timer.getDelay() * 4);

        } else {
            // ── RECURSE IN 4 DIRECTIONS ──────────────────
            // DFS: try each direction one by one
            for (int i = 0; i < 4; i++) {
                int nextRow = row + MOVE_ROW[i];
                int nextCol = col + MOVE_COL[i];

                // V3.0: Tell stats + Learning Panel which direction we're trying
                stats.setCurrentDirection(getArrow(i) + " " + DIRECTION_NAME[i]);
                stats.setCurrentDecision("Trying " + DIRECTION_NAME[i] + " from (" + row + "," + col + ")");

                // Recurse (go deeper into the maze)
                solveRecursive(nextRow, nextCol, currentPath, depth + 1);

                // Check after each direction
                if (timer.isCancelled() || allSolutions.size() >= Constants.MAX_SOLUTIONS) {
                    break;
                }
            }
        }

        // Determine if it was a dead end (no other open unvisited neighbors when we arrived)
        // Guard: if cancelled during recursion, skip this check to avoid stale-state NPE
        boolean isDeadEnd = true;
        if (!timer.isCancelled()) {
            for (int i = 0; i < 4; i++) {
                int nextRow = row + MOVE_ROW[i];
                int nextCol = col + MOVE_COL[i];
                if (maze.isInBounds(nextRow, nextCol)) {
                    Cell neighbor = maze.getCell(nextRow, nextCol);
                    // Null-guard: neighbor should never be null, but guard defensively
                    if (neighbor != null && neighbor.isWalkable() && !visited[nextRow][nextCol]) {
                        isDeadEnd = false;
                        break;
                    }
                }
            }
        }

        // ── BACKTRACK ────────────────────────────────────
        // This cell didn't lead to a solution (or we explored all its paths).
        // UNDO: unmark and remove from current path.
        visited[row][col] = false;
        currentPath.remove(currentPath.size() - 1);
        stats.incrementBacktrack();
        stats.popFromStack();
        stats.setCurrentDirection("← Backtrack");
        stats.setCurrentDecision("Dead end at (" + row + "," + col + ") — backtracking up");

        // Notify UI: backtracking from this cell
        final boolean finalDeadEnd = isDeadEnd;
        notifyUI(() -> callback.onBacktrack(row, col, finalDeadEnd));
        sleep(timer.getDelay() / 3);
    }

    /**
     * Maps a direction index (0=Down,1=Right,2=Up,3=Left) to a Unicode arrow.
     * Used by the Learning Panel to display which direction DFS is exploring.
     */
    private String getArrow(int dirIndex) {
        switch (dirIndex) {
            case 0: return "\u2193"; // Down
            case 1: return "\u2192"; // Right
            case 2: return "\u2191"; // Up
            case 3: return "\u2190"; // Left
            default: return "?";
        }
    }

    /**
     * Safely sends a UI update to the Swing Event Dispatch Thread.
     * IMPORTANT: Never update Swing components from a background thread!
     * SwingUtilities.invokeLater() queues the task on the EDT.
     */
    private void notifyUI(Runnable task) {
        javax.swing.SwingUtilities.invokeLater(task);
    }

    /**
     * Pauses the solver thread for the specified duration.
     * This creates the step-by-step visualization effect.
     */
    private void sleep(int ms) {
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
