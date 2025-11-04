package com.example.cicsgenapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test-specific Spring Security configuration for unit and integration tests.
 *
 * <p>This configuration:
 * <ul>
 *   <li>Preserves RBAC (Role-Based Access Control) testing with @WithMockUser
 *   <li>Enables method-level security (@PreAuthorize, @RolesAllowed, etc.)
 *   <li>Disables CSRF/CORS for easier testing
 *   <li>Supports testing authenticated and unauthenticated requests
 *   <li>Allows testing role-based endpoint access
 * </ul>
 *
 * <p><b>Key Difference from Production:</b> Uses a simplified authorization rule
 * (all authenticated requests allowed) while still validating role checks via
 * @PreAuthorize annotations.
 *
 * <p>Applied only when using @TestSecurityContext or when test profile is active.
 *
 * @author Development Team
 * @version 1.0.0
 */
@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class TestSecurityConfig {

  /**
   * Configures the test security filter chain.
   *
   * <p>Enables:
   * <ul>
   *   <li>HTTP Basic authentication (for MockMvc requests)
   *   <li>Session management with IF_REQUIRED policy
   *   <li>Method-level security via @PreAuthorize annotations
   *   <li>Disabled CSRF/CORS for test simplicity
   *   <li>All authenticated requests allowed at endpoint level
   * </ul>
   *
   * <p><b>Why this works:</b> The security filter chain allows any authenticated
   * request, but @PreAuthorize annotations on methods still enforce role checks.
   * This allows us to test:
   * <ul>
   *   <li>Authentication (401 without credentials)
   *   <li>Authorization (403 with wrong role)
   *   <li>Happy path (200/201 with correct role)
   * </ul>
   *
   * @param http HTTP security configuration
   * @return configured security filter chain
   * @throws Exception if security configuration fails
   */
  @Bean
  public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
    http.httpBasic(basic -> {})
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(
            org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(authz -> authz
            // Allow public endpoints
            .requestMatchers("/login", "/logout").permitAll()
            .requestMatchers("/actuator/health", "/actuator/health/live", "/actuator/health/ready")
            .permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
            .permitAll()
            .requestMatchers("/VAADIN/**").permitAll()
            // All other requests require authentication
            // Role checks are enforced via @PreAuthorize on methods
            .anyRequest().authenticated());

    return http.build();
  }

  /**
   * Provides test users with various roles for RBAC testing.
   *
   * <p>Test Users:
   * <ul>
   *   <li><b>admin:</b> ADMIN, USER roles (full access)
   *   <li><b>csr:</b> CUSTOMER_SERVICE_AGENT role (CSR operations - create, update)
   *   <li><b>compliance:</b> COMPLIANCE_OFFICER role (delete operations)
   *   <li><b>user:</b> USER role (limited read-only access)
   *   <li><b>noRole:</b> No roles (authentication-only user)
   * </ul>
   *
   * @return UserDetailsService with test users
   */
  @Bean
  @Primary
  public UserDetailsService testUserDetailsService() {
    UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("admin123"))
        .roles("ADMIN", "USER")
        .build();

    UserDetails csr = User.builder()
        .username("csr")
        .password(passwordEncoder().encode("csr123"))
        .roles("CUSTOMER_SERVICE_AGENT")
        .build();

    UserDetails compliance = User.builder()
        .username("compliance")
        .password(passwordEncoder().encode("compliance123"))
        .roles("COMPLIANCE_OFFICER")
        .build();

    UserDetails user = User.builder()
        .username("user")
        .password(passwordEncoder().encode("user123"))
        .roles("USER")
        .build();

    UserDetails noRole = User.builder()
        .username("noRole")
        .password(passwordEncoder().encode("noRole123"))
        .roles("NONE")
        .build();

    return new InMemoryUserDetailsManager(admin, csr, compliance, user, noRole);
  }

  /**
   * Password encoder using BCrypt (matches production).
   *
   * @return BCryptPasswordEncoder bean
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
