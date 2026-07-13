package com.mazesolver.view;

import com.mazesolver.util.Constants;
import com.mazesolver.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ToastNotification.java
 * ─────────────────────────────────────────────────────
 * A premium custom non-blocking toast overlay notification.
 * Slides and fades in from the bottom-right corner of the window.
 *
 * WHY THIS CLASS EXISTS:
 *   Replaces default JOptionPanes which are blocking, modal,
 *   and look like primitive operating system popups.
 */
public class ToastNotification extends JPanel {

    public enum Type {
        SUCCESS, INFO, WARNING
    }

    private final Type type;
    private final String message;
    private float alpha = 0f;
    private int yOffset = 40; // Starts 40 pixels below final position for slide-in effect
    private final JLayeredPane parentPane;

    private Timer fadeInTimer;
    private Timer fadeOutTimer;
    private Timer holdTimer;

    private ToastNotification(JFrame parentFrame, String message, Type type) {
        this.message = message;
        this.type = type;
        this.parentPane = parentFrame.getLayeredPane();

        setupPanel();
        buildUI();
    }

    private void setupPanel() {
        setLayout(new BorderLayout(10, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        setSize(320, 50);
    }

    private void buildUI() {
        // Status indicator Icon
        String icon = "ℹ️";
        Color accentColor = ThemeManager.getAccentBlue();
        if (type == Type.SUCCESS) {
            icon = "✅";
            accentColor = ThemeManager.getAccentGreen();
        } else if (type == Type.WARNING) {
            icon = "⚠️";
            accentColor = ThemeManager.getAccentOrange();
        }

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(Constants.FONT_BODY);
        msgLabel.setForeground(ThemeManager.getTextPrimary());

        add(iconLabel, BorderLayout.WEST);
        add(msgLabel, BorderLayout.CENTER);

        // Position at bottom-right corner of the parent frame
        updatePosition();
    }

    private void updatePosition() {
        int x = parentPane.getWidth() - getWidth() - 25;
        int y = parentPane.getHeight() - getHeight() - Constants.STATUS_BAR_HEIGHT - 20 - (40 - yOffset);
        setLocation(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Apply alpha transition
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Background
        g2d.setColor(ThemeManager.getBgCard());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        // Status accent color border indicator (Left border)
        Color accentColor = ThemeManager.getAccentBlue();
        if (type == Type.SUCCESS) accentColor = ThemeManager.getAccentGreen();
        if (type == Type.WARNING) accentColor = ThemeManager.getAccentOrange();
        
        g2d.setColor(accentColor);
        g2d.fillRoundRect(0, 0, 5, getHeight(), 5, 5);

        // Border outline
        g2d.setColor(ThemeManager.getBorderColor());
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        g2d.dispose();
        super.paintComponent(g);
    }

    private void startAnimations() {
        parentPane.add(this, JLayeredPane.POPUP_LAYER);
        parentPane.revalidate();

        // 1. Slide & Fade In (Duration: 200ms)
        fadeInTimer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.1f;
                yOffset -= 4;
                if (alpha >= 1f) {
                    alpha = 1f;
                    yOffset = 0;
                    fadeInTimer.stop();
                    holdTimer.start(); // Start hold timer once fully visible
                }
                updatePosition();
                parentPane.repaint(getBounds());
            }
        });

        // 2. Hold Timer (Duration: 2.5s)
        holdTimer = new Timer(2500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                holdTimer.stop();
                fadeOutTimer.start(); // Start fade out
            }
        });

        // 3. Fade Out (Duration: 200ms)
        fadeOutTimer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.1f;
                yOffset += 4;
                if (alpha <= 0f) {
                    alpha = 0f;
                    fadeOutTimer.stop();
                    parentPane.remove(ToastNotification.this);
                    parentPane.revalidate();
                    parentPane.repaint();
                } else {
                    updatePosition();
                    parentPane.repaint(getBounds());
                }
            }
        });

        fadeInTimer.start();
    }

    // ── Static Helper Launchers ───────────────────────────

    public static void showSuccess(JFrame parent, String message) {
        if (parent == null) return;
        ToastNotification toast = new ToastNotification(parent, message, Type.SUCCESS);
        toast.startAnimations();
    }

    public static void showInfo(JFrame parent, String message) {
        if (parent == null) return;
        ToastNotification toast = new ToastNotification(parent, message, Type.INFO);
        toast.startAnimations();
    }

    public static void showWarning(JFrame parent, String message) {
        if (parent == null) return;
        ToastNotification toast = new ToastNotification(parent, message, Type.WARNING);
        toast.startAnimations();
    }
}
