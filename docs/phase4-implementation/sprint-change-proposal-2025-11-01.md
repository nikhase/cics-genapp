# Sprint Change Proposal: Java 21 LTS Technology Stack Upgrade

**Author:** Correct-Course Workflow
**Date:** November 1, 2025
**Status:** Ready for Review
**Scope Classification:** MINOR (Direct Implementation)
**Project:** CICS GenApp Cloud Modernization

---

## 1. Issue Summary

### Problem Statement

The CICS GenApp Cloud Modernization project launched with Java 17+ and Spring Boot 3.3+ specifications. While technically sound, these versions present **End-of-Life (EOL) risk** that compounds technical debt when starting a fresh cloud-native project. Java 17 enters maintenance-only phase (expires September 2026), and PostgreSQL 15 approaches EOL (October 2025). This change proposal upgrades the technology stack to stable, production-grade LTS releases with extended support windows, eliminating version obsolescence risk at project inception.

### Discovery Context

**When Identified:** During Sprint 1, immediately after Story 1-1 (Project Repository Setup) implementation was completed but not yet merged.

**Why It Matters:** Story 1-1 establishes the foundational technology configuration that all subsequent 42 stories (Epics 2-5) will build upon. Correcting this during the foundation phase (before merging) has **zero timeline impact** and prevents propagating outdated versions through the entire project codebase.

### Supporting Evidence

**Java 17 EOL Timeline:**
- Released: September 2021
- Maintenance Phase: September 2026
- Extended Support Ends: September 2026
- **Java 21 LTS:** September 2023, Support until September 2031 ✅

**PostgreSQL EOL Timeline:**
- PostgreSQL 15: Released October 2023, EOL: October 2025 (1 year support remaining)
- **PostgreSQL 16 LTS:** Released October 2023, Support until October 2028 ✅

**Spring Ecosystem:**
- Spring Boot 3.3.x: LTS track with Spring Framework 6.1.x ✅
- All versions compatible with Java 21 and PostgreSQL 16

---

## 2. Impact Analysis

### Epic Impact Assessment

| Epic | Current Status | Impact | Notes |
|------|---|---|---|
| **Epic 1** (Cloud Foundation) | 1-1 in review | ✅ Direct adjustment | Story 1-1 updated during review; no timeline impact |
| **Epic 2** (Customer API) | Backlog | ✅ Inherits Java 21 | No changes needed; foundation is more stable |
| **Epic 3** (React Frontend) | Backlog | ✅ No impact | Frontend uses Node.js; independent of Java version |
| **Epic 4** (Parallel Run) | Backlog | ✅ Stronger foundation | Benefits from Java 21 GC improvements |
| **Epic 5** (Policy API) | Backlog | ✅ Inherits Java 21 | Better performance baseline for API workloads |

**Summary:** Zero epic-level impact. All 5 epics remain viable with enhanced stability.

### Artifact Conflicts & Required Updates

**PRD (Product Requirements Document):**
- ✅ No conflicts identified
- ✅ MVP scope unaffected
- ✅ All functional/non-functional requirements remain achievable
- **Action:** No PRD changes required

**Architecture Documentation (target-architecture.md):**
- ⚠️ **REQUIRES UPDATE:** Technology Stack Decisions table specifies Java 17+, PostgreSQL 15+
- **Action:** Update version specifications to Java 21 LTS, PostgreSQL 16 LTS (see Detailed Proposals below)

**Story 1-1 (Project Repository Setup):**
- ⚠️ **REQUIRES UPDATE:** Acceptance Criteria specify Java 17, README specifies Java 17+ prerequisite
- **Action:** Update AC and README before Story 1-1 merge (see Detailed Proposals below)

**Secondary Artifacts Affected:**

| Artifact | Change Required | Scope |
|----------|---|---|
| `genapp-backend/pom.xml` | `<maven.compiler.source>` & `<target>`: 17 → 21 | Compiler configuration |
| `genapp-backend/Dockerfile` | `FROM eclipse-temurin:17-jre-alpine` → `21-jre-alpine` | Runtime container image |
| `genapp-backend/README.md` | Prerequisites: Java 17+ → 21 LTS | Developer documentation |
| GitHub Actions CI/CD | Java matrix: add 21, optionally remove 17 | Build pipeline configuration |
| `docker-compose.yml` | PostgreSQL image: postgres:15 → postgres:16 | Local development environment |
| Architecture Document | Tech stack table: version columns | Reference documentation |

