package view.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Theme - Central color and font registry for the entire application.
 *
 * WHY THIS CLASS EXISTS:
 *   Instead of hardcoding colors everywhere, all colors are defined here.
 *   This makes it trivial to switch between Dark and Light themes.
 *   Every other class calls Theme.getBg(), Theme.getText(), etc.
 *
 * JAVA CONCEPTS USED: Static methods, Enum, Constants
 */
public class Theme {

    public enum Mode { DARK, LIGHT }

    private static Mode currentMode = Mode.DARK;

    // ---- DARK THEME COLORS ----
    private static final Color D_BG         = new Color(13, 17, 23);
    private static final Color D_SIDEBAR    = new Color(22, 27, 34);
    private static final Color D_CARD       = new Color(33, 38, 45);
    private static final Color D_CARD2      = new Color(40, 46, 55);
    private static final Color D_BORDER     = new Color(48, 54, 61);
    private static final Color D_TEXT       = new Color(230, 237, 243);
    private static final Color D_TEXT2      = new Color(139, 148, 158);

    // ---- LIGHT THEME COLORS ----
    private static final Color L_BG         = new Color(250, 251, 252);
    private static final Color L_SIDEBAR    = new Color(246, 248, 250);
    private static final Color L_CARD       = new Color(255, 255, 255);
    private static final Color L_CARD2      = new Color(246, 248, 250);
    private static final Color L_BORDER     = new Color(208, 215, 222);
    private static final Color L_TEXT       = new Color(31, 35, 40);
    private static final Color L_TEXT2      = new Color(101, 109, 118);

    // ---- ACCENT COLORS (same for both themes) ----
    public static final Color BLUE          = new Color(88, 166, 255);
    public static final Color BLUE_DARK     = new Color(58, 130, 220);
    public static final Color GREEN         = new Color(63, 185, 80);
    public static final Color RED           = new Color(248, 81, 73);
    public static final Color PURPLE        = new Color(188, 140, 255);
    public static final Color ORANGE        = new Color(251, 189, 8);
    public static final Color TEAL          = new Color(56, 189, 248);

    // ---- CELL COLORS ----
    public static final Color CELL_WALL     = new Color(13, 17, 23);
    public static final Color CELL_PATH     = new Color(240, 244, 248);
    public static final Color CELL_PATH_DK  = new Color(48, 54, 61);
    public static final Color CELL_START    = new Color(56, 189, 248);
    public static final Color CELL_END      = new Color(251, 189, 8);
    public static final Color CELL_VISITED  = new Color(109, 67, 201);
    public static final Color CELL_FOUND    = new Color(63, 185, 80);
    public static final Color CELL_DEAD     = new Color(248, 81, 73);

    // ---- MODE CONTROLS ----
    public static Mode getMode()    { return currentMode; }
    public static boolean isDark()  { return currentMode == Mode.DARK; }
    public static void toggleMode() {
        currentMode = (currentMode == Mode.DARK) ? Mode.LIGHT : Mode.DARK;
    }

    // ---- THEME-AWARE COLOR ACCESSORS ----
    public static Color getBg()         { return isDark() ? D_BG : L_BG; }
    public static Color getSidebar()    { return isDark() ? D_SIDEBAR : L_SIDEBAR; }
    public static Color getCard()       { return isDark() ? D_CARD : L_CARD; }
    public static Color getCard2()      { return isDark() ? D_CARD2 : L_CARD2; }
    public static Color getBorder()     { return isDark() ? D_BORDER : L_BORDER; }
    public static Color getText()       { return isDark() ? D_TEXT : L_TEXT; }
    public static Color getText2()      { return isDark() ? D_TEXT2 : L_TEXT2; }
    public static Color getCellPath()      { return isDark() ? CELL_PATH_DK : CELL_PATH; }
    public static Color getCellWall()      { return CELL_WALL; }
    // Aliases used by view panels
    public static Color getBackground()    { return getBg(); }
    public static Color getSecondaryText() { return getText2(); }

    // ---- FONTS ----
    public static Font titleFont()   { return new Font("Segoe UI", Font.BOLD, 22); }
    public static Font headingFont() { return new Font("Segoe UI", Font.BOLD, 15); }
    public static Font bodyFont()    { return new Font("Segoe UI", Font.PLAIN, 14); }
    public static Font smallFont()   { return new Font("Segoe UI", Font.PLAIN, 12); }
    public static Font monoFont()    { return new Font("Consolas", Font.PLAIN, 12); }
    public static Font boldFont(int size) { return new Font("Segoe UI", Font.BOLD, size); }
    // Font constants used by view panels
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_BODY  = new Font("Segoe UI", Font.PLAIN, 14);
}
