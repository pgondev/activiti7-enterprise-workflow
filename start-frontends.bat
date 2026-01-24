@echo off
REM ===========================================
REM Start Frontend Apps Only
REM ===========================================
TITLE Workflow Frontends

echo.
echo Starting Frontend Apps...
echo.

echo [1/3] Starting modeler-ui on port 3000...
start "modeler-ui" cmd /k "cd /d %~dp0apps\modeler-ui && npm run dev"

timeout /t 2 /nobreak >nul

echo [2/3] Starting tasklist-ui on port 3001...
start "tasklist-ui" cmd /k "cd /d %~dp0apps\tasklist-ui && npm run dev"

timeout /t 2 /nobreak >nul

echo [3/3] Starting admin-ui on port 3002...
start "admin-ui" cmd /k "cd /d %~dp0apps\admin-ui && npm run dev"

echo.
echo ========================================
echo   Frontend Apps Started!
echo ========================================
echo   - Modeler UI:   http://localhost:3000
echo   - Tasklist UI:  http://localhost:3001
echo   - Admin UI:     http://localhost:3002
echo ========================================
echo.
pause
