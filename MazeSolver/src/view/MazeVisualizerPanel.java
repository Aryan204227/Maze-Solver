package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import controller.MazeController;
import algorithm.SolvingStep;
import model.CellType;
import model.Maze;
import model.Position;
import model.SolveStatistics;
import view.components.CustomDialog;
import view.components.ModernSlider;
import view.components.RoundedButton;
import view.components.RoundedPanel;

/**
 * The visual playback canvas for maze-solving execution.
 * Includes interactive playback timers, zoom levels, speed toggles,
 * and a side-log showing DFS recursion depth logs.
 */
public class MazeVisualizerPanel extends JPanel implements MazeController.ControllerListener {
    private final MazeController controller;
    private final MainFrame mainFrame;
    private final VisualGridCanvas gridCanvas;

    // UI elements
    private JLabel lblStepCount;
    private JLabel lblDepth;
    private JLabel lblVisitedCount;
    private JLabel lblShortestPath;
    private JTextArea txtLogArea;

    private RoundedButton btnPlay;
    private RoundedButton btnPause;
    private RoundedButton btnStepBack;
    private RoundedButton btnStepForward;

    // Zoom and Speed values
    private double zoomScale = 1.0;
    private static final double ZOOM_MIN = 0.5;
    private static final double ZOOM_MAX = 2.5;

