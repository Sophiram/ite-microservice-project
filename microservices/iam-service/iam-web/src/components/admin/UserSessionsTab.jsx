import React, { useState, useEffect, useMemo } from 'react';
import { Laptop, Smartphone, Search, LogOut, ShieldAlert, UserCheck, ChevronLeft, ChevronRight, Activity } from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function UserSessionsTab() {
  const { locale } = useLanguage();
  const [sessions, setSessions] = useState([]);
  const [search, setSearch] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  useEffect(() => {
    adminService.getAllUserSessions().then((data) => {
      // Sort newest-first (by createdAt or id)
      const sorted = [...(data || [])].sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0));
      setSessions(sorted);
    });
  }, []);

  const handleTerminate = async (sessionId) => {
    await adminService.terminateUserSession(sessionId);
    setSessions(sessions.filter((s) => s.id !== sessionId));
  };

  const handleTerminateAllForUser = async (username) => {
    const confirmMsg = locale === 'km'
      ? `តើអ្នកប្រាកដជាចង់បញ្ចប់គ្រប់សម័យការងារទាំងអស់របស់ @${username} មែនទេ?`
      : `Revoke all active sessions for @${username}?`;
    if (window.confirm(confirmMsg)) {
      await adminService.terminateAllSessionsForUser(username);
      setSessions(sessions.filter((s) => s.username !== username));
    }
  };

  const filteredSessions = useMemo(() => {
    return sessions.filter(
      (s) =>
        (s.username || '').toLowerCase().includes(search.toLowerCase()) ||
        (s.name || '').toLowerCase().includes(search.toLowerCase()) ||
        (s.ip || '').includes(search) ||
        (s.device || '').toLowerCase().includes(search.toLowerCase()) ||
        (s.location || '').toLowerCase().includes(search.toLowerCase())
    );
  }, [sessions, search]);

  useEffect(() => {
    setCurrentPage(1);
  }, [search]);

  const totalPages = Math.max(1, Math.ceil(filteredSessions.length / itemsPerPage));
  const paginatedSessions = useMemo(() => {
    const start = (currentPage - 1) * itemsPerPage;
    return filteredSessions.slice(start, start + itemsPerPage);
  }, [filteredSessions, currentPage, itemsPerPage]);

  return (
    <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-xs border border-slate-200/80 dark:border-slate-800 transition-colors">
      <div className="flex flex-col md:flex-row justify-between md:items-center gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
        <div>
          <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {locale === 'km' ? 'សម័យការងារសកម្មទូទាំងប្រព័ន្ធ' : 'Live User Sessions & Token Tracker'}
          </h2>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            {locale === 'km'
              ? 'ត្រួតពិនិត្យ និងបញ្ចប់សម័យការងាររបស់អ្នកប្រើប្រាស់ទាំងអស់ក្នុងប្រព័ន្ធ (រៀបតាមពេលថ្មីបំផុតនៅលើ)'
              : 'Monitor real-time access tokens and force-terminate unauthorized or stale sessions (newest first).'}
          </p>
        </div>

        <div className="flex items-center gap-2">
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200/80">
            <Activity size={13} />
            {filteredSessions.length} {locale === 'km' ? 'សម័យសកម្ម' : 'Active Sessions'}
          </span>
        </div>
      </div>

      <div className="my-5 flex items-center gap-3">
        <div className="relative flex-1 sm:max-w-md">
          <Search size={15} className="absolute left-3.5 top-3 text-slate-400" />
          <input
            type="text"
            placeholder={locale === 'km' ? 'ស្វែងរកតាមគណនី, IP, ឬ ឧបករណ៍...' : 'Filter by username, IP, or device...'}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-9 pr-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
      </div>

      <div className="overflow-x-auto rounded-2xl border border-slate-200/80 dark:border-slate-800">
        <table className="w-full text-left text-xs sm:text-sm text-slate-600 dark:text-slate-300">
          <thead className="bg-slate-50/80 dark:bg-slate-800/80 text-slate-500 dark:text-slate-400 uppercase text-[11px] font-bold border-b border-slate-200/80 dark:border-slate-800">
            <tr>
              <th className="px-4 py-3.5">{locale === 'km' ? 'គណនី' : 'Account'}</th>
              <th className="px-4 py-3.5">{locale === 'km' ? 'ឧបករណ៍ / កម្មវិធី' : 'Device / Client'}</th>
              <th className="px-4 py-3.5">{locale === 'km' ? 'អាសយដ្ឋាន IP & ទីតាំង' : 'IP Address & Location'}</th>
              <th className="px-4 py-3.5">{locale === 'km' ? 'ពេលចាប់ផ្តើម / សកម្មភាព' : 'Started / Last Active'}</th>
              <th className="px-4 py-3.5 text-right">{locale === 'km' ? 'សកម្មភាពលុប' : 'Revocation'}</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60 bg-white dark:bg-slate-900">
            {paginatedSessions.length === 0 ? (
              <tr>
                <td colSpan="5" className="px-4 py-12 text-center text-xs text-slate-400">
                  {locale === 'km' ? 'មិនមានសម័យការងារសកម្មឡើយ។' : 'No active user sessions found.'}
                </td>
              </tr>
            ) : (
              paginatedSessions.map((sess) => (
                <tr key={sess.id} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/40 transition">
                  <td className="px-4 py-3.5 font-semibold text-slate-900 dark:text-white">
                    <div className="flex items-center gap-2">
                      <UserCheck size={16} className="text-indigo-600 dark:text-indigo-400" />
                      <div>
                        <div>@{sess.username}</div>
                        {sess.name && <div className="text-[11px] font-normal text-slate-400">{sess.name}</div>}
                      </div>
                    </div>
                  </td>
                  <td className="px-4 py-3.5 text-slate-700 dark:text-slate-300">
                    <div className="flex items-center gap-2">
                      {(sess.device || '').includes('iPhone') || (sess.device || '').includes('Mobile') || (sess.device || '').includes('Galaxy') ? (
                        <Smartphone size={16} className="text-indigo-500" />
                      ) : (
                        <Laptop size={16} className="text-indigo-500" />
                      )}
                      <div>
                        <div className="font-medium">{sess.device}</div>
                        <div className="text-[11px] text-slate-400">{sess.browser}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-4 py-3.5">
                    <span className="font-mono text-xs font-semibold text-indigo-600 dark:text-indigo-400">{sess.ip}</span>
                    <div className="text-[11px] text-slate-400">{sess.location}</div>
                  </td>
                  <td className="px-4 py-3.5 text-xs text-slate-500 dark:text-slate-400">
                    <div>Started: {sess.createdAt || 'Recent'}</div>
                    <div className="text-emerald-600 dark:text-emerald-400 font-medium">Active: {sess.lastActive}</div>
                  </td>
                  <td className="px-4 py-3.5 text-right">
                    <div className="flex justify-end gap-2">
                      <button
                        type="button"
                        onClick={() => handleTerminate(sess.id)}
                        className="text-xs px-2.5 py-1 text-rose-600 dark:text-rose-400 border border-rose-200 dark:border-rose-900/50 hover:bg-rose-50 dark:hover:bg-rose-950/30 rounded-xl transition cursor-pointer font-medium"
                      >
                        {locale === 'km' ? 'បញ្ចប់សម័យ' : 'Kill Session'}
                      </button>
                      <button
                        type="button"
                        onClick={() => handleTerminateAllForUser(sess.username)}
                        className="text-xs px-2.5 py-1 bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 hover:bg-rose-100 dark:hover:bg-rose-900/50 rounded-xl font-medium transition cursor-pointer"
                      >
                        {locale === 'km' ? 'បញ្ចប់ទាំងអស់' : 'Revoke All'}
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination Controls */}
      {totalPages > 1 && (
        <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mt-5 pt-4 border-t border-slate-100 dark:border-slate-800 text-xs text-slate-500 dark:text-slate-400">
          <div>
            {locale === 'km'
              ? `បង្ហាញពី ${(currentPage - 1) * itemsPerPage + 1} ដល់ ${Math.min(currentPage * itemsPerPage, filteredSessions.length)} នៃ ${filteredSessions.length} សម័យ`
              : `Showing ${(currentPage - 1) * itemsPerPage + 1} to ${Math.min(currentPage * itemsPerPage, filteredSessions.length)} of ${filteredSessions.length} sessions`}
          </div>
          <div className="flex items-center gap-1">
            <button
              onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
              disabled={currentPage === 1}
              className="p-1.5 rounded-lg border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition"
              title="Previous Page"
            >
              <ChevronLeft size={16} />
            </button>
            {Array.from({ length: totalPages }, (_, i) => i + 1).map((pageNum) => (
              <button
                key={pageNum}
                onClick={() => setCurrentPage(pageNum)}
                className={`w-8 h-8 rounded-lg font-medium text-xs transition cursor-pointer ${
                  currentPage === pageNum
                    ? 'bg-indigo-600 text-white font-bold shadow-xs'
                    : 'border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300'
                }`}
              >
                {pageNum}
              </button>
            ))}
            <button
              onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
              disabled={currentPage === totalPages}
              className="p-1.5 rounded-lg border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition"
              title="Next Page"
            >
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
