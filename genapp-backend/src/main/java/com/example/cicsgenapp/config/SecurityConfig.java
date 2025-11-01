package com.example.cicsgenapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security configuration for CICS GenApp Backend.
 *
 * <p>Configures:
 * <ul>
 *   <li>CORS (Cross-Origin Resource Sharing) for frontend access
 *   <li>CSRF protection (disabled for stateless REST API)
 *   <li>Session management (stateless JWT-based authentication)
 *   <li>OIDC/OAuth2 resource server placeholder for Zitadel integration (Story 1.5)
 *   <li>Public and protected endpoints
 * </ul>
 *
 * <p><b>OIDC Integration (Story 1.5):</b> This configuration is prepared for OIDC/OAuth2
 * resource server pattern. JWT token validation will be added in Story 1.5 via
 * {@code spring.security.oauth2.resourceserver.jwt.issuer-uri} property pointing to Zitadel.
 *
 * @author Development Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityConfig.OidcProperties.class)
public class SecurityConfig {

  /**
   * OIDC/OAuth2 configuration properties (placeholder for Story 1.5).
   *
   * <p>Properties will be loaded from:
   * <ul>
   *   <li>application.yml: spring.security.oauth2.resourceserver.jwt.issuer-uri
   *   <li>application.yml: spring.security.oauth2.resourceserver.jwt.jwk-set-uri
   * </ul>
   */
  @ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
  public static class OidcProperties {
    private String issuerUri;
    private String jwkSetUri;

    public String getIssuerUri() {
      return issuerUri;
    }

    public void setIssuerUri(String issuerUri) {
      this.issuerUri = issuerUri;
    }

    public String getJwkSetUri() {
      return jwkSetUri;
    }

    public void setJwkSetUri(String jwkSetUri) {
      this.jwkSetUri = jwkSetUri;
    }
  }

  /**
   * Configures the security filter chain.
   *
   * <p>Currently allows all requests. Future Story 1.5 will add OIDC authentication,
   * JWT validation, and role-based access control via OAuth2 resource server pattern.
   *
   * @param http HTTP security configuration
   * @return configured security filter chain
   * @throws Exception if security configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // TODO: Story 1.5 - Add OAuth2 resource server configuration here
        // .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/actuator/health", "/actuator/health/live", "/actuator/health/ready")
            .permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
            .permitAll()
            .requestMatchers("/api/v1/auth/**")  // Auth endpoints (for Story 1.5 callback)
            .permitAll()
            .anyRequest()
            .permitAll()); // Temporary: allow all requests for development

    return http.build();
  }

  /**
   * Configures CORS (Cross-Origin Resource Sharing).
   *
   * <p>Allows requests from the React frontend and other authorized origins.
   * This configuration will be refined for production environments.
   *
   * @return CORS configuration source
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:3000"); // React dev server
    configuration.addAllowedOrigin("http://localhost:8080"); // Backend dev
    configuration.addAllowedOrigin("http://127.0.0.1:3000");
    configuration.addAllowedOrigin("http://127.0.0.1:8080");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
