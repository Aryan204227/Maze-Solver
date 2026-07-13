# 50 VIVA QUESTIONS & INTERVIEW PREPARATION GUIDE
## Maze Solver Project Defense Documentation

---

### Part 1: Project Architecture & Core Java Viva Questions (1–25)

#### Q1: What is the main objective of this project?
**Answer**: To solve a 2D maze using recursive DFS and Backtracking, while displaying the execution path, dead ends, correct path, and calculating statistics like execution time, total solution counts, and shortest path.

#### Q2: Why did you choose Swing and AWT instead of newer frameworks like JavaFX or web-based technologies?
**Answer**: Java Swing and AWT are part of the Core Java Standard Library, making the project lightweight, portable, and independent of external runtime wrappers or thick framework dependencies, satisfying university core constraints.

#### Q3: Explain the Model-View-Controller (MVC) design pattern in the context of your project.
**Answer**: 
*   **Model**: Represents grid configuration and cell states (`Maze`, `Position`, `CellType`).
*   **View**: Handles rendering (`MainFrame`, `MazeVisualizerPanel`, `MazeEditorPanel`).
*   **Controller**: Coordinates inputs, updates model, calls algorithms, handles undo/redo, and manages playback timers (`MazeController`).

#### Q4: How is encapsulation used in your data structures?
**Answer**: Core classes like `Maze`, `Position`, and `SolveStatistics` restrict direct field access. Variables are private, exposing access only through public getters, setters, or clean mutation methods (e.g., `setCell()` with bounds checks).

#### Q5: What is the purpose of the `Position` class? Why is it immutable?
**Answer**: `Position` represents 2D coordinates `(row, col)`. Immutability prevents external modifiers from changing a position's coordinates after it has been stored in lists, path arrays, or maps, ensuring hash-code stability.

#### Q6: How do you prevent thread lockups in Swing when running step-by-step solver animations?
**Answer**: The algorithm executes its path search, recording a timeline of `SolvingStep` objects. The visualizer then plays these steps back using a `javax.swing.Timer` which ticks on the Event Dispatch Thread (EDT), keeping the UI responsive.

#### Q7: How does the Undo/Redo mechanism work in the Maze Editor?
**Answer**: The controller maintains two stacks: `undoStack` and `redoStack`. Before any edit change is made to the maze, a cloned copy of the maze is pushed onto `undoStack`. When Undo is called, the current state goes to `redoStack`, and the popped state is restored.

#### Q8: Why does `Maze` implement `Cloneable`?
**Answer**: To support deep cloning of the maze grid. This is essential for the undo/redo stack and to save a clean copy of the maze before solving so the solver doesn't permanently overwrite path cells during step-by-step animation.

#### Q9: What is the difference between `Graphics` and `Graphics2D` in your drawing panels?
**Answer**: `Graphics2D` extends `Graphics` to provide more sophisticated control over geometry, coordinate transformations, color management, and text layout. We cast `Graphics` to `Graphics2D` to enable anti-aliasing rendering hints for rounded buttons and cards.

#### Q10: How do you calculate cell sizes dynamically when rendering the maze?
**Answer**: By dividing the canvas width and height by the number of columns and rows respectively: `int cellSize = Math.min(canvasWidth / cols, canvasHeight / rows)`. This ensures the maze grid scales to fit the panel bounds.

#### Q11: Explain how you implement Zoom in the visualizer.
**Answer**: We multiply the base cell size (e.g., 25px) by a `zoomScale` variable (e.g., `0.5` to `2.5`). When the scale changes, we resize the preferred dimensions of the grid panel and trigger `revalidate()`, forcing the containing `JScrollPane` to update its scroll bars.

#### Q12: How is exception handling utilized in this application?
**Answer**: Custom exceptions like `InvalidMazeException` are thrown when a file is corrupted, empty, has bad dimensions, or lacks start/end points. These are caught in the view layer to trigger modern error dialog popups.

#### Q13: What is the purpose of `System.setProperty("awt.useSystemAAFontSettings", "on")` in `Main.java`?
**Answer**: It forces Java's AWT engine to render text using anti-aliased font smoothing, preventing jagged edges and improving readability on high-DPI screens.

#### Q14: Why do we use custom UI components instead of standard JButtons or JPanels?
**Answer**: Standard Swing components look dated. Custom components like `RoundedButton` override `paintComponent()` to draw custom rounded corners, hover/pressed state gradients, and custom borders to provide a premium design.

#### Q15: How does the `FileHandler` save and load mazes?
**Answer**: It writes/reads plain text. The first lines denote dimensions, start coordinates, and end coordinates. The remaining lines represent the grid layout, using `#` for walls, `.` for paths, `S` for start, and `E` for end.

