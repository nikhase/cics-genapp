# Customer List Page Implementation Analysis (Story 3.4)
## Reference for Policy List Page Implementation

This document provides a comprehensive analysis of how the Customer List Page (Story 3.4) is implemented in the CICS GenApp Vaadin project. Use this as a reference for implementing the Policy List Page.

---

## 1. ARCHITECTURE OVERVIEW

### Layered Architecture
The implementation follows a clean three-layer architecture:

```
┌─────────────────────────────────────────────┐
│ PRESENTATION LAYER: CustomerSearchView      │
│ - Vaadin UI components (Grid, Search form)  │
│ - Event handling and navigation              │
│ - Loading states and error handling          │
└──────────────┬──────────────────────────────┘
               │ Dependency Injection
               ↓
┌─────────────────────────────────────────────┐
│ SERVICE LAYER: CustomerService              │
│ - Business logic (search criteria validation)│
│ - Spring Data integration                    │
│ - Pagination and sorting                     │
└──────────────┬──────────────────────────────┘
               │ Repository pattern
               ↓
┌─────────────────────────────────────────────┐
│ DATA/API LAYER: CustomerController          │
│ - REST API endpoints (/api/v1/customers)    │
│ - Request/response handling                  │
│ - Error responses and status codes           │
└─────────────────────────────────────────────┘
```

### Framework & Dependencies
- **Frontend**: Vaadin Flow (Java-based UI framework)
- **Backend**: Spring Boot with Spring Data JPA
- **API**: RESTful with OpenAPI/Swagger documentation
- **Database**: PostgreSQL via JPA/Hibernate
- **Security**: Spring Security with JWT/Bearer tokens
- **Routing**: Vaadin Router with @Route annotation

---

## 2. CUSTOMER SEARCH VIEW (Frontend)

### File Location
`/Users/niklas/genai/sw-modernization/cics-genapp-bmad/genapp-backend/src/main/java/com/example/cicsgenapp/ui/views/CustomerSearchView.java`

### Class Declaration & Routing
```java
@Route(value = "/customers", layout = com.example.cicsgenapp.ui.layouts.MainLayout.class)
@PageTitle("Search Customers - CICS GenApp")
public class CustomerSearchView extends VerticalLayout {
```

**Key Points:**
- Extends `VerticalLayout` - Vaadin's vertical stacking layout
- Route: `/customers` - accessible via navigation
- Uses `MainLayout` as parent layout (provides header and navigation drawer)
- Page title displayed in browser tab

### Dependency Injection
```java
private final CustomerService customerService;

@Autowired
public CustomerSearchView(CustomerService customerService) {
    this.customerService = customerService;
    initializeView();
}
```

**Pattern:** Constructor injection with @Autowired annotation. Service is final and initialized during construction.

### UI Components (Instance Variables)
```java
private TextField searchInput;           // Search query input
private ComboBox<Status> statusFilter;   // Status dropdown filter
private Button searchButton;              // Search action button
private Button clearButton;               // Clear form button
private Grid<CustomerResponse> grid;      // Results display
private ProgressBar loadingIndicator;     // Loading spinner
private Div emptyStateDiv;               // "No results" message
private Div errorDiv;                    // Error message container
private Div paginationDiv;               // Pagination controls
```

### Search State Management
```java
private int currentPage = 0;             // Current page (0-based)
private int pageSize = 20;               // Items per page
private String lastQuery = "";           // Last search query
private Status lastStatus = null;        // Last status filter
```

---

## 3. VIEW INITIALIZATION & LAYOUT

