import React, { useState } from 'react';
import {
  X,
  Shield,
  Smartphone,
  Laptop,
  Key,
  Calendar,
  Clock,
  ChevronDown,
  Edit,
  Lock,
  User,
  History,
  Link2,
} from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';

export default function UserDetailModal({ user, onClose }) {
  const { locale } = useLanguage();
  const [activeTab, setActiveTab] = useState('profile');

  if (!user) return null;

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 overflow-y-auto">
      <div className="bg-white dark:bg-slate-900 rounded-3xl p-6 sm:p-8 max-w-4xl w-full shadow-2xl border border-slate-100 dark:border-slate-800 my-8">
        {/* Top Breadcrumb & Close */}
        <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800 text-xs text-slate-400 mb-6">
          <div className="flex items-center gap-1.5 font-medium">
            <span>Administration</span>
            <span>/</span>
            <span>Users</span>
            <span>/</span>
            <span className="text-slate-700 dark:text-slate-200 font-mono font-semibold">
              {user.username}
            </span>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600 cursor-pointer"
          >
            <X size={20} />
          </button>
        </div>

        {/* User Header matching Screenshot 6 */}
        <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 font-extrabold text-lg flex items-center justify-center border-2 border-slate-200 dark:border-slate-700 shrink-0">
              {user.name
                .split(' ')
                .map((n) => n[0])
                .join('')
                .slice(0, 2)
                .toUpperCase()}
            </div>
            <div>
              <div className="flex items-center gap-2 flex-wrap">
                <h2 className="text-xl font-extrabold text-slate-900 dark:text-white">
                  {user.name}
                </h2>
                <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-bold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                  Active
                </span>
                {user.mustChangePassword && (
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-50 text-amber-700 border border-amber-200">
                    Must change password
                  </span>
                )}
              </div>
              <div className="flex items-center gap-2 text-xs text-slate-500 font-mono mt-1">
                <span>@{user.username}</span>
                <span>•</span>
                <span>{user.email}</span>
              </div>
            </div>
          </div>

          <button className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl border border-slate-200 dark:border-slate-700 text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 transition cursor-pointer">
            <span>Manage</span>
            <ChevronDown size={14} />
          </button>
        </div>

        {/* 3 Summary Stats matching Screenshot 6 */}
        <div className="grid grid-cols-3 gap-4 py-5 border-b border-slate-100 dark:border-slate-800 text-xs">
          <div>
            <div className="text-slate-400 mb-1">Last sign-in</div>
            <div className="font-bold text-slate-700 dark:text-slate-200">—</div>
          </div>
          <div>
            <div className="text-slate-400 mb-1">Created</div>
            <div className="font-bold text-slate-700 dark:text-slate-200">
              {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : '—'}
            </div>
          </div>
          <div>
            <div className="text-slate-400 mb-1">Roles</div>
            <div className="font-bold text-slate-700 dark:text-slate-200">
              {Array.isArray(user.roles) && user.roles.length > 0
                ? `${user.roles.length} `
                : user.role ? '1 ' : '0 '}
              <span className="font-mono text-indigo-600">
                {Array.isArray(user.roles) && user.roles.length > 0
                  ? (typeof user.roles[0] === 'object' ? user.roles[0].name : user.roles[0])
                  : (user.role || 'USER')}
              </span>
            </div>
          </div>
        </div>

        {/* Tab Navigation matching Screenshot 6 & 7 */}
        <div className="flex items-center gap-6 border-b border-slate-100 dark:border-slate-800 text-xs font-semibold pt-4 mb-6">
          <button
            onClick={() => setActiveTab('profile')}
            className={`pb-3 cursor-pointer transition border-b-2 ${
              activeTab === 'profile'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            Profile
          </button>
          <button
            onClick={() => setActiveTab('roles')}
            className={`pb-3 cursor-pointer transition border-b-2 ${
              activeTab === 'roles'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            Roles & permissions
          </button>
          <button
            onClick={() => setActiveTab('security')}
            className={`pb-3 cursor-pointer transition border-b-2 ${
              activeTab === 'security'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            Security
          </button>
          <button
            onClick={() => setActiveTab('linked')}
            className={`pb-3 cursor-pointer transition border-b-2 ${
              activeTab === 'linked'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            Linked accounts
          </button>
        </div>

        {/* Tab 1: Profile View matching Screenshot 6 */}
        {activeTab === 'profile' && (
          <div className="space-y-6 text-xs">
            <div className="flex items-center justify-between">
              <div>
                <h4 className="font-bold text-sm text-slate-900 dark:text-white">Profile</h4>
                <p className="text-slate-400">OpenID Connect profile shared with applications.</p>
              </div>
              <button className="px-3 py-1.5 rounded-xl border border-slate-200 text-xs font-semibold hover:bg-slate-50 cursor-pointer">
                Edit
              </button>
            </div>

            <div className="grid grid-cols-2 gap-y-4 gap-x-8">
              <div>
                <div className="text-slate-400 mb-0.5">First name</div>
                <div className="font-semibold text-slate-800 dark:text-slate-200">
                  {user.givenName || user.name?.split(' ')?.[0] || '—'}
                </div>
              </div>

              <div>
                <div className="text-slate-400 mb-0.5">Last name</div>
                <div className="font-semibold text-slate-800 dark:text-slate-200">
                  {user.familyName || user.name?.split(' ')?.[1] || '—'}
                </div>
              </div>

              <div>
                <div className="text-slate-400 mb-0.5">Middle name</div>
                <div className="text-slate-400">—</div>
              </div>

              <div>
                <div className="text-slate-400 mb-0.5">Nickname</div>
                <div className="text-slate-400">—</div>
              </div>

              <div>
                <div className="text-slate-400 mb-0.5">Email</div>
                <div className="font-semibold text-slate-800 dark:text-slate-200 font-mono">
                  {user.email || '—'}
                </div>
              </div>

              <div>
                <div className="text-slate-400 mb-0.5">Phone number</div>
                <div className="font-semibold text-slate-800 dark:text-slate-200">
                  {user.phoneNumber || '—'}
                </div>
              </div>

              <div>
                <div className="text-slate-400 mb-0.5">Gender</div>
                <div className="font-semibold text-slate-800 dark:text-slate-200">
                  {user.gender || '—'}
                </div>
              </div>

              <div>
                <div className="text-slate-400 mb-0.5">Date of birth</div>
                <div className="font-semibold text-slate-800 dark:text-slate-200">
                  {user.birthdate || '—'}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Tab 2: Roles & Permissions */}
        {activeTab === 'roles' && (
          <div className="space-y-4 text-xs">
            <h4 className="font-bold text-sm text-slate-900 dark:text-white">Assigned Roles</h4>
            {(Array.isArray(user.roles) && user.roles.length > 0 ? user.roles : (user.role ? [{ name: user.role, description: 'Assigned system role' }] : [])).map((r, i) => (
              <div key={i} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-700/60 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Shield size={18} className="text-indigo-600" />
                  <div>
                    <div className="font-bold font-mono text-slate-800 dark:text-slate-200">
                      {typeof r === 'object' ? r.name : r}
                    </div>
                    <div className="text-slate-400 text-[11px]">{r.description || 'System role'}</div>
                  </div>
                </div>
                <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                  Active
                </span>
              </div>
            ))}
          </div>
        )}

        {/* Tab 3: Security View matching Screenshot 7 */}
        {activeTab === 'security' && (
          <div className="space-y-6 text-xs">
            {/* 2-step verification */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
              <div>
                <h5 className="font-bold text-slate-800 dark:text-slate-200">2-step verification</h5>
                <p className="text-slate-400 text-[11px] mt-0.5">
                  Reset it when the user lost their phone and recovery codes.
                </p>
              </div>
              <div className="md:col-span-2">
                <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-700/60 flex items-center gap-3">
                  <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-slate-200 text-slate-700">
                    Off
                  </span>
                  <span className="text-slate-500">
                    The user signs in with a password only.
                  </span>
                </div>
              </div>
            </div>

            {/* Signed-in devices */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
              <div>
                <h5 className="font-bold text-slate-800 dark:text-slate-200">Signed-in devices</h5>
                <p className="text-slate-400 text-[11px] mt-0.5">
                  Browsers where the user is signed in now.
                </p>
              </div>
              <div className="md:col-span-2 text-center py-6 text-slate-400 border border-dashed rounded-2xl">
                <Laptop size={24} className="mx-auto mb-1 opacity-50" />
                <span>No signed-in devices.</span>
              </div>
            </div>

            {/* Sign-in attempts */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <h5 className="font-bold text-slate-800 dark:text-slate-200">Sign-in attempts</h5>
                <p className="text-slate-400 text-[11px] mt-0.5">
                  Successful and failed, newest first.
                </p>
              </div>
              <div className="md:col-span-2 text-slate-400 text-center py-4">
                No recent sign-in attempts recorded.
              </div>
            </div>
          </div>
        )}

        {/* Tab 4: Linked Accounts */}
        {activeTab === 'linked' && (
          <div className="text-xs py-6 text-center text-slate-400">
            No external social accounts linked for this user.
          </div>
        )}
      </div>
    </div>
  );
}
