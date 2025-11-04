# Story 3.7: Policy List Page with Vaadin Grid

**Story ID:** 3-7-policy-list-page-with-vaadin-grid
**Epic:** Epic 3 - Vaadin Frontend - Core User Interface
**Status:** Ready for Dev
**Story Points:** 5
**Sprint:** TBD

---

## Story Summary

**As a** Customer Service Agent,
**I want** to search for and view a list of all policies with filtering by type and status,
**So that** I can manage policies across the customer base efficiently.

---

## Acceptance Criteria

1. **Search & Filter Form**
   - [ ] Search input field with placeholder "Search by policy number or customer"
   - [ ] Policy Type filter dropdown: All, Motor, Endowment, House, Commercial
   - [ ] Status filter dropdown: All, Active, Lapsed, Renewed
   - [ ] Search/Filter button (or auto-filter on change)
   - [ ] Loading indicator while fetching results
   - [ ] API call: `GET /api/v1/policies?search={query}&type={type}&status={status}&page=0&size=20`

2. **Results Grid (Vaadin Grid)**
   - [ ] Displays results in table with columns:
     - [ ] Policy ID
     - [ ] Customer Name (linked from customer record)
     - [ ] Policy Type (Motor, Endowment, House, Commercial)
     - [ ] Status (Active, Lapsed, Renewed)
     - [ ] Premium Amount
     - [ ] Created Date
   - [ ] Grid sorted by Policy ID (default)
   - [ ] Sortable columns (click header to sort)
   - [ ] Click row → navigates to Policy Detail page (Story 3.8)

3. **Pagination**
   - [ ] Results limited to 20 per page
   - [ ] Pagination controls: Previous/Next buttons or page selector
   - [ ] Current page indicator (e.g., "Page 1 of 10")
   - [ ] Clicking pagination updates grid

4. **Empty State**
   - [ ] No search executed initially (show placeholder message)
   - [ ] Search with no results shows "No policies found"
   - [ ] Message suggests trying different filters

5. **Performance**
   - [ ] Search/filter results load in < 2 seconds
   - [ ] Grid renders 20 rows smoothly
   - [ ] Pagination responsive

6. **User Experience**
   - [ ] Search field focused on page load (ready to type)
   - [ ] Enter key triggers search
   - [ ] Filter dropdowns default to "All" (show all results)
   - [ ] Responsive layout (full-width grid on mobile)
   - [ ] Accessible: proper labels, keyboard navigation

7. **Type-Specific Display**
   - [ ] Policy Type column clearly shows type abbreviation (M, E, H, C) with full name in tooltip
   - [ ] Future story (3.8) can show type-specific details on detail page

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
