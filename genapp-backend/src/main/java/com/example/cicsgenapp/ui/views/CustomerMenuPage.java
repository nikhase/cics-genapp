package com.example.cicsgenapp.ui.views;

import com.example.cicsgenapp.dto.CreateCustomerRequest;
import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.dto.UpdateCustomerRequest;
import com.example.cicsgenapp.exception.CustomerAlreadyExistsException;
import com.example.cicsgenapp.exception.ResourceNotFoundException;
import com.example.cicsgenapp.service.CustomerService;
import com.example.cicsgenapp.ui.components.BreadcrumbNavigation;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
@Route(value = "customers/menu", layout = com.example.cicsgenapp.ui.layouts.MainLayout.class)
@PageTitle("Customer Menu - CICS GenApp")
public class CustomerMenuPage extends VerticalLayout {

  private static final Pattern HOUSE_NUMBER_PATTERN = Pattern.compile("^(\\d+)\\s+(.*)$");
  private static final Pattern DOB_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
  private static final DateTimeFormatter DOB_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  private final CustomerService customerService;

  // State management
  private int selectedOption = 1;  // Default to Inquiry
  private UUID currentCustomerId;
  private FormState formState = FormState.INQUIRY;

  // Menu section
  private RadioButtonGroup<Integer> optionGroup;
  private Paragraph menuDescription;

  // Form section
  private H3 formTitle;
  private TextField customerNumberField;
  private TextField firstNameField;
  private TextField lastNameField;
  private TextField dobField;
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

    BreadcrumbNavigation breadcrumbs = new BreadcrumbNavigation();
    breadcrumbs.setItems(
        new BreadcrumbNavigation.Item("Dashboard", ""),
        new BreadcrumbNavigation.Item("Customer Menu", null)
    );
    add(breadcrumbs);

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
    registerKeyboardShortcuts();
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

    menuDescription = new Paragraph();
    menuDescription.addClassNames(LumoUtility.FontSize.SMALL);
    updateMenuDescription();

    descriptionBox.add(menuDescription);
    menu.add(descriptionBox);

    menu.add(createShortcutHelpSection());

