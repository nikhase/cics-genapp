package com.example.cicsgenapp.ui.views;

import com.example.cicsgenapp.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * DashboardView is the main dashboard page of CICS GenApp.
 *
 * <p>This is the landing page after successful authentication. It displays
 * quick action buttons for common tasks and is the foundation for other stories.
 *
 * <p><b>Acceptance Criteria (Story 3.3):</b>
 * <ul>
 *   <li>Displays quick action buttons: Customer Management, Policy Management
 *   <li>Uses MainLayout with AppLayout navigation
 *   <li>Responsive layout (works on mobile, tablet, desktop)
 *   <li>Proper theme integration with Vaadin Lumo design system
 *   <li>Navigation integrated with MainLayout
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

    // Title
    H2 title = new H2("Welcome to CICS GenApp");
    title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Bottom.MEDIUM);

    // Subtitle
    Div subtitle = new Div();
    subtitle.setText("Manage customers and policies with ease");
    subtitle.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.TextColor.SECONDARY);

    // Quick Actions Section
    H2 quickActionsTitle = new H2("Quick Actions");
    quickActionsTitle.addClassNames(LumoUtility.Margin.Top.LARGE, LumoUtility.Margin.Bottom.MEDIUM);

    // Button for Customer Management
    Button customersButton = new Button("Manage Customers");
    customersButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    customersButton.setWidth("200px");
    customersButton.setHeight("60px");
    customersButton.addClassNames(LumoUtility.FontSize.MEDIUM);
    customersButton.addClickListener(event -> {
      getUI().ifPresent(ui -> ui.navigate("/customers"));
    });

    // Button for Policy Management
    Button policiesButton = new Button("Manage Policies");
    policiesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    policiesButton.setWidth("200px");
    policiesButton.setHeight("60px");
    policiesButton.addClassNames(LumoUtility.FontSize.MEDIUM);
    policiesButton.addClickListener(event -> {
      getUI().ifPresent(ui -> ui.navigate("/policies"));
    });

    // Add components to layout
    add(title, subtitle, quickActionsTitle, customersButton, policiesButton);

    setJustifyContentMode(FlexComponent.JustifyContentMode.START);
    setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);
  }
}
