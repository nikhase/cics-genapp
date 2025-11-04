# CICS GenApp Backend

Spring Boot backend for CICS GenApp modernization - A cloud-native microservices platform for modernizing legacy IBM CICS transaction processing systems.

## Project Overview

CICS GenApp Backend is a Spring Boot 3.3+ REST API designed to gradually replace the legacy COBOL/CICS system with modern cloud-native services. The system provides:

- **RESTful APIs** for Customer Management, Policy Management, and legacy system integration
- **Spring Cloud Gateway** for API routing and traffic management
- **Cloud-ready Architecture** with containerization and Kubernetes support
- **Enterprise Security** with OIDC authentication (Zitadel)
- **Observability** with structured logging, metrics, and tracing
- **Parallel Run Support** for safe migration from legacy CICS system

## Technology Stack

- **Language:** Java 21 LTS
- **Framework:** Spring Boot 3.3+ LTS
- **API:** Spring Web (REST)
- **UI Framework:** Vaadin 24.5.0 (Server-side Java web framework)
- **Data Access:** Spring Data JPA
- **Security:** Spring Security (Form-based auth for MVP, OIDC deferred)
- **Gateway:** Spring Cloud Gateway
- **Database:** PostgreSQL 16 LTS
- **Build Tool:** Maven 3.8+ (single build process for backend + frontend)
- **Container:** Docker
- **Orchestration:** Kubernetes (Helm charts included)