### initializeView() Method
```java
private void initializeView() {
    setWidthFull();
    setPadding(true);
    setSpacing(true);
    addClassNames(LumoUtility.Padding.LARGE);

    // Title
    H2 title = new H2("Search Customers");
    title.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.FontWeight.BOLD);

    // Search form section
    add(title, createSearchForm());

    // Loading indicator (initially hidden)
    loadingIndicator = new ProgressBar();
    loadingIndicator.setIndeterminate(true);
    loadingIndicator.setVisible(false);
    add(loadingIndicator);

    // Error div (initially hidden)
    errorDiv = new Div();
    errorDiv.setVisible(false);
    errorDiv.addClassNames(
        LumoUtility.Background.ERROR_10,
        LumoUtility.Padding.MEDIUM,
        LumoUtility.BorderRadius.MEDIUM,
        LumoUtility.TextColor.ERROR);
    add(errorDiv);

    // Grid for results
    grid = new Grid<>(CustomerResponse.class, false);
    configureGrid();
    add(grid);

    // Empty state message
    emptyStateDiv = new Div();
    emptyStateDiv.setVisible(false);
    emptyStateDiv.addClassNames(
        LumoUtility.Padding.LARGE,
        LumoUtility.TextAlignment.CENTER,
        LumoUtility.TextColor.SECONDARY);
    add(emptyStateDiv);

    // Pagination controls
    paginationDiv = new Div();
    paginationDiv.setVisible(false);
    add(paginationDiv);

    showEmptyState("Enter a search query to find customers");
}
```

**Key Pattern:** Build UI bottom-up, initialize all components with visibility=false, display them conditionally based on state.

---

## 4. SEARCH FORM CREATION

### createSearchForm() Implementation
```java
private HorizontalLayout createSearchForm() {
    HorizontalLayout form = new HorizontalLayout();
    form.setWidthFull();
    form.setSpacing(true);
    form.addClassNames(LumoUtility.Gap.MEDIUM);

    // Search input
    searchInput = new TextField();
    searchInput.setPlaceholder("Search by name, email, or ID");
    searchInput.setWidth("300px");
    searchInput.setAutofocus(true);
    searchInput.addKeyDownListener(event -> {
        if (event.getKey().equals(com.vaadin.flow.component.Key.ENTER)) {
            performSearch();
        }
    });

    // Status filter
    statusFilter = new ComboBox<>();
    statusFilter.setItems(Status.ACTIVE, Status.INACTIVE);
    statusFilter.setPlaceholder("Filter by status (optional)");
    statusFilter.setWidth("200px");
    statusFilter.setClearButtonVisible(true);

    // Search button
    searchButton = new Button("Search");
    searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    searchButton.setIcon(VaadinIcon.SEARCH.create());
    searchButton.addClickListener(event -> performSearch());

    // Clear button
    clearButton = new Button("Clear");
    clearButton.addClickListener(event -> clearSearch());

    form.add(searchInput, statusFilter, searchButton, clearButton);
    form.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

    return form;
}
```

**Design Patterns:**
1. **HorizontalLayout**: Components arranged left-to-right
2. **Enter Key Binding**: `addKeyDownListener` for Enter key triggers search
3. **Placeholder Text**: Guides user on search behavior
4. **Button Variants**: LUMO_PRIMARY for primary action
5. **Icons**: VaadinIcon.SEARCH for visual clarity
6. **Clear Button**: Resets all filters and search state
7. **Optional Filter**: Status filter is optional (clearButtonVisible = true)

---

## 5. GRID CONFIGURATION

### configureGrid() Method
```java
private void configureGrid() {
    grid.setWidthFull();
    grid.setHeight("400px");
    grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    grid.addClassNames(LumoUtility.Border.ALL);

    // Define columns
    grid.addColumn(CustomerResponse::getCustomerId)
        .setHeader("ID")
        .setWidth("150px")
        .setFrozen(true);

    grid.addColumn(customer -> customer.getFirstName() + " " + customer.getLastName())
        .setHeader("Name")
        .setWidth("200px")
        .setSortable(true);

    grid.addColumn(CustomerResponse::getEmail)
        .setHeader("Email")
        .setWidth("250px")
        .setSortable(true);

    grid.addColumn(CustomerResponse::getPhone)
        .setHeader("Phone")
        .setWidth("150px");

    grid.addColumn(CustomerResponse::getStatus)
        .setHeader("Status")
        .setWidth("100px")
        .setSortable(true);

    // Row click listener to navigate to customer detail
    grid.asSingleSelect().addValueChangeListener(event -> {
        if (event.getValue() != null) {
            getUI().ifPresent(ui -> ui.navigate("/customers/" + event.getValue().getCustomerId()));
        }
    });
}
```