#### Q16: How do you handle file path validation during loading?
**Answer**: File filters (e.g., `FileNameExtensionFilter` for `.maze`) limit selection, and try-catch blocks capture file-not-found or read errors, prompting details to the user.

#### Q17: What does `repaint()` do in Swing?
**Answer**: It requests the Repaint Manager to schedule a call to the component's `paint()` and `paintComponent()` methods, rendering the updated model state on the screen.

#### Q18: What is a JWindow, and why did you use it for the Splash Screen?
**Answer**: A `JWindow` is a container that does not display title bars, window controls, or taskbar borders. This is ideal for minimal splash screen cards.

#### Q19: How does the `SystemPrinter` connect to Java's printing service?
**Answer**: It implements `java.awt.print.Printable`, scaling the graphical output of the maze component using `g2d.scale()` to fit within the printable bounds of a physical page.

#### Q20: Explain the role of `CardLayout`.
**Answer**: It acts as a card container where only one panel (card) is visible at a time. This allows the sidebar menu to switch pages (Dashboard, Editor, Visualizer, Stats) instantly.

#### Q21: What are layout managers, and which ones did you use?
**Answer**: Layout managers control component sizes and positions. We used `BorderLayout` (for general panel divisions), `GridLayout` (for equal-sized stat blocks), and `GridBagLayout` (for aligned text/labels).

#### Q22: What happens if there is no path between Start and End?
**Answer**: The recursive solver completes exploration of all paths, returns `false` (no path found), logs `0` solutions, and visualizer alerts the user via a custom error dialog.

#### Q23: Why do we set `setOpaque(false)` on custom rounded panels?
**Answer**: To prevent Swing's default rectangular background from painting over the rounded corners of our panel, ensuring transparency outside the border.

#### Q24: What is the purpose of `RenderingHints.KEY_ANTIALIASING`?
**Answer**: It tells the Java rendering engine to smooth out the edges of drawn shapes (lines, circles, text) using sub-pixel rendering.

#### Q25: How do you prevent memory leaks when managing multiple listeners?
**Answer**: We enforce safe registration methods that prevent duplicate listener references: `if (!listeners.contains(listener)) listeners.add(listener)`.

---

### Part 2: Algorithm & Data Structures Viva Questions (26–50)

#### Q26: Define Depth-First Search (DFS) in the context of maze solving.
**Answer**: DFS is an algorithm for traversing tree or graph structures. Starting at the start cell, it explores as far as possible along each branch before backtracking.

#### Q27: What is backtracking?
**Answer**: Backtracking is an algorithmic method for finding all (or some) solutions to computational problems by incrementally building candidates, and abandoning a candidate ("backtracking") as soon as it determines the candidate cannot lead to a valid solution.

#### Q28: How does recursion facilitate backtracking?
**Answer**: The runtime call stack automatically stores the local variables and execution state of each cell step. When a function returns `false`, execution resumes at the previous step's frame, achieving backtracking without manually maintaining a node stack.

#### Q29: What is the base case of your recursive DFS solver?
**Answer**: When the current position equals the end position: `if (current.equals(end)) return true;`.

#### Q30: How do you prevent cycles/infinite loops during DFS recursion?
**Answer**: We use a 2D boolean array `visited[rows][cols]`. Before stepping into a cell, we mark it as `true`. If the algorithm tries to re-enter it, the check fails, preventing loops.

#### Q31: In what order does your solver check neighboring cells?
**Answer**: It checks directions sequentially: Up `(-1, 0)`, Right `(0, 1)`, Down `(1, 0)`, and Left `(0, -1)`.

#### Q32: What is the time complexity of finding a single path using DFS?
**Answer**: In the worst-case, it is $O(R \times C)$ where $R$ is rows and $C$ is columns, as it might visit every cell once.

#### Q33: What is the space complexity of your DFS solver?
**Answer**: $O(R \times C)$ due to the recursion stack depth and the `visited` boolean grid.

#### Q34: What is the maximum recursion depth for a grid size of $21 \times 21$?
**Answer**: In the worst case (a single winding path visiting all cells), the maximum depth is $21 \times 21 = 441$.

#### Q35: How do you count ALL possible solution paths?
**Answer**: We run a full DFS search. When we reach the end node, we increment our solution counter. Instead of returning `true` to terminate, we unmark the cell as visited and return, allowing the recursion to explore remaining paths.

#### Q36: How does the solver find the SHORTEST path?
**Answer**: During the "all paths" search, every time the solver hits the destination, it compares the current path length against the shortest path length recorded so far. If it is shorter, the solver stores a copy of the path.

#### Q37: Why can we not log steps for all paths in the visualizer?
**Answer**: Log lists would grow exponentially in large open grids (millions of states), causing `OutOfMemoryError`. Therefore, we only record steps for the first visual solution path.

