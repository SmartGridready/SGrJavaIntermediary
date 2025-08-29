# creates openapi/index.html using openapi-generator-cli

Write-Host "building OpenAPI"
.\mvnw.cmd "-Dmaven.test.skip=true" package

if ($?) {
  docker compose -f .\generate-openapi-html.yml run --rm generator
}

Write-Host "shutting down compose stack"
docker compose -f .\generate-openapi-html.yml down
