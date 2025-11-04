/**
 * Sidebar Component
 * Left navigation menu with role-based item visibility
 */

import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Box,
  useMediaQuery,
  useTheme,
  IconButton,
} from '@mui/material';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { filterMenuItemsByRole } from '../config/menuConfig';
import { useState } from 'react';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';

interface SidebarProps {
  onClose?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ onClose }) => {
  const { user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [mobileOpen, setMobileOpen] = useState(false);

  // Filter menu items based on user roles
  const visibleItems = filterMenuItemsByRole(user?.roles || []);

  const handleItemClick = (path: string) => {
    navigate(path);
    if (isMobile && onClose) {
      onClose();
      setMobileOpen(false);
    }
  };

  const isItemActive = (path: string): boolean => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };

  const drawerContent = (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <List sx={{ flex: 1 }}>
        {visibleItems.map((item) => {
          const Icon = item.icon;
          const isActive = isItemActive(item.path);

          return (
            <ListItem key={item.path} disablePadding>
              <ListItemButton
                onClick={() => handleItemClick(item.path)}
                selected={isActive}
                sx={{
                  '&.Mui-selected': {
                    bgcolor: 'primary.light',
                    color: 'primary.main',
                    '& .MuiListItemIcon-root': {
                      color: 'primary.main',
                    },
                    '&:hover': {
                      bgcolor: 'primary.light',
                    },
                  },
                }}
              >
                <ListItemIcon>
                  <Icon />
                </ListItemIcon>
                <ListItemText primary={item.label} />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
    </Box>
  );

  if (isMobile) {
    return (
      <>
        {/* Mobile hamburger button - shown in header */}
        <IconButton
          edge="start"
          color="inherit"
          aria-label="menu"
          onClick={() => setMobileOpen(true)}
          sx={{ mr: 2, display: { xs: 'flex', md: 'none' } }}
        >
          <MenuIcon />
        </IconButton>

        {/* Mobile drawer */}
        <Drawer
          anchor="left"
          open={mobileOpen}
          onClose={() => setMobileOpen(false)}
          sx={{ display: { xs: 'flex', md: 'none' } }}
        >
          <Box sx={{ width: 250, p: 2, display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
              <IconButton onClick={() => setMobileOpen(false)}>
                <CloseIcon />
              </IconButton>
            </Box>
            {drawerContent}
          </Box>
        </Drawer>
      </>
    );
  }

  // Desktop sidebar
  return (
    <Drawer
      variant="permanent"
      sx={{
        width: 250,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: 250,
          boxSizing: 'border-box',
          borderRight: 1,
          borderColor: 'divider',
        },
      }}
    >
      <Box sx={{ p: 2 }} />
      {drawerContent}
    </Drawer>
  );
};

export default Sidebar;
