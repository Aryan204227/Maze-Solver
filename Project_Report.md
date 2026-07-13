# 📄 Project Report: Academic Maze Solver Application

> **Project Title:** Maze Solver  
> **Course:** Data Structures & Algorithms Lab / Object-Oriented Programming  
> **Technology Stack:** Core Java, Java Swing, Java AWT  

---

## 📝 Abstract
The **Maze Solver** application is a desktop utility designed to generate random mazes and solve them using Depth-First Search (DFS) and recursive Backtracking. The software provides a real-time visualization of the pathfinding process, showing explore and backtrack steps as they occur. Built with pure Java Swing and AWT, the application adheres to the Model-View-Controller (MVC) architecture, offering a clean, responsive interface without external dependencies.

---

## 1. Introduction
Pathfinding is a fundamental problem in computer science with applications in robotics, gaming, and network routing. This project demonstrates these concepts by visualizing a maze solver.

### 1.1 Objectives
- Implement a maze generator using the Randomized Recursive Backtracker algorithm.
- Implement a solver using Depth-First Search (DFS) and recursive Backtracking to find all paths.
- Provide a responsive UI that displays the solving process step-by-step.
- Track performance metrics, including search depth, total execution time, and visited cells.

---

## 2. Requirements Specification

### 2.1 Functional Requirements
- **Dashboard:** A welcome screen introducing the application features.
- **Random Maze Generator:** Creates solvable mazes automatically.
- **Maze Editor:** Allows users to add or remove walls manually by clicking or dragging.
- **Visualizer:** Shows step-by-step exploring (blue), backtracking (orange), and solution paths (green).
- **Speed Slider:** Adjusts visualization delays from 5ms to 500ms.
- **Save/Load:** Exports or imports maze layouts using a `.maze` file format.
- **Theme Manager:** Toggles between Dark and Light color themes.

### 2.2 Non-Functional Requirements
- **Performance:** Solving animations run on a background thread, keeping the GUI responsive.
- **Extensibility:** Modularity makes it easy to add other algorithms in the future.
- **Simplicity:** Written using basic Java concepts suitable for student evaluations.

---

## 3. System Design & Architecture

The project uses the **Model-View-Controller (MVC)** design pattern to separate logic from representation.

### 3.1 MVC Component Breakdown
- **Model:** Classes like `Cell.java`, `Maze.java`, and `SolverStats.java` manage state and coordinate data.
- **View:** Classes in the `view/` package handle layout rendering, drawing, and color themes.
- **Controller:** `AppController` and `MazeController` process user input, manage execution threads, and coordinate model updates.

### 3.2 Threading Architecture
To prevent UI freezes, the application uses two threads:
1. **Event Dispatch Thread (EDT):** Manages user interactions and UI repaints.
2. **Worker Thread:** Executes the recursive solving algorithm, sleeping to animate steps and posting updates to the EDT via `SwingUtilities.invokeLater()`.

---

## 4. Algorithm Description

### 4.1 Maze Generation (Recursive Backtracker)
1. Initialize the grid with all cells set to `WALL`.
2. Start at the top-left cell, marking it as `OPEN` and `visited`.
3. Pick a random neighbor cell 2 units away that has not been visited.
4. Carve a path by setting both the neighbor and the cell between them to `OPEN`.
5. Recurse from the neighbor cell.
6. When no unvisited neighbors remain, backtrack to the previous cell.
7. Repeat until all cells have been visited.

### 4.2 Maze Solving (DFS + Backtracking)
```
solveRecursive(row, col):
    if current cell is END:
        Save path to solutions list
        Increment total solutions
        Backtrack to find other paths
        return

    if cell is out of bounds, a WALL, or already visited:
        return

    Mark current cell as visited
    Add cell to current path

    For each direction (Down, Right, Up, Left):
        solveRecursive(neighbor)

    Remove cell from current path
    Unmark cell as visited (Backtrack)
```

---

## 5. Implementation Details

The project is structured into modular packages:
- `com.mazesolver.model`: Holds structural coordinate data.
- `com.mazesolver.algorithm`: Houses path carving and solving logic.
- `com.mazesolver.controller`: Links algorithm triggers with GUI controls.
- `com.mazesolver.view`: Renders panels, dialogs, and themes.
- `com.mazesolver.util`: Manages global constants and color themes.

---

## 6. Testing & Results

### 6.1 Test Scenarios

| Test Case | Description | Input / Action | Expected Result | Status |
|---|---|---|---|---|
| TC-01 | Resize Grid | Select "10x10" from dropdown | Maze grid resizes and resets start/end positions | Pass |
| TC-02 | Generate Maze | Click "Generate" | A solvable maze is generated | Pass |
| TC-03 | Draw Walls | Drag mouse over cells in Edit mode | Dragged cells turn into walls | Pass |
| TC-04 | Stop at Walls | Click "Solve" on blocked maze | Solver stops at walls and backtracks | Pass |
| TC-05 | No Solution | Block the end cell and solve | Solver completes and displays a "No Solutions" popup | Pass |
| TC-06 | Save & Load | Save maze, clear grid, and load | The saved wall configuration is restored | Pass |
| TC-07 | Theme Switch | Click "Theme Toggle" | UI colors switch instantly | Pass |

---

## 7. Conclusion
The **Maze Solver** application implements recursive pathfinding and maze generation algorithms in an interactive Java desktop application. By using standard Java Swing APIs, the MVC design pattern, and clean multithreading, it demonstrates fundamental software engineering practices in a simple, readable package.
