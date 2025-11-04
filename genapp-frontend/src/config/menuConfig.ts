/**
 * Menu Configuration
 * Defines navigation menu items with role-based visibility
 */

import {
  Home as HomeIcon,
  People as PeopleIcon,
  Description as DescriptionIcon,
  Schedule as ScheduleIcon,
  Analytics as AnalyticsIcon,
  Settings as SettingsIcon,
} from '@mui/icons-material';

export interface MenuItem {
  label: string;
  icon: React.ElementType;
  path: string;
  roles?: string[]; // empty array means visible to all users
}

export const menuItems: MenuItem[] = [
  {
    label: 'Dashboard',
    icon: HomeIcon,
    path: '/dashboard',
    roles: [], // visible to all
  },
  {
    label: 'Customers',
    icon: PeopleIcon,
    path: '/customers/search',
    roles: [], // visible to all
  },
  {
    label: 'Policies',
    icon: DescriptionIcon,
    path: '/policies/search',
    roles: [], // visible to all
  },
  {
    label: 'Audit Log',
    icon: ScheduleIcon,
    path: '/audit',
    roles: ['compliance_officer', 'admin'],
  },
  {
    label: 'Reports',
    icon: AnalyticsIcon,
    path: '/reports',
    roles: ['admin'],
  },
  {
    label: 'Admin',
    icon: SettingsIcon,
    path: '/admin',
    roles: ['admin'],
  },
];

/**
 * Filter menu items based on user roles
 * @param userRoles - Array of user roles
 * @returns Filtered menu items visible to the user
 */
export const filterMenuItemsByRole = (userRoles: string[] = []): MenuItem[] => {
  return menuItems.filter((item) => {
    if (item.roles === undefined || item.roles.length === 0) {
      return true; // visible to all
    }
    return userRoles.some((role) => item.roles?.includes(role));
  });
};

export default menuItems;
