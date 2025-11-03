# Story 3.1: React Project Setup with Vite and Material Design

Status: ready-for-dev

## Story

As a Frontend Developer,
I want to set up a React 18 SPA with Vite, TypeScript, routing, and Material Design 3,
So that I have a solid foundation for building UI components.

## Acceptance Criteria

1. React 18 project created with Vite (npm create vite@latest cicsgenapp-frontend -- --template react-ts)
2. TypeScript configured for type safety (tsconfig.json with strict mode enabled)
3. React Router v6 configured for client-side routing (BrowserRouter, Routes, lazy loading)
4. Material-UI (MUI) v5 installed with TypeScript support
5. Clarity Enterprise Design System theme customization applied (color palette, typography)
6. Project structure created:
   - src/components/ (reusable UI components)
   - src/pages/ (page-level components: Login, Dashboard, CustomerSearch, etc.)
   - src/services/ (API client, authentication service)
   - src/hooks/ (custom React hooks: useAuth, useApi, etc.)
   - src/context/ (React Context: AuthContext, ThemeContext)
   - src/types/ (TypeScript interfaces)
   - src/styles/ (theme configuration, global CSS)
7. ESLint and Prettier configured for code style
8. Public folder with favicon, index.html, robots.txt
9. Environment variables configured (.env.dev, .env.prod) with API_BASE_URL
10. Build process tested: npm run build (optimized build, size < 500KB gzipped for JavaScript bundle)
11. Dev server runs on http://localhost:3000

## Tasks / Subtasks

