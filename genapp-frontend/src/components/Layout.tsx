/**
 * Root Layout Component
 * Provides main structure: header, sidebar, main content area
 */

import { Outlet } from 'react-router-dom';
import { Box } from '@mui/material';

export const Layout: React.FC = () => {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      {/* Header will be added in Story 3.3 */}
      <Box component="header" sx={{ bgcolor: 'primary.main', color: 'white', p: 2 }}>
        Header Placeholder
      </Box>

      {/* Main content area */}
      <Box sx={{ display: 'flex', flex: 1 }}>
        {/* Sidebar will be added in Story 3.3 */}
        <Box component="aside" sx={{ display: 'none', width: 250, bgcolor: 'background.paper' }}>
          Sidebar Placeholder
        </Box>

        {/* Main content */}
        <Box component="main" sx={{ flex: 1, overflow: 'auto' }}>
          <Outlet />
        </Box>
      </Box>

      {/* Footer will be added later */}
      <Box component="footer" sx={{ bgcolor: 'grey.100', p: 2, textAlign: 'center' }}>
        © 2024 CICS GenApp. All rights reserved.
      </Box>
    </Box>
  );
};

export default Layout;
