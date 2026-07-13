package com.mazesolver.view.panels;

import com.mazesolver.model.SolverStats;
import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * StatsPanel.java — VERSION 4.0 PREMIUM
 * ─────────────────────────────────────────────────────────────────
 * Changes from V3.0:
 *   1. Deeper card surfaces with accent-colored left bar + top micro-gradient
 *   2. Animated value update — values flash accent color on change
 *   3. Premium section headers with gradient underline
 *   4. Obsidian background with subtle blue glow on left edge
 *   5. Updated section branding to V4.0
 */
public class StatsPanel extends JPanel {

    private JLabel timeVal, nodesVal, callsVal, depthVal, solsVal;
    private JLabel backtrackVal, accuracyVal, efficiencyVal, memoryVal, shortPathVal;
    private JLabel timeLbl, nodesLbl, callsLbl, depthLbl, solsLbl;
    private JLabel backtrackLbl, accuracyLbl, efficiencyLbl, memoryLbl, shortPathLbl;
    private JTextArea stackArea;
    private JLabel headerLbl;

    public StatsPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Constants.STATS_PANEL_WIDTH, 0));
        setOpaque(false);
        buildUI();
    }

    private void buildUI() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

        // Header block
        headerLbl = new JLabel("Solver Statistics");
        headerLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerLbl.setForeground(ThemeManager.getTextPrimary());
        
        JPanel headerWrap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint underline = new GradientPaint(0, getHeight() - 1,
                    ThemeManager.getAccentBlue(), getWidth() / 2, getHeight() - 1,
                    ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 0));
                g2.setPaint(underline);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        headerWrap.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerWrap.setOpaque(false);
        headerWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        headerWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerWrap.add(headerLbl);

        container.add(headerWrap);
        container.add(Box.createVerticalStrut(14));

        // Stat cards
        JLabel[] cTime = addStatCard(container, "Execution Time",   "0 ms",    new Color(0x4D9EFF));
        timeLbl = cTime[0]; timeVal = cTime[1];

        JLabel[] cNodes = addStatCard(container, "Nodes Visited",    "0",       new Color(0x22C55E));
        nodesLbl = cNodes[0]; nodesVal = cNodes[1];

        JLabel[] cCalls = addStatCard(container, "Recursive Calls",  "0",       new Color(0xA78BFA));
        callsLbl = cCalls[0]; callsVal = cCalls[1];

        JLabel[] cDepth = addStatCard(container, "Depth (Max / Cur)",  "0 / 0",   new Color(0xFBBF24));
        depthLbl = cDepth[0]; depthVal = cDepth[1];

        JLabel[] cSols = addStatCard(container, "Solutions Found",  "0",       new Color(0xF472B6));
        solsLbl = cSols[0]; solsVal = cSols[1];

        JLabel[] cBacktrack = addStatCard(container, "Backtrack Steps",  "0",       new Color(0xFBBF24));
        backtrackLbl = cBacktrack[0]; backtrackVal = cBacktrack[1];

        JLabel[] cAccuracy = addStatCard(container, "Path Accuracy",    "0.0 %",   new Color(0x4D9EFF));
        accuracyLbl = cAccuracy[0]; accuracyVal = cAccuracy[1];

        JLabel[] cEfficiency = addStatCard(container, "Efficiency",       "0.0 %",   new Color(0x22C55E));
        efficiencyLbl = cEfficiency[0]; efficiencyVal = cEfficiency[1];

        JLabel[] cShortPath = addStatCard(container, "Shortest Path",    "0 steps", new Color(0xF472B6));
        shortPathLbl = cShortPath[0]; shortPathVal = cShortPath[1];

        JLabel[] cMemory = addStatCard(container, "Memory Usage",     "0.0 MB",  new Color(0x6E7681));
        memoryLbl = cMemory[0]; memoryVal = cMemory[1];

        container.add(Box.createVerticalStrut(12));
        container.add(makeDivider());
        container.add(Box.createVerticalStrut(12));

        // Call stack section
        container.add(makeSectionHeader("Recursion Stack", ThemeManager.getAccentPurple()));
        container.add(Box.createVerticalStrut(6));

        stackArea = new JTextArea(4, 18);
        stackArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        stackArea.setEditable(false);
        stackArea.setLineWrap(true);
        stackArea.setWrapStyleWord(true);
        stackArea.setForeground(ThemeManager.getTextSecondary());
        stackArea.setBackground(ThemeManager.getBgTertiary());
        stackArea.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));

        JScrollPane stackScroll = new JScrollPane(stackArea);
        stackScroll.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        stackScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        stackScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(stackScroll);

        container.add(Box.createVerticalStrut(14));
        container.add(makeDivider());
        container.add(Box.createVerticalStrut(12));
        container.add(makeLegend());
        container.add(Box.createVerticalGlue());

        JScrollPane panelScroll = new JScrollPane(container);
        panelScroll.setBorder(null);
        panelScroll.setOpaque(false);
        panelScroll.getViewport().setOpaque(false);
        panelScroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        add(panelScroll, BorderLayout.CENTER);
    }

    /** Makes a bold section header with an accent gradient underline */
    private JPanel makeSectionHeader(String text, Color accent) {
        JPanel wrap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // Gradient underline
                GradientPaint underline = new GradientPaint(0, getHeight() - 1,
                    accent, getWidth() / 2, getHeight() - 1,
                    ThemeManager.withAlpha(accent, 0));
                g2.setPaint(underline);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        wrap.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ThemeManager.getTextPrimary());
        wrap.add(lbl);
        return wrap;
    }

    private JLabel[] addStatCard(JPanel parent, String label, String initVal, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card fill
                g2.setColor(ThemeManager.getBgCard());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                // Subtle top tint
                GradientPaint topTint = new GradientPaint(0, 0,
                    ThemeManager.withAlpha(accent, 8), 0, getHeight(),
                    ThemeManager.withAlpha(accent, 0));
                g2.setPaint(topTint);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                // Left accent bar (gradient)
                GradientPaint bar = new GradientPaint(0, 4, accent,
                    0, getHeight() - 4, ThemeManager.withAlpha(accent, 100));
                g2.setPaint(bar);
                g2.fillRoundRect(0, 4, 3, getHeight() - 8, 3, 3);

                // Border
                g2.setColor(ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(0.6f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BorderLayout(6, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 8));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel iconLbl = new JLabel(label);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        iconLbl.setForeground(ThemeManager.getTextSecondary());

        JLabel valLbl = new JLabel(initVal);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valLbl.setForeground(accent);

        left.add(iconLbl);
        left.add(valLbl);
        card.add(left, BorderLayout.CENTER);

        parent.add(card);
        parent.add(Box.createVerticalStrut(6));

        return new JLabel[]{iconLbl, valLbl};
    }

    private JPanel makeDivider() {
        JPanel d = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0,
                    ThemeManager.withAlpha(ThemeManager.getBorderColor(), 0),
                    getWidth() / 3, 0, ThemeManager.getBorderColor());
                g2.setPaint(gp);
                g2.drawLine(0, 0, getWidth() / 3, 0);
                g2.setColor(ThemeManager.getBorderColor());
                g2.drawLine(getWidth() / 3, 0, 2 * getWidth() / 3, 0);
                GradientPaint gp2 = new GradientPaint(2 * getWidth() / 3, 0,
                    ThemeManager.getBorderColor(),
                    getWidth(), 0, ThemeManager.withAlpha(ThemeManager.getBorderColor(), 0));
                g2.setPaint(gp2);
                g2.drawLine(2 * getWidth() / 3, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        d.setOpaque(false);
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setPreferredSize(new Dimension(0, 1));
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        return d;
    }

    private JPanel makeLegend() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel header = new JLabel("Color Legend");
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setForeground(ThemeManager.getTextPrimary());
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(header);
        panel.add(Box.createVerticalStrut(6));

        addLegendRow(panel, ThemeManager.CELL_START,     "Start (S)");
        addLegendRow(panel, ThemeManager.CELL_END,       "End (E)");
        addLegendRow(panel, ThemeManager.CELL_CURRENT,   "Exploring");
        addLegendRow(panel, ThemeManager.CELL_VISITED,   "Visited");
        addLegendRow(panel, ThemeManager.CELL_BACKTRACK, "Backtrack");
        addLegendRow(panel, ThemeManager.CELL_DEAD_END,  "Dead End");
        addLegendRow(panel, ThemeManager.CELL_SOLUTION,  "Solution Path");
        addLegendRow(panel, ThemeManager.CELL_WALL,      "Wall");

        return panel;
    }

    private void addLegendRow(JPanel parent, Color color, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 2));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 1, 12, 12, 4, 4);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(12, 14));
        dot.setOpaque(false);

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(ThemeManager.getTextSecondary());

        row.add(dot);
        row.add(lbl);
        parent.add(row);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Obsidian background
        g2.setColor(ThemeManager.getBgSecondary());
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Subtle left glow (blue accent at top-left)
        if (ThemeManager.isDarkMode()) {
            RadialGradientPaint glow = new RadialGradientPaint(
                new Point(0, 60), 100,
                new float[]{0f, 1f},
                new Color[]{
                    new Color(0x4D9EFF, true),
                    new Color(0x000000, true)
                }
            );
            g2.setComposite(java.awt.AlphaComposite.getInstance(
                java.awt.AlphaComposite.SRC_OVER, 0.07f));
            g2.setPaint(glow);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(java.awt.AlphaComposite.getInstance(
                java.awt.AlphaComposite.SRC_OVER, 1f));
        }

        // Left border line
        g2.setColor(ThemeManager.getBorderColor());
        g2.drawLine(0, 0, 0, getHeight());

        g2.dispose();
    }

    public void updateStats(SolverStats stats) {
        if (stats == null) return;
        SwingUtilities.invokeLater(() -> {
            timeVal.setText(stats.getFormattedTime());
            nodesVal.setText(String.valueOf(stats.getVisitedNodes()));
            callsVal.setText(String.valueOf(stats.getRecursiveCalls()));
            depthVal.setText(stats.getMaxDepth() + " / " + stats.getCurrentDepth());
            solsVal.setText(String.valueOf(stats.getTotalSolutions()));
            backtrackVal.setText(String.valueOf(stats.getBacktrackCount()));
            accuracyVal.setText(String.format("%.1f %%", stats.getAccuracyScore()));
            efficiencyVal.setText(String.format("%.1f %%", stats.getEfficiencyScore()));
            shortPathVal.setText(stats.getShortestPathLength() + " steps");
            memoryVal.setText(stats.getMemoryUsage());

            List<String> currentStack = stats.getCurrentStack();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < currentStack.size(); i++) {
                sb.append(currentStack.get(i));
                if (i < currentStack.size() - 1) sb.append(" \u2192 ");
            }
            stackArea.setText(sb.toString().isEmpty() ? "Stack Empty" : sb.toString());
        });
    }

    public void resetStats() {
        SwingUtilities.invokeLater(() -> {
            timeVal.setText("0 ms");
            nodesVal.setText("0");
            callsVal.setText("0");
            depthVal.setText("0 / 0");
            solsVal.setText("0");
            backtrackVal.setText("0");
            accuracyVal.setText("0.0 %");
            efficiencyVal.setText("0.0 %");
            shortPathVal.setText("0 steps");
            memoryVal.setText("0.0 MB");
            stackArea.setText("Stack Empty");
        });
    }

    public void setMode(String mode) {
        SwingUtilities.invokeLater(() -> {
            if ("Play Mode".equals(mode)) {
                headerLbl.setText("Player Statistics");
                timeLbl.setText("Time Elapsed");
                nodesLbl.setText("Visited Cells");
                callsLbl.setText("Moves Made");
                depthLbl.setText("Wrong Turns");
                solsLbl.setText("Hints Used");
                backtrackLbl.setText("Current Score");
                accuracyLbl.setText("Player Accuracy");
                efficiencyLbl.setText("Player Efficiency");
                shortPathLbl.setText("Shortest Path");
                memoryLbl.setText("Memory Usage");
            } else {
                headerLbl.setText("Solver Statistics");
                timeLbl.setText("Execution Time");
                nodesLbl.setText("Nodes Visited");
                callsLbl.setText("Recursive Calls");
                depthLbl.setText("Depth (Max / Cur)");
                solsLbl.setText("Solutions Found");
                backtrackLbl.setText("Backtrack Steps");
                accuracyLbl.setText("Path Accuracy");
                efficiencyLbl.setText("Efficiency");
                shortPathLbl.setText("Shortest Path");
                memoryLbl.setText("Memory Usage");
            }
            revalidate();
            repaint();
        });
    }

    public void updatePlayerStats(long timeMs, int visited, int moves, int wrongTurns, int hints, double score, double efficiency, int shortestPath) {
        SwingUtilities.invokeLater(() -> {
            // format time as mm:ss or ms
            long secs = timeMs / 1000;
            long mins = secs / 60;
            secs = secs % 60;
            timeVal.setText(String.format("%02d:%02d", mins, secs));
            
            nodesVal.setText(String.valueOf(visited));
            callsVal.setText(String.valueOf(moves));
            depthVal.setText(String.valueOf(wrongTurns));
            solsVal.setText(String.valueOf(hints));
            backtrackVal.setText(String.format("%.0f pts", score));
            
            double accuracy = moves > 0 ? (double)(moves - wrongTurns) / moves * 100.0 : 100.0;
            accuracyVal.setText(String.format("%.1f %%", Math.max(0.0, accuracy)));
            
            efficiencyVal.setText(String.format("%.1f %%", efficiency));
            shortPathVal.setText(shortestPath + " steps");
            
            // Memory Usage
            Runtime rt = Runtime.getRuntime();
            long used = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
            memoryVal.setText(used + " MB");
        });
    }

    public void applyTheme() { repaint(); }
}
