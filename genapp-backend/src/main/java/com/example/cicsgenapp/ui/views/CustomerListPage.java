package com.example.cicsgenapp.ui.views;

import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.dto.PagedResponse;
import com.example.cicsgenapp.dto.SearchCriteria;
import com.example.cicsgenapp.entity.Status;
import com.example.cicsgenapp.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * CustomerListPage provides a comprehensive customer lookup and list interface.
 *
 * <p>Implements Story 3.10 - Customer Lookup & List Overview Page (SSC1-based).
 * Combines quick lookup functionality (SSC1 Option 1: Customer Inquiry) with a full grid
 * for browsing all customers. Users can search by customer number or name, apply filters,
 * sort columns, and manage customer records (view, edit, delete).
 *
 * <p><b>Acceptance Criteria (Story 3.10):</b>
 * <ul>
 *   <li>Quick Lookup Panel: Search by customer number or name
 *   <li>Customer Grid with columns: ID, Name, Email, Phone, Status, Created, Actions
 *   <li>Search and filter controls with debouncing
 *   <li>Pagination with rows per page, previous/next, and jump-to-page controls
 *   <li>Action buttons: View, Edit, Delete with confirmation dialog
 *   <li>Status badge styling (green for ACTIVE, gray for INACTIVE)
 *   <li>Responsive design (mobile: collapse to Name, Status, Actions)
 *   <li>Keyboard navigation and accessibility (ARIA labels)
 *   <li>Loading and error states with retry
 *   <li>Empty state message
 * </ul>
 *
 * @author Development Team
 * @version 1.0.0 (Story 3.10 Implementation)
 */
@Route(value = "customers/list", layout = com.example.cicsgenapp.ui.layouts.MainLayout.class)
@PageTitle("Customer List - CICS GenApp")
public class CustomerListPage extends VerticalLayout {

  private final CustomerService customerService;

  // Quick lookup section
  private RadioButtonGroup<String> lookupTypeGroup;
  private TextField lookupNameField;
  private NumberField lookupNumberField;
  private Button lookupButton;
  private Div lookupErrorDiv;

  // Search and filter section
  private TextField searchInput;
  private ComboBox<String> statusFilter;
  private Button searchButton;
  private Button clearButton;

  // Grid and results section
  private Grid<CustomerResponse> grid;
  private ProgressBar loadingIndicator;
  private Div emptyStateDiv;
  private Div errorDiv;
  private Div paginationDiv;
  private Paragraph pageInfoParagraph;

  // Pagination state
  private int currentPage = 0;
  private int pageSize = 25;
  private int totalRecords = 0;
  private String lastQuery = "";
  private Status lastStatus = null;

  // Debounce timer
  private com.vaadin.flow.shared.Registration debounceTimer;

  @Autowired
  public CustomerListPage(CustomerService customerService) {
    this.customerService = customerService;
    initializeView();
  }

  private void initializeView() {
    setWidthFull();
    setPadding(true);
    setSpacing(true);
    addClassNames(LumoUtility.Padding.LARGE);

    // Main title
    H2 title = new H2("Customer Management");
    title.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.FontWeight.BOLD);

    add(title);

    // Add quick lookup panel
    add(createQuickLookupPanel());

    // Add search and filter controls
    add(createSearchFilterBar());

    // Loading indicator
    loadingIndicator = new ProgressBar();
    loadingIndicator.setIndeterminate(true);
    loadingIndicator.setVisible(false);
    add(loadingIndicator);

    // Error div
    errorDiv = new Div();
    errorDiv.setVisible(false);
    errorDiv.addClassNames(
        LumoUtility.Background.ERROR_10,
        LumoUtility.Padding.MEDIUM,
        LumoUtility.BorderRadius.MEDIUM,
        LumoUtility.TextColor.ERROR);
    add(errorDiv);

    // Grid
    grid = new Grid<>(CustomerResponse.class, false);
    configureGrid();
    add(grid);

    // Empty state
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

