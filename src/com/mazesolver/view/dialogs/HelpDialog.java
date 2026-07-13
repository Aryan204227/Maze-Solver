package com.mazesolver.view.dialogs;

import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * HelpDialog.java — UPGRADED with icon cards instead of HTML walls
 */
public class HelpDialog extends JDialog {

    public HelpDialog(Frame parent) {
        super(parent, "Help Guide", true);
        setSize(520, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 520, 500, 20, 20));
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
                GradientPaint accent = new GradientPaint(0, 0, ThemeManager.getAccentPurple(),
                        getWidth(), 0, ThemeManager.getAccentBlue());
                g2.setPaint(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.setColor(ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        main.setLayout(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(18, 24, 20, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = new JLabel("❓  Help Guide");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setForeground(ThemeManager.getTextPrimary());
        header.add(titleLbl, BorderLayout.WEST);
        header.add(makeCloseBtn(), BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // Scrollable cards content
        JPanel cards = new JPanel();
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        addHelpCard(cards, "🗺", "Navigation", "Use the left sidebar to switch between Dashboard and Maze Editor views.", new Color(0x58A6FF));
        addHelpCard(cards, "🔀", "Generate Maze", "Click Generate to create a randomized maze using the DFS Recursive Backtracker algorithm. Each time produces a unique layout.", new Color(0x8250DF));
        addHelpCard(cards, "✏", "Edit Mode", "Toggle the Edit button, then click or drag your mouse across cells to paint walls. Click an existing wall to remove it. Start and End corners are protected.", new Color(0xF0A500));
        addHelpCard(cards, "▶", "Solve & Visualize", "Click Solve to watch DFS + Backtracking trace paths step-by-step. Blue = exploring, Purple = visited, Orange = backtrack, Green = solution.", new Color(0x3FB950));
        addHelpCard(cards, "⏸", "Pause & Resume", "Use the Pause and Resume buttons to control the animation. Adjust the Speed slider to change the delay between each step.", new Color(0x388BFD));
        addHelpCard(cards, "💾", "Save & Load Files", "Export your custom maze layout as a .maze text file. Re-import saved configurations using the Load button.", new Color(0xF78166));
        addHelpCard(cards, "🌙", "Theme Toggle", "Switch between Dark Mode and Light Mode using the toggle at the bottom of the sidebar.", new Color(0xBC8CFF));

        JScrollPane scroll = new JScrollPane(cards);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        main.add(scroll, BorderLayout.CENTER);

        add(main);
    }

    private void addHelpCard(JPanel parent, String icon, String title, String desc, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getBgCard());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                // Left accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 6, 4, getHeight()-12, 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel headerLbl = new JLabel(icon + "  " + title);
        headerLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerLbl.setForeground(accent);
        headerLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("<html>" + desc + "</html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(ThemeManager.getTextSecondary());
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(descLbl);

        parent.add(card);
        parent.add(Box.createVerticalStrut(8));
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
