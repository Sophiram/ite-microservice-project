import React, { useState } from 'react';
import { AlertTriangle, Trash2, X } from 'lucide-react';
import { accountService } from '../../services/accountService';
import { useLanguage } from '../../context/LanguageContext';

export default function DeactivationTab() {
    const { t, locale } = useLanguage();
    const [reason, setReason] = useState('');
    const [password, setPassword] = useState('');
    const [confirmed, setConfirmed] = useState(false);
    const [modalOpen, setModalOpen] = useState(false);
    const [status, setStatus] = useState(null);

    const handleDeactivate = async () => {
        try {
            await accountService.requestDeactivation({ reason, password });
            setStatus({
                type: 'success',
                text: locale === 'km'
                    ? 'សំណើសុំបិទគណនីត្រូវបានបញ្ជូន។ គណនីរបស់អ្នកនឹងត្រូវកំណត់ពេលបិទដំណើរការ។'
                    : 'Deactivation request submitted. Your account will be scheduled for deactivation.',
            });
            setModalOpen(false);
        } catch {
            setStatus({
                type: 'error',
                text: locale === 'km' ? 'បរាជ័យក្នុងការដំណើរការសំណើ។' : 'Failed to process request.',
            });
        }
    };

    return (
        <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-xs border border-rose-200/80 dark:border-rose-950/60 w-full transition-colors">
            <div className="flex items-center gap-3.5 text-rose-600 dark:text-rose-400 mb-4 pb-4 border-b border-rose-100 dark:border-rose-950/40">
                <div className="w-12 h-12 rounded-2xl bg-rose-50 dark:bg-rose-950/50 flex items-center justify-center shrink-0">
                    <AlertTriangle size={24} />
                </div>
                <div>
                    <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
                        {locale === 'km' ? 'ស្នើសុំបិទគណនី' : 'Request Account Deactivation'}
                    </h2>
                    <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400">
                        {locale === 'km'
                            ? 'បិទការចូលប្រើប្រាស់ជាអចិន្ត្រៃយ៍ និងលុបចោលអតិថិជនដែលមានសិទ្ធិទាំងអស់។'
                            : 'Permanently close your access and revoke all authorized clients.'}
                    </p>
                </div>
            </div>

            <p className="text-xs sm:text-sm text-slate-600 dark:text-slate-300 mb-6">
                {locale === 'km'
                    ? 'ការបិទដំណើរការគណនីរបស់អ្នក នឹងបិទប្រវត្តិរូបរបស់អ្នកភ្លាមៗ លុបចោលសម័យការងារសកម្ម និងកំណត់ការចូលប្រើសេវាកម្ម OAuth2 ដែលបានភ្ជាប់ទាំងអស់។'
                    : 'Deactivating your account will immediately disable your profile, revoke active sessions, and restrict access to all connected OAuth2 services.'}
            </p>

            {status && (
                <div className={`p-4 mb-6 rounded-2xl text-xs sm:text-sm font-semibold ${status.type === 'success' ? 'bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800' : 'bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 border border-rose-200 dark:border-rose-800'}`}>
                    {status.text}
                </div>
            )}

            <div className="space-y-4">
                <div>
                    <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                        {locale === 'km' ? 'មូលហេតុនៃការចាកចេញ' : 'Reason for leaving'}
                    </label>
                    <select
                        value={reason}
                        onChange={(e) => setReason(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-rose-500/20 focus:border-rose-500 shadow-2xs cursor-pointer"
                    >
                        <option value="">{locale === 'km' ? 'សូមជ្រើសរើសមូលហេតុ...' : 'Please select a reason...'}</option>
                        <option value="privacy">{locale === 'km' ? 'ការបារម្ភអំពីភាពឯកជន' : 'Privacy concerns'}</option>
                        <option value="not_needed">{locale === 'km' ? 'លែងត្រូវការប្រើប្រាស់សេវាកម្មនេះទៀត' : 'No longer using the service'}</option>
                        <option value="another_account">{locale === 'km' ? 'បានបង្កើតគណនីផ្សេងមួយទៀត' : 'Created a different account'}</option>
                        <option value="other">{locale === 'km' ? 'មូលហេតុផ្សេងៗ' : 'Other reason'}</option>
                    </select>
                </div>

                <div className="flex items-center gap-2.5 pt-2">
                    <input
                        type="checkbox"
                        id="confirm-deact"
                        checked={confirmed}
                        onChange={(e) => setConfirmed(e.target.checked)}
                        className="rounded-md w-4 h-4 text-rose-600 focus:ring-rose-500 dark:bg-slate-800 dark:border-slate-700 cursor-pointer"
                    />
                    <label htmlFor="confirm-deact" className="text-xs sm:text-sm text-slate-600 dark:text-slate-300 cursor-pointer">
                        {locale === 'km'
                            ? 'ខ្ញុំយល់ច្បាស់ថា សកម្មភាពនេះអាចកែប្រែឡើងវិញបានលុះត្រាតែទាក់ទងអ្នកគ្រប់គ្រង (Admin)។'
                            : 'I understand this action can only be reversed by contacting an administrator.'}
                    </label>
                </div>

                <button
                    type="button"
                    disabled={!confirmed || !reason}
                    onClick={() => setModalOpen(true)}
                    className="mt-4 px-6 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold shadow-md shadow-rose-600/20 disabled:opacity-50 transition cursor-pointer"
                >
                    {locale === 'km' ? 'បិទគណនីរបស់ខ្ញុំ' : 'Deactivate My Account'}
                </button>
            </div>

            {/* Confirmation Modal */}
            {modalOpen && (
                <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-in fade-in-0">
                    <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl max-w-md w-full shadow-2xl border border-slate-100 dark:border-slate-800">
                        <div className="flex items-center justify-between mb-4">
                            <h3 className="text-lg font-bold text-slate-900 dark:text-white">
                                {locale === 'km' ? 'បញ្ជាក់ការបិទគណនី' : 'Confirm Account Deactivation'}
                            </h3>
                            <button onClick={() => setModalOpen(false)} className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200">
                                <X size={18} />
                            </button>
                        </div>
                        <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mb-4">
                            {locale === 'km' ? 'សូមបញ្ចូលពាក្យសម្ងាត់របស់អ្នកដើម្បីបញ្ជាក់ការបិទគណនី។' : 'Please enter your password to confirm deactivation.'}
                        </p>
                        <input
                            type="password"
                            placeholder={locale === 'km' ? 'ពាក្យសម្ងាត់បច្ចុប្បន្នរបស់អ្នក' : 'Your current password'}
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm mb-5 outline-none focus:ring-2 focus:ring-rose-500"
                        />
                        <div className="flex justify-end gap-3">
                            <button
                                onClick={() => setModalOpen(false)}
                                className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition cursor-pointer"
                            >
                                {t('cancel')}
                            </button>
                            <button
                                onClick={handleDeactivate}
                                disabled={!password}
                                className="px-4 py-2 text-xs font-bold bg-rose-600 hover:bg-rose-700 text-white rounded-xl shadow-sm transition disabled:opacity-50 cursor-pointer"
                            >
                                {locale === 'km' ? 'បញ្ជាក់ & បិទគណនី' : 'Confirm & Deactivate'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}