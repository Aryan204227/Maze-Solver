package com.mazesolver.util;

import java.awt.Dimension;
import java.awt.Font;

/**
 * Constants.java
 * ─────────────────────────────────────────────────────
 * Stores all app-wide constant values in one place.
 * This avoids "magic numbers" scattered across the code.
 *
 * WHY THIS CLASS EXISTS:
 *   Centralizing constants makes it easy to change a value
 *   (like window size) in one place instead of hunting for
 *   it in 10 different files.
 *
 * JAVA CONCEPT USED: static final fields (constants)
 */
public class Constants {

    // ── Window ────────────────────────────────────────────
    public static final int    WINDOW_WIDTH      = 1400;
    public static final int    WINDOW_HEIGHT     = 840;
    public static final String APP_TITLE         = "Maze Solver  —  V2.0  |  DFS + Backtracking Visualizer";
    public static final String APP_VERSION       = "2.0";
    public static final String APP_AUTHOR        = "University Project";

    // ── Maze Grid ─────────────────────────────────────────
    public static final int DEFAULT_ROWS         = 15;
    public static final int DEFAULT_COLS         = 15;
    public static final int MIN_GRID_SIZE        = 5;
    public static final int MAX_GRID_SIZE        = 30;

    // ── Animation ─────────────────────────────────────────
    public static final int DEFAULT_DELAY_MS     = 60;   // ms between each step
    public static final int MIN_DELAY_MS         = 0;
    public static final int MAX_DELAY_MS         = 500;

    // ── Solutions ─────────────────────────────────────────
    /** Safety cap so app doesn't freeze on very open mazes */
    public static final int MAX_SOLUTIONS        = 50;

    // ── Layout ────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH        = 220;
    public static final int STATS_PANEL_WIDTH    = 265;
    public static final int CONTROL_PANEL_HEIGHT = 90;
    public static final int STATUS_BAR_HEIGHT    = 32;

    // ── Fonts (Segoe UI looks beautiful on Windows) ───────
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING   = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_SUBHEADING= new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO      = new Font("Consolas",  Font.PLAIN, 12);
    public static final Font FONT_ICON      = new Font("Segoe UI Emoji", Font.PLAIN, 16);

    // ── File I/O ──────────────────────────────────────────
    public static final String FILE_EXTENSION   = ".maze";
    public static final String FILE_DESCRIPTION = "Maze Files (*.maze)";

    // ── Corner Radius ─────────────────────────────────────
    public static final int RADIUS_BUTTON   = 10;
    public static final int RADIUS_CARD     = 14;
    public static final int RADIUS_PANEL    = 18;

    // ── Splash Screen ─────────────────────────────────────
    public static final int SPLASH_WIDTH    = 520;
    public static final int SPLASH_HEIGHT   = 320;
    public static final int SPLASH_DURATION = 2500; // ms

    // Prevent instantiation — this is a utility class
    private Constants() {}
}
