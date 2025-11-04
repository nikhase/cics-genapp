# SSC1 Implementation Mapping: 3270 to Vaadin

**Purpose:** Document how the legacy 3270 SSC1 Customer Menu screen maps to the new Vaadin UI across Stories 3.5, 3.6, and 3.10.

**Date:** 2025-11-04

---

## SSC1 3270 Terminal Screen Overview

The SSC1 screen is a **hybrid menu/form interface** on a 24×80 character terminal:

```
┌────────────────────────────────────────────────────────────────────────────┐
│SSC1        General Insurance Customer Menu                                 │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│        1. Cust Inquiry       Cust Number [__________]                     │
│        2. Cust Add           Cust Name :First[__________]                 │
│                                     :Last[____________________]           │
│        4. Cust Update                                                      │
│                              DOB         [__________]                     │
│                              House Name  [____________________]           │
│                              House Number[____]                           │
│                              Postcode    [________]                       │
│                              Phone: Home [____________________]           │
│                              Phone: Mob  [____________________]           │
│                              Email  Addr [___________________________]    │
│                                                                            │
│        Select Option [_]                                                  │
│[                                        ]                                 │
└────────────────────────────────────────────────────────────────────────────┘
```

**Two distinct sections:**
- **Left (rows 4-7):** Menu options (1=Inquiry, 2=Add, 4=Update)
- **Right (rows 4-13):** Data entry fields for customer lookup/creation/modification

---

## Workflows Supported by SSC1

### Workflow 1: Customer Inquiry (Option 1) - **1:1 View Flow**

**3270 Steps:**
1. Enter customer number in "Cust Number" field
2. Select option "1"
3. Press Enter
4. System displays customer details in same form fields (read-only)

**Vaadin Implementation:**
- Story 3.10 (Lookup Panel) + Story 3.5 (Customer Detail Page)
- User enters customer number in Lookup Panel at top of page
- Clicks "Lookup" button
- Navigates to `/customers/{customerId}` (Story 3.5 detail page)
- Detail page shows all customer information in read-only display

**SSC1 → Vaadin Mapping:**

| SSC1 Field | 3270 Screen | Vaadin Detail Page (Story 3.5) |
|-----------|-------------|------|
| Cust Number | [__________] (input) | Customer ID: 550e8400... (display) |
| First Name | [__________] (input) | First Name: Jane (display) |
| Last Name | [____________________] (input) | Last Name: Smith (display) |
| DOB | [__________] (input) | Date of Birth: 1985-03-15 (display) |
| House Name | [____________________] (input) | Address: 123 Main St (display) |
| House Number | [____] (input) | — (not shown separately in Vaadin) |
| Postcode | [________] (input) | ZIP Code: 97201 (display) |
| Phone: Home | [____________________] (input) | Phone: +1-555-0123 (display) |
| Phone: Mob | [____________________] (input) | Phone (Mobile): +1-555-0124 (display) |
| Email Addr | [___________________________] (input) | Email: jane.smith@example.com (display) |

---

### Workflow 2: Customer Add (Option 2) - **Create Flow**

**3270 Steps:**
1. Fill in customer details in all form fields
2. Select option "2"
3. Press Enter
4. System creates customer record

**Vaadin Implementation:**
- Story 3.6 (Customer Create/Edit Form)
- User clicks "New Customer" button in header
- Multi-step wizard:
  - Step 1: Basic Info (first name, last name, DOB)
  - Step 2: Contact (email, phone)
  - Step 3: Address (street, city, state, zip)
  - Step 4: Review
- Submits form → API creates customer → Redirects to detail page

**SSC1 → Vaadin Mapping:** Same fields as Inquiry, but form is editable and split across wizard steps for better UX

---

### Workflow 3: Customer Update (Option 4) - **Edit Flow**

**3270 Steps:**
1. Enter customer number in "Cust Number" field
2. Modify desired fields
3. Select option "4"
4. Press Enter
5. System updates customer record

**Vaadin Implementation:**
- Story 3.10 (Lookup Panel) + Story 3.5 (Edit Mode) or Story 3.6 (if full re-form)
- **Option A (Quicker):**
  1. User enters customer number in Lookup Panel
  2. Clicks "Lookup" button
  3. Navigates to `/customers/{customerId}?mode=edit`
  4. Story 3.5 detail page switches to edit mode
  5. User modifies fields and clicks "Save"

