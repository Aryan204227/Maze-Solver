package com.mazesolver.view.panels;

import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * DashboardPanel.java — VERSION 4.0 PREMIUM
 * ─────────────────────────────────────────────────────────────────
 * Changes from V3.0:
 *   1. Full-window radial background glow (Aurora effect)
 *   2. Hero card with animated glowing top-bar (blue→purple→pink gradient)
 *   3. Glassmorphic feature cards with drop-shadow simulation
 *   4. Version 4.0 branding in hero badge row
 *   5. Improved hover animations with 3D-lift scale effect
 *   6. Premium "Open Maze Editor" CTA button with shimmer
 */
public class DashboardPanel extends JPanel {

    private Runnable onStartEditor;

    // Animation state for the hero glow bar
    private final Timer pulseTimer;
    private float pulseAlpha = 0f;
    private boolean pulseUp = true;

    // Card hover scale animation (one active card tracked)
    private float[] cardScale;
    private Timer[] cardTimers;
    private static final int CARD_COUNT = 6;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        cardScale  = new float[CARD_COUNT];
        cardTimers = new Timer[CARD_COUNT];
        for (int i = 0; i < CARD_COUNT; i++) cardScale[i] = 1.0f;

        // Subtle pulse for hero glow bar
        pulseTimer = new Timer(35, e -> {
            pulseAlpha += pulseUp ? 0.025f : -0.025f;
            if (pulseAlpha >= 1f) { pulseAlpha = 1f; pulseUp = false; }
            if (pulseAlpha <= 0f) { pulseAlpha = 0f; pulseUp = true;  }
            repaint();
        });
        pulseTimer.start();

        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(30, 56, 30, 56));

        content.add(buildHeroSection());
        content.add(Box.createVerticalStrut(28));
        content.add(buildMetaBar());
        content.add(Box.createVerticalStrut(24));
        content.add(buildFeatureGrid());
        content.add(Box.createVerticalStrut(20));
        content.add(buildAlgoBox());
        content.add(Box.createVerticalStrut(24));
        content.add(buildStartButton());
        content.add(Box.createVerticalStrut(24));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        add(scroll, BorderLayout.CENTER);
    }

    // ── Hero Section ──────────────────────────────────────
    private JPanel buildHeroSection() {
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int W = getWidth(), H = getHeight();

                // Glass card background
                g2.setColor(ThemeManager.getBgCard());
                g2.fillRoundRect(0, 0, W - 1, H - 1, 22, 22);

                // Subtle inner glow
                GradientPaint innerGlow = new GradientPaint(0, 0,
                    ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 8),
                    W, H,
                    ThemeManager.withAlpha(ThemeManager.getAccentPurple(), 6));
                g2.setPaint(innerGlow);
                g2.fillRoundRect(0, 0, W - 1, H - 1, 22, 22);

                // Animated top glow bar — blue → purple → pink
                int alpha1 = (int)(70 + 60 * pulseAlpha);
                int alpha2 = (int)(50 + 50 * pulseAlpha);
                int alpha3 = (int)(40 + 40 * pulseAlpha);
                Color c1 = ThemeManager.withAlpha(ThemeManager.getAccentBlue(),   alpha1);
                Color c2 = ThemeManager.withAlpha(ThemeManager.getAccentPurple(), alpha2);
                Color c3 = ThemeManager.withAlpha(ThemeManager.getAccentPink(),   alpha3);

                // Three-stop gradient via two-segment approach
                GradientPaint gp1 = new GradientPaint(0, 0, c1, W / 2, 0, c2);
                g2.setPaint(gp1);
                g2.fillRoundRect(0, 0, W / 2 + 5, 5, 4, 4);
                GradientPaint gp2 = new GradientPaint(W / 2, 0, c2, W, 0, c3);
                g2.setPaint(gp2);
                g2.fillRoundRect(W / 2 - 5, 0, W / 2 + 5, 5, 4, 4);

                // Border
                g2.setColor(ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 35));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, W - 1, H - 1, 22, 22);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setOpaque(false);
        hero.setBorder(BorderFactory.createEmptyBorder(30, 36, 28, 36));
        hero.setAlignmentX(Component.CENTER_ALIGNMENT);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        JLabel iconLbl = new JLabel("\uD83E\uDDE9", SwingConstants.CENTER); // 🧩
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel("Maze Solver", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLbl.setForeground(ThemeManager.getTextPrimary());
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel(
            "DFS + Backtracking Visualizer  \u00b7  Java Swing",
            SwingConstants.CENTER);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLbl.setForeground(ThemeManager.getTextSecondary());
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        hero.add(iconLbl);
        hero.add(Box.createVerticalStrut(8));
        hero.add(titleLbl);
        hero.add(Box.createVerticalStrut(6));
        hero.add(subLbl);

        return hero;
    }


    // ── Project Metadata Bar ──────────────────────────────
    private JPanel buildMetaBar() {
        JPanel bar = new JPanel(new GridLayout(1, 4, 14, 0));
        bar.setOpaque(false);
        bar.setAlignmentX(Component.CENTER_ALIGNMENT);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        addMetaCard(bar, "\uD83D\uDDFA",  "Grid Size",    "15 \u00d7 15",         ThemeManager.getAccentBlue());
        addMetaCard(bar, "\uD83D\uDD0D",  "Algorithm",    "DFS + Backtrack", ThemeManager.getAccentPurple());
        addMetaCard(bar, "\u26A1",        "Status",       "Ready",           ThemeManager.getAccentGreen());
        addMetaCard(bar, "\uD83C\uDFC6",  "Solutions Cap","50 Paths",        ThemeManager.getAccentYellow());

        return bar;
    }

    private void addMetaCard(JPanel parent, String icon, String label, String value, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card fill
                g2.setColor(ThemeManager.getBgCard());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                // Accent left bar
                GradientPaint accentBar = new GradientPaint(0, 0, accent,
                    0, getHeight(), ThemeManager.withAlpha(accent, 120));
                g2.setPaint(accentBar);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);

                // Border
                g2.setColor(ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 12));

        JLabel iconLbl = new JLabel(icon + "  " + label);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        iconLbl.setForeground(ThemeManager.getTextSecondary());
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valLbl.setForeground(ThemeManager.getTextPrimary());
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valLbl);
        parent.add(card);
    }

    // ── Feature Cards Grid ────────────────────────────────
    private JPanel buildFeatureGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.CENTER_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));

        addFeatureCard(grid, 0, "Generate Maze",      "Random, Spiral, Corridor, Checkerboard templates", new Color(0x8250DF));
        addFeatureCard(grid, 1, "Solve Maze",          "DFS + Backtracking — explores all possible paths",  new Color(0x22C55E));
        addFeatureCard(grid, 2, "Live Statistics",     "Nodes visited, recursion depth, efficiency score",  new Color(0x4D9EFF));
        addFeatureCard(grid, 3, "Algorithm Trace",     "Step-by-step explanation of each DFS decision",     new Color(0xF472B6));
        addFeatureCard(grid, 4, "Edit Walls",          "Click and drag to draw or erase walls on the grid", new Color(0xFBBF24));
        addFeatureCard(grid, 5, "Save & Load",         "Save maze layouts and export solution reports",      new Color(0x22D3EE));

        return grid;
    }

    private void addFeatureCard(JPanel parent, int idx, String title, String desc, Color accent) {
        JPanel card = new JPanel() {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        // Animate scale up
                        if (cardTimers[idx] != null) cardTimers[idx].stop();
                        cardTimers[idx] = new Timer(12, ev -> {
                            cardScale[idx] = Math.min(1.025f, cardScale[idx] + 0.003f);
                            if (cardScale[idx] >= 1.025f) ((Timer)ev.getSource()).stop();
                            repaint();
                        });
                        cardTimers[idx].start();
                        repaint();
                    }
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        if (cardTimers[idx] != null) cardTimers[idx].stop();
                        cardTimers[idx] = new Timer(12, ev -> {
                            cardScale[idx] = Math.max(1.0f, cardScale[idx] - 0.003f);
                            if (cardScale[idx] <= 1.0f) ((Timer)ev.getSource()).stop();
                            repaint();
                        });
                        cardTimers[idx].start();
                        repaint();
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Scale transform for hover lift
                if (cardScale[idx] != 1.0f) {
                    float s = cardScale[idx];
                    float tx = getWidth()  * (1 - s) / 2;
                    float ty = getHeight() * (1 - s) / 2;
                    g2.translate(tx, ty);
                    g2.scale(s, s);
                }

                // Background
                Color bg = hovered ? ThemeManager.getBgCardHover() : ThemeManager.getBgCard();
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

                // Subtle top gradient tint on hover
                if (hovered) {
                    GradientPaint tint = new GradientPaint(0, 0,
                        ThemeManager.withAlpha(accent, 20), 0, getHeight(),
                        ThemeManager.withAlpha(accent, 0));
                    g2.setPaint(tint);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                }

                // Top accent bar
                int barAlpha = hovered ? 220 : 130;
                GradientPaint topBar = new GradientPaint(12, 0,
                    ThemeManager.withAlpha(accent, barAlpha),
                    getWidth() - 12, 0,
                    ThemeManager.withAlpha(accent, barAlpha / 3));
                g2.setPaint(topBar);
                g2.fillRoundRect(12, 0, getWidth() - 24, 3, 2, 2);

                // Border
                g2.setColor(hovered ? ThemeManager.withAlpha(accent, 70) : ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(hovered ? 1.2f : 0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(ThemeManager.getTextPrimary());
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("<html>" + desc + "</html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(ThemeManager.getTextSecondary());
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(descLbl);

        parent.add(card);
    }

    // ── Algorithm Info Box ────────────────────────────────
    private JPanel buildAlgoBox() {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle blue-tinted card
                g2.setColor(ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 10));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 45));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                // Left accent bar
                GradientPaint bar = new GradientPaint(0, 0, ThemeManager.getAccentBlue(),
                    0, getHeight(), ThemeManager.withAlpha(ThemeManager.getAccentCyan(), 180));
                g2.setPaint(bar);
                g2.fillRoundRect(0, 6, 3, getHeight() - 12, 3, 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));

        JLabel line1 = new JLabel(
            "Algorithm: Depth-First Search (DFS) + Backtracking  |  Finds all valid paths  |  Max solutions: 50");
        line1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        line1.setForeground(ThemeManager.getAccentBlue());
        line1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel line2 = new JLabel(
            "Time complexity: O(4^(R\u00d7C))   |   Space: O(R\u00d7C)   |   R = Rows, C = Columns");
        line2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        line2.setForeground(ThemeManager.getTextSecondary());
        line2.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(line1);
        box.add(Box.createVerticalStrut(6));
        box.add(line2);

        return box;
    }

    // ── CTA Start Button ──────────────────────────────────
    private JPanel buildStartButton() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setOpaque(false);

        JButton btn = new JButton("Open Maze Editor") {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hovered ? new Color(0x3D8EFF) : new Color(0x4D9EFF);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> { if (onStartEditor != null) onStartEditor.run(); });

        wrapper.add(btn);
        return wrapper;
    }

    // ── Background paint ──────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(ThemeManager.getBgPrimary());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    public void setOnStartEditor(Runnable cb) { this.onStartEditor = cb; }
    public void applyTheme() { repaint(); }
}
