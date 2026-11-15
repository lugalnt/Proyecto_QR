#!/bin/bash
set -e

IMAGE_NAME=${1:-"ghcr.io/lugalnt/proyecto_qr:latest"}
COMPOSE_FILE=${2:-"docker-compose.staging.yml"}

echo "$(date '+%Y-%m-%d %H:%M:%S') - Iniciando despliegue Rolling Update para: $IMAGE_NAME"
docker compose -f "$COMPOSE_FILE" up -d --build --remove-orphans

for i in $(seq 1 12); do
    if curl -fsS "http://localhost:8080/health.php" > /dev/null; then
        echo "Contenedor saludable tras despliegue."
        ./scripts/smoke-test.sh "http://localhost:8080"
        exit 0
    fi
    echo "Esperando disponibilidad del contenedor ($i/12)..."
    sleep 5
done

echo "ERROR: Fallo en verificacion de salud. Ejecutando rollback..."
docker compose -f "$COMPOSE_FILE" down
exit 1
