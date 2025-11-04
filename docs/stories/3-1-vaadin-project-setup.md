# Story 3.1: Vaadin Project Setup with Spring Boot Integration

**Story ID:** 3-1-vaadin-project-setup-with-spring-boot-integration
**Epic:** Epic 3 - Vaadin Frontend - Core User Interface
**Status:** Ready for Dev
**Story Points:** 5
**Sprint:** TBD

---

## Story Summary

**As a** Backend Developer,
**I want** to integrate Vaadin into the existing Spring Boot application with proper build configuration and development setup,
**So that** I have a working foundation to build UI pages without managing separate frontend build tools.

---

## Acceptance Criteria

1. **Vaadin Dependency Added**
   - [ ] `com.vaadin:vaadin-spring-boot-starter` added to pom.xml (latest stable 24.x version)
   - [ ] Maven build succeeds with `mvn clean package`
   - [ ] Vaadin theme and component dependencies included automatically

2. **Application Boots Successfully**
   - [ ] Application starts with `mvn spring-boot:run`
   - [ ] Vaadin serves at `http://localhost:8080` (root path)
   - [ ] Default Vaadin "Hello World" view accessible (temporary)
   - [ ] No TypeScript/Node.js build steps required

3. **Development Environment Configured**
   - [ ] Vaadin development mode enabled in application-dev.yml
   - [ ] Live reload configured (Vaadin live reload on file save)
   - [ ] Chrome DevTools debugging available for UI
   - [ ] Maven hot-reload functional (code changes reflect on save)

4. **Folder Structure Established**
   - [ ] Create `src/main/java/com/cicsgenapp/ui/views/` folder for Vaadin views
   - [ ] Create `src/main/java/com/cicsgenapp/ui/layouts/` folder for layout components
   - [ ] Create `src/main/java/com/cicsgenapp/ui/components/` folder for reusable components
   - [ ] Create `src/main/resources/themes/` folder for custom theming (if needed)

5. **Theme Configuration**
   - [ ] Vaadin Lumo theme selected (default, professional appearance)
   - [ ] Light/dark mode toggle ready (built into Lumo)
   - [ ] No custom Material Design styling required

6. **Spring Security Integration**
   - [ ] Spring Security configured for form-based login (simple session-based auth for MVP)
   - [ ] Placeholder SecurityConfig created (authentication to be implemented in Story 3.2)
   - [ ] Public endpoints configured for login page (not yet created)

7. **README Documentation**
   - [ ] Developer setup instructions updated: "No Node.js required; pure Maven build"
   - [ ] Vaadin development mode noted (live reload works automatically)
   - [ ] First-time Vaadin build may take longer (pre-downloads themes)

8. **No Build Complexity**
   - [ ] No npm, no webpack, no TypeScript compilation
   - [ ] No separate frontend build process
   - [ ] Single Maven command: `mvn spring-boot:run` starts full application

---

## Technical Notes

- Vaadin Spring Boot Starter includes all required dependencies (Vaadin Flow, Spring integration)
- Vaadin serves static frontend from same Spring Boot process
- No separate React build/bundling needed
- Development mode provides hot-reload (file changes trigger automatic reload in browser)

---

## Dependencies

**Depends On:** Stories 1.1, 1.2 (Spring Boot foundation)
**Blocks:** All remaining Epic 3 stories (3.2-3.8)

---

## Test Plan

**Manual Testing:**
1. Start application: `mvn spring-boot:run`
2. Access http://localhost:8080 (should show working Vaadin page)
3. Make code change in a View class
4. Save file → browser automatically reloads (live reload)
5. Verify no TypeScript/npm errors in console
6. Verify no Node.js processes running (only Java)

**Build Verification:**
- `mvn clean package` produces single JAR with Vaadin embedded
- JAR includes all themes and components
- JAR runs on any system with Java 21 (no Node.js needed)

---

## Acceptance Notes

- Live reload should work seamlessly (Vaadin handles browser refresh)
- First-time Maven build may take 2-3 minutes to download Vaadin dependencies
- Subsequent builds are much faster
- No TypeScript errors or build warnings should appear

---

**Created:** 2025-11-04 (Vaadin Pivot Decision)
**Story Replanned From:** 3-1 React Project Setup
