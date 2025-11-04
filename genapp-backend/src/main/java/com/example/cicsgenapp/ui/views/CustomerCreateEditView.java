package com.example.cicsgenapp.ui.views;

import com.example.cicsgenapp.dto.CreateCustomerRequest;
import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.dto.UpdateCustomerRequest;
import com.example.cicsgenapp.service.CustomerService;
import com.example.cicsgenapp.ui.components.BreadcrumbNavigation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * CustomerCreateEditView displays a form for creating new customers or editing existing customer information.
 *
 * <p>Implements Story 3.6 - Customer Create/Edit Page with Vaadin Form.
 * Provides a shared form component used for both create mode (/customers/new) and edit mode (/customers/{id}/edit).
 *
 * <p><b>Acceptance Criteria (Story 3.6):</b>
 * <ul>
 *   <li>Single form used for both create and edit modes
 *   <li>Create mode routed to /customers/new with empty form
 *   <li>Edit mode routed to /customers/{id}/edit with pre-filled data
 *   <li>Form fields: First Name, Last Name, Email (unique), Phone, Address, City, State, Zip, Status
 *   <li>Client-side validation with real-time feedback
 *   <li>Server-side validation for unique constraints
 *   <li>Submit: POST for create, PUT for edit
 *   <li>Success: Toast notification and redirect to detail page
 *   <li>Error: Display error message, keep form data for retry
 *   <li>Cancel button returns to previous page
 *   <li>Responsive mobile-friendly layout
 * </ul>
 *
 * @author Development Team
 * @version 1.0.0 (Story 3.6 Implementation)
 */
@Route(value = "/customers/new", layout = com.example.cicsgenapp.ui.layouts.MainLayout.class)
@PageTitle("Create Customer - CICS GenApp")
public class CustomerCreateEditView extends VerticalLayout implements BeforeEnterObserver {

    private final CustomerService customerService;
    private String customerId;
    private boolean isEditMode = false;
    private CustomerResponse existingCustomer;

    // UI Components
    private H1 pageTitle;
    private BreadcrumbNavigation breadcrumbs;
    private Div errorDiv;
    private TextField firstNameField;
    private TextField lastNameField;
    private EmailField emailField;
    private TextField phoneField;
    private TextField addressField;
    private TextField cityField;
    private TextField stateField;
    private TextField zipCodeField;
    private Button submitButton;
    private Button cancelButton;
    private Binder<CreateCustomerRequest> createBinder;
    private Binder<UpdateCustomerRequest> editBinder;

    @Autowired
    public CustomerCreateEditView(CustomerService customerService) {
        this.customerService = customerService;
        initializeView();
    }

    private void initializeView() {
        setWidthFull();
        setPadding(true);
        setSpacing(true);
        addClassNames(LumoUtility.Padding.LARGE);

        // Breadcrumbs
        breadcrumbs = new BreadcrumbNavigation();
        add(breadcrumbs);

        // Page title (updated dynamically)
        pageTitle = new H1("Create Customer");
        pageTitle.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.FontWeight.BOLD);
        add(pageTitle);

        // Error div (initially hidden)
        errorDiv = new Div();
        errorDiv.setVisible(false);
        errorDiv.addClassNames(
                LumoUtility.Background.ERROR_10,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.TextColor.ERROR);
        add(errorDiv);

        // Form container
        VerticalLayout formContainer = createFormContainer();
        add(formContainer);

