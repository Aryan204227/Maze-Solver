package com.mazesolver.algorithm;

import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * MazeGenerator.java
 * ─────────────────────────────────────────────────────
 * Generates a random maze using the Recursive Backtracker algorithm.
 *
 * WHY THIS CLASS EXISTS:
 *   Instead of users drawing mazes by hand, we can generate
 *   random, beautiful mazes automatically. This gives the
 *   solver interesting problems to solve.
 *
 * HOW IT WORKS (Recursive Backtracker):
 *   1. Fill everything with WALLS.
 *   2. Start at (0,0), mark it as OPEN.
 *   3. Randomly pick an unvisited neighbor 2 steps away.
 *   4. Carve a passage (open the wall between them).
 *   5. Recurse from the new cell.
 *   6. If stuck, BACKTRACK to try another direction.
 *   Result: A perfect maze — every cell is reachable.
 *
 * NOTE: We work on cells 2 steps apart so walls are preserved
 *       between "room" cells. This creates clean corridors.
 *
 * JAVA CONCEPTS USED:
 *   - Recursion + Backtracking (same concept as solver!)
 *   - Random, ArrayList, Collections.shuffle
 *   - 2D boolean array for visited tracking
 */
public class MazeGenerator {

    private final Maze   maze;
    private final Random random;

    // We move in steps of 2 to leave walls between rooms
    private static final int[] MOVE_ROW = { -2, 2,  0, 0 };
    private static final int[] MOVE_COL = {  0, 0, -2, 2 };

    public MazeGenerator(Maze maze) {
        this.maze   = maze;
        this.random = new Random();
    }

    /**
     * Generates a new random maze with difficulty.
     * Fills the grid with walls, then carves passages recursively.
     */
    public void generate() {
        generate("medium");
    }

