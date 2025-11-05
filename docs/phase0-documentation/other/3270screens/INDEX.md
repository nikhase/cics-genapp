# 3270 Terminal Screen Index

Complete visual documentation of all CICS GenApp 3270 terminal screens from the ssmap.bms BMS map definition.

## Quick Reference

| Code | Name | Purpose | Menu Options | Key Fields |
|------|------|---------|--------------|-----------|
| **SSC1** | Customer Management | Add, Inquiry, Update customers | 3 options (1,2,4) | Customer Number, Name, Contact Info |
| **SSP1** | Motor Insurance | Motor vehicle insurance policies | 4 options (1-4) | Policy #, Customer #, Vehicle Details |
| **SSP2** | Endowment Insurance | Life insurance policies | 4 options (1-4) | Policy #, Customer #, Fund Options |
| **SSP3** | House Insurance | Home/property insurance | 4 options (1-4) | Policy #, Property Type, Address |
| **SSP4** | Commercial Insurance | Commercial property insurance | 3 options (1-3) | Policy #, Address, Multi-Peril Coverage |
| **SSP5** | Claims Management | Register and track claims | 2 options (1-2) | Claim #, Policy #, Amount, Cause |

## Screen Navigation Map

```
┌─────────────────────────────────────────────────────────────────┐
│                     CICS GenApp Main Menu                       │
│                    (Accessible via Terminal)                     │
└──────────┬──────────────────────────────────────────────────────┘
           │
    ┌──────┴──────────────────────────────────────────────────┐
    │                                                          │
    ▼                                                          ▼
┌─────────────────────────────────────┐  ┌─────────────────────┐
│    SSC1: Customer Management        │  │  SSP1-P5: Policies  │
│  ┌─ 1. Inquiry (lookup)             │  │  ┌─ SSP1: Motor     │
│  ├─ 2. Add (new record)             │  │  ├─ SSP2: Endowment │
│  └─ 4. Update (existing record)     │  │  ├─ SSP3: House     │
└─────────────────────────────────────┘  │  ├─ SSP4: Commercial│
         │                                │  └─ SSP5: Claims    │
         │                                └─────────────────────┘
         │                                      │
         ▼                                      ▼
    [Customer Database]               [Policy Database]
    - Customer Master                 - Policy Headers
    - Contact Information             - Type-Specific Data
    - Address Data                    - Claims History
```

## Detailed Screen Guides

### 1. [SSC1: Customer Management Menu](SSC1-Customer-Menu.md)

**Transaction:** `SSC1`

Manage customer records for the insurance system.

**Menu Options:**
- **1** - Inquiry: Search and display existing customer details
- **2** - Add: Create new customer record
- **4** - Update: Modify existing customer information

**Key Fields:**
- Customer Number (10 digits) - Primary key
- First Name / Last Name (customer name)
- Date of Birth (yyyy-mm-dd)
- House Name / Number / Postcode (address)
- Home Phone / Mobile / Email (contact information)

**Data Flow:**
- Input → SSC1 screen
- Business logic → LGCUS layer (linkage)
- Database → LGCUSDB01 (Db2) + LGCUSVS01 (VSAM)

---

### 2. [SSP1: Motor Insurance Policy Menu](SSP1-Motor-Policy.md)

**Transaction:** `SSP1`

Manage motor vehicle insurance policies.

**Menu Options:**
- **1** - Inquiry: Display existing motor policy
- **2** - Add: Create new motor policy
- **3** - Delete: Remove motor policy (with confirmation)
- **4** - Update: Modify motor policy details

**Key Fields:**
- Policy Number (10 digits) - Primary key
- Customer Number (10 digits) - Foreign key to customer
- Issue Date / Expiry Date (yyyy-mm-dd)
- Car Make / Model / Colour (vehicle details)
- Registration / CC (vehicle identification)
- Car Value / Premium (financial details)
- Accidents (claims history)

**Policy Type:** M (Motor)

