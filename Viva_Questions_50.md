# ❓ 50 Viva Questions & Detailed Answers for Maze Solver Project

This document contains 50 comprehensive viva questions and expert answers, designed to help a second or third-year computer science student ace their academic project defense.

---

## 💻 Category 1: Core Java Concepts

### Q1: What is the entry point of a Java application?
**Answer:** The `public static void main(String[] args)` method. The JVM looks for this specific signature to start executing the program.

### Q2: Why is the main method declared as static?
**Answer:** Making it `static` allows the Java Virtual Machine (JVM) to invoke the method without creating an instance of the class containing it. This saves memory and simplifies startup.

### Q3: What is the purpose of `SwingUtilities.invokeLater()`?
**Answer:** It schedules a Runnable task to be executed on the Event Dispatch Thread (EDT). Since Swing GUI components are not thread-safe, all updates to the interface must run on the EDT to prevent GUI glitches and race conditions.

### Q4: What is the difference between AWT and Swing?
**Answer:** AWT (Abstract Window Toolkit) components are "heavyweight" because they map directly to native operating system peer windows. Swing components are "lightweight" because they are written entirely in Java and rendered from scratch on a blank container canvas, making them cross-platform consistent and customizable.

### Q5: How do we customize Look & Feel in Java Swing?
**Answer:** We call `UIManager.setLookAndFeel(String className)` passing the class name of the look and feel skin (e.g., the platform native system skin). This changes how borders, buttons, and scrolls are drawn.

### Q6: What does the `volatile` keyword do?
**Answer:** It guarantees that changes made to a variable by one thread are instantly visible to all other threads. In our project, the `paused` and `cancelled` flags in `AnimationTimer` are marked `volatile` so the solving thread immediately stops or pauses when the user clicks buttons on the GUI thread.

### Q7: What is an Enum in Java and why did you use it?
**Answer:** An Enum (Enumeration) is a special data type representing a fixed set of constants. We used it in `Cell.State` to restrict the possible cell states (WALL, OPEN, VISITED, etc.) at compile time, eliminating invalid state assignments.

### Q8: What are `final` variables?
**Answer:** Variables declared as `final` cannot be reassigned once initialized. We use them for constants (e.g., in `Constants.java`) to prevent bugs where configuration parameters are accidentally modified.

### Q9: Why is a private constructor used in `Constants.java`?
**Answer:** To prevent instantiation. Since `Constants.java` only contains static final configuration values, creating an object of this class is unnecessary. A private constructor enforces this rule.

### Q10: How does Java handle file I/O?
**Answer:** Java uses streams and readers. In `FileController`, we used `BufferedReader` and `BufferedWriter` wrapped around `FileReader` and `FileWriter` respectively to read and write character files line-by-line efficiently.

---

## 📂 Category 2: Object-Oriented Programming (OOP)

### Q11: How is Encapsulation implemented in your project?
**Answer:** Encapsulation is wrapping variables and methods inside a class and restricting direct access to the variables using private modifiers. For example, `Cell` coordinates are private, and other classes access or modify them only through `getRow()`, `getCol()`, and `setState()` methods.

### Q12: How is Inheritance used in your project?
**Answer:** Our custom panels (like `MazePanel`, `ControlPanel`) inherit from `JPanel` using the `extends` keyword. This allows them to reuse Swing panel rendering mechanisms while adding custom layout controls and custom painting methods.

### Q13: What is Polymorphism and how is it used here?
**Answer:** Polymorphism allows a single interface to represent multiple underlying forms. For example, our callback interfaces (`SolveCallback`, `ControlListener`) allow controllers to receive events from GUI components without the views knowing the specific controller class.

### Q14: What is the difference between an Interface and an Abstract Class?
**Answer:** An interface contains only method signatures (until Java 8 which introduced default methods) and represents a contract. An abstract class can have member fields, partially implemented methods, and constructors. We used interfaces for simple event callbacks.

