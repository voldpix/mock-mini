#!/bin/bash

set -e

echo "Building mock-mini native executable..."

docker build -t mock-mini-builder .

echo "Extracting executable..."
docker create --name mock-mini-extract mock-mini-builder
docker cp mock-mini-extract:/build/target/mock-mini ./mock-mini-linux

docker rm mock-mini-extract

chmod +x mock-mini-linux

# reduce size
strip -s mock-mini-linux

echo "Build complete: mock-mini-linux"