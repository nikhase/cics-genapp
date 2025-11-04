/**
 * Authentication Context
 * Provides authentication state and methods to all components
 */

import React, { createContext, useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import config from '../config/config';
import * as authService from '../services/authService';

export interface User {
  userId: string;
  email: string;
  name: string;
  roles: string[];
}

export interface AuthContextType {
  user: User | null;
  token: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (code: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshAccessToken: () => Promise<void>;
  clearError: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Create a wrapper component that can safely use useNavigate
const AuthProviderInner: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [refreshToken, setRefreshToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [tokenRefreshInterval, setTokenRefreshInterval] = useState<NodeJS.Timeout | null>(null);

  // Initialize auth from session storage on mount
  useEffect(() => {
    const storedToken = sessionStorage.getItem('auth_token');
    const storedRefreshToken = sessionStorage.getItem('refresh_token');
    const storedUser = sessionStorage.getItem('auth_user');

    // Development mode: use mock authentication if no stored session
    if (!storedToken && config.isDevelopment) {
      const mockToken =
        'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLWRldiIsImVtYWlsIjoiZGV2QGdlbmFwcC5sb2NhbCIsIm5hbWUiOiJEZXZlbG9wZXIgVXNlciIsInJvbGVzIjpbImFkbWluIiwiY3VzdG9tZXJfc2VydmljZV9hZ2VudCIsImNvbXBsaWFuY2Vfb2ZmaWNlciJdLCJleHAiOjk5OTk5OTk5OTl9.dev-mock-token';
      const mockUser: User = {
        userId: 'user-dev',
        email: 'dev@genapp.local',
        name: 'Developer User',
        roles: ['admin', 'customer_service_agent', 'compliance_officer'],
      };

      console.info(
        '%c[DEV MODE] Using mock authentication',
        'background: #ff9500; color: white; padding: 4px 8px; border-radius: 3px; font-weight: bold;'
      );

      sessionStorage.setItem('auth_token', mockToken);
      sessionStorage.setItem('auth_user', JSON.stringify(mockUser));
      setToken(mockToken);
      setUser(mockUser);
      setupTokenRefreshInterval(mockToken, null);
      return;
    }

    if (storedToken && storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser);
        setToken(storedToken);
        setRefreshToken(storedRefreshToken);
        setUser(parsedUser);

        // Check if token is expired
        if (authService.isTokenExpired(storedToken)) {
          if (storedRefreshToken) {
            // Try to refresh the token
            handleRefreshToken(storedRefreshToken);
          } else {
            // Token is expired and no refresh token, clear session
            handleLogout();
          }
        } else {
          // Token is valid, set up refresh interval
          setupTokenRefreshInterval(storedToken, storedRefreshToken);
        }
      } catch (err) {
        console.error('Error restoring auth session:', err);
        handleLogout();
      }
    }
  }, []);

  /**
   * Setup automatic token refresh interval
   */
  const setupTokenRefreshInterval = useCallback(
    (currentToken: string, currentRefreshToken: string | null) => {
      // Clear existing interval if any
      if (tokenRefreshInterval) {
        clearInterval(tokenRefreshInterval);
      }

      // Calculate time until token expiration
      const timeUntilExpiration = authService.getTokenExpirationTime(currentToken);

      // Refresh token 5 minutes before expiration
      const refreshTime = Math.max(timeUntilExpiration - 5 * 60 * 1000, 1000);

      const interval = setTimeout(() => {
        if (currentRefreshToken) {
          handleRefreshToken(currentRefreshToken);
        } else {
          handleLogout();
        }
      }, refreshTime);

      setTokenRefreshInterval(interval as unknown as NodeJS.Timeout);
    },
    [tokenRefreshInterval]
  );

  /**
   * Handle token refresh
   */
  const handleRefreshToken = useCallback(
    async (refreshTokenValue: string) => {
      try {
        const response = await authService.refreshAccessToken(refreshTokenValue);

        // Update state
        setToken(response.accessToken);
        setRefreshToken(response.refreshToken || refreshTokenValue);
        setUser(response.user);

        // Update session storage
        sessionStorage.setItem('auth_token', response.accessToken);
        if (response.refreshToken) {
          sessionStorage.setItem('refresh_token', response.refreshToken);
        }
        sessionStorage.setItem('auth_user', JSON.stringify(response.user));

        // Set up next refresh
        setupTokenRefreshInterval(response.accessToken, response.refreshToken || refreshTokenValue);
      } catch (err) {
        console.error('Token refresh failed:', err);
        handleLogout();
      }
    },
    [setupTokenRefreshInterval]
  );

  /**
   * Handle login with authorization code
   */
  const handleLogin = useCallback(
    async (code: string) => {
      setIsLoading(true);
      setError(null);

      try {
        const response = await authService.exchangeCodeForToken(code);

        // Update state
        setToken(response.accessToken);
        setRefreshToken(response.refreshToken || null);
        setUser(response.user);

        // Store in session storage
        sessionStorage.setItem('auth_token', response.accessToken);
        if (response.refreshToken) {
          sessionStorage.setItem('refresh_token', response.refreshToken);
        }
        sessionStorage.setItem('auth_user', JSON.stringify(response.user));

        // Set up token refresh
        setupTokenRefreshInterval(response.accessToken, response.refreshToken || null);

        // Navigate to dashboard
        navigate('/dashboard');
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Authentication failed';
        setError(errorMessage);
        console.error('Login error:', err);
      } finally {
        setIsLoading(false);
      }
    },
    [navigate, setupTokenRefreshInterval]
  );

  /**
   * Handle logout
   */
  const handleLogout = useCallback(async () => {
    setIsLoading(true);

    try {
      // Call backend logout if token exists
      if (token) {
        try {
          await authService.logout(token);
        } catch (err) {
          console.warn('Backend logout failed, continuing with client-side logout:', err);
        }
      }
    } finally {
      // Clear state
      setToken(null);
      setRefreshToken(null);
      setUser(null);
      setError(null);

      // Clear session storage
      sessionStorage.removeItem('auth_token');
      sessionStorage.removeItem('refresh_token');
      sessionStorage.removeItem('auth_user');

      // Clear token refresh interval
      if (tokenRefreshInterval) {
        clearInterval(tokenRefreshInterval);
        setTokenRefreshInterval(null);
      }

      // Navigate to login
      navigate('/login');
      setIsLoading(false);
    }
  }, [token, navigate, tokenRefreshInterval]);

  /**
   * Refresh token method exposed to consumers
   */
  const refreshAccessTokenMethod = useCallback(async () => {
    if (refreshToken) {
      await handleRefreshToken(refreshToken);
    }
  }, [refreshToken, handleRefreshToken]);

  /**
   * Clear error state
   */
  const clearError = useCallback(() => {
    setError(null);
  }, []);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      if (tokenRefreshInterval) {
        clearInterval(tokenRefreshInterval);
      }
    };
  }, [tokenRefreshInterval]);

  const value: AuthContextType = {
    user,
    token,
    refreshToken,
    isAuthenticated: !!user && !!token,
    isLoading,
    error,
    login: handleLogin,
    logout: handleLogout,
    refreshAccessToken: refreshAccessTokenMethod,
    clearError,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

/**
 * AuthProvider Wrapper
 * This wrapper ensures the Router context is available before AuthProviderInner uses useNavigate
 */
export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return <AuthProviderInner>{children}</AuthProviderInner>;
};

export default AuthContext;
