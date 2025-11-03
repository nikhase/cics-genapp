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

- [ ] Task 1: Create search form component (AC: #2, #3)
  - [ ] Create `src/components/CustomerSearchForm.tsx`
    - Query input field with onChange handler and debouncing (300ms)
    - Status filter dropdown (options: All, ACTIVE, INACTIVE)
    - Search button to trigger search
    - Clear button to reset form to defaults
    - Controlled component with React state
  - [ ] Use MUI TextField for inputs, Select for dropdown, Button for actions
  - [ ] Implement debouncing on query input (use custom hook or lodash.debounce)
  - [ ] Pressing Enter in query input triggers search
  - [ ] Test: Enter query and verify debouncing works (300ms delay)
  - [ ] Test: Click Clear button and verify form resets
  - [ ] Test: Pressing Enter submits search

- [ ] Task 2: Create customer search/list page (AC: #1, #4, #9)
  - [ ] Create `src/pages/CustomerSearchPage.tsx`
    - Main page component at route /customers/search
    - Header: "Customer Search" title
    - CustomerSearchForm component
    - Results area below form (initially empty)
    - Results table component (created in Task 3)
    - Pagination controls component (created in Task 4)
  - [ ] Parse URL query parameters: query, status, page, limit
    - Use `useSearchParams` hook from react-router-dom
    - Update form state from URL params on page load
    - Update URL params when search form changes
  - [ ] State management: results, loading, error, pagination (limit, offset, total)
  - [ ] Test: Verify URL reflects search state
  - [ ] Test: Page load restores search state from URL params

- [ ] Task 3: Create customer results table component (AC: #4, #5)
  - [ ] Create `src/components/CustomerResultsTable.tsx`
    - Table displaying customer results
    - Columns: ID (truncated UUID), Name, Email, Phone, Status, Actions
    - Sortable columns (click header to sort)
    - Sort direction indicator (↑ ascending, ↓ descending)
    - Status badge: green for ACTIVE, gray for INACTIVE
    - Action buttons: "View", "Edit", "More" (three-dot menu)
    - Rows per page selector (25, 50, 100)
  - [ ] Use MUI Table, TableHead, TableBody, TableRow, TableCell
  - [ ] Use MUI IconButton for More menu (three dots icon)
  - [ ] Use MUI Menu for More menu dropdown
  - [ ] Sort by column when header clicked
    - Emit event to parent (CustomerSearchPage) to update search parameters
    - Highlight sorted column with direction indicator
  - [ ] Show empty state if results array is empty
  - [ ] Test: Click column header to sort
  - [ ] Test: Verify sort direction indicator changes
  - [ ] Test: Change rows per page selector
  - [ ] Test: Action buttons emit correct events

- [ ] Task 4: Create pagination controls (AC: #4)
  - [ ] Create `src/components/PaginationControls.tsx`
    - Previous/Next buttons
    - "Page X of Y" display (e.g., "Page 1 of 5")
    - Rows per page dropdown (25, 50, 100)
    - Current limit and offset displayed
  - [ ] Use MUI Button, Select, Stack components
  - [ ] Previous button disabled on first page
  - [ ] Next button disabled on last page
  - [ ] Rows per page selector updates limit
  - [ ] Emit event to parent when pagination changes (offset, limit)
  - [ ] Test: Click Previous/Next and verify page changes
  - [ ] Test: Change rows per page and verify results update
  - [ ] Test: Buttons disable correctly on first/last page

- [ ] Task 5: Implement customer search API call (AC: #1, #10)
  - [ ] Create/update `src/services/CustomerService.ts`
    - Implement `searchCustomers(query: string, status?: string, limit?: number, offset?: number): Promise<SearchResult<Customer>>`
    - Query endpoint: GET /api/v1/customers?query=...&status=...&limit=...&offset=...
    - Handle response: { data: Customer[], pagination: { limit, offset, total, hasMore } }
    - Error handling: network errors, 4xx, 5xx responses
  - [ ] Use axios or fetch (consistent with Story 3.8 API client)
  - [ ] Include Authorization header (JWT token from context)
  - [ ] Add debouncing to search calls (avoid excessive API requests)
  - [ ] Test: Search with valid query and verify results
  - [ ] Test: Search with empty query and verify all customers returned
  - [ ] Test: Filter by status and verify results
  - [ ] Test: Pagination with different limits

- [ ] Task 6: Create loading and error states (AC: #7, #8)
  - [ ] Create loading state:
    - Show spinner (MUI Skeleton or CircularProgress) while fetching
    - Disable form and pagination while loading
    - Centered on page
  - [ ] Create error state:
    - Show error message with details (red text)
    - "Retry" button to re-execute search
    - Don't clear previous results (user can see last known good state)
  - [ ] Use try-catch blocks in search handler
  - [ ] Log errors to console for debugging
  - [ ] Test: Verify spinner displays while loading
  - [ ] Test: Verify error message displays on API failure
  - [ ] Test: Click Retry button and verify search re-executes

- [ ] Task 7: Implement delete customer modal (AC: #5)
  - [ ] Create `src/components/DeleteCustomerModal.tsx`
    - Confirmation dialog for soft-delete
    - Warning message: "This will mark the customer as inactive. Continue?"
    - "Cancel" and "Delete" buttons
    - Call DELETE /api/v1/customers/{customerId}
  - [ ] Use MUI Dialog component
  - [ ] Show loading state on Delete button while processing
  - [ ] Show error message if delete fails
  - [ ] Emit event to parent when delete succeeds
  - [ ] Parent refreshes search results after delete
  - [ ] Test: Click delete button and verify modal appears
  - [ ] Test: Click Cancel and verify modal closes without deleting
  - [ ] Test: Click Delete and verify customer is removed from list

- [ ] Task 8: Implement action button handlers (AC: #5)
  - [ ] Update CustomerSearchPage:
    - "View" button: `navigate(`/customers/${customerId}`)`
    - "Edit" button: `navigate(`/customers/${customerId}?mode=edit`)`
    - "More" menu: show delete option
    - Delete option: open DeleteCustomerModal
  - [ ] Use `useNavigate` hook from react-router-dom
  - [ ] Test: Click View button and verify navigation to customer detail
  - [ ] Test: Click Edit button and verify navigation with ?mode=edit
  - [ ] Test: Click delete from More menu and verify modal opens

- [ ] Task 9: Implement accessibility features (AC: #11)
  - [ ] Add ARIA labels to all buttons:
    - `aria-label="View customer details"`
    - `aria-label="Edit customer"`
    - `aria-label="More options for customer"`
    - `aria-label="Delete customer"`
  - [ ] Add ARIA labels to form fields:
    - `aria-label="Search customers by name, email, or phone"`
    - `aria-label="Filter by customer status"`
  - [ ] Keyboard navigation:
    - Tab through form fields and buttons
    - Enter to submit search
    - Escape to close modals/menus
  - [ ] Screen reader support:
    - Table headers marked with <th>
    - Status badges described with text (e.g., aria-label="Active" for green badge)
    - Loading state announced (use aria-live="polite")
  - [ ] Test: Use keyboard to navigate entire page
  - [ ] Test: Screen reader (NVDA, JAWS, or browser) can read all content

- [ ] Task 10: Implement responsive design (AC: #11)
  - [ ] Search form responsive:
    - Desktop: fields in single row
    - Mobile: fields stack vertically
    - Use MUI Grid with responsive breakpoints (xs, sm, md, lg)
  - [ ] Results table responsive:
    - Desktop: all columns visible
    - Tablet: hide Phone column, show in row expansion
    - Mobile: collapse to card view (Name, Email, Status, Actions)
  - [ ] Use MUI useMediaQuery hook for responsive behavior
  - [ ] Test: DevTools responsive mode (iPhone, iPad, Desktop)
  - [ ] Test: Verify layout adapts correctly to screen size

- [ ] Task 11: Create integration tests (AC: all)
  - [ ] Create `tests/integration/CustomerSearchPage.test.tsx`
    - Test: Page renders with search form
    - Test: Enter query and click Search (verify API call)
    - Test: Results display in table
    - Test: Click View button (verify navigation)
    - Test: Click Edit button (verify navigation with ?mode=edit)
    - Test: Click delete and confirm (verify deletion)
    - Test: Sort by column (verify API call with sortBy param)
    - Test: Change pagination limit (verify API call)
    - Test: Filter by status (verify API call)
    - Test: Error handling (verify error message and retry)
  - [ ] Mock CustomerService API calls using Jest
  - [ ] Mock react-router-dom useNavigate
  - [ ] Test: Run `npm test` and verify all pass

- [ ] Task 12: Add API endpoint verification (AC: #4)
  - [ ] Backend requirement (Story 2.4): Verify endpoint exists
    - GET /api/v1/customers?query=&status=&limit=&offset= → returns paginated results
    - Response format: { data: Customer[], pagination: { limit, offset, total, hasMore } }
  - [ ] Frontend test: Verify API client can call endpoint
  - [ ] Test: Fetch sample data and verify structure

## Dev Notes

### Architecture Context

Story 3.4 implements the customer search and list interface, a critical feature for agents to find customers efficiently. This story builds on the dashboard (Story 3.3) and establishes the pattern for paginated list pages that will be reused in policy management (Stories 5.7-5.8).

**Key Design Decisions:**

1. **Shareable URLs**: Search state persisted in URL query params, allowing users to share search results and bookmark common searches
2. **Debounced search**: 300ms debounce on query input reduces API calls during typing
3. **Soft-delete with confirmation**: Delete operations are reversible via compliance admin
4. **Sortable columns**: Allows agents to organize results by name, email, status, creation date
5. **Responsive table**: Graceful degradation on mobile (card view) vs desktop (full table)
6. **Empty and error states**: Clear messaging prevents user confusion on no-results or failures

**Dependency Chain:**
- Depends on: Story 3.1 (React setup), Story 3.2 (Auth), Story 3.3 (Navigation), Story 2.4 (API)
- Prerequisite for: Stories 3.5 (customer detail), 5.7-5.8 (policy management)

### Technical Requirements

1. **Frontend Libraries**:
   - `react-router-dom`: useSearchParams, useNavigate, useLocation
   - `@mui/material`: Table, Dialog, Select, TextField, Button, Badge
   - `@mui/icons-material`: MoreVertIcon, OpenInNewIcon, EditIcon, DeleteIcon
   - `lodash.debounce` or custom debounce hook

2. **Backend Requirements** (from Story 2.4):
   - GET /api/v1/customers?query=&status=&limit=&offset=&sortBy=&sortOrder=
   - Returns: { data: Customer[], pagination: { limit, offset, total, hasMore }, metadata }

3. **Environment Variables**:
   - No new variables needed

### Constraints & Requirements

- **Performance**: Search completes < 2s (typical), pagination refresh < 1s
- **Accessibility**: WCAG 2.1 AA compliance, keyboard navigation, screen reader support
- **Responsive**: Works on mobile (375px), tablet (768px), desktop (1920px+)
- **Data consistency**: Reflects latest state from backend
- **Network resilience**: Retry logic, error recovery

### Project Structure Notes

From Story 3.1 unified-project-structure:

```
src/
├── pages/
│   └── CustomerSearchPage.tsx
├── components/
│   ├── CustomerSearchForm.tsx
│   ├── CustomerResultsTable.tsx
│   ├── PaginationControls.tsx
│   ├── DeleteCustomerModal.tsx
│   └── StatusBadge.tsx (reusable)
├── services/
│   └── CustomerService.ts (implements searchCustomers method)
├── hooks/
│   ├── useDebounce.ts (custom debounce hook, if needed)
│   └── useSearch.ts (custom hook for search state management)
└── types/
    └── index.ts (Customer, SearchResult types)
```

### Learnings from Previous Story

**From Story 3.3 (Dashboard):**
- Layout component wraps pages with Header and Sidebar
- Use useNavigate for programmatic navigation
- MUI components for consistent UI (Button, TextField, Select)
- Role-based visibility patterns (can extend for delete permission checks)

**From Story 3.2 (Authentication):**
- useAuth hook provides user context (email, roles)
- JWT token available for API calls
- Protected routes redirect unauthenticated users

**From Story 3.1 (React Setup):**
- TypeScript strict mode enabled
- Vite build system
- Material Design 3 theme
- ESLint + Prettier configured

**From Story 2.4 (Customer Search API):**
- API endpoint: GET /api/v1/customers?query=&status=&limit=&offset=
- Response includes pagination metadata
- Sorting supported via sortBy/sortOrder params

**Patterns to Maintain:**
- Use custom hooks for API calls and state management
- Controlled components with React state
- MUI components for UI consistency
- TypeScript for type safety
- Error boundaries and error handling throughout

### References

- [React Router useSearchParams Hook](https://reactrouter.com/en/main/hooks/use-search-params)
- [MUI Table Documentation](https://mui.com/material-ui/react-table/)
- [MUI Dialog/Modal Documentation](https://mui.com/material-ui/react-dialog/)
- [React Debouncing Pattern](https://react.dev/reference/react-dom/flushSync)
- [ARIA Tables Guide](https://www.w3.org/WAI/tutorials/tables/)
- [WCAG 2.1 Accessibility](https://www.w3.org/WAI/WCAG21/quickref/)
- [Source: docs/epics.md#story-34-customer-search-and-list-page]
- [Source: docs/stories/3-3-dashboard-page-with-navigation-and-quick-actions.md]
- [Source: docs/stories/3-1-react-project-setup-with-vite-and-material-design.md]

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 3.4 CREATED from Epic 3 via create-story workflow
- 2025-11-03: Based on epics.md AC and Story 2.4 Customer Search API
- 2025-11-03: Incorporates patterns from Story 3.3 (dashboard/navigation)

### Completion Notes List

### File List
