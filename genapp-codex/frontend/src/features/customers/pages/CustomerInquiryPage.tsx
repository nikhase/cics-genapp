import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useQuery } from '@tanstack/react-query';
import { getCustomer } from '@/features/customers/api/getCustomer';
import { CustomerDto } from '@/features/customers/types';
import { CustomerSummary } from '@/features/customers/components/CustomerSummary';

interface CustomerInquiryForm {
  customerId: string;
}

export function CustomerInquiryPage() {
  const [submittedId, setSubmittedId] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<CustomerInquiryForm>({
    defaultValues: { customerId: '' }
  });

  const {
    data: customer,
    error,
    isError,
    isFetching
  } = useQuery<CustomerDto, Error>({
    queryKey: ['customer', submittedId],
    queryFn: () => getCustomer(submittedId ?? ''),
    enabled: submittedId !== null
  });

  const onSubmit = (values: CustomerInquiryForm) => {
    const cleaned = values.customerId.trim();
    if (!cleaned) {
      return;
    }
    setSubmittedId(cleaned);
  };

  return (
    <main className="container" style={{ margin: '2rem auto', maxWidth: '720px' }}>
      <h1>Customer Inquiry</h1>
      <p>Retrieve customer records by ID to validate parity with the legacy SSC1 transaction.</p>

      <form onSubmit={handleSubmit(onSubmit)} style={{ marginTop: '1.5rem', marginBottom: '1.5rem' }}>
        <label htmlFor="customerId">Customer ID</label>
        <div style={{ display: 'flex', gap: '0.75rem', marginTop: '0.5rem' }}>
          <input
            id="customerId"
            type="text"
            placeholder="Enter numeric ID"
            {...register('customerId', {
              required: 'Customer ID is required',
              maxLength: { value: 10, message: 'ID should not exceed 10 characters' }
            })}
            style={{ flex: 1, padding: '0.5rem', fontSize: '1rem' }}
          />
          <button type="submit" style={{ padding: '0.5rem 1rem' }} disabled={isFetching}>
            {isFetching ? 'Searching…' : 'Search'}
          </button>
        </div>
        {errors.customerId && (
          <p style={{ color: 'crimson', marginTop: '0.5rem' }}>{errors.customerId.message}</p>
        )}
      </form>

      {isError && <p style={{ color: 'crimson' }}>{error.message || 'Customer not found'}</p>}

      {customer && <CustomerSummary customer={customer} />}

      {!customer && !isFetching && submittedId && !isError && (
        <p>No customer found for ID "{submittedId}".</p>
      )}
    </main>
  );
}
