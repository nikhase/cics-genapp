export interface CustomerDto {
  id: string;
  firstName: string;
  lastName: string;
  dateOfBirth?: string | null;
  houseName?: string | null;
  houseNumber?: string | null;
  postalCode?: string | null;
  numPolicies?: number | null;
  phoneMobile?: string | null;
  phoneHome?: string | null;
  email?: string | null;
}
