/**
 * Application Router Configuration
 * Defines all routes with lazy loading and error boundaries
 */

import { lazy, Suspense, ReactNode } from 'react';
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router-dom';
import type { RouteObject } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { NotFoundPage } from '../pages/NotFoundPage';
import { LoadingFallback } from '../components/LoadingFallback';
import { ProtectedRoute } from './ProtectedRoute';
import { AuthProvider } from '../context/AuthContext';

// Lazy-loaded pages for code splitting
const LoginPage = lazy(() => import('../pages/LoginPage').then((m) => ({ default: m.LoginPage })));
const AuthCallbackPage = lazy(() =>
  import('../pages/AuthCallbackPage').then((m) => ({ default: m.AuthCallbackPage }))
);
const DashboardPage = lazy(() =>
  import('../pages/DashboardPage').then((m) => ({ default: m.DashboardPage }))
);

/**
 * Route definitions
 */
const routes: RouteObject[] = [
  {
    path: '/login',
    element: (
      <Suspense fallback={<LoadingFallback />}>
        <LoginPage />
      </Suspense>
    ),
  },
  {
    path: '/auth/callback',
    element: (
      <Suspense fallback={<LoadingFallback />}>
        <AuthCallbackPage />
      </Suspense>
    ),
  },
  {
    path: '/',
    element: <Layout />,
    children: [
      {
        index: true,
        element: (
          <Suspense fallback={<LoadingFallback />}>
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          </Suspense>
        ),
      },
      {
        path: 'dashboard',
        element: (
          <Suspense fallback={<LoadingFallback />}>
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          </Suspense>
        ),
      },
      // Routes for customers (Story 3.4-3.6)
      {
        path: 'customers',
        children: [
          {
            index: true,
            element: <Navigate to="/customers/search" replace />,
          },
          {
            path: 'search',
            element: <NotFoundPage />, // To be implemented in Story 3.4
          },
          {
            path: ':id',
            element: <NotFoundPage />, // To be implemented in Story 3.5
          },
          {
            path: ':id/edit',
            element: <NotFoundPage />, // To be implemented in Story 3.5
          },
          {
            path: 'create',
            element: <NotFoundPage />, // To be implemented in Story 3.6
          },
        ],
      },
      // Routes for policies (Stories 5.x)
      {
        path: 'policies',
        children: [
          {
            index: true,
            element: <Navigate to="/policies/search" replace />,
          },
          {
            path: 'search',
            element: <NotFoundPage />, // To be implemented in Story 5.x
          },
          {
            path: ':id',
            element: <NotFoundPage />, // To be implemented in Story 5.x
          },
          {
            path: ':id/edit',
            element: <NotFoundPage />, // To be implemented in Story 5.x
          },
          {
            path: 'create',
            element: <NotFoundPage />, // To be implemented in Story 5.x
          },
        ],
      },
    ],
  },
  // Catch-all 404
  {
    path: '*',
    element: <NotFoundPage />,
  },
];

/**
 * Create browser router
 */
const browserRouter = createBrowserRouter(routes);

/**
 * App Root Component with Auth Provider
 */
export const AppWithAuth: React.FC = () => (
  <AuthProvider>
    <RouterProvider router={browserRouter} />
  </AuthProvider>
);

/**
 * Create and export browser router (legacy export for compatibility)
 */
export const router = browserRouter;

export default browserRouter;
