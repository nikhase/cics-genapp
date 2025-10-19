# GenApp Modernization Roadmap: 2-Week Sprint

## 📋 Overview

This document outlines the complete 2-week sprint plan to modernize GenApp from CICS/COBOL to Spring Boot 3 + Java 21 + React + PostgreSQL.

**Timeline**: 14 days
**Progress**: Week 1 Foundation Complete (40%), Week 2 Polish & Integration (60%)
**MVP Features**: Customer CRUD + Motor Policy CRUD + React UI

---

## 📊 Week 1: Foundation (Days 1-7) ✅ COMPLETE

### Days 1-2: Infrastructure Setup ✅
- ✅ Maven pom.xml with Java 21 + Spring Boot 3.2.3
- ✅ Spring Boot application class with CORS config
- ✅ application.yml with PostgreSQL, JPA, Flyway, Swagger
- ✅ Docker Compose multi-service setup (PostgreSQL, Backend, Frontend)
- ✅ Backend Dockerfile with multi-stage build

### Days 3-4: Database Design ✅
- ✅ V1: Customer table (replaces KSDSCUST VSAM)
- ✅ V2: Policy tables with discriminator pattern (replaces KSDSPOLY + Db2 tables)
- ✅ Flyway migrations with triggers and indexes
- ✅ PostgreSQL SEQUENCE for auto-increment IDs

### Days 5-7: Customer Domain Implementation ✅
- ✅ Customer JPA entity with validation
- ✅ CustomerDTO Java 21 record (immutable DTO)
- ✅ CustomerRepository (Spring Data JPA)
- ✅ CustomerService (business logic)
- ✅ CustomerController (REST endpoints: POST, GET, PUT, DELETE, GET all, search)
- ✅ GlobalExceptionHandler (centralized error handling)

**Result**: Fully functional Customer REST API ready for testing

---

## 🚀 Week 2: Polish, Testing & Frontend (Days 8-14)

### Day 8: Integration Tests ⏳
**Goal**: Ensure backend works end-to-end with real database

```
Tasks:
- Create @SpringBootTest configuration
- Write CustomerServiceTests with @DataJpaTest
- Use TestContainers for real PostgreSQL
- Test all CRUD operations
- Test exception handling
- Test validation errors
```

**Deliverable**: `CustomerServiceTest.java` with 100% coverage

---

### Day 9: Swagger UI Configuration ⏳
**Goal**: Auto-generate interactive API documentation

```
Tasks:
- Configure SpringDoc OpenAPI
- Add @Operation and @ApiResponse annotations (already done)
- Customize Swagger UI styling
- Test API endpoints via Swagger UI
- Document request/response examples
```

**Result**: Swagger UI at `http://localhost:8080/swagger-ui.html`

---

### Days 10-11: Policy Domain (Motor Insurance) ⏳
**Goal**: Implement Motor Policy with sealed class hierarchy

```
Tasks:
- Create Policy sealed class (parent)
- Create MotorPolicy, HousPolicy, EndowmentPolicy, CommercialPolicy classes
- PolicyRepository with custom queries
- PolicyService (CRUD + search)
- PolicyController (4 endpoints: POST, GET, PUT, DELETE)
- Relationship validation (policy.customerId must exist)

Database Constraints:
- Foreign key from policies.customer_id → customers.customer_id
- Check constraint for policy_type IN ('C', 'E', 'H', 'M')
- Unique constraint on (policy_number, customer_id)
```

**Deliverable**: Full motor policy CRUD API

---

### Days 12-13: React Frontend ⏳
**Goal**: Build modern React UI for customer and policy management

```
Frontend Structure:
frontend/
├── src/
│   ├── components/
│   │   ├── CustomerList.tsx
│   │   ├── CustomerForm.tsx (add/edit)
│   │   ├── PolicyList.tsx
│   │   └── PolicyForm.tsx (motor policy)
│   ├── api/
│   │   └── client.ts (Axios with base URL)
│   ├── providers/
│   │   └── AppProviders.tsx (context, toast, theme)
│   ├── App.tsx (router setup)
│   └── main.tsx
├── vite.config.ts
├── package.json
├── Dockerfile
└── index.html

Task Breakdown (Day 12):
1. Initialize React project with Vite + TypeScript
2. Install dependencies (React Router, Axios, UI library)
3. Create API client with error handling
4. Build CustomerList component (table with CRUD buttons)
5. Build CustomerForm component (add/edit modal)

Task Breakdown (Day 13):
6. Build PolicyList component (motor policies)
7. Build PolicyForm component (add/edit motor policy)
8. Add React Router for navigation
9. Error handling and validation messages
10. Create Dockerfile for frontend (Node multi-stage build)
```

