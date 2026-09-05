@echo off
cd /d "%~dp0"
echo ATENCAO: este comando apaga o banco Docker local do SorteLoto e recria tudo.
pause
docker compose down -v
docker compose up --build -d
echo.
echo SorteLoto iniciado. Abra http://localhost:8088
pause
