-- V5__add_soft_delete_fields_to_customer.sql
-- Add soft-delete support columns to customer table
-- Allows marking customers as INACTIVE while preserving data for audit/compliance

ALTER TABLE customer
ADD COLUMN deleted_at TIMESTAMP,
ADD COLUMN deletion_reason VARCHAR(500);

-- Create index on deleted_at for efficient filtering of soft-deleted records
CREATE INDEX idx_customer_deleted_at ON customer(deleted_at);

-- Create index on status to support filtering active/inactive customers
-- (this may already exist from initial schema, but ensure it exists)
CREATE INDEX IF NOT EXISTS idx_customer_status_deleted ON customer(status, deleted_at);

-- Comment on the new columns
COMMENT ON COLUMN customer.deleted_at IS 'Timestamp when customer was soft-deleted (marked INACTIVE). NULL if active.';
COMMENT ON COLUMN customer.deletion_reason IS 'Optional reason for customer deletion, max 500 characters. Used for audit/compliance purposes.';
