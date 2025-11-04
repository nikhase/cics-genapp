/**
 * Authentication Service
 * Handles OAuth2 code exchange, token management, and Zitadel integration
 */

import config from '../config/config';

export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  expiresIn: number;
  user: {
    userId: string;
    email: string;
    name: string;
    roles: string[];
  };
}

export interface TokenPayload {
  sub: string;
  email: string;
  name: string;
  roles?: string[];
  iat: number;
  exp: number;
}

/**
 * Decode JWT token and extract payload
 */
export const decodeToken = (token: string): TokenPayload | null => {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) {
      console.error('Invalid token format');
      return null;
    }

    // Decode the payload (second part)
    const payload = JSON.parse(atob(parts[1]));
    return payload as TokenPayload;
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
};

/**
 * Check if token is expired
 */
export const isTokenExpired = (token: string): boolean => {
  const payload = decodeToken(token);
  if (!payload) return true;

  const currentTime = Math.floor(Date.now() / 1000);
  const bufferTime = 5 * 60; // 5 minutes buffer
  return payload.exp < currentTime + bufferTime;
};

/**
 * Get time until token expiration in milliseconds
 */
export const getTokenExpirationTime = (token: string): number => {
  const payload = decodeToken(token);
  if (!payload) return 0;

  const expiresAt = payload.exp * 1000;
  const now = Date.now();
  return Math.max(0, expiresAt - now);
};

/**
 * Exchange authorization code for JWT token
 * Calls backend endpoint to handle token exchange
 */
export const exchangeCodeForToken = async (code: string): Promise<AuthResponse> => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/api/v1/auth/callback?code=${code}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Trace-Id': generateTraceId(),
      },
    });

    if (!response.ok) {
      const error = await response.json().catch(() => ({}));
      throw new Error(
        error.message || `Authentication failed: ${response.status} ${response.statusText}`
      );
    }

    const data = await response.json();

    // Validate response structure
    if (!data.data?.token) {
      throw new Error('Invalid token response from server');
    }

    const token = data.data.token;
    const payload = decodeToken(token);

    if (!payload) {
      throw new Error('Unable to decode token');
    }

    return {
      accessToken: token,
      refreshToken: data.data.refreshToken,
      expiresIn: data.data.expiresIn || payload.exp - Math.floor(Date.now() / 1000),
      user: {
        userId: payload.sub,
        email: payload.email || '',
        name: payload.name || '',
        roles: payload.roles || [],
      },
    };
  } catch (error) {
    console.error('Error exchanging code for token:', error);
    throw error;
  }
};

/**
 * Refresh access token using refresh token
 */
export const refreshAccessToken = async (refreshToken: string): Promise<AuthResponse> => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/api/v1/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Trace-Id': generateTraceId(),
      },
      body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
      throw new Error('Token refresh failed');
    }

    const data = await response.json();

    if (!data.data?.token) {
      throw new Error('Invalid token response from server');
    }

    const token = data.data.token;
    const payload = decodeToken(token);

    if (!payload) {
      throw new Error('Unable to decode token');
    }

    return {
      accessToken: token,
      refreshToken: data.data.refreshToken || refreshToken,
      expiresIn: data.data.expiresIn || payload.exp - Math.floor(Date.now() / 1000),
      user: {
        userId: payload.sub,
        email: payload.email || '',
        name: payload.name || '',
        roles: payload.roles || [],
      },
    };
  } catch (error) {
    console.error('Error refreshing token:', error);
    throw error;
  }
};

/**
 * Call backend logout endpoint
 */
export const logout = async (token: string): Promise<void> => {
  try {
    await fetch(`${config.apiBaseUrl}/api/v1/auth/logout`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`,
        'X-Trace-Id': generateTraceId(),
      },
    });
  } catch (error) {
    console.warn('Error calling logout endpoint:', error);
    // Don't throw - we want to clear client-side session even if backend fails
  }
};

/**
 * Generate a trace ID for request correlation
 */
const generateTraceId = (): string => {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
};

export default {
  decodeToken,
  isTokenExpired,
  getTokenExpirationTime,
  exchangeCodeForToken,
  refreshAccessToken,
  logout,
};
