-- Motor Policy Testdaten Generierung und Validierung
-- Dieses Skript hilft bei der Überprüfung der Datenbankstrukturen und bei der Generierung von Testdaten

-- 1. Struktur-Validierung
-- Prüfen der Tabellendefinitionen in beiden Systemen

-- Altsystem
CONNECT TO OLD_DB;

SELECT TABNAME, COLNAME, TYPENAME, LENGTH, SCALE, NULLS
FROM SYSCAT.COLUMNS
WHERE TABNAME = 'POLICY'
   OR TABNAME = 'MOTOR'
ORDER BY TABNAME, COLNO;

-- Neusystem
CONNECT TO NEW_DB;

SELECT TABNAME, COLNAME, TYPENAME, LENGTH, SCALE, NULLS
FROM SYSCAT.COLUMNS
WHERE TABNAME = 'POLICY'
   OR TABNAME = 'MOTOR'
ORDER BY TABNAME, COLNO;

-- 2. Testdaten-Generierung

-- Altsystem: Kundendaten für Tests anlegen
CONNECT TO OLD_DB;

INSERT INTO CUSTOMER (
    CUSTOMERNUMBER,
    FIRSTNAME,
    LASTNAME,
    DATEOFBIRTH,
    HOUSENAME,
    HOUSENUMBER,
    POSTCODE,
    PHONE_MOBILE,
    PHONE_HOME,
    EMAIL_ADDRESS
) VALUES (
    1234567890,
    'Max',
    'Mustermann',
    '1980-01-01',
    'Testhaus',
    '123',
    '12345',
    '01701234567',
    '0301234567',
    'max.mustermann@test.de'
);

-- Neusystem: Gleiche Kundendaten anlegen
CONNECT TO NEW_DB;

INSERT INTO CUSTOMER (
    CUSTOMERNUMBER,
    FIRSTNAME,
    LASTNAME,
    DATEOFBIRTH,
    HOUSENAME,
    HOUSENUMBER,
    POSTCODE,
    PHONE_MOBILE,
    PHONE_HOME,
    EMAIL_ADDRESS
) VALUES (
    1234567890,
    'Max',
    'Mustermann',
    '1980-01-01',
    'Testhaus',
    '123',
    '12345',
    '01701234567',
    '0301234567',
    'max.mustermann@test.de'
);

-- 3. Testdaten für bestehende Policen erstellen (für Update- und Delete-Tests)

-- Altsystem: Motor Policy anlegen
CONNECT TO OLD_DB;

-- Policy-Haupteintrag
INSERT INTO POLICY (
    POLICYNUMBER,
    CUSTOMERNUMBER,
    POLICYTYPE,
    ISSUEDATE,
    EXPIRYDATE,
    LASTCHANGED,
    BROKERID,
    BROKERSREFERENCE,
    PAYMENT
) VALUES (
    1000000001,
    1234567890,
    'M',
    '2023-01-01',
    '2024-01-01',
    CURRENT_TIMESTAMP,
    1234567890,
    'BRREF12345',
    120000
);

-- Motor-spezifische Daten
INSERT INTO MOTOR (
    POLICYNUMBER,
    MAKE,
    MODEL,
    VALUE,
    REGISTRATION,
    COLOUR,
    CC,
    YEAROFMANUFACTURE,
    PREMIUM,
    ACCIDENTS
) VALUES (
    1000000001,
    'Volkswagen',
    'Golf',
    250000,
    'K-AB-123',
    'Rot',
    1998,
    '2022-01-15',
    120000,
    0
);

-- Neusystem: Gleiche Motor Policy anlegen
CONNECT TO NEW_DB;

-- Policy-Haupteintrag
INSERT INTO POLICY (
    POLICYNUMBER,
    CUSTOMERNUMBER,
    POLICYTYPE,
    ISSUEDATE,
    EXPIRYDATE,
    LASTCHANGED,
    BROKERID,
    BROKERSREFERENCE,
    PAYMENT
) VALUES (
    1000000001,
    1234567890,
    'M',
    '2023-01-01',
    '2024-01-01',
    CURRENT_TIMESTAMP,
    1234567890,
    'BRREF12345',
    120000
);

-- Motor-spezifische Daten
INSERT INTO MOTOR (
    POLICYNUMBER,
    MAKE,
    MODEL,
    VALUE,
    REGISTRATION,
    COLOUR,
    CC,
    YEAROFMANUFACTURE,
    PREMIUM,
    ACCIDENTS
) VALUES (
    1000000001,
    'Volkswagen',
    'Golf',
    250000,
    'K-AB-123',
    'Rot',
    1998,
    '2022-01-15',
    120000,
    0
);

-- 4. Validierungsprozeduren für Paralleltests

-- Altsystem: Prozedur zum Prüfen einer Motorpolicy
CONNECT TO OLD_DB;

