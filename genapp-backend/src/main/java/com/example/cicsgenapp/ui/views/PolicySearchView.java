package com.example.cicsgenapp.ui.views;

import com.example.cicsgenapp.dto.PagedResponse;
import com.example.cicsgenapp.dto.PolicyResponse;
import com.example.cicsgenapp.entity.PolicyStatus;
import com.example.cicsgenapp.entity.PolicyType;
import com.example.cicsgenapp.service.PolicyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PolicySearchView provides a UI for searching and listing policies.
 *
 * <p>Implements Story 3.7 - Policy List Page with Vaadin Grid.
 * Allows users to search for policies by number or customer name, filter by type and status,
 * view results in a paginated grid, and navigate to policy details.
 *
 * <p><b>Acceptance Criteria (Story 3.7):</b>
 * <ul>
 *   <li>Search form with query input and optional type/status filters
 *   <li>Search button and auto-search on Enter key
 *   <li>Loading indicator while fetching
 *   <li>Results displayed in Vaadin Grid with sortable columns: ID, Customer, Type, Status,
 *       Premium, Created
 *   <li>Pagination with Previous/Next buttons and page indicator
 *   <li>Row click navigation to policy detail page (Story 3.8)
 *   <li>Empty state message if no search executed yet
 *   <li>Empty state message "No policies found" if search returns no results
 *   <li>Error message with retry button if search fails
 *   <li>Performance: results load in < 2 seconds
 *   <li>Responsive layout (full-width grid on mobile)
 *   <li>Accessibility: proper labels, keyboard navigation
 * </ul>
 *
 * @author Development Team
 * @version 1.0.0 (Story 3.7 Implementation)
 */
@Route(value = "/policies", layout = com.example.cicsgenapp.ui.layouts.MainLayout.class)
@PageTitle("Search Policies - CICS GenApp")
public class PolicySearchView extends VerticalLayout {

  private final PolicyService policyService;

  // UI Components
  private TextField searchInput;
  private ComboBox<PolicyType> typeFilter;
  private ComboBox<PolicyStatus> statusFilter;
  private Button searchButton;
  private Button clearButton;
  private Grid<PolicyResponse> grid;
  private ProgressBar loadingIndicator;
  private Div emptyStateDiv;
  private Div errorDiv;
  private Div paginationDiv;

  // Search state
  private int currentPage = 0;
  private int pageSize = 20;
  private String lastQuery = "";
  private PolicyType lastType = null;
  private PolicyStatus lastStatus = null;

  @Autowired
  public PolicySearchView(PolicyService policyService) {
    this.policyService = policyService;
    initializeView();
  }

  private void initializeView() {
    setWidthFull();
    setPadding(true);
    setSpacing(true);
    addClassNames(LumoUtility.Padding.LARGE);

    // Title
    H2 title = new H2("Search Policies");
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
    grid = new Grid<>(PolicyResponse.class, false);
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

    showEmptyState("Enter search criteria to find policies");
  }

  private HorizontalLayout createSearchForm() {
    HorizontalLayout form = new HorizontalLayout();
    form.setWidthFull();
    form.setSpacing(true);
    form.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.FlexWrap.WRAP);

    // Search input
    searchInput = new TextField();
    searchInput.setPlaceholder("Search by policy number or customer");
    searchInput.setWidth("250px");
    searchInput.setAutofocus(true);
    searchInput.addKeyDownListener(event -> {
      if (event.getKey().equals(com.vaadin.flow.component.Key.ENTER)) {
        performSearch();
      }
    });

    // Type filter
    typeFilter = new ComboBox<>();
    typeFilter.setItems(PolicyType.values());
    typeFilter.setItemLabelGenerator(PolicyType::getDisplayName);
    typeFilter.setPlaceholder("Filter by type (optional)");
    typeFilter.setWidth("180px");
    typeFilter.setClearButtonVisible(true);

    // Status filter
    statusFilter = new ComboBox<>();
    statusFilter.setItems(PolicyStatus.values());
    statusFilter.setItemLabelGenerator(PolicyStatus::getDisplayName);
    statusFilter.setPlaceholder("Filter by status (optional)");
    statusFilter.setWidth("180px");
    statusFilter.setClearButtonVisible(true);

