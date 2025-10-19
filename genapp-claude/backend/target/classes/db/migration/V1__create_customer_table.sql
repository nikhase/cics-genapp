/**
 * Flyway Database Migration: V1 - Create Customer Table
 *
 * This table replaces the VSAM KSDSCUST file from the original CICS GenApp.
 *
 * Original COBOL reference:
 *   - lgcmarea.cpy: Customer data structure (32,500 bytes)
 *   - KSDSCUST: VSAM indexed sequential file
 *   - Key: First 10 characters (customer ID)
 *
 * Mapping:
 *   COBOL Field → SQL Column
 *   ---
 *   Customer ID → customer_id (primary key)
 *   First Name → first_name
 *   Last Name → last_name
 *   Address → address
 *   City → city
 *   State → state
 *   Zip Code → zip_code
 *   Phone → phone
 *   Email → email
 */

-- Create SEQUENCE for auto-incrementing customer IDs
CREATE SEQUENCE IF NOT EXISTS seq_customer_id START WITH 1000 INCREMENT BY 1;

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGINT PRIMARY KEY DEFAULT nextval('seq_customer_id'),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    address VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(2),
    zip_code VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT email_unique UNIQUE (email)
);

-- Create INDEX for common queries
CREATE INDEX idx_customers_first_name ON customers(first_name);
CREATE INDEX idx_customers_last_name ON customers(last_name);
CREATE INDEX idx_customers_email ON customers(email);

-- Create AUDIT trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER customers_update_timestamp
BEFORE UPDATE ON customers
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();
