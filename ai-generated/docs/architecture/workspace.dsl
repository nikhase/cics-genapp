workspace "CICS GenApp - General Insurance Application" "C4 model for IBM CICS GenApp demonstration application" {

    !identifiers hierarchical

    model {
        # People
        insuranceAgent = person "Insurance Agent" "Insurance company employee who manages customer and policy information" "User"
        systemAdmin = person "System Administrator" "Manages and monitors the GenApp application" "Administrator"

        # External Systems
        db2 = softwareSystem "IBM Db2" "Relational database storing customer and policy data with referential integrity" "External System"
        couplingFacility = softwareSystem "z/OS Coupling Facility" "Provides named counter server for generating unique customer IDs" "External System"

        # GenApp System
        genApp = softwareSystem "GenApp" "General insurance application for managing customers and policies (Motor, Endowment, House, Commercial)" {

            # Containers (CICS Region)
            cicsRegion = container "CICS Transaction Server" "Hosts COBOL programs, manages transactions, and provides BMS map support" "IBM CICS TS V4.1+" "CICS Container" {

                # Presentation Layer Components
                customerUI = component "Customer UI Program" "3270 screen handling for customer operations (add, inquire, update)" "COBOL (lgtestc1.cbl)" "Presentation"
                motorPolicyUI = component "Motor Policy UI Program" "3270 screen handling for motor insurance policies" "COBOL (lgtestp1.cbl)" "Presentation"
                endowmentPolicyUI = component "Endowment Policy UI Program" "3270 screen handling for endowment insurance policies" "COBOL (lgtestp2.cbl)" "Presentation"
                housePolicyUI = component "House Policy UI Program" "3270 screen handling for house insurance policies" "COBOL (lgtestp3.cbl)" "Presentation"
                commercialPolicyUI = component "Commercial Policy UI Program" "3270 screen handling for commercial property insurance" "COBOL (lgtestp4.cbl)" "Presentation"

                # Business Logic Layer Components
                addCustomerBL = component "Add Customer Business Logic" "Validates customer data and orchestrates add operation" "COBOL (lgacus01.cbl)" "Business Logic"
                inquireCustomerBL = component "Inquire Customer Business Logic" "Retrieves customer information" "COBOL (lgicus01.cbl)" "Business Logic"
                updateCustomerBL = component "Update Customer Business Logic" "Validates and orchestrates customer updates" "COBOL (lgucus01.cbl)" "Business Logic"

                addPolicyBL = component "Add Policy Business Logic" "Validates policy data for all policy types" "COBOL (lgapol01.cbl)" "Business Logic"
                inquirePolicyBL = component "Inquire Policy Business Logic" "Retrieves policy details with type-specific data" "COBOL (lgipol01.cbl)" "Business Logic"
                updatePolicyBL = component "Update Policy Business Logic" "Validates and orchestrates policy updates" "COBOL (lgupol01.cbl)" "Business Logic"
                deletePolicyBL = component "Delete Policy Business Logic" "Orchestrates policy deletion with cleanup" "COBOL (lgdpol01.cbl)" "Business Logic"

                # Data Layer - Db2 Components
                addCustomerDb2 = component "Add Customer Db2 Program" "Inserts customer records to Db2, generates customer numbers" "COBOL (lgacdb01.cbl)" "Data Access"
                inquireCustomerDb2 = component "Inquire Customer Db2 Program" "Queries customer table in Db2" "COBOL (lgicdb01.cbl)" "Data Access"
                updateCustomerDb2 = component "Update Customer Db2 Program" "Updates customer records in Db2" "COBOL (lgucdb01.cbl)" "Data Access"

                addPolicyDb2 = component "Add Policy Db2 Program" "Inserts policy records across multiple Db2 tables" "COBOL (lgapdb01.cbl)" "Data Access"
                inquirePolicyDb2 = component "Inquire Policy Db2 Program" "Complex joins across policy tables (1030 LOC)" "COBOL (lgipdb01.cbl)" "Data Access"
                updatePolicyDb2 = component "Update Policy Db2 Program" "Updates policy records with validation" "COBOL (lgupdb01.cbl)" "Data Access"
                deletePolicyDb2 = component "Delete Policy Db2 Program" "Deletes policy records from Db2" "COBOL (lgdpdb01.cbl)" "Data Access"

                # Data Layer - VSAM Components
                addCustomerVsam = component "Add Customer VSAM Program" "Writes customer records to KSDSCUST file" "COBOL (lgacvs01.cbl)" "Data Access"
                inquireCustomerVsam = component "Inquire Customer VSAM Program" "Reads customer records from VSAM" "COBOL (lgicvs01.cbl)" "Data Access"
                updateCustomerVsam = component "Update Customer VSAM Program" "Updates customer records in VSAM" "COBOL (lgucvs01.cbl)" "Data Access"

                addPolicyVsam = component "Add Policy VSAM Program" "Writes policy records to KSDSPOLY file" "COBOL (lgapvs01.cbl)" "Data Access"
                inquirePolicyVsam = component "Inquire Policy VSAM Program" "Reads policy records from VSAM" "COBOL (lgipvs01.cbl)" "Data Access"
                updatePolicyVsam = component "Update Policy VSAM Program" "Updates policy records in VSAM" "COBOL (lgupvs01.cbl)" "Data Access"
                deletePolicyVsam = component "Delete Policy VSAM Program" "Deletes policy records from VSAM" "COBOL (lgdpvs01.cbl)" "Data Access"

                # Utility Components
                errorLogger = component "Error Logger" "Writes error messages to GENAERRS TSQ" "COBOL (lgstsq.cbl)" "Utility"
                systemSetup = component "System Setup" "Initializes named counters and temporary storage queues" "COBOL (lgsetup.cbl)" "Utility"
                webServicesAdapter = component "Web Services Adapter" "Exposes counter statistics via web services" "COBOL (lgwebst5.cbl)" "Utility"

                # BMS Maps
                bmsMapset = component "BMS Mapset" "Screen layouts for customer and 4 policy types" "BMS (ssmap.bms)" "Presentation"
            }

            # VSAM File Container
            vsamFiles = container "VSAM Files" "Key-sequenced data sets for customer and policy data" "VSAM KSDS" "Storage" {
                ksdscust = component "KSDSCUST File" "Customer records (225 bytes, key: customer number)" "VSAM KSDS" "Data Store"
                ksdspoly = component "KSDSPOLY File" "Policy records (key: policy type + customer num + policy num)" "VSAM KSDS" "Data Store"
            }

            # Temporary Storage
            tsQueues = container "Temporary Storage Queues" "Ephemeral storage for control data and errors" "CICS TSQ" "Storage" {
                genacntl = component "GENACNTL Queue" "Stores control data like high customer number" "TSQ" "Cache"
                genaerrs = component "GENAERRS Queue" "Stores error messages for debugging" "TSQ" "Log"
            }
        }

        # Relationships - Users to System
        insuranceAgent -> genApp "Manages customers and policies using" "3270 terminal"
        systemAdmin -> genApp "Monitors and maintains" "CICS commands"

        # Relationships - System to External Systems
        genApp -> db2 "Reads from and writes to" "EXEC SQL"
        genApp -> couplingFacility "Generates unique customer IDs using" "EXEC CICS GET COUNTER"

        # Relationships - Users to Containers
        insuranceAgent -> genApp.cicsRegion "Interacts with" "3270/TN3270"
        systemAdmin -> genApp.cicsRegion "Manages" "CICS transactions"

        # Relationships - Containers
        genApp.cicsRegion -> db2 "Reads/writes customer and policy data" "EXEC SQL / Db2 connection"
        genApp.cicsRegion -> couplingFacility "Requests counter increments" "EXEC CICS GET COUNTER"
        genApp.cicsRegion -> genApp.vsamFiles "Reads/writes records (dual write pattern)" "EXEC CICS READ/WRITE FILE"
        genApp.cicsRegion -> genApp.tsQueues "Writes control data and errors" "EXEC CICS WRITEQ TS"

        # Component Relationships - Customer Flow (Presentation -> Business -> Data)
        genApp.cicsRegion.customerUI -> genApp.cicsRegion.addCustomerBL "Initiates customer add" "EXEC CICS LINK COMMAREA(32500)"
        genApp.cicsRegion.customerUI -> genApp.cicsRegion.inquireCustomerBL "Initiates customer inquiry" "EXEC CICS LINK"
        genApp.cicsRegion.customerUI -> genApp.cicsRegion.updateCustomerBL "Initiates customer update" "EXEC CICS LINK"

        genApp.cicsRegion.addCustomerBL -> genApp.cicsRegion.addCustomerDb2 "Delegates Db2 operations" "EXEC CICS LINK"
        genApp.cicsRegion.addCustomerDb2 -> db2 "Inserts customer record" "EXEC SQL INSERT"
        genApp.cicsRegion.addCustomerDb2 -> genApp.cicsRegion.addCustomerVsam "Triggers VSAM write (dual write)" "EXEC CICS LINK"
        genApp.cicsRegion.addCustomerVsam -> genApp.vsamFiles.ksdscust "Writes customer record" "EXEC CICS WRITE FILE"

        genApp.cicsRegion.inquireCustomerBL -> genApp.cicsRegion.inquireCustomerDb2 "Delegates Db2 query" "EXEC CICS LINK"
        genApp.cicsRegion.inquireCustomerDb2 -> db2 "Selects customer data" "EXEC SQL SELECT"

        genApp.cicsRegion.updateCustomerBL -> genApp.cicsRegion.updateCustomerDb2 "Delegates Db2 update" "EXEC CICS LINK"
        genApp.cicsRegion.updateCustomerDb2 -> db2 "Updates customer record" "EXEC SQL UPDATE"
        genApp.cicsRegion.updateCustomerDb2 -> genApp.cicsRegion.updateCustomerVsam "Triggers VSAM update (dual write)" "EXEC CICS LINK"
        genApp.cicsRegion.updateCustomerVsam -> genApp.vsamFiles.ksdscust "Updates customer record" "EXEC CICS REWRITE FILE"

        # Component Relationships - Policy Flow (Motor example)
        genApp.cicsRegion.motorPolicyUI -> genApp.cicsRegion.addPolicyBL "Initiates policy add" "EXEC CICS LINK"
        genApp.cicsRegion.motorPolicyUI -> genApp.cicsRegion.inquirePolicyBL "Initiates policy inquiry" "EXEC CICS LINK"
        genApp.cicsRegion.motorPolicyUI -> genApp.cicsRegion.updatePolicyBL "Initiates policy update" "EXEC CICS LINK"
        genApp.cicsRegion.motorPolicyUI -> genApp.cicsRegion.deletePolicyBL "Initiates policy delete" "EXEC CICS LINK"

        genApp.cicsRegion.addPolicyBL -> genApp.cicsRegion.addPolicyDb2 "Delegates Db2 operations" "EXEC CICS LINK"
        genApp.cicsRegion.addPolicyDb2 -> db2 "Inserts policy and type-specific records" "EXEC SQL INSERT"

        genApp.cicsRegion.inquirePolicyBL -> genApp.cicsRegion.inquirePolicyDb2 "Delegates Db2 query" "EXEC CICS LINK"
        genApp.cicsRegion.inquirePolicyDb2 -> db2 "Joins policy with motor/endowment/house/commercial tables" "EXEC SQL DECLARE CURSOR"

        genApp.cicsRegion.updatePolicyBL -> genApp.cicsRegion.updatePolicyDb2 "Delegates Db2 update" "EXEC CICS LINK"
        genApp.cicsRegion.updatePolicyDb2 -> db2 "Updates policy records" "EXEC SQL UPDATE"
        genApp.cicsRegion.updatePolicyDb2 -> genApp.cicsRegion.updatePolicyVsam "Triggers VSAM update (dual write)" "EXEC CICS LINK"
        genApp.cicsRegion.updatePolicyVsam -> genApp.vsamFiles.ksdspoly "Updates policy record" "EXEC CICS REWRITE FILE"

        genApp.cicsRegion.deletePolicyBL -> genApp.cicsRegion.deletePolicyDb2 "Delegates Db2 delete" "EXEC CICS LINK"
        genApp.cicsRegion.deletePolicyDb2 -> db2 "Deletes policy records" "EXEC SQL DELETE"
        genApp.cicsRegion.deletePolicyDb2 -> genApp.cicsRegion.deletePolicyVsam "Triggers VSAM delete (dual write)" "EXEC CICS LINK"
        genApp.cicsRegion.deletePolicyVsam -> genApp.vsamFiles.ksdspoly "Deletes policy record" "EXEC CICS DELETE FILE"

        # Component Relationships - Utility Flow
        genApp.cicsRegion.addCustomerBL -> genApp.cicsRegion.errorLogger "Logs errors" "PERFORM WRITE-ERROR-MESSAGE"
        genApp.cicsRegion.addPolicyBL -> genApp.cicsRegion.errorLogger "Logs errors" "PERFORM"
        genApp.cicsRegion.errorLogger -> genApp.tsQueues.genaerrs "Writes error messages" "EXEC CICS WRITEQ TS"

        genApp.cicsRegion.addCustomerDb2 -> couplingFacility "Generates customer number" "EXEC CICS GET COUNTER"
        genApp.cicsRegion.addCustomerDb2 -> genApp.tsQueues.genacntl "Updates high customer number" "EXEC CICS WRITEQ TS"

        systemAdmin -> genApp.cicsRegion.systemSetup "Initializes system" "Transaction LGSE"
        genApp.cicsRegion.systemSetup -> couplingFacility "Creates 19 named counters" "EXEC CICS DEFINE COUNTER"
        genApp.cicsRegion.systemSetup -> genApp.tsQueues.genacntl "Initializes control queue" "EXEC CICS WRITEQ TS"

        # BMS relationships
        genApp.cicsRegion.customerUI -> genApp.cicsRegion.bmsMapset "Sends/receives screens" "EXEC CICS SEND/RECEIVE MAP"
        genApp.cicsRegion.motorPolicyUI -> genApp.cicsRegion.bmsMapset "Sends/receives screens" "EXEC CICS SEND/RECEIVE MAP"
        genApp.cicsRegion.endowmentPolicyUI -> genApp.cicsRegion.bmsMapset "Sends/receives screens" "EXEC CICS SEND/RECEIVE MAP"
        genApp.cicsRegion.housePolicyUI -> genApp.cicsRegion.bmsMapset "Sends/receives screens" "EXEC CICS SEND/RECEIVE MAP"
        genApp.cicsRegion.commercialPolicyUI -> genApp.cicsRegion.bmsMapset "Sends/receives screens" "EXEC CICS SEND/RECEIVE MAP"

        # Other policy UI relationships (to keep it complete but concise)
        genApp.cicsRegion.endowmentPolicyUI -> genApp.cicsRegion.addPolicyBL "Initiates policy operations" "EXEC CICS LINK"
        genApp.cicsRegion.endowmentPolicyUI -> genApp.cicsRegion.inquirePolicyBL "Initiates policy operations" "EXEC CICS LINK"
        genApp.cicsRegion.housePolicyUI -> genApp.cicsRegion.addPolicyBL "Initiates policy operations" "EXEC CICS LINK"
        genApp.cicsRegion.housePolicyUI -> genApp.cicsRegion.inquirePolicyBL "Initiates policy operations" "EXEC CICS LINK"
        genApp.cicsRegion.commercialPolicyUI -> genApp.cicsRegion.addPolicyBL "Initiates policy operations" "EXEC CICS LINK"
        genApp.cicsRegion.commercialPolicyUI -> genApp.cicsRegion.inquirePolicyBL "Initiates policy operations" "EXEC CICS LINK"

        # Deployment Model
        production = deploymentEnvironment "Production" {
            mainframe = deploymentNode "IBM z/OS Mainframe" "" "IBM z/OS" {
                cicsNode = deploymentNode "CICS Region" "" "IBM CICS TS V5.6" {
                    cicsInstance = containerInstance genApp.cicsRegion
                    tsqInstance = containerInstance genApp.tsQueues
                }

                vsamNode = deploymentNode "VSAM Storage" "" "VSAM" {
                    vsamInstance = containerInstance genApp.vsamFiles
                }

                db2Node = deploymentNode "Db2 Subsystem" "" "IBM Db2 for z/OS" {
                    db2Instance = softwareSystemInstance db2
                }

                cfNode = deploymentNode "Coupling Facility" "" "z/OS Parallel Sysplex" {
                    cfInstance = softwareSystemInstance couplingFacility
                }
            }

            terminal = deploymentNode "Terminal" "" "3270 Terminal / TN3270 Emulator" {
                terminalInstance = infrastructureNode "3270 Display" "" "Terminal"
            }

            terminal.terminalInstance -> mainframe.cicsNode.cicsInstance "Sends transactions" "TN3270 / 3270 data stream"
        }
    }

    views {
        # System Context View
        systemContext genApp "SystemContext" {
            include *
            autoLayout lr
            description "System context diagram for GenApp showing external dependencies"
        }

        # Container View
        container genApp "Containers" {
            include *
            autoLayout lr
            description "Container view showing CICS region, VSAM files, and external systems"
        }

        # Component View - CICS Region (All Components)
        component genApp.cicsRegion "CICSComponents" {
            include *
            autoLayout tb
            description "All components within CICS Transaction Server organized by layer"
        }

        # Component View - Customer Operations Focus
        component genApp.cicsRegion "CustomerOperations" {
            include insuranceAgent
            include genApp.cicsRegion.customerUI
            include genApp.cicsRegion.addCustomerBL genApp.cicsRegion.inquireCustomerBL genApp.cicsRegion.updateCustomerBL
            include genApp.cicsRegion.addCustomerDb2 genApp.cicsRegion.inquireCustomerDb2 genApp.cicsRegion.updateCustomerDb2
            include genApp.cicsRegion.addCustomerVsam genApp.cicsRegion.updateCustomerVsam
            include genApp.vsamFiles.ksdscust
            include db2
            include genApp.cicsRegion.errorLogger genApp.tsQueues.genaerrs
            include genApp.cicsRegion.bmsMapset
            include couplingFacility
            include genApp.tsQueues.genacntl
            autoLayout tb
            description "Component view focused on customer operations flow"
        }

        # Component View - Policy Operations Focus
        component genApp.cicsRegion "PolicyOperations" {
            include insuranceAgent
            include genApp.cicsRegion.motorPolicyUI genApp.cicsRegion.endowmentPolicyUI genApp.cicsRegion.housePolicyUI genApp.cicsRegion.commercialPolicyUI
            include genApp.cicsRegion.addPolicyBL genApp.cicsRegion.inquirePolicyBL genApp.cicsRegion.updatePolicyBL genApp.cicsRegion.deletePolicyBL
            include genApp.cicsRegion.addPolicyDb2 genApp.cicsRegion.inquirePolicyDb2 genApp.cicsRegion.updatePolicyDb2 genApp.cicsRegion.deletePolicyDb2
            include genApp.cicsRegion.updatePolicyVsam genApp.cicsRegion.deletePolicyVsam
            include genApp.vsamFiles.ksdspoly
            include db2
            include genApp.cicsRegion.errorLogger genApp.tsQueues.genaerrs
            include genApp.cicsRegion.bmsMapset
            autoLayout tb
            description "Component view focused on policy operations flow (all 4 types)"
        }

        # Component View - Data Layer Architecture
        component genApp.cicsRegion "DataLayerArchitecture" {
            include genApp.cicsRegion.addCustomerBL genApp.cicsRegion.updateCustomerBL genApp.cicsRegion.addPolicyBL genApp.cicsRegion.updatePolicyBL genApp.cicsRegion.deletePolicyBL
            include genApp.cicsRegion.addCustomerDb2 genApp.cicsRegion.updateCustomerDb2 genApp.cicsRegion.addPolicyDb2 genApp.cicsRegion.updatePolicyDb2 genApp.cicsRegion.deletePolicyDb2
            include genApp.cicsRegion.addCustomerVsam genApp.cicsRegion.updateCustomerVsam genApp.cicsRegion.updatePolicyVsam genApp.cicsRegion.deletePolicyVsam
            include genApp.vsamFiles.ksdscust genApp.vsamFiles.ksdspoly
            include db2
            autoLayout lr
            description "Dual-write pattern: Db2 and VSAM data persistence (acknowledged technical debt)"
        }

        # Deployment View
        deployment genApp production "ProductionDeployment" {
            include *
            autoLayout lr
            description "Production deployment on IBM z/OS mainframe"
        }

        # Dynamic View - Add Customer Transaction (Internal components only)
        dynamic genApp.cicsRegion "AddCustomerTransaction" "Add Customer transaction flow (SSC1 -> Option 2)" {
            genApp.cicsRegion.customerUI -> genApp.cicsRegion.addCustomerBL "EXEC CICS LINK PROGRAM('LGACUS01')"
            genApp.cicsRegion.addCustomerBL -> genApp.cicsRegion.addCustomerDb2 "EXEC CICS LINK PROGRAM('LGACDB01')"
            genApp.cicsRegion.addCustomerDb2 -> genApp.cicsRegion.addCustomerVsam "EXEC CICS LINK PROGRAM('LGACVS01')"
            genApp.cicsRegion.addCustomerBL -> genApp.cicsRegion.errorLogger "Logs errors on failure"
            autoLayout lr
            description "Sequence of calls for adding a new customer (internal CICS flow)"
        }

        # Dynamic View - Inquire Policy Transaction (Internal components only)
        dynamic genApp.cicsRegion "InquirePolicyTransaction" "Inquire Motor Policy transaction flow (SSP1 -> Option 1)" {
            genApp.cicsRegion.motorPolicyUI -> genApp.cicsRegion.inquirePolicyBL "EXEC CICS LINK PROGRAM('LGIPOL01')"
            genApp.cicsRegion.inquirePolicyBL -> genApp.cicsRegion.inquirePolicyDb2 "EXEC CICS LINK PROGRAM('LGIPDB01')"
            genApp.cicsRegion.inquirePolicyBL -> genApp.cicsRegion.motorPolicyUI "Returns policy data in COMMAREA"
            autoLayout lr
            description "Sequence of calls for inquiring on a motor policy (internal CICS flow)"
        }

        # Styles
        styles {
            element "Person" {
                shape person
                background #08427b
                color #ffffff
            }
            element "User" {
                background #08427b
            }
            element "Administrator" {
                background #ff6b35
            }
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "External System" {
                background #999999
                color #ffffff
            }
            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "CICS Container" {
                background #2a9d8f
                color #ffffff
            }
            element "Storage" {
                background #e76f51
                color #ffffff
            }
            element "Component" {
                background #85bbf0
                color #000000
            }
            element "Presentation" {
                background #f4a261
                color #000000
            }
            element "Business Logic" {
                background #2a9d8f
                color #ffffff
            }
            element "Data Access" {
                background #264653
                color #ffffff
            }
            element "Utility" {
                background #e9c46a
                color #000000
            }
            element "Data Store" {
                shape Cylinder
                background #e76f51
                color #ffffff
            }
            element "Cache" {
                shape Cylinder
                background #ffb703
                color #000000
            }
            element "Log" {
                shape Cylinder
                background #fb8500
                color #ffffff
            }
            relationship "Relationship" {
                thickness 2
                color #707070
                fontSize 24
                dashed false
            }
        }

        # Themes
        theme default
    }

    configuration {
        scope softwaresystem
    }
}
