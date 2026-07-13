package algorithm;

import model.Cell;
import model.Maze;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * MazeGenerator - Creates a random perfect maze using Randomized DFS.
 *
 * WHY THIS CLASS EXISTS:
 *   So users can generate interesting mazes instantly instead of
 *   drawing them manually. A "perfect maze" has exactly one path
 *   between any two points.
 *
 * HOW IT WORKS:
 *   Starts at a cell, randomly picks an unvisited neighbor 2 steps away,
 *   knocks down the wall between them, and recursively continues.
 *   This is called "Recursive Backtracker" maze generation.
 *
 * JAVA CONCEPTS USED: Recursion, ArrayList, Collections.shuffle(), Random
 */
public class MazeGenerator {

    private static final Random random = new Random();

    /**
     * Generates a random perfect maze into the given Maze object.
     * The maze must have odd dimensions for this algorithm to work correctly.
     */
    public static void generate(Maze maze) {
        int rows = maze.getRows();
        int cols = maze.getCols();

        // Step 1: Fill everything with walls
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                maze.setCell(r, c, Cell.WALL);
            }
        }

        // Step 2: Carve paths using randomized DFS starting from (1,1)
        carve(maze, 1, 1);

        // Step 3: Restore START and END positions
        maze.setCell(maze.getStartRow(), maze.getStartCol(), Cell.START);
        maze.setCell(maze.getEndRow(), maze.getEndCol(), Cell.END);
    }

    /**
     * Recursive carving function. Moves 2 cells at a time and
     * knocks down the wall between current cell and chosen neighbor.
     */
    private static void carve(Maze maze, int row, int col) {
        maze.setCell(row, col, Cell.PATH);

        // Shuffle directions so the maze is random each time
        List<int[]> directions = new ArrayList<int[]>();
        directions.add(new int[]{ -2, 0 }); // Up
        directions.add(new int[]{  2, 0 }); // Down
        directions.add(new int[]{ 0, -2 }); // Left
        directions.add(new int[]{ 0,  2 }); // Right
        Collections.shuffle(directions, random);

        for (int[] dir : directions) {
            int nextRow = row + dir[0];
            int nextCol = col + dir[1];

            // Check if the neighbor is inside bounds and still a wall (unvisited)
            if (maze.isInBounds(nextRow, nextCol) && maze.isWall(nextRow, nextCol)) {
                // Knock down the wall between current and next
                int wallRow = row + dir[0] / 2;
                int wallCol = col + dir[1] / 2;
                maze.setCell(wallRow, wallCol, Cell.PATH);

                // Recurse into the neighbor
                carve(maze, nextRow, nextCol);
            }
        }
    }
}
