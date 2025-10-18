import axios from 'axios';
import { apiClient } from '@/api/client';
import { CustomerDto } from '@/features/customers/types';

export async function getCustomer(customerId: string): Promise<CustomerDto> {
  try {
    const response = await apiClient.get<CustomerDto>(`/api/customers/${customerId}`);
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      throw new Error('Customer not found');
    }
    throw error;
  }
}
