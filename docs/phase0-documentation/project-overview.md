# CICS GenApp - Project Overview

## Executive Summary

The CICS GenApp (General Insurance Application) is a production-grade COBOL application designed for z/OS CICS Transaction Server. It demonstrates core CICS capabilities including transaction processing, database integration, terminal interfaces, and optional advanced features like coupling facility integration and workload simulation.

**Project Type:** Monolithic COBOL/CICS Backend Application
**Language:** COBOL
**Platform:** z/OS CICS TS
**Database:** Db2 + VSAM
**Terminal Interface:** 3270
**Total Files:** 133
**Source Files:** 44 COBOL programs and copybooks

## Business Purpose

The application simulates an insurance company's core operations:
- **Customer Management:** Create, retrieve, and manage customer records
- **Policy Management:** Create and manage four policy types (Motor, Endowment, House, Commercial Property)
- **Transaction Processing:** Five main transactions handle different business functions
- **Data Persistence:** Dual storage via Db2 (primary) and VSAM (secondary)

## Key Features

### 1. Multi-Tier Architecture
- **Presentation Tier:** 3270 BMS maps for user interface
- **Business Logic Tier:** Policy and customer processing logic
- **Data Management Tier:** Database and file operations

### 2. Transaction Processing
Five core transactions:
- **SSC1:** Customer inquiry and creation
- **SSP1:** Motor insurance policy creation
- **SSP2:** Endowment insurance policy creation
- **SSP3:** House insurance policy creation
- **SSP4:** Commercial property insurance policy creation

### 3. Data Storage
- **Db2:** Primary database for customers and policies
- **VSAM:** Secondary file storage (KSDSCUST, KSDSPOLY)
- **Temporary Storage Queues:** For control and error tracking

### 4. Advanced Capabilities (Optional)
- Named counter server for unique customer ID generation
- Coupling facility integration
- Workload Simulator support
- Business event publishing

## Technology Stack

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | COBOL | - | Application source code |
| **Runtime** | CICS Transaction Server | 4.1+ | Transaction processing engine |
| **Database** | Db2 | Integrated with z/OS | Primary data storage |
| **File System** | VSAM | z/OS native | Secondary data storage |
| **UI Framework** | BMS (Basic Mapping Support) | CICS native | 3270 terminal screens |
| **Integration** | Coupling Facility (Optional) | z/OS Parallel Sysplex | Named counters, TSQ |

## Architecture Pattern

**Monolithic 3-Tier Architecture**
```
┌─────────────────────────────────────────────────┐
│  Presentation Layer (3270 Terminal Interface)   │
│  - BMS maps (ssmap.bms)                        │
│  - Transaction handling (SSC1, SSP1-4)         │
└──────────────────┬──────────────────────────────┘
                   │ EXEC CICS LINK
┌──────────────────▼──────────────────────────────┐
│  Business Logic Layer                           │
│  - Policy processing (lga/lgd/lgi/lgu*.cbl)    │
│  - Customer processing logic                    │
│  - Validation and rules                        │
└──────────────────┬──────────────────────────────┘
                   │ EXEC CICS LINK
┌──────────────────▼──────────────────────────────┐
│  Data Management Layer                          │
│  - Db2 SQL operations                          │
│  - VSAM file access                            │
│  - Two-phase commit coordination                │
└──────────────────┬──────────────────────────────┘
                   │
        ┌──────────┼──────────┐
        │          │          │
   ┌────▼──┐   ┌──▼──┐   ┌──▼──┐
   │  Db2  │   │VSAM │   │ TSQ │
   └───────┘   └─────┘   └─────┘
```

## Repository Structure

```
cics-genapp-bmad/
├── base/                    # Main application code
│   ├── src/                # COBOL source code (44 files)
│   ├── cntl/               # CICS control files & JCL
│   ├── data/               # Sample data files
│   ├── exec/               # Executable scripts
│   ├── event-bindings/     # Event configuration
│   ├── wsim/               # Workload simulator
│   ├── bin/                # Build artifacts
│   └── images/             # Documentation images
└── docs/                   # Generated documentation
```

## Development Workflow

### Setup Phase
1. Clone repository or transfer files to z/OS
2. Allocate MVS data sets for source code
3. Configure CICS region parameters
4. Set up Db2 tables and VSAM files

### Build Phase
1. Compile COBOL programs
2. Create CICS resources (programs, transactions, files)
3. Configure BMS maps for 3270 interface

### Testing Phase
1. Start CICS region
2. Execute test transactions
3. Verify data in Db2 and VSAM
4. Monitor temporary storage queues

## Deployment Considerations

### Production Configuration
- Single or multiple CICS regions
- Db2 connection pool management
- VSAM file allocation and backup
- Named counter server configuration (if using parallel sysplex)

### Monitoring
- CICS transaction statistics
- Db2 connection monitoring
- VSAM file I/O performance
- Temporary storage queue management

## Modernization Context

This application serves as a reference implementation for:
- **CICS to Cloud Migration:** Understanding mainframe application architecture
- **Microservices Transformation:** Identifying service boundaries (customers, policies)
- **API Gateway Exposure:** Exposing legacy transactions as REST services
- **Event-Driven Architecture:** Publishing business events from transactions

## Next Steps

1. **Review Architecture:** See [Architecture.md](./architecture.md) for detailed design patterns
2. **Setup Instructions:** Follow [Installation Guide](./installation-guide.md)
3. **Development:** Refer to [Development Guide](./development-guide.md)
4. **Testing:** Use procedures in [Testing Guide](./testing-guide.md)
5. **Modernization Planning:** Use this documentation as baseline for transformation planning

---

**Generated:** October 30, 2025
**Scan Type:** Deep Scan
**Source Location:** `/base`
**Documentation Location:** `/docs`
