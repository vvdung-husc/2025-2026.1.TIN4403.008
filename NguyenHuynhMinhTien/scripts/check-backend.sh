#!/usr/bin/env bash
set -euo pipefail

BACKEND_DIR="backend"
PORT=3000

echo "--- Kiểm tra Backend (bash) ---"

# 1) Check node
if ! command -v node >/dev/null 2>&1; then
  echo "ERROR: node not found in PATH" >&2; exit 2
fi
echo "node: $(node -v)"
echo "npm: $(npm -v)"

# 2) check project
if [ ! -d "$BACKEND_DIR" ]; then
  echo "ERROR: $BACKEND_DIR not found" >&2; exit 2
fi
cd "$BACKEND_DIR"

if [ ! -f package.json ]; then
  echo "ERROR: package.json not found" >&2; exit 2
fi

if [ ! -f server.js ]; then
  echo "WARNING: server.js not found" >&2
fi

if [ ! -d node_modules ]; then
  echo "node_modules missing. Running npm install..."
  npm install
fi

echo "MONGODB_URI=$MONGODB_URI"
echo "DB_NAME=$DB_NAME"

# Start server in background
node server.js &
PID=$!
sleep 3

# Check endpoints
if curl -sS "http://localhost:${PORT}/" >/dev/null 2>&1; then
  echo "ROOT OK"
else
  echo "ROOT failed"
fi

if curl -sS "http://localhost:${PORT}/api/students" >/dev/null 2>&1; then
  echo "/api/students OK"
else
  echo "/api/students failed"
fi

# check port
if ss -ltn | grep -q ":${PORT} "; then
  echo "Port ${PORT} listening"
else
  echo "Port ${PORT} not listening (or ss not available)"
fi

# cleanup
kill $PID || true
wait $PID 2>/dev/null || true

echo "--- Done ---"