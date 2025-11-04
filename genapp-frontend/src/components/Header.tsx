/**
 * Header Component
 * Top navigation bar with app title, user greeting, and profile menu
 */

import { AppBar, Toolbar, Box, Typography, Avatar, Menu, MenuItem, Button, IconButton, useMediaQuery, useTheme } from '@mui/material';
import { useAuth } from '../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { getTimeOfDayGreeting } from '../utils/greetings';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';

interface HeaderProps {
  onMenuToggle?: (open: boolean) => void;
}

export const Header: React.FC<HeaderProps> = ({ onMenuToggle }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const open = Boolean(anchorEl);

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = async () => {
    handleMenuClose();
    await logout();
  };

  const handleProfile = () => {
    handleMenuClose();
    navigate('/profile');
  };

  const handleSettings = () => {
    handleMenuClose();
    navigate('/settings');
  };

  const handleMobileMenuToggle = () => {
    setMobileMenuOpen(!mobileMenuOpen);
    onMenuToggle?.(!mobileMenuOpen);
  };

  const greeting = getTimeOfDayGreeting(new Date().getHours());

  return (
    <AppBar position="static" sx={{ boxShadow: 2 }}>
      <Toolbar sx={{ justifyContent: 'space-between', pr: { xs: 1, sm: 2 } }}>
        {/* Left side: Logo and title with mobile menu button */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: { xs: 1, sm: 2 } }}>
          {/* Mobile hamburger menu */}
          {isMobile && (
            <IconButton
              edge="start"
              color="inherit"
              aria-label="menu"
              onClick={handleMobileMenuToggle}
              sx={{ mr: 1 }}
            >
              {mobileMenuOpen ? <CloseIcon /> : <MenuIcon />}
            </IconButton>
          )}

          <Typography variant="h6" sx={{ fontWeight: 'bold', fontSize: { xs: '1rem', sm: '1.25rem' } }}>
            CICS GenApp
          </Typography>
        </Box>

        {/* Right side: User greeting and profile menu */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: { xs: 0.5, sm: 2 } }}>
          <Box sx={{ display: { xs: 'none', sm: 'flex' }, alignItems: 'center', gap: 1 }}>
            <Typography variant="subtitle1" sx={{ fontSize: '0.95rem' }}>
              {greeting}, {user?.name || 'User'}
            </Typography>
          </Box>

          {/* User Avatar and Menu */}
          <Button
            onClick={handleMenuOpen}
            sx={{
              display: 'flex',
              alignItems: 'center',
              gap: { xs: 0, sm: 1 },
              color: 'white',
              textTransform: 'none',
              p: { xs: 0.5, sm: 1 },
              minWidth: 'auto',
              '&:hover': { bgcolor: 'rgba(255, 255, 255, 0.1)' },
            }}
          >
            <Avatar sx={{ width: { xs: 28, sm: 32 }, height: { xs: 28, sm: 32 }, bgcolor: 'secondary.main' }}>
              {user?.name
                ?.split(' ')
                .map((n) => n[0])
                .join('')
                .toUpperCase() || 'U'}
            </Avatar>
            <Typography variant="body2" sx={{ display: { xs: 'none', sm: 'inline' }, fontSize: '0.875rem' }}>
              {user?.name?.split(' ')[0] || 'User'}
            </Typography>
          </Button>

          {/* User Menu */}
          <Menu
            anchorEl={anchorEl}
            open={open}
            onClose={handleMenuClose}
            anchorOrigin={{
              vertical: 'bottom',
              horizontal: 'right',
            }}
            transformOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
          >
            <MenuItem disabled sx={{ fontSize: '0.875rem', color: 'text.secondary', maxWidth: 250 }}>
              {user?.email}
            </MenuItem>
            <MenuItem onClick={handleProfile}>Profile</MenuItem>
            <MenuItem onClick={handleSettings}>Settings</MenuItem>
            <MenuItem onClick={handleLogout} sx={{ color: 'error.main' }}>
              Logout
            </MenuItem>
          </Menu>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
