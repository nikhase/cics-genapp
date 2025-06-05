    workspace "GenApp Legacy System" "C4 Modell für die bestehende CICS-GenApp" {
        model {
            // Personen/Akteure
            customer = person "Kunde" "Ein Kunde der Versicherungsgesellschaft"
            customerServiceAgent = person "Kundendienstmitarbeiter" "Bearbeitet Kundenanfragen und verwaltet Policen"
            underwriter = person "Underwriter" "Bewertet Risiken und erstellt Angebote"
            claimAdjuster = person "Schadensachbearbeiter" "Bearbeitet und reguliert Schadensfälle"
            administrator = person "System Administrator" "Verwaltet das Mainframe-System"

            // Hauptsystem
            genAppSystem = softwareSystem "GenApp Versicherungssystem" "Legacy-Kerngeschäftssystem zur Verwaltung von Versicherungspolicen und Kundendaten" {
                // Benutzerschnittstellen
                cicsTerminal = container "CICS Terminal" "3270 Terminal-Emulation für die Interaktion mit dem System" "3270 Terminal Emulator" {
                    customerMenu = component "Kundenmenü" "CICS Transaktionen für die Kundenverwaltung (SSC1)" "CICS Screen"
                    motorPolicyMenu = component "KFZ-Versicherungsmenü" "CICS Transaktionen für KFZ-Policen (SSP1)" "CICS Screen"
                    endowmentPolicyMenu = component "Kapitallebensversicherungsmenü" "CICS Transaktionen für Kapitallebensversicherungen (SSP2)" "CICS Screen"
                    housePolicyMenu = component "Wohngebäudeversicherungsmenü" "CICS Transaktionen für Wohngebäudeversicherungen (SSP3)" "CICS Screen"
                    commercialPolicyMenu = component "Gewerbeversicherungsmenü" "CICS Transaktionen für Gewerbeversicherungen (SSP4)" "CICS Screen"
                    claimMenu = component "Schadensmenü" "CICS Transaktionen für die Schadensbearbeitung (SSP5)" "CICS Screen"
                }

                // Anwendungslogik - CICS-Region
                cicsRegion = container "CICS Region" "Region für die Ausführung der CICS-Transaktionen" "IBM CICS" {
                    // Hauptkomponenten für Kundendaten
                    customerInquiry = component "Kundenabfrage" "Liest Kundendaten aus DB2" "LGICUS01 (COBOL)"
                    customerAdd = component "Kundenanlage" "Fügt neue Kunden zur DB2 hinzu" "LGACUS01 (COBOL)"
                    customerUpdate = component "Kundenaktualisierung" "Aktualisiert Kundendaten in DB2" "LGUCUS01 (COBOL)"
                    
                    // Hauptkomponenten für KFZ-Versicherungen
                    motorPolicyInquiry = component "KFZ-Policenabfrage" "Liest KFZ-Policendaten" "LGIPOL01 (COBOL)"
                    motorPolicyAdd = component "KFZ-Policenanlage" "Erstellt neue KFZ-Policen" "LGAPOL01 (COBOL)"
                    motorPolicyUpdate = component "KFZ-Policenaktualisierung" "Aktualisiert KFZ-Policendaten" "LGUPOL01 (COBOL)"
                    
                    // Hauptkomponenten für Wohngebäudeversicherungen
                    housePolicyInquiry = component "Haus-Policenabfrage" "Liest Hausvesicherungsdaten" "LGIPOL01 (COBOL)"
                    housePolicyAdd = component "Haus-Policenanlage" "Erstellt neue Hausversicherungspolicen" "LGAPOL01 (COBOL)"
                    housePolicyUpdate = component "Haus-Policenaktualisierung" "Aktualisiert Hausversicherungsdaten" "LGUPOL01 (COBOL)"
                    
                    // Hauptkomponenten für Kapitallebensversicherungen
                    endowmentPolicyInquiry = component "Lebensversicherungsabfrage" "Liest Kapitallebensversicherungsdaten" "LGIPOL01 (COBOL)"
                    endowmentPolicyAdd = component "Lebensversicherungsanlage" "Erstellt neue Lebensversicherungspolicen" "LGAPOL01 (COBOL)"
                    endowmentPolicyUpdate = component "Lebensversicherungsaktualisierung" "Aktualisiert Lebensversicherungsdaten" "LGUPOL01 (COBOL)"
                    
                    // Hauptkomponenten für Gewerbeversicherungen
                    commercialPolicyInquiry = component "Gewerbeversicherungsabfrage" "Liest Gewerbeversicherungsdaten" "LGIPOL01 (COBOL)"
                    commercialPolicyAdd = component "Gewerbeversicherungsanlage" "Erstellt neue Gewerbeversicherungspolicen" "LGAPOL01 (COBOL)"
                    commercialPolicyUpdate = component "Gewerbeversicherungsaktualisierung" "Aktualisiert Gewerbeversicherungsdaten" "LGUPOL01 (COBOL)"
                    
                    // Datenzugriffskomponenten
                    customerDB = component "Kundendatenzugriff" "DB2-Zugriff für Kundendaten" "LGICDB01, LGACDB01, LGUCDB01 (COBOL)"
                    policyDB = component "Policendatenzugriff" "DB2-Zugriff für Policendaten" "LGIPDB01, LGAPDB01, LGUPDB01 (COBOL)"
                    
                    // Validierungskomponenten
                    customerValidation = component "Kundenvalidierung" "Validiert Kundendaten" "LGICVS01, LGACVS01, LGUCVS01 (COBOL)"
                    policyValidation = component "Policenvalidierung" "Validiert Policendaten" "LGIPVS01, LGAPVS01, LGUPVS01 (COBOL)"
                    
                    // Batchverarbeitung und Reporting
                    batchProcessing = component "Batch-Verarbeitung" "Führt geplante Batch-Jobs aus" "LGSETUP (COBOL)"
                    reporting = component "Reporting" "Generiert Berichte über Kunden und Policen" "LGTESTC1, LGTESTP1 (COBOL)"
                    
                    // TSQ-Management
                    temporaryStorage = component "Temporary Storage" "Verwaltet temporäre Speicherwarteschlangen" "LGSTSQ (COBOL)"
                }

                // Datenbanken
                db2Database = container "DB2 Datenbank" "Speichert alle Geschäftsdaten" "IBM DB2" "Database" {
                    customerTable = component "Kundentabelle" "Speichert Kundendaten" "DB2 Table"
                    policyTable = component "Policentabelle" "Speichert Policendaten für alle Versicherungsarten" "DB2 Table"
                    claimTable = component "Schadenstabelle" "Speichert Schadensfälle" "DB2 Table"
                }
                
                // VSAM Files
                vsamFiles = container "VSAM Files" "Zusätzliche Datenspeicherung für bestimmte Anwendungsfälle" "VSAM" {
                    configFiles = component "Konfigurationsdateien" "Speichert Systemkonfigurationen" "VSAM File"
                    archiveFiles = component "Archivdateien" "Speichert archivierte Daten" "VSAM File"
                }
                
                // JCL Batch Jobs
                batchSystem = container "Batch System" "System für die Ausführung von Batch-Jobs" "JCL/Batch" {
                    policyBatch = component "Policen Batch Jobs" "Batch-Verarbeitung für Policen" "JCL"
                    reportBatch = component "Report Batch Jobs" "Batch-Verarbeitung für Berichte" "JCL"
                    maintenanceBatch = component "Wartungs-Jobs" "Systemwartungs-Jobs" "JCL"
                }
                
                // REXX Scripts
                rexxScripts = container "REXX Scripts" "Skripte für verschiedene Automatisierungsaufgaben" "REXX" {
                    customerScript = component "Kunden-Skript" "REXX-Skript für Kundendatenverarbeitung" "cust1.rexx"
                    maintenanceScript = component "Wartungs-Skript" "REXX-Skript für Systemwartung" "mac1.rexx"
                }
            }
            
            // Externe Systeme
            printSystem = softwareSystem "Drucksystem" "System für das Drucken von Dokumenten und Berichten" "Externes System"
            archiveSystem = softwareSystem "Archivsystem" "System für die langfristige Archivierung von Daten" "Externes System"
            
            // Beziehungen: Personen -> System
            customer -> genAppSystem "Erhält Versicherungsdienstleistungen"
            customerServiceAgent -> genAppSystem "Verwaltet Kunden und Policen"
            underwriter -> genAppSystem "Bewertet Risiken und erstellt Angebote"
            claimAdjuster -> genAppSystem "Bearbeitet Schadensfälle"
            administrator -> genAppSystem "Administriert"
            
            // Beziehungen: Personen -> Container
            customerServiceAgent -> cicsTerminal "Interagiert über"
            underwriter -> cicsTerminal "Interagiert über"
            claimAdjuster -> cicsTerminal "Interagiert über"
            administrator -> cicsTerminal "Administriert über"
            administrator -> batchSystem "Konfiguriert und überwacht"
            
            // Beziehungen: Container -> Container
            cicsTerminal -> cicsRegion "Sendet Transaktionsanfragen"
            cicsRegion -> db2Database "Liest und schreibt Daten"
            cicsRegion -> vsamFiles "Liest und schreibt Daten"
            batchSystem -> db2Database "Verarbeitet und aktualisiert Daten"
            batchSystem -> vsamFiles "Liest und schreibt Daten"
            rexxScripts -> db2Database "Verarbeitet Daten"
            
            // Beziehungen: Container -> External Systems
            cicsRegion -> printSystem "Sendet Druckaufträge"
            batchSystem -> archiveSystem "Archiviert Daten"
            
            // Beziehungen: CICS Terminal Komponenten
            customerMenu -> customerInquiry "Ruft auf"
            customerMenu -> customerAdd "Ruft auf"
            customerMenu -> customerUpdate "Ruft auf"
            
            motorPolicyMenu -> motorPolicyInquiry "Ruft auf"
            motorPolicyMenu -> motorPolicyAdd "Ruft auf"
            motorPolicyMenu -> motorPolicyUpdate "Ruft auf"
            
            housePolicyMenu -> housePolicyInquiry "Ruft auf"
            housePolicyMenu -> housePolicyAdd "Ruft auf"
            housePolicyMenu -> housePolicyUpdate "Ruft auf"
            
            endowmentPolicyMenu -> endowmentPolicyInquiry "Ruft auf"
            endowmentPolicyMenu -> endowmentPolicyAdd "Ruft auf"
            endowmentPolicyMenu -> endowmentPolicyUpdate "Ruft auf"
            
            commercialPolicyMenu -> commercialPolicyInquiry "Ruft auf"
            commercialPolicyMenu -> commercialPolicyAdd "Ruft auf"
            commercialPolicyMenu -> commercialPolicyUpdate "Ruft auf"
            
            // Beziehungen: CICS Transaktionskomponenten
            customerInquiry -> customerValidation "Validiert Anfragen"
            customerAdd -> customerValidation "Validiert Eingaben"
            customerUpdate -> customerValidation "Validiert Änderungen"
            
            customerInquiry -> customerDB "Ruft ab"
            customerAdd -> customerDB "Speichert"
            customerUpdate -> customerDB "Aktualisiert"
            
            motorPolicyInquiry -> policyValidation "Validiert Anfragen"
            motorPolicyAdd -> policyValidation "Validiert Eingaben"
            motorPolicyUpdate -> policyValidation "Validiert Änderungen"
            
            motorPolicyInquiry -> policyDB "Ruft ab"
            motorPolicyAdd -> policyDB "Speichert"
            motorPolicyUpdate -> policyDB "Aktualisiert"
            
            housePolicyInquiry -> policyValidation "Validiert Anfragen"
            housePolicyAdd -> policyValidation "Validiert Eingaben"
            housePolicyUpdate -> policyValidation "Validiert Änderungen"
            
            housePolicyInquiry -> policyDB "Ruft ab"
            housePolicyAdd -> policyDB "Speichert"
            housePolicyUpdate -> policyDB "Aktualisiert"
            
            endowmentPolicyInquiry -> policyValidation "Validiert Anfragen"
            endowmentPolicyAdd -> policyValidation "Validiert Eingaben"
            endowmentPolicyUpdate -> policyValidation "Validiert Änderungen"
            
            endowmentPolicyInquiry -> policyDB "Ruft ab"
            endowmentPolicyAdd -> policyDB "Speichert"
            endowmentPolicyUpdate -> policyDB "Aktualisiert"
            
            commercialPolicyInquiry -> policyValidation "Validiert Anfragen"
            commercialPolicyAdd -> policyValidation "Validiert Eingaben"
            commercialPolicyUpdate -> policyValidation "Validiert Änderungen"
            
            commercialPolicyInquiry -> policyDB "Ruft ab"
            commercialPolicyAdd -> policyDB "Speichert"
            commercialPolicyUpdate -> policyDB "Aktualisiert"
            
            // Beziehungen: CICS komponenten -> DB2
            customerDB -> customerTable "Liest und schreibt"
            policyDB -> policyTable "Liest und schreibt"
            
            // Beziehungen: Batch Jobs
            policyBatch -> policyTable "Verarbeitet"
            reportBatch -> customerTable "Liest"
            reportBatch -> policyTable "Liest"
            reportBatch -> claimTable "Liest"
            maintenanceBatch -> configFiles "Pflegt"
            
            // Beziehungen: REXX Scripts
            customerScript -> customerTable "Verarbeitet"
            maintenanceScript -> configFiles "Pflegt"
            
            // TSQ-Beziehung (entfernt, da Parent-Child nicht zulässig)
            // temporaryStorage -> cicsRegion "Speichert temporäre Daten für"
            
            // Beziehungen: Reporting
            reporting -> printSystem "Sendet Berichte an"
        }
        
        views {
            systemContext genAppSystem "SystemContext" {
                include *
                autoLayout
            }
            
            container genAppSystem "Containers" {
                include *
                autoLayout
            }
            
            component cicsTerminal "TerminalComponents" {
                include *
                autoLayout
            }
            
            component cicsRegion "CICSRegionComponents" {
                include *
                autoLayout
            }
            
            component db2Database "DatabaseComponents" {
                include *
                autoLayout
            }
            
            component batchSystem "BatchComponents" {
                include *
                autoLayout
            }
            
            styles {
                element "Person" {
                    shape Person
                    background #08427B
                    color #ffffff
                }
                element "Software System" {
                    background #1168BD
                    color #ffffff
                }
                element "Container" {
                    background #438DD5
                    color #ffffff
                }
                element "Component" {
                    background #85BBF0
                    color #000000
                }
                element "Database" {
                    shape Cylinder
                    background #438DD5
                    color #ffffff
                }
                element "External System" {
                    background #999999
                    color #ffffff
                }
            }
            
            themes default
        }
    }
