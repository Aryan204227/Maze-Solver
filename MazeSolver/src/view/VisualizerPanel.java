package view;

import controller.AppController;
import model.Cell;
import model.SolveResult;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class VisualizerPanel extends JPanel {

    private final AppController controller;

    private JLabel stepLabel;
    private JLabel depthLabel;
    private JLabel visitedLabel;
    private JLabel solutionsLabel;
    private JLabel pathLabel;
    private JLabel timeLabel;
    private JTextArea logArea;

    private VisualizationCanvas canvas;

    public VisualizerPanel(AppController ctrl) {
        this.controller = ctrl;
        setLayout(new BorderLayout());
        setOpaque(false);

        canvas = new VisualizationCanvas();
        add(canvas, BorderLayout.CENTER);

        JPanel statsPanel = buildStatsPanel();
        statsPanel.setPreferredSize(new Dimension(240, 0));
        add(statsPanel, BorderLayout.EAST);

        JPanel controlBar = buildControlBar();
        add(controlBar, BorderLayout.SOUTH);
    }

    private JPanel buildStatsPanel() {
        RoundedPanel panel = new RoundedPanel(0);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        JLabel headerLabel = new JLabel("Solver Stats");
        headerLabel.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 16f));
        headerLabel.setForeground(Theme.getText());
        headerLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(16));

        stepLabel      = addStatRow(panel, "Step: 0 / 0");
        depthLabel     = addStatRow(panel, "Depth: 0");
        visitedLabel   = addStatRow(panel, "Visited: 0");
        solutionsLabel = addStatRow(panel, "Solutions: 0");
        pathLabel      = addStatRow(panel, "Path: N/A");
        timeLabel      = addStatRow(panel, "Time: 0 ms");

        panel.add(Box.createVerticalStrut(16));

        JLabel logLabel = new JLabel("Step Log");
        logLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 11f));
        logLabel.setForeground(Theme.getSecondaryText());
        logLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(logLabel);
        panel.add(Box.createVerticalStrut(6));

        logArea = new JTextArea();
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logArea.setEditable(false);
        logArea.setBackground(Theme.getBackground());
        logArea.setForeground(Theme.getSecondaryText());
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        scroll.setPreferredSize(new Dimension(200, 180));
        scroll.setBorder(BorderFactory.createLineBorder(Theme.getBorder()));
        panel.add(scroll);

        return panel;
    }

    private JLabel addStatRow(JPanel parent, String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BODY.deriveFont(13f));
        label.setForeground(Theme.getText());
        label.setAlignmentX(LEFT_ALIGNMENT);
        parent.add(label);
        parent.add(Box.createVerticalStrut(6));
        return label;
    }

    private JPanel buildControlBar() {
        RoundedPanel bar = new RoundedPanel(0);
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 10));
        bar.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        JButton runBtn = createBarButton("Run Solver", Theme.BLUE, Color.WHITE);
        JButton instantBtn = createBarButton("Instant Solve", Theme.PURPLE, Color.WHITE);

        JLabel sep1 = new JLabel("|");
        sep1.setForeground(Theme.getSecondaryText());

        JButton stepBackBtn = createBarButton("Step Back", Theme.getCard(), Theme.getText());
        JButton playBtn = createBarButton("Play", Theme.getCard(), Theme.getText());
        JButton pauseBtn = createBarButton("Pause", Theme.getCard(), Theme.getText());
        JButton stepFwdBtn = createBarButton("Step Forward", Theme.getCard(), Theme.getText());

        JLabel sep2 = new JLabel("|");
        sep2.setForeground(Theme.getSecondaryText());

        JButton resetBtn = createBarButton("Reset", Theme.RED, Color.WHITE);

        JLabel speedLabel = new JLabel("Speed:");
        speedLabel.setFont(Theme.FONT_BODY.deriveFont(13f));
        speedLabel.setForeground(Theme.getText());

        final JSlider speedSlider = new JSlider(10, 200, 40);
        speedSlider.setOpaque(false);
        speedSlider.setPreferredSize(new Dimension(120, 30));

        runBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.solveMaze();
                updateStats();
                canvas.repaint();
            }
        });

        instantBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.solveInstant();
                updateStats();
                canvas.repaint();
            }
        });

        stepBackBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.stepBackward();
                canvas.repaint();
            }
        });

        playBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.startAnimation();
            }
        });

        pauseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.pauseAnimation();
            }
        });

        stepFwdBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.stepForward();
                canvas.repaint();
            }
        });

        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.resetVisualization();
                stepLabel.setText("Step: 0 / 0");
                depthLabel.setText("Depth: 0");
                visitedLabel.setText("Visited: 0");
                solutionsLabel.setText("Solutions: 0");
                pathLabel.setText("Path: N/A");
                timeLabel.setText("Time: 0 ms");
                logArea.setText("");
                canvas.repaint();
            }
        });

        speedSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                controller.setAnimationDelay(speedSlider.getValue());
            }
        });

        bar.add(runBtn);
        bar.add(instantBtn);
        bar.add(sep1);
        bar.add(stepBackBtn);
        bar.add(playBtn);
        bar.add(pauseBtn);
        bar.add(stepFwdBtn);
        bar.add(sep2);
        bar.add(resetBtn);
        bar.add(speedLabel);
        bar.add(speedSlider);

        return bar;
    }

    private JButton createBarButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BODY.deriveFont(Font.PLAIN, 12f));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        return btn;
    }

    public void repaintGrid() {
        canvas.repaint();
    }

    public void updateStepLabel(int current, int total) {
        stepLabel.setText("Step: " + current + " / " + total);
        appendLog("Step " + current + " of " + total);
    }

    private void appendLog(String line) {
        String existing = logArea.getText();
        String[] lines = existing.split("\n", -1);
        if (lines.length > 100) {
            StringBuilder sb = new StringBuilder();
            for (int i = lines.length - 99; i < lines.length; i++) {
                sb.append(lines[i]).append("\n");
            }
            logArea.setText(sb.toString());
        }
        logArea.append(line + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void onAnimationFinished() {
        updateStats();
        JOptionPane.showMessageDialog(this, "Solving complete!", "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateStats() {
        SolveResult result = controller.getLastResult();
        if (result != null) {
            visitedLabel.setText("Visited: " + result.getCellsVisited());
            solutionsLabel.setText("Solutions: " + result.getSolutionCount());
            depthLabel.setText("Depth: " + result.getMaxDepth());
            timeLabel.setText("Time: " + result.getElapsedMs() + " ms");
            int pathLen = result.getShortestPathLength();
            pathLabel.setText("Path: " + (pathLen > 0 ? pathLen + " cells" : "N/A"));
        }
        canvas.repaint();
    }

    public void refreshTheme() {
        logArea.setBackground(Theme.getBackground());
        logArea.setForeground(Theme.getSecondaryText());
        canvas.repaint();
        repaint();
    }

    // ----------------------- Inner Visualization Canvas -----------------------

    private class VisualizationCanvas extends JPanel {

        private static final int BASE_CELL_SIZE = 25;

        public VisualizationCanvas() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Theme.getBackground());

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Cell[][] displayGrid = controller.getDisplayGrid();
            if (displayGrid == null) {
                displayGrid = controller.getMaze().getGrid();
            }

            if (displayGrid == null || displayGrid.length == 0) {
                g2.dispose();
                return;
            }

            int rows = displayGrid.length;
            int cols = displayGrid[0].length;

            int cellSize = Math.min(
                Math.max(4, getWidth() / cols),
                Math.max(4, getHeight() / rows)
            );
            cellSize = Math.min(cellSize, BASE_CELL_SIZE);

            int totalW = cellSize * cols;
            int totalH = cellSize * rows;
            int offX = (getWidth() - totalW) / 2;
            int offY = (getHeight() - totalH) / 2;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int x = offX + c * cellSize;
                    int y = offY + r * cellSize;
                    Color cellColor;
                    switch (displayGrid[r][c]) {
                        case WALL:     cellColor = Theme.getCellWall();    break;
                        case START:    cellColor = Theme.CELL_START;       break;
                        case END:      cellColor = Theme.CELL_END;         break;
                        case VISITED:  cellColor = Theme.CELL_VISITED;     break;
                        case FOUND:    cellColor = Theme.CELL_FOUND;       break;
                        case DEAD_END: cellColor = Theme.CELL_DEAD;        break;
                        default:       cellColor = Theme.getCellPath();    break;
                    }
                    g2.setColor(cellColor);
                    g2.fillRect(x, y, cellSize, cellSize);

                    if (cellSize > 6) {
                        g2.setColor(Theme.getBackground().darker());
                        g2.setStroke(new BasicStroke(0.5f));
                        g2.drawRect(x, y, cellSize, cellSize);
                    }
                }
            }

            g2.dispose();
        }
    }
}