    public void generate(String difficulty) {
        int rows = maze.getRows();
        int cols = maze.getCols();

        // Step 1: Fill everything with walls
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                maze.setWall(r, c);
            }
        }

        // Step 2: Carve passages starting from top-left
        boolean[][] visited = new boolean[rows][cols];
        carvePath(0, 0, visited);

        // Step 3: Always ensure start and end are open
        maze.getCell(0, 0).setState(Cell.State.START);
        maze.getCell(rows - 1, cols - 1).setState(Cell.State.END);

        // Step 4: Open some extra passages for multiple solutions based on difficulty
        if ("easy".equalsIgnoreCase(difficulty)) {
            addRandomOpeningsPercentage(rows, cols, 25);
        } else if ("medium".equalsIgnoreCase(difficulty)) {
            addRandomOpeningsPercentage(rows, cols, 10);
        } else {
            // "hard" - keep it perfect (exactly 1 solution)
        }
    }

    private void addRandomOpeningsPercentage(int rows, int cols, int percentage) {
        int extraOpenings = (rows * cols) * percentage / 100;
        for (int i = 0; i < extraOpenings; i++) {
            int r = 1 + random.nextInt(rows - 2);
            int c = 1 + random.nextInt(cols - 2);
            Cell cell = maze.getCell(r, c);
            if (cell.isWall() && !cell.isEndpoint()) {
                cell.setState(Cell.State.OPEN);
            }
        }
    }


    /**
     * Recursive Backtracker — carves a maze by visiting cells
     * and opening walls between them.
     *
     * @param row     current row
     * @param col     current column
     * @param visited tracks which "room" cells have been carved
     */
    private void carvePath(int row, int col, boolean[][] visited) {
        visited[row][col] = true;
        maze.setOpen(row, col); // carve this cell open

        // Get shuffled directions for randomness
        List<Integer> directions = getShuffledDirections();

        for (int dir : directions) {
            int nextRow = row + MOVE_ROW[dir];
            int nextCol = col + MOVE_COL[dir];

            // Check if the next "room" is valid and unvisited
            if (maze.isInBounds(nextRow, nextCol) && !visited[nextRow][nextCol]) {
                // Carve the WALL between current and next room
                int wallRow = row + MOVE_ROW[dir] / 2;
                int wallCol = col + MOVE_COL[dir] / 2;
                maze.setOpen(wallRow, wallCol);

                // Recurse into the next room
                carvePath(nextRow, nextCol, visited);
            }
        }
        // If no unvisited neighbors → BACKTRACK (just return)
    }

    /**
     * Returns a shuffled list of direction indices [0,1,2,3].
     * Shuffling makes the maze look different each time.
     */
    private List<Integer> getShuffledDirections() {
        List<Integer> dirs = new ArrayList<>();
        dirs.add(0); // Up
        dirs.add(1); // Down
        dirs.add(2); // Left
        dirs.add(3); // Right
        Collections.shuffle(dirs, random);
        return dirs;
    }

    /**
     * Randomly removes some extra walls to create multiple solution paths.
     * Without this, the recursive backtracker creates only ONE solution.
     *
     * @param rows total rows
     * @param cols total columns
     */
    private void addRandomOpenings(int rows, int cols) {
        // Open ~10% of interior walls randomly
        int extraOpenings = (rows * cols) / 10;

        for (int i = 0; i < extraOpenings; i++) {
            int r = 1 + random.nextInt(rows - 2);
            int c = 1 + random.nextInt(cols - 2);
            Cell cell = maze.getCell(r, c);
            if (cell.isWall() && !cell.isEndpoint()) {
                cell.setState(Cell.State.OPEN);
            }
        }
    }

    /** Generates a spiral wall pattern winding towards the center. */
    public void generateSpiral() {
        int rows = maze.getRows();
        int cols = maze.getCols();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                maze.setOpen(r, c);
            }
        }
        int top = 1, bottom = rows - 2, left = 1, right = cols - 2;
        while (top <= bottom && left <= right) {
            for (int c = left - 1; c <= right + 1; c++) {
                if (top - 1 >= 0) maze.setWall(top - 1, c);
            }
            for (int r = top - 1; r <= bottom + 1; r++) {
                if (right + 1 < cols) maze.setWall(r, right + 1);
            }
            for (int c = left - 1; c <= right + 1; c++) {
                if (bottom + 1 < rows) maze.setWall(bottom + 1, c);
            }
            for (int r = top + 1; r <= bottom + 1; r++) {
                if (left - 1 >= 0) maze.setWall(r, left - 1);
            }
            top += 2;
            bottom -= 2;
            left += 2;
            right -= 2;
        }
        maze.getCell(0, 0).setState(Cell.State.START);
        maze.getCell(rows - 1, cols - 1).setState(Cell.State.END);
    }

    /** Generates a snake-like single long corridor winding across rows. */
    public void generateCorridor() {
        int rows = maze.getRows();
        int cols = maze.getCols();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r % 2 == 1) {
                    maze.setWall(r, c);
                } else {
                    maze.setOpen(r, c);
                }
            }
        }
        for (int r = 1; r < rows; r += 2) {
            if ((r / 2) % 2 == 0) {
                maze.setOpen(r, cols - 1);
                if (r + 1 < rows) maze.setOpen(r + 1, cols - 1);
            } else {
                maze.setOpen(r, 0);
                if (r + 1 < rows) maze.setOpen(r + 1, 0);
            }
        }
        maze.getCell(0, 0).setState(Cell.State.START);
        maze.getCell(rows - 1, cols - 1).setState(Cell.State.END);
    }

    /** Generates a checkerboard pattern of alternating wall cells. */
    public void generateCheckerboard() {
        int rows = maze.getRows();
        int cols = maze.getCols();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if ((r + c) % 2 == 1) {
                    maze.setWall(r, c);
                } else {
                    maze.setOpen(r, c);
                }
            }
        }
        maze.getCell(0, 0).setState(Cell.State.START);
        maze.getCell(rows - 1, cols - 1).setState(Cell.State.END);
    }
}
