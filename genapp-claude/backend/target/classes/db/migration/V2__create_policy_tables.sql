/**
 * Flyway Database Migration: V2 - Create Policy Tables
 *
 * This replaces the VSAM KSDSPOLY file and Db2 policy tables from the original CICS GenApp.
 *
 * Original COBOL reference:
 *   - KSDSPOLY: VSAM indexed sequential file
 *   - Key: First 21 characters (policy type + customer ID + policy number)
 *   - Policy types: C (Commercial), E (Endowment), H (House), M (Motor)
 *   - Db2 tables: POLICY, MOTPOL, ENDPOL, HOUSEPOL, COMMPOL
 *
 * New approach: Single POLICIES table with policy_type discriminator (table-per-type pattern)
 *
 * Mapping:
 *   COBOL Field → SQL Column
 *   ---
 *   Policy Type (C/E/H/M) → policy_type (discriminator)
 *   Policy Number → policy_number
 *   Customer ID → customer_id (foreign key)
 *   Start Date → start_date
 *   End Date → end_date
 *   Premium → premium
 */

-- Create SEQUENCE for auto-incrementing policy IDs
CREATE SEQUENCE IF NOT EXISTS seq_policy_id START WITH 5000 INCREMENT BY 1;

-- Create base POLICIES table with inheritance/discriminator pattern
CREATE TABLE IF NOT EXISTS policies (
    policy_id BIGINT PRIMARY KEY DEFAULT nextval('seq_policy_id'),
    policy_type VARCHAR(1) NOT NULL, -- C (Commercial), E (Endowment), H (House), M (Motor)
    policy_number VARCHAR(20) NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE,
    premium DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, EXPIRED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_policies_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CONSTRAINT chk_policy_type CHECK (policy_type IN ('C', 'E', 'H', 'M')),
    CONSTRAINT policy_number_unique UNIQUE (policy_number, customer_id)
);

-- Create MOTOR_POLICY_DETAILS table for motor-specific fields
CREATE TABLE IF NOT EXISTS motor_policy_details (
    policy_id BIGINT PRIMARY KEY REFERENCES policies(policy_id) ON DELETE CASCADE,
    vehicle_make VARCHAR(50) NOT NULL,
    vehicle_model VARCHAR(50) NOT NULL,
    vehicle_year INTEGER NOT NULL,
    vehicle_vin VARCHAR(17),
    usage_type VARCHAR(20), -- PERSONAL, COMMERCIAL, etc.
    annual_mileage INTEGER,
    driver_age_group VARCHAR(10),
    coverage_type VARCHAR(20), -- BASIC, COMPREHENSIVE, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create ENDOWMENT_POLICY_DETAILS table for endowment-specific fields
CREATE TABLE IF NOT EXISTS endowment_policy_details (
    policy_id BIGINT PRIMARY KEY REFERENCES policies(policy_id) ON DELETE CASCADE,
    maturity_date DATE NOT NULL,
    insured_amount DECIMAL(12, 2) NOT NULL,
    bonus_rate DECIMAL(5, 2),
    investment_type VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create HOUSE_POLICY_DETAILS table for house insurance specific fields
CREATE TABLE IF NOT EXISTS house_policy_details (
    policy_id BIGINT PRIMARY KEY REFERENCES policies(policy_id) ON DELETE CASCADE,
    property_address VARCHAR(150) NOT NULL,
    property_type VARCHAR(20), -- HOUSE, APARTMENT, CONDO, etc.
    construction_year INTEGER,
    square_footage INTEGER,
    replacement_cost DECIMAL(12, 2),
    deductible DECIMAL(8, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create COMMERCIAL_POLICY_DETAILS table for commercial property insurance
CREATE TABLE IF NOT EXISTS commercial_policy_details (
    policy_id BIGINT PRIMARY KEY REFERENCES policies(policy_id) ON DELETE CASCADE,
    business_name VARCHAR(100) NOT NULL,
    business_type VARCHAR(50),
    property_address VARCHAR(150) NOT NULL,
    annual_revenue DECIMAL(12, 2),
    num_employees INTEGER,
    coverage_limit DECIMAL(12, 2),
    deductible DECIMAL(8, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create INDEXes for common queries
CREATE INDEX idx_policies_customer_id ON policies(customer_id);
CREATE INDEX idx_policies_policy_type ON policies(policy_type);
CREATE INDEX idx_policies_policy_number ON policies(policy_number);
CREATE INDEX idx_policies_status ON policies(status);
CREATE INDEX idx_policies_start_date ON policies(start_date);

-- Create audit trigger
CREATE OR REPLACE FUNCTION update_policy_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER policies_update_timestamp
BEFORE UPDATE ON policies
FOR EACH ROW
EXECUTE FUNCTION update_policy_timestamp();

CREATE TRIGGER motor_policy_details_update_timestamp
BEFORE UPDATE ON motor_policy_details
FOR EACH ROW
EXECUTE FUNCTION update_policy_timestamp();

CREATE TRIGGER endowment_policy_details_update_timestamp
BEFORE UPDATE ON endowment_policy_details
FOR EACH ROW
EXECUTE FUNCTION update_policy_timestamp();

CREATE TRIGGER house_policy_details_update_timestamp
BEFORE UPDATE ON house_policy_details
FOR EACH ROW
EXECUTE FUNCTION update_policy_timestamp();

CREATE TRIGGER commercial_policy_details_update_timestamp
BEFORE UPDATE ON commercial_policy_details
FOR EACH ROW
EXECUTE FUNCTION update_policy_timestamp();
