package view.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * RoundedButton - A custom JButton with rounded corners and hover effects.
 *
 * WHY THIS CLASS EXISTS:
 *   Default Swing buttons look like Windows XP. This class overrides
 *   paintComponent() to paint a modern rounded button with color
 *   transitions on hover and press.
 *
 * JAVA CONCEPTS USED: Inheritance, Method Overriding, Anonymous Inner Class,
 *                     Graphics2D, Mouse Events
 */
public class RoundedButton extends JButton {

    private Color normalBg;
    private Color hoverBg;
    private Color pressBg;
    private Color textColor;
    private int radius = 10;

    // State flags for painting
    private boolean hovered  = false;
    private boolean pressed  = false;

    public RoundedButton(String text) {
        super(text);
        // Use accent blue as default style
        setStyle(Theme.BLUE, Theme.BLUE_DARK, Theme.BLUE_DARK.darker(), Color.WHITE);
        setupButton();
    }

    /**
     * Sets a custom color style for this button.
     */
    public void setStyle(Color normal, Color hover, Color press, Color text) {
        this.normalBg  = normal;
        this.hoverBg   = hover;
        this.pressBg   = press;
        this.textColor = text;
        setForeground(text);
        repaint();
    }

    /**
     * Secondary/outline style - transparent background, accent border.
     */
    public void setSecondaryStyle() {
        setStyle(Theme.getCard2(), Theme.getCard(), Theme.getBorder(), Theme.getText());
    }

    /**
     * Red danger style for destructive actions like "Clear Grid".
     */
    public void setDangerStyle() {
        setStyle(new Color(188, 30, 30), new Color(220, 50, 50), new Color(160, 20, 20), Color.WHITE);
    }

    private void setupButton() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(Theme.bodyFont());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pick background color based on state
        Color bg;
        if (!isEnabled()) {
            bg = Theme.getBorder();
        } else if (pressed) {
            bg = pressBg;
        } else if (hovered) {
            bg = hoverBg;
        } else {
            bg = normalBg;
        }

        // Draw rounded rectangle background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Draw the button text centered
        g2.setFont(getFont());
        g2.setColor(isEnabled() ? textColor : Theme.getText2());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
