package view;

import java.awt.Color;
import java.awt.Font;

/**
 * Manages UI colors, typography, styles, and themes (Light Mode / Dark Mode)
 * for the entire Maze Solver application.
 */
public class Theme {
    
    public enum Mode {
        LIGHT, DARK
    }

    private static Mode currentMode = Mode.DARK; // Default is Dark Mode

    // --- COLOR PALETTES ---
    
    // Core Backgrounds
    public static Color getBgColor() {
        return currentMode == Mode.DARK ? new Color(18, 18, 20) : new Color(245, 246, 248);
    }

    public static Color getSidebarColor() {
        return currentMode == Mode.DARK ? new Color(26, 26, 30) : new Color(255, 255, 255);
    }

    public static Color getCardBgColor() {
        return currentMode == Mode.DARK ? new Color(36, 36, 42) : new Color(255, 255, 255);
    }

    public static Color getCardBorderColor() {
        return currentMode == Mode.DARK ? new Color(50, 50, 60, 100) : new Color(220, 224, 230);
    }

    // Text Colors
    public static Color getPrimaryTextColor() {
        return currentMode == Mode.DARK ? new Color(240, 240, 245) : new Color(33, 37, 41);
    }

    public static Color getSecondaryTextColor() {
        return currentMode == Mode.DARK ? new Color(160, 160, 175) : new Color(108, 117, 125);
    }

    // Accents & Buttons
    public static Color getAccentColor() {
        return currentMode == Mode.DARK ? new Color(98, 0, 238) : new Color(103, 58, 183); // Purple
    }

    public static Color getAccentHoverColor() {
        return currentMode == Mode.DARK ? new Color(119, 34, 255) : new Color(126, 87, 194);
    }

    public static Color getSecondaryAccentColor() {
        return currentMode == Mode.DARK ? new Color(3, 218, 198) : new Color(0, 150, 136); // Teal
    }

    // Maze Visuals
    public static Color getWallColor() {
        return currentMode == Mode.DARK ? new Color(45, 45, 52) : new Color(190, 195, 202);
    }

    public static Color getPathColor() {
        return currentMode == Mode.DARK ? new Color(24, 24, 28) : new Color(255, 255, 255);
    }

    public static Color getStartCellColor() {
        return new Color(66, 133, 244); // Blue
    }

    public static Color getEndCellColor() {
        return new Color(234, 67, 53); // Red
    }

    public static Color getVisitedCellColor() {
        return currentMode == Mode.DARK ? new Color(98, 0, 238, 70) : new Color(227, 242, 253);
    }

    public static Color getDeadEndCellColor() {
        return currentMode == Mode.DARK ? new Color(234, 67, 53, 50) : new Color(255, 205, 210);
    }

    public static Color getCorrectPathCellColor() {
        return new Color(15, 157, 88); // Green
    }

    // --- FONTS ---
    public static Font getTitleFont(int size) {
        return new Font("Segoe UI", Font.BOLD, size);
    }

    public static Font getSemiboldFont(int size) {
        return new Font("Segoe UI", Font.BOLD, size); // Note: Java's standard Font doesn't have SEMIBOLD, so we use BOLD.
    }

    public static Font getBodyFont(int size) {
        return new Font("Segoe UI", Font.PLAIN, size);
    }

    public static Font getMonospaceFont(int size) {
        return new Font("Consolas", Font.PLAIN, size);
    }

    // --- UTILITIES ---
    public static Mode getMode() {
        return currentMode;
    }

    public static void setMode(Mode mode) {
        currentMode = mode;
    }

    public static void toggleMode() {
        currentMode = (currentMode == Mode.DARK) ? Mode.LIGHT : Mode.DARK;
    }
}
