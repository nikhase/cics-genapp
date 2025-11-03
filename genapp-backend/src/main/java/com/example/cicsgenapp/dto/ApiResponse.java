package com.example.cicsgenapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic API response wrapper for consistent response structure.
 *
 * <p>Wraps response data with metadata including timestamp, version, and trace ID.
 * Used in all API endpoints to provide consistent response format.
 *
 * @param <T> the type of response data
 */
@Schema(
    title = "API Response",
    description = "Standard API response wrapper containing data and metadata for all endpoints")
public class ApiResponse<T> {

  @JsonProperty("data")
  @Schema(description = "Response payload (varies by endpoint)")
  private T data;

  @JsonProperty("metadata")
  @Schema(description = "Response metadata including timestamp, version, and operation details")
  private Map<String, Object> metadata;

  /**
   * Default constructor for JSON serialization.
   */
  public ApiResponse() {
    this.metadata = new HashMap<>();
  }

  /**
   * Constructor with data and metadata.
   *
   * @param data the response data
   * @param metadata the metadata map
   */
  public ApiResponse(T data, Map<String, Object> metadata) {
    this.data = data;
    this.metadata = metadata != null ? metadata : new HashMap<>();
    initializeMetadata();
  }

  /**
   * Constructor with just data (metadata will be auto-initialized).
   *
   * @param data the response data
   */
  public ApiResponse(T data) {
    this.data = data;
    this.metadata = new HashMap<>();
    initializeMetadata();
  }

  /**
   * Initializes standard metadata fields.
   */
  private void initializeMetadata() {
    if (!metadata.containsKey("timestamp")) {
      metadata.put("timestamp", LocalDateTime.now());
    }
    if (!metadata.containsKey("version")) {
      metadata.put("version", "v1");
    }
  }

  /**
   * Adds a custom metadata field.
   *
   * @param key the metadata key
   * @param value the metadata value
   * @return this ApiResponse for method chaining
   */
  public ApiResponse<T> withMetadata(String key, Object value) {
    this.metadata.put(key, value);
    return this;
  }

  // Getters and setters

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  @Override
  public String toString() {
    return "ApiResponse{"
        + "data=" + data
        + ", metadata=" + metadata
        + '}';
  }
}
