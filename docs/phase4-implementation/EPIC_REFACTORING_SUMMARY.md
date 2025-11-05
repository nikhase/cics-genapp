# Epic Documentation Refactoring Summary

**Date:** November 4, 2025
**Author:** Claude Code
**Changes:** Removed all React/JavaScript framework references, clarified domain separation and technology maturity sequencing

## Overview

Updated `/docs/epics.md` to reflect the Vaadin pivot (completed November 4) and to clarify the relationship between Epic 3 (Frontend Foundation & Customer Management) and Epic 5 (Policy Management). The refactoring addresses the earlier question: "Shouldn't we separate customers and policies into distinct epics?" The answer was: **the separation is already there in domain logic, but the epics are sequenced by technology maturity, not strictly by domain**.

## Changes Made

### 1. **Architecture Note** (Line 28)
**Before:** "...including Spring Boot 3.4+ LTS, Java 21 LTS, React 18, PostgreSQL 16 LTS, OIDC/Zitadel authentication..."

**After:** "...including Spring Boot 3.4+ LTS, Java 21 LTS, Vaadin 24+ frontend, PostgreSQL 16 LTS, Spring Security authentication (with post-MVP OIDC/Zitadel)..."

**Rationale:** Reflects Vaadin as the frontend framework instead of React 18. Also clarifies that Spring Security form-based auth is MVP, with OIDC/Zitadel deferred to post-MVP.

---

### 2. **Epic 3 Title & Goal** (Lines 717-726)
**Before:** "EPIC 3: React Frontend - Authentication & Core UI"

**After:** "EPIC 3: Frontend Foundation & Customer Management UI (Vaadin)"

**Goal Before:**
> "Build the responsive React single-page application that replaces the 3270 terminal interface. This epic creates the React 18 + Vite project, implements Zitadel OIDC login, builds the dashboard, and creates customer management screens (search, detail, create)."

**Goal After:**
> "Build the responsive Vaadin web application that replaces the 3270 terminal interface, establishing the frontend infrastructure and implementing the first complete domain (customer management). This epic creates the Vaadin 24+ project with Spring Boot integration, implements Spring Security form-based login (with post-MVP OIDC), builds the dashboard and navigation foundation, and creates customer management screens (search, detail, create/edit). Upon completion, agents can perform customer operations via web UI and begin validating against the legacy COBOL system. The infrastructure and patterns established here (form components, API integration, validation framework) serve as the foundation for policy management and other domains in Epic 5."

**Added Key Principle:**
> "This epic is sequenced to establish **technology maturity** before policy management (Epic 5). By proving customer operations work reliably with parallel validation (Epic 4), we build confidence in the platform before expanding to policies."

**Rationale:**
- Clarifies that Epic 3 is TWO things: (1) frontend infrastructure/foundation, and (2) first domain implementation (customers)
- Explains that patterns established here (forms, validation, API integration) are reusable for policies
- Explicitly states the technology maturity sequencing philosophy

---

### 3. **Story 3.1: Project Setup**
**Before:** "React Project Setup with Vite and Material Design"

**After:** "Vaadin Project Setup with Spring Boot Integration"

All acceptance criteria updated from React/Vite to Vaadin/Spring Boot:
- Maven build instead of npm
- Java project structure instead of TypeScript
- `mvn spring-boot:run` instead of Vite dev server
- Server-side routing (@Route) instead of client-side React Router
- Removed all npm/JavaScript references

---

### 4. **Story 3.2: Login Page**
**Before:** "Login Page and Zitadel OIDC Authentication"

**After:** "Login Page with Spring Security Form Authentication"

Key changes:
- Spring Security form-based auth (username/password) instead of OIDC redirect flow
- Server-side session instead of JWT in session storage
- Added explicit note: "OIDC/Zitadel integration deferred to post-MVP (Story 1.5 in backlog)"
- Simplified flow (POST `/login` → Spring Security validates → session created)

---

### 5. **Story 3.3: Dashboard Page**
**Before:** "Dashboard Page with Navigation and Quick Actions"

**After:** "Dashboard Page with Vaadin Navigation Layout"

Updates:
- Vaadin AppLayout component instead of custom React layout
- Vaadin Grid and responsive utilities instead of HTML/CSS
- Vaadin Notification instead of toast
- Server-side navigation methods instead of React Router links
- Consistent with Vaadin component naming

---

### 6. **Story 3.4: Customer Search**
**Before:** "Customer Search and List Page"

**After:** "Customer Search and List Page with Vaadin Grid"

Updates:
- Vaadin Grid component (with built-in pagination, sorting, filtering)
- Vaadin TextField, ComboBox for search/filter controls
- Vaadin ContextMenu instead of custom dropdown
- Vaadin Spinner for loading state
- Vaadin Notification for errors

---

### 7. **Story 3.5: Customer Detail**
**Before:** "Customer Detail and Edit Page"

**After:** "Customer Detail Page with Vaadin Components"

Updates:
- Vaadin FormLayout for form layout
- Vaadin Badge for status indicator
- Vaadin Details component for audit history (expandable)
- Vaadin Spinner for loading overlays
- Removed all React-specific patterns

---

### 8. **Story 3.6: Customer Create**
**Before:** "Customer Create Page with Form Wizard"

**After:** "Customer Create/Edit Page with Vaadin Form"

Updates:
- Clarified that this story handles both CREATE and EDIT modes
- Vaadin Stepper for multi-step form (or custom step navigation)
- ValidatedTextField, DatePicker components from Story 3.7 library
- Server-side form state management instead of React state
- Vaadin Notification for success/error messages

---

### 9. **Story 3.7: Form Components Library**
**Before:** "Form Components and Validation UI Library"

