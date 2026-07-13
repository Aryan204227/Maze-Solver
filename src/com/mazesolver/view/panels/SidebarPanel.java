package com.mazesolver.view.panels;

import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * SidebarPanel.java — VERSION 4.0 PREMIUM
 * ─────────────────────────────────────────────────────────────────
 * Changes from V3.0:
 *   1. Multi-stop vertical gradient background (deep obsidian)
 *   2. Glass-style logo card at the top with animated border
 *   3. Active nav item gets a full-width glowing pill indicator
 *   4. Smooth hover animation (alpha-animated fill)
 *   5. Version badge at bottom with V4.0 branding
 *   6. Improved theme toggle button with icon swap animation
 */
public class SidebarPanel extends JPanel {

    public interface NavigationListener {
        void onNavigate(String page);
    }
    private NavigationListener navigationListener;
    private SidebarButton activeButton;

    private static final String[][] NAV_ITEMS = {
        { "\uD83C\uDFE0", "Dashboard",  "dashboard" },
        { "\uD83D\uDDFA", "Maze Editor","editor"    },
        { "\uD83C\uDF93", "Learning",   "learning"  },
        { "\u2699",       "Settings",   "settings"  },
        { "\u2753",       "Help",       "help"      },
        { "\u2139",       "About",      "about"     },
    };

