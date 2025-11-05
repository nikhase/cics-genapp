# 3270 Terminal Screen Renderings

This directory contains visual renderings of all 3270 terminal screens defined in the CICS GenApp application (ssmap.bms). Each screen is rendered as an ASCII representation showing the layout, field positions, and input areas as they would appear on a traditional IBM 3270 terminal.

## Screen Map Index

1. **SSC1** - Customer Management Menu
2. **SSP1** - Motor Insurance Policy Menu
3. **SSP2** - Endowment Insurance Policy Menu
4. **SSP3** - House Insurance Policy Menu
5. **SSP4** - Commercial Property Insurance Policy Menu
6. **SSP5** - Policy Claim Menu

## Screen Notation

- **Field Names**: Prefixed with `[` and `]` indicate editable input fields
- **Text Labels**: Static labels that appear on the screen
- **Protected Fields**: Read-only areas (not editable)
- **Unprotected Fields**: Editable input areas
- **Position Reference**: `(row, col)` format for field positioning

## Terminal Characteristics

- **Size**: 24 rows × 80 columns (standard 3270 display)
- **Terminal Type**: IBM 3270 Information Display System
- **Control**: Free keyboard (FREEKB) - users can edit any unprotected field
- **Storage Mode**: Automatic TIOA prefix with extended attributes

## Keyboard Input

For each menu screen:
- **Option Selection**: Enter a number (1-4) at the "Select Option" prompt
- **Field Navigation**: Tab (or arrow keys) between unprotected fields
- **Field Input**: Type values directly into highlighted input fields
- **Submission**: Press Enter to submit the form

## Data Entry Rules

### Date Fields
- Format: `yyyy-mm-dd` (e.g., 2023-12-25)

### Numeric Fields
- Right-justified with zero padding
- Examples: Customer Number, Policy Number, Amounts

### Text Fields
- Left-justified with space padding
- Examples: Customer Name, Car Make, Address
