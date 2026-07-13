package com.mazesolver.controller;

import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import com.mazesolver.util.Constants;
import com.mazesolver.view.ToastNotification;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * FileController.java — VERSION 2.0 UPGRADED
 * Features:
 *   1. Auto-maintenance of a static list of the last 3 loaded/saved files.
 *   2. Decoupled loading logic allowing direct loads from external references.
 */
public class FileController {

    private final JFrame parentFrame;
    private static final List<File> recentFiles = new ArrayList<>();
    private static final String AUTOSAVE_FILENAME = "autosave.maze";

    public FileController(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Returns the list of last 3 loaded or saved files.
     */
    public static List<File> getRecentFiles() {
        synchronized (recentFiles) {
            return new ArrayList<>(recentFiles);
        }
    }

    private static void addRecentFile(File file) {
        if (file == null) return;
        synchronized (recentFiles) {
            recentFiles.remove(file); // Avoid duplicates
            recentFiles.add(0, file); // Push to top
            if (recentFiles.size() > 3) {
                recentFiles.remove(recentFiles.size() - 1); // Keep max 3
            }
        }
    }


    /**
     * Autosaves the current maze state to a local file.
     */
    public void autosave(Maze maze) {
        if (maze == null) return;
        File file = new File(AUTOSAVE_FILENAME);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ROWS=" + maze.getRows());
            writer.newLine();
            writer.write("COLS=" + maze.getCols());
            writer.newLine();
            writer.write("START=" + maze.getStartRow() + "," + maze.getStartCol());
            writer.newLine();
            writer.write("END=" + maze.getEndRow() + "," + maze.getEndCol());
            writer.newLine();
            writer.write("GRID=");
            writer.newLine();

            for (int r = 0; r < maze.getRows(); r++) {
                StringBuilder rowStr = new StringBuilder();
                for (int c = 0; c < maze.getCols(); c++) {
                    Cell cell = maze.getCell(r, c);
                    rowStr.append(cell.isWall() ? "1" : "0").append(" ");
                }
                writer.write(rowStr.toString().trim());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Autosave failed: " + e.getMessage());
        }
    }

    /**
     * Restores the autosaved maze if it exists.
     */
    public Maze loadAutosave() {
        File file = new File(AUTOSAVE_FILENAME);
        if (!file.exists()) return null;
        try {
            return loadMazeFromFileQuietly(file);
        } catch (Exception e) {
            System.err.println("Autosave load failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Internal quiet loader to avoid toasts during startup.
     */
    private Maze loadMazeFromFileQuietly(File fileToLoad) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
            int rows = 0;
            int cols = 0;
            int startR = 0, startC = 0;
            int endR = 0, endC = 0;
            boolean readingGrid = false;
            int currentRow = 0;
            
            Maze loadedMaze = null;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (!readingGrid) {
                    if (line.startsWith("ROWS=")) {
                        rows = Integer.parseInt(line.substring(5));
                    } else if (line.startsWith("COLS=")) {
                        cols = Integer.parseInt(line.substring(5));
                    } else if (line.startsWith("START=")) {
                        String[] parts = line.substring(6).split(",");
                        startR = Integer.parseInt(parts[0]);
                        startC = Integer.parseInt(parts[1]);
                    } else if (line.startsWith("END=")) {
                        String[] parts = line.substring(4).split(",");
                        endR = Integer.parseInt(parts[0]);
                        endC = Integer.parseInt(parts[1]);
                    } else if (line.startsWith("GRID=")) {
                        if (rows <= 0 || cols <= 0) {
                            throw new IOException("Invalid maze size declaration in file.");
                        }
                        loadedMaze = new Maze(rows, cols);
                        readingGrid = true;
                    }
                } else {
                    if (currentRow >= rows) break;
                    StringTokenizer tokenizer = new StringTokenizer(line, " ");
                    int currentCol = 0;
                    while (tokenizer.hasMoreTokens() && currentCol < cols) {
                        String cellVal = tokenizer.nextToken();
                        if (cellVal.equals("1")) {
                            loadedMaze.setWall(currentRow, currentCol);
                        } else {
                            loadedMaze.setOpen(currentRow, currentCol);
                        }
                        currentCol++;
                    }
                    currentRow++;
                }
            }

            if (loadedMaze == null || currentRow != rows) {
                throw new IOException("Corrupted grid layout. Row mismatch.");
            }

            loadedMaze.setStart(startR, startC);
            loadedMaze.setEnd(endR, endC);
            return loadedMaze;
        }
    }



    /**
     * Saves the given Maze state to file.
     */
    public boolean saveMaze(Maze maze) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Maze Configuration");
        fileChooser.setFileFilter(new FileNameExtensionFilter(Constants.FILE_DESCRIPTION, "maze"));

        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File fileToSave = fileChooser.getSelectedFile();
        String path = fileToSave.getAbsolutePath();
        if (!path.toLowerCase().endsWith(Constants.FILE_EXTENSION)) {
            path += Constants.FILE_EXTENSION;
            fileToSave = new File(path);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
            writer.write("ROWS=" + maze.getRows());
            writer.newLine();
            writer.write("COLS=" + maze.getCols());
            writer.newLine();
            writer.write("START=" + maze.getStartRow() + "," + maze.getStartCol());
            writer.newLine();
            writer.write("END=" + maze.getEndRow() + "," + maze.getEndCol());
            writer.newLine();
            writer.write("GRID=");
            writer.newLine();

            for (int r = 0; r < maze.getRows(); r++) {
                StringBuilder rowStr = new StringBuilder();
                for (int c = 0; c < maze.getCols(); c++) {
                    Cell cell = maze.getCell(r, c);
                    rowStr.append(cell.isWall() ? "1" : "0").append(" ");
                }
                writer.write(rowStr.toString().trim());
                writer.newLine();
            }

            addRecentFile(fileToSave);
            ToastNotification.showSuccess(parentFrame, "Maze saved: " + fileToSave.getName());
            return true;

        } catch (IOException e) {
            ToastNotification.showWarning(parentFrame, "Save failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Presents a Load Dialog and instantiates a new Maze from file data.
     */
    public Maze loadMaze() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Maze Configuration");
        fileChooser.setFileFilter(new FileNameExtensionFilter(Constants.FILE_DESCRIPTION, "maze"));

        int userSelection = fileChooser.showOpenDialog(parentFrame);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        return loadMazeFromFile(fileChooser.getSelectedFile());
    }

    /**
     * Direct loading from a specific File reference.
     */
    public Maze loadMazeFromFile(File fileToLoad) {
        if (fileToLoad == null || !fileToLoad.exists()) {
            ToastNotification.showWarning(parentFrame, "File does not exist.");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
            int rows = 0;
            int cols = 0;
            int startR = 0, startC = 0;
            int endR = 0, endC = 0;
            boolean readingGrid = false;
            int currentRow = 0;
            
            Maze loadedMaze = null;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (!readingGrid) {
                    if (line.startsWith("ROWS=")) {
                        rows = Integer.parseInt(line.substring(5));
                    } else if (line.startsWith("COLS=")) {
                        cols = Integer.parseInt(line.substring(5));
                    } else if (line.startsWith("START=")) {
                        String[] parts = line.substring(6).split(",");
                        startR = Integer.parseInt(parts[0]);
                        startC = Integer.parseInt(parts[1]);
                    } else if (line.startsWith("END=")) {
                        String[] parts = line.substring(4).split(",");
                        endR = Integer.parseInt(parts[0]);
                        endC = Integer.parseInt(parts[1]);
                    } else if (line.startsWith("GRID=")) {
                        if (rows <= 0 || cols <= 0) {
                            throw new IOException("Invalid maze size declaration in file.");
                        }
                        loadedMaze = new Maze(rows, cols);
                        readingGrid = true;
                    }
                } else {
                    if (currentRow >= rows) break;
                    StringTokenizer tokenizer = new StringTokenizer(line, " ");
                    int currentCol = 0;
                    while (tokenizer.hasMoreTokens() && currentCol < cols) {
                        String cellVal = tokenizer.nextToken();
                        if (cellVal.equals("1")) {
                            loadedMaze.setWall(currentRow, currentCol);
                        } else {
                            loadedMaze.setOpen(currentRow, currentCol);
                        }
                        currentCol++;
                    }
                    currentRow++;
                }
            }

            if (loadedMaze == null || currentRow != rows) {
                throw new IOException("Corrupted grid layout. Row mismatch.");
            }

            loadedMaze.setStart(startR, startC);
            loadedMaze.setEnd(endR, endC);

            addRecentFile(fileToLoad);
            ToastNotification.showSuccess(parentFrame, "Maze loaded: " + fileToLoad.getName());
            return loadedMaze;

        } catch (Exception e) {
            ToastNotification.showWarning(parentFrame, "Load failed: " + e.getMessage());
            return null;
        }
    }

    /** Exports execution statistics and solution coordinate routes to a formatted text file. */
    public boolean exportSolutionReport(List<com.mazesolver.model.MazeSolution> solutions, com.mazesolver.model.SolverStats stats) {
        if (solutions == null || solutions.isEmpty()) {
            ToastNotification.showWarning(parentFrame, "No solution path exists to export.");
            return false;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Solution Paths");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File fileToSave = fileChooser.getSelectedFile();
        String path = fileToSave.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".txt")) {
            path += ".txt";
            fileToSave = new File(path);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
            writer.write("=================================================");
            writer.newLine();
            writer.write("             MAZE SOLVER SOLUTION REPORT         ");
            writer.newLine();
            writer.write("=================================================");
            writer.newLine();
            writer.write("Solver Run Telemetry:");
            writer.newLine();
            writer.write(" - Execution Time: " + stats.getFormattedTime());
            writer.newLine();
            writer.write(" - Visited Nodes: " + stats.getVisitedNodes());
            writer.newLine();
            writer.write(" - Recursive Calls: " + stats.getRecursiveCalls());
            writer.newLine();
            writer.write(" - Max Recursion Depth: " + stats.getMaxDepth());
            writer.newLine();
            writer.write(" - Total Solution Paths Found: " + solutions.size());
            writer.newLine();
            writer.write(" - Shortest Solution Path Length: " + stats.getShortestPathLength() + " steps");
            writer.newLine();
            writer.write(" - Solver Accuracy: " + String.format("%.1f %%", stats.getAccuracyScore()));
            writer.newLine();
            writer.write(" - Solver Efficiency: " + String.format("%.1f %%", stats.getEfficiencyScore()));
            writer.newLine();
            writer.write("=================================================");
            writer.newLine();
            writer.newLine();

            for (com.mazesolver.model.MazeSolution solution : solutions) {
                writer.write("Solution #" + solution.getSolutionNumber() + " (Length: " + solution.getLength() + " steps):");
                writer.newLine();
                List<int[]> steps = solution.getPath();
                for (int i = 0; i < steps.size(); i++) {
                    int[] step = steps.get(i);
                    writer.write("(" + step[0] + "," + step[1] + ")");
                    if (i < steps.size() - 1) {
                        writer.write(" -> ");
                    }
                    if ((i + 1) % 8 == 0) {
                        writer.newLine();
                    }
                }
                writer.newLine();
                writer.write("-------------------------------------------------");
                writer.newLine();
                writer.newLine();
            }

            ToastNotification.showSuccess(parentFrame, "Report exported: " + fileToSave.getName());
            return true;
        } catch (IOException e) {
            ToastNotification.showWarning(parentFrame, "Export failed: " + e.getMessage());
            return false;
        }
    }
}
