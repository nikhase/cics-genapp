# KFZ-Police (Motor Policy) C4 Komponentendiagramm

Dieses Dokument stellt die KFZ-Police (Motor Policy) im CICS-GenApp System als C4 Komponentendiagramm dar.

## C4 Komponentendiagramm der KFZ-Police

```mermaid
flowchart TB
    %% Hauptcontainer für das CICS-GenApp System
    subgraph CICS["CICS-GenApp System"]
        %% Präsentationsschicht
        subgraph UI["Präsentationsschicht"]
            LGTESTP1["LGTESTP1\n(Motor UI)"]
            SSMAP["SSMAP\n(BMS Maps)"]
        end
        
        %% Business-Logik
        subgraph BL["Business-Logik"]
            LGIPOL01["LGIPOL01\n(Policy Inquiry)"]
            LGAPOL01["LGAPOL01\n(Add Policy)"]
            LGUPOL01["LGUPOL01\n(Update Policy)"]
            LGDPOL01["LGDPOL01\n(Delete Policy)"]
        end
        
        %% Datenzugriffsschicht
        subgraph DAL["Datenzugriffsschicht"]
            %% DB2 Zugriff
            subgraph DB2A["DB2-Zugriff"]
                LGIPDB01["LGIPDB01\n(Policy DB Inquiry)"]
                LGAPDB01["LGAPDB01\n(Policy DB Add)"]
                LGUPDB01["LGUPDB01\n(Policy DB Update)"]
                LGDPDB01["LGDPDB01\n(Policy DB Delete)"]
            end
            
            %% VSAM Zugriff
            subgraph VSAMA["VSAM-Zugriff"]
                LGIPVS01["LGIPVS01\n(Policy VSAM Inquiry)"]
                LGAPVS01["LGAPVS01\n(Policy VSAM Add)"]
                LGUPVS01["LGUPVS01\n(Policy VSAM Update)"]
                LGDPVS01["LGDPVS01\n(Policy VSAM Delete)"]
            end
        end
    end
    
    %% Externe Systeme
    subgraph DB2["DB2 Datenbank"]
        POLICY["policy\n- policyNumber\n- policyType\n- customerId\n- ..."]
        MOTOR["motor\n- policyNumber\n- make\n- model\n- value\n- ..."]
    end
    
    subgraph VSAM["VSAM Dateien"]
        KSDSPOLY["KSDSPOLY\n(Policy File)"]
        KSDSCUST["KSDSCUST\n(Customer)"]
    end
    
    %% Verbindungen zwischen Komponenten
    %% UI zu Business Logic
    LGTESTP1 <--> LGIPOL01
    LGTESTP1 <--> LGAPOL01
    LGTESTP1 <--> LGUPOL01
    LGTESTP1 <--> LGDPOL01
    
    %% Business Logic zu DB2 Zugriff
    LGIPOL01 <--> LGIPDB01
    LGAPOL01 <--> LGAPDB01
    LGUPOL01 <--> LGUPDB01
    LGDPOL01 <--> LGDPDB01
    
    %% Business Logic zu VSAM Zugriff
    LGAPOL01 <--> LGAPVS01
    LGIPOL01 <--> LGIPVS01
    LGUPOL01 <--> LGUPVS01
    LGDPOL01 <--> LGDPVS01
    
    %% Zugriffe auf externe Systeme
    LGIPDB01 --> POLICY
    LGAPDB01 --> POLICY
    LGUPDB01 --> POLICY
    LGDPDB01 --> POLICY
    
    LGIPDB01 --> MOTOR
    LGAPDB01 --> MOTOR
    LGUPDB01 --> MOTOR
    LGDPDB01 --> MOTOR
    
    LGIPVS01 --> KSDSPOLY
    LGAPVS01 --> KSDSPOLY
    LGUPVS01 --> KSDSPOLY
    LGDPVS01 --> KSDSPOLY
    
    LGIPVS01 --> KSDSCUST
    LGAPVS01 --> KSDSCUST
    LGUPVS01 --> KSDSCUST
    LGDPVS01 --> KSDSCUST

    %% Styling
    classDef uiClass fill:#e6f3ff,stroke:#6ca0dc,stroke-width:2px
    classDef blClass fill:#d5e8d4,stroke:#82b366,stroke-width:2px
    classDef dalClass fill:#fff2cc,stroke:#d6b656,stroke-width:2px
    classDef dbClass fill:#f8cecc,stroke:#b85450,stroke-width:2px
    classDef vsamClass fill:#e1d5e7,stroke:#9673a6,stroke-width:2px
    
    class UI,LGTESTP1,SSMAP uiClass
    class BL,LGIPOL01,LGAPOL01,LGUPOL01,LGDPOL01 blClass
    class DAL,DB2A,VSAMA,LGIPDB01,LGAPDB01,LGUPDB01,LGDPDB01,LGIPVS01,LGAPVS01,LGUPVS01,LGDPVS01 dalClass
    class DB2,POLICY,MOTOR dbClass
    class VSAM,KSDSPOLY,KSDSCUST vsamClass
```

