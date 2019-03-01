#!/usr/bin/env sh
# Build and then run the validator

./gradlew --quiet installDist

./build/install/openc2-json-schema/bin/openc2-json-schema "$@"
