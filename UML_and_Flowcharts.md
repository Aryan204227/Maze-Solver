# 📊 UML Class Diagram & Algorithm Flowcharts

This document provides system design diagrams, including a UML class diagram and flowcharts for the solving and generation algorithms.

---

## 1. UML Class Diagram

Copy the Mermaid code below into any Mermaid renderer (like [mermaid.live](https://mermaid.live)) to display the interactive diagram.

```mermaid
classDiagram
    direction TB
    
    %% Models
    class Cell {
        -int row
        -int col
        -State state
        +isWall() boolean
        +isWalkable() boolean
        +isEndpoint() boolean
    }
    
    class Maze {
        -int rows
        -int cols
        -Cell[][] grid
        -int startRow
        -int startCol
        -int endRow
        -int endCol
        +toggleWall(row, col)
        +resetSolvingState()
        +resize(rows, cols)
    }
    
    class MazeSolution {
        -int solutionNumber
        -List~int[]~ path
        +addStep(row, col)
        +getLength() int
    }
    
    class SolverStats {
        -long startTimeMs
        -long executionTimeMs
        -int visitedNodes
        -int recursiveCalls
        -int maxDepth
        -int totalSolutions
        +reset()
        +startTimer()
        +stopTimer()
    }

    %% Algorithms
    class MazeSolver {
        -Maze maze
        -SolverStats stats
        -AnimationTimer timer
        -SolveCallback callback
        -boolean[][] visited
        +solve()
        -solveRecursive(row, col, path, depth)
    }

    class MazeGenerator {
        -Maze maze
        -Random random
        +generate()
        -carvePath(row, col, visited)
    }

    %% Interfaces
    class SolveCallback {
        <<interface>>
        +onCellVisited(row, col)
        +onBacktrack(row, col)
        +onSolutionFound(solution)
        +onComplete(stats, solutions)
    }

    %% Controllers
    class AppController {
        -MainWindow window
        -SidebarPanel sidebar
        -MazePanel mazePanel
        -ControlPanel controlPanel
        -StatsPanel statsPanel
        -StatusBar statusBar
        -MazeController mazeController
        +onNavigate(page)
        +onSolve()
    }
    
    class MazeController {
        -MazePanel mazePanel
        -StatsPanel statsPanel
        -StatusBar statusBar
        -Maze maze
        -SolverStats stats
        -AnimationTimer timer
        +solveMaze()
        +pauseSolving()
        +stopSolvingForcefully()
    }

    %% Utility
    class ThemeManager {
        -boolean darkMode
        +toggleTheme()
        +getBgPrimary() Color
        +getBgSecondary() Color
    }

    %% Relationships
    Maze "1" *-- "many" Cell : contains
    MazeSolver ..> SolveCallback : notifies
    MazeController ..|> SolveCallback : implements
    AppController --> MazeController : delegates to
    MazeController --> Maze : manages
    MazeSolver --> Maze : reads/writes
    MazeGenerator --> Maze : carves
    MazeSolver --> SolverStats : updates
    MazeSolver --> MazeSolution : creates
```

---

## 2. Algorithm Flowcharts

### 2.1 DFS + Backtracking Solver Flowchart

```mermaid
graph TD
    Start([Start solverThread]) --> Init[Initialize visited array & reset stats]
    Init --> Call[Call solveRecursive(startRow, startCol)]
    
    %% Recursive Function
    Call --> GuardCheck{Is Cell out of bounds, WALL, or already visited?}
    GuardCheck -- Yes --> Backtrack[Return / Backtrack]
    GuardCheck -- No --> Mark[Mark Cell visited & update stats]
    
    Mark --> UIUpdate[Notify UI: Visited Blue]
    UIUpdate --> SpeedDelay[Sleep for speed delay]
    
    SpeedDelay --> EndCheck{Is Cell END?}
    EndCheck -- Yes --> SaveSol[Save Solution & Highlight Green]
    SaveSol --> ExploreRemaining[Backtrack to explore remaining directions]
    
    EndCheck -- No --> LoopDirs[Loop 4 directions: Down, Right, Up, Left]
    LoopDirs --> Recurse[Recurse: Call solveRecursive on neighbor]
    
    Recurse --> LoopDirs
    LoopDirs -- All directions checked --> Unmark[Unmark visited & Remove from path]
    Unmark --> UIBacktrack[Notify UI: Backtrack Orange]
    UIBacktrack --> Return[Return]
    
    ExploreRemaining --> Unmark
    Return --> End([Complete & Highlight Shortest Path])
```

### 2.2 Maze Generation (Recursive Backtracker) Flowchart

```mermaid
graph TD
    Start([Start Generation]) --> Fill[Fill entire grid with WALLS]
    Fill --> Call[Call carvePath(0, 0)]
    
    Call --> Mark[Mark current cell as OPEN & visited]
    Mark --> Neighbors{Are there unvisited neighbors 2 steps away?}
    
    Neighbors -- Yes --> Choose[Choose random unvisited neighbor]
    Choose --> Carve[Carve passage: Set neighbor and intermediate cell to OPEN]
    Carve --> Recurse[Recurse: Call carvePath on neighbor]
    Recurse --> Neighbors
    
    Neighbors -- No --> Backtrack[Return / Backtrack]
    Backtrack --> End([End Generation: Add random loop openings])
```
