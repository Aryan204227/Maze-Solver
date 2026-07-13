package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import controller.MazeController;
import view.components.RoundedButton;
import view.components.RoundedPanel;

/**
 * The main application window for the Maze Solver desktop application.
 * Manages the left navigation sidebar, cards switching, and light/dark theme switches.
 */
public class MainFrame extends JFrame {
    private final MazeController controller;
    private final CardLayout cardLayout;
    private final JPanel contentCards;
    
    // Sub Panels
    private final DashboardPanel dashboardPanel;
    private final MazeEditorPanel editorPanel;
    private final MazeVisualizerPanel visualizerPanel;
    private final StatsPanel statsPanel;

    // Header label references
    private JLabel lblPageTitle;
    private RoundedButton btnThemeToggle;
    private JPanel sidebarPanel;
    private JPanel headerPanel;

    // Navigation buttons
    private RoundedButton btnNavDash;
    private RoundedButton btnNavVisualizer;
    private RoundedButton btnNavEditor;
    private RoundedButton btnNavStats;

    /**
     * Constructs the main application frame.
     */
    public MainFrame() {
        super("Maze Solver - Backtracking & DFS Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setLocationRelativeTo(null); // Center on screen

        this.controller = new MazeController();
        this.cardLayout = new CardLayout();
        this.contentCards = new JPanel(cardLayout);
        this.contentCards.setOpaque(false);

        // Initialize panels
        this.dashboardPanel = new DashboardPanel(controller, this);
        this.editorPanel = new MazeEditorPanel(controller, this);
        this.visualizerPanel = new MazeVisualizerPanel(controller, this);
        this.statsPanel = new StatsPanel(controller);

        // Register views to controller for automatic repaint cycles
        controller.registerView(dashboardPanel);
        controller.registerView(editorPanel);
        controller.registerView(visualizerPanel);
        controller.registerView(statsPanel);

        // Assemble cards
        contentCards.add(dashboardPanel, "Dashboard");
        contentCards.add(visualizerPanel, "Visualizer");
        contentCards.add(editorPanel, "Editor");
        contentCards.add(statsPanel, "Stats");

        setupWindowLayout();
        applyThemeStyles();
    }

    private void setupWindowLayout() {
        JPanel rootPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Paint base background color
                g.setColor(Theme.getBgColor());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        rootPanel.setOpaque(false);
        setContentPane(rootPanel);

        // 1. Sidebar (West)
        rootPanel.add(createSidebarPanel(), BorderLayout.WEST);

        // 2. Center Panel (Header + Card Content)
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(createHeaderPanel(), BorderLayout.NORTH);
        centerContainer.add(contentCards, BorderLayout.CENTER);

        rootPanel.add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createSidebarPanel() {
        sidebarPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Painted background matching theme sidebar settings
                g.setColor(Theme.getSidebarColor());
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Fine separator line
                g.setColor(Theme.getCardBorderColor());
                g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            }
        };
        sidebarPanel.setOpaque(false);
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(new EmptyBorder(30, 20, 30, 20));

        // Brand Title
        JPanel brandPanel = new JPanel(new GridLayout(2, 1));
        brandPanel.setOpaque(false);
        
        JLabel brandName = new JLabel("Maze Solver");
        brandName.setFont(Theme.getTitleFont(20));
        brandName.setForeground(Theme.getSecondaryAccentColor());
        
        JLabel brandSub = new JLabel("Recursion Engine");
        brandSub.setFont(Theme.getBodyFont(11));
        brandSub.setForeground(Theme.getSecondaryTextColor());
        
        brandPanel.add(brandName);
        brandPanel.add(brandSub);
        sidebarPanel.add(brandPanel, BorderLayout.NORTH);

        // Navigation links
        JPanel navPanel = new JPanel(new GridLayout(6, 1, 0, 12));
        navPanel.setOpaque(false);
        navPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        btnNavDash = new RoundedButton("⛃ Dashboard");
        btnNavDash.addActionListener(e -> switchTab("Dashboard"));

        btnNavVisualizer = new RoundedButton("▶ Solver Visualizer");
        btnNavVisualizer.addActionListener(e -> switchTab("Visualizer"));

        btnNavEditor = new RoundedButton("✎ Maze Editor");
        btnNavEditor.addActionListener(e -> switchTab("Editor"));

        btnNavStats = new RoundedButton("📊 Analytics Stats");
        btnNavStats.addActionListener(e -> switchTab("Stats"));

        navPanel.add(btnNavDash);
        navPanel.add(btnNavVisualizer);
        navPanel.add(btnNavEditor);
        navPanel.add(btnNavStats);

        sidebarPanel.add(navPanel, BorderLayout.CENTER);

        // Small university footer
        JLabel footerLabel = new JLabel("<html><div style='text-align: center;'>University Project<br>Core Java Swing</div></html>", JLabel.CENTER);
        footerLabel.setFont(Theme.getBodyFont(10));
        footerLabel.setForeground(Theme.getSecondaryTextColor());
        sidebarPanel.add(footerLabel, BorderLayout.SOUTH);

        return sidebarPanel;
    }

