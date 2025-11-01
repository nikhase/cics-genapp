# CICS GenApp Cloud Modernization - Product Requirements Document (PRD)

**Author:** Niklas
**Date:** November 1, 2025
**Project Level:** Level 3 (Enterprise Cloud Modernization)
**Target Scale:** Enterprise Platform with Phased Delivery

---

## Goals and Background Context

### Goals

1. **Enable Gradual Migration Without Operational Disruption** - Implement strangler pattern to route new traffic to Spring Boot services while legacy COBOL remains operational; support dual-run capabilities for risk-free, phased cutover over 12 months

2. **Accelerate Feature Delivery with Modern Developer Experience** - Reduce feature cycle time from COBOL/CICS development to agile Spring Boot/React sprints; eliminate COBOL expertise dependency; enable Java/JavaScript developer hiring and productivity

3. **Establish Cloud-Native Architecture Foundation** - Build Spring Boot + React + PostgreSQL + Kubernetes patterns ready for cloud deployment (AWS/Azure/GCP/hybrid); reduce operational complexity and z/OS hosting costs through modern infrastructure

4. **Modernize User Experience with Feature Parity Plus New Capabilities** - Replace 3270 terminal interface with responsive web UI maintaining full feature parity; position system for incremental UX improvements and new features after cutover

5. **Create Reusable, Testable, Maintainable Codebase** - Establish patterns and architecture that support long-term team scalability and system evolution; build comprehensive test coverage and deployment automation from day one

### Background Context

CICS GenApp is a 40+ year-old enterprise insurance application built on COBOL for z/OS with CICS Transaction Server, 3270 terminal UI, and dual Db2/VSAM storage. While technically sound, the system suffers from rising operational costs, difficulty attracting modern developers, and limited integration with contemporary systems.

The modernization initiative addresses multiple business drivers simultaneously: reducing z/OS hosting costs, improving developer experience and retention, enabling cloud migration, and establishing a foundation for faster feature delivery. By adopting a **strangler pattern** with Spring Boot backend, React frontend, API Gateway layer, and OIDC authentication, the project maintains zero operational disruption to current users while gradually absorbing functionality into modern services. Research has validated this approach as proven with COBOL systems, achievable within the 12-month timeline, and flexible across cloud providers (AWS, Azure, GCP, or hybrid).

---

## Requirements

### Functional Requirements

#### A. Customer Management (4 FRs)
- **FR001:** Add customer record with required personal/contact information (COBOL: SSC1 transaction equivalent)
- **FR002:** Search and retrieve customer by ID, name, email, or phone with pagination
- **FR003:** Update customer personal/contact details with audit trail
- **FR004:** Soft-delete customer (mark inactive while preserving history for compliance)

#### B. Policy Management (6 FRs)
- **FR005:** Create policy (customer linked, type-specific attributes) - supports Motor/Endowment/House/Commercial
- **FR006:** Search policies by customer ID, policy number, type, or status with pagination
- **FR007:** Retrieve policy details with type-specific attributes and related customer
- **FR008:** Update policy details and change policy status (active/renewed/lapsed) with audit trail
- **FR009:** Delete policy (soft-delete with audit trail, preserve for compliance)
- **FR010:** Policy type validation (Motor, Endowment, House, Commercial with type-specific rules)

#### C. API Layer & Service Exposure (4 FRs)
- **FR011:** REST API endpoints for all CRUD operations (standardized, OpenAPI 3.0 documented)
- **FR012:** Request/response payload validation with clear, field-level error messaging
- **FR013:** Audit logging for all data mutations (who, what, when, why, before/after values)
- **FR014:** API versioning support (/api/v1/) for backward compatibility during transition

#### D. Strangler Pattern & Traffic Management (3 FRs)
- **FR015:** Feature toggles (Unleash) to control traffic routing (new Spring Boot vs legacy COBOL) without redeployment
- **FR016:** Debezium CDC with HTTP callbacks to sync PostgreSQL data changes to legacy Db2 (async, eventual consistency)
- **FR017:** Circuit breaker patterns and graceful fallback if legacy system unavailable; Spring Boot services fail independently

#### E. Authentication & Authorization (3 FRs)
- **FR018:** User authentication via OIDC/Zitadel (external provider, eliminates password management)
- **FR019:** Role-based access control (RBAC: customer service agents, admin, compliance officer)
- **FR020:** Session management with secure token handling and logout

