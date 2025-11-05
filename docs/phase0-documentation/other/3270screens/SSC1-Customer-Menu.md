# SSC1: Customer Management Menu

**Transaction Code:** `SSC1`
**Purpose:** Manage customer records (inquiry, add, update)
**Screen Size:** 24 rows × 80 columns

## Screen Rendering

```
┌────────────────────────────────────────────────────────────────────────────┐
│SSC1        General Insurance Customer Menu                                 │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│                                                                            │
│        1. Cust Inquiry       Cust Number [__________]                     │
│        2. Cust Add           Cust Name :First[__________]                 │
│                                     :Last[____________________]           │
│        4. Cust Update                                                      │
│                              DOB         [__________] (yyyy-mm-dd)         │
│                              House Name  [____________________]           │
│                              House Number[____]                           │
│                              Postcode    [________]                       │
│                              Phone: Home [____________________]           │
│                              Phone: Mob  [____________________]           │
│                              Email  Addr [___________________________]    │
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
| ENT1CNO | (4, 50) | 10 | Numeric | Customer Number (right-justified, zero-padded) |
| ENT1FNA | (5, 50) | 10 | Text | First Name |
| ENT1LNA | (6, 50) | 20 | Text | Last Name |
| ENT1DOB | (7, 50) | 10 | Text | Date of Birth (yyyy-mm-dd format) |
| ENT1HNM | (8, 50) | 20 | Text | House Name |
| ENT1HNO | (9, 50) | 4 | Numeric | House Number |
| ENT1HPC | (10, 50) | 8 | Text | Postcode |
| ENT1HP1 | (11, 50) | 20 | Text | Home Phone Number |
| ENT1HP2 | (12, 50) | 20 | Text | Mobile Phone Number |
| ENT1HMO | (13, 50) | 27 | Text | Email Address |
| ENT1OPT | (22, 24) | 1 | Numeric | Menu Selection (1, 2, or 4) |
| ERRFLD | (24, 8) | 40 | Text | Error Message Area (read-only) |

## Screen Layout Description

### Header Section (Row 1)
- **Transaction Code**: "SSC1" (4 characters, bright)
- **Title**: "General Insurance Customer Menu" (31 characters, bright)

### Menu Options Section (Rows 4-7)
Four menu options displayed on the left side:
1. **Option 1** - Customer Inquiry (row 4)
2. **Option 2** - Customer Add (row 5)
3. **Option 3** - (blank/disabled) (row 6)
4. **Option 4** - Customer Update (row 7)

### Data Entry Section (Rows 4-13)
Located on the right side of the screen (starting column 30):

**Customer Identification** (Rows 4-6):
- Cust Number (10 digits, right-justified)
- First Name (10 characters)
- Last Name (20 characters)

**Personal Details** (Rows 7-8):
- DOB - Date of Birth in yyyy-mm-dd format
- House Name (20 characters)

**Address Information** (Rows 9-10):
- House Number (4 digits)
- Postcode (8 characters)

**Contact Information** (Rows 11-13):
- Home Phone (20 characters)
- Mobile Phone (20 characters)
- Email Address (27 characters)

### Control Section (Row 22)
- **Option Prompt**: "Select Option" label
- **Option Input**: Single character field for menu selection (must enter)

### Status Area (Row 24)
- **Error Message Field**: 40-character display area for error messages (read-only)

## Usage Instructions

1. **To Inquire a Customer:**
   - Enter customer number in ENT1CNO
   - Enter 1 in the option field
   - Press Enter

2. **To Add a New Customer:**
   - Fill in customer details in the data entry fields
   - Enter 2 in the option field
   - Press Enter

3. **To Update a Customer:**
   - Enter customer number to identify the record
   - Modify desired fields
   - Enter 4 in the option field
   - Press Enter

## Notes

- All numeric fields are right-justified with zero padding
- Text fields are left-justified with space padding
- The error message area at the bottom displays validation or processing errors
- Field validation occurs after form submission
- Customer Number is a key field (required for inquiry and update operations)
