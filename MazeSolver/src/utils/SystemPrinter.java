package utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Provides printing capabilities for Swing components using Java's native printing service.
 */
public class SystemPrinter implements Printable {
    private final Component componentToPrint;

    /**
     * Constructs a SystemPrinter with the component to be printed.
     *
     * @param component the Swing component to print
     */
    public SystemPrinter(Component component) {
        this.componentToPrint = component;
    }

    /**
     * Launches the system printer dialog to print a component.
     *
     * @param component the component to print
     * @param jobName   the name of the printing job
     * @return true if printing completed successfully, false if cancelled or errored
     */
    public static boolean printComponent(Component component, String jobName) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(jobName);
        job.setPrintable(new SystemPrinter(component));
        
        if (job.printDialog()) {
            try {
                job.print();
                return true;
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        // Move graphics origin to printable area
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // Calculate scaling factors to fit component within the page dimensions
        double pfWidth = pf.getImageableWidth();
        double pfHeight = pf.getImageableHeight();
        
        Dimension compSize = componentToPrint.getSize();
        if (compSize.width <= 0 || compSize.height <= 0) {
            compSize = componentToPrint.getPreferredSize();
        }

        double scaleX = pfWidth / compSize.width;
        double scaleY = pfHeight / compSize.height;
        double scale = Math.min(scaleX, scaleY);

        // Limit maximum scaling to 1.0 (no enlargement)
        if (scale > 1.0) {
            scale = 1.0;
        }

        // Apply scale transformation
        g2d.scale(scale, scale);

        // Disable double buffering to avoid blank prints in some JVMs
        componentToPrint.paint(g2d);

        return PAGE_EXISTS;
    }
}
