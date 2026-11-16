import React, { useState, useEffect, useMemo } from 'react';
import { History, CheckCircle2, XCircle, Search, ChevronLeft, ChevronRight, Globe, Shield } from 'lucide-react';
import { accountService } from '../../services/accountService';
import { useLanguage } from '../../context/LanguageContext';

export default function LoginHistoryTab() {
    const { t, locale } = useLanguage();
    const [history, setHistory] = useState([]);
    const [search, setSearch] = useState('');
    const [statusFilter, setStatusFilter] = useState('ALL');
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 5;

    useEffect(() => {
        accountService.getLoginHistory().then((data) => {
            // Ensure sorted newest-first (descending by timestamp/id)
            const sorted = [...(data || [])].sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp) || b.id - a.id);
            setHistory(sorted);
        });
    }, []);

    // Filter and sort newest first
    const filteredHistory = useMemo(() => {
        return history.filter((item) => {
            const matchesSearch =
                (item.ip || '').toLowerCase().includes(search.toLowerCase()) ||
                (item.device || '').toLowerCase().includes(search.toLowerCase()) ||
                (item.location || '').toLowerCase().includes(search.toLowerCase()) ||
                (item.timestamp || '').toLowerCase().includes(search.toLowerCase());

            if (statusFilter === 'SUCCESS') return matchesSearch && item.status === 'SUCCESS';
            if (statusFilter === 'FAILED') return matchesSearch && item.status === 'FAILED';
            return matchesSearch;
        });
    }, [history, search, statusFilter]);

    // Reset pagination when search or filter changes
    useEffect(() => {
        setCurrentPage(1);
    }, [search, statusFilter]);

    const totalPages = Math.max(1, Math.ceil(filteredHistory.length / itemsPerPage));
    const paginatedHistory = useMemo(() => {
        const start = (currentPage - 1) * itemsPerPage;
        return filteredHistory.slice(start, start + itemsPerPage);
    }, [filteredHistory, currentPage, itemsPerPage]);

    return (
        <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-xs border border-slate-200/80 dark:border-slate-800 w-full transition-colors">
            {/* Header */}
            <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
                <div className="flex items-center gap-3.5">
                    <div className="w-12 h-12 rounded-2xl bg-indigo-50 dark:bg-slate-800 text-indigo-600 dark:text-indigo-400 flex items-center justify-center shrink-0">
                        <History size={22} />
                    </div>
                    <div>
                        <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
                            {locale === 'km' ? 'ប្រវត្តិនៃការចូលប្រព័ន្ធ' : 'Login History'}
                        </h2>
                        <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400">
                            {locale === 'km'
                                ? 'ពិនិត្យមើលព្រឹត្តិការណ៍ចូលប្រព័ន្ធជោគជ័យ និងបរាជ័យនាពេលថ្មីៗតាមឧបករណ៍របស់អ្នក (រៀបតាមពេលថ្មីបំផុតនៅលើ)'
                                : 'Review recent successful and failed sign-in events across your devices (sorted newest first).'}
                        </p>
                    </div>
                </div>

                <div className="flex items-center gap-2">
                    <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-indigo-50 dark:bg-indigo-950/50 text-indigo-700 dark:text-indigo-300 border border-indigo-200/80 dark:border-indigo-800/60">
                        <Shield size={13} />
                        {locale === 'km' ? `សរុប ${filteredHistory.length} កំណត់ត្រា` : `${filteredHistory.length} Total Events`}
                    </span>
                </div>
            </div>

            {/* Search and Filters */}
            <div className="my-5 flex flex-col sm:flex-row gap-3">
                <div className="relative flex-1">
                    <Search size={15} className="absolute left-3.5 top-3 text-slate-400" />
                    <input
                        type="text"
                        placeholder={locale === 'km' ? 'ស្វែងរកតាម IP, ឧបករណ៍, ឬទីតាំង...' : 'Search by IP, device, or location...'}
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full pl-9 pr-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-indigo-500"
                    />
                </div>
                <select
                    value={statusFilter}
                    onChange={(e) => setStatusFilter(e.target.value)}
                    className="px-3.5 py-2 text-xs border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 text-slate-700 dark:text-slate-300"
                >
                    <option value="ALL">{locale === 'km' ? 'គ្រប់ស្ថានភាពទាំងអស់' : 'All Statuses'}</option>
                    <option value="SUCCESS">{locale === 'km' ? 'ជោគជ័យ' : 'Success Only'}</option>
                    <option value="FAILED">{locale === 'km' ? 'បរាជ័យ' : 'Failed Only'}</option>
                </select>
            </div>

            {/* Table */}
            <div className="overflow-x-auto rounded-2xl border border-slate-200/80 dark:border-slate-800">
                <table className="w-full text-left text-xs sm:text-sm text-slate-600 dark:text-slate-300">
                    <thead className="bg-slate-50/80 dark:bg-slate-800/80 text-slate-500 dark:text-slate-400 uppercase text-[11px] font-bold border-b border-slate-200/80 dark:border-slate-800">
                        <tr>
                            <th className="px-4 py-3.5">{locale === 'km' ? 'កាលបរិច្ឆេទ & ម៉ោង' : 'Timestamp'}</th>
                            <th className="px-4 py-3.5">{locale === 'km' ? 'ស្ថានភាព' : 'Status'}</th>
                            <th className="px-4 py-3.5">{locale === 'km' ? 'អាសយដ្ឋាន IP' : 'IP Address'}</th>
                            <th className="px-4 py-3.5">{locale === 'km' ? 'ឧបករណ៍ / កម្មវិធីរុករក' : 'Client / Browser'}</th>
                            <th className="px-4 py-3.5">{locale === 'km' ? 'ទីតាំង' : 'Location'}</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60 bg-white dark:bg-slate-900">
                        {paginatedHistory.length === 0 ? (
                            <tr>
                                <td colSpan="5" className="px-4 py-12 text-center text-xs text-slate-400">
                                    {locale === 'km' ? 'មិនមានកំណត់ត្រាប្រវត្តិការចូលប្រព័ន្ធត្រូវនឹងការស្វែងរកឡើយ។' : 'No login history records found matching criteria.'}
                                </td>
                            </tr>
                        ) : (
                            paginatedHistory.map((item, idx) => (
                                <tr key={item.id || idx} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/40 transition">
                                    <td className="px-4 py-3.5 whitespace-nowrap font-medium font-mono text-xs text-slate-800 dark:text-slate-200">
                                        {item.timestamp}
                                    </td>
                                    <td className="px-4 py-3.5 whitespace-nowrap">
                                        {item.status === 'SUCCESS' ? (
                                            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200/80 dark:border-emerald-800/60">
                                                <CheckCircle2 size={12} /> {locale === 'km' ? 'ជោគជ័យ' : 'Success'}
                                            </span>
                                        ) : (
                                            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 border border-rose-200/80 dark:border-rose-800/60">
                                                <XCircle size={12} /> {locale === 'km' ? 'បរាជ័យ' : 'Failed'}
                                            </span>
                                        )}
                                    </td>
                                    <td className="px-4 py-3.5 font-mono text-xs text-indigo-600 dark:text-indigo-400 font-semibold">
                                        {item.ip}
                                    </td>
                                    <td className="px-4 py-3.5 text-slate-700 dark:text-slate-200 font-medium">
                                        {item.device}
                                    </td>
                                    <td className="px-4 py-3.5 text-slate-500 dark:text-slate-400 flex items-center gap-1.5">
                                        <Globe size={13} className="text-slate-400" />
                                        {item.location}
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
                            ? `បង្ហាញពី ${(currentPage - 1) * itemsPerPage + 1} ដល់ ${Math.min(currentPage * itemsPerPage, filteredHistory.length)} នៃ ${filteredHistory.length} កំណត់ត្រា`
                            : `Showing ${(currentPage - 1) * itemsPerPage + 1} to ${Math.min(currentPage * itemsPerPage, filteredHistory.length)} of ${filteredHistory.length} entries`}
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