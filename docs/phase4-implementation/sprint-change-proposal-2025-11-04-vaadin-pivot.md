# Sprint Change Proposal: Pivot to Vaadin Full-Stack Java Architecture
**Date:** November 4, 2025
**Author:** Claude Code (Workflow: correct-course)
**Status:** Pending User Approval
**Change Scope:** Major (Epic-level architecture change)

---

## Section 1: Issue Summary

### Problem Statement
The current React/TypeScript/Vite frontend architecture creates significant friction:
- **Skill Gap:** Developer lacks TypeScript experience, making error troubleshooting difficult
- **Cognitive Overhead:** Context-switching between Java backend and TypeScript/React frontend slows productivity
- **Overengineering:** Material Design + Vite + TypeScript + OIDC complexity exceeds MVP requirements
- **Timeline Impact:** Frontend debugging is blocking backend API progress and team momentum

### Discovery Context
- Stories 3-1, 3-2, 3-3 (React Frontend with OIDC) are in review/in-progress status
- Developer requested simpler approach aligned with Java expertise
- Backend APIs (Epics 1, 2) are stable and complete
- MVP goal is rapid delivery, not sophisticated frontend framework showcasing

### Evidence
1. Cannot troubleshoot TypeScript errors independently
2. React development requires separate build toolchain management
3. OIDC authentication adds complexity not critical for MVP
4. Timeline pressure requires removing non-essential complexity

---

## Section 2: Impact Analysis

### Epic-Level Impact Assessment

| Epic | Current Status | Impact | Action |
|------|---|---|---|
| **Epic 1:** Cloud Foundation & Spring Boot | Complete (✓) | **No change needed** | Keep as-is |
| **Epic 2:** Customer Service APIs | Review status (✓) | **No change needed** | Keep as-is |
| **Epic 3:** React Frontend - Auth & Core UI | In-progress (✗) | **Replace entirely** | Pivot to Vaadin |
| **Epic 4:** Parallel Run Validation | Backlog (▲) | **Simplify UI layer** | Keep backend, replace dashboard with Vaadin |
| **Epic 5:** Policy Management APIs | Backlog (✓) | **No change needed** | Keep backend, UI becomes Vaadin pages |

### Artifact Conflict Analysis

#### PRD Requirements Impact
**Current Requirement:** "React frontend with Material Design 3 and Clarity theme"

**Change Needed:**
- Remove React/TypeScript/Material Design requirement
- Replace with: "Vaadin UI framework with built-in components"
- Remove OIDC authentication from MVP; defer to post-MVP
- Keep all functional requirements intact (CRUD operations, validation, audit logging)

**Sections Affected in PRD:**
- Line 20 (UX Design Goals) - Remove Material Design reference; note Vaadin built-in themes
- Line 183 (Epic 3 description) - Change from React to Vaadin
- Line 185 (React frontend goal) - Replace with Vaadin goal

#### Architecture Document Impact
**Current Architecture:** React SPA → Spring Cloud Gateway → Spring Boot Services

**New Architecture:** Vaadin UI (server-side Java) → Spring Cloud Gateway → Spring Boot Services

**Components Affected:**
- Remove: React build process, Node.js dependencies, TypeScript configuration
- Add: Vaadin dependency (com.vaadin:vaadin-spring-boot-starter)
- Keep: Spring Cloud Gateway, Spring Security, PostgreSQL, all backend services

**Sections Affected:**
- Frontend Architecture (remove React, add Vaadin server-side rendering)
- Deployment (simplify - no separate frontend build)
- Development Workflow (no Node.js/npm, pure Maven build)

#### UI/UX Specification Impact
**Current Specs:** React component library, Material Design patterns

**New Approach:**
- Vaadin built-in components (Grid, Form, Button, TextField, etc.)
- Standard Vaadin theming (Lumo theme, light/dark mode built-in)
- Server-side form validation and data binding
- Responsive layouts using Vaadin's Layout components

**Pages Remain the Same:**
- Login page (now Vaadin Form instead of React)
- Dashboard (now Vaadin layouts instead of React)
- Customer search/list (now Vaadin Grid instead of React DataTable)
- Customer detail/edit (now Vaadin Form instead of React)

