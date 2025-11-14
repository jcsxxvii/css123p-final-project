@echo off
REM CORR4 Bank Application - Connection Test Script for Windows
REM This script tests database connectivity before launching the full app

echo ============================================================
echo     CORR4 Bank - Database Connection Test
echo ============================================================
echo.

REM Set Java home
set JAVA_HOME=C:\Program Files\Java\jdk-21

REM Check if Java exists
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: Java not found at %JAVA_HOME%
    echo Please update JAVA_HOME in this script.
    pause
    exit /b 1
)

cd /d "%~dp0Corr4_App"

echo Step 1: Compiling database connection classes...
"%JAVA_HOME%\bin\javac" -cp "src/main/java" ^
    src/main/java/com/css123group/corr4_be/DatabaseConnection.java ^
    src/main/java/com/css123group/corr4_be/DatabaseDiagnostic.java 2>nul

if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo Step 2: Running diagnostic tool...
echo.
"%JAVA_HOME%\bin\java" -cp "src/main/java" com.css123group.corr4_be.DatabaseDiagnostic

if %ERRORLEVEL% equ 0 (
    echo.
    echo ============================================================
    echo SUCCESS: Database connection verified!
    echo ============================================================
    echo.
    echo Ready to launch the application. Press any key to continue...
    pause >nul
) else (
    echo.
    echo ============================================================
    echo FAILURE: Could not connect to database
    echo ============================================================
    echo.
    echo Troubleshooting:
    echo   1. Verify Supabase credentials in DatabaseConnection.java
    echo   2. Check network connectivity to db.mcrkbayvjgoqdxykhngc.supabase.co
    echo   3. Ensure PostgreSQL JDBC driver is in classpath
    echo.
    pause
    exit /b 1
)
