package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates statistical information about the execution of the maze-solving algorithm.
 */
public class SolveStatistics {
    private long executionTimeMs;
    private int totalSolutions;
    private int visitedNodes;
    private int maxRecursionDepth;
    private int shortestPathLength;
    private List<Position> shortestPath;

    /**
     * Constructs a new SolveStatistics instance with default values.
     */
    public SolveStatistics() {
        this.executionTimeMs = 0;
        this.totalSolutions = 0;
        this.visitedNodes = 0;
        this.maxRecursionDepth = 0;
        this.shortestPathLength = -1;
        this.shortestPath = new ArrayList<>();
    }

    /**
     * Gets the execution time in milliseconds.
     *
     * @return the execution time in ms
     */
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    /**
     * Sets the execution time in milliseconds.
     *
     * @param executionTimeMs the execution time in ms
     */
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    /**
     * Gets the total number of solutions found.
     *
     * @return the total solutions count
     */
    public int getTotalSolutions() {
        return totalSolutions;
    }

    /**
     * Sets the total number of solutions found.
     *
     * @param totalSolutions the total solutions count
     */
    public void setTotalSolutions(int totalSolutions) {
        this.totalSolutions = totalSolutions;
    }

    /**
     * Gets the count of visited nodes.
     *
     * @return the count of visited cells
     */
    public int getVisitedNodes() {
        return visitedNodes;
    }

    /**
     * Sets the count of visited nodes.
     *
     * @param visitedNodes the count of visited cells
     */
    public void setVisitedNodes(int visitedNodes) {
        this.visitedNodes = visitedNodes;
    }

    /**
     * Gets the maximum recursion depth reached.
     *
     * @return the maximum recursion depth
     */
    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    /**
     * Sets the maximum recursion depth reached.
     *
     * @param maxRecursionDepth the maximum recursion depth
     */
    public void setMaxRecursionDepth(int maxRecursionDepth) {
        this.maxRecursionDepth = maxRecursionDepth;
    }

    /**
     * Gets the length of the shortest path found.
     *
     * @return the length of the shortest path (-1 if no path exists)
     */
    public int getShortestPathLength() {
        return shortestPathLength;
    }

    /**
     * Sets the length of the shortest path found.
     *
     * @param shortestPathLength the length of the shortest path
     */
    public void setShortestPathLength(int shortestPathLength) {
        this.shortestPathLength = shortestPathLength;
    }

    /**
     * Gets the shortest path as a list of positions.
     *
     * @return list of positions representing the shortest path
     */
    public List<Position> getShortestPath() {
        return new ArrayList<>(shortestPath);
    }

    /**
     * Sets the shortest path.
     *
     * @param shortestPath list of positions representing the shortest path
     */
    public void setShortestPath(List<Position> shortestPath) {
        this.shortestPath = shortestPath != null ? new ArrayList<>(shortestPath) : new ArrayList<>();
        this.shortestPathLength = this.shortestPath.size();
    }
}
