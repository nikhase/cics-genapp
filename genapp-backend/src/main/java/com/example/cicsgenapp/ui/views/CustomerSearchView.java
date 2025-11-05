package com.example.cicsgenapp.ui.views;

import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.dto.PagedResponse;
import com.example.cicsgenapp.entity.Status;
import com.example.cicsgenapp.service.CustomerService;
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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * CustomerSearchView provides a UI for searching and listing customers.
 *
 * <p>Implements Story 3.4 - Customer Search & List Page with Vaadin Grid.
 * Allows users to search for customers by name, email, or ID, view results in a paginated grid,
 * and navigate to customer details.
 *
 * <p><b>Acceptance Criteria (Story 3.4):</b>
 * <ul>
 *   <li>Search form with query input and optional status filter
 *   <li>Search button and auto-search on Enter key
 *   <li>Loading indicator while fetching
 *   <li>Results displayed in Vaadin Grid with sortable columns: ID, Name, Email, Phone, Status
 *   <li>Pagination with Previous/Next buttons and page indicator
 *   <li>Row click navigation to customer detail page (Story 3.5)
 *   <li>Empty state message if no search executed yet
 *   <li>Empty state message "No customers found" if search returns no results
 *   <li>Error message with retry button if search fails
 *   <li>Performance: results load in < 2 seconds
 *   <li>Responsive layout (full-width grid on mobile)
 *   <li>Accessibility: proper labels, keyboard navigation
 * </ul>
 *
 * @author Development Team
 * @version 1.0.0 (Story 3.4 Implementation)
 */
@Route(value = "customers/search", layout = com.example.cicsgenapp.ui.layouts.MainLayout.class)
@PageTitle("Search Customers - CICS GenApp")
public class CustomerSearchView extends VerticalLayout {

  private final CustomerService customerService;

  // UI Components
  private TextField searchInput;
  private ComboBox<Status> statusFilter;
  private Button searchButton;
  private Button clearButton;
  private Grid<CustomerResponse> grid;
  private ProgressBar loadingIndicator;
  private Div emptyStateDiv;
  private Div errorDiv;
  private Div paginationDiv;

  // Search state
  private int currentPage = 0;
  private int pageSize = 20;
  private String lastQuery = "";
  private Status lastStatus = null;

  @Autowired
  public CustomerSearchView(CustomerService customerService) {
    this.customerService = customerService;
    initializeView();
  }

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

  private void executeSearch() {
    showLoading(true);
    hideErrorMessage();

    try {
      // Build search criteria for the service
      com.example.cicsgenapp.dto.SearchCriteria criteria =
          new com.example.cicsgenapp.dto.SearchCriteria(
              lastQuery.isEmpty() ? null : lastQuery,
              lastStatus,
              pageSize,
              currentPage * pageSize,
              "lastName",
              "ASC"
          );

      // Call backend service which handles the REST API call
      PagedResponse<CustomerResponse> response = customerService.searchCustomers(criteria);

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

  private void displayResults(PagedResponse<CustomerResponse> response) {
    grid.setItems(response.getData());
    grid.setVisible(true);
    emptyStateDiv.setVisible(false);

    // Setup pagination
    updatePaginationControls(response);
  }

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
    statusFilter.setValue(null);
    currentPage = 0;
    lastQuery = "";
    lastStatus = null;
    grid.setVisible(false);
    paginationDiv.setVisible(false);
    hideErrorMessage();
    showEmptyState("Enter a search query to find customers");
  }
}
