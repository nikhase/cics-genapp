# Story 3.3: Dashboard Page with Vaadin Navigation Layout

**Story ID:** 3-3-dashboard-page-with-vaadin-navigation-layout
**Epic:** Epic 3 - Vaadin Frontend - Core User Interface
**Status:** Ready for Dev
**Story Points:** 5
**Sprint:** TBD

---

## Story Summary

**As a** Customer Service Agent,
**I want** to see a Dashboard after login with navigation to all major features,
**So that** I have a central hub to access customer management, policy management, and other key functions.

---

## Acceptance Criteria

1. **Main Layout Structure**
   - [ ] Vaadin AppLayout component configured (header + sidebar navigation)
   - [ ] Header displays "CICS GenApp" logo/title
   - [ ] Header shows current user (e.g., "Logged in as: admin") on right side
   - [ ] Logout button in header (triggers logout)
   - [ ] Sidebar navigation menu on left side (persistent across page changes)

2. **Sidebar Navigation**
   - [ ] Navigation items for main features:
     - [ ] Dashboard (home)
     - [ ] Customers (search, list, create)
     - [ ] Policies (search, list, create)
     - [ ] Admin (future, grayed out for MVP)
   - [ ] Active navigation item highlighted
   - [ ] Responsive: sidebar collapses to menu icon on mobile
   - [ ] Menu items navigate to corresponding pages

3. **Dashboard Content**
   - [ ] Welcome message: "Welcome, [username]!"
   - [ ] Quick action buttons:
     - [ ] "Create New Customer" → navigates to Customer Create page (Story 3.6)
     - [ ] "Search Customers" → navigates to Customer Search page (Story 3.4)
     - [ ] "Create New Policy" → navigates to Policy Create (Story TBD)
     - [ ] "Search Policies" → navigates to Policy Search page (Story 3.7)
   - [ ] Buttons styled consistently (Vaadin primary/secondary styling)
   - [ ] Optional: Summary cards showing counts (total customers, total policies)

4. **Layout Responsiveness**
   - [ ] Desktop layout: sidebar always visible, content area spacious
   - [ ] Tablet layout: sidebar toggles with menu button
   - [ ] Mobile layout: sidebar collapses, full-width content
   - [ ] All layouts tested and functional

5. **Theme Integration**
   - [ ] Uses Vaadin Lumo theme (light mode default)
   - [ ] Colors match Clarity Enterprise design (if available) or professional neutral palette
   - [ ] Font sizes and spacing readable on all devices
   - [ ] Dark mode toggle available (Vaadin Lumo supports this natively)

6. **Navigation Integration**
   - [ ] Sidebar links navigate to pages created in Stories 3.4-3.8
   - [ ] URL routing matches Vaadin @Route annotations
   - [ ] Browser back button works correctly
   - [ ] Page title updates when navigating

7. **User Context**
   - [ ] Current user information available throughout app
   - [ ] Username displayed in header
   - [ ] Logout button accessible from any page
   - [ ] Session timeout displays warning (optional enhancement)

---

## Technical Notes

- **Layout Pattern:** Vaadin AppLayout (standard for full-page apps)
- **Navigation:** Vaadin Router integration (automatic routing based on @Route)
- **Theme:** Vaadin Lumo (built-in, no custom CSS required)
- **Responsive Design:** Vaadin layouts auto-responsive (handled by framework)

---

## Dependencies

**Depends On:** Story 3.2 (Login page, authentication context)
**Blocks:** Stories 3.4-3.8 (Customer and Policy pages need navigation links from Dashboard)

---

## Test Plan

**Manual Testing:**
1. Login successfully (Story 3.2)
2. Dashboard displays with correct username
3. Sidebar shows all navigation items
4. Click "Create New Customer" → navigates to Customer Create page (will be created in Story 3.6)
5. Sidebar remains visible and accessible
6. Click back button → returns to Dashboard
7. Test on mobile/tablet → sidebar collapses properly
8. Click logout → returns to login page

**Browser Testing:**
- Chrome, Firefox, Safari on desktop
- Chrome mobile on Android (or emulator)
- Safari on iOS (or simulator)

---

## Acceptance Notes

- Dashboard is the main landing page after login (default @Route("/"))
- Layout should feel professional but not over-designed (simple is better)
- Navigation should be intuitive (users immediately know where to go)
- No complex styling or animations needed (focus on usability)

---

**Created:** 2025-11-04 (Vaadin Pivot Decision)
**Story Replanned From:** 3-3 Dashboard with React/Material Design
