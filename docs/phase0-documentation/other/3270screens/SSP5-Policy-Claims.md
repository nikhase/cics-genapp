# SSP5: Policy Claims Menu

**Transaction Code:** `SSP5`
**Purpose:** Manage insurance claims against customer policies
**Screen Size:** 24 rows × 80 columns
**Entity Type:** Claims Management

## Screen Rendering

```
┌────────────────────────────────────────────────────────────────────────────┐
│SSP5        General Insurance Policy Claim Menu                              │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│                                                                            │
│        1. Claim Inquiry      Claim Number [__________]                    │
│        2. Claim Add          Policy Number [__________]                   │
│                              Customer Number [__________]                 │
│                              Claim date [__________] (yyyy-mm-dd)         │
│                                                                            │
│                              Paid        [__________]                     │
│                              Value       [__________]                     │
│                              Cause       [_________________________]       │
│                              Observation [_________________________]       │
│                                                                            │
│                                                                            │
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
| ENP5LNO | (4, 50) | 10 | Numeric | Claim Number (right-justified, zero-padded) |
| ENP5PNO | (5, 50) | 10 | Numeric | Policy Number (right-justified, zero-padded) |
| ENP5CNO | (6, 50) | 10 | Numeric | Customer Number (right-justified, zero-padded) |
| ENP5CDA | (7, 50) | 10 | Text | Claim Date (yyyy-mm-dd format) |
| ENP5PAD | (8, 50) | 10 | Numeric | Amount Paid (right-justified, zero-padded) |
| ENP5VAL | (9, 50) | 10 | Numeric | Claim Value (right-justified, zero-padded) |
| ENP5CAU | (10, 50) | 25 | Text | Cause of Loss (reason for claim) |
| ENP5OBS | (11, 50) | 25 | Text | Observation/Notes (additional details) |
| ENP5OPT | (22, 24) | 1 | Numeric | Menu Selection (1-2) |
| ERP5FLD | (24, 8) | 40 | Text | Error Message Area (read-only) |

## Screen Layout Description

### Header Section (Row 1)
- **Transaction Code**: "SSP5" (4 characters, bright)
- **Title**: "General Insurance Policy Claim Menu" (40 characters, bright)

### Menu Options Section (Rows 4-7)
Two menu options displayed on the left side:
1. **Option 1** - Claim Inquiry (look up existing claim details)
2. **Option 2** - Claim Add (register new claim against policy)

### Data Entry Section (Rows 4-11)
Located on the right side of the screen (starting column 30):

**Claim Identification** (Row 4):
- Claim Number (10 digits) - Unique identifier for the claim

**Claim Reference** (Rows 5-6):
- Policy Number (10 digits) - Which policy the claim is against
- Customer Number (10 digits) - Customer reference

**Claim Timeline** (Row 7):
- Claim Date (yyyy-mm-dd) - Date loss occurred or claim filed

**Claim Amount Details** (Rows 8-9):
- Paid (10 digits) - Amount already paid out by insurer
- Value (10 digits) - Total claimed value of loss

**Claim Description** (Rows 10-11):
- Cause (25 characters) - Primary cause of loss
  - Examples: "Fire damage", "Theft", "Water damage", "Traffic accident"
- Observation (25 characters) - Additional notes or observations
  - Examples: "Additional survey required", "Awaiting police report"

### Control Section (Row 22)
- **Option Input**: Single character field for menu selection (must enter)

### Status Area (Row 24)
- **Error Message Field**: 40-character display area (read-only)

## Usage Instructions

1. **To Inquire a Claim:**
   - Enter claim number to retrieve existing claim details
   - OR enter policy number and/or customer number
   - Enter 1 in the option field
   - Press Enter
   - System will retrieve and display claim information

2. **To Add a New Claim:**
   - Enter policy number (identifies the policy being claimed against)
   - Enter customer number
   - Enter claim date (when loss occurred)
   - Enter claimed value (total amount of loss)
   - Describe cause of loss
   - Add any observations/notes
   - Enter 2 in the option field
   - Press Enter
   - System generates claim number and records claim

## Field Validation Rules

- **Claim Number**: Auto-generated for new claims; required for inquiry
- **Policy Number**: Required to link claim to policy
- **Customer Number**: Required for claim identification
- **Claim Date**: Must be in yyyy-mm-dd format (valid calendar date)
  - Should be less than or equal to today's date
  - Should be within policy period (between Issue Date and Expiry Date)
- **Paid Amount**: Numeric value, 0-9999999999
  - Should not exceed Claim Value
  - Typically 0 for new claims (updated when payment processed)
- **Claim Value**: Positive numeric value
  - Must be reasonable relative to policy limits
  - Must exceed deductible/excess amount
- **Cause**: Brief description of loss reason (25 characters max)
- **Observation**: Additional notes or status (25 characters max)

## Common Claim Causes by Policy Type

### Motor Policy Claims
- Traffic accident
- Theft/vandalism
- Fire damage
- Water damage
- Breakdown repair
- Third-party liability claim

### Endowment Policy Claims
- Maturity claim (end of term)
- Death claim
- Policy surrender
- Withdrawal request

### House Policy Claims
- Fire damage
- Theft/burglary
- Water damage (flood, burst pipe)
- Subsidence
- Weather damage (storm, wind)
- Vandalism

### Commercial Property Claims
- Fire damage
- Theft/robbery
- Flood damage
- Storm/weather damage
- Business interruption
- Casualty (injury on premises)

## Claim Processing Workflow

1. **Claim Registration**
   - Claimant files claim (SSP5 Option 2)
   - Claim number generated
   - Paid amount initially 0

2. **Initial Assessment**
   - Policy coverage verified
   - Claim validity assessed
   - Claim amount estimated

3. **Investigation** (if required)
   - Survey/assessment conducted
   - Evidence gathered
   - Observations documented

4. **Settlement**
   - Amount approved for payment
   - Paid amount updated in system
   - Payment processed to claimant

5. **Closure**
   - Claim marked as settled
   - Documentation archived
   - Claims history updated

## Notes

- Claims are linked to specific policies
- Claim number is unique identifier for each claim
- Paid amount tracks cumulative payments for the claim
- Value represents total claimed amount (may exceed payment due to deductibles)
- Cause field is mandatory for claim processing
- Observation field used for internal notes and status tracking
- All claims require valid policy and customer records
- Claims affect future policy renewal and premium rates
- Only 2 operations available: Inquiry and Add (Delete/Update not implemented)
