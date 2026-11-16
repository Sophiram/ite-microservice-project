import React, { useState, useEffect } from 'react';
import {
  LayoutDashboard,
  User,
  Key,
  Link2,
  Shield,
  ShieldCheck,
  Users,
  Mail,
  Layers,
  Activity,
  ChevronLeft,
  ChevronRight,
  LogOut,
  Laptop,
  History,
  AlertOctagon,
  FileText,
  Sliders,
  ShieldAlert,
  MonitorSmartphone,
  KeyRound,
  X,
} from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';
import { accountService } from '../../services/accountService';

export default function ModernSidebar({ activeTab, onSelectTab }) {
  const { t, locale, sidebarCollapsed, toggleSidebar, mobileSidebarOpen, setMobileSidebarOpen } = useLanguage();
  const [profile, setProfile] = useState({
    name: '',
    email: '',
  });

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

  const NAV_SECTIONS = [
    {
      title: locale === 'km' ? 'គណនីផ្ទាល់ខ្លួន' : 'My Account',
      items: [
        { id: 'overview', label: locale === 'km' ? 'ទិដ្ឋភាពទូទៅ' : 'Overview', icon: LayoutDashboard },
        { id: 'profile', label: locale === 'km' ? 'ព័ត៌មានផ្ទាល់ខ្លួន' : 'Profile', icon: User },
        { id: 'account_security', label: locale === 'km' ? 'ពាក្យសម្ងាត់ និងសុវត្ថិភាព' : 'Security & Password', icon: Key },
        { id: 'linked', label: locale === 'km' ? 'គណនីដែលបានភ្ជាប់' : 'Linked Accounts', icon: Link2 },
        { id: 'sessions', label: locale === 'km' ? 'សម័យការងារសកម្ម' : 'Active Sessions', icon: Laptop },
        { id: 'history', label: locale === 'km' ? 'ប្រវត្តិនៃការចូល' : 'Login History', icon: History },
        { id: 'activity', label: locale === 'km' ? 'សកម្មភាពគណនី' : 'Account Activity', icon: FileText },
        { id: 'deactivation', label: locale === 'km' ? 'ការបិទគណនី' : 'Account Deactivation', icon: AlertOctagon },
      ],
    },
    ...(isAdmin
      ? [
          {
            title: locale === 'km' ? 'ការគ្រប់គ្រងប្រព័ន្ធ' : 'Administration',
            items: [
              { id: 'dashboard', label: locale === 'km' ? 'ផ្ទាំងគ្រប់គ្រង Admin' : 'Admin Overview', icon: Shield },
              { id: 'users', label: locale === 'km' ? 'អ្នកប្រើប្រាស់ & សិទ្ធិ' : 'User Management', icon: Users },
              { id: 'roles', label: locale === 'km' ? 'តួនាទី (Roles)' : 'Roles Management', icon: ShieldCheck },
              { id: 'permissions', label: locale === 'km' ? 'កាតាឡុកសិទ្ធិ' : 'Permissions Catalog', icon: Key },
              { id: 'clients', label: locale === 'km' ? 'កម្មវិធី OAuth2 Clients' : 'OAuth2 Clients', icon: Layers },
              { id: 'user_sessions', label: locale === 'km' ? 'គ្រប់គ្រងសម័យការងារ' : 'User Active Sessions', icon: MonitorSmartphone },
              { id: 'user_logins', label: locale === 'km' ? 'ត្រួតពិនិត្យការចូល' : 'User Login History', icon: ShieldAlert },
              { id: 'admin_security', label: locale === 'km' ? 'ប្រតិបត្តិការសុវត្ថិភាព' : 'Account Security Ops', icon: KeyRound },
              { id: 'audit', label: locale === 'km' ? 'កំណត់ហេតុសវនកម្ម' : 'Audit Logs', icon: Activity },
              { id: 'config', label: locale === 'km' ? 'គោលការណ៍ប្រព័ន្ធ' : 'System Configuration', icon: Sliders },
            ],
          },
        ]
      : []),
  ];

  return (
    <>
      {/* Mobile Backdrop */}
      {mobileSidebarOpen && (
        <div
          onClick={() => setMobileSidebarOpen(false)}
          className="fixed inset-0 bg-slate-950/60 backdrop-blur-xs z-40 md:hidden transition-opacity"
        />
      )}

      <aside
        className={`fixed inset-y-0 left-0 z-50 md:sticky md:top-0 h-screen shrink-0 flex flex-col justify-between bg-white dark:bg-slate-900 border-r border-slate-200/80 dark:border-slate-800 transition-all duration-300 select-none ${
          mobileSidebarOpen ? 'translate-x-0 shadow-2xl' : '-translate-x-full md:translate-x-0'
        } ${
          sidebarCollapsed ? 'md:w-20 w-72' : 'w-72 md:w-64'
        }`}
      >
        {/* Floating Collapse Button positioned cleanly on the border (desktop only) */}
        <button
          onClick={toggleSidebar}
          className="hidden md:flex absolute -right-3 top-5 w-6 h-6 rounded-full bg-indigo-600 text-white items-center justify-center shadow-md hover:bg-indigo-700 transition cursor-pointer z-50 border-2 border-white dark:border-slate-900"
          title={sidebarCollapsed ? 'Expand Sidebar' : 'Collapse Sidebar'}
        >
          {sidebarCollapsed ? <ChevronRight size={13} /> : <ChevronLeft size={13} />}
        </button>

        {/* 1. Brand Logo Header */}
        <div className="h-16 shrink-0 flex items-center justify-between px-5 border-b border-slate-100 dark:border-slate-800">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-rose-500 via-indigo-600 to-indigo-800 flex items-center justify-center text-white font-black text-sm shadow-md shrink-0">
              K
            </div>
            {(!sidebarCollapsed || mobileSidebarOpen) && (
              <div className="overflow-hidden">
                <div className="font-extrabold text-sm text-slate-900 dark:text-white tracking-tight leading-tight">
                  ISTAD
                </div>
                <div className="text-[10px] text-slate-400 font-medium tracking-wide">
                  Identity & Access
                </div>
              </div>
            )}
          </div>

          {/* Close button for mobile drawer */}
          <button
            onClick={() => setMobileSidebarOpen(false)}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 md:hidden cursor-pointer"
            title="Close Menu"
          >
            <X size={18} />
          </button>
        </div>

        {/* 2. Middle Navigation */}
        <div className="flex-1 overflow-y-auto py-4 px-3 space-y-6 scrollbar-thin">
          {NAV_SECTIONS.map((section, sIdx) => (
            <div key={sIdx} className="space-y-1">
              {(!sidebarCollapsed || mobileSidebarOpen) && (
                <div className="px-3 pb-1 text-[11px] font-bold text-slate-400 dark:text-slate-500 tracking-wider">
                  {section.title}
                </div>
              )}
              {section.items.map((item) => {
                const Icon = item.icon;
                const isActive = activeTab === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => {
                      onSelectTab(item.id);
                      setMobileSidebarOpen(false);
                    }}
                    title={sidebarCollapsed ? item.label : undefined}
                    className={`relative w-full flex items-center gap-3 px-3 py-2 rounded-xl font-medium text-xs sm:text-sm transition cursor-pointer group ${
                      isActive
                        ? 'bg-indigo-50/80 dark:bg-indigo-950/40 text-indigo-700 dark:text-indigo-400 font-semibold'
                        : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100/70 dark:hover:bg-slate-800/60 hover:text-slate-900 dark:hover:text-slate-200'
                    }`}
                  >
                    <Icon
                      size={17}
                      className={
                        isActive
                          ? 'text-indigo-600 dark:text-indigo-400'
                          : 'text-slate-400 group-hover:text-slate-700 dark:group-hover:text-slate-300 transition-colors'
                      }
                    />
                    {(!sidebarCollapsed || mobileSidebarOpen) && <span className="truncate">{item.label}</span>}

                    {/* Left edge indicator */}
                    {isActive && (
                      <span className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-5 bg-indigo-600 rounded-r-full" />
                    )}
                  </button>
                );
              })}
            </div>
          ))}
        </div>

        {/* 3. Bottom User Profile Pill */}
        <div className="shrink-0 p-3 border-t border-slate-100 dark:border-slate-800 bg-white dark:bg-slate-900">
          <div className="flex items-center justify-between p-2 rounded-2xl bg-slate-50 dark:bg-slate-800/50">
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-full bg-indigo-50 dark:bg-slate-700 text-indigo-700 dark:text-indigo-300 font-bold text-xs flex items-center justify-center shrink-0 border border-indigo-200 dark:border-slate-600">
                {profile.name ? profile.name.split(' ').map((n) => n[0]).join('').slice(0, 2).toUpperCase() : 'SS'}
              </div>
              {(!sidebarCollapsed || mobileSidebarOpen) && (
                <div className="overflow-hidden">
                  <div className="font-semibold text-xs text-slate-800 dark:text-slate-200 truncate">
                    {profile.name || profile.username || 'Administrator'}
                  </div>
                  <div className="text-[10px] text-slate-400 truncate">
                    {profile.email && !profile.email.includes('@users.invalid')
                      ? profile.email
                      : (profile.username ? `@${profile.username}` : 'Social Account')}
                  </div>
                </div>
              )}
            </div>
            {(!sidebarCollapsed || mobileSidebarOpen) && (
              <button
                type="button"
                onClick={() => {
                  try {
                    localStorage.clear();
                    sessionStorage.clear();
                  } catch {}
                  const form = document.createElement('form');
                  form.method = 'POST';
                  form.action = '/logout';
                  document.body.appendChild(form);
                  form.submit();
                }}
                title="Sign Out"
                className="p-1.5 text-slate-400 hover:text-rose-600 transition cursor-pointer"
              >
                <LogOut size={15} />
              </button>
            )}
          </div>
        </div>
      </aside>
    </>
  );
}
