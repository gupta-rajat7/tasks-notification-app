#!/bin/sh

APP_HOME=$(cd "$(dirname "$0")" && pwd -P) || exit
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
if [ -n "$JAVA_HOME" ]; then
    JAVACMD=$JAVA_HOME/bin/java
else
    JAVACMD=java
fi

if ! command -v "$JAVACMD" >/dev/null 2>&1; then
    echo "ERROR: Java is required to run Gradle." >&2
    echo "Set JAVA_HOME to a JDK 17 installation or add java to PATH." >&2
    exit 1
fi

exec "$JAVACMD" -Xmx64m -Xms64m -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
