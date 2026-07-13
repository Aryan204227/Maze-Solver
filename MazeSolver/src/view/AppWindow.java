package view;

import controller.AppController;
import view.ui.Theme;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * AppWindow - The main application JFrame.
 * Holds a sidebar on the left and a CardLayout content area on the right.
 */
public class AppWindow extends JFrame {

    private final AppController controller;
    private final CardLayout cardLayout;
    private final JPanel contentArea;

    private final Sidebar sidebar;
    private final DashboardPanel dashboardPanel;
    private final EditorPanel editorPanel;
    private final VisualizerPanel visualizerPanel;
    private final SettingsPanel settingsPanel;
    private final HelpPanel helpPanel;

    public AppWindow(final AppController ctrl) {
        this.controller = ctrl;

        setTitle("Maze Solver - DFS & Backtracking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Root panel with themed background
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.getBg());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        root.setOpaque(false);
        setContentPane(root);

        // Create all panels
        dashboardPanel  = new DashboardPanel(controller);
        editorPanel     = new EditorPanel(controller);
        visualizerPanel = new VisualizerPanel(controller);

        settingsPanel = new SettingsPanel(controller, new SettingsPanel.SettingsListener() {
            @Override
            public void onThemeChanged() {
                refreshAllThemes();
            }
        });

        helpPanel = new HelpPanel();

        // Sidebar with navigation
        sidebar = new Sidebar(new Sidebar.NavListener() {
            @Override
            public void onNavigate(String panel) {
                showPanel(panel);
            }
        });

        // CardLayout content area
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setOpaque(false);
        contentArea.add(dashboardPanel,  "Dashboard");
        contentArea.add(editorPanel,     "Editor");
        contentArea.add(visualizerPanel, "Visualizer");
        contentArea.add(settingsPanel,   "Settings");
        contentArea.add(helpPanel,       "Help");

        root.add(sidebar,     BorderLayout.WEST);
        root.add(contentArea, BorderLayout.CENTER);

        // Dashboard quick-action buttons
        dashboardPanel.setDashboardListener(new DashboardPanel.DashboardListener() {
            @Override
            public void onOpenEditor() {
                showPanel("Editor");
            }
            @Override
            public void onOpenVisualizer() {
                showPanel("Visualizer");
            }
        });

        showPanel("Dashboard");
    }

    /** Switches the visible panel and updates the sidebar highlight. */
    public void showPanel(String name) {
        cardLayout.show(contentArea, name);
        sidebar.setActiveButton(name);
        if ("Dashboard".equals(name)) {
            dashboardPanel.refreshStats();
        }
    }

    /** Re-applies the current theme to every panel. */
    public void refreshAllThemes() {
        sidebar.refreshTheme();
        dashboardPanel.refreshTheme();
        editorPanel.refreshTheme();
        visualizerPanel.refreshTheme();
        settingsPanel.refreshTheme();
        helpPanel.refreshTheme();
        repaint();
    }

    public VisualizerPanel getVisualizerPanel() { return visualizerPanel; }
    public EditorPanel     getEditorPanel()     { return editorPanel; }
    public DashboardPanel  getDashboardPanel()  { return dashboardPanel; }
}
