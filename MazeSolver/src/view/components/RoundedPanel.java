package view.components;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import view.Theme;

/**
 * A customized JPanel that renders with rounded corners,
 * support for gradients, and optional glassmorphic visual borders.
 */
public class RoundedPanel extends JPanel {
    private final int cornerRadius;
    private Color startGradientColor;
    private Color endGradientColor;
    private boolean useGradient;
    private boolean drawBorder;

    /**
     * Constructs a RoundedPanel with custom corner radius.
     *
     * @param cornerRadius the roundness of the corner in pixels
     */
    public RoundedPanel(int cornerRadius) {
        super();
        this.cornerRadius = cornerRadius;
        this.useGradient = false;
        this.drawBorder = true;
        setOpaque(false); // Enable transparency outside rounded rect
    }

    /**
     * Constructs a RoundedPanel with a layout manager and corner radius.
     *
     * @param layout       the layout manager
     * @param cornerRadius the roundness of the corner in pixels
     */
    public RoundedPanel(LayoutManager layout, int cornerRadius) {
        super(layout);
        this.cornerRadius = cornerRadius;
        this.useGradient = false;
        this.drawBorder = true;
        setOpaque(false);
    }

    /**
     * Sets a gradient background configuration for the panel.
     *
     * @param start start color of the gradient
     * @param end   end color of the gradient
     */
    public void setGradientBackground(Color start, Color end) {
        this.startGradientColor = start;
        this.endGradientColor = end;
        this.useGradient = (start != null && end != null);
        repaint();
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Fill background
        if (useGradient) {
            GradientPaint gp = new GradientPaint(0, 0, startGradientColor, 0, height, endGradientColor);
            g2.setPaint(gp);
        } else {
            g2.setColor(getBackground());
        }
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // 2. Draw border (essential for premium glassmorphism effect)
        if (drawBorder) {
            g2.setColor(Theme.getCardBorderColor());
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
