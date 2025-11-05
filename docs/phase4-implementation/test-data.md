# Test Data Reference Guide

**Project:** CICS GenApp Cloud Modernization
**Last Updated:** 2025-11-04
**Status:** Active (18 test customers seeded)

## Overview

This document describes the test customer data seeded into the development and test databases. Test data is automatically loaded via Flyway database migration `V7__seed_test_customers.sql` during application startup.

## Test Customer Records

The following 18 realistic customer records are available for testing customer management functionality:

### Common Names Group (for "Smith" search testing)

| ID | First Name | Last Name | Email | Phone | City, State | Age |
|---|---|---|---|---|---|---|
| f47ac10b-58cc-4372-a567-0e02b2c3d479 | Jane | Smith | jane.smith@example.com | +15550101 | Portland, OR | 39 |
| f47ac10b-58cc-4372-a567-0e02b2c3d480 | John | Smith | john.smith@example.com | +15550102 | Seattle, WA | 46 |

### Diverse Last Names Group

| ID | First Name | Last Name | Email | Phone | City, State | Age |
|---|---|---|---|---|---|---|
| f47ac10b-58cc-4372-a567-0e02b2c3d481 | Sarah | Johnson | sarah.johnson@example.com | +15550103 | Portland, OR | 32 |
| f47ac10b-58cc-4372-a567-0e02b2c3d482 | Michael | Brown | michael.brown@example.com | +15550104 | Eugene, OR | 42 |
| f47ac10b-58cc-4372-a567-0e02b2c3d483 | Emily | Davis | emily.davis@example.com | +15550105 | Salem, OR | 36 |
| f47ac10b-58cc-4372-a567-0e02b2c3d484 | James | Wilson | james.wilson@example.com | +15550106 | Bend, OR | 49 |
| f47ac10b-58cc-4372-a567-0e02b2c3d485 | Lisa | Martinez | lisa.martinez@example.com | +15550107 | Corvallis, OR | 34 |
| f47ac10b-58cc-4372-a567-0e02b2c3d486 | Robert | Garcia | robert.garcia@example.com | +15550108 | Gresham, OR | 44 |

### International Names Group (Unicode support testing)

| ID | First Name | Last Name | Email | Phone | City, State | Age |
|---|---|---|---|---|---|---|
| f47ac10b-58cc-4372-a567-0e02b2c3d487 | Anna | Müller | anna.muller@example.com | +15550109 | Portland, OR | 37 |
| f47ac10b-58cc-4372-a567-0e02b2c3d488 | Pierre | Dupont | pierre.dupont@example.com | +15550110 | Salem, OR | 40 |
| f47ac10b-58cc-4372-a567-0e02b2c3d489 | Maria | Rossi | maria.rossi@example.com | +15550111 | Beaverton, OR | 33 |
| f47ac10b-58cc-4372-a567-0e02b2c3d48a | Carlos | Hernandez | carlos.hernandez@example.com | +15550112 | Lake Oswego, OR | 41 |

### Additional Customers (pagination testing)

| ID | First Name | Last Name | Email | Phone | City, State | Age |
|---|---|---|---|---|---|---|
| f47ac10b-58cc-4372-a567-0e02b2c3d48b | Jennifer | Anderson | jennifer.anderson@example.com | +15550113 | Tigard, OR | 35 |
| f47ac10b-58cc-4372-a567-0e02b2c3d48c | William | Taylor | william.taylor@example.com | +15550114 | Aloha, OR | 38 |
| f47ac10b-58cc-4372-a567-0e02b2c3d48d | Michelle | Thomas | michelle.thomas@example.com | +15550115 | West Linn, OR | 43 |
| f47ac10b-58cc-4372-a567-0e02b2c3d48e | Christopher | Jackson | christopher.jackson@example.com | +15550116 | Oregon City, OR | 45 |
| f47ac10b-58cc-4372-a567-0e02b2c3d48f | Amanda | White | amanda.white@example.com | +15550117 | Hillsboro, OR | 31 |

## Key Characteristics

### Data Validation Compliance
- **First/Last Name:** All names are 1-100 characters as per validation rules
- **Email:** All emails follow valid email format and use `@example.com` domain (safe for development)
- **Phone:** All phone numbers follow E.164 international format (+1-555-XXXX)
- **Date of Birth:** All customers are 18+ years old (oldest born 1975, youngest born 1993)
- **Status:** All test customers are created with `ACTIVE` status

### Geographic Diversity
Test data includes customers from multiple Oregon cities to support regional search/filtering testing:
- **Portland Area:** Portland, Beaverton, Tigard, West Linn
- **Salem Area:** Salem, Corvallis
- **Eugene/Bend:** Eugene, Bend
- **Gresham/Lake Oswego/Oregon City/Aloha/Hillsboro:** Metropolitan Portland suburbs
- **Out-of-State:** Seattle, WA (for cross-state testing)

### Name Diversity
- **Common First Names:** Jane, John, Sarah, Michael, Emily, James, Lisa, Robert, Jennifer, William, Michelle, Christopher, Amanda
- **Common Last Names:** Smith (×2), Johnson, Brown, Davis, Wilson, Martinez, Garcia, Anderson, Taylor, Thomas, Jackson, White
- **International Names:** Müller (German), Dupont (French), Rossi (Italian), Hernandez (Spanish)

## Sample Test Queries

### 1. Find All Test Customers
```sql
SELECT customer_id, first_name, last_name, email, status
FROM customer
WHERE email LIKE '%@example.com'
ORDER BY last_name, first_name;
```
**Expected Result:** 18 customers, all with ACTIVE status

### 2. Search by Last Name "Smith"
```sql
SELECT customer_id, first_name, last_name, email, phone
FROM customer
WHERE last_name = 'Smith' AND email LIKE '%@example.com'
ORDER BY first_name;
```
**Expected Result:** 2 customers (Jane Smith, John Smith)

