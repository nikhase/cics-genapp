# Story 3.3: Dashboard Page with Navigation and Quick Actions

Status: drafted

## Story

As a Customer Service Agent,
I want to see a dashboard with overview of workload and quick actions,
So that I can quickly navigate to common tasks.

## Acceptance Criteria

1. Dashboard page created at route /dashboard (protected route, requires auth)
2. Header section displays:
   - App logo/title ("CICS GenApp Modernization")
   - User greeting ("Welcome, Jane Smith")
   - User profile dropdown (with options: Profile, Settings, Logout)
   - Time-of-day greeting ("Good Morning" / "Good Afternoon" / "Good Evening" based on local time)
3. Sidebar navigation with menu items:
   - 🏠 Dashboard (home icon)
   - 👥 Customers (people icon)
   - 📄 Policies (document icon)
   - 📋 Audit Log (clock icon, visible to compliance_officer and admin roles only)
   - 📊 Reports (chart icon, visible to admin roles)
   - ⚙️ Admin (settings icon, visible to admin roles)
4. Sidebar collapses on mobile devices (hamburger menu)
5. Active menu item highlighted based on current route
6. Main content area displays quick actions:
   - "New Customer" button → /customers/create
   - "New Policy" button → /policies/create
   - "Search Customer" text input → /customers/search?query=...
   - "Search Policy" text input → /policies/search?query=...
7. Optional metrics dashboard (if business requires):
   - Total customers count (retrieved from backend)
   - Total policies count (retrieved from backend)
   - Recent activity feed (last 5 operations)
8. Responsive design: sidebar collapses on mobile, main content full width
9. Keyboard shortcut: Ctrl+K opens search modal (search customers globally)
10. Loading state while fetching metrics (skeleton loaders)

## Tasks / Subtasks

