# Modernization Roadmap

## Phase 0 – Foundations
- Validate Spring Boot + React stack with hello-world services.
- Establish Docker Compose baseline (backend, frontend, PostgreSQL).
- Wire GitHub Actions workflow skeleton for lint/test/build.

## Phase 1 – Customer Inquiry Slice
- Model customer schema in PostgreSQL from COBOL copybooks.
- Deliver `/api/customers/{id}` and `/api/customers/search` endpoints.
- Build React customer inquiry view mirroring SSC1.
- Create automated tests (JUnit, Cypress) and basic observability (HTTP logging, health checks).

## Phase 2 – Customer Maintenance
- Implement customer create/update flows (Spring validation, transaction handling).
- Introduce authentication guard (JWT issuance, Role-based access control).
- Add form validation and optimistic UI updating in React.

## Phase 3 – Policy Issuance
- Extend domain model for policies, referencing legacy logic in `LGAP*` programs.
- Add policy creation and inquiry APIs, reusing customer data.
- Incorporate asynchronous processing if needed for long-running tasks.

## Phase 4 – Operational Hardening
- Introduce observability stack (Prometheus, Grafana, Loki or ELK).
- Run load tests, resilience drills, and finalize backup/restore procedures for PostgreSQL.
- Prepare cutover plan, data migration strategy, and retirement checklist for COBOL transactions.

## Phase 5 – Expansion & Optimization
- Evaluate additional modernization opportunities (event streaming, reporting services).
- Assess Kubernetes adoption once service portfolio or scaling demands grow.
- Capture lessons learned and update organizational standards.
