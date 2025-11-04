/**
 * Root Layout Component
 * Provides main structure: header, sidebar, main content area
 */

import { Outlet } from 'react-router-dom';
import { Box, useTheme, useMediaQuery } from '@mui/material';
import { Header } from './Header';
import { Sidebar } from './Sidebar';
import { SearchModal } from './SearchModal';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import { useState } from 'react';

export const Layout: React.FC = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [searchModalOpen, setSearchModalOpen] = useState(false);

  // Setup keyboard shortcuts (Ctrl+K for search)
  useKeyboardShortcuts([
    {
      key: 'k',
      ctrlKey: true,
      callback: () => setSearchModalOpen(true),
    },
  ]);

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      {/* Header */}
      <Header />

      {/* Main content area */}
      <Box sx={{ display: 'flex', flex: 1 }}>
        {/* Sidebar - hidden on mobile, shown on desktop */}
        {!isMobile && <Sidebar />}

        {/* Main content */}
        <Box
          component="main"
          sx={{
            flex: 1,
            overflow: 'auto',
            display: 'flex',
            flexDirection: 'column',
          }}
        >
          <Outlet />
        </Box>
      </Box>

      {/* Search Modal */}
      <SearchModal open={searchModalOpen} onClose={() => setSearchModalOpen(false)} />

      {/* Footer */}
      <Box component="footer" sx={{ bgcolor: 'grey.100', p: 2, textAlign: 'center', fontSize: '0.875rem' }}>
        © 2024 CICS GenApp Modernization. All rights reserved.
      </Box>
    </Box>
  );
};

export default Layout;
