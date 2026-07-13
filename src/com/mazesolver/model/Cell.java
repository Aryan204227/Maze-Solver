package com.mazesolver.model;

/**
 * Cell.java
 * ─────────────────────────────────────────────────────
 * Represents a single cell in the maze grid.
 * Each cell knows its (row, col) position and current state.
 *
 * WHY THIS CLASS EXISTS:
 *   The maze is a 2D grid of Cell objects.
 *   Each cell can be a wall, open path, start, end,
 *   currently explored, visited, backtracked, or part of a solution.
 *
 * JAVA CONCEPTS USED:
 *   - Enum (State) for fixed set of values
 *   - Encapsulation (private fields, getters/setters)
 *   - OOP — Cell is a real-world object in our domain
 */
public class Cell {

    /**
     * All possible states a cell can be in.
     * The state determines how the cell is drawn on screen.
     */
    public enum State {
        WALL,       // ██  Blocked — cannot pass through
        OPEN,       //     Empty path — can walk through
        START,      // S   Starting position
        END,        // E   Destination
        CURRENT,    // 🔵  Currently being explored (DFS frontier)
        VISITED,    // 🟣  Already visited in this path
        BACKTRACK,  // 🟠  Being backtracked over
        SOLUTION,   // 🟢  Part of a valid solution path
        DEAD_END    // 🔴  Dead end cell (no unvisited options remaining)
    }

    // ── Fields ────────────────────────────────────────────
    private final int row;   // row position in the grid (0-indexed)
    private final int col;   // column position in the grid (0-indexed)
    private State state;     // current visual/logical state
    private int depth;       // recursion depth when visited

    // ── Constructor ───────────────────────────────────────
    public Cell(int row, int col, State state) {
        this.row   = row;
        this.col   = col;
        this.state = state;
        this.depth = 0;
    }

    // ── Getters & Setters ─────────────────────────────────
    public int   getRow()   { return row;   }
    public int   getCol()   { return col;   }
    public State getState() { return state; }
    public void  setState(State state) { this.state = state; }
    public int   getDepth() { return depth; }
    public void  setDepth(int depth) { this.depth = depth; }

    // ── Convenience helpers ───────────────────────────────

    /** Returns true if this cell blocks movement (wall) */
    public boolean isWall() {
        return state == State.WALL;
    }

    /** Returns true if this cell can be walked on */
    public boolean isWalkable() {
        return state != State.WALL;
    }

    /** Returns true if this cell is the start or end */
    public boolean isEndpoint() {
        return state == State.START || state == State.END;
    }

    @Override
    public String toString() {
        return "Cell[" + row + "," + col + "=" + state + "]";
    }
}
