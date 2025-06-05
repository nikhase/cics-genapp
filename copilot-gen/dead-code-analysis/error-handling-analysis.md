# Analyse der Error-Handling-Routinen und Code nach GOBACK

## Übersicht

In mehreren Modulen des CICS-GenApp-Systems wurden Codeabschnitte identifiziert, die nach EXIT oder GOBACK-Anweisungen stehen. Dies ist ein typisches Muster in COBOL-Programmen, bei dem Error-Handling-Routinen nach der Hauptlogik platziert werden. Obwohl dieser Code nicht direkt in der Sequenz ausgeführt wird, wird er normalerweise über PERFORM-Anweisungen erreicht.

Es ist wichtig zu unterscheiden zwischen:

1. **Legitimen Error-Handling-Routinen**: Diese werden durch PERFORM-Anweisungen aufgerufen und sind kein Dead Code.
2. **Echtem Dead Code**: Code, der nach GOBACK/EXIT steht und nicht durch PERFORM oder andere Mechanismen erreicht wird.

## Gefundene Muster

### Muster 1: Error-Handling-Routine nach A-EXIT/GOBACK

In mehreren Modulen, darunter `lgacvs01.cbl` und `lgupvs01.cbl`, wurde folgendes Muster identifiziert:

```cobol
A-EXIT.
    EXIT.
    GOBACK.
*---------------------------------------------------------------*
WRITE-ERROR-MESSAGE.
    EXEC CICS ASKTIME ABSTIME(WS-ABSTIME)
    END-EXEC
    EXEC CICS FORMATTIME ABSTIME(WS-ABSTIME)
             MMDDYYYY(WS-DATE)
             TIME(WS-TIME)
    END-EXEC
    ...
```

Diese WRITE-ERROR-MESSAGE-Routine wird typischerweise früher im Code durch PERFORM-Anweisungen aufgerufen:

```cobol
IF WS-RESP Not = DFHRESP(NORMAL)
    Move EIBRESP2 To WS-RESP2
    MOVE '80' TO CA-RETURN-CODE
    PERFORM WRITE-ERROR-MESSAGE
    EXEC CICS ABEND ABCODE('LGV0') NODUMP END-EXEC
    EXEC CICS RETURN END-EXEC
End-If.
```

In diesem Fall ist die WRITE-ERROR-MESSAGE-Routine **kein Dead Code**, obwohl sie nach GOBACK steht, da sie durch PERFORM-Anweisungen erreicht wird.

### Muster 2: Potentiell ungenutzter Error-Handling-Code

In einigen Modulen gibt es Error-Handling-Code, der möglicherweise nie erreicht wird. Zum Beispiel in `lgucdb01.cbl`:

```cobol
IF EIBCALEN IS EQUAL TO ZERO
    MOVE ' NO COMMAREA RECEIVED' TO EM-VARIABLE
    PERFORM WRITE-ERROR-MESSAGE
    EXEC CICS ABEND ABCODE('LGCA') NODUMP END-EXEC
END-IF
```

Nach dem ABEND wird der nachfolgende Code nie ausgeführt, es sei denn, ABEND schlägt fehl. Dies könnte dazu führen, dass bestimmte Fehlerbehandlungspfade nie erreicht werden.

## Detaillierte Beispiele

### Beispiel 1: `lgacvs01.cbl`

```cobol
A-EXIT.
    EXIT.
    GOBACK.
*---------------------------------------------------------------*
WRITE-ERROR-MESSAGE.
    EXEC CICS ASKTIME ABSTIME(WS-ABSTIME)
    END-EXEC
    EXEC CICS FORMATTIME ABSTIME(WS-ABSTIME)
             MMDDYYYY(WS-DATE)
             TIME(WS-TIME)
    END-EXEC
*
    MOVE WS-DATE TO EM-DATE
    MOVE WS-TIME TO EM-TIME
    Move CA-Customer-Num To EM-Cusnum
    Move WS-RESP         To EM-RespRC
```

Diese Error-Handling-Routine wird aktiv im Code verwendet und ist daher kein Dead Code.

### Beispiel 2: `lgicus01.cbl`

