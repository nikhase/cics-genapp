# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CICS GenApp is a demonstration IBM CICS Transaction Server application written in Enterprise COBOL for z/OS. It models a general insurance application with customer and policy management, designed to showcase CICS modernization patterns and enterprise transaction processing.

**Technology Stack:**
- Language: Enterprise COBOL for z/OS (v6.x+)
- Platform: z/OS with CICS Transaction Server (v4.1+)
- Database: IBM Db2 for z/OS with VSAM files
- UI: 3270 Terminal Interface with BMS maps

## Build Commands

### Standard Build Process

The application uses JCL (Job Control Language) scripts in `base/cntl/` for building. Jobs must be submitted to z/OS in sequence:

```
1. @ADEF121  - Create VSAM files and populate with sample data
2. @ASMMAP   - Assemble BMS maps for 3270 terminal interface (RC=4 expected)
3. @CDEF121  - Define CICS resources to CSD (group list: GENALIST)
4. @COBOL    - Compile all COBOL programs to LOAD library
5. @DB2CRE   - Create Db2 database, tables, indexes, and populate data
6. @DB2BIND  - Bind COBOL programs to Db2 objects
7. @SAMPNCS  - (Optional) Create named counter server (long-running)
8. @SAMPTSQ  - (Optional) Create temporary storage queue server (long-running)
```

**Note:** All JCL files require customization to replace tokens like `<USERID>`, `<COBOLHLQ>`, `<CICSHLQ>`, `<DB2HLQ>`, `<DB2SSID>` before submission.

### Cleanup Command

```
@DB2DEL - Delete Db2 database to rebuild environment
```

## Testing Commands

### Manual Testing via 3270 Terminal

Access CICS terminal and run these transactions:

```
SSC1  - Customer management (add, inquire, update customer records)
SSP1  - Motor insurance policy management
SSP2  - Endowment insurance policy management
SSP3  - House insurance policy management
SSP4  - Commercial property insurance policy management
```

### Workload Testing

The `base/wsim/` directory contains 40+ workload simulator scripts for automated testing. These simulate realistic user patterns and generate performance metrics.

### CICS Debugging Commands

```
CEMT INQUIRE PROG(<progname>)      - Check program status
CEMT INQUIRE TASK(<taskid>)        - Monitor active task
CEMT INQUIRE FILE(<filename>)      - Check VSAM file status
CEMT INQUIRE DB2CONN               - Verify Db2 connection
CEBR                               - Browse temporary storage queues
```

## Architecture

### 3-Tier Layered Architecture

The application uses strict layer separation for componentization and modernization readiness:

```
┌─────────────────────────────────────────┐
│  PRESENTATION LAYER                     │
│  Programs: lg*us01.cbl, lgtest*.cbl    │
│  Entry: SSC1, SSP1-P4 transactions     │
│  UI: ssmap.bms (3270 BMS maps)         │
└────────────┬────────────────────────────┘
             │ EXEC CICS LINK
             ↓
┌─────────────────────────────────────────┐
│  BUSINESS LOGIC LAYER                   │
│  Programs: lg*ol01.cbl                  │
│  Rules: validation, business logic      │
│  Coordination: two-phase commit         │
└────────────┬────────────────────────────┘
             │ EXEC CICS LINK
             ↓
┌─────────────────────────────────────────┐
│  DATA MANAGEMENT LAYER                  │
│  Db2: lg*db01.cbl (SQL operations)     │
│  VSAM: lg*vs01.cbl (file I/O)          │
└─────────────────────────────────────────┘
```

**Key Pattern:** Programs in each layer use `EXEC CICS LINK` to call the layer below, passing data via COMMAREA (shared data structure defined in copybooks).

### Dual-Storage Consistency

The application maintains data in both Db2 (primary ACID-compliant) and VSAM (shadow/legacy) storage:

1. Insert to Db2 first
2. If successful, replicate to VSAM
3. Two-phase commit coordination via CICS
4. Errors logged to temporary storage queue `GENAERRS`

**Important:** This is an educational pattern for demonstrating transaction coordination, not a production best practice.

### Polymorphic Policy Handling

Four policy types share common structure but have type-specific attributes:
- **Motor (M)** - Programs: lg*p1*.cbl
- **Endowment (E)** - Programs: lg*p2*.cbl
- **House (H)** - Programs: lg*p3*.cbl
- **Commercial (C)** - Programs: lg*p4*.cbl

Each type has dedicated Db2 table and uses COBOL REDEFINES clauses in `lgpolicy.cpy` for polymorphic data handling.

## Code Organization

### Naming Convention: `lg[CONTEXT][OPERATION][LAYER][NUMBER]`

- **Prefix:** `lg` = GenApp
- **Context:** `a` (add), `i` (inquire), `u` (update), `d` (delete)
- **Entity:** `c` (customer), `p` (policy)
- **Layer:** `us` (presentation), `ol` (business logic), `db` (Db2), `vs` (VSAM)
- **Number:** Sequential (01, 02, etc.)

Examples:
- `LGACUS01` - Add Customer presentation layer
- `LGAPDB01` - Add Policy Db2 layer
- `LGICVS01` - Inquire Customer VSAM layer

### Key Files

