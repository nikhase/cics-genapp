package com.example.cicsgenapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for customer soft-delete request.
 *
 * <p>Accepts DELETE request body with optional deletion reason. The reason field is optional
 * but recommended for audit and compliance purposes. Used to record why a customer was deleted.
 */
public class DeleteCustomerRequest {

  @Size(max = 500, message = "Deletion reason must not exceed 500 characters")
  @JsonProperty("reason")
  private String reason;

  /**
   * Default constructor for JSON deserialization.
   */
  public DeleteCustomerRequest() {
  }

  /**
   * Constructor with deletion reason.
   *
   * @param reason optional deletion reason
   */
  public DeleteCustomerRequest(String reason) {
    this.reason = reason;
  }

  // Getters and setters

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  @Override
  public String toString() {
    return "DeleteCustomerRequest{"
        + "reason='" + reason + '\''
        + '}';
  }
}
