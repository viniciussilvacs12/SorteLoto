#!/usr/bin/env sh
set -e
docker compose up --build -d
echo "SmartLoto iniciado em http://localhost:8088"
