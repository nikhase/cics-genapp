import { render, screen } from '@testing-library/react';
import { CustomerSummary } from '@/features/customers/components/CustomerSummary';
import { CustomerDto } from '@/features/customers/types';

describe('CustomerSummary', () => {
  const baseCustomer: CustomerDto = {
    id: '0000000001',
    firstName: 'Andrew',
    lastName: 'Pandy'
  };

  it('renders mandatory fields', () => {
    render(<CustomerSummary customer={baseCustomer} />);

    expect(screen.getByRole('heading', { name: /Andrew Pandy/i })).toBeInTheDocument();
    expect(screen.getByText('0000000001')).toBeInTheDocument();
  });

  it('renders optional fields when present', () => {
    const customer: CustomerDto = {
      ...baseCustomer,
      dateOfBirth: '1950-07-11',
      houseName: 'Beech House',
      houseNumber: '34',
      postalCode: 'PI101O',
      numPolicies: 4,
      phoneMobile: '01234 567890',
      phoneHome: '09876 543210',
      email: 'a.pandy@example.com'
    };

    render(<CustomerSummary customer={customer} />);

    expect(screen.getByText('1950-07-11')).toBeInTheDocument();
    expect(screen.getByText('34, Beech House, PI101O')).toBeInTheDocument();
    expect(screen.getByText('4')).toBeInTheDocument();
    expect(screen.getByText('01234 567890')).toBeInTheDocument();
    expect(screen.getByText('09876 543210')).toBeInTheDocument();
    expect(screen.getByText('a.pandy@example.com')).toBeInTheDocument();
  });
});
