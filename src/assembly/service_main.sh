#!/usr/bin/env bash
JAVA_HOME="/opt/jdk-24.0.1" JAVA_OPTS="-Dspring.profiles.active=prod,loopback-only,notify-systemd" "$(dirname "$0")/start.sh"