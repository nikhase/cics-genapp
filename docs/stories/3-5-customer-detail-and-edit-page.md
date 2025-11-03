# Story 3.5: Customer Detail and Edit Page

Status: drafted

## Story

As a Customer Service Agent,
I want to view and edit customer details,
So that I can manage customer information.

## Acceptance Criteria

1. Page created at route /customers/{customerId}
2. Detail view (read-only mode) shows customer information:
   - Name (first/last)
   - Date of birth
   - Email
   - Phone
   - Address (street, city, state, zip)
   - Status (badge: green/gray)
   - Created/updated timestamps
   - Created/updated by (user information)
3. Edit button switches to edit mode
4. Edit mode displays form fields with current values pre-populated
5. Form validation (real-time feedback):
   - Email valid format
   - Phone valid format (if provided)
   - Name required, 1-100 chars
   - Show error message below field if validation fails
6. Save button submits changes (disabled until form is valid)
7. Cancel button discards changes and returns to read-only mode
8. Success message on save: "Customer updated successfully" (toast notification, 3s duration)
9. Error message on failure with details
10. 404 Not Found error if customer doesn't exist
11. Optimistic locking: if customer was updated by another user, show conflict error with option to refresh
12. Loading state while fetching/saving (spinner)
13. Related Policies section (read-only):
    - Table of policies linked to this customer
    - Columns: Policy Number, Type, Status, Start Date, Actions (View)
    - "Create New Policy" button → /policies/create?customerId={customerId}
14. Audit History section (if user has audit_viewer role):
    - Expandable section showing immutable audit log for this customer
    - Columns: Date, User, Operation, Changes
    - Limit to last 10 entries

## Tasks / Subtasks

