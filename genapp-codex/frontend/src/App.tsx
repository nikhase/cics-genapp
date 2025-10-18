import { Navigate, Route, Routes } from 'react-router-dom';
import { CustomerInquiryPage } from '@/features/customers/pages/CustomerInquiryPage';

function App() {
  return (
    <Routes>
      <Route path="/customers" element={<CustomerInquiryPage />} />
      <Route path="/" element={<Navigate to="/customers" replace />} />
      <Route path="*" element={<Navigate to="/customers" replace />} />
    </Routes>
  );
}

export default App;
