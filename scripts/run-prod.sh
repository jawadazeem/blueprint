#!/usr/bin/env bash

cd "$(dirname "$0")/.."
export SPRING_PROFILES_ACTIVE=prod
java -jar target/blueprint.jar

# Run the script with:
#chmod +x scripts/run-prod.sh
#./scripts/run-prod.sh