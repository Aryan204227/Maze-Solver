package com.mazesolver.view.dialogs;

import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * SettingsDialog.java — UPGRADED with preset speed buttons + grid dropdown
 */
public class SettingsDialog extends JDialog {

    private boolean settingsUpdated = false;
    private int maxSolutionsLimit = Constants.MAX_SOLUTIONS;

    public SettingsDialog(Frame parent) {
        super(parent, "Settings", true);
        setSize(420, 340);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 420, 340, 20, 20));
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
                GradientPaint accent = new GradientPaint(0, 0, ThemeManager.getAccentGreen(),
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
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(18, 28, 22, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel titleLbl = new JLabel("⚙  Settings & Preferences");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(ThemeManager.getTextPrimary());
        header.add(titleLbl, BorderLayout.WEST);
        header.add(makeCloseBtn(), BorderLayout.EAST);
        main.add(header);
        main.add(Box.createVerticalStrut(20));

        // Setting 1: Max solutions slider
        addSettingSection(main, "🏆  Maximum Solutions Tracked");
        JLabel sliderVal = new JLabel(String.valueOf(maxSolutionsLimit));
        sliderVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sliderVal.setForeground(ThemeManager.getAccentBlue());
        sliderVal.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSlider slider = new JSlider(5, 100, maxSolutionsLimit);
        slider.setOpaque(false);
        slider.setFocusable(false);
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);
        slider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        slider.addChangeListener(e -> {
            maxSolutionsLimit = slider.getValue();
            sliderVal.setText(String.valueOf(maxSolutionsLimit));
        });
        main.add(sliderVal);
        main.add(Box.createVerticalStrut(4));
        main.add(slider);
        main.add(Box.createVerticalStrut(20));

        // Setting 2: Speed presets
        addSettingSection(main, "⏱  Animation Speed Presets");
        JPanel presets = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        presets.setOpaque(false);
        presets.setAlignmentX(Component.LEFT_ALIGNMENT);
        presets.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        presets.add(makePresetBtn("⚡ Fast", new Color(0x3FB950)));
        presets.add(makePresetBtn("🔄 Medium", new Color(0x58A6FF)));
        presets.add(makePresetBtn("🐢 Slow", new Color(0xF0A500)));
        main.add(presets);
        main.add(Box.createVerticalStrut(30));

        // Save button
        JButton btnSave = new JButton("Save & Apply") {
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
                Color c1 = hov ? new Color(0x388BFD) : ThemeManager.getAccentBlue();
                Color c2 = hov ? new Color(0x2EA043) : ThemeManager.getAccentGreen();
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (hov) {
                    g2.setColor(ThemeManager.withAlpha(c1, 50));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(-1, -1, getWidth()+1, getHeight()+1, 12, 12);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btnSave.setPreferredSize(new Dimension(160, 40));
        btnSave.setMaximumSize(new Dimension(160, 40));
        btnSave.setBorderPainted(false); btnSave.setFocusPainted(false); btnSave.setContentAreaFilled(false);
        btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> { settingsUpdated = true; dispose(); });
        main.add(btnSave);

        add(main);
    }

    private void addSettingSection(JPanel parent, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTextSecondary());
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(6));
    }

    private JButton makePresetBtn(String text, Color color) {
        JButton btn = new JButton(text) {
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
                g2.setColor(hov ? ThemeManager.withAlpha(color, 35) : ThemeManager.getBgCard());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(hov ? color : ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(hov ? color : ThemeManager.getTextSecondary());
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(100, 32));
        btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
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

    public boolean isSettingsUpdated() { return settingsUpdated; }
    public int getMaxSolutionsLimit()  { return maxSolutionsLimit; }
}
