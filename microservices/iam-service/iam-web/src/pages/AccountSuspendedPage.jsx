import React from 'react';
import { Lock, ShieldAlert, ArrowLeft, Mail, AlertTriangle, Sun, Moon, HelpCircle } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';

export default function AccountSuspendedPage() {
  const { locale, setLocale, theme, toggleTheme } = useLanguage();
  const params = new URLSearchParams(window.location.search);
  const reason = params.get('reason') || 'account_disabled';
  const isLocked = reason === 'account_locked';

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 flex flex-col justify-center py-12 sm:px-6 lg:px-8 transition-colors selection:bg-rose-500 selection:text-white">
      {/* Top Controls: Language & Theme */}
      <div className="absolute top-4 right-4 flex items-center gap-2">
        <select
          value={locale}
          onChange={(e) => setLocale(e.target.value)}
          className="px-2.5 py-1.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 text-xs font-semibold text-slate-700 dark:text-slate-300 outline-none shadow-2xs cursor-pointer"
        >
          <option value="en">English</option>
          <option value="km">ភាសាខ្មែរ</option>
        </select>
        <button
          type="button"
          onClick={toggleTheme}
          className="p-2 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition shadow-2xs cursor-pointer"
          title="Toggle Theme"
        >
          {theme === 'dark' ? <Sun size={16} /> : <Moon size={16} />}
        </button>
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-lg px-4 sm:px-0">
        <div className="bg-white dark:bg-slate-900 py-10 px-6 sm:px-12 shadow-2xl shadow-slate-200/50 dark:shadow-none rounded-3xl border border-slate-200/80 dark:border-slate-800 text-center">
          {/* Animated Warning Icon */}
          <div className="flex justify-center mb-6">
            <div className={`w-20 h-20 rounded-3xl flex items-center justify-center text-white shadow-xl ${
              isLocked
                ? 'bg-gradient-to-tr from-amber-500 to-orange-500 shadow-amber-500/30'
                : 'bg-gradient-to-tr from-rose-600 to-red-500 shadow-rose-600/30'
            }`}>
              {isLocked ? <Lock size={40} /> : <ShieldAlert size={40} />}
            </div>
          </div>

          {/* Status Badge */}
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold mb-3 border">
            {isLocked ? (
              <span className="bg-amber-50 dark:bg-amber-950/50 text-amber-700 dark:text-amber-300 border-amber-200 dark:border-amber-800 px-3 py-1 rounded-full flex items-center gap-1.5">
                <AlertTriangle size={13} />
                {locale === 'km' ? 'ស្ថានភាព៖ ត្រូវបានចាក់សោ (Locked)' : 'Status: Account Locked'}
              </span>
            ) : (
              <span className="bg-rose-50 dark:bg-rose-950/50 text-rose-700 dark:text-rose-300 border-rose-200 dark:border-rose-800 px-3 py-1 rounded-full flex items-center gap-1.5">
                <AlertTriangle size={13} />
                {locale === 'km' ? 'ស្ថានភាព៖ ត្រូវបានផ្អាកដំណើរការ (Deactivated)' : 'Status: Account Deactivated'}
              </span>
            )}
          </div>

          {/* Title */}
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {isLocked
              ? (locale === 'km' ? 'គណនីរបស់អ្នកត្រូវបានចាក់សោ' : 'Your Account Has Been Locked')
              : (locale === 'km' ? 'គណនីត្រូវបានផ្អាកដំណើរការ' : 'Account Has Been Deactivated')}
          </h1>

          {/* Explanation */}
          <p className="mt-3 text-xs sm:text-sm text-slate-600 dark:text-slate-300 leading-relaxed">
            {isLocked
              ? (locale === 'km'
                  ? 'គណនីរបស់អ្នកត្រូវបានចាក់សោជាបណ្តោះអាសន្នដោយសារតែគោលការណ៍សុវត្ថិភាព ឬត្រូវបានចាក់សោដោយអ្នកគ្រប់គ្រងប្រព័ន្ធ (Admin)។ អ្នកមិនអាចចូលប្រើប្រាស់បានទេនៅពេលនេះ។'
                  : 'Your account has been temporarily locked due to security policies or by an administrator. Access to services has been restricted.')
              : (locale === 'km'
                  ? 'គណនីនេះត្រូវបានផ្អាកដំណើរការ (Disabled/Deactivated) ដោយអ្នកគ្រប់គ្រងប្រព័ន្ធ (Admin)។ អ្នកមិនអាចចូលប្រើប្រាស់សេវាកម្ម ISTAD ជាមួយគណនីនេះទៀតឡើយ។'
                  : 'This account has been deactivated or disabled by an administrator. You do not currently have permission to access ISTAD services.')}
          </p>

          {/* Support / Contact Card */}
          <div className="mt-6 p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200/80 dark:border-slate-700/60 text-left text-xs space-y-2">
            <div className="font-bold text-slate-800 dark:text-slate-200 flex items-center gap-2">
              <HelpCircle size={15} className="text-indigo-600 dark:text-indigo-400" />
              <span>{locale === 'km' ? 'តើអ្នកត្រូវធ្វើដូចម្តេច?' : 'How to resolve this?'}</span>
            </div>
            <p className="text-slate-500 dark:text-slate-400">
              {locale === 'km'
                ? 'សូមទាក់ទងទៅកាន់អ្នកគ្រប់គ្រងប្រព័ន្ធ (System Administrator) ឬក្រុមការងារបច្ចេកទេស ដើម្បីផ្ទៀងផ្ទាត់ និងដោះស្រាយបញ្ហាគណនីរបស់អ្នកឡើងវិញ។'
                : 'Please contact your system administrator or the IT Helpdesk team to verify your identity and restore your account access.'}
            </p>
            <div className="pt-2 border-t border-slate-200 dark:border-slate-700 flex flex-col sm:flex-row sm:items-center justify-between gap-2 text-[11px] font-mono text-slate-600 dark:text-slate-300">
              <span>Admin: <strong>admin@istad.edu.kh</strong></span>
              <span className="text-slate-400">Code: {isLocked ? 'ERR_ACCOUNT_LOCKED' : 'ERR_ACCOUNT_DISABLED'}</span>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-3">
            <a
              href="/login"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 dark:bg-white dark:hover:bg-slate-100 text-white dark:text-slate-900 font-bold text-xs transition shadow-md cursor-pointer"
            >
              <ArrowLeft size={16} />
              <span>{locale === 'km' ? 'ត្រឡប់ទៅទំព័រចូល' : 'Back to Sign In'}</span>
            </a>

            <a
              href="mailto:admin@istad.edu.kh?subject=Account%20Suspension%20Assistance"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-200 font-semibold text-xs transition cursor-pointer"
            >
              <Mail size={16} />
              <span>{locale === 'km' ? 'ទាក់ទងជំនួយ (Email Support)' : 'Email Support'}</span>
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}
