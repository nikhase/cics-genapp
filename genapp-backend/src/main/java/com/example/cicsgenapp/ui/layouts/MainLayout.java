package com.example.cicsgenapp.ui.layouts;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * MainLayout serves as the root layout for the Vaadin application.
 * It provides the AppLayout structure with header and navigation drawer.
 */
public class MainLayout extends AppLayout {

  public MainLayout() {
    createHeader();
    createDrawer();
  }

  private void createHeader() {
    // Header with title and drawer toggle
    Header header = new Header();
    header.addClassNames(
        LumoUtility.Display.FLEX,
        LumoUtility.AlignItems.CENTER,
        LumoUtility.Padding.Horizontal.LARGE,
        LumoUtility.Padding.Vertical.MEDIUM,
        "header-background");

    DrawerToggle toggle = new DrawerToggle();

    H1 appTitle = new H1("CICS GenApp");
    appTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

    // Get current user from Spring Security
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth != null ? auth.getName() : "Guest";

    Div userInfo = new Div();
    userInfo.setText("Logged in as: " + username);
    userInfo.addClassNames(
        LumoUtility.TextAlignment.RIGHT,
        LumoUtility.FontSize.SMALL,
        LumoUtility.Margin.Left.AUTO);

    HorizontalLayout headerLayout = new HorizontalLayout(toggle, appTitle, userInfo);
    headerLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    headerLayout.setWidthFull();
    headerLayout.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);

    header.add(headerLayout);
    addToNavbar(header);
  }

  private void createDrawer() {
    // Create navigation drawer with SideNav
    SideNav nav = new SideNav();
    nav.addClassNames(LumoUtility.Padding.MEDIUM);

    // Dashboard item
    SideNavItem dashboard =
        new SideNavItem("Dashboard", "/", VaadinIcon.HOME.create());
    nav.addItem(dashboard);

    // Customers section (SSC1-style menu)
    SideNavItem customers =
        new SideNavItem("Customer Menu", "/customers", VaadinIcon.USERS.create());
    nav.addItem(customers);

    // Customer List section
    SideNavItem customerList =
        new SideNavItem("Customer List", "/customers/list", VaadinIcon.LIST.create());
    nav.addItem(customerList);

    // Policies section
    SideNavItem policies =
        new SideNavItem("Policies", "/policies", VaadinIcon.FILE_TEXT.create());
    nav.addItem(policies);

    // Admin section (optional, can be hidden based on user role)
    SideNavItem admin =
        new SideNavItem("Admin", "/admin", VaadinIcon.COGS.create());
    nav.addItem(admin);

    // Logout option
    SideNavItem logout =
        new SideNavItem("Logout", "/logout", VaadinIcon.SIGN_OUT.create());
    nav.addItem(logout);

    addToDrawer(nav);
  }
}
