import React, { useState, useEffect } from 'react';
import { Search, User, Filter, Eye, Globe, Shield, Clock, X, CheckCircle, AlertTriangle, ChevronLeft, ChevronRight } from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function AuditLogView() {
  const { locale } = useLanguage();
  const [logs, setLogs] = useState([]);
  const [search, setSearch] = useState('');
  const [actionFilter, setActionFilter] = useState('ALL');
  const [selectedLog, setSelectedLog] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 8;

  useEffect(() => {
    adminService.getAuditLogs().then(setLogs);
  }, []);

  const sortedLogs = [...logs].sort((a, b) => {
    const timeA = a.timestamp ? new Date(a.timestamp).getTime() : 0;
    const timeB = b.timestamp ? new Date(b.timestamp).getTime() : 0;
    if (timeA !== timeB) return timeB - timeA;
    return (b.id || '').localeCompare(a.id || '');
  });

  const filteredLogs = sortedLogs.filter((log) => {
    const matchSearch =
      (log.changed || '').toLowerCase().includes(search.toLowerCase()) ||
      (log.by || '').toLowerCase().includes(search.toLowerCase()) ||
      (log.action || '').toLowerCase().includes(search.toLowerCase()) ||
      (log.ip || '').toLowerCase().includes(search.toLowerCase());
    const matchAction =
      actionFilter === 'ALL' || (log.action || '').toLowerCase().includes(actionFilter.toLowerCase());
    return matchSearch && matchAction;
  });

  const totalPages = Math.max(1, Math.ceil(filteredLogs.length / pageSize));
  const paginatedLogs = filteredLogs.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  const getActionBadgeClass = (action = '') => {
    const act = action.toUpperCase();
    if (act.includes('ROLE')) {
      return 'bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border-emerald-200 dark:border-emerald-800';
    }
    if (act.includes('USER') || act.includes('CREATE_USER')) {
      return 'bg-indigo-50 dark:bg-indigo-950/40 text-indigo-700 dark:text-indigo-300 border-indigo-200 dark:border-indigo-800';
    }
    if (act.includes('LOCK')) {
      return 'bg-amber-50 dark:bg-amber-950/40 text-amber-700 dark:text-amber-300 border-amber-200 dark:border-amber-800';
    }
    if (act.includes('CLIENT')) {
      return 'bg-cyan-50 dark:bg-cyan-950/40 text-cyan-700 dark:text-cyan-300 border-cyan-200 dark:border-cyan-800';
    }
    if (act.includes('PERMISSION')) {
      return 'bg-purple-50 dark:bg-purple-950/40 text-purple-700 dark:text-purple-300 border-purple-200 dark:border-purple-800';
    }
    if (act.includes('PASSWORD')) {
      return 'bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 border-rose-200 dark:border-rose-800';
    }
    if (act.includes('EMAIL') || act.includes('VERIFIED')) {
      return 'bg-blue-50 dark:bg-blue-950/40 text-blue-700 dark:text-blue-300 border-blue-200 dark:border-blue-800';
    }
    return 'bg-slate-50 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border-slate-200 dark:border-slate-700';
  };

  return (
    <div className="space-y-6 w-full">
      {/* 1. Page Header matching Screenshot */}
      <div>
        <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
          {locale === 'km' ? 'កំណត់ហេតុសវនកម្ម' : 'Audit log'}
        </h1>
        <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
          {locale === 'km'
            ? 'រាល់ការផ្លាស់ប្តូរនៅក្នុង IAM៖ អ្នកណាធ្វើ ធ្វើលើអ្វី និងនៅពេលណា។'
            : 'Every change made in IAM: who did it, to what, and when.'}
        </p>
      </div>

      {/* 2. Search & Filters Bar matching Screenshot */}
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
        <div className="relative flex-1 sm:max-w-md">
          <Search size={15} className="absolute left-3.5 top-3 text-slate-400" />
          <input
            type="text"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setCurrentPage(1);
            }}
            placeholder={locale === 'km' ? 'ស្វែងរកតាមបុគ្គល ឬឈ្មោះ...' : 'Search by person or name...'}
            className="w-full pl-9 pr-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-xs outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <div className="flex items-center gap-2 flex-wrap">
          <select
            value={actionFilter}
            onChange={(e) => {
              setActionFilter(e.target.value);
              setCurrentPage(1);
            }}
            className="px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-xs text-slate-700 dark:text-slate-300 outline-none"
          >
            <option value="ALL">{locale === 'km' ? 'សកម្មភាពទាំងអស់' : 'All actions'}</option>
            <option value="ASSIGN_ROLE">ASSIGN_ROLE ({locale === 'km' ? 'កំណត់តួនាទី' : 'Roles assigned'})</option>
            <option value="LOCK_USER">LOCK_USER ({locale === 'km' ? 'ចាក់សោគណនី' : 'Lock Account'})</option>
            <option value="CREATE_CLIENT">CREATE_CLIENT ({locale === 'km' ? 'បង្កើត Client' : 'Create Client'})</option>
            <option value="CHANGE_PASSWORD">CHANGE_PASSWORD ({locale === 'km' ? 'ប្តូរពាក្យសម្ងាត់' : 'Change Password'})</option>
            <option value="CREATE_PERMISSION">CREATE_PERMISSION ({locale === 'km' ? 'បង្កើតសិទ្ធិ' : 'Create Permission'})</option>
            <option value="USER_CREATED">USER_CREATED ({locale === 'km' ? 'បង្កើតអ្នកប្រើប្រាស់' : 'User created'})</option>
            <option value="EMAIL_VERIFIED">EMAIL_VERIFIED ({locale === 'km' ? 'បានផ្ទៀងផ្ទាត់អ៊ីមែល' : 'Email verified'})</option>
          </select>

          <select className="px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-xs text-slate-700 dark:text-slate-300 outline-none">
            <option>{locale === 'km' ? 'ប្រភេទទាំងអស់' : 'All types'}</option>
          </select>

          <select className="px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-xs text-slate-700 dark:text-slate-300 outline-none">
            <option>{locale === 'km' ? 'គ្រប់ពេល' : 'Any time'}</option>
            <option>{locale === 'km' ? 'ថ្ងៃនេះ' : 'Today'}</option>
            <option>{locale === 'km' ? '៧ ថ្ងៃមុន' : 'Past 7 days'}</option>
            <option>{locale === 'km' ? '៣០ ថ្ងៃមុន' : 'Past 30 days'}</option>
          </select>
        </div>
      </div>

      {/* 3. Audit Log Table matching Screenshot */}
      <div className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-200/80 dark:border-slate-800 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-slate-100 dark:border-slate-800 text-slate-400 font-semibold">
                <th className="py-3 px-6">{locale === 'km' ? 'ពេលវេលា' : 'When'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'សកម្មភាព' : 'Action'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'ការផ្លាស់ប្តូរ' : 'Changed'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'ដោយ' : 'By'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'អាសយដ្ឋាន IP' : 'Client IP'}</th>
                <th className="py-3 px-6 text-right">{locale === 'km' ? 'សកម្មភាព' : 'Details'}</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-slate-600 dark:text-slate-300">
              {filteredLogs.length === 0 ? (
                <tr>
                  <td colSpan="6" className="py-8 text-center text-xs text-slate-400">
                    {locale === 'km' ? 'មិនមានកំណត់ត្រាសវនកម្មឡើយ។' : 'No audit log records recorded.'}
                  </td>
                </tr>
              ) : (
                paginatedLogs.map((log) => (
                  <tr
                    key={log.id}
                    onClick={() => setSelectedLog(log)}
                    className="hover:bg-slate-50/70 dark:hover:bg-slate-800/40 transition cursor-pointer"
                  >
                    {/* When */}
                    <td className="py-4 px-6">
                      <div className="font-semibold text-slate-800 dark:text-slate-200">
                        {log.when}
                      </div>
                      <div className="text-[11px] text-slate-400 font-mono">
                        {log.timestamp}
                      </div>
                    </td>

                    {/* Action Pill */}
                    <td className="py-4 px-4">
                      <span className={`px-2.5 py-0.5 rounded-full text-[11px] font-semibold border ${getActionBadgeClass(log.action)}`}>
                        {log.action}
                      </span>
                    </td>

                    {/* Changed Target */}
                    <td className="py-4 px-4">
                      <div className="flex items-center gap-2 font-mono text-xs text-slate-800 dark:text-slate-200">
                        <User size={13} className="text-slate-400" />
                        <span>{log.changed}</span>
                      </div>
                    </td>

                    {/* By Actor */}
                    <td className="py-4 px-4 font-mono text-slate-500 dark:text-slate-400 text-xs">
                      {log.by}
                    </td>

                    {/* Client IP */}
                    <td className="py-4 px-4 font-mono text-slate-500 dark:text-slate-400 text-xs">
                      <div className="flex items-center gap-1.5">
                        <Globe size={12} className="text-slate-400" />
                        <span>{log.ip || '127.0.0.1'}</span>
                      </div>
                    </td>

                    {/* Inspect Button */}
                    <td className="py-4 px-6 text-right">
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          setSelectedLog(log);
                        }}
                        className="p-1.5 text-slate-400 hover:text-indigo-600 dark:hover:text-indigo-400 hover:bg-indigo-50 dark:hover:bg-indigo-950/40 rounded-lg transition"
                        title={locale === 'km' ? 'មើលព័ត៌មានលម្អិត' : 'View Details'}
                      >
                        <Eye size={15} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>

          {/* Pagination Controls */}
          {filteredLogs.length > pageSize && (
            <div className="flex flex-col sm:flex-row items-center justify-between gap-3 px-6 py-3.5 border-t border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-900/50">
              <div className="text-xs text-slate-500 dark:text-slate-400">
                {locale === 'km' ? (
                  <>
                    បង្ហាញ <span className="font-semibold text-slate-700 dark:text-slate-200">{(currentPage - 1) * pageSize + 1}</span> ដល់{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{Math.min(currentPage * pageSize, filteredLogs.length)}</span> នៃ{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{filteredLogs.length}</span> កំណត់ត្រា
                  </>
                ) : (
                  <>
                    Showing <span className="font-semibold text-slate-700 dark:text-slate-200">{(currentPage - 1) * pageSize + 1}</span> to{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{Math.min(currentPage * pageSize, filteredLogs.length)}</span> of{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{filteredLogs.length}</span> records
                  </>
                )}
              </div>

              <div className="flex items-center gap-1.5">
                <button
                  type="button"
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                  className="p-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-300 disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-50 dark:hover:bg-slate-700 transition"
                  title="Previous page"
                >
                  <ChevronLeft size={16} />
                </button>

                {Array.from({ length: totalPages }, (_, i) => i + 1).map((pg) => (
                  <button
                    key={pg}
                    type="button"
                    onClick={() => setCurrentPage(pg)}
                    className={`min-w-8 h-8 rounded-xl text-xs font-bold transition ${
                      currentPage === pg
                        ? 'bg-indigo-600 text-white shadow-xs'
                        : 'border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700'
                    }`}
                  >
                    {pg}
                  </button>
                ))}

                <button
                  type="button"
                  disabled={currentPage === totalPages}
                  onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                  className="p-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-300 disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-50 dark:hover:bg-slate-700 transition"
                  title="Next page"
                >
                  <ChevronRight size={16} />
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* 4. Details Inspection Modal (Task 2.11) */}
      {selectedLog && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-3xl max-w-lg w-full p-6 shadow-2xl border border-slate-100 dark:border-slate-800 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between pb-4 border-b border-slate-100 dark:border-slate-800">
              <div className="flex items-center gap-2.5">
                <div className="w-9 h-9 rounded-xl bg-indigo-50 dark:bg-indigo-950/40 flex items-center justify-center text-indigo-600 dark:text-indigo-400">
                  <Shield size={18} />
                </div>
                <div>
                  <h3 className="text-base font-bold text-slate-900 dark:text-white">
                    {locale === 'km' ? 'ព័ត៌មានលម្អិតសវនកម្ម' : 'Audit Event Details'}
                  </h3>
                  <p className="text-xs text-slate-400 font-mono">{selectedLog.id}</p>
                </div>
              </div>
              <button
                onClick={() => setSelectedLog(null)}
                className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 p-1.5 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 transition"
              >
                <X size={18} />
              </button>
            </div>

            <div className="py-4 space-y-4 text-xs">
              <div className="flex items-center justify-between p-3 rounded-2xl bg-slate-50 dark:bg-slate-800/50">
                <span className="text-slate-500">{locale === 'km' ? 'សកម្មភាព (Action):' : 'Action:'}</span>
                <span className={`px-2.5 py-0.5 rounded-full font-semibold border ${getActionBadgeClass(selectedLog.action)}`}>
                  {selectedLog.action}
                </span>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="p-3 rounded-2xl border border-slate-100 dark:border-slate-800">
                  <span className="text-slate-400 block mb-1">{locale === 'km' ? 'ធ្វើឡើងដោយ (Actor)' : 'Performed By'}</span>
                  <span className="font-mono font-medium text-slate-800 dark:text-slate-200 break-all">{selectedLog.by}</span>
                </div>
                <div className="p-3 rounded-2xl border border-slate-100 dark:border-slate-800">
                  <span className="text-slate-400 block mb-1">{locale === 'km' ? 'គោលដៅ (Target)' : 'Target / Resource'}</span>
                  <span className="font-mono font-medium text-slate-800 dark:text-slate-200 break-all">{selectedLog.changed}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="p-3 rounded-2xl border border-slate-100 dark:border-slate-800">
                  <span className="text-slate-400 block mb-1">{locale === 'km' ? 'អាសយដ្ឋាន IP (Client IP)' : 'Client IP'}</span>
                  <span className="font-mono font-medium text-slate-800 dark:text-slate-200">{selectedLog.ip || '127.0.0.1'}</span>
                </div>
                <div className="p-3 rounded-2xl border border-slate-100 dark:border-slate-800">
                  <span className="text-slate-400 block mb-1">{locale === 'km' ? 'កាលបរិច្ឆេទ (Timestamp)' : 'Timestamp'}</span>
                  <span className="font-mono font-medium text-slate-800 dark:text-slate-200">{selectedLog.timestamp}</span>
                </div>
              </div>

              <div className="p-3 rounded-2xl border border-slate-100 dark:border-slate-800">
                <span className="text-slate-400 block mb-1">{locale === 'km' ? 'ការពិពណ៌នាសកម្មភាព (Description)' : 'Event Description'}</span>
                <p className="text-slate-700 dark:text-slate-300 leading-relaxed font-sans">
                  {selectedLog.details || `${selectedLog.action} performed on ${selectedLog.changed} by ${selectedLog.by}`}
                </p>
              </div>
            </div>

            <div className="pt-3 border-t border-slate-100 dark:border-slate-800 flex justify-end">
              <button
                onClick={() => setSelectedLog(null)}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-medium text-xs transition"
              >
                {locale === 'km' ? 'បិទ' : 'Close'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
