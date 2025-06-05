# Motor Policy API Dokumentation

Dies ist die OpenAPI-Spezifikation für die modernisierte Motor Policy API des GenApp-Versicherungssystems.

## Übersicht

Die API wurde basierend auf den Anforderungen aus dem Motor Policy-Anforderungsdokument entwickelt und verwendet moderne REST-Prinzipien und Best Practices. Sie ist in OpenAPI 3.0.3 spezifiziert.

## API-Struktur

Die API ist hierarchisch strukturiert und folgt dem Ressourcen-orientierten Design:

1. **Kunden-Ressource** (`/customers`)
   - Verwaltung von Kundendaten

2. **Policen-Ressource** (`/customers/{customerId}/policies`)
   - Allgemeine Verwaltung von Versicherungspolicen

3. **Motor-Policen-Ressource** (`/customers/{customerId}/policies/motor`)
   - Spezifische Verwaltung von Kfz-Versicherungspolicen

## Hauptfunktionalitäten

Die API unterstützt alle im Anforderungsdokument spezifizierten Kernfunktionalitäten:

- **Erstellung einer Motor Policy** (POST-Endpunkt)
- **Abfrage einer Motor Policy** (GET-Endpunkt)
- **Aktualisierung einer Motor Policy** (PUT-Endpunkt)
- **Löschung einer Motor Policy** (DELETE-Endpunkt)

## Verwendung der API-Dokumentation

Die OpenAPI-Spezifikation kann mit Swagger UI, Redoc oder einem anderen OpenAPI-kompatiblen Tool visualisiert und getestet werden.

### Mit Swagger UI visualisieren

1. Besuchen Sie [Swagger Editor](https://editor.swagger.io/)
2. Laden Sie die `motor-policy-openapi.yaml`-Datei hoch
3. Erkunden Sie die API-Dokumentation und testen Sie die API-Endpunkte

### Mit Redoc visualisieren

Wenn Sie Redoc lokal installiert haben:

```bash
npx @redocly/cli preview-docs motor-policy-openapi.yaml
```

## Fehlerbehandlung

Die API verwendet Standard-HTTP-Statuscodes und gibt detaillierte Fehlermeldungen im JSON-Format zurück. Jede Fehlermeldung enthält:

- Zeitstempel des Fehlers
- HTTP-Statuscode
- Fehlertyp und -meldung
- Applikations-spezifischen Fehlercode
- Path, der den Fehler verursacht hat
- Details zu Validierungsfehlern (falls vorhanden)

## Sicherheit

Die API verwendet JWT-basierte Authentifizierung und Autorisierung. Ein gültiger JWT-Token muss im Authorization-Header jeder Anfrage mitgesendet werden.

## Implementierung

Diese Spezifikation kann mit Spring Boot implementiert werden, wie im Anforderungsdokument empfohlen. Die Spring Boot-Implementierung würde die folgenden Komponenten umfassen:

- Controller für REST-Endpunkte
- Service-Schicht für Geschäftslogik
- Repository-Schicht für Datenbankzugriff
- DTOs für Request/Response-Objekte
- Validierungslogik
- Security-Konfiguration für JWT-Authentifizierung
