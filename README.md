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
