package com.mazesolver.view;

import com.mazesolver.util.ThemeManager;
import com.mazesolver.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * SplashScreen.java — VERSION 4.0 PREMIUM
 * ─────────────────────────────────────────────────────────────────
 * Features:
 *   1. Deep obsidian gradient background with radial glow center
 *   2. Animated loading bar with blue-to-green gradient
 *   3. V4.0 branding with premium typography
 *   4. Rounded window shape
 */
public class SplashScreen extends JWindow {

    private final Runnable onComplete;
    private int progress = 0;

    // Phase text shown below the progress bar
    private static final String[] PHASES = {
        "Initializing engine...",
        "Loading maze generator...",
        "Configuring DFS algorithm...",
        "Building UI components...",
        "Starting visualizer...",
    };

    public SplashScreen(Runnable onComplete) {
        this.onComplete = onComplete;

        setSize(Constants.SPLASH_WIDTH, Constants.SPLASH_HEIGHT);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, Constants.SPLASH_WIDTH, Constants.SPLASH_HEIGHT, 28, 28));

        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int W = getWidth(), H = getHeight();

                // Deep obsidian gradient background
                GradientPaint bg = new GradientPaint(0, 0, new Color(0x060A10),
                        W, H, new Color(0x0E1520));
                g2.setPaint(bg);
                g2.fillRoundRect(0, 0, W, H, 28, 28);

                // Radial glow in the center-top area
                RadialGradientPaint glow = new RadialGradientPaint(
                    new Point(W / 2, 90), 180,
                    new float[]{0f, 1f},
                    new Color[]{new Color(0x4D9EFF, true), new Color(0x000000, true)}
                );
                Composite prev = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2.setPaint(glow);
                g2.fillRect(0, 0, W, H);
                g2.setComposite(prev);

                // Outer border with accent
                g2.setColor(new Color(0x2A3140));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, W - 3, H - 3, 28, 28);

                // Top accent gradient bar (3px)
                GradientPaint topBar = new GradientPaint(60, 0, new Color(0x4D9EFF),
                        W - 60, 0, new Color(0x22C55E));
                g2.setPaint(topBar);
                g2.fillRoundRect(60, 0, W - 120, 3, 2, 2);

                // Icon
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
                String icon = "\uD83E\uDDE9"; // 🧩
                FontMetrics fmIcon = g2.getFontMetrics();
                g2.drawString(icon, (W - fmIcon.stringWidth(icon)) / 2, 96);

                // Title — "Maze Solver"
                g2.setColor(new Color(0xECF0F6));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fmT = g2.getFontMetrics();
                String title = "Maze Solver";
                g2.drawString(title, (W - fmT.stringWidth(title)) / 2, 148);

                // Version chip
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String ver = " V" + Constants.APP_VERSION + " ";
                FontMetrics fmChip = g2.getFontMetrics();
                int chipW = fmChip.stringWidth(ver) + 14;
                int chipX = (W - chipW) / 2;
                g2.setColor(new Color(0x4D9EFF, true));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2.fillRoundRect(chipX, 156, chipW, 20, 10, 10);
                g2.setComposite(prev);
                g2.setColor(new Color(0x4D9EFF));
                g2.drawString(ver, chipX + 7, 170);

                // Subtitle
                g2.setColor(new Color(0x7A8596));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                FontMetrics fmS = g2.getFontMetrics();
                String sub = "DFS + Recursive Backtracking Visualizer";
                g2.drawString(sub, (W - fmS.stringWidth(sub)) / 2, 192);

                // Divider
                g2.setColor(new Color(0x1E2635));
                g2.drawLine(60, 210, W - 60, 210);

                // Progress bar track
                int barX = 60, barY = 232, barW = W - 120, barH = 6;
                g2.setColor(new Color(0x161B22));
                g2.fillRoundRect(barX, barY, barW, barH, 6, 6);

                // Progress bar fill
                if (progress > 0) {
                    int fillW = Math.max(12, (int)((progress / 100.0) * barW));
                    GradientPaint barGrad = new GradientPaint(barX, 0, new Color(0x4D9EFF),
                            barX + fillW, 0, new Color(0x22C55E));
                    g2.setPaint(barGrad);
                    g2.fillRoundRect(barX, barY, fillW, barH, 6, 6);

                    // Glow effect on bar
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                    g2.setColor(new Color(0x4D9EFF));
                    g2.fillRoundRect(barX, barY - 2, fillW, barH + 4, 8, 8);
                    g2.setComposite(prev);
                }

                // Phase label
                int phaseIdx = Math.min(progress / 20, PHASES.length - 1);
                g2.setColor(new Color(0x3D4F6A));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                FontMetrics fmL = g2.getFontMetrics();
                String loadTxt = PHASES[phaseIdx] + "  " + progress + "%";
                g2.drawString(loadTxt, (W - fmL.stringWidth(loadTxt)) / 2, 258);

                // Bottom credits
                g2.setColor(new Color(0x2D3748));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                FontMetrics fmV = g2.getFontMetrics();
                String cred = "v" + Constants.APP_VERSION + "  \u2022  University Project  \u2022  Java Swing";
                g2.drawString(cred, (W - fmV.stringWidth(cred)) / 2, H - 18);

                g2.dispose();
            }
        });
    }

    public void showSplash() {
        setVisible(true);

        Timer timer = new Timer(20, null);
        timer.addActionListener(e -> {
            progress += 2;
            repaint();
            if (progress >= 100) {
                timer.stop();
                Timer delay = new Timer(300, ev -> {
                    dispose();
                    if (onComplete != null) onComplete.run();
                });
                delay.setRepeats(false);
                delay.start();
            }
        });
        timer.start();
    }
}
