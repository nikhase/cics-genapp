# Implementation Tasks: Java 21 LTS Technology Stack Upgrade

**Story:** 1-1 (Project Repository Setup and Developer Environment)
**Approved:** November 1, 2025
**Change Scope:** MINOR (Direct Implementation)
**Effort:** 2-3 hours

---

## Overview

This document outlines specific implementation tasks to upgrade the CICS GenApp technology baseline from Java 17 to Java 21 LTS. All changes are configuration-only; no source code modifications required.

**Reference Document:** [Sprint Change Proposal](./sprint-change-proposal-2025-11-01.md)

---

## Implementation Tasks

### Task 1: Update Maven Build Configuration

**File:** `genapp-backend/pom.xml`

**Action:** Update Java compiler version properties

```bash
# Navigate to project root
cd genapp-backend

# Edit pom.xml and locate <properties> section
# Find these lines:
#   <maven.compiler.source>17</maven.compiler.source>
#   <maven.compiler.target>17</maven.compiler.target>
#
# Replace with:
#   <maven.compiler.source>21</maven.compiler.source>
#   <maven.compiler.target>21</maven.compiler.target>
```

**Verification:**
```bash
mvn clean install
# Should complete successfully with:
# [INFO] BUILD SUCCESS

# Verify Java version in build output
mvn -v
# Should show Java 21.x
```

**Acceptance Criteria:**
- [ ] pom.xml properties updated
- [ ] `mvn clean install` succeeds without warnings
- [ ] Spring Boot application starts with Java 21 compiler target
- [ ] No deprecation warnings related to language features

---

### Task 2: Update Docker Base Image (Multi-stage Build)

**File:** `genapp-backend/Dockerfile`

**Action:** Update both build stage and runtime stage

```dockerfile
# Original (2 occurrences):
# FROM eclipse-temurin:17-jre-alpine

# Updated to:
# FROM eclipse-temurin:21-jre-alpine
```

**Specific Changes:**

**Line ~1 (Builder stage):**
```dockerfile
# BEFORE
FROM eclipse-temurin:17-jre-alpine AS builder

# AFTER
FROM eclipse-temurin:21-jre-alpine AS builder
```

**Line ~XX (Runtime stage):**
```dockerfile
# BEFORE
FROM eclipse-temurin:17-jre-alpine

# AFTER
FROM eclipse-temurin:21-jre-alpine
```

**Verification:**
```bash
docker build -t cicsgenapp-backend:test .
# Should complete successfully

docker run --rm cicsgenapp-backend:test java -version
# Should output: openjdk version "21.x.x" LTS

docker images | grep cicsgenapp-backend
# Verify image size < 500MB (Alpine optimization maintained)
```

**Acceptance Criteria:**
- [ ] Both `FROM` directives updated
- [ ] Docker build completes without errors
- [ ] Container image size remains < 500MB
- [ ] Runtime Java version is 21.x LTS
- [ ] Healthcheck passes: `curl -f http://localhost:8080/actuator/health`

---

### Task 3: Update Docker Compose Development Environment

**File:** `genapp-backend/docker-compose.yml`

**Action:** Update PostgreSQL service image version

```yaml
# BEFORE
services:
  postgres:
    image: postgres:15-alpine

# AFTER
services:
  postgres:
    image: postgres:16-alpine
```

**Verification:**
```bash
# Stop any existing containers
docker-compose down

# Start fresh environment
docker-compose up -d

# Verify PostgreSQL version
docker exec <container-id> psql --version
# Should output: psql (PostgreSQL) 16.x

# Verify database initialization
docker-compose logs postgres | grep "database system is ready"
```

**Acceptance Criteria:**
- [ ] PostgreSQL image updated to 16-alpine
- [ ] `docker-compose up` starts without errors
- [ ] Database initializes correctly
- [ ] Spring Boot application connects to PostgreSQL 16 successfully
- [ ] No schema migration errors on first connection

---

### Task 4: Update Story 1-1 Acceptance Criteria

**File:** `docs/stories/1-1-project-repository-setup-and-developer-environment.md`

**Action:** Update 2 acceptance criteria to reflect Java 21 and PostgreSQL 16

**AC #2 (Maven Configuration):**
```markdown
# BEFORE
2. Maven pom.xml configured in `genapp-backend/` with Spring Boot 3.3+ LTS,
   all required dependencies (Spring Web, Data JPA, Security, Cloud Gateway,
   Actuator, etc.)

# AFTER
2. Maven pom.xml configured in `genapp-backend/` with Java 21 LTS,
   Spring Boot 3.3.x LTS (Spring Framework 6.1.x), all required dependencies
   (Spring Web, Data JPA, Security, Cloud Gateway, Actuator, etc.)
```

**AC #4 (README Prerequisites):**
```markdown
# BEFORE
4. README.md created in `genapp-backend/` with developer setup instructions
   (Java 17+, Maven 3.8+, Docker, PostgreSQL, mvn clean install)

# AFTER
4. README.md created in `genapp-backend/` with developer setup instructions
   (Java 21 LTS, Maven 3.8+, Docker, PostgreSQL 16 LTS, mvn clean install)
```