**Grid Configuration Details:**
- **Width/Height**: Full width, 400px height for readable result set
- **Selection Mode**: SINGLE - only one row can be selected
- **ID Column**: Frozen (sticky on left) for easy reference
- **Sortable Columns**: Name, Email, Status support sorting
- **Column Widths**: Fixed widths for layout consistency
- **Name Composition**: Combines firstName + lastName with lambda
- **Row Click Navigation**: Selecting a row navigates to `/customers/{customerId}` detail page

---

## 6. SEARCH EXECUTION FLOW

### performSearch() - User Entry Point
```java
private void performSearch() {
    lastQuery = searchInput.getValue().trim();
    lastStatus = statusFilter.getValue();

    if (lastQuery.isEmpty() && lastStatus == null) {
        showErrorMessage("Please enter a search query");
        return;
    }

    currentPage = 0;
    executeSearch();
}
```

**Logic:**
1. Capture current form values
2. Trim whitespace from query
3. Validate: at least query OR status must be provided
4. Reset to first page (important for new searches)
5. Call executeSearch()

### executeSearch() - API Call & Result Handling
```java
private void executeSearch() {
    showLoading(true);
    hideErrorMessage();

    try {
        // Call backend API
        PagedResponse<CustomerResponse> response = customerService.searchCustomers(
            new com.example.cicsgenapp.dto.SearchCriteria(
                lastQuery.isEmpty() ? null : lastQuery,
                lastStatus,
                pageSize,
                currentPage * pageSize,
                "lastName",
                "ASC"
            )
        );

        if (response.getData().isEmpty()) {
            showEmptyState("No customers found. Try different search criteria.");
        } else {
            displayResults(response);
        }
    } catch (Exception e) {
        showErrorMessage("Search failed: " + e.getMessage());
    } finally {
        showLoading(false);
    }
}
```

**API Call Details:**
- **SearchCriteria Parameters:**
  - `query`: nullable (null if empty)
  - `status`: nullable
  - `limit`: 20 items per page
  - `offset`: `currentPage * pageSize` (0-based offset)
  - `sortBy`: "lastName" (default)
  - `sortOrder`: "ASC"

- **Error Handling:** Try-catch with finally block ensures loading spinner is hidden

- **State Management:**
  - Show loading spinner before API call
  - Hide any previous error messages
  - Hide loading after response (finally block)

---

## 7. PAGINATION CONTROLS

### updatePaginationControls() Method
```java
private void updatePaginationControls(PagedResponse<CustomerResponse> response) {
    paginationDiv.removeAll();

    long totalItems = response.getPagination().getTotal();
    long totalPages = (totalItems + pageSize - 1) / pageSize;

    HorizontalLayout pagination = new HorizontalLayout();
    pagination.setSpacing(true);
    pagination.addClassNames(LumoUtility.Margin.Top.MEDIUM);

    // Previous button
    Button prevButton = new Button("Previous");
    prevButton.setEnabled(currentPage > 0);
    prevButton.addClickListener(event -> {
        if (currentPage > 0) {
            currentPage--;
            executeSearch();
        }
    });
    pagination.add(prevButton);

    // Page indicator
    Paragraph pageIndicator = new Paragraph();
    pageIndicator.setText("Page " + (currentPage + 1) + " of " + (totalPages > 0 ? totalPages : 1));
    pageIndicator.addClassNames(LumoUtility.Padding.Vertical.MEDIUM);
    pagination.add(pageIndicator);

    // Next button
    Button nextButton = new Button("Next");
    nextButton.setEnabled(currentPage < totalPages - 1);
    nextButton.addClickListener(event -> {
        if (currentPage < totalPages - 1) {
            currentPage++;
            executeSearch();
        }
    });
    pagination.add(nextButton);

    paginationDiv.add(pagination);
    paginationDiv.setVisible(true);
}
```

**Pagination Logic:**
- **Total Pages Calculation:** `(totalItems + pageSize - 1) / pageSize` (ceiling division)
- **Previous Button:** Enabled only if currentPage > 0
- **Next Button:** Enabled only if currentPage < (totalPages - 1)
- **Page Indicator:** Shows "Page X of Y" format
- **Button Click Handlers:** Decrement/increment currentPage and re-execute search

---