---

## 3. Recommended Approach

### Selected Path: **Option 1 - Direct Adjustment**

**Why This Approach?**

1. **Perfect Timing:** Story 1-1 is still in "review" status (not yet merged to main). No rollback needed; simple update before merge.
2. **Zero Timeline Impact:** Change is purely configuration; no story flow impact or reprioritization required.
3. **Backward Compatible:** Java 21 is drop-in replacement for Java 17 code; no source code changes needed.
4. **Risk Minimal:** Spring Boot 3.3.x explicitly tested and certified with Java 21; no compatibility concerns.
5. **Future-Proof:** Java 21 extends project support window by 5+ years vs. Java 17.

### Effort & Risk Assessment

| Factor | Assessment |
|--------|---|
| **Implementation Effort** | LOW (2-3 hours) - Configuration file edits only |
| **Code Changes Required** | NONE - No Java source code modifications |
| **Testing Required** | MEDIUM - Verify builds, run Story 1-1 acceptance tests, Docker image validation |
| **Risk Level** | MINIMAL - Java 21 backward compatible, no architectural implications |
| **Timeline Impact** | NONE - Can complete during Story 1-1 review phase |
| **Team Coordination** | LOW - Single story (1-1) affected; other stories inherit foundation |

### MVP Impact

✅ **MVP Unaffected**
- Original PRD MVP (Customer & Policy CRUD APIs with web UI) remains fully achievable
- Java 21 improves performance baseline vs Java 17
- No scope reduction needed
- No additional work required beyond version updates

---

## 4. Detailed Change Proposals

### Change Group 1: Maven Build Configuration

#### Change 1A: Update pom.xml Java Version

**File:** `genapp-backend/pom.xml`
**Section:** `<properties>`

```xml
<!-- BEFORE -->
<maven.compiler.source>17</maven.compiler.source>
<maven.compiler.target>17</maven.compiler.target>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

<!-- AFTER -->
<maven.compiler.source>21</maven.compiler.source>
<maven.compiler.target>21</maven.compiler.target>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
```

**Rationale:** Updates Java compiler to target Java 21 bytecode; aligns with chosen LTS version strategy.

**Acceptance Criteria:**
- [ ] Maven build (`mvn clean install`) succeeds with Java 21 compiler
- [ ] No compilation warnings related to language features
- [ ] Spring Boot application starts successfully

---

### Change Group 2: Container Runtime Configuration

#### Change 2A: Update Dockerfile Base Image

**File:** `genapp-backend/Dockerfile`
**Section:** `FROM` directive (first build stage)

```dockerfile
<!-- BEFORE -->
FROM eclipse-temurin:17-jre-alpine AS builder
...
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

<!-- AFTER -->
FROM eclipse-temurin:21-jre-alpine AS builder
...
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
```

**Rationale:** Runtime container must match Java 21 compiler target; Alpine variant maintains lightweight production image.

**Acceptance Criteria:**
- [ ] Docker image builds successfully
- [ ] Image size remains < 500MB (Alpine optimization maintained)
- [ ] Container starts and passes healthcheck

---

#### Change 2B: Update docker-compose.yml PostgreSQL Version

**File:** `genapp-backend/docker-compose.yml`
**Section:** Services → `postgres`

```yaml
# BEFORE
postgres:
  image: postgres:15-alpine
  environment:
    POSTGRES_DB: cicsgenapp
    POSTGRES_PASSWORD: dev_password

# AFTER
postgres:
  image: postgres:16-alpine
  environment:
    POSTGRES_DB: cicsgenapp
    POSTGRES_PASSWORD: dev_password
```

**Rationale:** PostgreSQL 16 LTS provides extended support (to Oct 2028) vs PostgreSQL 15 (EOL Oct 2025); maintains local dev parity with production target.

**Acceptance Criteria:**
- [ ] `docker-compose up` starts PostgreSQL 16 successfully
- [ ] Database initializes correctly for Spring Boot application
- [ ] No schema migration errors during first connection

---

### Change Group 3: Story 1-1 Updates

#### Change 3A: Update Story 1-1 Acceptance Criteria

**File:** `docs/stories/1-1-project-repository-setup-and-developer-environment.md`
**Section:** Acceptance Criteria

