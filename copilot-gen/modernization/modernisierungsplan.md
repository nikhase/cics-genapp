# Modernisierungsplan für CICS-GenApp

## 1. Teamzusammensetzung

### Kernteam (Full-time)

| Rolle | Anzahl | Verantwortlichkeiten |
|-------|--------|----------------------|
| **Projektleiter / Scrum Master** | 1 | Projektverwaltung, Sprint-Planung, Beseitigung von Hindernissen |
| **Solution Architect** | 1 | Gesamtarchitektur, technische Entscheidungen, Modernisierungsstrategie |
| **Legacy-System-Experte (Mainframe/COBOL)** | 2 | Analyse des bestehenden Systems, Unterstützung bei der Migration |
| **Backend-Entwickler (Java/Spring)** | 4 | Implementierung der modernen Backend-Komponenten |
| **Frontend-Entwickler** | 2 | Entwicklung der neuen Benutzeroberflächen |
| **QA/Test-Ingenieure** | 2 | Testautomatisierung, Qualitätssicherung |
| **DevOps-Ingenieur** | 1 | CI/CD-Pipeline, Containerisierung, Cloud-Infrastruktur |
| **Datenbank-Spezialist** | 1 | Datenmigration, Datenbankoptimierung |

### Erweitertes Team (Part-time / Beratend)

| Rolle | Zeitaufwand | Verantwortlichkeiten |
|-------|-------------|----------------------|
| **Business-Analysten** | 50% | Anforderungsanalyse, Business-Prozess-Dokumentation |
| **UX/UI-Designer** | 30% | Design der Benutzeroberflächen |
| **Security-Experte** | 20% | Sicherheitskonzepte, Penetrationstests |
| **IT-Betrieb** | 20% | Abstimmung mit bestehender IT-Infrastruktur |
| **Fachbereichsvertreter** | 20% | Validierung der Geschäftsanforderungen |

## 2. Modernisierungs-Roadmap und Epics

### Phase 1: Vorbereitung und Analyse (3 Monate)

#### Epic 1: Systemanalyse und Dokumentation
**Ziel**: Vollständiges Verständnis des bestehenden Systems und seiner Funktionen
- Story 1.1: Analyse der COBOL-Programme und Erstellung von Funktionsdokumentation
- Story 1.2: Erfassung aller Geschäftsregeln und -prozesse
- Story 1.3: Analyse der Datenbankstruktur und Datenflüsse
- Story 1.4: Identifikation von technischen Schulden und Risikobereichen
- Story 1.5: Erstellung eines Feature-Katalogs für die Migration

**Geschätzter Aufwand**: 240 Personentage

#### Epic 2: Modernisierungsstrategie und Architekturdesign
**Ziel**: Detaillierte Architektur und Migrationsstrategie definieren
- Story 2.1: Entwicklung der Zielarchitektur (Monolith vs. Microservices Entscheidung)
- Story 2.2: Technologieauswahl und Evaluierung
- Story 2.3: Erstellung der Modernisierungsroadmap mit Prioritäten
- Story 2.4: Entwicklung von Performance-Benchmarks und Erfolgsmetriken
- Story 2.5: Erstellung von Proof of Concepts für kritische Komponenten

**Geschätzter Aufwand**: 120 Personentage

#### Epic 3: Einrichtung der Entwicklungsumgebung
**Ziel**: Produktive Entwicklungsinfrastruktur für das Team
- Story 3.1: Einrichtung von Entwicklungs-, Test- und Staging-Umgebungen
- Story 3.2: Implementierung der CI/CD-Pipeline
- Story 3.3: Einrichtung der Testautomatisierung
- Story 3.4: Konfiguration des Monitoring und Logging
- Story 3.5: Setup der Codequality- und Security-Scans

**Geschätzter Aufwand**: 60 Personentage

### Phase 2: Foundation und Proof of Concept (4 Monate)

