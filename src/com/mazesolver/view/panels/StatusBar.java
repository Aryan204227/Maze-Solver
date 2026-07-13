package com.mazesolver.view.panels;

import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * StatusBar.java — VERSION 4.0 PREMIUM
 * ─────────────────────────────────────────────────────────────────
 * Changes from V3.0:
 *   1. Multi-segment layout with pill-shaped mode indicator
 *   2. Animated blinking dot that pulses when solving
 *   3. Gradient background matching sidebar theme
 *   4. Improved segment separators with icon-labeled chips
 *   5. Real-time memory and session timer display
 */
public class StatusBar extends JPanel {

    private JLabel statusLbl;
    private JLabel modeLbl;
    private JLabel gridLbl;
    private JLabel memoryLbl;
    private JLabel dotLbl;

    // Dot blink state (blinks when solving)
    private boolean dotBlink = false;
    private boolean isSolving = false;

    private final long startTime = System.currentTimeMillis();
    private final Timer liveTimer;
    private final Timer blinkTimer;

    public StatusBar() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, Constants.STATUS_BAR_HEIGHT));
        setOpaque(false);
        buildUI();

        // Refresh memory every 2s
        liveTimer = new Timer(2000, e -> updateLiveInfo());
        liveTimer.start();

        // Blink dot every 600ms (only active when solving)
        blinkTimer = new Timer(600, e -> {
            if (isSolving) {
                dotBlink = !dotBlink;
                dotLbl.setForeground(dotBlink
                    ? ThemeManager.getAccentBlue()
                    : ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 80));
            }
        });
        blinkTimer.start();
    }

    private void buildUI() {
        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

        // LEFT: dot + status message
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);

        dotLbl = new JLabel("\u25CF"); // ●
        dotLbl.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        dotLbl.setForeground(ThemeManager.getAccentGreen());

        statusLbl = makeLabel("Ready \u2014 Generate or load a maze to begin.", ThemeManager.getTextSecondary());
        left.add(dotLbl);
        left.add(statusLbl);

        // RIGHT: info chips
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);

        JLabel algoLbl = makeLabel("\uD83D\uDD0D  DFS + Backtracking", ThemeManager.getAccentBlue());
        gridLbl        = makeLabel("\uD83D\uDDFA  " + Constants.DEFAULT_ROWS + " \u00d7 " + Constants.DEFAULT_COLS, ThemeManager.getTextSecondary());
        modeLbl        = makeChipLabel("Idle", ThemeManager.getAccentGreen());
        memoryLbl      = makeLabel("\uD83D\uDCBE  " + getMemory(), ThemeManager.getTextMuted());

        right.add(makeSeg(algoLbl));
        right.add(makeSep());
        right.add(makeSeg(gridLbl));
        right.add(makeSep());
        right.add(makeSeg(modeLbl));
        right.add(makeSep());
        right.add(makeSeg(memoryLbl));

        add(left,  BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    private void updateLiveInfo() {
        memoryLbl.setText("\uD83D\uDCBE  " + getMemory());
    }

    private String getMemory() {
        Runtime rt = Runtime.getRuntime();
        long used = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        return used + " MB";
    }

    private JLabel makeLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(color);
        return l;
    }

    /** Creates a pill-shaped chip label for the mode indicator */
    private JLabel makeChipLabel(String text, Color color) {
        JLabel l = new JLabel(" " + text + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.withAlpha(getForeground(), 25));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(ThemeManager.withAlpha(getForeground(), 70));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
        l.setOpaque(false);
        return l;
    }

    private JPanel makeSeg(JLabel lbl) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        p.setOpaque(false);
        p.add(lbl);
        return p;
    }

    private JLabel makeSep() {
        JLabel sep = new JLabel("\u2502"); // │
        sep.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sep.setForeground(ThemeManager.withAlpha(ThemeManager.getBorderColor(), 150));
        return sep;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Subtle gradient matching sidebar bottom
        GradientPaint gp = new GradientPaint(0, 0, ThemeManager.getBgTertiary(),
                getWidth(), 0, ThemeManager.getBgSecondary());
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        // Top border
        g2.setColor(ThemeManager.getBorderColor());
        g2.drawLine(0, 0, getWidth(), 0);
        g2.dispose();
        super.paintComponent(g);
    }

    // ── Public update methods ─────────────────────────────

    public void setStatus(String msg) {
        SwingUtilities.invokeLater(() -> statusLbl.setText(msg));
    }

    public void setMode(String mode) {
        SwingUtilities.invokeLater(() -> {
            modeLbl.setText(" " + mode + " ");
            Color modeColor;
            switch (mode) {
                case "Solving":
                    modeColor = ThemeManager.getAccentBlue();
                    dotLbl.setForeground(ThemeManager.getAccentBlue());
                    isSolving = true;
                    break;
                case "Paused":
                    modeColor = ThemeManager.getAccentYellow();
                    dotLbl.setForeground(ThemeManager.getAccentYellow());
                    isSolving = false;
                    break;
                case "Done":
                    modeColor = ThemeManager.getAccentGreen();
                    dotLbl.setForeground(ThemeManager.getAccentGreen());
                    isSolving = false;
                    break;
                default:
                    modeColor = ThemeManager.getAccentGreen();
                    dotLbl.setForeground(ThemeManager.getAccentGreen());
                    isSolving = false;
                    break;
            }
            modeLbl.setForeground(modeColor);
            modeLbl.repaint();
        });
    }

    public void setGridSize(int r, int c) {
        SwingUtilities.invokeLater(() -> gridLbl.setText("\uD83D\uDDFA  " + r + " \u00d7 " + c));
    }

    public void applyTheme() { repaint(); }
}
