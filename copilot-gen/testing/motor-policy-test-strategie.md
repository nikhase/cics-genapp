# Teststrategie für die Modernisierung der "Motor Policy"

## 1. Überblick über das zu testende System

Die Motor Policy ist ein zentraler Bestandteil des GenApp Versicherungssystems. Aus den untersuchten Quelldateien geht hervor, dass die Motor Policy folgende Kernfunktionalitäten umfasst:
- Hinzufügen neuer Kfz-Versicherungspolicen (Add Policy)
- Aktualisieren bestehender Policen (Update Policy)
- Löschen von Policen (Delete Policy)
- Abfragen von Policen (Inquire Policy)

Die Hauptkomponenten sind:
- `LGAPOL01` - Business Logic für Motor Policy Operationen
- `LGAPDB01` - DB2 Datenbankzugriffe für Motor Policy
- `LGAPVS01` - VSAM Datenzugriffe für Motor Policy
- `LGPOLICY.CPY` und `LGCMAREA.CPY` - Datenstrukturen

## 2. Testansatz

### 2.1 Testphasen

1. **Komponententests**
   - Isolierte Tests der einzelnen Komponenten des neuen Systems
   - Überprüfung der korrekten Implementierung der Geschäftslogik

2. **Integrationstests**
   - Testen der Zusammenarbeit der verschiedenen Komponenten
   - Überprüfung der Schnittstellen zwischen Frontend, Business-Logic und Datenbank

3. **Systemtests**
   - End-to-End-Tests des gesamten Motor Policy Subsystems
   - Überprüfung der Geschäftsprozesse von Anfang bis Ende

4. **Regressionstests**
   - Sicherstellen, dass bestehende Funktionalitäten nach Änderungen weiterhin korrekt arbeiten

5. **Paralleltests**
   - Gleichzeitige Verwendung des alten und neuen Systems mit identischen Eingaben
   - Vergleich der Ergebnisse zur Validierung der Funktionsgleichheit

### 2.2 Test-Automatisierung

1. **Unit-Tests**
   - Automatisierte Komponententests für alle Geschäftslogikfunktionen
   - Mockups für Datenbankzugriffe

2. **API-Tests**
   - Automatisierte Tests für alle API-Endpunkte des neuen Systems
   - Verifizierung der korrekten Datenverarbeitung

3. **UI-Tests**
   - Automatisierte Tests der Benutzeroberfläche
   - Sicherstellen, dass alle UI-Funktionen korrekt arbeiten

## 3. Testfälle für Motor Policy

### 3.1 Hauptfunktionen zu testen

#### 3.1.1 Add Motor Policy (Hinzufügen einer Kfz-Versicherung)

**Test 1: Erfolgreiche Erstellung einer neuen Kfz-Versicherung**
- **Eingabe:**
  - Kundennummer (CA-CUSTOMER-NUM)
  - Fahrzeugdaten:
    - Marke (CA-M-MAKE): "Volkswagen"
    - Modell (CA-M-MODEL): "Golf"
    - Wert (CA-M-VALUE): 250000
    - Kennzeichen (CA-M-REGNUMBER): "K-AB-123"
    - Farbe (CA-M-COLOUR): "Rot"
    - Hubraum (CA-M-CC): 1998
    - Baujahr (CA-M-MANUFACTURED): "2022-01-15"
    - Prämie (CA-M-PREMIUM): 120000
    - Unfälle (CA-M-ACCIDENTS): 0
  - Policendaten:
    - Ausstellungsdatum (CA-ISSUE-DATE): "2023-01-01"
    - Ablaufdatum (CA-EXPIRY-DATE): "2024-01-01"
    - Broker-ID (CA-BROKERID): 1234567890
    - Broker-Referenz (CA-BROKERSREF): "BRREF12345"
    - Zahlung (CA-PAYMENT): 120000
- **Erwartetes Ergebnis:**
  - Rückgabecode (CA-RETURN-CODE): "00"
  - Neue Policennummer wurde generiert
  - Bestätigungsmeldung "New Motor Policy Inserted"
  - Datensatz in der Datenbank vorhanden

**Test 2: Fehler beim Hinzufügen einer Police wegen fehlender Pflichtfelder**
- **Eingabe:**
  - Kundennummer (CA-CUSTOMER-NUM)
  - Unvollständige Fahrzeugdaten (z.B. fehlendes Kennzeichen)
