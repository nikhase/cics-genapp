# Story 3.11: Customer Menu - Unified Management Interface (SSC1-based)

**Epic:** 3 (Vaadin Frontend - Core User Interface)
**Story ID:** 3.11
**Status:** Drafted
**Priority:** High
**Story Points:** 8

## User Story

**As a** Customer Service Agent,
**I want** a unified customer management interface that combines menu selection with contextual form handling,
**So that** I can perform inquiry, add, or update operations from a single integrated screen (matching the familiar SSC1 terminal experience).

## Context

This story implements the **SSC1 Customer Management Menu** as a single, unified Vaadin page that combines:
- **Left side:** Menu options (1=Inquiry, 2=Add, 4=Update)
- **Right side:** Dynamic data entry form that changes behavior based on selected option

The key concept: **Option selection determines what the form does**, not navigation to different pages.

**Reference:** See `/docs/3270screens/SSC1-Customer-Menu.md` for the original 3270 screen specification.

### SSC1 Behavioral Mapping

| Option | User Action | Form Behavior | Required Fields | On Submit |
|--------|------------|---------------|-----------------|-----------|
| 1: **Inquiry** | User enters customer number | Pre-fills all fields from DB | Customer Number (required) | Load customer record, display details |
| 2: **Add** | User fills all blank fields | All fields enabled, blank | Name (required) | Create new customer record |
| 4: **Update** | User enters number, modifies fields | Pre-fills fields, enables editing | Customer Number (required) | Update existing customer record |

---

## Acceptance Criteria

### Display Requirements - Menu Section

1. **Page Layout** (SSC1-inspired unified interface)
   - Page title: "SSC1 - General Insurance Customer Menu"
   - Two-column layout:
     - **Left column:** Menu options (width ~30% of viewport)
     - **Right column:** Data entry form (width ~70% of viewport)
   - Responsive on mobile: Stack vertically (menu above form)

