import { useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useQuery } from '@tanstack/react-query';
import { getCustomer, searchCustomers } from '@/features/customers/api/getCustomer';
import { CustomerDto } from '@/features/customers/types';
import { CustomerSummary } from '@/features/customers/components/CustomerSummary';

interface CustomerInquiryForm {
  query: string;
}

interface CustomerResultsProps {
  customers: CustomerDto[];
  onSelect: (customer: CustomerDto) => void;
  selectedId: string | null;
  isLoading: boolean;
}

function CustomerResultsTable({ customers, onSelect, selectedId, isLoading }: CustomerResultsProps) {
  if (isLoading) {
    return <p>Searching customers…</p>;
  }

  if (customers.length === 0) {
    return <p>No customers match the search criteria.</p>;
  }

  return (
    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '1rem' }}>
      <thead>
        <tr>
          <th style={{ textAlign: 'left', borderBottom: '1px solid #ccc', padding: '0.5rem' }}>ID</th>
          <th style={{ textAlign: 'left', borderBottom: '1px solid #ccc', padding: '0.5rem' }}>Name</th>
          <th style={{ textAlign: 'left', borderBottom: '1px solid #ccc', padding: '0.5rem' }}>Postal Code</th>
          <th style={{ textAlign: 'left', borderBottom: '1px solid #ccc', padding: '0.5rem' }}>Email</th>
          <th style={{ textAlign: 'right', borderBottom: '1px solid #ccc', padding: '0.5rem' }}>Policies</th>
        </tr>
      </thead>
      <tbody>
        {customers.map((customer) => {
          const isSelected = customer.id === selectedId;
          return (
            <tr
              key={customer.id}
              onClick={() => onSelect(customer)}
              style={{
                cursor: 'pointer',
                backgroundColor: isSelected ? '#e3f2fd' : 'transparent'
              }}
            >
              <td style={{ padding: '0.5rem', borderBottom: '1px solid #eee' }}>{customer.id}</td>
              <td style={{ padding: '0.5rem', borderBottom: '1px solid #eee' }}>
                {customer.firstName} {customer.lastName}
              </td>
              <td style={{ padding: '0.5rem', borderBottom: '1px solid #eee' }}>{customer.postalCode ?? ''}</td>
              <td style={{ padding: '0.5rem', borderBottom: '1px solid #eee' }}>{customer.email ?? ''}</td>
              <td style={{ padding: '0.5rem', borderBottom: '1px solid #eee', textAlign: 'right' }}>
                {customer.numPolicies ?? ''}
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}

export function CustomerInquiryPage() {
  const [submittedQuery, setSubmittedQuery] = useState('');
  const [selectedCustomerId, setSelectedCustomerId] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors }
  } = useForm<CustomerInquiryForm>({
    defaultValues: { query: '' }
  });

  const currentQueryValue = watch('query');

  const {
    data: customers = [],
    isFetching: isSearching,
    error: searchError
  } = useQuery<CustomerDto[], Error>({
    queryKey: ['customers', submittedQuery],
    queryFn: () => searchCustomers(submittedQuery),
    keepPreviousData: true
  });

  const {
    data: selectedCustomer,
    error: detailError,
    isError: isDetailError,
    isFetching: isLoadingCustomer
  } = useQuery<CustomerDto, Error>({
    queryKey: ['customer', selectedCustomerId],
    queryFn: () => getCustomer(selectedCustomerId ?? ''),
    enabled: Boolean(selectedCustomerId)
  });

  useEffect(() => {
    if (submittedQuery && /^\d{10}$/.test(submittedQuery)) {
      setSelectedCustomerId(submittedQuery);
    }
  }, [submittedQuery]);

  useEffect(() => {
    if (customers.length === 0) {
      setSelectedCustomerId(null);
      return;
    }
    if (!selectedCustomerId || !customers.some((c) => c.id === selectedCustomerId)) {
      setSelectedCustomerId(customers[0].id);
    }
  }, [customers, selectedCustomerId]);

  const onSubmit = (values: CustomerInquiryForm) => {
    const cleaned = values.query.trim();
    setSubmittedQuery(cleaned);
    if (!cleaned) {
      setSelectedCustomerId(null);
    }
  };

  const handleRowSelect = (customer: CustomerDto) => {
    setSelectedCustomerId(customer.id);
  };

  const helperText = useMemo(() => {
    const base = submittedQuery
      ? `Results for "${submittedQuery}"`
      : 'Showing the first 20 customers ordered by last name.';
    return `${base} (${customers.length} record${customers.length === 1 ? '' : 's'}).`;
  }, [submittedQuery, customers.length]);

  return (
    <main className="container" style={{ margin: '2rem auto', maxWidth: '960px' }}>
      <h1>Customer Inquiry</h1>
      <p>
        Use the search box to locate customers by ID, first name, or last name. Select a row to view the detailed
        panel, similar to the SSC1 transaction.
      </p>

      <form onSubmit={handleSubmit(onSubmit)} style={{ marginTop: '1.5rem', marginBottom: '1.5rem' }}>
        <label htmlFor="query">Search</label>
        <div style={{ display: 'flex', gap: '0.75rem', marginTop: '0.5rem' }}>
          <input
            id="query"
            type="text"
            placeholder="Customer ID or part of a name"
            {...register('query', {
              maxLength: { value: 40, message: 'Query should not exceed 40 characters' }
            })}
            style={{ flex: 1, padding: '0.5rem', fontSize: '1rem' }}
          />
          <button type="submit" style={{ padding: '0.5rem 1rem' }} disabled={isSearching}>
            {isSearching ? 'Searching…' : 'Search'}
          </button>
        </div>
        {errors.query && <p style={{ color: 'crimson', marginTop: '0.5rem' }}>{errors.query.message}</p>}
        {currentQueryValue && currentQueryValue.length > 0 && currentQueryValue.length < 10 && (
          <p style={{ marginTop: '0.5rem', color: '#555' }}>
            Tip: enter a full 10-digit ID to jump straight to a single record.
          </p>
        )}
      </form>

      {searchError && <p style={{ color: 'crimson' }}>{searchError.message}</p>}
      <p>{helperText}</p>
      <CustomerResultsTable
        customers={customers}
        onSelect={handleRowSelect}
        selectedId={selectedCustomerId}
        isLoading={isSearching && customers.length === 0}
      />

      {isDetailError && <p style={{ color: 'crimson' }}>{detailError?.message ?? 'Unable to load customer'}</p>}
      {isLoadingCustomer && <p>Loading selected customer…</p>}
      {selectedCustomer && <CustomerSummary customer={selectedCustomer} />}
    </main>
  );
}