    /**
     * Constructs a MazeVisualizerPanel.
     */
    public MazeVisualizerPanel(MazeController controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;

        setOpaque(false);
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        controller.addListener(this);

        // 1. Center grid panel (wrapped in a JScrollPane to support Zoom panning)
        gridCanvas = new VisualGridCanvas();
        controller.registerView(gridCanvas);
        
        JScrollPane scrollPane = new JScrollPane(gridCanvas);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.getCardBorderColor(), 1));
        scrollPane.getViewport().setBackground(Theme.getBgColor());
        scrollPane.setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // 2. Playback control bar (Bottom)
        add(createPlaybackControlBar(), BorderLayout.SOUTH);

        // 3. Side Stats panel (Right)
        add(createSideStatsPanel(), BorderLayout.EAST);
    }

    private JPanel createSideStatsPanel() {
        RoundedPanel panel = new RoundedPanel(16);
        panel.setBackground(Theme.getCardBgColor());
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(280, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Header Title
        JLabel titleLabel = new JLabel("Solver Statistics");
        titleLabel.setFont(Theme.getSemiboldFont(16));
        titleLabel.setForeground(Theme.getPrimaryTextColor());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        // Realtime Stats Row labels
        lblStepCount = createStatLabel(panel, "Steps Executed: ", "0 / 0", 1);
        lblDepth = createStatLabel(panel, "Recursion Depth: ", "0", 2);
        lblVisitedCount = createStatLabel(panel, "Visited Cells: ", "0", 3);
        lblShortestPath = createStatLabel(panel, "Shortest Path: ", "N/A", 4);

        // Text Log for DFS movements
        JLabel logLabel = new JLabel("TRAVERSAL LOG");
        logLabel.setFont(Theme.getSemiboldFont(11));
        logLabel.setForeground(Theme.getSecondaryTextColor());
        gbc.gridy = 5;
        gbc.insets = new Insets(16, 0, 4, 0);
        panel.add(logLabel, gbc);

        txtLogArea = new JTextArea(8, 20);
        txtLogArea.setFont(Theme.getMonospaceFont(12));
        txtLogArea.setBackground(Theme.getBgColor());
        txtLogArea.setForeground(Theme.getPrimaryTextColor());
        txtLogArea.setEditable(false);
        txtLogArea.setLineWrap(true);
        txtLogArea.setWrapStyleWord(true);
        txtLogArea.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane logScroll = new JScrollPane(txtLogArea);
        logScroll.setBorder(BorderFactory.createLineBorder(Theme.getCardBorderColor(), 1));
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 0, 6, 0);
        panel.add(logScroll, gbc);

        // Export utility buttons
        JPanel exportRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        exportRow.setOpaque(false);
        
        RoundedButton btnExport = new RoundedButton("Export Report");
        btnExport.setColors(Theme.getCardBgColor(), Theme.getBgColor(), Theme.getBgColor().darker(), Theme.getPrimaryTextColor(), Theme.getAccentColor());
        btnExport.addActionListener(e -> handleExportReport());

        RoundedButton btnPrint = new RoundedButton("Print");
        btnPrint.setColors(Theme.getCardBgColor(), Theme.getBgColor(), Theme.getBgColor().darker(), Theme.getPrimaryTextColor(), Theme.getAccentColor());
        btnPrint.addActionListener(e -> controller.printVisualizer(gridCanvas));

        RoundedButton btnCapture = new RoundedButton("Screenshot");
        btnCapture.setColors(Theme.getCardBgColor(), Theme.getBgColor(), Theme.getBgColor().darker(), Theme.getPrimaryTextColor(), Theme.getAccentColor());
        btnCapture.addActionListener(e -> handleScreenshot());

        exportRow.add(btnExport);
        exportRow.add(btnPrint);
        exportRow.add(btnCapture);
        
        gbc.gridy = 7;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(16, 0, 0, 0);
        panel.add(exportRow, gbc);

        return panel;
    }

    private JLabel createStatLabel(JPanel parent, String labelText, String defaultValue, int gridY) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel title = new JLabel(labelText);
        title.setFont(Theme.getBodyFont(13));
        title.setForeground(Theme.getSecondaryTextColor());

        JLabel value = new JLabel(defaultValue);
        value.setFont(Theme.getSemiboldFont(13));
        value.setForeground(Theme.getPrimaryTextColor());
        
        row.add(title, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.insets = new Insets(2, 0, 2, 0);
        parent.add(row, gbc);

        return value;
    }

    private JPanel createPlaybackControlBar() {
        RoundedPanel bar = new RoundedPanel(16);
        bar.setBackground(Theme.getCardBgColor());
        bar.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 12));

        // 1. Solving Triggers
        RoundedButton btnInstant = new RoundedButton("Instant Solve");
        btnInstant.setColors(Theme.getCardBgColor(), Theme.getBgColor(), Theme.getBgColor().darker(), Theme.getPrimaryTextColor(), Theme.getAccentColor());
        btnInstant.addActionListener(e -> {
            try {
                controller.solveInstantly();
                updateStatsPanel(controller.getLastSolveStats());
                txtLogArea.setText("Instant solution calculated successfully.\nTotal solutions: " + controller.getLastSolveStats().getTotalSolutions());
                mainFrame.updateDashboardStats();
            } catch (Exception ex) {
                CustomDialog.showError(this, "Solver Error", ex.getMessage());
            }
        });

        RoundedButton btnSolveAnimate = new RoundedButton("Run DFS Solver");
        btnSolveAnimate.addActionListener(e -> {
            try {
                boolean solved = controller.solveMaze();
                updateStatsPanel(controller.getLastSolveStats());
                txtLogArea.setText("DFS solving process compiled.\nClick PLAY below to run visualization step-by-step.");
                mainFrame.updateDashboardStats();
                btnPlay.setEnabled(true);
                btnStepForward.setEnabled(true);
                btnStepBack.setEnabled(true);
            } catch (Exception ex) {
                CustomDialog.showError(this, "Solver Error", ex.getMessage());
            }
        });

        bar.add(btnInstant);
        bar.add(btnSolveAnimate);

        // Divider spacer
        JLabel divider1 = new JLabel("|");
        divider1.setForeground(Theme.getCardBorderColor());
        bar.add(divider1);

        // 2. Playback Controls
        btnStepBack = new RoundedButton("◀ Step Back");
        btnStepBack.setEnabled(false);
        btnStepBack.addActionListener(e -> controller.stepBackward());

        btnPlay = new RoundedButton("▶ Play");
        btnPlay.setEnabled(false);
        btnPlay.addActionListener(e -> {
            controller.startAnimation();
            btnPlay.setEnabled(false);
            btnPause.setEnabled(true);
        });

        btnPause = new RoundedButton("⏸ Pause");
        btnPause.setEnabled(false);
        btnPause.addActionListener(e -> {
            controller.stopAnimation();
            btnPlay.setEnabled(true);
            btnPause.setEnabled(false);
        });

        btnStepForward = new RoundedButton("Step Forward ▶");
        btnStepForward.setEnabled(false);
        btnStepForward.addActionListener(e -> controller.stepForward());

        bar.add(btnStepBack);
        bar.add(btnPlay);
        bar.add(btnPause);
        bar.add(btnStepForward);

        // Divider spacer
        JLabel divider2 = new JLabel("|");
        divider2.setForeground(Theme.getCardBorderColor());
        bar.add(divider2);

        // 3. Playback Speed Slider
        JLabel lblSpeed = new JLabel("Delay (ms):");
        lblSpeed.setFont(Theme.getBodyFont(12));
        lblSpeed.setForeground(Theme.getSecondaryTextColor());
        bar.add(lblSpeed);

        ModernSlider speedSlider = new ModernSlider(10, 400, 50);
        speedSlider.setPreferredSize(new Dimension(100, 24));
        speedSlider.addChangeListener(e -> controller.setAnimationDelay(speedSlider.getValue()));
        bar.add(speedSlider);

        // 4. Zoom Slider
        JLabel lblZoom = new JLabel("Zoom:");
        lblZoom.setFont(Theme.getBodyFont(12));
        lblZoom.setForeground(Theme.getSecondaryTextColor());
        bar.add(lblZoom);

        ModernSlider zoomSlider = new ModernSlider(50, 250, 100);
        zoomSlider.setPreferredSize(new Dimension(80, 24));
        zoomSlider.addChangeListener(e -> {
            zoomScale = zoomSlider.getValue() / 100.0;
            gridCanvas.updateCanvasSize();
        });
        bar.add(zoomSlider);

        // 5. Reset Solver state
        RoundedButton btnReset = new RoundedButton("Reset");
        btnReset.setColors(Theme.getCardBgColor(), new Color(234, 67, 53, 30), new Color(234, 67, 53, 50), Theme.getEndCellColor(), Theme.getEndCellColor());
        btnReset.addActionListener(e -> {
            controller.stopAnimationAndReset();
            btnPlay.setEnabled(false);
            btnPause.setEnabled(false);
            btnStepForward.setEnabled(false);
            btnStepBack.setEnabled(false);
            txtLogArea.setText("");
            lblStepCount.setText("0 / 0");
            lblDepth.setText("0");
            lblVisitedCount.setText("0");
            lblShortestPath.setText("N/A");
            mainFrame.updateDashboardStats();
        });
        bar.add(btnReset);

        return bar;
    }

    private void updateStatsPanel(SolveStatistics stats) {
        lblVisitedCount.setText(String.valueOf(stats.getVisitedNodes()));
        lblShortestPath.setText(stats.getShortestPathLength() > 0 ? stats.getShortestPathLength() + " cells" : "No Path");
    }

    private void handleExportReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Solve Report");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        int option = chooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }
            try {
                controller.exportTextReport(selectedFile);
                CustomDialog.showSuccess(this, "Exported", "Report saved successfully.");
            } catch (Exception ex) {
                CustomDialog.showError(this, "Export Failed", ex.getMessage());
            }
        }
    }

    private void handleScreenshot() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Grid Screenshot");
        chooser.setFileFilter(new FileNameExtensionFilter("PNG Images (*.png)", "png"));
        int option = chooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().endsWith(".png")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".png");
            }
            try {
                controller.exportScreenshot(gridCanvas, selectedFile);
                CustomDialog.showSuccess(this, "Captured", "Screenshot saved successfully.");
            } catch (Exception ex) {
                CustomDialog.showError(this, "Capture Failed", ex.getMessage());
            }
        }
    }

    // --- CONTROLLER LISTENER CALLBACKS ---

    @Override
    public void onStateChanged() {
        gridCanvas.repaint();
    }

    @Override
    public void onAnimationStep(int stepIndex, int totalSteps, SolvingStep step) {
        if (step != null) {
            lblStepCount.setText(String.format("%d / %d", stepIndex + 1, totalSteps));
            lblDepth.setText(String.valueOf(step.getRecursionDepth()));
            txtLogArea.append(step + "\n");
            txtLogArea.setCaretPosition(txtLogArea.getDocument().getLength()); // Scroll to end
        }
    }

    @Override
    public void onAnimationFinished() {
        btnPlay.setEnabled(true);
        btnPause.setEnabled(false);
        CustomDialog.showSuccess(this, "Solving Complete", "Visual solve finished rendering.");
    }

    // --- CANVAS COMPONENT ---

    private class VisualGridCanvas extends JPanel {
        public VisualGridCanvas() {
            setOpaque(false);
            updateCanvasSize();
        }

        public void updateCanvasSize() {
            Maze m = controller.getMaze();
            int baseSize = 25; // Base cell dimension in pixels
            int w = (int) (m.getCols() * baseSize * zoomScale);
            int h = (int) (m.getRows() * baseSize * zoomScale);
            
            setPreferredSize(new Dimension(w + 40, h + 40));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Maze m = controller.getMaze();
            int rows = m.getRows();
            int cols = m.getCols();

            int canvasWidth = getWidth();
            int canvasHeight = getHeight();

            int baseCellSize = 25;
            int cellSize = (int) (baseCellSize * zoomScale);
            
            // Centering offsets
            int offsetX = (canvasWidth - cols * cellSize) / 2;
            int offsetY = (canvasHeight - rows * cellSize) / 2;

            // Draw cells
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    Position cellPos = new Position(r, c);
                    CellType cellType = m.getCell(cellPos);

                    int x = offsetX + c * cellSize;
                    int y = offsetY + r * cellSize;

                    // Choose colors
                    Color cellColor = Theme.getPathColor();
                    switch (cellType) {
                        case WALL:
                            cellColor = Theme.getWallColor();
                            break;
                        case START:
                            cellColor = Theme.getStartCellColor();
                            break;
                        case END:
                            cellColor = Theme.getEndCellColor();
                            break;
                        case VISITED:
                            cellColor = Theme.getVisitedCellColor();
                            break;
                        case DEAD_END:
                            cellColor = Theme.getDeadEndCellColor();
                            break;
                        case CORRECT_PATH:
                            cellColor = Theme.getCorrectPathCellColor();
                            break;
                        default:
                            break;
                    }

                    g2.setColor(cellColor);
                    g2.fillRect(x, y, cellSize, cellSize);

                    // Grid lines (thin subtle border)
                    g2.setColor(Theme.getCardBorderColor());
                    g2.drawRect(x, y, cellSize, cellSize);
                }
            }

            g2.dispose();
        }
    }
}
