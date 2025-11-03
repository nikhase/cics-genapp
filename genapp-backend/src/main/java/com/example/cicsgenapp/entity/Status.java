package com.example.cicsgenapp.entity;

/**
 * Enumeration for Customer status values.
 * Soft-delete pattern: customers marked INACTIVE rather than hard-deleted.
 */
public enum Status {
  ACTIVE,
  INACTIVE
}
