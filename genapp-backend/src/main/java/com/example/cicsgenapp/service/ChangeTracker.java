package com.example.cicsgenapp.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.example.cicsgenapp.dto.UpdateCustomerRequest;
import com.example.cicsgenapp.entity.Customer;

/**
 * Utility class for tracking field changes during update operations.
 *
 * <p>Compares before/after values and produces a change map suitable for audit logging.
 * Only tracks fields that actually changed.
 *
 * <p>Example output:
 * <pre>
 * {
 *   "email": {"before": "old@example.com", "after": "new@example.com"},
 *   "phone": {"before": "+1-555-1234", "after": "+1-555-5678"}
 * }
 * </pre>
 */
public class ChangeTracker {

  private final Map<String, Map<String, Object>> changes = new HashMap<>();

  /**
   * Tracks changes from an UpdateCustomerRequest applied to a Customer entity.
   *
   * @param before the original customer entity
   * @param request the update request with new values
   * @return a ChangeTracker with tracked changes
   */
  public static ChangeTracker trackChanges(Customer before, UpdateCustomerRequest request) {
    ChangeTracker tracker = new ChangeTracker();

    // Track firstName change
    if (request.getFirstName() != null) {
      tracker.trackChange("firstName", before.getFirstName(), request.getFirstName());
    }

    // Track lastName change
    if (request.getLastName() != null) {
      tracker.trackChange("lastName", before.getLastName(), request.getLastName());
    }

    // Track dateOfBirth change
    if (request.getDateOfBirth() != null) {
      tracker.trackChange("dateOfBirth", before.getDateOfBirth(), request.getDateOfBirth());
    }

    // Track email change
    if (request.getEmail() != null) {
      tracker.trackChange("email", before.getEmail(), request.getEmail());
    }

    // Track phone change
    if (request.getPhone() != null) {
      tracker.trackChange("phone", before.getPhone(), request.getPhone());
    }

    // Track address change
    if (request.getAddress() != null) {
      tracker.trackChange("address", before.getAddress(), request.getAddress());
    }

    // Track city change
    if (request.getCity() != null) {
      tracker.trackChange("city", before.getCity(), request.getCity());
    }

    // Track state change
    if (request.getState() != null) {
      tracker.trackChange("state", before.getState(), request.getState());
    }

    // Track zipCode change
    if (request.getZipCode() != null) {
      tracker.trackChange("zipCode", before.getZipCode(), request.getZipCode());
    }

    return tracker;
  }

  /**
   * Tracks a single field change if the before and after values differ.
   *
   * @param fieldName the name of the field
   * @param beforeValue the value before the change
   * @param afterValue the value after the change
   */
  private void trackChange(String fieldName, Object beforeValue, Object afterValue) {
    // Only track if values actually changed
    if (!Objects.equals(beforeValue, afterValue)) {
      Map<String, Object> change = new HashMap<>();
      change.put("before", beforeValue);
      change.put("after", afterValue);
      this.changes.put(fieldName, change);
    }
  }

  /**
   * Gets the map of tracked changes.
   *
   * @return a map of field name to change map (before/after values)
   */
  public Map<String, Map<String, Object>> getChanges() {
    return changes;
  }

  /**
   * Checks if there are any tracked changes.
   *
   * @return true if any changes were tracked, false otherwise
   */
  public boolean hasChanges() {
    return !changes.isEmpty();
  }

  /**
   * Gets a human-readable summary of changes.
   *
   * @return a comma-separated list of changed field names
   */
  public String getSummary() {
    if (changes.isEmpty()) {
      return "No changes";
    }
    return String.join(", ", changes.keySet());
  }

  @Override
  public String toString() {
    return "ChangeTracker{" + "changes=" + changes + '}';
  }
}
