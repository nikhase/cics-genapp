package com.example.cicsgenapp.api;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoint for CICS GenApp Backend.
 *
 * <p>Provides basic health status and application information. Used by load balancers,
 * Kubernetes, and monitoring systems to verify application is running.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

  /**
   * Returns application health status.
   *
   * @return health status response
   */
  @GetMapping
  public ResponseEntity<Map<String, Object>> health() {
    Map<String, Object> response = new HashMap<>();
    response.put("status", "UP");
    response.put("timestamp", LocalDateTime.now());
    response.put("application", "CICS GenApp Backend");
    response.put("version", "1.0.0-SNAPSHOT");

    return ResponseEntity.ok(response);
  }
}
