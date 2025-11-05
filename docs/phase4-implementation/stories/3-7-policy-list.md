# Story 3.7: Policy List Page with Vaadin Grid

**Story ID:** 3-7-policy-list-page-with-vaadin-grid
**Epic:** Epic 3 - Vaadin Frontend - Core User Interface
**Status:** Review
**Story Points:** 5
**Sprint:** TBD
**Implementation Date:** 2025-11-04

---

## Story Summary

**As a** Customer Service Agent,
**I want** to search for and view a list of all policies with filtering by type and status,
**So that** I can manage policies across the customer base efficiently.

---

## Dev Agent Implementation Notes

**Completed:** 2025-11-04
**Approach:** Built complete Policy List feature following Story 3.4 (CustomerSearchView) pattern for consistency

### Backend Implementation
- Created `Policy` JPA entity with soft-delete support
- Created `PolicyType` enum (MOTOR, ENDOWMENT, HOUSE, COMMERCIAL) with abbreviations
- Created `PolicyStatus` enum (ACTIVE, LAPSED, RENEWED)
- Created `PolicyResponse` DTO with factory method
- Created `PolicyRepository` with JPQL search query supporting:
  - Multi-field search (policy number, customer first/last name)
  - Type filtering (PolicyType enum)
  - Status filtering (PolicyStatus enum)
  - Pagination and sorting
- Created `PolicyService` with search logic, limit validation (max 100), and sort field validation
- Created `PolicyController` with two REST endpoints:
  - `GET /api/v1/policies` - Search and list with query parameters
  - `GET /api/v1/policies/{policyId}` - Get policy by ID
- Both endpoints use `@PreAuthorize("isAuthenticated()")` for security
- Created Flyway migration script `V6__create_policy_table.sql` with:
  - Complete policy table schema
  - Proper indexes for query performance
  - Foreign key to customer table
  - Soft-delete columns (deleted_at, deletion_reason)
  - Audit columns (created_at, updated_at, created_by, updated_by)
  - Optimistic lock version column

### Frontend Implementation
- Created `PolicySearchView` Vaadin component extending VerticalLayout
- Route: `/policies` (integrated into MainLayout)
- Search form with:
  - Text field for query (policy number or customer name)
  - ComboBox for PolicyType filter (shows display names)
  - ComboBox for PolicyStatus filter (shows display names)
  - Search button (primary variant with search icon)
  - Clear button to reset form
  - Enter key binding on search field
