#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ -z "$JAVA_HOME" ] && [ -d "/c/Program Files/Java/jdk-21" ]; then
    export JAVA_HOME="/c/Program Files/Java/jdk-21"
fi

echo "========================================================"
echo "Building Venerable Knowledge (Fabric 1.20.x)..."
echo "========================================================"
cd "$DIR/fabric"
./gradlew "$@"

echo "========================================================"
echo "Building Venerable Knowledge (Forge 1.20.x)..."
echo "========================================================"
cd "$DIR/forge"
./gradlew "$@"

echo "========================================================"
echo "All 1.20.x builds completed successfully!"
echo "========================================================"
