# Slice 01 – Customer Inquiry

## Objective
Deliver a read-only customer inquiry experience that mirrors the SSC1 transaction, powered by the new Spring Boot backend and React frontend.

## Acceptance Criteria
- Customer data persisted in PostgreSQL with Flyway-managed schema.
- `/api/customers/{id}` returns mapped DTOs with status codes for not found/validation errors.
- React page loads customer details, handles errors gracefully, and provides simple search.
- Docker Compose environment runs backend, frontend, and database together.
- Automated tests cover service logic, repository integration, and UI happy-path flow.

## Task Backlog
### Backend
- Reverse-engineer COBOL copybooks (`LGCMAREA`, `LGPOLICY`) into entity + Flyway migration scripts. ✅
- Implement JPA entities, repositories, service layer, and controller for customer inquiry. ✅
- Add MapStruct (or manual mapper) to convert entities to DTOs. ✅
- Create integration tests using Testcontainers for `/api/customers/{id}` and search endpoint. ✅

### Frontend
- Scaffold React app and configure TypeScript aliases + linting. ✅
- Build API client wrapper (axios + interceptors for future auth). ✅
- Implement customer inquiry page with search results table, selection, and detail panel. ✅
- Write Cypress e2e test using mocked API (MSW) until backend is ready.

### Data
- Draft sample customer data set for development seeding via Flyway or data loader. ✅
- Create mapping documentation showing field correspondence between legacy VSAM/Db2 and PostgreSQL columns.

### Infrastructure
- Finalize backend/frontend Dockerfiles once projects are initialized. ✅
- Update `docker-compose.yml` services with build args and volume mounts for local dev. ✅
- Add GitHub Actions workflow: lint + unit tests (backend/frontend) on pull requests.

### Learning & Documentation
- Record key takeaways (Spring Boot controllers, React Query patterns) in `docs/learning-plan.md`.
- Document API contract in OpenAPI (Springdoc) and expose via `/v3/api-docs` + Swagger UI.

## Status Notes
- Customer inquiry backend now supports list + search with deterministic ordering and DTO mapping.
- React page shows default top customers, search filtering, row selection, and detail panel.
- Testcontainers integration test covers fetch + search; Maven execution currently blocked by offline repository access (restricted network). Re-run `mvn -Dmaven.repo.local=genapp-codex/.m2 test` once dependencies are cached.