## 8. STATE MANAGEMENT METHODS

### showEmptyState(String message)
```java
private void showEmptyState(String message) {
    emptyStateDiv.removeAll();
    emptyStateDiv.setText(message);
    emptyStateDiv.setVisible(true);
    grid.setVisible(false);
    paginationDiv.setVisible(false);
}
```

### showErrorMessage(String message)
```java
private void showErrorMessage(String message) {
    errorDiv.removeAll();

    Div errorContent = new Div();
    Paragraph errorText = new Paragraph(message);
    Button retryButton = new Button("Retry");
    retryButton.addClickListener(event -> executeSearch());

    errorContent.add(errorText, retryButton);
    errorDiv.add(errorContent);
    errorDiv.setVisible(true);
}
```

### showLoading(boolean show)
```java
private void showLoading(boolean show) {
    loadingIndicator.setVisible(show);
}
```

### clearSearch()
```java
private void clearSearch() {
    searchInput.setValue("");
    statusFilter.setValue(null);
    currentPage = 0;
    lastQuery = "";
    lastStatus = null;
    grid.setVisible(false);
    paginationDiv.setVisible(false);
    hideErrorMessage();
    showEmptyState("Enter a search query to find customers");
}
```

---

## 9. BACKEND: CUSTOMER CONTROLLER API

### File Location
`/Users/niklas/genai/sw-modernization/cics-genapp-bmad/genapp-backend/src/main/java/com/example/cicsgenapp/api/CustomerController.java`

### Search Endpoint Signature
```java
@GetMapping
@PreAuthorize("isAuthenticated()")
@Operation(
    summary = "Search/list customers",
    description = "Search for customers with multi-field search, filtering, sorting, and pagination. "
        + "Returns paginated results with pagination metadata.",
    security = @SecurityRequirement(name = "bearer-jwt")
)
public ResponseEntity<ApiResponse<PagedResponse<CustomerResponse>>> searchCustomers(
    @RequestParam(required = false)
    @Parameter(description = "Search query (searches firstName, lastName, email, phone)")
    String query,

    @RequestParam(required = false)
    @Parameter(description = "Filter by customer status (ACTIVE, INACTIVE)")
    Status status,

    @RequestParam(defaultValue = "50")
    @Parameter(description = "Page size (default 50, max 100)")
    int limit,

    @RequestParam(defaultValue = "0")
    @Parameter(description = "Pagination offset (default 0)")
    int offset,

    @RequestParam(defaultValue = "lastName")
    @Parameter(description = "Field to sort by (firstName, lastName, email, createdAt)")
    String sortBy,

    @RequestParam(defaultValue = "ASC")
    @Parameter(description = "Sort direction (ASC or DESC)")
    String sortOrder
)
```

### URL Examples
```
GET /api/v1/customers?query=john&status=ACTIVE&limit=20&offset=0&sortBy=lastName&sortOrder=ASC
GET /api/v1/customers?query=jane&limit=20&offset=0
GET /api/v1/customers?status=INACTIVE
GET /api/v1/customers (list all)
```

### Response Structure
```java
ResponseEntity<ApiResponse<PagedResponse<CustomerResponse>>>
```

**Response Envelope:**
```json
{
  "data": {
    "data": [
      {
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "firstName": "Jane",
        "lastName": "Smith",
        "email": "jane.smith@example.com",
        "phone": "+1-555-123-4567",
        "address": "123 Main Street",
        "city": "Springfield",
        "state": "IL",
        "zipCode": "62701",
        "status": "ACTIVE",
        "createdAt": "2025-11-01T10:30:00",
        "updatedAt": "2025-11-03T14:45:00",
        "createdBy": "admin@example.com",
        "updatedBy": "user@example.com"
      }
    ],
    "pagination": {
      "limit": 20,
      "offset": 0,
      "total": 150,
      "hasMore": true
    }
  },
  "metadata": {
    "timestamp": "2025-11-04T12:00:00",
    "version": "v1",
    "operation": "SEARCH",
    "resultCount": 1
  }
}
```

---

## 10. BACKEND: CUSTOMER SERVICE

