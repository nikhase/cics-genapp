# Development Guide

## Overview

This guide provides instructions for developing, building, testing, and deploying changes to the CICS GenApp application.

## Prerequisites

### Required Systems
- **z/OS:** CICS TS V4.1 or later
- **Compiler:** COBOL compiler (IBM Enterprise COBOL for z/OS)
- **Database:** Db2 for z/OS
- **Storage:** VSAM support (native z/OS)

### Development Tools
- **Terminal:** Mainframe terminal emulator (Rumba, PCOM, etc.)
- **Editor:** ISPF or external editor
- **FTP Client:** For file transfer (if not using USS Git)
- **Git Client:** Git for z/OS (optional, for version control)
- **VS Code:** With COBOL extension (for code analysis)

### Development Environment Setup

**1. Access CICS Region**
```
Connect to mainframe via terminal emulator
TSO/ISPF userid and password
Navigate to CICS command prompt
```

**2. Set Up Data Sets**
```
TSO Command:
  ALLOCATE DATASET('userid.GENAPP.SRC')
           NEW
           RECFM(FB)
           LRECL(80)
           BLKSIZE(0)
           PRIMARY(2)
           SECONDARY(1)
           CYLINDERS
           SPACE
```

**3. Load Source Code**
Via FTP or Git:
```bash
# Via Git (if using USS)
git clone https://github.com/cicsdev/cics-genapp.git

# Via FTP (traditional)
ftp> cd 'userid.GENAPP.SRC'
ftp> mput *.cbl
ftp> mput *.cpy
ftp> mput *.bms
```

## Project Structure Review

```
Project Root: base/
├── src/              ← COBOL source files
│   ├── *us01.cbl    ← Presentation layer (start here for UI changes)
│   ├── *ol01.cbl    ← Business logic (start here for business rule changes)
│   ├── *db01.cbl    ← Db2 operations (start here for schema changes)
│   ├── *vs01.cbl    ← VSAM operations (start here for file format changes)
│   ├── *.cpy        ← Shared data structures (update here for data changes)
│   └── ssmap.bms    ← 3270 screen definition (update here for UI screen changes)
├── cntl/            ← JCL and CICS control files
├── data/            ← Sample test data
└── exec/            ← Build and test scripts
```

## Development Workflow

### 1. Identify Change Scope

**Type of Change → Target Files:**

| Change Type | Primary Files | Secondary Files |
|-------------|---------------|-----------------|
| **Add new field to customer** | lgcmarea.cpy, soaic01.cpy | Customer programs (lg*c*.cbl) |
| **Modify 3270 screen** | ssmap.bms | Presentation programs (lg*us01.cbl) |
| **Add business rule** | lg*ol01.cbl | Data layer programs (lg*db01, lg*vs01) |
| **Add new policy type** | New lg*p*.cbl files | lgpolicy.cpy, soaiXX.cpy |
| **Modify Db2 schema** | lg*db01.cbl | Data model, copybooks |
| **Modify VSAM structure** | lg*vs01.cbl, soavXXo.cpy | Data access patterns |

### 2. Edit Source Code

**Example: Add new field to customer**

**Step 1: Update data structure (lgcmarea.cpy)**
```cobol
       01 WS-CUSTOMER-AREA.
          05 CUST-ID            PIC 9(10).
          05 CUST-NAME          PIC X(30).
          05 CUST-ADDRESS       PIC X(50).
          05 CUST-CITY          PIC X(20).
          05 CUST-STATE         PIC X(2).
          05 CUST-ZIP           PIC X(5).
       *> NEW FIELD
          05 CUST-PHONE         PIC X(10).
          05 CUST-EMAIL         PIC X(40).
```

**Step 2: Update presentation layer (lgacus01.cbl)**
```cobol
       PROCEDURE DIVISION.
           EXEC CICS RECEIVE MAP('ssmap')
               INTO(WS-MAP-AREA)
           END-EXEC.

           MOVE WS-MAP-PHONE TO CUST-PHONE.
           MOVE WS-MAP-EMAIL TO CUST-EMAIL.

           EXEC CICS LINK
               PROGRAM('lgacol01')
               COMMAREA(WS-CUSTOMER-AREA)
           END-EXEC.
```

