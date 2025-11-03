package com.example.cicsgenapp.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * Gateway filter for Unleash feature toggle integration.
 *
 * This filter checks feature toggle state before routing requests to determine
 * whether to route to the new Spring Boot service or the legacy COBOL system.
 *
 * Toggles controlled:
 * - customer-api-enabled: Controls routing for /api/v1/customers/*
 * - policy-api-enabled: Controls routing for /api/v1/policies/*
 *
 * Behavior:
 * - If toggle enabled (true): route to Spring Boot service (http://localhost:8080)
 * - If toggle disabled (false): route to legacy COBOL system (http://localhost:8081)
 * - If toggle missing: default to Spring Boot (fail-safe to new system)
 *
 * Note: This is a placeholder implementation. In the full solution (Story 1.6),
 * this will integrate with Unleash client for dynamic feature toggles.
 * Toggles can be percentage-based (0%, 10%, 50%, 100%) for canary deployments.
 */
@Component
public class FeatureToggleFilter extends AbstractGatewayFilterFactory<FeatureToggleFilter.Config> {

  private static final Logger LOG = LoggerFactory.getLogger(FeatureToggleFilter.class);

  // Placeholder for Unleash client (will be injected in Story 1.6)
  // private final UnleashClient unleashClient;

  public FeatureToggleFilter() {
    super(Config.class);
    // In Story 1.6, unleashClient will be injected here
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String path = exchange.getRequest().getPath().toString();

      // Check if this request should use feature toggle routing
      if (shouldApplyToggleRouting(path)) {
        String toggleName = getToggleName(path);
        boolean toggleEnabled = checkFeatureToggle(toggleName);

        LOG.debug("Feature toggle '{}' state: {} for path: {}",
            toggleName, toggleEnabled, path);

        // Log toggle decision (for debugging routing decisions)
        String destination = toggleEnabled ? "Spring Boot" : "Legacy COBOL";
        LOG.info("Routing {} to {} (toggle={}, percentage=100%)", path, destination, toggleEnabled);

        // In Story 1.6, this will be enhanced with:
        // - Unleash client integration
        // - Percentage-based routing (canary deployments)
        // - A/B testing support
      }

      return chain.filter(exchange);
    };
  }

  /**
   * Determines if feature toggle routing should be applied to this request.
   *
   * @param path request path
   * @return true if path matches customer or policy endpoints
   */
  private boolean shouldApplyToggleRouting(String path) {
    return path.startsWith("/api/v1/customers") || path.startsWith("/api/v1/policies");
  }

  /**
   * Maps request path to feature toggle name.
   *
   * @param path request path
   * @return toggle name (e.g., "customer-api-enabled")
   */
  private String getToggleName(String path) {
    if (path.startsWith("/api/v1/customers")) {
      return "customer-api-enabled";
    } else if (path.startsWith("/api/v1/policies")) {
      return "policy-api-enabled";
    }
    return "unknown-api";
  }

  /**
   * Checks feature toggle state.
   *
   * Placeholder implementation that defaults to Spring Boot (fail-safe).
   * In Story 1.6, this will use Unleash client:
   *
   * <code>
   * return unleashClient.isEnabled(toggleName, false); // false = fail-safe default
   * </code>
   *
   * @param toggleName name of the toggle to check
   * @return true if toggle is enabled (route to Spring Boot), false for legacy
   */
  private boolean checkFeatureToggle(String toggleName) {
    // Placeholder: Default to Spring Boot service (fail-safe to new system)
    // In Story 1.6, this will query Unleash:
    // return unleashClient.isEnabled(toggleName, false);
    LOG.debug("Feature toggle placeholder: {} = true (default to Spring Boot)", toggleName);
    return true;
  }

  /**
   * Configuration for FeatureToggleFilter.
   */
  public static class Config {
    // Configuration properties can be added here for future enhancements
    // Example: canaryPercentage, toggleStrategy, etc.
  }

  @Override
  public String[] shortcutFieldOrder() {
    return new String[]{};
  }
}