### searchCustomers() Method
```java
@Transactional(readOnly = true)
public PagedResponse<CustomerResponse> searchCustomers(SearchCriteria criteria) {
    logger.debug("Searching customers with criteria: {}", criteria);

    // Validate and normalize search criteria
    int limit = criteria.getLimit();
    int offset = criteria.getOffset();

    // Enforce limits
    if (limit > 100) {
      limit = 100;
    }
    if (limit <= 0) {
      limit = 50;
    }
    if (offset < 0) {
      offset = 0;
    }

    // Build Sort object from sortBy and sortOrder
    Sort.Direction direction = Sort.Direction.fromString(criteria.getSortOrder().toUpperCase());
    Sort sort = Sort.by(direction, criteria.getSortBy());

    // Create Pageable with offset and limit
    Pageable pageable = PageRequest.of(offset / limit, limit, sort);

    // Execute search
    Page<Customer> customerPage = customerRepository.searchCustomers(
        criteria.getQuery(),
        criteria.getStatus(),
        pageable
    );

    // Convert to response DTOs
    List<CustomerResponse> responseList = customerPage.getContent()
        .stream()
        .map(CustomerResponse::from)
        .collect(Collectors.toList());

    // Create pagination info
    PagedResponse.PaginationInfo pagination = PagedResponse.PaginationInfo.of(
        limit,
        offset,
        customerPage.getTotalElements()
    );

    // Create and log response
    PagedResponse<CustomerResponse> response = new PagedResponse<>(responseList, pagination);

    logger.info(
        "Customer search completed: found {} results (limit={}, offset={}, total={})",
        responseList.size(),
        limit,
        offset,
        customerPage.getTotalElements()
    );

    return response;
}
```

**Service Responsibilities:**
1. **Input Validation:** Enforce limits (max 100), sanitize offset
2. **Sort Building:** Convert criteria to Spring Sort object
3. **Pagination:** Convert offset-based to page-based (Pageable)
4. **Repository Delegation:** Call repository search method
5. **DTO Conversion:** Map entities to response DTOs
6. **Logging:** Info and debug logging for debugging
7. **Response Building:** Wrap data with pagination metadata

---

## 11. DATA TRANSFER OBJECTS (DTOs)

### CustomerResponse DTO
```java
@Schema(title = "Customer Response", 
    description = "Customer data returned by API, including all customer details and audit fields")
public class CustomerResponse {

  @JsonProperty("customerId")
  @Schema(description = "Unique customer identifier (UUID)")
  private UUID customerId;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("dateOfBirth")
  private LocalDate dateOfBirth;

  @JsonProperty("email")
  private String email;

  @JsonProperty("phone")
  private String phone;

  @JsonProperty("address")
  private String address;

  @JsonProperty("city")
  private String city;

  @JsonProperty("state")
  private String state;

  @JsonProperty("zipCode")
  private String zipCode;

  @JsonProperty("status")
  private Status status;

  @JsonProperty("createdAt")
  private LocalDateTime createdAt;

  @JsonProperty("updatedAt")
  private LocalDateTime updatedAt;

  @JsonProperty("createdBy")
  private String createdBy;

  @JsonProperty("updatedBy")
  private String updatedBy;

  // Static factory method
  public static CustomerResponse from(Customer customer) {
    CustomerResponse response = new CustomerResponse();
    response.customerId = customer.getCustomerId();
    response.firstName = customer.getFirstName();
    // ... map all fields
    return response;
  }
}
```

### SearchCriteria DTO
```java
public class SearchCriteria {
  private String query;
  private Status status;
  
  @Min(value = 1, message = "Limit must be at least 1")
  @Max(value = 100, message = "Limit cannot exceed 100")
  private int limit = 50;

  @Min(value = 0, message = "Offset must be non-negative")
  private int offset = 0;

  private String sortBy = "lastName";
  private String sortOrder = "ASC";

  public SearchCriteria(String query, Status status, int limit, int offset, 
                       String sortBy, String sortOrder) {
    this.query = query;
    this.status = status;
    this.limit = limit;
    this.offset = offset;
    this.sortBy = sortBy;
    this.sortOrder = sortOrder;
  }
}
```

