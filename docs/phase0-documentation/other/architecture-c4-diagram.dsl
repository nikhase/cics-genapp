workspace "CICS GenApp" "Enterprise COBOL application for IBM CICS Transaction Server demonstrating modernization patterns" {

    model {
        # ======================================
        # EXTERNAL SYSTEMS & USERS
        # ======================================
        insuranceAgent = person "Insurance Agent" "Creates policies, manages customer records via 3270 terminal" {
            tags "Person,External"
        }
        customerService = person "Customer Service Rep" "Inquires and updates policies and customer info" {
            tags "Person,External"
        }
        sysAdmin = person "System Administrator" "Maintains CICS region, monitors transactions, manages coupling facility" {
            tags "Person,External"
        }

        # ======================================
        # EXTERNAL SYSTEMS
        # ======================================
        cicsMon = softwareSystem "CICS Monitoring" "Real-time monitoring and performance tracking tools" {
            tags "SoftwareSystem,External"
        }
        batchScheduler = softwareSystem "Batch Job Scheduler" "Submits JCL jobs for builds, maintenance, and data loads" {
            tags "SoftwareSystem,External"
        }
        externalPolicy = softwareSystem "External Policy Feed" "Optional integration for policy data from partner systems" {
            tags "SoftwareSystem,External"
        }

        # ======================================
        # MAINFRAME SYSTEMS
        # ======================================
        zos = softwareSystem "z/OS Mainframe" "IBM operating system hosting CICS, Db2, and VSAM subsystems" {
            tags "SoftwareSystem,External,Mainframe"
            db2 = container "Db2 Database" "Primary ACID-compliant data store for customers and policies" {
                tags "Database"
                customerTable = component "CUSTOMER Table" "Stores customer master records" {
                    tags "Db2Table"
                }
                policyTable = component "POLICY Table" "Policy header records with type and customer references" {
                    tags "Db2Table"
                }
                motorTable = component "MOTOR Table" "Motor insurance policy details" {
                    tags "Db2Table"
                }
                endowmentTable = component "ENDOWMENT Table" "Endowment insurance policy details" {
                    tags "Db2Table"
                }
                houseTable = component "HOUSE Table" "House insurance policy details" {
                    tags "Db2Table"
                }
                commercialTable = component "COMMERCIAL Table" "Commercial property insurance policy details" {
                    tags "Db2Table"
                }
            }

            vsam = container "VSAM Files" "Secondary file-based storage for shadow records and legacy compatibility" {
                tags "FileSystem"
                ksdscust = component "KSDSCUST" "Customer KSDS file (key=first 10 chars)" {
                    tags "VsamFile"
                }
                ksdspoly = component "KSDSPOLY" "Policy KSDS file (key=Type+CustomerID+PolicyID)" {
                    tags "VsamFile"
                }
            }

            # ======================================
            # CICS GENAPP APPLICATION CONTAINER
            # ======================================
            cicApp = container "CICS GenApp Application" "3-tier enterprise COBOL application: Presentation layer (3270 UI) → Business Logic layer (rules & 2PC) → Data layer (Db2 + VSAM shadow)" {
                tags "CicsComponent"
                # ======================================
                # PRESENTATION LAYER (3270 Terminal Interface)
                # ======================================
                bmsMap = component "ssmap.bms" "3270 BMS screen map definitions" {
                    tags "BmsMap"
                }
                presentationGroup = component "Transaction Handlers" "5 transaction programs (SSC1 for customers, SSP1-P4 for policy types)" {
                    tags "CobolProgram,Presentation"
                }

                # ======================================
                # BUSINESS LOGIC LAYER
                # ======================================
                customerOpsGroup = component "Customer Operations" "Add/Inquire/Update customer master records with validation" {
                    tags "CobolProgram,BusinessLogic"
                }
                motorPolicyGroup = component "Motor Policy Handler" "Motor insurance policy CRUD operations (Type M)" {
                    tags "CobolProgram,BusinessLogic"
                }
                endowmentPolicyGroup = component "Endowment Policy Handler" "Endowment insurance policy CRUD operations (Type E)" {
                    tags "CobolProgram,BusinessLogic"
                }
                housePolicyGroup = component "House Policy Handler" "House insurance policy CRUD operations (Type H)" {
                    tags "CobolProgram,BusinessLogic"
                }
                commercialPolicyGroup = component "Commercial Policy Handler" "Commercial insurance policy CRUD operations (Type C)" {
                    tags "CobolProgram,BusinessLogic"
                }

                # ======================================
                # DATA MANAGEMENT LAYER - Db2 Access
                # ======================================
                db2AccessGroup = component "Db2 Data Access Layer" "COBOL+SQL programs for customer and policy CRUD with SQL execution" {
                    tags "CobolSql,DataAccess"
                }

                # ======================================
                # DATA MANAGEMENT LAYER - VSAM Access
                # ======================================
                vsamAccessGroup = component "VSAM Data Access Layer" "COBOL+CICS programs for shadow storage with KSDS file I/O" {
                    tags "CobolVsam,DataAccess"
                }

                # ======================================
                # SHARED DATA STRUCTURES (COPYBOOKS)
                # ======================================
                commareaBook = component "lgcmarea.cpy" "Primary 32KB+ COMMAREA shared by all programs" {
                    tags "Copybook"
                }
                policyBook = component "lgpolicy.cpy" "Polymorphic policy structures (REDEFINES for 4 types)" {
                    tags "Copybook"
                }
                copybooks = component "soai*.cpy, soav*.cpy, pollook*.cpy" "Type-specific input/output and lookup structures" {
                    tags "Copybook"
                }

                # ======================================
                # INFRASTRUCTURE & UTILITIES
                # ======================================
                statUtil = component "Statistics & Counters" "LGASTAT1, LGSETUP, LGWEBST5 - transaction counters and monitoring" {
                    tags "Utility,Infrastructure"
                }
                errorQueue = component "Error Logging (GENAERRS)" "Temporary storage queue for transaction error logging" {
                    tags "Tsq,Infrastructure"
                }

                # ======================================
                # OPTIONAL COUPLING FACILITY (Parallel Sysplex)
                # ======================================
                namedCounter = component "Named Counter Server" "Optional distributed unique ID generation (pool: GENA)" {
                    tags "Counter,Infrastructure"
                }
                controlQueue = component "Control Queue (GENACNTL)" "Optional control data and counter range tracking" {
                    tags "Tsq,Infrastructure"
                }

                # ======================================
                # LAYER INTERACTIONS (EXEC CICS LINK)
                # ======================================
                # Presentation -> Business Logic
                presentationGroup -> customerOpsGroup "EXEC CICS LINK (SSC1)"
                presentationGroup -> motorPolicyGroup "EXEC CICS LINK (SSP1)"
                presentationGroup -> endowmentPolicyGroup "EXEC CICS LINK (SSP2)"
                presentationGroup -> housePolicyGroup "EXEC CICS LINK (SSP3)"
                presentationGroup -> commercialPolicyGroup "EXEC CICS LINK (SSP4)"

                # Business Logic -> Data Access Layers
                customerOpsGroup -> db2AccessGroup "EXEC CICS LINK (add/inquire/update)"
                customerOpsGroup -> vsamAccessGroup "EXEC CICS LINK (Phase 2 of 2PC, shadow sync)"
                motorPolicyGroup -> db2AccessGroup "EXEC CICS LINK (add/inquire/update/delete, Motor type)"
                motorPolicyGroup -> vsamAccessGroup "EXEC CICS LINK (Phase 2 of 2PC)"
                endowmentPolicyGroup -> db2AccessGroup "EXEC CICS LINK (add/inquire/update/delete, Endowment type)"
                endowmentPolicyGroup -> vsamAccessGroup "EXEC CICS LINK (Phase 2 of 2PC)"
                housePolicyGroup -> db2AccessGroup "EXEC CICS LINK (add/inquire/update/delete, House type)"
                housePolicyGroup -> vsamAccessGroup "EXEC CICS LINK (Phase 2 of 2PC)"
                commercialPolicyGroup -> db2AccessGroup "EXEC CICS LINK (add/inquire/update/delete, Commercial type)"
                commercialPolicyGroup -> vsamAccessGroup "EXEC CICS LINK (Phase 2 of 2PC)"

                # Data Access Layers -> Storage
                db2AccessGroup -> customerTable "EXEC SQL (INSERT/SELECT/UPDATE)"
                db2AccessGroup -> policyTable "EXEC SQL (INSERT/SELECT/UPDATE/DELETE)"
                db2AccessGroup -> motorTable "EXEC SQL (conditional on Type=M)"
                db2AccessGroup -> endowmentTable "EXEC SQL (conditional on Type=E)"
                db2AccessGroup -> houseTable "EXEC SQL (conditional on Type=H)"
                db2AccessGroup -> commercialTable "EXEC SQL (conditional on Type=C)"
                vsamAccessGroup -> ksdscust "EXEC CICS FILE I/O (customer shadow)"
                vsamAccessGroup -> ksdspoly "EXEC CICS FILE I/O (policy shadow)"

                # All Business Logic uses shared copybooks
                customerOpsGroup -> commareaBook "uses"
                customerOpsGroup -> copybooks "uses"
                motorPolicyGroup -> commareaBook "uses"
                motorPolicyGroup -> policyBook "uses"
                motorPolicyGroup -> copybooks "uses"
                endowmentPolicyGroup -> commareaBook "uses"
                endowmentPolicyGroup -> policyBook "uses"
                endowmentPolicyGroup -> copybooks "uses"
                housePolicyGroup -> commareaBook "uses"
                housePolicyGroup -> policyBook "uses"
                housePolicyGroup -> copybooks "uses"
                commercialPolicyGroup -> commareaBook "uses"
                commercialPolicyGroup -> policyBook "uses"
                commercialPolicyGroup -> copybooks "uses"

                # Infrastructure interactions
                customerOpsGroup -> statUtil "updates counters"
                motorPolicyGroup -> statUtil "updates counters"
                endowmentPolicyGroup -> statUtil "updates counters"
                housePolicyGroup -> statUtil "updates counters"
                commercialPolicyGroup -> statUtil "updates counters"
                customerOpsGroup -> errorQueue "logs failures"
                motorPolicyGroup -> errorQueue "logs failures"
                endowmentPolicyGroup -> errorQueue "logs failures"
                housePolicyGroup -> errorQueue "logs failures"
                commercialPolicyGroup -> errorQueue "logs failures"

                # Optional coupling facility
                statUtil -> namedCounter "uses for distributed IDs"
                statUtil -> controlQueue "updates counter ranges"
            }
        }

        # ======================================
        # EXTERNAL USER RELATIONSHIPS
        # ======================================
        insuranceAgent -> cicApp "Uses 3270 terminal (SSC1, SSP1-P4)"
        customerService -> cicApp "Uses 3270 terminal (SSC1, SSP1-P4)"
        sysAdmin -> cicsMon "Monitors CICS region health and performance"
        sysAdmin -> zos "Manages z/OS system"

        # ======================================
        # EXTERNAL SYSTEM INTEGRATIONS
        # ======================================
        cicsMon -> cicApp "Real-time monitoring via CICS APIs"
        batchScheduler -> zos "Submits JCL build/maintenance jobs"
        externalPolicy -> cicApp "Optional policy data integration (future enhancement)"
    }

    views {
        # ======================================
        # SYSTEM CONTEXT VIEW
        # ======================================
        systemContext "zos" {
            title "System Context: Enterprise Insurance Transaction Processing"
            description "External actors, systems, and integrations for CICS GenApp. Shows insurance agents and customer service reps as primary users, with optional CICS monitoring and batch job scheduling for operations."
            include insuranceAgent customerService sysAdmin cicsMon batchScheduler zos
            autoLayout lr
        }

        # ======================================
        # CONTAINER VIEW (Architectural Layers)
        # ======================================
        container "zos" {
            title "Container View: 3-Tier Architecture on z/OS"
            description "CICS GenApp implements a monolithic 3-tier architecture with dual-storage consistency. Presentation layer handles 3270 terminal UI → Business Logic layer orchestrates rules and 2PC → Data layer manages Db2 (primary) and VSAM (shadow) storage."
            include cicApp db2 vsam
            include insuranceAgent customerService
            autoLayout tb
        }

        # ======================================
        # APPLICATION COMPONENTS (FUNCTIONAL GROUPS)
        # ======================================
        component "cicApp" {
            title "Component View: CICS GenApp Functional Architecture"
            description "Organized by 3 layers and 5 functional domains. Presentation → 5 Transaction Handlers. Business Logic → Customer Operations + 4 Policy Type Handlers (polymorphic). Data Access → Db2 SQL layer + VSAM shadow layer. Infrastructure → Statistics/Monitoring + Error Logging + Optional Coupling Facility."
            include bmsMap presentationGroup
            include customerOpsGroup motorPolicyGroup endowmentPolicyGroup housePolicyGroup commercialPolicyGroup
            include db2AccessGroup vsamAccessGroup
            include commareaBook policyBook copybooks
            include statUtil errorQueue namedCounter controlQueue
            autoLayout tb
        }

        # ======================================
        # DATA LAYER COMPONENTS (Db2)
        # ======================================
        component "db2" {
            title "Component View: Db2 Primary Storage"
            description "6 tables: CUSTOMER (master) + POLICY (header) + 4 type-specific tables. Primary ACID-compliant data store."
            include customerTable policyTable motorTable endowmentTable houseTable commercialTable
            autoLayout tb
        }

        # ======================================
        # DATA LAYER COMPONENTS (VSAM)
        # ======================================
        component "vsam" {
            title "Component View: VSAM Shadow Storage"
            description "2 KSDS files for customer and policy records. Maintains denormalized shadow copies synchronized via 2PC for legacy compatibility and disaster recovery."
            include ksdscust ksdspoly
            autoLayout tb
        }

        # ======================================
        # SEQUENCE DIAGRAM: TWO-PHASE COMMIT PATTERN
        # ======================================
        dynamic "cicApp" "TwoPhaseCommitFlow" {
            title "Transaction Flow: Two-Phase Commit (Db2 Primary → VSAM Shadow)"
            description "Demonstrates the 2PC pattern: Phase 1 commits to Db2 (ACID), Phase 2 asynchronously syncs to VSAM. Errors logged to GENAERRS queue for operator review."

            presentationGroup -> customerOpsGroup "1. EXEC CICS LINK with COMMAREA (SSC1)"
            customerOpsGroup -> db2AccessGroup "2. Phase 1: EXEC CICS LINK to Db2"
            db2AccessGroup -> customerTable "3. EXEC SQL INSERT INTO CUSTOMER"
            customerTable -> db2AccessGroup "4. Success SQLCODE=0"
            db2AccessGroup -> customerOpsGroup "5. Return OK status (Phase 1 committed)"
            customerOpsGroup -> vsamAccessGroup "6. Phase 2: EXEC CICS LINK to VSAM (best-effort shadow sync)"
            vsamAccessGroup -> ksdscust "7. EXEC CICS WRITE FILE('KSDSCUST')"
            ksdscust -> vsamAccessGroup "8. File I/O result (success or error)"
            vsamAccessGroup -> customerOpsGroup "9. Return Phase 2 status"
            customerOpsGroup -> statUtil "10. Update transaction counters"
            customerOpsGroup -> errorQueue "11. Log any Phase 2 failures to GENAERRS"
            customerOpsGroup -> presentationGroup "12. Return to presentation layer with result"

            autoLayout tb
        }

        # ======================================
        # SEQUENCE DIAGRAM: POLYMORPHIC POLICY TYPES
        # ======================================
        dynamic "cicApp" "PolicyPolymorphismFlow" {
            title "Transaction Flow: Polymorphic Policy Handling (Type Dispatch)"
            description "Shows how policy transactions (SSP1-P4) dispatch to type-specific handlers. Each handler routes to appropriate policy type table (Motor/Endowment/House/Commercial) using shared COMMAREA with type-specific REDEFINES structures."

            presentationGroup -> motorPolicyGroup "1. SSP1 EXEC CICS LINK (Motor policy, Type=M)"
            motorPolicyGroup -> db2AccessGroup "2. EXEC CICS LINK to Db2 data access"
            db2AccessGroup -> motorTable "3. EXEC SQL INSERT/SELECT MOTOR table"
            motorTable -> db2AccessGroup "4. Return motor policy records"
            db2AccessGroup -> motorPolicyGroup "5. Return with Db2 results"
            motorPolicyGroup -> vsamAccessGroup "6. EXEC CICS LINK for shadow sync"
            vsamAccessGroup -> ksdspoly "7. EXEC CICS WRITE FILE('KSDSPOLY') with Type=M key"

            presentationGroup -> endowmentPolicyGroup "8. SSP2 EXEC CICS LINK (Endowment policy, Type=E)"
            endowmentPolicyGroup -> db2AccessGroup "9. EXEC CICS LINK to Db2 data access"
            db2AccessGroup -> endowmentTable "10. EXEC SQL INSERT/SELECT ENDOWMENT table"

            presentationGroup -> housePolicyGroup "11. SSP3 EXEC CICS LINK (House policy, Type=H)"
            housePolicyGroup -> db2AccessGroup "12. EXEC CICS LINK to Db2 data access"
            db2AccessGroup -> houseTable "13. EXEC SQL INSERT/SELECT HOUSE table"

            presentationGroup -> commercialPolicyGroup "14. SSP4 EXEC CICS LINK (Commercial policy, Type=C)"
            commercialPolicyGroup -> db2AccessGroup "15. EXEC CICS LINK to Db2 data access"
            db2AccessGroup -> commercialTable "16. EXEC SQL INSERT/SELECT COMMERCIAL table"

            autoLayout tb
        }

        # ======================================
        # STYLES & COLORS
        # ======================================
        styles {
            # External users and systems
            element "External" {
                background "#B0B0B0"
                color "#1B1B1B"
            }

            element "Mainframe" {
                background "#1473BA"
                color "#ffffff"
            }

            # Presentation layer - Green
            element "Presentation" {
                background "#1E8449"
                color "#ffffff"
            }

            element "BmsMap" {
                background "#27AE60"
                color "#ffffff"
            }

            # Business logic layer - Blue shades
            element "BusinessLogic" {
                background "#0066CC"
                color "#ffffff"
            }

            # Data access layer - Dark blues
            element "DataAccess" {
                background "#004C7A"
                color "#ffffff"
            }

            element "CobolSql" {
                background "#0052A3"
                color "#ffffff"
            }

            element "CobolVsam" {
                background "#003D7A"
                color "#ffffff"
            }

            # Data structures - Purple
            element "Copybook" {
                background "#6600CC"
                color "#ffffff"
            }

            # Infrastructure - Browns and greys
            element "Utility" {
                background "#003D82"
                color "#ffffff"
            }

            element "Infrastructure" {
                background "#5D4E37"
                color "#ffffff"
            }

            element "Counter" {
                background "#8B6F47"
                color "#ffffff"
            }

            element "Tsq" {
                background "#704214"
                color "#ffffff"
            }

            # Storage - Orange/Red
            element "Database" {
                shape Cylinder
                background "#FF6B35"
                color "#ffffff"
            }

            element "Db2Table" {
                background "#DD4444"
                color "#ffffff"
            }

            element "FileSystem" {
                shape Folder
                background "#FF9F1C"
                color "#ffffff"
            }

            element "VsamFile" {
                background "#EE9944"
                color "#ffffff"
            }

            # CICS component
            element "CicsComponent" {
                background "#1F7F7F"
                color "#ffffff"
            }

            # Relationships
            relationship "uses" {
                color "#666666"
            }
        }
    }
}