#### Q38: How do you cap the "all-paths" search to prevent application lockup?
**Answer**: We restrict the search to a maximum of 5,000 solutions and set a timeout threshold of 3 seconds. If exceeded, the search terminates safely and outputs current statistics.

#### Q39: What is a "perfect maze"?
**Answer**: A maze that contains no loops, has closed cycles, and has exactly one unique path connecting any two points.

#### Q40: Explain the Randomized DFS algorithm for maze generation.
**Answer**: Starting from a cell, it randomly picks an unvisited neighbor 2 cells away, knocks down the dividing wall between them, marks them as visited, and recursively steps. If no neighbors remain, it backtracks.

#### Q41: Why does Randomized DFS step 2 cells at a time?
**Answer**: To maintain a grid structure of alternating wall/path lines, ensuring walls are not completely cleared.

#### Q42: What is the Simple Noise maze generator?
**Answer**: It iterates over all cells (excluding borders and start/end) and turns a cell into a wall with a fixed probability (e.g. 32%).

#### Q43: What is the difference between BFS and DFS for maze solving?
**Answer**: BFS explores neighbors level-by-level, guaranteed to find the shortest path first, but requires high memory queue space. DFS goes deep down a branch first, which is simpler to implement recursively and visually represents backtracking.

#### Q44: What is a "dead end" cell?
**Answer**: A cell that has been explored but has no unvisited paths leading to the destination.

#### Q45: How does the visualizer draw a cell transition?
**Answer**: By checking the state grid in `Maze`. Depending on whether a cell is labeled `VISITED`, `DEAD_END`, or `CORRECT_PATH`, it paints corresponding theme colors.

#### Q46: Why is recursive depth tracking important?
**Answer**: It measures stack usage. If stack depth exceeds the JVM stack limits, a `StackOverflowError` occurs. Tracking it helps evaluate boundary constraints.

#### Q47: How does step-by-step backward animation work?
**Answer**: When the user clicks "Step Back", the controller decrements the step index, resets the maze grid, and re-applies all steps from the beginning up to the current index.

#### Q48: What are coordinate offsets?
**Answer**: Increments added to a cell's coordinates to compute neighbors. For example, offset `(-1, 0)` moves Up.

#### Q49: How does the mouse drag listener work in editor?
**Answer**: It continuously tracks the mouse coordinate, converts the pixel position to grid coordinates, and updates the cell state at that position.

#### Q50: How can you optimize the solver further?
**Answer**: By using A* search or Dijkstra's algorithm to speed up shortest-path calculation, or using bitwise grids to reduce memory footprints.

---

### Part 3: Advanced Software Engineering Interview Questions (Recursion/Backtracking Focus)

#### Q51: How would you convert this recursive solver into an iterative solver?
**Answer**: By implementing an iterative DFS using an explicit `Stack` object. Instead of function calls, we push neighboring nodes onto a custom `Stack<Position>` alongside visited states, avoiding JVM call stack overflows.

#### Q52: What is the difference between recursion and iteration?
**Answer**: Recursion solves a problem by calling itself with smaller inputs, relying on stack frames. Iteration uses loops (`while`, `for`) to repeat instructions, using less memory overhead but sometimes resulting in more complex code.

#### Q53: Explain the tail-recursion optimization (TRO). Does Java support it?
**Answer**: TRO occurs when the recursive call is the final action in a method, allowing compilers to reuse the current stack frame. Java does *not* natively support TRO, which is why deep recursions can cause `StackOverflowError` in Java.

#### Q54: How does the A* algorithm differ from DFS?
**Answer**: DFS is blind exploration. A* uses heuristics (estimated distance to target, e.g., Manhattan distance) to prioritize exploring cells that are closer to the end, reducing visited nodes.

#### Q55: How would you parallelize a maze-solving algorithm?
**Answer**: By splitting exploration at branches. When a branch is met, a new thread can explore it. Threads must share a concurrent visited set to avoid double exploration.

#### Q56: What is the Manhattan distance?
**Answer**: The distance between two points measured along axes at right angles: $|x_1 - x_2| + |y_1 - y_2|$.

#### Q57: How do you identify a cycle in a graph using DFS?
**Answer**: If DFS encounters a node that is currently in the active recursion stack (marked as visiting but not yet popped), a cycle exists.

#### Q58: Explain the difference between stack and heap memory.
**Answer**: Stack stores thread-specific primitive values and active method references. Heap stores instances of objects. Recursion uses stack memory; objects like grids use heap.

#### Q59: What is the time complexity of the Randomized DFS maze generator?
**Answer**: $O(V)$ where $V$ is total cells, as it visits each grid cell exactly once to carve paths.

#### Q60: How does pruning optimize backtracking?
**Answer**: Pruning cuts off branch exploration early if it's determined that the current branch cannot lead to a valid or optimal solution (e.g. current path length exceeds the shortest path length found so far).
