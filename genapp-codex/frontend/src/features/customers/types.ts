export interface CustomerDto {
  id: string;
  firstName: string;
  lastName: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  stateProvince?: string;
  postalCode?: string;
  country?: string;
  phoneNumber?: string;
  email?: string;
}
