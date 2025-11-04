package com.example.cicsgenapp.entity;

/**
 * Enumeration of insurance policy types.
 * Implements Story 3.7 - Policy List Page with Vaadin Grid
 */
public enum PolicyType {
  MOTOR("Motor", "M"),
  ENDOWMENT("Endowment", "E"),
  HOUSE("House", "H"),
  COMMERCIAL("Commercial", "C");

  private final String displayName;
  private final String abbreviation;

  PolicyType(String displayName, String abbreviation) {
    this.displayName = displayName;
    this.abbreviation = abbreviation;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getAbbreviation() {
    return abbreviation;
  }
}
