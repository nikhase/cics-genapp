-- V6__create_policy_table.sql
-- Create policy table for insurance policy management
-- Implements Story 3.7 - Policy List Page with Vaadin Grid
-- Supports four policy types: MOTOR, ENDOWMENT, HOUSE, COMMERCIAL

CREATE TABLE IF NOT EXISTS policy (
  policy_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  policy_number VARCHAR(50) UNIQUE NOT NULL,
  customer_id UUID NOT NULL REFERENCES customer(customer_id) ON DELETE CASCADE,
  policy_type VARCHAR(20) NOT NULL CHECK (policy_type IN ('MOTOR', 'ENDOWMENT', 'HOUSE', 'COMMERCIAL')),
  effective_date DATE NOT NULL,
  expiration_date DATE NOT NULL,
  premium_amount NUMERIC(10, 2) NOT NULL CHECK (premium_amount > 0),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'LAPSED', 'RENEWED')),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(255),
  updated_by VARCHAR(255),
  version BIGINT DEFAULT 0,
  deleted_at TIMESTAMP,
  deletion_reason VARCHAR(500)
);

-- Create indexes for efficient querying
CREATE INDEX IF NOT EXISTS idx_policy_customer_id ON policy(customer_id);
CREATE INDEX IF NOT EXISTS idx_policy_number ON policy(policy_number);
CREATE INDEX IF NOT EXISTS idx_policy_type ON policy(policy_type);
CREATE INDEX IF NOT EXISTS idx_policy_status ON policy(status);
CREATE INDEX IF NOT EXISTS idx_policy_status_deleted ON policy(status, deleted_at);
CREATE INDEX IF NOT EXISTS idx_policy_effective_date ON policy(effective_date);
CREATE INDEX IF NOT EXISTS idx_policy_expiration_date ON policy(expiration_date);
CREATE INDEX IF NOT EXISTS idx_policy_deleted_at ON policy(deleted_at);

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_policy_customer_status ON policy(customer_id, status);
CREATE INDEX IF NOT EXISTS idx_policy_type_status ON policy(policy_type, status);

-- Comments on columns
COMMENT ON TABLE policy IS 'Insurance policies linked to customers. Supports soft-delete via deleted_at.';
COMMENT ON COLUMN policy.policy_id IS 'Unique policy identifier (UUID).';
COMMENT ON COLUMN policy.policy_number IS 'Unique policy number for business reference.';
COMMENT ON COLUMN policy.customer_id IS 'Foreign key reference to customer table.';
COMMENT ON COLUMN policy.policy_type IS 'Type of policy: MOTOR, ENDOWMENT, HOUSE, or COMMERCIAL.';
COMMENT ON COLUMN policy.effective_date IS 'Date when policy coverage begins.';
COMMENT ON COLUMN policy.expiration_date IS 'Date when policy coverage expires.';
COMMENT ON COLUMN policy.premium_amount IS 'Annual or term premium amount in USD.';
COMMENT ON COLUMN policy.status IS 'Current status: ACTIVE, LAPSED, or RENEWED.';
COMMENT ON COLUMN policy.created_at IS 'Audit: timestamp when record created.';
COMMENT ON COLUMN policy.updated_at IS 'Audit: timestamp of last update.';
COMMENT ON COLUMN policy.created_by IS 'Audit: user who created the record.';
COMMENT ON COLUMN policy.updated_by IS 'Audit: user who last updated the record.';
COMMENT ON COLUMN policy.version IS 'Optimistic lock version number for concurrency control.';
COMMENT ON COLUMN policy.deleted_at IS 'Soft-delete timestamp. NULL if record is active.';
COMMENT ON COLUMN policy.deletion_reason IS 'Optional reason for policy deletion/cancellation (max 500 chars).';
