#!/bin/bash

# creates openapi/index.html using openapi-generator-cli

echo building OpenAPI
./mvnw -Dmaven.test.skip=true package

if [ "$?" == "0" ]; then
  docker compose -f ./generate-openapi-html.yml run --rm generator
fi

echo shutting down compose stack
docker compose -f ./generate-openapi-html.yml down
