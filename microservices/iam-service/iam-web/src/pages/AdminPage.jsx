import React, { useState, useEffect } from 'react';
import ModernSidebar from '../components/layout/ModernSidebar';
import ModernHeader from '../components/layout/ModernHeader';
import { accountService } from '../services/accountService';
import PersonalOverviewView from '../components/account/PersonalOverviewView';
import PasswordAndSecurityView from '../components/account/PasswordAndSecurityView';
import ProfileTab from '../components/account/ProfileTab';
import LinkedAccountsTab from '../components/account/LinkedAccountsTab';
import SessionTab from '../components/account/SessionTab';
import LoginHistoryTab from '../components/account/LoginHistoryTab';
import AccountActivityTab from '../components/account/AccountActivityTab';
import DeactivationTab from '../components/account/DeactivationTab';
import AdminOverviewTab from '../components/admin/AdminOverviewTab';
import UserManagementTab from '../components/admin/UserManagementTab';
import RoleManagementTab from '../components/admin/RoleManagementTab';
import PermissionManagementTab from '../components/admin/PermissionManagementTab';
import ApplicationsView from '../components/admin/ApplicationsView';
import UserSessionsTab from '../components/admin/UserSessionsTab';
import UserLoginHistoryTab from '../components/admin/UserLoginHistoryTab';
import AccountSecurityTab from '../components/admin/AccountSecurityTab';
import AuditLogView from '../components/admin/AuditLogView';
import SystemConfigTab from '../components/admin/SystemConfigTab';
import ModernDashboardView from '../components/admin/ModernDashboardView';

export default function AdminPage({ onSwitchPortal }) {
  const [activeTab, setActiveTab] = useState('overview');
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    accountService.getProfile().then((data) => {
      if (data) setProfile(data);
    });
    const handleUpdate = (e) => {
      if (e.detail) setProfile((p) => ({ ...p, ...e.detail }));
    };
    window.addEventListener('profile-updated', handleUpdate);
    return () => window.removeEventListener('profile-updated', handleUpdate);
  }, []);

  const isAdmin =
    profile?.roles?.some((r) => (r.name || r).toUpperCase() === 'ADMIN') ||
    profile?.role?.toUpperCase() === 'ADMIN' ||
    profile?.username === 'admin';

  const adminTabs = [
    'dashboard',
    'users',
    'roles',
    'permissions',
    'clients',
    'user_sessions',
    'user_logins',
    'admin_security',
    'invitations',
    'audit',
    'config',
    'live_monitor',
  ];

  // Prevent non-admin users from viewing admin tabs
  useEffect(() => {
    if (profile && !isAdmin && adminTabs.includes(activeTab)) {
      setActiveTab('overview');
    }
  }, [profile, isAdmin, activeTab]);

  return (
    <div className="h-screen w-full overflow-hidden flex bg-slate-50/70 dark:bg-slate-950 text-slate-800 dark:text-slate-100 transition-colors">
      {/* 1. Left Sidebar Navigation (Unified My Account & Administration) */}
      <ModernSidebar activeTab={activeTab} onSelectTab={setActiveTab} />

      {/* 2. Main Content Area */}
      <div className="flex-1 flex flex-col h-screen min-w-0 overflow-hidden relative z-10">
        {/* Top Header Bar */}
        <ModernHeader
          currentTab={activeTab}
          onNavigateTab={setActiveTab}
          onSwitchPortal={onSwitchPortal}
        />

        {/* Scrollable Page Body */}
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8">
          <div className="w-full">
            {/* My Account Section Views */}
            {activeTab === 'overview' && (
              <PersonalOverviewView onNavigateTab={setActiveTab} />
            )}
            {activeTab === 'profile' && <ProfileTab />}
            {activeTab === 'account_security' && (
              <PasswordAndSecurityView onNavigateTab={setActiveTab} />
            )}
            {activeTab === 'linked' && <LinkedAccountsTab />}
            {activeTab === 'sessions' && <SessionTab />}
            {activeTab === 'history' && <LoginHistoryTab />}
            {activeTab === 'activity' && <AccountActivityTab />}
            {activeTab === 'deactivation' && <DeactivationTab />}

            {/* Administration Section Views (Admin only) */}
            {isAdmin && (
              <>
                {activeTab === 'dashboard' && (
                  <AdminOverviewTab onNavigateTab={setActiveTab} />
                )}
                {activeTab === 'users' && <UserManagementTab />}
                {activeTab === 'roles' && <RoleManagementTab />}
                {activeTab === 'permissions' && <PermissionManagementTab />}
                {activeTab === 'clients' && <ApplicationsView />}
                {activeTab === 'user_sessions' && <UserSessionsTab />}
                {activeTab === 'user_logins' && <UserLoginHistoryTab />}
                {activeTab === 'admin_security' && <AccountSecurityTab />}
                {activeTab === 'invitations' && (
                  <div className="bg-white dark:bg-slate-900 p-12 rounded-3xl border border-slate-200/80 dark:border-slate-800 text-center shadow-xs">
                    <h3 className="font-extrabold text-lg text-slate-900 dark:text-white mb-1">
                      Invitations
                    </h3>
                    <p className="text-xs text-slate-400">
                      Manage organization member invitations. No pending invitations at this time.
                    </p>
                  </div>
                )}
                {activeTab === 'audit' && <AuditLogView />}
                {activeTab === 'config' && <SystemConfigTab />}
                {activeTab === 'live_monitor' && (
                  <ModernDashboardView onNavigateTab={setActiveTab} />
                )}
              </>
            )}
          </div>
        </main>
      </div>
    </div>
  );
}
