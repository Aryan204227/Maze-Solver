package com.mazesolver.view.panels;

import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import com.mazesolver.model.SolverStats;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MazePanel.java — VERSION 4.0 PREMIUM
 * ─────────────────────────────────────────────────────────────────
 * Changes from V3.0:
 *   1. Premium obsidian maze background with rounded border card
 *   2. V4.0 HUD overlay with richer colors and cleaner layout
 *   3. Solution path glow effect improved
 *   4. Edit mode crosshair with yellow tint
 *   5. Maze outer border with blue-to-purple gradient stroke
 */
public class MazePanel extends JPanel {

    private Maze        maze;
    private SolverStats stats; // Version 2.0 Stats reference for HUD
    private boolean     editMode = false;

    // Hover tracking
    private int hoverRow = -1;
    private int hoverCol = -1;

    // Pulse animation for current node
    private float pulseSize = 0f;
    private boolean pulseGrow = true;
    private final Timer pulseTimer;

    // Player Mode state
    private int playerRow = -1;
    private int playerCol = -1;
    private boolean[][] playerVisited;
    private int hintRow = -1;
    private int hintCol = -1;

    // Player smooth sliding animation fields
    private double animPlayerX = -1;
    private double animPlayerY = -1;
    private double startAnimPlayerX = -1;
    private double startAnimPlayerY = -1;
    private double targetAnimPlayerX = -1;
    private double targetAnimPlayerY = -1;
    private Timer moveTimer;
    private long animStartTime;
    private static final int ANIM_DURATION = 150; // ms

    public interface WallToggleListener {
        void onWallToggled(int row, int col);
    }
    private WallToggleListener wallToggleListener;

    private int lastDragRow = -1;
    private int lastDragCol = -1;

    public MazePanel(Maze maze) {
        this.maze = maze;
        setOpaque(false);
        setFocusable(true); // Needed so Play Mode keys work without clicking the panel first

        // Pulse timer: animates current node glow
        pulseTimer = new Timer(30, e -> {
            pulseSize += pulseGrow ? 0.08f : -0.08f;
            if (pulseSize >= 1f) { pulseSize = 1f; pulseGrow = false; }
            if (pulseSize <= 0f) { pulseSize = 0f; pulseGrow = true; }
            repaint();
        });
        pulseTimer.start();

        setupMouseListeners();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cellSize = getCellSize();
        int offsetX  = getOffsetX(cellSize);
        int offsetY  = getOffsetY(cellSize);
        int mazeW    = maze.getCols() * cellSize;
        int mazeH    = maze.getRows() * cellSize;

        // ── Maze background card ───────────────────────
        // Deep card bg with rounded corners
        g2d.setColor(ThemeManager.getBgCard());
        g2d.fillRoundRect(offsetX - 6, offsetY - 6, mazeW + 12, mazeH + 12, 16, 16);
        // Subtle inner gradient tint
        GradientPaint mazeBg = new GradientPaint(offsetX, offsetY,
            ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 4),
            offsetX + mazeW, offsetY + mazeH,
            ThemeManager.withAlpha(ThemeManager.getAccentPurple(), 3));
        g2d.setPaint(mazeBg);
        g2d.fillRoundRect(offsetX - 6, offsetY - 6, mazeW + 12, mazeH + 12, 16, 16);

