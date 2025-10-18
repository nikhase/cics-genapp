# Backend (Spring Boot)

This module will host the REST APIs that replace the original COBOL transactions. Keep the structure opinionated but approachable for learning.

## Planned Stack
- Java 21, Spring Boot 3.x
- Maven build (`mvnw` checked in for reproducibility)
- Spring Web, Spring Validation, Spring Data JPA, Spring Security (JWT), Flyway, Testcontainers

## Package Layout
```
com.ibm.genappcodex
├── customer         # Customer domain (entities, DTOs, services, controllers)
├── policy           # Policy domain to be implemented in later slices
├── shared           # Cross-cutting utilities (error handling, config)
└── config           # Security, OpenAPI, persistence configuration
```

## Immediate Tasks
1. Generate the project using Spring Initializr (group `com.ibm`, artifact `genapp-codex`).
2. Add Flyway migrations describing the customer tables derived from copybooks.
3. Implement the `/api/customers/{id}` read endpoint with integration tests using Testcontainers (PostgreSQL).
4. Configure JWT auth placeholder (hard-coded secret initially) to prepare for secure endpoints.

## Docker
Create a `Dockerfile` that packages the service via `./mvnw clean package` and runs the fat JAR with `java -jar`. This directory already contains a placeholder Dockerfile to update later.

## Testing Strategy
- Unit tests via JUnit + Mockito for service logic.
- Integration tests with Spring Boot Test + Testcontainers.
- Contract tests (Spring Cloud Contract) once the frontend consumes the APIs.

Document insights in `../docs/learning-plan.md` after each milestone so you track Spring Boot progress.