**Step 3: Update business logic (lgacol01.cbl)**
```cobol
           IF CUST-PHONE = SPACES
              MOVE 'E001' TO RETURN-CODE
              MOVE 'Phone required' TO ERROR-MSG
              GOBACK
           END-IF.

           EXEC CICS LINK
               PROGRAM('lgacdb01')
               COMMAREA(WS-CUSTOMER-AREA)
           END-EXEC.
```

**Step 4: Update Db2 layer (lgacdb01.cbl)**
```cobol
       EXEC SQL
           INSERT INTO CUSTOMER
           (CUST_ID, CUST_NAME, CUST_ADDRESS,
            CUST_CITY, CUST_STATE, CUST_ZIP,
            CUST_PHONE, CUST_EMAIL)
           VALUES
           (:CUST-ID, :CUST-NAME, :CUST-ADDRESS,
            :CUST-CITY, :CUST-STATE, :CUST-ZIP,
            :CUST-PHONE, :CUST-EMAIL)
       END-EXEC.
```

**Step 5: Update VSAM layer (lgacvs01.cbl)**
```cobol
       EXEC CICS WRITE FILE('KSDSCUST')
           FROM(WS-VSAM-CUSTOMER-RECORD)
           RIDFLD(CUST-ID)
       END-EXEC.
```

### 3. Build and Compile

**Option A: JCL Compilation (Traditional)**

Create JCL in cntl/:
```jcl
//COMPILE JOB (ACCT,CLASS),'GENAPP BUILD'
//STEP1 EXEC COBOLC,MEMBER=lgacus01
//SYSIN DD DSN=userid.GENAPP.SRC(LGACUS01),DISP=SHR
//SYSLIN DD DSN=userid.GENAPP.LOADLIB(LGACUS01),
//          DISP=(NEW,KEEP)
```

Submit and monitor:
```
TSO: SUBMIT 'userid.GENAPP.CNTL(COMPCOB)'
TSO: SDSF → Select job → Monitor compile output
```

**Option B: Modern Build Tools (if available)**
```bash
# Using make or gradle build system
make compile TARGET=lgacus01
# or
gradle compile -Pprogram=lgacus01
```

### 4. Unit Testing

**Test Customer Addition (SSC1 transaction):**

```
CICS Terminal:
SSC1
CUST NAME: John Smith
PHONE: 5551234567
EMAIL: john@example.com
[ENTER]

Check Results:
- New CUST-ID assigned
- Record in Db2 CUSTOMER table
- Record in VSAM KSDSCUST file
- Return code = 0 (success)
```

**CICS Commands for Testing:**

```
Display program status:
  CEMT SET PROG(LGACUS01) AUT(YES)

Browse temporary storage queue:
  CEBR (CICS Browse) → Select TSQ name

Monitor Db2 table:
  SPUFI → Run SELECT from CUSTOMER

Check VSAM file:
  ISPF 3.4 → Display VSAM data set
```

### 5. Integration Testing

**Test Two-Phase Commit (Db2 + VSAM sync):**

```
1. Add customer record
   Observe Db2 insert
   Observe VSAM write
   Verify both have matching data

2. Simulate Db2 failure
   Kill Db2 connection
   Run SSC1 transaction
   Verify VSAM rollback (no record written)

3. Simulate VSAM failure
   Disable KSDSCUST file
   Run SSC1 transaction
   Verify Db2 rollback (no record in database)
```

**Verify Data Consistency:**
```sql
-- Count customers
SELECT COUNT(*) FROM CUSTOMER;  -- e.g., 1000

-- Count policies by type
SELECT POLICY_TYPE, COUNT(*)
FROM POLICY
GROUP BY POLICY_TYPE;
-- M: 500, E: 250, H: 150, C: 100
```

### 6. Performance Testing

**Load Testing with Workload Simulator (wsim):**

```
1. Configure load profile (wsim configuration)
   - Transaction mix (SSC1: 20%, SSP1-4: 80%)
   - Load: 10 TPS, 50 TPS, 100 TPS
   - Duration: 5 minutes per test level

2. Run test
   CICS command: SPMON REPORT

3. Analyze results
   - Response time: Target < 1 second
   - Throughput: Measure TPS achieved
   - CPU: Monitor CPU utilization
   - I/O: Monitor Db2 and VSAM I/O rates
```

