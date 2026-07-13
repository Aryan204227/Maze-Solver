package com.mazesolver.view;

import com.mazesolver.controller.AppController;
import com.mazesolver.model.Maze;
import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;
import com.mazesolver.view.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MainWindow.java — VERSION 4.0 PREMIUM
 * Features:
 *   1. Custom exit dialog intercepting close events
 *   2. Global Keyboard Shortcuts via JComponent KeyBindings
 *   3. Layout component rendering propagation
 *   4. V4.0: Deeper obsidian background
 */
public class MainWindow extends JFrame {

    private SidebarPanel   sidebarPanel;
    private DashboardPanel dashboardPanel;
    private MazePanel      mazePanel;
    private ControlPanel   controlPanel;
    private StatsPanel     statsPanel;
    private LearningPanel  learningPanel;
    private StatusBar      statusBar;

    private JPanel         contentWrapper;
    private CardLayout     cardLayout;

    private JPanel         editorWorkspacePanel;
    private JPanel         learningWorkspacePanel;

    private Runnable       exitCallback;

    public void setExitCallback(Runnable callback) {
        this.exitCallback = callback;
    }

    public MainWindow() {
        super(Constants.APP_TITLE);
        setupFrame();
        buildUI();
    }

    private void setupFrame() {
        // Intercept close events to prompt custom dialog
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showExitConfirmation();
            }
        });
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(1050, 720));
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Initialize shared model and components
        Maze initialMaze = new Maze(Constants.DEFAULT_ROWS, Constants.DEFAULT_COLS);

        sidebarPanel   = new SidebarPanel();
        dashboardPanel = new DashboardPanel();
        mazePanel      = new MazePanel(initialMaze);
        controlPanel   = new ControlPanel();
        statsPanel     = new StatsPanel();
        learningPanel  = new LearningPanel();
        statusBar      = new StatusBar();

        // ── Left: Sidebar ─────────────────────────────────
        add(sidebarPanel, BorderLayout.WEST);

        // ── Center/Right: Main panel CardLayout routing ───
        cardLayout     = new CardLayout();
        contentWrapper = new JPanel(cardLayout);
        contentWrapper.setOpaque(false);

        // Card 1: Home Dashboard Screen
        contentWrapper.add(dashboardPanel, "dashboard");

        // Card 2: Workspace Editor Screen Layout
        editorWorkspacePanel = new JPanel(new BorderLayout());
        editorWorkspacePanel.setOpaque(false);

        // Center maze grid drawing panel
        editorWorkspacePanel.add(mazePanel, BorderLayout.CENTER);

        // Right hand solver statistics details
        editorWorkspacePanel.add(statsPanel, BorderLayout.EAST);

        // Bottom control toolbar interface
        editorWorkspacePanel.add(controlPanel, BorderLayout.SOUTH);

        contentWrapper.add(editorWorkspacePanel, "editor");

        // Card 3: Learning Workspace Screen Layout
        learningWorkspacePanel = new JPanel(new BorderLayout());
        learningWorkspacePanel.setOpaque(false);
        // Note: mazePanel and controlPanel will be dynamically added here by AppController when card is active.
        learningWorkspacePanel.add(learningPanel, BorderLayout.EAST);

        contentWrapper.add(learningWorkspacePanel, "learning");

        add(contentWrapper, BorderLayout.CENTER);

        // ── Bottom: Status Bar ────────────────────────────
        add(statusBar, BorderLayout.SOUTH);

        // Instantiate and hook routing logic via controller
        AppController controller = new AppController(this, sidebarPanel, dashboardPanel, 
                mazePanel, controlPanel, statsPanel, learningPanel, statusBar);
        
        controller.setViewRouter(cardLayout, contentWrapper);

        // Version 2.0: Bind global shortcuts to the root pane
        registerShortcuts(controller);

        // Initial view
        cardLayout.show(contentWrapper, "dashboard");

        applyTheme();
    }

    public LearningPanel getLearningPanel() { return learningPanel; }
    public JPanel getEditorWorkspacePanel() { return editorWorkspacePanel; }
    public JPanel getLearningWorkspacePanel() { return learningWorkspacePanel; }
    public MazePanel getMazePanel() { return mazePanel; }
    public ControlPanel getControlPanel() { return controlPanel; }


    /**
     * Binds Ctrl-key shortcuts to content action loops.
     */
    private void registerShortcuts(AppController controller) {
        JComponent root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        // Ctrl+G: Generate
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), "generate");
        am.put("generate", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onGenerate(); }
        });

        // Ctrl+S: Solve
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "solve");
        am.put("solve", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onSolve(); }
        });

        // Ctrl+P: Pause/Resume
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "pause_resume");
        am.put("pause_resume", new AbstractAction() {
            private boolean paused = false;
            @Override public void actionPerformed(ActionEvent e) {
                if (paused) {
                    controller.onResume();
                    paused = false;
                } else {
                    controller.onPause();
                    paused = true;
                }
            }
        });

        // Ctrl+R: Reset
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "reset");
        am.put("reset", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onReset(); }
        });

        // Ctrl+E: Toggle Edit Mode
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "edit_mode");
        am.put("edit_mode", new AbstractAction() {
            private boolean edit = false;
            @Override public void actionPerformed(ActionEvent e) {
                edit = !edit;
                controller.onEditToggle(edit);
            }
        });

        // Ctrl+O: Load
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "load");
        am.put("load", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onLoadFile(); }
        });

        // Ctrl+Shift+S: Save
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "save");
        am.put("save", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onSaveFile(); }
        });

        // Ctrl+Z: Undo Wall Edit
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        am.put("undo", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onUndo(); }
        });

        // Ctrl+Y: Redo Wall Edit
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        am.put("redo", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onRedo(); }
        });

        // Ctrl+H: AI Hint
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK), "hint");
        am.put("hint", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onHint(); }
        });

        // Player Arrow Keys
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "play_up");
        am.put("play_up", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onPlayerMove("UP"); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "play_down");
        am.put("play_down", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onPlayerMove("DOWN"); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "play_left");
        am.put("play_left", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onPlayerMove("LEFT"); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "play_right");
        am.put("play_right", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onPlayerMove("RIGHT"); }
        });

        // Player WASD
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "play_w");
        am.put("play_w", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onPlayerMove("UP"); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "play_s");
        am.put("play_s", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onPlayerMove("DOWN"); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "play_a");
        am.put("play_a", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onPlayerMove("LEFT"); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "play_d");
        am.put("play_d", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { controller.onPlayerMove("RIGHT"); }
        });
    }

    /**
     * Spawns a custom exit dialog with yes/no options.
     */
    private void showExitConfirmation() {
        JDialog dialog = new JDialog(this, "Exit", true);
        dialog.setUndecorated(true);
        dialog.setSize(330, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 330, 150, 16, 16));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getBgSecondary());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("🚪  Exit Application");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(ThemeManager.getTextPrimary());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Are you sure you want to exit the visualizer?");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(ThemeManager.getTextSecondary());
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnExit = new JButton("Exit") {
            private boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov = true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hov = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? new Color(0xCF222E) : new Color(0xF85149));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btnExit.setPreferredSize(new Dimension(100, 32));
        btnExit.setBorderPainted(false); btnExit.setFocusPainted(false); btnExit.setContentAreaFilled(false);
        btnExit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExit.addActionListener(e -> {
            if (exitCallback != null) {
                exitCallback.run();
            } else {
                System.exit(0);
            }
        });

        JButton btnCancel = new JButton("Cancel") {
            private boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov = true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hov = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? ThemeManager.getSidebarHover() : ThemeManager.getBgTertiary());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(ThemeManager.getTextSecondary());
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btnCancel.setPreferredSize(new Dimension(100, 32));
        btnCancel.setBorderPainted(false); btnCancel.setFocusPainted(false); btnCancel.setContentAreaFilled(false);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnExit);
        btnPanel.add(btnCancel);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sub);
        panel.add(Box.createVerticalStrut(18));
        panel.add(btnPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /** Applies active dark/light palette theme settings to the JFrame */
    public void applyTheme() {
        getContentPane().setBackground(ThemeManager.getBgPrimary());
        contentWrapper.setBackground(ThemeManager.getBgPrimary());
        editorWorkspacePanel.setBackground(ThemeManager.getBgPrimary());
        learningWorkspacePanel.setBackground(ThemeManager.getBgPrimary());
        repaint();
    }
}