- [ ] Task 1: Initialize Vite React TypeScript project (AC: #1, #2)
  - [ ] Run: `npm create vite@latest cicsgenapp-frontend -- --template react-ts`
  - [ ] Navigate to project directory: `cd cicsgenapp-frontend`
  - [ ] Install dependencies: `npm install`
  - [ ] Verify dev server runs: `npm run dev` (should start on http://localhost:5173)
  - [ ] Review default folder structure and Vite configuration (vite.config.ts)
  - [ ] Test: Verify dev build and production build work without errors

- [ ] Task 2: Configure TypeScript for strict type safety (AC: #2)
  - [ ] Update tsconfig.json:
    - Set `"strict": true` (enables all strict type checking options)
    - Set `"noImplicitAny": true`
    - Set `"strictNullChecks": true`
    - Set `"strictFunctionTypes": true`
    - Set `"noUnusedLocals": true` (warn on unused variables)
    - Set `"noUnusedParameters": true`
    - Set `"noImplicitReturns": true`
    - Set `"esModuleInterop": true` (for CommonJS/ESM interop)
    - Set `"skipLibCheck": true` (skip type checking of declaration files)
    - Set `"forceConsistentCasingInFileNames": true`
  - [ ] Create tsconfig.app.json and tsconfig.node.json if needed
  - [ ] Test: Verify TypeScript compilation with `npm run build`

- [ ] Task 3: Install and configure React Router v6 (AC: #3)
  - [ ] Install React Router: `npm install react-router-dom@^6`
  - [ ] Create `src/router/Router.tsx`:
    - Define root layout component with `<Outlet />`
    - Configure routes: `/login`, `/dashboard`, `/customers/...`, `/policies/...`, etc.
    - Implement lazy loading for routes: `const Dashboard = lazy(() => import('../pages/Dashboard'))`
    - Wrap lazy-loaded components with `<Suspense fallback={<Loading />}>`
  - [ ] Create `src/pages/`:
    - Create placeholder pages: LoginPage.tsx, DashboardPage.tsx, NotFoundPage.tsx
  - [ ] Create `src/components/Layout.tsx`:
    - Root layout component with header, sidebar, main content area
    - `<Outlet />` for child routes
  - [ ] Update `src/main.tsx`:
    - Wrap App with `<BrowserRouter>` and router configuration
  - [ ] Test: Navigate between routes (should not reload page)
  - [ ] Test: Verify lazy loading works (check Network tab in DevTools)

- [ ] Task 4: Install and configure Material-UI (MUI) v5 (AC: #4)
  - [ ] Install MUI: `npm install @mui/material @emotion/react @emotion/styled`
  - [ ] Install MUI Icons: `npm install @mui/icons-material`
  - [ ] Install MUI Lab (optional utilities): `npm install @mui/lab`
  - [ ] Create `src/theme/theme.ts`:
    - Import `createTheme` from @mui/material/styles
    - Define custom theme with:
      - Color palette (primary, secondary, success, error, warning)
      - Typography (font family, sizes, weights)
      - Component overrides (Button, TextField, etc.)
    - Export theme: `export const theme = createTheme({...})`
  - [ ] Update `src/App.tsx`:
    - Import `ThemeProvider` from @mui/material/styles
    - Wrap app with `<ThemeProvider theme={theme}>`
    - Test: Verify MUI components render with custom theme

- [ ] Task 5: Apply Clarity Enterprise Design System theme customization (AC: #5)
  - [ ] Create `src/theme/clarityTheme.ts`:
    - Import Clarity color palette (or define custom colors matching Clarity):
      - Primary blue: #0050D8
      - Secondary gray: #6A7781
      - Success green: #2D8F3E
      - Warning yellow: #E6A600
      - Error red: #D31C1C
      - Neutral grays: #F5F5F5, #D9DCDE, #6A7781, #1D3D5C, #000000
    - Define typography:
      - Font family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif (or Clarity's font)
      - Headings: H1 (32px), H2 (24px), H3 (18px), H4 (16px)
      - Body: 14px, 12px
    - Define component overrides:
      - Button: Clarity button styles (filled, outlined, ghost)
      - TextField: Clarity form styles
      - Card: Clarity card styles
  - [ ] Update theme.ts to merge Clarity theme
  - [ ] Test: Verify Clarity colors and typography applied in browser

- [ ] Task 6: Create folder structure and placeholder components (AC: #6)
  - [ ] Create folders:
    - src/components/ (Button.tsx, Card.tsx, Header.tsx, Sidebar.tsx, etc.)
    - src/pages/ (LoginPage.tsx, DashboardPage.tsx, CustomerSearchPage.tsx, etc.)
    - src/services/ (api.ts, authService.ts, customerService.ts)
    - src/hooks/ (useAuth.ts, useApi.ts, useForm.ts)
    - src/context/ (AuthContext.tsx, ThemeContext.tsx)
    - src/types/ (index.ts - export all TypeScript interfaces)
    - src/styles/ (already contains theme files, add global.css)
    - src/utils/ (helpers, formatters, validators)
  - [ ] Create placeholder files in each folder (empty or with TODOs)
  - [ ] Create `src/types/index.ts`:
    - Define TypeScript interfaces: User, Customer, Policy, ApiResponse, etc.
    - Export all types for use throughout app
  - [ ] Create `src/styles/global.css`:
    - Global styles (reset, font family, colors)
    - CSS variables for theme values
  - [ ] Create `src/utils/helpers.ts` with utility functions (formatDate, truncateString, etc.)

- [ ] Task 7: Install and configure ESLint and Prettier (AC: #7)
  - [ ] Install ESLint: `npm install --save-dev eslint eslint-plugin-react eslint-plugin-react-hooks eslint-plugin-@typescript-eslint`
  - [ ] Initialize ESLint: `npx eslint --init` (select: JavaScript modules, React, TypeScript, browser/ES2021)
  - [ ] Update .eslintrc.json:
    - Add rules: `"react/react-in-jsx-scope": "off"`, `"@typescript-eslint/no-unused-vars": "error"`
    - Configure React version: `"react": { "version": "detect" }`
  - [ ] Install Prettier: `npm install --save-dev prettier eslint-config-prettier eslint-plugin-prettier`
  - [ ] Create .prettierrc.json:
    - Set `"singleQuote": true`, `"trailing-comma": "es5"`, `"printWidth": 100`, `"tabWidth": 2`, `"useTabs": false`
  - [ ] Create .prettierignore: ignore build, node_modules, dist
  - [ ] Add npm scripts to package.json:
    - `"lint": "eslint src --ext ts,tsx"`,
    - `"lint:fix": "eslint src --ext ts,tsx --fix"`,
    - `"format": "prettier --write \"src/**/*.{ts,tsx,css}\""`
  - [ ] Test: Run `npm run lint` and verify no errors
  - [ ] Test: Run `npm run format` and verify code is formatted

- [ ] Task 8: Set up public folder with favicon and metadata (AC: #8)
  - [ ] Create public/ folder if not exists
  - [ ] Add favicon.ico (or favicon.png)
  - [ ] Update public/index.html:
    - Set title: `<title>CICS GenApp Modernization</title>`
    - Add meta tags: charset, viewport, description, theme-color
    - Add favicon: `<link rel="icon" href="/favicon.ico" />`
  - [ ] Create public/robots.txt:
    - Allow all: `User-agent: *` `Disallow:`
  - [ ] Create public/manifest.json (if using PWA):
    - Name, short_name, icons, theme_color, background_color
  - [ ] Test: Verify favicon displays in browser tab
  - [ ] Test: Verify robots.txt accessible at /robots.txt

- [ ] Task 9: Configure environment variables for dev and prod (AC: #9)
  - [ ] Create .env.dev:
    - `VITE_API_BASE_URL=http://localhost:8080`
    - `VITE_ZITADEL_CLIENT_ID=<dev-client-id>`
    - `VITE_ZITADEL_AUTHORITY=<dev-zitadel-url>`
  - [ ] Create .env.prod:
    - `VITE_API_BASE_URL=https://api.example.com`
    - `VITE_ZITADEL_CLIENT_ID=<prod-client-id>`
    - `VITE_ZITADEL_AUTHORITY=<prod-zitadel-url>`
  - [ ] Create .env.example (template for developers):
    - Document all environment variables with placeholders
  - [ ] Update src/config/config.ts:
    - Load environment variables: `export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL`
    - Export all config values
  - [ ] Create src/config/index.ts exporting config
  - [ ] Update vite.config.ts:
    - Set define variables for compile-time constants (if needed)
  - [ ] Test: Verify environment variables loaded in browser console

- [ ] Task 10: Test build and dev server startup (AC: #10, #11)
  - [ ] Run dev server: `npm run dev`
    - Verify starts on http://localhost:5173 (or configured port)
    - Verify hot module replacement works (save file, page updates)
    - Verify no console errors
  - [ ] Run production build: `npm run build`
    - Verify build completes without errors
    - Check dist/ folder for output
    - Run `npm run preview` to serve production build
    - Verify application runs from dist files
  - [ ] Check bundle size: `npm run build` and inspect dist/assets/
    - Verify main JavaScript bundle < 500KB gzipped
    - Document bundle size metrics
  - [ ] Test TypeScript compilation: `npm run build` (should compile without errors)
  - [ ] Test: Verify dev server restart capability (stop and start)

- [ ] Task 11: Create comprehensive README with setup instructions (AC: #11)
  - [ ] Create/update README.md:
    - Project overview and purpose
    - Prerequisites (Node.js version, npm, git)
    - Installation steps:
      - `git clone <repo>`
      - `cd cicsgenapp-frontend`
      - `npm install`
      - `npm run dev`
    - Available npm scripts with descriptions
    - Environment setup section (copy .env.example to .env.dev)
    - Folder structure explanation
    - Contributing guidelines (branch naming, commit messages)
    - Troubleshooting section
  - [ ] Create docs/DEVELOPMENT.md with detailed development guide:
    - How to run tests
    - How to add new pages
    - How to add new components
    - TypeScript best practices
    - Component file structure template
  - [ ] Test: Verify README instructions allow new developer to setup project

## Dev Notes

### Architecture Context

Story 3.1 establishes the foundational React 18 frontend infrastructure. This story is the frontend equivalent of Story 1.2 (Spring Boot Backend Setup) - it creates the build system, project structure, styling foundation, and development tooling that all subsequent frontend stories depend on.

**Key Design Decisions:**

1. **Vite over Create React App**: Faster build times (es native modules), better DX, smaller bundle size
2. **TypeScript Strict Mode**: Enforces type safety from day one, prevents runtime errors
3. **React Router v6**: Modern routing with nested routes, lazy loading, data loading patterns
4. **Material-UI v5**: Comprehensive component library aligned with Clarity Enterprise Design System
5. **Theme Customization**: Clarity color palette and typography applied via MUI theme provider
6. **Environment Variables**: VITE_ prefix for Vite's env variable convention

**Dependency Chain:**
- Frontend can develop independently once backend APIs are available (Stories 2.1-2.8)
- This story is a prerequisite for all subsequent frontend stories (3.2-3.8)
- Integration with backend API happens in Story 3.8 (API Integration and Client Service Layer)

### Technical Requirements

1. **Node.js**: v18+ LTS (same as project runtime)
2. **npm**: v9+
3. **React**: v18.2+
4. **Vite**: v5+
5. **TypeScript**: v5.2+
6. **React Router**: v6.8+
7. **Material-UI**: v5.14+
8. **Emotion** (peer dependency for MUI): latest

### Constraints & Requirements

- **No hard dependency on backend API yet** - Frontend can develop UI independently
- **Type safety mandatory**: strict TypeScript throughout
- **Mobile-responsive design**: all components must work on mobile (covered in later stories)
- **Performance target**: < 500KB gzipped JavaScript bundle
- **Accessibility**: WCAG 2.1 AA compliance (enforced via linting)

### Project Structure Notes

From unified-project-structure.md, the frontend follows standard React SPA conventions:

```
cicsgenapp-frontend/
├── src/
│   ├── main.tsx (entry point)
│   ├── App.tsx (root component)
│   ├── components/ (reusable UI components)
│   ├── pages/ (page-level components, 1:1 with routes)
│   ├── services/ (API client, auth, etc.)
│   ├── hooks/ (custom React hooks)
│   ├── context/ (React Context providers)
│   ├── types/ (TypeScript interfaces)
│   ├── styles/ (theme, global CSS)
│   ├── utils/ (helpers, formatters)
│   ├── router/ (route definitions)
│   └── config/ (environment configuration)
├── public/ (favicon, metadata)
├── vite.config.ts
├── tsconfig.json
├── package.json
├── .env.dev, .env.prod, .env.example
├── .eslintrc.json
├── .prettierrc.json
├── README.md
└── docs/
    └── DEVELOPMENT.md
```

**Important:** Keep frontend build artifacts (dist/) separate from backend (genapp-backend/). Both projects share same git repo but build independently.

### Learnings from Previous Story

**From Story 2.8 (API Documentation - most recent completed Epic 2 story):**

- **Backend readiness**: Customer APIs are documented and ready for integration
- **API patterns to follow**: Response format uses data/metadata wrapper (not raw data)
- **Authentication model**: JWT bearer tokens from Zitadel - frontend must store and send in Authorization header
- **Error handling pattern**: Validation errors return detailed field-level errors
- **Correlation IDs**: All API requests should include X-Trace-Id header for request tracing

**Architectural decisions to reuse in frontend:**
- Use same ApiResponse wrapper format (data + metadata) when mocking API responses
- AuthContext should follow OAuth2/OIDC flow pattern for Zitadel integration
- Error handling service should map backend validation errors to user-friendly messages
- API client should automatically attach JWT to all requests

**Files from Story 2.8 to be aware of:**
- Backend endpoints documented in Swagger: http://localhost:8080/api/docs
- API schema available at: http://localhost:8080/v3/api-docs (for code generation if needed)
- Example payloads in test files can guide frontend request/response shapes

### References

- [Vite Official Documentation](https://vitejs.dev/)
- [React 18 Documentation](https://react.dev/)
- [React Router v6 Documentation](https://reactrouter.com/)
- [TypeScript 5.2 Handbook](https://www.typescriptlang.org/docs/)
- [Material-UI v5 Documentation](https://mui.com/material-ui/)
- [Clarity Design System](https://clarity.design/)
- [ESLint Configuration](https://eslint.org/docs/latest/use/configure)
- [Prettier Code Formatter](https://prettier.io/)
- [Source: docs/epics.md#epic-3-react-frontend-authentication--core-ui - Story 3.1]
- [Source: docs/epics.md#epic-2-customer-service-api - Reference for backend API design]
- [Source: docs/stories/2-8-api-documentation-openapi-swagger-and-audit-logging.md - API patterns]
- [Source: docs/unified-project-structure.md - Frontend folder conventions]
- [Source: docs/tech-stack.md - Technology choices and versions]

## Dev Agent Record

### Context Reference

- docs/stories/3-1-react-project-setup-with-vite-and-material-design.context.xml

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 3.1 CREATED from Epic 3 - first story in React frontend epic
- 2025-11-03: Based on epics.md acceptance criteria and Epic 3 goal
- 2025-11-03: Incorporates learnings from Story 2.8 (API patterns, authentication flow)

### Completion Notes List

### File List