**Data Flow:**
- Input → SSP1 screen
- Business logic → LGAP1 layer (linkage)
- Database → LGAP1DB01 (Db2) + LGAP1VS01 (VSAM)

---

### 3. [SSP2: Endowment Insurance Policy Menu](SSP2-Endowment-Policy.md)

**Transaction:** `SSP2`

Manage endowment life insurance policies with investment-linked benefits.

**Menu Options:**
- **1** - Inquiry: Display existing endowment policy
- **2** - Add: Create new endowment policy
- **3** - Delete: Remove endowment policy
- **4** - Update: Modify endowment policy (typically fund allocations)

**Key Fields:**
- Policy Number (10 digits)
- Customer Number (10 digits)
- Life Assured (25 characters) - Person whose life is insured
- Issue Date / Expiry Date (yyyy-mm-dd)
- Term (2 digits) - Years (typically 5-25)
- Sum Assured (6 digits) - Death benefit value
- Fund Name (10 characters) - Investment fund selection
- Investment Options: With Profits (Y/N), Equities (Y/N), Managed Funds (Y/N)

**Policy Type:** E (Endowment)

**Data Flow:**
- Input → SSP2 screen
- Business logic → LGAP2 layer
- Database → LGAP2DB01 (Db2) + LGAP2VS01 (VSAM)

---

### 4. [SSP3: House Insurance Policy Menu](SSP3-House-Policy.md)

**Transaction:** `SSP3`

Manage home and residential property insurance policies.

**Menu Options:**
- **1** - Inquiry: Display existing house policy
- **2** - Add: Create new house policy
- **3** - Delete: Remove house policy
- **4** - Update: Modify house policy details

**Key Fields:**
- Policy Number (10 digits)
- Customer Number (10 digits)
- Issue Date / Expiry Date (yyyy-mm-dd)
- Property Type (15 characters) - Detached, Semi, Terraced, Flat, etc.
- Bedrooms (3 digits)
- House Value (8 digits) - Property value in thousands
- House Name / Number (address identification)
- Postcode (8 characters)

**Policy Type:** H (House)

**Data Flow:**
- Input → SSP3 screen
- Business logic → LGAP3 layer
- Database → LGAP3DB01 (Db2) + LGAP3VS01 (VSAM)

---

### 5. [SSP4: Commercial Property Insurance Menu](SSP4-Commercial-Policy.md)

**Transaction:** `SSP4`

Manage commercial property insurance with multiple perils and custom premiums.

**Menu Options:**
- **1** - Inquiry: Display existing commercial policy
- **2** - Add: Create new commercial policy
- **3** - Delete: Remove commercial policy
*(Update operation not implemented)*

**Key Fields:**
- Policy Number (10 digits)
- Customer Number (10 digits)
- Start Date / Expiry Date (yyyy-mm-dd)
- Address (25 characters) - Full property address
- Postcode (8 characters)
- Latitude / Longitude (GPS coordinates for risk assessment)
- Customer Name (25 characters) - Business name
- Property Type (25 characters) - Commercial classification

**Multi-Peril Coverage** (each with exposure code and premium):
- Fire Peril (FPE/FPR)
- Crime Peril (CPE/CPR)
- Flood Peril (XPE/XPR)
- Weather Peril (WPE/WPR)

**Status Management:**
- Status Code (4 digits) - Active/Pending/Declined
- Reject Reason (25 characters) - For declined policies

**Policy Type:** C (Commercial)

**Data Flow:**
- Input → SSP4 screen
- Business logic → LGAP4 layer
- Database → LGAP4DB01 (Db2) + LGAP4VS01 (VSAM)

---

### 6. [SSP5: Policy Claims Menu](SSP5-Policy-Claims.md)

**Transaction:** `SSP5`

Manage insurance claims against customer policies.

**Menu Options:**
- **1** - Inquiry: Look up existing claim details
- **2** - Add: Register new claim against policy
*(Delete and Update not implemented)*