### Q15: Why did you choose MVC (Model-View-Controller) architecture?
**Answer:** MVC separates the application data (Model), user interface rendering (View), and program control logic (Controller). This separation keeps the codebase modular, easy to explain, and ensures that changing the GUI styling doesn't break the solving algorithm.

### Q16: How do the Model and View communicate in your project?
**Answer:** The View reads states directly from the Model to redraw the grid, while the Controller updates the Model's data and triggers `repaint()` on the View. Directly updating the View from the Model is avoided to maintain decoupling.

### Q17: What is class decoupling?
**Answer:** Decoupling ensures classes have minimal dependency on one another. We achieved this by using interfaces to trigger updates, allowing the solving algorithm to run independently of the specific Swing panels.

### Q18: What is the super keyword used for?
**Answer:** The `super` keyword refers to parent class constructors or methods. For example, in `MainWindow.java`, calling `super(Constants.APP_TITLE)` invokes the `JFrame` constructor to set the title bar text of our window.

### Q19: Why do we override `paintComponent()` in Swing?
**Answer:** To perform custom graphics drawing. By overriding `paintComponent(Graphics g)` in `MazePanel`, we can use Java2D instructions to manually draw cell grids and highlight active search paths.

### Q20: What is the role of the `Graphics2D` class?
**Answer:** `Graphics2D` extends the basic `Graphics` class to provide advanced control over coordinate geometry, antialiasing, color fills, strokes, and font layouts.

---

## 🌀 Category 3: Recursion & Backtracking Algorithms

### Q21: What is recursion?
**Answer:** Recursion is a programming technique where a method calls itself to solve a smaller subproblem of the same problem, ending with a base case.

### Q22: What is backtracking?
**Answer:** Backtracking is an algorithmic approach that tries to construct a solution incrementally. When it discovers that a current branch cannot lead to a valid solution, it discards the last step (backtracks) and tries another branch.

### Q23: What is the base case in your recursive solver?
**Answer:** The base case is when the current cell row and column match the designated end cell coordinates (`row == maze.getEndRow() && col == maze.getEndCol()`). At this point, a complete solution is found.

### Q24: What happens if you forget to write a base case in recursion?
**Answer:** The program will enter infinite recursion, resulting in a `StackOverflowError` because JVM call stacks have a limited size limit.

### Q25: How does the recursive call stack act as a backtracking mechanism?
**Answer:** When the solver calls `solveRecursive()` on a neighbor, the JVM pushes the call onto its execution stack. If the neighbor returns without finding a path, execution resumes at the previous step, allowing the program to try different directions.

### Q26: Why is the visited array necessary in DFS?
**Answer:** It keeps track of cells visited along the current search path. This prevents the solver from moving back and forth between two open cells, preventing infinite loops.

### Q27: How does your solver find ALL solutions rather than just one?
**Answer:** When the solver hits the base case, it increments its solutions counter and saves the path, but it *does not stop*. Instead of returning, it backtracks from the destination cell, allowing the recursive loops to explore alternative paths.

### Q28: How do you prevent stack overflow on large grid sizes?
**Answer:** We restrict the maximum grid dimensions to `25×25`. This caps the recursion depth, keeping the call stack footprint safe.

### Q29: What is the time complexity of the DFS maze solver?
**Answer:** In the worst-case scenario (an empty grid with no walls), the solver can branch in 4 directions at each step, yielding a time complexity of $O(4^{R \times C})$ where $R$ is rows and $C$ is columns.

### Q30: What is the space complexity of the solver?
**Answer:** The space complexity is $O(R \times C)$ to store the 2D grid cells, visited states, and recursion stack memory.

---

## ⚙️ Category 4: Multithreading & Thread Safety

### Q31: Why do we need a separate thread for solving?
**Answer:** If we ran the solving loop on the main Event Dispatch Thread (EDT), the thread sleep calls used for animation would freeze the UI, preventing buttons from rendering and making it impossible to click "Pause" or "Reset".

