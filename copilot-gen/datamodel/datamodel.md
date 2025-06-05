# GenApp Datenmodell

Das folgende Diagramm zeigt das Datenmodell der GenApp-Anwendung basierend auf der Analyse der COBOL-Programme und Datenstrukturen.

## ER-Diagramm

```mermaid
erDiagram
    CUSTOMER {
        int CustomerNumber PK
        string FirstName
        string LastName
        date DateOfBirth
        string HouseName
        string HouseNumber
        string Postcode
        string PhoneMobile
        string PhoneHome
        string EmailAddress
        int NumPolicies
    }

    POLICY {
        int PolicyNumber PK
        int CustomerNumber FK
        char PolicyType "E=Endowment, H=House, M=Motor, B=Commercial"
        date IssueDate
        date ExpiryDate
        timestamp LastChanged
        int BrokerId
        string BrokersReference
        int Payment
    }

    ENDOWMENT {
        int PolicyNumber PK
        char WithProfits "Y/N"
        char Equities "Y/N"
        char ManagedFund "Y/N"
        string FundName
        int Term "in years"
        int SumAssured
        string LifeAssured
        string PaddingData
    }

    HOUSE {
        int PolicyNumber PK
        string PropertyType
        int Bedrooms
        int Value
        string HouseName
        string HouseNumber
        string Postcode
    }

    MOTOR {
        int PolicyNumber PK
        string Make
        string Model
        int Value
        string RegNumber
        string Colour
        int CC "Engine size"
        date Manufactured
        int Premium
        int Accidents
    }

    COMMERCIAL {
        int PolicyNumber PK
        string Address
        string Postcode
        string Latitude
        string Longitude
        string Customer
        string PropertyType
        int FirePeril
        int FirePremium
        int CrimePeril
        int CrimePremium
        int FloodPeril
        int FloodPremium
        int WeatherPeril
        int WeatherPremium
        int Status
        string RejectReason
    }

    CLAIM {
        int ClaimNumber PK
        int PolicyNumber FK
        date ClaimDate
        int Paid
        int Value
        string Cause
        string Observations
    }

    BROKER {
        int BrokerId PK
        string BrokerName
        string Address
        string Phone
        string Email
    }

    CUSTOMER ||--o{ POLICY : "has"
    POLICY ||--o| ENDOWMENT : "can be"
    POLICY ||--o| HOUSE : "can be"
    POLICY ||--o| MOTOR : "can be"
    POLICY ||--o| COMMERCIAL : "can be"
    POLICY ||--o{ CLAIM : "has"
    BROKER ||--o{ POLICY : "manages"
```

## Tabellenbeschreibungen

### CUSTOMER
Eine Tabelle für alle Kundeninformationen der Versicherungsgesellschaft.

### POLICY
Die zentrale Policentabelle, die alle Arten von Versicherungspolicen verwaltet und eine 1:1-Beziehung zu den speziellen Policentabellen (ENDOWMENT, HOUSE, MOTOR, COMMERCIAL) hat. Jede Police gehört zu genau einem Kunden.

### ENDOWMENT
Enthält spezifische Daten für Kapitallebensversicherungspolicen.

### HOUSE
Enthält spezifische Daten für Wohngebäudeversicherungspolicen.

### MOTOR
Enthält spezifische Daten für Kfz-Versicherungspolicen.

### COMMERCIAL
Enthält spezifische Daten für Gewerbeversicherungspolicen, einschließlich verschiedener Risiken und Prämien.

### CLAIM
Speichert Informationen zu Schadensfällen, die mit einer bestimmten Police verbunden sind.

### BROKER
Informationen über die Versicherungsmakler, die Policen vermitteln.

## Datentypen und Beziehungen

- Ein **Kunde** (CUSTOMER) kann mehrere **Policen** haben.
- Jede **Police** (POLICY) gehört genau zu einem **Kunden**.
- Eine **Police** ist einem der vier Typen zugeordnet: **Kapitallebensversicherung** (ENDOWMENT), **Wohngebäudeversicherung** (HOUSE), **Kfz-Versicherung** (MOTOR) oder **Gewerbeversicherung** (COMMERCIAL).
- Zu einer **Police** können mehrere **Schadenfälle** (CLAIM) erfasst werden.
- Ein **Makler** (BROKER) kann mehrere **Policen** betreuen.