**Key Fields:**
- Claim Number (10 digits) - Auto-generated by system
- Policy Number (10 digits) - Which policy claim relates to
- Customer Number (10 digits) - Claim customer reference
- Claim Date (yyyy-mm-dd) - When loss occurred
- Claim Value (10 digits) - Total amount claimed
- Paid Amount (10 digits) - Amount paid to date
- Cause (25 characters) - Reason for claim (accident, theft, fire, etc.)
- Observation (25 characters) - Additional notes/status

**Data Flow:**
- Input → SSP5 screen
- Business logic → LGAP5 layer
- Database → LGAP5DB01 (Db2) + LGAP5VS01 (VSAM)

---

## Field Format Reference

### Date Fields
Format: `yyyy-mm-dd`
- Example: `2024-12-25` (December 25, 2024)
- Validates: Date must be valid calendar date
- Used in: All transaction dates, issue/expiry dates, claim dates

### Numeric Fields - Right-Justified with Zero Padding
Examples:
- `0000000001` - Customer Number 1
- `0000000042` - Policy Number 42
- `0000000100` - Amount 100 (in currency units/thousands)

### Text Fields - Left-Justified with Space Padding
Examples:
- `John                ` - First name padded to field length
- `London              ` - City name padded
- `john@example.com    ` - Email padded to field length

### Numeric Entry Fields - Automatic Justification
The 3270 terminal with FSET attribute automatically:
- Right-justifies numeric entries
- Left-justifies text entries
- Pads with spaces or zeros as needed

## BMS Map Reference

**Source File:** `/base/src/ssmap.bms`

**Map Definitions:**
- `SSMAPC1` - Customer menu (SSC1)
- `SSMAPP1` - Motor policy menu (SSP1)
- `SSMAPP2` - Endowment policy menu (SSP2)
- `SSMAPP3` - House policy menu (SSP3)
- `SSMAPP4` - Commercial policy menu (SSP4)
- `SSMAPP5` - Claims menu (SSP5)

**Terminal Specification:**
- Mode: INOUT (can send and receive data)
- Language: COBOL
- Storage: AUTO (automatic buffer allocation)
- Control: FREEKB (free keyboard - cursor anywhere)
- Attributes: MAPONLY with extended attributes

## Common Operations Across Screens

### To Perform an Inquiry (Option 1)
1. Enter identifying information (key field):
   - Customer Number for SSC1
   - Policy Number or Customer Number for SSP1-P4
   - Claim Number or Policy Number for SSP5
2. Enter `1` in the Select Option field
3. Press Enter
4. System retrieves and displays record details

### To Add a New Record (Option 2)
1. Fill in all required fields with new data
2. Enter `2` in the Select Option field
3. Press Enter
4. System validates data
5. System generates new record with unique number
6. System confirms successful creation

### To Delete a Record (Option 3)
1. Enter identifying information to select record
2. Enter `3` in the Select Option field
3. Press Enter
4. System requests confirmation
5. Confirm deletion
6. System marks record as deleted or removes from database

### To Update a Record (Option 4)
1. Enter identifying information to select record
2. Modify desired fields
3. Enter `4` in the Select Option field
4. Press Enter
5. System validates changes
6. System updates database

## Error Handling

All screens include an error message field at row 24 that displays:
- Validation errors (invalid formats, missing required fields)
- Business rule violations (policy not found, invalid customer)
- Processing errors (database connectivity, duplicate records)

When errors occur:
- Error message is displayed at bottom of screen
- Problematic field is highlighted (typically)
- User corrects data and resubmits
- No data is saved until all validations pass

## Related Documentation

- **[COBOL Programs Reference](../api/programs.md)** - Program layer details
- **[Data Structures](../api/data-structures.md)** - COMMAREA and copybook definitions
- **[Database Schema](../api/database.md)** - Db2 tables and VSAM files
- **[Architecture Overview](../architecture.md)** - System design and layers
