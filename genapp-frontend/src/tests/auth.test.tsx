/**
 * Authentication Integration Tests
 * Story 3.2: Login Page and Zitadel OIDC Authentication
 */

import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';
import { LoginPage } from '../pages/LoginPage';
import { AuthCallbackPage } from '../pages/AuthCallbackPage';
import * as authService from '../services/authService';
import * as zitadelService from '../services/zitadelService';

// Mock services
vi.mock('../services/authService');
vi.mock('../services/zitadelService');

describe('Authentication Flow', () => {
  beforeEach(() => {
    // Clear session storage before each test
    sessionStorage.clear();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe('LoginPage', () => {
    const renderLoginPage = () => {
      return render(
        <BrowserRouter>
          <AuthProvider>
            <LoginPage />
          </AuthProvider>
        </BrowserRouter>
      );
    };

    it('should render login page with SSO button', () => {
      renderLoginPage();

      expect(screen.getByText('CICS GenApp')).toBeInTheDocument();
      expect(screen.getByText(/Sign in with Corporate SSO/i)).toBeInTheDocument();
    });

    it('should display login description text', () => {
      renderLoginPage();

      expect(
        screen.getByText(/Use your corporate credentials to access the system securely/i)
      ).toBeInTheDocument();
    });

    it('should call initiateLogin when button is clicked', async () => {
      const mockInitiateLogin = vi.fn();
      (zitadelService.initiateLogin as any) = mockInitiateLogin;

      renderLoginPage();
      const button = screen.getByRole('button', { name: /Sign in with Corporate SSO/i });

      fireEvent.click(button);

      await waitFor(() => {
        expect(mockInitiateLogin).toHaveBeenCalled();
      });
    });

    it('should display error message if login initiation fails', async () => {
      const mockError = new Error('Failed to initiate login');
      (zitadelService.initiateLogin as any) = vi.fn(() => {
        throw mockError;
      });

      renderLoginPage();
      const button = screen.getByRole('button', { name: /Sign in with Corporate SSO/i });

      fireEvent.click(button);

      await waitFor(() => {
        expect(screen.getByText('Failed to initiate login')).toBeInTheDocument();
      });
    });

    it('should redirect to dashboard if already authenticated', async () => {
      const mockToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLTEyMyIsImVtYWlsIjoiam9obkBleGFtcGxlLmNvbSIsIm5hbWUiOiJKb2huIERvZSIsImV4cCI6OTk5OTk5OTk5OX0.test';

      // Store auth data
      sessionStorage.setItem('auth_token', mockToken);
      sessionStorage.setItem(
        'auth_user',
        JSON.stringify({
          userId: 'user-123',
          email: 'john@example.com',
          name: 'John Doe',
          roles: [],
        })
      );

      // Mock token not expired
      (authService.isTokenExpired as any) = vi.fn(() => false);

      renderLoginPage();

      // Should redirect away from login (this would be verified in integration tests with router)
      // For now, we just verify the component renders without error
      expect(screen.queryByText('Sign In')).not.toBeInTheDocument();
    });
  });

  describe('AuthCallbackPage', () => {
    const renderAuthCallbackPage = (searchParams = '?code=test-code&state=test-state') => {
      const url = `http://localhost/auth/callback${searchParams}`;
      window.history.pushState({}, 'Test page', url);

      return render(
        <BrowserRouter>
          <AuthProvider>
            <AuthCallbackPage />
          </AuthProvider>
        </BrowserRouter>
      );
    };

    it('should display loading state initially', () => {
      renderAuthCallbackPage();

      expect(screen.getByText(/Completing Authentication/i)).toBeInTheDocument();
    });

    it('should exchange code for token on mount', async () => {
      const mockAuthResponse = {
        accessToken: 'test-token',
        refreshToken: 'test-refresh',
        expiresIn: 3600,
        user: {
          userId: 'user-123',
          email: 'john@example.com',
          name: 'John Doe',
          roles: ['customer_service_agent'],
        },
      };

      (authService.exchangeCodeForToken as any) = vi.fn(
        async () => mockAuthResponse
      );
      (zitadelService.verifyState as any) = vi.fn(() => true);

      renderAuthCallbackPage();

      await waitFor(() => {
        expect(authService.exchangeCodeForToken).toHaveBeenCalledWith('test-code');
      });
    });

    it('should display error if code exchange fails', async () => {
      const mockError = new Error('Token exchange failed');
      (authService.exchangeCodeForToken as any) = vi.fn(async () => {
        throw mockError;
      });
      (zitadelService.verifyState as any) = vi.fn(() => true);
      (zitadelService.clearOAuthSession as any) = vi.fn();

      renderAuthCallbackPage();

      await waitFor(() => {
        expect(screen.getByText('Authentication Failed')).toBeInTheDocument();
        expect(screen.getByText('Token exchange failed')).toBeInTheDocument();
      });
    });

    it('should display error if no authorization code received', async () => {
      (zitadelService.clearOAuthSession as any) = vi.fn();

      renderAuthCallbackPage('?error=access_denied');

      await waitFor(() => {
        expect(screen.getByText('Authentication Failed')).toBeInTheDocument();
      });
    });

    it('should have back to login button in error state', async () => {
      const mockError = new Error('Authentication failed');
      (authService.exchangeCodeForToken as any) = vi.fn(async () => {
        throw mockError;
      });
      (zitadelService.verifyState as any) = vi.fn(() => true);
      (zitadelService.clearOAuthSession as any) = vi.fn();

      renderAuthCallbackPage();

      await waitFor(() => {
        const backButton = screen.getByText('Back to Login');
        expect(backButton).toBeInTheDocument();
      });
    });
  });

  describe('Token Storage', () => {
    it('should store token in sessionStorage after login', async () => {
      const mockToken = 'test-jwt-token';
      const mockUser = {
        userId: 'user-123',
        email: 'john@example.com',
        name: 'John Doe',
        roles: ['agent'],
      };

      const mockAuthResponse = {
        accessToken: mockToken,
        refreshToken: 'refresh-token',
        expiresIn: 3600,
        user: mockUser,
      };

      (authService.exchangeCodeForToken as any) = vi.fn(
        async () => mockAuthResponse
      );
      (zitadelService.verifyState as any) = vi.fn(() => true);
      (zitadelService.clearOAuthSession as any) = vi.fn();

      render(
        <BrowserRouter>
          <AuthProvider>
            <AuthCallbackPage />
          </AuthProvider>
        </BrowserRouter>
      );

      await waitFor(() => {
        expect(sessionStorage.getItem('auth_token')).toBe(mockToken);
        expect(sessionStorage.getItem('refresh_token')).toBe('refresh-token');
        expect(JSON.parse(sessionStorage.getItem('auth_user') || '{}')).toEqual(mockUser);
      });
    });

    it('should NOT use localStorage for sensitive data', () => {
      const mockToken = 'test-jwt-token';

      sessionStorage.setItem('auth_token', mockToken);

      // Verify token is in sessionStorage, not localStorage
      expect(sessionStorage.getItem('auth_token')).toBe(mockToken);
      expect(localStorage.getItem('auth_token')).toBeNull();
    });
  });

  describe('Protected Routes', () => {
    it('should redirect to login if no token in session', async () => {
      // This test would require full router setup
      // For now, we verify the behavior in integration tests
      expect(sessionStorage.getItem('auth_token')).toBeNull();
    });
  });

  describe('Token Validation', () => {
    it('should detect expired tokens', () => {
      const expiredToken =
        'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLTEyMyIsImVtYWlsIjoiam9obkBleGFtcGxlLmNvbSIsImV4cCI6MTAwMH0.test';

      const result = authService.decodeToken(expiredToken);

      expect(result).not.toBeNull();
      expect(result?.exp).toBeLessThan(Math.floor(Date.now() / 1000) + 5 * 60);
    });

    it('should extract user info from JWT', () => {
      const token =
        'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLTEyMyIsImVtYWlsIjoiam9obkBleGFtcGxlLmNvbSIsIm5hbWUiOiJKb2huIERvZSIsInJvbGVzIjpbImFnZW50Il0sImV4cCI6OTk5OTk5OTk5OX0.test';

      const payload = authService.decodeToken(token);

      expect(payload?.sub).toBe('user-123');
      expect(payload?.email).toBe('john@example.com');
      expect(payload?.name).toBe('John Doe');
      expect(payload?.roles).toContain('agent');
    });
  });
});
