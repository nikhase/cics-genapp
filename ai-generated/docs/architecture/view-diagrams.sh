#!/bin/bash

# Helper script to view GenApp C4 diagrams using Structurizr Lite
# Usage: ./view-diagrams.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PORT=8080

echo "=================================================="
echo "GenApp C4 Architecture Diagrams Viewer"
echo "=================================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed or not in PATH"
    echo ""
    echo "Please install Docker from https://www.docker.com/get-started"
    echo ""
    echo "Alternatively, you can:"
    echo "1. Visit https://structurizr.com and create a free account"
    echo "2. Copy the contents of workspace.dsl to the web editor"
    echo ""
    exit 1
fi

# Check if Docker is running
if ! docker info &> /dev/null; then
    echo "ERROR: Docker is not running"
    echo ""
    echo "Please start Docker Desktop and try again"
    echo ""
    exit 1
fi

# Check if port is already in use
if lsof -Pi :$PORT -sTCP:LISTEN -t &> /dev/null; then
    echo "WARNING: Port $PORT is already in use"
    echo ""
    read -p "Do you want to use a different port? (y/n) " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        read -p "Enter port number (e.g., 8081): " PORT
    else
        echo "Attempting to connect to existing instance at http://localhost:$PORT"
        open "http://localhost:$PORT" || xdg-open "http://localhost:$PORT" || echo "Please open http://localhost:$PORT in your browser"
        exit 0
    fi
fi

echo "Starting Structurizr Lite..."
echo ""
echo "Directory: $SCRIPT_DIR"
echo "Port: $PORT"
echo ""

# Pull the latest Structurizr Lite image
echo "Pulling Structurizr Lite Docker image..."
docker pull structurizr/lite:latest

echo ""
echo "Starting Structurizr Lite container..."
echo ""

# Run Structurizr Lite
echo "Starting Docker container..."
docker run \
    -d \
    -p $PORT:8080 \
    -v "$SCRIPT_DIR:/usr/local/structurizr" \
    --name structurizr-genapp \
    structurizr/lite:latest

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to start Docker container"
    exit 1
fi

# Wait for the server to start
echo "Waiting for Structurizr Lite to start..."
sleep 3

# Check if container is running
if ! docker ps | grep -q structurizr-genapp; then
    echo "ERROR: Container failed to start"
    docker logs structurizr-genapp
    exit 1
fi

# Open browser
URL="http://localhost:$PORT"
echo ""
echo "=================================================="
echo "Structurizr Lite is running!"
echo "=================================================="
echo ""
echo "Open in browser: $URL"
echo ""
echo "Available diagrams:"
echo "  - System Context"
echo "  - Containers"
echo "  - CICS Components (all)"
echo "  - Customer Operations (focused)"
echo "  - Policy Operations (focused)"
echo "  - Data Layer Architecture (dual-write pattern)"
echo "  - Add Customer Transaction (dynamic)"
echo "  - Inquire Policy Transaction (dynamic)"
echo "  - Production Deployment"
echo ""
echo "To stop the server, run:"
echo "  docker stop structurizr-genapp && docker rm structurizr-genapp"
echo ""

# Try to open browser automatically
if command -v open &> /dev/null; then
    # macOS
    sleep 2
    open "$URL"
elif command -v xdg-open &> /dev/null; then
    # Linux
    sleep 2
    xdg-open "$URL"
elif command -v start &> /dev/null; then
    # Windows
    sleep 2
    start "$URL"
else
    echo "Please open $URL in your browser"
fi

echo ""
echo "Container is running. Docker will keep it in the background."
echo "View logs: docker logs -f structurizr-genapp"
