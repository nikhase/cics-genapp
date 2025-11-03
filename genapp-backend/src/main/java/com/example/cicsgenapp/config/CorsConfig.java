package com.example.cicsgenapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * CORS (Cross-Origin Resource Sharing) configuration for the API gateway.
 *
 * This configuration is applied at the gateway level (not service level) to take
 * precedence over any service-level CORS configuration. It allows the React frontend
 * to make requests to the backend API from different origins depending on environment.
 *
 * CORS headers configured:
 * - Access-Control-Allow-Origin: Frontend URL (dev/test/prod specific)
 * - Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
 * - Access-Control-Allow-Headers: Content-Type, Authorization, X-Trace-Id, X-Requested-With
 * - Access-Control-Expose-Headers: X-Trace-Id (allows client to read correlation ID)
 * - Access-Control-Allow-Credentials: true (for cookies if needed)
 * - Access-Control-Max-Age: 3600 (1 hour cache for preflight)
 */
@Configuration
public class CorsConfig {

  @Value("${cors.allowed-origins:http://localhost:3000}")
  private String allowedOrigins;

  @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
  private String allowedMethods;

  @Value("${cors.allowed-headers:Content-Type,Authorization,X-Trace-Id,X-Requested-With}")
  private String allowedHeaders;

  @Value("${cors.exposed-headers:X-Trace-Id}")
  private String exposedHeaders;

  @Value("${cors.max-age:3600}")
  private Long maxAge;

  @Value("${cors.allow-credentials:true}")
  private Boolean allowCredentials;

  /**
   * Creates a CorsWebFilter for Spring Cloud Gateway.
   *
   * This filter intercepts all requests and adds appropriate CORS headers based on
   * the request origin. For development, it allows requests from http://localhost:3000.
   * For production, it restricts to the configured domain.
   *
   * @return CorsWebFilter bean
   */
  @Bean
  public CorsWebFilter corsWebFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = buildCorsConfiguration();

    // Apply CORS configuration to all paths
    source.registerCorsConfiguration("/**", config);

    return new CorsWebFilter(source);
  }

  /**
   * Builds the CORS configuration object.
   *
   * @return configured CorsConfiguration
   */
  private CorsConfiguration buildCorsConfiguration() {
    CorsConfiguration config = new CorsConfiguration();

    // Parse and set allowed origins
    // Supports comma-separated list for multiple origins
    String[] origins = allowedOrigins.split(",");
    for (String origin : origins) {
      config.addAllowedOrigin(origin.trim());
    }

    // Set allowed HTTP methods
    String[] methods = allowedMethods.split(",");
    for (String method : methods) {
      config.addAllowedMethod(method.trim());
    }

    // Set allowed request headers
    String[] headers = allowedHeaders.split(",");
    for (String header : headers) {
      config.addAllowedHeader(header.trim());
    }

    // Set headers that client can read from response
    String[] exposed = exposedHeaders.split(",");
    for (String header : exposed) {
      config.addExposedHeader(header.trim());
    }

    // Allow credentials (cookies, authorization headers)
    config.setAllowCredentials(allowCredentials);

    // Cache preflight response for 1 hour
    config.setMaxAge(maxAge);

    return config;
  }
}