#### F. Reporting & Analytics (3 FRs)
- **FR021:** Customer summary reports (count by status, segments, trends)
- **FR022:** Policy summary reports (by type, status, customer, coverage analysis)
- **FR023:** System activity/audit trail reports (operations log, compliance export)

**Total: 23 Functional Requirements**

### Non-Functional Requirements

#### NFR001: Performance & Response Time
- API endpoint response times: **< 200ms target**, < 500ms maximum acceptable
- Database query response: < 100ms for typical operations
- Page load time (React frontend): < 2 seconds on typical network (4G/broadband)
- Throughput: Support **1M+ API calls per day** baseline; scale to 2-3x during transition
- P99 latency: < 500ms for predictable performance under load

#### NFR002: Availability & Reliability
- System uptime: **99.5%** (rolling 12-month SLA)
- Graceful degradation: Spring Boot services fail independently; legacy COBOL remains unaffected
- Automated failover/circuit breaker patterns for API→legacy routing
- Data consistency: CDC verification (PostgreSQL ✓ Db2 ✓) with validation dashboard
- Recovery Time Objective (RTO): < 1 hour for infrastructure failure; < 5 minutes for service restart

#### NFR003: Security & Compliance
- All API communications encrypted in transit (TLS 1.2+)
- At-rest encryption for sensitive data (PII) in PostgreSQL database
- No plain-text passwords in logs or error messages; PII masking in audit trails
- Audit trail for all data mutations (immutable, append-only, 7-year retention)
- OIDC/Zitadel manages authentication; RBAC prevents unauthorized data access
- OWASP Top 10 compliance (SQL injection, XSS, CSRF, SSRF protections)

#### NFR004: Scalability & Cloud-Readiness
- Stateless service design (Spring Boot) enables horizontal scaling via Kubernetes
- Support container deployment (Docker images) for cloud platforms (AWS/Azure/GCP)
- Database connection pooling (HikariCP) for concurrent load; read replicas for scaling
- API design supports pagination/filtering (prevents large payload issues)
- Infrastructure-as-Code (IaC) with Helm charts for repeatable, environment-agnostic deployments
- Support for multiple Kubernetes clusters (failover, multi-region ready)

#### NFR005: Maintainability & DevOps
- Comprehensive structured JSON logging (searchable, ELK Stack aggregation)
- Distributed tracing for request flow across services (Jaeger) with correlation IDs
- Automated testing: Unit (80%+ coverage), Integration (TestContainers), API (Pact contracts)
- Deployment pipeline: CI/CD with automated build, test, security scan, deploy (GitHub Actions)
- Monitoring & alerting on key metrics (error rates, latency, CPU, memory) via Prometheus/Grafana
- Documentation: OpenAPI 3.0 (Swagger UI), runbooks, C4 architecture diagrams, ADRs

---

## User Journeys

### Journey 1: Agent Creates New Customer & Policy (Primary Use Case)

1. Agent logs in via Zitadel OIDC (SSO with corporate credentials)
2. Navigates to Dashboard and clicks "New Customer"
3. Fills multi-step wizard: Basic Info → Contact → Address → Review
4. System validates input in real-time (email uniqueness, phone format, age validation)
5. Confirms creation; receives customer ID and optional "Create Policy Now" prompt
6. Selects "Create Policy"; wizard opens with customer pre-selected
7. Selects policy type (Motor/Endowment/House/Commercial)
8. Enters type-specific attributes (e.g., registration number for Motor)
9. System validates data consistency within 1-2 seconds
10. Policy created; confirmation displays policy number
11. System logs action in audit trail (user, timestamp, before/after values)
12. Debezium CDC syncs to legacy Db2 asynchronously (within 5 seconds typical)
13. Feature toggle validates both systems match (parallel run dashboard shows result)

### Journey 2: Agent Searches & Updates Existing Customer

1. Agent logs in and navigates to "Customers"
2. Searches by name or email; results show within 1 second
3. Clicks customer row; detail page loads showing full customer info + linked policies
4. Clicks "Edit"; form shows with current values
5. Updates phone number and address
6. System highlights changed fields and shows validation feedback
7. Saves; receives confirmation message
8. Audit log automatically captures: user, timestamp, field changes (phone: "+1-555-1234" → "+1-555-5678")
9. CDC propagates change to Db2 within 5 seconds
10. Agent can view change history on customer detail page (immutable audit log)

