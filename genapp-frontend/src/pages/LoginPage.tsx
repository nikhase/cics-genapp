/**
 * Login Page Component
 * Displays Zitadel OIDC login form with corporate SSO button
 * Story 3.2: Login Page and Zitadel OIDC Authentication
 */

import React, { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Button,
  Typography,
  Alert,
  Paper,
  CircularProgress,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import { initiateLogin } from '../services/zitadelService';
import { useAuth } from '../hooks/useAuth';

export const LoginPage: React.FC = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const navigate = useNavigate();
  const { isAuthenticated, error, isLoading } = useAuth();
  const [localError, setLocalError] = React.useState<string | null>(null);

  // If already authenticated, redirect to dashboard
  React.useEffect(() => {
    if (isAuthenticated) {
      navigate('/dashboard', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  /**
   * Handle login button click
   */
  const handleLoginClick = useCallback(async () => {
    try {
      setLocalError(null);
      initiateLogin();
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to initiate login';
      setLocalError(errorMessage);
      console.error('Login initiation error:', err);
    }
  }, []);

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
          gap: 3,
          py: 4,
        }}
      >
        {/* Logo and Header */}
        <Box sx={{ textAlign: 'center' }}>
          <Typography
            variant={isMobile ? 'h4' : 'h2'}
            sx={{
              fontWeight: 600,
              color: theme.palette.primary.main,
              mb: 1,
            }}
          >
            CICS GenApp
          </Typography>
          <Typography variant="subtitle1" color="textSecondary">
            Secure Enterprise System
          </Typography>
        </Box>

        {/* Login Card */}
        <Paper
          elevation={3}
          sx={{
            width: '100%',
            p: isMobile ? 3 : 4,
            borderRadius: 2,
          }}
        >
          <Box
            sx={{
              display: 'flex',
              flexDirection: 'column',
              gap: 3,
            }}
          >
            {/* Title */}
            <Box>
              <Typography variant="h5" sx={{ fontWeight: 600, mb: 1 }}>
                Sign In
              </Typography>
              <Typography variant="body2" color="textSecondary">
                Use your corporate credentials to access the system securely
              </Typography>
            </Box>

            {/* Error Messages */}
            {(error || localError) && (
              <Alert
                severity="error"
                onClose={() => {
                  setLocalError(null);
                }}
                sx={{ mb: 2 }}
              >
                {error || localError}
              </Alert>
            )}

            {/* Zitadel OIDC Button */}
            <Button
              variant="contained"
              size="large"
              onClick={handleLoginClick}
              disabled={isLoading}
              fullWidth
              sx={{
                py: 1.5,
                fontSize: '1.1rem',
                fontWeight: 600,
                textTransform: 'none',
                position: 'relative',
              }}
            >
              {isLoading ? (
                <>
                  <CircularProgress size={20} sx={{ mr: 1 }} />
                  Signing In...
                </>
              ) : (
                '🔐 Sign in with Corporate SSO'
              )}
            </Button>

            {/* Info Text */}
            <Box sx={{ mt: 2, p: 2, backgroundColor: theme.palette.info.light, borderRadius: 1 }}>
              <Typography variant="caption" sx={{ color: theme.palette.info.dark }}>
                You will be redirected to Zitadel for authentication. Sign in with your corporate
                credentials. No passwords are stored in this application.
              </Typography>
            </Box>
          </Box>
        </Paper>

        {/* Footer */}
        <Typography variant="caption" color="textSecondary" sx={{ mt: 2 }}>
          Powered by Zitadel OIDC Authentication
        </Typography>
      </Box>
    </Container>
  );
};

export default LoginPage;
