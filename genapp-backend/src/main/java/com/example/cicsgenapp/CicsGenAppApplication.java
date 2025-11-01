package com.example.cicsgenapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for CICS GenApp Backend.
 *
 * <p>This application provides a modern Spring Boot REST API for the CICS GenApp modernization
 * project. It includes:
 *
 * <ul>
 *   <li>Customer management APIs
 *   <li>Policy management APIs
 *   <li>Spring Cloud Gateway for API routing
 *   <li>OIDC authentication with Zitadel
 *   <li>PostgreSQL data persistence
 *   <li>Structured logging and observability
 * </ul>
 *
 * @author Development Team
 * @version 1.0.0
 */
@SpringBootApplication
public class CicsGenAppApplication {

  /**
   * Main entry point for the Spring Boot application.
   *
   * @param args command-line arguments (Spring profiles, server port, etc.)
   */
  public static void main(String[] args) {
    SpringApplication.run(CicsGenAppApplication.class, args);
  }
}