**Monitoring Commands:**
```
CICS Transactions in Progress:
  CEMT INQUIRE ALL

Monitor Db2 Connections:
  CEMT INQUIRE DB2CONN

Monitor VSAM Files:
  CEMT INQUIRE FILE(KSDSCUST)
```

## Code Review Checklist

Before committing changes:

- [ ] **Copybooks Updated:** All data structure changes reflected
- [ ] **All Layers Modified:** Presentation, Business, Db2, VSAM
- [ ] **Error Handling:** RESP codes checked, error cases handled
- [ ] **Logging:** Important operations logged
- [ ] **Testing:** Unit and integration tests passed
- [ ] **Performance:** No new N+1 queries or loops
- [ ] **Comments:** Code changes documented
- [ ] **Security:** No SQL injection, input validation present
- [ ] **Transactions:** SYNCPOINT correct for all data changes
- [ ] **Backward Compatibility:** Existing features not broken

## Testing Procedures

### Manual Test Case: Add Customer

**Prerequisites:**
- CICS region running
- Db2 available
- VSAM files available

**Test Steps:**
```
1. Connect to CICS terminal
   Screen shows: CICS TRANSACTION SELECTION

2. Enter transaction
   SSC1
   [ENTER]

3. See customer entry screen
   Expected: BMS map displays with input fields

4. Enter customer data
   NAME: Jane Doe
   ADDRESS: 123 Main St
   CITY: Springfield
   STATE: IL
   ZIP: 62701
   PHONE: 2175551234
   EMAIL: jane@example.com
   [ENTER]

5. System returns new customer ID
   Expected: CUST-ID: 0000001001
   Message: "Customer added successfully"

6. Verify in Db2
   SELECT * FROM CUSTOMER WHERE CUST_ID = 1001;
   Result: Row found with all entered data

7. Verify in VSAM
   ISPF Browse KSDSCUST
   Seek to key M0000001001
   Result: Record found matching Db2 row
```

**Expected Result:** PASS ✓

### Automated Test Case: Add Policy for Customer

```cobol
IDENTIFICATION DIVISION.
PROGRAM-ID. TEST-ADD-POLICY.
DATA DIVISION.
WORKING-STORAGE SECTION.
   01 WS-POLICY-DATA.
      05 CUST-ID    PIC 9(10) VALUE 1001.
      05 POLICY-TYPE PIC X VALUE 'M'.
      ...
   01 WS-RESP       PIC 9(4).

PROCEDURE DIVISION.
   PERFORM TEST-ADD-MOTOR-POLICY.
   PERFORM VERIFY-POLICY-CREATED.
   PERFORM VERIFY-DUAL-STORAGE.
   STOP RUN.

TEST-ADD-MOTOR-POLICY.
   EXEC CICS LINK
       PROGRAM('LGTESTP1')
       COMMAREA(WS-POLICY-DATA)
       RESP(WS-RESP)
   END-EXEC.

   IF WS-RESP NOT = 0
       DISPLAY "ERROR: Policy add failed"
       PERFORM CLEANUP-AND-STOP
   END-IF.

VERIFY-POLICY-CREATED.
   EXEC SQL
       SELECT COUNT(*) FROM POLICY
       WHERE CUST_ID = :CUST-ID
   END-EXEC.

   EVALUATE SQLCODE
       WHEN 0
           DISPLAY "SUCCESS: Policy found in Db2"
       WHEN 100
           DISPLAY "ERROR: Policy not found in Db2"
           PERFORM CLEANUP-AND-STOP
   END-EVALUATE.

VERIFY-DUAL-STORAGE.
   EXEC CICS READ FILE('KSDSPOLY')
       INTO(WS-POLICY-REC)
       RIDFLD(WS-POLICY-KEY)
       RESP(WS-RESP)
   END-EXEC.

   EVALUATE WS-RESP
       WHEN 0
           DISPLAY "SUCCESS: Policy found in VSAM"
       WHEN 13
           DISPLAY "ERROR: Policy not found in VSAM"
           PERFORM CLEANUP-AND-STOP
   END-EVALUATE.
```

## Debugging Techniques

