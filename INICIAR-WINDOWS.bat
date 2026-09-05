@echo off
cd /d "%~dp0"
echo ========================================
echo       SorteLoto ☘️ v2.0 - Inicializando
echo ========================================
docker compose up --build -d
echo.
echo Abra: http://localhost:8088
pause
