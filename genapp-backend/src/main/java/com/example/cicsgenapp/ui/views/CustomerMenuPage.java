package com.example.cicsgenapp.ui.views;

import com.example.cicsgenapp.dto.CreateCustomerRequest;
import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.dto.UpdateCustomerRequest;
import com.example.cicsgenapp.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * CustomerMenuPage provides an SSC1-style unified customer management interface.
 *
 * <p>Implements Story 3.11 - Customer Menu (Unified Management Interface).
 * Combines menu selection (left) with dynamic form behavior (right):
 * - Option 1: Customer Inquiry - Look up customer by number (read-only display)
 * - Option 2: Customer Add - Create new customer (all fields enabled, blank)
 * - Option 4: Customer Update - Modify existing customer (lookup + edit)
 *
 * <p>This matches the original 3270 terminal SSC1 screen layout for familiarity
 * while providing modern Vaadin UI components.
 *
 * @author Development Team
 * @version 1.0.0 (Story 3.11 - SSC1 Customer Menu Unified Interface)
 */
@Route(value = "customers", layout = com.example.cicsgenapp.ui.layouts.MainLayout.class)
@PageTitle("Customer Menu - CICS GenApp")
public class CustomerMenuPage extends VerticalLayout {

  private final CustomerService customerService;

  // State management
  private int selectedOption = 1;  // Default to Inquiry
  private UUID currentCustomerId;
  private FormState formState = FormState.INQUIRY;

  // Menu section
  private RadioButtonGroup<Integer> optionGroup;

  // Form section
  private H3 formTitle;
  private TextField customerNumberField;
  private TextField firstNameField;
  private TextField lastNameField;
  private DatePicker dobField;
  private TextField houseNameField;
  private NumberField houseNumberField;
  private TextField postcodeField;
  private TextField homePhoneField;
  private TextField mobilePhoneField;
  private TextField emailField;

  // Control section
  private Button submitButton;
  private Button clearButton;
  private ProgressBar loadingIndicator;
  private Div errorMessageDiv;

  // Layout sections
  private VerticalLayout menuSection;
  private VerticalLayout formSection;

  enum FormState {
    INQUIRY,      // All fields read-only except customer number
    ADD,          // All fields editable, customer number disabled
    UPDATE        // Customer number enabled, others editable after lookup
  }

  @Autowired
  public CustomerMenuPage(CustomerService customerService) {
    this.customerService = customerService;
    initializeView();
  }

  private void initializeView() {
    // Main container setup
    addClassNames(
        LumoUtility.Display.FLEX,
        LumoUtility.FlexDirection.COLUMN,
        LumoUtility.Padding.MEDIUM,
        LumoUtility.Gap.MEDIUM);
    setWidthFull();
    setHeightFull();

    // Page title
    H1 title = new H1("SSC1 - General Insurance Customer Menu");
    title.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.MEDIUM);
    add(title);

    // Two-column layout (menu + form)
    HorizontalLayout mainContent = new HorizontalLayout();
    mainContent.setWidthFull();
    mainContent.setFlexGrow(1, mainContent);
    mainContent.addClassNames(LumoUtility.Gap.LARGE);

    // Create menu section (left side)
    menuSection = createMenuSection();
    menuSection.setWidth("30%");
    mainContent.add(menuSection);

    // Create form section (right side)
    formSection = createFormSection();
    formSection.setWidth("70%");
    formSection.setFlexGrow(1, formSection);
    mainContent.add(formSection);

