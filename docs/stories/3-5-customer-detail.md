# Story 3.5: Customer Detail Page with Vaadin Components

**Story ID:** 3-5-customer-detail-page-with-vaadin-components
**Epic:** Epic 3 - Vaadin Frontend - Core User Interface
**Status:** Ready for Dev
**Story Points:** 5
**Sprint:** TBD

---

## Story Summary

**As a** Customer Service Agent,
**I want** to view full details of a selected customer including name, email, phone, address, and linked policies,
**So that** I can understand the customer's complete profile and relationship with the company.

---

## Acceptance Criteria

1. **Customer Details Display**
   - [ ] Page routed to `/customers/{id}` (Vaadin routing parameter)
   - [ ] Calls backend API: `GET /api/v1/customers/{id}`
   - [ ] Displays customer information in a read-only form:
     - [ ] ID (displayed, not editable)
     - [ ] First Name
     - [ ] Last Name
     - [ ] Email
     - [ ] Phone
     - [ ] Address
     - [ ] Status (Active/Inactive)
     - [ ] Created Date
     - [ ] Last Updated Date
   - [ ] Information displayed in clear, organized layout

2. **Linked Policies Section**
   - [ ] Grid showing all policies linked to customer:
     - [ ] Policy ID
     - [ ] Policy Type (Motor, Endowment, House, Commercial)
     - [ ] Status (Active, Lapsed, Renewed)
     - [ ] Premium Amount
     - [ ] Created Date
   - [ ] Click policy row → navigates to Policy Detail page (Story 3.8)
   - [ ] Empty message if no policies: "No policies found for this customer"

3. **Action Buttons**
   - [ ] "Edit Customer" button → navigates to Customer Create/Edit page (Story 3.6) with pre-filled data
   - [ ] "Create Policy" button → navigates to Policy Create (Story TBD) with customer pre-selected
   - [ ] "Back" button → returns to Customer Search page (Story 3.4)
   - [ ] "Delete Customer" button (optional, grayed out if customer has active policies)

4. **Loading & Error States**
   - [ ] Loading indicator while fetching customer data
   - [ ] If customer not found: "Customer not found" message
   - [ ] If API error: "Unable to load customer, please try again" message
   - [ ] Linked policies load after customer details (can happen in background)

5. **Responsive Layout**
   - [ ] Desktop: 2-column layout (customer details on left, policies on right)
   - [ ] Tablet/Mobile: 1-column layout (customer details, then policies below)
   - [ ] All text readable and accessible

6. **User Experience**
   - [ ] Page title updates to customer name (e.g., "John Smith - Customer Details")
   - [ ] Breadcrumbs show navigation path: Dashboard > Customers > John Smith
   - [ ] All information readable without scrolling horizontally (mobile-friendly)

---

## Technical Notes

- **Route Parameter:** Use Vaadin RouteParameters to extract customer ID from URL
- **API Calls:** Backend provides customer details + linked policies in single response (or two calls)
- **Read-Only Display:** Use Vaadin's read-only form components (no TextField edits)
- **Styling:** Grid and form layout use Vaadin built-in spacing/theming

---

## Dependencies

**Depends On:** Stories 3.1, 3.4 (Vaadin setup, Customer Search navigates here)
**Blocks:** Story 3.6 (Edit page shares form with Create page)

---

## Test Plan

**Manual Testing:**
1. Navigate to Customer Search (Story 3.4)
2. Search for a customer
3. Click result row → navigates to Customer Detail page
4. Verify all customer information displayed correctly
5. Verify linked policies grid displays (if any)
6. Click "Edit Customer" → navigates to edit form (Story 3.6)
7. Click "Back" → returns to Customer Search
8. Test with customer that has no policies → "No policies found" message
9. Test with invalid customer ID in URL → "Customer not found" message

**API Integration Test:**
- Verify HTTP GET request made to `/api/v1/customers/{id}`
- Verify response fields mapped correctly to display fields
- Verify error responses handled gracefully

---

## Acceptance Notes

- Detail page should be clean and focused on customer information
- Policies section should not overwhelm the view (grid with basic columns only)
- No inline editing on this page (editing is Story 3.6)

---

**Created:** 2025-11-04 (Vaadin Pivot Decision)
**Story Replanned From:** 3-5 Customer Detail Page (React → Vaadin)
