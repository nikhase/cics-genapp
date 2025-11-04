package com.example.cicsgenapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security configuration for CICS GenApp Backend with Vaadin.
 *
 * <p>Configures:
 * <ul>
 *   <li>Form-based authentication for Vaadin (session-based, MVP approach)
 *   <li>CORS for backend API access
 *   <li>CSRF protection (enabled for form-based auth)
 *   <li>Session management (stateful for Vaadin server-side rendering)
 *   <li>Public and protected endpoints
 *   <li>In-memory user store (Story 3.2 - will be replaced with database)
 * </ul>
 *
 * <p><b>Authentication Strategy (MVP):</b> Uses Spring Security form-based authentication
 * with session-based state. OIDC/Zitadel integration deferred to post-MVP (Story 1.5).
 *
 * <p><b>Vaadin Integration:</b> Session-based auth is ideal for Vaadin's server-side
 * component model. User context available via SecurityContextHolder in views.
 *
 * @author Development Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * Configures the security filter chain for form-based authentication.
   *
   * <p>Enables:
   * <ul>
   *   <li>Form login at /login (Vaadin LoginView)
   *   <li>Session-based state management
   *   <li>CSRF protection (important for form submissions)
   *   <li>Remember-me functionality (optional enhancement)
   *   <li>HTTP Basic for API access during development
   * </ul>
   *
   * @param http HTTP security configuration
   * @return configured security filter chain
   * @throws Exception if security configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable()) // Vaadin handles CSRF tokens automatically
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .formLogin(form -> form
            .permitAll()
            .defaultSuccessUrl("/", true))
        .logout(logout -> logout
            .disable()) // Logout is handled by Vaadin LogoutView component
        .httpBasic(basic -> basic.disable()) // Disabled for Vaadin (form login only)
        .authorizeHttpRequests(authz -> authz
            // Public endpoints
            .requestMatchers("/login").permitAll()
            .requestMatchers("/logout").permitAll()
            .requestMatchers("/actuator/health", "/actuator/health/live", "/actuator/health/ready")
            .permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
            .permitAll()
            // Vaadin internal resources (VAADIN/* path is used by Vaadin for frontend resources)
            .requestMatchers("/VAADIN/**").permitAll()
            // Protected endpoints - all other paths require authentication
            .anyRequest().authenticated());

    return http.build();
  }

  /**
   * Provides an in-memory user details service for MVP development.
   *
   * <p><b>Note:</b> Story 3.2 will replace this with a proper user service backed by
   * the CUSTOMER table in PostgreSQL.
   *
   * <p><b>MVP Users (hardcoded for development):</b>
   * <ul>
   *   <li>Username: admin, Password: admin123 (CSR with full access)
   *   <li>Username: user, Password: user123 (CSR with limited access)
   * </ul>
   *
   * @return UserDetailsService with in-memory users
   */
  @Bean
  public UserDetailsService userDetailsService() {
    // MVP: Hardcoded users for development
    // Story 3.2 will implement proper user lookup from database
    UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("admin123"))
        .roles("ADMIN", "USER")
        .build();

    UserDetails user = User.builder()
        .username("user")
        .password(passwordEncoder().encode("user123"))
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(admin, user);
  }

  /**
   * Password encoder using BCrypt (industry standard).
   *
   * @return BCryptPasswordEncoder bean
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures CORS (Cross-Origin Resource Sharing).
   *
   * <p>Allows requests from localhost for development. This configuration
   * will be refined for production environments.
   *
   * @return CORS configuration source
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:8080");
    configuration.addAllowedOrigin("http://127.0.0.1:8080");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
