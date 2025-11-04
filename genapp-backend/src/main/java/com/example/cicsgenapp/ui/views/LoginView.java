package com.example.cicsgenapp.ui.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * LoginView is the login page for CICS GenApp.
 *
 * <p>This is a placeholder view that will be replaced in Story 3.2 with
 * a proper Vaadin login form. Currently displays a simple login page.
 *
 * <p>Authentication is handled by Spring Security form login at POST /login.
 * The actual form will be implemented in Story 3.2 with proper validation.
 *
 * @author Development Team
 * @version 1.0.0
 */
@Route("/login")
@PageTitle("Login - CICS GenApp")
public class LoginView extends VerticalLayout {

  public LoginView() {
    setWidthFull();
    setHeightFull();
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
    addClassNames(LumoUtility.Background.PRIMARY_10);

    // Title
    H1 title = new H1("CICS GenApp");
    title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Bottom.LARGE);

    // Placeholder message
    Div message = new Div();
    message.setText("Login form will be implemented in Story 3.2");
    message.addClassNames(
        LumoUtility.TextAlignment.CENTER,
        LumoUtility.FontSize.MEDIUM,
        LumoUtility.TextColor.SECONDARY);

    // Placeholder info
    Div info = new Div();
    info.setText("For MVP development, use:\n" +
        "Username: admin | Password: admin123\n" +
        "Username: user | Password: user123");
    info.addClassNames(
        LumoUtility.TextAlignment.CENTER,
        LumoUtility.FontSize.SMALL,
        LumoUtility.Margin.Top.LARGE,
        LumoUtility.Padding.Horizontal.MEDIUM);

    add(title, message, info);
  }
}
