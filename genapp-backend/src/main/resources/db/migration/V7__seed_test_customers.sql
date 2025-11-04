-- V7__seed_test_customers.sql
-- Seed test database with realistic customer test data for development and testing
-- This migration is idempotent and safe to run multiple times

-- Delete existing test data (optional - for reset capability)
-- Uncomment the line below to reset test data on each migration
-- DELETE FROM customer WHERE email LIKE '%.test-%' OR email LIKE '%@example.com';

-- ============================================================================
-- INSERT TEST CUSTOMER DATA
-- ============================================================================
-- Inserting 18 realistic test customer records with diverse demographics
-- All customers created with ACTIVE status for testing active customer scenarios
-- Email addresses use example.com domain to ensure they never reach production
-- Phone numbers follow E.164 international format
-- Date of birth values ensure all customers are 18+ years old

INSERT INTO customer (
  customer_id,
  first_name,
  last_name,
  date_of_birth,
  email,
  phone,
  address,
  city,
  state,
  zip_code,
  status,
  created_by,
  updated_by,
  created_at,
  updated_at
)
VALUES
  -- Common names for testing search by last name
  ('f47ac10b-58cc-4372-a567-0e02b2c3d479'::uuid, 'Jane', 'Smith', '1985-03-15'::date, 'jane.smith@example.com', '+15550101', '123 Main St', 'Portland', 'OR', '97201', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d480'::uuid, 'John', 'Smith', '1978-07-22'::date, 'john.smith@example.com', '+15550102', '456 Oak Ave', 'Seattle', 'WA', '98101', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  -- Diverse last names for variety
  ('f47ac10b-58cc-4372-a567-0e02b2c3d481'::uuid, 'Sarah', 'Johnson', '1992-11-08'::date, 'sarah.johnson@example.com', '+15550103', '789 Elm St', 'Portland', 'OR', '97202', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d482'::uuid, 'Michael', 'Brown', '1982-05-14'::date, 'michael.brown@example.com', '+15550104', '321 Pine Rd', 'Eugene', 'OR', '97401', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d483'::uuid, 'Emily', 'Davis', '1988-09-30'::date, 'emily.davis@example.com', '+15550105', '654 Maple Dr', 'Salem', 'OR', '97301', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d484'::uuid, 'James', 'Wilson', '1975-01-25'::date, 'james.wilson@example.com', '+15550106', '987 Cedar Ln', 'Bend', 'OR', '97702', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d485'::uuid, 'Lisa', 'Martinez', '1990-06-12'::date, 'lisa.martinez@example.com', '+15550107', '159 Birch Ave', 'Corvallis', 'OR', '97330', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d486'::uuid, 'Robert', 'Garcia', '1980-08-19'::date, 'robert.garcia@example.com', '+15550108', '753 Spruce St', 'Gresham', 'OR', '97030', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  -- International names for Unicode/encoding support testing
  ('f47ac10b-58cc-4372-a567-0e02b2c3d487'::uuid, 'Anna', 'Müller', '1987-04-11'::date, 'anna.muller@example.com', '+15550109', '456 Walnut St', 'Portland', 'OR', '97203', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d488'::uuid, 'Pierre', 'Dupont', '1984-02-28'::date, 'pierre.dupont@example.com', '+15550110', '321 Chestnut Rd', 'Salem', 'OR', '97302', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d489'::uuid, 'Maria', 'Rossi', '1991-12-05'::date, 'maria.rossi@example.com', '+15550111', '654 Hazel Dr', 'Beaverton', 'OR', '97005', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d48a'::uuid, 'Carlos', 'Hernandez', '1983-09-20'::date, 'carlos.hernandez@example.com', '+15550112', '987 Hickory Ln', 'Lake Oswego', 'OR', '97034', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  -- Additional customers for pagination testing (need at least 15)
  ('f47ac10b-58cc-4372-a567-0e02b2c3d48b'::uuid, 'Jennifer', 'Anderson', '1989-10-16'::date, 'jennifer.anderson@example.com', '+15550113', '159 Laurel Ave', 'Tigard', 'OR', '97223', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d48c'::uuid, 'William', 'Taylor', '1986-07-03'::date, 'william.taylor@example.com', '+15550114', '753 Oak Blvd', 'Aloha', 'OR', '97007', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d48d'::uuid, 'Michelle', 'Thomas', '1981-04-09'::date, 'michelle.thomas@example.com', '+15550115', '456 Poplar Rd', 'West Linn', 'OR', '97068', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d48e'::uuid, 'Christopher', 'Jackson', '1979-11-27'::date, 'christopher.jackson@example.com', '+15550116', '321 Cherry Ln', 'Oregon City', 'OR', '97045', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f47ac10b-58cc-4372-a567-0e02b2c3d48f'::uuid, 'Amanda', 'White', '1993-05-21'::date, 'amanda.white@example.com', '+15550117', '654 Peach St', 'Hillsboro', 'OR', '97123', 'ACTIVE', 'test-data-seed', 'test-data-seed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (customer_id) DO NOTHING;

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================
-- The following queries can be used to verify the test data was seeded correctly:
--
-- 1. Count total test customers:
--    SELECT COUNT(*) as total_test_customers FROM customer WHERE email LIKE '%@example.com';
--
-- 2. Find customers with last name "Smith":
--    SELECT customer_id, first_name, last_name, email FROM customer WHERE last_name = 'Smith' AND email LIKE '%@example.com';
--
-- 3. Find all test customers by email domain:
--    SELECT customer_id, first_name, last_name, email FROM customer WHERE email LIKE '%@example.com' ORDER BY last_name, first_name;
--
-- 4. Find active test customers:
--    SELECT COUNT(*) FROM customer WHERE status = 'ACTIVE' AND email LIKE '%@example.com';
--
-- 5. Verify diverse demographics:
--    SELECT city, state, COUNT(*) as count FROM customer WHERE email LIKE '%@example.com' GROUP BY city, state ORDER BY count DESC;

-- ============================================================================
-- IDEMPOTENCY NOTES
-- ============================================================================
-- This migration uses ON CONFLICT (customer_id) DO NOTHING to ensure idempotency.
-- If the migration is run multiple times, existing records will NOT be updated or duplicated.
-- This allows safe re-running for dev/test environment resets.
--
-- To reset test data completely:
-- 1. Delete existing test customers: DELETE FROM customer WHERE email LIKE '%@example.com';
-- 2. Re-run this migration to re-seed: flyway clean && flyway migrate
--
-- WARNING: Using flyway clean will delete ALL migrations and data. Use with caution!
