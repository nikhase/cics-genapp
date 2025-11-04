# Policy List Page (Story 3.8) - Implementation Quick Reference

This guide provides the specific patterns and code structure needed to implement the Policy List Page based on the Customer List Page reference.

---

## 1. ENTITY STRUCTURE

### Policy Entity (Proposed)
```java
@Entity
@Table(name = "policy")
@EntityListeners(AuditingEntityListener.class)
public class Policy {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID policyId;

  @ManyToOne
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @Enumerated(EnumType.STRING)
  @Column(name = "policy_type", nullable = false)
  private PolicyType policyType;  // MOTOR, ENDOWMENT, HOUSE, COMMERCIAL

  @Column(name = "policy_number", unique = true, nullable = false)
  private String policyNumber;

  @Column(name = "effective_date", nullable = false)
  private LocalDate effectiveDate;

  @Column(name = "expiration_date", nullable = false)
  private LocalDate expirationDate;

  @Column(name = "premium_amount", nullable = false)
  private BigDecimal premiumAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private PolicyStatus status;  // ACTIVE, EXPIRED, CANCELLED

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Version
  private Long version;
}
```

### Enums
```java
public enum PolicyType {
  MOTOR,
  ENDOWMENT,
  HOUSE,
  COMMERCIAL
}

public enum PolicyStatus {
  ACTIVE,
  EXPIRED,
  CANCELLED
}
```

---

## 2. DTO STRUCTURE

### PolicyResponse DTO
```java
@Schema(title = "Policy Response", description = "Policy data returned by API")
public class PolicyResponse {

  @JsonProperty("policyId")
  @Schema(description = "Unique policy identifier (UUID)")
  private UUID policyId;

  @JsonProperty("policyNumber")
  @Schema(description = "Unique policy number")
  private String policyNumber;

  @JsonProperty("customerId")
  private UUID customerId;

  @JsonProperty("customerName")
  @Schema(description = "Customer full name")
  private String customerName;

  @JsonProperty("policyType")
  @Schema(description = "Type of policy (MOTOR, ENDOWMENT, HOUSE, COMMERCIAL)")
  private PolicyType policyType;

  @JsonProperty("effectiveDate")
  private LocalDate effectiveDate;

  @JsonProperty("expirationDate")
  private LocalDate expirationDate;

  @JsonProperty("premiumAmount")
  @Schema(description = "Policy premium amount")
  private BigDecimal premiumAmount;

  @JsonProperty("status")
  private PolicyStatus status;

  @JsonProperty("createdAt")
  private LocalDateTime createdAt;

  @JsonProperty("updatedAt")
  private LocalDateTime updatedAt;

  // Factory method
  public static PolicyResponse from(Policy policy) {
    PolicyResponse response = new PolicyResponse();
    response.policyId = policy.getPolicyId();
    response.policyNumber = policy.getPolicyNumber();
    response.customerId = policy.getCustomer().getCustomerId();
    response.customerName = policy.getCustomer().getFirstName() + " " + 
                            policy.getCustomer().getLastName();
    response.policyType = policy.getPolicyType();
    response.effectiveDate = policy.getEffectiveDate();
    response.expirationDate = policy.getExpirationDate();
    response.premiumAmount = policy.getPremiumAmount();
    response.status = policy.getStatus();
    response.createdAt = policy.getCreatedAt();
    response.updatedAt = policy.getUpdatedAt();
    return response;
  }
}
```

### PolicySearchCriteria DTO
```java
public class PolicySearchCriteria extends SearchCriteria {
  
  private PolicyType policyType;      // Filter by type
  private PolicyStatus policyStatus;  // Filter by status
  private LocalDate effectiveDateFrom;
  private LocalDate effectiveDateTo;

  // Constructor
  public PolicySearchCriteria(String query, PolicyType policyType, 
                              PolicyStatus policyStatus, int limit, int offset,
                              String sortBy, String sortOrder) {
    super(query, null, limit, offset, sortBy, sortOrder);
    this.policyType = policyType;
    this.policyStatus = policyStatus;
  }
}
```

---

## 3. BACKEND: POLICY CONTROLLER

