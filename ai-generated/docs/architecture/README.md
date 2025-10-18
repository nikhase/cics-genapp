# GenApp C4 Architecture Diagrams

This directory contains C4 model diagrams for the CICS GenApp application using Structurizr DSL.

> **Note**: This workspace uses hierarchical identifiers (`!identifiers hierarchical`), which means all component references use fully qualified paths like `genApp.cicsRegion.customerUI`.

## Diagram Overview

The `workspace.dsl` file defines the following views:

### 1. System Context Diagram
Shows GenApp in relation to its users and external systems (Db2, Coupling Facility).

### 2. Container Diagram
Shows the main containers: CICS Transaction Server, VSAM Files, Temporary Storage Queues, and their relationships.

### 3. Component Diagrams
- **CICSComponents**: All 31+ components organized by layer (Presentation, Business, Data Access, Utilities)
- **CustomerOperations**: Focused view on customer add/inquire/update operations
- **PolicyOperations**: Focused view on policy management for all 4 policy types
- **DataLayerArchitecture**: Shows the dual-write pattern (Db2 + VSAM) - the acknowledged technical debt

### 4. Dynamic Diagrams
- **AddCustomerTransaction**: Step-by-step sequence for adding a customer (SSC1 transaction)
- **InquirePolicyTransaction**: Step-by-step sequence for inquiring on a motor policy (SSP1 transaction)

### 5. Deployment Diagram
Shows how GenApp deploys on IBM z/OS mainframe infrastructure.

## How to Visualize the Diagrams

### Option 1: Structurizr Lite (Recommended - Free, Local)

**Using Docker:**

```bash
# From this directory
docker pull structurizr/lite
docker run -it --rm -p 8080:8080 -v $(pwd):/usr/local/structurizr structurizr/lite

# Open in browser
open http://localhost:8080
```

**Manual Setup:**

1. Download Structurizr Lite from https://structurizr.com/help/lite
2. Extract and run:
   ```bash
   java -jar structurizr-lite.jar /path/to/this/directory
   ```
3. Open http://localhost:8080

### Option 2: Structurizr Cloud (Free tier available)

1. Go to https://structurizr.com
2. Create a free account
3. Create a new workspace
4. Copy the contents of `workspace.dsl` into the web editor
5. Click "Save" to render the diagrams

### Option 3: VS Code Extension

1. Install the "Structurizr" extension by structurizr
2. Open `workspace.dsl` in VS Code
3. Use the preview pane to view diagrams

### Option 4: Export to PlantUML/Mermaid

You can use the Structurizr CLI to export to other formats:

```bash
# Install Structurizr CLI
npm install -g @structurizr/cli

# Export to PlantUML
structurizr export -workspace workspace.dsl -format plantuml

# Export to Mermaid
structurizr export -workspace workspace.dsl -format mermaid
```

## Diagram Navigation

Once you have the workspace loaded in Structurizr Lite/Cloud:

1. **Start with System Context** - Understand GenApp's boundaries
2. **Move to Container View** - See the CICS region, VSAM, and external dependencies
3. **Explore Component Views**:
   - Start with "CustomerOperations" for a focused, understandable flow
   - Then look at "PolicyOperations" to see the 4 policy types
   - Study "DataLayerArchitecture" to understand the dual-write pattern
4. **View Dynamic Diagrams** - See step-by-step transaction flows
5. **Check Deployment** - Understand the z/OS infrastructure requirements

## Key Insights from the Diagrams

### Three-Tier Architecture
The component diagrams clearly show the separation:
- **Presentation Layer** (orange): 5 programs (lgtestXX.cbl) - 3270 UI handling
- **Business Logic Layer** (teal): 7 programs (lgXXus01, lgXXpol01) - Validation & orchestration
- **Data Access Layer** (dark teal): 14 programs (lgXXdb01, lgXXvs01) - Db2 and VSAM operations

### Dual-Write Pattern (Technical Debt)
The "DataLayerArchitecture" view highlights that write operations go to BOTH:
- Db2 (primary, relational database)
- VSAM (secondary, file system)