2. **Menu Options**
   - Display three selectable options as buttons or radio buttons:
     - **Option 1:** "Customer Inquiry" (description: "Look up and view customer details")
     - **Option 2:** "Customer Add" (description: "Create a new customer record")
     - **Option 4:** "Customer Update" (description: "Modify existing customer information")
   - Only one option can be selected at a time
   - Default selection: Option 1 (Inquiry)
   - Selected option is visually highlighted (bold, background color, or border)
   - Radio button or tab-style selection (user's choice of implementation)

3. **Form Section Title**
   - Dynamically changes based on selected option:
     - Option 1 selected → "Customer Inquiry Form"
     - Option 2 selected → "Add New Customer"
     - Option 4 selected → "Update Customer"

### Display Requirements - Data Entry Form

4. **Form Fields** (All fields present, behavior changes by option)

   **Customer Identification Section:**
   - **Customer Number** (text input, 10 characters max)
     - Placeholder: "Enter 10-digit customer number"
     - Enabled: Always (for Inquiry and Update to identify record)
     - Required: Yes for Inquiry and Update, No for Add
     - Format validation: Numeric, left-pad with zeros

   - **First Name** (text input, 10 characters max)
     - Placeholder: "First name"
     - Enabled: Always
     - Required: Yes for Add, Read-only for Inquiry, Editable for Update

   - **Last Name** (text input, 20 characters max)
     - Placeholder: "Last name"
     - Enabled: Always
     - Required: Yes for Add, Read-only for Inquiry, Editable for Update

   **Personal Details Section:**
   - **Date of Birth** (date input, yyyy-mm-dd format)
     - Placeholder: "yyyy-mm-dd"
     - Format: YYYY-MM-DD
     - Required: Yes for Add, Read-only for Inquiry, Editable for Update

   - **House Name** (text input, 20 characters max)
     - Placeholder: "House name"
     - Required: Yes for Add, Read-only for Inquiry, Editable for Update

   **Address Information Section:**
   - **House Number** (numeric input, 4 digits max)
     - Placeholder: "0000"
     - Required: Yes for Add, Read-only for Inquiry, Editable for Update

   - **Postcode** (text input, 8 characters max)
     - Placeholder: "Postcode"
     - Required: Yes for Add, Read-only for Inquiry, Editable for Update

   **Contact Information Section:**
   - **Home Phone** (text input, 20 characters max)
     - Placeholder: "+1234567890"
     - Required: No
     - Read-only for Inquiry, Editable for Add/Update

   - **Mobile Phone** (text input, 20 characters max)
     - Placeholder: "+1234567890"
     - Required: No
     - Read-only for Inquiry, Editable for Add/Update

   - **Email Address** (text input, 27 characters max, email validation)
     - Placeholder: "user@example.com"
     - Required: No
     - Read-only for Inquiry, Editable for Add/Update

### Functional Behavior by Option

5. **Option 1 - Customer Inquiry**
   - **Form state:** Read-only (all fields disabled for editing)
   - **Customer Number field:** Enabled for input
   - **Submit button label:** "Look Up"
   - **On submit:**
     - Validate customer number is provided and numeric
     - Call GET `/api/v1/customers/{customerId}` to fetch record
     - Pre-fill all form fields with returned data
     - Display success message: "Customer found"
     - If not found: Show error "Customer not found. Please verify the number."
   - **Use case:** User enters number → Sees complete customer profile (read-only)

6. **Option 2 - Customer Add**
   - **Form state:** All fields editable, blank
   - **Customer Number field:** Disabled (will be auto-generated)
   - **Submit button label:** "Create Customer"
   - **On submit:**
     - Validate all required fields (Name, DOB, Address, Contact)
     - Call POST `/api/v1/customers` with form data
     - On success: Show "Customer created successfully. ID: {newCustomerId}"
     - Clear form and reset to blank
     - Optionally: Offer button to "View Created Customer"
     - On error: Show validation errors per field
   - **Use case:** User fills blank form → Creates new customer record

7. **Option 4 - Customer Update**
   - **Form state:** Customer Number enabled, other fields enabled after lookup
   - **Customer Number field:** Enabled for input
   - **Submit button label:** "Update Customer"
   - **Two-step workflow:**
     - **Step 1 - Lookup:** User enters customer number, clicks "Look Up"
       - Calls GET `/api/v1/customers/{customerId}`
       - Pre-fills all form fields with current data
       - Form becomes editable (fields change from read-only to enabled)
     - **Step 2 - Edit & Save:** User modifies fields, clicks "Update Customer"
       - Validates required fields
       - Calls PUT `/api/v1/customers/{customerId}` with modified data
       - Shows success: "Customer updated successfully"
       - Displays updated record (or offers to re-lookup)
   - **Use case:** User enters number → Sees current data → Modifies fields → Saves changes

### Status Display and Error Handling

8. **Error Message Area**
   - Located at bottom of form
   - Displays validation errors, API errors, success messages
   - Background color indicates status:
     - **Red:** Error (required field missing, API error, customer not found)
     - **Green:** Success (customer found, created, updated)
     - **Yellow:** Warning (validation needed)
   - Auto-clears after successful operation or when user starts typing

9. **Loading States**
   - Show spinner during "Look Up" or "Create/Update" operations
   - Submit button disabled during API call
   - Message: "Loading..." or "Saving..."

10. **Form Reset**
    - "Clear Form" button clears all fields and resets to default state
    - Changing selected option optionally clears form (with confirmation if data entered)

### Responsive Design

11. **Desktop Layout (≥768px)**
    - Two-column layout side-by-side
    - Menu on left (30% width)
    - Form on right (70% width)
    - Full-width form fields

12. **Mobile Layout (<768px)**
    - Single column, stacked vertically
    - Menu options above form
    - Full-width form fields
    - Touch-friendly button sizes

### Accessibility & Keyboard Navigation

13. **Keyboard Support**
    - Tab through menu options and form fields in logical order
    - Enter key on selected option triggers selection
    - Enter key in last form field triggers submit
    - Escape key resets form (with confirmation)
    - Number keys (1, 2, 4) as keyboard shortcuts to select options

14. **Screen Reader Support**
    - Fieldsets group related sections (Customer ID, Personal Details, Address, Contact)
    - Labels associated with all inputs (for accessibility)
    - Radio buttons or tabs properly labeled ("Option 1: Inquiry", etc.)
    - Error messages announced to screen readers
    - Form title updates screen reader when option changes

---

## Technical Implementation Details

### Component Structure
```
CustomerMenuPage
├── MenuSection (left side)
│   ├── RadioGroup or TabPanel: Options 1, 2, 4
│   └── SelectionChangeListener → updateFormBehavior()
├── FormSection (right side)
│   ├── FormLayout or VerticalLayout
│   ├── FormTitle (updates by option)
│   ├── FormFields
│   │   ├── CustomerNumberField (enabled/disabled by option)
│   │   ├── FirstNameField (read-only/editable by option)
│   │   ├── LastNameField (read-only/editable by option)
│   │   ├── DOBField
│   │   ├── HouseNameField
│   │   ├── HouseNumberField
│   │   ├── PostcodeField
│   │   ├── HomePhoneField
│   │   ├── MobilePhoneField
│   │   └── EmailField
│   ├── ErrorMessageArea (status display)
│   └── ButtonBar
│       ├── PrimaryButton (Look Up / Create / Update - changes by option)
│       └── ClearButton
└── LoadingOverlay (shows during API calls)
```

### Vaadin Components Used
- **RadioButtonGroup** or **Tabs**: Menu option selection
- **FormLayout**: Organized form fields with labels
- **TextField**: Customer Number, Names, Address fields
- **DateField**: Date of Birth
- **Button**: Submit button (changes label by option)
- **Div**: Error message display area with styling
- **ProgressBar** or **Spinner**: Loading indicator

### Route Configuration
```java
@Route("customers/menu")
@PageTitle("Customer Menu - CICS GenApp")
public class CustomerMenuPage extends VerticalLayout {
  // Two-column layout with menu and form
}
```

### State Management
- **selectedOption**: Tracks which option (1, 2, or 4) is selected
- **isRecordLoaded**: Flag indicating if customer record was loaded (for Update)
- **currentCustomerId**: UUID of loaded customer (for Update/Inquiry)
- **formMode**: Enum (INQUIRY, ADD, UPDATE) controlling field states

### Method Structure
```java
private void initializeMenuSection()
private void initializeFormSection()
private void updateFormBehavior(int selectedOption)  // Changes field state
private void handleLookUp()     // For Inquiry & Update option 1
private void handleCreateCustomer()  // For Add
private void handleUpdateCustomer()  // For Update option 2
private void setFieldState(FormField field, FieldState state)
private void showMessage(String msg, MessageType type)
private void clearForm()
```

### Form State Enum
```java
enum FormState {
  INQUIRY,      // All fields read-only except customer number
  ADD,          // All fields editable, customer number disabled
  UPDATE        // Customer number enabled, others editable after lookup
}
```

---

## Dependencies

- Story 3.1: Vaadin project setup with Spring Boot integration (framework ready)
- Story 3.3: Dashboard page with navigation (breadcrumb and navigation bar)
- Story 2.2: Customer Create API (POST /api/v1/customers)
- Story 2.3: Customer Read API (GET /api/v1/customers/{id})
- Story 2.4: Customer Search API (GET /api/v1/customers) [optional - for dropdown lookup]
- Story 2.5: Customer Update API (PUT /api/v1/customers/{id})
- Story 3-9: Test Data Setup (test data for realistic testing)

---

## Tasks / Subtasks

- [ ] **Create CustomerMenuPage Component**
  - [ ] Create `CustomerMenuPage.java` with route `/customers/menu`
  - [ ] Set up two-column responsive layout (menu + form)
  - [ ] Configure responsive breakpoint (768px)

- [ ] **Implement Menu Selection**
  - [ ] Create RadioButtonGroup or Tabs for options 1, 2, 4
  - [ ] Wire selection change listener
  - [ ] Highlight selected option visually
  - [ ] Implement keyboard shortcuts (1, 2, 4 keys)

- [ ] **Implement Form Fields**
  - [ ] Create all form fields with proper labels and placeholders
  - [ ] Group fields into sections (Customer ID, Personal, Address, Contact)
  - [ ] Set field length limits (10, 20, 27 characters per spec)
  - [ ] Apply email validation to email field
  - [ ] Apply date format validation (yyyy-mm-dd) to DOB field

- [ ] **Implement Option 1 - Inquiry Behavior**
  - [ ] Make all fields except customer number read-only by default
  - [ ] Implement "Look Up" button that calls GET /api/v1/customers/{id}
  - [ ] Pre-fill form fields with returned data
  - [ ] Show success/error messages
  - [ ] Handle "customer not found" scenario

- [ ] **Implement Option 2 - Add Behavior**
  - [ ] Disable customer number field
  - [ ] Enable all other fields
  - [ ] Mark required fields (Name, DOB, Address, Contact)
  - [ ] Implement "Create Customer" button
  - [ ] Call POST /api/v1/customers with form data
  - [ ] Validate all required fields before submit
  - [ ] Clear form on successful creation

- [ ] **Implement Option 4 - Update Behavior**
  - [ ] Two-step workflow: Look Up → Edit → Update
  - [ ] Enable customer number field
  - [ ] After lookup, enable other fields for editing
  - [ ] Implement "Update Customer" button
  - [ ] Call PUT /api/v1/customers/{id} with modified data
  - [ ] Validate required fields before update
  - [ ] Show success/error messages

- [ ] **Implement Error Message Display**
  - [ ] Create error message area at bottom of form
  - [ ] Color-code messages (red=error, green=success, yellow=warning)
  - [ ] Display field-level validation errors
  - [ ] Display API error messages
  - [ ] Auto-clear on form reset or successful operation

- [ ] **Implement Loading States**
  - [ ] Show spinner during API calls
  - [ ] Disable submit button during loading
  - [ ] Show "Loading..." message

- [ ] **Implement Clear Form Functionality**
  - [ ] "Clear Form" button resets all fields to blank
  - [ ] Confirmation dialog if user has entered data
  - [ ] Reset selected option to default (Inquiry)

- [ ] **Implement Responsive Design**
  - [ ] Layout switches to vertical stack on mobile (<768px)
  - [ ] Menu and form full-width on mobile
  - [ ] Touch-friendly button sizes on mobile
  - [ ] Test on different screen sizes

- [ ] **Implement Accessibility**
  - [ ] Add aria-labels to all interactive elements
  - [ ] Group form fields into fieldsets (Customer ID, Personal, Address, Contact)
  - [ ] Implement keyboard shortcuts (1, 2, 4 to select options)
  - [ ] Test keyboard navigation (Tab, Enter, Escape)
  - [ ] Test with screen reader (NVDA/JAWS)

- [ ] **Testing**
  - [ ] Unit tests for CustomerMenuPage component
  - [ ] Test each option's form behavior independently
  - [ ] Integration test: Load page, select option, verify form state
  - [ ] Test Inquiry with existing customer number
  - [ ] Test Inquiry with non-existent customer number
  - [ ] Test Add with valid data (create new customer)
  - [ ] Test Add with missing required fields (validation)
  - [ ] Test Update with existing customer (lookup + modify + save)
  - [ ] Test Update with non-existent customer number
  - [ ] Test form clearing and reset
  - [ ] Test error message display and clearing
  - [ ] Test responsive layout on mobile/tablet/desktop
  - [ ] Test keyboard navigation and shortcuts
  - [ ] Test accessibility with screen reader

- [ ] **Documentation**
  - [ ] Update README with customer menu page description
  - [ ] Document option-specific behavior (Inquiry vs Add vs Update)
  - [ ] Add screenshot/demo of each option mode
  - [ ] Document keyboard shortcuts
  - [ ] Add accessibility notes

---

## Design Specification

### Layout and Spacing
- **Container max-width**: 1200px (desktop), full-width (mobile)
- **Column split**: 30% menu / 70% form (desktop)
- **Menu item spacing**: 16px padding vertical, 12px margin between options
- **Form field spacing**: 16px margin bottom between fields
- **Section spacing**: 24px margin between form sections (Customer ID, Personal, Address, Contact)
- **Button spacing**: 8px gap between Submit and Clear buttons

### Typography
- **Page title**: H1, 24px, font-weight 600
- **Section headers**: H3, 16px, font-weight 500
- **Field labels**: 14px, font-weight 500, gray (#666)
- **Placeholder text**: 14px, gray (#999)
- **Error messages**: 14px, red (#D32F2F)
- **Success messages**: 14px, green (#388E3C)

### Colors (Material Design 3 / Lumo Theme)
- **Menu selected**: Primary color (#1976D2) background, white text
- **Menu unselected**: Light gray (#F5F5F5) background
- **Form field border**: Gray (#CCCCCC), blue on focus (#1976D2)
- **Error background**: Light red (#FFEBEE)
- **Success background**: Light green (#E8F5E9)
- **Button primary**: Blue (#1976D2)
- **Button text**: White
- **Disabled field**: Light gray (#F5F5F5) background, gray text (#999)

### Form Field Styling
- **Height**: 40px standard
- **Border**: 1px solid #CCCCCC
- **Border-radius**: 4px
- **Padding**: 8px 12px
- **Font-size**: 14px
- **Focus state**: Blue border (#1976D2), blue shadow
- **Read-only state**: Gray background, disabled cursor

---

## Success Criteria

### Menu and Layout
- ✅ Menu displays three options (1, 2, 4) with clear labels and descriptions
- ✅ Only one option selectable at a time
- ✅ Selected option visually highlighted
- ✅ Form title changes dynamically based on selected option
- ✅ Layout is side-by-side on desktop, stacked on mobile

### Inquiry (Option 1)
- ✅ Form fields are read-only by default
- ✅ Customer Number field is enabled for input
- ✅ "Look Up" button executes API call with customer number
- ✅ Successful lookup pre-fills all form fields with customer data
- ✅ Failed lookup shows "Customer not found" error message
- ✅ Customer data displays correctly (name, address, contact info)

### Add (Option 2)
- ✅ Customer Number field is disabled
- ✅ All other fields are enabled and blank
- ✅ "Create Customer" button calls POST /api/v1/customers API
- ✅ Required field validation works (name, DOB, address required)
- ✅ Validation errors display per-field
- ✅ Successful creation shows success message with new customer ID
- ✅ Form clears after successful creation

### Update (Option 4)
- ✅ Two-step workflow: Look Up → Edit → Update
- ✅ Customer Number field enabled for input
- ✅ "Look Up" loads customer data and enables other fields for editing
- ✅ "Update Customer" button calls PUT /api/v1/customers/{id} API
- ✅ Validation works on update (required fields)
- ✅ Success message shows "Customer updated successfully"
- ✅ Updated data persists (or can re-lookup to verify)

### Error Handling & UX
- ✅ Loading spinner shows during API calls
- ✅ Submit buttons disabled during loading
- ✅ Error messages display in red at bottom of form
- ✅ Success messages display in green
- ✅ Form reset clears all fields and resets to default option
- ✅ Changing option clears form (with confirmation if needed)

### Responsive & Accessibility
- ✅ Layout adapts to mobile (<768px): vertical stack
- ✅ Touch-friendly button sizes on mobile
- ✅ Keyboard navigation works (Tab, Enter, Escape, 1/2/4 shortcuts)
- ✅ Screen reader announces form title, field labels, messages
- ✅ ARIA labels present on all buttons and interactive elements
- ✅ Focus indicators visible on all fields

---

## Related Stories

- 3.1: Vaadin Project Setup (framework)
- 3.3: Dashboard Page (navigation and breadcrumb)
- 3.10: Customer List & Browse Page (complementary list view)
- 3.5: Customer Detail Page (for viewing full customer profiles)
- 3.6: Customer Create/Edit Page (alternative create/edit interface)
- 2.2: Customer Create API (POST /api/v1/customers)
- 2.3: Customer Read API (GET /api/v1/customers/{id})
- 2.5: Customer Update API (PUT /api/v1/customers/{id})

---

## Reference Materials

- `/docs/3270screens/SSC1-Customer-Menu.md` - Original 3270 screen specification
- Vaadin FormLayout documentation: https://vaadin.com/components/vaadin-form-layout
- Vaadin RadioButtonGroup documentation: https://vaadin.com/components/vaadin-radio-button
- Material Design 3 guidelines: https://m3.material.io/

---

## Changelog

**Created:** 2025-11-05
**Author:** Claude Code (with user input on SSC1 behavior)
**Status:** Drafted

---
