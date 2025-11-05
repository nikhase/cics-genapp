package com.example.cicsgenapp.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.example.cicsgenapp.ui.components.BreadcrumbNavigation;

/**
 * CustomerMenuView is the legacy button-based navigation page retained temporarily.
 *
 * <p>Route: /customers/menu/legacy
 * Provides menu buttons for:
 * - View Customer List
 * - Search Customer
 * - Create New Customer
 */
@Route(value = "/customers/menu/legacy", layout = com.example.cicsgenapp.ui.layouts.MainLayout.class)
@PageTitle("Customer Menu (Legacy) - CICS GenApp")
public class CustomerMenuView extends VerticalLayout {

    /**
     * Default constructor for Vaadin routing.
     * Initializes the customer menu view with navigation buttons.
     */
    public CustomerMenuView() {
        initializeView();
    }

    private void initializeView() {
        setWidthFull();
        setHeightFull();
        setPadding(true);
        setSpacing(true);
        addClassNames(LumoUtility.Padding.LARGE);

        // Breadcrumbs
        BreadcrumbNavigation breadcrumbs = new BreadcrumbNavigation();
        breadcrumbs.setItems(
                new BreadcrumbNavigation.Item("Dashboard", ""),
                new BreadcrumbNavigation.Item("Customer Menu", null)
        );
        add(breadcrumbs);

        // Page title
        H1 pageTitle = new H1("Customer Management");
        pageTitle.addClassNames(LumoUtility.Margin.Bottom.LARGE, LumoUtility.FontWeight.BOLD);
        add(pageTitle);

        // Description
        Paragraph description = new Paragraph(
                "Welcome to Customer Management. Select an option below to manage customer records."
        );
        description.addClassNames(LumoUtility.Margin.Bottom.LARGE, LumoUtility.TextColor.SECONDARY);
        add(description);

        // Customer List button
        Button customerListBtn = new Button("Customer List", VaadinIcon.LIST.create());
        customerListBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        customerListBtn.setWidthFull();
        customerListBtn.setHeight("50px");
        customerListBtn.addClickListener(event ->
                getUI().ifPresent(ui -> ui.navigate("/customers/list"))
        );
        add(customerListBtn);

        // Search Customer button
        Button searchCustomerBtn = new Button("Search Customer", VaadinIcon.SEARCH.create());
        searchCustomerBtn.setWidthFull();
        searchCustomerBtn.setHeight("50px");
        searchCustomerBtn.addClickListener(event ->
                getUI().ifPresent(ui -> ui.navigate("/customers/search"))
        );
        add(searchCustomerBtn);

        // Create New Customer button
        Button createCustomerBtn = new Button("Create New Customer", VaadinIcon.PLUS.create());
        createCustomerBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        createCustomerBtn.setWidthFull();
        createCustomerBtn.setHeight("50px");
        createCustomerBtn.addClickListener(event ->
                getUI().ifPresent(ui -> ui.navigate("/customers/new"))
        );
        add(createCustomerBtn);
    }
}
