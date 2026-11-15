#!/bin/bash
set -e

APP_URL=${1:-"http://localhost:8080"}
echo "Iniciando Smoke Tests sobre: $APP_URL"

check_http_status() {
    local url=$1
    local expected_status=$2
    local actual_status
    actual_status=$(curl -o /dev/null -s -w "%{http_code}" "$url")

    if [ "$actual_status" -eq "$expected_status" ]; then
        echo "SUCCESS: $url respondio con HTTP $actual_status."
    else
        echo "FAILURE: $url respondio con HTTP $actual_status (esperado: $expected_status)."
        exit 1
    fi
}

check_http_status "$APP_URL/health.php" 200
echo "Todos los Smoke Tests criticos superados correctamente."
