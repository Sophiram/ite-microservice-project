import React, { useState } from 'react';
import { User, Shield, Link2, Laptop, History, Activity, AlertOctagon, ArrowLeft } from 'lucide-react';
import ProfileTab from '../components/account/ProfileTab';
import SecurityTab from '../components/account/PasswordAndSecurityView';
import LinkedAccountsTab from '../components/account/LinkedAccountsTab';
import SessionsTab from '../components/account/SessionsTab';
import LoginHistoryTab from '../components/account/LoginHistoryTab';
import AccountActivityTab from '../components/account/AccountActivityTab';
import DeactivationTab from '../components/account/DeactivationTab';
import { useLanguage } from '../context/LanguageContext';

export default function AccountPage({ onSwitchPortal }) {
  const { t, locale } = useLanguage();
  const [activeTab, setActiveTab] = useState('profile');

  const TABS = [
    { id: 'profile', label: t('tabProfile'), icon: User },
    { id: 'security', label: t('tabSecurity'), icon: Shield },
    { id: 'linked', label: t('tabLinked'), icon: Link2 },
    { id: 'sessions', label: t('tabSessions'), icon: Laptop },
    { id: 'history', label: t('tabHistory'), icon: History },
    { id: 'activity', label: t('tabActivity'), icon: Activity },
    { id: 'deactivation', label: t('tabDeactivation'), icon: AlertOctagon },
  ];

  return (
    <div className="min-h-screen bg-slate-50/70 dark:bg-slate-950 p-4 sm:p-8 transition-colors">
      <div className="w-full">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          {/* Sticky Left Sidebar (Back button, Header & Tabs) */}
          <aside className="lg:col-span-4 xl:col-span-3 lg:sticky lg:top-8 self-start space-y-6 max-h-[calc(100vh-4rem)] overflow-y-auto scrollbar-thin pr-1">
            {/* Back to Dashboard Navigation */}
            <div>
              <button
                onClick={() => onSwitchPortal && onSwitchPortal('admin')}
                className="inline-flex items-center gap-2 text-xs font-semibold text-indigo-600 dark:text-indigo-400 hover:text-indigo-700 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 px-3.5 py-2 rounded-xl shadow-2xs transition cursor-pointer"
              >
                <ArrowLeft size={14} />
                <span>{locale === 'km' ? 'ត្រឡប់ទៅផ្ទាំងគ្រប់គ្រង' : 'Back to Admin Dashboard'}</span>
              </button>
            </div>

            {/* Header */}
            <div>
              <div className="flex items-center gap-2 mb-1.5">
                <span className="px-2.5 py-0.5 text-[11px] font-bold bg-indigo-50 dark:bg-indigo-950/50 text-indigo-750 dark:text-indigo-300 border border-indigo-200/80 dark:border-indigo-800/60 rounded-full tracking-wide">
                  {t('userSelfServiceBadge')}
                </span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight leading-tight">
                {t('accountSettings')}
              </h1>
              <p className="text-slate-500 dark:text-slate-400 mt-1 text-xs sm:text-sm">
                {t('accountSubtitle')}
              </p>
            </div>

            {/* Navigation Sidebar */}
            <nav className="space-y-1.5">
              {TABS.map((tab) => {
                const Icon = tab.icon;
                const isActive = activeTab === tab.id;
                return (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id)}
                    className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium text-sm transition cursor-pointer ${
                      isActive
                        ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/20 font-semibold'
                        : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200/60 dark:hover:bg-slate-800/60'
                    }`}
                  >
                    <Icon size={18} />
                    <span className="truncate">{tab.label}</span>
                  </button>
                );
              })}
            </nav>
          </aside>

          {/* Active Tab View */}
          <main className="lg:col-span-8 xl:col-span-9 w-full min-w-0">
            {activeTab === 'profile' && <ProfileTab />}
            {activeTab === 'security' && <SecurityTab />}
            {activeTab === 'linked' && <LinkedAccountsTab />}
            {activeTab === 'sessions' && <SessionsTab />}
            {activeTab === 'history' && <LoginHistoryTab />}
            {activeTab === 'activity' && <AccountActivityTab />}
            {activeTab === 'deactivation' && <DeactivationTab />}
          </main>
        </div>
      </div>
    </div>
  );
}