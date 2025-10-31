# CICS GenApp Cloud Modernization - Product Requirements Document (PRD)

**Author:** Niklas
**Date:** October 31, 2025
**Project Level:** Level 3
**Target Scale:** Enterprise Platform with Phased Delivery

---

## Goals and Background Context

### Goals

1. **Enable Gradual Migration Without Operational Disruption** - Implement strangler pattern to route new traffic to Spring Boot services while legacy COBOL remains operational; support dual-run capabilities for risk-free, phased cutover over 12 months

2. **Accelerate Feature Delivery with Modern Developer Experience** - Reduce feature cycle time from COBOL/CICS development to agile Spring Boot/React sprints; eliminate COBOL expertise dependency; enable Java/JavaScript developer hiring and productivity

3. **Establish Cloud-Native Architecture Foundation** - Build Spring Boot + React + API Gateway patterns ready for cloud deployment (AWS/Azure/GCP/hybrid); reduce operational complexity and z/OS hosting costs through modern infrastructure

4. **Modernize User Experience with Feature Parity Plus New Capabilities** - Replace 3270 terminal interface with responsive web UI maintaining full feature parity; position system for incremental UX improvements and new features after cutover

5. **Create Reusable, Testable, Maintainable Codebase** - Establish patterns and architecture that support long-term team scalability and system evolution; build comprehensive test coverage and deployment automation from day one

### Background Context

CICS GenApp is a 40+ year-old enterprise insurance application built on COBOL for z/OS with CICS Transaction Server, 3270 terminal UI, and dual Db2/VSAM storage. While technically sound, the system suffers from rising operational costs, difficulty attracting modern developers, and limited integration with contemporary systems.

The modernization initiative addresses multiple business drivers simultaneously: reducing z/OS hosting costs, improving developer experience and retention, enabling cloud migration, and establishing a foundation for faster feature delivery. By adopting a **strangler pattern** with Spring Boot backend, React frontend, and API Gateway layer, the project maintains zero operational disruption to current users while gradually absorbing functionality into modern services. Research has validated this approach as proven with COBOL systems, achievable within the 12-month timeline, and flexible across cloud providers.

---

## Requirements

### Functional Requirements

#### A. Customer Management (4 FRs)
- **FR001:** Add customer record with required personal/contact information (COBOL: SSC1 transaction equivalent)
- **FR002:** Search and retrieve customer by ID or name
- **FR003:** Update customer personal/contact details
- **FR004:** Soft-delete customer (mark inactive while preserving history)

#### B. Policy Management (6 FRs)
- **FR005:** Create policy (customer linked, type-specific attributes) - supports Motor/Endowment/House/Commercial
- **FR006:** Search policies by customer ID or policy number
- **FR007:** Retrieve policy details with type-specific attributes
- **FR008:** Update policy status (active/renewed/lapsed)
- **FR009:** Delete policy (soft-delete with audit trail)
- **FR010:** Policy type validation (Motor, Endowment, House, Commercial)

#### C. API Layer & Service Exposure (4 FRs)
- **FR011:** REST API endpoints for all CRUD operations (standardized, OpenAPI-documented)
- **FR012:** Request/response payload validation with clear error messaging
- **FR013:** Audit logging for all data mutations (who, what, when, why)
- **FR014:** API versioning support for backward compatibility during transition

#### D. Strangler Pattern & Traffic Management (3 FRs)
- **FR015:** Feature toggles to control traffic routing (new Spring Boot vs legacy COBOL)
- **FR016:** Dual-write capability for data consistency during transition (Db2 + VSAM sync)
- **FR017:** Fallback/circuit breaker patterns if legacy system unavailable

#### E. Authentication & Authorization (3 FRs)
- **FR018:** User authentication (replaces legacy terminal authentication)
- **FR019:** Role-based access control (customer service agents, admin, etc.)
- **FR020:** Session management with timeout/refresh token support

#### F. Reporting & Analytics (3 FRs)
- **FR021:** Customer summary reports (count, segments, etc.)
- **FR022:** Policy summary reports (by type, status, customer)
- **FR023:** System activity/audit trail reports

**Total: 23 Functional Requirements**

### Non-Functional Requirements

#### NFR001: Performance & Response Time
- API endpoint response times: **< 200ms target**, < 500ms maximum acceptable
- Database query response: < 100ms for typical operations
- Page load time (React frontend): < 2 seconds on typical network (4G/broadband)
- Throughput: Support **1M+ API calls per day** (baseline; scale to 2-3x during transition)
- P99 latency: < 500ms for predictable performance under load

