# Story 3.8: API Integration and Client Service Layer

Status: drafted

## Story

As a Frontend Developer,
I want a centralized API client service that handles authentication, error handling, and request/response,
So that components can easily call APIs without repetitive boilerplate.

## Acceptance Criteria

1. ApiClient service created with methods:
   - get<T>(url: string, options?: AxiosRequestConfig): Promise<T>
   - post<T>(url: string, body: unknown, options?: AxiosRequestConfig): Promise<T>
   - put<T>(url: string, body: unknown, options?: AxiosRequestConfig): Promise<T>
   - delete<T>(url: string, options?: AxiosRequestConfig): Promise<T>
2. Automatically attaches JWT token to Authorization header
3. Handles response status codes:
   - 2xx: resolve promise with parsed JSON
   - 4xx: throw error with field-level details
   - 5xx: throw error with message
4. Retry logic for transient failures:
   - Max 3 retries with exponential backoff (100ms, 200ms, 400ms)
   - Retry on: 408, 429, 5xx errors
   - Don't retry on: 4xx validation errors
5. Handles 401 Unauthorized (token expired):
   - Attempt to refresh token via refresh endpoint
   - If refresh fails, redirect to /login
6. Logging in development mode (console.log all API calls with timing)
7. Correlation ID (X-Trace-Id header) included in all requests (UUID)
8. Timeout handling (15s default timeout, configurable)
9. CustomerService wrapper with methods:
   - createCustomer(data: CreateCustomerRequest): Promise<Customer>
   - getCustomer(id: string): Promise<Customer>
   - searchCustomers(query: SearchQuery): Promise<SearchResult<Customer>>
   - updateCustomer(id: string, data: UpdateCustomerRequest): Promise<Customer>
   - deleteCustomer(id: string): Promise<void>
10. PolicyService wrapper (for future use)
11. Error handling wrapper for common error scenarios
12. Usage example in components

## Tasks / Subtasks

- [ ] Task 1: Create ApiClient service with base HTTP methods
- [ ] Task 2: Implement JWT token attachment to requests
- [ ] Task 3: Implement retry logic with exponential backoff
- [ ] Task 4: Implement 401 token refresh handling
- [ ] Task 5: Implement request/response logging
- [ ] Task 6: Implement correlation ID generation and propagation
- [ ] Task 7: Implement timeout handling
- [ ] Task 8: Create CustomerService wrapper with customer-specific methods
- [ ] Task 9: Create PolicyService wrapper
- [ ] Task 10: Create error handling utilities
- [ ] Task 11: Write unit tests for ApiClient and services
- [ ] Task 12: Document API client usage with examples

## Dev Notes

- Use Axios library for HTTP client
- Use UUID library for correlation ID generation
- Implement error types for different error scenarios
- Coordinate with Story 1.5 (Zitadel OIDC) for token refresh endpoint
- Coordinate with Story 2.8 (API Documentation) for response format

## Dev Agent Record

- Context Reference: docs/stories/3-8-api-integration-and-client-service-layer.context.xml
