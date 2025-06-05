#!/bin/bash
# Paralleltest-Skript für Motor Policy Modernisierung
# Dieses Skript führt Tests parallel im alten und neuen System aus und vergleicht die Ergebnisse

# Konfiguration
ALT_SYSTEM_URL="http://alt-genapp-system:9080/api"
NEU_SYSTEM_URL="http://neu-genapp-system:9080/api"
TESTDATEN_DATEI="./motor-policy-testdaten.json"
ERGEBNIS_DATEI="./paralleltest-ergebnisse-$(date +%Y%m%d-%H%M%S).csv"
LOG_DATEI="./paralleltest-log-$(date +%Y%m%d-%H%M%S).log"

# Header der Ergebnisdatei
echo "Test-ID;Testfall;Erwartetes Ergebnis;Ergebnis Altsystem;Ergebnis Neusystem;Übereinstimmung;Anmerkungen" > "$ERGEBNIS_DATEI"

# Logging-Funktion
log() {
  local message="$1"
  local timestamp=$(date +"%Y-%m-%d %H:%M:%S")
  echo "[$timestamp] $message" | tee -a "$LOG_DATEI"
}

# Funktion zum Ausführen eines einzelnen Tests
run_test() {
  local test_id="$1"
  local description="$2"
  local operation="$3"
  local input_json="$4"
  local expected_json="$5"

  log "Starte Test: $test_id - $description"
  log "Operation: $operation"
  log "Eingabe: $input_json"
  
  # Ausführen der API-Anfrage im alten System
  log "Rufe altes System auf..."
  local alt_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$input_json" "$ALT_SYSTEM_URL/$operation")
  log "Alt-System Antwort: $alt_response"

  # Ausführen der API-Anfrage im neuen System
  log "Rufe neues System auf..."
  local neu_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$input_json" "$NEU_SYSTEM_URL/$operation")
  log "Neu-System Antwort: $neu_response"
  
  # Vergleich der Antworten
  local match="NEIN"
  local notes=""
  
  # Einfacher Vergleich der JSON-Antworten (kann erweitert werden, um spezifische Felder zu vergleichen)
  if [ "$(echo "$alt_response" | jq -S)" = "$(echo "$neu_response" | jq -S)" ]; then
    match="JA"
  else
    # Analyse der Unterschiede
    local diff_output=$(diff <(echo "$alt_response" | jq -S) <(echo "$neu_response" | jq -S))
    notes="Unterschiede: $(echo "$diff_output" | tr '\n' ' ' | tr ';' ',')"
    log "Unterschiede gefunden: $diff_output"
  fi
  
  # Bei Datenbankprüfungen
  if [[ "$expected_json" == *"dbCheck"* ]]; then
    log "Führe Datenbankprüfungen durch..."
    
    # Datenbankzugriffe müssten hier implementiert werden
    # Beispiel: Prüfen, ob ein Datensatz existiert oder bestimmte Werte hat
    
    # Beispielhafte Implementierung für dbCheck="recordExists"
    if [[ "$operation" == "add" && "$(echo "$expected_json" | jq -r '.dbCheck')" == "recordExists" ]]; then
      local policy_num_alt=$(echo "$alt_response" | jq -r '.CA_POLICY_NUM')
      local policy_num_neu=$(echo "$neu_response" | jq -r '.CA_POLICY_NUM')
      
      # Prüfen, ob der Datensatz im Altsystem existiert
      local alt_db_check=$(curl -s "$ALT_SYSTEM_URL/verify_policy?policyNum=$policy_num_alt")
      
      # Prüfen, ob der Datensatz im Neusystem existiert
      local neu_db_check=$(curl -s "$NEU_SYSTEM_URL/verify_policy?policyNum=$policy_num_neu")
      
      if [ "$(echo "$alt_db_check" | jq -r '.exists')" != "$(echo "$neu_db_check" | jq -r '.exists')" ]; then
        match="NEIN"
        notes="$notes Datenbankprüfung fehlgeschlagen."
      fi
    fi
  fi
  
  # Speichern des Ergebnisses
  local expected_result=$(echo "$expected_json" | jq -r 'if .message then .message else .CA_RETURN_CODE end')
  local alt_result=$(echo "$alt_response" | jq -r 'if .message then .message else .CA_RETURN_CODE end')
  local neu_result=$(echo "$neu_response" | jq -r 'if .message then .message else .CA_RETURN_CODE end')
  
  echo "$test_id;\"$description\";\"$expected_result\";\"$alt_result\";\"$neu_result\";$match;\"$notes\"" >> "$ERGEBNIS_DATEI"
  
  log "Test $test_id abgeschlossen. Übereinstimmung: $match"
  log "----------------------------------------"
}

# Hauptprogramm
log "Starte Paralleltests für Motor Policy"
log "Altes System: $ALT_SYSTEM_URL"
log "Neues System: $NEU_SYSTEM_URL"
log "Testdatendatei: $TESTDATEN_DATEI"

# Prüfen, ob jq installiert ist
if ! command -v jq &> /dev/null; then
  log "FEHLER: jq ist nicht installiert. Bitte installieren Sie jq, um dieses Skript ausführen zu können."
  exit 1
fi

# Prüfen, ob die Testdatendatei existiert
if [ ! -f "$TESTDATEN_DATEI" ]; then
  log "FEHLER: Testdatendatei $TESTDATEN_DATEI nicht gefunden."
  exit 1
fi

# Für jede Testgruppe
for test_set in $(jq -r '.testSets | keys[]' "$TESTDATEN_DATEI"); do
  log "Verarbeite Testgruppe: $test_set"
  
  # Anzahl der Testfälle in der Gruppe
  test_count=$(jq -r ".testSets[\"$test_set\"].testCases | length" "$TESTDATEN_DATEI")
  
  # Für jeden Testfall in der Gruppe
  for ((i=0; i<$test_count; i++)); do
    test_id=$(jq -r ".testSets[\"$test_set\"].testCases[$i].testId" "$TESTDATEN_DATEI")
    description=$(jq -r ".testSets[\"$test_set\"].testCases[$i].description" "$TESTDATEN_DATEI")
    operation=$(jq -r ".testSets[\"$test_set\"].testCases[$i].operation" "$TESTDATEN_DATEI")
    input_json=$(jq -c ".testSets[\"$test_set\"].testCases[$i].input" "$TESTDATEN_DATEI")
    expected_json=$(jq -c ".testSets[\"$test_set\"].testCases[$i].expectedResult" "$TESTDATEN_DATEI")
    
    run_test "$test_id" "$description" "$operation" "$input_json" "$expected_json"
  done
done

# Abschluss und Zusammenfassung
total_tests=$(wc -l < "$ERGEBNIS_DATEI")
((total_tests--))  # Header-Zeile abziehen
successful_tests=$(grep -c ";JA;" "$ERGEBNIS_DATEI")
failed_tests=$((total_tests - successful_tests))

log "Paralleltests abgeschlossen."
log "Gesamtanzahl der Tests: $total_tests"
log "Erfolgreiche Tests: $successful_tests"
log "Fehlgeschlagene Tests: $failed_tests"
log "Erfolgsquote: $(echo "scale=2; $successful_tests * 100 / $total_tests" | bc)%"
log "Ergebnisse wurden in $ERGEBNIS_DATEI gespeichert."
log "Logs wurden in $LOG_DATEI gespeichert."

if [ $failed_tests -gt 0 ]; then
  log "WARNUNG: Es wurden $failed_tests fehlgeschlagene Tests festgestellt."
  exit 1
else
  log "ERFOLG: Alle Tests waren erfolgreich."
  exit 0
fi