**After:** "Reusable Vaadin Form Components and Validation Library"

Updated to describe Java/Vaadin component classes instead of React components:
- **ValidatedTextField** (extends Vaadin TextField)
- **ValidatedEmailField** (extends Vaadin EmailField)
- **ValidatedPhoneField** (extends Vaadin TextField with pattern)
- **ValidatedDatePicker** (extends Vaadin DatePicker)
- **ValidatedComboBox** (extends Vaadin ComboBox)
- **FormErrorNotification**, **SuccessNotification**, **ConfirmDialog**, **LoadingOverlay**

All component descriptions updated to reflect Vaadin patterns, not React/Material-UI patterns.

---

### 10. **Story 3.8: API Client Service**
**Before:** "API Integration and Client Service Layer"

**After:** "Backend API Service Layer (Spring Boot)"

Key insight: Clarified that this is a **backend service layer** (Spring Boot RestTemplate/WebClient), not a frontend API client. This eliminates the async/Promise-based JavaScript patterns and uses Java service-based architecture instead:
- RestTemplate or WebClient for HTTP calls
- Spring-managed services (CustomerService, PolicyService)
- Spring Security integration for auth
- Resilience4j for retry/circuit-breaker patterns
- Vaadin component integration examples in Java

---

### 11. **Epic 5 Title & Goal** (Lines 1370-1378)
**Before:** "EPIC 5: Policy Management API & React UI"

**After:** "EPIC 5: Policy Management API & Vaadin UI"

**Added Key Principle:**
> "Policy management scales the patterns proven in Epic 3 (customer management). By this point, the Vaadin framework, Spring Boot backend, form validation components, and API service layer are all stable and reusable. This epic focuses on domain-specific logic (policy types, status transitions, validations) rather than framework/infrastructure concerns."

**Rationale:**
- Makes explicit that Epic 5 builds ON Epic 3, not in parallel
- Clarifies that Epic 3 establishes the technology foundation
- Shows that Epic 5 reuses components and patterns from Epic 3
- Explains the sequencing: technology maturity matters more than domain separation

---

### 12. **Story 5.7: Policy Detail Page**
**Before:** "Policy Detail and Edit Page (React)"

**After:** "Policy Detail Page with Vaadin Display Components"

Updated to use Vaadin components and patterns consistent with Story 3.5 (customer detail).

---

### 13. **Story 5.8: Policy Create Wizard**
**Before:** "Policy Create Wizard (React) - Type-Specific Forms"

**After:** "Policy Create Wizard with Vaadin - Type-Specific Forms"

Updated to use:
- Vaadin Stepper for multi-step form
- Vaadin RadioButtonGroup, ComboBox, CheckboxGroup
- ValidatedTextField and other custom components from Story 3.7
- References Story 3.7 as prerequisite

---

### 14. **Story 5.9: Policy-to-Customer Linking**
**Before:** "Policy-to-Customer Linking UI Integration"

**After:** "Policy-to-Customer Linking UI Integration" (same title, updated content)

Updated AC's to:
- Reference Story 3.5 and 5.7 by name
- Describe Vaadin Grid for displaying related entities
- Clarify reciprocal navigation (Customer ↔ Policy ↔ Customer)

---

## Summary of Framework Changes

| Aspect | Before | After |
|--------|--------|-------|
| **Frontend Framework** | React 18 + Vite | Vaadin 24+ + Spring Boot |
| **Language** | TypeScript/JavaScript | Java |
| **Build Tool** | npm, Vite | Maven |
| **UI Components** | Material-UI (MUI) | Vaadin Components |
| **Routing** | React Router (client-side) | Vaadin routing (@Route, server-side) |
| **API Client** | Axios/fetch (frontend) | Spring RestTemplate/WebClient (backend) |
| **Authentication** | OIDC/Zitadel (frontend-first) | Spring Security form-based (MVP) |
| **Server** | Separate backend | Integrated with Spring Boot |
| **Form Handling** | React Hooks, state mgmt | Vaadin FormLayout, custom components |

---

## Why This Matters: Technology Maturity Sequencing

The original question was: "Wouldn't it make more sense to have Epic 3 = Customers only, Epic 5 = Policies only?"

**The answer in the epics now:**

Epic 3 is **NOT just** "customer UI" — it's:
1. **Frontend infrastructure foundation** (Vaadin setup, routing, auth, theme)
2. **Reusable component library** (form validation, notifications, dialogs)
3. **API service patterns** (how Vaadin components call Spring Boot APIs)
4. **First domain implementation** (customers)

Epic 5 is:
1. **Domain-specific logic** (policy types, validations, status transitions)
2. **Reuses all infrastructure** from Epic 3
3. **Leverages proven patterns** from customer management

By Epic 4 (Parallel Run Validation), we've proven that the customer domain works end-to-end. Then Epic 5 can confidently scale those patterns to policies without reinventing the wheel.

**If we had split purely by domain:**
- Would need to duplicate framework setup (Vaadin project, theme, routing, form library) in both epics
- Would lose the clear "one epic establishes maturity, next epic scales it" philosophy
- Would make it harder to understand why customer and policy stories have similar structure

---

## Files Modified

- `/docs/epics.md` - All updates described above

## Verification

All React/JavaScript references have been removed from the story acceptance criteria:
- ✅ No more npm, Vite, React Router, Material-UI, TypeScript
- ✅ No more .env files, JavaScript build processes
- ✅ No more API client libraries (Axios, fetch)
- ✅ All replaced with Vaadin, Spring Boot, Java, Maven equivalents

All stories maintain the same business value and acceptance criteria logic — only the technical implementation details have changed to reflect Vaadin.