**Verification:**
```bash
# Verify AC content matches actual implementation
cat docs/stories/1-1-project-repository-setup-and-developer-environment.md | grep -A2 "## Acceptance Criteria"
```

**Acceptance Criteria:**
- [ ] AC #2 updated to specify Java 21 LTS and Spring Framework 6.1.x
- [ ] AC #4 updated to specify Java 21 LTS and PostgreSQL 16 LTS
- [ ] No other ACs modified
- [ ] All 8 ACs remain consistent with implementation

---

### Task 5: Update genapp-backend/ README.md Prerequisites Section

**File:** `genapp-backend/README.md`

**Actions:**
1. Update Prerequisites section
2. Update Java version verification example
3. Update Development Profiles section
4. Add rationale for Java 21 choice

**Specific Changes:**

**Section: Prerequisites**
```markdown
# BEFORE
### Prerequisites

- **Java 17+** (OpenJDK or Eclipse Temurin) — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Install Guide](https://maven.apache.org/install.html)
- **Docker 24.x** (for PostgreSQL) — [Download](https://www.docker.com/products/docker-desktop)
- **PostgreSQL 15** (or use docker-compose for auto-provisioning)
- **Git** (for repository access)

# AFTER
### Prerequisites

- **Java 21 LTS** (OpenJDK or Eclipse Temurin) — [Download](https://adoptium.net/)
  - **Why Java 21?** Long-term support until September 2031; eliminates version obsolescence risk
- **Maven 3.8+** — [Install Guide](https://maven.apache.org/install.html)
- **Docker 24.x** (for PostgreSQL) — [Download](https://www.docker.com/products/docker-desktop)
- **PostgreSQL 16 LTS** (or use docker-compose for auto-provisioning)
- **Git** (for repository access)
```

**Section: Local Setup**
```markdown
# BEFORE
1. **Install Java 17+:**
   ```bash
   # Verify Java installation
   java -version
   # Should output: openjdk version "17.x.x"
   ```

# AFTER
1. **Install Java 21 LTS:**
   ```bash
   # Verify Java installation
   java -version
   # Should output: openjdk version "21.x.x" LTS
   ```
```

**Section: Development Profiles**
```markdown
# BEFORE
Spring Boot uses profiles for environment-specific configuration:

- `dev` — Local development with PostgreSQL 15
- `test` — Testing environment
- `prod` — Production configuration (Kubernetes/Cloud)

# AFTER
Spring Boot uses profiles for environment-specific configuration:

- `dev` — Local development with PostgreSQL 16
- `test` — Testing environment
- `prod` — Production configuration (Kubernetes/Cloud)
```

**Verification:**
```bash
# Verify README accuracy
grep -n "Java 21" genapp-backend/README.md
grep -n "PostgreSQL 16" genapp-backend/README.md
grep -n "17+" genapp-backend/README.md  # Should return no results
```

**Acceptance Criteria:**
- [ ] All references to Java 17+ updated to Java 21 LTS
- [ ] All references to PostgreSQL 15 updated to PostgreSQL 16 LTS
- [ ] Prerequisites section includes rationale for Java 21
- [ ] Java version verification example updated
- [ ] Development Profiles section updated

---

### Task 6: Update target-architecture.md Technology Stack Table

**File:** `docs/new/target-architecture.md`

**Actions:** Update 3 rows in Technology Stack Decisions table

**Change 6A: Language row**
```markdown
# BEFORE
| **Language**                | Java                         | 17+         | Spring Boot 3.3+ requirement; team experienced                   | 1, 2, 4, 5    |

# AFTER
| **Language**                | Java                         | 21 LTS      | Long-term support until Sept 2031; zero EOL risk; team ready     | 1, 2, 4, 5    |
```

**Change 6B: Backend Framework row**
```markdown
# BEFORE
| **Backend Framework**       | Spring Boot                  | 3.3+ LTS    | Modern, OIDC support, team ready, fast time-to-market            | 1, 2, 4, 5    |

# AFTER
| **Backend Framework**       | Spring Boot                  | 3.3.x LTS   | Modern, OIDC support, team ready, fast time-to-market; Java 21 compatible | 1, 2, 4, 5    |
```

**Change 6C: Primary Database row**
```markdown
# BEFORE
| **Primary Database**        | PostgreSQL                   | 15+         | Modern, open-source, excellent Spring Boot integration           | 1, 2, 5       |

# AFTER
| **Primary Database**        | PostgreSQL                   | 16 LTS      | Long-term support until Oct 2028; excellent Spring Boot integration | 1, 2, 5       |
```

**Verification:**
```bash
# Verify table syntax and content
grep -A5 "Technology Stack Decisions" docs/new/target-architecture.md
grep "Java.*21 LTS" docs/new/target-architecture.md
grep "PostgreSQL.*16 LTS" docs/new/target-architecture.md
```

