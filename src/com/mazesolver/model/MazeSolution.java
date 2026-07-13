package com.mazesolver.model;

import java.util.ArrayList;
import java.util.List;

/**
 * MazeSolution.java
 * ─────────────────────────────────────────────────────
 * Stores one complete, valid solution path from Start → End.
 * Each step in the path is a {row, col} coordinate pair.
 *
 * WHY THIS CLASS EXISTS:
 *   The solver may find multiple solutions.
 *   We store each one as a MazeSolution object so we can
 *   display them, compare lengths, and show statistics.
 *
 * JAVA CONCEPTS USED:
 *   - ArrayList (dynamic list of steps)
 *   - Encapsulation
 *   - int[] to store row/col pairs
 */
public class MazeSolution {

    private final int        solutionNumber;  // which solution is this? (1st, 2nd, etc.)
    private final List<int[]> path;           // ordered list of cells: each int[] = {row, col}

    public MazeSolution(int solutionNumber) {
        this.solutionNumber = solutionNumber;
        this.path           = new ArrayList<>();
    }

    /**
     * Adds one step (cell) to this solution's path.
     * @param row row index of the cell
     * @param col column index of the cell
     */
    public void addStep(int row, int col) {
        path.add(new int[]{row, col});
    }

    // ── Getters ───────────────────────────────────────────

    /** Returns the solution number (1 = first found, 2 = second, etc.) */
    public int getSolutionNumber() { return solutionNumber; }

    /** Returns the full list of steps (coordinates) in this solution */
    public List<int[]> getPath()   { return path; }

    /** Returns the number of cells in this solution path */
    public int getLength()         { return path.size(); }

    @Override
    public String toString() {
        return "Solution #" + solutionNumber + " (Length: " + path.size() + ")";
    }
}