    private JPanel createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Fine separator line under header
                g.setColor(Theme.getCardBorderColor());
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(16, 30, 16, 30));
        headerPanel.setPreferredSize(new Dimension(0, 70));

        lblPageTitle = new JLabel("Home Dashboard");
        lblPageTitle.setFont(Theme.getTitleFont(20));
        lblPageTitle.setForeground(Theme.getPrimaryTextColor());
        headerPanel.add(lblPageTitle, BorderLayout.WEST);

        // Action controls (Theme switch)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        btnThemeToggle = new RoundedButton("🌙 Dark Mode");
        btnThemeToggle.addActionListener(e -> toggleTheme());
        actionPanel.add(btnThemeToggle);

        headerPanel.add(actionPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Toggles between dark mode and light mode, repainting all items.
     */
    private void toggleTheme() {
        Theme.toggleMode();
        applyThemeStyles();
        
        // Notify views to adapt their graphic displays
        controller.notifyViews();
        repaint();
    }

    private void applyThemeStyles() {
        boolean isDark = Theme.getMode() == Theme.Mode.DARK;
        btnThemeToggle.setText(isDark ? "🌙 Dark Mode" : "☀ Light Mode");

        // Adapt button default color highlights based on theme mode
        Color navBg = Theme.getSidebarColor();
        Color hoverBg = Theme.getBgColor();
        Color text = Theme.getPrimaryTextColor();
        Color activeText = Theme.getAccentColor();

        // Standard styling for navigation buttons
        applyNavBtnStyle(btnNavDash, "Dashboard");
        applyNavBtnStyle(btnNavVisualizer, "Visualizer");
        applyNavBtnStyle(btnNavEditor, "Editor");
        applyNavBtnStyle(btnNavStats, "Stats");

        // Set theme button colors
        if (isDark) {
            btnThemeToggle.setColors(Theme.getCardBgColor(), Theme.getBgColor(), Theme.getBgColor().darker(), text, Color.WHITE);
        } else {
            btnThemeToggle.setColors(Theme.getCardBgColor(), Theme.getBgColor(), Theme.getBgColor().darker(), text, Theme.getAccentColor());
        }
    }

    private void applyNavBtnStyle(RoundedButton btn, String targetName) {
        Color cardBg = Theme.getCardBgColor();
        Color hoverBg = Theme.getBgColor();
        Color text = Theme.getPrimaryTextColor();
        
        btn.setColors(cardBg, hoverBg, hoverBg.darker(), text, Theme.getAccentColor());
    }

    /**
     * Swaps the visible dashboard card.
     */
    public void switchTab(String tabName) {
        cardLayout.show(contentCards, tabName);
        lblPageTitle.setText(tabName.equals("Stats") ? "Performance & Analytics" : tabName + " Workspace");
        
        // Refresh specific view metrics upon switching tabs
        if (tabName.equals("Dashboard")) {
            dashboardPanel.updateDashboardData();
        } else if (tabName.equals("Stats")) {
            statsPanel.updateStatsData();
        }
    }

    /**
     * Triggers data updates on subpanels after solver runs.
     */
    public void updateDashboardStats() {
        dashboardPanel.updateDashboardData();
        statsPanel.updateStatsData();
    }
}