**Acceptance Criteria:**
- [ ] Language row specifies Java 21 LTS with EOL justification
- [ ] Backend Framework row specifies Spring Boot 3.3.x with Java 21 compatibility note
- [ ] Primary Database row specifies PostgreSQL 16 LTS with support timeline
- [ ] Markdown table formatting preserved
- [ ] No broken links in updated rows

---

### Task 7: Update Dockerfile Example in Architecture Document

**File:** `docs/new/target-architecture.md`

**Action:** Update Dockerfile example in Deployment Architecture section

**Locate:** "## Deployment Architecture" → "### Docker Images" → "**Spring Boot:**" code block

```dockerfile
# BEFORE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "app.jar"]

# AFTER
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "app.jar"]
```

**Verification:**
```bash
# Verify documentation example matches actual Dockerfile
grep "FROM eclipse-temurin" docs/new/target-architecture.md
grep "FROM eclipse-temurin" genapp-backend/Dockerfile
# Both should reference eclipse-temurin:21-jre-alpine
```

**Acceptance Criteria:**
- [ ] Dockerfile example updated to eclipse-temurin:21-jre-alpine
- [ ] Example code block remains syntactically valid
- [ ] No other lines in example modified
- [ ] Matches actual Dockerfile in genapp-backend/

---

## Completion Checklist

### Pre-Implementation
- [ ] Story 1-1 is currently in "review" status
- [ ] Java 21 LTS is installed locally (java -version confirms 21.x)
- [ ] Maven 3.8+ is available (mvn -v confirms version)
- [ ] Docker 24.x is running
- [ ] Git repository is on feature branch (not main)

### Task Execution (in order)
- [ ] **Task 1:** pom.xml updated and `mvn clean install` succeeds
- [ ] **Task 2:** Dockerfile updated and image builds successfully
- [ ] **Task 3:** docker-compose.yml updated and PostgreSQL 16 container starts
- [ ] **Task 4:** Story 1-1 ACs updated (AC #2 and #4)
- [ ] **Task 5:** genapp-backend/README.md updated comprehensively
- [ ] **Task 6:** target-architecture.md tech stack table updated (3 rows)
- [ ] **Task 7:** Architecture Dockerfile example updated

### Quality Assurance
- [ ] All 8 Acceptance Criteria for Story 1-1 still pass
- [ ] Pre-commit hooks execute successfully (no formatting issues)
- [ ] CI/CD pipeline (GitHub Actions) passes all checks
- [ ] No deprecation warnings in build output
- [ ] Code review: All changes follow naming conventions and style guide
- [ ] Architecture reviewer: Approves target-architecture.md updates

### Handoff & Merge
- [ ] All tasks completed and tested locally
- [ ] Pull Request created (or updated if already exists)
- [ ] PR description references Sprint Change Proposal document
- [ ] Code review approved by architecture team
- [ ] Tests passing in CI/CD pipeline
- [ ] Ready to merge to main branch

---

## Testing Requirements

### Local Verification Commands

```bash
# 1. Verify Java version
java -version
# Expected: openjdk version "21.x.x" LTS

# 2. Build project
cd genapp-backend
mvn clean install
# Expected: BUILD SUCCESS

# 3. Verify pom.xml settings
grep -A2 "<maven.compiler" pom.xml
# Expected: source and target both "21"

# 4. Build Docker image
docker build -t cicsgenapp:test .
# Expected: Successfully built...

# 5. Verify Docker image Java version
docker run --rm cicsgenapp:test java -version
# Expected: openjdk version "21.x.x" LTS

# 6. Start local environment
docker-compose up -d
# Expected: PostgreSQL 16 starts successfully

# 7. Verify PostgreSQL version
docker ps  # Get container ID
docker exec <container-id> psql --version
# Expected: psql (PostgreSQL) 16.x

# 8. Run tests
mvn test
# Expected: All tests pass
```

### CI/CD Validation

The GitHub Actions workflow will automatically:
- [ ] Build with Java 21
- [ ] Run unit tests
- [ ] Execute integration tests with TestContainers
- [ ] Build Docker image
- [ ] Scan for vulnerabilities
- [ ] Check code quality (Checkstyle, SpotBugs)

---

## Rollback Plan (if needed)

If any task fails or issues are discovered:

1. **Revert Changes:**
   ```bash
   git checkout -- .
   git clean -fd
   ```

2. **Diagnostic Steps:**
   - Check Java version matches system installation
   - Verify Maven can resolve dependencies
   - Check Docker daemon is running
   - Review CI/CD logs for specific errors

3. **Escalation:**
   - If Java 21 compatibility issue found: Create issue and document in Sprint Change Proposal
   - If PostgreSQL 16 migration issue: Review container logs
   - If build failures: Check Maven dependency tree for conflicts

---

## Sign-Off

**Implementation Owner:** [Developer assigned to Story 1-1]

**Expected Completion:** Before Story 1-1 merge to main

**Questions/Blockers:** Reference Sprint Change Proposal document or escalate to Scrum Master

