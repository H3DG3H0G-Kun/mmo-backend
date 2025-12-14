@echo off
setlocal EnableExtensions

echo =========================================
echo MMO Backend - LOCAL DEV RESET (DESTRUCTIVE)
echo =========================================
echo.
echo This will:
echo  - Stop all containers
echo  - DELETE all local volumes
echo  - Recreate everything from scratch
echo  - Run seed SQL scripts (if present)
echo.
echo Press CTRL+C to cancel now.
echo.

pause

REM Move to repo root
cd /d "%~dp0\..\..\" || exit /b 1

set "COMPOSE_FILE=infrastructure\local\docker-compose.yml"
if not exist "%COMPOSE_FILE%" goto err_no_compose

set "ENV_NAME=%~1"
if "%ENV_NAME%"=="" set "ENV_NAME=dev"

set "ENV_FILE=infrastructure\local\env\%ENV_NAME%.env"
if not exist "%ENV_FILE%" goto err_no_env

set "MODE=%~2"
if "%MODE%"=="" set "MODE=infra"

echo [INFO] Using env: %ENV_NAME%
echo [INFO] Mode: %MODE%
echo [INFO] Env file: %ENV_FILE%

echo [INFO] Stopping and removing containers + volumes...
docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" down -v
if errorlevel 1 goto err_compose_down

echo.
echo [INFO] Starting fresh infrastructure...
if /I "%MODE%"=="app" goto start_app

:start_infra
docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" up -d
if errorlevel 1 goto err_compose_up
goto wait_pg

:start_app
docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" --profile app up -d --build
if errorlevel 1 goto err_compose_up
goto wait_pg

:wait_pg
echo.
echo [INFO] Waiting for Postgres to be ready...

set "PGUSER=mmo"
set "PGDB=mmorpg"
set /a tries=0

:wait_pg_loop
set /a tries+=1
docker exec mmo_postgres pg_isready -U "%PGUSER%" -d "%PGDB%" >nul 2>&1
if errorlevel 1 goto wait_pg_not_ready

echo [INFO] Postgres is ready.
goto run_seeds

:wait_pg_not_ready
if %tries% GEQ 60 goto err_pg_timeout
timeout /t 2 /nobreak >nul
goto wait_pg_loop

:run_seeds
set "SEED_DIR=infrastructure\local\postgres\seed"

REM If no sql files, skip
dir /b /a:-d "%SEED_DIR%\*.sql" >nul 2>&1
if errorlevel 1 goto done_no_seeds

echo [INFO] Running seed SQL files from %SEED_DIR% ...

for /f "usebackq delims=" %%F in (`dir /b /a:-d "%SEED_DIR%\*.sql"`) do call :apply_seed "%%F"
if errorlevel 1 goto err_seed_failed

echo [INFO] Seed completed.
goto done

:apply_seed
echo [INFO] Applying: %~1
docker exec -i mmo_postgres psql -U "%PGUSER%" -d "%PGDB%" -f "/seed/%~1"
if errorlevel 1 exit /b 1
exit /b 0

:done_no_seeds
echo [WARN] No seed scripts found at %SEED_DIR% (skipping).
goto done

:done
echo.
echo =========================================
echo Local environment RESET completed
echo =========================================
echo.

docker exec -it mmo_postgres psql -U "%PGUSER%" -d "%PGDB%" -c "select * from mmo_meta.seed_runs order by executed_at desc;"
pause >nul
exit /b 0

REM -------- errors --------
:err_no_compose
echo [ERROR] docker-compose.yml not found: %COMPOSE_FILE%
exit /b 1

:err_no_env
echo [ERROR] Env file not found: %ENV_FILE%
exit /b 1

:err_compose_down
echo [ERROR] Failed to reset infrastructure (compose down).
exit /b 1

:err_compose_up
echo [ERROR] Failed to start infrastructure (compose up).
exit /b 1

:err_pg_timeout
echo [ERROR] Postgres did not become ready in time.
exit /b 1

:err_seed_failed
echo [ERROR] One or more seed scripts failed.
exit /b 1