```markdown
<!-- BEFORE (AC #2) -->
2. Maven pom.xml configured in `genapp-backend/` with Spring Boot 3.3+ LTS, all required dependencies (Spring Web, Data JPA, Security, Cloud Gateway, Actuator, etc.)

<!-- AFTER (AC #2) -->
2. Maven pom.xml configured in `genapp-backend/` with Java 21 LTS, Spring Boot 3.3.x LTS (Spring Framework 6.1.x), all required dependencies (Spring Web, Data JPA, Security, Cloud Gateway, Actuator, etc.)
```

```markdown
<!-- BEFORE (AC #4) -->
4. README.md created in `genapp-backend/` with developer setup instructions (Java 17+, Maven 3.8+, Docker, PostgreSQL, mvn clean install)

<!-- AFTER (AC #4) -->
4. README.md created in `genapp-backend/` with developer setup instructions (Java 21 LTS, Maven 3.8+, Docker, PostgreSQL 16 LTS, mvn clean install)
```

**Rationale:** Acceptance criteria are contract between story and implementation; must reflect actual versions deployed.

---

#### Change 3B: Update genapp-backend/ README.md Prerequisites

**File:** `genapp-backend/README.md`
**Section:** Prerequisites

```markdown
<!-- BEFORE -->
### Prerequisites

- **Java 17+** (OpenJDK or Eclipse Temurin) — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Install Guide](https://maven.apache.org/install.html)
- **Docker 24.x** (for PostgreSQL) — [Download](https://www.docker.com/products/docker-desktop)
- **PostgreSQL 15** (or use docker-compose for auto-provisioning)
- **Git** (for repository access)

### Local Setup

1. **Install Java 17+:**
   ```bash
   # Verify Java installation
   java -version
   # Should output: openjdk version "17.x.x"
   ```

### Development Profiles

Spring Boot uses profiles for environment-specific configuration:

- `dev` — Local development with PostgreSQL 15
- `test` — Testing environment
- `prod` — Production configuration (Kubernetes/Cloud)

<!-- AFTER -->
### Prerequisites

- **Java 21 LTS** (OpenJDK or Eclipse Temurin) — [Download](https://adoptium.net/)
  - **Why Java 21?** Long-term support until September 2031; eliminates version obsolescence risk
- **Maven 3.8+** — [Install Guide](https://maven.apache.org/install.html)
- **Docker 24.x** (for PostgreSQL) — [Download](https://www.docker.com/products/docker-desktop)
- **PostgreSQL 16 LTS** (or use docker-compose for auto-provisioning)
- **Git** (for repository access)

### Local Setup

1. **Install Java 21 LTS:**
   ```bash
   # Verify Java installation
   java -version
   # Should output: openjdk version "21.x.x" LTS
   ```

### Development Profiles

Spring Boot uses profiles for environment-specific configuration:

- `dev` — Local development with PostgreSQL 16
- `test` — Testing environment
- `prod` — Production configuration (Kubernetes/Cloud)
```

**Rationale:** Developer documentation is primary reference for environment setup; must be accurate and include rationale for Java 21 choice.

---

### Change Group 4: Architecture Documentation

#### Change 4A: Update target-architecture.md Technology Stack Table

**File:** `docs/new/target-architecture.md`
**Section:** Technology Stack Decisions

```markdown
<!-- BEFORE -->
| Component                   | Decision                     | Version     | Rationale                                                        | Affects Epics |
| --------------------------- | ---------------------------- | ----------- | ---------------------------------------------------------------- | ------------- |
| **Language**                | Java                         | 17+         | Spring Boot 3.3+ requirement; team experienced                   | 1, 2, 4, 5    |
| **Backend Framework**       | Spring Boot                  | 3.3+ LTS    | Modern, OIDC support, team ready, fast time-to-market            | 1, 2, 4, 5    |
| **Primary Database**        | PostgreSQL                   | 15+         | Modern, open-source, excellent Spring Boot integration           | 1, 2, 5       |

<!-- AFTER -->
| Component                   | Decision                     | Version     | Rationale                                                        | Affects Epics |
| --------------------------- | ---------------------------- | ----------- | ---------------------------------------------------------------- | ------------- |
| **Language**                | Java                         | 21 LTS      | Long-term support until Sept 2031; zero EOL risk; team ready     | 1, 2, 4, 5    |
| **Backend Framework**       | Spring Boot                  | 3.3.x LTS   | Modern, OIDC support, team ready, fast time-to-market; Java 21 compatible | 1, 2, 4, 5    |
| **Primary Database**        | PostgreSQL                   | 16 LTS      | Long-term support until Oct 2028; excellent Spring Boot integration | 1, 2, 5       |
```

