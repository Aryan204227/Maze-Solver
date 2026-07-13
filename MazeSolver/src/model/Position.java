package model;

import java.util.Objects;

/**
 * Represents a coordinate in the 2D grid of the maze.
 * This class is immutable.
 */
public class Position {
    private final int row;
    private final int col;

    /**
     * Constructs a new position with the specified row and column coordinates.
     *
     * @param row the grid row index
     * @param col the grid column index
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row index.
     *
     * @return the row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column index.
     *
     * @return the column index
     */
    public int getCol() {
        return col;
    }

    /**
     * Returns a new position representing a step in the specified direction.
     *
     * @param dRow the row offset
     * @param dCol the column offset
     * @return the new Position object
     */
    public Position move(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