- **Option B (Guided):**
  1. User enters customer number in Lookup Panel
  2. Clicks "Lookup" button
  3. Navigates to customer detail page
  4. User clicks "Edit Customer" button
  5. Launches wizard form similar to Story 3.6
  6. User modifies fields across wizard steps and submits

**Note:** Story 3.5 and 3.6 should clarify which approach is used.

---

## Story 3.10: Lookup & List Overview Page - SSC1 Mapping

### Lookup Panel (SSC1 Option 1 Direct Lookup)

**3270 Equivalent:** User enters customer number in "Cust Number" field, selects Option 1

**Vaadin UI:**
```
┌─────────────────────────────────────────────────────────────┐
│ Quick Customer Lookup                                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ ◯ Search by Number    ◯ Search by Name                     │
│                                                             │
│ [Enter Customer Number...............] [ Lookup ]           │
│                                                             │
│ OR                                                          │
│                                                             │
│ [Enter First or Last Name..........] [ Lookup ]             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Acceptance Criteria (3.10 AC #1):**
- Input: Customer Number (10 digits, exact match) → Navigate to `/customers/{customerId}`
- Input: Customer Name (substring match) → Navigate to `/customers/{customerId}`
- Error: "Customer not found" if no match
- **Maps directly to SSC1 Option 1 workflow**

### List Grid (Extension beyond SSC1)

**3270 Equivalent:** N/A - SSC1 only shows one customer at a time

**Vaadin UI:**
```
┌────────────────────────────────────────────────────────────────────┐
│ Customer List                                                      │
├────────────────────────────────────────────────────────────────────┤
│ Search: [____________________] Status: [All ▼] [Search] [Clear]   │
├────────────────────────────────────────────────────────────────────┤
│ ID      │ Name             │ Email            │ Phone    │ Status │ │
├─────────┼──────────────────┼──────────────────┼──────────┼────────┤ │
│ 550e... │ Jane Smith       │ jane@example.com │ 555-0101 │ Active │ │
│ 641f... │ John Doe         │ john@example.com │ 555-0102 │ Active │ │
│ 792a... │ Sarah Johnson    │ sarah@example.com│ 555-0103 │ Inact. │ │
└────────────────────────────────────────────────────────────────────┘
Showing 25 of 1,234 customers | Page 1 of 50
```

**Purpose:**
- Provides modern browsing alternative to single-customer lookup
- Enables discovery of customers without knowing exact number/name
- Provides filtering, sorting, pagination for large datasets
- **Complements SSC1 functionality for modern UX**

---

## Complete User Journey Mapping

### Journey 1: Find Customer by Number (Option 1)

| Step | SSC1 (3270) | Vaadin (Story 3.10) |
|------|-------------|----------|
| 1 | User sees SSC1 menu screen | User lands on `/customers/list` |
| 2 | User enters customer number (e.g., "0000000123") | User enters number in Lookup Panel |
| 3 | User selects Option "1" | User clicks "Lookup" button |
| 4 | System displays customer record in form fields | System navigates to `/customers/0000000123` |
| 5 | User sees full customer details (read-only) | User sees Story 3.5 detail page |

### Journey 2: Add New Customer (Option 2)

| Step | SSC1 (3270) | Vaadin (Story 3.6) |
|------|-------------|----------|
| 1 | User sees SSC1 menu screen | User clicks "New Customer" button |
| 2 | User fills in customer details | Multi-step wizard opens |
| 3 | User selects Option "2" | User fills form across wizard steps |
| 4 | System creates customer record | User clicks "Confirm" on review step |
| 5 | System displays confirmation | API creates customer, navigates to detail |

### Journey 3: Update Existing Customer (Option 4)

| Step | SSC1 (3270) | Vaadin (Story 3.5/3.6) |
|------|-------------|----------|
| 1 | User sees SSC1 menu screen | User lands on `/customers/list` |
| 2 | User enters customer number | User enters number in Lookup Panel |
| 3 | User modifies fields | User clicks "Lookup" → navigates to detail |
| 4 | User selects Option "4" | User clicks "Edit" button |
| 5 | System updates record | Form opens (inline or wizard) |
| 6 | — | User modifies fields and clicks "Save" |

---

## Field Mapping: SSC1 ↔ Vaadin Database Schema

**SSC1 COBOL Form Fields** → **Vaadin Java/Database Fields**

| SSC1 Field | COBOL Type | Length | Vaadin Field | Java Type | Notes |
|-----------|-----------|--------|-------------|----------|-------|
| ENT1CNO (Cust Number) | PIC 9(10) | 10 | customerId | UUID | Note: UUID format differs from COBOL numeric |
| ENT1FNA (First Name) | PIC X(10) | 10 | firstName | String(100) | Extended for modern names |
| ENT1LNA (Last Name) | PIC X(20) | 20 | lastName | String(100) | Extended for modern names |
| ENT1DOB (DOB) | PIC X(10) | 10 | dateOfBirth | LocalDate | ISO 8601 format (YYYY-MM-DD) |
| ENT1HNM (House Name) | PIC X(20) | 20 | address | String(255) | Generalized for street addresses |
| ENT1HNO (House Number) | PIC 9(4) | 4 | — | — | Merged into address field |
| ENT1HPC (Postcode) | PIC X(8) | 8 | zipCode | String(10) | Extended for international formats |
| ENT1HP1 (Home Phone) | PIC X(20) | 20 | phone | String(20) | Single phone field; type specified separately |
| ENT1HP2 (Mobile Phone) | PIC X(20) | 20 | phone_mobile | String(20) | Optional mobile field |
| ENT1HMO (Email) | PIC X(27) | 27 | email | String(100) | Email validation added |

**Additional Vaadin Fields (Not in SSC1):**
- `status` (ACTIVE/INACTIVE) - for soft-delete
- `createdAt`, `updatedAt` - audit trail
- `createdBy`, `updatedBy` - audit trail
- `city`, `state` - address normalization (SSC1 only had postcode)

---

## Design Principles for Vaadin Implementation

1. **Functional Equivalence:** Lookup panel + detail page = SSC1 Inquiry flow
2. **UX Enhancement:** Grid + search + filter = modern alternative to sequential lookups
3. **Field Normalization:** Map rigid COBOL fields to flexible modern schema (e.g., address consolidation)
4. **1:1 Detail View:** Story 3.5 provides the "1:1 view" that SSC1 displayed after Inquiry
5. **Responsive:** Works on desktop, tablet, mobile (SSC1 was fixed 80-column width)

---

## Validation Mapping

**SSC1 Validation Rules** → **Vaadin Acceptance Criteria**

| Validation | SSC1 | Vaadin Implementation |
|-----------|------|----------------------|
| Customer Number Required | Option 1: Must enter for Inquiry | Story 3.10: Lookup panel requires input |
| Name Required | Option 2: Must enter for Add | Story 3.6: Step 1 (Basic Info) required |
| Phone Format | (not strictly enforced in terminal) | Story 3.6: Regex validation + format mask |
| Email Format | (not in SSC1) | Story 3.6: Email validation, uniqueness check |
| DOB Validation | (not strictly enforced) | Story 3.6: Must be 18+ years old |
| Duplicate Prevention | (handled in COBOL) | Story 2.2 API: Returns 409 Conflict |

---

## Summary

**Story 3.10 completes the SSC1 modernization by providing:**

1. ✅ **Lookup Panel** - Implements SSC1 Option 1 (Customer Inquiry) with customer number search
2. ✅ **List Grid** - Extends beyond SSC1 with modern browsing, filtering, sorting, pagination
3. ✅ **Navigation** - Links to Story 3.5 (Detail/1:1 view) for reading customer information
4. ✅ **Edit Support** - Links to Story 3.5 (Edit mode) for Workflow 3 (SSC1 Option 4 Update)
5. ✅ **Create Support** - Links to Story 3.6 (Create wizard) for Workflow 2 (SSC1 Option 2 Add)

**The combination of Stories 3.10 + 3.5 + 3.6 provides full feature parity with SSC1 Customer Menu while providing a modern, responsive, accessible web UI.**

---

**Reference Documents:**
- `/docs/3270screens/SSC1-Customer-Menu.md` - Original SSC1 specification
- `/docs/stories/3-5-customer-detail-and-edit-page.md` - Customer 1:1 view (detail & edit)
- `/docs/stories/3-6-customer-create-page-with-form-wizard.md` - Customer create form
- `/docs/stories/3-10-customer-list-overview-page-with-vaadin-grid.md` - Customer lookup & list

