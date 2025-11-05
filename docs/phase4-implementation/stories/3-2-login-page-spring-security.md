# Story 3.2: Login Page with Spring Security Form Authentication

**Story ID:** 3-2-login-page-with-spring-security-form-authentication
**Epic:** Epic 3 - Vaadin Frontend - Core User Interface
**Status:** Ready for Dev
**Story Points:** 5
**Sprint:** TBD

---

## Story Summary

**As a** Customer Service Agent,
**I want** to log in with username and password before accessing customer/policy data,
**So that** the system is secured and only authorized users can view sensitive information.

---

## Acceptance Criteria

1. **Login Page UI (Vaadin Form)**
   - [ ] Vaadin LoginForm component created at route `/login`
   - [ ] Username field with placeholder "Username"
   - [ ] Password field with placeholder "Password"
   - [ ] "Sign In" button (primary action)
   - [ ] Clean, professional appearance using Vaadin Lumo theme
   - [ ] Responsive layout (works on mobile, tablet, desktop)

2. **Spring Security Configuration**
   - [ ] SecurityConfig bean configured with form-based authentication
   - [ ] Default user credentials configured (e.g., username: `admin`, password: `admin` for MVP testing)
   - [ ] `/login` endpoint publicly accessible (no auth required)
   - [ ] `/` (dashboard) requires authentication
   - [ ] Session management configured (30-minute timeout)

3. **Form Submission & Validation**
   - [ ] Username and password submitted to `/login` endpoint (POST)
   - [ ] Spring Security processes credentials
   - [ ] Successful login redirects to Dashboard (Story 3.3)
   - [ ] Failed login shows error message: "Invalid username or password"
   - [ ] Empty fields show client-side validation error

4. **Session & Logout**
   - [ ] Successful login creates HTTP session cookie
   - [ ] Session persists across page reloads
   - [ ] Logout endpoint (`/logout`) implemented
   - [ ] Clicking logout clears session and redirects to login page
   - [ ] Session timeout returns user to login page

5. **Security Considerations**
   - [ ] CSRF protection enabled (Spring Security default)
   - [ ] Passwords not logged or displayed in error messages
   - [ ] No credentials hardcoded (loaded from application.properties or environment variables)
   - [ ] HTTPS enforced in production (HTTP OK for local dev)

6. **Integration with Dashboard**
   - [ ] Successful login transitions smoothly to Dashboard (no page jank)
   - [ ] Current user visible in Dashboard header (username or display name)
   - [ ] Logout button accessible from Dashboard (creates seamless logout flow)

7. **User Experience**
   - [ ] Login form remembers username (browser default behavior)
   - [ ] Enter key submits form (standard web convention)
   - [ ] Error messages display below form fields in red
   - [ ] Loading indicator shows during login processing
   - [ ] Accessibility: All fields have proper labels, keyboard navigable

---

## Technical Notes

- **Authentication Method:** Spring Security form-based (session cookies)
- **Not OIDC:** OIDC deferred to post-MVP (simpler session-based auth sufficient for MVP)
- **User Source:** Initially hardcoded; can be extended to database later
- **Password Encoding:** Spring Security uses bcrypt (passwords never stored plain-text)

---

## Dependencies

**Depends On:** Story 3.1 (Vaadin setup + Spring Security config)
**Blocks:** Story 3.3 (Dashboard needs authenticated user context)

---

## Test Plan

**Manual Testing:**
1. Access http://localhost:8080 → redirects to /login (unauthenticated)
2. Leave username/password empty → submit → error "Please fill out both fields"
3. Enter invalid credentials (e.g., `admin` / `wrong`) → submit → error "Invalid username or password"
4. Enter correct credentials (`admin` / `admin`) → submit → redirects to Dashboard
5. Reload page → Dashboard still accessible (session persists)
6. Click logout → redirects to login page
7. Try accessing Dashboard directly without login → redirects to login

**Browser Dev Tools:**
- Verify session cookie present (e.g., JSESSIONID)
- Verify no credentials in console logs
- Verify CSRF token present in form (Spring Security automatic)

---

## Acceptance Notes

- Login form should be minimal and clean (not over-engineered)
- Vaadin LoginForm component is purpose-built for this use case
- Error messages should be user-friendly (not technical stack traces)
- Session timeout can be tested by waiting 30 minutes or adjusting application properties

---

**Created:** 2025-11-04 (Vaadin Pivot Decision)
**Story Replanned From:** 3-2 Zitadel OIDC Login (simplified to Spring Security for MVP)