- [ ] Task 1: Create layout structure with Header and Sidebar (AC: #2, #3, #4)
  - [ ] Create `src/components/Header.tsx`
    - Top navigation bar with app logo and title
    - User greeting with time-of-day message (calculate from current time)
    - User profile dropdown menu (Profile, Settings, Logout)
    - Responsive: logo/title on left, user menu on right
    - MUI AppBar component for styling
  - [ ] Create `src/components/Sidebar.tsx`
    - Vertical navigation menu with icons and labels
    - Menu items with role-based visibility (use `useAuth()` hook to get user roles)
    - Active menu item highlighting (compare current route to menu item)
    - Collapse/expand on mobile (hamburger menu icon)
    - MUI Drawer component for layout
  - [ ] Create `src/components/Layout.tsx`
    - Root layout component combining Header and Sidebar
    - Main content area with `<Outlet />` for child pages
    - Responsive grid layout (sidebar left, content right; sidebar collapse on mobile)
  - [ ] Update `src/router/Router.tsx`
    - Wrap protected routes with Layout component
    - Update /dashboard route to use DashboardPage inside Layout
  - [ ] Test: Verify layout renders correctly on desktop and mobile
  - [ ] Test: Verify sidebar collapses on mobile (use DevTools responsive mode)

- [ ] Task 2: Implement user profile dropdown menu (AC: #2)
  - [ ] Create `src/components/UserProfileMenu.tsx`
    - Menu button showing user name
    - Dropdown with options: Profile, Settings, Logout
    - "Profile" option: navigates to /profile (placeholder for later)
    - "Settings" option: navigates to /settings (placeholder for later)
    - "Logout" option: calls `useAuth().logout()`, clears session, redirects to /login
  - [ ] Use MUI Menu and MenuItem components
  - [ ] Test: Click menu button and verify dropdown appears
  - [ ] Test: Click Logout and verify session cleared and redirected to /login

- [ ] Task 3: Implement navigation menu with role-based visibility (AC: #3, #5)
  - [ ] Create `src/types/index.ts` (extend from Story 3.1)
    - Define MenuItem type: { label, icon, path, roles? }
    - Define User type with roles: string[]
  - [ ] Create `src/config/menuConfig.ts`
    - Define menu items with optional roles filter:
      ```
      const menuItems = [
        { label: 'Dashboard', icon: 'home', path: '/dashboard', roles: [] }, // all users
        { label: 'Customers', icon: 'people', path: '/customers/search', roles: [] },
        { label: 'Policies', icon: 'description', path: '/policies/search', roles: [] },
        { label: 'Audit Log', icon: 'schedule', path: '/audit', roles: ['compliance_officer', 'admin'] },
        { label: 'Reports', icon: 'analytics', path: '/reports', roles: ['admin'] },
        { label: 'Admin', icon: 'settings', path: '/admin', roles: ['admin'] },
      ]
      ```
  - [ ] Update Sidebar component:
    - Get user roles from `useAuth()` hook
    - Filter menu items based on user roles (hide items if user doesn't have required role)
    - Render filtered menu items
  - [ ] Test: Login as different user roles and verify menu items appear/disappear correctly
  - [ ] Test: Verify icons display correctly (use MUI icons library)

- [ ] Task 4: Implement active menu item highlighting (AC: #5)
  - [ ] Update Sidebar component:
    - Get current route from `useLocation()` hook (React Router)
    - Compare current route pathname to menu item path
    - Apply active styling (background color, bold text, highlight) to matching menu item
  - [ ] Use MUI ListItem selected state for styling
  - [ ] Test: Click menu items and verify active highlight follows current route
  - [ ] Test: Refresh page and verify correct menu item is highlighted

- [ ] Task 5: Create Dashboard page with quick actions (AC: #6, #9)
  - [ ] Create `src/pages/DashboardPage.tsx`
    - Main content area with greeting
    - Quick action buttons:
      - "New Customer" button → navigates to /customers/create
      - "New Policy" button → navigates to /policies/create
    - Quick search inputs:
      - "Search Customer" text input with button → navigates to /customers/search?query=...
      - "Search Policy" text input with button → navigates to /policies/search?query=...
    - Grid layout for actions (2x2 on desktop, stacked on mobile)
  - [ ] Use MUI Button, TextField, Card, Grid components
  - [ ] Test: Click buttons and verify navigation works
  - [ ] Test: Enter search query and verify URL includes query parameter

- [ ] Task 6: Implement keyboard shortcut (Ctrl+K) for customer search (AC: #9)
  - [ ] Create `src/hooks/useKeyboardShortcuts.ts`
    - Custom hook to handle keyboard shortcuts
    - Listen for Ctrl+K (or Cmd+K on Mac)
    - Prevent default browser behavior
    - Trigger search modal or focus search input
  - [ ] Create `src/components/SearchModal.tsx`
    - Modal that opens on Ctrl+K
    - Search input with autocomplete suggestions
    - Keyboard navigation (up/down arrows, Enter to select)
    - Dismiss on Escape
  - [ ] Integrate into Layout or DashboardPage
  - [ ] Test: Press Ctrl+K and verify modal opens
  - [ ] Test: Type search query and verify autocomplete suggestions (mock data)
  - [ ] Test: Press Escape and verify modal closes

- [ ] Task 7: Add optional metrics dashboard (AC: #7, #10)
  - [ ] Create `src/components/MetricCard.tsx`
    - Reusable card component displaying metric
    - Shows: label, value, optional icon, optional trend
    - Skeleton loader while loading
  - [ ] Create `src/hooks/useMetrics.ts`
    - Custom hook to fetch metrics from backend
    - Endpoints: GET /api/v1/customers/count, GET /api/v1/policies/count, GET /api/v1/audit?limit=5
    - Handle loading, error, success states
    - Retry on error
  - [ ] Update DashboardPage to display metrics:
    - Total customers count
    - Total policies count
    - Recent activity feed (last 5 audit log entries)
  - [ ] Show skeleton loaders while fetching
  - [ ] Test: Verify metrics load correctly
  - [ ] Test: Verify error message if API fails
  - [ ] Test: Verify skeleton loaders display while loading

- [ ] Task 8: Implement time-of-day greeting in Header (AC: #2)
  - [ ] Create `src/utils/greetings.ts`
    - Export function: `getTimeOfDayGreeting(hour: number)` → returns "Good Morning" / "Good Afternoon" / "Good Evening"
    - Use current time: morning (6-11), afternoon (12-17), evening (18-23)
  - [ ] Update Header component:
    - Get current time and calculate greeting
    - Display greeting alongside user name: "Good Morning, Jane Smith"
  - [ ] Test: Verify correct greeting displays based on time
  - [ ] Test: Verify greeting updates on midnight boundary (optional: use React effect)

- [ ] Task 9: Test responsive design on mobile devices (AC: #4, #8)
  - [ ] Manual testing using DevTools responsive mode:
    - iPhone SE (375px), iPhone 12 (390px), iPad (768px), Desktop (1920px)
    - Verify sidebar collapses to hamburger menu on mobile
    - Verify header text and buttons are accessible on small screens
    - Verify quick action buttons stack vertically on mobile
    - Verify search inputs are full width and accessible
  - [ ] Test keyboard navigation:
    - Tab through all interactive elements
    - Verify focus indicators are visible
    - Verify screen readers can access all elements
  - [ ] Test: Verify hamburger menu opens/closes sidebar on mobile

- [ ] Task 10: Create component tests (AC: all)
  - [ ] Create `tests/integration/dashboard.test.tsx`
    - Test: DashboardPage renders with header and sidebar
    - Test: Quick action buttons navigate to correct routes
    - Test: Search inputs submit with query parameter
    - Test: Menu items filter by user role
    - Test: Ctrl+K opens search modal
    - Test: Logout button clears session and redirects
  - [ ] Create `tests/unit/Header.test.tsx`
    - Test: Time-of-day greeting displays correctly
    - Test: User name displays correctly (from Auth context)
    - Test: Profile dropdown menu appears on click
  - [ ] Create `tests/unit/Sidebar.test.tsx`
    - Test: All menu items render
    - Test: Active menu item is highlighted
    - Test: Menu items filter by role
    - Test: Hamburger menu toggles sidebar on mobile
  - [ ] Use React Testing Library and mock useAuth hook
  - [ ] Test: Run `npm test` and verify all tests pass

- [ ] Task 11: Add optional loading state transitions (AC: #10)
  - [ ] Create `src/components/SkeletonLoader.tsx`
    - Reusable skeleton loader component
    - Matches MetricCard layout
    - Pulsing animation while loading
  - [ ] Update MetricCard:
    - Show skeleton loader while `isLoading=true`
    - Show error state if API fails
    - Show data when loaded
  - [ ] Test: Verify skeleton loaders display while fetching
  - [ ] Test: Verify skeleton loaders disappear when data loads

- [ ] Task 12: Setup metrics API client (AC: #7)
  - [ ] Backend task (Story 2.X): Ensure endpoints exist:
    - GET /api/v1/customers/count → { count: number }
    - GET /api/v1/policies/count → { count: number }
    - GET /api/v1/audit?limit=5 → { items: AuditLog[], total: number }
  - [ ] Frontend API client (src/services/apiClient.ts): add methods for these endpoints
  - [ ] Test: Verify endpoints return expected data structure

## Dev Notes

### Architecture Context

Story 3.3 creates the main application layout and dashboard, serving as the central hub for the application. This story is critical for user experience and provides the navigation foundation for all subsequent features (customer management, policy management, reporting).

**Key Design Decisions:**

1. **Role-based menu visibility**: Menu items filter based on user roles (from JWT), enabling different views for different user types (agent vs. compliance officer vs. admin)
2. **Sidebar layout pattern**: Standard SPA layout with persistent navigation, improves discoverability of features
3. **Quick actions dashboard**: Reduces clicks to common tasks, improves productivity
4. **Keyboard shortcuts (Ctrl+K)**: Allows power users to quickly access search without clicking
5. **Metrics dashboard (optional)**: Gives agents quick overview of system state without navigating elsewhere

**Dependency Chain:**
- Depends on: Story 3.1 (React setup), Story 3.2 (Authentication)
- Prerequisite for: Stories 3.4-3.6 (customer and policy management pages)
- Soft dependency on: Story 2.4 (Customer search API), Story 2.X (metrics endpoints)

### Technical Requirements

1. **Frontend Libraries**:
   - `react-router-dom`: for routing and useLocation hook
   - `@mui/material`: for AppBar, Drawer, Button, TextField, Card, Grid, Menu
   - `@mui/icons-material`: for icons (Home, People, Description, Schedule, Analytics, Settings)

2. **Backend Requirements** (optional for MVP):
   - GET /api/v1/customers/count (for metrics)
   - GET /api/v1/policies/count (for metrics)
   - GET /api/v1/audit?limit=5 (for activity feed)

3. **Environment Variables**:
   - No new variables needed (uses existing config from Stories 3.1, 3.2)

### Constraints & Requirements

- **Authentication required**: Dashboard is protected route, unauthenticated users redirected to /login
- **Role-based access**: Menu visibility controlled by JWT roles claim
- **Responsive design**: Must work on mobile (375px), tablet (768px), desktop (1920px+)
- **Accessibility**: WCAG 2.1 AA compliance, keyboard navigation, screen reader support
- **Performance**: Dashboard loads in <2s even with metrics (lazy load metrics if needed)
- **Keyboard shortcuts**: Ctrl+K on Windows/Linux, Cmd+K on Mac

### Project Structure Notes

From Story 3.1 and unified-project-structure.md:

```
src/
├── pages/
│   └── DashboardPage.tsx
├── components/
│   ├── Layout.tsx (root layout)
│   ├── Header.tsx (top navigation)
│   ├── Sidebar.tsx (left navigation)
│   ├── UserProfileMenu.tsx (user dropdown)
│   ├── MetricCard.tsx (metric display)
│   ├── SkeletonLoader.tsx (loading state)
│   └── SearchModal.tsx (Ctrl+K search)
├── hooks/
│   ├── useAuth.ts (from Story 3.2)
│   ├── useMetrics.ts (fetch metrics)
│   └── useKeyboardShortcuts.ts (Ctrl+K listener)
├── config/
│   └── menuConfig.ts (menu items with roles)
├── utils/
│   ├── auth.ts (from Story 3.2)
│   └── greetings.ts (time-of-day greeting)
└── router/
    └── Router.tsx (updated with Layout wrapper)
```

**Important:** Layout component wraps all protected routes, ensuring Header and Sidebar persist across navigation. Use `<Outlet />` for child route rendering.

### Learnings from Previous Story

**From Story 3.2 (Login and Authentication):**

- **useAuth hook**: Returns `{ userId, email, name, roles }` from JWT context
- **Protected routes**: Routes inside ProtectedRoute wrapper require valid token
- **Logout functionality**: `useAuth().logout()` clears session and updates context
- **Header integration**: Header component should use `useAuth()` to display user name and roles

**From Story 3.1 (React Project Setup):**

- **MUI theme**: Use theme from context for colors, typography, spacing
- **Responsive design**: Use MUI Grid for responsive layouts; sm, md, lg breakpoints
- **Icons**: Use @mui/icons-material for consistent icon set
- **Component structure**: Keep components small, reusable, single responsibility

**Architectural decisions to maintain:**
- Use React Router v6 for navigation (useNavigate, useLocation)
- Use React Context for global state (Auth context established in Story 3.2)
- Use custom hooks for business logic (useAuth, useMetrics, useKeyboardShortcuts)
- Follow TypeScript strict mode (from Story 3.1)

**Files from Story 3.2 to build upon:**
- `src/context/AuthContext.tsx`: extend with logout capability
- `src/hooks/useAuth.ts`: extend to provide useAuth() hook for menu filtering
- `src/config/config.ts`: reference ZITADEL config for environment
- `src/router/Router.tsx`: update to include Layout wrapper and new routes

### References

- [MUI AppBar Documentation](https://mui.com/material-ui/react-app-bar/)
- [MUI Drawer (Sidebar) Documentation](https://mui.com/material-ui/react-drawer/)
- [React Router useLocation and useNavigate](https://reactrouter.com/en/main/hooks/use-location)
- [Keyboard Event Handling in React](https://react.dev/reference/react-dom/components/common#handling-other-events)
- [WCAG 2.1 Accessibility Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [Material Design Grid System](https://material.io/design/layout/understanding-layout.html)
- [Source: docs/epics.md#story-33-dashboard-page-with-navigation-and-quick-actions]
- [Source: docs/stories/3-1-react-project-setup-with-vite-and-material-design.md - Project setup]
- [Source: docs/stories/3-2-login-page-and-zitadel-oidc-authentication.md - Auth context and useAuth hook]

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 3.3 CREATED from Epic 3 via create-story workflow
- 2025-11-03: Based on epics.md acceptance criteria and Story 3.2 authentication foundation
- 2025-11-03: Incorporates learnings from Story 3.1 (project setup) and Story 3.2 (auth context)

### Completion Notes List

### File List

