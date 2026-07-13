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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import controller.MazeController;
import model.CellType;
import model.Maze;
import model.Position;
import view.components.CustomDialog;
import view.components.RoundedButton;
import view.components.RoundedPanel;

/**
 * Provides the interactive workspace for drawing, editing, and generating mazes.
 * Features a toolbar for brush selections, sizing controls, loading, and saving.
 */
public class MazeEditorPanel extends JPanel {
    private final MazeController controller;
    private final MainFrame mainFrame;
    private final GridCanvas canvas;

    // Brush Tool options
    private enum BrushMode {
        WALL, PATH, START, END
    }
    private BrushMode activeBrush = BrushMode.WALL;

    // Sizing controls
    private JComboBox<String> sizeCombo;

    /**
     * Constructs a MazeEditorPanel linked to the coordinator.
     */
    public MazeEditorPanel(MazeController controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;

        setOpaque(false);
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // 1. Grid Canvas (Center)
        canvas = new GridCanvas();
        controller.registerView(canvas);
        add(canvas, BorderLayout.CENTER);

        // 2. Toolbar panel (West/Left side)
        add(createToolboxPanel(), BorderLayout.WEST);
    }

    private JPanel createToolboxPanel() {
        RoundedPanel toolbox = new RoundedPanel(16);
        toolbox.setBackground(Theme.getCardBgColor());
        toolbox.setLayout(new GridBagLayout());
        toolbox.setBorder(new EmptyBorder(20, 20, 20, 20));
        toolbox.setPreferredSize(new Dimension(240, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Header Title
        JLabel titleLabel = new JLabel("Editor Toolbox");
        titleLabel.setFont(Theme.getSemiboldFont(16));
        titleLabel.setForeground(Theme.getPrimaryTextColor());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        gbc.gridy = 0;
        toolbox.add(titleLabel, gbc);

        // Brush selector buttons
        JLabel sectionBrush = new JLabel("BRUSH TOOL");
        sectionBrush.setFont(Theme.getSemiboldFont(11));
        sectionBrush.setForeground(Theme.getSecondaryTextColor());
        gbc.gridy = 1;
        toolbox.add(sectionBrush, gbc);

        ButtonGroup brushGroup = new ButtonGroup();
        
        JToggleButton btnWall = createBrushToggleButton("Draw Walls", activeBrush == BrushMode.WALL);
        btnWall.addActionListener(e -> activeBrush = BrushMode.WALL);
        brushGroup.add(btnWall);
        gbc.gridy = 2;
        toolbox.add(btnWall, gbc);

        JToggleButton btnPath = createBrushToggleButton("Erase Walls", activeBrush == BrushMode.PATH);
        btnPath.addActionListener(e -> activeBrush = BrushMode.PATH);
        brushGroup.add(btnPath);
        gbc.gridy = 3;
        toolbox.add(btnPath, gbc);

        JToggleButton btnStart = createBrushToggleButton("Set Start Position", activeBrush == BrushMode.START);
        btnStart.addActionListener(e -> activeBrush = BrushMode.START);
        brushGroup.add(btnStart);
        gbc.gridy = 4;
        toolbox.add(btnStart, gbc);

        JToggleButton btnEnd = createBrushToggleButton("Set End Position", activeBrush == BrushMode.END);
        btnEnd.addActionListener(e -> activeBrush = BrushMode.END);
        brushGroup.add(btnEnd);
        gbc.gridy = 5;
        toolbox.add(btnEnd, gbc);

        // Sizing selectors
        JLabel sectionSize = new JLabel("GRID SIZE");
        sectionSize.setFont(Theme.getSemiboldFont(11));
        sectionSize.setForeground(Theme.getSecondaryTextColor());
        gbc.gridy = 6;
        gbc.insets = new Insets(16, 0, 4, 0);
        toolbox.add(sectionSize, gbc);

        String[] sizes = {"15 x 15 (Small)", "21 x 21 (Default)", "31 x 31 (Medium)", "45 x 45 (Large)", "61 x 61 (Huge)"};
        sizeCombo = new JComboBox<>(sizes);
        sizeCombo.setFont(Theme.getBodyFont(13));
        sizeCombo.setSelectedIndex(1); // 21x21 default
        sizeCombo.addActionListener(e -> handleSizeChange());
        gbc.gridy = 7;
        gbc.insets = new Insets(4, 0, 6, 0);
        toolbox.add(sizeCombo, gbc);

        // Generator actions
        JLabel sectionGen = new JLabel("AUTO GENERATORS");
        sectionGen.setFont(Theme.getSemiboldFont(11));
        sectionGen.setForeground(Theme.getSecondaryTextColor());
        gbc.gridy = 8;
        gbc.insets = new Insets(16, 0, 4, 0);
        toolbox.add(sectionGen, gbc);

        RoundedButton btnPerfect = new RoundedButton("Generate Perfect DFS");
        btnPerfect.addActionListener(e -> {
            controller.generatePerfectMaze();
            mainFrame.updateDashboardStats();
        });
        gbc.gridy = 9;
        gbc.insets = new Insets(4, 0, 6, 0);
        toolbox.add(btnPerfect, gbc);

        RoundedButton btnNoise = new RoundedButton("Generate Random Noise");
        btnNoise.addActionListener(e -> {
            controller.generateRandomNoise(0.32);
            mainFrame.updateDashboardStats();
        });
        gbc.gridy = 10;
        toolbox.add(btnNoise, gbc);

        // File operations
        JLabel sectionFile = new JLabel("FILE OPERATIONS");
        sectionFile.setFont(Theme.getSemiboldFont(11));
        sectionFile.setForeground(Theme.getSecondaryTextColor());
        gbc.gridy = 11;
        gbc.insets = new Insets(16, 0, 4, 0);
        toolbox.add(sectionFile, gbc);

        JPanel fileRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        fileRow.setOpaque(false);
        
        RoundedButton btnOpen = new RoundedButton("Open");
        btnOpen.setColors(Theme.getCardBgColor(), Theme.getBgColor(), Theme.getBgColor().darker(), Theme.getPrimaryTextColor(), Theme.getAccentColor());
        btnOpen.addActionListener(e -> handleOpenMaze());
        
        RoundedButton btnSave = new RoundedButton("Save");
        btnSave.setColors(Theme.getCardBgColor(), Theme.getBgColor(), Theme.getBgColor().darker(), Theme.getPrimaryTextColor(), Theme.getAccentColor());
        btnSave.addActionListener(e -> handleSaveMaze());

        fileRow.add(btnOpen);
        fileRow.add(btnSave);
        gbc.gridy = 12;
        gbc.insets = new Insets(4, 0, 6, 0);
        toolbox.add(fileRow, gbc);

        // Reset/Clear button
        RoundedButton btnClear = new RoundedButton("Clear Grid");
        btnClear.setColors(Theme.getCardBgColor(), new Color(234, 67, 53, 30), new Color(234, 67, 53, 50), Theme.getEndCellColor(), Theme.getEndCellColor());
        btnClear.addActionListener(e -> {
            if (CustomDialog.showConfirm(this, "Reset Grid", "Are you sure you want to clear the entire maze grid?")) {
                controller.resetMaze();
                mainFrame.updateDashboardStats();
            }
        });
        gbc.gridy = 13;
        gbc.insets = new Insets(16, 0, 6, 0);
        toolbox.add(btnClear, gbc);

        // Spacer to push everything to top
        gbc.gridy = 14;
        gbc.weighty = 1.0;
        toolbox.add(new JLabel(""), gbc);

        return toolbox;
    }

    private JToggleButton createBrushToggleButton(String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text, selected);
        btn.setFont(Theme.getBodyFont(13));
        btn.setForeground(Theme.getPrimaryTextColor());
        btn.setBackground(Theme.getCardBgColor());
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.getCardBorderColor(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Custom look adjustments
        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(Theme.getAccentColor());
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(Theme.getCardBgColor());
                btn.setForeground(Theme.getPrimaryTextColor());
            }
        });
        