### Controller Endpoint
```java
@RestController
@RequestMapping("/api/v1/policies")
@Tag(name = "Policies", description = "Policy management APIs")
public class PolicyController {

  private final PolicyService policyService;

  public PolicyController(PolicyService policyService) {
    this.policyService = policyService;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Search/list policies")
  public ResponseEntity<ApiResponse<PagedResponse<PolicyResponse>>> searchPolicies(
      @RequestParam(required = false)
      @Parameter(description = "Search query (searches policyNumber, customerName)")
      String query,

      @RequestParam(required = false)
      @Parameter(description = "Filter by policy type")
      PolicyType policyType,

      @RequestParam(required = false)
      @Parameter(description = "Filter by policy status")
      PolicyStatus status,

      @RequestParam(defaultValue = "50")
      int limit,

      @RequestParam(defaultValue = "0")
      int offset,

      @RequestParam(defaultValue = "policyNumber")
      String sortBy,

      @RequestParam(defaultValue = "ASC")
      String sortOrder
  ) {
    PolicySearchCriteria criteria = new PolicySearchCriteria(
        query, policyType, status, limit, offset, sortBy, sortOrder
    );

    PagedResponse<PolicyResponse> response = policyService.searchPolicies(criteria);

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("timestamp", LocalDateTime.now());
    metadata.put("version", "v1");
    metadata.put("operation", "SEARCH");
    metadata.put("resultCount", response.getData().size());

    return ResponseEntity.ok(new ApiResponse<>(response, metadata));
  }

  @GetMapping("/{policyId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<PolicyResponse>> getPolicy(
      @PathVariable UUID policyId) {
    PolicyResponse response = policyService.getPolicy(policyId);
    return ResponseEntity.ok(new ApiResponse<>(response, new HashMap<>()));
  }
}
```

---

## 4. BACKEND: POLICY SERVICE

```java
@Service
public class PolicyService {

  private final PolicyRepository policyRepository;
  private final AuditService auditService;

  public PolicyService(PolicyRepository policyRepository, AuditService auditService) {
    this.policyRepository = policyRepository;
    this.auditService = auditService;
  }

  @Transactional(readOnly = true)
  public PagedResponse<PolicyResponse> searchPolicies(PolicySearchCriteria criteria) {
    // Validate limits
    int limit = Math.min(criteria.getLimit(), 100);
    limit = Math.max(limit, 1);
    int offset = Math.max(criteria.getOffset(), 0);

    // Build sort
    Sort.Direction direction = Sort.Direction.fromString(criteria.getSortOrder().toUpperCase());
    Sort sort = Sort.by(direction, criteria.getSortBy());

    // Create pageable (convert offset to page number)
    Pageable pageable = PageRequest.of(offset / limit, limit, sort);

    // Search with filters
    Page<Policy> policyPage = policyRepository.searchPolicies(
        criteria.getQuery(),
        criteria.getPolicyType(),
        criteria.getPolicyStatus(),
        pageable
    );

    // Convert to DTOs
    List<PolicyResponse> responseList = policyPage.getContent()
        .stream()
        .map(PolicyResponse::from)
        .collect(Collectors.toList());

    // Pagination info
    PagedResponse.PaginationInfo pagination = PagedResponse.PaginationInfo.of(
        limit, offset, policyPage.getTotalElements()
    );

    return new PagedResponse<>(responseList, pagination);
  }

  @Transactional(readOnly = true)
  public PolicyResponse getPolicy(UUID policyId) {
    Policy policy = policyRepository.findById(policyId)
        .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
    return PolicyResponse.from(policy);
  }
}
```

---

## 5. BACKEND: POLICY REPOSITORY

```java
@Repository
public interface PolicyRepository extends JpaRepository<Policy, UUID> {

  Page<Policy> searchPolicies(
      @Param("query") String query,
      @Param("policyType") PolicyType policyType,
      @Param("status") PolicyStatus status,
      Pageable pageable
  );

  Optional<Policy> findByPolicyNumber(String policyNumber);

  Page<Policy> findByCustomerId(UUID customerId, Pageable pageable);
}
```

