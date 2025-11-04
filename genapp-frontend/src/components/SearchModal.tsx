/**
 * SearchModal Component
 * Modal search interface triggered by Ctrl+K keyboard shortcut
 */

import {
  Dialog,
  DialogTitle,
  TextField,
  Box,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
  Divider,
} from '@mui/material';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import PeopleIcon from '@mui/icons-material/People';
import DescriptionIcon from '@mui/icons-material/Description';
import SearchIcon from '@mui/icons-material/Search';

export interface SearchResult {
  id: string;
  title: string;
  description: string;
  icon: React.ElementType;
  action: () => void;
}

interface SearchModalProps {
  open: boolean;
  onClose: () => void;
}

export const SearchModal: React.FC<SearchModalProps> = ({ open, onClose }) => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedIndex, setSelectedIndex] = useState(0);

  // Mock search results
  const getSearchResults = (query: string): SearchResult[] => {
    if (!query.trim()) {
      return [
        {
          id: 'customers',
          title: 'Search Customers',
          description: 'Find and manage customer records',
          icon: PeopleIcon,
          action: () => {
            navigate('/customers/search');
            onClose();
          },
        },
        {
          id: 'policies',
          title: 'Search Policies',
          description: 'Find and manage policy records',
          icon: DescriptionIcon,
          action: () => {
            navigate('/policies/search');
            onClose();
          },
        },
      ];
    }

    // Mock results based on query
    return [
      {
        id: 'customer-search',
        title: `Search customers for "${query}"`,
        description: 'Find customers matching your search',
        icon: PeopleIcon,
        action: () => {
          navigate(`/customers/search?query=${encodeURIComponent(query)}`);
          onClose();
        },
      },
      {
        id: 'policy-search',
        title: `Search policies for "${query}"`,
        description: 'Find policies matching your search',
        icon: DescriptionIcon,
        action: () => {
          navigate(`/policies/search?query=${encodeURIComponent(query)}`);
          onClose();
        },
      },
    ];
  };

  const results = getSearchResults(searchQuery);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    switch (e.key) {
      case 'ArrowUp':
        e.preventDefault();
        setSelectedIndex(Math.max(0, selectedIndex - 1));
        break;
      case 'ArrowDown':
        e.preventDefault();
        setSelectedIndex(Math.min(results.length - 1, selectedIndex + 1));
        break;
      case 'Enter':
        e.preventDefault();
        if (results[selectedIndex]) {
          results[selectedIndex].action();
        }
        break;
      case 'Escape':
        e.preventDefault();
        onClose();
        break;
      default:
        break;
    }
  };

  useEffect(() => {
    setSelectedIndex(0);
  }, [searchQuery]);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ pb: 0 }}>
        <TextField
          fullWidth
          autoFocus
          placeholder="Search customers, policies..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          onKeyDown={handleKeyDown}
          variant="outlined"
          size="small"
          InputProps={{
            startAdornment: <SearchIcon sx={{ mr: 1, color: 'text.secondary' }} />,
          }}
        />
      </DialogTitle>

      <Divider />

      {results.length > 0 ? (
        <List sx={{ py: 1 }}>
          {results.map((result, index) => {
            const Icon = result.icon;
            return (
              <ListItem key={result.id} disablePadding>
                <ListItemButton
                  selected={index === selectedIndex}
                  onClick={() => result.action()}
                  onMouseEnter={() => setSelectedIndex(index)}
                  sx={{
                    '&.Mui-selected': {
                      bgcolor: 'action.selected',
                    },
                  }}
                >
                  <ListItemIcon>
                    <Icon />
                  </ListItemIcon>
                  <ListItemText
                    primary={result.title}
                    secondary={result.description}
                    primaryTypographyProps={{ variant: 'body2', fontWeight: 'bold' }}
                    secondaryTypographyProps={{ variant: 'caption' }}
                  />
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>
      ) : (
        <Box sx={{ p: 3, textAlign: 'center' }}>
          <Typography variant="body2" sx={{ color: 'text.secondary' }}>
            No results found for "{searchQuery}"
          </Typography>
        </Box>
      )}

      <Divider />

      <Box sx={{ p: 1, bgcolor: 'action.hover' }}>
        <Typography variant="caption" sx={{ color: 'text.secondary', px: 2 }}>
          Press <strong>↑↓</strong> to navigate, <strong>Enter</strong> to select, <strong>Esc</strong> to close
        </Typography>
      </Box>
    </Dialog>
  );
};

export default SearchModal;
