# GenApp Codex Modernization

This folder is the home for the Spring Boot + React rewrite of the CICS GenApp insurance system. The goal is to iterate feature-by-feature, stay Docker-first for deployment, and keep the architecture simple enough for hands-on learning while remaining production-ready.

## Structure
- `backend/` – Spring Boot service modules (REST APIs, business logic, data migrations) plus Maven build files.
- `frontend/` – React single-page application for customer and policy workflows.
- `docs/` – Roadmap, slice plans, and personal learning notes.
- `docker-compose.yml` – Local deployment definition (backend, frontend, PostgreSQL, supportive tooling).

## Modernization Approach
1. **Strangler façade** – Wrap existing CICS flows to keep the green-screen live while Spring services are implemented.
2. **Domain slices** – Deliver thin end-to-end increments (customer inquiry → customer maintenance → policy issuance).
3. **12-factor services** – Externalize config, keep stateless services, and rely on PostgreSQL for persistence.
4. **Automated quality gates** – Unit, integration, and end-to-end tests enforced through GitHub Actions before container builds.

## Current Focus
The first modernization slice targets read-only customer inquiry. See `docs/slice-01-customer-inquiry.md` for backlog items, acceptance criteria, and learning goals.

## Getting Started
1. Walk through the learning plan in `docs/learning-plan.md` to prepare the required Java, Spring, React, and Docker skills.
2. Initialize the Spring Boot backend using Spring Initializr (Maven, Java 21, Web, Validation, Spring Data JPA, Flyway, PostgreSQL).
3. Scaffold the React app with Vite + TypeScript (or CRA if preferred) and integrate axios/React Query for data fetching.
4. Use `scripts/run-dev.sh` (or VS Code tasks) for local dev servers—the script boots the PostgreSQL container via Docker Compose and then launches Spring Boot + Vite. Use `docker compose up --build` when you want the full stack running inside containers.

### Resetting the Local Database
If you previously ran the stack with PostgreSQL 16, remove the old volume before starting the dev script so Flyway can apply migrations against PostgreSQL 15:

```bash
docker compose -f genapp-codex/docker-compose.yml down
docker volume rm genapp-codex_db_data
```

Afterwards rerun `./genapp-codex/scripts/run-dev.sh`; the script recreates the volume and loads the seed data automatically.

## Next Milestones
- Backfill customer schema in PostgreSQL using Flyway migrations.
- Implement the `/api/customers/{id}` endpoint with integration tests.
- Build the React customer detail view mirroring SSC1 read-only data.

Document key learnings along the way so the modernization doubles as your full-stack training log.