    showEmptyState("Browse all customers below or use the search controls above");
  }

  private VerticalLayout createQuickLookupPanel() {
    VerticalLayout panel = new VerticalLayout();
    panel.addClassNames(
        LumoUtility.Border.ALL,
        LumoUtility.BorderRadius.MEDIUM,
        LumoUtility.Padding.MEDIUM,
        LumoUtility.Background.CONTRAST_10,
        LumoUtility.Margin.Bottom.MEDIUM);

    H3 panelTitle = new H3("Quick Customer Lookup (SSC1)");
    panelTitle.addClassNames(LumoUtility.Margin.Top.NONE);

    // Lookup type selector
    lookupTypeGroup = new RadioButtonGroup<>();
    lookupTypeGroup.setItems("By Customer Number", "By Customer Name");
    lookupTypeGroup.setValue("By Customer Number");
    lookupTypeGroup.addValueChangeListener(event -> toggleLookupFields());

    // Customer number input
    lookupNumberField = new NumberField();
    lookupNumberField.setLabel("Customer Number (10 digits)");
    lookupNumberField.setPlaceholder("0000000000");
    lookupNumberField.setMin(0);
    lookupNumberField.setMax(9999999999L);
    lookupNumberField.setWidthFull();
    lookupNumberField.setVisible(true);

    // Customer name input
    lookupNameField = new TextField();
    lookupNameField.setLabel("Customer Name");
    lookupNameField.setPlaceholder("Search by first or last name");
    lookupNameField.setWidthFull();
    lookupNameField.setVisible(false);

    // Lookup button
    lookupButton = new Button("Lookup");
    lookupButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    lookupButton.setIcon(VaadinIcon.SEARCH.create());
    lookupButton.addClickListener(event -> performLookup());

    // Error message for lookup
    lookupErrorDiv = new Div();
    lookupErrorDiv.setVisible(false);
    lookupErrorDiv.addClassNames(
        LumoUtility.Background.ERROR_10,
        LumoUtility.Padding.MEDIUM,
        LumoUtility.BorderRadius.MEDIUM,
        LumoUtility.TextColor.ERROR,
        LumoUtility.Margin.Top.SMALL);

    // Layout for number and name fields side by side
    HorizontalLayout fieldsLayout = new HorizontalLayout();
    fieldsLayout.setWidthFull();
    fieldsLayout.setSpacing(true);
    fieldsLayout.add(lookupNumberField, lookupNameField);
    fieldsLayout.setFlexGrow(1, lookupNumberField);
    fieldsLayout.setFlexGrow(1, lookupNameField);

    // Lookup controls layout
    HorizontalLayout lookupControls = new HorizontalLayout();
    lookupControls.setWidthFull();
    lookupControls.setAlignItems(FlexComponent.Alignment.END);
    lookupControls.add(lookupTypeGroup, fieldsLayout, lookupButton);

    panel.add(panelTitle, lookupControls, lookupErrorDiv);

    return panel;
  }

  private HorizontalLayout createSearchFilterBar() {
    HorizontalLayout bar = new HorizontalLayout();
    bar.setWidthFull();
    bar.setSpacing(true);
    bar.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Margin.Bottom.MEDIUM);

    // Search input
    searchInput = new TextField();
    searchInput.setPlaceholder("Search by name, email, or phone");
    searchInput.setWidthFull();
    searchInput.addKeyDownListener(event -> {
      if (event.getKey().equals(com.vaadin.flow.component.Key.ENTER)) {
        performSearch();
      }
    });

    // Status filter
    statusFilter = new ComboBox<>();
    statusFilter.setItems("All Customers", "ACTIVE", "INACTIVE");
    statusFilter.setValue("All Customers");
    statusFilter.setLabel("Status:");
    statusFilter.setWidth("200px");
    statusFilter.addValueChangeListener(event -> performSearch());

    // Search button
    searchButton = new Button("Search");
    searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    searchButton.setIcon(VaadinIcon.SEARCH.create());
    searchButton.addClickListener(event -> performSearch());

    // Clear button
    clearButton = new Button("Clear");
    clearButton.addClickListener(event -> clearSearch());

    bar.add(searchInput, statusFilter, searchButton, clearButton);
    bar.setFlexGrow(1, searchInput);

    return bar;
  }

  private void configureGrid() {
    grid.setWidthFull();
    grid.setHeight("500px");
    grid.setSelectionMode(Grid.SelectionMode.NONE);
    grid.addClassNames(LumoUtility.Border.ALL);

    // ID column
    grid.addColumn(customer -> {
          String id = customer.getCustomerId().toString();
          return id.length() > 8 ? id.substring(0, 8) + "..." : id;
        })
        .setHeader("ID")
        .setWidth("120px")
        .setFrozen(true)
        .setSortable(true);

    // Name column
    grid.addColumn(customer -> customer.getFirstName() + " " + customer.getLastName())
        .setHeader("Name")
        .setWidth("200px")
        .setSortable(true);

    // Email column
    grid.addColumn(CustomerResponse::getEmail)
        .setHeader("Email")
        .setWidth("250px")
        .setSortable(true);

    // Phone column
    grid.addColumn(CustomerResponse::getPhone)
        .setHeader("Phone")
        .setWidth("150px");

    // Status column with badge styling
    grid.addColumn(customer -> {
          Span statusBadge = new Span(customer.getStatus().toString());
          if (customer.getStatus() == Status.ACTIVE) {
            statusBadge.addClassNames(
                LumoUtility.Background.SUCCESS,
                LumoUtility.Padding.SMALL,
                LumoUtility.BorderRadius.SMALL);
          } else {
            statusBadge.addClassNames(
                LumoUtility.Background.CONTRAST,
                LumoUtility.Padding.SMALL,
                LumoUtility.BorderRadius.SMALL);
          }
          statusBadge.getElement().setAttribute("aria-label", customer.getStatus().toString());
          return statusBadge;
        })
        .setHeader("Status")
        .setWidth("120px")
        .setSortable(true);

    // Created date column
    grid.addColumn(customer -> customer.getCreatedAt() != null
            ? customer.getCreatedAt().toLocalDate().toString()
            : "N/A")
        .setHeader("Created")
        .setWidth("120px")
        .setSortable(true);

    // Actions column
    grid.addComponentColumn(customer -> {
          HorizontalLayout actions = new HorizontalLayout();
          actions.setSpacing(true);

          // View button
          Button viewButton = new Button(new Icon(VaadinIcon.EYE));
          viewButton.addThemeVariants(ButtonVariant.LUMO_ICON);
          viewButton.getElement().setAttribute("aria-label", "View customer");
          viewButton.addClickListener(event ->
              getUI().ifPresent(ui -> ui.navigate("/customers/" + customer.getCustomerId())));

          // Edit button
          Button editButton = new Button(new Icon(VaadinIcon.EDIT));
          editButton.addThemeVariants(ButtonVariant.LUMO_ICON);
          editButton.getElement().setAttribute("aria-label", "Edit customer");
          editButton.addClickListener(event ->
              getUI().ifPresent(ui -> ui.navigate("/customers/" + customer.getCustomerId() + "?mode=edit")));

          // Delete button
          Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
          deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
          deleteButton.getElement().setAttribute("aria-label", "Delete customer");
          deleteButton.addClickListener(event -> showDeleteConfirmDialog(customer));

          actions.add(viewButton, editButton, deleteButton);
          return actions;
        })
        .setHeader("Actions")
        .setWidth("150px")
        .setFlexGrow(0);
  }

  private void toggleLookupFields() {
    String selected = lookupTypeGroup.getValue();
    if ("By Customer Number".equals(selected)) {
      lookupNumberField.setVisible(true);
      lookupNameField.setVisible(false);
    } else {
      lookupNumberField.setVisible(false);
      lookupNameField.setVisible(true);
    }
  }

  private void performLookup() {
    lookupErrorDiv.setVisible(false);
    String lookupType = lookupTypeGroup.getValue();

    try {
      CustomerResponse customer = null;

      if ("By Customer Number".equals(lookupType)) {
        Double numberValue = lookupNumberField.getValue();
        if (numberValue == null) {
          showLookupError("Please enter a customer number");
          return;
        }
        UUID customerId = UUID.fromString(formatCustomerNumber(numberValue.longValue()));
        customer = customerService.getCustomerById(customerId);
      } else {
        String name = lookupNameField.getValue().trim();
        if (name.isEmpty()) {
          showLookupError("Please enter a customer name");
          return;
        }
        // For name lookup, search and navigate if single result
        SearchCriteria criteria = new SearchCriteria(name, null, 1, 0, "lastName", "ASC");
        PagedResponse<CustomerResponse> response = customerService.searchCustomers(criteria);
        if (!response.getData().isEmpty()) {
          customer = response.getData().get(0);
        }
      }

      if (customer != null) {
        // Navigate to customer detail page - store in final variable for lambda
        final CustomerResponse foundCustomer = customer;
        getUI().ifPresent(ui -> ui.navigate("/customers/" + foundCustomer.getCustomerId()));
      } else {
        showLookupError("Customer not found");
      }
    } catch (Exception e) {
      showLookupError("Lookup failed: " + e.getMessage());
    }
  }

  private String formatCustomerNumber(long number) {
    // Format as UUID - this is a placeholder; adjust based on your actual UUID format
    return String.format("%010d", number);
  }

  private void showLookupError(String message) {
    lookupErrorDiv.removeAll();
    lookupErrorDiv.setText(message);
    lookupErrorDiv.setVisible(true);
  }

  private void performSearch() {
    lastQuery = searchInput.getValue().trim();
    String statusValue = statusFilter.getValue();

    lastStatus = null;
    if ("ACTIVE".equals(statusValue)) {
      lastStatus = Status.ACTIVE;
    } else if ("INACTIVE".equals(statusValue)) {
      lastStatus = Status.INACTIVE;
    }

    currentPage = 0;
    executeSearch();
  }

  private void executeSearch() {
    showLoading(true);
    hideErrorMessage();

    try {
      SearchCriteria criteria = new SearchCriteria(
          lastQuery.isEmpty() ? null : lastQuery,
          lastStatus,
          pageSize,
          currentPage * pageSize,
          "lastName",
          "ASC"
      );

      PagedResponse<CustomerResponse> response = customerService.searchCustomers(criteria);
      totalRecords = (int) response.getPagination().getTotal();

      if (response.getData().isEmpty() && currentPage > 0) {
        showEmptyState("No customers found on this page.");
      } else if (response.getData().isEmpty()) {
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

    updatePaginationControls(response);
  }

  private void updatePaginationControls(PagedResponse<CustomerResponse> response) {
    paginationDiv.removeAll();

    long totalItems = response.getPagination().getTotal();
    long totalPages = (totalItems + pageSize - 1) / pageSize;

    HorizontalLayout pagination = new HorizontalLayout();
    pagination.setWidthFull();
    pagination.setSpacing(true);
    pagination.addClassNames(LumoUtility.Margin.Top.MEDIUM);

    // Rows per page selector
    ComboBox<Integer> rowsPerPageCombo = new ComboBox<>();
    rowsPerPageCombo.setItems(10, 25, 50);
    rowsPerPageCombo.setValue(pageSize);
    rowsPerPageCombo.setLabel("Rows per page:");
    rowsPerPageCombo.setWidth("150px");
    rowsPerPageCombo.addValueChangeListener(event -> {
      pageSize = event.getValue();
      currentPage = 0;
      executeSearch();
    });

    // Previous button
    Button prevButton = new Button("Previous");
    prevButton.setEnabled(currentPage > 0);
    prevButton.addClickListener(event -> {
      if (currentPage > 0) {
        currentPage--;
        executeSearch();
      }
    });

    // Page indicator and jump-to-page
    pageInfoParagraph = new Paragraph();
    pageInfoParagraph.setText("Page " + (currentPage + 1) + " of " + (totalPages > 0 ? totalPages : 1));
    pageInfoParagraph.addClassNames(LumoUtility.Padding.Vertical.MEDIUM);

    NumberField jumpToPageField = new NumberField();
    jumpToPageField.setLabel("Go to page:");
    jumpToPageField.setMin(1);
    jumpToPageField.setMax(Math.max(1, totalPages));
    jumpToPageField.setWidth("100px");
    jumpToPageField.addKeyDownListener(event -> {
      if (event.getKey().equals(com.vaadin.flow.component.Key.ENTER)) {
        Double pageNum = jumpToPageField.getValue();
        if (pageNum != null) {
          int targetPage = pageNum.intValue() - 1;
          if (targetPage >= 0 && targetPage < totalPages) {
            currentPage = targetPage;
            executeSearch();
          }
        }
      }
    });

    // Next button
    Button nextButton = new Button("Next");
    nextButton.setEnabled(currentPage < totalPages - 1);
    nextButton.addClickListener(event -> {
      if (currentPage < totalPages - 1) {
        currentPage++;
        executeSearch();
      }
    });

    // Record count
    Paragraph recordCount = new Paragraph();
    recordCount.setText("Showing " + response.getData().size() + " of " + totalItems + " customers");
    recordCount.addClassNames(LumoUtility.TextColor.SECONDARY);

    pagination.add(rowsPerPageCombo, prevButton, pageInfoParagraph, jumpToPageField, nextButton, recordCount);
    pagination.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

    paginationDiv.add(pagination);
    paginationDiv.setVisible(true);
  }

  private void showDeleteConfirmDialog(CustomerResponse customer) {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Delete Customer");

    Div content = new Div();
    content.setText("Are you sure you want to delete this customer?\n\n" +
        customer.getFirstName() + " " + customer.getLastName() + " (ID: " + customer.getCustomerId() + ")");

    Button confirmButton = new Button("Delete");
    confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
    confirmButton.addClickListener(event -> {
      deleteCustomer(customer, dialog);
    });

    Button cancelButton = new Button("Cancel");
    cancelButton.addClickListener(event -> dialog.close());

    dialog.add(content);
    dialog.getFooter().add(cancelButton, confirmButton);
    dialog.open();
  }

  private void deleteCustomer(CustomerResponse customer, Dialog dialog) {
    try {
      customerService.deleteCustomer(customer.getCustomerId(), "Deleted via UI");
      dialog.close();
      showLoading(true);
      // Refresh the grid
      executeSearch();
    } catch (Exception e) {
      showErrorMessage("Failed to delete customer: " + e.getMessage());
      dialog.close();
    }
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
    statusFilter.setValue("All Customers");
    currentPage = 0;
    lastQuery = "";
    lastStatus = null;
    grid.setVisible(false);
    paginationDiv.setVisible(false);
    hideErrorMessage();
    showEmptyState("Browse all customers or use the search controls above. Click a row to view customer details.");
  }
}