### Story-Level Impact

**Stories Affected (Removed from current Epic 3):**
- 3-1: React Project Setup with Vite → **DELETE** (replaced by Vaadin setup)
- 3-2: Login Page + Zitadel OIDC → **DELETE/REPLACE** (simple Spring Security login instead)
- 3-3: Dashboard Page → **DELETE/REPLACE** (Vaadin dashboard instead)
- 3-4 through 3-8: **REPLAN** as Vaadin stories (customer pages, forms, API integration)

**Stories Unaffected (Upstream Dependencies Stable):**
- 1-1 through 1-4: Cloud Foundation & Deployment ✓
- 2-1 through 2-8: Customer Service APIs ✓

**Stories Affected (Downstream - Timeline Changes):**
- 4-3: Parallel Run Validation Dashboard → Replace with Vaadin implementation
- 5-7, 5-8: Policy UI pages → Create as Vaadin pages (much simpler)

### Technical Debt & Cleanup Required
1. **Delete entire frontend directory:** `genapp-frontend/` (React/TypeScript/Vite)
2. **Remove frontend dependencies from pom.xml:** No more Node.js dev setup
3. **Remove OIDC story 1.5** from Epic 1 scope (defer to post-MVP)
4. **Archive git history:** Keep React code in git for reference, but delete working directory

---

## Section 3: Recommended Approach

### Selected Path: Direct Adjustment with Vaadin Pivot (Option 1) ✅

**Rationale:**
- **Skill Alignment:** Pure Java development aligns with team expertise
- **Timeline:** Faster MVP delivery (eliminate TypeScript/React learning curve)
- **Complexity Reduction:** Vaadin's built-in components reduce custom CSS/JS
- **Momentum:** Unblocks backend team to finish Core APIs without frontend delays
- **Risk:** Low - backend is complete and stable; UI is greenfield Vaadin development

### Implementation Strategy

#### Phase 1: Foundation Setup (Days 1-3)
1. Add Vaadin Spring Boot starter dependency to pom.xml
2. Create Vaadin application structure (views package)
3. Configure Spring Security for simple form-based authentication
4. Set up basic Vaadin theme and layout structure
5. Create main navigation sidebar

#### Phase 2: Core UI Pages (Days 4-10)
1. **Login Page** - Vaadin Form with username/password
2. **Dashboard Page** - Welcome page with navigation shortcuts
3. **Customer Search & List** - Vaadin Grid with filtering
4. **Customer Detail Page** - Display customer info + linked policies
5. **Customer Create/Edit Page** - Vaadin Form with validation
6. **Policy List Page** - Vaadin Grid with policy type filter
7. **Policy Detail Page** - Display policy info by type

#### Phase 3: API Integration & Testing (Days 11-14)
1. Connect Vaadin UI to Spring Boot REST APIs
2. Handle API error responses in UI
3. User acceptance testing (UAT)
4. Performance testing (page load times, response handling)

### Effort & Timeline Impact

| Phase | Effort | Timeline | Risk |
|-------|--------|----------|------|
| Phase 1: Foundation | 2-3 days | Minimal delay | Low |
| Phase 2: Core Pages | 5-7 days | **2-3 week improvement** over React | Low |
| Phase 3: Integration | 3-4 days | Minimal delay | Low |
| **Total** | **10-14 days** | **2-week faster than React/TypeScript** | **Low** |

### Risk Assessment

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|-----------|
| Vaadin learning curve | Low | Medium | Vaadin docs excellent; Spring Boot integration straightforward |
| Component limitations | Low | Low | Vaadin has 40+ built-in components; sufficient for CRUD MVP |
| Performance concerns | Low | Low | Vaadin is server-rendered (no SPA overhead); proven at scale |
| Browser compatibility | Very Low | Low | Vaadin handles cross-browser support automatically |

---

## Section 4: Detailed Change Proposals

### 4.1 PRD Changes

#### Change 1: UX Design Principles Section
**Section:** Lines 171-178 (UX Design Principles)