This is acknowledged in the codebase documentation as "not a best practice" and is a priority for removal.

### COMMAREA Communication
All component interactions use EXEC CICS LINK with COMMAREA (32,500 bytes):
- Fixed 6-byte request ID
- 2-byte return code
- 10-byte customer number
- 32,482 bytes of request-specific data

### Critical Dependencies
- **lgstsq** (Error Logger): Called by 27+ programs - single point of failure for observability
- **Coupling Facility**: Required for named counter server (customer number generation)
- **BMS Mapset**: Single monolithic map (688 LOC) serving 5 different screens

## Customizing the Diagrams

### Adding New Components

```dsl
# In the cicsRegion container section
newComponent = component "New Component Name" "Description" "Technology" "Tag"

# Add relationships
existingComponent -> newComponent "Description" "Technology"
```

### Creating Custom Views

```dsl
# In the views section
component cicsRegion "MyCustomView" {
    include component1 component2 component3
    include relationship1 relationship2
    autoLayout tb  # or lr, rl, bt
    description "Description of what this view shows"
}
```

### Styling

Modify the `styles` section to change colors, shapes, etc.:

```dsl
styles {
    element "YourTag" {
        background #hexcolor
        color #hexcolor
        shape Box  # or RoundedBox, Circle, Ellipse, Hexagon, Cylinder, Component, Person, Robot, Folder, WebBrowser, MobileDevicePortrait, MobileDeviceLandscape, Pipe
    }
}
```

## Using These Diagrams

### For Documentation
- Include diagram PNGs in your modernization proposals
- Reference specific component names in technical discussions
- Use deployment diagram for infrastructure planning

### For Analysis
- Identify tight coupling between components
- Spot single points of failure (e.g., error logger)
- Understand transaction flows for testing

### For Modernization Planning
- Use component boundaries as microservice candidates
- Map EXEC CICS LINK calls to REST API endpoints
- Identify which components to rewrite vs. rehost vs. retire

### For Onboarding
- New team members can understand architecture in 30 minutes
- Visual representation of COBOL program relationships
- Clear separation of concerns across layers

## Exporting Diagrams

### From Structurizr Lite
1. Open the diagram you want to export
2. Click the download icon in the top-right
3. Choose format: PNG, SVG, or PlantUML

### From Structurizr Cloud
1. Navigate to the diagram
2. Click "Export" → Choose format
3. Download the file

### Using CLI

```bash
# Export all diagrams as PNG
structurizr export -workspace workspace.dsl -format png -output ./exports/

# Export specific diagram
structurizr export -workspace workspace.dsl -format svg -output ./exports/ -view SystemContext
```

## Diagram Files Structure

```
architecture/
├── README.md                    # This file
├── workspace.dsl                # Structurizr DSL source (single source of truth)
├── exports/                     # Generated diagram images (gitignored)
│   ├── SystemContext.png
│   ├── Containers.png
│   ├── CustomerOperations.png
│   └── ...
└── docs/                        # Additional documentation (optional)
    └── architecture-decisions/
```

## Related Documentation

- [CODEBASE_ANALYSIS.md](../CODEBASE_ANALYSIS.md) - Detailed codebase analysis with metrics and modernization roadmap
- [TESTING_WITHOUT_MAINFRAME.md](../TESTING_WITHOUT_MAINFRAME.md) - Guide for testing without z/OS access
- [CLAUDE.md](../../../CLAUDE.md) - Repository overview and build instructions

## Further Reading

- [C4 Model](https://c4model.com/) - Detailed explanation of the C4 modeling approach
- [Structurizr DSL Documentation](https://github.com/structurizr/dsl) - Complete DSL reference
- [Structurizr Examples](https://structurizr.com/help/examples) - Sample workspaces

## Contributing

To update the architecture diagrams:

1. Edit `workspace.dsl`
2. Test locally with Structurizr Lite
3. Export diagrams to `exports/` (if needed for presentations)
4. Commit both the DSL and exported images (if applicable)

## License

These diagrams are part of the GenApp project and follow the same Eclipse Public License 2.0.
