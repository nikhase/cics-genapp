-- V4__add_version_to_customer.sql
-- Add version column to customer table for optimistic locking support
-- This enables concurrent update conflict detection via JPA @Version annotation

ALTER TABLE customer
ADD COLUMN version BIGINT DEFAULT 0;

-- Comment on the new column
COMMENT ON COLUMN customer.version IS 'Version number for optimistic locking. Automatically incremented on each update.';
