package view.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import view.Theme;

/**
 * A premium custom-painted JComponent button with rounded corners,
 * smooth hover/pressed color transitions, and anti-aliasing.
 */
public class RoundedButton extends JButton {
    private final int cornerRadius;
    private Color defaultBg;
    private Color hoverBg;
    private Color pressedBg;
    private Color defaultText;
    private Color hoverText;
    private boolean isHovered;
    private boolean isPressed;
    
    /**
     * Constructs a RoundedButton with custom label and corner radius.
     *
     * @param text         button label text
     * @param cornerRadius the roundness of the corners in pixels
     */
    public RoundedButton(String text, int cornerRadius) {
        super(text);
        this.cornerRadius = cornerRadius;
        this.isHovered = false;
        this.isPressed = false;

        // Visual setup
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Theme.getSemiboldFont(14));

        // Assign default colors
        setColors(
            Theme.getAccentColor(),
            Theme.getAccentHoverColor(),
            Theme.getAccentColor().darker(),
            Color.WHITE,
            Color.WHITE
        );

        // Hook mouse interactions
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    /**
     * Constructs a default RoundedButton with 12px corner radius.
     *
     * @param text button label text
     */
    public RoundedButton(String text) {
        this(text, 12);
    }

    /**
     * Explicitly defines the color schemes used for different button states.
     */
    public void setColors(Color bg, Color hover, Color pressed, Color text, Color textHover) {
        this.defaultBg = bg;
        this.hoverBg = hover;
        this.pressedBg = pressed;
        this.defaultText = text;
        this.hoverText = textHover;
        setBackground(defaultBg);
        setForeground(defaultText);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background color calculation
        Color currentBg = defaultBg;
        Color currentFg = defaultText;

        if (!isEnabled()) {
            currentBg = Theme.getCardBgColor();
            currentFg = Theme.getSecondaryTextColor();
        } else if (isPressed) {
            currentBg = pressedBg;
            currentFg = hoverText;
        } else if (isHovered) {
            currentBg = hoverBg;
            currentFg = hoverText;
        }

        // Draw background
        g2.setColor(currentBg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Draw thin card border if we are in non-accent mode
        if (defaultBg.equals(Theme.getCardBgColor())) {
            g2.setColor(Theme.getCardBorderColor());
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();

        // Let standard Swing draw the text (centered)
        setForeground(currentFg);
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        // Give padding
        return new Dimension(d.width + 24, d.height + 12);
    }
}