### Repository Query Implementation
```java
@Query("""
    SELECT p FROM Policy p
    WHERE (:query IS NULL OR 
           LOWER(p.policyNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR
           LOWER(p.customer.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR
           LOWER(p.customer.lastName) LIKE LOWER(CONCAT('%', :query, '%')))
    AND (:policyType IS NULL OR p.policyType = :policyType)
    AND (:status IS NULL OR p.status = :status)
    AND p.status != 'CANCELLED'
""")
Page<Policy> searchPolicies(
    @Param("query") String query,
    @Param("policyType") PolicyType policyType,
    @Param("status") PolicyStatus status,
    Pageable pageable
);
```

---

## 6. FRONTEND: POLICY SEARCH VIEW

### Class Structure
```java
@Route(value = "/policies", layout = MainLayout.class)
@PageTitle("Search Policies - CICS GenApp")
public class PolicySearchView extends VerticalLayout {

  private final PolicyService policyService;

  // UI Components (same pattern as Customer)
  private TextField searchInput;
  private ComboBox<PolicyType> policyTypeFilter;
  private ComboBox<PolicyStatus> statusFilter;
  private Button searchButton;
  private Button clearButton;
  private Grid<PolicyResponse> grid;
  private ProgressBar loadingIndicator;
  private Div emptyStateDiv;
  private Div errorDiv;
  private Div paginationDiv;

  // State
  private int currentPage = 0;
  private int pageSize = 20;
  private String lastQuery = "";
  private PolicyType lastPolicyType = null;
  private PolicyStatus lastStatus = null;

  @Autowired
  public PolicySearchView(PolicyService policyService) {
    this.policyService = policyService;
    initializeView();
  }
}
```

### createSearchForm() Method
```java
private HorizontalLayout createSearchForm() {
    HorizontalLayout form = new HorizontalLayout();
    form.setWidthFull();
    form.setSpacing(true);
    form.addClassNames(LumoUtility.Gap.MEDIUM);

    // Search input
    searchInput = new TextField();
    searchInput.setPlaceholder("Search by policy number or customer");
    searchInput.setWidth("300px");
    searchInput.setAutofocus(true);
    searchInput.addKeyDownListener(event -> {
        if (event.getKey().equals(Key.ENTER)) {
            performSearch();
        }
    });

    // Policy Type filter
    policyTypeFilter = new ComboBox<>();
    policyTypeFilter.setItems(PolicyType.MOTOR, PolicyType.ENDOWMENT, 
                              PolicyType.HOUSE, PolicyType.COMMERCIAL);
    policyTypeFilter.setPlaceholder("Filter by type (optional)");
    policyTypeFilter.setWidth("200px");
    policyTypeFilter.setClearButtonVisible(true);

    // Status filter
    statusFilter = new ComboBox<>();
    statusFilter.setItems(PolicyStatus.ACTIVE, PolicyStatus.EXPIRED, PolicyStatus.CANCELLED);
    statusFilter.setPlaceholder("Filter by status (optional)");
    statusFilter.setWidth("200px");
    statusFilter.setClearButtonVisible(true);

    // Buttons
    searchButton = new Button("Search");
    searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    searchButton.setIcon(VaadinIcon.SEARCH.create());
    searchButton.addClickListener(event -> performSearch());

    clearButton = new Button("Clear");
    clearButton.addClickListener(event -> clearSearch());

    form.add(searchInput, policyTypeFilter, statusFilter, searchButton, clearButton);
    form.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

    return form;
}
```

### configureGrid() Method
```java
private void configureGrid() {
    grid.setWidthFull();
    grid.setHeight("400px");
    grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    grid.addClassNames(LumoUtility.Border.ALL);

    // Frozen ID column
    grid.addColumn(PolicyResponse::getPolicyNumber)
        .setHeader("Policy #")
        .setWidth("150px")
        .setFrozen(true);

    // Customer Name
    grid.addColumn(PolicyResponse::getCustomerName)
        .setHeader("Customer")
        .setWidth("200px")
        .setSortable(true);

    // Policy Type
    grid.addColumn(PolicyResponse::getPolicyType)
        .setHeader("Type")
        .setWidth("120px")
        .setSortable(true);

    // Effective Date
    grid.addColumn(response -> response.getEffectiveDate().format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .setHeader("Effective")
        .setWidth("130px");

    // Expiration Date
    grid.addColumn(response -> response.getExpirationDate().format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .setHeader("Expiration")
        .setWidth("130px");

    // Premium
    grid.addColumn(response -> "$" + response.getPremiumAmount())
        .setHeader("Premium")
        .setWidth("120px");

    // Status
    grid.addColumn(PolicyResponse::getStatus)
        .setHeader("Status")
        .setWidth("100px")
        .setSortable(true);

    // Row click navigation
    grid.asSingleSelect().addValueChangeListener(event -> {
        if (event.getValue() != null) {
            getUI().ifPresent(ui -> 
                ui.navigate("/policies/" + event.getValue().getPolicyId())
            );
        }
    });
}
```

