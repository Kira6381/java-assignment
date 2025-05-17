#!/bin/bash

mkdir -p out

# COMPILE IT FIRST
javac -cp "lib/mysql-connector-j-8.3.0.jar" -d out $(find src -name "*.java")

echo "Build complete."

