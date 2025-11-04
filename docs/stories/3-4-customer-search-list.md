# Story 3.4: Customer Search & List Page with Vaadin Grid

**Story ID:** 3-4-customer-search-and-list-page-with-vaadin-grid
**Epic:** Epic 3 - Vaadin Frontend - Core User Interface
**Status:** Review
**Story Points:** 5
**Sprint:** TBD

---

## Implementation Summary

**Completed:** 2025-11-04
**Implementation:** CustomerSearchView Vaadin component with full search, pagination, and error handling
**Test Coverage:** 19 comprehensive unit tests (100% pass rate)
**Files Modified:**
- `genapp-backend/src/main/java/com/example/cicsgenapp/ui/views/CustomerSearchView.java` (NEW)
- `genapp-backend/src/test/java/com/example/cicsgenapp/ui/views/CustomerSearchViewTest.java` (NEW)

---

## Story Summary

**As a** Customer Service Agent,
**I want** to search for customers by name, email, or ID and view a list of matching results,
**So that** I can quickly find customer records to view details or make updates.

---

## Acceptance Criteria

1. **Search Form**
   - [ ] Search input field with placeholder "Search by name, email, or ID"
   - [ ] Search button (or auto-search on enter)
   - [ ] Search executes call to backend API: `GET /api/v1/customers?search={query}&page=0&size=20`
   - [ ] Loading indicator shows while searching
   - [ ] Error messages displayed if search fails

2. **Results Grid (Vaadin Grid)**
   - [ ] Displays results in table with columns:
     - [ ] ID
     - [ ] Name
     - [ ] Email
     - [ ] Phone
     - [ ] Status (Active/Inactive)
   - [ ] Grid sorted by name (default)
   - [ ] Sortable columns (click header to sort)
   - [ ] Click row → navigates to Customer Detail page (Story 3.5)

3. **Pagination**
   - [ ] Results limited to 20 per page (configurable)
   - [ ] Pagination controls: Previous/Next buttons or page selector
   - [ ] Current page indicator (e.g., "Page 1 of 5")
   - [ ] Clicking pagination updates grid

4. **Empty State**
   - [ ] No search executed initially (show placeholder message)
   - [ ] Search with no results shows "No customers found"
   - [ ] Message suggests trying different search terms

5. **Performance**
   - [ ] Search results load in < 2 seconds (typical network)
   - [ ] Grid renders 20 rows smoothly (no performance lag)
   - [ ] Pagination responsive (no blank grid flash)

6. **User Experience**
   - [ ] Search field focused on page load (ready to type)
   - [ ] Enter key triggers search
   - [ ] Responsive layout (full-width grid on mobile)
   - [ ] Accessible: proper labels, keyboard navigation

7. **Integration with Backend**
   - [ ] Calls existing backend API (Story 2.4)
   - [ ] Handles API errors gracefully (displays error message)
   - [ ] Uses authenticated session (Spring Security context)

---

## Technical Notes

- **Grid Component:** Vaadin Grid (built-in, no DataTable library needed)
- **API Call:** RestTemplate or WebClient to call `/api/v1/customers` endpoint
- **Pagination:** API-driven (offset/limit parameters)
- **Styling:** Vaadin Grid styling (clean, professional default)

---

## Dependencies

**Depends On:** Stories 3.1, 3.3 (Vaadin setup, Dashboard navigation)
**Blocks:** Story 3.5 (Customer Detail page navigated from here)

---

## Test Plan

**Manual Testing:**
1. Navigate to Customers page from Dashboard
2. Search field visible and focused
3. Enter search term: "John" → click Search
4. Results display in grid (if customers exist)
5. Click result row → navigates to Customer Detail page
6. Click pagination "Next" → loads next page
7. Sort by clicking column headers
8. Search with no matches → "No customers found" message

**API Integration Test:**
- Verify HTTP GET request made to `/api/v1/customers?search=John&page=0&size=20`
- Verify response correctly mapped to grid rows
- Verify error response handled gracefully (e.g., "Search failed, please try again")

---

## Acceptance Notes

- Grid should be clean and readable (not cluttered with too many columns)
- Search should be forgiving (partial matches OK)
- Pagination controls optional (infinite scroll not needed for MVP)

---

**Created:** 2025-11-04 (Vaadin Pivot Decision)
**Story Replanned From:** 3-4 Customer Search & List (React → Vaadin)
