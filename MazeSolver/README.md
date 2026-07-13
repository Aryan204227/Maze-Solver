# Maze Solver - DFS Backtracking & Recursion Visualizer

A premium, interactive desktop application built in **Core Java** using **Swing** and **AWT**. This project visualizes the classic maze-solving problem using recursive Depth-First Search (DFS) and backtracking, tracking single paths, locating shortest routes, counting solutions, and measuring performance.

## 🚀 Key Features

*   **Home Dashboard**: Display statistics (grid configuration, execution times, solution count) and custom-drawn line graphs of recursion profiles.
*   **Maze Editor**: Interactive canvas allowing users to brush walls, erase barriers, set Start/End cells, and resize the grid dynamically.
*   **Auto-Generators**:
    *   *Randomized DFS*: Generates perfect mazes with single-solution paths.
    *   *Noise Density*: Spawns wall grids based on statistical probabilities.
*   **Solver Visualizer**:
    *   *Step-by-Step Playback*: Play, pause, step forward, step backward, or instantly solve.
    *   *Visual Indicators*: Color-coded visited cells, dead ends, and correct paths.
    *   *Execution Log*: Real-time DFS state stack tracker with recursion depth.
*   **Visual Customizations**:
    *   *Light & Dark Themes*: Easily switch themes with glassmorphism-inspired components.
    *   *Zoom & Panning*: Zoom in/out from 0.5x to 2.5x with responsive scroll panes.
*   **Utility Tools**: Export text-based reports, capture high-res PNG screenshots of the grid, or print directly onto paper.

---

## 📁 File Structure

```text
MazeSolver/
├── src/
│   ├── Main.java              # Entry point
│   ├── model/
│   │   ├── CellType.java      # Enum for cell states
│   │   ├── Position.java      # Coordinate tracker
│   │   ├── Maze.java          # Grid model representation
│   │   ├── SolveStatistics.java # Statistics tracker
│   │   └── InvalidMazeException.java # Custom exception
│   ├── controller/
│   │   └── MazeController.java # Application orchestrator
│   ├── algorithm/
│   │   ├── BacktrackingSolver.java # DFS solving logic
│   │   ├── SolvingStep.java   # Playback step record
│   │   └── RandomMazeGenerator.java # Generation algorithms
│   ├── view/
│   │   ├── Theme.java         # Palette and font configs
│   │   ├── SplashScreen.java  # Loading screen
│   │   ├── MainFrame.java     # Sidebar navigation structure
│   │   ├── DashboardPanel.java # Analytics home panel
│   │   ├── MazeEditorPanel.java # Canvas drawing panel
│   │   ├── MazeVisualizerPanel.java # Animated solver panel
│   │   ├── StatsPanel.java    # Efficiency dial charts
│   │   └── components/
│   │       ├── RoundedButton.java # Styled buttons
│   │       ├── RoundedPanel.java  # Styled container cards
│   │       ├── ModernSlider.java  # Custom slider tracks
│   │       └── CustomDialog.java  # Modern custom dialog modals
│   └── utils/
│       ├── FileHandler.java   # Save/Load and text exports
│       ├── ImageExporter.java # Screenshot captures
│       └── SystemPrinter.java  # Paper printer interface
├── report/
│   ├── project_report.md      # Comprehensive Academic Report
│   ├── viva_questions.md      # 50 Viva QA + Interview questions
│   └── presentation_slides.md  # 15-Slide Presentation guide
├── README.md                  # Project overview
└── LICENSE                    # MIT License
```

---

## 🛠️ Installation & Running Guide

### Prerequisites
*   Java Development Kit (JDK) 8 or higher.
*   Command-line access (terminal/cmd).

### Compiling
Navigate to the root directory `MazeSolver/` and compile all java source files:
```bash
javac -d bin -sourcepath src src/Main.java
```

### Running
Execute the compiled byte-code:
```bash
java -cp bin Main
```

---

## 💡 How to Use

1.  **Dashboard**: Launching the app plays the **Splash Screen** and opens the **Dashboard** displaying current stats.
2.  **Edit Grid**: Switch to **Maze Editor** from the left sidebar. Drag the mouse to draw walls, select "Erase Walls" to clear them, or move the Start (Blue) and End (Red) nodes.
3.  **Run Solver**: Switch to **Solver Visualizer**. Click **Run DFS Solver** to solve. 
4.  **Control Animation**: Click **Play** to watch the solver step through the cells, or adjust the **Delay** slider to speed up/slow down. You can also use **Step Forward** and **Step Backward** to inspect decision branches.
5.  **Save & Export**: Use the toolbar buttons to save configurations to `.maze` files, export reports to `.txt`, or print.