**COBOL Programs (base/src/):**
- `lgtest*.cbl` - Presentation layer (5 programs)
- `lg*us01.cbl` - Business logic upper layer (9 programs)
- `lg*db01.cbl` - Db2 data access (12 programs)
- `lg*vs01.cbl` - VSAM data access (12 programs)
- `lgsetup.cbl`, `lgstsq.cbl`, `lgastat1.cbl`, `lgwebst5.cbl` - Utilities (4 programs)

**Copybooks (base/src/):**
- `lgcmarea.cpy` - Primary COMMAREA structure (32KB+, used for inter-program communication)
- `lgpolicy.cpy` - All policy type structures with REDEFINES
- `soai*.cpy` - Policy type-specific input structures
- `soav*.cpy` - Policy type-specific output structures
- `pollook.cpy`, `polloo2.cpy` - Policy lookup structures

**UI Definition:**
- `ssmap.bms` - BMS screen map for 3270 terminal interface

**JCL Scripts (base/cntl/):**
- 29 customizable build and deployment scripts

### Database Structure

**Db2 Tables:**
- `CUSTOMER` - Customer master records
- `POLICY` - Policy header records
- `MOTOR`, `ENDOWMENT`, `HOUSE`, `COMMERCIAL` - Type-specific policy details

**VSAM Files:**
- `KSDSCUST` - Customer KSDS (key = first 10 chars)
- `KSDSPOLY` - Policy KSDS (key = first 21 chars, format: `[Type][CustomerID][PolicyID]`)

## Development Workflow

### Making Changes

**1. Identify the layer(s) affected:**
- UI changes → `ssmap.bms` + `lg*us01.cbl`
- Business rule changes → `lg*ol01.cbl`
- Data model changes → `lg*db01.cbl` + `lg*vs01.cbl` + copybooks
- New policy type → Create new `lg*pX*.cbl` programs + update `lgpolicy.cpy`

**2. Update copybooks first (if data structures change):**
- `lgcmarea.cpy` - For COMMAREA changes
- `lgpolicy.cpy` - For policy structure changes
- Type-specific copybooks as needed

**3. Update all affected layers:**
- Changes typically cascade through all three layers
- Always update both Db2 and VSAM layers for data changes
- Maintain COMMAREA structure consistency across layers

**4. Compile and test:**
- Customize and submit JCL from `base/cntl/`
- Test via 3270 terminal using SSC1/SSP1-P4 transactions
- Verify data consistency between Db2 and VSAM

### Error Handling Patterns

```cobol
EXEC CICS <COMMAND>
    RESP(WS-RESP)
    RESP2(WS-RESP2)
END-EXEC.

EVALUATE WS-RESP
    WHEN 0
        * Success
    WHEN 13
        * Record not found
    WHEN OTHER
        * Log to GENAERRS queue
        PERFORM ERROR-HANDLER
END-EVALUATE.
```

### Transaction Coordination

```cobol
* Start transaction
EXEC CICS LINK PROGRAM('LGxxDB01') ... END-EXEC.
IF WS-RESP = 0
    * Db2 succeeded, now VSAM
    EXEC CICS LINK PROGRAM('LGxxVS01') ... END-EXEC
    IF WS-RESP NOT = 0
        * Log error but accept transaction
        PERFORM LOG-VSAM-ERROR
    END-IF
ELSE
    * Db2 failed, abort
    PERFORM ABORT-TRANSACTION
END-IF.
```

## Important Notes

### COBOL-Specific Considerations

- All programs use `EXEC CICS` embedded commands for CICS API
- All programs use `EXEC SQL` embedded commands for Db2 access
- Column 7-72 contain code; columns 1-6 are sequence numbers
- Variable names follow COBOL conventions (hyphens, not underscores)
- PICTURE clauses define data types: `PIC X(n)` = character, `PIC 9(n)` = numeric

### Data Sharing via COMMAREA

Programs communicate by passing COMMAREA structures (defined in copybooks) via `EXEC CICS LINK`. The COMMAREA is the primary inter-layer contract - changes to COMMAREA structure require updating all programs that use it.

### Coupling Facility (Optional)

The application can optionally use:
- **Named Counter Server** (pool: GENA) - For distributed unique ID generation
- **Temporary Storage Queues** (GENACNTL, GENAERRS) - For control data and error logging

These are optional for single-region deployment but enable horizontal scaling across multiple CICS regions.

### Modernization Context

This codebase includes the BMAD (BMad) framework in `/bmad/` for structured modernization workflows:
- `/bmad/bmm/` - Modernization workflows (PRD, architecture, sprints)
- `/bmad/bmb/` - Builder workflows (create agents, modules, workflows)
- `/bmad/cis/` - Creative innovation workflows (brainstorming, storytelling)

The `/docs/` folder contains auto-generated documentation created via BMAD's `document-project` workflow.

## References

- **Original Documentation:** `base/Architecture.md`, `base/Building.md`, `base/Installation.md`, `base/Testing.md`, `base/Reference.md`
- **Generated Documentation:** `docs/index.md` (navigation hub for all auto-generated docs)
- **BMAD Framework:** `bmad/core/workflows/`, `bmad/bmm/workflows/`, `bmad/bmb/workflows/`
