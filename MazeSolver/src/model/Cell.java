package model;

/**
 * Cell - Enum representing every possible state of a single maze cell.
 *
 * WHY THIS CLASS EXISTS:
 *   Instead of using integers like 0, 1, 2 (which are hard to read),
 *   we use an enum so the code reads like plain English.
 *
 * JAVA CONCEPT USED: Enum (Enumeration)
 */
public enum Cell {
    PATH,      // Empty walkable cell (white/light)
    WALL,      // Blocked cell (dark/black)
    START,     // Where the solver begins (blue)
    END,       // The target destination (orange)
    VISITED,   // Explored by the solver (purple)
    FOUND,     // Part of the final solution path (green)
    DEAD_END   // Explored but led nowhere - backtracked (red)
}