#### Epic 4: Aufbau der Core-Infrastruktur
**Ziel**: Basiskomponenten für die modernisierte Anwendung
- Story 4.1: Implementierung des API-Gateways
- Story 4.2: Aufbau der Sicherheitsinfrastruktur (Authentifizierung/Autorisierung)
- Story 4.3: Implementierung der Datenzugriffsschicht
- Story 4.4: Einrichtung der Caching-Infrastruktur
- Story 4.5: Implementierung des Logging- und Monitoring-Frameworks

**Geschätzter Aufwand**: 160 Personentage

#### Epic 5: Legacy-Integration
**Ziel**: Nahtlose Kommunikation zwischen modernen und Legacy-Komponenten
- Story 5.1: Entwicklung des CICS-Adapters
- Story 5.2: Implementierung des DB2-Adapters
- Story 5.3: Aufbau der Datenkonvertierungslogik
- Story 5.4: Implementierung des Transaktionsmanagements über Systemgrenzen
- Story 5.5: Entwicklung eines Legacy-Monitoring-Systems

**Geschätzter Aufwand**: 200 Personentage

#### Epic 6: Commercial Property Policy Inquiry PoC
**Ziel**: Erste End-to-End-Funktion als Proof of Concept
- Story 6.1: Implementierung der Data Access Layer für Policy-Abfragen
- Story 6.2: Entwicklung der Business-Logik für Policy-Inquiry
- Story 6.3: Erstellung der REST-API-Endpoints
- Story 6.4: Entwicklung des Frontend-Prototyps
- Story 6.5: End-to-End-Tests und Performance-Optimierung

**Geschätzter Aufwand**: 120 Personentage

### Phase 3: Kernmodule-Modernisierung (8 Monate)

#### Epic 7: Policy-Domain-Modernisierung
**Ziel**: Vollständige Modernisierung des Policy-Managements
- Story 7.1: Implementierung des Commercial Policy Managements
- Story 7.2: Entwicklung des Home Policy Managements
- Story 7.3: Implementierung des Auto Policy Managements
- Story 7.4: Entwicklung des Travel Policy Managements
- Story 7.5: Implementierung der domänenübergreifenden Policensuche

**Geschätzter Aufwand**: 320 Personentage

#### Epic 8: Customer-Domain-Modernisierung
**Ziel**: Vollständige Modernisierung des Kundenmanagements
- Story 8.1: Implementierung der Kundenanlage und -verwaltung
- Story 8.2: Entwicklung der Kundensuchfunktionen
- Story 8.3: Implementierung der Kundenhistorie
- Story 8.4: Entwicklung des Kontomanagements
- Story 8.5: Implementierung des Kundenportals

**Geschätzter Aufwand**: 280 Personentage

#### Epic 9: Claim-Domain-Modernisierung
**Ziel**: Vollständige Modernisierung der Schadensbearbeitung
- Story 9.1: Implementierung der Schadenserfassung
- Story 9.2: Entwicklung der Schadensbearbeitung
- Story 9.3: Implementierung des Zahlungsmanagements
- Story 9.4: Entwicklung des Rückversicherungs-Interfaces
- Story 9.5: Implementierung des Schadenreportings

**Geschätzter Aufwand**: 300 Personentage

#### Epic 10: Support-Services-Modernisierung
**Ziel**: Implementierung aller unterstützenden Dienste
- Story 10.1: Entwicklung des Reporting-Services
- Story 10.2: Implementierung des Notification-Services
- Story 10.3: Entwicklung des Dokumentenmanagements
- Story 10.4: Implementierung des Audit-Services
- Story 10.5: Entwicklung des Batch-Processing-Systems

**Geschätzter Aufwand**: 240 Personentage

### Phase 4: Integration und Erweiterung (6 Monate)

#### Epic 11: Externe System-Integrationen
**Ziel**: Anbindung aller externen Systeme
- Story 11.1: Integration mit dem Payment-Processing-System
- Story 11.2: Anbindung des Risk-Rating-Systems
- Story 11.3: Integration mit dem Document-Generation-System
- Story 11.4: Anbindung der Partner-APIs
- Story 11.5: Integration mit regulatorischen Reporting-Systemen

