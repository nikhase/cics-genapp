import { CustomerDto } from '@/features/customers/types';

interface CustomerSummaryProps {
  customer: CustomerDto;
}

function buildAddress({ houseName, houseNumber, postalCode }: CustomerDto): string {
  const segments = [houseNumber, houseName, postalCode].filter(Boolean);
  return segments.join(', ');
}

export function CustomerSummary({ customer }: CustomerSummaryProps) {
  const address = buildAddress(customer);

  return (
    <section style={{ border: '1px solid #ddd', padding: '1rem', borderRadius: '8px', marginTop: '1.5rem' }}>
      <h2 style={{ marginTop: 0 }}>
        {customer.firstName} {customer.lastName}
      </h2>
      <dl style={{ display: 'grid', gridTemplateColumns: 'auto 1fr', gap: '0.5rem 1rem' }}>
        <dt>ID</dt>
        <dd>{customer.id}</dd>
        {customer.dateOfBirth && (
          <>
            <dt>Date of Birth</dt>
            <dd>{customer.dateOfBirth}</dd>
          </>
        )}
        {address && (
          <>
            <dt>Address</dt>
            <dd>{address}</dd>
          </>
        )}
        {customer.numPolicies !== undefined && customer.numPolicies !== null && (
          <>
            <dt>Policies</dt>
            <dd>{customer.numPolicies}</dd>
          </>
        )}
        {customer.phoneMobile && (
          <>
            <dt>Mobile</dt>
            <dd>{customer.phoneMobile}</dd>
          </>
        )}
        {customer.phoneHome && (
          <>
            <dt>Home</dt>
            <dd>{customer.phoneHome}</dd>
          </>
        )}
        {customer.email && (
          <>
            <dt>Email</dt>
            <dd>{customer.email}</dd>
          </>
        )}
      </dl>
    </section>
  );
}
