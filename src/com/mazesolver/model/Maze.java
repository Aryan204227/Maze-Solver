package com.mazesolver.model;

/**
 * Maze.java
 * ─────────────────────────────────────────────────────
 * The main data model — stores the maze as a 2D grid of Cell objects.
 * Think of it as the "brain" of the maze.
 *
 * WHY THIS CLASS EXISTS:
 *   The Maze is the central data structure. Every other class
 *   either reads from it (MazePanel) or writes to it (MazeSolver).
 *   It owns the grid and provides clean methods to modify it.
 *
 * HOW IT WORKS:
 *   Internally: Cell[][] grid  (2D array of Cell objects)
 *   Start: always top-left     (row=0, col=0)
 *   End:   always bottom-right (row=rows-1, col=cols-1)
 *
 * JAVA CONCEPTS USED:
 *   - 2D Arrays
 *   - Encapsulation
 *   - OOP (Maze owns its Cells)
 */
public class Maze {

    // ── Grid data ─────────────────────────────────────────
    private int    rows;
    private int    cols;
    private Cell[][] grid;

    // ── Start / End positions ─────────────────────────────
    private int startRow, startCol;
    private int endRow,   endCol;

    // ── Constructor ───────────────────────────────────────

    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        initGrid();
    }

    /**
     * Initializes (or re-initializes) the grid.
     * Creates all cells as OPEN, then marks START and END.
     */
    private void initGrid() {
        grid = new Cell[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c, Cell.State.OPEN);
            }
        }

        // Default: Start = top-left, End = bottom-right
        startRow = 0;
        startCol = 0;
        endRow   = rows - 1;
        endCol   = cols - 1;

        grid[startRow][startCol].setState(Cell.State.START);
        grid[endRow][endCol].setState(Cell.State.END);
    }

    // ── Maze Modification ─────────────────────────────────

    /**
     * Toggles a cell between WALL and OPEN.
     * Does nothing if the cell is START or END.
     */
    public void toggleWall(int row, int col) {
        if (!isInBounds(row, col)) return;

        Cell cell = grid[row][col];
        if (cell.isEndpoint()) return; // protect start/end

        if (cell.isWall()) {
            cell.setState(Cell.State.OPEN);
        } else {
            cell.setState(Cell.State.WALL);
        }
    }

    /**
     * Sets a cell as a WALL directly (used by MazeGenerator).
     * Does not affect START or END cells.
     */
    public void setWall(int row, int col) {
        if (!isInBounds(row, col)) return;
        Cell cell = grid[row][col];
        if (!cell.isEndpoint()) {
            cell.setState(Cell.State.WALL);
        }
    }

    /**
     * Sets a cell as OPEN directly (used by MazeGenerator).
     * Does not affect START or END cells.
     */
    public void setOpen(int row, int col) {
        if (!isInBounds(row, col)) return;
        Cell cell = grid[row][col];
        if (!cell.isEndpoint()) {
            cell.setState(Cell.State.OPEN);
        }
    }

    /**
     * Resets only the solving-related states (VISITED, CURRENT, BACKTRACK, SOLUTION).
     * Walls and open cells are preserved.
     * Called before each new solve attempt.
     */
    public void resetSolvingState() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                cell.setDepth(0);
                Cell.State s = cell.getState();
                if (s == Cell.State.VISITED   ||
                    s == Cell.State.CURRENT   ||
                    s == Cell.State.BACKTRACK ||
                    s == Cell.State.SOLUTION  ||
                    s == Cell.State.DEAD_END) {
                    cell.setState(Cell.State.OPEN);
                }
            }
        }
        // Always restore start/end markers
        grid[startRow][startCol].setState(Cell.State.START);
        grid[endRow][endCol].setState(Cell.State.END);
    }

    /**
     * Completely clears the maze — all cells become OPEN.
     * Resets to default size and resets start/end.
     */
    public void clearAll() {
        initGrid();
    }

    /**
     * Resizes the maze to a new dimension.
     * All existing content is lost.
     */
    public void resize(int newRows, int newCols) {
        this.rows = newRows;
        this.cols = newCols;
        initGrid();
    }

    /**
     * Sets a new Start position and updates cell states.
     */
    public void setStart(int row, int col) {
        if (!isInBounds(row, col)) return;
        grid[startRow][startCol].setState(Cell.State.OPEN);
        startRow = row;
        startCol = col;
        grid[startRow][startCol].setState(Cell.State.START);
    }

    /**
     * Sets a new End position and updates cell states.
     */
    public void setEnd(int row, int col) {
        if (!isInBounds(row, col)) return;
        grid[endRow][endCol].setState(Cell.State.OPEN);
        endRow = row;
        endCol = col;
        grid[endRow][endCol].setState(Cell.State.END);
    }

    // ── Getters ───────────────────────────────────────────

    public int       getRows()     { return rows;     }
    public int       getCols()     { return cols;     }
    public Cell[][]  getGrid()     { return grid;     }
    public int       getStartRow() { return startRow; }
    public int       getStartCol() { return startCol; }
    public int       getEndRow()   { return endRow;   }
    public int       getEndCol()   { return endCol;   }

    /** Returns the Cell at (row, col). Always use isInBounds() first. */
    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    /**
     * Checks if a given position is within maze boundaries.
     * Used as a guard in the recursive solver.
     */
    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}