**Geschätzter Aufwand**: 180 Personentage

#### Epic 12: Mobile App-Entwicklung
**Ziel**: Native mobile Anwendung für Kunden
- Story 12.1: Entwicklung der App-Infrastruktur
- Story 12.2: Implementierung des Policy-Managements auf Mobilgeräten
- Story 12.3: Entwicklung der Schadensmeldung via App
- Story 12.4: Implementierung von Push-Benachrichtigungen
- Story 12.5: Integration von biometrischer Authentifizierung

**Geschätzter Aufwand**: 200 Personentage

#### Epic 13: Advanced Analytics und Reporting
**Ziel**: Erweiterte Datenanalyse und Reporting-Funktionen
- Story 13.1: Implementierung des Data-Warehouse-Connectors
- Story 13.2: Entwicklung von Business-Intelligence-Dashboards
- Story 13.3: Implementierung von Ad-hoc-Reporting
- Story 13.4: Entwicklung von Vorhersagemodellen
- Story 13.5: Integration mit Data-Science-Werkzeugen

**Geschätzter Aufwand**: 160 Personentage

### Phase 5: Legacy-Ablösung und Optimierung (3 Monate)

#### Epic 14: Vollständige Datenmigration
**Ziel**: Komplette Migration aller Daten in das neue System
- Story 14.1: Entwicklung der Datenmigrationsstrategie
- Story 14.2: Implementierung der Migrationsskripte
- Story 14.3: Durchführung von Testmigrationen
- Story 14.4: Planung und Durchführung der finalen Migration
- Story 14.5: Datenqualitätsprüfung und Bereinigung

**Geschätzter Aufwand**: 140 Personentage

#### Epic 15: Performance-Optimierung und Skalierung
**Ziel**: Optimierung für hohen Durchsatz und niedrige Latenz
- Story 15.1: Durchführung von Last- und Performance-Tests
- Story 15.2: Optimierung von Datenbankabfragen
- Story 15.3: Implementierung von zusätzlichen Caching-Strategien
- Story 15.4: Feintuning der Anwendungskonfiguration
- Story 15.5: Skalierungstests und -optimierung

**Geschätzter Aufwand**: 120 Personentage

#### Epic 16: Dekommissionierung von Legacy-Komponenten
**Ziel**: Geordnete Außerbetriebnahme der Legacy-Systeme
- Story 16.1: Erstellung eines Dekommissionierungsplans
- Story 16.2: Schrittweise Abschaltung von Legacy-Komponenten
- Story 16.3: Archivierung relevanter Legacy-Daten
- Story 16.4: Dokumentation der dekommissionierten Systeme
- Story 16.5: Abschlussprüfung und Signoff

**Geschätzter Aufwand**: 100 Personentage

## 3. Zeitplanung und Meilensteine

| Phase | Dauer | Meilensteine | Geschätztes Ende |
|-------|-------|-------------|-----------------|
| **1: Vorbereitung und Analyse** | 3 Monate | • Vollständige System-Dokumentation<br>• Architekturkonzept finalisiert<br>• Entwicklungsumgebung betriebsbereit | Monat 3 |
| **2: Foundation und PoC** | 4 Monate | • API-Gateway produktiv<br>• Legacy-Integration funktionsfähig<br>• Erste Funktion (Commercial Property Policy) produktiv | Monat 7 |
| **3: Kernmodule-Modernisierung** | 8 Monate | • Policy-Management vollständig modernisiert<br>• Kundenmanagement vollständig modernisiert<br>• Schadensmanagement vollständig modernisiert<br>• Support-Services implementiert | Monat 15 |
| **4: Integration und Erweiterung** | 6 Monate | • Alle externen Systeme angebunden<br>• Mobile App veröffentlicht<br>• Analytics-Plattform implementiert | Monat 21 |
| **5: Legacy-Ablösung und Optimierung** | 3 Monate | • Vollständige Datenmigration abgeschlossen<br>• Performance-Ziele erreicht<br>• Legacy-Systeme dekommissioniert | Monat 24 |

