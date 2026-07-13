package com.mazesolver.util;

/**
 * AnimationTimer.java
 * ─────────────────────────────────────────────────────
 * Controls the speed and state of the maze solving animation.
 *
 * WHY THIS CLASS EXISTS:
 *   The MazeSolver runs in a background thread.
 *   This class acts as a "remote control" that the UI can
 *   use to pause, resume, or cancel the solver at any time.
 *
 * HOW IT WORKS:
 *   - The solver thread calls waitIfPaused() at each step.
 *   - If paused=true, it loops and sleeps until paused=false.
 *   - If cancelled=true, the solver stops immediately.
 *   - volatile keyword ensures thread-safe visibility.
 *
 * JAVA CONCEPTS USED:
 *   - volatile for thread visibility
 *   - Thread.sleep() for pausing
 *   - Encapsulation
 */
public class AnimationTimer {

    private volatile int     delayMs;    // sleep time between each recursive step
    private volatile boolean paused;     // true = solver is paused
    private volatile boolean cancelled;  // true = solver must stop

    public AnimationTimer(int initialDelayMs) {
        this.delayMs   = initialDelayMs;
        this.paused    = false;
        this.cancelled = false;
    }

    // ── Speed Control ─────────────────────────────────────

    /** Returns the current animation delay in milliseconds */
    public int getDelay() { return delayMs; }

    /** Sets the delay (0 = fastest, 500 = slowest) */
    public void setDelay(int delayMs) {
        this.delayMs = Math.max(0, delayMs);
    }

    // ── Pause / Resume ────────────────────────────────────

    public void pause()  { this.paused = true;  }
    public void resume() { this.paused = false; }
    public boolean isPaused() { return paused; }

    // ── Cancel ────────────────────────────────────────────

    public void cancel()    { this.cancelled = true; }
    public boolean isCancelled() { return cancelled; }

    // ── Reset (before each new solve) ─────────────────────

    public void reset() {
        this.paused    = false;
        this.cancelled = false;
    }

    /**
     * Called by the solver thread at each step.
     * If paused, this method blocks until resumed or cancelled.
     */
    public void waitIfPaused() {
        while (paused && !cancelled) {
            try {
                Thread.sleep(50); // check every 50ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
