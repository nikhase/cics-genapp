# Frontend Development Guide

This guide provides detailed instructions for developing features and components in the CICS GenApp frontend.

## Getting Started

### 1. Local Setup

```bash
# Clone and navigate to project
git clone <repo>
cd cics-genapp-bmad/cicsgenapp-frontend

# Install dependencies
npm install

# Copy environment file
cp .env.example .env.dev

# Start development server
npm run dev
```

The application will be available at `http://localhost:3000`.

### 2. Development Workflow

```bash
# Keep these running in separate terminals:
npm run dev           # Dev server (auto-reload on changes)
npm run lint          # ESLint (watch mode via IDE)

# Before committing:
npm run lint:fix      # Auto-fix linting issues
npm run format        # Format code with Prettier
npm run type-check    # Verify TypeScript types
npm run build         # Build for production
```

## Adding New Pages

### Step 1: Create the Page Component

Create a new file in `src/pages/`:

```typescript
// src/pages/CustomerListPage.tsx
import { Container, Box, Typography } from '@mui/material';

export const CustomerListPage: React.FC = () => {
  return (
    <Container maxWidth="lg">
      <Box sx={{ py: 4 }}>
        <Typography variant="h1">Customers</Typography>
        {/* Page content */}
      </Box>
    </Container>
  );
};

export default CustomerListPage;
```

### Step 2: Register the Route

Edit `src/router/Router.tsx`:

```typescript
// Add lazy-loaded import
const CustomerListPage = lazy(() =>
  import('../pages/CustomerListPage').then((m) => ({
    default: m.CustomerListPage,
  }))
);

// Add to routes array
{
  path: 'customers',
  children: [
    {
      index: true,
      element: (
        <Suspense fallback={<LoadingFallback />}>
          <CustomerListPage />
        </Suspense>
      ),
    },
    // ... other customer routes
  ],
}
```

### Step 3: Link to the Page

In other components, use `<Link>` or `useNavigate()`:

```typescript
import { Link } from 'react-router-dom';
import { Button } from '@mui/material';

<Button component={Link} to="/customers">
  View Customers
</Button>
```

## Adding New Components

### Component Structure

```typescript
// src/components/CustomerCard.tsx
import { Card, CardContent, CardActions, Button, Typography } from '@mui/material';
import { Customer } from '../types';

interface CustomerCardProps {
  customer: Customer;
  onEdit?: (id: string) => void;
  onDelete?: (id: string) => void;
}

export const CustomerCard: React.FC<CustomerCardProps> = ({
  customer,
  onEdit,
  onDelete,
}) => {
  return (
    <Card>
      <CardContent>
        <Typography variant="h5">{customer.firstName}</Typography>
        <Typography variant="body2">{customer.email}</Typography>
      </CardContent>
      <CardActions>
        {onEdit && (
          <Button size="small" onClick={() => onEdit(customer.id)}>
            Edit
          </Button>
        )}
        {onDelete && (
          <Button size="small" onClick={() => onDelete(customer.id)}>
            Delete
          </Button>
        )}
      </CardActions>
    </Card>
  );
};

export default CustomerCard;
```

### Best Practices

1. **Props**: Define `Props` interface for all props
2. **Types**: Import from `src/types/index.ts`
3. **Export**: Export both named and default
4. **Styling**: Use `sx` prop for MUI, not inline styles
5. **Comments**: Add JSDoc comments for complex logic

## Using TypeScript Types

All domain types are defined in `src/types/index.ts`:

```typescript
import { Customer, Policy, ApiResponse } from '../types';

// Use types for function parameters
const handleCustomerUpdate = (customer: Customer): void => {
  // API call
};

// Use types for state
const [customers, setCustomers] = useState<Customer[]>([]);

// Use types for API responses
const response: ApiResponse<Customer> = {
  data: { /* customer data */ },
  metadata: { /* response metadata */ },
};
```

## API Integration

### Using the API Service

```typescript
import { customerService } from '../services/customerService';
import { Customer } from '../types';

const MyComponent = () => {
  useEffect(() => {
    // Call API
    customerService
      .getCustomers()
      .then((customers: Customer[]) => {
        setCustomers(customers);
      })
      .catch((error) => {
        console.error('Failed to load customers:', error);
      });
  }, []);
};
```

### Creating API Services

```typescript
// src/services/customerService.ts
import { api } from './api';
import { Customer, CreateCustomerRequest, ApiResponse } from '../types';

export const customerService = {
  async getCustomers(): Promise<Customer[]> {
    const response = await api.get<ApiResponse<Customer[]>>('/customers');
    return response.data.data;
  },

  async getCustomer(id: string): Promise<Customer> {
    const response = await api.get<ApiResponse<Customer>>(`/customers/${id}`);
    return response.data.data;
  },

  async createCustomer(data: CreateCustomerRequest): Promise<Customer> {
    const response = await api.post<ApiResponse<Customer>>('/customers', data);
    return response.data.data;
  },

  async updateCustomer(id: string, data: Partial<Customer>): Promise<Customer> {
    const response = await api.put<ApiResponse<Customer>>(`/customers/${id}`, data);
    return response.data.data;
  },

  async deleteCustomer(id: string): Promise<void> {
    await api.delete(`/customers/${id}`);
  },
};
```

## Using Custom Hooks

### useAuth - Authentication

```typescript
import { useAuth } from '../hooks/useAuth';

const MyComponent = () => {
  const { user, isAuthenticated, logout } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  return <div>Welcome, {user?.email}</div>;
};
```

### useApi - Data Fetching

