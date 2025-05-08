#!/bin/bash

JAR_PATH="$(dirname "$0")/@artifactId@-@version@.jar"

# use JAVA_HOME if set, otherwise use java from PATH
if [ -n "$JAVA_HOME" ]; then
  JAVA_CMD="$JAVA_HOME/bin/java"
else
  JAVA_CMD="java"
fi

# lets go
# shellcheck disable=SC2086
#           V fuck you ron pressler
"$JAVA_CMD" --enable-native-access=ALL-UNNAMED $JAVA_OPTS -jar "$JAR_PATH" "$@"
