# Story 1.5: OIDC Authentication with Zitadel Integration

Status: drafted

## Story

As a Security Engineer,
I want to integrate OpenID Connect (OIDC) authentication with Zitadel for user login,
So that users can securely authenticate without the application managing passwords.

## Acceptance Criteria

1. Zitadel configured as external OIDC provider (self-hosted or cloud instance)
2. Spring Security configured with OAuth2 resource server (validates JWT tokens from Zitadel)
3. Login flow implemented:
   - React frontend redirects to Zitadel login (GET /authorize?client_id=...&redirect_uri=http://localhost:3000/auth/callback&...)
   - Zitadel handles authentication (username/password or SSO)
   - Zitadel redirects back to React frontend with authorization code
   - React frontend exchanges code for JWT token (via backend endpoint or directly to Zitadel)
   - React frontend stores JWT in secure session storage
4. JWT token contains:
   - User ID in subject claim
   - Email claim
   - Role(s) in custom claim (e.g., "customer_service_agent", "admin", "compliance_officer")
   - Expiration (configurable, typical 1 hour for access token)
5. All API requests require valid JWT token in Authorization header: `Authorization: Bearer {token}`
6. Invalid/expired tokens return 401 Unauthorized with error code and message
7. Role-based access control configured (@PreAuthorize("hasRole('ADMIN')") on protected endpoints)
8. Logout endpoint implemented: POST /api/v1/auth/logout (clears session, invalidates token)
9. Token refresh mechanism if needed (via refresh token)

## Tasks / Subtasks

- [ ] Task 1: Set up Zitadel instance and configure OIDC application (AC: #1)
  - [ ] Deploy Zitadel (Docker/Docker Compose for dev, cloud instance for prod)
  - [ ] Create OIDC application in Zitadel console
  - [ ] Configure redirect URIs (http://localhost:3000/auth/callback for dev, production URL for prod)
  - [ ] Configure allowed scopes (openid, email, profile)
  - [ ] Generate client_id and client_secret
  - [ ] Store credentials in environment variables (Spring properties)
  - [ ] Create test user accounts with different roles (customer_service_agent, admin, compliance_officer)

- [ ] Task 2: Configure Spring Security with OAuth2 Resource Server (AC: #2)
  - [ ] Add Spring Security 6.x and spring-boot-starter-oauth2-resource-server dependencies
  - [ ] Create SecurityConfig.java with OAuth2 resource server configuration
  - [ ] Configure JWT decoder (points to Zitadel's OIDC discovery endpoint: https://zitadel-instance/.well-known/openid-configuration)
  - [ ] Test JWT token validation (sign token with Zitadel, verify Spring validates correctly)
  - [ ] Configure global security: all /api/v1/* endpoints require valid token, /auth/* endpoints public
  - [ ] Exception handling: invalid/expired tokens return 401 with error details
  - [ ] Test with tools: curl with valid token (200), without token (401), with invalid token (401)

- [ ] Task 3: Implement authentication endpoints (AC: #3, #5)
  - [ ] Create AuthController.java with endpoints:
    - GET /api/v1/auth/login → initiates OAuth2 login flow (redirects to Zitadel)
    - GET /api/v1/auth/callback → receives authorization code from Zitadel, exchanges for token
    - GET /api/v1/auth/me → returns current authenticated user details (requires valid token)
    - POST /api/v1/auth/logout → clears session, returns 204 No Content
  - [ ] Login endpoint flow: redirects to Zitadel with client_id, redirect_uri, scope, state parameter
  - [ ] Callback endpoint flow: exchange code for token, store in session/context, redirect to frontend
  - [ ] Me endpoint: extract user claims from JWT (subject, email, roles)
  - [ ] Logout endpoint: invalidate session/token (in future, add token blacklist)

- [ ] Task 4: Implement JWT token extraction and role-based access control (AC: #4, #7)
  - [ ] Create custom JwtAuthenticationConverter to extract roles from JWT custom claims
  - [ ] Configure @EnableGlobalMethodSecurity for @PreAuthorize annotations
  - [ ] Create test roles: CUSTOMER_SERVICE_AGENT, ADMIN, COMPLIANCE_OFFICER
  - [ ] Apply @PreAuthorize("hasRole('ADMIN')") to sensitive endpoints (customer delete, audit log access)
  - [ ] Test with tokens containing different roles (verify 403 Forbidden for unauthorized users)
  - [ ] Verify role claims extracted correctly from Zitadel JWT

- [ ] Task 5: Implement token refresh mechanism (AC: #9)
  - [ ] Create RefreshTokenService to manage refresh tokens
  - [ ] Implement POST /api/v1/auth/refresh endpoint
  - [ ] Exchange refresh token for new access token
  - [ ] Handle expired refresh tokens (return 401)
  - [ ] Configure refresh token expiration (default: 7 days)
  - [ ] Test token refresh flow: use refresh token to get new access token

- [ ] Task 6: Create authentication error handling and response models (AC: #6)
  - [ ] Create AuthErrorResponse model: {code, message, timestamp}
  - [ ] Create TokenResponse model: {access_token, refresh_token, expires_in, token_type}
  - [ ] Create UserResponse model: {id, email, firstName, lastName, roles}
  - [ ] Implement custom exception: InvalidTokenException
  - [ ] Configure @ControllerAdvice to handle authentication exceptions globally
  - [ ] Return proper error responses (401, 403) with detail messages

- [ ] Task 7: Configure multi-environment settings (dev, test, prod) (AC: #1)
  - [ ] Create application-dev.yml:
    - Zitadel instance: http://localhost:8080 (local Docker)
    - OAuth2 client settings: client_id, client_secret, scope
    - CORS allowed origins: http://localhost:3000, http://localhost:3001
    - Token validation: local validation via discovery endpoint
  - [ ] Create application-test.yml:
    - Zitadel instance: http://zitadel:8080 (Docker network)
    - OAuth2 settings for test environment
    - CORS allowed origins: * (allow all for test)
  - [ ] Create application-prod.yml:
    - Zitadel instance: https://zitadel.corporate.com (production)
    - OAuth2 settings with production credentials
    - CORS allowed origins: https://cicsgenapp.example.com (production domain only)
    - TLS 1.2+ required for token endpoint calls

- [ ] Task 8: Implement session management and secure cookie handling (AC: #3, #5)
  - [ ] Configure secure session cookies (HttpOnly, Secure, SameSite=Strict)
  - [ ] Implement session timeout (default: 15 minutes of inactivity)
  - [ ] Store JWT in secure session storage (not localStorage to prevent XSS)
  - [ ] Implement remember-me functionality (optional: 30-day refresh token)
  - [ ] Clear session on logout endpoint call

- [ ] Task 9: Create integration tests for OIDC flow (AC: 1-9)
  - [ ] Integration test: Valid JWT token → access to protected endpoint (200)
  - [ ] Integration test: No token → 401 Unauthorized
  - [ ] Integration test: Invalid token → 401 Unauthorized
  - [ ] Integration test: Expired token → 401 Unauthorized
  - [ ] Integration test: Token with ADMIN role → access to admin endpoint (200)
  - [ ] Integration test: Token with USER role → 403 Forbidden on admin endpoint
  - [ ] Integration test: Logout endpoint → clears session (200)
  - [ ] Integration test: Refresh token → new access token issued (200)
  - [ ] Integration test: Invalid refresh token → 401 Unauthorized
  - [ ] Mock Zitadel OIDC endpoint for testing (no external dependency)

- [ ] Task 10: Create unit tests for JWT processing (AC: #4)
  - [ ] Unit test: JwtAuthenticationConverter extracts roles correctly
  - [ ] Unit test: Role-based access control validation
  - [ ] Unit test: Token expiration check
  - [ ] Unit test: Invalid token rejection
  - [ ] Unit test: AuthController methods (login, callback, me, logout, refresh)

- [ ] Task 11: Document authentication configuration and flows (AC: #1, #2, #8)
  - [ ] Created AUTH.md technical documentation:
    - OIDC flow diagram (login, callback, token exchange)
    - Zitadel setup instructions (Docker/cloud deployment)
    - Spring Security configuration overview
    - Role-based access control examples
    - Token claims documentation
    - Environment-specific configuration
    - Troubleshooting guide
    - Security best practices (secure cookie handling, token storage, CORS)
  - [ ] Updated README.md with authentication section
  - [ ] Document Zitadel credentials and how to obtain them
  - [ ] Document role mappings: Zitadel roles → Spring Security roles

- [ ] Task 12: Integration with Spring Cloud Gateway (Story 1.4) (AC: #3)
  - [ ] Ensure gateway propagates Authorization header to downstream services
  - [ ] Configure gateway to route /api/v1/auth/* requests to authentication service
  - [ ] Test end-to-end flow: client → gateway → authentication service → Zitadel
  - [ ] Verify X-Trace-Id correlation ID flows through authentication flow

- [ ] Task 13: Prepare for React frontend integration (Story 3.2) (AC: #3, #5)
  - [ ] Document frontend authentication requirements:
    - OIDC redirect flow (to Zitadel, back to frontend)
    - Token storage (sessionStorage vs localStorage security considerations)
    - Authorization header format (Bearer {token})
  - [ ] Ensure backend endpoints (login, callback, me, logout) match React expectations
  - [ ] Create example curl commands for testing login flow
  - [ ] Verify CORS headers allow frontend to call auth endpoints

## Dev Notes

### Architecture Context

This story extends Story 1.2 (Spring Security placeholder) by implementing full OIDC authentication with Zitadel. The authentication layer is critical for:

1. **Zero-Trust Security**: All API requests must provide valid JWT token; no anonymous access
2. **Role-Based Access Control**: Sensitive operations (delete, audit logs) restricted by user role
3. **Compliance**: Audit trail must capture user ID for all operations
4. **Frontend Integration**: React login page (Story 3.2) depends on this authentication backend

**Key Pattern**: Use Spring Security's OAuth2 Resource Server (not Authorization Server). This means:
- Zitadel is the **Authorization Server** (manages users, credentials, token issuance)
- Spring Boot is a **Resource Server** (validates tokens, enforces authorization)
- Clear separation of concerns; future microservices can share same Zitadel instance

**Learnings from Story 1.4:**
- Story 1.4 established Spring Cloud Gateway with CORS and routing
- Story 1.4's gateway routing now requires authentication at resource server level
- X-Trace-Id correlation ID from Story 1.4 should flow through authentication endpoints

### Technical Requirements for This Story

1. **Zitadel 1.x+** - OIDC provider (self-hosted Docker or cloud)
2. **Spring Security 6.x** - OAuth2 resource server
3. **JWT Token Processing** - Extract claims, validate signature
4. **Role-Based Access Control** - @PreAuthorize annotations
5. **Session Management** - Secure cookie handling (HttpOnly, Secure, SameSite)

### Constraints & Requirements

- **Java 21 LTS:** Required (from Story 1.1)
- **Spring Boot 3.3.4 LTS:** Already established
- **Token Expiration:** Configurable, typical 1 hour for access token
- **Refresh Token:** Optional but recommended for user experience
- **CORS:** Gateway handles CORS; authentication endpoints follow same CORS config
- **Port:** Authentication endpoints on same port as Spring Boot (8080)
- **HTTPS/TLS:** Required for production (token endpoint must be HTTPS)

### Testing Standards Summary

Story 1.5 requires validation of OIDC flow, token validation, and role-based access control:
- **Unit Tests**: JWT processing, role extraction, token validation
- **Integration Tests**: Full OIDC flow with mock Zitadel, unauthorized access rejection, role-based authorization
- **Security Tests**: Token expiration, invalid tokens, role enforcement
- **E2E Tests**: Login → protected endpoint access → logout flow

Target: 85%+ test coverage for authentication logic

### Project Structure Notes

Expected file locations based on unified project structure:

```
genapp-backend/src/main/java/com/example/cicsgenapp/
├── config/
│   └── SecurityConfig.java (NEW - OAuth2 resource server)
├── controller/
│   └── AuthController.java (NEW - /api/v1/auth/* endpoints)
├── security/
│   └── JwtAuthenticationConverter.java (NEW - JWT claims extraction)
├── service/
│   └── RefreshTokenService.java (NEW - token refresh logic)
└── exception/
    └── InvalidTokenException.java (NEW - authentication errors)

genapp-backend/src/main/resources/
├── application.yml (MODIFIED - OAuth2 configuration)
├── application-dev.yml (MODIFIED - Zitadel dev instance)
├── application-test.yml (MODIFIED - Zitadel test instance)
└── application-prod.yml (MODIFIED - Zitadel production instance)

genapp-backend/src/test/java/com/example/cicsgenapp/
├── controller/
│   └── AuthControllerTest.java (NEW)
└── security/
    └── JwtAuthenticationConverterTest.java (NEW)
```

**Conflicts/Variances**: None expected. Story 1.2 established basic Spring Security with CORS; this story extends with OAuth2 resource server configuration.

### References

- [Spring Security OAuth2 Resource Server Documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [Zitadel OIDC Documentation](https://zitadel.com/docs/guides/integrate/oidc)
- [JWT Claims Standard (RFC 7519)](https://tools.ietf.org/html/rfc7519)
- [OAuth 2.0 Authorization Code Flow](https://tools.ietf.org/html/rfc6749#section-1.3.1)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [Source: docs/epics.md#Story-1.5](./epics.md#Story-1.5)
- [Source: docs/PRD.md#FR018-FR020](./PRD.md#FR018-FR020)
- [Source: docs/architecture.md](./architecture.md)

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

### Completion Notes List

### File List

## Change Log

- **2025-11-03 [16:00 UTC]:** Story 1.5 DRAFTED - OIDC Authentication with Zitadel Integration

---

## Prerequisites

- Story 1.2: Spring Boot Starter Project with Core Configuration (✓ COMPLETED)
- Story 1.4: Spring Cloud Gateway and Strangler Pattern Routing (✓ COMPLETED)

## Story Type

Security & Authentication Layer Setup

## Story Points (Estimate)

13 points