    public SidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(Constants.SIDEBAR_WIDTH, 0));
        setOpaque(false);
        buildUI();
    }

    private void buildUI() {
        add(makeLogo());
        add(Box.createVerticalStrut(8));
        add(makeDivider());
        add(Box.createVerticalStrut(10));

        // Section label: NAVIGATION
        JLabel navLbl = new JLabel("  NAVIGATION");
        navLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        navLbl.setForeground(ThemeManager.getTextMuted());
        navLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        navLbl.setBorder(BorderFactory.createEmptyBorder(0, 18, 4, 0));
        navLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        add(navLbl);
        add(Box.createVerticalStrut(4));

        SidebarButton first = null;
        for (String[] item : NAV_ITEMS) {
            SidebarButton btn = new SidebarButton(item[0], item[1], item[2]);
            btn.addActionListener(e -> onNavClick(btn));
            add(btn);
            add(Box.createVerticalStrut(2));
            if (first == null) first = btn;
        }
        if (first != null) setActiveButton(first);

        add(Box.createVerticalGlue());
        add(makeDivider());
        add(Box.createVerticalStrut(10));
        add(makeThemeToggle());
        add(Box.createVerticalStrut(10));
        add(makeVersionBadge());
        add(Box.createVerticalStrut(14));
    }

    private JPanel makeLogo() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glass card
                g2.setColor(ThemeManager.getBgCard());
                g2.fillRoundRect(12, 8, getWidth() - 24, getHeight() - 14, 16, 16);
                // Top accent bar
                GradientPaint bar = new GradientPaint(12, 0,
                    ThemeManager.getAccentBlue(), getWidth() - 12, 0,
                    ThemeManager.getAccentPurple());
                g2.setPaint(bar);
                g2.fillRoundRect(12, 8, getWidth() - 24, 3, 3, 3);
                // Border
                g2.setColor(ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 40));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(12, 8, getWidth() - 25, getHeight() - 15, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(22, 0, 16, 0));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel icon = new JLabel("\uD83E\uDDE9", SwingConstants.CENTER); // 🧩
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("MAZE SOLVER", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(ThemeManager.getTextPrimary());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("DFS + Backtracking", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(ThemeManager.getTextMuted());
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(icon);
        p.add(Box.createVerticalStrut(4));
        p.add(title);
        p.add(Box.createVerticalStrut(2));
        p.add(sub);
        return p;
    }

    private JPanel makeDivider() {
        JPanel d = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                    16, 0, ThemeManager.withAlpha(ThemeManager.getBorderColor(), 0),
                    getWidth() / 2, 0, ThemeManager.getBorderColor());
                // Fade in from left, full at center, fade out to right
                g2.setPaint(gp);
                g2.drawLine(16, 0, getWidth() / 2, 0);
                GradientPaint gp2 = new GradientPaint(
                    getWidth() / 2, 0, ThemeManager.getBorderColor(),
                    getWidth() - 16, 0, ThemeManager.withAlpha(ThemeManager.getBorderColor(), 0));
                g2.setPaint(gp2);
                g2.drawLine(getWidth() / 2, 0, getWidth() - 16, 0);
                g2.dispose();
            }
        };
        d.setOpaque(false);
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setPreferredSize(new Dimension(0, 1));
        return d;
    }

    private JPanel makeThemeToggle() {
        JPanel w = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        w.setOpaque(false);

        JButton btn = new JButton() {
            private boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hov = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = hov ? ThemeManager.getSidebarHover() : ThemeManager.getBgTertiary();
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Accent border
                g2.setColor(hov
                    ? ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 80)
                    : ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                String txt = ThemeManager.isDarkMode() ? "\u2600  Light Mode" : "\uD83C\uDF19  Dark Mode";
                g2.setColor(hov ? ThemeManager.getAccentBlue() : ThemeManager.getTextSecondary());
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(txt,
                    (getWidth() - fm.stringWidth(txt)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(Constants.SIDEBAR_WIDTH - 28, 36));
        btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            if (navigationListener != null) navigationListener.onNavigate("theme-toggle");
            btn.repaint();
        });

        w.add(btn);
        return w;
    }

    private JPanel makeVersionBadge() {
        JPanel w = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        w.setOpaque(false);

        JLabel badge = new JLabel(" v" + Constants.APP_VERSION + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                    ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 40),
                    getWidth(), 0,
                    ThemeManager.withAlpha(ThemeManager.getAccentPurple(), 40));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 80));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(ThemeManager.getAccentBlue());
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        badge.setOpaque(false);

        w.add(badge);
        return w;
    }

    private void onNavClick(SidebarButton b) {
        setActiveButton(b);
        if (navigationListener != null) navigationListener.onNavigate(b.getPageName());
    }

    private void setActiveButton(SidebarButton b) {
        if (activeButton != null) activeButton.setActive(false);
        activeButton = b;
        b.setActive(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Multi-stop gradient sidebar
        int W = getWidth(), H = getHeight();
        if (ThemeManager.isDarkMode()) {
            GradientPaint gp = new GradientPaint(0, 0,
                new Color(0x0C111A),
                0, H,
                new Color(0x080C14));
            g2.setPaint(gp);
        } else {
            GradientPaint gp = new GradientPaint(0, 0,
                new Color(0xF0F4FA),
                0, H,
                new Color(0xE8EDF8));
            g2.setPaint(gp);
        }
        g2.fillRect(0, 0, W, H);

        // Right border with gradient fade
        GradientPaint border = new GradientPaint(0, 0,
            ThemeManager.withAlpha(ThemeManager.getBorderColor(), 0),
            0, H / 2,
            ThemeManager.getBorderColor());
        g2.setPaint(border);
        g2.drawLine(W - 1, 0, W - 1, H / 2);

        GradientPaint border2 = new GradientPaint(0, H / 2,
            ThemeManager.getBorderColor(),
            0, H,
            ThemeManager.withAlpha(ThemeManager.getBorderColor(), 60));
        g2.setPaint(border2);
        g2.drawLine(W - 1, H / 2, W - 1, H);

        g2.dispose();
    }

    public void setNavigationListener(NavigationListener l) { this.navigationListener = l; }
    public void applyTheme() { repaint(); }

    // ── Inner: SidebarButton ──────────────────────────────
    private class SidebarButton extends JButton {
        private final String pageName;
        private boolean active  = false;
        private boolean hovered = false;
        private float hoverAlpha = 0f;
        private final Timer hoverTimer;

        SidebarButton(String icon, String label, String pageName) {
            super(icon + "  " + label);
            this.pageName = pageName;

            hoverTimer = new Timer(14, e -> {
                hoverAlpha += hovered ? 0.12f : -0.12f;
                hoverAlpha = Math.max(0f, Math.min(1f, hoverAlpha));
                if ((!hovered && hoverAlpha <= 0f) || (hovered && hoverAlpha >= 1f)) {
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });

            setBorderPainted(false); setFocusPainted(false); setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setPreferredSize(new Dimension(Constants.SIDEBAR_WIDTH, 42));
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  hoverTimer.start(); }
                public void mouseExited (MouseEvent e) { hovered = false; hoverTimer.start(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int W = getWidth(), H = getHeight();

            if (active) {
                // Active item: glowing glass pill
                GradientPaint activeBg = new GradientPaint(6, 0,
                    ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 35),
                    W - 12, 0,
                    ThemeManager.withAlpha(ThemeManager.getAccentPurple(), 20));
                g2.setPaint(activeBg);
                g2.fillRoundRect(6, 3, W - 12, H - 6, 10, 10);

                // Left active pill with gradient
                GradientPaint pill = new GradientPaint(0, 6,
                    ThemeManager.getAccentBlue(),
                    0, H - 6,
                    ThemeManager.getAccentPurple());
                g2.setPaint(pill);
                g2.fillRoundRect(0, 8, 4, H - 16, 4, 4);

                // Subtle border
                g2.setColor(ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 50));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(6, 3, W - 13, H - 7, 10, 10);

            } else if (hoverAlpha > 0f) {
                g2.setColor(ThemeManager.withAlpha(ThemeManager.getSidebarHover(), (int)(hoverAlpha * 200)));
                g2.fillRoundRect(6, 3, W - 12, H - 6, 10, 10);
            }

            // Text
            Color textColor = active
                ? ThemeManager.getAccentBlue()
                : ThemeManager.withAlpha(ThemeManager.getTextSecondary(), (int)(170 + 85 * hoverAlpha));
            g2.setColor(textColor);
            g2.setFont(active
                ? new Font("Segoe UI", Font.BOLD, 13)
                : new Font("Segoe UI", Font.PLAIN, 13));

            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(), 20, (H + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }

        void setActive(boolean a)  { this.active = a; repaint(); }
        String getPageName()       { return pageName; }
    }
}
