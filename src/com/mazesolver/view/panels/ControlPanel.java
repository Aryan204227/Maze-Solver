package com.mazesolver.view.panels;

import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ControlPanel.java — VERSION 4.0 PREMIUM
 * Features:
 *   1. Grouped button sections with separators
 *   2. Difficulty preset buttons (Easy / Medium / Hard)
 *   3. Speed preset buttons (Slow / Normal / Fast / Instant)
 *   4. Step controls (⏮ Back, ⏭ Next, 🔁 Replay) when paused or complete
 *   5. Hover glow + press animation effects
 *   6. Load recent files dropdown
 *   7. V4.0: Deeper obsidian gradient toolbar background
 */
public class ControlPanel extends JPanel {

    public interface ControlListener {
        void onGenerate();
        void onGenerateTemplate(String template);
        void onSolve();
        void onPause();
        void onResume();
        void onReset();
        void onPrevStep();
        void onNextStep();
        void onReplay();
        void onEditToggle(boolean editMode);
        void onEditToolChanged(String tool);
        void onEditBrushSizeChanged(int size);
        void onSpeedChanged(int delayMs);
        void onSizeChanged(int rows, int cols);
        void onSaveFile();
        void onExportSolution();
        void onLoadFile();
        void onLoadRecentFile(java.io.File file);
        void onHint();          // Phase 7: AI Hint
        void onModeChanged(String mode);
    }

    private ControlListener controlListener;
    private JButton btnSolve, btnPause, btnResume, btnEdit, btnReset;
    private JButton btnPrev, btnNext, btnReplay;
    private JButton btnGenerate, btnSave, btnLoad, btnHint;
    private JComboBox<String> modeCombo;
    private JLabel modeLbl;
    private boolean editMode = false;

    // V4.0: Edit Mode Brush controls
    private JPanel editToolPanel;
    private JComboBox<String> toolCombo;
    private JComboBox<String> brushSizeCombo;


    // Speed preset button references for active highlight
    private JButton speedSlow, speedNormal, speedFast, speedInstant;
    private JButton activeSpeedBtn;

    // Difficulty preset button references for active highlight
    private JButton diffEasy, diffMedium, diffHard;
    private JButton activeDiffBtn;

