@echo off
setlocal

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile all Java source files in src and subfolders
echo [INFO] Compiling Java files...
javac -d bin -cp src src\main\Main.java src\view\*.java src\algorithm\*.java src\util\*.java

REM Check for compilation success
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b 1
)

REM Run the program (specify the fully qualified main class)
echo [INFO] Running main.Main...
java -cp bin main.Main

pause
endlocal
