import React, { useState, useEffect } from 'react';
import {
  Users,
  Shield,
  Key,
  Mail,
  UserPlus,
  ArrowRight,
  User,
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';
import UserDetailModal from './UserDetailModal';

export default function AdminOverviewTab({ onNavigateTab }) {
  const { locale } = useLanguage();
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [counts, setCounts] = useState({ users: 1, roles: 0, permissions: 0, pending: 0 });

  useEffect(() => {
    adminService.getUsers(0, 10).then((data) => {
      setUsers(data?.content || []);
      setCounts((prev) => ({ ...prev, users: data?.totalElements || data?.content?.length || 1 }));
    });
    adminService.getRoles().then((data) => {
      setCounts((prev) => ({ ...prev, roles: Array.isArray(data) ? data.length : 0 }));
    });
    adminService.getPermissions().then((data) => {
      setCounts((prev) => ({ ...prev, permissions: Array.isArray(data) ? data.length : 0 }));
    });
  }, []);

  return (
    <div className="space-y-6 w-full">
      {/* 1. Header matching Screenshot 5 */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            Admin overview
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Manage who can sign in to ISTAD applications and what they can do.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => onNavigateTab && onNavigateTab('invitations')}
            className="inline-flex items-center gap-2 px-3.5 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-700 dark:text-slate-200 text-xs font-semibold shadow-xs hover:bg-slate-50 transition cursor-pointer"
          >
            <Mail size={14} className="text-slate-400" />
            <span>Invite</span>
          </button>
          <button
            onClick={() => onNavigateTab && onNavigateTab('users')}
            className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-indigo-900 hover:bg-indigo-950 text-white text-xs font-bold shadow-xs transition cursor-pointer"
          >
            <UserPlus size={14} />
            <span>Create user</span>
          </button>
        </div>
      </div>

      {/* 2. 4 Metric Cards matching Screenshot 5 */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Users Card */}
        <div className="bg-white dark:bg-slate-900 p-5 rounded-2xl border border-slate-200/80 dark:border-slate-800 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400 mb-2">
            <Users size={15} />
            <span>Users</span>
          </div>
          <div className="text-3xl font-extrabold text-slate-900 dark:text-white">
            {counts.users}
          </div>
          <div className="text-[11px] text-slate-400 mt-1">
            All accounts
          </div>
        </div>

        {/* Active Roles Card */}
        <div className="bg-white dark:bg-slate-900 p-5 rounded-2xl border border-slate-200/80 dark:border-slate-800 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400 mb-2">
            <Shield size={15} />
            <span>Active roles</span>
          </div>
          <div className="text-3xl font-extrabold text-slate-900 dark:text-white">
            {counts.roles}
          </div>
          <div className="text-[11px] text-slate-400 mt-1">
            Groups of permissions
          </div>
        </div>

        {/* Active Permissions Card */}
        <div className="bg-white dark:bg-slate-900 p-5 rounded-2xl border border-slate-200/80 dark:border-slate-800 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400 mb-2">
            <Key size={15} />
            <span>Active permissions</span>
          </div>
          <div className="text-3xl font-extrabold text-slate-900 dark:text-white">
            {counts.permissions}
          </div>
          <div className="text-[11px] text-slate-400 mt-1">
            service:resource:action
          </div>
        </div>

        {/* Pending Invitations Card */}
        <div className="bg-white dark:bg-slate-900 p-5 rounded-2xl border border-slate-200/80 dark:border-slate-800 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400 mb-2">
            <Mail size={15} />
            <span>Pending invitations</span>
          </div>
          <div className="text-3xl font-extrabold text-slate-900 dark:text-white">
            {counts.pending}
          </div>
          <div className="text-[11px] text-slate-400 mt-1">
            Waiting to be accepted
          </div>
        </div>
      </div>

      {/* 3. Newest Users & Pending Invitations matching Screenshot 5 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Newest Users Card */}
        <div className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-200/80 dark:border-slate-800 p-6 shadow-xs">
          <div className="flex items-center justify-between pb-4 border-b border-slate-100 dark:border-slate-800 mb-4">
            <h3 className="font-bold text-sm text-slate-900 dark:text-white">
              Newest users
            </h3>
            <button
              onClick={() => onNavigateTab && onNavigateTab('users')}
              className="inline-flex items-center gap-1 text-xs font-semibold text-slate-500 hover:text-indigo-600 transition cursor-pointer"
            >
              <span>View all</span>
              <ArrowRight size={13} />
            </button>
          </div>

          <div className="divide-y divide-slate-100 dark:divide-slate-800">
            {users.map((u) => (
              <div
                key={u.uuid}
                onClick={() => setSelectedUser(u)}
                className="py-3 flex items-center justify-between hover:bg-slate-50/70 dark:hover:bg-slate-800/40 px-2 rounded-xl transition cursor-pointer"
              >
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 font-bold text-xs flex items-center justify-center shrink-0">
                    {u.name
                      .split(' ')
                      .map((n) => n[0])
                      .join('')
                      .slice(0, 2)
                      .toUpperCase()}
                  </div>
                  <div>
                    <div className="font-bold text-xs text-slate-800 dark:text-slate-200">
                      {u.name}
                    </div>
                    <div className="font-mono text-[11px] text-slate-400">
                      {u.email}
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-3 text-xs">
                  <span className="text-slate-400 text-[11px]">13 Nov 2026</span>
                  <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                    Active
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Pending Invitations Card */}
        <div className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-200/80 dark:border-slate-800 p-6 shadow-xs flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between pb-4 border-b border-slate-100 dark:border-slate-800 mb-4">
              <h3 className="font-bold text-sm text-slate-900 dark:text-white">
                Pending invitations
              </h3>
              <button
                onClick={() => onNavigateTab && onNavigateTab('invitations')}
                className="inline-flex items-center gap-1 text-xs font-semibold text-slate-500 hover:text-indigo-600 transition cursor-pointer"
              >
                <span>View all</span>
                <ArrowRight size={13} />
              </button>
            </div>

            <div className="py-20 text-center text-xs text-slate-400">
              No pending invitations.
            </div>
          </div>
        </div>
      </div>

      {/* User Detail Modal */}
      {selectedUser && (
        <UserDetailModal user={selectedUser} onClose={() => setSelectedUser(null)} />
      )}
    </div>
  );
}
