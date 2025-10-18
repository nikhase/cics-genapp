# Quick Start: View GenApp Architecture Diagrams

## 1. Launch the Viewer (Easiest)

```bash
cd ai-generated/docs/architecture
./view-diagrams.sh
```

This will:
- Pull the Structurizr Lite Docker image
- Start the viewer on port 8080
- Open your browser automatically to http://localhost:8080

## 2. Alternative: Manual Docker Command

```bash
cd ai-generated/docs/architecture
docker run -it --rm -p 8080:8080 -v $(pwd):/usr/local/structurizr structurizr/lite
```

Then open http://localhost:8080

## 3. No Docker? Use Structurizr Cloud

1. Go to https://structurizr.com and create a free account
2. Create a new workspace
3. Copy the entire contents of `workspace.dsl`
4. Paste into the web editor
5. Click "Save"

## What You'll See

### 9 Interactive Diagrams

1. **System Context** - GenApp's place in the ecosystem
   - 2 users (Insurance Agent, System Admin)
   - 2 external systems (Db2, Coupling Facility)

2. **Containers** - Major architectural components
   - CICS Transaction Server (31+ programs)
   - VSAM Files (KSDSCUST, KSDSPOLY)
   - Temporary Storage Queues
   - External Db2 and Coupling Facility

3. **CICS Components (Complete)** - All 31+ programs color-coded by layer
   - Orange: Presentation layer (5 programs)
   - Teal: Business logic (7 programs)
   - Dark teal: Data access (14 programs)
   - Yellow: Utilities (4 programs)

4. **Customer Operations** - Focused view
   - Customer add/inquire/update flow
   - Shows COMMAREA communication
   - Highlights dual-write pattern

5. **Policy Operations** - Focused view
   - All 4 policy types (Motor, Endowment, House, Commercial)
   - Add/inquire/update/delete operations
   - Complex Db2 joins visualization

6. **Data Layer Architecture** - Technical debt view
   - **Dual-write pattern** clearly visible
   - Shows why VSAM removal is priority #1
   - Db2 and VSAM side-by-side

7. **Add Customer Transaction** - Step-by-step sequence (CICS internal flow)
   - Presentation → Business logic → Db2 → VSAM
   - Shows error logging on failure
   - BMS screen updates

8. **Inquire Policy Transaction** - Step-by-step sequence (CICS internal flow)
   - Motor policy inquiry through layers
   - Data retrieval from Db2
   - Screen formatting and return

9. **Production Deployment** - Infrastructure topology
   - z/OS mainframe layout
   - CICS region configuration
   - VSAM storage
   - Db2 subsystem
   - Coupling facility
   - 3270 terminal connection

## Navigation Tips

### In Structurizr Viewer:

- **Click on elements** to see details and relationships
- **Use the dropdown** at the top to switch between views
- **Zoom**: Mouse wheel or pinch gesture
- **Pan**: Click and drag
- **Export**: Click download icon (top-right) for PNG/SVG

### Recommended Viewing Order:

1. Start with **System Context** → understand boundaries
2. Move to **Containers** → see major components
3. Explore **Customer Operations** → understand a complete flow
4. Check **Data Layer Architecture** → see the technical debt
5. View **Add Customer Transaction** → step-by-step execution
6. Finally **CICS Components** → complete program map

## Understanding the Color Coding

- **Blue** = Software Systems (GenApp, Db2, Coupling Facility)
- **Gray** = External Systems (Db2, Coupling Facility)
- **Dark Blue (Person)** = Users (Insurance Agent)
- **Orange (Person)** = Administrators (System Admin)
- **Teal** = CICS Container
- **Orange (Component)** = Presentation layer programs
- **Teal (Component)** = Business logic programs
- **Dark Teal (Component)** = Data access programs
- **Yellow (Component)** = Utility programs
- **Red (Cylinder)** = Data stores (VSAM, TSQ)

## Key Insights to Look For

### 1. Three-Tier Architecture
Notice how every operation flows through exactly 3 layers:
```
Presentation (lgtestXX)
    → Business Logic (lgXXus01/pol01)
        → Data Access (lgXXdb01 + lgXXvs01)
```

### 2. Dual-Write Technical Debt
In the "Data Layer Architecture" view, you'll see:
- Every write operation goes to BOTH Db2 AND VSAM
- This is acknowledged technical debt
- Removing VSAM is priority #1 for modernization

### 3. COMMAREA Coupling
Every `EXEC CICS LINK` passes a 32,500-byte COMMAREA:
- 6 bytes: Request ID
- 2 bytes: Return code
- 10 bytes: Customer number
- 32,482 bytes: Request-specific data

This is a modernization opportunity → REST APIs with JSON

### 4. Single Point of Failure
The "Error Logger" (lgstsq) component is called by 27+ programs.
If it fails, observability is lost across the entire system.

### 5. Critical Dependencies
- **Coupling Facility**: Required for unique customer numbers
- **Db2**: Primary data store (7 tables)
- **VSAM**: Secondary store (acknowledged as unnecessary)
- **BMS Mapset**: Single 688-line file serving 5 screens

## Troubleshooting

### Port 8080 Already in Use?

Edit the script or run manually with a different port:

```bash
docker run -it --rm -p 8081:8080 -v $(pwd):/usr/local/structurizr structurizr/lite
```

Then open http://localhost:8081

### Docker Not Running?

Start Docker Desktop, then try again.

### Want to Export Diagrams?

Once loaded in Structurizr:
1. Navigate to the diagram you want
2. Click the download icon (top-right)
3. Choose format: PNG, SVG, or PlantUML
4. Save to your local machine

### Editing the Diagrams?

Edit `workspace.dsl` and refresh the browser. Structurizr Lite watches for changes.

## Next Steps

After viewing the diagrams:

1. **Read the analysis**: `../CODEBASE_ANALYSIS.md`
2. **Plan testing**: `../TESTING_WITHOUT_MAINFRAME.md`
3. **Review README**: `./README.md` for detailed documentation

## Questions?

The diagrams answer common questions:

- **"How complex is this system?"** → See CICS Components view (31+ programs)
- **"What's the biggest technical debt?"** → Data Layer Architecture (VSAM dual-write)
- **"How do transactions flow?"** → Dynamic diagrams (step-by-step)
- **"What can we modernize?"** → Business Logic layer is cleanest (7 programs, 139-201 LOC each)
- **"What are the dependencies?"** → System Context + Containers views
- **"How does it deploy?"** → Production Deployment view

## Enjoy Exploring!

The diagrams make it easy to:
- Onboard new team members (30 minutes vs. 3 days)
- Plan modernization efforts
- Identify refactoring opportunities
- Communicate architecture to stakeholders
- Document technical debt
- Estimate migration effort
