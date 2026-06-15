@echo off
REM Stop the Watcher backend: close service windows first, then this stops Postgres.
setlocal
cd /d "%~dp0..\..\infrastructure\local"
echo Stopping Postgres container...
set "POSTGRES_PORT=15432"
docker compose --env-file ./env/dev.env down
echo Done. (If the two service windows are still open, close them.)
pause