### 3. Search by Email Domain
```sql
SELECT COUNT(*) as total
FROM customer
WHERE email LIKE '%@example.com';
```
**Expected Result:** 18

### 4. Find Active Test Customers
```sql
SELECT COUNT(*) as active_count
FROM customer
WHERE status = 'ACTIVE' AND email LIKE '%@example.com';
```
**Expected Result:** 18

### 5. Geographic Distribution
```sql
SELECT city, state, COUNT(*) as count
FROM customer
WHERE email LIKE '%@example.com'
GROUP BY city, state
ORDER BY count DESC, city;
```
**Expected Result:** Distribution across 10 Oregon cities + 1 Washington city

### 6. Pagination Test
```sql
SELECT customer_id, first_name, last_name, email
FROM customer
WHERE email LIKE '%@example.com'
ORDER BY last_name, first_name
LIMIT 5 OFFSET 0;  -- Page 1 (5 per page)
```
**Expected Result:** First 5 customers in alphabetical order

## Using Test Data in Development

### In UI Testing
1. Start the application (test data auto-loads via Flyway)
2. Navigate to **Customer Search** page
3. Verify 18+ customers appear in the list
4. Test search filters:
   - **Last Name Filter:** Type "Smith" → should show 2 results
   - **City Filter:** Select "Portland" → should show 4+ results
   - **Status Filter:** Select "ACTIVE" → should show all 18

### In API Testing (REST Client, Postman, curl)
```bash
# Get all test customers
curl http://localhost:8080/api/v1/customers?search=example.com

# Search by last name
curl http://localhost:8080/api/v1/customers?search=Smith

# Pagination test
curl "http://localhost:8080/api/v1/customers?page=0&size=5&sort=lastName,asc"

# Filter by status
curl http://localhost:8080/api/v1/customers?status=ACTIVE
```

### In Automated Tests
```java
// Example JUnit 5 test with test data
@Test
void testCustomerSearch() {
    // Search for "Smith" customers
    List<Customer> results = customerService.search("Smith", null);
    assertThat(results).hasSize(2);
    assertThat(results).allMatch(c -> c.getLastName().equals("Smith"));
}

@Test
void testPagination() {
    // Test pagination with seeded data
    Page<Customer> page = customerService.findAll(PageRequest.of(0, 5));
    assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(18);
    assertThat(page.getContent()).hasSize(5);
}
```

## Resetting Test Data

### Option 1: Delete and Re-seed (Recommended for CI/CD)
```bash
# In SQL client (psql, DBeaver, etc.):
DELETE FROM customer WHERE email LIKE '%@example.com';

# Then restart application to trigger Flyway migration V7
```

### Option 2: Full Database Reset (Development Only)
```bash
# Drop and recreate entire database
DROP DATABASE genapp_dev;
CREATE DATABASE genapp_dev;

# Start application to run all migrations (including V7)
```

### Option 3: Custom Reset Script
A reset script can be added later to `scripts/reset-testdata.sh` for automated testing workflows.

## Data Seeding Configuration

### Flyway Migration Details
- **File:** `genapp-backend/src/main/resources/db/migration/V7__seed_test_customers.sql`
- **Triggers:** Automatically on application startup
- **Idempotency:** Uses `ON CONFLICT (customer_id) DO NOTHING` to prevent duplicates
- **Safety:** Safe to run multiple times without side effects

### Environment-Specific Seeding
- **Development (application-dev.yml):** Flyway auto-runs V7 on startup
- **Test (application-test.yml):** Flyway auto-runs V7 on startup
- **Production:** Migration V7 skipped via environment configuration

## Troubleshooting

### Test Data Not Appearing
1. **Check Flyway Status:** Verify migration V7 completed in logs
2. **Verify Database Connection:** Ensure PostgreSQL is running and connected
3. **Check for Constraints:** Ensure email uniqueness constraint allows test data
4. **Restart Application:** Flyway only runs on startup

### Duplicate Email Errors
If you see "unique constraint violation" on email:
1. Delete existing test data: `DELETE FROM customer WHERE email LIKE '%@example.com'`
2. Restart application to re-seed

### Character Encoding Issues
If international names (Müller, Dupont, Rossi) display incorrectly:
1. Verify PostgreSQL uses UTF-8 encoding: `SHOW client_encoding;`
2. Ensure database created with UTF-8: `CREATE DATABASE genapp_dev ENCODING 'UTF8';`

## Notes for Developers

### Adding More Test Data
To add additional test customers:
1. Edit `V7__seed_test_customers.sql`
2. Add new INSERT statement with unique UUID and email
3. Restart application (Flyway won't re-run existing migration, create V8)
4. Alternatively, use UI to manually create additional customers

### Performance Testing
With 18 seeded customers, you can test:
- ✅ List pagination (default 5-10 per page)
- ✅ Search performance with multiple filters
- ✅ Sorting by name, city, status
- For larger dataset testing (100+ records), manually insert via API or create a new migration

### Testing Soft-Delete
Test data includes `ACTIVE` status customers. Test soft-delete by:
1. Create an additional customer manually
2. Delete it via API
3. Verify `deleted_at` timestamp is set
4. Verify deleted customer doesn't appear in search

## Related Stories
- **Story 3.4:** Customer Search and List Page (uses this test data)
- **Story 3.5:** Customer Detail and Edit Page (uses this test data)
- **Story 3.6:** Customer Create/Edit Page (test creating alongside seeded data)
- **Story 2.1-2.5:** Customer CRUD APIs (tested with this data)

---

**Last Updated:** 2025-11-04 by Claude Code
**Status:** Complete - All 18 test customers seeded and documented
