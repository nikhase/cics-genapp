import { createTheme } from '@mui/material/styles';

// Clarity Enterprise Design System color palette
const clarityPalette = {
  primary: '#0050D8',
  secondary: '#6A7781',
  success: '#2D8F3E',
  warning: '#E6A600',
  error: '#D31C1C',
  neutral: {
    50: '#F5F5F5',
    100: '#EBEBEB',
    200: '#D9DCDE',
    300: '#9A9C9F',
    400: '#6A7781',
    500: '#4A4D50',
    600: '#313438',
    700: '#1D3D5C',
    800: '#1D1F21',
    900: '#000000',
  },
};

export const theme = createTheme({
  palette: {
    primary: {
      main: clarityPalette.primary,
      light: '#5284FF',
      dark: '#003DB3',
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: clarityPalette.secondary,
      light: '#8A9199',
      dark: '#505D67',
      contrastText: '#FFFFFF',
    },
    success: {
      main: clarityPalette.success,
      light: '#4FAE5C',
      dark: '#1F662E',
      contrastText: '#FFFFFF',
    },
    warning: {
      main: clarityPalette.warning,
      light: '#FFB800',
      dark: '#B38300',
      contrastText: '#000000',
    },
    error: {
      main: clarityPalette.error,
      light: '#F44F4F',
      dark: '#A01717',
      contrastText: '#FFFFFF',
    },
    background: {
      default: '#FFFFFF',
      paper: '#F5F5F5',
    },
    text: {
      primary: clarityPalette.neutral[900],
      secondary: clarityPalette.neutral[400],
      disabled: clarityPalette.neutral[300],
    },
    divider: clarityPalette.neutral[200],
  },
  typography: {
    fontFamily: '"Segoe UI", Tahoma, Geneva, Verdana, sans-serif',
    h1: {
      fontSize: '32px',
      fontWeight: 600,
      lineHeight: 1.3,
      letterSpacing: '-0.5px',
    },
    h2: {
      fontSize: '24px',
      fontWeight: 600,
      lineHeight: 1.3,
      letterSpacing: '-0.25px',
    },
    h3: {
      fontSize: '18px',
      fontWeight: 600,
      lineHeight: 1.4,
      letterSpacing: 0,
    },
    h4: {
      fontSize: '16px',
      fontWeight: 600,
      lineHeight: 1.4,
      letterSpacing: 0,
    },
    h5: {
      fontSize: '14px',
      fontWeight: 600,
      lineHeight: 1.5,
      letterSpacing: 0,
    },
    h6: {
      fontSize: '12px',
      fontWeight: 600,
      lineHeight: 1.5,
      letterSpacing: 0.5,
    },
    body1: {
      fontSize: '14px',
      fontWeight: 400,
      lineHeight: 1.5,
      letterSpacing: 0.25,
    },
    body2: {
      fontSize: '12px',
      fontWeight: 400,
      lineHeight: 1.6,
      letterSpacing: 0.4,
    },
    button: {
      fontSize: '14px',
      fontWeight: 600,
      lineHeight: 1.5,
      textTransform: 'none',
      letterSpacing: 0.5,
    },
    caption: {
      fontSize: '12px',
      fontWeight: 400,
      lineHeight: 1.6,
      letterSpacing: 0.4,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: '3px',
          padding: '8px 16px',
          fontWeight: 600,
          fontSize: '14px',
        },
        contained: {
          backgroundColor: clarityPalette.primary,
          color: '#FFFFFF',
          '&:hover': {
            backgroundColor: '#003DB3',
          },
          '&:active': {
            backgroundColor: '#002885',
          },
          '&:disabled': {
            backgroundColor: clarityPalette.neutral[200],
            color: clarityPalette.neutral[300],
          },
        },
        outlined: {
          borderColor: clarityPalette.primary,
          color: clarityPalette.primary,
          '&:hover': {
            backgroundColor: '#F0F6FF',
          },
          '&:disabled': {
            borderColor: clarityPalette.neutral[200],
            color: clarityPalette.neutral[300],
          },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: clarityPalette.neutral[200],
            },
            '&:hover fieldset': {
              borderColor: clarityPalette.neutral[400],
            },
            '&.Mui-focused fieldset': {
              borderColor: clarityPalette.primary,
              borderWidth: '2px',
            },
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: '3px',
          border: `1px solid ${clarityPalette.neutral[200]}`,
          boxShadow: '0 2px 4px rgba(0, 0, 0, 0.08)',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: '3px',
        },
      },
    },
  },
});
