/**
 * Application Router Configuration
 * Defines all routes with lazy loading and error boundaries
 */

import { lazy, Suspense } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import type { RouteObject } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { NotFoundPage } from '../pages/NotFoundPage';
import { LoadingFallback } from '../components/LoadingFallback';

// Lazy-loaded pages for code splitting
const LoginPage = lazy(() => import('../pages/LoginPage').then((m) => ({ default: m.LoginPage })));
const DashboardPage = lazy(() =>
  import('../pages/DashboardPage').then((m) => ({ default: m.DashboardPage }))
);

/**
 * Route definitions
 */
const routes: RouteObject[] = [
  {
    path: '/',
    element: <Layout />,
    children: [
      {
        index: true,
        element: (
          <Suspense fallback={<LoadingFallback />}>
            <DashboardPage />
          </Suspense>
        ),
      },
      {
        path: 'dashboard',
        element: (
          <Suspense fallback={<LoadingFallback />}>
            <DashboardPage />
          </Suspense>
        ),
      },
      {
        path: 'login',
        element: (
          <Suspense fallback={<LoadingFallback />}>
            <LoginPage />
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
 * Create and export browser router
 */
export const router = createBrowserRouter(routes);

export default router;