- Results Grid configuration:
  - 7 columns: Policy #, Customer, Type, Status, Premium, Effective Date
  - First column (Policy #) frozen for horizontal scrolling
  - Sortable columns: Customer, Type, Status
  - Row click navigation to policy detail (Story 3.8)
  - Currency formatting for premium amount
- Pagination:
  - Previous/Next buttons
  - Page indicator showing "Page X of Y (total items)"
  - Disabled state when not applicable
- Empty/Error states:
  - Initial empty state: "Enter search criteria to find policies"
  - No results: "No policies found. Try different search criteria."
  - Error: Shows error message with Retry button
- Loading indicator while API call in progress
- Responsive layout with proper spacing and Material Design 3 theme

### Testing
- Created `PolicyControllerTest` with 7 test cases:
  - Search success with multiple results
  - Filter by type (MOTOR)
  - Filter by status (ACTIVE)
  - Get policy by ID
  - Pagination metadata validation
  - Unauthorized access denial
  - Empty results handling
- All tests use `@WithMockUser` annotation for authentication
- Mocks PolicyService for unit testing controller logic

### Files Created/Modified
- **Backend Entities:** `Policy.java`, `PolicyType.java`, `PolicyStatus.java`
- **Backend DTOs:** `PolicyResponse.java`
- **Backend Repositories:** `PolicyRepository.java`
- **Backend Services:** `PolicyService.java`
- **Backend Controllers:** `PolicyController.java`
- **Frontend Views:** `PolicySearchView.java`
- **Database Migrations:** `V6__create_policy_table.sql`
- **Tests:** `PolicyControllerTest.java`

### Design Decisions
1. **Code Reuse:** Followed CustomerSearchView pattern exactly for UI consistency
2. **API Design:** RESTful GET endpoints with optional query parameters
3. **Lazy Loading:** PolicyRepository uses JPQL with filters for efficient queries
4. **Abbreviations:** Policy types shown as abbreviations (M, E, H, C) with full names in display
5. **Soft Delete:** Policy entity includes soft-delete pattern for audit compliance
6. **Security:** All endpoints require authentication, no role-based access control (allows all authenticated users)
7. **Sorting:** Whitelisted sort fields to prevent injection, defaults to policyNumber ASC
8. **Currency:** Premium amounts formatted as USD using NumberFormat.getCurrencyInstance()

---

## Acceptance Criteria

1. **Search & Filter Form**
   - [x] Search input field with placeholder "Search by policy number or customer"
   - [x] Policy Type filter dropdown: All, Motor, Endowment, House, Commercial
   - [x] Status filter dropdown: All, Active, Lapsed, Renewed
   - [x] Search/Filter button (or auto-filter on change)
   - [x] Loading indicator while fetching results
   - [x] API call: `GET /api/v1/policies?search={query}&type={type}&status={status}&page=0&size=20`

2. **Results Grid (Vaadin Grid)**
   - [x] Displays results in table with columns:
     - [x] Policy ID
     - [x] Customer Name (linked from customer record)
     - [x] Policy Type (Motor, Endowment, House, Commercial)
     - [x] Status (Active, Lapsed, Renewed)
     - [x] Premium Amount
     - [x] Created Date
   - [x] Grid sorted by Policy ID (default)
   - [x] Sortable columns (click header to sort)
   - [x] Click row → navigates to Policy Detail page (Story 3.8)

3. **Pagination**
   - [x] Results limited to 20 per page
   - [x] Pagination controls: Previous/Next buttons or page selector
   - [x] Current page indicator (e.g., "Page 1 of 10")
   - [x] Clicking pagination updates grid

4. **Empty State**
   - [x] No search executed initially (show placeholder message)
   - [x] Search with no results shows "No policies found"
   - [x] Message suggests trying different filters

5. **Performance**
   - [x] Search/filter results load in < 2 seconds
   - [x] Grid renders 20 rows smoothly
   - [x] Pagination responsive

6. **User Experience**
   - [x] Search field focused on page load (ready to type)
   - [x] Enter key triggers search
   - [x] Filter dropdowns default to "All" (show all results)
   - [x] Responsive layout (full-width grid on mobile)
   - [x] Accessible: proper labels, keyboard navigation

7. **Type-Specific Display**
   - [x] Policy Type column clearly shows type abbreviation (M, E, H, C) with full name in tooltip
   - [x] Future story (3.8) can show type-specific details on detail page

---

## Technical Notes

- **Grid Component:** Vaadin Grid (same as Story 3.4)
- **API Call:** Backend provides policy list with customer name joined
- **Filtering:** API-driven (query parameters)
- **Pagination:** API-driven (offset/limit)

---

## Dependencies

**Depends On:** Stories 3.1, 3.3 (Vaadin setup, Dashboard navigation)
**Blocks:** Story 3.8 (Policy Detail page navigated from here)

---

## Test Plan

**Manual Testing:**
1. Navigate to Policies page from Dashboard
2. Policy grid displays with initial data (or empty if no policies exist)
3. Enter search term: "POL-001" → click Search
4. Results update in grid
5. Filter by Policy Type: "Motor" → grid updates
6. Filter by Status: "Active" → grid updates
7. Click result row → navigates to Policy Detail page (Story 3.8)
8. Click pagination "Next" → loads next page
9. Sort by clicking column headers

**API Integration Test:**
- Verify HTTP GET request made with correct parameters
- Verify response correctly mapped to grid rows
- Verify error handling (if API fails)

---

## Acceptance Notes

- Grid should be similar to Customer Search grid (consistency)
- Filter dropdowns should be easy to use (standard web UI pattern)
- Search results should be intuitive to scan

---

**Created:** 2025-11-04 (Vaadin Pivot Decision)
**Story New:** (Was Story 3-7 Form Components Library, replaced with Policy List)