**Rationale:** Architecture document is source-of-truth for technology decisions; must reflect choices made during sprint planning.

---

#### Change 4B: Update Dockerfile Example in Architecture Document

**File:** `docs/new/target-architecture.md`
**Section:** Deployment Architecture → Docker Images → Spring Boot

```dockerfile
<!-- BEFORE -->
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "app.jar"]

<!-- AFTER -->
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "app.jar"]
```

**Rationale:** Documentation examples must match actual implementation; prevents confusion during future deployments.

---

## 5. Implementation Handoff

### Change Scope Classification

**MINOR Scope** - All changes are configuration-only; no architectural implications or strategic reconsiderations required.

### Implementation Team & Responsibilities

| Role | Responsibility | Timeline |
|------|---|---|
| **Developer (Story 1-1)** | Execute all 7 change proposals in genapp-backend/ directory and Story 1-1 acceptance criteria | Within story 1-1 review cycle (before merge) |
| **Scrum Master** | Update sprint status if needed; confirm Story 1-1 testing covers Java 21 and PostgreSQL 16 | Approval checkpoint |
| **Architecture Reviewer** | Review and approve target-architecture.md updates; verify consistency with other tech choices | Before Story 1-1 merge |

### Success Criteria

✅ **Story 1-1 Must Satisfy All Updated Acceptance Criteria:**
- [ ] Maven build (`mvn clean install`) succeeds with Java 21
- [ ] Story 1-1 README.md documents Java 21 LTS and PostgreSQL 16 prerequisites
- [ ] Dockerfile builds successfully and passes healthcheck with Java 21 runtime
- [ ] `docker-compose up` provisions PostgreSQL 16 without errors
- [ ] Pre-commit hooks and code quality tools function correctly with Java 21
- [ ] All unit tests pass (`mvn test`)
- [ ] Spring Boot application starts and responds to `/actuator/health` endpoint

✅ **Documentation Updated Consistently:**
- [ ] target-architecture.md technology stack table reflects Java 21 LTS, PostgreSQL 16 LTS choices
- [ ] All references to Java 17+ updated to Java 21 LTS
- [ ] Architecture diagrams and examples show correct versions
- [ ] No remaining inconsistencies between documents

### Definition of Done

Story 1-1 is complete when:
1. ✅ All 5 change proposals implemented and tested
2. ✅ All 8 acceptance criteria verified
3. ✅ target-architecture.md updated and reviewed
4. ✅ Code passes pre-commit hooks and CI/CD pipeline
5. ✅ PR approved by architecture reviewer
6. ✅ Merged to main branch

### Next Steps (Post-Merge)

Once Story 1-1 merges with Java 21 foundation:

1. **Epic 1 Continuation:** All remaining Epic 1 stories (1-2 through 1-12) inherit Java 21 foundation — no version adjustments needed
2. **Epic 2-5 Stories:** Customer and Policy API stories automatically benefit from Java 21 GC improvements and virtual thread support
3. **CI/CD Validation:** GitHub Actions pipeline will validate all builds with Java 21 baseline
4. **No Retroactive Changes:** Downstream stories do not need updates; foundation change cascades forward

---

## Summary

| Aspect | Finding |
|--------|---------|
| **Issue** | Project launched with Java 17 & PostgreSQL 15; both approaching EOL with continued risk |
| **Change Scope** | MINOR - Configuration updates only; zero code changes required |
| **Impact** | ✅ No MVP impact; ✅ No epic timeline changes; ✅ Strengthened foundation |
| **Recommended Path** | Direct Adjustment during Story 1-1 review (perfect timing, zero delay) |
| **Effort** | 2-3 hours; 7 configuration file changes + documentation updates |
| **Risk** | Minimal; Java 21 is backward compatible, fully certified with Spring Boot 3.3.x |
| **Success Measure** | Story 1-1 passes all AC with Java 21; architecture documents updated consistently |

---

**Prepared by:** Correct-Course Workflow
**Status:** Ready for User Approval
**Next Step:** Obtain explicit approval to proceed with implementation (Step 5)

