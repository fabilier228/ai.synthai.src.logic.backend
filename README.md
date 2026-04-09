# Logic Backend - CI & Basic Information

## CI (Continuous Integration)

- Every push/PR to main, develop, feature/_, fix/_, hotfix/\* triggers a GitHub Actions pipeline:
  - Builds the project with Maven (`./mvnw clean package`)
  - Runs tests (`./mvnw test`)
  - Builds a Docker image
  - Scans the image with Trivy (results in logs)
- No extra tokens or secrets required (unless you use SARIF upload).

## How to run locally

- Build: `./mvnw clean package`
- Tests: `./mvnw test`
- Docker build: `docker build -t logic-backend:dev .`

## Integration Test Reports

- Run integration tests (example class): `./mvnw test -Dtest=ControllersIntegrationTest`
- Generate HTML test report: `./mvnw surefire-report:report-only`

Generated artifacts:

- `target/surefire-reports/*.xml` (JUnit XML for CI)
- `target/site/surefire-report.html` (HTML report)
