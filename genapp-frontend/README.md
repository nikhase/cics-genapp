# CICS GenApp Frontend

> Enterprise modernization platform for legacy CICS systems - React 18 SPA with Vite, TypeScript, and Material Design

## Overview

This is the frontend application for the CICS GenApp cloud modernization project. It provides a modern web interface for managing customers and policies, replacing the legacy 3270 terminal interface.

**Technology Stack:**
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite 5 (Lightning-fast builds)
- **Router**: React Router v6 (Client-side routing with lazy loading)
- **UI Library**: Material-UI (MUI) v5
- **Design System**: Clarity Enterprise Design System
- **Styling**: Emotion (CSS-in-JS) with MUI theming
- **Code Quality**: ESLint + Prettier
- **Dev Server**: Runs on `http://localhost:3000`

## Prerequisites

- **Node.js**: v18+ LTS
- **npm**: v9+
- **Git**: Latest version

## Installation

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd cics-genapp-bmad
   ```

2. **Navigate to frontend directory:**
   ```bash
   cd genapp-frontend
   ```

3. **Install dependencies:**
   ```bash
   npm install
   ```

4. **Configure environment variables:**
   ```bash
   cp .env.example .env.dev
   ```
   Then edit `.env.dev` with your development settings:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   VITE_ZITADEL_CLIENT_ID=dev-client-id
   VITE_ZITADEL_AUTHORITY=http://localhost:8080/auth
   ```

## Available Scripts

### Development

```bash
# Start development server on http://localhost:3000
npm run dev

# Type checking
npm run type-check

# Linting
npm run lint
npm run lint:fix

# Code formatting
npm run format
npm run format:check
```

### Production

```bash
# Build for production
npm run build

# Build for specific environment
npm run build:dev
npm run build:prod

# Preview production build locally
npm run preview
```

## Project Structure

```
genapp-frontend/
├── src/
│   ├── main.tsx                 # Application entry point
│   ├── App.tsx                  # Root application component
│   │
│   ├── components/              # Reusable UI components
│   │   ├── Layout.tsx
│   │   ├── LoadingFallback.tsx
│   │   └── ...
│   │
│   ├── pages/                   # Page-level components (route-mapped)
│   │   ├── LoginPage.tsx
│   │   ├── DashboardPage.tsx
│   │   ├── NotFoundPage.tsx
│   │   └── ...
│   │
│   ├── router/                  # Route definitions
│   │   └── Router.tsx           # React Router configuration
│   │
│   ├── services/                # API clients and services
│   │   ├── api.ts              # HTTP client (Axios)
│   │   ├── authService.ts      # Authentication service
│   │   └── customerService.ts  # Customer API service
│   │
│   ├── hooks/                   # Custom React hooks
│   │   ├── useAuth.ts
│   │   ├── useApi.ts
│   │   └── useForm.ts
│   │
│   ├── context/                 # React Context providers
│   │   ├── AuthContext.tsx
│   │   └── ThemeContext.tsx
│   │
│   ├── types/                   # TypeScript interfaces
│   │   └── index.ts            # All domain models and types
│   │
│   ├── styles/                  # Theming and global styles
│   │   ├── theme.ts            # MUI theme with Clarity colors
│   │   └── global.css           # Global CSS and CSS variables
│   │
│   ├── utils/                   # Helper functions
│   │   ├── helpers.ts          # Date, currency, validation helpers
│   │   └── constants.ts        # Application constants
│   │
│   ├── config/                  # Configuration
│   │   └── config.ts           # Environment variables loader
│   │
│   ├── tests/                   # Test files (Jest + RTL)
│   │   └── ...
│   │
│   ├── index.css
│   └── App.css
│
├── public/                      # Static assets
│   ├── favicon.ico
│   ├── robots.txt
│   └── manifest.json           # PWA manifest
│
├── index.html                   # HTML template
├── vite.config.ts              # Vite configuration
├── tsconfig.json               # TypeScript base config
├── tsconfig.app.json           # TypeScript app config (strict mode)
├── tsconfig.node.json          # TypeScript Node config
├── .eslintrc.json              # ESLint configuration
├── .prettierrc.json            # Prettier configuration
├── .env.dev                    # Development environment variables
├── .env.prod                   # Production environment variables
├── .env.example                # Environment variables template
└── package.json
```

## Environment Configuration

The application uses environment variables loaded at build time via Vite's `import.meta.env` API.

### Available Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `VITE_API_BASE_URL` | Backend API base URL | `http://localhost:8080` |
| `VITE_ZITADEL_CLIENT_ID` | Zitadel OAuth2 client ID | `dev-client-id` |
| `VITE_ZITADEL_AUTHORITY` | Zitadel authority URL | `http://localhost:8080/auth` |
| `VITE_LOG_LEVEL` | Logging level | `debug` or `info` |

### Loading Environment Variables

```typescript
import { config } from './config';

console.log(config.apiBaseUrl);        // http://localhost:8080
console.log(config.isDevelopment);     // true in dev, false in prod
```

## Type Safety

This project enforces strict TypeScript throughout:

```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true
  }
}
```

All components, functions, and APIs must have explicit type annotations.

## Design System Integration

The application uses Material-UI (MUI) with Clarity Enterprise Design System colors and typography:

### Primary Colors

