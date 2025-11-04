# Story 3.5: Customer Detail and Edit Page

Status: drafted

## Story

As a Customer Service Agent,
I want to view and edit customer details,
So that I can manage customer information.

## Acceptance Criteria

1. Page created at route /customers/{customerId}
2. Detail view (read-only mode) shows customer information:
   - Name (first/last), Date of birth, Email, Phone
   - Address (street, city, state, zip)
   - Status (badge: green/gray)
   - Created/updated timestamps and by whom
3. Edit button switches to edit mode
4. Edit mode displays form fields with current values pre-populated
5. Form validation (real-time feedback):
   - Email valid format
   - Phone valid format (if provided)
   - Name required, 1-100 chars
6. Save button submits changes (disabled until form is valid)
7. Cancel button discards changes and returns to read-only mode
8. Success message on save: "Customer updated successfully" (toast notification, 3s duration)
9. Error message on failure with details
10. 404 Not Found error if customer doesn't exist
11. Optimistic locking: if customer was updated by another user, show conflict error
12. Loading state while fetching/saving (spinner)
13. Related Policies section (read-only):
    - Table of policies linked to this customer
    - Columns: Policy Number, Type, Status, Start Date, Actions (View)
    - "Create New Policy" button → /policies/create?customerId={customerId}
14. Audit History section (if user has audit_viewer role):
    - Expandable section showing immutable audit log for this customer
    - Columns: Date, User, Operation, Changes

## Tasks / Subtasks

- [ ] Task 1: Create page component and fetch customer data
- [ ] Task 2: Implement detail view (read-only mode)
- [ ] Task 3: Implement edit mode with form
- [ ] Task 4: Implement form validation and error handling
- [ ] Task 5: Implement save functionality with optimistic locking
- [ ] Task 6: Implement related policies section
- [ ] Task 7: Implement audit history section
- [ ] Task 8: Implement 404 error handling
- [ ] Task 9: Responsive design and accessibility

## Dev Notes

- Depends on Story 3.8 (API Service Layer) for CustomerService.getCustomer() and updateCustomer()
- Depends on Story 2.3 and 2.5 (Backend APIs)
- Reuse form components from Story 3.7
- Use MUI for UI components

## Dev Agent Record

- Context Reference: docs/stories/3-5-customer-detail-and-edit-page.context.xml
