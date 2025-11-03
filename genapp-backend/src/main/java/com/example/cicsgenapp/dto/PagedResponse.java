package com.example.cicsgenapp.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Generic paginated response wrapper.
 *
 * <p>Wraps paginated data with pagination metadata for REST API responses.
 *
 * @param <T> the type of data in the response
 */
public class PagedResponse<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  /** The data items in the current page */
  private List<T> data;

  /** Pagination metadata */
  private PaginationInfo pagination;

  /**
   * Default constructor.
   */
  public PagedResponse() {
  }

  /**
   * Constructor with data and pagination info.
   *
   * @param data list of items in current page
   * @param pagination pagination metadata
   */
  public PagedResponse(List<T> data, PaginationInfo pagination) {
    this.data = data;
    this.pagination = pagination;
  }

  // Getters and Setters

  public List<T> getData() {
    return data;
  }

  public void setData(List<T> data) {
    this.data = data;
  }

  public PaginationInfo getPagination() {
    return pagination;
  }

  public void setPagination(PaginationInfo pagination) {
    this.pagination = pagination;
  }

  @Override
  public String toString() {
    return "PagedResponse{"
        + "data size=" + (data != null ? data.size() : 0)
        + ", pagination=" + pagination
        + '}';
  }

  /**
   * Pagination metadata including limit, offset, total count, and hasMore flag.
   */
  public static class PaginationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Number of items per page */
    private int limit;

    /** Offset from start (0-based) */
    private int offset;

    /** Total number of items available */
    private long total;

    /** Whether there are more items beyond this page */
    private boolean hasMore;

    /**
     * Default constructor.
     */
    public PaginationInfo() {
    }

    /**
     * Constructor with all fields.
     *
     * @param limit page size
     * @param offset pagination offset
     * @param total total item count
     * @param hasMore whether more items exist
     */
    public PaginationInfo(int limit, int offset, long total, boolean hasMore) {
      this.limit = limit;
      this.offset = offset;
      this.total = total;
      this.hasMore = hasMore;
    }

    /**
     * Factory method to create PaginationInfo from search criteria and total count.
     *
     * @param limit page size
     * @param offset pagination offset
     * @param total total item count
     * @return PaginationInfo with hasMore calculated
     */
    public static PaginationInfo of(int limit, int offset, long total) {
      boolean hasMore = (offset + limit) < total;
      return new PaginationInfo(limit, offset, total, hasMore);
    }

    // Getters and Setters

    public int getLimit() {
      return limit;
    }

    public void setLimit(int limit) {
      this.limit = limit;
    }

    public int getOffset() {
      return offset;
    }

    public void setOffset(int offset) {
      this.offset = offset;
    }

    public long getTotal() {
      return total;
    }

    public void setTotal(long total) {
      this.total = total;
    }

    public boolean isHasMore() {
      return hasMore;
    }

    public void setHasMore(boolean hasMore) {
      this.hasMore = hasMore;
    }

    @Override
    public String toString() {
      return "PaginationInfo{"
          + "limit=" + limit
          + ", offset=" + offset
          + ", total=" + total
          + ", hasMore=" + hasMore
          + '}';
    }
  }
}