### PagedResponse<T> Generic Wrapper
```java
public class PagedResponse<T> implements Serializable {
  private List<T> data;
  private PaginationInfo pagination;

  public static class PaginationInfo implements Serializable {
    private int limit;
    private int offset;
    private long total;
    private boolean hasMore;

    public static PaginationInfo of(int limit, int offset, long total) {
      boolean hasMore = (offset + limit) < total;
      return new PaginationInfo(limit, offset, total, hasMore);
    }
  }
}
```

---

## 12. ENTITY MODEL

### Customer Entity
```java
@Entity
@Table(name = "customer")
@EntityListeners(AuditingEntityListener.class)
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "customer_id")
  private UUID customerId;

  @NotNull(message = "First name is required")
  @Size(min = 1, max = 100)
  @Column(name = "first_name", nullable = false)
  private String firstName;

  @NotNull(message = "Last name is required")
  @Size(min = 1, max = 100)
  @Column(name = "last_name", nullable = false)
  private String lastName;

  @PastOrPresent(message = "Date of birth must be in the past or today")
  @ValidAge(message = "Customer must be at least 18 years old")
  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @NotNull(message = "Email is required")
  @Email(message = "Email should be valid")
  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$|^$")
  @Column(name = "phone")
  private String phone;

  @Column(name = "address")
  private String address;

  @Column(name = "city")
  private String city;

  @Column(name = "state")
  private String state;

  @Column(name = "zip_code")
  private String zipCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private Status status = Status.ACTIVE;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;

  @Version
  @Column(name = "version")
  private Long version;  // Optimistic locking

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "deletion_reason")
  private String deletionReason;
}
```

### Status Enum
```java
public enum Status {
  ACTIVE,
  INACTIVE
}
```

---

## 13. ROUTING & NAVIGATION

### MainLayout Navigation
```java
// Customers section
SideNavItem customers = new SideNavItem("Customers", "/customers", VaadinIcon.USERS.create());
nav.addItem(customers);

// Policies section
SideNavItem policies = new SideNavItem("Policies", "/policies", VaadinIcon.FILE_TEXT.create());
nav.addItem(policies);
```

### Route URLs
- **Customer List:** `/customers` → CustomerSearchView
- **Customer Detail:** `/customers/{customerId}` → CustomerDetailView
- **Customer Create/Edit:** `/customers/new` and `/customers/{customerId}/edit`

### Navigation from Grid
```java
grid.asSingleSelect().addValueChangeListener(event -> {
    if (event.getValue() != null) {
        getUI().ifPresent(ui -> ui.navigate("/customers/" + event.getValue().getCustomerId()));
    }
});
```

---

## 14. IMPLEMENTATION PATTERNS & BEST PRACTICES

### 1. Component State Visibility Pattern
```
Initial State: showEmptyState("Enter search query...")
         ↓
User Clicks Search: showLoading(true)
         ↓
API Response: displayResults() OR showErrorMessage()
         ↓
Final State: grid.setVisible(true), paginationDiv.setVisible(true)
            OR emptyStateDiv.setVisible(true)
            OR errorDiv.setVisible(true)
```

### 2. Error Handling with Retry
```java
try {
    // API call
} catch (Exception e) {
    showErrorMessage("Search failed: " + e.getMessage());
    // Error message includes Retry button
} finally {
    showLoading(false);
}
```

### 3. Pagination with Page Tracking
```
User on Page 1 clicks "Next"
         ↓
currentPage++ (0 → 1)
         ↓
executeSearch() with offset = currentPage * pageSize
         ↓
API returns next batch
```

### 4. Nullable Filter Pattern
```java
// Status filter is optional
statusFilter = new ComboBox<>();
statusFilter.setClearButtonVisible(true);  // Allow unsetting
// Later: lastStatus = statusFilter.getValue();  // May be null
// In SearchCriteria: status can be null in repository query
```

### 5. Lambda Column Rendering
```java
// Computed columns
grid.addColumn(customer -> customer.getFirstName() + " " + customer.getLastName())
    .setHeader("Name");

// Method reference columns
grid.addColumn(CustomerResponse::getEmail)
    .setHeader("Email");
```

### 6. Column Freezing for Fixed Headers
```java
grid.addColumn(CustomerResponse::getCustomerId)
    .setHeader("ID")
    .setFrozen(true);  // Stays visible when scrolling
```

