# 🧩 Maze Solver — Complete Project Guide & Code Walkthrough

This repository contains a premium desktop-grade **Maze Solver** application written in Core Java using Swing and AWT. It visualizes Depth-First Search (DFS) and recursive Backtracking pathfinding algorithms in real-time.

---

## 🏛️ Class Explanations & Viva Reference Sheet

For every class created in this codebase, here is the official explanation covering why it exists, how it works, which Java concepts are used, and potential viva questions.

### 1. `Main.java`
*   **Why it exists**: It is the main entry point to initiate the application.
*   **How it works**: Sets the system Native Look & Feel to make Swing components look modern, then schedules the display of the animated `SplashScreen` followed by the `MainWindow` on the Event Dispatch Thread (EDT).
*   **Java concepts used**: `SwingUtilities.invokeLater()`, Look & Feel settings, static main method.
*   **Viva Questions**: 
    *   *Why do we call invokeLater()?* Swing is not thread-safe. All GUI changes must run on the Event Dispatch Thread (EDT).
    *   *What is Look and Feel (L&F)?* A Java mechanism that customizes visual styles.

### 2. `Constants.java`
*   **Why it exists**: Stores app-wide configuration constants (sizes, styles, limits) in a single place to prevent "magic numbers" in logic.
*   **How it works**: Declares static final variables that are read by panels and controllers.
*   **Java concepts used**: `static final` fields, private constructor (prevents instantiation).
*   **Viva Questions**:
    *   *Why is the constructor private?* It is a utility class containing only static constants. Instantiating it makes no sense and wastes memory.
    *   *What are static final variables?* `static` means they belong to the class rather than an object. `final` makes them constant (cannot be changed after initialization).

### 3. `ThemeManager.java`
*   **Why it exists**: Powers the app-wide design system. Defines color schemes for both Light and Dark themes.
*   **How it works**: Contains color definitions for different UI states and dynamically returns the correct Color objects based on a theme flag.
*   **Java concepts used**: Static methods, encapsulation, RGB hexadecimal integers.
*   **Viva Questions**:
    *   *How does theme switching work?* We toggle a boolean flag `darkMode` and call `applyTheme()` on all UI components to trigger repaint cycles.
    *   *Why did you use RGB Hex integers?* Hexadecimal notation (`0x0D1117`) maps directly to standard web styling tokens, making the design process easier.

### 4. `Cell.java`
*   **Why it exists**: Represents a single cell coordinate in the maze grid layout.
*   **How it works**: Tracks row/column positions and cell states (WALL, OPEN, START, END, CURRENT, VISITED, BACKTRACK, SOLUTION).
*   **Java concepts used**: Nested `enum`, encapsulation (private variables, public getters/setters).
*   **Viva Questions**:
    *   *Why use an enum for Cell States?* Enums provide compile-time safety. They prevent invalid states (like setting a state to an arbitrary integer).
    *   *What is encapsulation?* Hiding cell states and coordinates behind private access modifiers and exposing them only via getter and setter methods.

### 5. `Maze.java`
*   **Why it exists**: The core data structure representing the 2D grid matrix.
*   **How it works**: Holds a 2D array of `Cell` objects and provides methods to toggle walls, resize dimensions, and clear solver visuals.
*   **Java concepts used**: Multi-dimensional arrays (`Cell[][]`), boundary validations.
*   **Viva Questions**:
    *   *How does grid resizing work?* It instantiates a new 2D array of the new dimensions and recreates cells as open space, keeping the start at top-left and end at bottom-right.
    *   *How are boundaries protected?* Using the `isInBounds()` helper, which validates that coordinates are within `[0, rows-1]` and `[0, cols-1]`.

### 6. `MazeSolver.java`
*   **Why it exists**: The heart of the application containing the recursive DFS and backtracking logic.
*   **How it works**: Recursively visits cells. If it hits a dead end, it backtracks. When it reaches the end cell, it saves the path as a solution and continues searching to find all alternative routes.
*   **Java concepts used**: Recursion, backtracking (implicit stack), multi-threading callbacks, `volatile` flags for animation control.
*   **Viva Questions**:
    *   *How is backtracking implemented?* Through recursion. When a recursive call returns `false` (or finishes exploring), the code automatically pops the call off the JVM call stack, reverts the cell's visited state, and removes it from the current path.
    *   *Why does the solver run in a separate thread?* If it ran on the EDT (Swing thread), `Thread.sleep()` would freeze the entire UI. Running on a background thread allows the UI to stay responsive and animate step-by-step.

### 7. `MazeGenerator.java`
*   **Why it exists**: Auto-generates random mazes so users don't have to draw walls manually.
*   **How it works**: Implements the Recursive Backtracker algorithm. It starts with a grid full of walls, visits cells 2 steps apart, and carves pathways between them.
*   **Java concepts used**: Random number generation, Collections shuffling (`Collections.shuffle`).
*   **Viva Questions**:
    *   *What is a perfect maze?* A maze generated such that there are no loops and there is exactly one path between any two points.
    *   *How do you get multiple solutions?* After generating a perfect maze (1 path), we randomly open ~10% of interior walls. This creates loops, allowing the solver to find multiple valid solutions.

### 8. `MainWindow.java`
*   **Why it exists**: The main frame window container.
*   **How it works**: Instantiates all visual panels and manages layout transitions using Swing's `CardLayout`.
*   **Java concepts used**: `JFrame`, `CardLayout`, `BorderLayout`.
*   **Viva Questions**:
    *   *What is CardLayout?* A layout manager that lets you stack multiple panels on top of each other and show only one at a time (like cards in a deck).
    *   *How is theme updates propagated?* The window calls `applyTheme()` which updates its own background and triggers repaint updates on all children panels.

### 9. `MazePanel.java`
*   **Why it exists**: Custom-draws the maze grid on the screen.
*   **How it works**: Overrides `paintComponent()` to manually draw grid cells, walls, start/end markers, and path animations using Java2D APIs. It also catches mouse clicks and drags to toggle walls.
*   **Java concepts used**: 2D Graphics (`Graphics2D`), mouse event listeners (`MouseAdapter`, `MouseMotionListener`).
*   **Viva Questions**:
    *   *How is responsiveness achieved?* Cell size is calculated dynamically in `getCellSize()` by dividing panel width and height by the number of columns and rows.
    *   *Why did you use MouseAdapter?* It is an abstract class that implements all mouse listener methods as empty bodies. Using it prevents us from writing empty overrides for events we don't need (e.g. mouseMoved, mouseEntered).

---

## 🚀 Compilation & Running Instructions

To compile and execute this university project, open a terminal in the root directory:

### Step 1: Compile the Project
```bash
# Create bin directory for compiled class files
mkdir bin

# Compile all source files using UTF-8 encoding
javac -encoding UTF-8 -d bin -sourcepath src src/com/mazesolver/Main.java
```

### Step 2: Run the Application
```bash
# Execute compiled class files
java -cp bin com.mazesolver.Main
```
