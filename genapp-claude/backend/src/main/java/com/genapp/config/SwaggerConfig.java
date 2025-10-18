package com.genapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SwaggerConfig - OpenAPI 3.0 / Swagger UI Configuration
 *
 * Provides comprehensive API documentation for all REST endpoints.
 * Replaces manual API documentation that would be needed for COBOL-based systems.
 *
 * Old COBOL Approach:
 *   - Manual documentation on paper or Word docs
 *   - No automated API discovery
 *   - Hard to keep up-to-date
 *   - No interactive testing capability
 *
 * New Approach with Swagger/OpenAPI:
 *   - Auto-generated from annotations
 *   - Interactive testing via Swagger UI
 *   - Always in sync with code
 *   - Available at http://localhost:8080/swagger-ui.html
 *
 * Benefits:
 *   ✅ Real-time documentation
 *   ✅ Interactive API testing (no need for Postman)
 *   ✅ Machine-readable OpenAPI spec
 *   ✅ Code-first documentation
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure OpenAPI 3.0 documentation
     *
     * This bean provides metadata about the API including:
     * - General information (title, version, description)
     * - Contact information
     * - License
     * - Servers (local dev, staging, production)
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GenApp Modernized API")
                        .version("0.1.0")
                        .description("""
                                REST API for GenApp - Insurance Application Modernization

                                This API replaces the CICS/COBOL three-tier architecture:
                                - OLD: 3270 terminal → BMS → COBOL programs → VSAM/Db2
                                - NEW: REST API → Spring Boot → JPA → PostgreSQL

                                **Architecture:**
                                - Presentation: React SPA (replacing BMS screens)
                                - API: Spring Boot 3 with Java 21 (replacing COBOL programs)
                                - Database: PostgreSQL (replacing VSAM + Db2)

                                **Key Endpoints:**
                                - `/api/customers` - Customer management (CRUD)
                                - `/api/policies` - Insurance policy management (CRUD)

                                **Data Models:**
                                - Customer: Personal information and contact details
                                - Policy: Insurance policies (Motor, House, Endowment, Commercial)

                                **Authentication:**
                                Currently public API (no authentication required for demo)

                                **Rate Limiting:**
                                None (development environment)

                                **CORS:**
                                Enabled for localhost:3000 and localhost:5173 (React dev servers)
                                """)
                        .contact(new Contact()
                                .name("Claude AI")
                                .url("https://claude.ai")
                                .email("claude@anthropic.com"))
                        .license(new License()
                                .name("Eclipse Public License 2.0")
                                .url("https://www.eclipse.org/legal/epl-2.0/")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development"),
                        new Server()
                                .url("http://localhost:8081")
                                .description("Alternative Local Port"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production (placeholder)")
                ));
    }
}
