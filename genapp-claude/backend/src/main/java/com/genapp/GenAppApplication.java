package com.genapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * GenApp Modernized Backend Application
 *
 * Spring Boot 3 + Java 21 REST API for GenApp (CICS insurance application modernization).
 * Replaces the three-tier COBOL architecture with modern microservices patterns.
 *
 * Old COBOL Architecture:
 *   3270 Terminal → BMS Maps → Presentation (lgtestc1) → Business Logic (lgacus01) → Data (lgacdb01 + lgacvs01) → Db2 + VSAM
 *
 * New Architecture:
 *   Browser → React UI → REST API (@RestController) → Business Logic (@Service) → Data Layer (JPA) → PostgreSQL
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@SpringBootApplication
public class GenAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenAppApplication.class, args);
    }

    /**
     * CORS Configuration for React frontend
     *
     * Allows the React frontend (running on different port/host) to call the backend API.
     * In production, configure specific allowed origins instead of "*".
     */
    @Configuration
    public static class CorsConfiguration implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000", "http://localhost:5173") // React dev servers
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        }
    }
}
