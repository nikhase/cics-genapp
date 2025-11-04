# Story 3.8: Policy Detail Page with Vaadin Display Components

**Story ID:** 3-8-policy-detail-page-with-vaadin-display-components
**Epic:** Epic 3 - Vaadin Frontend - Core User Interface
**Status:** Ready for Dev
**Story Points:** 5
**Sprint:** TBD

---

## Story Summary

**As a** Customer Service Agent,
**I want** to view full details of a selected policy including customer information and type-specific attributes,
**So that** I can understand policy coverage, terms, and customer relationship.

---

## Acceptance Criteria

1. **Policy Details Display**
   - [ ] Page routed to `/policies/{id}` (Vaadin routing parameter)
   - [ ] Calls backend API: `GET /api/v1/policies/{id}`
   - [ ] Displays policy information in a read-only form:
     - [ ] Policy ID
     - [ ] Policy Type (Motor, Endowment, House, Commercial)
     - [ ] Customer Name (link to Customer Detail page)
     - [ ] Status (Active, Lapsed, Renewed)
     - [ ] Premium Amount
     - [ ] Start Date
     - [ ] End Date
     - [ ] Created Date
     - [ ] Last Updated Date

2. **Type-Specific Attributes**
   - [ ] Based on policy type, display additional fields:
     - **Motor:** Vehicle Registration, Vehicle Model, Coverage Type
     - **Endowment:** Maturity Date, Bonus Rate, Fund Value
     - **House:** Property Address, Coverage Limit, Deductible
     - **Commercial:** Business Type, Coverage Limit, Deductible
   - [ ] Type-specific fields displayed in a separate section (clearly labeled)
   - [ ] Backend API returns type-specific fields based on policy type

3. **Customer Link**
   - [ ] Customer name displayed as clickable link → navigates to Customer Detail page (Story 3.5)
   - [ ] Customer contact information displayed (optional): phone, email
   - [ ] "View Customer" button as alternative to link

4. **Action Buttons**
   - [ ] "Edit Policy" button → navigates to Policy Create/Edit page (future story)
   - [ ] "Back" button → returns to Policy List page (Story 3.7)
   - [ ] "Delete Policy" button (optional, grayed out if policy is active)

5. **Loading & Error States**
   - [ ] Loading indicator while fetching policy data
   - [ ] If policy not found: "Policy not found" message
   - [ ] If API error: "Unable to load policy, please try again" message
   - [ ] Type-specific data loads after basic policy details

6. **Responsive Layout**
   - [ ] Desktop: 2-column layout (policy details on left, type-specific on right)
   - [ ] Tablet/Mobile: 1-column layout (stacked vertically)
   - [ ] All text readable and accessible on all devices

7. **User Experience**
   - [ ] Page title updates to policy number (e.g., "Policy POL-001234 - Details")
   - [ ] Breadcrumbs show navigation path: Dashboard > Policies > POL-001234
   - [ ] All information readable without scrolling horizontally (mobile-friendly)

---

## Technical Notes

- **Route Parameter:** Use Vaadin RouteParameters to extract policy ID
- **API Response:** Backend provides policy details + type-specific attributes
- **Type-Specific UI:** Use conditional rendering based on policy type enum
- **Customer Link:** Use Vaadin RouterLink to navigate to Customer Detail page
- **Read-Only Display:** Use Vaadin read-only components

---

## Dependencies

**Depends On:** Stories 3.1, 3.7 (Vaadin setup, Policy List navigates here)
**Blocks:** None (but future Policy Edit story will use similar form)

---

## Test Plan

**Manual Testing - Motor Policy:**
1. Navigate to Policy List (Story 3.7)
2. Search for a Motor policy
3. Click result row → navigates to Policy Detail page
4. Verify all basic policy information displayed correctly
5. Verify Motor-specific fields displayed (Registration, Model, Coverage Type)
6. Click customer name → navigates to Customer Detail page (Story 3.5)
7. Click "Back" → returns to Policy List

**Manual Testing - Other Policy Types:**
1. Test with Endowment policy → verify type-specific fields (Maturity, Bonus, Fund)
2. Test with House policy → verify type-specific fields (Address, Coverage, Deductible)
3. Test with Commercial policy → verify type-specific fields (Business, Coverage, Deductible)

**Error Testing:**
- Test with invalid policy ID in URL → "Policy not found" message
- Test with API error → graceful error message

**Responsive Testing:**
- Mobile layout: fields stack vertically, readable
- Tablet: comfortable spacing
- Desktop: 2-column layout functional

---

## Acceptance Notes

- Detail page should be clean and focused on policy information
- Type-specific section should be clearly distinct from basic details
- No inline editing on this page (editing will be future story)
- Link to customer should be intuitive (blue underlined link or button)

---

**Created:** 2025-11-04 (Vaadin Pivot Decision)
**Story New:** (Was Story 3-8 API Integration, replaced with Policy Detail)
