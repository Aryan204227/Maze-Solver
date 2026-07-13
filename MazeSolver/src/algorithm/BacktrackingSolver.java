package algorithm;

import java.util.ArrayList;
import java.util.List;
import model.CellType;
import model.InvalidMazeException;
import model.Maze;
import model.Position;
import model.SolveStatistics;

/**
 * Implements the recursive Depth-First Search (DFS) and Backtracking algorithm
 * for solving mazes, tracking single paths (for step-by-step visual animation)
 * and scanning all possible paths to count solutions.
 */
public class BacktrackingSolver {
    private List<SolvingStep> steps;
    private int stepCounter;
    private int currentRecursionDepth;
    private int maxRecursionDepthReached;
    private int visitedNodesCount;
    
    // Limits to prevent UI lockup/Out-of-memory when finding all solutions in large open grids
    private static final int MAX_ALL_SOLUTIONS = 5000;
    private static final long MAX_SEARCH_TIME_MS = 3000; // 3 seconds timeout

    // Directions: Up, Right, Down, Left
    private static final int[][] DIRECTIONS = {
        {-1, 0}, // Up
        {0, 1},  // Right
        {1, 0},  // Down
        {0, -1}  // Left
    };

    private boolean[][] visited;
    private List<Position> currentPath;
    private List<Position> shortestPath;
    private int shortestPathLength;
    private int solutionsCount;
    private boolean searchTimedOut;

    /**
     * Solves the maze for visualization. Records every transition step.
     *
     * @param maze the maze model to solve
     * @return the solver statistics
     * @throws InvalidMazeException if start or end position is missing
     */
    public SolveStatistics solveForVisualization(Maze maze) throws InvalidMazeException {
        Position start = maze.getStartPosition();
        Position end = maze.getEndPosition();

        if (start == null || end == null) {
            throw new InvalidMazeException("Maze must contain both a START and an END position.");
        }

        // Initialize tracking variables
        steps = new ArrayList<>();
        stepCounter = 0;
        currentRecursionDepth = 0;
        maxRecursionDepthReached = 0;
        visitedNodesCount = 0;
        
        int rows = maze.getRows();
        int cols = maze.getCols();
        visited = new boolean[rows][cols];
        currentPath = new ArrayList<>();
        shortestPath = new ArrayList<>();
        shortestPathLength = Integer.MAX_VALUE;
        solutionsCount = 0;
        searchTimedOut = false;

        SolveStatistics stats = new SolveStatistics();
        long startTime = System.nanoTime();

        // 1. First run: DFS to find visual steps for the first path
        boolean pathFound = findVisualPathRecursive(maze, start, end, 0);

        // 2. Second run: Fast check to count ALL paths (without heavy visual step-logging)
        // Re-initialize visited array for all-paths counting
        visited = new boolean[rows][cols];
        long allPathsStartTime = System.currentTimeMillis();
        findAllPathsRecursive(maze, start, end, allPathsStartTime);

        long endTime = System.nanoTime();
        
        // Populate stats
        stats.setExecutionTimeMs((endTime - startTime) / 1000000);
        stats.setVisitedNodes(visitedNodesCount);
        stats.setMaxRecursionDepth(maxRecursionDepthReached);
        stats.setTotalSolutions(solutionsCount);
        if (shortestPathLength != Integer.MAX_VALUE) {
            stats.setShortestPath(shortestPath);
        } else {
            stats.setShortestPathLength(-1);
        }

        return stats;
    }

    /**
     * Core recursive DFS backtracker that logs steps for UI visualization of a single path.
     */
    private boolean findVisualPathRecursive(Maze maze, Position current, Position end, int depth) {
        currentRecursionDepth = depth;
        if (depth > maxRecursionDepthReached) {
            maxRecursionDepthReached = depth;
        }

        int r = current.getRow();
        int c = current.getCol();

        // Base case: Reached target
        if (current.equals(end)) {
            visitedNodesCount++;
            steps.add(new SolvingStep(current, CellType.CORRECT_PATH, ++stepCounter, depth, "Destination reached!"));
            return true;
        }

        visited[r][c] = true;
        visitedNodesCount++;

        // Add a VISITED step for visualization (skip starting cell tag updates)
        if (maze.getCell(current) != CellType.START) {
            steps.add(new SolvingStep(current, CellType.VISITED, ++stepCounter, depth, "Visiting cell"));
        }

        // Try directions (Up, Right, Down, Left)
        for (int[] dir : DIRECTIONS) {
            Position next = current.move(dir[0], dir[1]);
            
            if (maze.isWithinBounds(next)) {
                CellType cellType = maze.getCell(next);
                if (cellType != CellType.WALL && !visited[next.getRow()][next.getCol()]) {
                    if (findVisualPathRecursive(maze, next, end, depth + 1)) {
                        // On the correct path way back
                        if (maze.getCell(current) != CellType.START) {
                            steps.add(new SolvingStep(current, CellType.CORRECT_PATH, ++stepCounter, depth, "Marking correct path"));
                        }
                        return true;
                    }
                }
            }
        }

        // Backtracking: mark as DEAD_END
        if (maze.getCell(current) != CellType.START) {
            steps.add(new SolvingStep(current, CellType.DEAD_END, ++stepCounter, depth, "Dead-end reached, backtracking"));
        }
        
        visited[r][c] = false; // backtrack
        return false;
    }

    /**
     * Fast DFS recursion to count solutions and locate the shortest path.
     * Includes execution safety constraints (timeout and count cap).
     */
    private void findAllPathsRecursive(Maze maze, Position current, Position end, long startTimeMs) {
        // Safety checks
        if (solutionsCount >= MAX_ALL_SOLUTIONS) {
            return;
        }
        if (System.currentTimeMillis() - startTimeMs > MAX_SEARCH_TIME_MS) {
            searchTimedOut = true;
            return;
        }

        int r = current.getRow();
        int c = current.getCol();

        // Add to current path
        currentPath.add(current);
        visited[r][c] = true;

        if (current.equals(end)) {
            solutionsCount++;
            if (currentPath.size() < shortestPathLength) {
                shortestPathLength = currentPath.size();
                shortestPath = new ArrayList<>(currentPath);
            }
        } else {
            for (int[] dir : DIRECTIONS) {
                Position next = current.move(dir[0], dir[1]);
                if (maze.isWithinBounds(next)) {
                    CellType cellType = maze.getCell(next);
                    if (cellType != CellType.WALL && !visited[next.getRow()][next.getCol()]) {
                        findAllPathsRecursive(maze, next, end, startTimeMs);
                    }
                }
            }
        }

        // Backtrack
        visited[r][c] = false;
        currentPath.remove(currentPath.size() - 1);
    }

    /**
     * Gets the recorded steps list for playback animations.
     *
     * @return the list of solving steps
     */
    public List<SolvingStep> getSteps() {
        return steps;
    }

    /**
     * Checks if the last all-paths traversal was stopped due to timeout.
     *
     * @return true if timed out, false otherwise
     */
    public boolean isSearchTimedOut() {
        return searchTimedOut;
    }
}
