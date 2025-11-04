/**
 * Dashboard Page Component
 * Dashboard with quick actions, metrics, and navigation
 */

import {
  Container,
  Box,
  Typography,
  Button,
  TextField,
  Card,
  CardContent,
  Divider,
  Paper,
  CircularProgress,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import AddIcon from '@mui/icons-material/Add';
import SearchIcon from '@mui/icons-material/Search';

export const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [customerSearch, setCustomerSearch] = useState('');
  const [policySearch, setPolicySearch] = useState('');
  const [metricsLoading, setMetricsLoading] = useState(true);

  interface Activity {
    id: number;
    action: string;
    user: string;
    timestamp: string;
  }

  interface Metrics {
    customerCount: number;
    policyCount: number;
    recentActivity: Activity[];
  }

  const [metrics, setMetrics] = useState<Metrics>({
    customerCount: 0,
    policyCount: 0,
    recentActivity: [],
  });

  // Simulate loading metrics
  useEffect(() => {
    const loadMetrics = async () => {
      setMetricsLoading(true);
      try {
        // TODO: Replace with actual API calls once backend endpoints exist
        // GET /api/v1/customers/count
        // GET /api/v1/policies/count
        // GET /api/v1/audit?limit=5

        // Mock data for now
        await new Promise((resolve) => setTimeout(resolve, 1000));
        setMetrics({
          customerCount: 42,
          policyCount: 128,
          recentActivity: [
            { id: 1, action: 'Customer added', user: 'John Smith', timestamp: '2 hours ago' },
            { id: 2, action: 'Policy updated', user: 'Jane Doe', timestamp: '4 hours ago' },
            { id: 3, action: 'Customer deleted', user: 'Admin', timestamp: '1 day ago' },
            { id: 4, action: 'Policy created', user: 'John Smith', timestamp: '2 days ago' },
            { id: 5, action: 'Audit log accessed', user: 'Compliance Officer', timestamp: '3 days ago' },
          ],
        });
      } catch (error) {
        console.error('Failed to load metrics:', error);
      } finally {
        setMetricsLoading(false);
      }
    };

    loadMetrics();
  }, []);

  const handleCustomerSearch = () => {
    if (customerSearch.trim()) {
      navigate(`/customers/search?query=${encodeURIComponent(customerSearch)}`);
    }
  };

  const handlePolicySearch = () => {
    if (policySearch.trim()) {
      navigate(`/policies/search?query=${encodeURIComponent(policySearch)}`);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent, callback: () => void) => {
    if (e.key === 'Enter') {
      callback();
    }
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ py: 4 }}>
        {/* Welcome Section */}
        <Box sx={{ mb: 4 }}>
          <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 1 }}>
            Dashboard
          </Typography>
          <Typography variant="body1" sx={{ color: 'text.secondary' }}>
            Welcome to CICS GenApp - Modernization Platform
          </Typography>
        </Box>

        <Divider sx={{ mb: 4 }} />

        {/* Quick Actions Grid */}
        <Box sx={{ mb: 4 }}>
          <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
            Quick Actions
          </Typography>

          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: '1fr 1fr 1fr 1fr' },
              gap: 2,
            }}
          >
            {/* New Customer Button */}
            <Button
              fullWidth
              variant="contained"
              color="primary"
              startIcon={<AddIcon />}
              onClick={() => navigate('/customers/create')}
              sx={{ py: 3, fontWeight: 'bold' }}
            >
              New Customer
            </Button>

            {/* New Policy Button */}
            <Button
              fullWidth
              variant="contained"
              color="secondary"
              startIcon={<AddIcon />}
              onClick={() => navigate('/policies/create')}
              sx={{ py: 3, fontWeight: 'bold' }}
            >
              New Policy
            </Button>

            {/* Search Customer */}
            <TextField
              fullWidth
              size="small"
              placeholder="Search customer"
              value={customerSearch}
              onChange={(e) => setCustomerSearch(e.target.value)}
              onKeyPress={(e) => handleKeyPress(e, handleCustomerSearch)}
              InputProps={{
                endAdornment: (
                  <Button
                    size="small"
                    onClick={handleCustomerSearch}
                    sx={{ p: 0.5 }}
                    color="primary"
                  >
                    <SearchIcon />
                  </Button>
                ),
              }}
            />

            {/* Search Policy */}
            <TextField
              fullWidth
              size="small"
              placeholder="Search policy"
              value={policySearch}
              onChange={(e) => setPolicySearch(e.target.value)}
              onKeyPress={(e) => handleKeyPress(e, handlePolicySearch)}
              InputProps={{
                endAdornment: (
                  <Button
                    size="small"
                    onClick={handlePolicySearch}
                    sx={{ p: 0.5 }}
                    color="primary"
                  >
                    <SearchIcon />
                  </Button>
                ),
              }}
            />
          </Box>
        </Box>

        <Divider sx={{ my: 4 }} />

        {/* Metrics Dashboard */}
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
            Overview
          </Typography>

          {metricsLoading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
              <CircularProgress />
            </Box>
          ) : (
            <Box
              sx={{
                display: 'grid',
                gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: '1fr 1fr 1fr' },
                gap: 3,
              }}
            >
              {/* Customer Count Card */}
              <Paper elevation={2} sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="h5" sx={{ fontWeight: 'bold', color: 'primary.main' }}>
                  {metrics.customerCount}
                </Typography>
                <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                  Total Customers
                </Typography>
              </Paper>

              {/* Policy Count Card */}
              <Paper elevation={2} sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="h5" sx={{ fontWeight: 'bold', color: 'secondary.main' }}>
                  {metrics.policyCount}
                </Typography>
                <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                  Total Policies
                </Typography>
              </Paper>

              {/* User Role Info Card */}
              <Paper elevation={2} sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="body2" sx={{ color: 'text.secondary', mb: 1 }}>
                  Your Roles
                </Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, justifyContent: 'center' }}>
                  {user?.roles && user.roles.length > 0 ? (
                    user.roles.map((role) => (
                      <Typography
                        key={role}
                        variant="caption"
                        sx={{
                          bgcolor: 'primary.light',
                          color: 'primary.dark',
                          px: 1.5,
                          py: 0.5,
                          borderRadius: 1,
                          fontWeight: 'bold',
                        }}
                      >
                        {role.replace(/_/g, ' ')}
                      </Typography>
                    ))
                  ) : (
                    <Typography variant="caption">No roles assigned</Typography>
                  )}
                </Box>
              </Paper>
            </Box>
          )}

          {/* Recent Activity */}
          <Box sx={{ mt: 4 }}>
            <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
              Recent Activity
            </Typography>

            <Card>
              <CardContent sx={{ p: 0 }}>
                {metrics.recentActivity.length > 0 ? (
                  metrics.recentActivity.map((activity, index) => (
                    <Box key={activity.id}>
                      <Box
                        sx={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center',
                          p: 2,
                          '&:hover': { bgcolor: 'action.hover' },
                        }}
                      >
                        <Box>
                          <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                            {activity.action}
                          </Typography>
                          <Typography variant="caption" sx={{ color: 'text.secondary' }}>
                            by {activity.user}
                          </Typography>
                        </Box>
                        <Typography variant="caption" sx={{ color: 'text.secondary' }}>
                          {activity.timestamp}
                        </Typography>
                      </Box>
                      {index < metrics.recentActivity.length - 1 && <Divider />}
                    </Box>
                  ))
                ) : (
                  <Box sx={{ p: 2, textAlign: 'center' }}>
                    <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                      No recent activity
                    </Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Box>
        </Box>
      </Box>
    </Container>
  );
};

export default DashboardPage;
