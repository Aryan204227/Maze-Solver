package algorithm;

import model.CellType;
import model.Position;

/**
 * Represents a single transition step in the maze solving process.
 * Used for animation playback, step-by-step navigation, and backtracking visualization.
 */
public class SolvingStep {
    private final Position position;
    private final CellType newType;
    private final int stepIndex;
    private final int recursionDepth;
    private final String description;

    /**
     * Constructs a new SolvingStep.
     *
     * @param position       the cell position
     * @param newType        the new state/type the cell transitioned to
     * @param stepIndex      the chronological step index
     * @param recursionDepth the recursion stack depth at this step
     * @param description    a user-friendly description of this action
     */
    public SolvingStep(Position position, CellType newType, int stepIndex, int recursionDepth, String description) {
        this.position = position;
        this.newType = newType;
        this.stepIndex = stepIndex;
        this.recursionDepth = recursionDepth;
        this.description = description;
    }

    public Position getPosition() {
        return position;
    }

    public CellType getNewType() {
        return newType;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public int getRecursionDepth() {
        return recursionDepth;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%d] Depth %d: Cell %s -> %s (%s)", 
            stepIndex, recursionDepth, position, newType, description);
    }
}
