package view;

import view.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * SplashScreen - Animated loading window shown when the application starts.
 * Uses a JWindow (no title bar) with a progress bar animation.
 */
public class SplashScreen extends JWindow {

    private int progress = 0;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private Runnable onComplete;

    public SplashScreen() {
        setSize(480, 280);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(13, 17, 23),
                    getWidth(), getHeight(), new Color(22, 27, 34)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(48, 54, 61));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 50, 8, 50);

        JLabel iconLabel = new JLabel("[ MAZE ]", JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        iconLabel.setForeground(new Color(88, 166, 255));
        panel.add(iconLabel, gbc);

        JLabel titleLabel = new JLabel("Maze Solver", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(230, 237, 243));
        panel.add(titleLabel, gbc);

        JLabel subLabel = new JLabel("DFS  Backtracking  Recursion", JLabel.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(139, 148, 158));
        gbc.insets = new Insets(0, 50, 20, 50);
        panel.add(subLabel, gbc);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setForeground(new Color(88, 166, 255));
        progressBar.setBackground(new Color(48, 54, 61));
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setPreferredSize(new Dimension(380, 5));
        gbc.insets = new Insets(4, 50, 4, 50);
        panel.add(progressBar, gbc);

        statusLabel = new JLabel("Initializing...", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(139, 148, 158));
        gbc.insets = new Insets(4, 50, 16, 50);
        panel.add(statusLabel, gbc);

        setContentPane(panel);
    }

    /**
     * Shows the splash screen, animates the progress bar, then calls onComplete.
     * @param durationMs  how long the splash lasts in milliseconds
     * @param onComplete  Runnable to execute after splash finishes
     */
    public void showSplash(final int durationMs, final Runnable onComplete) {
        this.onComplete = onComplete;
        setVisible(true);

        final String[] messages = {
            "Loading components...",
            "Initializing maze engine...",
            "Setting up algorithms...",
            "Preparing UI...",
            "Ready!"
        };

        // Each timer tick = durationMs/50 ms (50 ticks to reach 100%)
        final int tickDelay = Math.max(10, durationMs / 50);
        final Timer timer = new Timer(tickDelay, null);

        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 2;
                progressBar.setValue(progress);
                int msgIndex = Math.min(progress / 22, messages.length - 1);
                statusLabel.setText(messages[msgIndex]);

                if (progress >= 100) {
                    timer.stop();
                    dispose();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }
        });

        timer.start();
    }
}
