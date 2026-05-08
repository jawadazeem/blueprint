#!/usr/bin/env bash

cd "$(dirname "$0")/.."
export SPRING_PROFILES_ACTIVE=dev
java -jar target/blueprint.jar

# Run the script with:
#chmod +x scripts/run-dev.sh
#./scripts/run-dev.sh