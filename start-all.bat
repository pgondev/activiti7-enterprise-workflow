@echo off
REM ===========================================
REM Enterprise Workflow Platform - Start All
REM ===========================================
TITLE Workflow Platform Startup

echo.
echo ========================================
echo   Enterprise Workflow Platform
echo   Starting All Services...
echo ========================================
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

REM Start Infrastructure
echo [1/5] Starting Infrastructure (Docker)...
docker-compose up -d
if errorlevel 1 (
    echo [ERROR] Failed to start Docker containers
    pause
    exit /b 1
)
echo [OK] Infrastructure started

REM Wait for services to be ready
echo.
echo [2/5] Waiting for infrastructure to be ready...
timeout /t 15 /nobreak >nul

REM Start Backend Services in separate windows
echo.
echo [3/5] Starting Backend Services...

echo      - Starting workflow-engine on port 8080...
start "workflow-engine" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:workflow-engine:bootRun"

timeout /t 5 /nobreak >nul

echo      - Starting task-service on port 8081...
start "task-service" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:task-service:bootRun"

timeout /t 5 /nobreak >nul

echo      - Starting form-service on port 8082...
start "form-service" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:form-service:bootRun"

timeout /t 5 /nobreak >nul

echo      - Starting decision-engine on port 8083...
start "decision-engine" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:decision-engine:bootRun"

echo [OK] Backend services starting...

REM Wait for backend to start
echo.
echo [4/5] Waiting for backend services to start...
timeout /t 20 /nobreak >nul

REM Start Frontend Apps
echo.
echo [5/5] Starting Frontend Apps...

echo      - Starting modeler-ui on port 3000...
start "modeler-ui" cmd /k "cd /d %~dp0apps\modeler-ui && npm run dev"

timeout /t 3 /nobreak >nul

echo      - Starting tasklist-ui on port 3001...
start "tasklist-ui" cmd /k "cd /d %~dp0apps\tasklist-ui && npm run dev"

timeout /t 3 /nobreak >nul

echo      - Starting admin-ui on port 3002...
start "admin-ui" cmd /k "cd /d %~dp0apps\admin-ui && npm run dev"

echo.
echo ========================================
echo   All Services Started!
echo ========================================
echo.
echo   Frontend URLs:
echo   - Modeler UI:   http://localhost:3000
echo   - Tasklist UI:  http://localhost:3001
echo   - Admin UI:     http://localhost:3002
echo.
echo   Backend APIs:
echo   - Workflow:     http://localhost:8080/swagger-ui.html
echo   - Tasks:        http://localhost:8081/swagger-ui.html
echo   - Forms:        http://localhost:8082/swagger-ui.html
echo   - Decisions:    http://localhost:8083/swagger-ui.html
echo.
echo   Infrastructure:
echo   - PostgreSQL:   localhost:5432
echo   - Keycloak:     http://localhost:8180
echo   - Grafana:      http://localhost:3030
echo   - RabbitMQ:     http://localhost:15672
echo.
echo ========================================
echo.
pause
