# Maßnahmenplan zur Behebung von Dead Code

## Übersicht

Basierend auf der durchgeführten Dead-Code-Analyse wurden verschiedene Bereiche im CICS-GenApp-System identifiziert, die potentiell als Dead Code klassifiziert werden können. Dieser Maßnahmenplan schlägt konkrete Schritte vor, um diese Bereiche zu bereinigen und die Codequalität zu verbessern.

## Prioritäten

Die Maßnahmen sind nach folgenden Prioritäten kategorisiert:

- **Hoch**: Sofortige Bearbeitung empfohlen, hohes Potenzial zur Verbesserung der Codequalität
- **Mittel**: Sollte im Rahmen der normalen Wartung bearbeitet werden
- **Niedrig**: Kann bei Gelegenheit bearbeitet werden, geringe Auswirkung

## Maßnahmen

### 1. Entfernung oder Umstrukturierung der Test-Module (Priorität: Mittel)

**Betroffene Module**: `lgtestc1.cbl`, `lgtestp1.cbl`, `lgtestp2.cbl`, `lgtestp3.cbl`, `lgtestp4.cbl`

**Maßnahmen**:
1. Überprüfen, ob diese Module im Produktionsbetrieb genutzt werden
2. Falls ja: Umbenennen, um den Testcharakter zu entfernen (z.B. `lgmenuc1.cbl` statt `lgtestc1.cbl`)
3. Falls nein: Aus dem Produktionscode entfernen und in eine separate Testumgebung verschieben
4. Überprüfen, ob in der Produktionsumgebung entsprechende Transaktionsdefinitionen (SSC1, SSP1, etc.) existieren und diese ggf. anpassen

**Beispiel-Änderungen**:
- Umbenennung: `lgtestc1.cbl` → `lgmenuc1.cbl`
- Entfernen der entsprechenden Einträge in der JCL-Datei `cobol.jcl`

### 2. Bereinigung der Counter-Variablen (Priorität: Niedrig)

**Betroffene Module**: `lgsetup.cbl`, `lgwebst5.cbl`

**Maßnahmen**:
1. Jeden Counter daraufhin überprüfen, ob er tatsächlich im System inkrementiert wird
2. Nicht verwendete Counter identifizieren und entfernen
3. Die verbleibenden Counter besser dokumentieren

**Beispiel für potentiell ungenutzte Counter**:
```cobol
Exec CICS Delete Counter(GENACNTI00)
         Pool(GENApool)
         Resp(WS-RESP)
End-Exec.
Exec CICS Define Counter(GENACNTI00)
         Pool(GENApool)
         Value(0)
         Resp(WS-RESP)
End-Exec.
```

### 3. Überprüfung und Standardisierung der Error-Handling-Routinen (Priorität: Hoch)

**Betroffene Module**: Alle Module mit WRITE-ERROR-MESSAGE-Routinen nach GOBACK/EXIT

**Maßnahmen**:
1. Sicherstellen, dass alle Error-Handling-Routinen durch PERFORM-Anweisungen aufgerufen werden
2. Standardisierung der Error-Handling-Struktur über alle Module hinweg
3. Überprüfen, ob ABEND-Anweisungen dazu führen, dass bestimmte Fehlerbehandlungspfade nie erreicht werden

**Beispiel für eine verbesserte Struktur**:
```cobol
MAINLINE SECTION.
    ...
    IF fehlerfall THEN
        PERFORM ERROR-HANDLING
    END-IF
    ...
    GOBACK.

ERROR-HANDLING SECTION.
    MOVE error-data TO error-fields
    PERFORM WRITE-ERROR-MESSAGE
    ...
    .
```

### 4. Refactoring der unbenutzen Variablen (Priorität: Niedrig)

**Betroffene Module**: Verschiedene Module mit potenziell ungenutzten Variablen

**Maßnahmen**:
1. Identifikation von Variablen, die deklariert, aber nie verwendet werden
2. Entfernen dieser Variablen
3. Bei unsicheren Fällen Variablen mit einem Kommentar versehen, der auf die potenzielle Nicht-Nutzung hinweist

**Beispiel**:
In `lgicvs01.cbl` prüfen, ob folgende Variablen tatsächlich verwendet werden:
```cobol
01 WS-STARTCODE              PIC XX Value spaces.
01 WS-SYSID                  PIC X(4) Value spaces.
01 WS-Invokeprog             PIC X(8) Value spaces.
```

### 5. Code Coverage Analysis durchführen (Priorität: Hoch)

**Betroffene Bereiche**: Gesamtes System

**Maßnahmen**:
1. Einsatz eines Tools zur automatisierten Code Coverage Analysis
2. Ausführung der Tests mit aktivierter Code Coverage
3. Identifikation von Code-Zeilen, die nie ausgeführt werden
4. Entfernung oder Überarbeitung des nicht erreichten Codes

**Beispiel-Tool**: IBM Debug Tool für COBOL

### 6. Dokumentation der Codestrukturen verbessern (Priorität: Mittel)

**Betroffene Bereiche**: Gesamtes System

**Maßnahmen**:
1. Dokumentation der Aufrufreihenfolge und der Abhängigkeiten zwischen den Modulen
2. Dokumentation der Bedeutung und Verwendung der Counter-Variablen
3. Klarere Kommentierungen für Error-Handling-Logik

**Beispiel**:
```cobol
* Counter für erfolgreiche Kundenabfragen
* Wird in folgenden Modulen inkrementiert:
* - lgicus01.cbl (Zeile 123)
* - lgicvs01.cbl (Zeile 456)
01 GENACNT100                PIC X(16) Value 'GENA01ICUS00'.
```

## Implementierungsplan

1. **Phase 1 (Sofort)**:
   - Code Coverage Analysis durchführen
   - Überprüfung und Dokumentation der Test-Module

2. **Phase 2 (Innerhalb 3 Monate)**:
   - Standardisierung der Error-Handling-Routinen
   - Entscheidung über das Schicksal der Test-Module
   - Entfernung eindeutig identifizierten Dead Codes

3. **Phase 3 (Innerhalb 6 Monate)**:
   - Bereinigung der Counter-Variablen
   - Entfernung ungenutzter Variablen
   - Abschließende Dokumentation der bereinigten Codestrukturen

## Risiken und Abhängigkeiten

1. **Integrationsrisiken**: Die Entfernung von Code könnte unerwartete Abhängigkeiten offenbaren
2. **Dokumentationsbedarf**: Gründliche Dokumentation der vorgenommenen Änderungen ist erforderlich
3. **Testaufwand**: Umfassende Tests nach jeder Änderung sind notwendig, um Regressionen zu vermeiden

## Zusammenfassung

Die identifizierten Dead-Code-Bereiche im CICS-GenApp-System stellen eine gute Gelegenheit zur Code-Bereinigung und Qualitätsverbesserung dar. Durch einen strukturierten Ansatz mit klaren Prioritäten können diese Bereiche systematisch angegangen werden, ohne die Systemstabilität zu gefährden.

Der größte Nutzen wird voraussichtlich aus der Entscheidung über die Test-Module und der Standardisierung der Error-Handling-Routinen gezogen. Die Bereinigung der Counter-Variablen und ungenutzten Variablen wird zwar das System vereinfachen, hat aber eine geringere Auswirkung auf die Gesamtqualität.
