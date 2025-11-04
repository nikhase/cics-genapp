package com.example.cicsgenapp.entity;

/**
 * Enumeration of insurance policy status values.
 * Implements Story 3.7 - Policy List Page with Vaadin Grid
 */
public enum PolicyStatus {
  ACTIVE("Active"),
  LAPSED("Lapsed"),
  RENEWED("Renewed");

  private final String displayName;

  PolicyStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
