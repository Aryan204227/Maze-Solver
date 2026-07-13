package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import model.CellType;
import model.Maze;
import model.Position;

/**
 * Implements algorithms for generating mazes automatically.
 * Supports Randomized DFS (perfect maze generator) and Simple Random Noise.
 */
public class RandomMazeGenerator {
    private static final Random random = new Random();

    /**
     * Generates a random maze based on a basic noise density.
     *
     * @param maze         the maze model to modify
     * @param wallProbability probability (0.0 to 1.0) of a cell being a wall
     */
    public static void generateNoise(Maze maze, double wallProbability) {
        maze.clearAll();
        int rows = maze.getRows();
        int cols = maze.getCols();
        Position start = maze.getStartPosition();
        Position end = maze.getEndPosition();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Keep borders as walls
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    maze.setCell(new Position(r, c), CellType.WALL);
                    continue;
                }
                
                Position current = new Position(r, c);
                if (current.equals(start) || current.equals(end)) {
                    continue; // Skip start/end
                }

                if (random.nextDouble() < wallProbability) {
                    maze.setCell(current, CellType.WALL);
                } else {
                    maze.setCell(current, CellType.PATH);
                }
            }
        }
    }

    /**
     * Generates a perfect maze using the Randomized DFS algorithm.
     * Guaranteed to have solvable paths and realistic branching structures.
     * Works best with odd row and column counts.
     *
     * @param maze the maze model to modify
     */
    public static void generatePerfectMaze(Maze maze) {
        maze.clearAll();
        int rows = maze.getRows();
        int cols = maze.getCols();

        // 1. Fill the entire maze with walls
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                maze.setCell(new Position(r, c), CellType.WALL);
            }
        }

        // 2. Run Randomized DFS carving starting from (1, 1)
        boolean[][] visited = new boolean[rows][cols];
        carveDFS(maze, new Position(1, 1), visited);

        // 3. Re-assign START and END positions on open paths
        Position start = new Position(1, 1);
        Position end = new Position(rows - 2, cols - 2);
        
        maze.setCell(start, CellType.START);
        maze.setCell(end, CellType.END);
        
        // Ensure start and end neighbors are clear paths
        maze.setCell(start.move(0, 1), CellType.PATH);
        maze.setCell(end.move(0, -1), CellType.PATH);
    }

    /**
     * Recursive helper to carve paths by stepping 2 cells at a time.
     */
    private static void carveDFS(Maze maze, Position current, boolean[][] visited) {
        int r = current.getRow();
        int c = current.getCol();
        visited[r][c] = true;
        maze.setCell(current, CellType.PATH);

        // Define moves of 2 cells to maintain wall divisions
        int[][] moves = {
            {-2, 0}, // Up
            {0, 2},  // Right
            {2, 0},  // Down
            {0, -2}  // Left
        };

        // Randomize order of directions
        List<int[]> directions = new ArrayList<>();
        for (int[] move : moves) {
            directions.add(move);
        }
        Collections.shuffle(directions, random);

        for (int[] dir : directions) {
            Position neighbor = current.move(dir[0], dir[1]);
            
            // Check boundaries
            if (neighbor.getRow() > 0 && neighbor.getRow() < maze.getRows() - 1 &&
                neighbor.getCol() > 0 && neighbor.getCol() < maze.getCols() - 1) {
                
                if (!visited[neighbor.getRow()][neighbor.getCol()]) {
                    // Carve through the intermediate cell
                    Position wallBetween = current.move(dir[0] / 2, dir[1] / 2);
                    maze.setCell(wallBetween, CellType.PATH);
                    
                    // Recursive carve
                    carveDFS(maze, neighbor, visited);
                }
            }
        }
    }
}
