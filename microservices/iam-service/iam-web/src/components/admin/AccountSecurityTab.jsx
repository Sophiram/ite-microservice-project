import React, { useState, useEffect } from 'react';
import { ShieldCheck, Key, Lock, Unlock, AlertCircle, RefreshCw, CheckCircle2 } from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function AccountSecurityTab() {
  const { locale } = useLanguage();
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [statusMessage, setStatusMessage] = useState(null);

  useEffect(() => {
    adminService.getUsers().then((res) => setUsers(res.content || res));
  }, []);

  const handleAdminResetPassword = async (e) => {
    e.preventDefault();
    if (!selectedUser || !newPassword) return;
    await adminService.resetUserPassword(selectedUser, newPassword);
    setStatusMessage({ type: 'success', text: 'Password forcefully reset and saved.' });
    setNewPassword('');
  };

  const handleForceUnlock = async (uuid) => {
    await adminService.unlockUser(uuid);
    setUsers(users.map((u) => (u.uuid === uuid ? { ...u, locked: false } : u)));
    setStatusMessage({ type: 'success', text: 'Account has been unlocked successfully.' });
  };

  const lockedUsers = users.filter((u) => u.locked);

  return (
    <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-xs border border-slate-200/80 dark:border-slate-800 w-full transition-colors">
      <div className="flex items-center gap-3.5 pb-6 mb-6 border-b border-slate-100 dark:border-slate-800">
        <div className="w-12 h-12 rounded-2xl bg-indigo-50 dark:bg-slate-800 text-indigo-600 dark:text-indigo-400 flex items-center justify-center shrink-0">
          <ShieldCheck size={24} />
        </div>
        <div>
          <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {locale === 'km' ? 'ប្រតិបត្តិការសុវត្ថិភាពគណនី' : 'Account Security Operations'}
          </h2>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-0.5">
            {locale === 'km'
              ? 'កំណត់ពាក្យសម្ងាត់អ្នកប្រើប្រាស់ឡើងវិញ និងដោះសោគណនីបន្ទាន់'
              : 'Direct administrative overrides for compromised credentials and locked user accounts.'}
          </p>
        </div>
      </div>

      {statusMessage && (
        <div className={`p-4 mb-6 rounded-2xl text-xs sm:text-sm font-semibold flex items-center gap-2 ${
          statusMessage.type === 'success'
            ? 'bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800'
            : 'bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 border border-rose-200 dark:border-rose-800'
        }`}>
          <CheckCircle2 size={16} />
          <span>{statusMessage.text}</span>
        </div>
      )}

      {/* Force Password Reset */}
      <div className="p-5 sm:p-6 border border-slate-200/80 dark:border-slate-800 rounded-2xl mb-6 bg-slate-50/50 dark:bg-slate-800/40">
        <h3 className="text-sm font-bold text-slate-900 dark:text-white mb-1 flex items-center gap-2">
          <Key size={16} className="text-indigo-600 dark:text-indigo-400" />
          Administrative Password Override
        </h3>
        <p className="text-xs text-slate-500 dark:text-slate-400 mb-4">
          Reset any user's credential when locked out or upon verified IT helpdesk request.
        </p>

        <form onSubmit={handleAdminResetPassword} className="space-y-4">
          <div>
            <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">Target User</label>
            <select
              required
              value={selectedUser}
              onChange={(e) => setSelectedUser(e.target.value)}
              className="w-full px-3.5 py-2.5 text-xs bg-white dark:bg-slate-800 text-slate-900 dark:text-white border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">Select an account...</option>
              {users.map((u) => (
                <option key={u.uuid} value={u.uuid}>
                  @{u.username} ({u.email})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="text-xs font-bold text-slate-700 dark:text-slate-300 block mb-1">New Temporary / Permanent Password</label>
            <input
              required
              type="password"
              placeholder="Minimum 8 characters"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              className="w-full px-3.5 py-2.5 text-xs bg-white dark:bg-slate-800 text-slate-900 dark:text-white border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          <button
            type="submit"
            className="px-5 py-2.5 bg-indigo-900 hover:bg-indigo-950 text-white rounded-xl text-xs font-bold shadow-xs transition cursor-pointer"
          >
            Reset Password
          </button>
        </form>
      </div>

      {/* Currently Locked Accounts queue */}
      <div className="p-5 sm:p-6 border border-slate-200/80 dark:border-slate-800 rounded-2xl bg-white dark:bg-slate-800/20">
        <h3 className="text-sm font-bold text-slate-900 dark:text-white mb-1 flex items-center gap-2">
          <Lock size={16} className="text-amber-600 dark:text-amber-400" />
          Locked Accounts Awaiting IT Unlock
        </h3>
        <p className="text-xs text-slate-500 dark:text-slate-400 mb-4">
          Accounts locked due to exceeding maximum consecutive failed authentication attempts.
        </p>

        {lockedUsers.length === 0 ? (
          <div className="flex items-center gap-2 text-xs text-emerald-700 dark:text-emerald-300 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200/80 dark:border-emerald-800 p-4 rounded-xl font-medium">
            <CheckCircle2 size={16} /> No accounts are currently locked. System is clean.
          </div>
        ) : (
          <div className="space-y-2.5">
            {lockedUsers.map((u) => (
              <div key={u.uuid} className="flex items-center justify-between p-3.5 border border-slate-200/80 dark:border-slate-800 bg-white dark:bg-slate-800/60 rounded-xl shadow-2xs">
                <div>
                  <div className="font-semibold text-xs text-slate-900 dark:text-white">@{u.username}</div>
                  <div className="text-[11px] text-slate-400">{u.email}</div>
                </div>
                <button
                  type="button"
                  onClick={() => handleForceUnlock(u.uuid)}
                  className="flex items-center gap-1.5 px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl transition cursor-pointer"
                >
                  <Unlock size={14} /> Unlock Account
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
