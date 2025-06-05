# KFZ-Police (Motor Policy) Ablaufdiagramm

Dieses Dokument stellt die wichtigsten Abläufe bei der Verarbeitung von KFZ-Policen (Motor Policy) im CICS-GenApp System als Ablaufdiagramm dar.

## Überblick

Die KFZ-Police ist einer der vier Versicherungstypen, die im CICS-GenApp System verwaltet werden. Die Transaktions-ID `SSP1` wird verwendet, um auf das KFZ-Versicherungsmenü zuzugreifen. Über dieses Menü können Benutzer KFZ-Policen erstellen, abfragen, aktualisieren und löschen.

## Ablaufdiagramm für die Abfrage einer KFZ-Police

```
┌────────────────┐
│   Benutzer     │
│  startet SSP1  │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  LGTESTP1      │
│ zeigt Formular │
└───────┬────────┘
        │
        ▼
┌────────────────────────────┐
│ Benutzer gibt Kunden- und  │
│   Policen-Nummer ein und   │
│   wählt Option 4 (Inquiry) │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGTESTP1 ruft LGIPOL01    │
│  mit CA-REQUEST-ID='01IMOT'│
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGIPOL01 prüft CA-REQUEST-│
│  ID und wählt den          │
│  entsprechenden Pfad       │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGIPOL01 ruft LGIPDB01    │
│  für DB2-Datenbankabfrage  │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGIPDB01 führt SQL JOIN   │
│  zwischen policy und motor │
│  Tabellen durch            │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  Ergebnisdaten werden in   │
│  die COMMAREA kopiert      │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│ LGIPOL01 erhält Ergebnisse │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│ LGTESTP1 erhält Ergebnisse │
│ und zeigt KFZ-Policendaten │
│ auf dem Bildschirm an      │
└────────────────────────────┘
```

## Ablaufdiagramm für das Erstellen einer neuen KFZ-Police

```
┌────────────────┐
│   Benutzer     │
│  startet SSP1  │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  LGTESTP1      │
│ zeigt Formular │
└───────┬────────┘
        │
        ▼
┌────────────────────────────┐
│ Benutzer gibt Kundennummer │
│ und KFZ-Details ein und    │
│ wählt Option 1 (Add)       │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGTESTP1 validiert        │
│  Benutzereingaben          │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGTESTP1 ruft LGAPOL01    │
│  mit CA-REQUEST-ID='01AMOT'│
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGAPOL01 prüft CA-REQUEST-│
│  ID und wählt den          │
│  entsprechenden Pfad       │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGAPOL01 ruft LGAPDB01    │
│  für DB2-Datenbankzugriff  │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGAPDB01 führt INSERT in  │
│  die policy-Tabelle aus    │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGAPDB01 holt die neue    │
│  policy-ID                 │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGAPDB01 führt INSERT in  │
│  die motor-Tabelle aus     │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGAPOL01 ruft LGAPVS01    │
│  für VSAM-Dateneintrag     │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGAPVS01 schreibt Daten   │
│  in die VSAM-Datei KSDSPOLY│
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│ LGTESTP1 erhält Bestätigung│
│ und zeigt neue Policen-ID  │
│ auf dem Bildschirm an      │
└────────────────────────────┘
```

## Ablaufdiagramm für die Aktualisierung einer KFZ-Police

```
┌────────────────┐
│   Benutzer     │
│  startet SSP1  │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  LGTESTP1      │
│ zeigt Formular │
└───────┬────────┘
        │
        ▼
┌────────────────────────────┐
│ Benutzer gibt Kunden- und  │
│ Policen-Nummer ein und     │
│ wählt Option 2 (Update)    │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│ LGTESTP1 ruft zunächst     │
│ LGIPOL01 für aktuelle Daten│
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│ Benutzer bearbeitet die    │
│ KFZ-Policendaten           │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGTESTP1 validiert        │
│  Benutzereingaben          │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGTESTP1 ruft LGUPOL01    │
│  mit CA-REQUEST-ID='01UMOT'│
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGUPOL01 prüft CA-REQUEST-│
│  ID und wählt den          │
│  entsprechenden Pfad       │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGUPOL01 ruft LGUPDB01    │
│  für DB2-Datenbankzugriff  │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGUPDB01 prüft Timestamp  │
│  zum Schutz vor gleichzeit.│
│  Änderungen (Optimistic   │
│  Locking)                  │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGUPDB01 führt UPDATE der │
│  motor-Tabelle aus         │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGUPDB01 führt UPDATE der │
│  policy-Tabelle mit neuem  │
│  Timestamp aus             │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGUPOL01 ruft LGUPVS01    │
│  für VSAM-Datenaktualis.   │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGUPVS01 aktualisiert     │
│  Daten in KSDSPOLY         │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│ LGTESTP1 erhält Bestätigung│
│ und zeigt Erfolgsmeldung   │
└────────────────────────────┘
```