- **Erwartetes Ergebnis:**
  - Rückgabecode ungleich "00"
  - Fehlermeldung "Error Adding Motor Policy"

**Test 3: Fehler beim Hinzufügen einer Police mit dupliziertem Kennzeichen**
- **Eingabe:**
  - Vollständige Daten wie in Test 1, aber mit bereits existierendem Kennzeichen
- **Erwartetes Ergebnis:**
  - Rückgabecode ungleich "00"
  - Fehlermeldung "Error Adding Motor Policy"

#### 3.1.2 Update Motor Policy (Aktualisieren einer Kfz-Versicherung)

**Test 4: Erfolgreiche Aktualisierung einer bestehenden Police**
- **Eingabe:**
  - Kundennummer (CA-CUSTOMER-NUM)
  - Policennummer (CA-POLICY-NUM) einer bestehenden Motor Policy
  - Zu aktualisierende Daten:
    - Wert (CA-M-VALUE): 280000 (erhöht)
    - Farbe (CA-M-COLOUR): "Blau" (geändert)
    - Prämie (CA-M-PREMIUM): 135000 (erhöht)
- **Erwartetes Ergebnis:**
  - Rückgabecode (CA-RETURN-CODE): "00"
  - Bestätigungsmeldung "Motor Policy Updated"
  - Aktualisierte Daten in der Datenbank

**Test 5: Fehler beim Aktualisieren einer nicht existierenden Police**
- **Eingabe:**
  - Kundennummer (CA-CUSTOMER-NUM)
  - Nicht existierende Policennummer (CA-POLICY-NUM)
- **Erwartetes Ergebnis:**
  - Rückgabecode ungleich "00"
  - Fehlermeldung "Error Updating Motor Policy"

#### 3.1.3 Delete Motor Policy (Löschen einer Kfz-Versicherung)

**Test 6: Erfolgreiches Löschen einer bestehenden Police**
- **Eingabe:**
  - Kundennummer (CA-CUSTOMER-NUM)
  - Policennummer (CA-POLICY-NUM) einer bestehenden Motor Policy
- **Erwartetes Ergebnis:**
  - Rückgabecode (CA-RETURN-CODE): "00"
  - Bestätigungsmeldung "Motor Policy Deleted"
  - Datensatz nicht mehr in der Datenbank vorhanden

**Test 7: Fehler beim Löschen einer nicht existierenden Police**
- **Eingabe:**
  - Kundennummer (CA-CUSTOMER-NUM)
  - Nicht existierende Policennummer (CA-POLICY-NUM)
- **Erwartetes Ergebnis:**
  - Rückgabecode ungleich "00"
  - Fehlermeldung "Error Deleting Motor Policy"

#### 3.1.4 Inquire Motor Policy (Abfragen einer Kfz-Versicherung)

**Test 8: Erfolgreiche Abfrage einer bestehenden Police**
- **Eingabe:**
  - Kundennummer (CA-CUSTOMER-NUM)
  - Policennummer (CA-POLICY-NUM) einer bestehenden Motor Policy
- **Erwartetes Ergebnis:**
  - Rückgabecode (CA-RETURN-CODE): "00"
  - Alle Policendaten werden korrekt zurückgegeben

**Test 9: Fehler bei Abfrage einer nicht existierenden Police**
- **Eingabe:**
  - Kundennummer (CA-CUSTOMER-NUM)
  - Nicht existierende Policennummer (CA-POLICY-NUM)
- **Erwartetes Ergebnis:**
  - Rückgabecode ungleich "00"
  - Keine oder leere Policendaten

### 3.2 Grenzwerttests

**Test 10: Maximale Werte für numerische Felder**
- **Eingabe:**
  - Wert (CA-M-VALUE): 999999 (maximaler Wert)
  - Hubraum (CA-M-CC): 9999 (maximaler Wert)
  - Prämie (CA-M-PREMIUM): 999999 (maximaler Wert)
- **Erwartetes Ergebnis:**
  - Rückgabecode (CA-RETURN-CODE): "00"
  - Daten werden korrekt gespeichert und abgerufen

