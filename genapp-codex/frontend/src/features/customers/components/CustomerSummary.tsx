import { CustomerDto } from '@/features/customers/types';

interface CustomerSummaryProps {
  customer: CustomerDto;
}

export function CustomerSummary({ customer }: CustomerSummaryProps) {
  const addressLines = [customer.addressLine1, customer.addressLine2].filter(Boolean).join(', ');
  const cityLine = [customer.city, customer.stateProvince].filter(Boolean).join(', ');

  return (
    <section style={{ border: '1px solid #ddd', padding: '1rem', borderRadius: '8px' }}>
      <h2 style={{ marginTop: 0 }}>
        {customer.firstName} {customer.lastName}
      </h2>
      <dl style={{ display: 'grid', gridTemplateColumns: 'auto 1fr', gap: '0.5rem 1rem' }}>
        <dt>ID</dt>
        <dd>{customer.id}</dd>
        <dt>Address</dt>
        <dd>
          <div>{addressLines}</div>
          <div>
            {cityLine}
            {customer.postalCode ? ` ${customer.postalCode}` : ''}
          </div>
          {customer.country && <div>{customer.country}</div>}
        </dd>
        {customer.phoneNumber && (
          <>
            <dt>Phone</dt>
            <dd>{customer.phoneNumber}</dd>
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
