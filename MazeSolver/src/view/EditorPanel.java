package view;

import controller.AppController;
import model.Cell;
import view.ui.RoundedPanel;
import view.ui.Theme;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileFilter;

public class EditorPanel extends JPanel {

    public enum BrushMode { WALL, PATH, START, END }

    private final AppController controller;
    private BrushMode currentBrush = BrushMode.WALL;
    private GridCanvas gridCanvas;

    private JToggleButton btnWall;
    private JToggleButton btnPath;
    private JToggleButton btnStart;
    private JToggleButton btnEnd;

    public EditorPanel(AppController ctrl) {
        this.controller = ctrl;
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel toolbar = buildToolbar();
        toolbar.setPreferredSize(new Dimension(200, 0));
        add(toolbar, BorderLayout.WEST);

        gridCanvas = new GridCanvas();
        add(gridCanvas, BorderLayout.CENTER);
    }

    private JPanel buildToolbar() {
        RoundedPanel toolbar = new RoundedPanel(0);
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        toolbar.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

        toolbar.add(buildSectionLabel("BRUSH"));
        toolbar.add(Box.createVerticalStrut(8));
        toolbar.add(buildBrushSection());
        toolbar.add(Box.createVerticalStrut(16));

        toolbar.add(buildSectionLabel("GRID SIZE"));
        toolbar.add(Box.createVerticalStrut(8));
        toolbar.add(buildGridSizeSection());
        toolbar.add(Box.createVerticalStrut(16));

        toolbar.add(buildSectionLabel("ACTIONS"));
        toolbar.add(Box.createVerticalStrut(8));
        toolbar.add(buildActionsSection());
        toolbar.add(Box.createVerticalStrut(16));

        toolbar.add(buildSectionLabel("FILE"));
        toolbar.add(Box.createVerticalStrut(8));
        toolbar.add(buildFileSection());
        toolbar.add(Box.createVerticalGlue());

        return toolbar;
    }

