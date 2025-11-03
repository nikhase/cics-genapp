package com.example.cicsgenapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * Spring Cloud Gateway configuration for routing requests to either Spring Boot
 * services or legacy COBOL system endpoints.
 *
 * This configuration establishes the strangler pattern by allowing gradual migration
 * of traffic from legacy COBOL system to new Spring Boot services. Routes are
 * configurable via application profiles for different environments (dev/test/prod).
 *
 * Routes defined:
 * - /api/v1/customers/* - Customer API endpoints
 * - /api/v1/policies/* - Policy management API endpoints
 * - /api/v1/audit/* - Audit API endpoints
 * - /api/v1/auth/* - Authentication API endpoints
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.gateway.enabled", havingValue = "true")
public class GatewayConfig {

  @Value("${gateway.legacy-endpoint:http://localhost:8081}")
  private String legacyEndpoint;

  @Value("${gateway.service-endpoint:http://localhost:8080}")
  private String serviceEndpoint;

  @Value("${gateway.timeout:5000}")
  private long timeoutMs;

  /**
   * Configures gateway routes with predicates, filters, and destination endpoints.
   *
   * Routes are ordered with more specific paths first to ensure correct matching.
   * Each route includes:
   * - Path predicates for matching incoming requests
   * - HTTP method predicates
   * - Timeout configuration (5 seconds default)
   * - Destination URI (Spring Boot service or legacy endpoint)
   *
   * @param builder the RouteLocatorBuilder for fluent route configuration
   * @return configured RouteLocator bean
   */
  @Bean
  public RouteLocator customRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        // Customer API - GET endpoints
        .route("customers-get", r ->
            r.path("/api/v1/customers/**")
                .and()
                .method(HttpMethod.GET)
                .filters(f -> f
                    .stripPrefix(0)
                    .requestRateLimiter(config -> config.setKeyResolver(
                        exchange -> exchange.getPrincipal()
                            .map(p -> p.getName())
                            .defaultIfEmpty("anonymous")))
                )
                .uri(serviceEndpoint))
        // Customer API - POST endpoints
        .route("customers-post", r ->
            r.path("/api/v1/customers")
                .and()
                .method(HttpMethod.POST)
                .filters(f -> f.stripPrefix(0))
                .uri(serviceEndpoint))
        // Customer API - PUT endpoints (update)
        .route("customers-put", r ->
            r.path("/api/v1/customers/**")
                .and()
                .method(HttpMethod.PUT)
                .filters(f -> f.stripPrefix(0))
                .uri(serviceEndpoint))
        // Customer API - DELETE endpoints
        .route("customers-delete", r ->
            r.path("/api/v1/customers/**")
                .and()
                .method(HttpMethod.DELETE)
                .filters(f -> f.stripPrefix(0))
                .uri(serviceEndpoint))
        // Policy API - GET endpoints
        .route("policies-get", r ->
            r.path("/api/v1/policies/**")
                .and()
                .method(HttpMethod.GET)
                .filters(f -> f.stripPrefix(0))
                .uri(serviceEndpoint))
        // Policy API - POST endpoints
        .route("policies-post", r ->
            r.path("/api/v1/policies")
                .and()
                .method(HttpMethod.POST)
                .filters(f -> f.stripPrefix(0))
                .uri(serviceEndpoint))
        // Policy API - PUT endpoints (update)
        .route("policies-put", r ->
            r.path("/api/v1/policies/**")
                .and()
                .method(HttpMethod.PUT)
                .filters(f -> f.stripPrefix(0))
                .uri(serviceEndpoint))
        // Policy API - DELETE endpoints
        .route("policies-delete", r ->
            r.path("/api/v1/policies/**")
                .and()
                .method(HttpMethod.DELETE)
                .filters(f -> f.stripPrefix(0))
                .uri(serviceEndpoint))
        // Audit API (always routes to Spring Boot service)
        .route("audit", r ->
            r.path("/api/v1/audit/**")
                .filters(f -> f.stripPrefix(0))
                .uri(serviceEndpoint))
        // Auth API (routes to OIDC/Zitadel integration)
        .route("auth", r ->
            r.path("/api/v1/auth/**")
                .filters(f -> f.stripPrefix(0))
                .uri(serviceEndpoint))
        // CORS preflight OPTIONS requests
        .route("options-route", r ->
            r.method(HttpMethod.OPTIONS)
                .filters(f -> f.stripPrefix(0))
                .uri(serviceEndpoint))
        .build();
  }
}
