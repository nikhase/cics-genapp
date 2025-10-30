# Architecture Documentation

## Executive Summary

The CICS GenApp implements a **Monolithic 3-Tier Architecture** optimized for z/OS CICS Transaction Server. The application demonstrates enterprise-grade transaction processing with dual-storage consistency, referential integrity, and optional scaling capabilities through parallel sysplex integration.

**Architecture Style:** Layered (3-Tier)
**Deployment Model:** Single CICS Region (scalable to multiple regions)
**Data Consistency:** Two-phase commit across Db2 and VSAM
**Scalability Pattern:** Horizontal (parallel sysplex) with optional coupling facility

## Architectural Layers

### 1. Presentation Layer (3270 Terminal Interface)

**Purpose:** User interaction and screen management

**Components:**
- **BMS Map (ssmap.bms):** Defines 3270 terminal screen layouts
- **Presentation Programs (lg*us01.cbl):** Handle screen input/output
- **Transaction Router:** Routes user actions to appropriate programs

**Key Characteristics:**
- Terminal-oriented (3270 protocol)
- Map-based screen formatting (BMS)
- Stateful user sessions maintained by CICS
- Synchronous request-response pattern

**Programs:**
- `lgacus01.cbl` - Add customer screen
- `lgapus01.cbl` - Add policy screen
- `lgicvs01.cbl` - Inquire customer screen
- `lgipus01.cbl` - Inquire policy screen
- `lgucus01.cbl` - Update customer screen
- `lgupus01.cbl` - Update policy screen
- Plus specialized policy type screens

**API Usage:**
```cobol
EXEC CICS RECEIVE MAP('ssmap')
    INTO(ws-map-area)
END-EXEC

EXEC CICS SEND MAP('ssmap')
    FROM(ws-map-area)
    DATAONLY
END-EXEC
```

### 2. Business Logic Layer

**Purpose:** Validate data, apply business rules, coordinate data operations

**Components:**
- **Policy Logic Programs (lg*ol01.cbl):** Business rules for policy types
- **Customer Logic Programs (lgacol01, lgicol01, lgucol01.cbl):** Customer operations
- **Validation Routines:** Data validation before persistence
- **Calculation Engines:** Premium calculations, coverage determinations

**Key Characteristics:**
- Stateless request processing
- Encapsulates business rules
- Coordinates between presentation and data layers
- Handles error conditions and retries

**Decision Points:**
- Policy eligibility validation
- Customer duplicate detection
- Coverage amount verification
- Premium calculation based on risk factors

**Example: Add Motor Policy Logic**
```cobol
EXEC CICS LINK
    PROGRAM('lgapdb01')
    COMMAREA(ws-policy-data)
END-EXEC

If policy Db2 insert succeeds:
    EXEC CICS LINK
        PROGRAM('lgapvs01')
        COMMAREA(ws-policy-data)
    END-EXEC
Else:
    Rollback and return error
```

### 3. Data Management Layer

**Purpose:** Persist and retrieve data from Db2 and VSAM

**Components:**

#### 3a. Db2 Data Access (lg*db01.cbl)
- **SQL Execution:** INSERT, SELECT, UPDATE operations
- **Result Set Processing:** Fetch and format rows
- **Transaction Control:** Commit/rollback coordination
- **Error Handling:** Database error handling and logging

**Db2 Connection:**
- CICS DB2CONN resource
- JDBC connection pooling
- SQL pre-compilation

**Typical Operations:**
```cobol
EXEC SQL
    INSERT INTO CUSTOMER
    (CUST_ID, CUST_NAME, CUST_ADDRESS, ...)
    VALUES (:ws-cust-id, :ws-name, :ws-address, ...)
END-EXEC

EXEC SQL
    SELECT * FROM MOTOR_POLICY
    WHERE CUST_ID = :ws-cust-id
END-EXEC
```

#### 3b. VSAM Data Access (lg*vs01.cbl)
- **Sequential File Operations:** Read, write, rewrite, delete
- **Key-Based Access:** Direct record lookup
- **Record Formatting:** Structure conversion
- **File Management:** Open/close file operations

**VSAM File Configuration:**
- KSDS (Key-Sequenced Data Set) organization
- Dynamic file allocation
- Automatic key management

**Typical Operations:**
```cobol
EXEC CICS READ FILE('KSDSCUST')
    INTO(ws-customer-record)
    RIDFLD(ws-cust-id)
    RESP(ws-response-code)
END-EXEC

EXEC CICS WRITE FILE('KSDSPOLY')
    FROM(ws-policy-record)
    RIDFLD(ws-policy-key)
END-EXEC
```

## Data Flow Architecture

### Customer Addition Flow

