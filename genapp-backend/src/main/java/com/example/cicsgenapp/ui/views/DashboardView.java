package com.example.cicsgenapp.ui.views;

import com.example.cicsgenapp.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * DashboardView is the main dashboard page of CICS GenApp.
 *
 * <p>This is the landing page after successful authentication. It displays
 * a welcome message, quick action buttons for common tasks, and is the foundation
 * for navigation to all major features (customers, policies, etc.).
 *
 * <p><b>Acceptance Criteria (Story 3.3):</b>
 * <ul>
 *   <li>Main Layout Structure: AppLayout with header and sidebar (via MainLayout)
 *   <li>Sidebar Navigation: Dashboard, Customers, Policies, Admin menu items
 *   <li>Dashboard Content: Welcome message, quick action buttons
 *   <li>Layout Responsiveness: Works on desktop, tablet, and mobile
 *   <li>Theme Integration: Vaadin Lumo theme with professional styling
 *   <li>Navigation Integration: Sidebar and button links navigate correctly
 *   <li>User Context: Current username displayed in header
 * </ul>
 *
 * @author Development Team
 * @version 1.0.0
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard - CICS GenApp")
public class DashboardView extends VerticalLayout {

  public DashboardView() {
    setWidthFull();
    setPadding(true);
    setSpacing(true);
    addClassNames(LumoUtility.Padding.LARGE);

    // Get current user from Spring Security
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth != null ? auth.getName() : "User";

    // Welcome message with username
    H2 welcomeTitle = new H2("Welcome, " + username + "!");
    welcomeTitle.addClassNames(
        LumoUtility.FontSize.LARGE,
        LumoUtility.Margin.Bottom.SMALL,
        LumoUtility.FontWeight.BOLD);

    // Subtitle
    Div subtitle = new Div();
    subtitle.setText("CICS GenApp - Customer and Policy Management System");
    subtitle.addClassNames(
        LumoUtility.FontSize.MEDIUM,
        LumoUtility.TextColor.SECONDARY,
        LumoUtility.Margin.Bottom.LARGE);

    // Quick Actions Section
    H3 quickActionsTitle = new H3("Quick Actions");
    quickActionsTitle.addClassNames(
        LumoUtility.Margin.Top.LARGE,
        LumoUtility.Margin.Bottom.MEDIUM,
        LumoUtility.FontWeight.BOLD);

    // Quick action buttons grid (responsive)
    VerticalLayout buttonSection = new VerticalLayout();
    buttonSection.setWidth("100%");
    buttonSection.setSpacing(true);

    HorizontalLayout buttonsGrid = new HorizontalLayout();
    buttonsGrid.setSpacing(true);
    buttonsGrid.addClassNames(LumoUtility.Gap.MEDIUM);

    // "Create New Customer" button
    Button createCustomerBtn = createActionButton(
        "Create New Customer",
        VaadinIcon.PLUS.create(),
        ButtonVariant.LUMO_PRIMARY,
        () -> getUI().ifPresent(ui -> ui.navigate("/customers/create")));

    // "Search Customers" button
    Button searchCustomersBtn = createActionButton(
        "Search Customers",
        VaadinIcon.SEARCH.create(),
        ButtonVariant.LUMO_TERTIARY,
        () -> getUI().ifPresent(ui -> ui.navigate("/customers")));

    // "Search Policies" button
    Button searchPoliciesBtn = createActionButton(
        "Search Policies",
        VaadinIcon.SEARCH.create(),
        ButtonVariant.LUMO_TERTIARY,
        () -> getUI().ifPresent(ui -> ui.navigate("/policies")));

    buttonsGrid.add(createCustomerBtn, searchCustomersBtn, searchPoliciesBtn);
    buttonSection.add(buttonsGrid);

    // Optional: Summary information section (placeholder for future enhancements)
    H3 summaryTitle = new H3("Summary");
    summaryTitle.addClassNames(
        LumoUtility.Margin.Top.XLARGE,
        LumoUtility.Margin.Bottom.MEDIUM,
        LumoUtility.FontWeight.BOLD);

    Div summaryInfo = new Div();
    summaryInfo.setText("Additional summary cards (total customers, total policies) can be added here.");
    summaryInfo.addClassNames(
        LumoUtility.FontSize.SMALL,
        LumoUtility.TextColor.TERTIARY,
        LumoUtility.Padding.MEDIUM);
    summaryInfo.getElement().getStyle().set("border", "1px dashed var(--lumo-contrast-10pct)");

    // Add all components to layout
    add(welcomeTitle, subtitle, quickActionsTitle, buttonSection, summaryTitle, summaryInfo);

    setJustifyContentMode(FlexComponent.JustifyContentMode.START);
    setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);
  }

  /**
   * Helper method to create consistently styled action buttons.
   *
   * @param label the button label text
   * @param icon the button icon
   * @param variant the button theme variant
   * @param clickListener the click handler
   * @return a configured Button
   */
  private Button createActionButton(
      String label, com.vaadin.flow.component.Component icon, ButtonVariant variant, Runnable clickListener) {
    Button button = new Button(label, icon);
    button.addThemeVariants(variant);
    button.setWidth("180px");
    button.setHeight("50px");
    button.addClassNames(LumoUtility.FontSize.MEDIUM);
    button.addClickListener(event -> clickListener.run());
    return button;
  }
}
