package com.example.cicsgenapp.ui.views;

import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.dto.PagedResponse;
import com.example.cicsgenapp.dto.SearchCriteria;
import com.example.cicsgenapp.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
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
 * CustomerListPage displays a searchable, filterable list of all customers.
 *
 * <p>Implements Story 3.10 - Customer List & Browse Page.
 * Provides:
 * - Search functionality (by name, email, or phone)
 * - Status filtering (ACTIVE, INACTIVE, All)
 * - Paginated grid display
 * - Action buttons (View, Edit, Delete)
 *
 * @author Development Team
 * @version 2.0.0 (Story 3.10 - Customer List Page)
 */
@Route(value = "customers/list", layout = com.example.cicsgenapp.ui.layouts.MainLayout.class)
@PageTitle("Customer List - CICS GenApp")
public class CustomerListPage extends VerticalLayout {

  private final CustomerService customerService;

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

  @Autowired
  public CustomerListPage(CustomerService customerService) {
    this.customerService = customerService;
    initializeView();
  }

  private void initializeView() {
    addClassNames(
        LumoUtility.Display.FLEX,
        LumoUtility.FlexDirection.COLUMN,
        LumoUtility.Padding.MEDIUM,
        LumoUtility.Gap.MEDIUM);
    setWidthFull();
    setHeightFull();

    // Title
    H2 title = new H2("Customer List");
    title.addClassNames(LumoUtility.Margin.Top.NONE);
    add(title);

    // Search and Filter Controls
    createSearchFilterBar();

    // Grid
    createGrid();

    // Pagination
    createPaginationControls();

    // Load initial data
    loadCustomers("", "");
  }

  private void createSearchFilterBar() {
    HorizontalLayout searchBar = new HorizontalLayout();
    searchBar.setWidthFull();
    searchBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
    searchBar.addClassNames(LumoUtility.Gap.MEDIUM);

    searchInput = new TextField();
    searchInput.setPlaceholder("Search by name, email, or phone");
    searchInput.setWidthFull();
    searchBar.setFlexGrow(1, searchInput);

    statusFilter = new ComboBox<>();
    statusFilter.setItems("All Customers", "ACTIVE", "INACTIVE");
    statusFilter.setValue("All Customers");
    statusFilter.setLabel("Status");
    statusFilter.setWidth("150px");

    searchButton = new Button("Search");
    searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    searchButton.addClickListener(e -> performSearch());

    clearButton = new Button("Clear");
    clearButton.addClickListener(e -> clearSearch());

    searchBar.add(searchInput, statusFilter, searchButton, clearButton);
    add(searchBar);
  }

  private void performSearch() {
    String query = searchInput.getValue().trim();
    String status = statusFilter.getValue();
    String statusFilter = "All Customers".equals(status) ? "" : status;
    currentPage = 0;
    loadCustomers(query, statusFilter);
  }

  private void clearSearch() {
    searchInput.clear();
    statusFilter.setValue("All Customers");
    currentPage = 0;
    loadCustomers("", "");
  }

  private void createGrid() {
    grid = new Grid<>(CustomerResponse.class, false);
    grid.setWidthFull();
    grid.setHeight("500px");

    grid.addColumn(c -> c.getCustomerId().toString().substring(0, Math.min(8, c.getCustomerId().toString().length())))
        .setHeader("ID")
        .setSortable(true)
        .setWidth("120px");

    grid.addColumn(c -> c.getFirstName() + " " + c.getLastName())
        .setHeader("Name")
        .setSortable(true)
        .setComparator((c1, c2) ->
            (c1.getLastName() + c1.getFirstName())
                .compareTo(c2.getLastName() + c2.getFirstName()));

    grid.addColumn(CustomerResponse::getEmail)
        .setHeader("Email")
        .setSortable(true);

    grid.addColumn(CustomerResponse::getPhone)
        .setHeader("Phone")
        .setWidth("130px");

    grid.addColumn(c -> {
          Span badge = new Span(c.getStatus().toString());
          badge.addClassNames(
              LumoUtility.Padding.Horizontal.SMALL,
              LumoUtility.Padding.Vertical.XSMALL,
              LumoUtility.BorderRadius.SMALL,
              LumoUtility.FontSize.SMALL);
          if ("ACTIVE".equals(c.getStatus().toString())) {
            badge.addClassNames(LumoUtility.Background.SUCCESS, LumoUtility.TextColor.SUCCESS_CONTRAST);
          } else {
            badge.addClassNames(LumoUtility.Background.CONTRAST_20, LumoUtility.TextColor.BODY);
          }
          return badge;
        })
        .setHeader("Status")
        .setWidth("100px");

    grid.addColumn(c -> c.getCreatedAt().toString().split("T")[0])
        .setHeader("Created")
        .setWidth("120px");

    // Actions column
    grid.addComponentColumn(customer -> {
      HorizontalLayout actions = new HorizontalLayout();
      actions.setPadding(false);
      actions.setSpacing(true);

      Button viewBtn = new Button("View");
      viewBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
      viewBtn.addClickListener(e -> navigateToCustomer(customer.getCustomerId()));

      Button editBtn = new Button("Edit");
      editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
      editBtn.addClickListener(e -> navigateToEditCustomer(customer.getCustomerId()));

      Button deleteBtn = new Button("Delete");
      deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
      deleteBtn.addClickListener(e -> confirmDelete(customer));

      actions.add(viewBtn, editBtn, deleteBtn);
      return actions;
    })
        .setHeader("Actions")
        .setWidth("200px");

    loadingIndicator = new ProgressBar();
    loadingIndicator.setVisible(false);

    emptyStateDiv = new Div();
    emptyStateDiv.addClassNames(
        LumoUtility.Padding.LARGE,
        LumoUtility.TextAlignment.CENTER);
    emptyStateDiv.setVisible(false);
    Paragraph emptyMsg = new Paragraph("No customers found. Create your first customer.");
    emptyStateDiv.add(emptyMsg);

    errorDiv = new Div();
    errorDiv.addClassNames(
        LumoUtility.Padding.MEDIUM,
        LumoUtility.Background.ERROR_10,
        LumoUtility.BorderRadius.MEDIUM,
        LumoUtility.TextColor.ERROR);
    errorDiv.setVisible(false);

    VerticalLayout gridWrapper = new VerticalLayout();
    gridWrapper.setSpacing(false);
    gridWrapper.setPadding(false);
    gridWrapper.add(loadingIndicator, errorDiv, grid, emptyStateDiv);
    gridWrapper.setFlexGrow(1, grid);

    add(gridWrapper);
    setFlexGrow(1, gridWrapper);
  }