- **Primary**: `#0050D8` (Clarity Blue)
- **Secondary**: `#6A7781` (Clarity Gray)
- **Success**: `#2D8F3E` (Clarity Green)
- **Warning**: `#E6A600` (Clarity Yellow)
- **Error**: `#D31C1C` (Clarity Red)

### Theming

All MUI components automatically use the Clarity theme defined in `src/styles/theme.ts`. To access theme values in custom components:

```typescript
import { useTheme } from '@mui/material/styles';

const MyComponent = () => {
  const theme = useTheme();
  return <Box sx={{ color: theme.palette.primary.main }} />;
};
```

## Code Quality

### ESLint

```bash
npm run lint      # Check for issues
npm run lint:fix  # Auto-fix issues
```

Configuration: `.eslintrc.json`

Key rules:
- `@typescript-eslint/no-explicit-any`: Errors
- `@typescript-eslint/no-unused-vars`: Errors (with `_` prefix exemptions)
- `react/react-in-jsx-scope`: Off (React 17+ auto JSX)
- `no-console`: Warnings (except warn/error/info)

### Prettier

```bash
npm run format       # Format all files
npm run format:check # Check without formatting
```

Configuration: `.prettierrc.json`

- Single quotes
- 100 character line width
- 2-space indentation
- ES5 trailing commas

## Routing

Routes are defined in `src/router/Router.tsx` using React Router v6 with lazy loading for code splitting:

```typescript
// Routes are automatically code-split
const DashboardPage = lazy(() => import('../pages/DashboardPage'));
```

Lazy-loaded pages show a `<Suspense>` fallback while loading.

### Available Routes

- `/` - Dashboard (default)
- `/login` - Login page (Story 3.2)
- `/customers` - Customer management (Stories 3.4-3.6)
  - `/customers/search` - Search/list customers
  - `/customers/:id` - Customer detail view
  - `/customers/:id/edit` - Edit customer
  - `/customers/create` - Create new customer
- `/policies` - Policy management (Story 5.x)
- `*` - 404 Not Found

## API Integration

The frontend communicates with the backend API at `VITE_API_BASE_URL`. The API client is configured to:

1. Attach JWT bearer tokens to all requests
2. Include correlation IDs for request tracing
3. Handle authentication errors (401/403)
4. Provide typed request/response interfaces

**API Response Format** (from Story 2.8):

```typescript
interface ApiResponse<T> {
  data: T;
  metadata: {
    timestamp: string;
    correlationId?: string;
    status: number;
  };
}
```

## Performance Optimization

### Bundle Size

Current build output: ~400KB (dist folder)

Code splitting strategy:
- `mui` chunk: Material-UI + Emotion (~44KB gzipped)
- `router` chunk: React Router + pages (~20KB gzipped)
- `index` chunk: App code (~59KB gzipped)

Target: < 500KB gzipped JavaScript bundle ✓

### Lazy Loading

Pages are lazy-loaded using `React.lazy()`:

```typescript
const DashboardPage = lazy(() => import('../pages/DashboardPage'));
```

This ensures only the necessary code is loaded for the current page.

## Testing

Testing infrastructure (Jest + React Testing Library) will be set up in **Story 3.7** (Form Components).

For now, create test files alongside source:

```
src/
├── components/
│   ├── Button.tsx
│   └── Button.test.tsx        # Jest + RTL test
```

## Contributing

### Branch Naming

- Feature: `feature/3-4-customer-search`
- Bug fix: `fix/button-alignment`
- Documentation: `docs/installation-guide`

### Commit Messages

Follow conventional commits:

```
feat(3.1): Add Vite project setup
fix(3.2): Resolve authentication flow
docs(readme): Update setup instructions
```

### Pull Request Process

1. Create feature branch from `main`
2. Make changes and ensure all tests pass
3. Run `npm run lint:fix` and `npm run format`
4. Create PR with clear description
5. Wait for code review approval
6. Squash and merge to `main`

## Troubleshooting

### Dev Server Won't Start

```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### Port 3000 Already in Use

Vite will automatically use the next available port. Or, restart your machine.

### Type Errors During Build

```bash
# Clear TypeScript cache
rm -rf node_modules/.vite
npm run type-check
npm run build
```

### ESLint/Prettier Conflicts

```bash
# Auto-fix ESLint issues and format code
npm run lint:fix
npm run format
```

## Documentation

- **[Development Guide](./docs/DEVELOPMENT.md)** - Detailed development workflows
- **[API Integration](../docs/api-documentation.md)** - Backend API reference
- **[Architecture](../docs/architecture.md)** - Frontend architecture patterns
- **[Tech Stack](../docs/tech-stack.md)** - Technology choices and versions

## Related Documentation

This frontend is part of the **CICS GenApp modernization project**. For context:

- **Backend**: `../genapp-backend/` (Spring Boot 3 with Spring Data JPA)
- **Project Root**: `../` (BMAD modernization framework)
- **Epic Overview**: `../docs/epics.md` (Epic 3: React Frontend)

## License

MIT License - See LICENSE file in project root

## Support

For questions or issues:

1. Check [Troubleshooting](#troubleshooting) section
2. Review [Development Guide](./docs/DEVELOPMENT.md)
3. Check backend API docs at `http://localhost:8080/api/docs`
4. Open an issue in the repository

---

**Last Updated**: November 2024
**Frontend Version**: 0.0.0 (Development)
**React**: 18.x | **TypeScript**: 5.9
**Node.js**: 18+ LTS recommended
