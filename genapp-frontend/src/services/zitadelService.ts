/**
 * Zitadel OAuth2 Service
 * Handles Zitadel OIDC authorization flow
 */

import config from '../config/config';

/**
 * Generate a random state parameter for CSRF protection
 */
const generateState = (): string => {
  return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
};

/**
 * Generate PKCE code challenge (optional, for added security)
 * Note: Currently unused but kept for future PKCE implementation
 */
// const generateCodeChallenge = (): string => {
//   const array = new Uint8Array(32);
//   crypto.getRandomValues(array);
//   return btoa(String.fromCharCode.apply(null, Array.from(array)))
//     .replace(/\+/g, '-')
//     .replace(/\//g, '_')
//     .replace(/=/g, '');
// };

/**
 * Initiate login flow by redirecting to Zitadel authorization endpoint
 */
export const initiateLogin = (): void => {
  if (!config.zitadelClientId) {
    throw new Error('Zitadel client ID is not configured');
  }

  if (!config.zitadelAuthority) {
    throw new Error('Zitadel authority is not configured');
  }

  // Generate state for CSRF protection
  const state = generateState();
  sessionStorage.setItem('oauth_state', state);

  // Build authorization URL
  const authParams = new URLSearchParams({
    client_id: config.zitadelClientId,
    redirect_uri: `${window.location.origin}/auth/callback`,
    response_type: 'code',
    scope: 'openid profile email',
    state,
  });

  // Optional: Add PKCE for additional security
  // In production, uncomment the PKCE implementation
  // const codeChallenge = generateCodeChallenge();
  // sessionStorage.setItem('pkce_challenge', codeChallenge);
  // authParams.append('code_challenge', codeChallenge);
  // authParams.append('code_challenge_method', 'S256');

  const authorizationUrl = `${config.zitadelAuthority}/oauth/v2/authorize?${authParams.toString()}`;

  // Redirect to Zitadel
  window.location.href = authorizationUrl;
};

/**
 * Verify state parameter to prevent CSRF attacks
 */
export const verifyState = (stateFromUrl: string): boolean => {
  const storedState = sessionStorage.getItem('oauth_state');
  sessionStorage.removeItem('oauth_state'); // Clean up after verification

  if (!storedState || storedState !== stateFromUrl) {
    console.error('State parameter mismatch - possible CSRF attack');
    return false;
  }

  return true;
};

/**
 * Get authorization code from URL query parameter
 */
export const getAuthorizationCode = (): string | null => {
  const params = new URLSearchParams(window.location.search);
  const code = params.get('code');
  const error = params.get('error');
  const errorDescription = params.get('error_description');

  if (error) {
    console.error(`Authorization error: ${error}`, errorDescription);
    return null;
  }

  return code;
};

/**
 * Clear OAuth session data
 */
export const clearOAuthSession = (): void => {
  sessionStorage.removeItem('oauth_state');
  sessionStorage.removeItem('pkce_challenge');
};

export default {
  initiateLogin,
  verifyState,
  getAuthorizationCode,
  clearOAuthSession,
};
