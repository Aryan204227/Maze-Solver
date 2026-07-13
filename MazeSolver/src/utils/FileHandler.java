package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import model.CellType;
import model.InvalidMazeException;
import model.Maze;
import model.Position;
import model.SolveStatistics;

/**
 * Handles all file operations, including saving and opening mazes, 
 * exporting statistical reports, and validating file formats.
 */
public class FileHandler {

    /**
     * Saves the maze configuration to a text-based format.
     *
     * @param maze the maze to save
     * @param file the target file to write to
     * @throws IOException if a write error occurs
     */
    public static void saveMaze(Maze maze, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            int rows = maze.getRows();
            int cols = maze.getCols();
            
            // Header: dimensions
            writer.write(rows + " " + cols);
            writer.newLine();
            
            // Start position
            Position start = maze.getStartPosition();
            if (start != null) {
                writer.write(start.getRow() + " " + start.getCol());
            } else {
                writer.write("-1 -1");
            }
            writer.newLine();

            // End position
            Position end = maze.getEndPosition();
            if (end != null) {
                writer.write(end.getRow() + " " + end.getCol());
            } else {
                writer.write("-1 -1");
            }
            writer.newLine();

            // Grid content
            for (int r = 0; r < rows; r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < cols; c++) {
                    CellType type = maze.getCell(new Position(r, c));
                    switch (type) {
                        case WALL:
                            sb.append('#');
                            break;
                        case START:
                            sb.append('S');
                            break;
                        case END:
                            sb.append('E');
                            break;
                        case PATH:
                        default:
                            sb.append('.');
                            break;
                    }
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }

    /**
     * Loads a maze configuration from a file.
     *
     * @param file the source file to read from
     * @return the constructed Maze
     * @throws IOException            if a read error occurs
     * @throws InvalidMazeException if the file contains incorrect format or invalid dimensions
     */
    public static Maze loadMaze(File file) throws IOException, InvalidMazeException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            if (header == null || header.trim().isEmpty()) {
                throw new InvalidMazeException("The file is empty.");
            }

            // Parse dimensions
            String[] dimParts = header.trim().split("\\s+");
            if (dimParts.length != 2) {
                throw new InvalidMazeException("Invalid header format. Expected 'rows cols'.");
            }

            int rows, cols;
            try {
                rows = Integer.parseInt(dimParts[0]);
                cols = Integer.parseInt(dimParts[1]);
            } catch (NumberFormatException e) {
                throw new InvalidMazeException("Dimensions must be integer numbers.");
            }

            if (rows < Maze.MIN_SIZE || rows > Maze.MAX_SIZE || cols < Maze.MIN_SIZE || cols > Maze.MAX_SIZE) {
                throw new InvalidMazeException(
                    "Dimensions must be between " + Maze.MIN_SIZE + " and " + Maze.MAX_SIZE + "."
                );
            }

            // Create Maze instance
            Maze maze = new Maze(rows, cols);

            // Parse Start position
            String startLine = reader.readLine();
            if (startLine == null) {
                throw new InvalidMazeException("Missing start position coordinate.");
            }
            String[] startParts = startLine.trim().split("\\s+");
            Position startPos = null;
            if (startParts.length == 2) {
                int sr = Integer.parseInt(startParts[0]);
                int sc = Integer.parseInt(startParts[1]);
                if (sr != -1 && sc != -1) {
                    startPos = new Position(sr, sc);
                }
            }

            // Parse End position
            String endLine = reader.readLine();
            if (endLine == null) {
                throw new InvalidMazeException("Missing end position coordinate.");
            }
            String[] endParts = endLine.trim().split("\\s+");
            Position endPos = null;
            if (endParts.length == 2) {
                int er = Integer.parseInt(endParts[0]);
                int ec = Integer.parseInt(endParts[1]);
                if (er != -1 && ec != -1) {
                    endPos = new Position(er, ec);
                }
            }

            // Parse grid content
            for (int r = 0; r < rows; r++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new InvalidMazeException("File is shorter than expected rows count: " + rows);
                }
                if (line.length() < cols) {
                    throw new InvalidMazeException("Line " + (r + 4) + " is shorter than expected columns count: " + cols);
                }

                for (int c = 0; c < cols; c++) {
                    char ch = line.charAt(c);
                    Position cellPos = new Position(r, c);
                    switch (ch) {
                        case '#':
                            maze.setCell(cellPos, CellType.WALL);
                            break;
                        case 'S':
                            maze.setCell(cellPos, CellType.START);
                            break;
                        case 'E':
                            maze.setCell(cellPos, CellType.END);
                            break;
                        case '.':
                        default:
                            maze.setCell(cellPos, CellType.PATH);
                            break;
                    }
                }
            }

            // Restore start/end validation
            if (startPos != null && maze.isWithinBounds(startPos)) {
                maze.setCell(startPos, CellType.START);
            }
            if (endPos != null && maze.isWithinBounds(endPos)) {
                maze.setCell(endPos, CellType.END);
            }

            if (maze.getStartPosition() == null || maze.getEndPosition() == null) {
                throw new InvalidMazeException("Loaded maze must contain both S (START) and E (END) cells.");
            }

            return maze;
        } catch (Exception e) {
            if (e instanceof InvalidMazeException) {
                throw (InvalidMazeException) e;
            }
            throw new InvalidMazeException("Failed to read maze config file: " + e.getMessage());
        }
    }

    /**
     * Exports a comprehensive solution report in text format.
     */
    public static void exportReport(File file, Maze maze, SolveStatistics stats) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("=================================================");
            writer.newLine();
            writer.write("          MAZE SOLVER SOLUTION REPORT            ");
            writer.newLine();
            writer.write("=================================================");
            writer.newLine();
            writer.newLine();
            
            writer.write("Grid Dimensions: " + maze.getRows() + " rows x " + maze.getCols() + " columns");
            writer.newLine();
            writer.write("Start Position : " + maze.getStartPosition());
            writer.newLine();
            writer.write("End Position   : " + maze.getEndPosition());
            writer.newLine();
            writer.newLine();
            
            writer.write("---------------- STATISTICS -------------------");
            writer.newLine();
            writer.write("Execution Time         : " + stats.getExecutionTimeMs() + " ms");
            writer.newLine();
            writer.write("Visited Cells          : " + stats.getVisitedNodes());
            writer.newLine();
            writer.write("Max Recursion Depth    : " + stats.getMaxRecursionDepth());
            writer.newLine();
            writer.write("Total Solutions Found  : " + stats.getTotalSolutions());
            writer.newLine();
            writer.write("Shortest Path Length   : " + (stats.getShortestPathLength() > 0 ? stats.getShortestPathLength() + " cells" : "N/A"));
            writer.newLine();
            writer.newLine();
            
            writer.write("--------------- SHORTEST PATH -----------------");
            writer.newLine();
            List<Position> path = stats.getShortestPath();
            if (path == null || path.isEmpty()) {
                writer.write("No path exists between start and end.");
            } else {
                for (int i = 0; i < path.size(); i++) {
                    writer.write(path.get(i).toString());
                    if (i < path.size() - 1) {
                        writer.write(" -> ");
                    }
                    if ((i + 1) % 6 == 0) {
                        writer.newLine(); // Wrap for readability
                    }
                }
            }
            writer.newLine();
            writer.newLine();
            writer.write("=================================================");
            writer.newLine();
        }
    }
}