**Result**: Fully functional React SPA

---

### Day 14: Integration & Final Testing ⏳
**Goal**: End-to-end system testing and documentation

```
Tasks:
1. Update docker-compose.yml with all services
2. Test full stack locally:
   - Start: docker-compose up -d
   - Verify PostgreSQL is healthy
   - Verify backend startup and migrations run
   - Verify frontend loads at localhost:3000
   - Create a customer via React UI
   - Create a motor policy for that customer
   - View customer list with all policies
   - Update customer information
   - Delete a policy
3. Test error scenarios:
   - Invalid email format
   - Duplicate email
   - Non-existent customer ID
   - Missing required fields
4. Performance testing:
   - Load test with 100 concurrent users
   - Database query optimization
   - Response time < 200ms
5. Documentation:
   - Write README.md with setup instructions
   - API endpoint documentation
   - Architecture diagram comparison (old vs new)
   - Deployment instructions
6. Clean up and final verification
```

**Result**: Production-ready application

---

## 📁 File Structure (Week 2 Additions)

```
genapp-claude/
├── backend/
│   ├── src/main/java/com/genapp/
│   │   ├── model/
│   │   │   ├── Customer.java ✅
│   │   │   ├── Policy.java                    (NEW - sealed)
│   │   │   ├── MotorPolicy.java              (NEW)
│   │   │   ├── HousePolicy.java              (NEW)
│   │   │   ├── EndowmentPolicy.java          (NEW)
│   │   │   └── CommercialPolicy.java         (NEW)
│   │   ├── dto/
│   │   │   ├── CustomerDTO.java ✅
│   │   │   ├── PolicyDTO.java                (NEW)
│   │   │   ├── MotorPolicyDTO.java           (NEW)
│   │   │   └── (other policy DTOs)           (NEW)
│   │   ├── repository/
│   │   │   ├── CustomerRepository.java ✅
│   │   │   └── PolicyRepository.java         (NEW)
│   │   ├── service/
│   │   │   ├── CustomerService.java ✅
│   │   │   └── PolicyService.java            (NEW)
│   │   ├── controller/
│   │   │   ├── CustomerController.java ✅
│   │   │   └── PolicyController.java         (NEW)
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java ✅
│   ├── src/test/java/com/genapp/
│   │   ├── service/
│   │   │   ├── CustomerServiceTest.java      (NEW)
│   │   │   └── PolicyServiceTest.java        (NEW)
│   │   └── controller/
│   │       ├── CustomerControllerTest.java   (NEW)
│   │       └── PolicyControllerTest.java     (NEW)
│   └── pom.xml ✅
│
├── frontend/                                  (NEW directory)
│   ├── src/
│   │   ├── components/
│   │   │   ├── CustomerList.tsx
│   │   │   ├── CustomerForm.tsx
│   │   │   ├── PolicyList.tsx
│   │   │   └── PolicyForm.tsx
│   │   ├── api/
│   │   │   └── client.ts
│   │   ├── providers/
│   │   │   └── AppProviders.tsx
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   └── index.css
│   ├── public/
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── package.json
│   ├── Dockerfile
│   └── index.html
│
├── docker-compose.yml ✅
├── ROADMAP.md (this file) ✅
├── README.md                                  (NEW - with setup instructions)
└── ARCHITECTURE.md                            (NEW - comparison diagram)
```

---

## 🎯 Success Criteria

### Week 1 ✅
- [x] Maven project compiles without errors
- [x] PostgreSQL schema created via Flyway
- [x] Customer REST API functional (6 endpoints)
- [x] Docker Compose launches all services
- [x] Swagger UI accessible

### Week 2 ⏳
- [ ] 80%+ test coverage for services
- [ ] All endpoints return proper HTTP status codes
- [ ] React UI fully functional (customer + policy CRUD)
- [ ] End-to-end tests pass
- [ ] Full documentation complete
- [ ] Zero unhandled exceptions
- [ ] Response time < 200ms for all endpoints

---

## 💾 Technology Stack

