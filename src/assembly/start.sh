#!/bin/bash
# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ZUM LETZTEN MAL DIGGA CHECK OB DU DIE RICHTIGE DATEI BEARBEITEST!!! BIST DU IN DER GEBAUTEN ODER IM TEMPLATE??
# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


BIN_DIR=$(dirname "$0")
HOME_DIR="$BIN_DIR/.."
JAR_PATH=$(realpath "$BIN_DIR/@artifactId@-@version@.jar")

# use JAVA_HOME if set, otherwise use java from PATH
if [ -n "$JAVA_HOME" ]; then
  JAVA_CMD="$JAVA_HOME/bin/java"
else
  JAVA_CMD="java"
fi

pushd "$HOME_DIR" || exit 9

# shellcheck disable=SC2046
[ ! -f env ] || export $(grep -v '^#' env | xargs)

# lets go
# shellcheck disable=SC2086
#           V fuck you ron pressler
"$JAVA_CMD" --enable-native-access=ALL-UNNAMED $JAVA_OPTS -jar "$JAR_PATH" "$@" &
PID="$!"
echo "$PID" > "$HOME_DIR/pid"
wait "$PID"

popd || exit 9