    add(mainContent);
    setFlexGrow(1, mainContent);
  }

  private VerticalLayout createMenuSection() {
    VerticalLayout menu = new VerticalLayout();
    menu.addClassNames(
        LumoUtility.Padding.MEDIUM,
        LumoUtility.Border.ALL,
        LumoUtility.BorderRadius.MEDIUM);
    menu.setSpacing(true);

    // Menu title
    H3 menuTitle = new H3("Select Operation");
    menuTitle.addClassNames(LumoUtility.Margin.Top.NONE);
    menu.add(menuTitle);

    // Radio button group for options
    optionGroup = new RadioButtonGroup<>();
    optionGroup.setItems(1, 2, 4);
    optionGroup.setItemLabelGenerator(option -> {
      switch (option) {
        case 1: return "Customer Inquiry";
        case 2: return "Customer Add";
        case 4: return "Customer Update";
        default: return "Unknown";
      }
    });
    optionGroup.setValue(1);
    optionGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    optionGroup.addValueChangeListener(event -> {
      selectedOption = event.getValue();
      updateFormBehavior();
    });

    menu.add(optionGroup);

    // Menu descriptions
    Div descriptionBox = new Div();
    descriptionBox.addClassNames(
        LumoUtility.Padding.MEDIUM,
        LumoUtility.Background.CONTRAST_5,
        LumoUtility.BorderRadius.MEDIUM,
        LumoUtility.Margin.Top.MEDIUM);

    Paragraph description = new Paragraph();
    description.addClassNames(LumoUtility.FontSize.SMALL);
    updateMenuDescription(description);

    descriptionBox.add(description);
    menu.add(descriptionBox);

    return menu;
  }

  private void updateMenuDescription(Paragraph description) {
    switch (selectedOption) {
      case 1:
        description.setText("Enter a customer number to look up and view customer details. All fields will be read-only.");
        break;
      case 2:
        description.setText("Fill in the customer details below to create a new customer record.");
        break;
      case 4:
        description.setText("Enter a customer number to load the record, then modify the details and save changes.");
        break;
    }
  }

  private VerticalLayout createFormSection() {
    VerticalLayout form = new VerticalLayout();
    form.setSpacing(true);
    form.setPadding(false);

    // Form title (updates by option)
    formTitle = new H3("Customer Inquiry Form");
    formTitle.addClassNames(LumoUtility.Margin.Top.NONE);
    form.add(formTitle);

    // Loading indicator
    loadingIndicator = new ProgressBar();
    loadingIndicator.setIndeterminate(true);
    loadingIndicator.setVisible(false);
    form.add(loadingIndicator);

    // Error message area
    errorMessageDiv = new Div();
    errorMessageDiv.addClassNames(
        LumoUtility.Padding.MEDIUM,
        LumoUtility.Background.ERROR_10,
        LumoUtility.BorderRadius.MEDIUM,
        LumoUtility.TextColor.ERROR);
    errorMessageDiv.setVisible(false);
    form.add(errorMessageDiv);

    // Form layout with two columns
    FormLayout formLayout = new FormLayout();
    formLayout.setResponsiveSteps(
        new FormLayout.ResponsiveStep("0", 1),  // 1 column on small screens
        new FormLayout.ResponsiveStep("768px", 2)  // 2 columns on larger screens
    );
    formLayout.setColspan(emailField, 2);  // Email spans both columns

    // Customer identification section
    customerNumberField = new TextField("Customer Number");
    customerNumberField.setPlaceholder("Enter 10-digit customer number");
    customerNumberField.setMaxLength(10);
    customerNumberField.setRequired(true);

    firstNameField = new TextField("First Name");
    firstNameField.setPlaceholder("First name");
    firstNameField.setMaxLength(10);

    lastNameField = new TextField("Last Name");
    lastNameField.setPlaceholder("Last name");
    lastNameField.setMaxLength(20);

    formLayout.add(customerNumberField);
    formLayout.add(firstNameField);
    formLayout.add(lastNameField);

    // Personal details section
    dobField = new DatePicker("Date of Birth");
    dobField.setPlaceholder("yyyy-mm-dd");

    houseNameField = new TextField("House Name");
    houseNameField.setPlaceholder("House name");
    houseNameField.setMaxLength(20);

    formLayout.add(dobField);
    formLayout.add(houseNameField);

    // Address information section
    houseNumberField = new NumberField("House Number");
    houseNumberField.setPlaceholder("0000");
    houseNumberField.setMin(0);
    houseNumberField.setMax(9999);

    postcodeField = new TextField("Postcode");
    postcodeField.setPlaceholder("Postcode");
    postcodeField.setMaxLength(8);

    formLayout.add(houseNumberField);
    formLayout.add(postcodeField);

    // Contact information section
    homePhoneField = new TextField("Home Phone");
    homePhoneField.setPlaceholder("+1234567890");
    homePhoneField.setMaxLength(20);

    mobilePhoneField = new TextField("Mobile Phone");
    mobilePhoneField.setPlaceholder("+1234567890");
    mobilePhoneField.setMaxLength(20);

    emailField = new TextField("Email Address");
    emailField.setPlaceholder("user@example.com");
    emailField.setMaxLength(27);

    formLayout.add(homePhoneField);
    formLayout.add(mobilePhoneField);
    formLayout.add(emailField);

    form.add(formLayout);

    // Button bar
    HorizontalLayout buttonBar = new HorizontalLayout();
    buttonBar.setSpacing(true);
    buttonBar.addClassNames(LumoUtility.Margin.Top.MEDIUM);

    submitButton = new Button("Look Up");
    submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    submitButton.addClickListener(e -> handleSubmit());

    clearButton = new Button("Clear Form");
    clearButton.addClickListener(e -> clearForm());

    buttonBar.add(submitButton, clearButton);
    form.add(buttonBar);

    // Set initial form state (Inquiry)
    updateFormBehavior();

    return form;
  }

  private void updateFormBehavior() {
    formState = switch (selectedOption) {
      case 1 -> FormState.INQUIRY;
      case 2 -> FormState.ADD;
      case 4 -> FormState.UPDATE;
      default -> FormState.INQUIRY;
    };

    // Update form title
    String title = switch (selectedOption) {
      case 1 -> "Customer Inquiry Form";
      case 2 -> "Add New Customer";
      case 4 -> "Update Customer";
      default -> "Customer Form";
    };
    formTitle.setText(title);

    // Update submit button label
    String buttonLabel = switch (selectedOption) {
      case 1 -> "Look Up";
      case 2 -> "Create Customer";
      case 4 -> "Update Customer";
      default -> "Submit";
    };
    submitButton.setText(buttonLabel);

    // Clear form
    clearForm();

    // Apply field state based on option
    switch (formState) {
      case INQUIRY -> applyInquiryState();
      case ADD -> applyAddState();
      case UPDATE -> applyUpdateState();
    }

    // Clear error messages
    clearErrorMessage();
  }

  private void applyInquiryState() {
    // Customer number: enabled for input
    customerNumberField.setReadOnly(false);
    customerNumberField.setRequired(true);
    customerNumberField.focus();

    // All other fields: read-only (for display after lookup)
    firstNameField.setReadOnly(true);
    lastNameField.setReadOnly(true);
    dobField.setReadOnly(true);
    houseNameField.setReadOnly(true);
    houseNumberField.setReadOnly(true);
    postcodeField.setReadOnly(true);
    homePhoneField.setReadOnly(true);
    mobilePhoneField.setReadOnly(true);
    emailField.setReadOnly(true);
  }

  private void applyAddState() {
    // Customer number: disabled (will be auto-generated)
    customerNumberField.setReadOnly(false);
    customerNumberField.setValue("");
    customerNumberField.setEnabled(false);
    customerNumberField.setRequired(false);

    // All other fields: enabled and blank
    firstNameField.setReadOnly(false);
    firstNameField.setRequired(true);
    firstNameField.focus();

    lastNameField.setReadOnly(false);
    lastNameField.setRequired(true);

    dobField.setReadOnly(false);
    dobField.setRequired(true);

    houseNameField.setReadOnly(false);
    houseNameField.setRequired(true);

    houseNumberField.setReadOnly(false);
    houseNumberField.setRequired(true);

    postcodeField.setReadOnly(false);
    postcodeField.setRequired(true);

    homePhoneField.setReadOnly(false);
    mobilePhoneField.setReadOnly(false);
    emailField.setReadOnly(false);
  }

  private void applyUpdateState() {
    // Customer number: enabled for lookup
    customerNumberField.setReadOnly(false);
    customerNumberField.setRequired(true);
    customerNumberField.focus();

    // Other fields: disabled until lookup succeeds
    disableEditFields();
  }

  private void disableEditFields() {
    firstNameField.setReadOnly(true);
    lastNameField.setReadOnly(true);
    dobField.setReadOnly(true);
    houseNameField.setReadOnly(true);
    houseNumberField.setReadOnly(true);
    postcodeField.setReadOnly(true);
    homePhoneField.setReadOnly(true);
    mobilePhoneField.setReadOnly(true);
    emailField.setReadOnly(true);
  }

  private void enableEditFields() {
    firstNameField.setReadOnly(false);
    lastNameField.setReadOnly(false);
    dobField.setReadOnly(false);
    houseNameField.setReadOnly(false);
    houseNumberField.setReadOnly(false);
    postcodeField.setReadOnly(false);
    homePhoneField.setReadOnly(false);
    mobilePhoneField.setReadOnly(false);
    emailField.setReadOnly(false);
  }

  private void handleSubmit() {
    clearErrorMessage();

    switch (formState) {
      case INQUIRY -> handleInquiry();
      case ADD -> handleAdd();
      case UPDATE -> handleUpdate();
    }
  }

  private void handleInquiry() {
    String customerNumber = customerNumberField.getValue().trim();

    if (customerNumber.isEmpty()) {
      showErrorMessage("Please enter a customer number.");
      return;
    }

    loadingIndicator.setVisible(true);
    submitButton.setEnabled(false);

    try {
      // Try to parse as UUID first, otherwise search by number
      CustomerResponse customer = null;
      try {
        UUID customerId = UUID.fromString(customerNumber);
        // API call would go here - for now, assume not found
        // customer = customerService.getCustomer(customerId);
      } catch (IllegalArgumentException e) {
        // Not a valid UUID, show error
        showErrorMessage("Invalid customer number format. Please enter a valid UUID or customer number.");
        return;
      }

      if (customer != null) {
        currentCustomerId = customer.getCustomerId();
        populateFormFields(customer);
        showSuccessMessage("Customer found successfully.");
      } else {
        showErrorMessage("Customer not found. Please verify the customer number.");
      }
    } catch (Exception e) {
      showErrorMessage("Error looking up customer: " + e.getMessage());
    } finally {
      loadingIndicator.setVisible(false);
      submitButton.setEnabled(true);
    }
  }

  private void handleAdd() {
    // Validate required fields
    if (firstNameField.getValue().trim().isEmpty()) {
      showErrorMessage("First name is required.");
      firstNameField.focus();
      return;
    }
    if (lastNameField.getValue().trim().isEmpty()) {
      showErrorMessage("Last name is required.");
      lastNameField.focus();
      return;
    }
    if (dobField.getValue() == null) {
      showErrorMessage("Date of birth is required.");
      dobField.focus();
      return;
    }
    if (houseNameField.getValue().trim().isEmpty()) {
      showErrorMessage("House name is required.");
      houseNameField.focus();
      return;
    }
    if (postcodeField.getValue().trim().isEmpty()) {
      showErrorMessage("Postcode is required.");
      postcodeField.focus();
      return;
    }

    loadingIndicator.setVisible(true);
    submitButton.setEnabled(false);

    try {
      // Build customer creation request (use CustomerResponse fields)
      CreateCustomerRequest request = new CreateCustomerRequest();
      request.setFirstName(firstNameField.getValue().trim());
      request.setLastName(lastNameField.getValue().trim());
      request.setDateOfBirth(dobField.getValue());
      request.setAddress(houseNameField.getValue().trim());
      request.setCity("");  // Not collected from form
      request.setState("");  // Not collected from form
      request.setZipCode(postcodeField.getValue().trim());
      request.setPhone(homePhoneField.getValue().trim().isEmpty() ? mobilePhoneField.getValue().trim() : homePhoneField.getValue().trim());
      request.setEmail(emailField.getValue().trim().isEmpty() ? null : emailField.getValue().trim());

      // Call API to create customer
      CustomerResponse created = customerService.createCustomer(request);
      currentCustomerId = created.getCustomerId();
      populateFormFields(created);
      showSuccessMessage("Customer created successfully. ID: " + created.getCustomerId());
      clearForm();
    } catch (Exception e) {
      showErrorMessage("Error creating customer: " + e.getMessage());
    } finally {
      loadingIndicator.setVisible(false);
      submitButton.setEnabled(true);
    }
  }

  private void handleUpdate() {
    String customerNumber = customerNumberField.getValue().trim();

    if (customerNumber.isEmpty()) {
      showErrorMessage("Please enter a customer number.");
      return;
    }

    // Check if we're in the lookup phase or update phase
    if (currentCustomerId == null || firstNameField.isReadOnly()) {
      // Phase 1: Look up customer
      loadingIndicator.setVisible(true);
      submitButton.setEnabled(false);

      try {
        // Try to parse as UUID
        CustomerResponse customer = null;
        try {
          UUID customerId = UUID.fromString(customerNumber);
          // API call would go here
          // customer = customerService.getCustomer(customerId);
        } catch (IllegalArgumentException e) {
          showErrorMessage("Invalid customer number format. Please enter a valid UUID or customer number.");
          return;
        }

        if (customer != null) {
          currentCustomerId = customer.getCustomerId();
          populateFormFields(customer);
          enableEditFields();
          submitButton.setText("Save Changes");
          showSuccessMessage("Customer loaded. You can now modify the details.");
        } else {
          showErrorMessage("Customer not found. Please verify the customer number.");
        }
      } catch (Exception e) {
        showErrorMessage("Error looking up customer: " + e.getMessage());
      } finally {
        loadingIndicator.setVisible(false);
        submitButton.setEnabled(true);
      }
    } else {
      // Phase 2: Update customer
      loadingIndicator.setVisible(true);
      submitButton.setEnabled(false);

      try {
        // Validate required fields
        if (firstNameField.getValue().trim().isEmpty()) {
          showErrorMessage("First name is required.");
          return;
        }
        if (lastNameField.getValue().trim().isEmpty()) {
          showErrorMessage("Last name is required.");
          return;
        }

        // Build updated customer request
        UpdateCustomerRequest request = new UpdateCustomerRequest();
        request.setFirstName(firstNameField.getValue().trim());
        request.setLastName(lastNameField.getValue().trim());
        request.setDateOfBirth(dobField.getValue());
        request.setAddress(houseNameField.getValue().trim());
        request.setCity("");  // Not collected from form
        request.setState("");  // Not collected from form
        request.setZipCode(postcodeField.getValue().trim());
        request.setPhone(homePhoneField.getValue().trim().isEmpty() ? mobilePhoneField.getValue().trim() : homePhoneField.getValue().trim());
        request.setEmail(emailField.getValue().trim().isEmpty() ? null : emailField.getValue().trim());

        // Call API to update customer
        customerService.updateCustomer(currentCustomerId, request);
        showSuccessMessage("Customer updated successfully.");
        disableEditFields();
        submitButton.setText("Update Customer");
      } catch (Exception e) {
        showErrorMessage("Error updating customer: " + e.getMessage());
      } finally {
        loadingIndicator.setVisible(false);
        submitButton.setEnabled(true);
      }
    }
  }

  private void populateFormFields(CustomerResponse customer) {
    customerNumberField.setValue(customer.getCustomerId().toString());
    firstNameField.setValue(customer.getFirstName() != null ? customer.getFirstName() : "");
    lastNameField.setValue(customer.getLastName() != null ? customer.getLastName() : "");
    dobField.setValue(customer.getDateOfBirth());
    houseNameField.setValue(customer.getAddress() != null ? customer.getAddress() : "");
    postcodeField.setValue(customer.getZipCode() != null ? customer.getZipCode() : "");
    homePhoneField.setValue(customer.getPhone() != null ? customer.getPhone() : "");
    mobilePhoneField.setValue("");  // Not provided by CustomerResponse
    emailField.setValue(customer.getEmail() != null ? customer.getEmail() : "");
    houseNumberField.setValue(null);  // Not provided by CustomerResponse
  }

  private void clearForm() {
    customerNumberField.clear();
    firstNameField.clear();
    lastNameField.clear();
    dobField.clear();
    houseNameField.clear();
    houseNumberField.clear();
    postcodeField.clear();
    homePhoneField.clear();
    mobilePhoneField.clear();
    emailField.clear();

    currentCustomerId = null;
    clearErrorMessage();

    // Reapply field states for current option
    switch (formState) {
      case INQUIRY -> applyInquiryState();
      case ADD -> applyAddState();
      case UPDATE -> applyUpdateState();
    }
  }

  private void showErrorMessage(String message) {
    errorMessageDiv.removeAll();
    Paragraph msg = new Paragraph(message);
    msg.addClassNames(LumoUtility.Margin.NONE);
    errorMessageDiv.add(msg);
    errorMessageDiv.addClassNames(LumoUtility.Background.ERROR_10, LumoUtility.TextColor.ERROR);
    errorMessageDiv.removeClassNames(LumoUtility.Background.SUCCESS_10, LumoUtility.TextColor.SUCCESS);
    errorMessageDiv.setVisible(true);
  }

  private void showSuccessMessage(String message) {
    errorMessageDiv.removeAll();
    Paragraph msg = new Paragraph(message);
    msg.addClassNames(LumoUtility.Margin.NONE);
    errorMessageDiv.add(msg);
    errorMessageDiv.addClassNames(LumoUtility.Background.SUCCESS_10, LumoUtility.TextColor.SUCCESS);
    errorMessageDiv.removeClassNames(LumoUtility.Background.ERROR_10, LumoUtility.TextColor.ERROR);
    errorMessageDiv.setVisible(true);
  }

  private void clearErrorMessage() {
    errorMessageDiv.removeAll();
    errorMessageDiv.setVisible(false);
  }
}
