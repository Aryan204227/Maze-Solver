package com.mazesolver.controller;

import com.mazesolver.model.Maze;
import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;
import com.mazesolver.view.MainWindow;
import com.mazesolver.view.dialogs.AboutDialog;
import com.mazesolver.view.dialogs.HelpDialog;
import com.mazesolver.view.dialogs.SettingsDialog;
import com.mazesolver.view.panels.*;

import javax.swing.*;
import java.awt.*;

/**
 * AppController.java
 * ─────────────────────────────────────────────────────
 * The main controller class coordinating application navigation, configuration layout transitions,
 * and theme triggers across subcomponents.
 *
 * WHY THIS CLASS EXISTS:
 *   Acts as the central router of the application. It captures navigation commands
 *   from SidebarPanel, panel toggle requests from DashboardPanel, and settings popups.
 *
 * JAVA CONCEPTS USED:
 *   - MVC architecture pattern
 *   - Callback delegates (implementing control panel listeners)
 */
public class AppController implements SidebarPanel.NavigationListener, ControlPanel.ControlListener, MazeController.SolvingStateListener {

    private final MainWindow       window;
    private final SidebarPanel     sidebar;
    private final DashboardPanel   dashboard;
    private final MazePanel        mazePanel;
    private final ControlPanel     controlPanel;
    private final StatsPanel       statsPanel;
    private final LearningPanel    learningPanel;
    private final StatusBar        statusBar;

    private final MazeController   mazeController;
    private final FileController   fileController;

    private CardLayout             contentCardLayout;
    private JPanel                 contentWrapper;

    public AppController(MainWindow window, SidebarPanel sidebar, DashboardPanel dashboard,
                         MazePanel mazePanel, ControlPanel controlPanel, StatsPanel statsPanel, LearningPanel learningPanel, StatusBar statusBar) {
        
        this.window       = window;
        this.sidebar      = sidebar;
        this.dashboard    = dashboard;
        this.mazePanel    = mazePanel;
        this.controlPanel = controlPanel;
        this.statsPanel   = statsPanel;
        this.learningPanel = learningPanel;
        this.statusBar    = statusBar;

        // Initialize sub-controllers
        this.mazeController = new MazeController(mazePanel, statsPanel, learningPanel, statusBar, Constants.DEFAULT_DELAY_MS);
        this.fileController = new FileController(window);

        // Bind callback loops
        this.sidebar.setNavigationListener(this);
        this.controlPanel.setControlListener(this);
        this.mazeController.setStateListener(this);
        
        this.dashboard.setOnStartEditor(() -> {
            this.onNavigate("editor");
        });

        // Wire mouse editor triggers
        this.mazePanel.setWallToggleListener((row, col) -> {
            mazeController.toggleWallAt(row, col);
        });

        // Set exit callback for autosave
        this.window.setExitCallback(() -> shutdown());

        setupRouting();
        restoreAutosave();
    }

    /** Prepares CardLayout structures for switching views */
    public void setViewRouter(CardLayout cardLayout, JPanel contentWrapper) {
        this.contentCardLayout = cardLayout;
        this.contentWrapper = contentWrapper;
    }

    private void setupRouting() {
        // Initial setup
        statusBar.setGridSize(Constants.DEFAULT_ROWS, Constants.DEFAULT_COLS);
        statusBar.setMode("Idle");
    }

    private void restoreAutosave() {
        Maze autosaved = fileController.loadAutosave();
        if (autosaved != null) {
            mazeController.setMaze(autosaved);
            statusBar.setGridSize(autosaved.getRows(), autosaved.getCols());
            statusBar.setStatus("Autosaved maze state restored successfully.");
        }
    }

    private void shutdown() {
        fileController.autosave(mazeController.getMaze());
        System.exit(0);
    }

    public void onUndo() {
        if (mazePanel.isEditMode()) {
            mazeController.undoWallEdit();
        } else {
            statusBar.setStatus("Undo (Ctrl+Z) is only available in Wall Edit Mode.");
        }
    }

