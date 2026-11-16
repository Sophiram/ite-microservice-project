import React, { useState, useEffect, useRef } from 'react';
import {
  Bell,
  Moon,
  Sun,
  ChevronDown,
  Globe,
  User,
  Shield,
  CheckCircle,
  ExternalLink,
  LogOut,
  Menu,
  Sparkles,
} from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';
import { accountService } from '../../services/accountService';

export default function ModernHeader({ currentTab, onNavigateTab, onSwitchPortal }) {
  const { locale, setLocale, t, theme, toggleTheme, toggleMobileSidebar } = useLanguage();
  const [langMenuOpen, setLangMenuOpen] = useState(false);
  const [notifMenuOpen, setNotifMenuOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const [userProfile, setUserProfile] = useState(() => {
    try {
      const cached = localStorage.getItem('istad_user_profile');
      if (cached) return JSON.parse(cached);
    } catch {}
    return { name: '', email: '', role: '' };
  });

  const isAdmin =
    userProfile?.roles?.some((r) => (r.name || r).toUpperCase() === 'ADMIN') ||
    userProfile?.role?.toUpperCase() === 'ADMIN' ||
    userProfile?.username === 'admin';

  const headerRef = useRef(null);

  useEffect(() => {
    accountService.getProfile().then((data) => {
      if (data) setUserProfile(data);
    });
    const handleUpdate = (e) => {
      if (e.detail) setUserProfile((p) => ({ ...p, ...e.detail }));
    };
    window.addEventListener('profile-updated', handleUpdate);
    return () => window.removeEventListener('profile-updated', handleUpdate);
  }, []);

  // Close dropdowns on outside click
  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (headerRef.current && !headerRef.current.contains(event.target)) {
        setLangMenuOpen(false);
        setNotifMenuOpen(false);
        setUserMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handleOutsideClick);
    return () => document.removeEventListener('mousedown', handleOutsideClick);
  }, []);

  const notifications = [];

  return (
    <header
      ref={headerRef}
      className="sticky top-0 z-20 h-16 bg-white/90 dark:bg-slate-900/90 backdrop-blur-md border-b border-slate-200/70 dark:border-slate-800 px-3 sm:px-6 flex items-center justify-between transition-colors"
    >
      {/* Left: Mobile Hamburger + Breadcrumbs */}
      <div className="flex items-center gap-2 sm:gap-3 text-sm text-slate-500 dark:text-slate-400 font-medium min-w-0">
        <button
          onClick={toggleMobileSidebar}
          className="p-2 -ml-1 rounded-xl text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 md:hidden transition cursor-pointer"
          title="Open Menu"
        >
          <Menu size={20} />
        </button>

        <div className="flex items-center gap-1.5 sm:gap-2 truncate text-xs sm:text-sm">
          <span className="hover:text-slate-900 dark:hover:text-white cursor-pointer transition hidden xs:inline">
            {t('adminBreadcrumb')}
          </span>
          <span className="text-slate-300 dark:text-slate-600 hidden xs:inline">›</span>
          <span className="text-slate-900 dark:text-white font-semibold truncate">
            {t(`tab${currentTab.charAt(0).toUpperCase() + currentTab.slice(1)}`) || t('dashboardBreadcrumb')}
          </span>
        </div>
      </div>

      {/* Right: Redesigned Sleek Action Cluster */}
      <div className="flex items-center gap-2 sm:gap-2.5 shrink-0">
        {/* 1. Portal Switcher Pill Button */}
        <button
          onClick={() => onSwitchPortal && onSwitchPortal('account')}
          className="hidden md:inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-slate-100/80 hover:bg-slate-200/80 dark:bg-slate-800/80 dark:hover:bg-slate-750 border border-slate-200/80 dark:border-slate-700/80 text-xs font-semibold text-slate-700 dark:text-slate-200 shadow-2xs hover:shadow-xs hover:border-slate-300 dark:hover:border-slate-600 transition-all duration-200 active:scale-95 cursor-pointer"
          title={t('accountPortal')}
        >
          <div className="w-5 h-5 rounded-full bg-indigo-100 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 flex items-center justify-center shrink-0">
            <User size={12} />
          </div>
          <span>{t('accountPortal')}</span>
        </button>

        {/* 2. Language Switcher Pill */}
        <div className="relative">
          <button
            onClick={() => {
              setLangMenuOpen(!langMenuOpen);
              setNotifMenuOpen(false);
              setUserMenuOpen(false);
            }}
            className={`inline-flex items-center gap-2 px-3 py-1.5 rounded-full border text-xs font-semibold shadow-2xs hover:shadow-xs transition-all duration-200 active:scale-95 cursor-pointer ${
              langMenuOpen
                ? 'border-indigo-500 ring-2 ring-indigo-500/10 bg-white dark:bg-slate-800 text-indigo-600 dark:text-indigo-400'
                : 'border-slate-200/80 dark:border-slate-750 bg-white/90 dark:bg-slate-800/90 hover:bg-slate-50 dark:hover:bg-slate-750 text-slate-700 dark:text-slate-200'
            }`}
          >
            <span className="text-sm">{locale === 'km' ? '🇰🇭' : '🇬🇧'}</span>
            <span className="font-semibold text-xs">{locale === 'km' ? 'ខ្មែរ' : 'English'}</span>
            <ChevronDown
              size={13}
              className={`text-slate-400 transition-transform duration-200 ${langMenuOpen ? 'rotate-180' : ''}`}
            />
          </button>

          {langMenuOpen && (
            <div className="absolute right-0 mt-2 w-40 bg-white dark:bg-slate-850 rounded-2xl shadow-xl shadow-slate-900/10 dark:shadow-black/40 border border-slate-200/80 dark:border-slate-750 py-1.5 z-50 text-xs animate-in fade-in-0 zoom-in-95">
              <button
                onClick={() => {
                  setLocale('km');
                  setLangMenuOpen(false);
                }}
                className={`w-full flex items-center justify-between px-3.5 py-2 hover:bg-slate-50 dark:hover:bg-slate-750 font-medium transition ${
                  locale === 'km' ? 'text-indigo-600 dark:text-indigo-400 font-bold bg-indigo-50/50 dark:bg-indigo-950/30' : 'text-slate-700 dark:text-slate-300'
                }`}
              >
                <div className="flex items-center gap-2">
                  <span className="text-base">🇰🇭</span>
                  <span>ភាសាខ្មែរ</span>
                </div>
                {locale === 'km' && <CheckCircle size={14} className="text-indigo-600 dark:text-indigo-400" />}
              </button>
              <button
                onClick={() => {
                  setLocale('en');
                  setLangMenuOpen(false);
                }}
                className={`w-full flex items-center justify-between px-3.5 py-2 hover:bg-slate-50 dark:hover:bg-slate-750 font-medium transition ${
                  locale === 'en' ? 'text-indigo-600 dark:text-indigo-400 font-bold bg-indigo-50/50 dark:bg-indigo-950/30' : 'text-slate-700 dark:text-slate-300'
                }`}
              >
                <div className="flex items-center gap-2">
                  <span className="text-base">🇬🇧</span>
                  <span>English</span>
                </div>
                {locale === 'en' && <CheckCircle size={14} className="text-indigo-600 dark:text-indigo-400" />}
              </button>
            </div>
          )}
        </div>

        {/* 3. Dark Mode Toggle Button */}
        <button
          onClick={toggleTheme}
          title={theme === 'dark' ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
          className="w-9 h-9 rounded-full border border-slate-200/80 dark:border-slate-750 bg-white/90 dark:bg-slate-800/90 text-slate-600 dark:text-slate-300 hover:text-indigo-600 dark:hover:text-amber-400 hover:border-slate-300 dark:hover:border-slate-650 hover:bg-slate-50 dark:hover:bg-slate-750 shadow-2xs hover:shadow-xs transition-all duration-200 flex items-center justify-center active:scale-95 cursor-pointer"
        >
          {theme === 'dark' ? <Sun size={17} className="text-amber-400" /> : <Moon size={17} />}
        </button>

        {/* 4. Notification Bell */}
        <div className="relative">
          <button
            onClick={() => {
              setNotifMenuOpen(!notifMenuOpen);
              setLangMenuOpen(false);
              setUserMenuOpen(false);
            }}
            title="Notifications"
            className={`w-9 h-9 rounded-full border text-slate-600 dark:text-slate-300 hover:text-indigo-600 hover:border-slate-300 dark:hover:border-slate-650 shadow-2xs hover:shadow-xs transition-all duration-200 flex items-center justify-center relative active:scale-95 cursor-pointer ${
              notifMenuOpen
                ? 'border-indigo-500 ring-2 ring-indigo-500/10 bg-white dark:bg-slate-800 text-indigo-600'
                : 'border-slate-200/80 dark:border-slate-750 bg-white/90 dark:bg-slate-800/90 hover:bg-slate-50 dark:hover:bg-slate-750'
            }`}
          >
            <Bell size={17} />
            {notifications.length > 0 && (
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-indigo-600 rounded-full ring-2 ring-white dark:ring-slate-900" />
            )}
          </button>

          {notifMenuOpen && (
            <div className="absolute right-0 mt-2 w-80 bg-white dark:bg-slate-850 rounded-2xl shadow-xl shadow-slate-900/10 dark:shadow-black/40 border border-slate-200/80 dark:border-slate-750 p-4 z-50 text-xs animate-in fade-in-0 zoom-in-95">
              <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-750">
                <h4 className="font-bold text-sm text-slate-900 dark:text-white">
                  {locale === 'km' ? 'ការជូនដំណឹងសុវត្ថិភាព' : 'Security Alerts'}
                </h4>
                {notifications.length > 0 && (
                  <span className="text-[11px] text-indigo-600 font-semibold cursor-pointer">
                    {locale === 'km' ? 'សម្គាល់ថាបានអាន' : 'Mark all read'}
                  </span>
                )}
              </div>
              {notifications.length === 0 ? (
                <div className="py-6 text-center text-xs text-slate-400">
                  {locale === 'km' ? 'គ្មានការជូនដំណឹងថ្មីទេ' : 'No new notifications'}
                </div>
              ) : (
                <div className="divide-y divide-slate-100 dark:divide-slate-750 mt-2 space-y-2">
                  {notifications.map((n) => (
                    <div key={n.id} className="pt-2 text-xs">
                      <div className="flex items-center justify-between font-semibold text-slate-800 dark:text-slate-200">
                        <span>{n.title}</span>
                        <span className="text-[10px] text-slate-400 font-normal">{n.time}</span>
                      </div>
                      <p className="text-slate-500 dark:text-slate-400 mt-0.5">{n.desc}</p>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>

        {/* Subtle Vertical Divider */}
        <div className="h-5 w-px bg-slate-200/80 dark:border-slate-800 mx-0.5 hidden sm:block" />

        {/* 5. Sleek User Profile Avatar Pill */}
        <div className="relative">
          <button
            onClick={() => {
              setUserMenuOpen(!userMenuOpen);
              setLangMenuOpen(false);
              setNotifMenuOpen(false);
            }}
            className={`flex items-center gap-2 p-1 pl-1 pr-2 rounded-full border shadow-2xs hover:shadow-xs transition-all duration-200 active:scale-95 cursor-pointer ${
              userMenuOpen
                ? 'border-indigo-500 ring-2 ring-indigo-500/10 bg-white dark:bg-slate-800'
                : 'border-slate-200/80 dark:border-slate-750 bg-white/90 dark:bg-slate-800/90 hover:bg-slate-50 dark:hover:bg-slate-750'
            }`}
          >
            <div className="relative">
              <img
                src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100"
                alt="Avatar"
                className="w-7 h-7 rounded-full object-cover ring-1 ring-slate-200 dark:ring-slate-700"
              />
              <span className="absolute -bottom-0.5 -right-0.5 w-2.5 h-2.5 bg-emerald-500 rounded-full ring-2 ring-white dark:ring-slate-900" />
            </div>
            <span className="text-xs font-semibold text-slate-700 dark:text-slate-200 hidden lg:inline max-w-[100px] truncate">
              {userProfile.name || 'Sophiram'}
            </span>
            <ChevronDown
              size={13}
              className={`text-slate-400 transition-transform duration-200 ${userMenuOpen ? 'rotate-180' : ''}`}
            />
          </button>

          {userMenuOpen && (
            <div className="absolute right-0 mt-2 w-56 bg-white dark:bg-slate-850 rounded-2xl shadow-xl shadow-slate-900/10 dark:shadow-black/40 border border-slate-200/80 dark:border-slate-750 p-2 z-50 text-xs animate-in fade-in-0 zoom-in-95">
              <div className="p-3 border-b border-slate-100 dark:border-slate-750">
                <div className="font-bold text-slate-900 dark:text-white truncate">
                  {userProfile.name || 'Sorn Sophiram'}
                </div>
                <div className="text-[11px] text-slate-400 truncate mt-0.5">
                  {userProfile.email && !userProfile.email.includes('@users.invalid')
                    ? userProfile.email
                    : 'sornsophiram11@gmail.com'}
                </div>
                <span className={`inline-block mt-2 px-2.5 py-0.5 font-bold text-[10px] rounded-full ${
                  isAdmin
                    ? 'bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400'
                    : 'bg-emerald-50 dark:bg-emerald-950/50 text-emerald-600 dark:text-emerald-400'
                }`}>
                  {userProfile.roles?.[0]?.name || userProfile.role || (isAdmin ? 'ADMIN' : 'USER')}
                </span>
              </div>
              <div className="py-1">
                <button
                  onClick={() => {
                    onSwitchPortal && onSwitchPortal('account');
                    setUserMenuOpen(false);
                  }}
                  className="w-full flex items-center gap-2.5 px-3 py-2 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-750 rounded-xl transition cursor-pointer font-medium"
                >
                  <User size={14} className="text-indigo-500" />
                  <span>{t('tabProfile')}</span>
                </button>
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
                  className="w-full flex items-center gap-2.5 px-3 py-2 text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/30 rounded-xl font-medium transition cursor-pointer"
                >
                  <LogOut size={14} />
                  <span>{t('logout')}</span>
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
