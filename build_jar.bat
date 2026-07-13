@echo off
title Maze Solver - JAR Builder
color 0A
echo =================================================
echo   MAZE SOLVER STANDALONE JAR BUILDER
echo =================================================
echo.

if not exist bin mkdir bin

echo [1/3] Compiling source files to bin...
javac -encoding UTF-8 -d bin -sourcepath src src/com/mazesolver/Main.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)
echo.

echo [2/3] Building executable JAR file (MazeSolver.jar)...
jar cfe MazeSolver.jar com.mazesolver.Main -C bin .
if %errorlevel% neq 0 (
    echo [ERROR] JAR creation failed!
    pause
    exit /b %errorlevel%
)
echo.

echo [3/3] Copying MazeSolver.jar to Desktop...
set "DESKTOP_PATH=%USERPROFILE%\Desktop"
if exist "%USERPROFILE%\OneDrive\Desktop" (
    set "DESKTOP_PATH=%USERPROFILE%\OneDrive\Desktop"
)

copy /y MazeSolver.jar "%DESKTOP_PATH%\MazeSolver.jar" >nul
if %errorlevel% neq 0 (
    echo [WARNING] Could not copy to Desktop. Standalone JAR is saved in this folder.
) else (
    echo Standalone JAR copied to Desktop successfully!
)
echo.
echo =================================================
echo   SUCCESS! Standalone MazeSolver.jar built.
echo   You can run it by double-clicking it on your 
echo   Desktop or inside this folder.
echo =================================================
echo.
pause
