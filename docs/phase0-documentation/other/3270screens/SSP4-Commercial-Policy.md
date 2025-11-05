# SSP4: Commercial Property Insurance Policy Menu

**Transaction Code:** `SSP4`
**Purpose:** Manage commercial property insurance policies with multiple perils and premiums
**Screen Size:** 24 rows × 80 columns
**Policy Type:** Commercial Property Insurance

## Screen Rendering

```
┌────────────────────────────────────────────────────────────────────────────┐
│SSP4        General Insurance Commercial Policy Menu                         │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│                                                                            │
│        1. Policy Inquiry     Policy Number [__________]                   │
│        2. Policy Add         Cust Number [__________]                     │
│        3. Policy Delete      Start date [__________] (yyyy-mm-dd)         │
│                              Expiry date [__________] (yyyy-mm-dd)        │
│                                                                            │
│                              Address             [_________________________]│
│                              Postcode            [________]               │
│                              Latitude/Longitude  [___________] [___________]│
│                              Customer Name       [_________________________]│
│                              Property Type       [_________________________]│
│                                                                            │
│                              Fire Peril/Prem     [____] [________]        │
│                              Crime Peril/Prem    [____] [________]        │
│                              Flood Peril/Prem    [____] [________]        │
│                              Weather Peril/Prem  [____] [________]        │
│                              Status              [____]                   │
│                              Reject Reason       [_________________________]│
│                                                                            │
│        Select Option [_]                                                  │
│                                                                            │
│[                                        ]                                 │
└────────────────────────────────────────────────────────────────────────────┘
```

## Field Definitions

| Field Name | Position | Length | Type | Description |
|------------|----------|--------|------|-------------|
| ENP4PNO | (4, 50) | 10 | Numeric | Policy Number (right-justified, zero-padded) |
| ENP4CNO | (5, 50) | 10 | Numeric | Customer Number (right-justified, zero-padded) |
| ENP4IDA | (6, 50) | 10 | Text | Start Date (yyyy-mm-dd format) |
| ENP4EDA | (7, 50) | 10 | Text | Expiry Date (yyyy-mm-dd format) |
| ENP4ADD | (8, 50) | 25 | Text | Property Address |
| ENP4HPC | (9, 50) | 8 | Text | Postcode/ZIP Code |
| ENP4LAT | (10, 50) | 11 | Numeric | Latitude (GPS coordinate) |
| ENP4LON | (10, 64) | 11 | Numeric | Longitude (GPS coordinate) |
| ENP4CUS | (11, 50) | 25 | Text | Customer/Business Name |
| ENP4PTY | (12, 50) | 25 | Text | Property Type Classification |
| ENP4FPE | (13, 50) | 4 | Numeric | Fire Peril Exposure/Coverage |
| ENP4FPR | (13, 56) | 8 | Numeric | Fire Peril Premium |
| ENP4CPE | (14, 50) | 4 | Numeric | Crime Peril Exposure/Coverage |
| ENP4CPR | (14, 56) | 8 | Numeric | Crime Peril Premium |
| ENP4XPE | (15, 50) | 4 | Numeric | Flood Peril Exposure/Coverage |
| ENP4XPR | (15, 56) | 8 | Numeric | Flood Peril Premium |
| ENP4WPE | (16, 50) | 4 | Numeric | Weather Peril Exposure/Coverage |
| ENP4WPR | (16, 56) | 8 | Numeric | Weather Peril Premium |
| ENP4STA | (17, 50) | 4 | Numeric | Policy Status Code |
| ENP4REJ | (18, 50) | 25 | Text | Rejection/Decline Reason |
| ENP4OPT | (22, 24) | 1 | Numeric | Menu Selection (1-3) |
| ERP4FLD | (24, 8) | 40 | Text | Error Message Area (read-only) |

## Screen Layout Description

### Header Section (Row 1)
- **Transaction Code**: "SSP4" (4 characters, bright)
- **Title**: "General Insurance Commercial Policy Menu" (41 characters, bright)

### Menu Options Section (Rows 4-7)
Three menu options displayed on the left side:
1. **Option 1** - Policy Inquiry
2. **Option 2** - Policy Add
3. **Option 3** - Policy Delete
(Note: Update option is disabled/commented out in the BMS definition)