    public void onRedo() {
        if (mazePanel.isEditMode()) {
            mazeController.redoWallEdit();
        } else {
            statusBar.setStatus("Redo (Ctrl+Y) is only available in Wall Edit Mode.");
        }
    }


    // ─────────────────────────────────────────────────────
    // Navigation Listeners Implementation
    // ─────────────────────────────────────────────────────

    @Override
    public void onNavigate(String page) {
        if ("theme-toggle".equals(page)) {
            toggleTheme();
            return;
        }

        switch (page) {
            case "dashboard":
                mazeController.stopSolvingForcefully();
                contentCardLayout.show(contentWrapper, "dashboard");
                break;
            case "editor":
                mazeController.stopSolvingForcefully();
                window.getEditorWorkspacePanel().add(mazePanel, BorderLayout.CENTER);
                window.getEditorWorkspacePanel().add(controlPanel, BorderLayout.SOUTH);
                window.getEditorWorkspacePanel().revalidate();
                window.getEditorWorkspacePanel().repaint();
                contentCardLayout.show(contentWrapper, "editor");
                break;
            case "learning":
                mazeController.stopSolvingForcefully();
                window.getLearningWorkspacePanel().add(mazePanel, BorderLayout.CENTER);
                window.getLearningWorkspacePanel().add(controlPanel, BorderLayout.SOUTH);
                window.getLearningWorkspacePanel().revalidate();
                window.getLearningWorkspacePanel().repaint();
                contentCardLayout.show(contentWrapper, "learning");
                break;
            case "settings":
                showSettingsDialog();
                break;
            case "help":
                showHelpDialog();
                break;
            case "about":
                showAboutDialog();
                break;
        }
    }

    private void toggleTheme() {
        ThemeManager.toggleTheme();
        
        // Propagate updates to panels
        window.applyTheme();
        sidebar.applyTheme();
        dashboard.applyTheme();
        mazePanel.repaint();
        controlPanel.applyTheme();
        statsPanel.applyTheme();
        learningPanel.applyTheme();
        statusBar.applyTheme();
    }

    // ─────────────────────────────────────────────────────
    // Control Toolbar Actions
    // ─────────────────────────────────────────────────────

    @Override
    public void onGenerate() {
        mazeController.generateRandomMaze();
    }

    @Override
    public void onGenerateTemplate(String template) {
        mazeController.generateTemplateMaze(template);
    }

    @Override
    public void onSolve() {
        mazeController.solveMaze();
    }

    @Override
    public void onPause() {
        mazeController.pauseSolving();
        controlPanel.onSolvePaused();
    }

    @Override
    public void onResume() {
        mazeController.resumeSolving();
        controlPanel.onSolveResumed();
    }

    @Override
    public void onReset() {
        mazeController.resetSolvingState();
        controlPanel.onSolveFinished(false);
    }

    @Override
    public void onPrevStep() {
        mazeController.stepBackward();
    }

    @Override
    public void onNextStep() {
        mazeController.stepForward();
    }

    @Override
    public void onReplay() {
        mazeController.startReplay();
    }

    @Override
    public void onEditToggle(boolean editMode) {
        mazeController.stopSolvingForcefully();
        controlPanel.onSolveFinished(false);
        controlPanel.setEditModeActive(editMode);
        mazePanel.setEditMode(editMode);
        if (editMode) {
            statusBar.setStatus("Editing enabled: Drag or click mouse to construct wall grids.");
        } else {
            statusBar.setStatus("Editing completed.");
        }
    }

    @Override
    public void onEditToolChanged(String tool) {
        mazeController.setActiveTool(tool);
        statusBar.setStatus("Active Tool: " + tool.toUpperCase());
    }

    @Override
    public void onEditBrushSizeChanged(int size) {
        mazeController.setBrushSize(size);
        statusBar.setStatus("Active Brush Size: " + size + "x" + size);
    }

    @Override
    public void onSpeedChanged(int delayMs) {
        mazeController.setSpeed(delayMs);
    }

    @Override
    public void onSizeChanged(int rows, int cols) {
        mazeController.resizeMaze(rows, cols);
        String difficulty = "medium";
        if (rows == 10) difficulty = "easy";
        else if (rows == 22) difficulty = "hard";
        mazeController.generateRandomMaze(difficulty);
        controlPanel.onSolveFinished(false);
    }

