# 🧪 Testing Report: Maze Solver Application

This document outlines the testing strategy, test cases, and verification results for the **Maze Solver** application.

---

## 1. Testing Strategy

We used both automated unit testing concepts (during development) and manual functional testing to verify the application:
1. **Algorithmic Correctness:** Validated that DFS and backtracking explore all paths and find the shortest solution.
2. **UI & Theme Responsiveness:** Verified layout sizing, card navigation, and dark/light mode switches.
3. **Robustness & Edge Cases:** Tested handling of fully blocked mazes, large grids, and invalid file imports.

---

## 2. Test Cases and Verification

### 2.1 Maze Generation Tests

#### Test Case 1: Solvability of Random Mazes
*   **Method:** Click **Generate** 20 times on different grid sizes (5x5, 15x15, 25x25).
*   **Expected Result:** Every generated maze has at least one valid path from Start (top-left) to End (bottom-right).
*   **Actual Result:** Pass. Every generated maze was solvable.

#### Test Case 2: Randomized Configurations
*   **Method:** Click **Generate** consecutively.
*   **Expected Result:** Each generation creates a unique wall layout.
*   **Actual Result:** Pass. The wall layouts are randomized.

---

### 2.2 Editor Interaction Tests

#### Test Case 3: Grid Click & Drag Wall Painting
*   **Method:** Click **Edit** to enter edit mode, click and drag the mouse across cells, and verify results.
*   **Expected Result:** Cells change state to walls on drag, excluding start/end corners.
*   **Actual Result:** Pass. Walls are drawn on drag, and start/end coordinates are protected.

#### Test Case 4: Wall Toggling
*   **Method:** Click a cell to set it as a wall, then click it again.
*   **Expected Result:** The first click creates a wall; the second click removes it.
*   **Actual Result:** Pass. Cells toggle correctly.

---

### 2.3 Solver Execution Tests

#### Test Case 5: Pathfinding in Blocked Mazes
*   **Method:** Block the start or end cell completely, then click **Solve**.
*   **Expected Result:** Solver executes, explores all reachable cells, terminates without errors, and displays a "No Solutions" warning popup.
*   **Actual Result:** Pass. No solutions are found, and the popup displays correctly.

#### Test Case 6: Real-time Stats Updates
*   **Method:** Run the solver and observe the Stats panel.
*   **Expected Result:** Time, Visited Nodes, Recursive Calls, and Max Depth update dynamically.
*   **Actual Result:** Pass. Stats update in real-time.

---

### 2.4 Persistence (File I/O) Tests

#### Test Case 7: Save Maze Layout
*   **Method:** Click **Save**, choose a destination, and enter a filename.
*   **Expected Result:** A `.maze` configuration file is written to disk with correct dimensions and wall states.
*   **Actual Result:** Pass. File contents match the expected format.

#### Test Case 8: Load Maze Layout
*   **Method:** Load a saved `.maze` file.
*   **Expected Result:** The application loads the file, updates the grid dimensions, and draws the saved walls.
*   **Actual Result:** Pass. Saved layouts are restored correctly.

---

### 2.5 UI & Theme Tests

#### Test Case 9: Theme Mode Toggle
*   **Method:** Click **Dark Mode / Light Mode** in the sidebar.
*   **Expected Result:** Backgrounds, card borders, fonts, and grid cells change to the selected theme immediately.
*   **Actual Result:** Pass. Theme changes apply instantly.

#### Test Case 10: Dynamic Sizing
*   **Method:** Resize the main application window.
*   **Expected Result:** Panels adjust their sizes, and the maze grid scales to fit the center panel.
*   **Actual Result:** Pass. Layout is fully responsive.