```
User (3270 Terminal)
    ↓
Presentation Layer (lgacus01)
├─ Receive customer data from terminal
├─ Format data structure
└─ EXEC CICS LINK to business logic
    ↓
Business Logic Layer (lgacol01)
├─ Validate customer data
├─ Check for duplicates
├─ Verify address format
├─ Prepare data for persistence
└─ Call data layer (parallel)
    ├─ EXEC CICS LINK to Db2 (lgacdb01)
    │   └─ INSERT INTO CUSTOMER
    │
    └─ EXEC CICS LINK to VSAM (lgacvs01)
        └─ WRITE FILE KSDSCUST
            ↓
CICS Two-Phase Commit
├─ Db2 transaction phase
├─ VSAM transaction phase
└─ Atomic commit or rollback
    ↓
Response
├─ Success: Return new customer ID
└─ Error: Return error code and message
    ↓
Presentation Layer
└─ Display result to user
```

### Policy Inquiry Flow

```
User enters policy criteria
    ↓
Presentation Layer (lgipus01)
└─ Capture policy search criteria
    ↓
Business Logic Layer (lgipol01)
├─ Build query parameters
├─ Validate search criteria
└─ Call data layer
    ├─ EXEC CICS LINK to Db2 (lgipdb01)
    │   ├─ SELECT from POLICY
    │   ├─ SELECT from type-specific table
    │   └─ Return multiple rows
    │
    └─ EXEC CICS LINK to VSAM (lgipvs01)
        ├─ READ file KSDSPOLY
        └─ Retrieve policy record
            ↓
Format results
└─ Merge Db2 and VSAM data
    ↓
Presentation Layer
└─ Display policy details on screen
```

## Integration Points

### Internal Integration

**1. Program Linking (EXEC CICS LINK)**
- Synchronous call to linked programs
- Shared communication area (COMMAREA)
- Return codes in COMMAREA
- Same CICS transaction context

**2. Data Sharing**
- Common working area (lgcmarea.cpy)
- Shared copybooks for data structures
- Consistent record formatting

**3. File Access**
- VSAM files accessed through CICS FILE resources
- Db2 accessed through CICS DB2CONN resource
- Automatic transaction coordination

### External Integration (Optional Features)

**1. Workload Simulator (wsim/)**
- Automated transaction generation
- Load testing capability
- Performance measurement
- Scenario-based testing

**2. Business Event Publishing**
- Event subscription framework
- Integration adapters
- Monitoring system feeds
- Dashboard updates

**3. Web Services (Future)**
- REST API gateway
- JSON message conversion
- Web service adapters
- HTTP binding

## Transaction Processing Model

### CICS Transaction Lifecycle

```
1. Terminal User Input
   ↓
2. CICS Receives Transaction
   ├─ Transaction ID (SSC1, SSP1-4, etc.)
   ├─ User data in message
   └─ Routing to appropriate program
   ↓
3. User Program Executes
   ├─ EXEC CICS SEND MAP (receive screen)
   ├─ EXEC CICS RECEIVE MAP (get input)
   ├─ EXEC CICS LINK (call business logic)
   └─ EXEC CICS SEND MAP (display output)
   ↓
4. Data Persistence
   ├─ EXEC CICS SYNCPOINT ROLLBACK (start)
   ├─ Db2 operations (INSERT/SELECT/UPDATE)
   ├─ VSAM operations (READ/WRITE)
   └─ EXEC CICS SYNCPOINT (commit)
   ↓
5. Response to Terminal
   └─ Update map with results
   ↓
6. CICS Task Termination
   └─ Transaction ends, resources released
```

### Two-Phase Commit Sequence

```
Phase 1: Prepare
├─ Db2: Prepare to commit (acquire locks)
├─ VSAM: Prepare to write (allocate space)
└─ Wait for all resources ready

Phase 2: Commit
├─ Db2: Commit (release locks, persist)
├─ VSAM: Write (flush to disk)
└─ Both complete or both rollback
```

**Failure Handling:**
```
If Db2 fails:
  ├─ VSAM rollback
  ├─ Db2 rollback
  └─ Transaction failed, return to step 2

If VSAM fails:
  ├─ Db2 rollback
  ├─ VSAM rollback
  └─ Transaction failed, return to step 2

If both succeed:
  └─ Transaction complete, user sees success
```

## Scalability Architecture

### Single Region Configuration (Current)
```
┌─────────────────────────────────────────┐
│         CICS Region (Single)            │
│                                         │
│  ┌──────────────────────────────────┐  │
│  │  Program Execution Pool          │  │
│  │  - Customer programs (1-4 tasks) │  │
│  │  - Policy programs (1-4 tasks)   │  │
│  │  - Transaction router            │  │
│  └──────────────────────────────────┘  │
│                                         │
│  ┌──────────────────────────────────┐  │
│  │  Resource Pools                  │  │
│  │  - Db2 connection pool           │  │
│  │  - VSAM file handles             │  │
│  │  - Temporary storage queues      │  │
│  └──────────────────────────────────┘  │
│                                         │
└────────────────┬────────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
    ┌───▼──┐         ┌────▼───┐
    │ Db2  │         │ VSAM   │
    └──────┘         └────────┘
```

