# CICS GenApp - Complete Documentation Index

## 📋 Quick Navigation

Welcome to the CICS GenApp comprehensive documentation. This index provides your primary entry point for understanding, developing, and modernizing this legacy insurance application.

**Generated:** October 30, 2025
**Scan Type:** Deep Scan
**Documentation Level:** Comprehensive
**Last Updated:** October 30, 2025

---

## 🎯 Project At a Glance

| Property | Value |
|----------|-------|
| **Project Name** | CICS GenApp (General Insurance Application) |
| **Type** | Monolithic COBOL/CICS Backend Application |
| **Language** | COBOL (Enterprise COBOL for z/OS) |
| **Platform** | z/OS CICS Transaction Server (v4.1+) |
| **Database** | Db2 for z/OS + VSAM |
| **UI** | 3270 Terminal Interface (BMS maps) |
| **Total Files** | 133 |
| **Source Code** | 44 COBOL programs/copybooks (~5,800 LOC) |
| **Repository Type** | Monolith (single cohesive application) |

---

## 📚 Core Documentation

### 1. **[Project Overview](./project-overview.md)** 🚀
**Start here for a high-level understanding**

- Executive summary and business purpose
- Key features and capabilities
- Technology stack summary
- Repository structure at a glance
- Modernization context
- Development workflow overview

**Use When:** You're new to the project, need executive summary, or orienting stakeholders

**Read Time:** 10 minutes

---

### 2. **[Architecture Documentation](./architecture.md)** 🏗️
**Detailed architectural patterns and design**

- Architectural layers (Presentation, Business Logic, Data Management)
- Data flow patterns (customer addition, policy inquiry)
- Integration points (internal and external)
- Transaction processing model
- CICS transaction lifecycle
- Two-phase commit sequence
- Scalability architecture (single vs. multi-region)
- Performance architecture
- Error handling
- Security considerations
- Deployment architecture
- Modernization pathways (microservices, API gateway, events)

**Use When:** Designing new features, planning modernization, understanding system behavior, or conducting architecture reviews

**Read Time:** 30 minutes

---

### 3. **[Source Tree Analysis](./source-tree-analysis.md)** 🌳
**Complete directory and file structure with explanations**

- Detailed directory hierarchy with annotations
- Critical directories explained
- Technology stack details by layer
- Integration points
- File dependencies
- Development patterns and naming conventions
- Program linking approach
- Copybook usage patterns

**Use When:** Navigating the codebase, finding where to make changes, understanding file organization

**Read Time:** 20 minutes

---

### 4. **[Component Inventory](./component-inventory.md)** 📦
**Complete catalog of all programs, copybooks, and BMS maps**

- 25 COBOL programs with detailed descriptions
- Entry point programs
- Customer management programs (9 programs)
- Policy management programs (12 programs)
- Utility programs (3 programs)
- 1 BMS map definition
- 13 copybook data structures
- Program call hierarchy
- Data flow patterns
- Component statistics
- Reusability analysis

**Use When:** Locating specific programs, understanding dependencies, planning refactoring, or creating documentation

**Read Time:** 25 minutes

---

### 5. **[Data Models & Storage Schema](./data-models.md)** 💾
**Database schema, VSAM files, and data structures**

- Db2 database schema (7 tables with relationships)
- Customer table definition
- Generic policy table
- Policy type-specific tables (Motor, Endowment, House, Commercial)
- VSAM file structures (KSDSCUST, KSDSPOLY)
- Two-phase commit coordination
- Named counter server configuration
- Temporary storage queues (error and control tracking)
- Data relationships diagram
- Data integrity constraints
- Database statistics and sizing

**Use When:** Understanding data storage, designing schema changes, debugging data issues, or planning data migration

**Read Time:** 20 minutes

---

### 6. **[Development Guide](./development-guide.md)** 👨‍💻
**Step-by-step instructions for developing, testing, and deploying changes**

- Prerequisites and environment setup
- Project structure review
- Development workflow (5 steps)
- Code editing examples (adding new fields)
- Build and compilation procedures
- Unit testing walkthrough
- Integration testing
- Performance testing with workload simulator
- Code review checklist
- Testing procedures with examples
- Debugging techniques
- Version control with Git
- Deployment process and checklist
- Common development tasks with time estimates

**Use When:** Making code changes, setting up development environment, learning to test, or planning deployments

**Read Time:** 40 minutes

---

## 📖 Existing Documentation (From Repository)

The following documentation files exist in the base directory and should be reviewed:

- **[README.md](../base/README.md)** - Project introduction and overview
- **[Architecture.md](../base/Architecture.md)** - Original application architecture description
- **[Building.md](../base/Building.md)** - Build and setup procedures
- **[Installation.md](../base/Installation.md)** - Installation guide for z/OS deployment
- **[Testing.md](../base/Testing.md)** - Testing procedures and validation
- **[Reference.md](../base/Reference.md)** - Reference documentation for resources

---

## 🎯 Getting Started by Role

