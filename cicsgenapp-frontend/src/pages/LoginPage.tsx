/**
 * Login Page Component
 * Placeholder for Story 3.2 - Login page and Zitadel OIDC authentication
 */

import { Container, Box, Typography } from '@mui/material';

export const LoginPage: React.FC = () => {
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
        <Typography variant="h1">CICS GenApp</Typography>
        <Typography variant="body1">Login Page (Story 3.2)</Typography>
        <Typography variant="caption">Coming Soon: Zitadel OIDC Authentication</Typography>
      </Box>
    </Container>
  );
};

export default LoginPage;
