# Story 3.9: Test Data Setup and Seed Database

**Epic:** 3 (Vaadin Frontend - Core User Interface)
**Story ID:** 3.9
**Status:** Drafted
**Priority:** High
**Story Points:** 3

## User Story

**As a** QA Engineer / Test Administrator,
**I want** to seed the development and test databases with realistic customer test data,
**So that** I can perform comprehensive testing of the customer management UI without manually creating records.

## Acceptance Criteria

1. **Test Data File Created**
   - CSV file (`test-customers.csv`) created with 15-20 realistic customer records
   - Columns: firstName, lastName, email, phone, dateOfBirth, address, city, state, zipCode
   - Sample data representing diverse demographics (various ages, names, locations)
   - Data conforms to API validation rules (valid email format, phone format, age 18+)

2. **Data Seeding Script**
   - SQL/Flyway migration script (`V3__seed_test_customers.sql`) created
   - Script inserts test data into PostgreSQL CUSTOMER table via API or SQL
   - Script is idempotent (safe to run multiple times)
   - All customers created with status = ACTIVE

3. **Test Data Quantity**
   - Minimum 15 customers inserted to allow meaningful search/pagination testing
   - Mix of first/last name variations (common names, uncommon names, international names)
   - Email addresses follow pattern: test.customer.N@example.com (or realistic variations)
   - Phone numbers include different formats (some with dashes, some without, different country codes)

4. **Accessible Test Accounts**
   - Test data can be accessed via customer search API or directly in UI
   - Sample queries documented:
     - Find all customers: should return 15-20 results
     - Search by last name "Smith": should return at least 2 customers
     - Search by email domain "example.com": should return all test customers
     - Search by status "ACTIVE": should return all 15-20 customers

5. **Documentation**
   - README section created documenting:
     - How to seed test data (`./scripts/seed-testdata.sh` or SQL file location)
     - List of test customer records with their IDs (for reference in tests)
     - Instructions for resetting test data (clear existing, re-seed)
   - Example test data shown in docs/test-data.md

6. **Data Reset Capability**
   - Script allows clearing test data and re-seeding (for test isolation)
   - Optional: cleanup script that can remove all test data added by seed script
   - Development team can reset test database to clean state for CI/CD

## Technical Details

### Test Data Schema
```csv
firstName,lastName,email,phone,dateOfBirth,address,city,state,zipCode
Jane,Smith,jane.smith@example.com,+1-555-0101,1985-03-15,123 Main St,Portland,OR,97201
John,Doe,john.doe@example.com,+1-555-0102,1978-07-22,456 Oak Ave,Seattle,WA,98101
Sarah,Johnson,sarah.j@example.com,+1-555-0103,1992-11-08,789 Elm St,Portland,OR,97202
Michael,Brown,m.brown@example.com,+1-555-0104,1982-05-14,321 Pine Rd,Eugene,OR,97401
Emily,Davis,emily.d@example.com,+1-555-0105,1988-09-30,654 Maple Dr,Salem,OR,97301
...
```

### Implementation Options

**Option A: SQL Seed Script (Recommended)**
- Path: `base/cntl/migrations/V3__seed_test_customers.sql`
- Flyway automatically runs on database initialization
- Clean, version-controlled approach

**Option B: CSV Upload + Script**
- CSV file: `test-data/customers.csv`
- Shell script: `scripts/seed-testdata.sh`
- Script calls API to create customers in bulk
- Useful for testing API client error handling

**Recommendation:** Use Option A (SQL) for initial dev/test environment; Option B for CI/CD test fixture resets.

## Dependencies

- Story 2.1: Customer domain model and PostgreSQL schema (CUSTOMER table exists)
- Story 2.2: Customer Create API (POST /api/v1/customers) - for Option B
- Story 1.3: PostgreSQL database connectivity

## Tasks / Subtasks

- [ ] **Prepare Test Data**
  - [ ] Create test customer CSV with 15-20 realistic records
  - [ ] Validate data conforms to API validation rules
  - [ ] Document test data IDs and key attributes

- [ ] **Create Seed Scripts**
  - [ ] Write SQL migration V3__seed_test_customers.sql
  - [ ] Make script idempotent (upsert or delete-before-insert)
  - [ ] Test script runs without errors

- [ ] **Documentation**
  - [ ] Create docs/test-data.md with test customer reference
  - [ ] Update README with seeding instructions
  - [ ] Add test data section to development guide

- [ ] **Testing / Validation**
  - [ ] Verify test data appears in UI after seeding
  - [ ] Test search queries return expected customers
  - [ ] Test pagination with seeded data
  - [ ] Test reset/re-seed capability

## Dev Notes

- This story enables realistic testing of customer search, list, detail, and edit pages
- Seed data should persist across test runs (data layer test isolation via transactions)
- Consider adding Polish, German, or other international names to test encoding/Unicode support
- Test data can be used for performance testing (pagination with 100+ customers)

## Success Criteria

- ✅ 15-20 customers can be queried via API and displayed in UI
- ✅ Search functionality works with test data (by name, email, phone)
- ✅ Pagination displays correct page size and record count
- ✅ Test data documented and replicable for new developers
- ✅ CI/CD can seed database automatically on startup

## Related Stories

- 3.4: Customer Search and List Page (uses this test data)
- 3.5: Customer Detail and Edit Page (uses this test data)
- 2.2-2.5: Customer CRUD APIs (tested with this data)

## Changelog

**Created:** 2025-11-04
**Author:** Claude Code
**Status:** Drafted

---

## Dev Agent Record

*This section will be populated during story implementation*

### Completion Notes
*(To be filled by dev)*

### Debug Log
*(To be filled by dev)*

### File List
- **NEW:**
- **MODIFIED:**
- **DELETED:**

### Dev Notes for Next Story
*(To be filled by dev)*

---
