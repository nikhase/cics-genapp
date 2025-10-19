# GenApp Modernized - TODO & Progress Tracking

## 📊 Current Status: Week 2 (35% Complete)

**Date**: 2025-10-18
**Progress**: 17 files created, 10 test cases, 6 REST endpoints, Swagger UI configured

---

## ✅ Completed Tasks

### Week 1: Foundation (100% Complete)
- [x] Maven pom.xml with Java 21 + Spring Boot 3.2.3
- [x] PostgreSQL database schema (Customer + Policy tables)
- [x] Flyway migrations with triggers and indexes
- [x] Customer JPA entity with validation
- [x] CustomerDTO (Java 21 Record)
- [x] CustomerRepository (Spring Data JPA)
- [x] CustomerService (business logic)
- [x] CustomerController (6 endpoints: POST, GET, PUT, DELETE, GET all, search)
- [x] GlobalExceptionHandler (centralized error handling)
- [x] Docker Compose (PostgreSQL + Backend + Frontend)
- [x] ROADMAP.md (2-week sprint plan)

### Week 2: Days 8-9 (Completed)
- [x] CustomerServiceTest.java (9 unit tests with Mockito)
- [x] CustomerControllerIntegrationTest.java (10 integration tests with TestContainers)
- [x] SwaggerConfig.java (OpenAPI 3.0 configuration)
- [x] README.md (comprehensive documentation)

---

## ⏳ Pending Tasks (Days 10-14)

### Day 10-11: Policy Domain Implementation (HIGH PRIORITY)

**Policy Sealed Class Hierarchy:**
```bash
# Create files:
backend/src/main/java/com/genapp/model/Policy.java
  - Sealed class with 4 permitted subclasses
  - Base fields: policyId, policyNumber, customerId, startDate, endDate, premium, status

backend/src/main/java/com/genapp/model/MotorPolicy.java (sealed permits only)
  - vehicleMake, vehicleModel, vehicleYear, vehicleVin
  - usageType, annualMileage, driverAgeGroup, coverageType

backend/src/main/java/com/genapp/model/HousePolicy.java (sealed permits only)
  - propertyAddress, propertyType, constructionYear
  - squareFootage, replacementCost, deductible

backend/src/main/java/com/genapp/model/EndowmentPolicy.java (sealed permits only)
  - maturityDate, insuredAmount, bonusRate, investmentType

backend/src/main/java/com/genapp/model/CommercialPolicy.java (sealed permits only)
  - businessName, businessType, propertyAddress, annualRevenue
  - numEmployees, coverageLimit, deductible
```

**DTOs:**
```bash
# Create Java 21 Records:
backend/src/main/java/com/genapp/dto/PolicyDTO.java (base)
backend/src/main/java/com/genapp/dto/MotorPolicyDTO.java
backend/src/main/java/com/genapp/dto/HousePolicyDTO.java
backend/src/main/java/com/genapp/dto/EndowmentPolicyDTO.java
backend/src/main/java/com/genapp/dto/CommercialPolicyDTO.java
```

**Repository & Service:**
```bash
backend/src/main/java/com/genapp/repository/PolicyRepository.java
  - Methods: findByCustomerId, findByPolicyNumber, findByPolicyType, search, etc.

backend/src/main/java/com/genapp/service/PolicyService.java
  - Create, Get, Update, Delete operations for all policy types
  - Validation: customer must exist before creating policy
```

**Controller:**
```bash
backend/src/main/java/com/genapp/controller/PolicyController.java
  - POST /api/policies (create motor policy initially)
  - GET /api/policies/{policyId}
  - PUT /api/policies/{policyId}
  - DELETE /api/policies/{policyId}
  - GET /api/policies?customerId=X (get customer's policies)
```

**Tests:**
```bash
backend/src/test/java/com/genapp/service/PolicyServiceTest.java
backend/src/test/java/com/genapp/controller/PolicyControllerIntegrationTest.java
```

---

### Day 12-13: React Frontend (HIGH PRIORITY)

**Frontend Structure:**
```bash
# Initialize React project
frontend/package.json
frontend/src/App.tsx
frontend/src/main.tsx
frontend/src/components/CustomerList.tsx
frontend/src/components/CustomerForm.tsx
frontend/src/components/PolicyList.tsx
frontend/src/components/PolicyForm.tsx
frontend/src/api/client.ts
frontend/src/providers/AppProviders.tsx
frontend/Dockerfile
```

**Key Components:**
1. CustomerList - Display all customers in table
2. CustomerForm - Add/edit customer modal
3. PolicyList - Display customer's policies
4. PolicyForm - Add/edit motor policy
5. API client with error handling
6. React Router for navigation

---

### Day 14: Integration & Final Testing

**Testing Checklist:**
- [ ] Run all unit tests: `mvn test`
- [ ] Run integration tests: `mvn test -Dtest=*IntegrationTest`
- [ ] Start Docker Compose: `docker-compose up -d`
- [ ] Verify all services healthy: `docker-compose ps`
- [ ] Test REST endpoints via Swagger UI: http://localhost:8080/swagger-ui.html
- [ ] Test React frontend: http://localhost:3000
- [ ] Create customer via UI
- [ ] Create motor policy for customer
- [ ] Update customer and policy
- [ ] Delete policy and customer
- [ ] Search functionality
- [ ] Error handling (duplicate email, invalid data, etc.)

**Documentation:**
- [ ] Update README.md with any changes
- [ ] Add DEPLOYMENT.md (Kubernetes, Cloud Run, Docker)
- [ ] Create ARCHITECTURE.md (comparing old COBOL vs new Spring Boot)

