package com.mazesolver;

import com.mazesolver.view.MainWindow;
import com.mazesolver.view.SplashScreen;

import javax.swing.*;

/**
 * Main.java
 * ─────────────────────────────────────────────────────
 * The application main entry point class.
 *
 * WHY THIS CLASS EXISTS:
 *   Initiates program execution. Spawns the startup splash loader,
 *   prepares look and feel options, and schedules MainWindow displays on the EDT.
 *
 * JAVA CONCEPTS USED:
 *   - Look and Feel initialization
 *   - EDT thread scheduling (SwingUtilities.invokeLater)
 */
public class Main {

    public static void main(String[] args) {
        // Use system look and feel for premium rendering integrations
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Default swing styles fallback gracefully
        }

        // Run UI initializations on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen(() -> {
                MainWindow mainWin = new MainWindow();
                mainWin.setVisible(true);
            });
            splash.showSplash();
        });
    }
}