### Journey 3: Compliance Officer Reviews Audit Trail

1. Officer logs in with compliance role
2. Navigates to "Audit Logs" section (visible only to compliance/admin roles)
3. Filters by date range (last 30 days), entity (CUSTOMER/POLICY), operation (CREATE/UPDATE/DELETE)
4. Reviews operations: who did what, when, and what changed
5. Exports selected records to CSV for regulatory submission
6. System ensures audit entries cannot be modified/deleted (compliance requirement)

### Journey 4: Agent Validates New System Against Legacy (Parallel Run - Transition Phase)

1. Operations enables feature toggle: `customer-api-enabled = 50%`
2. Agent creates test customer in web UI
3. Debezium CDC syncs change to Db2 (asynchronously)
4. Agent navigates to "Parallel Run Validation" dashboard (admin-only view)
5. Dashboard shows: overall match rate (99.2%), recent operations (last 50), mismatches (if any)
6. Agent clicks on recent operation to see side-by-side comparison:
   - Spring Boot customer: {"firstName": "Jane", "email": "jane@example.com", ...}
   - Legacy Db2 customer: same values
   - Result: ✅ MATCH
7. Dashboard metrics by entity type show customer operations match rate: 100%
8. Confidence increases; toggle bumped to `customer-api-enabled = 100%`

---

## UX Design Principles

1. **Simplicity over Feature Overload** - Minimize clicks to accomplish tasks; wizard-based forms guide users step-by-step
2. **Familiar Mental Models** - Web UI follows standard SPA patterns; navigation is intuitive for users familiar with modern web apps
3. **Real-Time Feedback** - Form validation, loading indicators, success/error messages provide immediate response
4. **Accessibility First** - WCAG 2.1 AA compliance; keyboard navigation, screen reader support, sufficient color contrast
5. **Mobile-Responsive** - Works on desktop, tablet, and mobile devices (Progressive Web App ready)

---

## User Interface Design Goals

1. **Modern, Clean Aesthetic** - Material Design 3 via MUI; Clarity Enterprise Design System theme for insurance/business context
2. **Information Hierarchy** - Key data (customer name, policy status) prominent; secondary data accessible but not overwhelming
3. **Consistent Navigation** - Sidebar menu with role-based visibility; breadcrumbs for context; quick-access shortcuts (Ctrl+K search)
4. **Progressive Disclosure** - Hide advanced options; show power-user features when needed (admin panel, audit logs)
5. **Error Prevention & Recovery** - Confirmation dialogs for destructive actions; clear error messages with recovery suggestions

---

## Epic List

- **Epic 1: Cloud Foundation & Deployment Infrastructure** - Spring Boot starter, PostgreSQL setup, Docker/Kubernetes, CI/CD pipeline, OIDC/Zitadel integration, observability stack
- **Epic 2: Customer Service API & Core Backend** - Customer domain model, CRUD APIs, validation framework, audit logging, OpenAPI documentation
- **Epic 3: React Frontend - Authentication & Core UI** - React SPA setup, Login/Dashboard, customer/policy search and detail pages, form components
- **Epic 4: Parallel Run Validation Framework** - Debezium CDC setup, data comparison service, validation dashboard, feature toggle management, circuit breaker patterns
- **Epic 5: Policy Management API & React UI** - Policy domain models (4 types), CRUD APIs, type-specific validation, React policy pages, customer-policy linking

> **Note:** Detailed epic breakdown with full story specifications is available in [epics.md](./epics.md)

---

## Out of Scope

- **Mobile app** (web-responsive design sufficient for v1; native mobile (iOS/Android) considered post-launch)
- **Advanced analytics/BI** (basic summary reports included; comprehensive BI/data warehouse deferred to Phase 2)
- **Multi-tenant support** (single-tenant for v1; multi-tenant architecture deferred post-launch)
- **Offline mode** (online-only for v1; offline capability considered if business requirement emerges)
- **Advanced search/full-text search** (basic keyword search sufficient; Elasticsearch integration deferred)
- **Direct 3270 terminal access from Spring Boot** (legacy COBOL remains operational; no terminal emulation in new system)
- **Comprehensive legacy Db2 rewrite** (CDC keeps Db2 in sync; full normalization/modernization deferred post-cutover)
- **Third-party integrations** (insurance underwriting systems, payment processors deferred to Phase 2)