---

## 📁 File Structure Summary

### Backend Files (17 Created)
```
backend/
├── pom.xml ✅
├── Dockerfile ✅
├── src/main/java/com/genapp/
│   ├── GenAppApplication.java ✅
│   ├── model/
│   │   ├── Customer.java ✅
│   │   ├── Policy.java ⏳
│   │   ├── MotorPolicy.java ⏳
│   │   ├── HousePolicy.java ⏳
│   │   ├── EndowmentPolicy.java ⏳
│   │   └── CommercialPolicy.java ⏳
│   ├── dto/
│   │   ├── CustomerDTO.java ✅
│   │   ├── PolicyDTO.java ⏳
│   │   ├── MotorPolicyDTO.java ⏳
│   │   └── (other policy DTOs) ⏳
│   ├── repository/
│   │   ├── CustomerRepository.java ✅
│   │   └── PolicyRepository.java ⏳
│   ├── service/
│   │   ├── CustomerService.java ✅
│   │   └── PolicyService.java ⏳
│   ├── controller/
│   │   ├── CustomerController.java ✅
│   │   └── PolicyController.java ⏳
│   ├── exception/
│   │   └── GlobalExceptionHandler.java ✅
│   └── config/
│       └── SwaggerConfig.java ✅
├── src/test/java/com/genapp/
│   ├── service/
│   │   ├── CustomerServiceTest.java ✅
│   │   └── PolicyServiceTest.java ⏳
│   └── controller/
│       ├── CustomerControllerIntegrationTest.java ✅
│       └── PolicyControllerIntegrationTest.java ⏳
├── src/main/resources/
│   ├── application.yml ✅
│   └── db/migration/
│       ├── V1__create_customer_table.sql ✅
│       └── V2__create_policy_tables.sql ✅
└── src/test/ ⏳ (create application-test.yml)
```

### Frontend Files (To Create)
```
frontend/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── index.html
├── Dockerfile
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── index.css
│   ├── components/
│   │   ├── CustomerList.tsx
│   │   ├── CustomerForm.tsx
│   │   ├── PolicyList.tsx
│   │   └── PolicyForm.tsx
│   ├── api/
│   │   └── client.ts
│   └── providers/
│       └── AppProviders.tsx
└── public/
```

### Root Files
```
genapp-claude/
├── docker-compose.yml ✅
├── ROADMAP.md ✅
├── README.md ✅
├── TODO.md ✅ (this file)
└── .gitignore
```

---

## 🚀 How to Continue Tomorrow

1. **Start Fresh:**
   ```bash
   cd genapp-claude
   docker-compose up -d
   ```

2. **Pick Up at Day 10:**
   - Start with Policy sealed class hierarchy
   - See section above for exact files to create

3. **Git Status:**
   - Last commit: Week 2 Days 8-9 (Testing, Swagger, Docs)
   - Next commit: Week 2 Days 10-11 (Policy Domain)

4. **Testing Tomorrow:**
   - Run tests after each file: `mvn test`
   - Verify endpoints in Swagger UI

---

## 📊 Progress Tracking

```
Week 1: ████████████░░░░░░░░ 100% ✅
├── Infrastructure ✅
├── Database ✅
├── Backend API ✅
└── Documentation ✅

Week 2: ███████░░░░░░░░░░░░░ 35%
├── Days 8-9: Testing & Swagger ✅
├── Days 10-11: Policy Domain ⏳ (NEXT)
├── Days 12-13: React Frontend ⏳
└── Day 14: Integration ⏳
```

---

## 🎯 Key Implementation Details

### Policy Sealed Classes (Java 21 Feature)
```java
// Base class
public sealed class Policy permits MotorPolicy, HousePolicy, EndowmentPolicy, CommercialPolicy {
    // Common fields
}

// Subclass - only MotorPolicy can extend Policy
public final class MotorPolicy extends Policy {
    // Motor-specific fields
}
```

### Swagger Documentation
- All endpoints auto-documented at: http://localhost:8080/swagger-ui.html
- Test endpoints interactively without Postman
- Download OpenAPI spec at: http://localhost:8080/v3/api-docs

### Database Schema
- Customer table: Already created via V1 migration
- Policy tables: Already created via V2 migration
- Relationships: policies.customer_id → customers.customer_id (foreign key)

---

## 📝 Notes for Tomorrow

1. **Don't forget to:**
   - Update todo list after each completed task
   - Commit changes regularly (at end of each day)
   - Run tests before committing

2. **If stuck on:**
   - Sealed classes: See Java 21 docs or ChatGPT for sealed class patterns
   - TestContainers: Reference CustomerControllerIntegrationTest.java for pattern
   - React: Keep it simple - just CRUD forms with table display

3. **Performance targets:**
   - API response time: < 50ms
   - Test suite: < 30 seconds
   - Full stack startup: < 60 seconds

4. **By end of Week 2:**
   - All 17 test cases passing
   - React UI functional (customers and motor policies)
   - Docker Compose fully working end-to-end
   - All documentation complete

---

## 🔗 Quick References

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **PostgreSQL**: localhost:5432 (user: genapp_user, password: genapp_password)
- **Frontend Dev**: http://localhost:3000
- **API Base URL**: http://localhost:8080/api
- **Documentation**: README.md and ROADMAP.md in this directory

---

**Last Updated**: 2025-10-18
**Next Action**: Implement Policy sealed class hierarchy (Days 10-11)
**Estimated Time Remaining**: 4-5 hours (Days 10-14)

Good luck! 🚀
