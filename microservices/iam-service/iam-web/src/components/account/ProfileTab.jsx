import React, { useState, useEffect } from 'react';
import { Camera, Check, Save, User, Mail, Phone, Globe, Shield, Calendar, MapPin, AtSign, AlertCircle, X, Send } from 'lucide-react';
import { accountService } from '../../services/accountService';
import { useLanguage } from '../../context/LanguageContext';
import ModernDatePicker from '../ui/ModernDatePicker';

export default function ProfileTab() {
    const { t, locale } = useLanguage();
    const [profile, setProfile] = useState({
        givenName: '',
        familyName: '',
        middleName: '',
        nickname: '',
        username: '',
        email: '',
        phoneNumber: '',
        gender: '',
        birthdate: '',
        locale: 'km',
        zoneinfo: 'Asia/Phnom_Penh',
        websiteUrl: '',
        profileUrl: '',
        pictureUrl: null,
        emailVerified: false,
        role: '',
    });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);

    // Email verification state
    const [verifying, setVerifying] = useState(false);
    const [showOtpModal, setShowOtpModal] = useState(false);
    const [otpCode, setOtpCode] = useState('');
    const [otpFeedback, setOtpFeedback] = useState(null);

    useEffect(() => {
        // 1. Fetch user profile
        accountService.getProfile().then((data) => {
            if (data) {
                setProfile((prev) => ({
                    ...prev,
                    ...data,
                    phoneNumber: data.phoneNumber || data.phone || '',
                    givenName: data.givenName || '',
                    familyName: data.familyName || '',
                    middleName: data.middleName || '',
                    nickname: data.nickname || '',
                    gender: data.gender || '',
                    birthdate: data.birthdate || '',
                    locale: data.locale || 'km',
                    zoneinfo: data.zoneinfo || 'Asia/Phnom_Penh',
                    websiteUrl: data.websiteUrl || '',
                    profileUrl: data.profileUrl || '',
                    pictureUrl: data.pictureUrl || null,
                    emailVerified: !!data.emailVerified,
                }));
            }
        });

        // 2. Check for ?emailVerified=true in URL (redirected from verification email link)
        const params = new URLSearchParams(window.location.search);
        if (params.get('emailVerified') === 'true') {
            setMessage({
                type: 'success',
                text: t('verifyEmailSuccess') || 'Your email address has been verified successfully!',
            });
            setProfile((prev) => ({ ...prev, emailVerified: true }));
            try {
                const raw = localStorage.getItem('istad_user_profile');
                if (raw) {
                    const parsed = JSON.parse(raw);
                    parsed.emailVerified = true;
                    localStorage.setItem('istad_user_profile', JSON.stringify(parsed));
                }
            } catch {}
            // Clean URL query parameters
            window.history.replaceState({}, document.title, window.location.pathname);
        } else if (params.get('emailVerified') === 'false') {
            setMessage({
                type: 'error',
                text: locale === 'km' ? 'តំណភ្ជាប់ផ្ទៀងផ្ទាត់មិនត្រឹមត្រូវ ឬផុតកំណត់។' : 'Email verification link is invalid or expired.',
            });
            window.history.replaceState({}, document.title, window.location.pathname);
        }

        const handleProfileUpdate = (e) => {
            if (e.detail) {
                setProfile((prev) => ({ ...prev, ...e.detail }));
            }
        };
        window.addEventListener('profile-updated', handleProfileUpdate);
        return () => window.removeEventListener('profile-updated', handleProfileUpdate);
    }, [locale, t]);

    const handleChange = (e) => {
        setProfile((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleAvatarChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const previewUrl = URL.createObjectURL(file);
            setProfile((p) => ({ ...p, pictureUrl: previewUrl }));
        }
    };

    const handleSendVerification = async () => {
        setVerifying(true);
        setOtpFeedback(null);
        try {
            const res = await accountService.sendVerificationEmail();
            setShowOtpModal(true);
            if (res && res.otp) {
                setOtpFeedback({
                    type: 'info',
                    text: locale === 'km'
                        ? `កូដផ្ទៀងផ្ទាត់ត្រូវបានផ្ញើ! កូដគំរូ OTP: ${res.otp}`
                        : `Verification code sent via JavaMailSender! Dev OTP: ${res.otp}`,
                });
            }
        } catch {
            setShowOtpModal(true);
        } finally {
            setVerifying(false);
        }
    };

    const handleConfirmOtp = async () => {
        if (!otpCode || otpCode.trim().length === 0) return;
        try {
            const res = await accountService.verifyEmail(otpCode.trim());
            if (res && res.verified) {
                setProfile((prev) => ({ ...prev, emailVerified: true }));
                setMessage({
                    type: 'success',
                    text: t('verifyEmailSuccess') || 'Your email address has been verified successfully!',
                });
                setShowOtpModal(false);
                setOtpCode('');
            } else {
                setOtpFeedback({
                    type: 'error',
                    text: locale === 'km' ? 'កូដមិនត្រឹមត្រូវ ឬផុតកំណត់។ សូមសាកល្បងម្តងទៀត។' : 'Invalid or expired code. Please try again.',
                });
            }
        } catch {
            setProfile((prev) => ({ ...prev, emailVerified: true }));
            setShowOtpModal(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage(null);
        try {
            const payload = {
                givenName: profile.givenName || null,
                familyName: profile.familyName || null,
                middleName: profile.middleName || null,
                nickname: profile.nickname || null,
                gender: profile.gender || null,
                birthdate: profile.birthdate || null,
                phoneNumber: profile.phoneNumber || null,
                locale: profile.locale || 'km',
                zoneinfo: profile.zoneinfo || 'Asia/Phnom_Penh',
                websiteUrl: profile.websiteUrl || null,
                profileUrl: profile.profileUrl || null,
                pictureUrl: profile.pictureUrl || null,
            };
            await accountService.updateProfile(payload);
            setMessage({ type: 'success', text: t('profileSavedSuccess') || 'Profile changes saved successfully!' });
        } catch {
            setMessage({ type: 'error', text: t('profileSaveFailed') || 'Failed to update profile.' });
        } finally {
            setLoading(false);
        }
    };

    const isAdmin =
        profile.roles?.some((r) => (r.name || r).toUpperCase() === 'ADMIN') ||
        profile.role?.toUpperCase() === 'ADMIN' ||
        profile.username === 'admin';

    return (
        <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-xs border border-slate-200/80 dark:border-slate-800 w-full transition-colors space-y-6">
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
                <div>
                    <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
                        {t('profileTitle')}
                    </h2>
                    <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
                        {t('profileSubtitle')}
                    </p>
                </div>
                <span className="px-3 py-1 text-xs font-mono font-bold rounded-xl bg-indigo-50 dark:bg-indigo-950/40 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
                    {isAdmin ? t('adminAccountBadge') : t('userAccountBadge')}
                </span>
            </div>

            {message && (
                <div
                    className={`p-4 rounded-2xl text-xs sm:text-sm font-semibold flex items-center gap-2 transition-all ${
                        message.type === 'success'
                            ? 'bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800'
                            : 'bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 border border-rose-200 dark:border-rose-800'
                    }`}
                >
                    {message.type === 'success' ? <Check size={16} /> : <AlertCircle size={16} />}
                    <span>{message.text}</span>
                </div>
            )}

            {/* Profile Picture Header */}
            <div className="flex flex-col sm:flex-row items-center sm:items-start gap-6 py-6 border-b border-slate-100 dark:border-slate-800">
                <div className="relative group">
                    <img
                        src={profile.pictureUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150'}
                        alt="Profile Avatar"
                        className="w-24 h-24 rounded-2xl object-cover ring-4 ring-indigo-50 dark:ring-slate-800 shadow-md"
                    />
                    <label className="absolute -bottom-2 -right-2 p-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl cursor-pointer shadow-lg transition transform hover:scale-105">
                        <Camera size={16} />
                        <input type="file" className="hidden" accept="image/*" onChange={handleAvatarChange} />
                    </label>
                </div>
                <div className="text-center sm:text-left">
                    <h3 className="text-lg font-bold text-slate-900 dark:text-white">
                        {profile.givenName} {profile.familyName}
                    </h3>
                    <div className="flex items-center justify-center sm:justify-start gap-2 text-xs text-slate-500 dark:text-slate-400 font-mono mt-0.5">
                        <span>@{profile.username}</span>
                        <span>•</span>
                        <span>{profile.email}</span>
                    </div>
                    <p className="text-xs text-slate-400 dark:text-slate-500 mt-2">
                        {t('supportedPhotoFormats')}
                    </p>
                </div>
            </div>

            {/* Complete Profile Form with 3 Organized Sections */}
            <form onSubmit={handleSubmit} className="space-y-8">
                {/* Section 1: Names & Identity */}
                <div className="space-y-4">
                    <h4 className="text-sm font-bold text-slate-900 dark:text-white uppercase tracking-wider text-xs border-b border-slate-100 dark:border-slate-800 pb-2">
                        {t('secNameIdentity')}
                    </h4>
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('firstNameLabel')}
                            </label>
                            <input
                                type="text"
                                name="givenName"
                                value={profile.givenName || ''}
                                onChange={handleChange}
                                placeholder="Sorn"
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-2xs"
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('lastNameLabel')}
                            </label>
                            <input
                                type="text"
                                name="familyName"
                                value={profile.familyName || ''}
                                onChange={handleChange}
                                placeholder="Sophiram"
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-2xs"
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('middleNameLabel')}
                            </label>
                            <input
                                type="text"
                                name="middleName"
                                value={profile.middleName || ''}
                                onChange={handleChange}
                                placeholder={t('middleNamePlaceholder')}
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-2xs"
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('nicknameLabel')}
                            </label>
                            <input
                                type="text"
                                name="nickname"
                                value={profile.nickname || ''}
                                onChange={handleChange}
                                placeholder={t('nicknamePlaceholder')}
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-2xs"
                            />
                        </div>

                        <div className="sm:col-span-2">
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('usernameLabel')}
                            </label>
                            <input
                                type="text"
                                name="username"
                                value={profile.username || ''}
                                disabled
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/60 text-slate-600 dark:text-slate-400 text-sm outline-none font-mono shadow-2xs cursor-not-allowed"
                            />
                        </div>

                        <div className="sm:col-span-2">
                            <div className="flex items-center justify-between mb-1.5">
                                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300">
                                    {t('emailLabel')}
                                </label>
                                {profile.emailVerified ? (
                                    <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800">
                                        <Check size={12} />
                                        {t('emailVerified')}
                                    </span>
                                ) : (
                                    <div className="flex items-center gap-2">
                                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-bold bg-amber-50 dark:bg-amber-950/40 text-amber-700 dark:text-amber-300 border border-amber-200 dark:border-amber-800">
                                            {t('emailUnverified')}
                                        </span>
                                        <button
                                            type="button"
                                            onClick={handleSendVerification}
                                            disabled={verifying}
                                            className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-indigo-50 hover:bg-indigo-100 dark:bg-indigo-950/50 text-indigo-700 dark:text-indigo-300 border border-indigo-200 transition cursor-pointer"
                                        >
                                            <Mail size={11} />
                                            <span>{verifying ? t('savingBtn') : t('verifyEmailBtn')}</span>
                                        </button>
                                    </div>
                                )}
                            </div>
                            <input
                                type="email"
                                name="email"
                                value={profile.email || ''}
                                disabled
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/60 text-slate-600 dark:text-slate-400 text-sm outline-none font-mono shadow-2xs cursor-not-allowed"
                            />
                        </div>
                    </div>
                </div>

                {/* Section 2: Contact & Personal Demographics */}
                <div className="space-y-4">
                    <h4 className="text-sm font-bold text-slate-900 dark:text-white uppercase tracking-wider text-xs border-b border-slate-100 dark:border-slate-800 pb-2">
                        {t('secContactDetails')}
                    </h4>
                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('phoneNumberLabel')}
                            </label>
                            <input
                                type="tel"
                                name="phoneNumber"
                                placeholder="+855969144183"
                                value={profile.phoneNumber || ''}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-2xs"
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('genderLabel')}
                            </label>
                            <select
                                name="gender"
                                value={profile.gender || ''}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-2xs cursor-pointer"
                            >
                                <option value="">{t('selectGender')}</option>
                                <option value="male">{t('male')} (Male)</option>
                                <option value="female">{t('female')} (Female)</option>
                                <option value="other">{t('other')} (Other)</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('birthdateLabel')}
                            </label>
                            <ModernDatePicker
                                name="birthdate"
                                value={profile.birthdate || ''}
                                onChange={handleChange}
                                placeholder={t('selectBirthdatePlaceholder')}
                            />
                        </div>
                    </div>
                </div>

                {/* Section 3: Localization & Online Links */}
                <div className="space-y-4">
                    <h4 className="text-sm font-bold text-slate-900 dark:text-white uppercase tracking-wider text-xs border-b border-slate-100 dark:border-slate-800 pb-2">
                        {t('secLocalization')}
                    </h4>
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('preferredLanguage')}
                            </label>
                            <select
                                name="locale"
                                value={profile.locale || 'km'}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-2xs cursor-pointer"
                            >
                                <option value="km">{t('khmerLang')}</option>
                                <option value="en">{t('englishLang')}</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('timezoneLabel')}
                            </label>
                            <input
                                type="text"
                                name="zoneinfo"
                                placeholder="Asia/Phnom_Penh"
                                value={profile.zoneinfo || 'Asia/Phnom_Penh'}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-2xs font-mono"
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('websiteUrlLabel')}
                            </label>
                            <input
                                type="url"
                                name="websiteUrl"
                                placeholder="https://example.com"
                                value={profile.websiteUrl || ''}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-2xs"
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                                {t('profileUrlLabel')}
                            </label>
                            <input
                                type="url"
                                name="profileUrl"
                                placeholder="https://github.com/Sophiram"
                                value={profile.profileUrl || ''}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-2xs"
                            />
                        </div>
                    </div>
                </div>

                <div className="pt-4 flex items-center justify-end gap-3 border-t border-slate-100 dark:border-slate-800">
                    <button
                        type="submit"
                        disabled={loading}
                        className="inline-flex items-center gap-2 px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold shadow-md shadow-indigo-600/20 transition cursor-pointer disabled:opacity-50"
                    >
                        <Save size={16} />
                        <span>{loading ? t('savingBtn') : t('saveChangesBtn')}</span>
                    </button>
                </div>
            </form>

            {/* Email OTP Verification Modal */}
            {showOtpModal && (
                <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white dark:bg-slate-900 rounded-3xl max-w-md w-full p-6 shadow-2xl border border-slate-200/80 dark:border-slate-800 animate-in fade-in zoom-in-95">
                        <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800">
                            <div className="flex items-center gap-2.5">
                                <div className="w-9 h-9 rounded-xl bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400 flex items-center justify-center">
                                    <Mail size={18} />
                                </div>
                                <div>
                                    <h3 className="font-bold text-sm text-slate-900 dark:text-white">
                                        {t('verifyEmailBtn')}
                                    </h3>
                                    <p className="text-xs text-slate-400 font-mono">{profile.email}</p>
                                </div>
                            </div>
                            <button
                                onClick={() => setShowOtpModal(false)}
                                className="p-1 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 cursor-pointer"
                            >
                                <X size={18} />
                            </button>
                        </div>

                        {otpFeedback && (
                            <div
                                className={`mt-4 p-3 rounded-xl text-xs font-semibold ${
                                    otpFeedback.type === 'error'
                                        ? 'bg-rose-50 dark:bg-rose-950/40 text-rose-700 dark:text-rose-300 border border-rose-200'
                                        : 'bg-indigo-50 dark:bg-indigo-950/40 text-indigo-700 dark:text-indigo-300 border border-indigo-200'
                                }`}
                            >
                                {otpFeedback.text}
                            </div>
                        )}

                        <div className="py-5 space-y-3">
                            <p className="text-xs text-slate-500 dark:text-slate-400">
                                {locale === 'km'
                                    ? 'សូមបញ្ចូលកូដ ៦ ខ្ទង់ ដែលបានផ្ញើទៅកាន់អាសយដ្ឋានអ៊ីមែលរបស់អ្នក ឬចុចលើតំណភ្ជាប់ក្នុងអ៊ីមែលដើម្បីផ្ទៀងផ្ទាត់ដោយស្វ័យប្រវត្តិ។'
                                    : 'Please enter the 6-digit verification code sent to your email or click the link in your email to verify automatically.'}
                            </p>
                            <input
                                type="text"
                                maxLength={6}
                                value={otpCode}
                                onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, ''))}
                                placeholder="123456"
                                className="w-full text-center tracking-[0.5em] font-mono text-2xl font-extrabold py-3 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-indigo-500"
                            />
                        </div>

                        <div className="flex items-center justify-end gap-2.5 pt-3 border-t border-slate-100 dark:border-slate-800">
                            <button
                                type="button"
                                onClick={() => setShowOtpModal(false)}
                                className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition cursor-pointer"
                            >
                                {t('cancel')}
                            </button>
                            <button
                                type="button"
                                onClick={handleConfirmOtp}
                                disabled={otpCode.length < 6}
                                className="px-5 py-2 text-xs font-bold bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl shadow-md transition disabled:opacity-50 cursor-pointer"
                            >
                                {t('confirm')}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}