package com.example.cicsgenapp.dto;

import com.example.cicsgenapp.entity.Status;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * DTO for search criteria used in customer search operations.
 *
 * <p>Encapsulates all filter, sort, and pagination parameters for customer search.
 * All fields are optional to support flexible search patterns.
 */
public class SearchCriteria {

  /** Search query string (searches firstName, lastName, email, phone) */
  private String query;

  /** Filter by customer status (ACTIVE, INACTIVE) */
  private Status status;

  /** Number of results per page (max 100) */
  @Min(value = 1, message = "Limit must be at least 1")
  @Max(value = 100, message = "Limit cannot exceed 100")
  private int limit = 50;

  /** Pagination offset (0-based) */
  @Min(value = 0, message = "Offset must be non-negative")
  private int offset = 0;

  /** Field to sort by (firstName, lastName, email, createdAt) */
  private String sortBy = "lastName";

  /** Sort direction (ASC or DESC) */
  private String sortOrder = "ASC";

  /**
   * Default constructor.
   */
  public SearchCriteria() {
  }

  /**
   * Constructor with all parameters.
   *
   * @param query search query string
   * @param status status filter
   * @param limit page size
   * @param offset pagination offset
   * @param sortBy sort field
   * @param sortOrder sort direction
   */
  public SearchCriteria(String query, Status status, int limit, int offset, String sortBy,
      String sortOrder) {
    this.query = query;
    this.status = status;
    this.limit = limit;
    this.offset = offset;
    this.sortBy = sortBy;
    this.sortOrder = sortOrder;
  }

  // Getters and Setters

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    // Enforce max limit of 100
    this.limit = limit > 100 ? 100 : Math.max(limit, 1);
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = Math.max(offset, 0);
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public String getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }

  @Override
  public String toString() {
    return "SearchCriteria{"
        + "query='" + query + '\''
        + ", status=" + status
        + ", limit=" + limit
        + ", offset=" + offset
        + ", sortBy='" + sortBy + '\''
        + ", sortOrder='" + sortOrder + '\''
        + '}';
  }
}
