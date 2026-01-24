@echo off
REM ===========================================
REM Start Backend Services Only (requires Docker running)
REM ===========================================
TITLE Workflow Backend Services

echo.
echo Starting Backend Services...
echo.

echo [1/4] Starting workflow-engine on port 8080...
start "workflow-engine" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:workflow-engine:bootRun"

timeout /t 5 /nobreak >nul

echo [2/4] Starting task-service on port 8081...
start "task-service" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:task-service:bootRun"

timeout /t 5 /nobreak >nul

echo [3/4] Starting form-service on port 8082...
start "form-service" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:form-service:bootRun"

timeout /t 5 /nobreak >nul

echo [4/4] Starting decision-engine on port 8083...
start "decision-engine" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:decision-engine:bootRun"

echo.
echo ========================================
echo   Backend Services Starting!
echo ========================================
echo   - Workflow Engine: http://localhost:8080
echo   - Task Service:    http://localhost:8081
echo   - Form Service:    http://localhost:8082
echo   - Decision Engine: http://localhost:8083
echo.
echo   (Services will take ~30-60 seconds to fully start)
echo ========================================
echo.
pause
