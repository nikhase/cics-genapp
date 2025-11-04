-- V6__create_policy_table.sql
-- Enhance policy table with soft-delete and audit fields
-- Implements Story 3.7 - Policy List Page with Vaadin Grid
-- Supports four policy types: MOTOR, ENDOWMENT, HOUSE, COMMERCIAL

-- Add soft-delete and additional audit fields to existing policy table
ALTER TABLE policy
  ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0,
  ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP,
  ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);

-- Rename columns to match new schema (start_date -> effective_date, end_date -> expiration_date)
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='policy' AND column_name='start_date') THEN
    ALTER TABLE policy RENAME COLUMN start_date TO effective_date;
  END IF;
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='policy' AND column_name='end_date') THEN
    ALTER TABLE policy RENAME COLUMN end_date TO expiration_date;
  END IF;
END $$;

-- Drop old indexes that will be replaced
DROP INDEX IF EXISTS idx_policy_start_date;
DROP INDEX IF EXISTS idx_policy_policy_number;
DROP INDEX IF EXISTS idx_policy_created_at;

-- Create new indexes for efficient querying
CREATE INDEX IF NOT EXISTS idx_policy_effective_date ON policy(effective_date);
CREATE INDEX IF NOT EXISTS idx_policy_expiration_date ON policy(expiration_date);
CREATE INDEX IF NOT EXISTS idx_policy_status_deleted ON policy(status, deleted_at);
CREATE INDEX IF NOT EXISTS idx_policy_deleted_at ON policy(deleted_at);

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_policy_customer_status ON policy(customer_id, status);
CREATE INDEX IF NOT EXISTS idx_policy_type_status ON policy(policy_type, status);
