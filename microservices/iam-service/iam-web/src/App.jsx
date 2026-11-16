import React, { useState } from 'react';
import { LanguageProvider } from './context/LanguageContext';
import AdminPage from './pages/AdminPage';
import AccountPage from './pages/AccountPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import AccountSuspendedPage from './pages/AccountSuspendedPage';

function AppContent() {
  const [currentPortal, setCurrentPortal] = useState(() => {
    return window.location.pathname === '/account' ? 'account' : 'admin';
  });

  const pathname = window.location.pathname;

  if (pathname === '/login' || pathname.startsWith('/login/')) {
    return <LoginPage />;
  }
  if (pathname === '/register') {
    return <RegisterPage />;
  }
  if (pathname === '/account-suspended' || pathname === '/account-status') {
    return <AccountSuspendedPage />;
  }

  return (
    <div className="min-h-screen">
      {currentPortal === 'admin' ? (
        <AdminPage onSwitchPortal={setCurrentPortal} />
      ) : (
        <AccountPage onSwitchPortal={setCurrentPortal} />
      )}
    </div>
  );
}

export default function App() {
  return (
    <LanguageProvider>
      <AppContent />
    </LanguageProvider>
  );
}