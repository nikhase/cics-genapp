# Story 3.6: Customer Create/Edit Page with Vaadin Form

**Story ID:** 3-6-customer-create-edit-page-with-vaadin-form
**Epic:** Epic 3 - Vaadin Frontend - Core User Interface
**Status:** Ready for Dev
**Story Points:** 8
**Sprint:** TBD

---

## Story Summary

**As a** Customer Service Agent,
**I want** to create new customers or edit existing customer information with form validation,
**So that** I can manage customer records accurately and efficiently.

---

## Acceptance Criteria

1. **Shared Form Component (Create & Edit)**
   - [ ] Single form used for both create and edit modes
   - [ ] Form routed to `/customers/new` (create mode) and `/customers/{id}/edit` (edit mode)
   - [ ] Edit mode: pre-fills form with existing customer data from API call
   - [ ] Create mode: shows empty form

2. **Form Fields & Validation**
   - [ ] **First Name** - Required, text field, max 50 chars
   - [ ] **Last Name** - Required, text field, max 50 chars
   - [ ] **Email** - Required, email field, validated format, must be unique (checked via API)
   - [ ] **Phone** - Required, text field, formatted (+1-555-1234), max 20 chars
   - [ ] **Address (Street)** - Required, text field, max 100 chars
   - [ ] **Address (City)** - Required, text field, max 50 chars
   - [ ] **Address (State)** - Required, dropdown or text field, max 2 chars
   - [ ] **Address (Zip Code)** - Required, text field, validated format (5 digits), max 10 chars
   - [ ] **Status** - Dropdown (Active/Inactive), default Active
   - [ ] All validation errors displayed below each field in red

3. **Client-Side Validation**
   - [ ] Required fields marked with asterisk (*)
   - [ ] Real-time validation as user types (or on blur)
   - [ ] Email format validated before submit
   - [ ] Phone format validated or formatted automatically
   - [ ] Zip code must be numeric
   - [ ] Submit button disabled while form has errors

4. **Server-Side Validation**
   - [ ] All validations re-checked on backend (API)
   - [ ] Email uniqueness checked against existing customers
   - [ ] API returns validation errors → displayed in form
   - [ ] Duplicated unique constraint errors handled gracefully

5. **Form Submission**
   - [ ] Create mode: `POST /api/v1/customers` with form data
   - [ ] Edit mode: `PUT /api/v1/customers/{id}` with form data
   - [ ] Success: Shows toast notification "Customer saved successfully"
   - [ ] Success: Redirects to Customer Detail page (Story 3.5)
   - [ ] Error: Shows error message, keeps form data for retry

6. **Form State & UX**
   - [ ] Submit button shows loading indicator during save
   - [ ] Submit button disabled during save (prevent double-submit)
   - [ ] Cancel button returns to previous page (Detail or Search)
   - [ ] Unsaved changes warning (optional): "You have unsaved changes"
   - [ ] Page title indicates create or edit mode

7. **Accessibility & Responsiveness**
   - [ ] Form readable and usable on mobile devices
   - [ ] All fields keyboard accessible
   - [ ] Proper labels and error messaging
   - [ ] Submit button is large touch target (mobile-friendly)

---

## Technical Notes

- **Form Component:** Vaadin Form layout with Binder for data binding
- **Validation:** Vaadin's built-in validators + custom validators for unique fields
- **API Calls:** POST for create, PUT for edit
- **Error Handling:** Map API validation errors to form field errors
- **Data Binding:** Binder automatically manages form ↔ customer object mapping

---

## Dependencies

**Depends On:** Stories 3.1, 3.3, 3.5 (Vaadin setup, Dashboard navigation, Detail page links here)
**Blocks:** None (but links from Dashboard and Detail pages)

---

## Test Plan

**Manual Testing - Create Mode:**
1. Navigate to Dashboard → "Create New Customer" button
2. Form displays empty
3. Leave required field blank → error "This field is required"
4. Enter invalid email (e.g., "notanemail") → error "Invalid email format"
5. Enter invalid zip (e.g., "ABCDE") → error "Must be numeric"
6. Fill form completely with valid data
7. Submit → loading indicator shows
8. Success → redirects to Customer Detail page
9. Verify new customer appears in Customer Search (Story 3.4)

**Manual Testing - Edit Mode:**
1. Navigate to Customer Detail page (Story 3.5)
2. Click "Edit Customer" → form pre-filled with customer data
3. Edit phone number
4. Submit → success message
5. Detail page refreshes with updated data

**Validation Testing:**
- Required field validation (all required fields)
- Email uniqueness (attempt to use email of existing customer)
- Format validation (email, phone, zip)
- Server-side error handling (attempt invalid operation)

**Responsive Testing:**
- Mobile layout: form fields stack vertically
- Tablet: form readable, buttons accessible
- Desktop: comfortable spacing

---

## Acceptance Notes

- Form should be straightforward (no wizard/steps needed for MVP)
- Validation should be helpful (clear error messages, not cryptic)
- Success feedback should be clear (toast notification or page transition)
- Cancel button should be easily discoverable

---

**Created:** 2025-11-04 (Vaadin Pivot Decision)
**Story Replanned From:** 3-6 Customer Create with Form Wizard (simplified to single form for MVP)