**OLD:**
```
1. **Simplicity over Feature Overload** - Minimize clicks to accomplish tasks...
2. **Familiar Mental Models** - Web UI follows standard SPA patterns...
3. **Real-Time Feedback** - Form validation, loading indicators...
4. **Accessibility First** - WCAG 2.1 AA compliance...
5. **Mobile-Responsive** - Works on desktop, tablet, and mobile (Progressive Web App ready)
```

**NEW:**
```
1. **Simplicity over Feature Overload** - Minimize clicks to accomplish tasks...
2. **Familiar Mental Models** - Web UI follows standard web application patterns (server-rendered, familiar form layouts)...
3. **Real-Time Feedback** - Form validation, loading indicators, success/error messages provide immediate response...
4. **Accessibility First** - WCAG 2.1 AA compliance; Vaadin components have built-in accessibility support...
5. **Responsive Design** - Works on desktop, tablet, and mobile; Vaadin responsive layouts adapt to screen size...
```

**Rationale:** Update to reflect server-side rendering model instead of SPA; Vaadin handles accessibility built-in

---

#### Change 2: UI Design Goals Section
**Section:** Lines 181-188 (User Interface Design Goals)

**OLD:**
```
1. **Modern, Clean Aesthetic** - Material Design 3 via MUI; Clarity Enterprise Design System theme...
```

**NEW:**
```
1. **Modern, Clean Aesthetic** - Vaadin Lumo design system with built-in light/dark theme support...
```

**Rationale:** Vaadin's Lumo theme is modern and professional; no Material Design complexity needed for MVP

---

#### Change 3: Epic List Section
**Section:** Lines 191-197 (Epic List)

**OLD:**
```
- **Epic 3: React Frontend - Authentication & Core UI** - React SPA setup, Login/Dashboard, customer/policy search and detail pages, form components
```

**NEW:**
```
- **Epic 3: Vaadin Frontend - Core UI & User Interface** - Vaadin full-stack Java setup, Login (Spring Security), Dashboard, customer/policy search/detail/create pages, built-in form validation
```

**Rationale:** Clarify Java-only approach; defer OIDC to post-MVP

---

#### Change 4: Out of Scope Addition
**Section:** Line 210 (Out of Scope)

**ADD new line:**
```
- **OIDC Authentication (v1)** (Simple Spring Security form-based authentication sufficient for MVP; OIDC/Zitadel integration deferred to post-launch)
```

**Rationale:** Defer OIDC complexity to post-MVP; unblock faster delivery

---

### 4.2 Architecture Document Changes

#### Change 1: System Components Section
**File:** `docs/architecture.md`

**OLD:**
```
Frontend Layer: React SPA (Node.js build, TypeScript, Material Design)
API Gateway: Spring Cloud Gateway
Backend Services: Spring Boot microservices
Database: PostgreSQL + Db2 sync via Debezium
```

**NEW:**
```
Frontend Layer: Vaadin (server-side Java, built-in components, Lumo theme)
API Gateway: Spring Cloud Gateway
Backend Services: Spring Boot microservices (unchanged)
Database: PostgreSQL + Db2 sync via Debezium (unchanged)
```

**Rationale:** Simplify frontend technology stack; clarify server-side rendering model

---

#### Change 2: Development Workflow Section
**File:** `docs/architecture.md`

**OLD:**
```
Development Setup:
1. Install Node.js + npm
2. Run `npm install` for frontend dependencies
3. Run `npm run dev` for live reload
4. In separate terminal: `mvn spring-boot:run`
5. Access at http://localhost:3000 (frontend) → connects to http://localhost:8080 (backend)
```

**NEW:**
```
Development Setup:
1. Install Java 21 LTS + Maven
2. Run `mvn spring-boot:run`
3. Vaadin development mode provides live reload automatically
4. Access at http://localhost:8080 (Vaadin UI + backend in same process)
```

**Rationale:** Single Maven build process; simpler setup

---

### 4.3 Epic Changes

#### Epic 3 Scope Redefinition

**OLD Epic 3 Name:** "React Frontend - Authentication & Core UI"

