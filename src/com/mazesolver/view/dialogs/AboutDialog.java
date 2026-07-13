package com.mazesolver.view.dialogs;

import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * AboutDialog.java — UPGRADED premium about screen
 */
public class AboutDialog extends JDialog {

    public AboutDialog(Frame parent) {
        super(parent, "About Maze Solver", true);
        setSize(440, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 440, 480, 20, 20));
        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ThemeManager.getBgSecondary(),
                        0, getHeight(), ThemeManager.getBgPrimary());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Top accent gradient bar
                GradientPaint accent = new GradientPaint(0, 0, ThemeManager.getAccentBlue(),
                        getWidth(), 0, ThemeManager.getAccentPurple());
                g2.setPaint(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                // Border
                g2.setColor(ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(20, 28, 24, 28));

        // Close button
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        header.add(makeCloseBtn(), BorderLayout.EAST);
        main.add(header);
        main.add(Box.createVerticalStrut(12));

        // Logo
        JLabel icon = new JLabel("🧩", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(icon);
        main.add(Box.createVerticalStrut(10));

        // Title
        JLabel title = new JLabel(Constants.APP_TITLE, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ThemeManager.getTextPrimary());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(title);
        main.add(Box.createVerticalStrut(4));

        JLabel ver = new JLabel("Version " + Constants.APP_VERSION, SwingConstants.CENTER);
        ver.setFont(Constants.FONT_SMALL);
        ver.setForeground(ThemeManager.getTextMuted());
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(ver);
        main.add(Box.createVerticalStrut(18));

        // Description
        JLabel desc = new JLabel("<html><center>A premium university project visualizing recursive<br>DFS pathfinding and backtracking algorithms<br>with a custom-designed Swing UI.</center></html>");
        desc.setFont(Constants.FONT_BODY);
        desc.setForeground(ThemeManager.getTextSecondary());
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(desc);
        main.add(Box.createVerticalStrut(20));

        // Info Cards
        addInfoRow(main, "🏛", "Architecture",  "Model-View-Controller (MVC)");
        addInfoRow(main, "⚙", "Algorithm",      "DFS + Recursive Backtracking");
        addInfoRow(main, "🧱", "Technologies",  "Java Swing, AWT, Collections");
        addInfoRow(main, "🔒", "Threading",     "EDT + Background Worker Thread");
        addInfoRow(main, "💾", "File Format",   ".maze (Plain Text Config)");

        add(main);
    }

    private void addInfoRow(JPanel parent, String icon, String label, String value) {
        JPanel row = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getBgCard());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        row.setLayout(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel leftLbl = new JLabel(icon + "  " + label);
        leftLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftLbl.setForeground(ThemeManager.getTextSecondary());

        JLabel rightLbl = new JLabel(value);
        rightLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rightLbl.setForeground(ThemeManager.getTextPrimary());
        rightLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(leftLbl, BorderLayout.WEST);
        row.add(rightLbl, BorderLayout.EAST);

        parent.add(row);
        parent.add(Box.createVerticalStrut(6));
    }

    private JButton makeCloseBtn() {
        JButton btn = new JButton("✕");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(ThemeManager.getTextSecondary());
        btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(ThemeManager.getAccentOrange()); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(ThemeManager.getTextSecondary()); }
        });
        btn.addActionListener(e -> dispose());
        return btn;
    }
}