CREATE OR REPLACE PROCEDURE VALIDATE_MOTOR_POLICY (
    IN p_policy_num BIGINT,
    OUT p_exists INT,
    OUT p_policy_data VARCHAR(1000)
)
LANGUAGE SQL
BEGIN
    DECLARE v_count INT DEFAULT 0;
    DECLARE v_data VARCHAR(1000) DEFAULT '';
    
    SELECT COUNT(*)
    INTO v_count
    FROM POLICY P
    JOIN MOTOR M ON P.POLICYNUMBER = M.POLICYNUMBER
    WHERE P.POLICYNUMBER = p_policy_num;
    
    IF v_count > 0 THEN
        SET p_exists = 1;
        
        SELECT CONCAT(
            'TYPE:', P.POLICYTYPE,
            ';CUSTOMER:', CAST(P.CUSTOMERNUMBER AS VARCHAR(10)),
            ';MAKE:', M.MAKE, 
            ';MODEL:', M.MODEL,
            ';VALUE:', CAST(M.VALUE AS VARCHAR(10)),
            ';REG:', M.REGISTRATION,
            ';COLOUR:', M.COLOUR,
            ';CC:', CAST(M.CC AS VARCHAR(10)),
            ';YEAR:', M.YEAROFMANUFACTURE,
            ';PREMIUM:', CAST(M.PREMIUM AS VARCHAR(10)),
            ';ACCIDENTS:', CAST(M.ACCIDENTS AS VARCHAR(10))
        )
        INTO v_data
        FROM POLICY P
        JOIN MOTOR M ON P.POLICYNUMBER = M.POLICYNUMBER
        WHERE P.POLICYNUMBER = p_policy_num;
        
        SET p_policy_data = v_data;
    ELSE
        SET p_exists = 0;
        SET p_policy_data = '';
    END IF;
END;

-- Neusystem: Gleiche Prozedur zum Prüfen einer Motorpolicy
CONNECT TO NEW_DB;

CREATE OR REPLACE PROCEDURE VALIDATE_MOTOR_POLICY (
    IN p_policy_num BIGINT,
    OUT p_exists INT,
    OUT p_policy_data VARCHAR(1000)
)
LANGUAGE SQL
BEGIN
    DECLARE v_count INT DEFAULT 0;
    DECLARE v_data VARCHAR(1000) DEFAULT '';
    
    SELECT COUNT(*)
    INTO v_count
    FROM POLICY P
    JOIN MOTOR M ON P.POLICYNUMBER = M.POLICYNUMBER
    WHERE P.POLICYNUMBER = p_policy_num;
    
    IF v_count > 0 THEN
        SET p_exists = 1;
        
        SELECT CONCAT(
            'TYPE:', P.POLICYTYPE,
            ';CUSTOMER:', CAST(P.CUSTOMERNUMBER AS VARCHAR(10)),
            ';MAKE:', M.MAKE, 
            ';MODEL:', M.MODEL,
            ';VALUE:', CAST(M.VALUE AS VARCHAR(10)),
            ';REG:', M.REGISTRATION,
            ';COLOUR:', M.COLOUR,
            ';CC:', CAST(M.CC AS VARCHAR(10)),
            ';YEAR:', M.YEAROFMANUFACTURE,
            ';PREMIUM:', CAST(M.PREMIUM AS VARCHAR(10)),
            ';ACCIDENTS:', CAST(M.ACCIDENTS AS VARCHAR(10))
        )
        INTO v_data
        FROM POLICY P
        JOIN MOTOR M ON P.POLICYNUMBER = M.POLICYNUMBER
        WHERE P.POLICYNUMBER = p_policy_num;
        
        SET p_policy_data = v_data;
    ELSE
        SET p_exists = 0;
        SET p_policy_data = '';
    END IF;
END;

-- 5. Datenbank-Vergleichsskript

-- Skript zum Vergleichen aller Motorpolicen zwischen Alt- und Neusystem
CONNECT TO OLD_DB;

-- Temporäre Tabelle für Altsystem-Daten
CREATE GLOBAL TEMPORARY TABLE TEMP_OLD_MOTOR_POLICIES (
    POLICYNUMBER BIGINT,
    POLICY_DATA VARCHAR(1000)
) ON COMMIT PRESERVE ROWS;

-- Alte Motorpolicen in temporäre Tabelle einfügen
INSERT INTO SESSION.TEMP_OLD_MOTOR_POLICIES
SELECT P.POLICYNUMBER,
       CONCAT(
           'TYPE:', P.POLICYTYPE,
           ';CUSTOMER:', CAST(P.CUSTOMERNUMBER AS VARCHAR(10)),
           ';MAKE:', M.MAKE, 
           ';MODEL:', M.MODEL,
           ';VALUE:', CAST(M.VALUE AS VARCHAR(10)),
           ';REG:', M.REGISTRATION,
           ';COLOUR:', M.COLOUR,
           ';CC:', CAST(M.CC AS VARCHAR(10)),
           ';YEAR:', M.YEAROFMANUFACTURE,
           ';PREMIUM:', CAST(M.PREMIUM AS VARCHAR(10)),
           ';ACCIDENTS:', CAST(M.ACCIDENTS AS VARCHAR(10))
       )