    // Search button
    searchButton = new Button("Search");
    searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    searchButton.setIcon(VaadinIcon.SEARCH.create());
    searchButton.addClickListener(event -> performSearch());

    // Clear button
    clearButton = new Button("Clear");
    clearButton.addClickListener(event -> clearSearch());

    form.add(searchInput, typeFilter, statusFilter, searchButton, clearButton);
    form.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

    return form;
  }

  private void configureGrid() {
    grid.setWidthFull();
    grid.setHeight("400px");
    grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    grid.addClassNames(LumoUtility.Border.ALL);

    // Define columns
    grid.addColumn(PolicyResponse::getPolicyNumber)
        .setHeader("Policy #")
        .setWidth("120px")
        .setFrozen(true);

    grid.addColumn(PolicyResponse::getCustomerName)
        .setHeader("Customer")
        .setWidth("180px")
        .setSortable(true);

    grid.addColumn(policy -> policy.getPolicyType().getAbbreviation() +
                           " (" + policy.getPolicyType().getDisplayName() + ")")
        .setHeader("Type")
        .setWidth("130px")
        .setSortable(true);

    grid.addColumn(policy -> policy.getStatus().getDisplayName())
        .setHeader("Status")
        .setWidth("100px")
        .setSortable(true);

    grid.addColumn(policy -> formatCurrency(policy.getPremiumAmount()))
        .setHeader("Premium")
        .setWidth("120px");

    grid.addColumn(PolicyResponse::getEffectiveDate)
        .setHeader("Effective")
        .setWidth("110px");

    // Row click listener to navigate to policy detail
    grid.asSingleSelect().addValueChangeListener(event -> {
      if (event.getValue() != null) {
        getUI().ifPresent(ui -> ui.navigate("/policies/" + event.getValue().getPolicyId()));
      }
    });
  }

  private String formatCurrency(java.math.BigDecimal amount) {
    if (amount == null) {
      return "-";
    }
    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
    return format.format(amount);
  }

  private void performSearch() {
    lastQuery = searchInput.getValue().trim();
    lastType = typeFilter.getValue();
    lastStatus = statusFilter.getValue();

    if (lastQuery.isEmpty() && lastType == null && lastStatus == null) {
      showErrorMessage("Please enter a search query or select a filter");
      return;
    }

    currentPage = 0;
    executeSearch();
  }

  private void executeSearch() {
    showLoading(true);
    hideErrorMessage();

    try {
      // Call backend API
      PagedResponse<PolicyResponse> response = policyService.searchPolicies(
          lastQuery.isEmpty() ? null : lastQuery,
          lastType,
          lastStatus,
          pageSize,
          currentPage,
          "policyNumber",
          "ASC"
      );

      if (response.getData().isEmpty()) {
        showEmptyState("No policies found. Try different search criteria.");
      } else {
        displayResults(response);
      }
    } catch (Exception e) {
      showErrorMessage("Search failed: " + e.getMessage());
    } finally {
      showLoading(false);
    }
  }

  private void displayResults(PagedResponse<PolicyResponse> response) {
    grid.setItems(response.getData());
    grid.setVisible(true);
    emptyStateDiv.setVisible(false);

    // Setup pagination
    updatePaginationControls(response);
  }

  private void updatePaginationControls(PagedResponse<PolicyResponse> response) {
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
    pageIndicator.setText("Page " + (currentPage + 1) + " of " + (totalPages > 0 ? totalPages : 1) +
                         " (" + totalItems + " total)");
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

  private void showEmptyState(String message) {
    emptyStateDiv.removeAll();
    emptyStateDiv.setText(message);
    emptyStateDiv.setVisible(true);
    grid.setVisible(false);
    paginationDiv.setVisible(false);
  }

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

  private void hideErrorMessage() {
    errorDiv.setVisible(false);
  }

  private void showLoading(boolean show) {
    loadingIndicator.setVisible(show);
  }

  private void clearSearch() {
    searchInput.setValue("");
    typeFilter.setValue(null);
    statusFilter.setValue(null);
    currentPage = 0;
    lastQuery = "";
    lastType = null;
    lastStatus = null;
    grid.setVisible(false);
    paginationDiv.setVisible(false);
    hideErrorMessage();
    showEmptyState("Enter search criteria to find policies");
  }
}
