import React from 'react';
import { Shield, User, Globe, ChevronDown } from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';

export default function Navbar({ currentPortal, onSwitchPortal }) {
  const { locale, setLocale, t } = useLanguage();

  return (
    <header className="sticky top-0 z-40 bg-white/90 backdrop-blur-md border-b border-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-indigo-600 to-purple-600 text-white flex items-center justify-center font-black shadow-md shadow-indigo-100">
            <Shield size={20} />
          </div>
          <div>
            <div className="font-extrabold text-sm sm:text-base text-slate-900 leading-tight">
              {t('appTitle')}
            </div>
            <div className="text-[10px] text-slate-400 font-medium">
              Institute of Science & Technology Advanced Development
            </div>
          </div>
        </div>

        {/* Center / Right controls */}
        <div className="flex items-center gap-2 sm:gap-4">
          {/* Portal Switcher Buttons */}
          <div className="bg-slate-100 p-1 rounded-xl flex items-center gap-1">
            <button
              onClick={() => onSwitchPortal('account')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition ${
                currentPortal === 'account'
                  ? 'bg-white text-indigo-700 shadow-sm'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <User size={14} />
              <span className="hidden sm:inline">{t('accountPortal')}</span>
            </button>
            <button
              onClick={() => onSwitchPortal('admin')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition ${
                currentPortal === 'admin'
                  ? 'bg-indigo-600 text-white shadow-sm'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <Shield size={14} />
              <span className="hidden sm:inline">{t('adminPortal')}</span>
            </button>
          </div>

          {/* Language Switcher */}
          <div className="flex items-center gap-1 bg-slate-50 border border-slate-200 px-2.5 py-1.5 rounded-xl text-xs font-semibold text-slate-700">
            <Globe size={14} className="text-slate-400" />
            <select
              value={locale}
              onChange={(e) => setLocale(e.target.value)}
              className="bg-transparent outline-none cursor-pointer text-xs font-medium"
            >
              <option value="en">English (EN)</option>
              <option value="km">ភាសាខ្មែរ (KM)</option>
            </select>
          </div>
        </div>
      </div>
    </header>
  );
}
