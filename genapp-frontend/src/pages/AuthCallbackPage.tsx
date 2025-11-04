/**
 * Auth Callback Page Component
 * Handles OAuth2 callback after Zitadel authentication
 * Exchanges authorization code for JWT token
 * Story 3.2: Login Page and Zitadel OIDC Authentication
 */

import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Container, Box, Typography, Alert, CircularProgress } from '@mui/material';
import { useAuth } from '../hooks/useAuth';
import { getAuthorizationCode, verifyState, clearOAuthSession } from '../services/zitadelService';

export const AuthCallbackPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { login, error, clearError } = useAuth();
  const [localError, setLocalError] = React.useState<string | null>(null);

  useEffect(() => {
    const handleCallback = async () => {
      try {
        // Get code and state from URL
        const code = getAuthorizationCode();
        const stateFromUrl = searchParams.get('state');
        const errorFromUrl = searchParams.get('error');
        const errorDescription = searchParams.get('error_description');

        // Check for authorization errors
        if (errorFromUrl) {
          throw new Error(`Authorization error: ${errorFromUrl} - ${errorDescription || 'Unknown error'}`);
        }

        // Validate code was returned
        if (!code) {
          throw new Error('No authorization code received from Zitadel');
        }

        // Verify state parameter for CSRF protection
        if (stateFromUrl && !verifyState(stateFromUrl)) {
          throw new Error('Invalid state parameter - possible CSRF attack');
        }

        // Exchange code for token
        await login(code);
        // Navigation happens in AuthContext
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Authentication failed';
        setLocalError(errorMessage);
        console.error('Auth callback error:', err);
        clearOAuthSession();
      }
    };

    handleCallback();
  }, [login, searchParams]);

  // Display error if occurred
  if (error || localError) {
    return (
      <Container maxWidth="sm">
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            minHeight: '100vh',
            gap: 2,
          }}
        >
          <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>
            Authentication Failed
          </Typography>

          <Alert
            severity="error"
            sx={{ width: '100%', mb: 2 }}
            onClose={() => {
              setLocalError(null);
              clearError();
            }}
          >
            {error || localError}
          </Alert>

          <Box sx={{ textAlign: 'center', mt: 2 }}>
            <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
              Failed to complete authentication. Please try again.
            </Typography>
            <button
              onClick={() => navigate('/login')}
              style={{
                padding: '8px 16px',
                backgroundColor: '#1976d2',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '1rem',
              }}
            >
              Back to Login
            </button>
          </Box>
        </Box>
      </Container>
    );
  }

  // Show loading state while exchanging code
  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
          gap: 2,
        }}
      >
        <CircularProgress size={50} />
        <Typography variant="h6" sx={{ mt: 2 }}>
          Completing Authentication...
        </Typography>
        <Typography variant="body2" color="textSecondary">
          Please wait while we verify your credentials
        </Typography>
      </Box>
    </Container>
  );
};

export default AuthCallbackPage;
