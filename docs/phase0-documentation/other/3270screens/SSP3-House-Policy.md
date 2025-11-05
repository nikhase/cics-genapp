# SSP3: House Insurance Policy Menu

**Transaction Code:** `SSP3`
**Purpose:** Manage house/home insurance policies
**Screen Size:** 24 rows × 80 columns
**Policy Type:** House/Property Insurance

## Screen Rendering

```
┌────────────────────────────────────────────────────────────────────────────┐
│SSP3        General Insurance House Policy Menu                              │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│                                                                            │
│        1. Policy Inquiry     Policy Number [__________]                   │
│        2. Policy Add         Cust Number [__________]                     │
│        3. Policy Delete      Issue date [__________] (yyyy-mm-dd)         │
│        4. Policy Update      Expiry date [__________] (yyyy-mm-dd)        │
│                                                                            │
│                              Property Type   [_______________]            │
│                              Bedrooms        [___]                         │
│                              House Value     [________]                   │
│                              House Name      [____________________]       │
│                              House Number    [____]                       │
│                              Postcode        [________]                   │
│                                                                            │
│                                                                            │
│        Select Option [_]                                                  │
│                                                                            │
│[                                        ]                                 │
└────────────────────────────────────────────────────────────────────────────┘
```

## Field Definitions

| Field Name | Position | Length | Type | Description |
|------------|----------|--------|------|-------------|
| ENP3PNO | (4, 50) | 10 | Numeric | Policy Number (right-justified, zero-padded) |
| ENP3CNO | (5, 50) | 10 | Numeric | Customer Number (right-justified, zero-padded) |
| ENP3IDA | (6, 50) | 10 | Text | Issue Date (yyyy-mm-dd format) |
| ENP3EDA | (7, 50) | 10 | Text | Expiry Date (yyyy-mm-dd format) |
| ENP3TYP | (8, 50) | 15 | Text | Property Type (e.g., Detached, Semi, Terraced, Flat) |
| ENP3BED | (9, 50) | 3 | Numeric | Number of Bedrooms (right-justified) |
| ENP3VAL | (10, 50) | 8 | Numeric | House Value in thousands (right-justified) |
| ENP3HNM | (11, 50) | 20 | Text | House Name or Street Name |
| ENP3HNO | (12, 50) | 4 | Text | House Number |
| ENP3HPC | (13, 50) | 8 | Text | Postcode/ZIP Code |
| ENP3OPT | (22, 24) | 1 | Numeric | Menu Selection (1-4) |
| ERP3FLD | (24, 8) | 40 | Text | Error Message Area (read-only) |

## Screen Layout Description

### Header Section (Row 1)
- **Transaction Code**: "SSP3" (4 characters, bright)
- **Title**: "General Insurance House Policy Menu" (40 characters, bright)

### Menu Options Section (Rows 4-7)
Four menu options displayed on the left side:
1. **Option 1** - Policy Inquiry
2. **Option 2** - Policy Add
3. **Option 3** - Policy Delete
4. **Option 4** - Policy Update

### Data Entry Section (Rows 4-13)
Located on the right side of the screen (starting column 30):

**Policy Identification** (Rows 4-5):
- Policy Number (10 digits)
- Customer Number (10 digits)

**Policy Dates** (Rows 6-7):
- Issue Date (yyyy-mm-dd)
- Expiry Date (yyyy-mm-dd)

**Property Classification** (Rows 8-9):
- Property Type (15 characters) - Building classification
- Bedrooms (3 digits) - Number of bedrooms/sleeping areas

**Property Valuation** (Row 10):
- House Value (8 digits) - Current market value in thousands of currency units

**Property Address** (Rows 11-13):
- House Name (20 characters) - Property name or street name
- House Number (4 characters) - Building/house number
- Postcode (8 characters) - Postal code/ZIP code for address

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
   - Fill in all property details and valuation
   - Enter 2 in the option field
   - Press Enter

3. **To Delete a Policy:**
   - Enter policy number to identify the record
   - Enter 3 in the option field
   - Press Enter (will request confirmation)

4. **To Update a Policy:**
   - Enter policy number to identify the record
   - Modify desired fields (typically valuation or property details)
   - Enter 4 in the option field
   - Press Enter

## Field Validation Rules

- **Policy Number**: Required for Inquiry, Delete, Update operations
- **Customer Number**: Required for new policies or lookups
- **Dates**: Must be in yyyy-mm-dd format (valid calendar dates)
- **Property Type**: One of Detached, Semi-detached, Terraced, Bungalow, Flat, etc.
- **Bedrooms**: 1-9 (whole number only)
- **House Value**: Positive numeric value in thousands
- **House Name/Number**: Required for address identification
- **Postcode**: Must be valid format for jurisdiction

## Property Type Classifications

**Common Property Types:**
- **Detached** - Single, standalone house not attached to other buildings
- **Semi-detached** - House sharing one wall with adjacent property
- **Terraced** - House sharing walls with multiple adjacent properties
- **Bungalow** - Single-story residential building
- **Flat/Apartment** - Multi-unit building, individual unit
- **Maisonette** - Multi-level flat/apartment
- **Cottage** - Small rural house

## Premium Calculation Factors

House insurance premiums are typically calculated based on:
1. **Property Value** - Higher value = higher premium
2. **Property Type** - Detached typically cheaper than terraced
3. **Bedrooms** - More bedrooms may indicate larger/older property
4. **Age of Building** - Older buildings may have higher risk
5. **Security Features** - Alarms, locks, etc. reduce premium
6. **Claims History** - Previous claims increase premium

## Notes

- House policies are type "H" in the database
- Property value should reflect current market value
- Accurate house number and postcode are critical for claims processing
- House insurance covers both structure and contents (depending on policy type)
- Address must match customer records for consistency
- Used for both residential buildings and landlord/investment properties
