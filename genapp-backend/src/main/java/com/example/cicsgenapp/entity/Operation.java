package com.example.cicsgenapp.entity;

/**
 * Enumeration of audit-trackable operations.
 *
 * <p>Represents the type of operation performed on an entity. Used in AuditLog to track
 * what type of change occurred.
 */
public enum Operation {
  CREATE("Create"),
  READ("Read"),
  UPDATE("Update"),
  DELETE("Delete");

  private final String displayName;

  Operation(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
