workspace "CICS GenApp" "Enterprise COBOL application for IBM CICS Transaction Server demonstrating modernization patterns" {

    model {
        # ======================================
        # EXTERNAL SYSTEMS & USERS
        # ======================================
        businessUser = person "Business User" "Insurance agent or customer service representative using 3270 terminal" "External"

        # ======================================
        # MAINFRAME SYSTEMS
        # ======================================
        zos = softwareSystem "z/OS Mainframe" "IBM operating system hosting CICS and applications" "External" {
            db2 = container "Db2 Database" "Primary ACID-compliant data store for customers and policies" "Database - Db2 for z/OS" {
                customerTable = component "CUSTOMER Table" "Stores customer master records" "Db2 Table"
                policyTable = component "POLICY Table" "Policy header records with type and customer references" "Db2 Table"
                motorTable = component "MOTOR Table" "Motor insurance policy details" "Db2 Table"
                endowmentTable = component "ENDOWMENT Table" "Endowment insurance policy details" "Db2 Table"
                houseTable = component "HOUSE Table" "House insurance policy details" "Db2 Table"
                commercialTable = component "COMMERCIAL Table" "Commercial property insurance policy details" "Db2 Table"
            }

            vsam = container "VSAM Files" "Secondary file-based storage for shadow records and legacy compatibility" "File System - VSAM" {
                ksdscust = component "KSDSCUST" "Customer KSDS file (key=first 10 chars)" "VSAM KSDS File"
                ksdspoly = component "KSDSPOLY" "Policy KSDS file (key=Type+CustomerID+PolicyID)" "VSAM KSDS File"
            }

            # ======================================
            # CICS GENAPP APPLICATION CONTAINER
            # ======================================
            cicApp = container "CICS GenApp Application" "Enterprise COBOL application with transaction processing, business logic, and data layers" "CICS Component" {
                # ======================================
                # PRESENTATION LAYER (3270 UI)
                # ======================================
                bmsMap = component "ssmap.bms" "BMS screen map definitions for 3270 terminal" "BMS Screen Map"
                ssc1Prog = component "LGTESTC1" "Customer management transaction entry point (SSC1)" "COBOL Program"
                ssp1Prog = component "LGTESTP1" "Motor insurance policy transaction (SSP1)" "COBOL Program"
                ssp2Prog = component "LGTESTP2" "Endowment insurance policy transaction (SSP2)" "COBOL Program"
                ssp3Prog = component "LGTESTP3" "House insurance policy transaction (SSP3)" "COBOL Program"
                ssp4Prog = component "LGTESTP4" "Commercial property policy transaction (SSP4)" "COBOL Program"

                # ======================================
                # BUSINESS LOGIC LAYER
                # ======================================
                cusAddBiz = component "LGACUS01" "Customer addition business logic" "COBOL Program"
                cusInqBiz = component "LGICUS01" "Customer inquiry business logic" "COBOL Program"
                cusUpdBiz = component "LGUCUS01" "Customer update business logic" "COBOL Program"
                polAddBiz = component "LGAPOL01" "Policy addition business logic and two-phase commit" "COBOL Program"
                polInqBiz = component "LGIPOL01" "Policy inquiry business logic" "COBOL Program"
                polUpdBiz = component "LGUPOL01" "Policy update business logic" "COBOL Program"
                polDelBiz = component "LGDPOL01" "Policy deletion business logic" "COBOL Program"
                setupUtil = component "LGSETUP" "Application initialization and counter setup" "COBOL Utility"
                stsqUtil = component "LGSTSQ" "Temporary storage queue writer for control data" "COBOL Utility"
                statUtil = component "LGASTAT1" "Transaction counter and statistics updater" "COBOL Utility"
                webstUtil = component "LGWEBST5" "Counter to queue data copy utility" "COBOL Utility"

                # ======================================
                # DATA MANAGEMENT LAYER - Db2
                # ======================================
                cusAddDb = component "LGACDB01" "Customer addition to Db2" "COBOL + SQL"
                cusInqDb = component "LGICDB01" "Customer inquiry from Db2" "COBOL + SQL"
                cusUpdDb = component "LGUCDB01" "Customer update in Db2" "COBOL + SQL"
                polAddDb = component "LGAPDB01" "Policy addition to Db2" "COBOL + SQL"
                polInqDb = component "LGIPDB01" "Policy inquiry from Db2" "COBOL + SQL"
                polUpdDb = component "LGUPDB01" "Policy update in Db2" "COBOL + SQL"
                polDelDb = component "LGDPDB01" "Policy deletion from Db2" "COBOL + SQL"

                # ======================================
                # DATA MANAGEMENT LAYER - VSAM
                # ======================================
                cusAddVs = component "LGACVS01" "Customer addition to VSAM (shadow)" "COBOL + CICS File I/O"
                cusInqVs = component "LGICVS01" "Customer inquiry from VSAM" "COBOL + CICS File I/O"
                cusUpdVs = component "LGUCVS01" "Customer update in VSAM" "COBOL + CICS File I/O"
                polAddVs = component "LGAPVS01" "Policy addition to VSAM (shadow)" "COBOL + CICS File I/O"
                polInqVs = component "LGIPVS01" "Policy inquiry from VSAM" "COBOL + CICS File I/O"
                polUpdVs = component "LGUPVS01" "Policy update in VSAM" "COBOL + CICS File I/O"
                polDelVs = component "LGDPVS01" "Policy deletion from VSAM" "COBOL + CICS File I/O"

                # ======================================
                # SHARED DATA STRUCTURES (COPYBOOKS)
                # ======================================
                commareaBook = component "lgcmarea.cpy" "Primary 32KB+ COMMAREA structure for all programs" "COBOL Copybook"
                policyBook = component "lgpolicy.cpy" "Policy structures with REDEFINES for 4 policy types" "COBOL Copybook"
                inputBooks = component "soai*.cpy" "Policy-specific input format definitions" "COBOL Copybook"
                outputBooks = component "soav*.cpy" "Policy-specific output format definitions" "COBOL Copybook"
                lookupBooks = component "pollook*.cpy" "Policy lookup and reference structures" "COBOL Copybook"

                # ======================================
                # OPTIONAL COMPONENTS (Coupling Facility)
                # ======================================
                namedCounter = component "Named Counter Server" "Distributed unique ID generation (pool: GENA)" "CICS Named Counter"
                tsqControl = component "GENACNTL Queue" "Control data and counter range tracking" "Temporary Storage Queue"
                tsqErrors = component "GENAERRS Queue" "Error logging for Db2 and VSAM failures" "Temporary Storage Queue"

                # ======================================
                # LAYER INTERACTIONS (EXEC CICS LINK)
                # ======================================
                # Presentation self-interactions
                ssc1Prog -> ssc1Prog "input validation, COMMAREA setup"
                ssp1Prog -> ssp1Prog "input validation, COMMAREA setup"
                ssp2Prog -> ssp2Prog "input validation, COMMAREA setup"
                ssp3Prog -> ssp3Prog "input validation, COMMAREA setup"
                ssp4Prog -> ssp4Prog "input validation, COMMAREA setup"

                # Presentation -> Business Logic
                ssc1Prog -> cusAddBiz "EXEC CICS LINK"
                ssc1Prog -> cusInqBiz "EXEC CICS LINK"
                ssc1Prog -> cusUpdBiz "EXEC CICS LINK"
                ssp1Prog -> polAddBiz "EXEC CICS LINK"
                ssp1Prog -> polInqBiz "EXEC CICS LINK"
                ssp1Prog -> polUpdBiz "EXEC CICS LINK"
                ssp1Prog -> polDelBiz "EXEC CICS LINK"
                ssp2Prog -> polAddBiz "EXEC CICS LINK"
                ssp2Prog -> polInqBiz "EXEC CICS LINK"
                ssp2Prog -> polUpdBiz "EXEC CICS LINK"
                ssp2Prog -> polDelBiz "EXEC CICS LINK"
                ssp3Prog -> polAddBiz "EXEC CICS LINK"
                ssp3Prog -> polInqBiz "EXEC CICS LINK"
                ssp3Prog -> polUpdBiz "EXEC CICS LINK"
                ssp3Prog -> polDelBiz "EXEC CICS LINK"
                ssp4Prog -> polAddBiz "EXEC CICS LINK"
                ssp4Prog -> polInqBiz "EXEC CICS LINK"
                ssp4Prog -> polUpdBiz "EXEC CICS LINK"
                ssp4Prog -> polDelBiz "EXEC CICS LINK"

                # Business Logic self-interactions and checks
                cusAddBiz -> cusAddBiz "duplicate check, ID generation"
                cusInqBiz -> cusInqBiz "data validation"
                cusUpdBiz -> cusUpdBiz "data validation"
                polAddBiz -> polAddBiz "duplicate check, ID generation, 2PC orchestration"
                polInqBiz -> polInqBiz "data validation"
                polUpdBiz -> polUpdBiz "data validation, 2PC orchestration"
                polDelBiz -> polDelBiz "validation"

                # Business Logic -> Data Layers
                cusAddBiz -> cusAddDb "EXEC CICS LINK"
                cusInqBiz -> cusInqDb "EXEC CICS LINK"
                cusUpdBiz -> cusUpdDb "EXEC CICS LINK"
                polAddBiz -> polAddDb "EXEC CICS LINK (Phase 1)"
                polInqBiz -> polInqDb "EXEC CICS LINK"
                polUpdBiz -> polUpdDb "EXEC CICS LINK (Phase 1)"
                polDelBiz -> polDelDb "EXEC CICS LINK"

                # Business Logic -> VSAM (Phase 2 of 2PC)
                cusAddBiz -> cusAddVs "EXEC CICS LINK (if Db2 ok)"
                cusUpdBiz -> cusUpdVs "EXEC CICS LINK (if Db2 ok)"
                polAddBiz -> polAddVs "EXEC CICS LINK (Phase 2, best-effort)"
                polUpdBiz -> polUpdVs "EXEC CICS LINK (Phase 2, best-effort)"
                polDelBiz -> polDelVs "EXEC CICS LINK"

                # Data Access Layer self-interactions
                cusAddDb -> cusAddDb "SQL preparation"
                cusInqDb -> cusInqDb "SQL preparation"
                cusUpdDb -> cusUpdDb "SQL preparation"
                polAddDb -> polAddDb "SQL preparation"
                polInqDb -> polInqDb "SQL preparation"
                polUpdDb -> polUpdDb "SQL preparation"
                polDelDb -> polDelDb "SQL preparation"

                cusAddVs -> cusAddVs "record preparation"
                cusInqVs -> cusInqVs "record preparation"
                cusUpdVs -> cusUpdVs "record preparation"
                polAddVs -> polAddVs "record preparation"
                polInqVs -> polInqVs "record preparation"
                polUpdVs -> polUpdVs "record preparation"
                polDelVs -> polDelVs "record preparation"

                # Data Layers -> External Storage
                cusAddDb -> customerTable "EXEC SQL INSERT"
                cusInqDb -> customerTable "EXEC SQL SELECT"
                cusUpdDb -> customerTable "EXEC SQL UPDATE"
                polAddDb -> policyTable "EXEC SQL INSERT"
                polInqDb -> policyTable "EXEC SQL SELECT"
                polUpdDb -> policyTable "EXEC SQL UPDATE"
                polDelDb -> policyTable "EXEC SQL DELETE"

                cusAddVs -> ksdscust "EXEC CICS WRITE FILE"
                cusInqVs -> ksdscust "EXEC CICS READ FILE"
                cusUpdVs -> ksdscust "EXEC CICS WRITE FILE"
                polAddVs -> ksdspoly "EXEC CICS WRITE FILE"
                polInqVs -> ksdspoly "EXEC CICS READ FILE"
                polUpdVs -> ksdspoly "EXEC CICS WRITE FILE"
                polDelVs -> ksdspoly "EXEC CICS DELETE FILE"

                # Data Layers -> Shared Structures
                cusAddBiz -> commareaBook "uses"
                cusInqBiz -> commareaBook "uses"
                cusUpdBiz -> commareaBook "uses"
                polAddBiz -> commareaBook "uses"
                polAddBiz -> policyBook "uses"
                polInqBiz -> commareaBook "uses"
                polInqBiz -> policyBook "uses"
                polUpdBiz -> commareaBook "uses"
                polUpdBiz -> policyBook "uses"
                polDelBiz -> commareaBook "uses"
                polDelBiz -> policyBook "uses"

                # Business Logic -> Utilities
                cusAddBiz -> statUtil "calls to update transaction counter"
                cusInqBiz -> statUtil "calls to update transaction counter"
                cusUpdBiz -> statUtil "calls to update transaction counter"
                polAddBiz -> statUtil "calls to update transaction counter"
                polInqBiz -> statUtil "calls to update transaction counter"
                polUpdBiz -> statUtil "calls to update transaction counter"
                polDelBiz -> statUtil "calls to update transaction counter"

                # Utilities interactions
                setupUtil -> namedCounter "initializes"
                statUtil -> tsqControl "updates"
                polAddBiz -> tsqControl "updates"
            }
        }

        # ======================================
        # EXTERNAL RELATIONSHIPS
        # ======================================
        businessUser -> cicApp "Uses 3270 protocol"

        # Policy-specific table relationships
        polAddDb -> motorTable "EXEC SQL INSERT (if type=M)"
        polInqDb -> motorTable "EXEC SQL SELECT (if type=M)"
        polUpdDb -> motorTable "EXEC SQL UPDATE (if type=M)"

        polAddDb -> endowmentTable "EXEC SQL INSERT (if type=E)"
        polInqDb -> endowmentTable "EXEC SQL SELECT (if type=E)"
        polUpdDb -> endowmentTable "EXEC SQL UPDATE (if type=E)"

        polAddDb -> houseTable "EXEC SQL INSERT (if type=H)"
        polInqDb -> houseTable "EXEC SQL SELECT (if type=H)"
        polUpdDb -> houseTable "EXEC SQL UPDATE (if type=H)"

        polAddDb -> commercialTable "EXEC SQL INSERT (if type=C)"
        polInqDb -> commercialTable "EXEC SQL SELECT (if type=C)"
        polUpdDb -> commercialTable "EXEC SQL UPDATE (if type=C)"
    }

    views {
        # ======================================
        # SYSTEM CONTEXT VIEW
        # ======================================
        systemContext "zos" {
            title "System Context: CICS GenApp"
            description "High-level view showing external systems and users"
            include businessUser zos
            autoLayout lr
        }

        # ======================================
        # CONTAINER VIEW (Overview)
        # ======================================
        container "zos" {
            title "Container View: CICS GenApp on z/OS"
            description "All major subsystems within z/OS: Database, VSAM, and CICS application"
            include db2 vsam cicApp
            include businessUser
            autoLayout tb
        }

        # ======================================
        # APPLICATION COMPONENTS
        # ======================================
        component "cicApp" {
            title "Component View: CICS GenApp Application"
            description "All application components: Presentation, Business Logic, Data Access, and Support layers"
            include ssc1Prog ssp1Prog ssp2Prog ssp3Prog ssp4Prog bmsMap
            include cusAddBiz cusInqBiz cusUpdBiz polAddBiz polInqBiz polUpdBiz polDelBiz setupUtil stsqUtil statUtil webstUtil
            include cusAddDb cusInqDb cusUpdDb polAddDb polInqDb polUpdDb polDelDb
            include cusAddVs cusInqVs cusUpdVs polAddVs polInqVs polUpdVs polDelVs
            include commareaBook policyBook inputBooks outputBooks lookupBooks
            include namedCounter tsqControl tsqErrors
            autoLayout tb
        }

        # ======================================
        # DATA LAYER COMPONENTS (Db2)
        # ======================================
        component "db2" {
            title "Component View: Db2 Database"
            description "All Db2 tables for customer and policy data"
            include customerTable policyTable motorTable endowmentTable houseTable commercialTable
            autoLayout tb
        }

        # ======================================
        # DATA LAYER COMPONENTS (VSAM)
        # ======================================
        component "vsam" {
            title "Component View: VSAM Files"
            description "VSAM KSDS files for customer and policy shadow storage"
            include ksdscust ksdspoly
            autoLayout tb
        }

        # ======================================
        # SEQUENCE DIAGRAM (Customer Add Flow)
        # ======================================
        dynamic "cicApp" "CustomerAddFlow" {
            title "Sequence Diagram: Customer Addition Transaction"
            description "Two-phase commit flow: Db2 (primary) then VSAM (shadow)"

            ssc1Prog -> ssc1Prog "1. Validate input, create COMMAREA"
            ssc1Prog -> cusAddBiz "2. EXEC CICS LINK PROGRAM('LGACUS01')"
            cusAddBiz -> cusAddBiz "3. Check duplicates, obtain next customer ID"
            cusAddBiz -> cusAddDb "4. EXEC CICS LINK PROGRAM('LGACDB01')"
            cusAddDb -> customerTable "5. EXEC SQL INSERT INTO CUSTOMER"
            customerTable -> cusAddDb "6. Success SQLCODE=0"
            cusAddDb -> cusAddBiz "7. Return with status OK"
            cusAddBiz -> cusAddBiz "8. Evaluate response"
            cusAddBiz -> cusAddVs "9. EXEC CICS LINK PROGRAM('LGACVS01')"
            cusAddVs -> ksdscust "10. EXEC CICS WRITE FILE('KSDSCUST')"
            ksdscust -> cusAddVs "11. Success"
            cusAddVs -> cusAddBiz "12. Return status"
            cusAddBiz -> statUtil "13. Update transaction counter"
            cusAddBiz -> ssc1Prog "14. Return control"
            ssc1Prog -> ssc1Prog "15. Format response screen"

            autoLayout tb
        }

        # ======================================
        # STYLES & COLORS
        # ======================================
        styles {
            element "Software System" {
                background #1473BA
                color #ffffff
            }
            element "External" {
                background #999999
                color #ffffff
            }
            element "Person" {
                background #08A542
                color #ffffff
                fontSize 22
                shape Person
            }
            element "Database" {
                shape Cylinder
                background #FF6B35
                color #ffffff
            }
            element "File System" {
                shape Folder
                background #FF9F1C
                color #ffffff
            }
            element "COBOL Program" {
                background #0066CC
                color #ffffff
                icon https://www.plantuml.com/img/favicon.png
            }
            element "COBOL Copybook" {
                background #6600CC
                color #ffffff
                shape Box
            }
            element "COBOL Utility" {
                background #003D82
                color #ffffff
            }
            element "COBOL + SQL" {
                background #0052A3
                color #ffffff
            }
            element "COBOL + CICS File I/O" {
                background #004C7A
                color #ffffff
            }
            element "BMS Screen Map" {
                background #339933
                color #ffffff
            }
            element "CICS Component" {
                background #1F7F7F
                color #ffffff
            }
            element "Coupling Facility" {
                background #AA5500
                color #ffffff
            }
            element "CICS Named Counter" {
                background #885500
                color #ffffff
            }
            element "Temporary Storage Queue" {
                background #775500
                color #ffffff
            }
            element "Db2 Table" {
                background #DD4444
                color #ffffff
            }
            element "VSAM KSDS File" {
                background #EE9944
                color #ffffff
            }
            relationship "uses" {
                routing Direct
            }
        }
    }
}
