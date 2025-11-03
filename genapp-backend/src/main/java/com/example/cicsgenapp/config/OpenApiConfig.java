package com.example.cicsgenapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 Configuration for Swagger UI and API documentation.
 * Configures SpringDoc-OpenAPI to generate comprehensive API documentation
 * with security schemes for JWT Bearer tokens.
 */
@Configuration
public class OpenApiConfig {

  /**
   * Customizes the OpenAPI specification with title, version, security schemes,
   * and other metadata.
   *
   * @return OpenAPI specification with custom configuration
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("CICS GenApp Customer API")
            .version("1.0.0")
            .description("Customer management REST API for CICS GenApp modernization. "
                + "Provides comprehensive CRUD operations for customer records with audit logging "
                + "and compliance tracking."))
        .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
        .components(new Components()
            .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token from Zitadel OIDC provider. "
                    + "Include this token in the Authorization header for all authenticated requests.")));
  }
}
