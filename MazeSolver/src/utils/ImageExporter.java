package utils;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Handles capturing screenshots of Swing components (like the maze canvas)
 * and exporting them as image files.
 */
public class ImageExporter {

    /**
     * Captures a screenshot of the specified Swing component and writes it to a file.
     *
     * @param component the Swing component to capture
     * @param file      the target file (should have a .png extension)
     * @throws IOException if a write error occurs
     */
    public static void exportComponentToImage(Component component, File file) throws IOException {
        int width = component.getWidth();
        int height = component.getHeight();
        
        // If component has zero dimension (not showing yet), use its preferred size
        if (width <= 0 || height <= 0) {
            width = Math.max(component.getPreferredSize().width, 800);
            height = Math.max(component.getPreferredSize().height, 600);
        }

        // Create buffered image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        // Render component graphics onto image
        component.paintAll(image.getGraphics());
        
        // Write out file
        boolean success = ImageIO.write(image, "PNG", file);
        if (!success) {
            throw new IOException("No appropriate writer was found for PNG format.");
        }
    }
}
