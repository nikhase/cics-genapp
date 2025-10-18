# Generate C4 Architecture Diagram

Generate or regenerate C4 architecture diagrams for any codebase using Structurizr DSL and Claude Code.

This command will:
1. Analyze your codebase to identify architectural components, services, and dependencies
2. Generate a workspace.dsl file with the complete C4 model
3. Create supporting documentation and diagrams
4. Set up a local Docker-based viewer for interactive exploration

## Quick Start

```
/generate-c4-diagram
```

Then answer the prompts about your codebase structure.

## Prerequisites

- Docker installed and running (for viewing diagrams locally)
- A codebase with source files to analyze

## What Gets Generated

**Files created in `docs/architecture/` (or your specified directory):**
- `workspace.dsl` - Structurizr DSL model with C4 views
- `view-diagrams.sh` - Docker launch script for viewing diagrams locally
- `README.md` - Complete diagram documentation
- `QUICK_START.md` - 5-minute quick start guide

## How It Works

### Initial Generation

1. **Codebase Analysis**
   - Claude analyzes your source code structure
   - Identifies architectural patterns and layers
   - Maps components and their relationships
   - Documents external dependencies and integrations

2. **C4 Model Creation**
   - System Context view (external dependencies)
   - Container view (major software components)
   - Component views (internal structure of containers)
   - Dynamic views (key workflows and interactions)
   - Deployment view (infrastructure setup)

3. **Documentation & Viewing**
   - Generate markdown documentation
   - Set up Docker launch script
   - Instructions for viewing locally or on structurizr.com

### Regeneration

To update diagrams when your codebase changes:

```
/generate-c4-diagram --update
```

This will:
- Re-analyze the codebase
- Update workspace.dsl with new components and relationships
- Preserve your custom styling and view configurations
- Update supporting documentation

## Usage Examples

**For a new codebase:**
```
/generate-c4-diagram
```

**To regenerate after major changes:**
```
/generate-c4-diagram --update
```

**To specify output directory:**
```
/generate-c4-diagram --output-dir docs/diagrams
```

## Step-by-Step Process

### Step 1: Analyze Your Codebase
Provide information about your project:
- Application name and description
- Main technology stack (language, frameworks, platforms)
- Source code location
- Number of main components/modules
- External integrations (databases, APIs, services)

### Step 2: Claude Analyzes Structure
Claude will:
- Explore your source code
- Identify architectural layers and components
- Map component relationships and dependencies
- Document data flows and interactions

### Step 3: Generate workspace.dsl
Claude creates a Structurizr DSL file with:
- **Model section:** People, Systems, Containers, Components
- **Views section:** Context, Container, Component, Dynamic, Deployment
- **Styles section:** Color-coding by layer or function
- **Configuration:** Best practices for readability

### Step 4: View Your Diagrams

**Option A: Docker (Recommended)**
```bash
cd docs/architecture
./view-diagrams.sh
```
Then open http://localhost:8080 in your browser.

**Option B: Structurizr Cloud**
1. Visit https://structurizr.com
2. Create a free account
3. Copy contents of workspace.dsl into their web editor

**Option C: Export**
Use Structurizr tools to export diagrams as PNG, SVG, etc.

## Output Structure

```
docs/
└── architecture/
    ├── workspace.dsl              # C4 model source
    ├── view-diagrams.sh           # Docker launcher
    ├── README.md                  # Full documentation
    ├── QUICK_START.md             # 5-min guide
    └── VALIDATION.md              # Technical details
```

## Customizing Your Diagrams

After generation, you can edit `workspace.dsl` to:

**Add/modify components:**
```
lg = cicsRegion.component "Label" "Description" "Technology"
```

**Change relationships:**
```
componentA -> componentB "Uses"
```

**Adjust styling:**
```
styles {
    element "Tag" {
        background #1168BD
        color #ffffff
        fontSize 15
    }
}
```

**Add new views:**
```
views {
    component cicsRegion "Title" "Description" {
        include componentA, componentB, componentA->componentB
    }
}
```

For full DSL syntax, see: https://docs.structurizr.com/dsl

## Viewing Options

### Local Docker Viewer
```bash
./view-diagrams.sh
```
- Interactive, fast
- Full zoom and navigation
- Requires Docker
- Offline capable

### Structurizr Web Editor
- Free tier available
- Cloud storage
- Team collaboration
- Export capabilities

### Generate Static Images
```bash
# Using structurizr-cli (requires Java)
structurizr export -workspace workspace.dsl -format png
```

## Troubleshooting

**Docker won't start:**
```bash
# Stop old container
docker stop structurizr-app && docker rm structurizr-app

# Check Docker is running
docker info

# Run script again
./view-diagrams.sh
```

**Port 8080 already in use:**
The view-diagrams.sh script will prompt you to use a different port.

**Model validation errors:**
- Verify all element references use correct identifiers
- Check that relationships reference elements defined in the model
- Ensure hex color codes are valid format (#RRGGBB)
- Confirm component names don't contain special characters

**Missing components:**
- Re-run with `--update` flag
- Check source code location is correctly specified
- Verify Claude analyzed all relevant directories

## Tips for Best Results

1. **Provide clear context** - Describe your architecture upfront
2. **Include technology details** - Mention languages, frameworks, databases
3. **List key integrations** - External APIs, services, systems
4. **Identify layers** - Separate presentation, business logic, data layers
5. **Use meaningful names** - Clear component names improve diagram readability

## For Different Architectures

**Microservices:**
- Each service becomes a container
- Inter-service communication shown as relationships
- Shared data stores depicted separately

**Monolithic Applications:**
- Single application container
- Multiple components within it
- Internal layers and modules shown

**Distributed Systems:**
- Multiple systems with clear boundaries
- External integrations highlighted
- Data flows and communication protocols shown

**Enterprise Systems:**
- Multiple subsystems and integration points
- Deployment infrastructure details
- Redundancy and failover patterns

## Related Commands

- Archive existing diagrams: Create timestamped backup before updating
- Compare architectures: Keep multiple workspace files for different versions
- Document changes: Update README.md with migration notes between generations

## Next Steps

1. Run the command: `/generate-c4-diagram`
2. Answer the questions about your codebase
3. Review the generated workspace.dsl
4. View diagrams locally with `./view-diagrams.sh`
5. Customize as needed and share with your team

## Resources

- **Structurizr Documentation:** https://docs.structurizr.com
- **C4 Model Guide:** https://c4model.com
- **DSL Reference:** https://docs.structurizr.com/dsl
- **PlantUML Export:** Export from workspace.dsl for other tools