        return btn;
    }

    private void handleSizeChange() {
        int index = sizeCombo.getSelectedIndex();
        int size = 21;
        switch (index) {
            case 0: size = 15; break;
            case 1: size = 21; break;
            case 2: size = 31; break;
            case 3: size = 45; break;
            case 4: size = 61; break;
        }
        controller.resizeMaze(size, size);
        mainFrame.updateDashboardStats();
    }

    private void handleOpenMaze() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open Maze Configuration");
        chooser.setFileFilter(new FileNameExtensionFilter("Maze Files (*.maze)", "maze"));
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                controller.loadMaze(selectedFile);
                CustomDialog.showSuccess(this, "Success", "Maze loaded successfully.");
                mainFrame.updateDashboardStats();
            } catch (Exception ex) {
                CustomDialog.showError(this, "Loading Failed", ex.getMessage());
            }
        }
    }

    private void handleSaveMaze() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Maze Configuration");
        chooser.setFileFilter(new FileNameExtensionFilter("Maze Files (*.maze)", "maze"));
        int option = chooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().endsWith(".maze")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".maze");
            }
            try {
                controller.saveMaze(selectedFile);
                CustomDialog.showSuccess(this, "Success", "Maze configuration saved successfully.");
            } catch (Exception ex) {
                CustomDialog.showError(this, "Saving Failed", ex.getMessage());
            }
        }
    }

    public void updateView() {
        canvas.repaint();
    }

    // --- INNER CANVAS COMPONENT ---

    private class GridCanvas extends JPanel {
        private Position hoverPos = null;

        public GridCanvas() {
            setOpaque(false);
            
            // Handle clicking and drawing
            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleMouseDraw(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    handleMouseDraw(e);
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    Position p = getCellPositionAt(e.getX(), e.getY());
                    if (p != hoverPos) {
                        hoverPos = p;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoverPos = null;
                    repaint();
                }
            };
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }

        private Position getCellPositionAt(int px, int py) {
            Maze m = controller.getMaze();
            int rows = m.getRows();
            int cols = m.getCols();

            int canvasWidth = getWidth();
            int canvasHeight = getHeight();
            
            int cellSize = Math.min(canvasWidth / cols, canvasHeight / rows);
            
            // Centering offsets
            int offsetX = (canvasWidth - cols * cellSize) / 2;
            int offsetY = (canvasHeight - rows * cellSize) / 2;

            int col = (px - offsetX) / cellSize;
            int row = (py - offsetY) / cellSize;

            Position pos = new Position(row, col);
            if (m.isWithinBounds(pos)) {
                return pos;
            }
            return null;
        }

        private void handleMouseDraw(MouseEvent e) {
            Position pos = getCellPositionAt(e.getX(), e.getY());
            if (pos == null) return;

            // Prevent editing outer border cells to maintain outer walls
            Maze m = controller.getMaze();
            if (pos.getRow() == 0 || pos.getRow() == m.getRows() - 1 ||
                pos.getCol() == 0 || pos.getCol() == m.getCols() - 1) {
                return; 
            }

            CellType targetType = CellType.PATH;
            switch (activeBrush) {
                case WALL:
                    targetType = CellType.WALL;
                    break;
                case PATH:
                    targetType = CellType.PATH;
                    break;
                case START:
                    targetType = CellType.START;
                    break;
                case END:
                    targetType = CellType.END;
                    break;
            }

            controller.editCell(pos, targetType);
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

            int cellSize = Math.min(canvasWidth / cols, canvasHeight / rows);
            
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
                        default:
                            break;
                    }

                    g2.setColor(cellColor);
                    g2.fillRect(x, y, cellSize, cellSize);

                    // Grid lines (thin subtle border)
                    g2.setColor(Theme.getCardBorderColor());
                    g2.drawRect(x, y, cellSize, cellSize);

                    // Hover indicator
                    if (hoverPos != null && hoverPos.equals(cellPos)) {
                        g2.setColor(new Color(255, 255, 255, 60));
                        g2.fillRect(x, y, cellSize, cellSize);
                        
                        g2.setColor(Theme.getSecondaryAccentColor());
                        g2.drawRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
                    }
                }
            }

            g2.dispose();
        }
    }
}
