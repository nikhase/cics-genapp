/**
 * TypeScript Type Definitions for CICS GenApp Frontend
 */

// ============================================================================
// API Response Wrapper
// ============================================================================

export interface ApiResponse<T> {
  data?: T;
  metadata?: {
    timestamp: string;
    correlationId?: string;
    status: number;
    message?: string;
    errors?: ValidationError[];
  };
  error?: {
    code: string;
    message: string;
    details?: Record<string, string>;
  };
}

export interface ValidationError {
  field: string;
  message: string;
  rejectedValue?: unknown;
}

// ============================================================================
// User & Authentication
// ============================================================================

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

export interface AuthToken {
  accessToken: string;
  refreshToken?: string;
  expiresIn: number;
  tokenType: string;
}

export interface AuthContextType {
  user: User | null;
  token: AuthToken | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
}

// ============================================================================
// Customer Domain
// ============================================================================

export type CustomerStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';

export interface Customer {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  status: CustomerStatus;
  createdAt: string;
  updatedAt: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface CreateCustomerRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
}

export interface UpdateCustomerRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  status?: CustomerStatus;
}

export interface CustomerSearchFilters {
  email?: string;
  firstName?: string;
  lastName?: string;
  phone?: string;
  status?: CustomerStatus;
  pageNumber?: number;
  pageSize?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

// ============================================================================
// Policy Domain
// ============================================================================

export type PolicyType = 'MOTOR' | 'ENDOWMENT' | 'HOUSE' | 'COMMERCIAL';
export type PolicyStatus = 'ACTIVE' | 'EXPIRED' | 'CANCELLED' | 'SUSPENDED';

export interface Policy {
  id: string;
  customerId: string;
  policyType: PolicyType;
  policyNumber: string;
  status: PolicyStatus;
  startDate: string;
  endDate: string;
  premiumAmount: number;
  createdAt: string;
  updatedAt: string;
  createdBy?: string;
  updatedBy?: string;
  details?: PolicyDetails;
}

export interface PolicyDetails {
  // Motor-specific
  vehicleNumber?: string;
  vehicleType?: string;
  vehicleYear?: number;

  // Endowment-specific
  term?: number;
  maturityAmount?: number;
  bonusRate?: number;

  // House-specific
  propertyAddress?: string;
  propertyValue?: number;
  constructionType?: string;

  // Commercial-specific
  businessName?: string;
  businessType?: string;
  annualTurnover?: number;
}

export interface CreatePolicyRequest {
  customerId: string;
  policyType: PolicyType;
  policyNumber: string;
  startDate: string;
  endDate: string;
  premiumAmount: number;
  details?: PolicyDetails;
}

export interface UpdatePolicyRequest {
  status?: PolicyStatus;
  endDate?: string;
  premiumAmount?: number;
  details?: PolicyDetails;
}

// ============================================================================
// Form & UI State
// ============================================================================

export interface FormError {
  field: string;
  message: string;
}

export interface FormState<T> {
  values: T;
  errors: Record<string, string>;
  touched: Record<string, boolean>;
  isSubmitting: boolean;
  isValid: boolean;
}

export interface PaginationParams {
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

export interface PaginatedResponse<T> {
  data: T[];
  pagination: PaginationParams;
  metadata?: Record<string, unknown>;
}

// ============================================================================
// Theme & UI Configuration
// ============================================================================

export type ThemeMode = 'light' | 'dark';

export interface ThemeContextType {
  mode: ThemeMode;
  toggleTheme: () => void;
}

// ============================================================================
// API Configuration
// ============================================================================

export interface ApiConfig {
  baseURL: string;
  timeout: number;
  headers: Record<string, string>;
  withCredentials: boolean;
}

// ============================================================================
// Environment Variables
// ============================================================================

export interface Environment {
  VITE_API_BASE_URL: string;
  VITE_ZITADEL_CLIENT_ID: string;
  VITE_ZITADEL_AUTHORITY: string;
  MODE: 'development' | 'production' | 'test';
  SSR?: boolean;
}
