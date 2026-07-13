package com.mazesolver.view.panels;

import com.mazesolver.model.SolverStats;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * LearningPanel.java
 * ─────────────────────────────────────────────────────
 * Shows a live trace of the DFS algorithm while solving.
 * Displays the current action, reason, and call stack —
 * updated in real-time as the solver runs.
 *
 * WHY THIS EXISTS:
 *   During solving, it can be hard to understand what the
 *   algorithm is doing just by watching the grid.
 *   This panel explains each step in plain English.
 *
 * WHAT IT SHOWS:
 *   • Current action (what DFS is doing right now)
 *   • Reason (why this action is happening)
 *   • Active cell coordinates and recursion depth
 *   • Live call stack (path being explored)
 *
 * JAVA CONCEPTS USED:
 *   - Custom JPanel painting
 *   - SwingUtilities.invokeLater for thread-safe UI updates
 */
public class LearningPanel extends JPanel {

    // ── Labels for current step info ──────────────────────
    private JLabel lblAction;
    private JLabel lblReason;
    private JLabel lblNext;

    // ── Labels for current state ──────────────────────────
    private JLabel lblCell;
    private JLabel lblDirection;
    private JLabel lblDepth;
    private JLabel lblBacktracks;
    private JLabel lblSolutions;

    // ── Call stack display ────────────────────────────────
    private JTextArea stackArea;

