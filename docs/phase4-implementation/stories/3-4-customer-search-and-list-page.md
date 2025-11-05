# Story 3.4: Customer Search and List Page

Status: drafted

## Story

As a Customer Service Agent,
I want to search for customers and see results in a paginated list,
So that I can quickly find customers.

## Acceptance Criteria

1. Page created at route /customers/search
2. Search form with fields:
   - Query input (placeholder: "Search by name, email, or phone")
   - Status filter dropdown (All / Active / Inactive)
   - Search button
   - Clear button (resets form to defaults)
3. Pressing Enter in search input submits search
4. Results displayed in data table:
   - Columns: ID (uuid, truncated), Name, Email, Phone, Status, Actions
   - Status shown with badge: green for ACTIVE, gray for INACTIVE
   - Sortable columns (click header to sort; arrow indicator shows direction)
   - Pagination controls (Previous/Next buttons, "Page X of Y", Rows per page: 25/50/100)
5. Actions column with buttons:
   - "View" button → navigates to /customers/{customerId} (detail page)
   - "Edit" button → navigates to /customers/{customerId}?mode=edit
   - "More" menu (three dots) with delete option (soft-delete with confirmation)
6. Empty state message if no results: "No customers found. Try different search criteria."
7. Loading state while fetching (spinner centered on page)
8. Error message if search fails (with retry button)
9. URL reflects search state (shareable): /customers/search?query=smith&status=ACTIVE&page=1&limit=50
10. Performance: < 2s load time for typical searches (use debouncing on search input, 300ms delay)
11. Accessibility: keyboard navigation, ARIA labels on buttons, screen reader support

## Tasks / Subtasks

- [ ] Task 1: Create CustomerSearch page component (AC: #1, #2, #3)
- [ ] Task 2: Integrate API call to search customers (AC: #4, #9)
- [ ] Task 3: Implement results data table (AC: #4)
- [ ] Task 4: Implement pagination controls (AC: #4)
- [ ] Task 5: Implement actions column (AC: #5)
- [ ] Task 6: Implement empty state and error states (AC: #6, #8)
- [ ] Task 7: Implement loading state (AC: #7)
- [ ] Task 8: Implement responsive design and accessibility (AC: #10, #11)
- [ ] Task 9: URL state management (AC: #9)
- [ ] Task 10: Integration testing

## Dev Notes

- Depends on Story 3.8 (API Service Layer) for CustomerService.searchCustomers()
- Depends on Story 2.4 (Backend API) for GET /api/v1/customers endpoint
- Reuse form components from Story 3.7 (Form Components Library)
- Use MUI Table component for sortable table
- Use MUI Pagination component for pagination controls

## Dev Agent Record

- Context Reference: docs/stories/3-4-customer-search-and-list-page.context.xml
