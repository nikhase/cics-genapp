# Contributing to CICS GenApp Backend

Thank you for your interest in contributing to CICS GenApp Backend! This document provides guidelines and instructions for developers.

## Developer Guidelines

### Code Style

We follow the **Google Java Style Guide** enforced via Checkstyle integration. Code that doesn't conform to this standard will fail the build.

- **Naming conventions:** Use descriptive names for classes, methods, and variables
- **Formatting:** Let your IDE or Maven formatting tools handle indentation
- **Documentation:** Include Javadoc comments for public classes and methods
- **Line length:** Aim for 120 characters max per line

### Prerequisites

- **Java 17+** (LTS recommended)
- **Maven 3.8+**
- **Git**
- **Docker** (for local PostgreSQL testing)
- **Your preferred IDE** (IntelliJ IDEA recommended)

### Setup for Local Development

```bash
# Clone the repository
git clone https://github.com/yourorg/cics-genapp.git
cd cics-genapp/genapp-backend

# Build the project
mvn clean install

# Run tests
mvn test

# Run the application with dev profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Before Committing

1. **Run code quality checks:**
   ```bash
   mvn checkstyle:check
   mvn spotbugs:check
   ```

2. **Run all tests:**
   ```bash
   mvn test
   ```

3. **Format your code** (pre-commit hooks will enforce this):
   ```bash
   mvn spotless:apply
   ```

The repository includes pre-commit hooks that will prevent you from committing unformatted code. These hooks run automatically before each commit.

### Git Workflow

1. Create a feature branch from `main`:
   ```bash
   git checkout -b feature/my-feature-name
   ```

2. Commit your changes with clear, descriptive messages:
   ```bash
   git commit -m "Add feature: Brief description of what was implemented"
   ```

3. Push your branch to the remote:
   ```bash
   git push origin feature/my-feature-name
   ```

4. Open a Pull Request on GitHub with:
   - Clear title describing the changes
   - Reference to any related issues
   - Summary of what was changed and why

5. Ensure all CI/CD checks pass before requesting review

### Code Review Process

- At least one review approval required before merge
- CI/CD pipeline must pass all checks
- Address reviewer feedback with additional commits (do not force push)
- Keep discussions professional and constructive

### Testing Requirements

- **Unit tests:** Aim for 80%+ code coverage of business logic
- **Integration tests:** Use TestContainers for database testing
- **API tests:** Use MockMvc for controller endpoint testing
- All tests must pass before submitting a PR

### Testing Tools

- **Unit Testing:** JUnit 5 + Mockito
- **Integration Testing:** TestContainers (PostgreSQL)
- **API Testing:** Spring MockMvc
- **Code Coverage:** JaCoCo

### Reporting Issues

When reporting bugs or suggesting features:

1. Use GitHub Issues with clear, descriptive titles
2. Provide steps to reproduce (for bugs)
3. Include expected vs. actual behavior
4. Attach relevant logs or screenshots if applicable

### Questions?

Feel free to:
- Ask questions in GitHub Discussions
- Reach out to the team lead
- Reference the architecture documentation in `docs/`

## Acknowledgments

Thank you for helping improve CICS GenApp Backend!
