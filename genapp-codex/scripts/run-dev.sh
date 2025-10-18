#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"

cleanup() {
  echo "\nStopping dev services..."
  if [[ -n "${BACKEND_PID:-}" ]]; then
    kill "$BACKEND_PID" 2>/dev/null || true
  fi
  if [[ -n "${FRONTEND_PID:-}" ]]; then
    kill "$FRONTEND_PID" 2>/dev/null || true
  fi
}

trap cleanup EXIT

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven (mvn) not found. Install Maven or use ./mvnw if added." >&2
  exit 1
fi

if ! command -v npm >/dev/null 2>&1; then
  echo "npm not found. Install Node.js 20+ to continue." >&2
  exit 1
fi

echo "Starting GenApp Codex backend..."
(
  cd "$BACKEND_DIR"
  mvn -q package -DskipTests
  SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
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
