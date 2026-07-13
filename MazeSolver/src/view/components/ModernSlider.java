package view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import view.Theme;

/**
 * A sleek, modern custom-rendered JSlider component.
 * Replaces default native layouts with a minimal track and a clean accent-colored knob.
 */
public class ModernSlider extends JSlider {

    /**
     * Constructs a ModernSlider with minimum, maximum, and starting values.
     */
    public ModernSlider(int min, int max, int value) {
        super(min, max, value);
        setOpaque(false);
        setUI(new ModernSliderUI(this));
    }

    /**
     * Custom BasicSliderUI implementation to override thumb and track drawing.
     */
    private static class ModernSliderUI extends BasicSliderUI {
        
        public ModernSliderUI(JSlider b) {
            super(b);
        }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color trackBg = Theme.getMode() == Theme.Mode.DARK ? new Color(50, 50, 55) : new Color(210, 215, 222);
            g2.setColor(trackBg);

            int trackHeight = 4;
            int cy = trackRect.y + (trackRect.height - trackHeight) / 2;

            // Draw track
            g2.fillRoundRect(trackRect.x, cy, trackRect.width, trackHeight, trackHeight, trackHeight);

            // Highlight progress up to the thumb position
            int thumbPos = thumbRect.x + thumbRect.width / 2;
            g2.setColor(Theme.getAccentColor());
            g2.fillRoundRect(trackRect.x, cy, thumbPos - trackRect.x, trackHeight, trackHeight, trackHeight);

            g2.dispose();
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Outer thumb shadow
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);

            // Inner thumb
            int inset = 2;
            g2.setColor(Theme.getSecondaryAccentColor());
            g2.fillOval(thumbRect.x + inset, thumbRect.y + inset, thumbRect.width - inset * 2, thumbRect.height - inset * 2);

            // Center spot
            g2.setColor(Color.WHITE);
            g2.fillOval(thumbRect.x + thumbRect.width/2 - 2, thumbRect.y + thumbRect.height/2 - 2, 4, 4);

            g2.dispose();
        }

        @Override
        protected Dimension getThumbSize() {
            return new Dimension(16, 16);
        }
    }
}