**NEW Epic 3 Name:** "Vaadin Frontend - Core User Interface"

**OLD Epic 3 Goal:**
```
Establish React single-page application (SPA) with Vite build tooling,
Material Design components, OIDC authentication via Zitadel, and core UI pages
(Login, Dashboard, Customer CRUD, Policy management).
```

**NEW Epic 3 Goal:**
```
Establish Vaadin full-stack Java frontend integrated with Spring Boot backend,
leveraging Vaadin's built-in components for rapid CRUD UI development.
Implement Login (Spring Security session-based), Dashboard, and core pages
for Customer and Policy management. Focus on MVP delivery with built-in
form validation, responsive layouts, and accessibility support.
```

**Story Breakdown Changes:**

**OLD Stories (DELETE):**
- 3-1: React Project Setup with Vite and Material Design
- 3-2: Login Page and Zitadel OIDC Authentication
- 3-3: Dashboard Page with Navigation and Quick Actions
- 3-4: Customer Search and List Page
- 3-5: Customer Detail and Edit Page
- 3-6: Customer Create Page with Form Wizard
- 3-7: Form Components and Validation UI Library
- 3-8: API Integration and Client Service Layer

**NEW Stories (REPLAN AS VAADIN):**
- 3-1-v2: Vaadin Project Setup with Spring Boot Integration
- 3-2-v2: Login Page with Spring Security Form Authentication
- 3-3-v2: Dashboard Page with Vaadin Navigation Layout
- 3-4-v2: Customer Search & List Page with Vaadin Grid
- 3-5-v2: Customer Detail Page with Vaadin Components
- 3-6-v2: Customer Create/Edit Page with Vaadin Form
- 3-7-v2: Policy List Page with Vaadin Grid
- 3-8-v2: Policy Detail Page with Vaadin Display Components

**Effort Adjustment:**
- OLD estimate: 3-4 weeks (learning React/TypeScript/Material Design)
- NEW estimate: 2-2.5 weeks (Vaadin Java-only development)
- **Improvement: 1-1.5 weeks faster delivery**

---

### 4.4 Sprint Status Updates

**File:** `docs/sprint-status.yaml`

**Changes:**
```yaml
# OLD
3-1-react-project-setup-with-vite-and-material-design: review
3-2-login-page-and-zitadel-oidc-authentication: review
3-3-dashboard-page-with-navigation-and-quick-actions: in-progress

# NEW
3-1-vaadin-project-setup-with-spring-boot-integration: ready-for-dev
3-2-login-page-with-spring-security-form-authentication: ready-for-dev
3-3-dashboard-page-with-vaadin-navigation-layout: ready-for-dev
3-4-customer-search-and-list-page-with-vaadin-grid: ready-for-dev
3-5-customer-detail-page-with-vaadin-components: ready-for-dev
3-6-customer-create-edit-page-with-vaadin-form: ready-for-dev
3-7-policy-list-page-with-vaadin-grid: ready-for-dev
3-8-policy-detail-page-with-vaadin-display-components: ready-for-dev

# REMOVE THESE (DEFER TO POST-MVP)
1-5-oidc-authentication-with-zitadel-integration: → Move to Backlog (post-MVP)
```

---

## Section 5: Implementation Handoff

### Change Scope Classification: **MAJOR**

**Reason:** Epic-level architecture change affecting frontend technology stack, 5+ stories, multiple artifacts (PRD, Architecture, Sprint Status)

### Handoff Recipients & Responsibilities

#### 1. **Product Manager / Solution Architect** (Strategic Decision)
- **Responsibility:** Approve epic redefinition and post-MVP OIDC deferral
- **Decision:** Is descoping OIDC from MVP acceptable for faster delivery?
- **Action:** Confirm MVP scope still meets business requirements with simpler auth

#### 2. **Scrum Master** (Backlog Reorganization)
- **Responsibility:** Replan Epic 3 stories from React to Vaadin approach
- **Deliverables:**
  - Updated 8 Vaadin user stories in docs/stories folder
  - Sprint schedule reflecting 2-2.5 week Epic 3 timeline
  - Backlog move: Story 1.5 (OIDC) → Post-MVP backlog
