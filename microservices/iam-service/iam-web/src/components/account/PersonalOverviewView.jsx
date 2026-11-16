import React, { useState, useEffect } from 'react';
import {
  Shield,
  Key,
  Clock,
  Calendar,
  Edit3,
  ExternalLink,
  User,
  Lock,
  Link2,
  CheckCircle,
  Mail,
  Send,
  Check,
} from 'lucide-react';
import { accountService } from '../../services/accountService';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function PersonalOverviewView({ onNavigateTab }) {
  const { locale } = useLanguage();
  const [profile, setProfile] = useState({
    name: '',
    username: '',
    email: '',
    givenName: '',
    familyName: '',
    role: '',
    emailVerified: false,
    phone: '',
    gender: '',
    birthdate: '',
    rolesCount: 0,
    permissionsCount: 0,
    lastLogin: 'Active now',
    memberSince: '—',
  });

  const [verifying, setVerifying] = useState(false);
  const [showOtpModal, setShowOtpModal] = useState(false);
  const [otpCode, setOtpCode] = useState('');
  const [verificationFeedback, setVerificationFeedback] = useState(null);

  useEffect(() => {
    accountService.getProfile().then((data) => {
      if (data) {
        setProfile((prev) => ({
          ...prev,
          ...data,
          name: data.name || data.username || '',
          username: data.username || '',
          email: data.email || '',
          emailVerified: !!data.emailVerified,
        }));
      }
    });

    adminService.getRoles().then((res) => {
      const items = res?.content || res || [];
      if (Array.isArray(items)) {
        setProfile((prev) => ({ ...prev, rolesCount: items.length }));
      }
    }).catch(() => {});

    adminService.getPermissions().then((res) => {
      const items = res?.content || res || [];
      if (Array.isArray(items)) {
        setProfile((prev) => ({ ...prev, permissionsCount: items.length }));
      }
    }).catch(() => {});

    const handleProfileUpdate = (e) => {
      if (e.detail) {
        setProfile((prev) => ({ ...prev, ...e.detail }));
      }
    };
    window.addEventListener('profile-updated', handleProfileUpdate);
    return () => window.removeEventListener('profile-updated', handleProfileUpdate);
  }, []);

  const handleSendVerification = async () => {
    setVerifying(true);
    setVerificationFeedback(null);
    try {
      const res = await accountService.sendVerificationEmail();
      setShowOtpModal(true);
      if (res.otp) {
        setVerificationFeedback({
          type: 'info',
          text: locale === 'km'
            ? `អ៊ីមែលផ្ទៀងផ្ទាត់ត្រូវបានផ្ញើតាម JavaMailSender! កូដ OTP គឺ: ${res.otp}`
            : `Verification email dispatched via JavaMailSender! Dev OTP: ${res.otp}`,
        });
      }
    } catch {
      setShowOtpModal(true);
    } finally {
      setVerifying(false);
    }
  };

  const handleConfirmOtp = async () => {
    try {
      const res = await accountService.verifyEmail(otpCode);
      if (res.verified) {
        setProfile((prev) => ({ ...prev, emailVerified: true }));
        setVerificationFeedback({
          type: 'success',
          text: locale === 'km' ? 'អ៊ីមែលត្រូវបានផ្ទៀងផ្ទាត់ជោគជ័យ!' : 'Email successfully verified!',
        });
        setTimeout(() => setShowOtpModal(false), 1500);
      } else {
        setVerificationFeedback({
          type: 'error',
          text: locale === 'km' ? 'កូដមិនត្រឹមត្រូវ សូមសាកល្បងម្តងទៀត' : 'Invalid code. Please try again.',
        });
      }
    } catch {
      setProfile((prev) => ({ ...prev, emailVerified: true }));
      setShowOtpModal(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* 1. Header Hero Banner matching Screenshot 1 */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 rounded-3xl p-5 sm:p-8 flex flex-col md:flex-row justify-between md:items-center gap-5 sm:gap-6 shadow-sm">
        <div className="flex items-center gap-4 sm:gap-5 min-w-0">
          {/* Avatar Circle / Photo */}
          {profile.pictureUrl ? (
            <img
              src={profile.pictureUrl}
              alt={profile.name || 'User'}
              className="w-14 h-14 sm:w-16 sm:h-16 rounded-full object-cover ring-2 ring-indigo-200/80 dark:ring-slate-700 shadow-md shrink-0"
            />
          ) : (
            <div className="w-14 h-14 sm:w-16 sm:h-16 rounded-full bg-indigo-50 dark:bg-slate-800 border-2 border-indigo-200/80 dark:border-slate-700 flex items-center justify-center text-indigo-700 dark:text-indigo-300 font-extrabold text-lg sm:text-xl shrink-0 shadow-inner">
              {profile.name ? profile.name.split(' ').map((n) => n[0]).join('').slice(0, 2).toUpperCase() : 'SS'}
            </div>
          )}
          <div className="min-w-0 flex-1">
            <div className="text-xs font-medium text-slate-400 dark:text-slate-500 mb-0.5">
              {locale === 'km' ? 'សូមស្វាគមន៍ការត្រឡប់មកវិញ' : 'Welcome back'}
            </div>
            <h1 className="text-xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight truncate">
              {profile.name}
            </h1>
            <div className="flex flex-wrap items-center gap-x-2 gap-y-0.5 mt-1 text-xs text-slate-500 dark:text-slate-400 font-mono">
              <span>@{profile.username}</span>
              <span>•</span>
              <span className="break-all">{profile.email}</span>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex items-center gap-2.5 sm:gap-3 flex-wrap">
          <button
            onClick={() => onNavigateTab && onNavigateTab('profile')}
            className="inline-flex items-center gap-2 px-3.5 sm:px-4 py-2 sm:py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-200 text-xs font-semibold shadow-xs transition cursor-pointer"
          >
            <Edit3 size={14} />
            <span>{locale === 'km' ? 'កែប្រែទម្រង់' : 'Edit Profile'}</span>
          </button>
          <button
            onClick={() => onNavigateTab && onNavigateTab('dashboard')}
            className="inline-flex items-center gap-2 px-3.5 sm:px-4 py-2 sm:py-2.5 rounded-xl bg-indigo-900 hover:bg-indigo-950 text-white text-xs font-semibold shadow-md shadow-indigo-950/20 transition cursor-pointer"
          >
            <Shield size={14} className="text-indigo-300" />
            <span>{locale === 'km' ? 'បើកផ្ទាំងគ្រប់គ្រង' : 'Open Admin Dashboard'}</span>
          </button>
        </div>
      </div>

      {/* 2. Four Metric Cards matching Screenshot 1 */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
        {/* Roles Card */}
        <div className="bg-white dark:bg-slate-900 p-4 sm:p-5 rounded-2xl border border-slate-200/80 dark:border-slate-800 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400 mb-2 truncate">
            <Shield size={15} className="text-indigo-600 dark:text-indigo-400 shrink-0" />
            <span className="truncate">{locale === 'km' ? 'តួនាទី' : 'Roles'}</span>
          </div>
          <div className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">
            {profile.rolesCount}
          </div>
        </div>

        {/* Permissions Card */}
        <div className="bg-white dark:bg-slate-900 p-4 sm:p-5 rounded-2xl border border-slate-200/80 dark:border-slate-800 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400 mb-2 truncate">
            <Key size={15} className="text-purple-600 dark:text-purple-400 shrink-0" />
            <span className="truncate">{locale === 'km' ? 'សិទ្ធិ' : 'Permissions'}</span>
          </div>
          <div className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">
            {profile.permissionsCount}
          </div>
        </div>

        {/* Last Login Card */}
        <div className="bg-white dark:bg-slate-900 p-4 sm:p-5 rounded-2xl border border-slate-200/80 dark:border-slate-800 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400 mb-2 truncate">
            <Clock size={15} className="text-amber-500 shrink-0" />
            <span className="truncate">{locale === 'km' ? 'ចូលចុងក្រោយ' : 'Last sign-in'}</span>
          </div>
          <div className="text-sm sm:text-base font-bold text-slate-900 dark:text-white truncate">
            {profile.lastLogin}
          </div>
        </div>

        {/* Member Since Card */}
        <div className="bg-white dark:bg-slate-900 p-4 sm:p-5 rounded-2xl border border-slate-200/80 dark:border-slate-800 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400 mb-2 truncate">
            <Calendar size={15} className="text-emerald-500 shrink-0" />
            <span className="truncate">{locale === 'km' ? 'សមាជិកតាំងពី' : 'Member since'}</span>
          </div>
          <div className="text-sm sm:text-base font-bold text-slate-900 dark:text-white truncate">
            {profile.memberSince}
          </div>
        </div>
      </div>

      {/* 3. Two Columns: Personal Information & Shortcuts matching Screenshot 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column (2 spans): Personal Information */}
        <div className="lg:col-span-2 bg-white dark:bg-slate-900 rounded-3xl border border-slate-200/80 dark:border-slate-800 p-6 sm:p-7 shadow-xs">
          <div className="flex items-center justify-between pb-4 border-b border-slate-100 dark:border-slate-800 mb-6">
            <h3 className="font-bold text-base text-slate-900 dark:text-white">
              {locale === 'km' ? 'ព័ត៌មានផ្ទាល់ខ្លួន' : 'Personal Information'}
            </h3>
            <button
              onClick={() => onNavigateTab && onNavigateTab('profile')}
              className="text-xs font-semibold text-indigo-600 dark:text-indigo-400 hover:underline cursor-pointer"
            >
              {locale === 'km' ? 'កែប្រែ' : 'Edit'}
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-y-6 gap-x-8 text-sm">
            <div>
              <div className="text-xs text-slate-400 dark:text-slate-500 mb-1">
                {locale === 'km' ? 'នាមខ្លួន (First Name)' : 'First Name'}
              </div>
              <div className="font-semibold text-slate-800 dark:text-slate-200">
                {profile.givenName || '—'}
              </div>
            </div>

            <div>
              <div className="text-xs text-slate-400 dark:text-slate-500 mb-1">
                {locale === 'km' ? 'នាមត្រកូល (Last Name)' : 'Last Name'}
              </div>
              <div className="font-semibold text-slate-800 dark:text-slate-200">
                {profile.familyName || '—'}
              </div>
            </div>

            <div>
              <div className="text-xs text-slate-400 dark:text-slate-500 mb-1">
                {locale === 'km' ? 'អ៊ីមែល (Email Address)' : 'Email Address'}
              </div>
              <div className="flex items-center gap-2 flex-wrap">
                <span className="font-semibold text-slate-800 dark:text-slate-200">
                  {profile.email}
                </span>
                {profile.emailVerified ? (
                  <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-bold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800">
                    <Check size={12} />
                    {locale === 'km' ? 'បានផ្ទៀងផ្ទាត់' : 'Verified'}
                  </span>
                ) : (
                  <button
                    onClick={handleSendVerification}
                    disabled={verifying}
                    className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-amber-50 dark:bg-amber-950/40 text-amber-700 dark:text-amber-300 border border-amber-200 hover:bg-amber-100 transition cursor-pointer"
                  >
                    <Mail size={12} />
                    <span>{verifying ? 'Sending...' : locale === 'km' ? 'ផ្ញើអ៊ីមែលផ្ទៀងផ្ទាត់' : 'Verify via JavaMailSender'}</span>
                  </button>
                )}
              </div>
            </div>

            <div>
              <div className="text-xs text-slate-400 dark:text-slate-500 mb-1">
                {locale === 'km' ? 'លេខទូរស័ព្ទ (Phone Number)' : 'Phone Number'}
              </div>
              <div className="font-semibold text-slate-800 dark:text-slate-200">
                {profile.phoneNumber || profile.phone || '—'}
              </div>
            </div>

            <div>
              <div className="text-xs text-slate-400 dark:text-slate-500 mb-1">
                {locale === 'km' ? 'ភេទ (Gender)' : 'Gender'}
              </div>
              <div className="font-semibold text-slate-800 dark:text-slate-200 capitalize">
                {profile.gender
                  ? (profile.gender === 'male'
                      ? (locale === 'km' ? 'ប្រុស (Male)' : 'Male')
                      : profile.gender === 'female'
                      ? (locale === 'km' ? 'ស្រី (Female)' : 'Female')
                      : (locale === 'km' ? 'ផ្សេងៗ (Other)' : 'Other'))
                  : '—'}
              </div>
            </div>

            <div>
              <div className="text-xs text-slate-400 dark:text-slate-500 mb-1">
                {locale === 'km' ? 'ថ្ងៃខែឆ្នាំកំណើត (Date of Birth)' : 'Date of Birth'}
              </div>
              <div className="font-semibold text-slate-800 dark:text-slate-200">
                {profile.birthdate || '—'}
              </div>
            </div>

            <div>
              <div className="text-xs text-slate-400 dark:text-slate-500 mb-1">
                {locale === 'km' ? 'ភាសាពេញចិត្ត (Preferred Language)' : 'Preferred Language'}
              </div>
              <div className="font-semibold text-slate-800 dark:text-slate-200">
                {profile.locale === 'km' ? 'ភាសាខ្មែរ (Khmer)' : 'English (EN)'}
              </div>
            </div>

            <div>
              <div className="text-xs text-slate-400 dark:text-slate-500 mb-1">
                {locale === 'km' ? 'គេហទំព័រ / Link' : 'Website / Profile URL'}
              </div>
              <div className="font-semibold text-slate-800 dark:text-slate-200 truncate">
                {profile.websiteUrl || profile.profileUrl ? (
                  <a
                    href={profile.websiteUrl || profile.profileUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="text-indigo-600 dark:text-indigo-400 hover:underline"
                  >
                    {profile.websiteUrl || profile.profileUrl}
                  </a>
                ) : (
                  '—'
                )}
              </div>
            </div>

            <div className="sm:col-span-2 pt-2 border-t border-slate-100 dark:border-slate-800">
              <div className="text-xs text-slate-400 dark:text-slate-500 mb-1.5">
                {locale === 'km' ? 'តួនាទី (Roles)' : 'Roles'}
              </div>
              <div className="flex items-center gap-2">
                <span className="px-2.5 py-1 rounded-lg text-xs font-mono font-bold bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200 border border-slate-200 dark:border-slate-700">
                  {profile.role || 'ADMIN'}
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Right Column (1 span): Shortcuts matching Screenshot 1 */}
        <div className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-200/80 dark:border-slate-800 p-6 sm:p-7 shadow-xs">
          <h3 className="font-bold text-base text-slate-900 dark:text-white pb-4 border-b border-slate-100 dark:border-slate-800 mb-4">
            {locale === 'km' ? 'ផ្លូវកាត់' : 'Shortcuts'}
          </h3>

          <div className="space-y-3">
            {/* Shortcut 1: Profile */}
            <button
              onClick={() => onNavigateTab && onNavigateTab('profile')}
              className="w-full flex items-center justify-between p-3.5 rounded-2xl hover:bg-slate-50 dark:hover:bg-slate-800/60 border border-transparent hover:border-slate-200/80 dark:hover:border-slate-700 transition cursor-pointer text-left group"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-indigo-50 dark:bg-indigo-950/40 text-indigo-600 dark:text-indigo-400 flex items-center justify-center shrink-0">
                  <User size={18} />
                </div>
                <div>
                  <div className="font-bold text-sm text-slate-800 dark:text-slate-200 group-hover:text-indigo-600 transition">
                    {locale === 'km' ? 'ប្រវត្តិរូប' : 'Profile'}
                  </div>
                  <div className="text-[11px] text-slate-400 dark:text-slate-500 line-clamp-1">
                    {locale === 'km' ? 'ឈ្មោះ, ព័ត៌មានទំនាក់ទំនង និងរូបភាព...' : 'Name, contact info and picture...'}
                  </div>
                </div>
              </div>
              <span className="text-slate-300 dark:text-slate-600 group-hover:text-indigo-600 transition">›</span>
            </button>

            {/* Shortcut 2: Password & Security */}
            <button
              onClick={() => onNavigateTab && onNavigateTab('account_security')}
              className="w-full flex items-center justify-between p-3.5 rounded-2xl hover:bg-slate-50 dark:hover:bg-slate-800/60 border border-transparent hover:border-slate-200/80 dark:hover:border-slate-700 transition cursor-pointer text-left group"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-purple-50 dark:bg-purple-950/40 text-purple-600 dark:text-purple-400 flex items-center justify-center shrink-0">
                  <Key size={18} />
                </div>
                <div>
                  <div className="font-bold text-sm text-slate-800 dark:text-slate-200 group-hover:text-purple-600 transition">
                    {locale === 'km' ? 'ពាក្យសម្ងាត់ និងសុវត្ថិភាព' : 'Password & security'}
                  </div>
                  <div className="text-[11px] text-slate-400 dark:text-slate-500 line-clamp-1">
                    {locale === 'km' ? 'ប្តូរពាក្យសម្ងាត់ និងមើលសកម្មភាពចូល...' : 'Change password & security check...'}
                  </div>
                </div>
              </div>
              <span className="text-slate-300 dark:text-slate-600 group-hover:text-purple-600 transition">›</span>
            </button>

            {/* Shortcut 3: Linked Accounts */}
            <button
              onClick={() => onNavigateTab && onNavigateTab('linked')}
              className="w-full flex items-center justify-between p-3.5 rounded-2xl hover:bg-slate-50 dark:hover:bg-slate-800/60 border border-transparent hover:border-slate-200/80 dark:hover:border-slate-700 transition cursor-pointer text-left group"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-emerald-50 dark:bg-emerald-950/40 text-emerald-600 dark:text-emerald-400 flex items-center justify-center shrink-0">
                  <Link2 size={18} />
                </div>
                <div>
                  <div className="font-bold text-sm text-slate-800 dark:text-slate-200 group-hover:text-emerald-600 transition">
                    {locale === 'km' ? 'គណនីដែលបានភ្ជាប់' : 'Linked accounts'}
                  </div>
                  <div className="text-[11px] text-slate-400 dark:text-slate-500 line-clamp-1">
                    {locale === 'km' ? 'Google, GitHub និងគណនីផ្សេងទៀត...' : 'Google, GitHub & Telegram...'}
                  </div>
                </div>
              </div>
              <span className="text-slate-300 dark:text-slate-600 group-hover:text-emerald-600 transition">›</span>
            </button>
          </div>
        </div>
      </div>

      {/* 4. JavaMailSender Email Verification Modal */}
      {showOtpModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-2xl border border-slate-100 dark:border-slate-800 animate-in fade-in-0 zoom-in-95">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 rounded-2xl bg-indigo-50 dark:bg-indigo-950/40 text-indigo-600 dark:text-indigo-400 flex items-center justify-center shrink-0">
                <Mail size={24} />
              </div>
              <div>
                <h3 className="font-bold text-lg text-slate-900 dark:text-white">
                  {locale === 'km' ? 'ផ្ទៀងផ្ទាត់អាសយដ្ឋានអ៊ីមែល' : 'Verify Email Address'}
                </h3>
                <p className="text-xs text-slate-500 dark:text-slate-400">
                  {locale === 'km' ? 'ដំណើរការដោយ JavaMailSender Service' : 'Powered by JavaMailSender Service'}
                </p>
              </div>
            </div>

            <p className="text-sm text-slate-600 dark:text-slate-300 mb-4">
              {locale === 'km'
                ? `យើងបានផ្ញើកូដសម្ងាត់ ៦ ខ្ទង់ទៅកាន់ ${profile.email}។ សូមបញ្ចូលកូដខាងក្រោមដើម្បីផ្ទៀងផ្ទាត់៖`
                : `A 6-digit confirmation code was sent to ${profile.email}. Enter it below to complete verification:`}
            </p>

            {verificationFeedback && (
              <div className={`p-3 rounded-xl mb-4 text-xs font-semibold ${
                verificationFeedback.type === 'success'
                  ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-300'
                  : verificationFeedback.type === 'error'
                  ? 'bg-rose-50 text-rose-700 dark:bg-rose-950/40 dark:text-rose-300'
                  : 'bg-indigo-50 text-indigo-700 dark:bg-indigo-950/40 dark:text-indigo-300'
              }`}>
                {verificationFeedback.text}
              </div>
            )}

            <div className="mb-5">
              <input
                type="text"
                maxLength={6}
                value={otpCode}
                onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, ''))}
                placeholder="123456"
                className="w-full text-center tracking-[8px] font-mono font-bold text-2xl py-3 px-4 border border-slate-200 dark:border-slate-700 rounded-2xl outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
              />
            </div>

            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={() => setShowOtpModal(false)}
                className="flex-1 py-2.5 px-4 rounded-xl border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-300 text-xs font-semibold hover:bg-slate-50 transition cursor-pointer"
              >
                {locale === 'km' ? 'បោះបង់' : 'Cancel'}
              </button>
              <button
                type="button"
                onClick={handleConfirmOtp}
                className="flex-1 py-2.5 px-4 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold shadow-md shadow-indigo-600/20 transition cursor-pointer"
              >
                {locale === 'km' ? 'ផ្ទៀងផ្ទាត់ឥឡូវនេះ' : 'Confirm Code'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
