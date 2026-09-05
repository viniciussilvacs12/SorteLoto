@echo off
cd /d "%~dp0"
docker compose build --no-cache --progress=plain frontend
pause
