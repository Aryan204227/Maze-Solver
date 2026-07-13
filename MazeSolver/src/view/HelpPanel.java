package view;

import view.ui.Theme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class HelpPanel extends JPanel {

    private JPanel contentPanel;

    public HelpPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        contentPanel = buildContent();
        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Title
        JLabel title = new JLabel("Help & Instructions");
        title.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 26f));
        title.setForeground(Theme.getText());
        title.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(28));

        // How to Use
        panel.add(buildSectionTitle("How to Use"));
        panel.add(Box.createVerticalStrut(10));
        String[] steps = {
            "1. Go to the Editor tab to create or modify your maze.",
            "2. Select a brush (Wall, Path, Start, End) from the toolbar.",
            "3. Click or drag on the grid to paint cells.",
            "4. Use 'Generate Maze' to auto-create a maze.",
            "5. Switch to the Visualizer tab.",
            "6. Click 'Run Solver' to prepare the solving steps.",
            "7. Use Play, Step Forward, or Instant Solve to see the solution.",
            "8. Stats are updated in real time on the right panel."
        };
        for (String step : steps) {
            panel.add(buildBodyLabel(step));
            panel.add(Box.createVerticalStrut(4));
        }
        panel.add(Box.createVerticalStrut(24));

        // Keyboard Shortcuts
        panel.add(buildSectionTitle("Keyboard Shortcuts"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(buildBodyLabel("Use buttons in the UI — no keyboard shortcuts are configured."));
        panel.add(Box.createVerticalStrut(24));

        // Algorithm Explanation
        panel.add(buildSectionTitle("Algorithm Explanation"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(buildBodyLabel("DFS explores every possible path recursively from the Start cell."));
        panel.add(Box.createVerticalStrut(4));
        panel.add(buildBodyLabel("When stuck (no unvisited neighbors), it backtracks to try a new direction."));
        panel.add(Box.createVerticalStrut(4));
        panel.add(buildBodyLabel("This finds ALL solutions and reports the shortest one."));
        panel.add(Box.createVerticalStrut(4));
        panel.add(buildBodyLabel("Complexity: O(4^(rows*cols)) worst case — exponential but pruned by walls."));
        panel.add(Box.createVerticalStrut(24));

        // Color Guide
        panel.add(buildSectionTitle("Color Guide"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(buildColorRow(Theme.CELL_WALL,    "Wall - blocked cell"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildColorRow(Theme.getCellPath(), "Path - open walkable cell"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildColorRow(Theme.CELL_START,   "Start - starting position"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildColorRow(Theme.CELL_END,     "End - target/goal cell"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildColorRow(Theme.CELL_VISITED, "Visited - explored during search"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildColorRow(Theme.CELL_FOUND,   "Found - part of a solution path"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildColorRow(Theme.CELL_DEAD,    "Dead End - backtracked position"));

        return panel;
    }

    private JLabel buildSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 17f));
        label.setForeground(Theme.BLUE);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JLabel buildBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BODY.deriveFont(Font.PLAIN, 13f));
        label.setForeground(Theme.getText());
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JPanel buildColorRow(final Color color, String description) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JPanel swatch = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
        swatch.setOpaque(false);
        swatch.setPreferredSize(new Dimension(16, 16));
        swatch.setMinimumSize(new Dimension(16, 16));
        swatch.setMaximumSize(new Dimension(16, 16));

        JLabel desc = new JLabel("  " + description);
        desc.setFont(Theme.FONT_BODY.deriveFont(13f));
        desc.setForeground(Theme.getText());

        row.add(swatch);
        row.add(desc);
        return row;
    }

    public void refreshTheme() {
        // Rebuild the content to pick up new theme colors
        removeAll();
        contentPanel = buildContent();
        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
