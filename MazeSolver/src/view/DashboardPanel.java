package view;

import controller.AppController;
import model.SolveResult;
import view.ui.RoundedButton;
import view.ui.RoundedPanel;
import view.ui.Theme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DashboardPanel extends JPanel {

    public interface DashboardListener {
        void onOpenEditor();
        void onOpenVisualizer();
    }

    private final AppController controller;
    private DashboardListener dashboardListener;

    private JLabel statSolutions;
    private JLabel statCells;
    private JLabel statDepth;
    private JLabel statTime;

    private JPanel rootPanel;
    private JPanel cardGrid;
    private JPanel headerPanel;
    private JPanel southPanel;

    public DashboardPanel(AppController ctrl) {
        this.controller = ctrl;
        setLayout(new BorderLayout());
        setOpaque(false);

        headerPanel = buildHeader();
        cardGrid = buildStatCards();
        southPanel = buildActionButtons();

        add(headerPanel, BorderLayout.NORTH);
        add(cardGrid, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    public void setDashboardListener(DashboardListener listener) {
        this.dashboardListener = listener;
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 24, 40));

        JLabel title = new JLabel("Welcome to Maze Solver");
        title.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 28f));
        title.setForeground(Theme.getText());
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Visualize and solve mazes using recursive DFS backtracking.");
        sub.setFont(Theme.FONT_BODY.deriveFont(14f));
        sub.setForeground(Theme.getSecondaryText());
        sub.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(sub);
        return panel;
    }

    private JPanel buildStatCards() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 40, 24, 40));

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);

        statSolutions = new JLabel("0");
        statCells = new JLabel("0");
        statDepth = new JLabel("0");
        statTime = new JLabel("0 ms");

        grid.add(createStatCard("Solutions Found", statSolutions));
        grid.add(createStatCard("Cells Visited", statCells));
        grid.add(createStatCard("Max Depth", statDepth));
        grid.add(createStatCard("Execution Time", statTime));

        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        RoundedPanel card = new RoundedPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_BODY.deriveFont(Font.PLAIN, 12f));
        titleLabel.setForeground(Theme.getSecondaryText());
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        valueLabel.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 32f));
        valueLabel.setForeground(Theme.BLUE);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    private JPanel buildActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        RoundedButton editorBtn = new RoundedButton("Open Editor");
        editorBtn.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 14f));
        editorBtn.setBackground(Theme.BLUE);
        editorBtn.setForeground(Color.WHITE);
        editorBtn.setPreferredSize(new Dimension(160, 44));
        editorBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dashboardListener != null) dashboardListener.onOpenEditor();
            }
        });

        RoundedButton vizBtn = new RoundedButton("Open Visualizer");
        vizBtn.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 14f));
        vizBtn.setBackground(Theme.PURPLE);
        vizBtn.setForeground(Color.WHITE);
        vizBtn.setPreferredSize(new Dimension(160, 44));
        vizBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        vizBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dashboardListener != null) dashboardListener.onOpenVisualizer();
            }
        });

        panel.add(editorBtn);
        panel.add(vizBtn);
        return panel;
    }

    public void refreshStats() {
        SolveResult result = controller.getLastResult();
        if (result != null) {
            statSolutions.setText(String.valueOf(result.getSolutionCount()));
            statCells.setText(String.valueOf(result.getCellsVisited()));
            statDepth.setText(String.valueOf(result.getMaxDepth()));
            statTime.setText(result.getElapsedMs() + " ms");
        } else {
            statSolutions.setText("0");
            statCells.setText("0");
            statDepth.setText("0");
            statTime.setText("0 ms");
        }
        repaint();
    }

    public void refreshTheme() {
        repaint();
    }
}