#### NFR002: Availability & Reliability
- System uptime: **99.5%** (rolling 12-month SLA)
- Graceful degradation: Spring Boot services fail independently; legacy COBOL remains unaffected
- Automated failover/circuit breaker patterns for API→COBOL routing
- Data consistency: Dual-write verification (Db2 ✓ VSAM ✓) before acknowledging writes during transition period
- Recovery Time Objective (RTO): < 1 hour for infrastructure failure; < 5 minutes for service restart

#### NFR003: Security & Compliance
- All API communications encrypted in transit (TLS 1.2+)
- At-rest encryption for sensitive data (PII) in database
- No plain-text passwords in logs or error messages
- Audit trail for all data mutations (immutable log for compliance)
- Role-based access control (RBAC) prevents unauthorized data access
- OWASP Top 10 compliance (injection, XSS, CSRF protections)

#### NFR004: Scalability & Cloud-Readiness
- Stateless service design (Spring Boot) enables horizontal scaling
- Support container deployment (Docker) for cloud platforms (AWS/Azure/GCP)
- Database connection pooling for concurrent load
- API design supports pagination and filtering (prevents large payload issues)
- Infrastructure-as-Code (IaC) for repeatable deployments

#### NFR005: Maintainability & DevOps
- Comprehensive logging (structured JSON logs, searchable)
- Distributed tracing for request flow across services
- Automated testing: Unit (80%+ coverage), Integration, API contract tests
- Deployment pipeline: CI/CD with automated build, test, deploy
- Monitoring & alerting on key metrics (error rates, latency, resource usage)
- Documentation: API docs (OpenAPI), runbooks, architecture diagrams

---

## User Journeys

### Journey 1: Agent Creates New Customer & Policy (Primary Use Case)

1. Agent accesses web dashboard (login via authentication)
2. Searches for customer (not found → creates new)
3. Enters customer details (personal/contact info)
4. System validates input and confirms creation
5. Agent creates new policy for customer
6. Selects policy type (Motor/Endowment/House/Commercial)
7. Enters type-specific attributes
8. System validates data consistency
9. Policy created; confirmation with policy number displayed
10. System logs action in audit trail

### Journey 2: Agent Updates Existing Policy Status

1. Agent logs in and searches policies
2. Filters by customer or policy number
3. Selects policy from results
4. Reviews current status and details
5. Updates status (active → renewed/lapsed)
6. System validates policy state transition
7. Confirms update; displays updated policy info
8. Audit entry logged automatically

### Journey 3: Compliance Officer Reviews Audit Trail

1. Officer logs in with compliance role
2. Navigates to audit/reports section
3. Filters by date range, user, operation type, entity
4. Reviews immutable audit log entries
5. Exports report to CSV for regulatory submission
6. System ensures no entries can be modified/deleted

### Journey 4: Agent Validates New System Against Legacy (Parallel Run - Transition Phase)

**Context:** During the 12-month transition, agents test new Spring Boot system against legacy COBOL to verify data consistency and build confidence before full cutover.

1. **Agent logs into dual system mode** - Split-screen or tab-based interface showing both new (Spring Boot) and legacy (COBOL terminal)
2. **Agent performs operation in new system** - Example: Creates customer "Jane Smith" in Spring Boot UI, receives confirmation with customer ID
3. **Agent replicates same operation in legacy system** - Opens COBOL 3270 terminal, enters identical data
4. **System performs automated comparison** - Queries both Db2 and VSAM, verifies data match
5. **System displays validation result** - ✅ MATCH or ⚠️ DISCREPANCY with specific field differences
6. **Agent logs outcome** - Documents validation for audit trail
7. **Team uses validation data for rollout decisions** - After 100 successful parallel runs, feature toggles advance traffic percentage (10% → 50% → 100%)

**Validation Example:**
- Operation: Add Motor Policy for existing customer
- New system: POST /api/v1/policies → 201 Created, policyId: POLICY-2024-004521
- Legacy system: SSP1 transaction → Policy created, ID: P001-0847-00001
- Validation: ✅ Data match (ID format differs but expected), Db2 and VSAM consistent
- Outcome: Feature validated for traffic cutover

---

## UX Design Principles

### UX Principles

1. **Simplicity Over Features** - Intuitive workflows that agents can learn in hours, not days
2. **Efficiency for Power Users** - Keyboard shortcuts, quick actions, bulk operations for experienced agents
3. **Error Prevention & Recovery** - Clear validation, confirmation dialogs, undo/rollback where possible
4. **Responsive & Accessible** - Works on desktop, tablet, modern browsers; WCAG AA compliance for accessibility

### Design System: "Clarity Enterprise Design System" (CEDS)

