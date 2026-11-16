import React, { useState, useEffect, useMemo } from 'react';
import {
    Activity,
    Shield,
    KeyRound,
    UserCheck,
    CheckCircle2,
    Link2,
    LogOut,
    Sparkles,
    AlertOctagon,
    Search,
    ChevronLeft,
    ChevronRight,
    Clock,
    Lock,
    Unlock,
} from 'lucide-react';
import { accountService } from '../../services/accountService';
import { useLanguage } from '../../context/LanguageContext';

export default function AccountActivityTab() {
    const { t, locale } = useLanguage();
    const [activities, setActivities] = useState([]);
    const [search, setSearch] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 5;

    useEffect(() => {
        accountService.getAccountActivities().then((data) => {
            // Sort newest first
            const sorted = [...(data || [])].sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp) || b.id - a.id);
            setActivities(sorted);
        });
    }, []);

    const getIcon = (event) => {
        switch (event) {
            case 'PASSWORD_CHANGED':
                return <KeyRound className="text-amber-600 dark:text-amber-400" size={18} />;
            case 'LOCK_USER':
                return <Lock className="text-amber-600 dark:text-amber-400" size={18} />;
            case 'UNLOCK_USER':
                return <Unlock className="text-emerald-600 dark:text-emerald-400" size={18} />;
            case 'ENABLE_USER':
            case 'PROFILE_UPDATED':
            case 'USER_UPDATED':
                return <UserCheck className="text-indigo-600 dark:text-indigo-400" size={18} />;
            case 'DISABLE_USER':
                return <AlertOctagon className="text-slate-500" size={18} />;
            case 'DELETE_USER':
                return <LogOut className="text-rose-600 dark:text-rose-400" size={18} />;
            case 'EMAIL_VERIFIED':
                return <CheckCircle2 className="text-emerald-600 dark:text-emerald-400" size={18} />;
            case 'IDENTITY_LINKED':
                return <Link2 className="text-sky-600 dark:text-sky-400" size={18} />;
            case 'SESSION_REVOKED':
            case 'ALL_SESSIONS_REVOKED':
                return <LogOut className="text-rose-600 dark:text-rose-400" size={18} />;
            case 'USER_CREATED':
            case 'ACCOUNT_CREATED':
                return <Sparkles className="text-cyan-600 dark:text-cyan-400" size={18} />;
            case 'ASSIGN_ROLE':
            case 'REVOKE_ROLE':
                return <Shield className="text-purple-600 dark:text-purple-400" size={18} />;
            case 'DEACTIVATION_REQUESTED':
                return <AlertOctagon className="text-rose-600 dark:text-rose-400" size={18} />;
            default:
                return <Shield className="text-purple-600 dark:text-purple-400" size={18} />;
        }
    };

    const getEventBadge = (event) => {
        switch (event) {
            case 'PASSWORD_CHANGED':
            case 'LOCK_USER':
            case 'UNLOCK_USER':
                return <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-50 dark:bg-amber-950/40 text-amber-700 dark:text-amber-300 border border-amber-200/80">SECURITY</span>;
            case 'PROFILE_UPDATED':
            case 'USER_UPDATED':
            case 'USER_CREATED':
            case 'DELETE_USER':
                return <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-indigo-50 dark:bg-indigo-950/40 text-indigo-700 dark:text-indigo-300 border border-indigo-200/80">USER</span>;
            case 'ASSIGN_ROLE':
            case 'REVOKE_ROLE':
                return <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-purple-50 dark:bg-purple-950/40 text-purple-700 dark:text-purple-300 border border-purple-200/80">ROLE</span>;
            case 'EMAIL_VERIFIED':
                return <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200/80">VERIFIED</span>;
            case 'IDENTITY_LINKED':
                return <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-sky-50 dark:bg-sky-950/40 text-sky-700 dark:text-sky-300 border border-sky-200/80">OAUTH2</span>;
            case 'SESSION_REVOKED':
            case 'ALL_SESSIONS_REVOKED':
                return <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 border border-rose-200/80">SESSION</span>;
            case 'ACCOUNT_CREATED':
                return <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-cyan-50 dark:bg-cyan-950/40 text-cyan-700 dark:text-cyan-300 border border-cyan-200/80">ACCOUNT</span>;
            default:
                return <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300">{event}</span>;
        }
    };

    const filtered = useMemo(() => {
        return activities.filter((act) => {
            const desc = `${act.description || ''} ${act.descriptionKm || ''}`.toLowerCase();
            return (
                desc.includes(search.toLowerCase()) ||
                (act.event || '').toLowerCase().includes(search.toLowerCase()) ||
                (act.ip || '').includes(search) ||
                (act.timestamp || '').includes(search)
            );
        });
    }, [activities, search]);

    useEffect(() => {
        setCurrentPage(1);
    }, [search]);

    const totalPages = Math.max(1, Math.ceil(filtered.length / itemsPerPage));
    const paginated = useMemo(() => {
        const start = (currentPage - 1) * itemsPerPage;
        return filtered.slice(start, start + itemsPerPage);
    }, [filtered, currentPage, itemsPerPage]);

    return (
        <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-xs border border-slate-200/80 dark:border-slate-800 w-full transition-colors">
            {/* Header */}
            <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
                <div className="flex items-center gap-3.5">
                    <div className="w-12 h-12 rounded-2xl bg-indigo-50 dark:bg-slate-800 text-indigo-600 dark:text-indigo-400 flex items-center justify-center shrink-0">
                        <Activity size={22} />
                    </div>
                    <div>
                        <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
                            {locale === 'km' ? 'កំណត់ហេតុសកម្មភាពគណនី' : 'Account Activity Log'}
                        </h2>
                        <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400">
                            {locale === 'km'
                                ? 'តាមដានការផ្លាស់ប្តូរសុវត្ថិភាព និងលិខិតសម្គាល់សំខាន់ៗលើគណនីរបស់អ្នក (រៀបតាមពេលថ្មីបំផុតនៅលើ)'
                                : 'Audit trail of critical security and credential changes on your account (sorted newest first).'}
                        </p>
                    </div>
                </div>

                <div className="flex items-center gap-2">
                    <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-indigo-50 dark:bg-indigo-950/50 text-indigo-700 dark:text-indigo-300 border border-indigo-200/80 dark:border-indigo-800/60">
                        <Clock size={13} />
                        {locale === 'km' ? `សរុប ${filtered.length} សកម្មភាព` : `${filtered.length} Total Events`}
                    </span>
                </div>
            </div>

            {/* Search Filter */}
            <div className="my-5">
                <div className="relative max-w-md">
                    <Search size={15} className="absolute left-3.5 top-3 text-slate-400" />
                    <input
                        type="text"
                        placeholder={locale === 'km' ? 'ស្វែងរកសកម្មភាពគណនី...' : 'Search account activities...'}
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full pl-9 pr-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-indigo-500"
                    />
                </div>
            </div>

            {/* Activity Timeline List */}
            <div className="relative pl-6 border-l-2 border-indigo-100 dark:border-slate-800 space-y-6 my-6">
                {paginated.length === 0 ? (
                    <div className="text-xs text-slate-400 py-8">
                        {locale === 'km' ? 'មិនមានសកម្មភាពគណនីត្រូវនឹងការស្វែងរកឡើយ។' : 'No account activities found matching criteria.'}
                    </div>
                ) : (
                    paginated.map((act) => (
                        <div key={act.id} className="relative group">
                            {/* Marker Icon */}
                            <div className="absolute -left-[35px] top-1 bg-white dark:bg-slate-900 p-2 rounded-full border border-slate-200 dark:border-slate-700 shadow-xs group-hover:scale-110 transition-transform">
                                {getIcon(act.event)}
                            </div>

                            {/* Card Content */}
                            <div className="bg-slate-50/50 dark:bg-slate-800/40 p-4 rounded-2xl border border-slate-100 dark:border-slate-800 hover:border-indigo-200 dark:hover:border-slate-700 transition">
                                <div className="flex flex-wrap items-center justify-between gap-2 mb-1.5">
                                    <div className="flex items-center gap-2">
                                        {getEventBadge(act.event)}
                                        <p className="text-sm font-semibold text-slate-900 dark:text-slate-100">
                                            {locale === 'km' ? act.descriptionKm || act.description : act.description}
                                        </p>
                                    </div>
                                    <span className="text-xs font-mono text-slate-400 dark:text-slate-500">
                                        {act.timestamp}
                                    </span>
                                </div>
                                <div className="flex items-center gap-3 text-xs text-slate-500 dark:text-slate-400 font-mono">
                                    <span>IP: {act.ip}</span>
                                    <span>•</span>
                                    <span className="text-[11px] font-sans text-emerald-600 dark:text-emerald-400 font-medium">
                                        {locale === 'km' ? 'បានផ្ទៀងផ្ទាត់សុពលភាព' : 'Verified by IAM Audit'}
                                    </span>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* Pagination Controls */}
            {totalPages > 1 && (
                <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mt-6 pt-4 border-t border-slate-100 dark:border-slate-800 text-xs text-slate-500 dark:text-slate-400">
                    <div>
                        {locale === 'km'
                            ? `បង្ហាញពី ${(currentPage - 1) * itemsPerPage + 1} ដល់ ${Math.min(currentPage * itemsPerPage, filtered.length)} នៃ ${filtered.length} សកម្មភាព`
                            : `Showing ${(currentPage - 1) * itemsPerPage + 1} to ${Math.min(currentPage * itemsPerPage, filtered.length)} of ${filtered.length} activities`}
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