package com.example.cicsgenapp.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Vaadin LoginView for CICS GenApp authentication.
 *
 * <p>Implements Story 3.2 - Login Page with Spring Security Form Authentication.
 * Provides a Vaadin form with username and password fields for user authentication.
 *
 * <p>Features:
 * <ul>
 *   <li>Vaadin TextField and PasswordField components
 *   <li>Form-based authentication via Spring Security
 *   <li>Client-side validation (required fields)
 *   <li>Server-side error message display
 *   <li>Responsive, centered layout using Vaadin Lumo theme
 * </ul>
 *
 * @author Development Team
 * @version 1.0.0
 */
@Route("/login")
@PageTitle("Login - CICS GenApp")
public class LoginView extends VerticalLayout {

  private final AuthenticationManager authenticationManager;

  public LoginView(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;

    setWidthFull();
    setHeightFull();
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
    addClassNames(LumoUtility.Background.PRIMARY_10);

    // Title
    H1 title = new H1("CICS GenApp");
    title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Bottom.LARGE);

    // Create login form container
    Div formContainer = new Div();
    formContainer.addClassNames(
        LumoUtility.Background.BASE,
        LumoUtility.Padding.LARGE,
        LumoUtility.BorderRadius.MEDIUM);
    formContainer.setMaxWidth("400px");
    formContainer.setWidth("100%");

    // Form title
    Div formTitle = new Div();
    formTitle.setText("Sign In");
    formTitle.addClassNames(
        LumoUtility.FontSize.LARGE,
        LumoUtility.FontWeight.BOLD,
        LumoUtility.Margin.Bottom.MEDIUM);

    // Create form using FormLayout
    FormLayout loginForm = new FormLayout();
    loginForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

    // Username field
    TextField usernameField = new TextField();
    usernameField.setLabel("Username");
    usernameField.setPlaceholder("Username");
    usernameField.setRequired(true);
    usernameField.setRequiredIndicatorVisible(true);
    usernameField.setWidthFull();
    usernameField.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

    // Password field
    PasswordField passwordField = new PasswordField();
    passwordField.setLabel("Password");
    passwordField.setPlaceholder("Password");
    passwordField.setRequired(true);
    passwordField.setRequiredIndicatorVisible(true);
    passwordField.setWidthFull();
    passwordField.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

    // Error message area (hidden by default)
    Div errorMessage = new Div();
    errorMessage.setVisible(false);
    errorMessage.addClassNames(
        LumoUtility.Background.ERROR_10,
        LumoUtility.TextColor.ERROR,
        LumoUtility.Padding.MEDIUM,
        LumoUtility.BorderRadius.MEDIUM,
        LumoUtility.Margin.Bottom.MEDIUM);

    // Sign In button
    Button signInButton = new Button("Sign In");
    signInButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    signInButton.setWidthFull();
    signInButton.setHeight("44px");
    signInButton.addClassNames(LumoUtility.FontSize.MEDIUM);

    // Form submission handling
    signInButton.addClickListener(event -> {
      // Client-side validation
      if (usernameField.isEmpty() || passwordField.isEmpty()) {
        errorMessage.setText("Please fill out both fields");
        errorMessage.setVisible(true);
        return;
      }

      // Clear previous errors
      errorMessage.setVisible(false);

      // Attempt authentication
      try {
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(usernameField.getValue(),
                passwordField.getValue());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Redirect to dashboard on success
        getUI().ifPresent(ui -> ui.navigate("/"));
      } catch (Exception e) {
        // Show error message
        errorMessage.setText("Invalid username or password");
        errorMessage.setVisible(true);
        passwordField.clear();
      }
    });

    // Demo credentials hint
    Div credentialsHint = new Div();
    credentialsHint.setText("Demo: admin / admin123 or user / user123");
    credentialsHint.addClassNames(
        LumoUtility.FontSize.SMALL,
        LumoUtility.TextColor.SECONDARY,
        LumoUtility.Margin.Top.MEDIUM);

    // Add fields to form
    loginForm.add(usernameField, passwordField, errorMessage, signInButton);

    // Add components to container
    formContainer.add(formTitle, loginForm, credentialsHint);

    // Add container to view
    add(title, formContainer);

    // Adjust spacing
    setMargin(false);
    setPadding(false);
  }
}