**Note:** The application uses Vaadin for the frontend instead of React/TypeScript. This provides:
- Server-side rendering with Java-only development
- No separate Node.js build process
- Built-in UI components and theming
- Seamless integration with Spring Security for session-based authentication

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21 LTS** (Required)
  - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use [Eclipse Temurin](https://adoptium.net/)
  - Verify: `java -version` (should show 21.x.x)
  - Java 21 LTS provides long-term support and is required for Spring Boot 3.3+ compatibility

- **Maven 3.8+**
  - Download from [Maven Official](https://maven.apache.org/download.cgi)
  - Verify: `mvn -version`

- **Docker** (for local development and database)
  - Download from [Docker Desktop](https://www.docker.com/products/docker-desktop)
  - Verify: `docker --version`

- **Git**
  - Download from [Git Official](https://git-scm.com/)
  - Verify: `git --version`

- **PostgreSQL 16 LTS** (via Docker or local installation)
  - Database server for application data - PostgreSQL 16 LTS provides long-term support
  - We recommend using Docker for consistency: `docker compose up`

## Local Setup Steps

### 1. Clone the Repository

```bash
git clone https://github.com/yourorg/cics-genapp.git
cd cics-genapp/genapp-backend
```

### 2. Start PostgreSQL (via Docker)

```bash
# Using Docker Compose
docker-compose up -d postgresql

# Or using Docker directly
docker run --name cics-genapp-postgres \
  -e POSTGRES_USER=genapp \
  -e POSTGRES_PASSWORD=genapp123 \
  -e POSTGRES_DB=cicsgenapp \
  -p 5432:5432 \
  -d postgres:16-alpine
```

Verify PostgreSQL is running:
```bash
docker ps | grep postgres
```

### 3. Build the Project

```bash
cd genapp-backend
mvn clean install
```

This command will:
- Download dependencies
- Compile source code
- Run all tests
- Run Checkstyle code quality checks
- Run SpotBugs static analysis
- Package the application as a FAT JAR

### 4. Run the Application

```bash
# Development profile (local database, debug enabled)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Or run the packaged JAR
java -jar target/genapp-backend-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

The application will start on `http://localhost:8080`

### 5. Verify Application is Running

```bash
# Health check endpoint
curl http://localhost:8080/actuator/health

# Response should be:
# {"status":"UP"}
```

## Development Profiles

The application supports multiple Spring profiles for different environments:

### Development Profile (`dev`)
- **Activation:** `--spring.profiles.active=dev`
- **Configuration file:** `src/main/resources/application-dev.yml`
- **Features:**
  - Local PostgreSQL database
  - Debug logging enabled
  - Security relaxed for local testing
  - Mock authentication disabled
  - Hot reload enabled

### Test Profile (`test`)
- **Activation:** `--spring.profiles.active=test`
- **Configuration file:** `src/main/resources/application-test.yml`
- **Features:**
  - In-memory H2 database (for testing)
  - Test data pre-loaded
  - Security enabled for testing
  - Used by Maven tests automatically

### Production Profile (`prod`)
- **Activation:** `--spring.profiles.active=prod`
- **Configuration file:** `src/main/resources/application-prod.yml`
- **Features:**
  - Full security enabled
  - OIDC authentication required
  - Structured logging
  - Performance optimization
  - Used in Docker/Kubernetes

## Development Workflow

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CustomerServiceTests

# Run tests with coverage report
mvn test jacoco:report
```

### Code Quality Checks

```bash
# Run Checkstyle (Google Style Guide)
mvn checkstyle:check

# Run SpotBugs (static analysis)
mvn spotbugs:check

# Both are automatically run during build, but can be checked independently
```

### Code Formatting

```bash
# Format code according to style guide
mvn spotless:apply

# Check formatting without applying changes
mvn spotless:check
```

### Pre-commit Hooks

The repository includes automatic pre-commit hooks that ensure code formatting compliance before commits. If formatting issues are detected, the commit will be blocked:

```
# Pre-commit hook will run:
mvn spotless:check

# If it fails, run spotless:apply to fix formatting issues
mvn spotless:apply
git add .
git commit -m "your message"
```

## Project Structure

```
genapp-backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/cicsgenapp/
│   │   │   ├── api/                 # REST controllers (endpoints)
│   │   │   ├── service/             # Business logic and services
│   │   │   ├── repository/          # Data access (JPA repositories)
│   │   │   ├── model/               # Domain entities and DTOs
│   │   │   ├── config/              # Spring configuration classes
│   │   │   ├── exception/           # Custom exception classes
│   │   │   └── CicsGenAppApplication.java  # Main application class
│   │   └── resources/
│   │       ├── application.yml      # Default configuration
│   │       ├── application-dev.yml  # Development configuration
│   │       ├── application-test.yml # Test configuration
│   │       ├── application-prod.yml # Production configuration
│   │       └── db/migration/        # Flyway database migrations
│   └── test/
│       ├── java/com/example/cicsgenapp/  # Unit and integration tests
│       └── resources/
│           └── application-test.yml # Test configuration
├── config/
│   ├── docker/                      # Docker configuration
│   └── kubernetes/                  # Kubernetes manifests
├── helm/
│   └── cics-genapp/                 # Helm chart for Kubernetes deployment
├── pom.xml                          # Maven configuration
├── checkstyle.xml                   # Code style configuration
├── README.md                        # This file
├── CONTRIBUTING.md                  # Developer guidelines
├── .gitignore                       # Git ignore rules
├── Dockerfile                       # Docker image build
└── docker-compose.yml               # Docker Compose for local dev
```

## API Gateway & Routing

The application uses **Spring Cloud Gateway** to implement the strangler pattern, allowing gradual migration from legacy COBOL system to Spring Boot services.

### Key Features

- **Request Routing:** Routes requests to either new Spring Boot services or legacy COBOL system
- **Circuit Breaker:** Resilience4j protection against cascading failures
- **Correlation IDs:** X-Trace-Id header for distributed tracing and debugging
- **CORS Management:** Configurable CORS policies per environment
- **Structured Logging:** JSON-formatted logs with correlation IDs for ELK Stack integration

### Gateway Documentation

For comprehensive gateway configuration and troubleshooting:

**See [`GATEWAY.md`](./GATEWAY.md)** - Contains:
- Route definitions for all endpoints
- Circuit breaker configuration and state transitions
- CORS settings per environment
- Trace ID propagation flow
- Timeout handling
- Adding new routes
- Troubleshooting common issues

### Quick Reference

**Switch between Spring Boot and Legacy:**
```bash
# Feature toggles control routing (Story 1.6)
# For now, all requests route to Spring Boot service
```

**Check Circuit Breaker State:**
```bash
curl http://localhost:8080/actuator/metrics/resilience4j.circuitbreaker.state
```

**Example: Request with Trace ID**
```bash
curl http://localhost:8080/api/v1/customers/123 \
  -H "X-Trace-Id: my-trace-id-123"
# Response includes: X-Trace-Id: my-trace-id-123
```

---

## Vaadin Web UI (Frontend)

The application includes a server-side Vaadin web interface for customer service agent interaction:

### Accessing the UI

- **Web Interface:** `http://localhost:8080`
- **Login Page:** `http://localhost:8080/login`

### MVP Authentication

For development and MVP testing, use these hardcoded credentials:

```
Username: admin    Password: admin123   (Full access)
Username: user     Password: user123    (Limited access)
```

**Note:** These are for MVP development only. Story 3.2 will implement proper form-based authentication with database-backed users.

### Vaadin Features

- **Server-Side Rendering:** Eliminates complex frontend build tools (no Node.js, Webpack, or TypeScript)
- **Built-in UI Components:** Vaadin provides production-ready components (Grid, Form, AppLayout, etc.)
- **Session-Based Authentication:** Spring Security integration for stateful authentication
- **Responsive Design:** Auto-responsive layouts for mobile, tablet, and desktop
- **Lumo Design System:** Professional theming with light/dark mode support

### Project Structure (UI)

```
genapp-backend/src/main/java/com/example/cicsgenapp/ui/
├── views/                    # @Route pages for routes
│   ├── LoginView.java        # Form-based login (Story 3.2)
│   ├── DashboardView.java    # Home page after login (Story 3.3)
│   ├── customers/
│   │   ├── CustomerSearchView.java    # Customer search/list (Story 3.4)
│   │   ├── CustomerDetailView.java    # Customer details (Story 3.5)
│   │   └── CustomerFormView.java      # Create/Edit customer (Story 3.6)
│   └── policies/
│       ├── PolicyListView.java        # Policy search/list (Story 3.7)
│       └── PolicyDetailView.java      # Policy details (Story 3.8)
├── layouts/
│   ├── MainLayout.java       # AppLayout with navigation (Story 3.1)
│   └── NavigationMenu.java   # Shared navigation components
└── components/               # Reusable Vaadin components
    ├── CustomerGrid.java
    ├── PolicyGrid.java
    └── SearchForm.java
```

### Why Vaadin (Not React/TypeScript)?

The decision to use Vaadin instead of React/TypeScript was made to:

1. **Simplify Frontend Development:** Single Maven build process (no separate Node.js frontend)
2. **Improve Developer Productivity:** Java-only development; no TypeScript learning curve
3. **Faster MVP Delivery:** 2-2.5 weeks estimated (vs. 3-4 weeks with React)
4. **Server-Side Rendering:** Reduces complexity and client-side state management
5. **Built-in Features:** Professional UI components without additional libraries

### Building the Vaadin UI

The Vaadin frontend is built as part of the standard Maven build. No additional build steps required:

```bash
# Vaadin builds automatically with Maven
mvn clean install

# Or for development with auto-refresh
mvn spring-boot:run
```

The Vaadin application includes frontend resources that are compiled and packaged in the JAR.

---

## API Endpoints

Once the application is running, explore the API using Swagger/OpenAPI:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`
- **Health Check:** `http://localhost:8080/actuator/health`
- **Metrics:** `http://localhost:8080/actuator/metrics`
- **Gateway Metrics:** `http://localhost:8080/actuator/metrics/resilience4j.circuitbreaker.state`

## Database Setup

### Automatic Schema Creation

The application uses Flyway for database migrations. Migrations are automatically applied on startup:

```bash
# Migrations are in: src/main/resources/db/migration/
# Format: V1__Initial_schema.sql, V2__Add_customers_table.sql, etc.
```

### Test Data Seeding

The development database is automatically populated with realistic test customer data via Flyway migration `V7__seed_test_customers.sql`. This data includes:

- **18 test customer records** with diverse names, locations, and demographics
- **International names** (German, French, Italian, Spanish) for Unicode support testing
- **Realistic contact information** (email addresses using @example.com, valid E.164 phone numbers)
- **Multiple geographic locations** (Oregon cities + one Washington city) for regional testing
- **All customers marked ACTIVE** for testing active customer scenarios

#### Using Test Data

The test data is automatically loaded when the application starts. You can use it for:

1. **UI Testing:** Search, filter, and list customers in the Vaadin interface
2. **API Testing:** Test REST endpoints with real data via Postman or curl
3. **Pagination Testing:** Test with 18 records per page in various page sizes
4. **Search Functionality:** Test by name, email, location, status

#### Sample Test Queries

```sql
-- Find all test customers
SELECT COUNT(*) FROM customer WHERE email LIKE '%@example.com';
-- Result: 18 test customers

-- Search by last name
SELECT * FROM customer WHERE last_name = 'Smith' AND email LIKE '%@example.com';
-- Result: 2 customers (Jane Smith, John Smith)

-- Find active test customers
SELECT COUNT(*) FROM customer WHERE status = 'ACTIVE' AND email LIKE '%@example.com';
-- Result: 18 (all test customers)
```

#### Resetting Test Data

To reset test data during development:

```bash
# Option 1: Delete test data and restart (Flyway will re-seed)
DELETE FROM customer WHERE email LIKE '%@example.com';

# Then restart the application - Flyway will automatically re-seed on startup

# Option 2: Reset specific test customer
DELETE FROM customer WHERE customer_id = 'f47ac10b-58cc-4372-a567-0e02b2c3d479'::uuid;
```

**Note:** Do NOT use `flyway:clean` in production. It deletes all migrations and data!

#### Test Data Reference

For complete test data documentation including:
- Customer names, IDs, and contact information
- Demographic distribution
- Sample test queries
- Testing strategies

**See:** `../../docs/test-data.md`

### Manual Database Access

Connect to PostgreSQL:

```bash
# Using Docker
docker exec -it cics-genapp-postgres psql -U genapp -d cicsgenapp

# Or using local PostgreSQL client
psql -h localhost -U genapp -d cicsgenapp -W
```

## Troubleshooting

### Issue: `mvn clean install` fails with "Java version 17 not found"

**Solution:** Ensure Java 17+ is installed and `JAVA_HOME` environment variable is set:

```bash
# On macOS/Linux
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# On Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Verify
java -version
```

### Issue: PostgreSQL connection fails

**Solution:** Verify PostgreSQL is running:

```bash
# Check if Docker container is running
docker ps | grep postgres

# If not running, start it
docker-compose up -d postgresql

# Verify connection
psql -h localhost -U genapp -d cicsgenapp -W
```

### Issue: Port 8080 is already in use

**Solution:** Change the port in configuration:

```bash
# In application-dev.yml, change:
server:
  port: 8081  # or any available port

# Or run with different port:
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Issue: Code fails Checkstyle validation

**Solution:** Format code automatically:

```bash
# Apply Google Style Guide formatting
mvn spotless:apply

# Then verify
mvn checkstyle:check
```

## Documentation

- **Architecture Documentation:** See `docs/architecture/` in the repository root
- **API Documentation:** Available at `/swagger-ui.html` when app is running
- **Contributing Guidelines:** See `CONTRIBUTING.md`
- **Original COBOL System:** See `base/` folder and `docs/`

## Building Docker Image

```bash
# Build Docker image
docker build -t cics-genapp-backend:latest .

# Run Docker container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=host.docker.internal \
  -e DB_USER=genapp \
  -e DB_PASSWORD=genapp123 \
  cics-genapp-backend:latest
```

## Kubernetes Deployment

Helm charts are provided for Kubernetes deployment:

```bash
# Install the Helm chart
helm install cics-genapp helm/cics-genapp/

# Upgrade an existing installation
helm upgrade cics-genapp helm/cics-genapp/

# Verify deployment
kubectl get pods | grep cics-genapp
```

## Performance Considerations

- **Database Indexing:** Ensure frequently queried columns are indexed
- **Connection Pooling:** HikariCP is configured in pom.xml
- **Caching:** Use Spring Cache for frequently accessed data
- **Async Processing:** Use `@Async` for long-running operations

## Security Considerations

- **OIDC Authentication:** Production uses Zitadel for identity management
- **HTTPS:** Required in production environments
- **API Key Management:** Store credentials in environment variables or secrets vault
- **SQL Injection Prevention:** Always use parameterized queries (JPA handles this)
- **CORS Configuration:** Configure CORS policies in `config/SecurityConfig.java`

## Contributing

See `CONTRIBUTING.md` for detailed guidelines on:
- Code style requirements
- Git workflow and branch naming
- Pull request process
- Testing requirements
- Review process

## Support and Questions

- **Documentation:** Check `docs/` folder in repository root
- **Issues:** Report bugs via GitHub Issues
- **Discussions:** Use GitHub Discussions for questions
- **Team Lead:** Contact development team lead for guidance

## License

See `LICENSE` file in the repository root.

## Version History

- **1.0.0-SNAPSHOT:** Initial Spring Boot project setup
  - Maven configuration with Spring Boot 3.3+
  - Google Style Guide integration
  - Pre-commit hooks for code quality
  - Docker and Kubernetes configuration
  - Development environment setup

---

**Last Updated:** 2025-11-01
**Maintained by:** Development Team
