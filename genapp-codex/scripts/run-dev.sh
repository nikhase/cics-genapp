#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"
COMPOSE_FILE="$ROOT_DIR/docker-compose.yml"
MAVEN_REPO_LOCAL="$ROOT_DIR/.m2"
DB_STARTED_BY_SCRIPT=0

cleanup() {
  echo -e "\nStopping dev services..."
  if [[ -n "${FRONTEND_PID:-}" ]]; then
    kill "$FRONTEND_PID" 2>/dev/null || true
  fi
  if [[ -n "${BACKEND_PID:-}" ]]; then
    kill "$BACKEND_PID" 2>/dev/null || true
  fi
  if [[ "$DB_STARTED_BY_SCRIPT" -eq 1 ]]; then
    echo "Stopping postgres container..."
    docker compose -f "$COMPOSE_FILE" stop db >/dev/null 2>&1 || true
  fi
}

start_db() {
  echo "[info] Ensuring postgres data volume is compatible..."
  if docker volume inspect genapp-codex_db_data >/dev/null 2>&1; then
    local pg_version
    pg_version=$(docker compose -f "$COMPOSE_FILE" run --rm db postgres --version 2>/dev/null | awk '{print $3}' || true)
    if [[ "$pg_version" == 16.* ]]; then
      echo "[info] Removing old postgres 16 volume to avoid incompatibility."
      docker compose -f "$COMPOSE_FILE" down >/dev/null 2>&1 || true
      docker volume rm genapp-codex_db_data >/dev/null 2>&1 || true
    fi
  fi

  if ! command -v docker >/dev/null 2>&1; then
    echo "Docker is required to run the local PostgreSQL container." >&2
    exit 1
  fi
  if ! docker compose version >/dev/null 2>&1; then
    echo "\"docker compose\" CLI not available. Update Docker Desktop/CLI." >&2
    exit 1
  fi

  local running_container
  running_container=$(docker compose -f "$COMPOSE_FILE" ps -q db)
  if [[ -n "$running_container" ]] && docker ps -q --filter "id=$running_container" | grep -q .; then
    echo "Using existing postgres container ($running_container)."
  else
    echo "Starting postgres via docker compose..."
    docker compose -f "$COMPOSE_FILE" up -d db
    DB_STARTED_BY_SCRIPT=1
  fi

  echo -n "Waiting for postgres to accept connections"
  for attempt in $(seq 1 30); do
    if docker compose -f "$COMPOSE_FILE" exec -T db pg_isready -U genapp -d genapp >/dev/null 2>&1; then
      echo " - ready."
      return 0
    fi
    echo -n "."
    sleep 1
    if (( attempt % 5 == 0 )); then
      echo -e "\n[debug] Waiting for Postgres... attempt $attempt"
      docker compose -f "$COMPOSE_FILE" logs --tail=5 db
    fi
  done

  echo -e "\nPostgres did not become ready in time." >&2
  echo "[debug] Last logs from postgres:"
  docker compose -f "$COMPOSE_FILE" logs --tail=50 db >&2
  exit 1
}

trap cleanup EXIT

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven (mvn) not found. Install Maven or ensure it is on PATH." >&2
  exit 1
fi

if ! command -v npm >/dev/null 2>&1; then
  echo "npm not found. Install Node.js 20+ to continue." >&2
  exit 1
fi

mkdir -p "$MAVEN_REPO_LOCAL"

start_db

echo "Starting GenApp Codex backend..."
(
  cd "$BACKEND_DIR"
  mvn -q -Dmaven.repo.local="$MAVEN_REPO_LOCAL" package -DskipTests
  SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/genapp" \
  SPRING_DATASOURCE_USERNAME="genapp" \
  SPRING_DATASOURCE_PASSWORD="genapp" \
  mvn -Dmaven.repo.local="$MAVEN_REPO_LOCAL" spring-boot:run
) &
BACKEND_PID=$!

sleep 5

echo "Starting GenApp Codex frontend..."
(
  cd "$FRONTEND_DIR"
  npm install
  npm run dev -- --host
) &
FRONTEND_PID=$!

wait
