CREATE TABLE IF NOT EXISTS customers (
    id VARCHAR(10) PRIMARY KEY,
    first_name VARCHAR(10) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    date_of_birth DATE,
    house_name VARCHAR(20),
    house_number VARCHAR(4),
    postal_code VARCHAR(8),
    num_policies INTEGER,
    phone_mobile VARCHAR(20),
    phone_home VARCHAR(20),
    email VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_customers_last_name ON customers (last_name);

INSERT INTO customers (id, first_name, last_name, date_of_birth, house_name, house_number, postal_code, num_policies, phone_mobile, phone_home, email) VALUES
  ('0000000001', 'ANDREW', 'PANDY', '1950-07-11', NULL, '34', 'PI101O', NULL, '01962 811234', '07799 123456', 'A.PANDY@BEEBHOUSE.COM'),
  ('0000000002', 'SCOTT', 'TRACEY', '1965-09-30', NULL, '1', 'TB14TV', NULL, '001 911911', NULL, 'REFROOM@TBHOLDINGS.COM'),
  ('0000000003', 'JOHN', 'NOAKES', '1934-03-06', NULL, '70', 'HX116B', NULL, '0207 325656', '09008 329855', 'Noaksey@beebhouse.com'),
  ('0000000004', 'LOUIE', 'PUG', '1969-09-06', NULL, '21', 'PP159D', NULL, '0208 344344', NULL, NULL),
  ('0000000005', 'GRAHAM', 'CUTHBERT', '1967-01-03', NULL, '55', 'TR68BK', NULL, '0208 344344', '01101 499787', 'A.Cut@CG.co.uk'),
  ('0000000006', 'BRIAN', 'CANT', '1933-07-12', NULL, '58', 'IP221P', NULL, '01855 122134', NULL, 'CANT@beebhouse.com'),
  ('0000000007', 'TROY', 'TEMPEST', '1964-10-04', NULL, '48', 'ST106R', NULL, NULL, NULL, 'TROYT@BUSCOMM.COM'),
  ('0000000008', 'JOHNNY', 'MORRIS', '1961-06-20', NULL, '72', 'BS83HA', NULL, '0345 245245', '0345 245245', 'JM@ZOOLAND.CO.UK'),
  ('0000000009', 'MICKY', 'MURPHY', '1966-01-03', NULL, '51', 'CA316R', NULL, NULL, NULL, NULL),
  ('0000000010', 'SUSAN', 'STRANKS', '1938-12-02', NULL, '68', 'W1A4WW', NULL, '0207 845845', NULL, NULL)
ON CONFLICT (id) DO NOTHING;
