# GitHub Copilot Anweisungen für das CICS-GenApp Repository

## Projektübersicht
CICS-GenApp ist eine Anwendung für allgemeine Versicherungen, entwickelt für IBM CICS Transaction Server für z/OS. Das Repository demonstriert Anwendungsmodernisierung und wurde ursprünglich als IBM SupportPac CB12 vertrieben. Die Hauptkomponente ist eine COBOL-Anwendung ("GenApp base"), die verschiedene CICS-Funktionen nutzt und als Ausgangspunkt für Modernisierungsbemühungen dient.

## Technologie-Stack
- **Programmiersprachen**: Hauptsächlich COBOL
- **Systeme**: CICS Transaction Server für z/OS (Version 4.1 oder höher)
- **Datenbanken**: IBM Db2, VSAM KSDS (Key-Sequenced Data Sets)
- **Schnittstellen**: 3270-Terminal, SOAP/JSON Web Services (in modernisierten Versionen)
- **Job Control Language (JCL)**: Für Batch-Verarbeitung und System-Setup

## Projektstruktur
- `/base`: Die Kern-Versicherungsanwendung
  - `/bin`: Installation und Setup-Skripte
  - `/cntl`: JCL-Jobs zur Konfiguration der Anwendung
  - `/data`: Beispieldaten für die Anwendung
  - `/exec`: REXX-Code zur Anpassung der JCL
  - `/images`: Visuelle Dokumentation
  - `/src`: COBOL-Programme und Kopierelemente
  - `/wsim`: IBM Workload Simulator Skripte

## Anwendungsfunktionalitäten
- Kundenverwaltung (Hinzufügen, Abfragen, Aktualisieren)
- Policenverwaltung für verschiedene Versicherungstypen:
  - Kfz-Versicherungen
  - Hausratversicherungen
  - Kapitallebensversicherungen
  - Gewerbeversicherungen
- VSAM- und Db2-Datenbankinteraktion
- Webservice-Schnittstellen (in modernisierten Versionen)

## COBOL-Programm-Konventionen
- Programme beginnen mit "LG" (vermutlich für "Legacy")
- Programmkategorien folgen standardisierten Präfixen:
  - "LGA": Kundenoperationen (Add)
  - "LGI": Abfrageoperationen (Inquire)
  - "LGU": Update-Operationen
  - "LGD": Löschoperationen (Delete)
- Datenzugriffsschicht mit "DB" gekennzeichnet (z.B. LGACDB01)
- VS-Suffix deutet auf VSAM-Zugriff hin (z.B. LGIPVS01)
- Gemeinsame Datenstrukturen in Kopier-Dateien (.cpy)

## Modernisierungsperspektiven
Das Repository zeigt die Entwicklung einer Legacy-Anwendung über verschiedene Modernisierungsphasen:
1. Basis-3270-Terminal-Anwendung mit Db2-Backend
2. Erweiterung mit SOAP/JSON Web Services
3. Integration mit Liberty Profile
4. Cloud-fähige Erweiterungen
5. Business Rules Management

## Bekannte Muster und Praktiken
- Verwendung von CICS EXEC-Befehlen für Systeminteraktion
- Zwei-Phasen-Commit für transaktionale Integrität
- Named Counter Server für eindeutige Schlüsselgenerierung
- COMMAREA für Datenaustausch zwischen Programmen
- Web Service-Bindings (WSBIND) für SOAP-Integration
- VSAM KSDS für schnellen Schlüsselzugriff auf Daten

## Bekannte Herausforderungen
1. Legacy-Code-Modernisierung ohne Beeinträchtigung der Funktionalität
2. Integration mit modernen Web-Services und REST-APIs
3. Performance-Optimierung bei komplexen Datenbank-Transaktionen
4. Anpassung der Anwendung für verschiedene CICS-Umgebungen
5. Konfiguration und Anpassung für spezifische Nutzerumgebungen

## Installationshinweise
Die Anwendung erfordert:
- CICS TS V4.1 oder höher
- IBM Db2 (oder nur VSAM in der Lite-Version)
- COBOL-Compiler
- USS-Umgebung (UNIX System Services) für bestimmte Komponenten
- Anpassung von Konfigurationsparametern über CUST1 REXX-Skript

## Entwicklungsbezogene Anweisungen
Bei der Arbeit mit diesem Repository:
1. Beachte die strikte Trennung zwischen Datenzugriffscode und Geschäftslogik
2. Verwende die etablierten Namenskonventionen für neue Programme
3. Berücksichtige CICS-spezifische Aspekte bei der Codemodifikation
4. Teste Änderungen gründlich, um transaktionale Integrität zu gewährleisten
5. Dokumentiere Änderungen und erweitere die Changes.md bei signifikanten Updates
6. Beachte die Eclipse Public License 2.0 für alle Code-Änderungen

## Nützliche Testtransaktionen
- SSC1: Kundenverwaltung
- SSP1: Kfz-Versicherungspolice
- SSP2: Hausratversicherungspolice
- SSP3: Kapitallebensversicherungspolice
- SSP4: Gewerbeversicherungspolice

## Wartungshinweise
- Das Repository wird seit 2011 gepflegt, mit dem letzten signifikanten Update im November 2023
- Fehlerbehebungen fokussieren sich oft auf COMMAREA-Größen und Schnittstellen
- Updates erfolgen, um Kompatibilität mit neueren CICS- und COBOL-Versionen zu gewährleisten
