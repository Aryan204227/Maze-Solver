package com.mazesolver.util;

import java.awt.*;

/**
 * ThemeManager.java — VERSION 4.0 PREMIUM
 * ─────────────────────────────────────────────────────────────────
 * Provides the global dark / light color palette for the entire app.
 *
 * WHAT CHANGED IN V4.0:
 *   - Deeper, richer "Obsidian" dark background (true OLED-style blacks)
 *   - Upgraded card colors with subtle blue tint in dark mode
 *   - Added getAccentPink() and getAccentCyan() for more vibrant HUD elements
 *   - Added getGlassOverlay() for glassmorphic card surfaces
 *   - Added getShadowColor() for drop-shadow simulation
 *   - Light theme is warmer and more legible
 */
public class ThemeManager {

    private static boolean darkMode = true;

    // ── Static Cell Colors (same in both themes) ──────────
    public static final Color CELL_WALL      = new Color(0x21262D);
    public static final Color CELL_START     = new Color(0x2EA043);
    public static final Color CELL_END       = new Color(0xF85149);
    public static final Color CELL_CURRENT   = new Color(0x388BFD);
    public static final Color CELL_VISITED   = new Color(0x7C3AED);
    public static final Color CELL_BACKTRACK = new Color(0xE3B341);
    public static final Color CELL_SOLUTION  = new Color(0x22C55E);
    public static final Color CELL_DEAD_END  = new Color(0xDC2626);

    public static boolean isDarkMode() { return darkMode; }
    public static void toggleTheme()   { darkMode = !darkMode; }

    // ── Background Colors (V4.0 Obsidian Dark) ────────────
    public static Color getBgPrimary() {
        // Deepest background — near-black with very slight blue tint
        return darkMode ? new Color(0x080C12) : new Color(0xFAFBFC);
    }
    public static Color getBgSecondary() {
        return darkMode ? new Color(0x0D1117) : new Color(0xF0F2F5);
    }
    public static Color getBgTertiary() {
        return darkMode ? new Color(0x161B22) : new Color(0xE8ECF0);
    }
    public static Color getBgCard() {
        // Card surface — slightly lifted from bg
        return darkMode ? new Color(0x1C2230) : new Color(0xFFFFFF);
    }
    public static Color getBgCardHover() {
        return darkMode ? new Color(0x22293A) : new Color(0xF4F6FF);
    }

    // ── Text Colors ───────────────────────────────────────
    public static Color getTextPrimary() {
        return darkMode ? new Color(0xECF0F6) : new Color(0x1A1F2C);
    }
    public static Color getTextSecondary() {
        return darkMode ? new Color(0x8D96A8) : new Color(0x57606A);
    }
    public static Color getTextMuted() {
        return darkMode ? new Color(0x4A5160) : new Color(0x9CA3AF);
    }

    // ── Accent Colors ─────────────────────────────────────
    public static Color getAccentBlue() {
        return darkMode ? new Color(0x4D9EFF) : new Color(0x0969DA);
    }
    public static Color getAccentGreen() {
        return darkMode ? new Color(0x22C55E) : new Color(0x16A34A);
    }
    public static Color getAccentOrange() {
        return darkMode ? new Color(0xFB923C) : new Color(0xEA580C);
    }
    public static Color getAccentYellow() {
        return darkMode ? new Color(0xFBBF24) : new Color(0xD97706);
    }
    public static Color getAccentPurple() {
        return darkMode ? new Color(0xA78BFA) : new Color(0x7C3AED);
    }
    public static Color getAccentPink() {
        // New in V4.0 — for HUD highlights and solution path accents
        return darkMode ? new Color(0xF472B6) : new Color(0xDB2777);
    }
    public static Color getAccentCyan() {
        // New in V4.0 — used in learning mode narrator highlights
        return darkMode ? new Color(0x22D3EE) : new Color(0x0891B2);
    }
    public static Color getAccentRed() {
        return darkMode ? new Color(0xF87171) : new Color(0xDC2626);
    }

    // ── UI / Border Colors ────────────────────────────────
    public static Color getBorderColor() {
        return darkMode ? new Color(0x2A3140) : new Color(0xCDD5DF);
    }
    public static Color getBorderStrong() {
        // Stronger border for active / focused cards
        return darkMode ? new Color(0x3D4F6A) : new Color(0xA8B4C0);
    }
    public static Color getSidebarHover() {
        return darkMode ? new Color(0x1E2635) : new Color(0xE4E8F0);
    }
    public static Color getCellOpen() {
        return darkMode ? new Color(0x0D1117) : new Color(0xF6F8FA);
    }

    // ── Glassmorphism Helpers (V4.0 New) ──────────────────
    /**
     * Returns a semi-transparent glass overlay color — used on top of gradient
     * backgrounds to create a frosted-glass card look.
     * @param alpha 0–255
     */
    public static Color getGlassOverlay(int alpha) {
        if (darkMode) {
            return new Color(30, 40, 60, Math.max(0, Math.min(255, alpha)));
        } else {
            return new Color(255, 255, 255, Math.max(0, Math.min(255, alpha)));
        }
    }

    /**
     * Returns a shadow color for drop-shadow simulation under cards.
     */
    public static Color getShadowColor() {
        return darkMode ? new Color(0, 0, 0, 80) : new Color(0, 0, 20, 30);
    }

    // ── Utility ───────────────────────────────────────────
    public static Color withAlpha(Color c, int alpha) {
        alpha = Math.max(0, Math.min(255, alpha));
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    /**
     * Blends two colors by ratio (0.0 = fully c1, 1.0 = fully c2).
     * Useful for smooth hover-tint effects.
     */
    public static Color blend(Color c1, Color c2, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio));
        int r = (int)(c1.getRed()   * (1 - ratio) + c2.getRed()   * ratio);
        int g = (int)(c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int)(c1.getBlue()  * (1 - ratio) + c2.getBlue()  * ratio);
        return new Color(r, g, b);
    }
}
