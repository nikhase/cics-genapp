# Dead Code Analyse für CICS-GenApp

## Zusammenfassung

Diese Analyse identifiziert potentiellen Dead Code im CICS-GenApp-System. Dead Code sind Codebereiche, die im Programm vorhanden, aber nicht erreichbar oder niemals aufgerufen werden. Dies könnte aufgrund von:

1. Nicht verwendeten Funktionen
2. Nicht aufgerufenen Routinen
3. Redundanten Variablen und Definitionen
4. Code, der nach GOBACK/EXIT/RETURN-Anweisungen steht
5. Deklarierte aber nie verwendete Datenstrukturen
6. Alternative Codepfade, die niemals ausgeführt werden

## Gefundene Dead Code-Bereiche

### 1. Code nach GOBACK/EXIT-Anweisungen

Mehrere Module enthalten Code nach EXIT und GOBACK-Anweisungen, der niemals ausgeführt wird:

#### In `lgacvs01.cbl`
```cobol
A-EXIT.
    EXIT.
    GOBACK.
*---------------------------------------------------------------*
WRITE-ERROR-MESSAGE.
    EXEC CICS ASKTIME ABSTIME(WS-ABSTIME)
    END-EXEC
    ...
```
Der WRITE-ERROR-MESSAGE-Abschnitt steht nach GOBACK und scheint nur über eine PERFORM-Anweisung erreichbar zu sein. Dies ist ein typisches Muster in der Anwendung, bei dem Fehlerbehandlungsroutinen nach der Hauptbearbeitungslogik platziert werden.

#### In `lgupvs01.cbl`
```cobol
A-EXIT.
    EXIT.
    GOBACK.
*---------------------------------------------------------------*
WRITE-ERROR-MESSAGE.
    ...
```
Ähnliche Struktur wie in lgacvs01.cbl.

### 2. Unbenutzte oder redundante Variablen

#### In `lgwebst5.cbl`
Eine große Anzahl von Counter-Variablen wird definiert, jedoch ist unklar, ob alle tatsächlich verwendet werden:

```cobol
01 GENACNTE99-V                Pic 9(9)  Display.
01 GENACNTF00-V                Pic 9(9)  Display.
01 GENACNTF99-V                Pic 9(9)  Display.
01 GENACNTG00-V                Pic 9(9)  Display.
01 GENACNTG99-V                Pic 9(9)  Display.
01 GENACNTH00-V                Pic 9(9)  Display.
01 GENACNTH99-V                Pic 9(9)  Display.
01 GENACNTI00-V                Pic 9(9)  Display.
01 GENACNTI99-V                Pic 9(9)  Display.
01 GENAsucces-V                Pic 9(9)  Display.
01 GENAerrors-V                Pic 9(9)  Display.
```

Diese Variablen werden über das gesamte Programm verteilt verwendet, aber einige könnten unbenutzt sein.

#### In `lgicvs01.cbl`
Mehrere Variablen werden definiert, aber ihre Verwendung ist unklar:

```cobol
01 WS-STARTCODE              PIC XX Value spaces.
01 WS-SYSID                  PIC X(4) Value spaces.
01 WS-Invokeprog             PIC X(8) Value spaces.
01 WS-COMMAREA               PIC X(80).
01 WS-RECV.
  03 WS-RECV-TRANID         PIC X(5).
  03 WS-RECV-DATA           PIC X(74).
01 WS-RECV-LEN               PIC S9(4) COMP Value 80.
```

Diese Variablen könnten in bestimmten Ausführungspfaden nicht verwendet werden.

### 3. Nicht verwendete Testmodule

Die folgenden Test-Module könnten in der Produktionsumgebung nicht verwendet werden:

- `lgtestc1.cbl` - Customer Menu Testprogramm
- `lgtestp1.cbl` - Motor Policy Menu
- `lgtestp2.cbl` - Endowment Policy Menu 
- `lgtestp3.cbl` - House Policy Menu
- `lgtestp4.cbl` - Commercial Policy Menu