    public ControlPanel() {
        setupPanel();
        buildUI();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, Constants.CONTROL_PANEL_HEIGHT));
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        setOpaque(false);
    }

    private void buildUI() {
        // ── LEFT: Main action buttons ───────────────────
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);

        modeLbl = new JLabel("Mode:");
        modeLbl.setFont(Constants.FONT_SMALL);
        modeLbl.setForeground(ThemeManager.getTextSecondary());

        String[] modes = {"AI Solve", "Play Mode", "Replay"};
        modeCombo = new JComboBox<>(modes);
        modeCombo.setFont(Constants.FONT_SMALL);
        modeCombo.setPreferredSize(new Dimension(86, 26));
        modeCombo.setFocusable(false);
        modeCombo.addActionListener(e -> {
            String selected = (String) modeCombo.getSelectedItem();
            updateButtonVisibilities(selected);
            if (controlListener != null) {
                controlListener.onModeChanged(selected);
            }
        });

        btnGenerate = makeBtn("  Generate", new Color(0x8250DF), "Generate random maze (Ctrl+G)");
        btnEdit             = makeBtn("  Edit",     new Color(0x57606A), "Toggle wall edit mode (Ctrl+E)");
        btnSolve            = makeBtn("  Solve",    new Color(0x238636), "Start solving (Ctrl+S)");
        btnPause            = makeBtn("  Pause",    new Color(0xF0A500), "Pause animation (Ctrl+P)");
        btnResume           = makeBtn("  Resume",   new Color(0x238636), "Resume animation (Ctrl+P)");
        btnPrev             = makeBtn("  Back",     new Color(0x6E40C9), "Step backward in history");
        btnNext             = makeBtn("  Next",     new Color(0x6E40C9), "Step forward in history");
        btnReplay           = makeBtn("  Replay",   new Color(0x238636), "Replay last solve");
        btnReset            = makeBtn("  Reset",    new Color(0x424A53), "Clear solving state (Ctrl+R)");
        btnSave             = makeBtn("  Save",     new Color(0x0969DA), "Save maze (Ctrl+Shift+S)");
        btnLoad             = makeBtn("  Load",     new Color(0x0969DA), "Load maze (Ctrl+O)");

        btnPause.setVisible(false);
        btnResume.setVisible(false);
        btnPrev.setVisible(false);
        btnNext.setVisible(false);
        btnReplay.setVisible(false);

        // Build V4.0 Edit Tool Panel
        editToolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        editToolPanel.setOpaque(false);
        editToolPanel.setVisible(false);

        JLabel toolLbl = new JLabel("Tool:");
        toolLbl.setFont(Constants.FONT_SMALL);
        toolLbl.setForeground(ThemeManager.getTextSecondary());

        String[] tools = {"Brush ✏\ufe0f", "Eraser \uD83D\uDDF9"};
        toolCombo = new JComboBox<>(tools);
        toolCombo.setFont(Constants.FONT_SMALL);
        toolCombo.setPreferredSize(new Dimension(86, 26));
        toolCombo.setFocusable(false);
        toolCombo.addActionListener(e -> {
            if (controlListener != null) {
                int idx = toolCombo.getSelectedIndex();
                controlListener.onEditToolChanged(idx == 0 ? "brush" : "eraser");
            }
        });

        JLabel sizeLbl = new JLabel("Brush:");
        sizeLbl.setFont(Constants.FONT_SMALL);
        sizeLbl.setForeground(ThemeManager.getTextSecondary());

        String[] sizes = {"1x1", "2x2", "3x3"};
        brushSizeCombo = new JComboBox<>(sizes);
        brushSizeCombo.setFont(Constants.FONT_SMALL);
        brushSizeCombo.setPreferredSize(new Dimension(56, 26));
        brushSizeCombo.setFocusable(false);
        brushSizeCombo.addActionListener(e -> {
            if (controlListener != null) {
                int size = brushSizeCombo.getSelectedIndex() + 1;
                controlListener.onEditBrushSizeChanged(size);
            }
        });

        editToolPanel.add(toolLbl);
        editToolPanel.add(toolCombo);
        editToolPanel.add(Box.createHorizontalStrut(2));
        editToolPanel.add(sizeLbl);
        editToolPanel.add(brushSizeCombo);

        left.add(modeLbl);
        left.add(modeCombo);
        left.add(makeSep());
        left.add(btnGenerate); left.add(makeSep());
        left.add(btnEdit);     left.add(editToolPanel); left.add(makeSep());
        left.add(btnSolve); 
        left.add(btnPause); 
        left.add(btnResume);
        left.add(btnPrev);
        left.add(btnNext);
        left.add(btnReplay);
        left.add(btnReset); left.add(makeSep());
        left.add(btnSave); left.add(btnLoad);
        left.add(makeSep());
        btnHint = makeBtn("  Hint", new Color(0xE3B341), "Show AI path hint (Ctrl+H)");
        btnHint.addActionListener(e -> { if (controlListener != null) controlListener.onHint(); });
        left.add(btnHint);


        // ── CENTER: Difficulty presets ──────────────────
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        center.setOpaque(false);

        JLabel diffLbl = new JLabel("Difficulty:");
        diffLbl.setFont(Constants.FONT_SMALL);
        diffLbl.setForeground(ThemeManager.getTextSecondary());
        center.add(diffLbl);

        diffEasy   = makePresetBtn("Easy",   new Color(0x2EA043));
        diffMedium = makePresetBtn("Medium", new Color(0xE3B341));
        diffHard   = makePresetBtn("Hard",   new Color(0xF85149));
        diffEasy.setToolTipText("Resize grid to 10 x 10 (Easy)");
        diffMedium.setToolTipText("Resize grid to 15 x 15 (Medium)");
        diffHard.setToolTipText("Resize grid to 22 x 22 (Hard)");
        center.add(diffEasy);
        center.add(diffMedium);
        center.add(diffHard);

        // Wire difficulty buttons
        diffEasy.addActionListener(e -> setDifficulty(diffEasy, 10, 10));
        diffMedium.addActionListener(e -> setDifficulty(diffMedium, 15, 15));
        diffHard.addActionListener(e -> setDifficulty(diffHard, 22, 22));

        // Set medium active by default
        setActiveDiffBtn(diffMedium);

        // ── RIGHT: Speed presets + Size ─────────────────
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);

        JLabel speedLbl = new JLabel("Speed:");
        speedLbl.setFont(Constants.FONT_SMALL);
        speedLbl.setForeground(ThemeManager.getTextSecondary());
        right.add(speedLbl);

        speedSlow    = makePresetBtn("Slow",    new Color(0x6E40C9));
        speedNormal  = makePresetBtn("Normal",  new Color(0x0969DA));
        speedFast    = makePresetBtn("Fast",    new Color(0x2EA043));
        speedInstant = makePresetBtn("Instant", new Color(0xF85149));
        speedSlow.setToolTipText("Slow execution delay (300ms)");
        speedNormal.setToolTipText("Normal execution delay (60ms)");
        speedFast.setToolTipText("Fast execution delay (15ms)");
        speedInstant.setToolTipText("Instant execution (0ms)");
        right.add(speedSlow);
        right.add(speedNormal);
        right.add(speedFast);
        right.add(speedInstant);

        speedSlow.addActionListener(e    -> setSpeed(speedSlow,    300));
        speedNormal.addActionListener(e  -> setSpeed(speedNormal,  Constants.DEFAULT_DELAY_MS));
        speedFast.addActionListener(e    -> setSpeed(speedFast,    15));
        speedInstant.addActionListener(e -> setSpeed(speedInstant, 0));

        // Set normal active by default
        setActiveSpeedBtn(speedNormal);

        right.add(makeSep());
        right.add(makeSizeBox());

        add(left,   BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(right,  BorderLayout.EAST);

        // ── Wire main button actions ────────────────────
        btnGenerate.addActionListener(e -> {
            if (controlListener == null) return;
            JPopupMenu menu = new JPopupMenu();
            JMenuItem itemRandom       = new JMenuItem("🎲  Random Maze");
            JMenuItem itemSpiral       = new JMenuItem("🌀  Spiral Template");
            JMenuItem itemCorridor     = new JMenuItem("🏢  Long Corridor");
            JMenuItem itemCheckerboard = new JMenuItem("🧱  Checkerboard");

            itemRandom.addActionListener(ae       -> controlListener.onGenerateTemplate("random"));
            itemSpiral.addActionListener(ae       -> controlListener.onGenerateTemplate("spiral"));
            itemCorridor.addActionListener(ae     -> controlListener.onGenerateTemplate("corridor"));
            itemCheckerboard.addActionListener(ae -> controlListener.onGenerateTemplate("checkerboard"));

            menu.add(itemRandom);
            menu.add(itemSpiral);
            menu.add(itemCorridor);
            menu.add(itemCheckerboard);
            menu.show(btnGenerate, 0, btnGenerate.getHeight());
        });
        btnEdit.addActionListener(e -> {
            editMode = !editMode;
            if (controlListener != null) controlListener.onEditToggle(editMode);
        });
        btnSolve.addActionListener(e   -> { if (controlListener != null) controlListener.onSolve(); });
        btnPause.addActionListener(e   -> {
            if (controlListener != null) controlListener.onPause();
        });
        btnResume.addActionListener(e  -> {
            if (controlListener != null) controlListener.onResume();
        });
        btnPrev.addActionListener(e -> { if (controlListener != null) controlListener.onPrevStep(); });
        btnNext.addActionListener(e -> { if (controlListener != null) controlListener.onNextStep(); });
        btnReplay.addActionListener(e -> { if (controlListener != null) controlListener.onReplay(); });
        btnReset.addActionListener(e   -> { if (controlListener != null) controlListener.onReset(); });
        btnSave.addActionListener(e    -> {
            if (controlListener == null) return;
            JPopupMenu menu = new JPopupMenu();
            JMenuItem itemSave   = new JMenuItem("💾  Save Maze Layout");
            JMenuItem itemExport = new JMenuItem("📄  Export Solution paths (TXT)");

            itemSave.addActionListener(ae   -> controlListener.onSaveFile());
            itemExport.addActionListener(ae -> controlListener.onExportSolution());

            menu.add(itemSave);
            menu.add(itemExport);
            menu.show(btnSave, 0, btnSave.getHeight());
        });
        btnLoad.addActionListener(e    -> handleLoadButton(btnLoad));
    }

    // ── Speed preset logic ────────────────────────────────
    private void setSpeed(JButton btn, int delayMs) {
        setActiveSpeedBtn(btn);
        if (controlListener != null) controlListener.onSpeedChanged(delayMs);
    }

    private void setActiveSpeedBtn(JButton btn) {
        if (activeSpeedBtn != null) activeSpeedBtn.putClientProperty("active", false);
        activeSpeedBtn = btn;
        btn.putClientProperty("active", true);
        repaintPresets();
    }

    // ── Difficulty preset logic ───────────────────────────
    private void setDifficulty(JButton btn, int rows, int cols) {
        setActiveDiffBtn(btn);
        if (controlListener != null) controlListener.onSizeChanged(rows, cols);
    }

    private void setActiveDiffBtn(JButton btn) {
        if (activeDiffBtn != null) activeDiffBtn.putClientProperty("active", false);
        activeDiffBtn = btn;
        btn.putClientProperty("active", true);
        repaintPresets();
    }

    private void repaintPresets() {
        if (speedSlow != null) { speedSlow.repaint(); speedNormal.repaint(); speedFast.repaint(); speedInstant.repaint(); }
        if (diffEasy != null)  { diffEasy.repaint(); diffMedium.repaint(); diffHard.repaint(); }
    }

    // ── Load with recent files dropdown ──────────────────
    private void handleLoadButton(JButton btnLoad) {
        if (controlListener == null) return;
        java.util.List<java.io.File> recents = com.mazesolver.controller.FileController.getRecentFiles();
        if (recents.isEmpty()) {
            controlListener.onLoadFile();
        } else {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem loadNew = new JMenuItem("  Load New File...");
            loadNew.addActionListener(ae -> controlListener.onLoadFile());
            popup.add(loadNew);
            popup.addSeparator();
            JMenuItem titleItem = new JMenuItem("  Recent Files:");
            titleItem.setEnabled(false);
            popup.add(titleItem);
            for (java.io.File file : recents) {
                JMenuItem item = new JMenuItem("   " + file.getName());
                item.addActionListener(ae -> controlListener.onLoadRecentFile(file));
                popup.add(item);
            }
            popup.show(btnLoad, 0, btnLoad.getHeight());
        }
    }

    // ── Button factories ──────────────────────────────────

    /**
     * Creates a main action button with glow + press animation.
     * All buttons use custom paintComponent for premium look.
     */
    private JButton makeBtn(String text, Color bgColor, String tip) {
        JButton btn = new JButton(text) {
            private boolean hov = false, prs = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e)  { hov = true;  repaint(); }
                    public void mouseExited (MouseEvent e)  { hov = false; repaint(); }
                    public void mousePressed(MouseEvent e)  { prs = true;  repaint(); }
                    public void mouseReleased(MouseEvent e) { prs = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (prs) { g2.translate(1, 1); g2.scale(0.97, 0.97); }
                Color base = prs ? bgColor.darker().darker() : hov ? bgColor.brighter() : bgColor;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Constants.RADIUS_BUTTON, Constants.RADIUS_BUTTON);
                if (hov && !prs) {
                    g2.setColor(ThemeManager.withAlpha(base, 70));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(-1, -1, getWidth()+1, getHeight()+1, 12, 12);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(94, 34));
        btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setContentAreaFilled(false);
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tip);
        return btn;
    }

    /**
     * Creates a compact preset button (speed / difficulty).
     * When active (selected), shows a highlighted border.
     */
    private JButton makePresetBtn(String text, Color color) {
        JButton btn = new JButton(text) {
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
                boolean isActive = Boolean.TRUE.equals(getClientProperty("active"));
                // Background
                Color bg = isActive ? ThemeManager.withAlpha(color, 60) :
                           hov      ? ThemeManager.withAlpha(color, 30) :
                                      ThemeManager.getBgTertiary();
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                // Border
                g2.setColor(isActive ? color : ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(isActive ? 1.5f : 0.8f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                // Text
                g2.setColor(isActive ? color : ThemeManager.getTextSecondary());
                g2.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(60, 26));
        btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setContentAreaFilled(false);
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel makeSizeBox() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);

        JLabel lbl = new JLabel("Size:");
        lbl.setFont(Constants.FONT_SMALL);
        lbl.setForeground(ThemeManager.getTextSecondary());

        String[] sizes = {"5x5","8x8","10x10","15x15","20x20","25x25","30x30"};
        JComboBox<String> box = new JComboBox<>(sizes);
        box.setSelectedItem("15x15");
        box.setFont(Constants.FONT_SMALL);
        box.setPreferredSize(new Dimension(76, 26));
        box.setFocusable(false);

        box.addActionListener(e -> {
            String sel = (String) box.getSelectedItem();
            if (sel != null && controlListener != null) {
                String[] parts = sel.split("x");
                try {
                    controlListener.onSizeChanged(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException ex) { /* ignore */ }
            }
        });

        p.add(lbl); p.add(box);
        return p;
    }

    private JPanel makeSep() {
        JPanel s = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(ThemeManager.getBorderColor());
                g.drawLine(0, 6, 0, getHeight() - 6);
            }
        };
        s.setOpaque(false);
        s.setPreferredSize(new Dimension(1, 34));
        return s;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // V4.0: Deeper obsidian toolbar background
        GradientPaint gp = new GradientPaint(0, 0, ThemeManager.getBgTertiary(),
                getWidth(), 0, ThemeManager.getBgSecondary());
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        // Top border line
        g2.setColor(ThemeManager.getBorderColor());
        g2.drawLine(0, 0, getWidth(), 0);
        // Subtle inner highlight
        g2.setColor(ThemeManager.withAlpha(ThemeManager.getAccentBlue(), 12));
        g2.drawLine(0, 1, getWidth(), 1);
        g2.dispose();
        super.paintComponent(g);
    }

    // ── State update methods (called by AppController) ───
    public void onSolveStarted() {
        SwingUtilities.invokeLater(() -> {
            btnSolve.setVisible(false);
            btnPause.setVisible(true);
            btnResume.setVisible(false);
            btnPrev.setVisible(false);
            btnNext.setVisible(false);
            btnReplay.setVisible(false);
            btnReset.setEnabled(false);
        });
    }

    public void onSolvePaused() {
        SwingUtilities.invokeLater(() -> {
            btnSolve.setVisible(false);
            btnPause.setVisible(false);
            btnResume.setVisible(true);
            btnPrev.setVisible(true);
            btnNext.setVisible(true);
            btnReplay.setVisible(false);
            btnReset.setEnabled(true);
        });
    }

    public void onSolveResumed() {
        SwingUtilities.invokeLater(() -> {
            btnSolve.setVisible(false);
            btnPause.setVisible(true);
            btnResume.setVisible(false);
            btnPrev.setVisible(false);
            btnNext.setVisible(false);
            btnReplay.setVisible(false);
            btnReset.setEnabled(false);
        });
    }

    public void onSolveFinished(boolean hasHistory) {
        SwingUtilities.invokeLater(() -> {
            btnSolve.setVisible(true);
            btnSolve.setEnabled(true);
            btnPause.setVisible(false);
            btnResume.setVisible(false);
            btnPrev.setVisible(false);
            btnNext.setVisible(false);
            btnReplay.setVisible(hasHistory);
            btnReset.setEnabled(true);
        });
    }

    public void setEditModeActive(boolean active) {
        SwingUtilities.invokeLater(() -> {
            editToolPanel.setVisible(active);
            revalidate();
            repaint();
        });
    }

    public void updateButtonVisibilities(String mode) {
        SwingUtilities.invokeLater(() -> {
            boolean isPlay = "Play Mode".equals(mode);
            boolean isReplay = "Replay".equals(mode);
            boolean isAI = "AI Solve".equals(mode);

            btnGenerate.setVisible(!isReplay);
            btnEdit.setVisible(!isReplay);
            btnSave.setVisible(!isReplay);
            btnLoad.setVisible(!isReplay);
            btnHint.setVisible(!isReplay);

            btnSolve.setVisible(isAI);
            btnPause.setVisible(false);
            btnResume.setVisible(false);
            btnPrev.setVisible(isReplay);
            btnNext.setVisible(isReplay);
            btnReplay.setVisible(isReplay);
            btnReset.setVisible(true);

            revalidate();
            repaint();
        });
    }

    public void setMode(String mode) {
        SwingUtilities.invokeLater(() -> {
            modeCombo.setSelectedItem(mode);
            updateButtonVisibilities(mode);
        });
    }

    public void setControlListener(ControlListener l) { this.controlListener = l; }
    public void applyTheme() { repaint(); }
}