## 4. Ressourcenbedarf und Budget

### Personalbedarf
- **Kernteam**: 14 Vollzeit-Mitarbeiter
- **Erweitertes Team**: Entspricht ca. 2,4 Vollzeitäquivalenten
- **Gesamter Personalbedarf**: 16,4 Vollzeitäquivalente

### Grobe Budgetschätzung
- **Personalkosten**: Ca. 5,7 Mio. € (basierend auf durchschnittlich 145.000 € pro Person/Jahr)
- **Infrastruktur & Lizenzen**: Ca. 500.000 €
- **Schulungen & Workshops**: Ca. 150.000 €
- **Externe Beratung**: Ca. 400.000 €
- **Risikobudget (15%)**: Ca. 1.0 Mio. €
- **Gesamt**: Ca. 7,75 Mio. €

## 5. Risiken und Maßnahmen

| Risiko | Auswirkung | Eintrittswahrscheinlichkeit | Maßnahmen |
|--------|------------|------------------------------|-----------|
| **Unvollständige Dokumentation des Legacy-Systems** | Hoch | Hoch | • Frühzeitige Einbindung von Legacy-Experten<br>• Reverse-Engineering-Tools einsetzen<br>• Systematische Code-Analyse |
| **Performance-Probleme nach Modernisierung** | Hoch | Mittel | • Performance-Benchmark vor der Migration<br>• Kontinuierliche Performance-Tests<br>• Performance-optimierte Architektur |
| **Fachliche Logik geht bei der Migration verloren** | Hoch | Mittel | • Umfassende Tests mit echten Geschäftsszenarien<br>• Parallelbetrieb in der Übergangsphase<br>• Automatisierte Regressionstests |
| **Verzögerungen durch komplexe Integration** | Mittel | Hoch | • Klare Integrationsschnittstellen definieren<br>• Frühzeitige Integration testen<br>• Mockups für externe Systeme nutzen |
| **Widerstand der Endanwender** | Mittel | Mittel | • Frühzeitige Einbindung der Anwender<br>• Schulungsprogramm entwickeln<br>• Schrittweise Einführung mit Feedbackschleifen |
| **Budgetüberschreitung** | Hoch | Mittel | • Regelmäßige Budget-Reviews<br>• Agiles Scope-Management<br>• Pufferbudget einplanen |

## 6. Erfolgsfaktoren

1. **Klare Governance**: Etablierung einer klaren Entscheidungsstruktur mit definierten Verantwortlichkeiten
2. **Iteratives Vorgehen**: Regelmäßige Lieferung von funktionierendem Code mit kontinuierlichem Feedback
3. **Fokus auf Geschäftswert**: Priorisierung von Funktionen mit höchstem Geschäftswert
4. **Automatisierung**: Umfassende Test- und Deployment-Automatisierung zur Qualitätssicherung
5. **Wissenstransfer**: Systematischer Wissenstransfer von Legacy-Experten zum Entwicklungsteam
6. **Parallelbetrieb**: Zeitweise paralleler Betrieb von altem und neuem System zur Risikominimierung
7. **Kontinuierliche Bewertung**: Regelmäßige Bewertung des Projektfortschritts und Anpassung der Strategie

## 7. Nächste Schritte

1. **Team aufbauen**: Rekrutierung und Onboarding der Kernteam-Mitglieder
2. **Kick-off-Workshop**: Gemeinsames Verständnis für Vision und Ziele schaffen
3. **Systemanalyse starten**: Beginn der detaillierten Analyse des bestehenden Systems
4. **Entwicklungsumgebung einrichten**: Bereitstellung der initialen Entwicklungsinfrastruktur
5. **PoC definieren**: Detaillierte Anforderungen für den ersten Proof of Concept (Commercial Property Policy Inquiry)
