package com.mazesolver.model;

/**
 * SolverStats.java
 * ─────────────────────────────────────────────────────
 * Tracks all statistics collected during the maze solving process.
 * Updated live while the solver runs.
 *
 * WHY THIS CLASS EXISTS:
 *   We want to show the user how the algorithm performed:
 *   How many cells were visited? How deep did recursion go?
 *   How many solutions exist? This class collects all that.
 *
 * JAVA CONCEPTS USED:
 *   - Encapsulation
 *   - System.currentTimeMillis() for timing
 *   - Integer.MAX_VALUE as "infinity" initial value
 */
public class SolverStats {

    // ── Timing ────────────────────────────────────────────
    private long startTimeMs;      // when solving started
    private long executionTimeMs;  // total time taken

    // ── Counters ──────────────────────────────────────────
    private int visitedNodes;       // how many cells were explored
    private int recursiveCalls;     // total recursive function calls
    private int maxDepth;           // deepest recursion level reached
    private int totalSolutions;     // how many paths reached the END
    private int shortestPathLength; // smallest solution path length
    private int backtrackCount;     // how many times we backtracked
    // ── Version 2.0 / 3.0 Additions ──────────────────────
    private int currentDepth;           // current depth in active branch
    private int currentR = -1;          // active cell row index
    private int currentC = -1;          // active cell column index
    private String currentDirection = "—"; // e.g. "→ Right", "↓ Down"
    private String currentDecision  = "—"; // plain-English reason for current move
    private final java.util.List<String> currentStack = new java.util.ArrayList<>();

    // ── Constructor ───────────────────────────────────────
    public SolverStats() {
        reset();
    }

    /** Resets all stats to zero (call before each new solve) */
    public void reset() {
        startTimeMs       = 0;
        executionTimeMs   = 0;
        visitedNodes      = 0;
        recursiveCalls    = 0;
        maxDepth          = 0;
        totalSolutions    = 0;
        shortestPathLength= Integer.MAX_VALUE; // will be replaced by first solution
        backtrackCount    = 0;
        currentDepth      = 0;
        currentR          = -1;
        currentC          = -1;
        synchronized (currentStack) {
            currentStack.clear();
        }
    }

    // ── Timer Methods ─────────────────────────────────────

    public void startTimer() {
        startTimeMs = System.currentTimeMillis();
    }

    public void stopTimer() {
        executionTimeMs = System.currentTimeMillis() - startTimeMs;
    }

    // ── Increment Methods (called by MazeSolver) ──────────

    public void incrementVisited()   { visitedNodes++;   }
    public void incrementCalls()     { recursiveCalls++; }
    public void incrementBacktrack() { backtrackCount++;  }
    public void incrementSolutions() { totalSolutions++;  }

    public void updateMaxDepth(int depth) {
        if (depth > maxDepth) maxDepth = depth;
    }

    public void updateShortestPath(int pathLength) {
        if (pathLength < shortestPathLength) {
            shortestPathLength = pathLength;
        }
    }

    // ── V2.0 / V3.0 Setter Methods ────────────────────────
    public void setCurrentDepth(int depth)            { this.currentDepth     = depth;     }
    public void setCurrentCoords(int r, int c)        { this.currentR = r; this.currentC = c; }
    public void setCurrentDirection(String direction) { this.currentDirection = direction; }
    public void setCurrentDecision(String decision)   { this.currentDecision  = decision;  }

    public void pushToStack(int r, int c) {
        synchronized (currentStack) {
            currentStack.add("(" + r + "," + c + ")");
        }
    }

    public void popFromStack() {
        synchronized (currentStack) {
            if (!currentStack.isEmpty()) {
                currentStack.remove(currentStack.size() - 1);
            }
        }
    }

    // ── Getters ───────────────────────────────────────────

    public long   getExecutionTimeMs()    { return executionTimeMs;   }
    public int    getVisitedNodes()       { return visitedNodes;      }
    public int    getRecursiveCalls()     { return recursiveCalls;    }
    public int    getMaxDepth()           { return maxDepth;          }
    public int    getTotalSolutions()     { return totalSolutions;    }
    public int    getBacktrackCount()     { return backtrackCount;    }
    public int    getCurrentDepth()       { return currentDepth;      }
    public int    getCurrentR()           { return currentR;          }
    public int    getCurrentC()           { return currentC;          }
    public String getCurrentDirection()   { return currentDirection;  }
    public String getCurrentDecision()    { return currentDecision;   }

    public java.util.List<String> getCurrentStack() {
        synchronized (currentStack) {
            return new java.util.ArrayList<>(currentStack);
        }
    }

    /**
     * Calculates the solver accuracy percentage based on path efficiency.
     */
    public double getAccuracyScore() {
        if (visitedNodes == 0) return 0.0;
        int len = getShortestPathLength();
        if (len == 0) return 0.0;
        return Math.min(100.0, ((double) len / visitedNodes) * 100.0);
    }

    /**
     * Calculates efficiency percentage where fewer backtracks yield a higher score.
     */
    public double getEfficiencyScore() {
        if (visitedNodes == 0) return 0.0;
        return Math.max(0.0, 100.0 - (((double) backtrackCount / visitedNodes) * 100.0));
    }

    /**
     * Queries and formats active JVM Heap memory usage.
     */
    public String getMemoryUsage() {
        Runtime rt = Runtime.getRuntime();
        long usedBytes = rt.totalMemory() - rt.freeMemory();
        return String.format("%.1f MB", usedBytes / (1024.0 * 1024.0));
    }

    /**
     * Returns the shortest solution path length.
     * Returns 0 if no solution was found.
     */
    public int getShortestPathLength() {
        return (shortestPathLength == Integer.MAX_VALUE) ? 0 : shortestPathLength;
    }

    /** Restores stats from a historical snapshot for the Step/Replay mode. */
    public void restoreFromSnapshot(int visited, int calls, int maxD, int curD, int solutions, int backtracks, String direction, String decision, java.util.List<String> stack) {
        this.visitedNodes = visited;
        this.recursiveCalls = calls;
        this.maxDepth = maxD;
        this.currentDepth = curD;
        this.totalSolutions = solutions;
        this.backtrackCount = backtracks;
        this.currentDirection = direction;
        this.currentDecision = decision;
        synchronized (this.currentStack) {
            this.currentStack.clear();
            this.currentStack.addAll(stack);
        }
    }

    /** Returns execution time formatted as seconds (e.g., "0.032 s") */
    public String getFormattedTime() {
        return String.format("%.3f s", executionTimeMs / 1000.0);
    }
}