        // ── Draw each cell ───────────────────────────────
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Cell cell = maze.getCell(r, c);
                int  x    = offsetX + c * cellSize;
                int  y    = offsetY + r * cellSize;
                drawCell(g2d, cell, x, y, cellSize, r, c);
            }
        }

        // ── Grid lines ───────────────────────────────────
        g2d.setColor(ThemeManager.withAlpha(ThemeManager.getBorderColor(), 80));
        g2d.setStroke(new BasicStroke(0.5f));
        for (int r = 0; r <= maze.getRows(); r++) {
            g2d.drawLine(offsetX, offsetY + r * cellSize, offsetX + mazeW, offsetY + r * cellSize);
        }
        for (int c = 0; c <= maze.getCols(); c++) {
            g2d.drawLine(offsetX + c * cellSize, offsetY, offsetX + c * cellSize, offsetY + mazeH);
        }

        // ── Outer border (blue to purple gradient) ───────
        g2d.setColor(ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 90));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(offsetX - 3, offsetY - 3, mazeW + 6, mazeH + 6, 14, 14);

        // ── Draw Player Avatar ─────────────────────────
        if (playerRow != -1 && animPlayerX != -1 && animPlayerY != -1) {
            int pSize = (int)(cellSize * 0.8);
            int pX = (int)(animPlayerX + (cellSize - pSize) / 2);
            int pY = (int)(animPlayerY + (cellSize - pSize) / 2);
            
            // Draw a glowing outer drop-shadow ring
            int pulse = (int)(pulseSize * 6);
            g2d.setColor(ThemeManager.withAlpha(ThemeManager.getAccentBlue(), (int)(60 * (1 - pulseSize))));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(pX - pulse / 2, pY - pulse / 2, pSize + pulse, pSize + pulse);
            
            // Inner gradient body
            GradientPaint playerGrad = new GradientPaint(pX, pY,
                ThemeManager.getAccentCyan(), pX + pSize, pY + pSize,
                ThemeManager.getAccentBlue());
            g2d.setPaint(playerGrad);
            g2d.fillOval(pX, pY, pSize, pSize);
            
            // Bright highlight ring on top
            g2d.setColor(ThemeManager.withAlpha(Color.WHITE, 140));
            g2d.setStroke(new BasicStroke(1f));
            g2d.drawOval(pX + 1, pY + 1, pSize - 2, pSize - 2);
            
            // Small center core
            g2d.setColor(Color.WHITE);
            int core = Math.max(3, pSize / 4);
            g2d.fillOval(pX + pSize / 2 - core / 2, pY + pSize / 2 - core / 2, core, core);
        }

        // ── Edit mode indicator ──────────────────────────
        if (editMode) {
            g2d.setColor(ThemeManager.withAlpha(ThemeManager.getAccentYellow(), 230));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2d.drawString("✏  EDIT MODE — Click or drag to toggle walls", offsetX, offsetY - 12);
        }

        // ── Version 2.0 HUD Overlay Drawing ───────────────
        if (stats != null && stats.getCurrentR() != -1) {
            drawHUD(g2d, offsetX, offsetY);
        }

        g2d.dispose();
    }

    private void drawHUD(Graphics2D g2d, int offsetX, int offsetY) {
        int hudW = 240;
        int hudH = 80;
        int hudX = offsetX + 10;
        int hudY = offsetY + 10;

        // Glass card backing
        g2d.setColor(ThemeManager.withAlpha(ThemeManager.getBgCard(), 230));
        g2d.fillRoundRect(hudX, hudY, hudW, hudH, 14, 14);

        // Top accent bar (blue)
        GradientPaint topBar = new GradientPaint(hudX, 0,
            ThemeManager.getAccentBlue(), hudX + hudW, 0,
            ThemeManager.getAccentPurple());
        g2d.setPaint(topBar);
        g2d.fillRoundRect(hudX, hudY, hudW, 3, 4, 4);

        // Border
        g2d.setColor(ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 80));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(hudX, hudY, hudW, hudH, 14, 14);

        // V4.0 HUD label
        g2d.setColor(ThemeManager.getAccentBlue());
        g2d.setFont(new Font("Consolas", Font.BOLD, 8));
        g2d.drawString("V4.0 LIVE HUD", hudX + 12, hudY + 20);

        // Cell and depth
        g2d.setColor(ThemeManager.getTextPrimary());
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        String coords = "(" + stats.getCurrentR() + ", " + stats.getCurrentC() + ")";
        g2d.drawString("Cell: " + coords + "   Depth: " + stats.getCurrentDepth(), hudX + 12, hudY + 38);

        // Direction
        String dir = stats.getCurrentDirection();
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.setColor(ThemeManager.getAccentGreen());
        g2d.drawString("Dir: " + (dir == null || dir.isEmpty() ? "—" : dir), hudX + 12, hudY + 56);

        // Solutions count
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2d.setColor(ThemeManager.getAccentYellow());
        g2d.drawString("Paths: " + stats.getTotalSolutions(), hudX + 140, hudY + 56);

        // Nodes visited
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2d.setColor(ThemeManager.getTextSecondary());
        g2d.drawString("Nodes: " + stats.getVisitedNodes(), hudX + 140, hudY + 70);
    }

    private void drawCell(Graphics2D g2d, Cell cell, int x, int y, int size, int row, int col) {
        Cell.State state = cell.getState();
        Color fill;
        if ((state == Cell.State.VISITED || state == Cell.State.BACKTRACK || state == Cell.State.DEAD_END)
                && cell.getDepth() > 0 && stats != null && stats.getMaxDepth() > 0) {
            float ratio = (float) cell.getDepth() / stats.getMaxDepth();
            Color shallowColor = ThemeManager.CELL_VISITED; // Purple
            Color deepColor = ThemeManager.getAccentPink();  // Pink
            if (state == Cell.State.BACKTRACK) {
                shallowColor = ThemeManager.CELL_BACKTRACK; // Yellow
                deepColor = ThemeManager.getAccentOrange(); // Orange
            } else if (state == Cell.State.DEAD_END) {
                shallowColor = ThemeManager.CELL_DEAD_END; // Red
                deepColor = ThemeManager.getAccentOrange(); // Orange
            }
            fill = ThemeManager.blend(shallowColor, deepColor, ratio);
        } else {
            fill = getCellColor(state);
        }
        int pad = 1;

        // ── Base fill ────────────────────────────────────
        g2d.setColor(fill);
        g2d.fillRoundRect(x + pad, y + pad, size - pad * 2, size - pad * 2, 4, 4);

        // ── Player Trail ──────────────────────────────────
        if (playerVisited != null && row < playerVisited.length && col < playerVisited[0].length && playerVisited[row][col]) {
            if (state != Cell.State.START && state != Cell.State.END) {
                g2d.setColor(ThemeManager.withAlpha(ThemeManager.getAccentOrange(), 80));
                g2d.fillRoundRect(x + pad, y + pad, size - pad * 2, size - pad * 2, 4, 4);
            }
        }

        // ── Hint Overlay ──────────────────────────────────
        if (row == hintRow && col == hintCol) {
            g2d.setColor(ThemeManager.withAlpha(ThemeManager.getAccentYellow(), 100));
            g2d.fillRoundRect(x + pad, y + pad, size - pad * 2, size - pad * 2, 4, 4);
            g2d.setColor(ThemeManager.getAccentYellow());
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(x + pad + 1, y + pad + 1, size - pad * 2 - 2, size - pad * 2 - 2, 4, 4);
        }

        // ── Glow on solution path ─────────────────────────
        if (state == Cell.State.SOLUTION && size >= 14) {
            g2d.setColor(ThemeManager.withAlpha(ThemeManager.CELL_SOLUTION, 40));
            g2d.fillRoundRect(x - 1, y - 1, size + 2, size + 2, 6, 6);
            g2d.setColor(fill);
            g2d.fillRoundRect(x + pad, y + pad, size - pad * 2, size - pad * 2, 4, 4);
            // Bright inner dot
            g2d.setColor(ThemeManager.withAlpha(Color.WHITE, 60));
            int dot = size / 5;
            g2d.fillOval(x + size / 2 - dot / 2, y + size / 2 - dot / 2, dot, dot);
        }

        // ── Pulse ring on CURRENT node ─────────────────
        if (state == Cell.State.CURRENT) {
            int expand = (int)(pulseSize * 5);
            g2d.setColor(ThemeManager.withAlpha(ThemeManager.CELL_CURRENT, (int)(80 * (1 - pulseSize))));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(x - expand, y - expand, size + expand * 2, size + expand * 2, 6, 6);
            // Bright center dot
            g2d.setColor(ThemeManager.withAlpha(Color.WHITE, 80));
            int dot = Math.max(3, size / 4);
            g2d.fillOval(x + size / 2 - dot / 2, y + size / 2 - dot / 2, dot, dot);
            // Direction arrow overlay
            if (stats != null && stats.getCurrentDirection() != null && size >= 14) {
                String arrow = stats.getCurrentDirection();
                g2d.setFont(new Font("Segoe UI", Font.BOLD, Math.max(8, size / 3)));
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(arrow, x + (size - fm.stringWidth(arrow)) / 2,
                        y + (size + fm.getAscent() - fm.getDescent()) / 2);
            }
        }

        // ── Hover highlight (only in edit mode or general hover) ──
        if (row == hoverRow && col == hoverCol && editMode) {
            g2d.setColor(ThemeManager.withAlpha(ThemeManager.getAccentYellow(), 60));
            g2d.fillRoundRect(x + pad, y + pad, size - pad * 2, size - pad * 2, 4, 4);
            g2d.setColor(ThemeManager.getAccentYellow());
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(x + pad, y + pad, size - pad * 2, size - pad * 2, 4, 4);
        }

        // ── Labels for START and END ──────────────────────
        if (size >= 16) {
            if (state == Cell.State.START) drawLabel(g2d, "S", x, y, size, Color.WHITE);
            else if (state == Cell.State.END) drawLabel(g2d, "E", x, y, size, Color.WHITE);
        }
    }

    private void drawLabel(Graphics2D g2d, String text, int x, int y, int size, Color color) {
        g2d.setColor(color);
        int fs = size > 30 ? 14 : size > 20 ? 11 : 8;
        g2d.setFont(new Font("Segoe UI", Font.BOLD, fs));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, x + (size - fm.stringWidth(text)) / 2,
                y + (size - fm.getHeight()) / 2 + fm.getAscent());
    }

    private Color getCellColor(Cell.State state) {
        switch (state) {
            case WALL:      return ThemeManager.CELL_WALL;
            case OPEN:      return ThemeManager.getCellOpen();
            case START:     return ThemeManager.CELL_START;
            case END:       return ThemeManager.CELL_END;
            case CURRENT:   return ThemeManager.CELL_CURRENT;
            case VISITED:   return ThemeManager.CELL_VISITED;
            case BACKTRACK: return ThemeManager.CELL_BACKTRACK;
            case SOLUTION:  return ThemeManager.CELL_SOLUTION;
            case DEAD_END:  return ThemeManager.CELL_DEAD_END;
            default:        return ThemeManager.getCellOpen();
        }
    }

    private int getCellSize() {
        if (maze == null) return 30;
        int bw = (getWidth() - 60) / maze.getCols();
        int bh = (getHeight() - 60) / maze.getRows();
        return Math.max(10, Math.min(60, Math.min(bw, bh)));
    }
    private int getOffsetX(int cs) { return (getWidth()  - maze.getCols() * cs) / 2; }
    private int getOffsetY(int cs) { return (getHeight() - maze.getRows() * cs) / 2; }

    private void setupMouseListeners() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
                } else {
                    lastDragRow = -1;
                    lastDragCol = -1;
                    handleMouse(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
                }
            }

            @Override public void mouseDragged(MouseEvent e)  { handleMouse(e); }
            @Override public void mouseMoved(MouseEvent e)    { updateHover(e); }
            @Override public void mouseExited(MouseEvent e)   { hoverRow = -1; hoverCol = -1; repaint(); }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    private void handleMouse(MouseEvent e) {
        if (!editMode || wallToggleListener == null || maze == null) return;
        int cs = getCellSize();
        int col = (e.getX() - getOffsetX(cs)) / cs;
        int row = (e.getY() - getOffsetY(cs)) / cs;
        if (maze.isInBounds(row, col) && (row != lastDragRow || col != lastDragCol)) {
            lastDragRow = row; lastDragCol = col;
            wallToggleListener.onWallToggled(row, col);
        }
    }

    private void updateHover(MouseEvent e) {
        if (maze == null) return;
        int cs = getCellSize();
        int col = (e.getX() - getOffsetX(cs)) / cs;
        int row = (e.getY() - getOffsetY(cs)) / cs;
        if (maze.isInBounds(row, col)) { hoverRow = row; hoverCol = col; }
        else { hoverRow = -1; hoverCol = -1; }
        repaint();
    }

    private void showPopupMenu(MouseEvent e) {
        if (maze == null) return;
        int cs = getCellSize();
        int col = (e.getX() - getOffsetX(cs)) / cs;
        int row = (e.getY() - getOffsetY(cs)) / cs;
        if (!maze.isInBounds(row, col)) return;

        JPopupMenu menu = new JPopupMenu();
        JMenuItem setStart = new JMenuItem("🚩 Set Start Node (" + row + ", " + col + ")");
        JMenuItem setEnd   = new JMenuItem("🎯 Set End Node (" + row + ", " + col + ")");
        JMenuItem toggleW  = new JMenuItem("🧱 Toggle Wall");
        JMenuItem clearW   = new JMenuItem("⟳ Clear All Walls");

        setStart.addActionListener(ae -> { maze.setStart(row, col); repaint(); });
        setEnd.addActionListener(ae -> { maze.setEnd(row, col); repaint(); });
        toggleW.addActionListener(ae -> { maze.toggleWall(row, col); repaint(); });
        clearW.addActionListener(ae -> { maze.clearAll(); repaint(); });

        menu.add(setStart);
        menu.add(setEnd);
        menu.addSeparator();
        menu.add(toggleW);
        menu.add(clearW);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    public void setStats(SolverStats stats)                     { this.stats = stats; }
    public void setMaze(Maze maze)                              { this.maze = maze; repaint(); }
    public Maze getMaze()                                       { return maze; }
    public void setEditMode(boolean em) {
        this.editMode = em;
        setCursor(em ? Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR) : Cursor.getDefaultCursor());
        repaint();
    }
    public boolean isEditMode()                                 { return editMode; }
    public void setWallToggleListener(WallToggleListener l)     { this.wallToggleListener = l; }

    public void setPlayerPosition(int r, int c) {
        int cellSize = getCellSize();
        int offsetX  = getOffsetX(cellSize);
        int offsetY  = getOffsetY(cellSize);
        
        int targetX = offsetX + c * cellSize;
        int targetY = offsetY + r * cellSize;
        
        if (playerRow == -1 || r == -1 || c == -1) {
            playerRow = r;
            playerCol = c;
            animPlayerX = targetX;
            animPlayerY = targetY;
            repaint();
            return;
        }
        
        startAnimPlayerX = animPlayerX;
        startAnimPlayerY = animPlayerY;
        targetAnimPlayerX = targetX;
        targetAnimPlayerY = targetY;
        playerRow = r;
        playerCol = c;
        
        if (moveTimer != null && moveTimer.isRunning()) {
            moveTimer.stop();
        }
        
        animStartTime = System.currentTimeMillis();
        moveTimer = new Timer(15, e -> {
            long elapsed = System.currentTimeMillis() - animStartTime;
            double t = (double) elapsed / ANIM_DURATION;
            if (t >= 1.0) {
                t = 1.0;
                moveTimer.stop();
            }
            animPlayerX = startAnimPlayerX + (targetAnimPlayerX - startAnimPlayerX) * t;
            animPlayerY = startAnimPlayerY + (targetAnimPlayerY - startAnimPlayerY) * t;
            repaint();
        });
        moveTimer.start();
    }

    public void setPlayerVisited(boolean[][] pv) {
        this.playerVisited = pv;
        repaint();
    }

    public void setHintPosition(int r, int c) {
        this.hintRow = r;
        this.hintCol = c;
        repaint();
    }
}
