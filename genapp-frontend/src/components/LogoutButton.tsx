/**
 * Logout Button Component
 * Button to log out user and clear session
 * Story 3.2: Login Page and Zitadel OIDC Authentication
 */

import React from 'react';
import { Button, CircularProgress } from '@mui/material';
import { useAuth } from '../hooks/useAuth';

export const LogoutButton: React.FC = () => {
  const { logout, isLoading } = useAuth();

  const handleLogout = async () => {
    await logout();
  };

  return (
    <Button
      onClick={handleLogout}
      disabled={isLoading}
      color="inherit"
      sx={{ textTransform: 'none' }}
    >
      {isLoading ? <CircularProgress size={20} /> : '🚪 Logout'}
    </Button>
  );
};

export default LogoutButton;
