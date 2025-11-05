# Source Tree Analysis

## Directory Structure

```
base/
├── src/                         # COBOL Source Code (Primary)
│   ├── Transaction Handlers
│   │   ├── lgwebst5.cbl         # Web/REST interface entry point
│   │   ├── lgtestc1.cbl         # Test customer transaction (SSC1)
│   │   ├── lgtestp1.cbl         # Test policy transaction (SSP1 - Motor)
│   │   ├── lgtestp2.cbl         # Test policy transaction (SSP2 - Endowment)
│   │   ├── lgtestp3.cbl         # Test policy transaction (SSP3 - House)
│   │   └── lgtestp4.cbl         # Test policy transaction (SSP4 - Commercial)
│   │
│   ├── Customer Programs (lg*c*.cbl)
│   │   ├── lgacdb01.cbl         # Add customer - Db2 operations
│   │   ├── lgacus01.cbl         # Add customer - UI (presentation)
│   │   ├── lgacvs01.cbl         # Add customer - VSAM operations
│   │   ├── lgicdb01.cbl         # Inquire customer - Db2 operations
│   │   ├── lgicus01.cbl         # Inquire customer - UI (presentation)
│   │   ├── lgicvs01.cbl         # Inquire customer - VSAM operations
│   │   ├── lgucdb01.cbl         # Update customer - Db2 operations
│   │   ├── lgucus01.cbl         # Update customer - UI (presentation)
│   │   └── lgucvs01.cbl         # Update customer - VSAM operations
│   │
│   ├── Policy Programs (lg*p*.cbl)
│   │   ├── lgapdb01.cbl         # Add policy - Db2 operations
│   │   ├── lgapol01.cbl         # Add policy - Business logic
│   │   ├── lgapvs01.cbl         # Add policy - VSAM operations
│   │   ├── lgipdb01.cbl         # Inquire policy - Db2 operations
│   │   ├── lgipol01.cbl         # Inquire policy - Business logic
│   │   ├── lgipvs01.cbl         # Inquire policy - VSAM operations
│   │   ├── lgupdb01.cbl         # Update policy - Db2 operations
│   │   ├── lgupol01.cbl         # Update policy - Business logic
│   │   └── lgupvs01.cbl         # Update policy - VSAM operations
│   │
│   ├── Policy Type Specific (lg*d*.cbl)
│   │   ├── lgdpdb01.cbl         # Display policy - Db2
│   │   ├── lgdpol01.cbl         # Display policy - Business logic
│   │   └── lgdpvs01.cbl         # Display policy - VSAM
│   │
│   ├── Utility Programs
│   │   ├── lgastat1.cbl         # Application status/initialization
│   │   ├── lgsetup.cbl          # Setup and initialization
│   │   └── lgstsq.cbl           # Temporary storage queue operations
│   │
│   ├── BMS Maps
│   │   └── ssmap.bms            # Screen map for 3270 interface
│   │
│   └── Copybooks (Shared Data Structures)
│       ├── lgcmarea.cpy         # Common area definitions
│       ├── lgpolicy.cpy         # Policy data structure
│       ├── polloo2.cpy          # Policy lookup structure
│       ├── pollook.cpy          # Policy key structure
│       ├── soaic01.cpy          # Add customer interface
│       ├── soaipb1.cpy          # Policy body structure
│       ├── soaipe1.cpy          # Endowment policy structure
│       ├── soaiph1.cpy          # House policy structure
│       ├── soaipm1.cpy          # Motor policy structure
│       ├── soavcii.cpy          # VSAM customer input
│       ├── soavcio.cpy          # VSAM customer output
│       ├── soavpii.cpy          # VSAM policy input
│       ├── soavpio.cpy          # VSAM policy output
│       └── linkparm.txt         # Program link parameter definitions
│
├── cntl/                        # CICS Control & JCL Files
│   ├── Resource definitions
│   ├── Job control language
│   ├── CICS configuration
│   └── Sample test procedures
│
├── data/                        # Sample Data Files
│   ├── Customer sample data
│   ├── Policy sample data
│   └── Test data sets
│
├── exec/                        # Executable Scripts
│   ├── Build scripts
│   ├── Deploy scripts
│   └── Automation tools
│
├── wsim/                        # Workload Simulator Configuration
│   ├── Test scenarios
│   ├── Load generation profiles
│   └── Performance test cases
│
├── event-bindings/              # Event System Configuration
│   ├── Business event definitions
│   ├── Event routing rules
│   └── Integration adapters
│
├── bin/                         # Build Artifacts & Executables
│   ├── Compiled programs
│   ├── Load modules
│   └── Test binaries
│
├── images/                      # Documentation Images
│   └── initial_topology.jpg     # Architecture diagram
│
├── README.md                    # Root documentation
├── Architecture.md              # Detailed architecture
├── Building.md                  # Build procedures
├── Installation.md              # Installation guide
├── Testing.md                   # Testing procedures
└── Reference.md                 # Reference documentation
```

## Critical Directories Explained

### `/src` - Application Source Code
**Purpose:** Contains all COBOL source code, copybooks, and BMS maps

**Key Characteristics:**
- 44 files total (25 COBOL programs + 13 copybooks + 1 BMS map + linkparm.txt)
- ~5,800 lines of COBOL code
- Organized by function (customer, policy, utility)
- Programs follow naming convention: `lg[operation][type][layer].cbl`
  - Operation: `a` (add), `i` (inquire), `u` (update), `d` (display)
  - Type: `c` (customer), `p` (policy)
  - Layer: `us` (user/presentation), `ol` (business logic), `db` (database), `vs` (VSAM)

