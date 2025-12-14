@echo off
setlocal ENABLEDELAYEDEXPANSION

echo =========================================
echo MMO Backend - Local Dev Logs
echo =========================================

REM Move to repo root
cd /d "%~dp0\..\..\" || exit /b 1

REM Verify docker-compose file exists
set "COMPOSE_FILE=infrastructure\local\docker-compose.yml"
if not exist "%COMPOSE_FILE%" (
    echo [ERROR] docker-compose.yml not found:
    echo %COMPOSE_FILE%
    exit /b 1
)

REM Pick env (default dev)
set "ENV_NAME=%~1"
if "%ENV_NAME%"=="" set "ENV_NAME=dev"
set "ENV_FILE=infrastructure\local\env\%ENV_NAME%.env"

if not exist "%ENV_FILE%" (
    echo [ERROR] Env file not found:
    echo %ENV_FILE%
    exit /b 1
)

REM If second arg exists, treat it as service name
set "SERVICE=%~2"

if "%SERVICE%"=="" (
    echo [INFO] Showing logs for ALL services (env=%ENV_NAME%)
    echo Press CTRL+C to stop
    docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" logs -f
) else (
    echo [INFO] Showing logs for service: %SERVICE% (env=%ENV_NAME%)
    echo Press CTRL+C to stop
    docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" logs -f %SERVICE%
)

endlocal