```cobol
WRITE-ERROR-MESSAGE.
    EXEC CICS ASKTIME ABSTIME(WS-ABSTIME)
    END-EXEC
    EXEC CICS FORMATTIME ABSTIME(WS-ABSTIME)
            MMDDYYYY(WS-DATE)
            TIME(WS-TIME)
    END-EXEC
    MOVE WS-DATE TO EM-DATE
    MOVE WS-TIME TO EM-TIME
    Move CA-Customer-Num To EM-Cusnum
    Move SQLCODE To EM-SQLRC
    EXEC CICS LINK PROGRAM('LGSTSQ')
            COMMAREA(ERROR-MSG)
            LENGTH(LENGTH OF ERROR-MSG)
    END-EXEC.
    IF EIBCALEN > 0 THEN
        IF EIBCALEN < 91 THEN
            MOVE DFHCOMMAREA(1:EIBCALEN) TO CA-DATA
            EXEC CICS LINK PROGRAM('LGSTSQ')
                    COMMAREA(CA-ERROR-MSG)
                    LENGTH(LENGTH OF CA-ERROR-MSG)
            END-EXEC
        ELSE
            MOVE DFHCOMMAREA(1:90) TO CA-DATA
            EXEC CICS LINK PROGRAM('LGSTSQ')
                    COMMAREA(CA-ERROR-MSG)
                    LENGTH(LENGTH OF CA-ERROR-MSG)
            END-EXEC
        END-IF
    END-IF.
    EXIT.
```

Diese Routine scheint gut strukturiert und wird wahrscheinlich über PERFORM-Anweisungen aufgerufen, ist also kein Dead Code.

## Potentiell problematische Muster

### ERROR-OUT-Routinen in Test-Modulen

In den Test-Modulen (`lgtestc1.cbl`, `lgtestp1.cbl`, usw.) gibt es ERROR-OUT-Routinen, die möglicherweise in bestimmten Fehlerfällen nicht erreicht werden:

```cobol
NO-DATA.
    Move 'No data was returned.' To ERP1FLDO
    Go To ERROR-OUT.

ERROR-OUT.
    EXEC CICS SEND MAP ('SSMAPP1')
            FROM(SSMAPP1O)
            MAPSET ('SSMAP')
    END-EXEC.

    Initialize SSMAPP1I.
    Initialize SSMAPP1O.
    Initialize COMM-AREA.

    GO TO ENDIT-STARTIT.
```

Diese Routinen könnten in bestimmten Fehlersituationen übersprungen werden, insbesondere wenn vorher ein ABEND oder RETURN ausgelöst wird.

## Empfehlungen

1. **Code Review der Error-Handling-Pfade**: Eine gründliche Überprüfung aller Error-Handling-Pfade sollte durchgeführt werden, um sicherzustellen, dass sie in allen Fehlersituationen korrekt erreicht werden.

2. **Vereinfachung der Error-Handling-Logik**: Die Error-Handling-Logik sollte vereinfacht werden, um sicherzustellen, dass alle Fehlerfälle konsistent behandelt werden.

3. **Standardisierung der Error-Handling-Strukturen**: Eine einheitliche Struktur für Error-Handling sollte in allen Modulen implementiert werden.

4. **Überprüfung der ABEND-Verarbeitung**: Die Auswirkungen von ABEND-Anweisungen auf den Programmfluss sollten überprüft werden, um sicherzustellen, dass keine wichtigen Error-Handling-Routinen übersprungen werden.

## Zusammenfassung

Die identifizierten Muster, bei denen Code nach GOBACK/EXIT-Anweisungen steht, stellen in den meisten Fällen kein Dead Code dar, da diese Routinen typischerweise durch PERFORM-Anweisungen erreicht werden. Allerdings gibt es potenzielle Probleme mit der Fehlerbehandlung, die zu Dead Code-Pfaden führen könnten, insbesondere in Verbindung mit ABEND-Anweisungen.

Eine umfassendere Analyse des Kontrollflusses und der tatsächlichen Nutzung dieser Routinen ist erforderlich, um mit Sicherheit festzustellen, ob es sich um Dead Code handelt.
