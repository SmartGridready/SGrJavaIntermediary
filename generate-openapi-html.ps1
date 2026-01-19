# creates openapi/index.html using openapi-generator-cli

Write-Host "building OpenAPI"
.\gradlew.bat -x test bootJar

if ($?) {
  docker compose -f .\generate-openapi-html.yml run --rm generator
}

Write-Host "shutting down compose stack"
docker compose -f .\generate-openapi-html.yml down
