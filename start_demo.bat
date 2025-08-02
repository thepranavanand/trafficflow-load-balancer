@echo off
echo ========================================
echo   LOAD BALANCER DASHBOARD DEMO
echo ========================================
echo.

cd src

echo Compiling Java files...
javac *.java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

echo Starting servers...
echo Note: Each server will open in a new window
echo    Close all windows to stop the demo
echo.

echo Starting Single-Thread Server (Port 8001)...
start "Single-Thread Server" cmd /c "java EnhancedSingleThreadServer"
timeout /t 2 /nobreak >nul

echo Starting Thread Pool Server (Port 8002)...
start "Thread Pool Server" cmd /c "java EnhancedThreadPoolServer"
timeout /t 2 /nobreak >nul

echo Starting Multi-Thread Server (Port 8003)...
start "Multi-Thread Server" cmd /c "java EnhancedMultithreadedServer"
timeout /t 2 /nobreak >nul

echo Starting Load Balancer Dashboard...
start "Load Balancer Dashboard" cmd /c "java LoadBalancerDashboard"
timeout /t 3 /nobreak >nul

echo.
echo All servers started!
echo.
echo DASHBOARD: http://localhost:8080
echo LOAD BALANCER: localhost:9000
echo.
echo TEST COMMANDS:
echo    Light:   java ClientLoadGenerator light 9000
echo    Medium:  java ClientLoadGenerator medium 9000
echo    Heavy:   java ClientLoadGenerator heavy 9000
echo.
echo Press any key to open dashboard in browser...
pause >nul

start http://localhost:8080

echo.
echo Demo is running! Press any key to stop all servers...
pause >nul

echo Stopping all servers...
taskkill /f /fi "WindowTitle eq Single-Thread Server*" >nul 2>&1
taskkill /f /fi "WindowTitle eq Thread Pool Server*" >nul 2>&1
taskkill /f /fi "WindowTitle eq Multi-Thread Server*" >nul 2>&1
taskkill /f /fi "WindowTitle eq Load Balancer Dashboard*" >nul 2>&1

echo All servers stopped!
echo.
echo Demo completed!
pause