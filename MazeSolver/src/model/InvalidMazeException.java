package model;

/**
 * Exception thrown when a maze is determined to be invalid (e.g. missing start/end positions,
 * bad dimensions, or empty file configurations).
 */
public class InvalidMazeException extends Exception {
    
    /**
     * Constructs a new InvalidMazeException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidMazeException(String message) {
        super(message);
    }
}
