import React, { useState, useEffect, useMemo } from 'react';
import { History, ShieldAlert, CheckCircle2, XCircle, Search, Filter, ChevronLeft, ChevronRight, ShieldCheck } from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function UserLoginHistoryTab() {
  const { locale } = useLanguage();
  const [logs, setLogs] = useState([]);
  const [search, setSearch] = useState('');
  const [filterSuspicious, setFilterSuspicious] = useState('ALL');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;

  useEffect(() => {
    adminService.getSystemLoginLogs().then((data) => {
      // Sort newest first
      const sorted = [...(data || [])].sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp) || b.id - a.id);
      setLogs(sorted);
    });
  }, []);

  const filteredLogs = useMemo(() => {
    return logs.filter((log) => {
      const matches =
        (log.username || '').toLowerCase().includes(search.toLowerCase()) ||
        (log.ip || '').includes(search) ||
        (log.userAgent || '').toLowerCase().includes(search.toLowerCase()) ||
        (log.note || '').toLowerCase().includes(search.toLowerCase());

      if (filterSuspicious === 'SUSPICIOUS') return matches && log.suspicious;
      if (filterSuspicious === 'FAILED') return matches && log.status === 'FAILED';
      if (filterSuspicious === 'SUCCESS') return matches && log.status === 'SUCCESS';
      return matches;
    });
  }, [logs, search, filterSuspicious]);

  useEffect(() => {
    setCurrentPage(1);
  }, [search, filterSuspicious]);

  const totalPages = Math.max(1, Math.ceil(filteredLogs.length / itemsPerPage));
  const paginatedLogs = useMemo(() => {
    const start = (currentPage - 1) * itemsPerPage;
    return filteredLogs.slice(start, start + itemsPerPage);
  }, [filteredLogs, currentPage, itemsPerPage]);

  return (
    <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-xs border border-slate-200/80 dark:border-slate-800 transition-colors">
      <div className="flex flex-col md:flex-row justify-between md:items-center gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
        <div>
          <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {locale === 'km' ? 'តាមដានការចូលប្រព័ន្ធ & សកម្មភាពសង្ស័យ' : 'System Login Security & Anomaly Monitor'}
          </h2>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            {locale === 'km'
              ? 'ត្រួតពិនិត្យប្រវត្តិចូលជោគជ័យ បរាជ័យ និងការវាយប្រហារ Brute-force (រៀបតាមពេលថ្មីបំផុតនៅលើ)'
              : 'Audit authentication attempts, detect credential stuffing, and monitor IP threat logs (newest first).'}
          </p>
        </div>

        <div className="flex items-center gap-2">
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 border border-rose-200/80">
            <ShieldAlert size={13} />
            {filteredLogs.filter(l => l.suspicious).length} {locale === 'km' ? 'ការគំរាមកំហែង' : 'Threats Detected'}
          </span>
        </div>
      </div>

      <div className="my-5 flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1 sm:max-w-md">
          <Search size={15} className="absolute left-3.5 top-3 text-slate-400" />
          <input
            type="text"
            placeholder={locale === 'km' ? 'ស្វែងរកតាមឈ្មោះ, IP, ឬ Browser...' : 'Search by username, IP, or user-agent...'}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-9 pr-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <select
          value={filterSuspicious}
          onChange={(e) => setFilterSuspicious(e.target.value)}
          className="px-3.5 py-2 text-xs border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 text-slate-700 dark:text-slate-300"
        >
          <option value="ALL">{locale === 'km' ? 'គ្រប់ការចូលទាំងអស់' : 'All Login Attempts'}</option>
          <option value="SUSPICIOUS">{locale === 'km' ? '⚠️ សកម្មភាពសង្ស័យ' : '⚠️ Suspicious Activity Only'}</option>
          <option value="FAILED">{locale === 'km' ? 'ចូលបរាជ័យ' : 'Failed Attempts'}</option>
          <option value="SUCCESS">{locale === 'km' ? 'ចូលជោគជ័យ' : 'Successful Logins'}</option>
        </select>
      </div>

      <div className="overflow-x-auto rounded-2xl border border-slate-200/80 dark:border-slate-800">
        <table className="w-full text-left text-xs sm:text-sm text-slate-600 dark:text-slate-300">
          <thead className="bg-slate-50/80 dark:bg-slate-800/80 text-slate-500 dark:text-slate-400 uppercase text-[11px] font-bold border-b border-slate-200/80 dark:border-slate-800">
            <tr>
              <th className="px-4 py-3.5">{locale === 'km' ? 'កាលបរិច្ឆេទ & ម៉ោង' : 'Timestamp'}</th>
              <th className="px-4 py-3.5">{locale === 'km' ? 'គណនី' : 'Account'}</th>
              <th className="px-4 py-3.5">{locale === 'km' ? 'ស្ថានភាព' : 'Status'}</th>
              <th className="px-4 py-3.5">{locale === 'km' ? 'អាសយដ្ឋាន IP' : 'IP Address'}</th>
              <th className="px-4 py-3.5">{locale === 'km' ? 'ឧបករណ៍ / កម្មវិធីរុករក' : 'Client / User Agent'}</th>
              <th className="px-4 py-3.5">{locale === 'km' ? 'ការវាយតម្លៃសុវត្ថិភាព' : 'Threat Assessment'}</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60 bg-white dark:bg-slate-900">
            {paginatedLogs.length === 0 ? (
              <tr>
                <td colSpan="6" className="px-4 py-12 text-center text-xs text-slate-400">
                  {locale === 'km' ? 'មិនមានកំណត់ត្រាត្រូវនឹងលក្ខខណ្ឌស្វែងរកឡើយ។' : 'No login history records found.'}
                </td>
              </tr>
            ) : (
              paginatedLogs.map((log) => (
                <tr key={log.id} className={log.suspicious ? 'bg-rose-50/40 dark:bg-rose-950/20 hover:bg-rose-50/60' : 'hover:bg-slate-50/60 dark:hover:bg-slate-800/40 transition'}>
                  <td className="px-4 py-3.5 whitespace-nowrap text-xs font-mono text-slate-700 dark:text-slate-300">
                    {log.timestamp}
                  </td>
                  <td className="px-4 py-3.5 font-semibold text-slate-900 dark:text-white">
                    @{log.username}
                  </td>
                  <td className="px-4 py-3.5">
                    {log.status === 'SUCCESS' ? (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200/80 dark:border-emerald-800">
                        <CheckCircle2 size={12} /> {locale === 'km' ? 'ជោគជ័យ' : 'Success'}
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 border border-rose-200/80 dark:border-rose-800">
                        <XCircle size={12} /> {locale === 'km' ? 'បរាជ័យ' : 'Failed'}
                      </span>
                    )}
                  </td>
                  <td className="px-4 py-3.5 font-mono text-xs font-semibold text-indigo-600 dark:text-indigo-400">{log.ip}</td>
                  <td className="px-4 py-3.5 text-xs text-slate-500 dark:text-slate-400 max-w-xs truncate" title={log.userAgent}>
                    {log.userAgent}
                  </td>
                  <td className="px-4 py-3.5">
                    {log.suspicious ? (
                      <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-xl text-xs font-bold bg-rose-600 text-white shadow-xs">
                        <ShieldAlert size={14} /> {log.note || 'Suspicious Probe'}
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 text-xs text-emerald-600 dark:text-emerald-400 font-medium">
                        <ShieldCheck size={13} /> {locale === 'km' ? 'ប្រក្រតី' : 'Normal'}
                      </span>
                    )}
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
              ? `បង្ហាញពី ${(currentPage - 1) * itemsPerPage + 1} ដល់ ${Math.min(currentPage * itemsPerPage, filteredLogs.length)} នៃ ${filteredLogs.length} កំណត់ត្រា`
              : `Showing ${(currentPage - 1) * itemsPerPage + 1} to ${Math.min(currentPage * itemsPerPage, filteredLogs.length)} of ${filteredLogs.length} entries`}
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
