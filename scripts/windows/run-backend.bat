@echo off
REM ============================================================================
REM  Start the Watcher backend for local play / Unity client testing.
REM  Postgres runs in Docker on host port 15432 (your machine's native Postgres
REM  squats on 5432). Services run as plain Java on 18080 (auth) and 18090 (world).
REM
REM  Prereqs: Docker Desktop running, Java 25 on PATH, and the jars built once:
REM     mvnw.cmd -B -ntp -DskipTests package
REM ============================================================================
setlocal
cd /d "%~dp0..\.."
set "ROOT=%CD%"
set "AUTHJAR=%ROOT%\services\auth-service\target\auth-service-0.0.1-SNAPSHOT.jar"
set "WORLDJAR=%ROOT%\services\world-service\target\world-service-0.0.1-SNAPSHOT.jar"

if not exist "%AUTHJAR%" goto needbuild
if not exist "%WORLDJAR%" goto needbuild

echo Starting Postgres in Docker (host port 15432)...
cd /d "%ROOT%\infrastructure\local"
set "POSTGRES_PORT=15432"
docker compose --env-file ./env/dev.env up -d postgres
if errorlevel 1 (
  echo.
  echo   Could not start Postgres. Is Docker Desktop running?
  echo.
  pause
  exit /b 1
)

echo Waiting for Postgres to be ready...
set /a tries=0
:waitpg
set "PGS="
for /f %%s in ('docker inspect -f "{{.State.Health.Status}}" mmo_postgres 2^>nul') do set "PGS=%%s"
if "%PGS%"=="healthy" goto pgready
set /a tries+=1
if %tries% geq 30 (
  echo   Postgres did not become healthy in time. Check Docker Desktop.
  pause
  exit /b 1
)
timeout /t 2 >nul
goto waitpg

:pgready
echo Postgres ready.
cd /d "%ROOT%"
set "MMO_DB_PORT=15432"
set "AUTH_SERVICE_PORT=18080"
set "WORLD_SERVICE_PORT=18090"
start "Watcher auth-service (18080)"  cmd /k java -jar "%AUTHJAR%"
start "Watcher world-service (18090)" cmd /k java -jar "%WORLDJAR%"

echo.
echo   Backend starting in two new windows. Give them ~10 seconds.
echo     auth-service   http://localhost:18080
echo     world-service  http://localhost:18090
echo     Content Studio http://localhost:18090/studio/   (admin / admin-dev-secret)
echo.
echo   Unity GameBootstrap fields:
echo     Auth URL    http://localhost:18080
echo     World URL   http://localhost:18090
echo     WS URL      ws://localhost:18090/ws
echo.
echo   To STOP: close the two service windows, then run scripts\windows\stop-backend.bat
echo.
pause
exit /b 0

:needbuild
echo.
echo   Jars not found. Build them once from the repo root, then re-run this:
echo       mvnw.cmd -B -ntp -DskipTests package
echo.
pause
exit /b 1