### Backend
- **Java 21 LTS** - Latest long-term support version
- **Spring Boot 3.2.3** - Latest stable release
- **Maven** - Build tool and dependency management
- **PostgreSQL 16** - Modern relational database
- **JPA/Hibernate** - Object-relational mapping
- **Flyway** - Database schema versioning
- **SpringDoc OpenAPI** - API documentation
- **TestContainers** - Integration testing with real DB
- **JUnit 5 + Mockito** - Unit testing

### Frontend
- **React 18** - Latest stable UI library
- **TypeScript** - Type-safe JavaScript
- **Vite** - Ultra-fast build tool
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **Tailwind CSS** or **Material-UI** - Styling

### Infrastructure
- **Docker** - Containerization
- **Docker Compose** - Multi-service orchestration
- **PostgreSQL 16 Alpine** - Lightweight DB image

---

## 📊 COBOL → Java Mapping Reference

| COBOL Component | Purpose | New Component | Benefits |
|---|---|---|---|
| **Programs** | Transaction handlers | REST Controllers | HTTP standard, language-agnostic |
| **COMMAREA** | Inter-program data passing | DTO Records | Type-safe, self-documenting, 10x smaller |
| **EXEC CICS LINK** | Program-to-program calls | Dependency Injection | Cleaner, testable, no coupling |
| **VSAM files** | Indexed sequential data | PostgreSQL tables | ACID compliance, complex queries |
| **Db2 tables** | Relational data | PostgreSQL tables | Single source of truth (no dual-write) |
| **RESP codes** | Error handling | Exceptions + HTTP status | Consistent, automatic propagation |
| **3270 screens** | Terminal UI | React components | Modern, responsive, cross-platform |
| **BMS mapsets** | Screen formatting | React components | Dynamic, accessible, themeable |
| **Manual validation** | Data checking | Validation annotations | Declarative, automatic at API boundary |
| **Sequential file reads** | Data retrieval | JPA repository queries | Indexed, optimized, pagination support |

---

## 🔄 Daily Standup Template

```
Day X:

Completed:
- ✅ Task 1
- ✅ Task 2

In Progress:
- ⏳ Task 3

Blockers:
- ❌ None / Describe blocker

Tomorrow:
- Task 4
- Task 5
```

---

## 📈 Progress Tracking

```
Week 1 Completed: ████████████░░░░░░░░ 40%
├── Infrastructure ✅ 100%
├── Database ✅ 100%
├── Backend API ✅ 100%
├── Testing ⏳ 0%
└── Frontend ⏳ 0%

Week 2 Planning: ░░░░░░░░░░░░░░░░░░░░ 0%
├── Testing ⏳ 0%
├── Policy Domain ⏳ 0%
├── Frontend ⏳ 0%
└── Integration ⏳ 0%
```

---

## 🚀 Getting Started

### Start Week 1 (Foundation)
```bash
cd genapp-claude
docker-compose up -d
# Wait for PostgreSQL to be healthy
# Backend will automatically run Flyway migrations
# Navigate to http://localhost:8080/swagger-ui.html
```

### Start Week 2 (Polish)
```bash
# Run tests
cd backend
mvn test

# Build frontend
cd ../frontend
npm install
npm run dev

# Full stack
cd ..
docker-compose down
docker-compose up -d
```

---

## 📚 References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/)
- [Java 21 Features](https://openjdk.org/projects/jdk/21/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [React Documentation](https://react.dev)
- [C4 Model](https://c4model.com/)
- [REST API Best Practices](https://restfulapi.net/)

---

## 🎓 Learning Outcomes

After completing this 2-week sprint, you will understand:

- ✅ Spring Boot application architecture
- ✅ Java 21 modern features (Records, Sealed Classes, Pattern Matching)
- ✅ REST API design and HTTP best practices
- ✅ JPA/Hibernate ORM patterns
- ✅ PostgreSQL database design and optimization
- ✅ React component architecture and hooks
- ✅ Docker containerization and orchestration
- ✅ End-to-end testing with TestContainers
- ✅ API documentation with Swagger/OpenAPI
- ✅ COBOL to Java modernization patterns

---

## 📝 Notes

- This is a **learning project** - focus on understanding patterns, not just shipping features
- **Code comments** are provided to explain COBOL → Java mappings
- **Git commits** track progress and serve as learning checkpoints
- **Docker** simplifies local development - no manual database setup needed
- **Swagger UI** provides interactive API testing
- **React** shows modern frontend patterns complementing backend

---

**Created**: 2025-10-18
**Last Updated**: 2025-10-18
**Status**: Week 1 Complete, Week 2 Ready to Start
**Next Action**: Day 8 - Integration Tests