**Test 11: Maximale Länge für Textfelder**
- **Eingabe:**
  - Marke (CA-M-MAKE): 15 Zeichen (maximal)
  - Modell (CA-M-MODEL): 15 Zeichen (maximal)
  - Kennzeichen (CA-M-REGNUMBER): 7 Zeichen (maximal)
  - Farbe (CA-M-COLOUR): 8 Zeichen (maximal)
- **Erwartetes Ergebnis:**
  - Rückgabecode (CA-RETURN-CODE): "00"
  - Daten werden korrekt gespeichert und abgerufen

### 3.3 Fehlerbehandlungstests

**Test 12: Ungültige Datumsformate**
- **Eingabe:**
  - Ausstellungsdatum (CA-ISSUE-DATE): "31-02-2023" (ungültiges Datum)
  - Ablaufdatum (CA-EXPIRY-DATE): "2024/01/01" (falsches Format)
- **Erwartetes Ergebnis:**
  - Fehlerhafte Datumswerte werden erkannt und behandelt
  - Entsprechende Fehlermeldung wird ausgegeben

**Test 13: Ungültige Zeichen in Textfeldern**
- **Eingabe:**
  - Kennzeichen mit Sonderzeichen: "K@AB*123"
- **Erwartetes Ergebnis:**
  - Validierungsfehler wird erkannt
  - Entsprechende Fehlermeldung wird ausgegeben

## 4. Testdaten-Management

### 4.1 Testdatengenerierung

1. **Basisdatensatz**
   - Erstellung eines Grundbestands an Motor Policies für jeden Testtyp
   - Sicherstellen einer ausreichenden Diversität an Daten

2. **Testdaten-Variationen**
   - Systematische Variation der Eingabeparameter
   - Generierung spezieller Testfälle für Grenzwerte und Fehlerszenarien

### 4.2 Beispiel-Testdaten

**Testdatenset 1: Standarddaten für Add/Update/Delete/Inquire**

```
Kundennummer: 1234567890
Policennummer: (wird vom System generiert)
Marke: Volkswagen
Modell: Golf
Wert: 250000
Kennzeichen: K-AB-123
Farbe: Rot
Hubraum: 1998
Baujahr: 2022-01-15
Prämie: 120000
Unfälle: 0
Ausstellungsdatum: 2023-01-01
Ablaufdatum: 2024-01-01
Broker-ID: 1234567890
Broker-Referenz: BRREF12345
Zahlung: 120000
```

**Testdatenset 2: Grenzwerte**

```
Kundennummer: 9999999999
Policennummer: (wird vom System generiert)
Marke: AAAAABBBBBCCCCC (15 Zeichen)
Modell: DDDDDEEEEEFFFF (15 Zeichen)
Wert: 999999
Kennzeichen: ABCDEFG (7 Zeichen)
Farbe: ABCDEFGH (8 Zeichen)
Hubraum: 9999
Baujahr: 2023-12-31
Prämie: 999999
Unfälle: 999999
Ausstellungsdatum: 2023-01-01
Ablaufdatum: 2099-12-31
Broker-ID: 9999999999
Broker-Referenz: ABCDEFGHIJ (10 Zeichen)
Zahlung: 999999
```

**Testdatenset 3: Fehlerfälle**

```
Kundennummer: 1234567890
Policennummer: 9876543210 (nicht existent)
Marke: <leer>
Modell: Golf
Wert: -1
Kennzeichen: <leer>
Farbe: Rot
Hubraum: 12345 (zu groß)
Baujahr: 2022-13-15 (ungültig)
Prämie: 1200000 (zu groß)
Unfälle: -5 (negativ)
Ausstellungsdatum: 01/01/2023 (falsches Format)
Ablaufdatum: 31-02-2024 (ungültiges Datum)
Broker-ID: ABC (nicht numerisch)
Broker-Referenz: ABCDEFGHIJK (zu lang)
Zahlung: -100 (negativ)
```

## 5. Vergleichstests zwischen altem und neuem System

### 5.1 Paralleltest-Strategie

1. **Identische Eingaben**
   - Durchführung der gleichen Operation mit identischen Eingabedaten in beiden Systemen
   - Aufzeichnung und Vergleich der Ausgaben und Datenbankänderungen

2. **Datenbankzustandsvergleich**
   - Vergleich des Datenbankzustands nach jeder Operation
   - Sicherstellen, dass beide Systeme die gleichen Änderungen vornehmen

