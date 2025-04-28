@echo off

REM creates openapi/index.html using openapi-generator-cli

echo building OpenAPI
mvnw.cmd -Dmaven.test.skip=true package && docker compose -f .\generate-openapi-html.yml run --rm generator

echo shutting down compose stack
docker compose -f .\generate-openapi-html.yml down
