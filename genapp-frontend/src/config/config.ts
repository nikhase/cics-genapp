/**
 * Application Configuration
 * Loads environment variables and provides type-safe access to configuration
 */

interface Config {
  apiBaseUrl: string;
  zitadelClientId: string;
  zitadelAuthority: string;
  environment: 'development' | 'production' | 'test';
  isDevelopment: boolean;
  isProduction: boolean;
  logLevel: 'debug' | 'info' | 'warn' | 'error';
}

/**
 * Load and validate environment variables
 */
const loadConfig = (): Config => {
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
  const zitadelClientId = import.meta.env.VITE_ZITADEL_CLIENT_ID || '';
  const zitadelAuthority = import.meta.env.VITE_ZITADEL_AUTHORITY || '';
  const environment =
    (import.meta.env.MODE as 'development' | 'production' | 'test') || 'development';

  // Validate required environment variables in production
  if (environment === 'production') {
    if (!zitadelClientId) {
      console.warn('Warning: VITE_ZITADEL_CLIENT_ID is not set');
    }
    if (!zitadelAuthority) {
      console.warn('Warning: VITE_ZITADEL_AUTHORITY is not set');
    }
  }

  return {
    apiBaseUrl,
    zitadelClientId,
    zitadelAuthority,
    environment,
    isDevelopment: environment === 'development',
    isProduction: environment === 'production',
    logLevel: environment === 'development' ? 'debug' : 'info',
  };
};

export const config: Config = loadConfig();

/**
 * Log configuration (development only)
 */
if (config.isDevelopment) {
  console.info('Frontend Configuration Loaded:', {
    apiBaseUrl: config.apiBaseUrl,
    environment: config.environment,
    zitadelEnabled: !!config.zitadelClientId,
  });
}

export default config;
