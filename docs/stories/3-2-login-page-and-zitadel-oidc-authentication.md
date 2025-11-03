# Story 3.2: Login Page and Zitadel OIDC Authentication

Status: drafted

## Story

As a User,
I want to log in with my corporate credentials via Zitadel OIDC,
So that I can securely access the system without managing passwords.

## Acceptance Criteria

1. Login page created at route /login
2. Page displays Zitadel logo and "Sign in with corporate SSO" button
3. Clicking button redirects to Zitadel authorization endpoint with correct parameters:
   - `client_id` (configured in .env)
   - `redirect_uri` (http://localhost:3000/auth/callback for dev, production URL for prod)
   - `response_type=code`
   - `scope=openid profile email`
4. Zitadel handles authentication (username/password or SSO)
5. After authentication, Zitadel redirects to /auth/callback route with `code` parameter
6. Frontend exchanges code for JWT token (via backend: POST /api/v1/auth/callback?code=...)
7. Backend calls Zitadel's token endpoint to exchange code for JWT
8. Frontend receives JWT and stores in secure session storage (NOT localStorage)
9. User context updated with: userId, email, name, roles (from JWT claims)
10. User redirected to /dashboard on successful login
11. Logout button clears session storage, redirects to /login
12. Protected routes check for valid token; redirect to /login if missing or expired
13. Token refresh: if access token near expiration, automatically refresh via refresh token
14. Error messages displayed on login failure: "Authentication failed", "Invalid credentials", or "Server error"
15. Loading state while exchanging code for token (spinner displayed)

## Tasks / Subtasks

- [ ] Task 1: Create Login page component (AC: #1, #2)
  - [ ] Create `src/pages/LoginPage.tsx`
    - Responsive login container (centered, mobile-friendly)
    - Zitadel logo/branding section
    - "Sign in with corporate SSO" button (prominent)
    - Optional: tagline or description of login flow
    - MUI Button with onClick handler to initiate OAuth flow
  - [ ] Apply theme styling (MUI theme from Story 3.1)
  - [ ] Test: Verify page renders correctly on desktop and mobile
  - [ ] Test: Verify button is accessible (keyboard navigation, ARIA labels)

- [ ] Task 2: Implement Zitadel OIDC authorization flow (AC: #3, #4, #5)
  - [ ] Create `src/services/zitadelService.ts`
    - Export function: `initiateLogin()` → constructs OAuth2 authorize URL with:
      - `client_id` from environment
      - `redirect_uri` set to `{window.location.origin}/auth/callback`
      - `response_type=code` (authorization code flow)
      - `scope=openid profile email`
      - Optional: `state` parameter for CSRF protection
    - Call: `window.location.href = authorizationUrl` (redirects to Zitadel)
  - [ ] Update LoginPage.tsx: wire onClick handler to call `initiateLogin()`
  - [ ] Create config values in `src/config/config.ts`:
    - `ZITADEL_CLIENT_ID` (from .env VITE_ZITADEL_CLIENT_ID)
    - `ZITADEL_AUTHORITY` (e.g., https://zitadel.example.com)
  - [ ] Test: Manually verify redirect to Zitadel works (browser DevTools Network tab)
  - [ ] Test: Verify state parameter prevents CSRF (if implemented)

- [ ] Task 3: Create OAuth2 callback handler (AC: #5, #6)
  - [ ] Create `src/pages/AuthCallbackPage.tsx`
    - Route: /auth/callback
    - Extract `code` parameter from URL query string
    - Display loading spinner while exchanging code
    - Error handling: if no code or error parameter, show error message
  - [ ] Create `src/services/authService.ts`
    - Export function: `exchangeCodeForToken(code: string)` → POST /api/v1/auth/callback?code=...
    - Parse JWT from response (structure: { accessToken: string, refreshToken?: string, expiresIn: number })
    - Return: { userId, email, name, roles } (decoded from JWT claims)
  - [ ] In AuthCallbackPage, call `exchangeCodeForToken(code)` on component mount
  - [ ] Handle response: update AuthContext and navigate to /dashboard
  - [ ] Test: Manually exchange code and verify JWT received (check browser console, Network tab)

- [ ] Task 4: Implement secure session storage for JWT (AC: #8, #12)
  - [ ] Update `src/context/AuthContext.tsx`
    - Create Auth context with state: { userId, email, name, roles, token, isAuthenticated }
    - Provide functions: login(), logout(), refresh()
    - On login: store token in `sessionStorage.setItem('auth_token', token)`
    - On logout: `sessionStorage.removeItem('auth_token')` and clear user context
  - [ ] Create `src/hooks/useAuth.ts`
    - Custom hook: `useAuth()` → returns auth context, ensures user is logged in
    - If no token in sessionStorage, redirect to /login
  - [ ] Create protected route component: `src/components/ProtectedRoute.tsx`
    - Wraps routes that require authentication
    - If user not authenticated, redirects to /login
    - If authenticated, renders child component
  - [ ] Test: Verify token persists in sessionStorage during session
  - [ ] Test: Verify token is cleared on logout
  - [ ] Test: Verify sessionStorage is cleared on browser close (session storage behavior)

- [ ] Task 5: Implement token refresh mechanism (AC: #13)
  - [ ] Add to `authService.ts`:
    - Export function: `refreshAccessToken(refreshToken: string)` → POST /api/v1/auth/refresh?refresh_token=...
    - Store new access token in sessionStorage
  - [ ] Update AuthContext:
    - Add state: `tokenExpiresAt` (timestamp when token expires)
    - Add interceptor in API client: if token within 5 mins of expiration, call refresh before request
  - [ ] Create `src/hooks/useTokenRefresh.ts`
    - Custom hook to handle token refresh logic
    - Check token expiration on component mount
    - Set up interval to refresh token before expiration (e.g., every 10 mins for 1-hour tokens)
  - [ ] Test: Verify token refresh happens before expiration
  - [ ] Test: Verify expired token triggers redirect to /login

- [ ] Task 6: Implement error handling for login failures (AC: #14, #15)
  - [ ] Update AuthCallbackPage:
    - Handle error cases:
      - Missing code parameter: "Authentication failed - missing authorization code"
      - HTTP errors from /api/v1/auth/callback: "Authentication failed - {message}"
      - Network error: "Server error - please try again"
    - Display error message in a MUI Alert component (dismissible)
    - Show "Try Again" button → redirect to /login
  - [ ] Create `src/components/LoginErrorBoundary.tsx` (optional)
    - Catch errors during OAuth flow
    - Display user-friendly error message
    - Provide "Retry" action
  - [ ] Test: Verify error messages display correctly
  - [ ] Test: Verify user can retry after error

- [ ] Task 7: Update routing to include login and callback routes (AC: #5)
  - [ ] Update `src/router/Router.tsx`:
    - Add route: `/login` → LoginPage (not protected)
    - Add route: `/auth/callback` → AuthCallbackPage (not protected)
    - Update `/dashboard` and other protected routes to use ProtectedRoute wrapper
    - Update root route: if authenticated, show Dashboard; else redirect to /login
  - [ ] Test: Verify navigation flow works (login → callback → dashboard)
  - [ ] Test: Verify unauthenticated access to /dashboard redirects to /login

- [ ] Task 8: Implement logout functionality (AC: #11)
  - [ ] Add logout button to header/user menu (added in Story 3.3)
  - [ ] Create `src/components/LogoutButton.tsx`
    - Button that calls `authService.logout()`
    - Clears sessionStorage, updates AuthContext, redirects to /login
  - [ ] Optional: call backend logout endpoint: DELETE /api/v1/auth/logout
  - [ ] Test: Verify logout clears session and redirects to /login
  - [ ] Test: Verify logged-out user cannot access protected routes

- [ ] Task 9: Test complete authentication flow (AC: all)
  - [ ] Manual E2E test:
    - Navigate to /login
    - Click "Sign in with corporate SSO"
    - Complete login in Zitadel (enter credentials)
    - Verify redirect to /dashboard with user info displayed
    - Test logout and verify redirected to /login
  - [ ] Test edge cases:
    - Close browser tab during auth flow
    - Manually expire token and verify auto-refresh or redirect
    - Verify protected routes are truly protected
  - [ ] Test: Verify session persists across page refreshes (token in sessionStorage)
  - [ ] Test: Verify session clears on browser close (sessionStorage behavior)

- [ ] Task 10: Create integration tests (AC: #1-15)
  - [ ] Create `tests/integration/auth.test.tsx`
    - Test: LoginPage renders with correct button
    - Test: Clicking button constructs correct OAuth authorize URL
    - Test: AuthCallbackPage exchanges code for token
    - Test: Token stored in sessionStorage
    - Test: Logout clears session and redirects
    - Test: Protected route redirects to /login if not authenticated
  - [ ] Use React Testing Library for component tests
  - [ ] Mock Zitadel API responses and backend endpoints
  - [ ] Test: Run `npm test` and verify all tests pass

- [ ] Task 11: Add API endpoint for token exchange on backend (AC: #6, #7)
  - [ ] Backend task (Story 1.5): Ensure endpoint exists: POST /api/v1/auth/callback?code=...
    - Validate code and exchange for JWT from Zitadel
    - Return JWT in response: { accessToken, refreshToken, expiresIn }
  - [ ] Frontend should call this endpoint from AuthCallbackPage
  - [ ] Frontend should NOT call Zitadel directly (backend handles token security)
  - [ ] Test: Verify backend endpoint returns valid JWT

- [ ] Task 12: Update AuthContext to handle user info from JWT (AC: #9)
  - [ ] Create utility function: `decodeJWT(token: string)` in `src/utils/auth.ts`
    - Parse JWT payload (use `jwtDecode` library or manual base64 decode)
    - Extract: userId (from 'sub' claim), email, name, roles (from custom claims)
  - [ ] Update AuthContext login() to decode JWT and populate user info
  - [ ] Update Zitadel client configuration to include user info in JWT claims
  - [ ] Test: Verify JWT contains required claims
  - [ ] Test: Verify user info displays correctly in AuthContext

## Dev Notes

### Architecture Context

Story 3.2 implements the authentication layer for the frontend, enabling secure user access via Zitadel OIDC. This is a critical story that unblocks all other frontend stories (3.3-3.8), as protected routes depend on authentication.

**Key Design Decisions:**

1. **OAuth2 Authorization Code Flow**: Industry-standard security pattern for SPAs, prevents exposing credentials to frontend code
2. **Session Storage (not localStorage)**: Clears automatically on browser close, prevents token theft from XSS
3. **Backend token exchange**: Backend handles Zitadel communication, frontend never sees client secret (security best practice)
4. **Token refresh interceptor**: Seamless user experience - token automatically refreshed before expiration
5. **Protected route wrapper**: Prevents unauthorized access to protected pages

**Dependency Chain:**
- Depends on: Story 1.2 (Spring Boot backend auth endpoint), Story 3.1 (React setup)
- Prerequisite for: Stories 3.3-3.8 (all frontend features need authentication)

### Technical Requirements

1. **Frontend Libraries**:
   - `react-router-dom`: for routing (/login, /auth/callback)
   - `jwtDecode` (optional): for decoding JWT claims
   - `@mui/material`: for UI components (Button, TextField, Alert)

2. **Environment Variables** (from .env):
   - `VITE_ZITADEL_CLIENT_ID`: OAuth2 client ID
   - `VITE_ZITADEL_AUTHORITY`: Zitadel base URL

3. **Backend Requirement**:
   - Endpoint: `POST /api/v1/auth/callback?code=<code>`
   - Response: `{ accessToken: string, refreshToken?: string, expiresIn: number }`
   - Endpoint: `POST /api/v1/auth/refresh?refresh_token=<token>` (for token refresh)

### Constraints & Requirements

- **No password handling**: All credential entry via Zitadel, never stored in app
- **Secure token storage**: sessionStorage only, cleared on browser close
- **Token validation**: Every request must include valid token in Authorization header
- **CSRF protection**: State parameter in OAuth flow prevents CSRF attacks
- **Error handling**: User-friendly messages for all failure scenarios
- **Session duration**: Typical JWT expiration 1 hour; refresh token extends session

### Project Structure Notes

From unified-project-structure.md and Story 3.1, authentication infrastructure includes:

```
src/
├── pages/
│   ├── LoginPage.tsx
│   └── AuthCallbackPage.tsx
├── services/
│   ├── authService.ts (JWT exchange, refresh, logout)
│   └── zitadelService.ts (OAuth2 flow)
├── context/
│   └── AuthContext.tsx (user state, token)
├── hooks/
│   ├── useAuth.ts (access auth context, ensures logged in)
│   └── useTokenRefresh.ts (automatic token refresh)
├── components/
│   ├── ProtectedRoute.tsx (guards protected routes)
│   └── LogoutButton.tsx (logout UI)
├── config/
│   └── config.ts (ZITADEL_CLIENT_ID, ZITADEL_AUTHORITY)
└── utils/
    └── auth.ts (JWT decode, token validation)
```

**Important:** AuthContext is the single source of truth for authentication state. All components access via `useAuth()` hook, not direct context.useContext() calls.

### Learnings from Previous Story

**From Story 3.1 (React Project Setup):**

- **Project foundation**: Vite + React 18 + TypeScript + MUI theme already configured
- **Router setup**: React Router v6 configured and ready for new routes
- **Theme context**: MUI theme provider wraps app; use `useTheme()` hook in components
- **Config pattern**: Environment variables loaded via `src/config/config.ts`
- **Testing setup**: React Testing Library configured; follow existing test patterns

**From Story 1.5 (OIDC Authentication - backend):**

- **Token format**: JWT contains userId (sub), email, name, roles claims
- **Token expiration**: Typically 1 hour; refresh token provided for extension
- **Authorization header**: All API requests must include `Authorization: Bearer {token}`
- **Backend readiness**: /api/v1/auth/callback endpoint ready for token exchange

**Architectural decisions from previous stories to maintain:**
- Use same ApiResponse wrapper format for backend responses
- Follow same error handling pattern (validation errors, server errors)
- Include correlation ID (X-Trace-Id) in all API requests
- Use sessionStorage for sensitive data (not localStorage or cookies)

**Files from Story 3.1 to build upon:**
- `src/config/config.ts`: extend with ZITADEL_CLIENT_ID, ZITADEL_AUTHORITY
- `src/router/Router.tsx`: add login and callback routes
- `src/App.tsx`: wrap with AuthProvider (from AuthContext)
- `vite.config.ts`: ensure environment variables are accessible

### References

- [OAuth2 Authorization Code Flow](https://oauth.net/2/grant-types/authorization-code/)
- [OpenID Connect (OIDC) Specification](https://openid.net/connect/)
- [Zitadel Documentation](https://zitadel.com/docs/)
- [React Router v6 Protected Routes](https://reactrouter.com/en/main/route/route)
- [Material-UI Components](https://mui.com/material-ui/)
- [Session Storage vs LocalStorage](https://developer.mozilla.org/en-US/docs/Web/API/Window/sessionStorage)
- [JWT (JSON Web Token) RFC 7519](https://tools.ietf.org/html/rfc7519)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)
- [Source: docs/epics.md#story-32-login-page-and-zitadel-oidc-authentication]
- [Source: docs/stories/3-1-react-project-setup-with-vite-and-material-design.md - Project setup foundation]
- [Source: docs/stories/1-5-oidc-authentication-with-zitadel-integration.md - Backend auth implementation]

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 3.2 CREATED from Epic 3 via create-story workflow
- 2025-11-03: Based on epics.md acceptance criteria and Story 3.1 project setup
- 2025-11-03: Incorporates learnings from Story 1.5 (backend OIDC implementation)

### Completion Notes List

### File List