3. **Performance-Vergleich**
   - Messen der Antwortzeiten für identische Operationen
   - Vergleich der Leistungskennzahlen zwischen altem und neuem System

### 5.2 Vergleichstabelle für Testergebnisse

Folgende Tabelle kann für die systematische Erfassung der Testergebnisse verwendet werden:

| Test-ID | Testfall | Erwartetes Ergebnis | Ergebnis Altsystem | Ergebnis Neusystem | Übereinstimmung | Anmerkungen |
|---------|----------|---------------------|-------------------|-------------------|----------------|-------------|
| MP-A-001 | Add Motor Policy (Standard) | Erfolg, RC=00 | | | | |
| MP-A-002 | Add Motor Policy (fehlende Daten) | Fehler | | | | |
| MP-U-001 | Update Motor Policy (Standard) | Erfolg, RC=00 | | | | |
| MP-D-001 | Delete Motor Policy (Standard) | Erfolg, RC=00 | | | | |
| MP-I-001 | Inquire Motor Policy (Standard) | Erfolg, RC=00 | | | | |
| MP-G-001 | Grenzwerte (Max) | Erfolg, RC=00 | | | | |
| MP-E-001 | Fehlerfall (ungültiges Datum) | Fehler | | | | |

## 6. Akzeptanzkriterien

1. **Funktionale Äquivalenz**
   - 100% der Testfälle zeigen identisches Verhalten in Alt- und Neusystem

2. **Datenintegrität**
   - Datenbankinhalte nach identischen Operationen stimmen überein

3. **Fehlerbehandlung**
   - Identisches Fehlerverhalten bei ungültigen Eingaben

4. **Performance**
   - Das neue System zeigt mindestens gleichwertige Antwortzeiten

## 7. Testprozess und -dokumentation

### 7.1 Test-Durchführung

1. **Vorbereitung**
   - Einrichtung der Testumgebungen für Alt- und Neusystem
   - Import der Testdaten

2. **Durchführung**
   - Sequentielle Durchführung der Testfälle
   - Protokollierung aller Eingaben und Ergebnisse

3. **Analyse**
   - Vergleich der Ergebnisse und Identifikation von Abweichungen
   - Root-Cause-Analyse bei Unterschieden

4. **Nachbesserung**
   - Korrektur von Abweichungen im neuen System
   - Wiederholung der betroffenen Tests

### 7.2 Testdokumentation

1. **Testplan**
   - Detaillierte Beschreibung der Teststrategie und Testfälle

2. **Testprotokolle**
   - Aufzeichnung aller Testergebnisse mit Zeitstempeln

3. **Abweichungsberichte**
   - Dokumentation identifizierter Unterschiede

4. **Abnahmedokument**
   - Zusammenfassung der Testergebnisse und Bestätigung der Funktionsgleichheit

## 8. Risikobetrachtung und Mitigationsstrategien

1. **Datenmigrationsrisiken**
   - Risiko: Unvollständige oder fehlerhafte Datenmigration
   - Mitigation: Vollständige Datenbankvergleiche vor und nach der Migration

2. **Leistungsrisiken**
   - Risiko: Performance-Einbußen im neuen System
   - Mitigation: Frühzeitige Lastteststests und Performance-Optimierung

3. **Integrationsrisiken**
   - Risiko: Inkompatibilitäten mit anderen Systemkomponenten
   - Mitigation: Umfassende Integrationstests mit allen verbundenen Systemen

## Zusammenfassung

Diese Teststrategie stellt einen umfassenden Ansatz zur Validierung der Funktionsgleichheit zwischen dem alten und dem neuen "Motor Policy"-System dar. Durch die systematische Durchführung der definierten Testfälle und den kontinuierlichen Vergleich der Ergebnisse kann sichergestellt werden, dass das modernisierte System alle Anforderungen erfüllt und nahtlos das bestehende System ersetzen kann.

Die bereitgestellten Testdaten und Testfälle decken sowohl die Standardfunktionalität als auch Grenzfälle und Fehlerszenarien ab. Die vorgeschlagene Paralleltest-Strategie ermöglicht eine direkte Gegenüberstellung der Systeme unter identischen Bedingungen.

Diese Teststrategie bildet die Grundlage für eine erfolgreiche Modernisierung der "Motor Policy" im GenApp-System.
