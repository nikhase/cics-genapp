package com.example.cicsgenapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers configuration for Spring Boot integration tests.
 *
 * Provides a PostgreSQL container via spring-boot-testcontainers, allowing tests
 * to run against the actual PostgreSQL dialect instead of H2 in-memory. This ensures:
 * - All PostgreSQL-specific features work (JSONB, ILIKE, uuid, etc.)
 * - Flyway migrations run against the real database
 * - Tests use the actual database dialect (PostgreSQLDialect)
 *
 * The @ServiceConnection annotation automatically configures Spring's datasource
 * to connect to the PostgreSQL container.
 *
 * Usage: Add @Import(TestcontainersConfiguration.class) to your @SpringBootTest
 * or use @SpringBootTest(classes = {..., TestcontainersConfiguration.class})
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  /**
   * Creates and configures a PostgreSQL container for testing.
   * The @ServiceConnection annotation automatically configures Spring's datasource
   * to connect to this container.
   *
   * @return PostgreSQL container
   */
  @Bean
  @ServiceConnection
  public PostgreSQLContainer<?> postgresContainer() {
    return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
        .withDatabaseName("cicsgenapp_test")
        .withUsername("testuser")
        .withPassword("testpass");
  }
}
