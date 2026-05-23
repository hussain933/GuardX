#!/bin/sh
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
exec java -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