    public LearningPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        buildUI();
    }

    private void buildUI() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // ── Header ────────────────────────────────────────
        container.add(buildHeader());
        container.add(Box.createVerticalStrut(14));

        // ── Current Step ──────────────────────────────────
        container.add(makeSectionLabel("CURRENT STEP"));
        container.add(Box.createVerticalStrut(6));
        container.add(buildCurrentStepCard());
        container.add(Box.createVerticalStrut(14));

        // ── Current State ─────────────────────────────────
        container.add(makeSectionLabel("ALGORITHM STATE"));
        container.add(Box.createVerticalStrut(6));
        container.add(buildStateGrid());
        container.add(Box.createVerticalStrut(14));

        // ── Call Stack ────────────────────────────────────
        container.add(makeSectionLabel("CALL STACK  (active path)"));
        container.add(Box.createVerticalStrut(6));
        container.add(buildStackCard());
        container.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        add(scroll, BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getBgCard());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                // Top accent bar
                g2.setColor(ThemeManager.getAccentBlue());
                g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);
                g2.setColor(ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Algorithm Trace");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(ThemeManager.getTextPrimary());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("DFS + Backtracking — live step explanation");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(ThemeManager.getTextSecondary());
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(3));
        panel.add(sub);
        return panel;
    }

    // ── Current Step Card ─────────────────────────────────
    private JPanel buildCurrentStepCard() {
        JPanel card = makeCard(ThemeManager.getAccentBlue());
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Action row
        JPanel actionRow = makeRow("Action:", "Waiting for solver...");
        lblAction = (JLabel) ((JPanel) actionRow.getComponent(1)).getComponent(0);
        lblAction.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAction.setForeground(ThemeManager.getTextPrimary());

        // Reason row
        JPanel reasonRow = makeRow("Reason:", "—");
        lblReason = (JLabel) ((JPanel) reasonRow.getComponent(1)).getComponent(0);
        lblReason.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblReason.setForeground(ThemeManager.getTextSecondary());

        // Next row
        JPanel nextRow = makeRow("Next:", "—");
        lblNext = (JLabel) ((JPanel) nextRow.getComponent(1)).getComponent(0);
        lblNext.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNext.setForeground(ThemeManager.getTextMuted());

        card.add(actionRow);
        card.add(Box.createVerticalStrut(5));
        card.add(reasonRow);
        card.add(Box.createVerticalStrut(5));
        card.add(nextRow);
        return card;
    }

    // ── State Grid ────────────────────────────────────────
    private JPanel buildStateGrid() {
        JPanel grid = new JPanel(new GridLayout(5, 2, 8, 5));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblCell       = addRow(grid, "Cell:",          "(—, —)",   ThemeManager.getAccentBlue());
        lblDirection  = addRow(grid, "Direction:",     "—",         ThemeManager.getAccentGreen());
        lblDepth      = addRow(grid, "Depth:",         "0",         ThemeManager.getAccentPurple());
        lblBacktracks = addRow(grid, "Backtracks:",    "0",         ThemeManager.getAccentYellow());
        lblSolutions  = addRow(grid, "Solutions:",     "0",         new Color(0xF472B6));

        return grid;
    }

    private JLabel addRow(JPanel parent, String key, String val, Color color) {
        JLabel keyLbl = new JLabel(key);
        keyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        keyLbl.setForeground(ThemeManager.getTextSecondary());

        JLabel valLbl = new JLabel(val);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valLbl.setForeground(color);

        parent.add(keyLbl);
        parent.add(valLbl);
        return valLbl;
    }

    // ── Call Stack Card ───────────────────────────────────
    private JPanel buildStackCard() {
        JPanel card = makeCard(ThemeManager.getAccentPurple());
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        stackArea = new JTextArea("Stack is empty.");
        stackArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        stackArea.setForeground(ThemeManager.getTextSecondary());
        stackArea.setBackground(new Color(0, 0, 0, 0));
        stackArea.setOpaque(false);
        stackArea.setEditable(false);
        stackArea.setLineWrap(true);
        stackArea.setWrapStyleWord(true);
        stackArea.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        JScrollPane sp = new JScrollPane(stackArea);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setPreferredSize(new Dimension(0, 110));

        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // ── Helper: row panel with key + value label ──────────
    private JPanel makeRow(String key, String initValue) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel keyLbl = new JLabel(key);
        keyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        keyLbl.setForeground(ThemeManager.getTextMuted());
        keyLbl.setPreferredSize(new Dimension(55, 18));

        JPanel valWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valWrap.setOpaque(false);
        JLabel valLbl = new JLabel(initValue);
        valWrap.add(valLbl);

        row.add(keyLbl, BorderLayout.WEST);
        row.add(valWrap, BorderLayout.CENTER);
        return row;
    }

    // ── Helper: card panel with accent left bar ───────────
    private JPanel makeCard(Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getBgCard());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                // Left accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 6, 3, getHeight() - 12, 3, 3);
                // Border
                g2.setColor(ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 10));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return card;
    }

    // ── Helper: section label ─────────────────────────────
    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(ThemeManager.getTextMuted());
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ── Background paint ──────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(ThemeManager.getBgSecondary());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(ThemeManager.getBorderColor());
        g2.drawLine(0, 0, 0, getHeight());
        g2.dispose();
    }

    // ── Public update methods ─────────────────────────────

    /**
     * Updates the trace panel with the current solver state.
     * This is called by MazeController on each step.
     */
    public void updateLearning(SolverStats stats) {
        if (stats == null) return;

        SwingUtilities.invokeLater(() -> {
            // Update state labels
            lblCell.setText("(" + stats.getCurrentR() + ", " + stats.getCurrentC() + ")");
            lblDirection.setText(stats.getCurrentDirection());
            lblDepth.setText(String.valueOf(stats.getCurrentDepth()));
            lblBacktracks.setText(String.valueOf(stats.getBacktrackCount()));
            lblSolutions.setText(String.valueOf(stats.getTotalSolutions()));

            // Parse the action/reason from the current decision
            String decision = stats.getCurrentDecision();
            if (decision == null || decision.isEmpty() || decision.equals("—")) {
                lblAction.setText("Idle");
                lblReason.setText("—");
                lblNext.setText("—");
            } else if (decision.contains("Trying")) {
                // e.g. "Trying Right from (3,4)"
                String dir = decision.replace("Trying ", "").split(" from")[0];
                lblAction.setText("Moving " + dir);
                lblReason.setText("Neighbour is open and unvisited.");
                lblNext.setText("Recurse deeper into this cell.");
            } else if (decision.contains("backtracking") || decision.contains("Dead end")) {
                lblAction.setText("Backtracking");
                lblReason.setText("All neighbours are walls or already visited.");
                lblNext.setText("Return to previous cell and try next direction.");
            } else if (decision.contains("Solving completed")) {
                lblAction.setText("Solve complete");
                lblReason.setText("All reachable paths have been explored.");
                lblNext.setText("Review solution path highlighted in green.");
            } else {
                // fallback: show the raw decision string
                lblAction.setText(decision.length() > 40 ? decision.substring(0, 40) + "..." : decision);
                lblReason.setText("—");
                lblNext.setText("—");
            }

            // Update call stack display
            List<String> stack = stats.getCurrentStack();
            if (stack.isEmpty()) {
                stackArea.setText("Stack is empty.");
            } else {
                StringBuilder sb = new StringBuilder();
                int total = stack.size();
                for (int i = total - 1; i >= 0; i--) {
                    if (i == total - 1) {
                        sb.append("> ").append(stack.get(i)).append("  ← current\n");
                    } else if (i == 0) {
                        sb.append("  ").append(stack.get(i)).append("  (start)\n");
                    } else {
                        sb.append("  ").append(stack.get(i)).append("\n");
                    }
                }
                stackArea.setText(sb.toString().trim());
            }
        });
    }

    /** Resets all labels to their idle/initial state. */
    public void resetLearning() {
        SwingUtilities.invokeLater(() -> {
            lblAction.setText("Waiting for solver...");
            lblReason.setText("—");
            lblNext.setText("—");
            lblCell.setText("(—, —)");
            lblDirection.setText("—");
            lblDepth.setText("0");
            lblBacktracks.setText("0");
            lblSolutions.setText("0");
            stackArea.setText("Stack is empty.");
        });
    }

    public void applyTheme() { repaint(); }
}