### 7. Sortable Columns Configuration
```java
grid.addColumn(...)
    .setHeader("Name")
    .setSortable(true);  // User can click header to sort
```

---

## 15. ACCEPTANCE CRITERIA MAPPING

**Story 3.4 AC → Implementation:**

| Acceptance Criteria | Implementation |
|-------------------|-----------------|
| Search form with query input | `createSearchForm()` with TextField |
| Optional status filter | ComboBox with `setClearButtonVisible(true)` |
| Search button & auto-search on Enter | Button click + Enter key listener |
| Loading indicator | ProgressBar with `setIndeterminate(true)` |
| Results in Vaadin Grid | `configureGrid()` with 5 sortable columns |
| Sortable columns | `setSortable(true)` on Name, Email, Status |
| Pagination with Previous/Next buttons | `updatePaginationControls()` |
| Page indicator | "Page X of Y" Paragraph component |
| Row click navigation | `asSingleSelect().addValueChangeListener()` |
| Empty state message | `showEmptyState()` method |
| Error message with retry | `showErrorMessage()` with Retry button |
| Performance < 2 seconds | API call with standard pagination (limit=20) |
| Responsive layout | `setWidthFull()` on all containers |
| Accessibility | Proper labels and keyboard navigation (Enter key) |

---

## 16. KEY FILES SUMMARY

| File | Purpose | Key Class |
|------|---------|-----------|
| CustomerSearchView.java | Frontend UI component | `CustomerSearchView extends VerticalLayout` |
| CustomerController.java | REST API endpoints | `@GetMapping /api/v1/customers` |
| CustomerService.java | Business logic | `searchCustomers(SearchCriteria)` |
| CustomerResponse.java | DTO for API responses | Response envelope for grid |
| SearchCriteria.java | DTO for search params | Encapsulates all filter/sort/page params |
| PagedResponse.java | Generic pagination wrapper | Generic `<T>` for any paginated data |
| Customer.java | JPA entity | Maps to PostgreSQL customer table |
| Status.java | Enum for status values | ACTIVE / INACTIVE |
| MainLayout.java | Root layout | Navigation drawer with route links |

---

## 17. REFERENCE: HOW TO ADAPT FOR POLICY LIST PAGE

### Steps to create PolicySearchView:

1. **Create PolicySearchView.java**
   - Extend VerticalLayout
   - Route: `/policies`
   - Constructor inject PolicyService

2. **Create search form with:**
   - TextField for policy ID or customer name
   - ComboBox for PolicyType (MOTOR, ENDOWMENT, HOUSE, COMMERCIAL)
   - ComboBox for Status (ACTIVE, EXPIRED, CANCELLED)
   - Search and Clear buttons

3. **Configure Grid with columns:**
   - Policy ID (frozen)
   - Customer Name
   - Policy Type
   - Effective Date
   - Expiration Date
   - Status (sortable)
   - Premium Amount

4. **Implement same pagination and state management**
   - Same showLoading(), showErrorMessage(), showEmptyState() pattern
   - Same pagination controls with Previous/Next buttons

5. **Create PolicyResponse DTO**
   - Follow same pattern as CustomerResponse
   - Include policy-specific fields

6. **Create PolicyController endpoint**
   - GET /api/v1/policies
   - Same search parameters pattern (query, status, limit, offset, sortBy, sortOrder)
   - Return PagedResponse<PolicyResponse>

7. **Create PolicyService.searchPolicies()**
   - Same pattern as CustomerService.searchCustomers()
   - Repository call to search policies
   - DTO conversion and pagination info

---

## CONCLUSION

The Customer List Page implements a production-ready search and pagination UI with:
- **Clean separation of concerns** (UI, Service, API layers)
- **Spring Security integration** (authentication required)
- **RESTful API design** with OpenAPI documentation
- **Pagination and filtering** with offset-based navigation
- **Error handling and retry** mechanisms
- **Responsive UI** using Vaadin Grid
- **Accessibility support** with keyboard navigation
- **State management** for loading, empty, and error states

This architecture and pattern should be directly replicated for the Policy List Page with appropriate entity/field substitutions.

