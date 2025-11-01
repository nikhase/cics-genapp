package com.example.cicsgenapp.config;

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
 *   <li>Public and protected endpoints
 * </ul>
 *
 * @author Development Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * Configures the security filter chain.
   *
   * <p>Allows all requests to pass through initially. Future PRs will add OIDC
   * authentication, JWT validation, and role-based access control.
   *
   * @param http HTTP security configuration
   * @return configured security filter chain
   * @throws Exception if security configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/actuator/health", "/actuator/health/live", "/actuator/health/ready")
        .permitAll()
        .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
        .permitAll()
        .anyRequest()
        .permitAll(); // Temporary: allow all requests for development

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
