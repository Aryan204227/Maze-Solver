package view;

import view.ui.Theme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Sidebar extends JPanel {

    public interface NavListener {
        void onNavigate(String panel);
    }

    private static final int WIDTH = 220;
    private static final String[] NAV_ITEMS = {"Dashboard", "Editor", "Visualizer", "Settings", "Help"};

    private final NavListener navListener;
    private final Map<String, JButton> navButtons = new LinkedHashMap<String, JButton>();
    private String activePanel = "Dashboard";

    private JLabel brandLabel;
    private JLabel subtitleLabel;
    private JLabel footerLabel;

    public Sidebar(NavListener listener) {
        this.navListener = listener;
        setPreferredSize(new Dimension(WIDTH, 0));
        setLayout(new BorderLayout());
        setOpaque(false);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildNavArea(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(32, 20, 24, 20));

        brandLabel = new JLabel("Maze Solver");
        brandLabel.setFont(Theme.titleFont().deriveFont(Font.BOLD, 20f));
        brandLabel.setForeground(Theme.BLUE);
        brandLabel.setAlignmentX(LEFT_ALIGNMENT);

        subtitleLabel = new JLabel("Recursion & DFS");
        subtitleLabel.setFont(Theme.bodyFont().deriveFont(12f));
        subtitleLabel.setForeground(Theme.getText2());
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        header.add(brandLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);

        return header;
    }

    private JPanel buildNavArea() {
        JPanel navArea = new JPanel();
        navArea.setOpaque(false);
        navArea.setLayout(new BoxLayout(navArea, BoxLayout.Y_AXIS));
        navArea.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        for (String name : NAV_ITEMS) {
            JButton btn = createNavButton(name);
            navButtons.put(name, btn);
            navArea.add(btn);
            navArea.add(Box.createVerticalStrut(4));
        }

        return navArea;
    }

    private JButton createNavButton(final String name) {
        final JButton btn = new JButton(name);
        btn.setFont(Theme.bodyFont().deriveFont(Font.PLAIN, 14f));
        btn.setForeground(Theme.getText());
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setPreferredSize(new Dimension(WIDTH - 24, 48));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (navListener != null) {
                    navListener.onNavigate(name);
                }
            }
        });

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!name.equals(activePanel)) {
                    btn.setOpaque(true);
                    btn.setContentAreaFilled(true);
                    btn.setBackground(Theme.getCard());
                    btn.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!name.equals(activePanel)) {
                    btn.setOpaque(false);
                    btn.setContentAreaFilled(false);
                    btn.repaint();
                }
            }
        });

        return btn;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(BorderFactory.createEmptyBorder(16, 20, 24, 20));

        footerLabel = new JLabel("University Project");
        footerLabel.setFont(Theme.bodyFont().deriveFont(11f));
        footerLabel.setForeground(Theme.getText2());
        footerLabel.setAlignmentX(LEFT_ALIGNMENT);

        footer.add(footerLabel);
        return footer;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.getSidebar());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    public void setActiveButton(String panelName) {
        activePanel = panelName;
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(panelName)) {
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                btn.setBackground(Theme.BLUE);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setForeground(Theme.getText());
            }
            btn.repaint();
        }
    }

    public void refreshTheme() {
        brandLabel.setForeground(Theme.BLUE);
        subtitleLabel.setForeground(Theme.getText2());
        footerLabel.setForeground(Theme.getText2());
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (!entry.getKey().equals(activePanel)) {
                btn.setForeground(Theme.getText());
            }
        }
        repaint();
    }
}
