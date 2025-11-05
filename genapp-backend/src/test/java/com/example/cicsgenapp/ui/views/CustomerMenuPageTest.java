package com.example.cicsgenapp.ui.views;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cicsgenapp.dto.CreateCustomerRequest;
import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.service.CustomerService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerMenuPageTest {

  private RecordingCustomerService customerService;

  private UI ui;
  private CustomerMenuPage page;

  @BeforeEach
  void setUp() {
    ui = new UI();
    UI.setCurrent(ui);
    customerService = new RecordingCustomerService();
    page = new CustomerMenuPage(customerService);
    ui.add(page);
  }

  @AfterEach
  void tearDown() {
    UI.setCurrent(null);
  }

  @Test
  void addCustomer_invalidDate_showsErrorAndSkipsServiceCall() throws Exception {
    setOption(2);
    getField("firstNameField", TextField.class).setValue("Jane");
    getField("lastNameField", TextField.class).setValue("Doe");
    getField("dobField", TextField.class).setValue("2025/12/01");
    getField("houseNameField", TextField.class).setValue("Main Street");
    getField("houseNumberField", NumberField.class).setValue(12d);
    getField("postcodeField", TextField.class).setValue("12345");
    getField("homePhoneField", TextField.class).setValue("+123456789");
    getField("emailField", TextField.class).setValue("jane.doe@example.com");

    getField("submitButton", Button.class).click();

    assertThat(customerService.getLastCreateRequest()).isNull();
    Div messageBox = getField("errorMessageDiv", Div.class);
    assertThat(messageBox.isVisible()).isTrue();
    String message = messageBox.getChildren()
        .filter(component -> component instanceof Paragraph)
        .map(component -> ((Paragraph) component).getText())
        .findFirst()
        .orElse("");
    assertThat(message).contains("YYYY-MM-DD");
  }

  @Test
  void addCustomer_validInput_invokesServiceWithParsedDate() throws Exception {
    setOption(2);
    TextField firstName = getField("firstNameField", TextField.class);
    TextField lastName = getField("lastNameField", TextField.class);
    TextField dob = getField("dobField", TextField.class);
    TextField houseName = getField("houseNameField", TextField.class);
    NumberField houseNumber = getField("houseNumberField", NumberField.class);
    TextField postcode = getField("postcodeField", TextField.class);
    TextField homePhone = getField("homePhoneField", TextField.class);
    TextField email = getField("emailField", TextField.class);

    firstName.setValue("Jane");
    lastName.setValue("Doe");
    dob.setValue("1990-05-15");
    houseName.setValue("Main Street");
    houseNumber.setValue(12d);
    postcode.setValue("12345");
    homePhone.setValue("+123456789");
    email.setValue("jane.doe@example.com");

    CustomerResponse created = new CustomerResponse();
    created.setCustomerId(UUID.randomUUID());
    customerService.setCreateResponse(created);

    getField("submitButton", Button.class).click();

    CreateCustomerRequest captured = customerService.getLastCreateRequest();
    assertThat(captured).isNotNull();
    assertThat(captured.getDateOfBirth())
        .isEqualTo(LocalDate.of(1990, 5, 15));
  }

  @Test
  void shortcutChangesSelectionRegardlessOfFocus() throws Exception {
    setOption(1);

    invokeHandleOptionShortcut(2);

    RadioButtonGroup<Integer> options = getField("optionGroup", RadioButtonGroup.class);
    assertThat(options.getValue()).isEqualTo(2);
  }

  private void setOption(int option) throws NoSuchFieldException, IllegalAccessException {
    RadioButtonGroup<Integer> options = getField("optionGroup", RadioButtonGroup.class);
    options.setValue(option);
  }

  @SuppressWarnings("unchecked")
  private <T> T getField(String name, Class<T> type)
      throws NoSuchFieldException, IllegalAccessException {
    Field field = CustomerMenuPage.class.getDeclaredField(name);
    field.setAccessible(true);
    return (T) field.get(page);
  }

  private void invokeHandleOptionShortcut(int option)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = CustomerMenuPage.class.getDeclaredMethod("handleOptionShortcut", int.class);
    method.setAccessible(true);
    method.invoke(page, option);
  }

  private static class RecordingCustomerService extends CustomerService {

    private CreateCustomerRequest lastCreateRequest;
    private CustomerResponse createResponse;

    RecordingCustomerService() {
      super(null, null);
    }

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
      this.lastCreateRequest = request;
      return createResponse;
    }

    CreateCustomerRequest getLastCreateRequest() {
      return lastCreateRequest;
    }

    void setCreateResponse(CustomerResponse response) {
      this.createResponse = response;
    }
  }
}