  private void createPaginationControls() {
    HorizontalLayout paginationBar = new HorizontalLayout();
    paginationBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    paginationBar.setWidthFull();
    paginationBar.addClassNames(LumoUtility.Gap.MEDIUM);

    ComboBox<Integer> pageSize = new ComboBox<>();
    pageSize.setItems(10, 25, 50);
    pageSize.setValue(25);
    pageSize.setLabel("Rows per page");
    pageSize.setWidth("120px");
    pageSize.addValueChangeListener(e -> {
      this.pageSize = e.getValue();
      currentPage = 0;
      loadCustomers(lastQuery, "");
    });

    Button prevBtn = new Button("Previous");
    prevBtn.addClickListener(e -> {
      if (currentPage > 0) {
        currentPage--;
        loadCustomers(lastQuery, "");
      }
    });

    Button nextBtn = new Button("Next");
    nextBtn.addClickListener(e -> {
      if ((currentPage + 1) * this.pageSize < totalRecords) {
        currentPage++;
        loadCustomers(lastQuery, "");
      }
    });

    pageInfoParagraph = new Paragraph("Page " + (currentPage + 1));
    pageInfoParagraph.addClassNames(LumoUtility.FontSize.SMALL);

    paginationDiv = new Div();
    paginationDiv.add(pageSize, prevBtn, pageInfoParagraph, nextBtn);
    paginationDiv.addClassNames(LumoUtility.Display.FLEX, LumoUtility.Gap.MEDIUM, LumoUtility.AlignItems.CENTER);

    paginationBar.add(paginationDiv);
    add(paginationBar);
  }

  private void loadCustomers(String query, String status) {
    loadingIndicator.setVisible(true);
    errorDiv.setVisible(false);
    emptyStateDiv.setVisible(false);
    grid.setVisible(false);

    try {
      lastQuery = query;
      int offset = currentPage * pageSize;

      // Build SearchCriteria
      SearchCriteria criteria = new SearchCriteria();
      criteria.setQuery(query.isEmpty() ? null : query);
      if (!status.isEmpty() && !"All Customers".equals(status)) {
        criteria.setStatus(com.example.cicsgenapp.entity.Status.valueOf(status));
      }
      criteria.setLimit(pageSize);
      criteria.setOffset(offset);
      criteria.setSortBy("lastName");
      criteria.setSortOrder("ASC");

      PagedResponse<CustomerResponse> response = customerService.searchCustomers(criteria);

      if (response.getData().isEmpty()) {
        grid.setVisible(false);
        emptyStateDiv.setVisible(true);
      } else {
        grid.setVisible(true);
        grid.setItems(response.getData());
      }

      totalRecords = (int) response.getPagination().getTotal();
      updatePageInfo();

    } catch (Exception e) {
      grid.setVisible(false);
      errorDiv.removeAll();
      errorDiv.add(new Paragraph("Error loading customers: " + e.getMessage()));
      errorDiv.setVisible(true);
    } finally {
      loadingIndicator.setVisible(false);
    }
  }

  private void updatePageInfo() {
    int totalPages = (totalRecords + pageSize - 1) / pageSize;
    pageInfoParagraph.setText(
        "Page " + (currentPage + 1) + " of " + totalPages
            + " (Showing " + Math.min(pageSize, totalRecords - (currentPage * pageSize)) + " of " + totalRecords + ")");
  }

  private void navigateToCustomer(java.util.UUID customerId) {
    getUI().ifPresent(ui -> ui.navigate("/customers/" + customerId));
  }

  private void navigateToEditCustomer(java.util.UUID customerId) {
    getUI().ifPresent(ui -> ui.navigate("/customers/" + customerId + "?mode=edit"));
  }

  private void confirmDelete(CustomerResponse customer) {
    Dialog deleteDialog = new Dialog();
    deleteDialog.setHeaderTitle("Delete Customer");

    Paragraph message = new Paragraph(
        "Are you sure you want to delete " + customer.getFirstName() + " " + customer.getLastName() + "?");
    deleteDialog.add(message);

    Button confirmBtn = new Button("Delete", e -> {
      deleteDialog.close();
      deleteCustomer(customer.getCustomerId());
    });
    confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

    Button cancelBtn = new Button("Cancel", e -> deleteDialog.close());

    deleteDialog.getFooter().add(cancelBtn, confirmBtn);
    deleteDialog.open();
  }

  private void deleteCustomer(java.util.UUID customerId) {
    try {
      customerService.deleteCustomer(customerId, "Deleted via Customer List");
      loadCustomers(lastQuery, "");
    } catch (Exception e) {
      errorDiv.removeAll();
      errorDiv.add(new Paragraph("Error deleting customer: " + e.getMessage()));
      errorDiv.setVisible(true);
    }
  }
}
