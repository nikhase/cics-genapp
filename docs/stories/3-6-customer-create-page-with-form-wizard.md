# Story 3.6: Customer Create Page with Form Wizard

Status: drafted

## Story

As a Customer Service Agent,
I want to create a new customer using a guided multi-step wizard,
So that I can add customers with minimal errors.

## Acceptance Criteria

1. Page created at route /customers/create
2. Multi-step wizard with progress indicator:
   - Step 1: Basic Info (first name, last name, date of birth)
   - Step 2: Contact (email, phone)
   - Step 3: Address (street, city, state, zip code)
   - Step 4: Review and Confirm
3. Form fields with validation:
   - firstName: text, required, 1-100 chars
   - lastName: text, required, 1-100 chars
   - dateOfBirth: date picker, optional, must be age 18+
   - email: email input, required, unique, valid email format
   - phone: tel input, optional, valid phone format
   - address: text, optional
   - city: text, optional
   - state: dropdown, optional (populated from state list)
   - zipCode: text, optional, 5-6 digits
4. Real-time validation feedback (field-level error messages)
5. Next button disabled if current step has errors
6. Previous button always enabled (can go back)
7. Progress indicator shows current step
8. Review step shows all entered data (read-only)
9. "Edit" button next to each section allows editing that section
10. Confirm button creates customer via API
11. Loading state while creating (spinner, button disabled)
12. Success message with customer ID and options:
    - "View Customer" → /customers/{customerId}
    - "Create Another" → reset wizard
    - "Go to Dashboard" → /dashboard
13. Error handling for duplicate email and validation errors
14. Keyboard support: Tab navigation, Enter submits (on review step)

## Tasks / Subtasks

- [ ] Task 1: Create wizard layout and step management
- [ ] Task 2: Implement Step 1 (Basic Info) form
- [ ] Task 3: Implement Step 2 (Contact) form
- [ ] Task 4: Implement Step 3 (Address) form
- [ ] Task 5: Implement Step 4 (Review) page
- [ ] Task 6: Implement form state management and navigation
- [ ] Task 7: Implement API integration for customer creation
- [ ] Task 8: Implement success/error handling and user feedback
- [ ] Task 9: Implement accessibility and keyboard support

## Dev Notes

- Depends on Story 3.8 (API Service Layer) for CustomerService.createCustomer()
- Depends on Story 2.2 (Backend API)
- Reuse form components from Story 3.7
- Use MUI for UI components
- Consider using React Hook Form for form state management

## Dev Agent Record

- Context Reference: docs/stories/3-6-customer-create-page-with-form-wizard.context.xml