### Enable Debug Output
```cobol
       EXEC CICS MONITOR TRANSID
       END-EXEC.

       DISPLAY "DEBUG: Entering lgacol01"
       DISPLAY "DEBUG: CUST-ID=" CUST-ID
       DISPLAY "DEBUG: CUST-NAME=" CUST-NAME
```

### Monitor with CICS Commands
```
CEMT INQUIRE PROG(LGACUS01) ABNORMAL
CEMT INQUIRE TASK(12345)
CEMT INQUIRE FILE(KSDSCUST) OPENSTATUS
```

### Db2 Debugging
```sql
-- Enable SQL tracing
TRACE LEVEL(FULL)

-- Check locks
SELECT * FROM SYSCAT.LOCKS

-- Monitor connections
SELECT * FROM SYSIBMADM.DBCFG
```

### VSAM Debugging
```
VERIFY KSDSCUST
REPAIR KSDSCUST VERIFY
LISTCAT ENTRIES(userid.GENAPP.KSDSCUST)
```

## Version Control

**Using Git for COBOL:**

```bash
# Clone repository
git clone https://github.com/cicsdev/cics-genapp.git
cd cics-genapp

# Create feature branch
git checkout -b feature/add-phone-field

# Make changes
vi base/src/lgacus01.cbl
vi base/src/lgcmarea.cpy

# Commit
git add base/src/
git commit -m "feat: add phone field to customer

- Add CUST-PHONE field to customer structure
- Update all customer programs to handle phone
- Update Db2 and VSAM layers
- Add phone validation in business logic"

# Push and create pull request
git push origin feature/add-phone-field
```

## Deployment Process

### Pre-Deployment Checklist

- [ ] All code changes reviewed and approved
- [ ] Unit tests passed
- [ ] Integration tests passed
- [ ] Performance tests within baseline
- [ ] Documentation updated
- [ ] Migration scripts prepared (if schema changes)
- [ ] Backup of current programs and data sets
- [ ] Deployment plan documented
- [ ] Rollback plan documented
- [ ] Change management ticket created

### Deployment Steps

**1. Compile programs**
```
Submit JCL to compile modified programs
Monitor compilation for errors
```

**2. Deploy load modules**
```
Copy new load modules to production library
userid.GENAPP.LOADLIB
```

**3. Update CICS resources**
```
Define new programs/transactions if added
Update resource definitions if schema changed
```

**4. Migrate database (if schema changed)**
```sql
-- Migration script example
ALTER TABLE CUSTOMER ADD COLUMN CUST_PHONE VARCHAR(10);
ALTER TABLE CUSTOMER ADD COLUMN CUST_EMAIL VARCHAR(40);
COMMIT;
```

**5. Migrate VSAM files (if structure changed)**
```
Allocate new VSAM file with new structure
Copy data from old to new file
Update CICS FILE resource to point to new file
```

**6. Smoke test**
```
Run test transactions in production
Verify basic functionality
Check error logs for unexpected errors
```

**7. Monitor**
```
Monitor transaction response times
Monitor error rate
Monitor resource utilization
Check application logs
```

## Common Development Tasks

### Task 1: Add New Field to Customer

**Estimated Time:** 30 minutes
**Files to Change:** 4 (lgcmarea.cpy, 3 x lg*us01/ol01.cbl)
**Testing:** Unit + integration (10 minutes)

[See example in Step 2 above]

### Task 2: Create New Policy Type (e.g., Travel Insurance)

**Estimated Time:** 2 hours
**Files to Create:** 10 (lg*p*.cbl for CRUD operations)
**Files to Change:** 3 (lgpolicy.cpy, soaiXX.cpy for structure)
**Testing:** Comprehensive (30 minutes)

### Task 3: Modify 3270 Screen Layout

**Estimated Time:** 1 hour
**Files to Change:** 2 (ssmap.bms, lg*us01.cbl for handling)
**Testing:** UI only (10 minutes)

### Task 4: Add Business Validation Rule

**Estimated Time:** 30 minutes
**Files to Change:** 1-2 (lg*ol01.cbl)
**Testing:** Unit + integration (15 minutes)

---

**Generated:** October 30, 2025
**Scan Type:** Deep Scan
**Last Updated:** October 30, 2025