    @Override
    public void onSaveFile() {
        mazeController.stopSolvingForcefully();
        controlPanel.onSolveFinished(false);
        fileController.saveMaze(mazePanel.getMaze());
    }

    @Override
    public void onExportSolution() {
        fileController.exportSolutionReport(
            mazeController.getLastSolutions(),
            mazeController.getStats()
        );
    }

    @Override
    public void onLoadFile() {
        mazeController.stopSolvingForcefully();
        controlPanel.onSolveFinished(false);
        Maze loaded = fileController.loadMaze();
        if (loaded != null) {
            mazeController.setMaze(loaded);
            statusBar.setGridSize(loaded.getRows(), loaded.getCols());
            statusBar.setStatus("Grid configuration file loaded successfully.");
        }
    }

    @Override
    public void onLoadRecentFile(java.io.File file) {
        mazeController.stopSolvingForcefully();
        controlPanel.onSolveFinished(false);
        Maze loaded = fileController.loadMazeFromFile(file);
        if (loaded != null) {
            mazeController.setMaze(loaded);
            statusBar.setGridSize(loaded.getRows(), loaded.getCols());
            statusBar.setStatus("Grid loaded: " + file.getName());
        }
    }

    /** Suggests the next best move in Play/AI mode using BFS pathfinding. */
    @Override
    public void onHint() {
        mazeController.givePlayerHint();
    }

    @Override
    public void onModeChanged(String mode) {
        mazeController.setMode(mode);
        // Give MazePanel keyboard focus when Play Mode starts so the user
        // does not need to click the panel before pressing arrow / WASD keys.
        if ("Play Mode".equals(mode)) {
            SwingUtilities.invokeLater(() -> mazePanel.requestFocusInWindow());
        }
    }

    public void onPlayerMove(String direction) {
        mazeController.onPlayerMove(direction);
    }


    // ─────────────────────────────────────────────────────
    // Solving Thread State callbacks
    // ─────────────────────────────────────────────────────

    @Override
    public void onSolvingStateChanged(boolean running) {
        if (running) {
            controlPanel.onSolveStarted();
        } else {
            // Phase 7: Achievement Toasts
            boolean hasSolution = !mazeController.getSolveHistory().isEmpty();
            controlPanel.onSolveFinished(hasSolution);
            if (hasSolution) {
                com.mazesolver.model.SolverStats stats = mazeController.getCurrentStats();
                if (stats != null) {
                    if (stats.getEfficiencyScore() >= 80) {
                        statusBar.setStatus("\uD83C\uDFC6 ACHIEVEMENT: Speed Runner! Efficiency " + String.format("%.0f", stats.getEfficiencyScore()) + "%");
                    } else if (stats.getBacktrackCount() == 0) {
                        statusBar.setStatus("\uD83C\uDF1F ACHIEVEMENT: Perfect Path! No backtracks!");
                    } else {
                        statusBar.setStatus("\u2705 Solved! Accuracy: " + String.format("%.1f", stats.getAccuracyScore()) + "%, Efficiency: " + String.format("%.1f", stats.getEfficiencyScore()) + "%");
                    }
                }
            }
        }
    }

    @Override
    public void onRequestModeChange(String mode) {
        controlPanel.setMode(mode);
        onModeChanged(mode);
    }

    // ─────────────────────────────────────────────────────
    // Popups & Modal Utilities
    // ─────────────────────────────────────────────────────

    private void showAboutDialog() {
        AboutDialog dialog = new AboutDialog(window);
        dialog.setVisible(true);
    }

    private void showHelpDialog() {
        HelpDialog dialog = new HelpDialog(window);
        dialog.setVisible(true);
    }

    private void showSettingsDialog() {
        SettingsDialog dialog = new SettingsDialog(window);
        dialog.setVisible(true);
        if (dialog.isSettingsUpdated()) {
            // Apply preferences limits if settings changed
            statusBar.setStatus("Preferences updated. Max solutions: " + dialog.getMaxSolutionsLimit());
        }
    }
}
