# Analyse der Counter-Variablen und zugehörigem potentiellen Dead Code

## Übersicht der Counter-Definition

Das CICS-GenApp-System verwendet zahlreiche Counter-Variablen, insbesondere in den Modulen `lgsetup.cbl` und `lgwebst5.cbl`. Diese Counter scheinen für Statistikzwecke verwendet zu werden, aber einige könnten potenziell ungenutzt sein und somit Dead Code darstellen.

## Counter-Definition in `lgsetup.cbl`

Das Modul `lgsetup.cbl` enthält Code zur Initialisierung zahlreicher Counter:

```cobol
Exec CICS Delete Counter(GENAcount)
         Pool(GENApool)
         Resp(WS-RESP)
End-Exec.
Exec CICS Define Counter(GENAcount)
         Pool(GENApool)
         Value(LastCustNum)
         Resp(WS-RESP)
End-Exec.
```

Dieser Mustercode wird für eine große Anzahl verschiedener Counter wiederholt, darunter:

- GENACNT100 bis GENACNT999
- GENACNTA00 bis GENACNTA99
- GENACNTB00 bis GENACNTB99
- ...
- GENACNTI00 bis GENACNTI99

Es gibt insgesamt über 20 Counter-Paare, die jeweils gelöscht und neu definiert werden.

## Counter-Verwendung in `lgwebst5.cbl`

Das Modul `lgwebst5.cbl` enthält Code, der den Wert dieser Counter abfragt:

```cobol
Exec CICS Query Counter(GENACNT100)
         Pool(GENApool)
         Value(CountVal)
         Resp(WS-RESP)
End-Exec.
Move CountVal  To CountSuccess
Move CountVal  To CountInq
Move CountVal  To GENACNT100-V
Move GENACNT100-V To S3
```

Dieser Code wird für jede der Counter-Variablen wiederholt. 

## Potentiell ungenutzter Counter-Code

Folgende Anzeichen deuten auf potentiell ungenutzten Counter-Code hin:

1. **Nicht verwendete Counter**: Einige der definierten Counter könnten in der Praxis nie abgefragt werden.

2. **Redundante Definitionen**: In `lgwebst5.cbl` werden zahlreiche Counter-Variablen definiert:
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
   ```

3. **Unklare Verwendung**: Die Verwendung vieler Counter ist im Code nicht klar dokumentiert, was die Identifikation von Dead Code erschwert.

## Beispiel für potenziellen Dead Code: Counter für verschiedene Transaktionstypen

In `lgwebst5.cbl` werden verschiedene Counter für unterschiedliche Transaktionstypen definiert:

```cobol
01 GENACNT100                PIC X(16) Value 'GENA01ICUS00'.
01 GENACNT199                PIC X(32) Value 'GENA01ICUS99'.
01 GENACNT200                PIC X(16) Value 'GENA01ACUS00'.
01 GENACNT299                PIC X(32) Value 'GENA01ACUS99'.
01 GENACNT300                PIC X(16) Value 'GENA01UCUS00'.
01 GENACNT399                PIC X(32) Value 'GENA01UCUS99'.
01 GENACNT400                PIC X(16) Value 'GENA01IMOT00'.
01 GENACNT499                PIC X(32) Value 'GENA01IMOT99'.
01 GENACNT500                PIC X(16) Value 'GENA01AMOT00'.
...
```

Falls bestimmte Transaktionstypen in der Produktionsumgebung nicht verwendet werden, könnten die zugehörigen Counter als Dead Code betrachtet werden.

## Konkretes Beispiel: GENACNTI00 und GENACNTI99

Die Counter GENACNTI00 und GENACNTI99 werden in `lgsetup.cbl` definiert:

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

Und in `lgwebst5.cbl` werden sie abgefragt:

```cobol
Exec CICS Query Counter(GENACNTI00)
         Pool(GENApool)
         Value(CountVal)
         Resp(WS-RESP)
End-Exec.
...
Exec CICS Query Counter(GENACNTI99)
         Pool(GENApool)
         Value(CountVal)
         Resp(WS-RESP)
End-Exec.
```

Jedoch ist nicht klar, ob diese Counter in anderen Teilen der Anwendung inkrementiert oder für geschäftliche Zwecke verwendet werden. Falls nicht, könnte dieser Code als unbenutzt betrachtet werden.

## Empfehlungen

1. **Überprüfung der tatsächlichen Counter-Nutzung**: Eine umfassende Analyse sollte durchgeführt werden, um festzustellen, welche Counter tatsächlich in der Anwendung inkrementiert und für geschäftliche Zwecke verwendet werden.

2. **Bereinigung ungenutzter Counter**: Counter, die nicht aktiv verwendet werden, sollten entfernt werden, um den Code zu vereinfachen und die Wartbarkeit zu verbessern.

3. **Dokumentation der verbleibenden Counter**: Die Bedeutung und Verwendung der verbleibenden Counter sollte dokumentiert werden, um die zukünftige Wartung zu erleichtern.

4. **Prüfung der Transaktionstypen**: Es sollte geprüft werden, ob alle in den Counter-Namen referenzierten Transaktionstypen (ICUS, ACUS, UCUS, IMOT, usw.) tatsächlich im System verwendet werden.
