package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import controller.MazeController;
import model.SolveStatistics;
import view.components.RoundedPanel;

/**
 * The statistics panel. Renders custom performance gauges, recursion efficiency
 * stats, and pathing optimization ratios to evaluate backtracking metrics.
 */
public class StatsPanel extends JPanel {
    private final MazeController controller;

    private JLabel lblDuration;
    private JLabel lblSolutions;
    private JLabel lblVisited;
    private JLabel lblDepth;
    private JLabel lblLength;
    private JLabel lblEfficiency;

    private EfficiencyGaugePanel gaugePanel;

    /**
     * Constructs a StatsPanel linked to the coordinator.
     */
    public StatsPanel(MazeController controller) {
        this.controller = controller;

        setOpaque(false);
        setLayout(new BorderLayout(24, 24));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        setupUI();
    }

    private void setupUI() {
        // --- HEADER ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Performance & Analytics");
        lblTitle.setFont(Theme.getTitleFont(28));
        lblTitle.setForeground(Theme.getPrimaryTextColor());

        JLabel lblSubtitle = new JLabel("Detailed complexity analysis, backtracking pathing ratios, and safety gauges.");
        lblSubtitle.setFont(Theme.getBodyFont(14));
        lblSubtitle.setForeground(Theme.getSecondaryTextColor());

        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);
        add(headerPanel, BorderLayout.NORTH);

        // --- CONTENT BODY ---
        JPanel contentGrid = new JPanel(new GridBagLayout());
        contentGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Left Side: Core stats text card
        RoundedPanel statsCard = new RoundedPanel(16);
        statsCard.setBackground(Theme.getCardBgColor());
        statsCard.setLayout(new GridLayout(6, 1, 10, 10));
        statsCard.setBorder(new EmptyBorder(24, 24, 24, 24));

        lblDuration = createStatRow(statsCard, "Execution Duration:", "0 ms");
        lblSolutions = createStatRow(statsCard, "Total Solution Paths:", "0");
        lblVisited = createStatRow(statsCard, "Total Cells Visited:", "0");
        lblDepth = createStatRow(statsCard, "Max Recursion Depth:", "0");
        lblLength = createStatRow(statsCard, "Shortest Path Length:", "N/A");
        lblEfficiency = createStatRow(statsCard, "Search Efficiency Ratio:", "0.0%");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        contentGrid.add(statsCard, gbc);

        // Right Side: Graphic Visual Gauges
        RoundedPanel gaugeContainer = new RoundedPanel(16);
        gaugeContainer.setBackground(Theme.getCardBgColor());
        gaugeContainer.setLayout(new BorderLayout());
        gaugeContainer.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel gaugeTitle = new JLabel("Optimization Profile Visualizer");
        gaugeTitle.setFont(Theme.getSemiboldFont(16));
        gaugeTitle.setForeground(Theme.getPrimaryTextColor());
        gaugeTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        gaugeContainer.add(gaugeTitle, BorderLayout.NORTH);

        gaugePanel = new EfficiencyGaugePanel();
        gaugeContainer.add(gaugePanel, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        contentGrid.add(gaugeContainer, gbc);

        add(contentGrid, BorderLayout.CENTER);
    }

    private JLabel createStatRow(JPanel parent, String labelText, String defaultValue) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel title = new JLabel(labelText);
        title.setFont(Theme.getBodyFont(14));
        title.setForeground(Theme.getSecondaryTextColor());

        JLabel val = new JLabel(defaultValue);
        val.setFont(Theme.getTitleFont(15));
        val.setForeground(Theme.getPrimaryTextColor());

        row.add(title, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        parent.add(row);

        return val;
    }

    /**
     * Refreshes stats values and recalculates execution metrics.
     */
    public void updateStatsData() {
        SolveStatistics stats = controller.getLastSolveStats();
        
        lblDuration.setText(stats.getExecutionTimeMs() + " ms");
        lblSolutions.setText(String.valueOf(stats.getTotalSolutions()));
        lblVisited.setText(String.valueOf(stats.getVisitedNodes()));
        lblDepth.setText(String.valueOf(stats.getMaxRecursionDepth()));
        
        int pathLen = stats.getShortestPathLength();
        lblLength.setText(pathLen > 0 ? pathLen + " cells" : "N/A");

        // Efficiency ratio: shortest path length divided by visited cells count.
        // Higher means solver didn't waste time on dead ends.
        if (stats.getVisitedNodes() > 0 && pathLen > 0) {
            double efficiency = (double) pathLen / stats.getVisitedNodes() * 100;
            lblEfficiency.setText(String.format("%.1f%%", efficiency));
        } else {
            lblEfficiency.setText("0.0%");
        }

        gaugePanel.repaint();
    }

    // --- GRAPHICAL PERFORMANCE GAUGES ---

    private class EfficiencyGaugePanel extends JPanel {
        public EfficiencyGaugePanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int centerX = w / 2;
            int centerY = h / 2;

            SolveStatistics stats = controller.getLastSolveStats();
            int pathLen = stats.getShortestPathLength();
            int visited = stats.getVisitedNodes();

            double ratio = 0.0;
            if (visited > 0 && pathLen > 0) {
                ratio = (double) pathLen / visited;
            }

            // Draw a beautiful circular dial progress loop representing Search Efficiency
            int diameter = Math.min(w, h) - 60;
            int x = centerX - diameter / 2;
            int y = centerY - diameter / 2;

            // Background Track ring
            g2.setStroke(new java.awt.BasicStroke(16.0f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
            g2.setColor(Theme.getMode() == Theme.Mode.DARK ? new Color(50, 50, 56) : new Color(225, 230, 235));
            g2.drawArc(x, y, diameter, diameter, -225, 270);

            // Active Track sweep
            int angleSweep = (int) (ratio * 270);
            g2.setColor(Theme.getSecondaryAccentColor());
            g2.drawArc(x, y, diameter, diameter, -225, -angleSweep);

            // Text inside center
            g2.setFont(Theme.getTitleFont(24));
            g2.setColor(Theme.getPrimaryTextColor());
            String text = String.format("%.0f%%", ratio * 100);
            int tw = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, centerX - tw / 2, centerY + 8);

            // Description Label
            g2.setFont(Theme.getBodyFont(12));
            g2.setColor(Theme.getSecondaryTextColor());
            String desc = "DFS Solve Efficiency";
            int dw = g2.getFontMetrics().stringWidth(desc);
            g2.drawString(desc, centerX - dw / 2, centerY + diameter / 2 + 20);

            g2.dispose();
        }
    }
}