- [ ] Task 1: Create customer detail page component (AC: #1, #2)
  - [ ] Create `src/pages/CustomerDetailPage.tsx`
    - Route: /customers/:customerId
    - Fetch customer data on page load using customerId from URL params
    - Display loading spinner while fetching
    - Show 404 error if customer not found
    - Render customer details in read-only format initially
  - [ ] Use `useParams()` hook to extract customerId from URL
  - [ ] Use `useSearchParams()` hook to detect ?mode=edit from URL
  - [ ] Call CustomerService.getCustomer(customerId) on mount
  - [ ] Store customer data in React state
  - [ ] Test: Verify customer data loads on page mount
  - [ ] Test: Verify 404 displays if customer not found

- [ ] Task 2: Create customer detail display view (AC: #2)
  - [ ] Create `src/components/CustomerDetailView.tsx`
    - Display-only component showing customer fields
    - Render customer object data in read-only format
    - Use MUI Card, Grid, TextField (disabled), Stack
    - Show status as badge (green for ACTIVE, gray for INACTIVE)
    - Show timestamps: createdAt, updatedAt (format: YYYY-MM-DD HH:mm:ss)
    - Show createdBy, updatedBy user names
    - All fields read-only (no input interactions)
  - [ ] Layout fields in logical groups:
    - Basic Info: firstName, lastName, dateOfBirth, status
    - Contact: email, phone
    - Address: address, city, state, zipCode
    - Metadata: createdAt, updatedAt, createdBy, updatedBy
  - [ ] Test: Verify all customer fields display correctly
  - [ ] Test: Verify status badge renders with correct color

- [ ] Task 3: Create customer edit form component (AC: #4, #5, #6)
  - [ ] Create `src/components/CustomerEditForm.tsx`
    - Editable form with controlled components
    - Form fields pre-populated with current customer data
    - Fields: firstName, lastName, dateOfBirth, email, phone, address, city, state, zipCode
    - Real-time validation on each field
    - Show error messages below fields in red
    - Save and Cancel buttons
  - [ ] Use MUI TextField, DatePicker, Select components
  - [ ] Implement validation:
    - firstName/lastName: required, 1-100 chars
    - email: required, valid email format, check uniqueness via API (async)
    - phone: optional, valid phone format (regex)
    - dateOfBirth: optional, must be valid date and age >= 18
    - address/city/state: optional, any format
    - zipCode: optional, 5-6 digits if provided
  - [ ] Real-time validation feedback:
    - Green checkmark if field is valid
    - Red error message if invalid
    - Disable Save button until entire form is valid
  - [ ] Implement async email validation:
    - When user leaves email field, check if email is already in use (via API)
    - Show "checking..." text while validating
    - Show error if duplicate email
  - [ ] Track which fields have changed (for audit trail)
  - [ ] Test: Enter invalid email and verify error message
  - [ ] Test: Enter duplicate email and verify async error
  - [ ] Test: Fill form correctly and verify Save button enables
  - [ ] Test: Click Cancel and verify form resets

- [ ] Task 4: Implement mode switching (AC: #3, #4, #7)
  - [ ] Update CustomerDetailPage:
    - State: isEditMode (boolean)
    - Display CustomerDetailView if !isEditMode
    - Display CustomerEditForm if isEditMode
    - Edit button sets isEditMode = true
    - Cancel button in form sets isEditMode = false
  - [ ] Add Edit button in read-only view
    - Button label: "Edit"
    - Icon: pencil icon
    - Click handler: setIsEditMode(true)
  - [ ] Form Cancel button:
    - Discards unsaved changes
    - Returns to read-only view
    - Confirm if user has unsaved changes (optional modal)
  - [ ] Test: Click Edit button and verify form appears
  - [ ] Test: Make changes and click Cancel (verify discarded)
  - [ ] Test: Make changes and click Save (verify submitted)

- [ ] Task 5: Implement save functionality (AC: #6, #8)
  - [ ] Update CustomerDetailPage with save handler:
    - Call CustomerService.updateCustomer(customerId, changedFields)
    - Show loading spinner while saving
    - Show success toast: "Customer updated successfully" (3s auto-dismiss)
    - Return to read-only view
    - Refresh customer data from server (optimistic update)
  - [ ] Handle errors:
    - Conflict error (optimistic locking): "Customer was updated by another user. Refresh to see latest changes."
    - Duplicate email: "A customer with this email already exists."
    - Validation errors: show field-level errors in form
    - Generic error: "Failed to update customer. Please try again."
  - [ ] Use success toast from MUI Snackbar or custom component
  - [ ] Test: Make valid changes and click Save (verify update)
  - [ ] Test: Verify success message displays
  - [ ] Test: Verify form returns to read-only view
  - [ ] Test: Simulate conflict error and verify message

- [ ] Task 6: Implement conflict resolution UI (AC: #11)
  - [ ] Create `src/components/ConflictModal.tsx`
    - Modal showing conflict message
    - "Refresh" button to reload customer data from server
    - "Discard" button to cancel edit without saving
  - [ ] Detect conflict error on save:
    - HTTP 409 Conflict response from API
    - Show ConflictModal
  - [ ] Refresh handler:
    - Reload customer data from server
    - Refresh form values
    - Show updated data to user
    - Close modal and return to edit form
  - [ ] Test: Simulate concurrent edit (edit in 2 windows, save in first, try to save in second)
  - [ ] Test: Verify conflict modal displays
  - [ ] Test: Click Refresh and verify data reloads

- [ ] Task 7: Create related policies section (AC: #13)
  - [ ] Create `src/components/RelatedPoliciesSection.tsx`
    - Display table of policies linked to this customer
    - Columns: Policy Number, Type, Status, Start Date, Actions (View)
    - Policy Type shown as label: MOTOR, ENDOWMENT, HOUSE, COMMERCIAL
    - Status shown as badge: color coded
    - "Create New Policy" button → /policies/create?customerId={customerId}
    - Click "View" button → /policies/{policyId}
    - Loading spinner while fetching policies
    - "No policies" message if customer has no policies
  - [ ] Fetch policies on page load:
    - Call PolicyService.searchPolicies({ customerId, limit: 100 })
    - Store in state
    - Handle errors gracefully
  - [ ] Use MUI Table components
  - [ ] Test: Verify policies load and display correctly
  - [ ] Test: Click "Create New Policy" and verify navigation with customerId param
  - [ ] Test: Click "View" on policy and verify navigation

- [ ] Task 8: Create audit history section (AC: #14)
  - [ ] Create `src/components/AuditHistorySection.tsx`
    - Expandable section (collapsed by default)
    - Show audit log entries for this customer (last 10 entries)
    - Columns: Date, User, Operation, Changes
    - Operation values: CREATE, READ, UPDATE, DELETE
    - Changes shown as JSON or formatted text (e.g., "firstName: 'Jane' → 'Janet'")
    - Only visible if user has audit_viewer or admin role
  - [ ] Fetch audit history:
    - Call AuditService.getAuditLog({ entityType: 'CUSTOMER', entityId: customerId, limit: 10 })
    - Store in state
    - Handle errors gracefully
  - [ ] Use MUI Accordion for expand/collapse
  - [ ] Use MUI Table for audit entries
  - [ ] Visibility check:
    - Use useAuth() to get user roles
    - Show section only if user.roles includes 'audit_viewer' or 'admin'
  - [ ] Test: Verify section displays only for authorized users
  - [ ] Test: Click expand and verify audit entries load
  - [ ] Test: Verify changes displayed correctly

- [ ] Task 9: Add loading and error states (AC: #12)
  - [ ] Loading state while fetching customer:
    - Show skeleton loaders for all fields
    - Show spinner for related policies section
    - Show spinner for audit history section
    - Disable Edit button
  - [ ] Error state if fetch fails:
    - Show error message with details
    - "Retry" button to re-fetch
    - Back button to return to customer list
  - [ ] 404 state if customer not found:
    - Show 404 error message
    - "Back to Search" button → /customers/search
  - [ ] Test: Verify skeletons display while loading
  - [ ] Test: Verify error message displays on failure
  - [ ] Test: Verify 404 displays if customer not found

- [ ] Task 10: Implement accessibility features (AC: all)
  - [ ] Add ARIA labels:
    - `aria-label="Customer first name"`
    - `aria-label="Customer last name"`
    - etc. for all fields
  - [ ] Keyboard navigation:
    - Tab through all fields and buttons
    - Enter to submit form (Save button)
    - Escape to close edit mode (Cancel)
  - [ ] Screen reader support:
    - Form labels associated with inputs via htmlFor
    - Status badge described via aria-label
    - Error messages announced via aria-live
    - Timestamp formats should be clear (e.g., "Created November 1, 2025")
  - [ ] Test: Use keyboard to navigate entire page
  - [ ] Test: Screen reader reads all content correctly

- [ ] Task 11: Implement responsive design (AC: all)
  - [ ] Detail view responsive:
    - Desktop: fields in 2 columns
    - Tablet: fields in 1 column
    - Mobile: fields stacked vertically
  - [ ] Edit form responsive:
    - Desktop: fields in 2 columns
    - Mobile: fields in single column
  - [ ] Related Policies table responsive:
    - Desktop: all columns visible
    - Tablet: hide StartDate, show in row expansion
    - Mobile: collapse to card view
  - [ ] Use MUI Grid with responsive breakpoints
  - [ ] Test: DevTools responsive mode (mobile, tablet, desktop)

- [ ] Task 12: Create integration tests (AC: all)
  - [ ] Create `tests/integration/CustomerDetailPage.test.tsx`
    - Test: Page loads and displays customer details
    - Test: Edit button switches to edit mode
    - Test: Form validation works
    - Test: Save button submits form
    - Test: Cancel button discards changes
    - Test: Success message displays on save
    - Test: Error handling on API failure
    - Test: 404 displays if customer not found
    - Test: Related policies section displays
    - Test: Audit history section displays (if authorized)
    - Test: Conflict modal displays on concurrent edit
  - [ ] Mock CustomerService API calls
  - [ ] Mock PolicyService API calls (for related policies)
  - [ ] Mock AuditService API calls
  - [ ] Test: Run `npm test` and verify all pass

- [ ] Task 13: Add API endpoint verification (AC: #2, #8)
  - [ ] Backend requirements (Stories 2.3, 2.5, 2.8):
    - GET /api/v1/customers/{customerId} → returns customer object
    - PUT /api/v1/customers/{customerId} → updates customer
    - Response format: { data: Customer, metadata: {...} }
  - [ ] Backend requirements (Story 5.4):
    - GET /api/v1/policies?customerId=... → returns policies for customer
  - [ ] Backend requirements (Story 2.8):
    - GET /api/v1/audit?entity=CUSTOMER&entityId=... → returns audit log
  - [ ] Frontend test: Verify API client calls work correctly
  - [ ] Test: Fetch and update sample data

## Dev Notes

### Architecture Context

Story 3.5 implements the customer detail and edit interface, enabling agents to view and manage customer information. This story complements Story 3.4 (search/list) and establishes the pattern for detail pages that will be reused in policy management (Story 5.7).

**Key Design Decisions:**

1. **Mode switching (read/edit)**: Cleaner UX than separate pages; users see change impacts immediately
2. **Optimistic locking**: Prevents lost updates; detects concurrent edits gracefully
3. **Async email validation**: Prevents duplicate emails without blocking form submission
4. **Related entities inline**: Policies and audit history visible without additional navigation
5. **Real-time validation**: User gets immediate feedback on form errors
6. **Role-based visibility**: Audit history only shown to authorized users

**Dependency Chain:**
- Depends on: Story 3.1 (React setup), Story 3.2 (Auth), Story 3.3 (Navigation), Story 3.4 (Search/List), Stories 2.3, 2.5, 2.8 (Customer APIs)
- Prerequisite for: Story 3.6 (Customer Create wizard), Stories 5.7-5.9 (Policy management)

### Technical Requirements

1. **Frontend Libraries**:
   - `react-router-dom`: useParams, useSearchParams, useNavigate, useLocation
   - `@mui/material`: Card, Grid, TextField, Button, Badge, Accordion, Table, Snackbar, Dialog
   - `@mui/icons-material`: EditIcon, SaveIcon, CancelIcon, CheckCircleIcon
   - Custom validation hooks or form library (React Hook Form or Formik optional)

2. **Backend Requirements** (from Stories 2.3, 2.5, 2.8):
   - GET /api/v1/customers/{customerId} → { data: Customer, metadata }
   - PUT /api/v1/customers/{customerId} → { data: Customer, metadata }
   - GET /api/v1/policies?customerId=... → paginated policies
   - GET /api/v1/audit?entity=CUSTOMER&entityId=... → audit entries
   - 409 Conflict response on concurrent edit (optimistic locking)

3. **Environment Variables**:
   - No new variables needed

### Constraints & Requirements

- **Performance**: Page load < 2s, save < 1s, form validation < 100ms
- **Data consistency**: Always reflects latest data from backend
- **Conflict handling**: Detect and handle concurrent edits gracefully
- **Accessibility**: WCAG 2.1 AA compliance, keyboard navigation, screen reader support
- **Responsive**: Works on mobile (375px), tablet (768px), desktop (1920px+)
- **Immutable fields**: customerId, createdAt, createdBy cannot be edited

### Project Structure Notes

From Story 3.1 unified-project-structure:

```
src/
├── pages/
│   └── CustomerDetailPage.tsx
├── components/
│   ├── CustomerDetailView.tsx
│   ├── CustomerEditForm.tsx
│   ├── RelatedPoliciesSection.tsx
│   ├── AuditHistorySection.tsx
│   ├── ConflictModal.tsx
│   └── StatusBadge.tsx (reusable)
├── services/
│   ├── CustomerService.ts (add getCustomer, updateCustomer)
│   ├── PolicyService.ts (add searchPolicies)
│   └── AuditService.ts (add getAuditLog)
├── hooks/
│   └── useAsync.ts (custom hook for async operations)
└── types/
    └── index.ts (Customer, Policy, AuditEntry types)
```

### Learnings from Previous Story

**From Story 3.4 (Search/List Page):**
- Use useSearchParams for URL-based state management
- Pattern for loading/error/empty states
- API pagination and filtering patterns
- Table component patterns with actions

**From Story 3.3 (Dashboard):**
- Layout component wraps pages with Header and Sidebar
- MUI component patterns (Card, Grid, Button, etc.)
- Navigation patterns with useNavigate
- Role-based visibility checks using useAuth

**From Story 3.2 (Authentication):**
- useAuth hook provides user roles and info
- JWT token attached to API calls
- Protected route patterns

**From Story 3.1 (React Setup):**
- TypeScript strict mode
- Vite build system
- Material Design 3 theme
- ESLint + Prettier configured

**From Stories 2.3, 2.5, 2.8 (Customer APIs):**
- GET /api/v1/customers/{customerId} endpoint
- PUT /api/v1/customers/{customerId} endpoint
- 409 Conflict response on concurrent edit
- Audit logging pattern

**Patterns to Maintain:**
- Custom hooks for business logic
- Controlled components with React state
- MUI components for consistency
- TypeScript for type safety
- Error boundaries and recovery
- Success/error toast notifications

### References

- [React Hook Form Documentation](https://react-hook-form.com/)
- [MUI TextField Validation Patterns](https://mui.com/material-ui/react-text-field/)
- [MUI Accordion (Expand/Collapse)](https://mui.com/material-ui/react-accordion/)
- [Async Validation Patterns](https://react-hook-form.com/form-builder)
- [Optimistic Updates in React](https://react.dev/reference/react/useState#updating-state-based-on-previous-state)
- [Handling Concurrent Edits](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/409)
- [WCAG Form Accessibility](https://www.w3.org/WAI/tutorials/forms/)
- [Source: docs/epics.md#story-35-customer-detail-and-edit-page]
- [Source: docs/stories/3-4-customer-search-and-list-page.md]
- [Source: docs/stories/3-1-react-project-setup-with-vite-and-material-design.md]

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 3.5 CREATED from Epic 3 via create-story workflow
- 2025-11-03: Based on epics.md AC and Stories 2.3, 2.5, 2.8 Customer APIs
- 2025-11-03: Incorporates patterns from Story 3.4 (detail/list pattern)

### Completion Notes List

### File List
