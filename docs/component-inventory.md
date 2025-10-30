# Component Inventory

## COBOL Programs (25 total)

### Entry Point Programs

| Program | Type | Purpose | Calls |
|---------|------|---------|-------|
| `lgwebst5.cbl` | Entry Point | Web/REST interface adapter | All transaction handlers |
| `lgtestc1.cbl` | Transaction | Test Customer transaction (SSC1) | Customer presentation layer |
| `lgtestp1.cbl` | Transaction | Test Motor Policy transaction (SSP1) | Policy presentation layer |
| `lgtestp2.cbl` | Transaction | Test Endowment Policy transaction (SSP2) | Policy presentation layer |
| `lgtestp3.cbl` | Transaction | Test House Policy transaction (SSP3) | Policy presentation layer |
| `lgtestp4.cbl` | Transaction | Test Commercial Policy transaction (SSP4) | Policy presentation layer |

### Customer Management Programs (9 programs)

#### Add Customer Operations
| Program | Layer | Purpose | Called By | Calls |
|---------|-------|---------|-----------|-------|
| `lgacus01.cbl` | Presentation | Customer add - UI interaction, screen handling | SSC1 transaction | lgacol01 (via LINK) |
| `lgacol01.cbl` | Business Logic | Customer add - validation, business rules | lgacus01 (via LINK) | lgacdb01, lgacvs01 |
| `lgacdb01.cbl` | Data Layer | Customer add - Db2 INSERT operation | lgacol01 (via LINK) | Db2 SQL |
| `lgacvs01.cbl` | Data Layer | Customer add - VSAM file write | lgacol01 (via LINK) | VSAM file ops |

#### Inquire Customer Operations
| Program | Layer | Purpose | Called By | Calls |
|---------|-------|---------|-----------|-------|
| `lgicus01.cbl` | Presentation | Customer inquiry - screen display | SSC1 transaction | lgicol01 (via LINK) |
| `lgicol01.cbl` | Business Logic | Customer inquiry - lookup, validation | lgicus01 (via LINK) | lgicdb01, lgicvs01 |
| `lgicdb01.cbl` | Data Layer | Customer inquiry - Db2 SELECT | lgicol01 (via LINK) | Db2 SQL |
| `lgicvs01.cbl` | Data Layer | Customer inquiry - VSAM read | lgicol01 (via LINK) | VSAM file ops |

#### Update Customer Operations
| Program | Layer | Purpose | Called By | Calls |
|---------|-------|---------|-----------|-------|
| `lgucus01.cbl` | Presentation | Customer update - screen input | SSC1 transaction | lgucol01 (via LINK) |
| `lgucol01.cbl` | Business Logic | Customer update - validation | lgucus01 (via LINK) | lgucdb01, lgucvs01 |
| `lgucdb01.cbl` | Data Layer | Customer update - Db2 UPDATE | lgucol01 (via LINK) | Db2 SQL |
| `lgucvs01.cbl` | Data Layer | Customer update - VSAM rewrite | lgucol01 (via LINK) | VSAM file ops |

### Policy Management Programs (12 programs)

#### Add Policy Operations
| Program | Layer | Purpose | Called By | Calls |
|---------|-------|---------|-----------|-------|
| `lgapus01.cbl` | Presentation | Policy add - screen interaction | SSP1-4 transactions | lgapol01 (via LINK) |
| `lgapol01.cbl` | Business Logic | Policy add - validation, rules | lgapus01 (via LINK) | lgapdb01, lgapvs01 |
| `lgapdb01.cbl` | Data Layer | Policy add - Db2 INSERT (type-specific table) | lgapol01 (via LINK) | Db2 SQL |
| `lgapvs01.cbl` | Data Layer | Policy add - VSAM file write | lgapol01 (via LINK) | VSAM file ops |

