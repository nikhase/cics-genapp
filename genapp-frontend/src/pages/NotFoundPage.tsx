/**
 * Not Found Page Component
 * Handles 404 errors
 */

import { Container, Box, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

export const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();

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
        <Typography variant="h1" sx={{ fontSize: '64px', fontWeight: 700 }}>
          404
        </Typography>
        <Typography variant="h2">Page Not Found</Typography>
        <Typography variant="body1" color="textSecondary" sx={{ mb: 2 }}>
          The page you are looking for does not exist.
        </Typography>
        <Button variant="contained" onClick={() => navigate('/')}>
          Go to Dashboard
        </Button>
      </Box>
    </Container>
  );
};

export default NotFoundPage;
