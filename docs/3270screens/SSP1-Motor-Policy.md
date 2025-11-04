# SSP1: Motor Insurance Policy Menu

**Transaction Code:** `SSP1`
**Purpose:** Manage motor insurance policies
**Screen Size:** 24 rows × 80 columns
**Policy Type:** Motor Vehicle Insurance

## Screen Rendering

```
┌────────────────────────────────────────────────────────────────────────────┐
│SSP1        General Insurance Motor Policy Menu                              │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│                                                                            │
│        1. Policy Inquiry     Policy Number [__________]                   │
│        2. Policy Add         Cust Number [__________]                     │
│        3. Policy Delete      Issue date [__________] (yyyy-mm-dd)         │
│        4. Policy Update      Expiry date [__________] (yyyy-mm-dd)        │
│                                                                            │
│                              Car Make        [____________________]       │
│                              Car Model       [____________________]       │
│                              Car Value       [______]                     │
│                              Registration    [_______]                    │
│                              Car Colour      [________]                   │
│                              CC              [________]                   │
│                              Manufacture Date[__________] (yyyy-mm-dd)    │
│                              No. of Accidents[______]                     │
│                              Policy Premium  [______]                     │
│                                                                            │
│        Select Option [_]                                                  │
│                                                                            │
│[                                        ]                                 │
└────────────────────────────────────────────────────────────────────────────┘
```

## Field Definitions

| Field Name | Position | Length | Type | Description |
|------------|----------|--------|------|-------------|
| ENP1PNO | (4, 50) | 10 | Numeric | Policy Number (right-justified, zero-padded) |
| ENP1CNO | (5, 50) | 10 | Numeric | Customer Number (right-justified, zero-padded) |
| ENP1IDA | (6, 50) | 10 | Text | Issue Date (yyyy-mm-dd format) |
| ENP1EDA | (7, 50) | 10 | Text | Expiry Date (yyyy-mm-dd format) |
| ENP1CMK | (8, 50) | 20 | Text | Car Make (e.g., Toyota, Ford, BMW) |
| ENP1CMO | (9, 50) | 20 | Text | Car Model (e.g., Camry, Focus, 3 Series) |
| ENP1VAL | (10, 50) | 6 | Numeric | Car Value in thousands (right-justified) |
| ENP1REG | (11, 50) | 7 | Text | Vehicle Registration/License Plate |
| ENP1COL | (12, 50) | 8 | Text | Car Colour |
| ENP1CC | (13, 50) | 8 | Numeric | Engine Capacity in CC (right-justified) |
| ENP1MAN | (14, 50) | 10 | Text | Manufacture Date (yyyy-mm-dd format) |
| ENP1ACC | (15, 50) | 6 | Numeric | Number of Accidents (right-justified) |
| ENP1PRE | (16, 50) | 6 | Numeric | Policy Premium in currency units (right-justified) |
| ENP1OPT | (22, 24) | 1 | Numeric | Menu Selection (1-4) |
| ERP1FLD | (24, 8) | 40 | Text | Error Message Area (read-only) |

## Screen Layout Description

### Header Section (Row 1)
- **Transaction Code**: "SSP1" (4 characters, bright)
- **Title**: "General Insurance Motor Policy Menu" (37 characters, bright)

### Menu Options Section (Rows 4-7)
Four menu options displayed on the left side:
1. **Option 1** - Policy Inquiry
2. **Option 2** - Policy Add
3. **Option 3** - Policy Delete
4. **Option 4** - Policy Update

### Data Entry Section (Rows 4-16)
Located on the right side of the screen (starting column 30):

**Policy Identification** (Rows 4-5):
- Policy Number (10 digits)
- Customer Number (10 digits)

**Policy Dates** (Rows 6-7):
- Issue Date (yyyy-mm-dd)
- Expiry Date (yyyy-mm-dd)

**Vehicle Details** (Rows 8-12):
- Car Make (20 characters) - Manufacturer name
- Car Model (20 characters) - Model designation
- Car Value (6 digits) - Value in thousands of currency units
- Registration (7 characters) - Vehicle registration plate
- Car Colour (8 characters) - Paint color

**Technical Specifications** (Rows 13-14):
- CC (8 digits) - Engine cubic capacity
- Manufacture Date (yyyy-mm-dd) - Vehicle manufacturing date

**Claims History & Pricing** (Rows 15-16):
- No. of Accidents (6 digits) - Number of previous claims
- Policy Premium (6 digits) - Annual premium amount

### Control Section (Row 22)
- **Option Input**: Single character field for menu selection (must enter)

### Status Area (Row 24)
- **Error Message Field**: 40-character display area (read-only)

## Usage Instructions

1. **To Inquire a Policy:**
   - Enter policy number OR customer number
   - Enter 1 in the option field
   - Press Enter

2. **To Add a New Policy:**
   - Fill in all vehicle and policy details
   - Enter 2 in the option field
   - Press Enter

3. **To Delete a Policy:**
   - Enter policy number to identify the record
   - Enter 3 in the option field
   - Press Enter (will request confirmation)

4. **To Update a Policy:**
   - Enter policy number to identify the record
   - Modify desired fields
   - Enter 4 in the option field
   - Press Enter

## Field Validation Rules

- **Policy Number**: Required for Inquiry, Delete, Update operations
- **Customer Number**: Required for new policies or lookups
- **Dates**: Must be in yyyy-mm-dd format (valid calendar dates)
- **Numeric Fields**: Must contain only digits
- **Text Fields**: Can contain alphanumeric characters and spaces
- **Car Value**: Amount in thousands (e.g., 25 = £25,000)
- **Premium**: Annual insurance cost in currency units

## Notes

- Motor policies are type "M" in the database
- All accidents/claims history should be declared on initial entry
- Premium amounts are calculated based on vehicle type and claims history
- Registration plate format varies by country/jurisdiction
- Engine capacity (CC) affects insurance rating