#### Inquire Policy Operations
| Program | Layer | Purpose | Called By | Calls |
|---------|-------|---------|-----------|-------|
| `lgipus01.cbl` | Presentation | Policy inquiry - screen display | SSP1-4 transactions | lgipol01 (via LINK) |
| `lgipol01.cbl` | Business Logic | Policy inquiry - lookup | lgipus01 (via LINK) | lgipdb01, lgipvs01 |
| `lgipdb01.cbl` | Data Layer | Policy inquiry - Db2 SELECT | lgipol01 (via LINK) | Db2 SQL |
| `lgipvs01.cbl` | Data Layer | Policy inquiry - VSAM read | lgipol01 (via LINK) | VSAM file ops |

#### Update Policy Operations
| Program | Layer | Purpose | Called By | Calls |
|---------|-------|---------|-----------|-------|
| `lgupus01.cbl` | Presentation | Policy update - screen input | SSP1-4 transactions | lgupol01 (via LINK) |
| `lgupol01.cbl` | Business Logic | Policy update - validation | lgupus01 (via LINK) | lgupdb01, lgupvs01 |
| `lgupdb01.cbl` | Data Layer | Policy update - Db2 UPDATE | lgupol01 (via LINK) | Db2 SQL |
| `lgupvs01.cbl` | Data Layer | Policy update - VSAM rewrite | lgupol01 (via LINK) | VSAM file ops |

#### Display Policy Operations
| Program | Layer | Purpose | Called By | Calls |
|---------|-------|---------|-----------|-------|
| `lgdpol01.cbl` | Business Logic | Policy display - formatting | Various | lgdpdb01, lgdpvs01 |
| `lgdpdb01.cbl` | Data Layer | Policy display - Db2 retrieval | lgdpol01 (via LINK) | Db2 SQL |
| `lgdpvs01.cbl` | Data Layer | Policy display - VSAM retrieval | lgdpol01 (via LINK) | VSAM file ops |

### Utility Programs (3 programs)

| Program | Purpose | Called By | Calls |
|---------|---------|-----------|-------|
| `lgastat1.cbl` | Application status, initialization, startup | CICS initialization | CICS APIs |
| `lgsetup.cbl` | Setup and configuration procedures | Manual execution | CICS commands, Db2 |
| `lgstsq.cbl` | Temporary storage queue operations, control records | Various programs | CICS TSQ APIs |

## BMS Maps (1 file)

| Component | Purpose | Screens | Input Fields |
|-----------|---------|---------|--------------|
| `ssmap.bms` | 3270 Terminal screen definition | Menu, Customer entry, Policy entry | Customer ID, Policy details, Transaction codes |

## Copybooks - Data Structures (13 files)

### Common/Shared Structures

| Copybook | Purpose | Record Type | Key Fields |
|----------|---------|-------------|-----------|
| `lgcmarea.cpy` | Common working storage area passed between all programs | Communication area | Program control flags, return codes |
| `linkparm.txt` | Program link parameter definitions | Parameter definitions | Function codes, operation types |

### Customer Data Structures

| Copybook | Purpose | Fields | Size |
|----------|---------|--------|------|
| `soaic01.cpy` | Add customer interface structure | Customer name, address, contact | ~500 bytes |
| `soavcii.cpy` | VSAM customer input structure | Customer data for write | ~400 bytes |
| `soavcio.cpy` | VSAM customer output structure | Customer data retrieved | ~400 bytes |

### Policy Data Structures

| Copybook | Purpose | Fields | Size |
|----------|---------|--------|------|
| `lgpolicy.cpy` | Generic policy record structure | Policy ID, customer ID, type, dates | ~300 bytes |
| `polloo2.cpy` | Policy lookup structure | Policy search criteria | ~100 bytes |
| `pollook.cpy` | Policy key structure | Policy key composition (21 chars) | 21 bytes |

### Policy Type-Specific Structures

| Copybook | Policy Type | Key Fields | Premium Factors |
|----------|-------------|-----------|-----------------|
| `soaipm1.cpy` | Motor Insurance | Vehicle make, model, driver age | MPG, usage, risk |
| `soaipe1.cpy` | Endowment Insurance | Term length, benefit amount | Rate, mortality |
| `soaiph1.cpy` | House Insurance | Property value, location | Area, construction |
| `soaipb1.cpy` | Commercial Property | Building value, contents | Risk level, usage |