    return menu;
  }

  private void updateMenuDescription() {
    if (menuDescription == null) {
      return;
    }
    switch (selectedOption) {
      case 1:
        menuDescription.setText("Enter a customer number to look up and view customer details. All fields will be read-only.");
        break;
      case 2:
        menuDescription.setText("Fill in the customer details below to create a new customer record.");
        break;
      case 4:
        menuDescription.setText("Enter a customer number to load the record, then modify the details and save changes.");
        break;
      default:
        menuDescription.setText("");
        break;
    }
  }

  private Div createShortcutHelpSection() {
    Div wrapper = new Div();
    wrapper.addClassNames(
        LumoUtility.Margin.Top.LARGE,
        LumoUtility.Padding.MEDIUM,
        LumoUtility.Background.CONTRAST_5,
        LumoUtility.BorderRadius.MEDIUM,
        LumoUtility.Display.BLOCK);

    H4 title = new H4("Keyboard Shortcuts");
    title.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.XSMALL);

    UnorderedList list = new UnorderedList();
    list.addClassNames(
        LumoUtility.Margin.Top.NONE,
        LumoUtility.Margin.Bottom.NONE,
        LumoUtility.Padding.Left.MEDIUM,
        LumoUtility.FontSize.SMALL);

    list.add(new ListItem("Ctrl/⌘ + 1 – Customer Inquiry"));
    list.add(new ListItem("Ctrl/⌘ + 2 – Customer Add"));
    list.add(new ListItem("Ctrl/⌘ + 4 – Customer Update"));
    list.add(new ListItem("Enter – Submit current option"));
    list.add(new ListItem("Esc – Clear the form"));

    wrapper.add(title, list);
    return wrapper;
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

    // Customer identification section
    customerNumberField = new TextField("Customer Number");
    customerNumberField.setPlaceholder("Enter customer ID (UUID or legacy number)");
    customerNumberField.setHelperText("Accepts generated customer ID (UUID format).");
    customerNumberField.setMaxLength(36);
    customerNumberField.setPattern("[0-9A-Fa-f-]{1,36}");
    customerNumberField.setRequired(true);
    customerNumberField.setClearButtonVisible(true);

    firstNameField = new TextField("First Name");
    firstNameField.setPlaceholder("First name");
    firstNameField.setMaxLength(10);

    lastNameField = new TextField("Last Name");
    lastNameField.setPlaceholder("Last name");
    lastNameField.setMaxLength(20);

    formLayout.add(customerNumberField, firstNameField, lastNameField);
    formLayout.setColspan(customerNumberField, 2);

    // Personal details section
    dobField = new TextField("Date of Birth");
    dobField.setPlaceholder("yyyy-mm-dd");
    dobField.setHelperText("Format: YYYY-MM-DD");
    dobField.setMaxLength(10);
    dobField.setPattern("\\d{4}-\\d{2}-\\d{2}");

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
    houseNumberField.setStep(1);

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
    formLayout.setColspan(emailField, 2);  // Email spans both columns

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

    // Update description to match current option
    updateMenuDescription();

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
    customerNumberField.setEnabled(true);
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
    lastNameField.setEnabled(true);

    dobField.setReadOnly(false);
    dobField.setRequiredIndicatorVisible(true);
    dobField.setEnabled(true);

    houseNameField.setReadOnly(false);
    houseNameField.setRequired(true);
    houseNameField.setEnabled(true);

    houseNumberField.setReadOnly(false);
    houseNumberField.setRequired(true);
    houseNumberField.setEnabled(true);

    postcodeField.setReadOnly(false);
    postcodeField.setRequired(true);
    postcodeField.setEnabled(true);

    homePhoneField.setReadOnly(false);
    homePhoneField.setEnabled(true);
    mobilePhoneField.setReadOnly(false);
    mobilePhoneField.setEnabled(true);
    emailField.setReadOnly(false);
    emailField.setEnabled(true);
  }

  private void applyUpdateState() {
    // Customer number: enabled for lookup
    customerNumberField.setReadOnly(false);
    customerNumberField.setEnabled(true);
    customerNumberField.setRequired(true);
    customerNumberField.focus();

    // Other fields: disabled until lookup succeeds
    disableEditFields();
  }

  private void disableEditFields() {
    firstNameField.setReadOnly(true);
    firstNameField.setEnabled(true);
    lastNameField.setReadOnly(true);
    lastNameField.setEnabled(true);
    dobField.setReadOnly(true);
    dobField.setEnabled(true);
    houseNameField.setReadOnly(true);
    houseNameField.setEnabled(true);
    houseNumberField.setReadOnly(true);
    houseNumberField.setEnabled(true);
    postcodeField.setReadOnly(true);
    postcodeField.setEnabled(true);
    homePhoneField.setReadOnly(true);
    homePhoneField.setEnabled(true);
    mobilePhoneField.setReadOnly(true);
    mobilePhoneField.setEnabled(true);
    emailField.setReadOnly(true);
    emailField.setEnabled(true);
  }

  private void enableEditFields() {
    firstNameField.setReadOnly(false);
    firstNameField.setEnabled(true);
    lastNameField.setReadOnly(false);
    lastNameField.setEnabled(true);
    dobField.setReadOnly(false);
    dobField.setEnabled(true);
    houseNameField.setReadOnly(false);
    houseNameField.setEnabled(true);
    houseNumberField.setReadOnly(false);
    houseNumberField.setEnabled(true);
    postcodeField.setReadOnly(false);
    postcodeField.setEnabled(true);
    homePhoneField.setReadOnly(false);
    homePhoneField.setEnabled(true);
    mobilePhoneField.setReadOnly(false);
    mobilePhoneField.setEnabled(true);
    emailField.setReadOnly(false);
    emailField.setEnabled(true);
  }

  private void setLoading(boolean active) {
    loadingIndicator.setVisible(active);
    submitButton.setEnabled(!active);
    clearButton.setEnabled(!active);
    if (optionGroup != null) {
      optionGroup.setEnabled(!active);
    }
  }

  private UUID resolveCustomerIdentifier(String rawValue) {
    try {
      return UUID.fromString(rawValue);
    } catch (IllegalArgumentException ex) {
      if (rawValue.matches("\\d+")) {
        showErrorMessage("Legacy numeric customer numbers are not yet mapped. Please enter the generated customer ID shown on the detail pages.");
      } else {
        showErrorMessage("Invalid customer number format. Please enter a valid UUID (e.g., 550e8400-e29b-41d4-a716-446655440000).");
      }
      return null;
    }
  }

  private String composeAddress() {
    String houseName = houseNameField.getValue() != null ? houseNameField.getValue().trim() : "";
    Double numberValue = houseNumberField.getValue();
    String houseNumber = numberValue != null ? String.valueOf(numberValue.intValue()) : "";

    if (!houseNumber.isEmpty() && !houseName.isEmpty()) {
      return houseNumber + " " + houseName;
    }
    if (!houseNumber.isEmpty()) {
      return houseNumber;
    }
    return houseName.isEmpty() ? null : houseName;
  }

  private String resolvePrimaryPhone() {
    String home = homePhoneField.getValue() != null ? homePhoneField.getValue().trim() : "";
    String mobile = mobilePhoneField.getValue() != null ? mobilePhoneField.getValue().trim() : "";

    if (!home.isEmpty()) {
      return home;
    }
    return mobile.isEmpty() ? null : mobile;
  }

  private LocalDate resolveDateOfBirth(boolean required) {
    String value = dobField.getValue() != null ? dobField.getValue().trim() : "";

    if (value.isEmpty()) {
      if (required) {
        showErrorMessage("Date of birth is required.");
        dobField.focus();
      }
      return null;
    }

    if (!DOB_PATTERN.matcher(value).matches()) {
      showErrorMessage("Date of birth must be in format YYYY-MM-DD.");
      dobField.focus();
      return null;
    }

    try {
      return LocalDate.parse(value, DOB_FORMATTER);
    } catch (DateTimeParseException ex) {
      showErrorMessage("Invalid date. Please enter a valid date in YYYY-MM-DD format.");
      dobField.focus();
      return null;
    }
  }

  private String blankToNull(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private String valueOrEmpty(String value) {
    return value == null ? "" : value;
  }

  private void registerKeyboardShortcuts() {
    registerShortcut(Key.of("1"), 1);
    registerShortcut(Key.of("2"), 2);
    registerShortcut(Key.of("4"), 4);
    Shortcuts.addShortcutListener(this, event -> clearForm(), Key.ESCAPE);
    Shortcuts.addShortcutListener(this, event -> {
      if (submitButton.isEnabled()) {
        submitButton.click();
      }
    }, Key.ENTER);
  }

  private void handleOptionShortcut(int option) {
    selectOption(option);
  }

  private void selectOption(int option) {
    if (optionGroup != null && optionGroup.isEnabled()) {
      optionGroup.setValue(option);
    }
  }

  private void registerShortcut(Key key, int option) {
    Shortcuts.addShortcutListener(this, event -> handleOptionShortcut(option), key)
        .withModifiers(KeyModifier.CONTROL);
    Shortcuts.addShortcutListener(this, event -> handleOptionShortcut(option), key)
        .withModifiers(KeyModifier.META);
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
      customerNumberField.focus();
      return;
    }

    UUID customerId = resolveCustomerIdentifier(customerNumber);
    if (customerId == null) {
      return;
    }

    setLoading(true);
    try {
      CustomerResponse customer = customerService.getCustomer(customerId);
      currentCustomerId = customer.getCustomerId();
      populateFormFields(customer);
      showSuccessMessage("Customer found successfully.");
    } catch (ResourceNotFoundException e) {
      showErrorMessage("Customer not found. Please verify the customer number.");
    } catch (Exception e) {
      showErrorMessage("Error looking up customer: " + e.getMessage());
    } finally {
      setLoading(false);
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

    setLoading(true);

    try {
      LocalDate dateOfBirth = resolveDateOfBirth(true);
      if (dateOfBirth == null) {
        return;
      }

      CreateCustomerRequest request = new CreateCustomerRequest();
      request.setFirstName(firstNameField.getValue().trim());
      request.setLastName(lastNameField.getValue().trim());
      request.setDateOfBirth(dateOfBirth);
      request.setAddress(composeAddress());
      request.setCity(null);  // City/state not collected on SSC1 screen
      request.setState(null);
      request.setZipCode(postcodeField.getValue().trim());
      request.setPhone(resolvePrimaryPhone());
      request.setEmail(blankToNull(emailField.getValue()));

      CustomerResponse created = customerService.createCustomer(request);
      currentCustomerId = created.getCustomerId();
      populateFormFields(created);
      showSuccessMessage("Customer created successfully. ID: " + created.getCustomerId());
      clearForm();
    } catch (CustomerAlreadyExistsException e) {
      showErrorMessage("A customer with this email already exists. Please use a different email address.");
    } catch (Exception e) {
      showErrorMessage("Error creating customer: " + e.getMessage());
    } finally {
      setLoading(false);
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
      UUID customerId = resolveCustomerIdentifier(customerNumber);
      if (customerId == null) {
        return;
      }

      setLoading(true);

      try {
        CustomerResponse customer = customerService.getCustomer(customerId);
        currentCustomerId = customer.getCustomerId();
        populateFormFields(customer);
        enableEditFields();
        submitButton.setText("Save Changes");
        showSuccessMessage("Customer loaded. You can now modify the details.");
      } catch (ResourceNotFoundException e) {
        showErrorMessage("Customer not found. Please verify the customer number.");
      } catch (Exception e) {
        showErrorMessage("Error looking up customer: " + e.getMessage());
      } finally {
        setLoading(false);
      }
    } else {
      // Phase 2: Update customer
      setLoading(true);

      try {
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

        LocalDate dateOfBirth = resolveDateOfBirth(true);
        if (dateOfBirth == null) {
          return;
        }

        UpdateCustomerRequest request = new UpdateCustomerRequest();
        request.setFirstName(firstNameField.getValue().trim());
        request.setLastName(lastNameField.getValue().trim());
        request.setDateOfBirth(dateOfBirth);
        request.setAddress(composeAddress());
        request.setCity(null);
        request.setState(null);
        request.setZipCode(postcodeField.getValue().trim());
        request.setPhone(resolvePrimaryPhone());
        request.setEmail(blankToNull(emailField.getValue()));

        CustomerResponse updated = customerService.updateCustomer(currentCustomerId, request);
        populateFormFields(updated);
        showSuccessMessage("Customer updated successfully.");
        disableEditFields();
        submitButton.setText("Update Customer");
      } catch (CustomerAlreadyExistsException e) {
        showErrorMessage("A customer with this email already exists. Please use a different email address.");
      } catch (Exception e) {
        showErrorMessage("Error updating customer: " + e.getMessage());
      } finally {
        setLoading(false);
      }
    }
  }

  private void populateFormFields(CustomerResponse customer) {
    if (customer.getCustomerId() != null) {
      customerNumberField.setValue(customer.getCustomerId().toString());
    }

    firstNameField.setValue(valueOrEmpty(customer.getFirstName()));
    lastNameField.setValue(valueOrEmpty(customer.getLastName()));

    dobField.setValue(customer.getDateOfBirth() != null
        ? DOB_FORMATTER.format(customer.getDateOfBirth())
        : "");

    String address = blankToNull(customer.getAddress());
    if (address != null) {
      Matcher matcher = HOUSE_NUMBER_PATTERN.matcher(address);
      if (matcher.matches()) {
        try {
          houseNumberField.setValue(Double.valueOf(matcher.group(1)));
        } catch (NumberFormatException ex) {
          houseNumberField.clear();
        }
        houseNameField.setValue(matcher.group(2));
      } else {
        houseNumberField.clear();
        houseNameField.setValue(address);
      }
    } else {
      houseNumberField.clear();
      houseNameField.clear();
    }

    postcodeField.setValue(valueOrEmpty(customer.getZipCode()));
    homePhoneField.setValue(valueOrEmpty(customer.getPhone()));
    mobilePhoneField.clear();  // Not provided separately
    emailField.setValue(valueOrEmpty(customer.getEmail()));
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

    switch (formState) {
      case INQUIRY -> submitButton.setText("Look Up");
      case ADD -> submitButton.setText("Create Customer");
      case UPDATE -> submitButton.setText("Update Customer");
      default -> {
      }
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