Diese Module scheinen hauptsächlich für Testmenüs verwendet zu werden und könnten im Produktionssystem überflüssig sein.

### 4. Potentiell ungenutzter Code in DB-Routinen

In Modulen wie `lgipdb01.cbl`, `lgdpdb01.cbl` und anderen DB-Routinen gibt es Code, der unter Umständen nie ausgeführt wird:

#### In `lgipdb01.cbl` 
Indikatorvariablen für Nullwerte, die möglicherweise nicht in allen Codepfaden verwendet werden:

```cobol
* Indicator variables for columns which could return nulls
*   if these are not specified and SQL FETCH tries to return
*   null value for a listed column it will FAIL with SQLCODE=-305
77  IND-BROKERID                PIC S9(4) COMP.
77  IND-BROKERSREF              PIC S9(4) COMP.
77  IND-PAYMENT                 PIC S9(4) COMP.
77  IND-E-PADDINGDATA           PIC S9(4) COMP.
77  IND-E-PADDINGDATAL          PIC S9(4) COMP.
```

### 5. Error-Handling-Code der nie erreicht wird

In mehreren Modulen gibt es Error-Handling-Code, der möglicherweise nie erreicht wird, da die Fehlerbehandlung über ABEND-Anweisungen erfolgt:

#### In `lgucdb01.cbl`
```cobol
IF EIBCALEN IS EQUAL TO ZERO
    MOVE ' NO COMMAREA RECEIVED' TO EM-VARIABLE
    PERFORM WRITE-ERROR-MESSAGE
    EXEC CICS ABEND ABCODE('LGCA') NODUMP END-EXEC
END-IF
```

Nach dem ABEND wird der nachfolgende Code nie ausgeführt, es sei denn, ABEND schlägt fehl oder wird durch besondere System-Einstellungen abgefangen.

### 6. Redundante Zählervariablen in `lgsetup.cbl`

In `lgsetup.cbl` werden zahlreiche Counter definiert, gelöscht und neu definiert. Es ist unklar, ob alle diese Counter aktiv verwendet werden oder ob einige davon Überbleibsel früherer Implementierungen sind:

```cobol
Exec CICS Delete Counter(GENACNTI99)
                Pool(GENApool)
                Resp(WS-RESP)
End-Exec.
Exec CICS Define Counter(GENACNTI99)
                Pool(GENApool)
                Value(0)
                Resp(WS-RESP)
End-Exec.
```

## Empfehlungen

1. **Überprüfung der Test-Module**: Untersuchen Sie, ob die Test-Module (lgtestXX.cbl) im Produktionssystem notwendig sind oder entfernt werden können.

2. **Code-Coverage-Analyse**: Führen Sie eine formale Code-Coverage-Analyse durch, um nicht erreichbaren Code zu identifizieren.

3. **Rationalisierung der Counter-Variablen**: Überprüfen Sie die Nutzung der zahlreichen Counter in `lgwebst5.cbl` und `lgsetup.cbl` und entfernen Sie nicht verwendete Counter.

4. **Umstrukturierung der Fehlerbehandlung**: Überprüfen Sie die Platzierung von Error-Handling-Routinen nach EXIT/GOBACK-Anweisungen und stellen Sie sicher, dass diese erreichbar sind.

5. **Refactoring der Datensatzstrukturen**: Überprüfen Sie die in verschiedenen Modulen definierten Datensatzstrukturen auf Redundanzen und nicht verwendete Felder.

6. **Bereinigung nach GOBACK-Statements**: Stellen Sie sicher, dass kein Code nach GOBACK-Anweisungen steht, es sei denn, er wird durch PERFORM-Anweisungen erreicht.

## Hinweis zur weiteren Analyse

Die hier identifizierten Bereiche sind potentieller Dead Code. Eine vollständige Analyse erfordert Kenntnisse über den tatsächlichen Programmfluss während der Ausführung und kann am besten mit automatisierten Tools für die Code-Coverage-Analyse durchgeführt werden.
