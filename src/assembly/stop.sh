#!/usr/bin/env bash
BIN_DIR=$(dirname "$0")
HOME_DIR="$BIN_DIR/.."

if [[ ! -f "$HOME_DIR/pid" ]]; then
  echo "PID file does not exist"
  exit 1
fi

PIDS="$(cat "$HOME_DIR/pid")"
echo "$PIDS" | xargs kill
echo "$PIDS" | wait