@echo off
echo Starting Lab Management System...

:: Ensure no other instance is running that might lock the database
taskkill /f /im java.exe >nul 2>&1
timeout /t 1 >nul

:: Check for SQLite JDBC driver
if not exist lib\sqlite-jdbc-3.42.0.0.jar (
    echo Downloading SQLite JDBC driver...
    mkdir lib 2>nul
    powershell -Command "& {try { Invoke-WebRequest -Uri 'https://github.com/xerial/sqlite-jdbc/releases/download/3.42.0.0/sqlite-jdbc-3.42.0.0.jar' -OutFile 'lib/sqlite-jdbc-3.42.0.0.jar' -ErrorAction Stop } catch { exit 1 }}"
    
    if not exist lib\sqlite-jdbc-3.42.0.0.jar (
        echo Trying alternative download...
        curl -L -o lib/sqlite-jdbc-3.42.0.0.jar https://github.com/xerial/sqlite-jdbc/releases/download/3.42.0.0/sqlite-jdbc-3.42.0.0.jar
    )
)

:: Check if database journal file exists (sign of unclean shutdown)
if exist lms.db-journal (
    echo Cleaning up incomplete database transactions...
    del /f lms.db-journal >nul 2>&1
)

:: Run the application with extra database parameters
echo Running application...
java -cp ".;lib\sqlite-jdbc-3.42.0.0.jar" -Dsqlite.busy.timeout=10000 -Dsqlite.journal_mode=WAL -Dsqlite.synchronous=NORMAL MainApp

echo Application closed. 
pause 