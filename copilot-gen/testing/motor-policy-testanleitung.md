# Motor Policy Testanleitung

Diese Anleitung beschreibt die Durchführung der Tests zur Validierung der Funktionsgleichheit zwischen dem alten und dem neuen Motor Policy System.

## 1. Vorbereitungen

### 1.1 Testumgebungen aufsetzen

1. Stellen Sie sicher, dass beide Systeme (Alt- und Neusystem) in separaten Umgebungen laufen
2. Konfigurieren Sie die Datenbankverbindungen in beiden Systemen
3. Stellen Sie sicher, dass die APIs beider Systeme über die URLs erreichbar sind (siehe Konfiguration in `motor-policy-paralleltest.sh`)

### 1.2 Testdaten vorbereiten

1. Führen Sie das SQL-Skript `motor-policy-db-tests.sql` aus, um die Testdaten in beiden Datenbanken anzulegen
2. Verifizieren Sie, dass beide Datenbanken die gleichen initialen Testdaten enthalten

## 2. Testdurchführung

### 2.1 Ausführung der Paralleltests

1. Machen Sie das Skript ausführbar:
   ```bash
   chmod +x motor-policy-paralleltest.sh
   ```

2. Passen Sie die URLs und andere Konfigurationen im Skript an Ihre Umgebung an:
   ```bash
   vim motor-policy-paralleltest.sh
   # Ändern Sie die URLs:
   # ALT_SYSTEM_URL="http://alt-genapp-system:9080/api"
   # NEU_SYSTEM_URL="http://neu-genapp-system:9080/api"
   ```

3. Führen Sie das Skript aus:
   ```bash
   ./motor-policy-paralleltest.sh
   ```

4. Überwachen Sie die Ausgabe im Terminal und prüfen Sie die Log-Datei auf Fehler

### 2.2 Manuelle Tests

Folgen Sie der in `motor-policy-test-strategie.md` beschriebenen Teststrategie, um zusätzliche manuelle Tests durchzuführen:

1. Führen Sie die hinzugefügten Testfälle gegen beide Systeme aus
2. Dokumentieren Sie die Ergebnisse in der Vergleichstabelle
3. Analysieren Sie Unterschiede und führen Sie Root-Cause-Analysen durch

### 2.3 Datenbankvergleich

Nach dem Ausführen der Tests:

1. Führen Sie den Vergleichsteil des SQL-Skripts `motor-policy-db-tests.sql` aus, um die Datenbankinhalte zu vergleichen
2. Überprüfen Sie die Zusammenfassung der Unterschiede
3. Untersuchen Sie die identifizierten Abweichungen

## 3. Auswertung der Ergebnisse

### 3.1 Ergebnisanalyse

1. Öffnen Sie die CSV-Ergebnisdatei aus dem Paralleltest:
   ```bash
   cat paralleltest-ergebnisse-*.csv
   ```

2. Berechnen Sie die Erfolgsquote:
   ```bash
   grep -c ";JA;" paralleltest-ergebnisse-*.csv
   ```

3. Identifizieren Sie die Tests mit Unterschieden:
   ```bash
   grep ";NEIN;" paralleltest-ergebnisse-*.csv
   ```

### 3.2 Fehlerbehebung und Nachbesserung

Für jeden fehlgeschlagenen Test:

1. Analysieren Sie die genauen Unterschiede im Verhalten zwischen Alt- und Neusystem
2. Identifizieren Sie die Ursache im Code des neuen Systems
3. Beheben Sie die Abweichung
4. Führen Sie den Test erneut aus, um die Korrektur zu validieren

## 4. Abschlussbericht

Erstellen Sie einen Abschlussbericht, der folgende Punkte enthält:

1. Gesamtzahl der durchgeführten Tests
2. Erfolgsquote (% der Tests mit identischem Verhalten)
3. Liste der identifizierten Unterschiede und deren Behebung
4. Bewertung der Funktionsgleichheit und Empfehlung für die weitere Vorgehensweise

## 5. Zusätzliche Ressourcen

- `motor-policy-test-strategie.md`: Detaillierte Teststrategie
- `motor-policy-testdaten.json`: JSON-Testdaten für automatisierte Tests
- `motor-policy-paralleltest.sh`: Skript zur Durchführung der Paralleltests
- `motor-policy-db-tests.sql`: SQL-Skript für Datenbankvergleiche und Testdaten