### Search Execution
```java
private void performSearch() {
    lastQuery = searchInput.getValue().trim();
    lastPolicyType = policyTypeFilter.getValue();
    lastStatus = statusFilter.getValue();

    if (lastQuery.isEmpty() && lastPolicyType == null && lastStatus == null) {
        showErrorMessage("Enter search criteria");
        return;
    }

    currentPage = 0;
    executeSearch();
}

private void executeSearch() {
    showLoading(true);
    hideErrorMessage();

    try {
        PagedResponse<PolicyResponse> response = policyService.searchPolicies(
            new PolicySearchCriteria(
                lastQuery.isEmpty() ? null : lastQuery,
                lastPolicyType,
                lastStatus,
                pageSize,
                currentPage * pageSize,
                "policyNumber",
                "ASC"
            )
        );

        if (response.getData().isEmpty()) {
            showEmptyState("No policies found matching your criteria");
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

---

## 7. URL EXAMPLES

```
GET /api/v1/policies?query=POL123&policyType=MOTOR&status=ACTIVE&limit=20&offset=0
GET /api/v1/policies?query=Smith&limit=20&offset=0
GET /api/v1/policies?policyType=ENDOWMENT&status=ACTIVE
GET /api/v1/policies (list all active)
```

---

## 8. NAVIGATION UPDATES

### Update MainLayout
```java
// Policies section (already present)
SideNavItem policies = new SideNavItem("Policies", "/policies", VaadinIcon.FILE_TEXT.create());
nav.addItem(policies);
```

### Routes
- `/policies` → PolicySearchView (list page)
- `/policies/{policyId}` → PolicyDetailView (detail page)
- `/policies/new` → PolicyCreateView (create page)

---

## 9. KEY DIFFERENCES FROM CUSTOMER

| Aspect | Customer | Policy |
|--------|----------|--------|
| ID Field | customerId | policyNumber (+ policyId) |
| Search Fields | name, email, phone | policyNumber, customerName |
| Type Filter | Status (ACTIVE/INACTIVE) | PolicyType (4 types) + PolicyStatus |
| Grid Columns | 5 columns | 7 columns (includes dates, premium) |
| Date Handling | DOB only | effectiveDate + expirationDate |
| Numeric Field | None | premiumAmount (BigDecimal) |
| Relationship | None | Customer (via @ManyToOne) |
| Soft Delete | Via Status | Via Status |

---

## 10. TESTING CHECKLIST

- [ ] Search by policy number
- [ ] Filter by policy type
- [ ] Filter by status
- [ ] Combined search with multiple filters
- [ ] Pagination: Previous/Next buttons work
- [ ] Pagination: Correctly calculates total pages
- [ ] Row click navigates to detail page
- [ ] Empty state shows when no results
- [ ] Error message shows on API failure
- [ ] Clear button resets all filters
- [ ] Enter key triggers search
- [ ] Loading indicator visible during search
- [ ] Column sorting works
- [ ] Frozen column stays on scroll
- [ ] Responsive on mobile

---

## FILE CHECKLIST

Frontend to Create:
- [ ] PolicySearchView.java (230 lines, similar to CustomerSearchView)

Backend to Create:
- [ ] Policy.java (entity)
- [ ] PolicyResponse.java (DTO)
- [ ] PolicySearchCriteria.java (DTO)
- [ ] PolicyType.java (enum)
- [ ] PolicyStatus.java (enum)
- [ ] PolicyController.java (30 lines)
- [ ] PolicyService.java (40 lines)
- [ ] PolicyRepository.java (interface)

Backend to Update:
- [ ] MainLayout.java (already has policies link)
- [ ] Application configuration (if needed)

