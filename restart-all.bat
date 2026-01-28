@echo off
echo ========================================
echo   Restarting Enterprise Workflow Platform
echo ========================================

echo [1/2] Stopping all services...
call stop-all.bat

echo.
echo Waiting 5 seconds to ensure ports are released...
timeout /t 5 /nobreak >nul

echo.
echo [2/2] Starting all services...
call start-all.bat