### Q32: What is the Event Dispatch Thread (EDT)?
**Answer:** A background thread managed by Swing that handles GUI events like mouse clicks, key presses, and component paint requests.

### Q33: How does the solving thread safely update the UI?
**Answer:** It uses `SwingUtilities.invokeLater(Runnable)`. This packages the visual cell updates into a task and schedules it for execution on the EDT.

### Q34: What is the difference between `Thread.start()` and `Thread.run()`?
**Answer:** `start()` creates a new operating system thread and runs the target code concurrently. Calling `run()` executes the code on the *current* thread synchronously, defeats the purpose of multithreading.

### Q35: How does "Pause" work in your animation?
**Answer:** In `AnimationTimer`, the method `waitIfPaused()` runs inside a loop that checks the `paused` variable. If true, the solver thread sleeps in 50ms intervals until the variable is set to false.

### Q36: How does "Cancel/Reset" work?
**Answer:** When the user clicks Reset, the controller sets the `cancelled` flag in `AnimationTimer` to true. The recursive solver checks this flag at each step and exits immediately if it is set.

### Q37: Why did you use Thread.sleep()?
**Answer:** To slow down execution. Computers solve mazes in milliseconds; adding a sleep delay creates a step-by-step visualization, making the algorithm's decisions easier to follow.

### Q38: What is a Race Condition?
**Answer:** A situation where multiple threads attempt to read and write to the same shared resource simultaneously. We avoid this by isolating grid calculations on the worker thread and performing visual state updates on the EDT.

### Q39: What is thread interruption?
**Answer:** Interruption is a signal to a thread that it should stop running. In `stopSolvingForcefully()`, we call `solverThread.interrupt()` to wake the solver thread if it is sleeping.

### Q40: What does `InterruptedException` mean?
**Answer:** It is thrown when a thread that is sleeping, waiting, or otherwise occupied is interrupted by another thread. We handle this by resetting the thread's interrupted status and exiting the solver.

---

## 🛠️ Category 5: Practical Implementation details

### Q41: How does the Random Maze Generator work?
**Answer:** It uses the **Recursive Backtracker** algorithm. Starting with a grid of walls, it visits neighbor cells 2 units away in a randomized order, carving paths through the walls until all cells have been visited.

### Q42: Why do you carve paths 2 cells away in the generator?
**Answer:** Moving in steps of 2 preserves walls between the corridors, creating a clean maze layout instead of an empty grid.

### Q43: How is a maze configuration saved to a file?
**Answer:** We write metadata (dimensions, start/end coordinates) to a plain text file, followed by the grid cell values represented as `1` for walls and `0` for pathways.

### Q44: What is `StringTokenizer` and why did you use it?
**Answer:** `StringTokenizer` breaks a string into tokens based on delimiters. We use it to parse the space-separated wall values (`1 0 1`) when loading a maze file.

### Q45: How did you implement mouse wall drawing?
**Answer:** In `MazePanel`, we listen to mouse drag events, map the cursor pixel coordinates to grid cells, and toggle the target cell's state between WALL and OPEN.

### Q46: How do you prevent duplicate triggers during mouse drags?
**Answer:** We track the row and column of the last modified cell. If the mouse drags over the same cell multiple times, we ignore the subsequent events.

### Q47: Why did you select `Segoe UI` as your font?
**Answer:** Segoe UI is a clean, modern font that looks great on Windows, helping the UI feel like a premium application.

### Q48: What happens when the solving finishes?
**Answer:** The controller finds the shortest solution from the list, resets the solving visuals, and draws the shortest path on the grid in green.

### Q49: How does the SplashScreen close itself?
**Answer:** A Swing Timer increments the progress bar every 25ms. When progress reaches 100%, the timer stops, the splash window is disposed, and the main window opens.

### Q50: How would you scale this project to support other algorithms?
**Answer:** We could define a `Pathfinder` interface with a `solve()` method. Algorithms like BFS, Dijkstra, or A* could then implement this interface, allowing them to be swapped into the controller without changing the UI.