    private JLabel buildSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 10f));
        label.setForeground(Theme.getSecondaryText());
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JPanel buildBrushSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        ButtonGroup group = new ButtonGroup();
        btnWall = createToggle("Wall", group);
        btnPath = createToggle("Path", group);
        btnStart = createToggle("Start", group);
        btnEnd = createToggle("End", group);
        btnWall.setSelected(true);

        btnWall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { currentBrush = BrushMode.WALL; }
        });
        btnPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { currentBrush = BrushMode.PATH; }
        });
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { currentBrush = BrushMode.START; }
        });
        btnEnd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { currentBrush = BrushMode.END; }
        });

        panel.add(btnWall);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnPath);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnStart);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnEnd);
        return panel;
    }

    private JToggleButton createToggle(String text, ButtonGroup group) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(Theme.FONT_BODY.deriveFont(13f));
        btn.setForeground(Theme.getText());
        btn.setBackground(Theme.getCard());
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        group.add(btn);
        return btn;
    }

    private JPanel buildGridSizeSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        final String[] sizes = {"11x11", "15x15", "21x21", "31x31"};
        final JComboBox<String> combo = new JComboBox<String>(sizes);
        combo.setFont(Theme.FONT_BODY.deriveFont(13f));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        combo.setBackground(Theme.getCard());
        combo.setForeground(Theme.getText());

        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sel = (String) combo.getSelectedItem();
                if (sel != null) {
                    String[] parts = sel.split("x");
                    int rows = Integer.parseInt(parts[0]);
                    int cols = Integer.parseInt(parts[1]);
                    controller.getMaze().resize(rows, cols);
                    gridCanvas.repaint();
                }
            }
        });

        panel.add(combo);
        return panel;
    }

    private JPanel buildActionsSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton genBtn = createToolbarButton("Generate Maze");
        JButton clearBtn = createToolbarButton("Clear Grid");

        genBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.generateMaze();
                gridCanvas.repaint();
            }
        });

        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.getMaze().clearAll();
                gridCanvas.repaint();
            }
        });

        panel.add(genBtn);
        panel.add(Box.createVerticalStrut(4));
        panel.add(clearBtn);
        return panel;
    }

    private JPanel buildFileSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton saveBtn = createToolbarButton("Save Maze");
        JButton loadBtn = createToolbarButton("Load Maze");

        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileFilter() {
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().endsWith(".maze");
                    }
                    public String getDescription() { return "Maze files (*.maze)"; }
                });
                int result = chooser.showSaveDialog(EditorPanel.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    String path = file.getAbsolutePath();
                    if (!path.endsWith(".maze")) path += ".maze";
                    try {
                        controller.saveMaze(new java.io.File(path));
                        JOptionPane.showMessageDialog(EditorPanel.this, "Maze saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EditorPanel.this, "Error saving maze: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileFilter() {
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().endsWith(".maze");
                    }
                    public String getDescription() { return "Maze files (*.maze)"; }
                });
                int result = chooser.showOpenDialog(EditorPanel.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        controller.loadMaze(file);
                        gridCanvas.repaint();
                        JOptionPane.showMessageDialog(EditorPanel.this, "Maze loaded successfully.", "Loaded", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EditorPanel.this, "Error loading maze: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.add(saveBtn);
        panel.add(Box.createVerticalStrut(4));
        panel.add(loadBtn);
        return panel;
    }

    private JButton createToolbarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BODY.deriveFont(13f));
        btn.setForeground(Theme.getText());
        btn.setBackground(Theme.getCard());
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        return btn;
    }

    public void refreshTheme() {
        gridCanvas.repaint();
        repaint();
    }

    // ----------------------------- Inner Canvas -----------------------------

    private class GridCanvas extends JPanel {

        public GridCanvas() {
            setOpaque(true);
            setBackground(Theme.getBackground());

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handlePaint(e.getX(), e.getY());
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    handlePaint(e.getX(), e.getY());
                }
            };

            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }

        private void handlePaint(int px, int py) {
            Cell[][] grid = controller.getMaze().getGrid();
            int rows = grid.length;
            int cols = grid[0].length;

            int cellW = Math.max(1, getWidth() / cols);
            int cellH = Math.max(1, getHeight() / rows);
            int cellSize = Math.min(cellW, cellH);

            int totalW = cellSize * cols;
            int totalH = cellSize * rows;
            int offX = (getWidth() - totalW) / 2;
            int offY = (getHeight() - totalH) / 2;

            int col = (px - offX) / cellSize;
            int row = (py - offY) / cellSize;

            if (row < 0 || row >= rows || col < 0 || col >= cols) return;

            switch (currentBrush) {
                case WALL:  controller.getMaze().setCell(row, col, Cell.WALL);  break;
                case PATH:  controller.getMaze().setCell(row, col, Cell.PATH);  break;
                case START:
                    controller.getMaze().clearStart();
                    controller.getMaze().setCell(row, col, Cell.START);
                    break;
                case END:
                    controller.getMaze().clearEnd();
                    controller.getMaze().setCell(row, col, Cell.END);
                    break;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            setBackground(Theme.getBackground());

            Cell[][] grid = controller.getMaze().getGrid();
            int rows = grid.length;
            int cols = grid[0].length;

            int cellW = Math.max(1, getWidth() / cols);
            int cellH = Math.max(1, getHeight() / rows);
            int cellSize = Math.min(cellW, cellH);

            int totalW = cellSize * cols;
            int totalH = cellSize * rows;
            int offX = (getWidth() - totalW) / 2;
            int offY = (getHeight() - totalH) / 2;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int x = offX + c * cellSize;
                    int y = offY + r * cellSize;
                    Color cellColor;
                    switch (grid[r][c]) {
                        case WALL:  cellColor = Theme.CELL_WALL;  break;
                        case START: cellColor = Theme.CELL_START; break;
                        case END:   cellColor = Theme.CELL_END;   break;
                        default:    cellColor = Theme.getCellPath(); break;
                    }
                    g2.setColor(cellColor);
                    g2.fillRect(x, y, cellSize, cellSize);

                    g2.setColor(Theme.getBackground().darker());
                    g2.setStroke(new BasicStroke(0.5f));
                    g2.drawRect(x, y, cellSize, cellSize);
                }
            }

            g2.dispose();
        }
    }
}