**Design Foundation:**
- **Base Component Library:** Material Design 3 (MUI React) - battle-tested, accessible, customizable
- **Color Palette:** Professional, accessible (Primary: Deep blue, Secondary: Teal, Status colors)
- **Typography:** System fonts (Inter/Roboto for web, platform-native fallbacks)
- **Spacing:** 8px grid system (multiples: 8, 16, 24, 32, 48)
- **Elevation/Shadows:** Subtle, 2-4 levels for depth

**Key Interaction Patterns:**

| Pattern | Usage | Example |
|---------|-------|---------|
| **Search + List + Detail** | Customer/Policy workflows | Search → results table → detail panel |
| **Guided Wizard** | Policy creation (type-specific) | Step 1: Basic info → Step 2: Type details → Step 3: Review |
| **Data Table** | Lists with sorting, filtering, pagination | Customer list (searchable, sortable) |
| **Form Validation** | Real-time & on-submit | Phone format validation, required field markers |
| **Toast Notifications** | Quick feedback | "Customer created successfully" (2s auto-dismiss) |
| **Modal Dialogs** | Confirmations, complex flows | "Confirm delete policy?" with reason field |
| **Side Navigation** | Main menu, persistent | Dashboard, Customers, Policies, Audit, Reports, Admin |
| **Status Badges** | Policy state visualization | "Active" (green), "Lapsed" (gray), "Pending" (orange) |

**Key Interactions:**
- Keyboard shortcuts for power users (Ctrl+S save, Ctrl+F search, Ctrl+K quick command)
- Quick actions on list rows (edit, view, more actions)
- Bulk operations (select multiple, bulk update status)
- Smart search (search across ID, name, policy number simultaneously)
- Inline form errors with helpful messages

**Accessibility (WCAG AA):**
- Color not the only indicator (icons + text for status)
- 4.5:1 contrast ratio for all text
- Keyboard navigation throughout
- Screen reader support (semantic HTML, ARIA labels)
- Focus indicators visible on all interactive elements

### UI Design Goals

**Platform:** Responsive web application (React SPA)
- Desktop (Chrome, Firefox, Safari, Edge) - primary
- Tablet (iPad, Android) - secondary (responsive design)
- Mobile browsers - read-only access supported

**Core Screens:**

| Screen | Purpose | Users |
|--------|---------|-------|
| **Dashboard** | Overview of workload, quick actions, key metrics | All agents |
| **Customer Search & List** | Find customer by ID, name, partial match | Agents |
| **Customer Detail & Edit** | View/edit customer info, linked policies, history | Agents |
| **Policy Search & List** | Find policy by number, customer, type, status | Agents |
| **Policy Detail & Edit** | View/edit policy, type-specific attributes | Agents |
| **Policy Create Wizard** | Guided multi-step form for new policy (type-specific) | Agents |
| **Audit Log Viewer** | Search, filter, export audit trail | Compliance officers |
| **Reports** | Summary reports (customer, policy, activity) | Managers/Compliance |
| **Admin Console** | User management, system settings, feature toggles | Admins |
| **Parallel Run Validator** | Track validation results during transition | Agents + Team |

---

## Epic List

**Strangler Pattern Epic Sequencing Strategy**

For a strangler pattern migration, the epic sequence must:
1. **Establish foundation first** (infrastructure, patterns, CI/CD)
2. **Gradually absorb functionality** from legacy to new system
3. **Validate each phase** before moving to next
4. **Maintain operational stability** throughout

### Epic 1: Cloud Foundation & API Gateway Layer
- **Goal:** Establish infrastructure, CI/CD pipeline, API foundation, and strangler pattern setup
- **Delivers:** Spring Boot starter project, API Gateway, database connectivity (Db2), deployment pipeline, feature toggle system
- **Value:** Teams can begin new development; infrastructure ready for gradual traffic routing
- **Story Count:** 8-10 stories
- **Estimated Duration:** Weeks 1-6

### Epic 2: Customer Service API & Spring Boot Layer
- **Goal:** Implement complete customer management in Spring Boot (API + Db2)
- **Delivers:** Customer CRUD APIs, business logic layer, validation framework
- **Enables:** Parallel testing of customer operations (new vs legacy)
- **Validation:** Agents run 50+ parallel customer create/update operations, validate data match
- **Story Count:** 6-8 stories
- **Estimated Duration:** Weeks 7-10

### Epic 3: React Frontend - Customer & Policy Management
- **Goal:** Build responsive React UI replacing 3270 terminal
- **Delivers:** Dashboard, customer search/detail/create, policy search/detail, form validation
- **Enables:** Agents use web UI for first time; can simultaneously test against legacy
- **Story Count:** 8-10 stories
- **Estimated Duration:** Weeks 11-14

### Epic 4: Parallel Run Validation Framework
- **Goal:** Implement dual-write, validation tooling, and traffic routing for safe cutover
- **Delivers:** Dual-write sync, validation dashboard, feature toggle management, circuit breaker patterns
- **Enables:** Automatic traffic routing from new → legacy with rollback capability
- **Story Count:** 4-5 stories
- **Estimated Duration:** Weeks 15-18

