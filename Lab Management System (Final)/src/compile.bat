@echo off
if not exist lib\sqlite-jdbc-3.42.0.0.jar (
    echo SQLite JDBC driver not found. Running download_dependencies.bat first...
    call download_dependencies.bat
)
javac -cp ".;lib\sqlite-jdbc-3.42.0.0.jar" *.java
echo Compilation complete!
echo Please run with: java -cp ".;lib\sqlite-jdbc-3.42.0.0.jar" MainApp 