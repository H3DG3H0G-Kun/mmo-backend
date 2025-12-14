@echo off
setlocal ENABLEDELAYEDEXPANSION

echo =========================================
echo MMO Backend - Local Dev Environment UP
echo =========================================

REM Move to repo root
cd /d "%~dp0\..\..\" || exit /b 1

REM Verify Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not installed or not in PATH.
    exit /b 1
)

docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is installed but not running.
    exit /b 1
)

set "COMPOSE_FILE=infrastructure\local\docker-compose.yml"
if not exist "%COMPOSE_FILE%" (
    echo [ERROR] docker-compose.yml not found at:
    echo %COMPOSE_FILE%
    exit /b 1
)

REM Env (default dev)
set "ENV_NAME=%~1"
if "%ENV_NAME%"=="" set "ENV_NAME=dev"

set "ENV_FILE=infrastructure\local\env\%ENV_NAME%.env"
if not exist "%ENV_FILE%" (
    echo [ERROR] Env file not found:
    echo %ENV_FILE%
    exit /b 1
)

set "MODE=%~2"
if "%MODE%"=="" set "MODE=infra"

echo [INFO] Using env: %ENV_NAME%
echo [INFO] Mode: %MODE%

echo [INFO] Starting local infrastructure...
if /I "%MODE%"=="app" (
    docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" --profile app up -d --build
) else (
    docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" up -d
)

if errorlevel 1 (
    echo [ERROR] Failed to start infrastructure.
    exit /b 1
)

echo.
echo =========================================
echo Local infrastructure is UP
echo =========================================
echo Kafka UI   : http://localhost:8081
echo Grafana    : http://localhost:3000
echo Prometheus : http://localhost:9090
echo Loki ready : http://localhost:3100/ready
echo =========================================

echo.
pause >nul
endlocal
