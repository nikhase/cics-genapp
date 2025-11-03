-- V1__initial_schema.sql
-- Initial database schema for CICS GenApp Cloud Modernization
-- This migration creates all core tables: CUSTOMER, POLICY, AUDIT_LOG, FEATURE_TOGGLE

-- Enable UUID extension (PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- CUSTOMER TABLE
-- ============================================================================
-- Stores customer master data
CREATE TABLE customer (
  customer_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  date_of_birth DATE,
  email VARCHAR(255) NOT NULL UNIQUE,
  phone VARCHAR(20),
  address VARCHAR(255),
  city VARCHAR(100),
  state VARCHAR(50),
  zip_code VARCHAR(10),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(255),
  updated_by VARCHAR(255)
);

-- Create indexes for common queries
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_customer_status ON customer(status);
CREATE INDEX idx_customer_created_at ON customer(created_at);
CREATE INDEX idx_customer_last_name ON customer(last_name);

-- ============================================================================
-- POLICY TABLE (Base table for all policy types)
-- ============================================================================
-- Stores policy master data with support for 4 policy types (Motor, Endowment, House, Commercial)
CREATE TABLE policy (
  policy_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id UUID NOT NULL REFERENCES customer(customer_id) ON DELETE RESTRICT,
  policy_number VARCHAR(50) NOT NULL UNIQUE,
  policy_type VARCHAR(20) NOT NULL CHECK (policy_type IN ('MOTOR', 'ENDOWMENT', 'HOUSE', 'COMMERCIAL')),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'RENEWED', 'LAPSED')),
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  premium_amount NUMERIC(15, 2) NOT NULL CHECK (premium_amount > 0),
  notes TEXT,
  -- Type-specific fields (stored as JSON for flexibility)
  type_specific_data JSONB,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(255),
  updated_by VARCHAR(255)
);

-- Create indexes for common queries
CREATE INDEX idx_policy_customer_id ON policy(customer_id);
CREATE INDEX idx_policy_policy_number ON policy(policy_number);
CREATE INDEX idx_policy_status ON policy(status);
CREATE INDEX idx_policy_start_date ON policy(start_date);
CREATE INDEX idx_policy_policy_type ON policy(policy_type);
CREATE INDEX idx_policy_created_at ON policy(created_at);

-- ============================================================================
-- AUDIT_LOG TABLE
-- ============================================================================
-- Immutable audit trail for compliance (append-only, no updates/deletes)
CREATE TABLE audit_log (
  audit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_id VARCHAR(255),
  operation VARCHAR(20) NOT NULL CHECK (operation IN ('CREATE', 'READ', 'UPDATE', 'DELETE', 'SEARCH')),
  entity_type VARCHAR(100) NOT NULL,
  entity_id UUID,
  changes JSONB,
  ip_address VARCHAR(45),
  user_agent VARCHAR(500),
  trace_id VARCHAR(255)
);

-- Create indexes for audit queries
CREATE INDEX idx_audit_log_entity_type ON audit_log(entity_type);
CREATE INDEX idx_audit_log_entity_id ON audit_log(entity_id);
CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp);
CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX idx_audit_log_operation ON audit_log(operation);
CREATE INDEX idx_audit_log_trace_id ON audit_log(trace_id);

-- ============================================================================
-- FEATURE_TOGGLE TABLE
-- ============================================================================
-- Stores feature toggle state for Unleash integration
CREATE TABLE feature_toggle (
  toggle_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  toggle_name VARCHAR(100) NOT NULL UNIQUE,
  toggle_state BOOLEAN NOT NULL DEFAULT false,
  toggle_percentage INTEGER DEFAULT 0 CHECK (toggle_percentage >= 0 AND toggle_percentage <= 100),
  last_changed TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  changed_by VARCHAR(255),
  reason TEXT
);

-- Create index for toggle lookups
CREATE INDEX idx_feature_toggle_name ON feature_toggle(toggle_name);
CREATE INDEX idx_feature_toggle_last_changed ON feature_toggle(last_changed);

-- ============================================================================
-- Initial feature toggles for strangler pattern
-- ============================================================================
INSERT INTO feature_toggle (toggle_name, toggle_state, toggle_percentage, changed_by, reason)
VALUES
  ('customer-api-enabled', false, 0, 'system', 'Initial setup - disabled until testing complete'),
  ('policy-api-enabled', false, 0, 'system', 'Initial setup - disabled until testing complete'),
  ('cdc-sync-enabled', false, 0, 'system', 'Initial setup - disabled until testing complete'),
  ('parallel-run-validation-enabled', false, 0, 'system', 'Initial setup - disabled until testing complete')
ON CONFLICT (toggle_name) DO NOTHING;

-- ============================================================================
-- Data Consistency Notes
-- ============================================================================
-- - POLICY has FK constraint to CUSTOMER (ON DELETE RESTRICT to prevent orphaned policies)
-- - AUDIT_LOG is immutable (no updates/deletes, append-only)
-- - All timestamp fields default to CURRENT_TIMESTAMP for audit purposes
-- - UUID fields use gen_random_uuid() for automatic generation
-- - Email field is UNIQUE with NOT NULL to prevent duplicates
-- - Policy type enumeration enforces 4 types: MOTOR, ENDOWMENT, HOUSE, COMMERCIAL
-- - Status fields use CHECK constraints to restrict valid values
-- - premium_amount requires positive values (CHECK constraint)