### VSAM File Structures

| Copybook | VSAM File | Record Format | Key | Size |
|----------|-----------|---------------|-----|------|
| `soavpii.cpy` | KSDSPOLY | Policy input record | Policy ID (21 chars) | ~500 bytes |
| `soavpio.cpy` | KSDSPOLY | Policy output record | Policy ID (21 chars) | ~500 bytes |

## Program Call Hierarchy

```
CICS Transaction
    ↓
Entry Point Programs (lgtestc1, lgtestp1-4, lgwebst5)
    ↓
Presentation Layer (lg*us01)
    ├─ Uses copybooks: soaic01, lgcmarea
    └─ EXEC CICS LINK → Business Logic
        ↓
    Business Logic Layer (lg*ol01)
        ├─ Uses: lgpolicy, lgcmarea, soaipm1, soaipe1, soaiph1, soaipb1
        ├─ Validates data
        └─ EXEC CICS LINK → Data Layer (parallel Db2 + VSAM)
            ├─ Db2 Layer (lg*db01)
            │   └─ SQL INSERT/SELECT/UPDATE
            │
            └─ VSAM Layer (lg*vs01)
                └─ VSAM file operations
                ├─ Uses: soavcii, soavcio (customers)
                └─ Uses: soavpii, soavpio (policies)
```

## Data Flow Patterns

### Add Customer Flow
```
1. Customer enters data via 3270 (lgacus01)
2. Business logic validates (lgacol01)
3. Data layer executes parallel operations:
   a. Db2 INSERT via lgacdb01
   b. VSAM WRITE via lgacvs01
4. Two-phase commit ensures consistency
5. Response returned to screen
```

### Inquire Policy Flow
```
1. User enters policy criteria via 3270 (lgipus01)
2. Business logic builds search query (lgipol01)
3. Data layer retrieves from both sources:
   a. Db2 SELECT via lgipdb01 (primary)
   b. VSAM READ via lgipvs01 (secondary)
4. Results merged and formatted
5. Display on 3270 screen
```

## Component Statistics

| Category | Count | Details |
|----------|-------|---------|
| **Entry Points** | 6 | Transactions + web interface |
| **Presentation Layer** | 9 | Customer (3) + Policy (3) + Utility (3) |
| **Business Logic** | 6 | Customer (3) + Policy (3) |
| **Data Access (Db2)** | 6 | Customer (3) + Policy (3) |
| **Data Access (VSAM)** | 6 | Customer (3) + Policy (3) |
| **Utilities** | 3 | Status, setup, TSQ operations |
| **BMS Maps** | 1 | Screen definition |
| **Copybooks** | 13 | Data structures |
| **Total Code Files** | 44 | COBOL + BMS + Configuration |

## Code Metrics

| Metric | Value |
|--------|-------|
| Total Source Lines | ~5,800 |
| Average Program Size | ~140 lines |
| Largest Program | ~250 lines |
| Smallest Program | ~80 lines |
| Copybook Lines | ~800 |
| BMS Map Lines | ~200 |

## Reusability Analysis

### Highly Reusable
- **lgcmarea.cpy** - Used by all programs
- **linkparm.txt** - Parameter definitions
- **lgstsq.cbl** - Temporary storage operations

### Moderately Reusable
- **lgpolicy.cpy** - Policy operations
- **soaipm1.cpy** - Motor policy processing
- **soaipe1.cpy** - Endowment policy processing

### Specific to Function
- **lgacus01.cbl** - Customer add (presentation only)
- **lgipdb01.cbl** - Policy inquiry (Db2 specific)
- **lgacvs01.cbl** - Customer add (VSAM specific)

---

**Generated:** October 30, 2025
**Scan Type:** Deep Scan
**Total Programs Analyzed:** 25
**Total Copybooks Analyzed:** 13
