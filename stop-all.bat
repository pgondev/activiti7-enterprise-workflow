@echo off
REM ===========================================
REM Enterprise Workflow Platform - Stop All
REM ===========================================
TITLE Workflow Platform Shutdown

echo.
echo ========================================
echo   Stopping All Services...
echo ========================================
echo.

REM Kill Node.js processes (frontends)
echo [1/3] Stopping Frontend Apps...
taskkill /f /im node.exe 2>nul
echo [OK] Frontend apps stopped

REM Kill Java processes (backend services)
echo.
echo [2/3] Stopping Backend Services...
REM This will kill all Java processes - be careful if you have other Java apps running
taskkill /f /im java.exe 2>nul
echo [OK] Backend services stopped

REM Stop Docker containers
echo.
echo [3/3] Stopping Infrastructure (Docker)...
docker-compose down
echo [OK] Infrastructure stopped

echo.
echo ========================================
echo   All Services Stopped!
echo ========================================
echo.
pause
