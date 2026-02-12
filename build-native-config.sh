#!/bin/bash
set -e

CONFIG_REL_PATH="src/main/resources/META-INF/native-image"

echo "Building the Agent Runner image..."
docker build -t mock-mini-agent -f Dockerfile-agent .

echo "Running app with Native Image Agent..."
docker run --rm \
  -v "$(pwd)":/app:z \
  -w /app \
  -p 9001:9001 \
  --entrypoint /bin/bash \
  mock-mini-agent -c "
    ls -F /app/src/ > /dev/null || { echo 'ERROR: /app/src not found. Volume mount failed.'; exit 1; }

    mkdir -p /app/$CONFIG_REL_PATH
    mvn clean package -DskipTests

    java -agentlib:native-image-agent=config-merge-dir=/app/$CONFIG_REL_PATH \
         -jar target/mock-mini.jar &

    PID=\$!

    echo 'Waiting for server...'
    sleep 5

    echo 'Checking endpoints...'
    curl -v http://localhost:9001/health
    curl -v http://localhost:9001/native
    curl -v http://localhost:9001/
    curl -v http://localhost:9001/mock-rules

    echo 'Stopping server...'
    kill \$PID
    wait \$PID
"

echo "Done! (src/main/resources/META-INF/native-image)"