**Entry Points:**
- `lgwebst5.cbl` - Web/REST interface
- `lgtestc1.cbl` - Customer transaction entry point
- `lgtestp1.cbl` - Motor policy transaction entry point
- `lgtestp2.cbl` - Endowment policy transaction entry point
- `lgtestp3.cbl` - House policy transaction entry point
- `lgtestp4.cbl` - Commercial property policy transaction entry point

**Shared Code:**
- `/copybooks` - Data structure definitions shared across programs
- Common area (`lgcmarea.cpy`) - Cross-program communication area

### `/cntl` - Control Files & JCL
**Purpose:** CICS control resource definitions, job control language, and deployment procedures

**Contents:**
- CICS RESOURCE definitions (programs, transactions, files)
- JCL procedures for compilation and setup
- Test job procedures
- Configuration parameters

### `/data` - Sample Data
**Purpose:** Test data sets and sample records for development and testing

**VSAM Files:**
- KSDSCUST - Customer records (key: 10 chars customer ID)
- KSDSPOLY - Policy records (key: 21 chars policy identifier)

**Db2 Tables:**
- CUSTOMER - Customer records
- POLICY - All policies (generic)
- MOTOR_POLICY - Motor insurance policies
- ENDOWMENT_POLICY - Endowment policies
- HOUSE_POLICY - House insurance policies
- COMMERCIAL_POLICY - Commercial property policies

### `/exec` - Build & Deployment Scripts
**Purpose:** Automation scripts for compilation, setup, and testing

**Typical Operations:**
- Compile COBOL programs
- Create CICS resources
- Allocate data sets
- Run test procedures
- Generate load modules

### `/wsim` - Workload Simulator
**Purpose:** Performance testing and load generation configuration

**Capabilities:**
- Automated transaction execution
- Load profile definition
- Response time measurement
- Throughput testing
- Capacity planning

### `/event-bindings` - Event System
**Purpose:** Business event publishing and subscription configuration

**Features:**
- Event routing
- Integration with monitoring tools
- Business event notifications
- Dashboard updates

## Technology Stack Details

| Layer | Component | Files | Purpose |
|-------|-----------|-------|---------|
| **UI/Presentation** | BMS Maps | ssmap.bms | 3270 terminal screen definition |
| **Transaction Logic** | COBOL Programs | lg*us*.cbl (9 files) | User interface and screen handling |
| **Business Logic** | COBOL Programs | lg*ol*.cbl (9 files) | Policy/customer processing rules |
| **Data Access** | COBOL Programs | lg*db*.cbl, lg*vs*.cbl (18 files) | Db2 and VSAM operations |
| **Data Structures** | COBOL Copybooks | *.cpy (13 files) | Shared data definitions |
| **Runtime** | CICS TS | - | Transaction processing platform |
| **Database** | Db2 | - | Persistent relational data |
| **File System** | VSAM | - | Persistent sequential data |
| **Configuration** | JCL/Control | cntl/ | Resource definitions |

## Integration Points

### Intra-Application
1. **Transaction to Presentation:** SSC1/SSP1-4 → lg*us01.cbl
2. **Presentation to Business Logic:** EXEC CICS LINK → lg*ol01.cbl
3. **Business Logic to Data:** EXEC CICS LINK → lg*db01.cbl / lg*vs01.cbl
4. **Dual-Write:** Db2 + VSAM (two-phase commit)
5. **Named Counter:** Optional coupling facility integration

### External Integration (Optional Features)
- **Workload Simulator:** wsim/ → CICS transactions
- **Event Publishing:** Business events → Monitoring systems
- **Web Services:** Future REST API exposure

## File Dependencies

```
Entry Points (Transactions)
    ↓
Presentation Layer (lg*us01.cbl)
    ↓ EXEC CICS LINK
Business Logic Layer (lg*ol01.cbl)
    ├─ Uses copybooks (lgpolicy.cpy, lgcmarea.cpy)
    ├─ Validates using copybooks (soaipm1.cpy, etc.)
    └─ Calls LINK
        ↓
Data Access Layer
    ├─ lg*db01.cbl (Db2 SQL operations)
    │   └─ Uses copybooks for SQL structures
    └─ lg*vs01.cbl (VSAM operations)
        └─ Uses copybooks for record structures
```

## Development Patterns

### Naming Convention
```
lg[operation][type][layer][number].cbl

Components:
- lg = prefix (CICS GenApp)
- operation = a(add), i(inquire), u(update), d(display)
- type = c(customer), p(policy)
- layer = us(user/presentation), ol(logic), db(database), vs(VSAM)
- number = sequence/version

Examples:
- lgacus01 = Add Customer User/presentation
- lgapol01 = Add Policy business logic
- lgapdb01 = Add Policy Db2 operations
```

### Program Linking
```
EXEC CICS LINK
    PROGRAM('lgacus01')      ← Presentation
    COMMAREA(ws-area)
END-EXEC

EXEC CICS LINK
    PROGRAM('lgapol01')      ← Business logic
    COMMAREA(ws-area)
END-EXEC

EXEC CICS LINK
    PROGRAM('lgapdb01')      ← Data layer
    COMMAREA(ws-area)
END-EXEC
```

### Copybook Usage
- **lgcmarea.cpy:** Common working storage area passed between programs
- **lgpolicy.cpy:** Policy record structure
- **soaipm1.cpy:** Motor policy specific structure
- **soavcio.cpy:** VSAM customer output structure

---

**Generated:** October 30, 2025
**Scan Type:** Deep Scan
**Analysis Method:** Source tree enumeration + pattern analysis
