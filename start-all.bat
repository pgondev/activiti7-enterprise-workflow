@echo off
REM ===========================================
REM Enterprise Workflow Platform - Start All
REM ===========================================
REM Only starts FULLY IMPLEMENTED services
REM Scaffolded services excluded: case-engine, content-service,
REM   audit-service, history-service, query-service, notification-service,
REM   ai-service, connector-service, gateway-service
REM ===========================================
TITLE Workflow Platform Startup

echo.
echo ========================================
echo   Enterprise Workflow Platform
echo   Starting Implemented Services...
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
echo [1/4] Starting Infrastructure (Docker)...
docker-compose up -d
if errorlevel 1 (
    echo [ERROR] Failed to start Docker containers
    pause
    exit /b 1
)
echo [OK] Infrastructure started

REM Wait for services to be ready
echo.
echo [2/4] Waiting for infrastructure to be ready...
timeout /t 25 /nobreak >nul

REM Start Backend Services
echo.
echo [3/4] Starting Backend Services...

REM Core Engine
echo.
echo   [Group 1] Starting Core Engine...
echo      - Starting workflow-engine on port 8080...
start "workflow-engine" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:workflow-engine:bootRun"

echo   ...Waiting for Engine to initialize...
timeout /t 15 /nobreak >nul

REM Domain Services
echo.
echo   [Group 2] Starting Domain Services...
echo      - Starting task-service on port 8083...
start "task-service" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:task-service:bootRun"

echo      - Starting form-service on port 8084...
start "form-service" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:form-service:bootRun"

echo      - Starting decision-engine on port 8085...
start "decision-engine" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:decision-engine:bootRun"

echo   ...Waiting for Domain Services...
timeout /t 10 /nobreak >nul

REM Reporting Service
echo.
echo   [Group 3] Starting Reporting...
echo      - Starting reporting-service on port 8091...
start "reporting-service" cmd /k "cd /d %~dp0 && .\gradlew.bat :services:reporting-service:bootRun"

echo [OK] Backend services starting...

REM Wait for backend to start
echo.
echo [4/4] Waiting for backend services to start...
timeout /t 20 /nobreak >nul

REM Start Frontend Apps
echo.
echo [5/4] Starting Frontend Apps...

echo      - Starting modeler-ui on port 3000...
start "modeler-ui" cmd /k "cd /d %~dp0apps\modeler-ui && npm run dev"

echo      - Starting tasklist-ui on port 3001...
start "tasklist-ui" cmd /k "cd /d %~dp0apps\tasklist-ui && npm run dev"

echo      - Starting admin-ui on port 3002...
start "admin-ui" cmd /k "cd /d %~dp0apps\admin-ui && npm run dev"

echo      - Starting forms-ui on port 3003...
start "forms-ui" cmd /k "cd /d %~dp0apps\forms-ui && npm run dev"

echo.
echo ========================================
echo   All Implemented Services Started!
echo ========================================
echo.
echo   Frontend URLs:
echo   - Modeler UI:   http://localhost:3000
echo   - Tasklist UI:  http://localhost:3001
echo   - Admin UI:     http://localhost:3002
echo   - Forms UI:     http://localhost:3003
echo.
echo   Backend APIs (Implemented):
echo   - Workflow:     http://localhost:8080/swagger-ui.html
echo   - Tasks:        http://localhost:8083/swagger-ui.html
echo   - Forms:        http://localhost:8084/swagger-ui.html
echo   - Decisions:    http://localhost:8085/swagger-ui.html
echo   - Reporting:    http://localhost:8091/swagger-ui.html
echo.
echo   Infrastructure:
echo   - PostgreSQL:   localhost:5432
echo   - Keycloak:     http://localhost:8180
echo   - Grafana:      http://localhost:3030
echo   - RabbitMQ:     http://localhost:15672
echo.
echo   NOTE: Scaffolded services not started:
echo   - case-engine, content-service, audit-service
echo   - history-service, query-service, notification-service
echo   - ai-service, connector-service, gateway-service
echo.
echo ========================================
echo.
pause