- **Timeline:** 1-2 days to prepare replan for development team

#### 3. **Development Team** (Implementation)
- **Responsibility:** Execute Vaadin implementation per replan
- **Prerequisites:**
  - Vaadin Spring Boot starter dependency added to pom.xml
  - Vaadin Spring Boot documentation review (4-8 hours)
  - CRUD example from Vaadin docs studied
- **Success Criteria:**
  - All 8 Vaadin stories completed within 2-2.5 weeks
  - Core CRUD pages functional (customer & policy management)
  - API integration verified with backend services
  - No TypeScript/frontend build complexity

#### 4. **Quality Assurance / UAT Team** (Testing)
- **Responsibility:** Test Vaadin UI against functional requirements
- **Test Focus:**
  - Form validation works correctly
  - API calls execute and handle responses
  - Page navigation and sidebar menus functional
  - Cross-browser compatibility (Chrome, Firefox, Safari)
  - Mobile responsiveness (tablet/mobile screens)
- **No change in API testing** - Backend APIs already validated in Epics 1-2

### Success Criteria for Implementation

✅ **Technical Success:**
1. Vaadin project builds cleanly with `mvn clean package`
2. Application runs on `mvn spring-boot:run` with no TypeScript compilation
3. All core CRUD pages load and function without errors
4. API calls from UI to backend services return expected data
5. Forms validate input and display error messages correctly

✅ **Timeline Success:**
1. Epic 3 completed in 2-2.5 weeks (vs. 3-4 weeks for React)
2. No blockers from TypeScript/build tool issues
3. Team productivity increases with Java-only development

✅ **Business Success:**
1. MVP feature set still delivered on time
2. OIDC deferral to post-MVP is acceptable
3. User acceptance testing passes

---

## Section 6: Overall Impact Summary

### What Changes
- ❌ **REMOVE:** React/TypeScript/Vite/Material Design frontend
- ❌ **REMOVE:** Zitadel OIDC from MVP scope (defer to post-MVP)
- ✅ **ADD:** Vaadin Spring Boot full-stack Java UI
- ✅ **ADD:** Spring Security form-based authentication (simple, MVP-ready)

### What Stays the Same
- ✅ All Cloud Foundation & Deployment Infrastructure (Epic 1)
- ✅ All Customer Service APIs & Backend (Epic 2)
- ✅ All Parallel Run Validation Framework backend logic (Epic 4)
- ✅ All Policy Management API & Backend (Epic 5)
- ✅ PostgreSQL, Spring Boot 3.4, Java 21, Docker/Kubernetes readiness

### Timeline Impact
- **Total Project Timeline:** 1-1.5 weeks **IMPROVED** (faster Vaadin delivery vs React)
- **MVP Delivery:** Now achievable in 6-7 weeks instead of 7-8 weeks
- **Post-MVP Phase:** Add OIDC/advanced features with Vaadin capability

### Business Value
1. **Faster MVP:** Unblock backend team; deliver working system sooner
2. **Skill Alignment:** Leverage team Java expertise; eliminate learning curve
3. **Maintainability:** Single codebase (Java) easier to maintain long-term
4. **Cost:** No Node.js/frontend build infrastructure needed
5. **Flexibility:** Vaadin foundation makes adding React/mobile later easier if needed

---

## Approval & Next Steps

**This proposal is pending your explicit approval. Please confirm:**

- [ ] **Approve:** Pivot to Vaadin, descope OIDC from MVP, proceed with Phase 1 setup
- [ ] **Request Changes:** Specific aspects to reconsider or adjust
- [ ] **Reject:** Alternative approach preferred

**Upon Approval, Handoff Sequence:**

1. **Scrum Master:** Replan Epic 3 stories (3 days)
2. **Development Team:** Begin Phase 1 (Vaadin setup) - Days 1-3 of development sprint
3. **QA/UAT:** Stand by for Phase 3 testing

---

**Generated by:** Claude Code - correct-course workflow
**Date:** November 4, 2025
**Status:** Awaiting User Approval