### Data Entry Section (Rows 4-18)
Located on the right side of the screen (starting column 30):

**Policy Identification** (Rows 4-5):
- Policy Number (10 digits)
- Customer Number (10 digits)

**Policy Dates** (Rows 6-7):
- Start Date (yyyy-mm-dd)
- Expiry Date (yyyy-mm-dd)

**Property Location** (Rows 8-10):
- Address (25 characters) - Full street address
- Postcode (8 characters) - Postal code
- Latitude (11 digits) - GPS latitude coordinate
- Longitude (11 digits) - GPS longitude coordinate

**Business Information** (Rows 11-12):
- Customer Name (25 characters) - Business/organization name
- Property Type (25 characters) - Type of commercial property

**Coverage Details - Multiple Perils** (Rows 13-16):
Each peril line contains two fields:
- **Peril Exposure** (4 digits) - Coverage code or limit code
- **Peril Premium** (8 digits) - Insurance premium for that peril

Covered Perils:
- **Fire** - Protection against fire damage
- **Crime** - Protection against theft, burglary, robbery
- **Flood** - Protection against water damage from flooding
- **Weather** - Protection against storm, wind, hail damage

**Policy Status** (Rows 17-18):
- Status (4 digits) - Policy status code (active, pending, declined, etc.)
- Reject Reason (25 characters) - Reason if policy declined or suspended

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
   - Fill in property location and customer details
   - Define coverage for each peril type
   - Enter premium amounts for each peril
   - Enter 2 in the option field
   - Press Enter

3. **To Delete a Policy:**
   - Enter policy number to identify the record
   - Enter 3 in the option field
   - Press Enter (will request confirmation)

## Field Validation Rules

- **Policy Number**: Required for Inquiry, Delete operations
- **Customer Number**: Required for new policies
- **Dates**: Must be in yyyy-mm-dd format (valid calendar dates)
- **Address**: Full street address required
- **Postcode**: Must be valid format for jurisdiction
- **Coordinates**: Latitude -90 to +90, Longitude -180 to +180
- **Peril Exposure**: Numeric code indicating coverage level
- **Peril Premium**: Positive numeric value for insurance cost
- **Status Code**: Predefined codes (e.g., 0001=Active, 0002=Pending, 0003=Declined)

## Commercial Property Types

**Common Types:**
- **Retail Store** - Commercial retail space
- **Office Building** - General office space
- **Warehouse** - Storage facility
- **Manufacturing Plant** - Industrial facility
- **Restaurant/Hotel** - Food service/hospitality
- **Medical Facility** - Healthcare provider
- **School/University** - Educational institution
- **Multi-Use** - Mixed commercial/residential

## Peril Coverage Explanation

### Fire (FPE/FPR)
- Covers damage from fire and smoke
- Includes emergency services and firefighting expenses
- Most common peril in commercial policies
- Premium based on property type and fire protection systems

### Crime (CPE/CPR)
- Covers theft, burglary, robbery, and employee dishonesty
- Includes cash in safes and on premises
- Premium based on security systems and claims history
- May require security audit for large values

### Flood (XPE/XPR)
- Covers damage from water/flooding
- Often requires separate endorsement
- Premium varies by flood zone risk assessment
- Not included in standard property policies

### Weather (WPE/WPR)
- Covers damage from storms, wind, hail, and weather events
- Includes loss of business income during repairs
- Premium based on geographic location
- Higher in hurricane/tornado-prone areas

## Premium Calculation Factors

Commercial property insurance premiums consider:
1. **Property Type** - Risk category of business
2. **Property Value** - Replacement cost of building and contents
3. **Location** - Geographic risk factors (flood zone, crime area)
4. **Construction Type** - Building materials and age
5. **Occupancy** - Type of business operations
6. **Security Features** - Fire systems, alarms, surveillance
7. **Claims History** - Previous losses and claims
8. **Deductibles** - Chosen deductible amounts per peril
9. **Coverage Limits** - Maximum coverage per peril

## Notes

- Commercial policies are type "C" in the database
- GPS coordinates enable automated risk assessment
- Each peril can have independent coverage limits and premiums
- Perils are typically bundled but can be customized
- Status codes track policy lifecycle (active, pending underwriting, declined)
- Rejection reason important for audit trail and customer communication
- Update operation is not available in current implementation (can be added as future enhancement)