## Ablaufdiagramm für das Löschen einer KFZ-Police

```
┌────────────────┐
│   Benutzer     │
│  startet SSP1  │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  LGTESTP1      │
│ zeigt Formular │
└───────┬────────┘
        │
        ▼
┌────────────────────────────┐
│ Benutzer gibt Kunden- und  │
│ Policen-Nummer ein und     │
│ wählt Option 3 (Delete)    │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGTESTP1 ruft LGDPOL01    │
│  mit CA-REQUEST-ID='01DMOT'│
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGDPOL01 prüft CA-REQUEST-│
│  ID und wählt den          │
│  entsprechenden Pfad       │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGDPOL01 ruft LGDPDB01    │
│  für DB2-Datenbankzugriff  │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGDPDB01 führt DELETE aus │
│  der policy-Tabelle aus    │
│  (CASCADE löscht auch      │
│   motor-Einträge)          │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGDPOL01 ruft LGDPVS01    │
│  für VSAM-Datenlöschung    │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│  LGDPVS01 löscht Daten     │
│  aus der VSAM-Datei        │
└───────────┬────────────────┘
            │
            ▼
┌────────────────────────────┐
│ LGTESTP1 erhält Bestätigung│
│ und zeigt Erfolgsmeldung   │
└────────────────────────────┘
```

## Datenfluss bei KFZ-Policen

1. **Datenerfassung**: Über die LGTESTP1-Transaktion werden KFZ-Policendaten erfasst, darunter:
   - Kundennummer (Verknüpfung mit dem Kunden)
   - Fahrzeugmarke (z.B. FORD, VOLKSWAGEN)
   - Fahrzeugmodell (z.B. KA, BEETLE)
   - Fahrzeugwert
   - Kennzeichen
   - Fahrzeugfarbe
   - Hubraum (CC)
   - Herstellungsdatum
   - Anzahl der Unfälle
   - Prämie

2. **Datenspeicherung**:
   - In DB2: Die Daten werden in den Tabellen `policy` und `motor` gespeichert
   - In VSAM: Die Daten werden in der Datei KSDSPOLY mit einem Schlüssel gespeichert, der mit 'M' beginnt

3. **Datenzugriff**:
   - Abfragen können über Kunden- und Policennummer erfolgen
   - Die DB2-Abfragen verbinden die policy- und motor-Tabellen über JOINs
   - Der VSAM-Zugriff erfolgt über den zusammengesetzten Schlüssel

4. **Datenaktualisierung**:
   - Optimistic Locking wird verwendet, um Konflikte bei gleichzeitigen Aktualisierungen zu vermeiden
   - Ein Timestamp in der policy-Tabelle wird bei jeder Aktualisierung erneuert

5. **Datenlöschung**:
   - Beim Löschen einer KFZ-Police werden alle zugehörigen Daten gelöscht (CASCADE DELETE)
   - Sowohl DB2- als auch VSAM-Daten werden gelöscht

## DB2-Datenstruktur für KFZ-Policen

- **policy-Tabelle** (Allgemeine Policen-Informationen):
  - policyNumber (Primärschlüssel, automatisch generiert)
  - customerNumber (Fremdschlüssel auf customer.customerNumber)
  - policyType ('M' für KFZ-Policen)
  - issueDate (Ausstellungsdatum)
  - expiryDate (Ablaufdatum)
  - lastChanged (Timestamp für optimistic locking)
  - brokerID, brokersReference, payment, commission (weitere Felder)

- **motor-Tabelle** (KFZ-spezifische Informationen):
  - policyNumber (Primär- und Fremdschlüssel auf policy.policyNumber)
  - make (Fahrzeugmarke)
  - model (Fahrzeugmodell)
  - value (Fahrzeugwert)
  - regNumber (Kennzeichen)
  - colour (Farbe)
  - cc (Hubraum)
  - yearOfManufacture (Herstellungsdatum)
  - premium (Prämie)
  - accidents (Anzahl der Unfälle)
