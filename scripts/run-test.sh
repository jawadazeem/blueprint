#!/usr/bin/env bash

cd "$(dirname "$0")/.."
export SPRING_PROFILES_ACTIVE=test
java -jar target/blueprint.jar

# Run the script with:
# chmod +x scripts/run-test.sh
#./scripts/run-test.sh

# Same as: java -Dspring.profiles.active=test -jar target/blueprint.jar