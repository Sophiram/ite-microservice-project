import React, { useState, useEffect } from 'react';
import { Settings, Save, Shield, KeyRound, Clock, Check } from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function SystemConfigTab() {
  const { locale } = useLanguage();
  const [config, setConfig] = useState({
    maxFailedAttempts: 5,
    lockoutDurationMinutes: 30,
    mfaEnforcement: 'optional',
    passwordMinLength: 8,
    passwordRequireSpecial: true,
    passwordRequireNumbers: true,
    passwordExpirationDays: 90,
    sessionTimeoutMinutes: 60,
    accessTokenTtlSeconds: 3600,
    refreshTokenTtlSeconds: 86400,
  });
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    adminService.getSystemConfig().then(setConfig);
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    await adminService.saveSystemConfig(config);
    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  };

  return (
    <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-xs border border-slate-200/80 dark:border-slate-800 w-full transition-colors">
      <div className="flex items-center gap-3.5 mb-6 pb-6 border-b border-slate-100 dark:border-slate-800">
        <div className="w-12 h-12 rounded-2xl bg-indigo-50 dark:bg-slate-800 text-indigo-600 dark:text-indigo-400 flex items-center justify-center">
          <Settings size={22} />
        </div>
        <div>
          <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {locale === 'km' ? 'ការកំណត់គោលការណ៍ប្រព័ន្ធ IAM' : 'Global IAM Policies & System Configuration'}
          </h2>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400">
            {locale === 'km'
              ? 'កំណត់គោលការណ៍ផ្ទៀងផ្ទាត់ ពាក្យសម្ងាត់ សម័យការងារ និងអាយុកាល Token'
              : 'Enforce enterprise authentication policies, session timeouts, and token TTLs.'}
          </p>
        </div>
      </div>

      {saved && (
        <div className="p-4 mb-6 rounded-2xl text-xs sm:text-sm bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800 font-semibold flex items-center gap-2">
          <Check size={16} />
          <span>
            {locale === 'km'
              ? 'គោលការណ៍ប្រព័ន្ធត្រូវបានធ្វើបច្ចុប្បន្នភាព និងផ្សព្វផ្សាយទូទាំងប្រព័ន្ធ IAM។'
              : 'Global policies updated and propagated across IAM clusters.'}
          </span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Authentication & Lockout Policies */}
        <div className="p-5 sm:p-6 border border-slate-200/80 dark:border-slate-800 rounded-2xl bg-slate-50/50 dark:bg-slate-800/40">
          <h3 className="text-sm font-bold text-slate-900 dark:text-white mb-4 flex items-center gap-2">
            <Shield size={18} className="text-indigo-600 dark:text-indigo-400" />
            {locale === 'km' ? 'ការផ្ទៀងផ្ទាត់ & ការពារការទាយពាក្យសម្ងាត់' : 'Authentication & Brute Force Lockout'}
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                {locale === 'km' ? 'ចំនួនលើកបរាជ័យអតិបរមា' : 'Max Consecutive Failed Logins'}
              </label>
              <input
                type="number"
                min={1}
                max={20}
                value={config.maxFailedAttempts}
                onChange={(e) => setConfig({ ...config, maxFailedAttempts: Number(e.target.value) })}
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 mt-1.5"
              />
              <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">
                {locale === 'km' ? 'លើសពីចំនួននេះ គណនីនឹងត្រូវចាក់សោដោយស្វ័យប្រវត្តិ។' : 'Exceeding this triggers automatic account lockout.'}
              </p>
            </div>
            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                {locale === 'km' ? 'រយៈពេលចាក់សោគណនី (នាទី)' : 'Account Lockout Duration (Minutes)'}
              </label>
              <input
                type="number"
                min={5}
                max={1440}
                value={config.lockoutDurationMinutes}
                onChange={(e) => setConfig({ ...config, lockoutDurationMinutes: Number(e.target.value) })}
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 mt-1.5"
              />
              <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">
                {locale === 'km' ? 'រយៈពេលរង់ចាំមុនពេលដោះសោដោយស្វ័យប្រវត្តិ។' : 'Cooldown duration before auto-unlocking.'}
              </p>
            </div>
            <div className="md:col-span-2">
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                {locale === 'km' ? 'គោលការណ៍ 2FA / MFA' : 'MFA / 2FA Policy'}
              </label>
              <select
                value={config.mfaEnforcement}
                onChange={(e) => setConfig({ ...config, mfaEnforcement: e.target.value })}
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 mt-1.5 cursor-pointer"
              >
                <option value="optional">{locale === 'km' ? 'ជម្រើសសម្រាប់អ្នកប្រើប្រាស់ទាំងអស់' : 'Optional for all users'}</option>
                <option value="required_for_admins">{locale === 'km' ? 'តម្រូវឱ្យមានសម្រាប់តែ Admin ប៉ុណ្ណោះ' : 'Required for Administrators only'}</option>
                <option value="required_for_all">{locale === 'km' ? 'កាតព្វកិច្ចសម្រាប់គ្រប់គណនីទាំងអស់' : 'Mandatory for all accounts'}</option>
              </select>
            </div>
          </div>
        </div>

        {/* Password Policy */}
        <div className="p-5 sm:p-6 border border-slate-200/80 dark:border-slate-800 rounded-2xl bg-slate-50/50 dark:bg-slate-800/40">
          <h3 className="text-sm font-bold text-slate-900 dark:text-white mb-4 flex items-center gap-2">
            <KeyRound size={18} className="text-purple-600 dark:text-purple-400" />
            {locale === 'km' ? 'ភាពស្មុគស្មាញ & ការផុតកំណត់ពាក្យសម្ងាត់' : 'Password Complexity & Expiration'}
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                {locale === 'km' ? 'ប្រវែងពាក្យសម្ងាត់អប្បបរមា' : 'Minimum Password Length'}
              </label>
              <input
                type="number"
                min={8}
                max={64}
                value={config.passwordMinLength}
                onChange={(e) => setConfig({ ...config, passwordMinLength: Number(e.target.value) })}
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 mt-1.5"
              />
            </div>
            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                {locale === 'km' ? 'រយៈពេលផុតកំណត់ពាក្យសម្ងាត់ (ថ្ងៃ)' : 'Password Expiration (Days)'}
              </label>
              <input
                type="number"
                min={0}
                max={365}
                value={config.passwordExpirationDays}
                onChange={(e) => setConfig({ ...config, passwordExpirationDays: Number(e.target.value) })}
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 mt-1.5"
              />
              <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">
                {locale === 'km' ? 'លេខ 0 គឺមិនកំណត់ពេលផុតកំណត់ពាក្យសម្ងាត់ឡើយ។' : '0 disables scheduled password expiry.'}
              </p>
            </div>
            <div className="flex flex-wrap items-center gap-4 md:col-span-2 pt-2">
              <label className="flex items-center gap-2 text-xs font-semibold text-slate-700 dark:text-slate-300 cursor-pointer">
                <input
                  type="checkbox"
                  checked={config.passwordRequireSpecial}
                  onChange={(e) => setConfig({ ...config, passwordRequireSpecial: e.target.checked })}
                  className="rounded text-indigo-600 focus:ring-indigo-500"
                />
                {locale === 'km' ? 'ទាមទារតួអក្សរពិសេស (!@#$%)' : 'Require Special Characters (!@#$%)'}
              </label>
              <label className="flex items-center gap-2 text-xs font-semibold text-slate-700 dark:text-slate-300 cursor-pointer">
                <input
                  type="checkbox"
                  checked={config.passwordRequireNumbers}
                  onChange={(e) => setConfig({ ...config, passwordRequireNumbers: e.target.checked })}
                  className="rounded text-indigo-600 focus:ring-indigo-500"
                />
                {locale === 'km' ? 'ទាមទារលេខ (0-9)' : 'Require Numeric Digits (0-9)'}
              </label>
            </div>
          </div>
        </div>

        {/* Sessions & Token Lifetimes */}
        <div className="p-5 sm:p-6 border border-slate-200/80 dark:border-slate-800 rounded-2xl bg-slate-50/50 dark:bg-slate-800/40">
          <h3 className="text-sm font-bold text-slate-900 dark:text-white mb-4 flex items-center gap-2">
            <Clock size={18} className="text-emerald-600 dark:text-emerald-400" />
            {locale === 'km' ? 'សម័យការងារ & អាយុកាល JWT Token' : 'Session Inactivity & JWT Token Lifetimes'}
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                {locale === 'km' ? 'រយៈពេលផុតកំណត់សម័យការងារ (នាទី)' : 'Session Timeout (Min)'}
              </label>
              <input
                type="number"
                value={config.sessionTimeoutMinutes}
                onChange={(e) => setConfig({ ...config, sessionTimeoutMinutes: Number(e.target.value) })}
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 mt-1.5"
              />
            </div>
            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                {locale === 'km' ? 'អាយុកាល Access Token (វិនាទី)' : 'Access Token TTL (Sec)'}
              </label>
              <input
                type="number"
                value={config.accessTokenTtlSeconds}
                onChange={(e) => setConfig({ ...config, accessTokenTtlSeconds: Number(e.target.value) })}
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 mt-1.5"
              />
            </div>
            <div>
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                {locale === 'km' ? 'អាយុកាល Refresh Token (វិនាទី)' : 'Refresh Token TTL (Sec)'}
              </label>
              <input
                type="number"
                value={config.refreshTokenTtlSeconds}
                onChange={(e) => setConfig({ ...config, refreshTokenTtlSeconds: Number(e.target.value) })}
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 mt-1.5"
              />
            </div>
          </div>
        </div>

        <button
          type="submit"
          className="flex items-center gap-2 px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold shadow-md shadow-indigo-600/20 transition cursor-pointer"
        >
          <Save size={16} /> {locale === 'km' ? 'រក្សាទុកការកំណត់ប្រព័ន្ធ' : 'Save System Configuration'}
        </button>
      </form>
    </div>
  );
}