### 👔 Project Manager / Business Stakeholder
1. Read: [Project Overview](./project-overview.md) (10 min)
2. Read: "Business Purpose" section in [Architecture](./architecture.md) (5 min)
3. Review: [Modernization Considerations](./architecture.md#modernization-considerations) section

**Total Time:** 20 minutes

### 👨‍💻 New Developer
1. Read: [Project Overview](./project-overview.md) (10 min)
2. Read: [Source Tree Analysis](./source-tree-analysis.md) (20 min)
3. Skim: [Component Inventory](./component-inventory.md) - focus on naming conventions (10 min)
4. Read: [Development Guide](./development-guide.md) - prerequisites section (10 min)
5. Study: One example in Development Guide (15 min)

**Total Time:** 65 minutes

**Next Steps:** Set up development environment, run a test transaction, make a small code change

### 🏗️ Architect / Technical Lead
1. Read: [Architecture Documentation](./architecture.md) (30 min)
2. Review: [Data Models](./data-models.md) (20 min)
3. Study: [Component Inventory](./component-inventory.md) - program call hierarchy (15 min)
4. Review: Scalability and modernization sections in [Architecture](./architecture.md) (10 min)

**Total Time:** 75 minutes

**Next Steps:** Design architectural changes, plan modernization strategy, review code changes

### 🧪 QA / Test Engineer
1. Read: [Development Guide](./development-guide.md) - Testing Procedures section (20 min)
2. Read: [Component Inventory](./component-inventory.md) - understand 6 transactions (10 min)
3. Study: Testing examples in [Development Guide](./development-guide.md) (20 min)
4. Review: [Data Models](./data-models.md) - understand test data requirements (10 min)

**Total Time:** 60 minutes

**Next Steps:** Create test cases, set up test data, execute test procedures

### 📊 Data Analyst / DBA
1. Read: [Data Models](./data-models.md) (20 min)
2. Review: Data flow diagrams in [Architecture](./architecture.md) (10 min)
3. Study: Db2 schema and VSAM files in [Data Models](./data-models.md) (20 min)
4. Review: Two-phase commit section in [Architecture](./architecture.md) (10 min)

**Total Time:** 60 minutes

**Next Steps:** Verify schemas, plan backups, monitor performance

---

## 🔍 Documentation by Use Case

### Use Case: Adding a New Insurance Policy Type

**Documents to Review:**
1. [Component Inventory](./component-inventory.md) - Understand policy program pattern
2. [Data Models](./data-models.md) - Create new policy table schema
3. [Development Guide](./development-guide.md) - Task 2: Create New Policy Type
4. [Architecture](./architecture.md) - Understand layer responsibilities

**Estimated Time:** 90 minutes reading + 4 hours development

---

### Use Case: Modernizing to Microservices

**Documents to Review:**
1. [Architecture](./architecture.md) - Modernization Considerations section
2. [Component Inventory](./component-inventory.md) - Identify service boundaries
3. [Source Tree Analysis](./source-tree-analysis.md) - Understand dependencies
4. [Data Models](./data-models.md) - Plan data storage strategy

**Estimated Time:** 90 minutes reading + Planning session

---

### Use Case: Exposing as REST APIs

**Documents to Review:**
1. [Architecture](./architecture.md) - API Gateway Exposure section
2. [Component Inventory](./component-inventory.md) - Transaction interfaces
3. [Data Models](./data-models.md) - Input/output contracts
4. [Development Guide](./development-guide.md) - Testing procedures

**Estimated Time:** 75 minutes reading + API design session

---

### Use Case: Migrating to Cloud

**Documents to Review:**
1. [Project Overview](./project-overview.md) - Full overview
2. [Architecture](./architecture.md) - Complete architecture understanding
3. [Data Models](./data-models.md) - Identify data dependencies
4. [Source Tree Analysis](./source-tree-analysis.md) - Understand file dependencies

**Estimated Time:** 150 minutes reading + Architecture workshop

---

### Use Case: Debugging a Production Issue

**Documents to Review:**
1. [Component Inventory](./component-inventory.md) - Find affected programs
2. [Architecture](./architecture.md) - Data flow patterns
3. [Development Guide](./development-guide.md) - Debugging techniques
4. Specific program file (from src/)

**Estimated Time:** 20 minutes reading + Debugging session

---

### Use Case: Creating Automated Tests

**Documents to Review:**
1. [Development Guide](./development-guide.md) - Testing Procedures section
2. [Component Inventory](./component-inventory.md) - Understand transaction flow
3. [Data Models](./data-models.md) - Expected data states
4. [Development Guide](./development-guide.md) - Automated test example code

**Estimated Time:** 45 minutes reading + Test development

---

## 📊 Documentation Statistics

| Document | Pages | Topics | Use Cases |
|----------|-------|--------|-----------|
| Project Overview | 2 | 8 | New teams, stakeholders, executives |
| Architecture | 8 | 15 | Architects, technical leads, modernization |
| Source Tree | 4 | 7 | Developers, code navigation |
| Component Inventory | 6 | 8 | Developers, architects, refactoring |
| Data Models | 7 | 9 | DBAs, architects, schema changes |
| Development Guide | 9 | 12 | Developers, QA, DevOps |
| **Total** | **36** | **59** | **6 major roles** |

---

## 🔗 Quick Links by File Type

### COBOL Programs (25 total)

**Customer Programs:**
- `lgacus01.cbl` / `lgacol01.cbl` / `lgacdb01.cbl` / `lgacvs01.cbl` - Add customer
- `lgicus01.cbl` / `lgicol01.cbl` / `lgicdb01.cbl` / `lgicvs01.cbl` - Inquire customer
- `lgucus01.cbl` / `lgucol01.cbl` / `lgucdb01.cbl` / `lgucvs01.cbl` - Update customer

See [Component Inventory](./component-inventory.md) for complete list and descriptions.

**Policy Programs:**
- `lgapus01.cbl` / `lgapol01.cbl` / `lgapdb01.cbl` / `lgapvs01.cbl` - Add policy
- `lgipus01.cbl` / `lgipol01.cbl` / `lgipdb01.cbl` / `lgipvs01.cbl` - Inquire policy
- `lgupus01.cbl` / `lgupol01.cbl` / `lgupdb01.cbl` / `lgupvs01.cbl` - Update policy
- `lgdpol01.cbl` / `lgdpdb01.cbl` / `lgdpvs01.cbl` - Display policy

### Data Structures (13 copybooks)
See [Data Models](./data-models.md) and [Component Inventory](./component-inventory.md) for complete list.

### Screen Definition (1 BMS map)
- `ssmap.bms` - 3270 terminal screen definition. See [Source Tree Analysis](./source-tree-analysis.md) for details.

---

## 🚀 Next Steps

### First Time Here?
1. ✅ Read [Project Overview](./project-overview.md)
2. ✅ Choose your role above and follow the getting started guide
3. ✅ Bookmark this index for future reference

### Want to Make Code Changes?
1. ✅ Read [Development Guide](./development-guide.md)
2. ✅ Review [Source Tree Analysis](./source-tree-analysis.md)
3. ✅ Find your target file in [Component Inventory](./component-inventory.md)
4. ✅ Follow the development workflow

### Planning System Changes?
1. ✅ Review [Architecture](./architecture.md)
2. ✅ Study [Data Models](./data-models.md)
3. ✅ Examine [Component Inventory](./component-inventory.md)
4. ✅ Identify affected programs and data structures

### Modernization Planning?
1. ✅ Read [Project Overview](./project-overview.md) - Modernization Context
2. ✅ Review [Architecture](./architecture.md) - Modernization Considerations
3. ✅ Plan with technical team using this documentation as baseline

---

## 📝 Document Generation Details

| Aspect | Details |
|--------|---------|
| **Generation Date** | October 30, 2025, 14:50 UTC |
| **Scan Type** | Deep Scan (10-30 minutes analysis) |
| **Project Root** | `/Users/niklas/genai/sw-modernization/cics-genapp-bmad/base` |
| **Output Folder** | `/Users/niklas/genai/sw-modernization/cics-genapp-bmad/docs` |
| **Total Documentation** | 36+ pages, 15+ diagrams |
| **Analysis Method** | Source code enumeration + pattern analysis + Architecture documentation review |
| **Coverage** | 100% of source files analyzed |

---

## 🔄 Updates and Maintenance

This documentation is generated from source code analysis and should be updated when:

- [ ] New COBOL programs are added
- [ ] Copybook structures change
- [ ] Db2 schema is modified
- [ ] Architecture changes significantly
- [ ] New development patterns emerge

**To regenerate this documentation:**
```bash
# Using the BMAD document-project workflow
/bmad:bmm:workflows:document-project
# Select: Deep Scan option
# Expected time: 15-30 minutes
```

---

## 💡 Tips for Using This Documentation

1. **Use the Quick Navigation:** Bookmark the documents you use most
2. **Cross-Reference:** Notice the links between documents
3. **Study Examples:** Review code examples in Development Guide
4. **Reference Architecture:** Use Architecture as reference for design decisions
5. **Component Lookup:** Use Component Inventory as quick reference

---

## 📞 Questions or Feedback?

For questions about:
- **Project/Business:** See Project Overview
- **Technical Design:** See Architecture Documentation
- **Code Navigation:** See Source Tree Analysis
- **Specific Programs:** See Component Inventory
- **Making Changes:** See Development Guide
- **Data/Schemas:** See Data Models

---

## 📄 Document List

| Document | Purpose | Audience | Read Time |
|----------|---------|----------|-----------|
| [Project Overview](./project-overview.md) | High-level understanding | Everyone | 10 min |
| [Architecture](./architecture.md) | System design patterns | Architects, Leads | 30 min |
| [Source Tree Analysis](./source-tree-analysis.md) | Code organization | Developers | 20 min |
| [Component Inventory](./component-inventory.md) | Program catalog | Developers, Architects | 25 min |
| [Data Models](./data-models.md) | Database schema | DBAs, Developers | 20 min |
| [Development Guide](./development-guide.md) | Development procedures | Developers, QA | 40 min |

**Total Read Time (All Documents):** ~145 minutes (≈2.5 hours)

---

Generated with ❤️ by BMAD v6 documentation system
Last Updated: October 30, 2025