FROM POLICY P
JOIN MOTOR M ON P.POLICYNUMBER = M.POLICYNUMBER
WHERE P.POLICYTYPE = 'M';

-- Temporäre Tabelle für Neusystem-Daten
CONNECT TO NEW_DB;

CREATE GLOBAL TEMPORARY TABLE TEMP_NEW_MOTOR_POLICIES (
    POLICYNUMBER BIGINT,
    POLICY_DATA VARCHAR(1000)
) ON COMMIT PRESERVE ROWS;

-- Neue Motorpolicen in temporäre Tabelle einfügen
INSERT INTO SESSION.TEMP_NEW_MOTOR_POLICIES
SELECT P.POLICYNUMBER,
       CONCAT(
           'TYPE:', P.POLICYTYPE,
           ';CUSTOMER:', CAST(P.CUSTOMERNUMBER AS VARCHAR(10)),
           ';MAKE:', M.MAKE, 
           ';MODEL:', M.MODEL,
           ';VALUE:', CAST(M.VALUE AS VARCHAR(10)),
           ';REG:', M.REGISTRATION,
           ';COLOUR:', M.COLOUR,
           ';CC:', CAST(M.CC AS VARCHAR(10)),
           ';YEAR:', M.YEAROFMANUFACTURE,
           ';PREMIUM:', CAST(M.PREMIUM AS VARCHAR(10)),
           ';ACCIDENTS:', CAST(M.ACCIDENTS AS VARCHAR(10))
       )
FROM POLICY P
JOIN MOTOR M ON P.POLICYNUMBER = M.POLICYNUMBER
WHERE P.POLICYTYPE = 'M';

-- Vergleichstabelle (erfordert Federation zwischen den Datenbanken)
CREATE GLOBAL TEMPORARY TABLE POLICY_COMPARISON (
    POLICYNUMBER BIGINT,
    EXISTS_IN_OLD CHAR(1),
    EXISTS_IN_NEW CHAR(1),
    DATA_MATCHES CHAR(1),
    OLD_DATA VARCHAR(1000),
    NEW_DATA VARCHAR(1000)
) ON COMMIT PRESERVE ROWS;

-- Alle Policen aus beiden Systemen einfügen und vergleichen
INSERT INTO SESSION.POLICY_COMPARISON
SELECT COALESCE(O.POLICYNUMBER, N.POLICYNUMBER) AS POLICYNUMBER,
       CASE WHEN O.POLICYNUMBER IS NOT NULL THEN 'Y' ELSE 'N' END AS EXISTS_IN_OLD,
       CASE WHEN N.POLICYNUMBER IS NOT NULL THEN 'Y' ELSE 'N' END AS EXISTS_IN_NEW,
       CASE WHEN O.POLICY_DATA = N.POLICY_DATA THEN 'Y' ELSE 'N' END AS DATA_MATCHES,
       O.POLICY_DATA AS OLD_DATA,
       N.POLICY_DATA AS NEW_DATA
FROM SESSION.TEMP_OLD_MOTOR_POLICIES O
FULL OUTER JOIN SESSION.TEMP_NEW_MOTOR_POLICIES N
ON O.POLICYNUMBER = N.POLICYNUMBER;

-- Ergebnisse ausgeben
SELECT * FROM SESSION.POLICY_COMPARISON;

-- Zusammenfassung ausgeben
SELECT 
    COUNT(*) AS TOTAL_POLICIES,
    SUM(CASE WHEN EXISTS_IN_OLD = 'Y' AND EXISTS_IN_NEW = 'Y' THEN 1 ELSE 0 END) AS IN_BOTH_SYSTEMS,
    SUM(CASE WHEN EXISTS_IN_OLD = 'Y' AND EXISTS_IN_NEW = 'N' THEN 1 ELSE 0 END) AS ONLY_IN_OLD,
    SUM(CASE WHEN EXISTS_IN_OLD = 'N' AND EXISTS_IN_NEW = 'Y' THEN 1 ELSE 0 END) AS ONLY_IN_NEW,
    SUM(CASE WHEN DATA_MATCHES = 'Y' THEN 1 ELSE 0 END) AS MATCHING_DATA,
    SUM(CASE WHEN DATA_MATCHES = 'N' THEN 1 ELSE 0 END) AS DIFFERENT_DATA
FROM SESSION.POLICY_COMPARISON;
