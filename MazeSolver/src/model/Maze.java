package model;

/**
 * Maze - Holds the 2D grid of cells, and the start/end positions.
 *
 * WHY THIS CLASS EXISTS:
 *   This is the core data model. It stores the maze as a 2D array
 *   and provides simple methods to read/write cells.
 *
 * JAVA CONCEPTS USED: 2D Arrays, Encapsulation, OOP
 */
public class Maze {

    private Cell[][] grid;
    private int rows;
    private int cols;
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;

    // Default maze size
    public static final int DEFAULT_ROWS = 21;
    public static final int DEFAULT_COLS = 21;

    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        // Default start = top-left inner cell, end = bottom-right inner cell
        this.startRow = 1;
        this.startCol = 1;
        this.endRow = rows - 2;
        this.endCol = cols - 2;
        buildDefault();
    }

    /**
     * Fills the grid: border cells are walls, inner cells are paths.
     * Then places START and END markers.
     */
    private void buildDefault() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Border = WALL, inner = PATH
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    grid[r][c] = Cell.WALL;
                } else {
                    grid[r][c] = Cell.PATH;
                }
            }
        }
        grid[startRow][startCol] = Cell.START;
        grid[endRow][endCol] = Cell.END;
    }

    /**
     * Removes VISITED, FOUND, and DEAD_END markings so the maze
     * looks clean again before a new solve.
     */
    public void clearSolverMarkings() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                if (cell == Cell.VISITED || cell == Cell.FOUND || cell == Cell.DEAD_END) {
                    grid[r][c] = Cell.PATH;
                }
            }
        }
        // Make sure START and END are still marked
        grid[startRow][startCol] = Cell.START;
        grid[endRow][endCol] = Cell.END;
    }

    /**
     * Resets the entire grid back to the default state (all inner cells = PATH).
     */
    public void resetAll() {
        buildDefault();
    }

    /**
     * Creates a deep copy of this maze so the solver can work
     * on a copy without changing the original.
     */
    public Maze copy() {
        Maze copy = new Maze(rows, cols);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                copy.grid[r][c] = this.grid[r][c];
            }
        }
        copy.startRow = this.startRow;
        copy.startCol = this.startCol;
        copy.endRow = this.endRow;
        copy.endCol = this.endCol;
        return copy;
    }

    // --- Getters and Setters ---

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    public void setCell(int row, int col, Cell cell) {
        grid[row][col] = cell;
    }

    public boolean isWall(int row, int col) {
        return grid[row][col] == Cell.WALL;
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public int getRows()     { return rows; }
    public int getCols()     { return cols; }
    public int getStartRow() { return startRow; }
    public int getStartCol() { return startCol; }
    public int getEndRow()   { return endRow; }
    public int getEndCol()   { return endCol; }
    public Cell[][] getGrid(){ return grid; }

    public void setStart(int row, int col) {
        grid[startRow][startCol] = Cell.PATH; // clear old start
        startRow = row;
        startCol = col;
        grid[row][col] = Cell.START;
    }

    public void setEnd(int row, int col) {
        grid[endRow][endCol] = Cell.PATH; // clear old end
        endRow = row;
        endCol = col;
        grid[row][col] = Cell.END;
    }
}
