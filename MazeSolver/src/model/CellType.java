package model;

/**
 * Represents the type/state of a cell in the maze.
 */
public enum CellType {
    /** Represents an impassable obstacle cell. */
    WALL,
    
    /** Represents a path that can be traversed. */
    PATH,
    
    /** The starting cell of the maze. */
    START,
    
    /** The destination cell of the maze. */
    END,
    
    /** Marks a cell that was visited during solver traversal. */
    VISITED,
    
    /** Marks a cell that led to a dead-end during solver backtracking. */
    DEAD_END,
    
    /** Marks a cell that is part of the final correct solution path. */
    CORRECT_PATH
}
