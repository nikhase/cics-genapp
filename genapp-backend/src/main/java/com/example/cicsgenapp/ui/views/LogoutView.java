package com.example.cicsgenapp.ui.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * LogoutView handles the logout flow in a Vaadin-aware manner.
 *
 * <p>This component:
 * - Clears the Spring Security context
 * - Invalidates the HTTP session
 * - Invalidates the Vaadin session
 * - Redirects to the login page
 *
 * <p>Unlike traditional HTTP redirects, this component ensures proper cleanup
 * of both Spring Security and Vaadin session state before navigation.
 *
 * @author Development Team
 * @version 1.0.0
 */
@Route("/logout")
@PageTitle("Logout - CICS GenApp")
public class LogoutView extends Div {

  public LogoutView() {
    // Display a message while logout completes
    Div message = new Div();
    message.setText("Logging out...");
    add(message);
  }

  @PostConstruct
  public void logout() {
    // Clear Spring Security context
    SecurityContextHolder.clearContext();

    // Invalidate HTTP session
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      request.getSession().invalidate();
    }

    // Invalidate Vaadin session
    VaadinSession.getCurrent().close();

    // Redirect to login page using client-side navigation
    getUI()
        .ifPresent(
            ui -> {
              ui.getPage().setLocation("/login");
            });
  }
}
