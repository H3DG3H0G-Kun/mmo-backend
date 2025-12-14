@echo off
setlocal ENABLEDELAYEDEXPANSION

echo =========================================
echo MMO Backend - Local Dev Environment DOWN
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

REM Stop containers (keep volumes)
echo [INFO] Stopping local infrastructure (env=%ENV_NAME%)...
docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" down

if errorlevel 1 (
    echo [ERROR] Failed to stop infrastructure.
    exit /b 1
)

echo.
echo =========================================
echo Local infrastructure stopped successfully
echo Data volumes were preserved
echo =========================================

echo.
echo Press any key to close this window...
pause >nul

endlocal
