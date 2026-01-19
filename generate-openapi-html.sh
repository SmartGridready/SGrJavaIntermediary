#!/bin/bash

# creates openapi/index.html using openapi-generator-cli

echo building OpenAPI
./gradlew -x test bootJar

if [ "$?" == "0" ]; then
  docker compose -f ./generate-openapi-html.yml run --rm generator
fi

echo shutting down compose stack
docker compose -f ./generate-openapi-html.yml down
