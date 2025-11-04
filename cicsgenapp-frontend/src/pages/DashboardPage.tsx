/**
 * Dashboard Page Component
 * Placeholder for Story 3.3 - Dashboard page with navigation and quick actions
 */

import { Container, Box, Typography } from '@mui/material';

export const DashboardPage: React.FC = () => {
  return (
    <Container maxWidth="lg">
      <Box sx={{ py: 4 }}>
        <Typography variant="h1">Dashboard</Typography>
        <Typography variant="body1" sx={{ mt: 2 }}>
          Dashboard Page (Story 3.3)
        </Typography>
        <Typography variant="caption">Coming Soon: Navigation and Quick Actions</Typography>
      </Box>
    </Container>
  );
};

export default DashboardPage;
