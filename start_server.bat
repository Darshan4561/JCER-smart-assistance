@echo off
echo ==========================================
echo    JCER SMART ASSISTANT - STARTING
echo ==========================================
echo.
echo 1. Opening Browser...
start "" http://localhost:8081
echo 2. Compiling Backend...
javac Server.java
echo 3. Launching Server...
echo.
java Server
pause
