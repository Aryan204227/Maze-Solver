package view.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * RoundedPanel - A custom JPanel with rounded corners and optional border.
 *
 * WHY THIS CLASS EXISTS:
 *   Default Swing JPanels have sharp square corners which look outdated.
 *   This class overrides paintComponent() to draw a rounded rectangle,
 *   giving the application a modern card-style look.
 *
 * JAVA CONCEPTS USED: Inheritance (extends JPanel), Method Overriding,
 *                     Graphics2D, RenderingHints (anti-aliasing)
 */
public class RoundedPanel extends JPanel {

    private int cornerRadius;
    private Color bgColor;
    private boolean showBorder;

    public RoundedPanel(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        this.bgColor = Theme.getCard();
        this.showBorder = true;
        setOpaque(false); // Important: let us handle our own painting
    }

    public void setBgColor(Color color) {
        this.bgColor = color;
    }

    public void setShowBorder(boolean show) {
        this.showBorder = show;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable smooth rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw subtle shadow effect (slightly offset, semi-transparent)
        g2.setColor(new Color(0, 0, 0, 20));
        g2.fillRoundRect(2, 3, w - 2, h - 2, cornerRadius, cornerRadius);

        // Draw the main background
        Color bg = bgColor != null ? bgColor : Theme.getCard();
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Draw border if enabled
        if (showBorder) {
            g2.setStroke(new BasicStroke(1.0f));
            g2.setColor(Theme.getBorder());
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();

        // Let children (labels, buttons inside) paint themselves
        super.paintComponent(g);
    }
}
