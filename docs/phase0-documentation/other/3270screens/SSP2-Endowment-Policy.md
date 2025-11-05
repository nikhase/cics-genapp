# SSP2: Endowment Insurance Policy Menu

**Transaction Code:** `SSP2`
**Purpose:** Manage endowment life insurance policies
**Screen Size:** 24 rows × 80 columns
**Policy Type:** Endowment Insurance

## Screen Rendering

```
┌────────────────────────────────────────────────────────────────────────────┐
│SSP2        General Insurance Endowment Policy Menu                          │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│                                                                            │
│        1. Policy Inquiry     Policy Number [__________]                   │
│        2. Policy Add         Cust Number [__________]                     │
│        3. Policy Delete      Issue date [__________] (yyyy-mm-dd)         │
│        4. Policy Update      Expiry date [__________] (yyyy-mm-dd)        │
│                                                                            │
│                              Fund Name [__________]                       │
│                              Term       [__]                              │
│                              Sum Assured [______]                         │
│                              Life Assured [_________________________]     │
│                              With Profits [_]                             │
│                              Equities     [_]                             │
│                              Managed Funds[_]                             │
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
| ENP2PNO | (4, 50) | 10 | Numeric | Policy Number (right-justified, zero-padded) |
| ENP2CNO | (5, 50) | 10 | Numeric | Customer Number (right-justified, zero-padded) |
| ENP2IDA | (6, 50) | 10 | Text | Issue Date (yyyy-mm-dd format) |
| ENP2EDA | (7, 50) | 10 | Text | Expiry Date (yyyy-mm-dd format) |
| ENP2FNM | (8, 50) | 10 | Text | Fund Name (e.g., FTSE Growth, Global Equity) |
| ENP2TER | (9, 50) | 2 | Numeric | Term in years (00-99) |
| ENP2SUM | (10, 50) | 6 | Numeric | Sum Assured in thousands (right-justified) |
| ENP2LIF | (11, 50) | 25 | Text | Life Assured - Name of insured person |
| ENP2WPR | (12, 50) | 1 | Text | With Profits Flag (Y/N or blank) |
| ENP2EQU | (13, 50) | 1 | Text | Equities Flag (Y/N or blank) |
| ENP2MAN | (14, 50) | 1 | Text | Managed Funds Flag (Y/N or blank) |
| ENP2OPT | (22, 24) | 1 | Numeric | Menu Selection (1-4) |
| ERP2FLD | (24, 8) | 40 | Text | Error Message Area (read-only) |

## Screen Layout Description

### Header Section (Row 1)
- **Transaction Code**: "SSP2" (4 characters, bright)
- **Title**: "General Insurance Endowment Policy Menu" (40 characters, bright)

### Menu Options Section (Rows 4-7)
Four menu options displayed on the left side:
1. **Option 1** - Policy Inquiry
2. **Option 2** - Policy Add
3. **Option 3** - Policy Delete
4. **Option 4** - Policy Update

### Data Entry Section (Rows 4-14)
Located on the right side of the screen (starting column 30):

**Policy Identification** (Rows 4-5):
- Policy Number (10 digits)
- Customer Number (10 digits)

**Policy Dates** (Rows 6-7):
- Issue Date (yyyy-mm-dd)
- Expiry Date (yyyy-mm-dd)

**Policy Details** (Rows 8-10):
- Fund Name (10 characters) - Name of investment fund
- Term (2 digits) - Policy term in years
- Sum Assured (6 digits) - Death benefit in thousands of currency units

**Insured Party** (Row 11):
- Life Assured (25 characters) - Full name of person whose life is insured

**Fund Options** (Rows 12-14):
- With Profits (1 character) - "Y" or "N" for with-profits participation
- Equities (1 character) - "Y" or "N" for equity fund investment
- Managed Funds (1 character) - "Y" or "N" for managed fund participation

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
   - Fill in all policy details
   - Specify investment fund options (Y/N flags)
   - Enter 2 in the option field
   - Press Enter

3. **To Delete a Policy:**
   - Enter policy number to identify the record
   - Enter 3 in the option field
   - Press Enter (will request confirmation)

4. **To Update a Policy:**
   - Enter policy number to identify the record
   - Modify desired fields (typically fund allocations)
   - Enter 4 in the option field
   - Press Enter

## Field Validation Rules

- **Policy Number**: Required for Inquiry, Delete, Update operations
- **Customer Number**: Required for new policies
- **Dates**: Must be in yyyy-mm-dd format (valid calendar dates)
- **Term**: 1-99 years (typically 5-25 years for endowment policies)
- **Sum Assured**: Positive numeric value in thousands
- **Life Assured**: Full name of insured person (25 characters max)
- **Fund Flags**: Must be "Y", "N", or blank
  - Only one fund type should be selected per policy
  - With-Profits: Guaranteed + bonuses based on fund performance
  - Equities: Direct exposure to stock markets
  - Managed: Professional fund management with balanced allocation

## Investment Fund Characteristics

### With Profits (WPR = Y)
- Guaranteed minimum return
- Discretionary bonuses based on investment performance
- Lower volatility, steady growth
- Suitable for conservative investors

### Equities (EQU = Y)
- Direct investment in stock markets
- Higher growth potential
- Higher volatility and risk
- Suitable for long-term investment (10+ years)

### Managed Funds (MAN = Y)
- Professional fund management
- Balanced portfolio approach
- Mix of assets (stocks, bonds, cash)
- Medium risk/return profile

## Notes

- Endowment policies are type "E" in the database
- Policy term should match original contract term
- Life Assured name is required for all endowment policies
- Only one fund option should typically be selected
- Sum Assured is the death benefit payable to beneficiaries
- These are long-term investment-linked life policies
- Maturity value depends on fund performance and selected options