### Multi-Region Configuration (Scalable)
```
┌─────────────────────────────────────────┐
│    CICSPlex SM (Management Server)      │
│  - Monitor resources                    │
│  - Load balance requests                │
│  - Manage coupling facility             │
└─────────────────────────────────────────┘
        │
        ├─ CICS Region 1 ── Db2 \
        │                       ├─ Shared Db2 (DDF)
        ├─ CICS Region 2 ── Db2 /
        │
        └─ Coupling Facility
           ├─ Named Counter (GENACUSTID)
           ├─ Temporary Storage (GENACNTL)
           └─ Temporary Storage (GENAERRS)
```

**Benefits of Multi-Region:**
- Horizontal scaling (more regions = more capacity)
- High availability (one region failure isolated)
- Load distribution across regions
- Shared counter across regions (no duplicate IDs)
- Shared error tracking

## Performance Architecture

### Connection Pooling
```
Db2 Connection Pool (configured in CICS DB2CONN)
├─ Min connections: 5
├─ Max connections: 20
├─ Idle timeout: 5 minutes
└─ Used by all programs needing Db2

Benefit: Reuse connections, reduce connect overhead
```

### File Caching
```
VSAM File Buffer Pool
├─ Data component: Automatic OS caching
├─ Index component: Loaded in memory
└─ Accessed via CICS FILE resources

Benefit: Fast key-based access, reduced I/O
```

### Transaction Isolation
```
CICS Default: CS (Cursor Stability)
├─ Consistent read within transaction
├─ Locks released after fetch
└─ Prevents dirty reads

Application Consistency: Two-phase commit
├─ Atomic Db2 + VSAM update
└─ No partial updates
```

## Error Handling Architecture

### Application Error Handling
```
Layer 1: Validation (Business Logic)
├─ Data format validation
├─ Business rule enforcement
└─ Return error codes

Layer 2: Database Errors
├─ SQLCODE checking
├─ RESP codes from CICS commands
└─ Automatic rollback on error

Layer 3: System Errors
├─ CICS RESP codes
├─ ABEND handling
└─ Recovery procedures

Layer 4: Monitoring
├─ Error queue (GENAERRS)
├─ Log output
└─ Alerting
```

### Error Queuing
```
When Db2 error occurs:
1. Log error to GENAERRS temporary storage queue
2. Return error code to presentation layer
3. Transaction rolled back (SYNCPOINT ROLLBACK)
4. User sees error message
5. Error remains in GENAERRS for monitoring
```

## Security Architecture

### CICS Security
- **Transaction Security:** User ID required for sign-on
- **Program Security:** Resource access control
- **File Security:** VSAM file access via CICS

### Db2 Security
- **Authentication:** CICS user ID mapped to Db2 user
- **Authorization:** Table/view access control
- **Encryption:** Network encryption (SSL/TLS optional)

### Data Protection
- **VSAM Encryption:** Optional at storage layer
- **Db2 Encryption:** Optional column encryption
- **Audit Trail:** Transaction logging

## Deployment Architecture

### Development Environment
```
z/OS Development LPAR
└─ CICS DEV Region
   ├─ Development programs
   ├─ Test data (Db2 test schema)
   └─ Development VSAM files
```

### Test Environment
```
z/OS Test LPAR
└─ CICS TEST Region
   ├─ Pre-production programs
   ├─ Test data (realistic volumes)
   └─ Production VSAM clone
```

### Production Environment
```
z/OS Production LPAR (Parallel Sysplex)
├─ CICS PROD Region 1 ─┐
├─ CICS PROD Region 2 ─┼─ Shared Db2 (DDF)
├─ CICS PROD Region 3 ─┴─ Shared VSAM
└─ CICSPlex SM monitoring
```

## Modernization Considerations

### Microservices Decomposition
**Potential Services:**
1. **Customer Service** - lgacol01, lgicol01, lgucol01
2. **Policy Service** - lgapol01, lgipol01, lgupol01
3. **Motor Insurance Service** - Motor-specific logic
4. **Endowment Insurance Service** - Endowment-specific logic
5. **House Insurance Service** - House-specific logic
6. **Commercial Insurance Service** - Commercial-specific logic

### API Gateway Exposure
```
REST API Gateway
    ↓
├─ POST /customers → Add customer
├─ GET /customers/{id} → Inquire customer
├─ PUT /customers/{id} → Update customer
├─ POST /policies → Add policy
├─ GET /policies/{id} → Inquire policy
└─ PUT /policies/{id} → Update policy
    ↓
CICS Transaction Server
├─ lgwebst5.cbl (web entry point)
├─ Business logic programs
└─ Data access layer
```

### Event-Driven Architecture
```
CICS Transactions (Event Producers)
    ├─ Customer added event
    ├─ Policy created event
    └─ Policy canceled event
        ↓
Event Stream (Kafka/MQ)
    ├─ CRM System (subscribe to customer events)
    ├─ Analytics Platform (subscribe to all events)
    ├─ Reporting System (subscribe to policy events)
    └─ Dashboard (real-time updates)
```

---

**Generated:** October 30, 2025
**Scan Type:** Deep Scan
**Analysis Method:** Code structure analysis + transaction flow documentation
