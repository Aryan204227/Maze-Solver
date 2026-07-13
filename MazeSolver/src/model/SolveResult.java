package model;

/**
 * SolveResult - Stores all statistics produced after the solver finishes.
 *
 * WHY THIS CLASS EXISTS:
 *   We need a clean way to carry results from the algorithm layer
 *   to the UI. This is a simple data-holder (POJO).
 *
 * JAVA CONCEPTS USED: Encapsulation, OOP, Data Class
 */
public class SolveResult {

    private boolean solved;
    private long executionTimeMs;
    private int visitedCount;
    private int maxDepth;
    private int solutionCount;
    private int shortestPathLength;

    // Default constructor - all values start at zero
    public SolveResult() {
        this.solved = false;
        this.executionTimeMs = 0;
        this.visitedCount = 0;
        this.maxDepth = 0;
        this.solutionCount = 0;
        this.shortestPathLength = 0;
    }

    // --- Getters ---
    public boolean isSolved()           { return solved; }
    public long getExecutionTimeMs()    { return executionTimeMs; }
    public int getVisitedCount()        { return visitedCount; }
    public int getMaxDepth()            { return maxDepth; }
    public int getSolutionCount()       { return solutionCount; }
    public int getShortestPathLength()  { return shortestPathLength; }

    // Alias getters used by view panels
    public int  getCellsVisited()  { return visitedCount; }
    public long getElapsedMs()     { return executionTimeMs; }

    // --- Setters ---
    public void setSolved(boolean solved)                   { this.solved = solved; }
    public void setExecutionTimeMs(long executionTimeMs)    { this.executionTimeMs = executionTimeMs; }
    public void setVisitedCount(int visitedCount)           { this.visitedCount = visitedCount; }
    public void setMaxDepth(int maxDepth)                   { this.maxDepth = maxDepth; }
    public void setSolutionCount(int solutionCount)         { this.solutionCount = solutionCount; }
    public void setShortestPathLength(int length)           { this.shortestPathLength = length; }

    @Override
    public String toString() {
        return "Solved: " + solved
            + " | Solutions: " + solutionCount
            + " | Visited: " + visitedCount
            + " | Depth: " + maxDepth
            + " | Path Length: " + shortestPathLength
            + " | Time: " + executionTimeMs + " ms";
    }
}
