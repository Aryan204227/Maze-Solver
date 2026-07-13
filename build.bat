@echo off
if not exist out mkdir out
javac -encoding UTF-8 -d out ^
  "src\com\mazesolver\Main.java" ^
  "src\com\mazesolver\algorithm\MazeGenerator.java" ^
  "src\com\mazesolver\algorithm\MazeSolver.java" ^
  "src\com\mazesolver\controller\AppController.java" ^
  "src\com\mazesolver\controller\FileController.java" ^
  "src\com\mazesolver\controller\MazeController.java" ^
  "src\com\mazesolver\model\Cell.java" ^
  "src\com\mazesolver\model\Maze.java" ^
  "src\com\mazesolver\model\MazeSolution.java" ^
  "src\com\mazesolver\model\SolverStats.java" ^
  "src\com\mazesolver\util\AnimationTimer.java" ^
  "src\com\mazesolver\util\Constants.java" ^
  "src\com\mazesolver\util\ThemeManager.java" ^
  "src\com\mazesolver\view\MainWindow.java" ^
  "src\com\mazesolver\view\SplashScreen.java" ^
  "src\com\mazesolver\view\ToastNotification.java" ^
  "src\com\mazesolver\view\dialogs\AboutDialog.java" ^
  "src\com\mazesolver\view\dialogs\HelpDialog.java" ^
  "src\com\mazesolver\view\dialogs\SettingsDialog.java" ^
  "src\com\mazesolver\view\panels\ControlPanel.java" ^
  "src\com\mazesolver\view\panels\DashboardPanel.java" ^
  "src\com\mazesolver\view\panels\LearningPanel.java" ^
  "src\com\mazesolver\view\panels\MazePanel.java" ^
  "src\com\mazesolver\view\panels\SidebarPanel.java" ^
  "src\com\mazesolver\view\panels\StatsPanel.java" ^
  "src\com\mazesolver\view\panels\StatusBar.java"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ===== BUILD FAILED =====
    exit /b 1
) else (
    echo.
    echo ===== BUILD SUCCESS — V4.0 =====
)
