# Anforderungsdokument: Neuentwicklung der Motor Policy

## Inhaltsverzeichnis

1. [Einführung](#1-einführung)
2. [Geschäftskontext](#2-geschäftskontext)
3. [Überblick über die Motor Policy](#3-überblick-über-die-motor-policy)
4. [Funktionale Anforderungen](#4-funktionale-anforderungen)
   - 4.1 [Datenmodell](#41-datenmodell)
     - 4.1.1 [Policy-Basisdaten](#411-policy-basisdaten)
     - 4.1.2 [Motor Policy spezifische Daten](#412-motor-policy-spezifische-daten)
   - 4.2 [Kernfunktionalitäten](#42-kernfunktionalitäten)
     - 4.2.1 [Erstellung einer Motor Policy](#421-erstellung-einer-motor-policy-createadd)
     - 4.2.2 [Abfrage einer Motor Policy](#422-abfrage-einer-motor-policy-inquiry)
     - 4.2.3 [Aktualisierung einer Motor Policy](#423-aktualisierung-einer-motor-policy-update)
     - 4.2.4 [Löschung einer Motor Policy](#424-löschung-einer-motor-policy-delete)
   - 4.3 [Benutzeroberfläche](#43-benutzeroberfläche)
5. [Technische Anforderungen](#5-technische-anforderungen)
   - 5.1 [Datenbankschema](#51-datenbankschema)
     - 5.1.1 [POLICY-Tabelle](#511-policy-tabelle)
     - 5.1.2 [MOTOR-Tabelle](#512-motor-tabelle)
   - 5.2 [API-Schnittstellen](#52-api-schnittstellen)
   - 5.3 [Validierungsregeln](#53-validierungsregeln)
   - 5.4 [Fehlerbehandlung und Logging](#54-fehlerbehandlung-und-logging)
6. [Nicht-funktionale Anforderungen](#6-nicht-funktionale-anforderungen)
   - 6.1 [Performance](#61-performance)
   - 6.2 [Skalierbarkeit](#62-skalierbarkeit)
   - 6.3 [Sicherheit](#63-sicherheit)
   - 6.4 [Verfügbarkeit](#64-verfügbarkeit)
7. [Integration](#7-integration)
   - 7.1 [Integration mit anderen Systemen](#71-integration-mit-anderen-systemen)
   - 7.2 [Migrationsstrategie](#72-migrationsstrategie)
8. [Testanforderungen](#8-testanforderungen)
9. [Implementierungshinweise](#9-implementierungshinweise)
   - 9.1 [Technologiestack-Empfehlungen](#91-technologiestack-empfehlungen)
   - 9.2 [Architektur-Empfehlungen](#92-architektur-empfehlungen)
10. [Zusammenfassung](#10-zusammenfassung)

## 1. Einführung

Dieses Dokument beschreibt die Anforderungen für eine Neuentwicklung des "Motor Policy"-Moduls innerhalb des GenApp-Versicherungssystems. Die aktuelle Implementierung basiert auf einer COBOL-Anwendung in einer CICS-Umgebung mit DB2-Datenbankanbindung.

## 2. Geschäftskontext

Die Motor Policy ist Teil eines Versicherungsverwaltungssystems und bietet Funktionen zur Verwaltung von Kraftfahrzeugversicherungspolicen. Sie ermöglicht die Erstellung, Abfrage, Aktualisierung und Löschung von Kfz-Versicherungspolicen für Kunden.

## 3. Überblick über die Motor Policy

Die Motor Policy ist eine spezifische Art von Versicherungspolice im System, die Daten und Logik für Kfz-Versicherungen bereitstellt. Sie ist eng mit dem Kundendatensatz verknüpft und enthält spezifische Informationen zum versicherten Fahrzeug.

## 4. Funktionale Anforderungen

### 4.1 Datenmodell

#### 4.1.1 Policy-Basisdaten
- Kundennummer (Customer Number)
- Policennummer (Policy Number)
- Ausstellungsdatum (Issue Date)
- Ablaufdatum (Expiry Date)
- Letzte Änderung (Last Changed)
- Makler-ID (Broker ID)
- Makler-Referenz (Broker Reference)
- Zahlungsbetrag (Payment)

#### 4.1.2 Motor Policy spezifische Daten
- Fahrzeugmarke (Make) - max. 15 Zeichen
- Fahrzeugmodell (Model) - max. 15 Zeichen
- Fahrzeugwert (Value) - numerisch, bis zu 6 Stellen
- Kennzeichen (Registration Number) - max. 7 Zeichen
- Fahrzeugfarbe (Colour) - max. 8 Zeichen
- Hubraum (CC) - numerisch, bis zu 4 Stellen
- Herstellungsjahr/Datum (Manufactured) - max. 10 Zeichen
- Versicherungsprämie (Premium) - numerisch, bis zu 6 Stellen
- Anzahl Unfälle (Accidents) - numerisch, bis zu 6 Stellen

### 4.2 Kernfunktionalitäten

#### 4.2.1 Erstellung einer Motor Policy (Create/Add)
**Eingabewerte:**
- Kundennummer (muss existieren)
- Ausstellungsdatum
- Ablaufdatum
- Fahrzeugmarke
- Fahrzeugmodell
- Fahrzeugwert
- Kennzeichen
- Fahrzeugfarbe
- Hubraum
- Herstellungsjahr
- Versicherungsprämie
- Anzahl Unfälle

**Validierungen:**
- Kundennummer muss existieren (Fehlercode 70, wenn nicht vorhanden)
- Alle Pflichtfelder müssen ausgefüllt sein
- Numerische Felder müssen gültige Zahlenwerte enthalten

**Prozessablauf:**
1. Prüfung, ob der Kunde existiert
2. Erzeugung eines neuen Policy-Eintrags in der POLICY-Tabelle
3. Erzeugung eines neuen Motor-Policy-Eintrags in der MOTOR-Tabelle
4. Verknüpfung beider Einträge über die Policy-Nummer
5. Rückgabe der Policy-Nummer an den Benutzer

**Ergebnis:**
- Erfolgreich: Neue Motor Policy wurde erstellt (Rückgabecode '00')
- Fehler: Kundennummer existiert nicht (Rückgabecode '70')
- Fehler: Allgemeiner Datenbankfehler (Rückgabecode '90')

#### 4.2.2 Abfrage einer Motor Policy (Inquiry)
**Eingabewerte:**
- Kundennummer
- Policennummer

**Prozessablauf:**
1. Join zwischen POLICY und MOTOR-Tabellen
2. Abfrage der Daten basierend auf Kunden- und Policennummer

**Ergebnis:**
- Vollständige Motor Policy-Informationen oder Fehlermeldung
- Bei Erfolg: Alle Felder der Motor Policy
- Bei Fehler: Entsprechender Fehlercode ('01' für nicht gefunden, '90' für Datenbankfehler)

#### 4.2.3 Aktualisierung einer Motor Policy (Update)
**Eingabewerte:**
- Alle Felder wie bei der Erstellung
- Kundennummer und Policennummer zur Identifizierung

**Validierungen:**
- Kunden- und Policennummer müssen existieren
- Alle Pflichtfelder müssen ausgefüllt sein

**Prozessablauf:**
1. Aktualisierung der Daten in der MOTOR-Tabelle
2. Aktualisierung der allgemeinen Policendaten in der POLICY-Tabelle

**Ergebnis:**
- Erfolgreich: Aktualisierte Motor Policy (Rückgabecode '00')
- Fehler: Policy nicht gefunden (Rückgabecode '01')
- Fehler: Datenbankfehler (Rückgabecode '90')

#### 4.2.4 Löschung einer Motor Policy (Delete)
**Eingabewerte:**
- Kundennummer
- Policennummer

**Prozessablauf:**
1. Löschung des Eintrags aus der MOTOR-Tabelle
2. Löschung des zugehörigen Eintrags aus der POLICY-Tabelle

**Ergebnis:**
- Erfolgreich: Gelöschte Motor Policy (Rückgabecode '00')
- Fehler: Policy nicht gefunden (Rückgabecode '01')
- Fehler: Datenbankfehler (Rückgabecode '90')

### 4.3 Benutzeroberfläche

Die Anwendung sollte eine moderne, intuitive Benutzeroberfläche mit folgenden Ansichten bieten:
1. Übersichtsseite für alle Motor Policies eines Kunden
2. Detailansicht für eine einzelne Motor Policy
3. Bearbeitungsformular für die Erstellung und Aktualisierung einer Motor Policy
4. Bestätigungsdialog für das Löschen einer Policy

## 5. Technische Anforderungen

### 5.1 Datenbankschema

#### 5.1.1 POLICY-Tabelle
```sql
CREATE TABLE POLICY (
  POLICYNUMBER INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  CUSTOMERNUMBER INTEGER NOT NULL,
  ISSUEDATE DATE NOT NULL,
  EXPIRYDATE DATE NOT NULL,
  POLICYTYPE CHAR(1) NOT NULL,
  LASTCHANGED TIMESTAMP NOT NULL DEFAULT CURRENT TIMESTAMP,
  BROKERID INTEGER,
  BROKERSREFERENCE VARCHAR(10),
  PAYMENT INTEGER,
  FOREIGN KEY (CUSTOMERNUMBER) REFERENCES CUSTOMER(CUSTOMERNUMBER)
);
```

#### 5.1.2 MOTOR-Tabelle
```sql
CREATE TABLE MOTOR (
  POLICYNUMBER INTEGER PRIMARY KEY,
  MAKE VARCHAR(15) NOT NULL,
  MODEL VARCHAR(15) NOT NULL,
  VALUE INTEGER NOT NULL,
  REGNUMBER VARCHAR(7) NOT NULL,
  COLOUR VARCHAR(8) NOT NULL,
  CC INTEGER NOT NULL,
  YEAROFMANUFACTURE VARCHAR(10) NOT NULL,
  PREMIUM INTEGER NOT NULL,
  ACCIDENTS INTEGER NOT NULL,
  FOREIGN KEY (POLICYNUMBER) REFERENCES POLICY(POLICYNUMBER)
);
```

### 5.2 API-Schnittstellen

Die Neuentwicklung sollte moderne REST-API-Schnittstellen für alle Kernfunktionalitäten bereitstellen:

#### 5.2.1 Motor Policy erstellen
- `POST /api/customers/{customerId}/policies/motor`

#### 5.2.2 Motor Policy abfragen
- `GET /api/customers/{customerId}/policies/motor/{policyId}`
- `GET /api/customers/{customerId}/policies/motor` (alle Motor Policies eines Kunden)

#### 5.2.3 Motor Policy aktualisieren
- `PUT /api/customers/{customerId}/policies/motor/{policyId}`

#### 5.2.4 Motor Policy löschen
- `DELETE /api/customers/{customerId}/policies/motor/{policyId}`

### 5.3 Validierungsregeln

1. Kundennummer muss existieren
2. Pflichtfelder (Make, Model, Value, Registration Number, CC, Manufacturing Year)
3. Validierung der numerischen Werte:
   - Value: Positiver Wert ≤ 999999
   - CC: Positiver Wert ≤ 9999
   - Premium: Positiver Wert ≤ 999999
   - Accidents: Wert ≥ 0 und ≤ 999999
4. Registration Number: Format entsprechend den lokalen Kennzeichenvorschriften

### 5.4 Fehlerbehandlung und Logging

- Fehlerbehandlung mit standardisierten Fehlercodes und Fehlermeldungen
- Logging aller Transaktionen mit detaillierten Informationen (Zeitstempel, User, Operation, Daten)
- Auditing wichtiger Änderungen (Erstellung, Aktualisierung, Löschung)

## 6. Nicht-funktionale Anforderungen

### 6.1 Performance
- Antwortzeiten unter 500ms für alle API-Aufrufe
- Unterstützung von mindestens 100 gleichzeitigen Benutzern

### 6.2 Skalierbarkeit
- Horizontale Skalierung der Anwendung
- Datenbankoptimierung für große Datenmengen

### 6.3 Sicherheit
- Authentifizierung und Autorisierung für alle API-Endpunkte
- Verschlüsselung sensibler Daten
- Schutz vor SQL-Injection und anderen gängigen Sicherheitsrisiken

### 6.4 Verfügbarkeit
- 99,9% Verfügbarkeit (High Availability)
- Disaster Recovery mit RTO < 1 Stunde und RPO < 5 Minuten

## 7. Integration

### 7.1 Integration mit anderen Systemen
- Kundenmanagementsystem
- Zahlungssystem
- Berichtssystem

### 7.2 Migrationsstrategie
- Schrittweise Migration von der bestehenden COBOL-Anwendung
- Datenmigrationsplan für vorhandene Motor Policies

## 8. Testanforderungen

- Unit-Tests für alle Geschäftslogik-Komponenten
- Integrationstests für API-Schnittstellen
- Last- und Performancetests unter simulierten Produktionsbedingungen
- Sicherheitstests (Penetrationstests)

## 9. Implementierungshinweise

### 9.1 Technologiestack-Empfehlungen
- Backend: Java/Spring Boot oder Node.js/Express
- Frontend: React oder Angular
- Datenbank: PostgreSQL oder MongoDB
- API-Management: Swagger/OpenAPI

### 9.2 Architektur-Empfehlungen
- Microservice-Architektur
- Event-driven Ansatz mit Message-Queuing für asynchrone Prozesse
- Containerisierung mit Docker und Orchestrierung mit Kubernetes

## 10. Zusammenfassung

Die Neuentwicklung der Motor Policy soll das bestehende COBOL-basierte System modernisieren und durch eine flexible, skalierbare und wartbare Lösung ersetzen. Die Kernfunktionalitäten der Erstellung, Abfrage, Aktualisierung und Löschung von Kfz-Versicherungspolicen bleiben erhalten, werden jedoch mit modernen Technologien und Best Practices umgesetzt, um den aktuellen und zukünftigen Anforderungen gerecht zu werden.