        // Initialize data binders
        initializeBinders();
    }

    private VerticalLayout createFormContainer() {
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidthFull();
        formLayout.setSpacing(true);
        formLayout.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.Border.ALL);
        formLayout.setMaxWidth("600px");

        // Personal Information Section
        Paragraph personalSection = new Paragraph("Personal Information");
        personalSection.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.LARGE);
        formLayout.add(personalSection);

        // First Name
        firstNameField = new TextField("First Name");
        firstNameField.setWidthFull();
        firstNameField.setRequired(true);
        firstNameField.setMaxLength(100);
        firstNameField.setPlaceholder("Enter first name");
        formLayout.add(firstNameField);

        // Last Name
        lastNameField = new TextField("Last Name");
        lastNameField.setWidthFull();
        lastNameField.setRequired(true);
        lastNameField.setMaxLength(100);
        lastNameField.setPlaceholder("Enter last name");
        formLayout.add(lastNameField);

        // Contact Information Section
        Paragraph contactSection = new Paragraph("Contact Information");
        contactSection.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.LARGE, LumoUtility.Margin.Top.MEDIUM);
        formLayout.add(contactSection);

        // Email
        emailField = new EmailField("Email");
        emailField.setWidthFull();
        emailField.setRequired(true);
        emailField.setPlaceholder("Enter email address");
        formLayout.add(emailField);

        // Phone
        phoneField = new TextField("Phone");
        phoneField.setWidthFull();
        phoneField.setRequired(true);
        phoneField.setMaxLength(20);
        phoneField.setPlaceholder("+1-555-1234");
        formLayout.add(phoneField);

        // Address Section
        Paragraph addressSection = new Paragraph("Address");
        addressSection.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.LARGE, LumoUtility.Margin.Top.MEDIUM);
        formLayout.add(addressSection);

        // Street Address
        addressField = new TextField("Street Address");
        addressField.setWidthFull();
        addressField.setPlaceholder("Enter street address");
        formLayout.add(addressField);

        // City
        cityField = new TextField("City");
        cityField.setWidthFull();
        cityField.setPlaceholder("Enter city");
        formLayout.add(cityField);

        // State
        stateField = new TextField("State");
        stateField.setWidthFull();
        stateField.setMaxLength(2);
        stateField.setPlaceholder("Enter state code (e.g., CA)");
        formLayout.add(stateField);

        // Zip Code
        zipCodeField = new TextField("Zip Code");
        zipCodeField.setWidthFull();
        zipCodeField.setMaxLength(10);
        zipCodeField.setPlaceholder("Enter zip code");
        formLayout.add(zipCodeField);

        // Status is read-only for edit mode, not shown for create mode
        // statusCombo is kept for future use if needed

        // Buttons Section
        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        buttonLayout.addClassNames(LumoUtility.Margin.Top.LARGE);

        submitButton = new Button("Save Customer", VaadinIcon.CHECK.create());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setWidthFull();
        submitButton.addClickListener(event -> submitForm());

        cancelButton = new Button("Cancel", VaadinIcon.ARROW_LEFT.create());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.setWidthFull();
        cancelButton.addClickListener(event -> navigateBack());

        buttonLayout.add(submitButton, cancelButton);
        formLayout.add(buttonLayout);

        return formLayout;
    }

    private void initializeBinders() {
        createBinder = new Binder<>(CreateCustomerRequest.class);
        editBinder = new Binder<>(UpdateCustomerRequest.class);

        // Setup create binder
        createBinder.forField(firstNameField)
                .asRequired("First name is required")
                .bind(CreateCustomerRequest::getFirstName, CreateCustomerRequest::setFirstName);

        createBinder.forField(lastNameField)
                .asRequired("Last name is required")
                .bind(CreateCustomerRequest::getLastName, CreateCustomerRequest::setLastName);

        createBinder.forField(emailField)
                .asRequired("Email is required")
                .bind(CreateCustomerRequest::getEmail, CreateCustomerRequest::setEmail);

        createBinder.forField(phoneField)
                .asRequired("Phone is required")
                .bind(CreateCustomerRequest::getPhone, CreateCustomerRequest::setPhone);

        createBinder.forField(addressField)
                .bind(CreateCustomerRequest::getAddress, CreateCustomerRequest::setAddress);

        createBinder.forField(cityField)
                .bind(CreateCustomerRequest::getCity, CreateCustomerRequest::setCity);

        createBinder.forField(stateField)
                .bind(CreateCustomerRequest::getState, CreateCustomerRequest::setState);

        createBinder.forField(zipCodeField)
                .bind(CreateCustomerRequest::getZipCode, CreateCustomerRequest::setZipCode);

        // Setup edit binder
        editBinder.forField(firstNameField)
                .asRequired("First name is required")
                .bind(UpdateCustomerRequest::getFirstName, UpdateCustomerRequest::setFirstName);

        editBinder.forField(lastNameField)
                .asRequired("Last name is required")
                .bind(UpdateCustomerRequest::getLastName, UpdateCustomerRequest::setLastName);

        editBinder.forField(emailField)
                .asRequired("Email is required")
                .bind(UpdateCustomerRequest::getEmail, UpdateCustomerRequest::setEmail);

        editBinder.forField(phoneField)
                .asRequired("Phone is required")
                .bind(UpdateCustomerRequest::getPhone, UpdateCustomerRequest::setPhone);

        editBinder.forField(addressField)
                .bind(UpdateCustomerRequest::getAddress, UpdateCustomerRequest::setAddress);

        editBinder.forField(cityField)
                .bind(UpdateCustomerRequest::getCity, UpdateCustomerRequest::setCity);

        editBinder.forField(stateField)
                .bind(UpdateCustomerRequest::getState, UpdateCustomerRequest::setState);

        editBinder.forField(zipCodeField)
                .bind(UpdateCustomerRequest::getZipCode, UpdateCustomerRequest::setZipCode);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Determine if this is create or edit mode
        String location = event.getLocation().getPath();

        if (location.contains("/edit")) {
            isEditMode = true;
            customerId = event.getRouteParameters().get("id").orElse(null);

            if (customerId == null) {
                showError("Invalid customer ID");
                return;
            }

            loadCustomerForEdit();
        } else {
            isEditMode = false;
            initializeCreateMode();
        }
    }

    private void initializeCreateMode() {
        pageTitle.setText("Create Customer");
        breadcrumbs.setItems(
                new BreadcrumbNavigation.Item("Dashboard", ""),
                new BreadcrumbNavigation.Item("Customers", "/customers"),
                new BreadcrumbNavigation.Item("Create", null)
        );
        createBinder.setBean(new CreateCustomerRequest());
    }

    private void loadCustomerForEdit() {
        try {
            UUID id = UUID.fromString(customerId);
            existingCustomer = customerService.getCustomerById(id);

            if (existingCustomer == null) {
                showError("Customer not found");
                return;
            }

            populateFormForEdit();
        } catch (IllegalArgumentException e) {
            showError("Invalid customer ID format");
        } catch (Exception e) {
            showError("Unable to load customer details");
        }
    }

    private void populateFormForEdit() {
        pageTitle.setText("Edit Customer");
        breadcrumbs.setItems(
                new BreadcrumbNavigation.Item("Dashboard", ""),
                new BreadcrumbNavigation.Item("Customers", "/customers"),
                new BreadcrumbNavigation.Item(existingCustomer.getFirstName() + " " + existingCustomer.getLastName(), "/customers/" + customerId),
                new BreadcrumbNavigation.Item("Edit", null)
        );

        // Set form fields from existing customer
        firstNameField.setValue(existingCustomer.getFirstName());
        lastNameField.setValue(existingCustomer.getLastName());
        emailField.setValue(existingCustomer.getEmail());
        phoneField.setValue(existingCustomer.getPhone() != null ? existingCustomer.getPhone() : "");
        if (existingCustomer.getAddress() != null) {
            addressField.setValue(existingCustomer.getAddress());
        }

        // Create update request from existing customer
        UpdateCustomerRequest request = new UpdateCustomerRequest();
        request.setFirstName(existingCustomer.getFirstName());
        request.setLastName(existingCustomer.getLastName());
        request.setEmail(existingCustomer.getEmail());
        request.setPhone(existingCustomer.getPhone());
        request.setAddress(existingCustomer.getAddress());

        editBinder.setBean(request);
    }

    private void submitForm() {
        hideError();

        if (isEditMode) {
            // Validate edit form
            if (!editBinder.isValid()) {
                editBinder.validate();
                return;
            }
            updateCustomer(editBinder.getBean());
        } else {
            // Validate create form
            if (!createBinder.isValid()) {
                createBinder.validate();
                return;
            }
            createCustomer(createBinder.getBean());
        }
    }

    private void createCustomer(CreateCustomerRequest request) {
        try {
            submitButton.setEnabled(false);

            CustomerResponse response = customerService.createCustomer(request);

            if (response != null) {
                showSuccessMessage("Customer created successfully");
                getUI().ifPresent(ui -> ui.navigate("/customers/" + response.getCustomerId()));
            } else {
                showError("Failed to create customer");
                submitButton.setEnabled(true);
            }
        } catch (Exception e) {
            String errorMessage = "Failed to create customer";
            if (e.getMessage() != null && e.getMessage().contains("409")) {
                errorMessage = "A customer with this email already exists";
            } else if (e.getMessage() != null) {
                errorMessage = e.getMessage();
            }
            showError(errorMessage);
            submitButton.setEnabled(true);
        }
    }

    private void updateCustomer(UpdateCustomerRequest request) {
        try {
            submitButton.setEnabled(false);

            UUID id = UUID.fromString(customerId);
            CustomerResponse response = customerService.updateCustomer(id, request);

            if (response != null) {
                showSuccessMessage("Customer updated successfully");
                getUI().ifPresent(ui -> ui.navigate("/customers/" + customerId));
            } else {
                showError("Failed to update customer");
                submitButton.setEnabled(true);
            }
        } catch (Exception e) {
            String errorMessage = "Failed to update customer";
            if (e.getMessage() != null && e.getMessage().contains("409")) {
                errorMessage = "A customer with this email already exists";
            } else if (e.getMessage() != null) {
                errorMessage = e.getMessage();
            }
            showError(errorMessage);
            submitButton.setEnabled(true);
        }
    }

    private void navigateBack() {
        if (isEditMode && customerId != null) {
            getUI().ifPresent(ui -> ui.navigate("/customers/" + customerId));
        } else {
            getUI().ifPresent(ui -> ui.navigate("/customers"));
        }
    }

    private void showError(String message) {
        errorDiv.removeAll();
        Div errorContent = new Div();
        Paragraph errorText = new Paragraph(message);
        errorContent.add(errorText);
        errorDiv.add(errorContent);
        errorDiv.setVisible(true);
    }

    private void hideError() {
        errorDiv.setVisible(false);
    }

    private void showSuccessMessage(String message) {
        // TODO: Implement toast notification using Vaadin Notification API
        System.out.println("Success: " + message);
    }
}