### Epic 5: Policy Management API & React UI
- **Goal:** Complete policy lifecycle in Spring Boot + React (all 4 types)
- **Delivers:** Policy CRUD APIs (Motor/Endowment/House/Commercial), type-specific validation, React UI for all policy types
- **Enables:** Full feature parity with legacy system achieved
- **Story Count:** 8-10 stories
- **Estimated Duration:** Weeks 19-24

> **Note:** Detailed epic breakdown with full story specifications is available in [epics.md](./epics.md)

---

## Out of Scope

### Out of Scope (v1.0 Delivery - 12 Month MVP)

#### A. Legacy System Retirement (Phase 2)
- **Deferred:** Full decommissioning of COBOL/CICS/z/OS environment
- **Reason:** 12-month timeline focuses on parallel run and cutover to new system; actual z/OS shutdown happens post-cutover
- **Planned for:** Month 13-24 (post-MVP phase)

#### B. Advanced Reporting & Business Intelligence
- **Deferred:** Data warehouse, BI dashboards (Tableau/Power BI), predictive analytics
- **In Scope:** Basic summary reports (customer count, policy status, audit trail)
- **Reason:** Core transaction system first; analytics can follow once cutover is stable
- **Planned for:** Epic 6 (future phase)

#### C. Third-Party Integrations
- **Deferred:** Integrations with claims systems, underwriting platforms, payment gateways, email/SMS services
- **Reason:** Current system doesn't expose these; adds architectural complexity
- **Note:** API Gateway architecture enables future integrations without core changes
- **Planned for:** Post-MVP expansion

#### D. Mobile Native Applications
- **Deferred:** Native iOS/Android apps
- **In Scope:** Responsive web app works on mobile browsers (read-only for agents in field)
- **Reason:** Web-first approach faster; can add native apps later if needed
- **Timeline:** If needed, Epic 7+ (post-cutover)

#### E. Data Migration Tooling
- **Deferred:** Automated bulk migration of historical data from VSAM to modern database
- **Reason:** Parallel run keeps legacy data intact; cutover happens transactionally per operation
- **Note:** Historical audit trail remains in COBOL/VSAM system for compliance
- **Approach:** Archive VSAM data post-cutover; new transactions only in Spring Boot/Db2

#### F. Advanced Performance Optimization
- **Deferred:** Caching layers (Redis), database query optimization, CDN for static assets
- **In Scope:** Baseline performance targets (NFR001) using standard patterns
- **Reason:** Optimize after measuring real load; premature optimization wastes time
- **Planned for:** Post-MVP performance tuning phase

#### G. Multi-Tenancy / SAAS Conversion
- **Deferred:** Converting single-tenant system to multi-tenant SaaS
- **Scope:** Single-tenant insurance company application
- **Reason:** Adds complexity; not required for business drivers
- **Future:** Can refactor to multi-tenant later if needed

#### H. Compliance & Regulatory Changes
- **Deferred:** New compliance requirements (GDPR beyond current scope, PCI-DSS, SOX)
- **In Scope:** Maintain current audit trail and data protection standards
- **Reason:** Assume no new compliance mandates during 12-month project
- **Note:** Architecture designed to support future compliance additions

#### I. Legacy COBOL Business Logic Rewrite
- **Deferred:** Rewriting COBOL business rules in Java
- **Scope:** Keep COBOL logic intact; Spring Boot calls COBOL via APIs during transition
- **Reason:** Reduces migration risk; COBOL remains trusted system-of-record until validated
- **Timeline:** Post-cutover, can refactor business rules incrementally

#### J. Load Testing & Chaos Engineering
- **Deferred:** Comprehensive load testing (100k+ concurrent users), failure scenario testing
- **In Scope:** Unit/integration/API contract testing; manual validation during parallel run
- **Reason:** System is not greenfield; baseline performance known; test high-risk areas
- **Planned for:** Post-MVP performance validation phase

### Future Phases (Post-MVP, Month 13+)

These are explicitly acknowledged for future consideration:

1. **Phase 2: Advanced Reporting & BI** (Epics 6-7) - Data warehouse, dashboards, analytics
2. **Phase 3: Integration Hub** (Epics 8-9) - Third-party API integrations, messaging, webhooks
3. **Phase 4: Performance & Scaling** (Epic 10) - Caching, optimization, advanced patterns
4. **Phase 5: Legacy Retirement** - Full COBOL/CICS/z/OS decommissioning

---

**PRD Complete!** Strategic document is finalized.

Next: Epic breakdown with detailed stories in [epics.md](./epics.md)