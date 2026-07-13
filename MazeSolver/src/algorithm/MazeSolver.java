package algorithm;

import model.Cell;
import model.Maze;
import model.SolveResult;
import java.util.ArrayList;
import java.util.List;

/**
 * MazeSolver - Solves the maze using Recursive DFS + Backtracking.
 *
 * WHY THIS CLASS EXISTS:
 *   This is the core algorithm class. It has NO UI code at all.
 *   It records every step taken so the VisualizerPanel can replay them.
 *
 * HOW IT WORKS:
 *   1. Start at the START cell.
 *   2. Try to move in 4 directions (Up, Right, Down, Left).
 *   3. If a direction leads to END, mark path as FOUND and return true.
 *   4. If stuck, backtrack (mark as DEAD_END, return false).
 *   5. Separately count ALL possible paths for the solution counter.
 *
 * JAVA CONCEPTS USED:
 *   Recursion, Backtracking, 2D Arrays, ArrayList, boolean[][]
 */
public class MazeSolver {

    // Each step = { row, col, Cell.ordinal() }
    // This records every move for animation playback
    private List<int[]> steps;

    private boolean[][] visited;
    private int visitedCount;
    private int maxDepth;
    private int solutionCount;
    private int shortestPathLength;
    private int endRow;
    private int endCol;

    // Safety cap: stop counting after this many solutions (avoids infinite loops on open grids)
    private static final int MAX_SOLUTIONS = 500;

    /**
     * Main entry point. Solves the maze and returns full statistics.
     */
    public SolveResult solve(Maze maze) {
        // Initialize all tracking variables
        steps = new ArrayList<int[]>();
        visitedCount = 0;
        maxDepth = 0;
        solutionCount = 0;
        shortestPathLength = 0;
        endRow = maze.getEndRow();
        endCol = maze.getEndCol();

        int rows = maze.getRows();
        int cols = maze.getCols();
        visited = new boolean[rows][cols];

        // --- Phase 1: Find ONE path and record animation steps ---
        long startTime = System.currentTimeMillis();
        boolean found = findPath(maze.getGrid(), maze.getStartRow(), maze.getStartCol(), 0);
        long endTime = System.currentTimeMillis();

        // --- Phase 2: Count ALL solutions (no step recording) ---
        boolean[][] visited2 = new boolean[rows][cols];
        countAllSolutions(maze.getGrid(), maze.getStartRow(), maze.getStartCol(), visited2, 0);

        // Build and return the result object
        SolveResult result = new SolveResult();
        result.setSolved(found);
        result.setExecutionTimeMs(endTime - startTime);
        result.setVisitedCount(visitedCount);
        result.setMaxDepth(maxDepth);
        result.setSolutionCount(solutionCount);
        result.setShortestPathLength(shortestPathLength);
        return result;
    }

    /**
     * Recursive DFS: tries to find a path from (row, col) to END.
     * Records every move as a step for animation.
     *
     * @return true if a path to END was found from this cell
     */
    private boolean findPath(Cell[][] grid, int row, int col, int depth) {

        // ---- BASE CASES (stop recursion) ----
        // 1. Out of bounds
        if (!isValid(grid, row, col)) {
            return false;
        }
        // 2. Cell is a wall
        if (grid[row][col] == Cell.WALL) {
            return false;
        }
        // 3. Already visited (avoid cycles)
        if (visited[row][col]) {
            return false;
        }

        // ---- GOAL CHECK ----
        if (row == endRow && col == endCol) {
            steps.add(new int[]{ row, col, Cell.FOUND.ordinal() });
            return true;
        }

        // ---- EXPLORE THIS CELL ----
        visited[row][col] = true;
        visitedCount++;
        if (depth > maxDepth) {
            maxDepth = depth;
        }
        // Record VISITED step for animation
        steps.add(new int[]{ row, col, Cell.VISITED.ordinal() });

        // ---- TRY ALL 4 DIRECTIONS: Up, Right, Down, Left ----
        int[][] directions = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
        for (int[] dir : directions) {
            int nextRow = row + dir[0];
            int nextCol = col + dir[1];

            if (findPath(grid, nextRow, nextCol, depth + 1)) {
                // This cell is on the solution path
                steps.add(new int[]{ row, col, Cell.FOUND.ordinal() });
                return true;
            }
        }

        // ---- BACKTRACK ----
        // No direction worked from this cell - dead end
        steps.add(new int[]{ row, col, Cell.DEAD_END.ordinal() });
        visited[row][col] = false; // unmark so other paths can use this cell
        return false;
    }

    /**
     * Counts ALL possible paths from (row, col) to END.
     * Does NOT record steps (only counting). Has a cap to prevent freezing.
     */
    private void countAllSolutions(Cell[][] grid, int row, int col, boolean[][] vis, int pathLen) {
        if (solutionCount >= MAX_SOLUTIONS) {
            return; // Safety cap reached
        }
        if (!isValid(grid, row, col)) {
            return;
        }
        if (grid[row][col] == Cell.WALL) {
            return;
        }
        if (vis[row][col]) {
            return;
        }

        // Reached the end - found one complete solution
        if (row == endRow && col == endCol) {
            solutionCount++;
            // Track the shortest path length
            int currentPathLen = pathLen + 1;
            if (shortestPathLength == 0 || currentPathLen < shortestPathLength) {
                shortestPathLength = currentPathLen;
            }
            return;
        }

        vis[row][col] = true;

        int[][] directions = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
        for (int[] dir : directions) {
            countAllSolutions(grid, row + dir[0], col + dir[1], vis, pathLen + 1);
        }

        vis[row][col] = false; // backtrack
    }

    /**
     * Checks if a cell position is inside the grid boundaries.
     */
    private boolean isValid(Cell[][] grid, int row, int col) {
        return row >= 0 && col >= 0 && row < grid.length && col < grid[0].length;
    }

    /**
     * Returns the recorded animation steps.
     * Each step = int[]{ row, col, Cell.ordinal() }
     */
    public List<int[]> getSteps() {
        return steps;
    }
}
