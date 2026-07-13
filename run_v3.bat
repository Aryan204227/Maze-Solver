@echo off
title Maze Solver V3.0 Launcher
echo =================================================
echo   MAZE SOLVER VERSION 3.0 LAUNCHER
echo =================================================
echo.
echo Compiling latest changes...
javac -encoding UTF-8 -d bin -sourcepath src src/com/mazesolver/Main.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Compilation successful.
echo Launching GUI Application...
java -cp bin com.mazesolver.Main
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Application terminated unexpectedly.
)
pause