```typescript
import { useApi } from '../hooks/useApi';
import { Customer } from '../types';

const CustomerListPage = () => {
  const { data: customers, loading, error } = useApi<Customer[]>('/customers');

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <ul>
      {customers?.map((c) => (
        <li key={c.id}>{c.firstName}</li>
      ))}
    </ul>
  );
};
```

### useForm - Form State Management

```typescript
import { useForm } from '../hooks/useForm';
import { CreateCustomerRequest } from '../types';

const CreateCustomerForm = () => {
  const { values, errors, touched, handleChange, handleSubmit, isSubmitting } =
    useForm<CreateCustomerRequest>(
      {
        firstName: '',
        lastName: '',
        email: '',
      },
      async (values) => {
        await customerService.createCustomer(values);
      }
    );

  return (
    <form onSubmit={handleSubmit}>
      <input
        name="firstName"
        value={values.firstName}
        onChange={handleChange}
      />
      {errors.firstName && <span>{errors.firstName}</span>}
      {/* More fields */}
      <button type="submit" disabled={isSubmitting}>
        Create
      </button>
    </form>
  );
};
```

## Styling

### Using MUI Theme

```typescript
import { Box, Typography, useTheme } from '@mui/material';

const MyComponent = () => {
  const theme = useTheme();

  return (
    <Box
      sx={{
        // Use theme values
        backgroundColor: theme.palette.background.paper,
        padding: theme.spacing(2),
        // Or use MUI shorthand
        color: 'primary.main',
        margin: 2,
      }}
    >
      <Typography variant="h2" color="primary">
        My Title
      </Typography>
    </Box>
  );
};
```

### Global Styles

Add global CSS to `src/styles/global.css`:

```css
/* Global CSS Variables */
:root {
  --clarity-primary: #0050d8;
  --spacing-lg: 16px;
}

/* Global Styles */
body {
  font-family: var(--font-family);
  background-color: var(--clarity-gray-0);
}
```

## Code Quality

### Running Linter

```bash
npm run lint      # Check for issues
npm run lint:fix  # Auto-fix issues
```

Fix common issues:
- Remove unused variables
- Add missing type annotations
- Fix import ordering

### Code Formatting

```bash
npm run format  # Format all files
```

Formatting includes:
- Single quotes
- Trailing semicolons
- 2-space indentation
- 100-character line width

### Type Checking

```bash
npm run type-check  # Check for TypeScript errors
```

Ensure no `any` types without justification:

```typescript
// ❌ Bad
const data: any = response.data;

// ✓ Good
const data: Customer[] = response.data;
```

## Testing

### Manual Testing

```bash
npm run dev              # Start dev server
# Navigate to http://localhost:3000
# Test features manually
```

### Component Testing (Future)

Jest + React Testing Library will be added in Story 3.7.

For now:
```typescript
// src/components/Button.test.tsx (placeholder)
import { render } from '@testing-library/react';
import { Button } from './Button';

describe('Button Component', () => {
  it('renders correctly', () => {
    const { getByText } = render(<Button>Click me</Button>);
    expect(getByText('Click me')).toBeInTheDocument();
  });
});
```

## Building and Deployment

### Development Build

```bash
npm run build:dev  # Build with source maps
npm run preview    # Serve local preview
```

### Production Build

```bash
npm run build:prod  # Optimized production build
npm run preview     # Test production build locally
```

### Production Checklist

- [ ] All tests pass
- [ ] No ESLint warnings
- [ ] No TypeScript errors
- [ ] Bundle size < 500KB gzipped
- [ ] Environment variables configured
- [ ] API endpoints correct
- [ ] No console errors in DevTools
- [ ] Responsive design verified
- [ ] Accessibility tested (WCAG 2.1 AA)

## Debugging

### Browser DevTools

```typescript
// Log configuration
import { config } from './config';

if (config.isDevelopment) {
  console.info('Config:', config);
}

// React DevTools
// Install: https://react-devtools-tutorial.vercel.app/
```

### TypeScript Errors

```bash
# Get full error details
npm run type-check

# Check specific file
npx tsc --noEmit src/components/MyComponent.tsx
```

### Performance Profiling

In Chrome DevTools:
1. Open Performance tab
2. Record interaction
3. Check Component render times
4. Identify bottlenecks

## Common Patterns

### Protected Routes

```typescript
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

// Use in Router
<Route
  path="dashboard"
  element={
    <ProtectedRoute>
      <DashboardPage />
    </ProtectedRoute>
  }
/>
```

### Loading States

```typescript
const MyComponent = () => {
  const { data, loading, error } = useApi<Customer[]>('/customers');

  return (
    <>
      {loading && <CircularProgress />}
      {error && <Alert severity="error">{error.message}</Alert>}
      {data && <CustomerList customers={data} />}
    </>
  );
};
```

### Error Boundaries

```typescript
class ErrorBoundary extends React.Component<
  { children: React.ReactNode },
  { hasError: boolean }
> {
  constructor(props: { children: React.ReactNode }) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('Error caught:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return <Typography>Something went wrong</Typography>;
    }
    return this.props.children;
  }
}
```

## Resources

- **React**: https://react.dev/
- **TypeScript**: https://www.typescriptlang.org/docs/
- **Material-UI**: https://mui.com/
- **React Router**: https://reactrouter.com/
- **Vite**: https://vitejs.dev/
- **Clarity Design System**: https://clarity.design/

## Getting Help

1. Check [README.md](../README.md) for setup issues
2. Review [Type Definitions](../src/types/index.ts)
3. Check [API Integration](#api-integration) section
4. Search existing issues in repository
5. Ask team members in Slack/Teams

---

**Last Updated**: November 2024
