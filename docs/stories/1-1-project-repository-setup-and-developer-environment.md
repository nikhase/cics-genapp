# Story 1.1: Project Repository Setup and Developer Environment

Status: ready-for-dev

## Story

As a Development Team Lead,
I want to establish a standardized GitHub repository structure with build tooling configuration,
So that all developers have a consistent starting point and can quickly run local builds.

## Acceptance Criteria

1. `genapp-backend/` subfolder created with clear folder structure (src/, tests/, config/, helm/)
2. Maven pom.xml configured in `genapp-backend/` with Spring Boot 3.3+ LTS, all required dependencies (Spring Web, Data JPA, Security, Cloud Gateway, Actuator, etc.)
3. .gitignore configured in `genapp-backend/` for Maven/IntelliJ/VS Code/Docker artifacts
4. README.md created in `genapp-backend/` with developer setup instructions (Java 17+, Maven 3.8+, Docker, PostgreSQL, mvn clean install)
5. Local development can run from `genapp-backend/`: `mvn clean install && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"`
6. Git workflow documentation updated in root repository (branch naming: feature/*, bugfix/*, main is protected, PR process)
7. Java code style configuration (Google Style Guide via Checkstyle, SpotBugs) integrated into genapp-backend/ build
8. Pre-commit hooks configured to prevent unformatted code commits in genapp-backend/

## Tasks / Subtasks

- [ ] Task 1: Create genapp-backend/ subfolder and initial folder structure (AC: #1)
  - [ ] Create genapp-backend/ directory in repository root
  - [ ] Initialize genapp-backend/ with .gitignore, README.md, LICENSE
  - [ ] Create src/, tests/, config/, helm/ subdirectories inside genapp-backend/
  - [ ] Create CONTRIBUTING.md with developer guidelines
- [ ] Task 2: Configure Maven pom.xml with Spring Boot 3.3+ (AC: #2)
  - [ ] Create pom.xml in genapp-backend/ root
  - [ ] Set Java version to 17, Spring Boot version to 3.3+
  - [ ] Add dependencies: spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-security, spring-cloud-starter-gateway, spring-boot-starter-actuator
  - [ ] Add testing dependencies: spring-boot-starter-test, testcontainers
  - [ ] Configure maven-jar-plugin for fat JAR packaging
- [ ] Task 3: Configure .gitignore in genapp-backend/ (AC: #3)
  - [ ] Create .gitignore in genapp-backend/
  - [ ] Add Maven patterns (target/, .m2/)
  - [ ] Add IntelliJ patterns (.idea/, *.iml)
  - [ ] Add VS Code patterns (.vscode/, .settings/)
  - [ ] Add Docker patterns (local Dockerfile overrides if needed)
  - [ ] Add OS patterns (.DS_Store, Thumbs.db)
- [ ] Task 4: Write comprehensive README.md in genapp-backend/ (AC: #4)
  - [ ] Create genapp-backend/README.md
  - [ ] Project overview: Spring Boot backend for CICS GenApp modernization
  - [ ] Prerequisites: Java 17+, Maven 3.8+, Docker, PostgreSQL
  - [ ] Local setup steps: cd genapp-backend/, mvn clean install, mvn spring-boot:run
  - [ ] Development profile documentation (dev, test, prod)
  - [ ] Troubleshooting section
- [ ] Task 5: Configure Git workflow and protection rules (AC: #6)
  - [ ] Create or update WORKFLOW.md in repository root
  - [ ] Document branch naming conventions (feature/*, bugfix/*, hotfix/*)
  - [ ] Document PR process: feature branch → PR → review → merge to main
  - [ ] Set main branch to require PR reviews before merge
  - [ ] Configure branch protection to require tests passing
- [ ] Task 6: Configure Checkstyle and SpotBugs for code quality in genapp-backend/ (AC: #7)
  - [ ] Add Google Style Guide via Checkstyle Maven plugin in pom.xml
  - [ ] Configure pom.xml to run checkstyle during build
  - [ ] Add SpotBugs plugin for static analysis
  - [ ] Create genapp-backend/checkstyle.xml configuration file
- [ ] Task 7: Configure pre-commit hooks (AC: #8)
  - [ ] Create or update .git/hooks/pre-commit script in repository root
  - [ ] Hook runs: mvn -f genapp-backend/pom.xml spotless:check
  - [ ] Prevent commit if formatting issues found
  - [ ] Document hook setup in genapp-backend/README.md
- [ ] Task 8: Verify local build and run (AC: #5)
  - [ ] cd genapp-backend/ && mvn clean install and confirm success
  - [ ] Run mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
  - [ ] Verify application starts on port 8080
  - [ ] Test basic endpoints (e.g., GET /actuator/health)

## Dev Notes

### Architecture Context

This story establishes the foundational project structure that all subsequent stories will build upon. The project follows a standard Spring Boot Maven structure with clear separation of concerns:

- **src/main/java/** - Application code
- **src/test/java/** - Unit and integration tests
- **src/main/resources/** - Configuration files (application.yml, etc.)
- **docs/** - Architecture documentation, ADRs
- **config/** - Kubernetes, Helm, Docker configurations
- **helm/** - Helm charts for cloud deployment

**Key Architectural Decisions:**
- **Spring Boot 3.3+ LTS:** Latest long-term support release for stability
- **Maven:** Industry standard for Java projects; excellent dependency management
- **Git on GitHub:** Standard distributed version control; integrates with CI/CD
- **Code Quality Tools:** Checkstyle + SpotBugs prevent technical debt from day one
- **Pre-commit Hooks:** Catch formatting issues before they enter the repo

### Project Structure Notes

Repository layout with genapp-backend/ as the Spring Boot modernization subfolder:

```
/workspace/ (repository root)
├── base/                          # Legacy COBOL/CICS system (existing)
├── genapp-backend/                # NEW: Spring Boot backend (this story)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/cicsgenapp/
│   │   │   │   ├── api/                 # REST controllers
│   │   │   │   ├── service/             # Business logic
│   │   │   │   ├── repository/          # Data access
│   │   │   │   ├── model/               # Domain entities/DTOs
│   │   │   │   ├── config/              # Spring configuration
│   │   │   │   ├── exception/           # Custom exceptions
│   │   │   │   └── CicsGenAppApplication.java
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       ├── application-test.yml
│   │   │       └── application-prod.yml
│   │   └── test/
│   │       ├── java/com/example/cicsgenapp/  # Test classes
│   │       └── resources/
│   ├── config/
│   │   ├── docker/
│   │   └── kubernetes/
│   ├── helm/
│   │   └── cics-genapp/
│   ├── pom.xml                    # Maven configuration
│   ├── README.md
│   ├── CONTRIBUTING.md
│   ├── .gitignore
│   ├── Dockerfile
│   └── docker-compose.yml         # Local dev with PostgreSQL
├── genapp-frontend/               # Future: React frontend
├── docs/                          # Unified documentation (existing)
├── bmad/                          # Modernization framework (existing)
├── .github/
│   └── workflows/                 # Root-level CI/CD pipelines
└── WORKFLOW.md                    # Git workflow documentation
```

**Key Structure Points:**
- `genapp-backend/` contains all Spring Boot code and configuration
- Legacy COBOL system remains untouched in `base/`
- Unified `docs/` serves both systems (PRD, epics, stories, architecture)
- CI/CD pipelines at root `.github/workflows/` can orchestrate both systems
- Each subfolder has its own pom.xml, .gitignore, README.md

### Constraints & Requirements

- **Java 17+:** Required for Spring Boot 3.3+ (Java 17 is LTS with long support window)
- **Maven 3.8+:** Required for security improvements and dependency resolution
- **Git on GitHub:** Standard for team collaboration and CI/CD integration
- **Code Style:** Google Style Guide ensures consistency across team
- **Pre-commit Hooks:** Prevents unformatted code from being committed; improves review efficiency

### Testing Standards Summary

This story doesn't require extensive testing (setup/configuration), but subsequent stories will follow:
- Unit tests (80%+ coverage target) using JUnit 5 + Mockito
- Integration tests using TestContainers for database testing
- API tests using MockMvc for controller testing

### References

- [Spring Boot 3.3+ Documentation](https://spring.io/projects/spring-boot)
- [Maven Official Documentation](https://maven.apache.org/)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [GitHub Repository Best Practices](https://docs.github.com/en/repositories/creating-and-managing-repositories)

## Dev Agent Record

### Context Reference

- docs/stories/1-1-project-repository-setup-and-developer-environment.context.xml

### Agent Model Used

Claude 3 Haiku

### Debug Log References

### Completion Notes List

### File List

## Change Log

- **2025-11-01:** Story adapted for genapp-backend/ subfolder structure in monorepo (updated ACs, tasks, project structure diagram)