## Komponentenbeschreibung

### Präsentationsschicht
- **LGTESTP1** (Motor Policy UI): Hauptprogramm für die Benutzeroberfläche der KFZ-Police
  - Stellt Optionen für Hinzufügen, Ändern, Löschen und Anzeigen von KFZ-Policen bereit
  - Sammelt Benutzereingaben und ruft die entsprechenden Business-Logik-Funktionen auf
  - Zeigt Ergebnisse und Fehlermeldungen an
- **SSMAP**: BMS-Maps für die Terminal-Benutzeroberfläche

### Business-Logik
- **LGIPOL01** (Inquire Policy): Steuert die Abfrage von KFZ-Policen
- **LGAPOL01** (Add Policy): Steuert das Hinzufügen neuer KFZ-Policen
- **LGUPOL01** (Update Policy): Steuert die Aktualisierung von KFZ-Policen
- **LGDPOL01** (Delete Policy): Steuert das Löschen von KFZ-Policen

Jedes dieser Programme validiert die Eingabedaten, koordiniert den Datenbankzugriff und stellt sicher, dass die Geschäftsregeln eingehalten werden.

### Datenzugriffsschicht
- **DB2-Zugriff**:
  - **LGIPDB01**: Ruft KFZ-Policendaten aus der DB2-Datenbank ab
  - **LGAPDB01**: Fügt neue KFZ-Policen in die DB2-Datenbank ein
  - **LGUPDB01**: Aktualisiert KFZ-Policen in der DB2-Datenbank
  - **LGDPDB01**: Löscht KFZ-Policen aus der DB2-Datenbank

- **VSAM-Zugriff**:
  - **LGIPVS01**: Ruft KFZ-Policendaten aus VSAM-Dateien ab
  - **LGAPVS01**: Fügt neue KFZ-Policen in VSAM-Dateien ein
  - **LGUPVS01**: Aktualisiert KFZ-Policen in VSAM-Dateien
  - **LGDPVS01**: Löscht KFZ-Policen aus VSAM-Dateien

### Externe Systeme
- **DB2 Datenbank**: Persistiert KFZ-Policendaten in relationalen Tabellen
  - **policy**: Allgemeine Policen-Informationen, mit policyType 'M' für KFZ-Policen
  - **motor**: KFZ-spezifische Informationen (mit Fremdschlüssel auf policy.policyNumber)

- **VSAM Dateien**: Speichert Policen- und Kundendaten für schnellen Zugriff
  - **KSDSPOLY**: Speichert Policendaten mit einem Schlüssel, der mit 'M' beginnt für KFZ-Policen
  - **KSDSCUST**: Speichert Kundendaten

## Schlüsselinteraktionen

1. **Abfrage einer KFZ-Police**:
   - LGTESTP1 sammelt die Kunden- und Policennummer
   - Ruft LGIPOL01 über CICS LINK auf
   - LGIPOL01 ruft LGIPDB01 auf, um DB2-Daten abzufragen
   - LGIPDB01 führt einen SQL-JOIN zwischen policy und motor-Tabellen aus
   - Ergebnisse werden zurück an die Benutzeroberfläche weitergeleitet

2. **Hinzufügen einer KFZ-Police**:
   - LGTESTP1 sammelt alle KFZ-Policendetails
   - Ruft LGAPOL01 über CICS LINK auf
   - LGAPOL01 ruft LGAPDB01 auf, um die Daten in DB2 zu speichern
   - LGAPDB01 führt INSERT-Operationen in die policy- und motor-Tabellen aus
   - LGAPOL01 ruft LGAPVS01 auf, um Daten in VSAM zu speichern
   - Bestätigung wird zurück an die Benutzeroberfläche weitergeleitet

3. **Aktualisieren einer KFZ-Police**:
   - LGTESTP1 sammelt die geänderten KFZ-Policendaten
   - Ruft LGUPOL01 über CICS LINK auf
   - LGUPOL01 ruft LGUPDB01 auf, um die Daten in DB2 zu aktualisieren
   - LGUPDB01 führt UPDATE-Operationen in der policy- und motor-Tabellen aus
   - LGUPOL01 ruft LGUPVS01 auf, um Daten in VSAM zu aktualisieren
   - Bestätigung wird zurück an die Benutzeroberfläche weitergeleitet

4. **Löschen einer KFZ-Police**:
   - LGTESTP1 sammelt die Kunden- und Policennummer
   - Ruft LGDPOL01 über CICS LINK auf
   - LGDPOL01 ruft LGDPDB01 auf, um die Daten aus DB2 zu löschen
   - LGDPDB01 führt DELETE-Operationen in der policy-Tabelle aus (mit CASCADE-DELETE für motor)
   - LGDPOL01 ruft LGDPVS01 auf, um Daten aus VSAM zu löschen
   - Bestätigung wird zurück an die Benutzeroberfläche weitergeleitet
