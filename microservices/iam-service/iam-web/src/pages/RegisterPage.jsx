import React, { useState, useEffect } from 'react';
import { Shield, Eye, EyeOff, User, Mail, Lock, AlertCircle, CheckCircle, ArrowRight, ArrowLeft, Sun, Moon, KeyRound, RefreshCw, Loader2 } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';

export default function RegisterPage() {
  const { locale, setLocale, theme, toggleTheme } = useLanguage();
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    givenName: '',
    familyName: '',
    locale: locale,
  });

  // 2FA state
  const [step, setStep] = useState('details'); // 'details' | 'otp'
  const [emailMasked, setEmailMasked] = useState('');
  const [otpCode, setOtpCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);
  const [statusMessage, setStatusMessage] = useState(null);

  useEffect(() => {
    let timer;
    if (resendCooldown > 0) {
      timer = setTimeout(() => setResendCooldown(resendCooldown - 1), 1000);
    }
    return () => clearTimeout(timer);
  }, [resendCooldown]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (statusMessage) setStatusMessage(null);
  };

  const handleRegisterStep1 = async (e) => {
    e.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      setStatusMessage({
        type: 'error',
        text: locale === 'km' ? 'ពាក្យសម្ងាត់ផ្ទៀងផ្ទាត់មិនត្រូវគ្នាទេ។' : 'Passwords do not match.',
      });
      return;
    }

    if (formData.password.length < 8) {
      setStatusMessage({
        type: 'error',
        text: locale === 'km' ? 'ពាក្យសម្ងាត់ត្រូវមានយ៉ាងហោចណាស់ ៨ តួអក្សរ។' : 'Password must be at least 8 characters long.',
      });
      return;
    }

    setLoading(true);
    setStatusMessage(null);

    try {
      const res = await fetch('/api/auth/2fa/register-step1', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          locale,
        }),
      });

      const data = await res.json();
      if (!res.ok) {
        setStatusMessage({
          type: 'error',
          text: data.error || (locale === 'km' ? 'ការចុះឈ្មោះមិនបានសម្រេច។' : 'Registration failed. Please check your details.'),
        });
        setLoading(false);
        return;
      }

      if (data.step === '2fa_required') {
        setEmailMasked(data.emailMasked);
        setStep('otp');
        setResendCooldown(30);
        setStatusMessage({
          type: 'info',
          text: locale === 'km'
            ? `លេខកូដសម្ងាត់ ៦ ខ្ទង់ត្រូវបានផ្ញើទៅកាន់ ${data.emailMasked}`
            : `A 6-digit verification code has been dispatched to ${data.emailMasked}`,
        });
      }
    } catch (err) {
      console.error('Register Step 1 error:', err);
      setStatusMessage({
        type: 'error',
        text: locale === 'km' ? 'មានបញ្ហាក្នុងការតភ្ជាប់ទៅកាន់ម៉ាស៊ីនបម្រើ' : 'Unable to connect to registration service',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyRegister = async (e) => {
    e.preventDefault();
    if (!otpCode.trim() || otpCode.trim().length < 6) {
      setStatusMessage({
        type: 'error',
        text: locale === 'km' ? 'សូមបញ្ចូលលេខកូដ ៦ ខ្ទង់ឱ្យបានពេញលេញ' : 'Please enter the complete 6-digit code',
      });
      return;
    }

    setLoading(true);
    setStatusMessage(null);

    try {
      const res = await fetch('/api/auth/2fa/verify-register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: formData.email.trim(), code: otpCode.trim() }),
      });

      const data = await res.json();
      if (!res.ok) {
        setStatusMessage({
          type: 'error',
          text: data.error || (locale === 'km' ? 'លេខកូដមិនត្រឹមត្រូវ ឬផុតកំណត់' : 'Invalid or expired verification code'),
        });
        setLoading(false);
        return;
      }

      if (data.success) {
        setStatusMessage({
          type: 'success',
          text: locale === 'km' ? 'គណនីបានបង្កើត & ផ្ទៀងផ្ទាត់ជោគជ័យ! កំពុងបញ្ជូន...' : 'Account created & verified! Redirecting to dashboard...',
        });
        setTimeout(() => {
          window.location.href = data.redirectUrl || '/';
        }, 800);
      }
    } catch (err) {
      console.error('Verify Register error:', err);
      setStatusMessage({
        type: 'error',
        text: locale === 'km' ? 'មានបញ្ហាក្នុងការផ្ទៀងផ្ទាត់' : 'Failed to complete registration verification',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleResendCode = async () => {
    if (resendCooldown > 0) return;
    setStatusMessage(null);

    try {
      const res = await fetch('/api/auth/2fa/resend', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: formData.email.trim(), username: formData.username.trim() }),
      });
      const data = await res.json();
      if (res.ok) {
        setResendCooldown(45);
        setStatusMessage({
          type: 'success',
          text: locale === 'km' ? 'លេខកូដថ្មីត្រូវបានផ្ញើរួចរាល់!' : 'A new verification code has been dispatched!',
        });
      } else {
        setStatusMessage({ type: 'error', text: data.error || 'Failed to resend code' });
      }
    } catch (err) {
      setStatusMessage({ type: 'error', text: 'Error connecting to server' });
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 flex flex-col justify-center py-12 sm:px-6 lg:px-8 text-slate-800 dark:text-slate-100 transition-colors">
      {/* Top Controls: Locale & Theme Switcher */}
      <div className="absolute top-4 right-4 flex items-center gap-2">
        <button
          type="button"
          onClick={() => setLocale(locale === 'km' ? 'en' : 'km')}
          className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-700 dark:text-slate-300 shadow-2xs hover:bg-slate-100 dark:hover:bg-slate-800 transition cursor-pointer"
        >
          <span className="text-sm">{locale === 'km' ? '🇰🇭' : '🇬🇧'}</span>
          <span>{locale === 'km' ? 'ខ្មែរ' : 'English'}</span>
        </button>

        <button
          type="button"
          onClick={toggleTheme}
          className="p-2 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-300 shadow-2xs hover:bg-slate-100 dark:hover:bg-slate-800 transition cursor-pointer"
          title="Toggle Theme"
        >
          {theme === 'dark' ? <Sun size={16} /> : <Moon size={16} />}
        </button>
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center">
          <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-indigo-600 to-indigo-500 flex items-center justify-center text-white shadow-lg shadow-indigo-600/30">
            {step === 'otp' ? <KeyRound size={28} /> : <Shield size={28} />}
          </div>
        </div>
        <h2 className="mt-4 text-center text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
          {step === 'otp'
            ? (locale === 'km' ? 'ផ្ទៀងផ្ទាត់អ៊ីមែលរបស់អ្នក' : 'Verify Your Email')
            : (locale === 'km' ? 'បង្កើតគណនីថ្មី' : 'Create an Account')}
        </h2>
        <p className="mt-1 text-center text-xs sm:text-sm text-slate-500 dark:text-slate-400">
          {step === 'otp'
            ? (locale === 'km'
                ? `បញ្ចូលលេខកូដសម្ងាត់ ៦ ខ្ទង់ដែលបានផ្ញើទៅកាន់ ${emailMasked}`
                : `Enter the 6-digit verification code sent to ${emailMasked}`)
            : (locale === 'km'
                ? 'បញ្ចូលព័ត៌មានរបស់អ្នកដើម្បីចុះឈ្មោះប្រើប្រាស់ ISTAD IAM'
                : 'Fill in your details to register for ISTAD IAM')}
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md px-4 sm:px-0">
        <div className="bg-white dark:bg-slate-900 py-8 px-6 sm:px-10 shadow-xl shadow-slate-200/50 dark:shadow-none rounded-3xl border border-slate-200/80 dark:border-slate-800">
          {statusMessage && (
            <div
              className={`mb-6 p-3.5 rounded-2xl text-xs font-medium flex items-center gap-2.5 ${
                statusMessage.type === 'error'
                  ? 'bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-900/50 text-rose-700 dark:text-rose-300'
                  : statusMessage.type === 'info'
                  ? 'bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-900/50 text-blue-700 dark:text-blue-300'
                  : 'bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-900/50 text-emerald-700 dark:text-emerald-300'
              }`}
            >
              {statusMessage.type === 'error' ? (
                <AlertCircle size={18} className="shrink-0" />
              ) : (
                <CheckCircle size={18} className="shrink-0" />
              )}
              <span>{statusMessage.text}</span>
            </div>
          )}

          {step === 'details' ? (
            <form onSubmit={handleRegisterStep1} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                    {locale === 'km' ? 'នាមត្រកូល (Last Name)' : 'Family Name'}
                  </label>
                  <input
                    name="familyName"
                    type="text"
                    required
                    value={formData.familyName}
                    onChange={handleChange}
                    placeholder="Sorn"
                    className="block w-full px-3 py-2 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-700 rounded-xl text-xs sm:text-sm text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                    {locale === 'km' ? 'នាមខ្លួន (First Name)' : 'Given Name'}
                  </label>
                  <input
                    name="givenName"
                    type="text"
                    required
                    value={formData.givenName}
                    onChange={handleChange}
                    placeholder="Sophiram"
                    className="block w-full px-3 py-2 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-700 rounded-xl text-xs sm:text-sm text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  {locale === 'km' ? 'ឈ្មោះគណនី (Username)' : 'Username'}
                </label>
                <div className="relative rounded-xl">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <User size={16} />
                  </div>
                  <input
                    name="username"
                    type="text"
                    required
                    value={formData.username}
                    onChange={handleChange}
                    placeholder="sophiram"
                    className="block w-full pl-10 pr-3 py-2 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-700 rounded-xl text-xs sm:text-sm text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  {locale === 'km' ? 'អ៊ីមែល (Email Address)' : 'Email Address'}
                </label>
                <div className="relative rounded-xl">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <Mail size={16} />
                  </div>
                  <input
                    name="email"
                    type="email"
                    required
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="sophiram@example.com"
                    className="block w-full pl-10 pr-3 py-2 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-700 rounded-xl text-xs sm:text-sm text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  {locale === 'km' ? 'ពាក្យសម្ងាត់ (Password)' : 'Password'}
                </label>
                <div className="relative rounded-xl">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <Lock size={16} />
                  </div>
                  <input
                    name="password"
                    type={showPassword ? 'text' : 'password'}
                    required
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="••••••••••••"
                    className="block w-full pl-10 pr-10 py-2 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-700 rounded-xl text-xs sm:text-sm text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 cursor-pointer"
                  >
                    {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  {locale === 'km' ? 'ផ្ទៀងផ្ទាត់ពាក្យសម្ងាត់' : 'Confirm Password'}
                </label>
                <div className="relative rounded-xl">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <Lock size={16} />
                  </div>
                  <input
                    name="confirmPassword"
                    type={showPassword ? 'text' : 'password'}
                    required
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    placeholder="••••••••••••"
                    className="block w-full pl-10 pr-3 py-2 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-700 rounded-xl text-xs sm:text-sm text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full flex justify-center items-center gap-2 py-2.5 px-4 rounded-xl text-xs sm:text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 shadow-md shadow-indigo-600/30 transition cursor-pointer"
              >
                {loading ? (
                  <Loader2 size={16} className="animate-spin" />
                ) : (
                  <>
                    <span>{locale === 'km' ? 'បន្តទៅកាន់ការផ្ទៀងផ្ទាត់ (2FA)' : 'Continue to 2FA Verification'}</span>
                    <ArrowRight size={16} />
                  </>
                )}
              </button>
            </form>
          ) : (
            /* Step 2: 2FA Verification OTP Form */
            <form onSubmit={handleVerifyRegister} className="space-y-5">
              <div className="p-4 rounded-2xl bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-100 dark:border-indigo-900/40 flex items-start gap-3">
                <Mail className="w-5 h-5 text-indigo-600 dark:text-indigo-400 mt-0.5 shrink-0" />
                <div className="text-xs text-indigo-950 dark:text-indigo-200">
                  <p className="font-semibold mb-0.5">
                    {locale === 'km' ? 'ផ្ទៀងផ្ទាត់អ៊ីមែល' : 'Email Security Code'}
                  </p>
                  <p className="text-slate-600 dark:text-slate-400">
                    {locale === 'km'
                      ? `យើងបានផ្ញើលេខកូដសម្ងាត់ ៦ ខ្ទង់ទៅកាន់ ${emailMasked}។`
                      : `We've sent a 6-digit security code to ${emailMasked}.`}
                  </p>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5 text-center">
                  {locale === 'km' ? 'លេខកូដផ្ទៀងផ្ទាត់ ៦ ខ្ទង់' : '6-Digit Verification Code'}
                </label>
                <input
                  type="text"
                  maxLength={6}
                  required
                  autoFocus
                  value={otpCode}
                  onChange={(e) => setOtpCode(e.target.value.replace(/[^0-9]/g, ''))}
                  placeholder="123456"
                  className="block w-full tracking-[0.5em] text-center font-mono text-xl sm:text-2xl font-bold py-3 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-white placeholder-slate-300 dark:placeholder-slate-600 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition"
                />
              </div>

              <button
                type="submit"
                disabled={loading || otpCode.length < 6}
                className="w-full flex justify-center items-center gap-2 py-2.5 px-4 rounded-xl text-xs sm:text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 shadow-md shadow-indigo-600/30 transition cursor-pointer"
              >
                {loading ? (
                  <Loader2 size={16} className="animate-spin" />
                ) : (
                  <>
                    <span>{locale === 'km' ? 'ផ្ទៀងផ្ទាត់ & បញ្ចប់ការចុះឈ្មោះ' : 'Verify & Complete Registration'}</span>
                    <ArrowRight size={16} />
                  </>
                )}
              </button>

              <div className="flex items-center justify-between text-xs pt-1">
                <button
                  type="button"
                  onClick={() => {
                    setStep('details');
                    setStatusMessage(null);
                  }}
                  className="flex items-center gap-1.5 text-slate-500 hover:text-slate-800 dark:hover:text-slate-200 transition font-medium cursor-pointer"
                >
                  <ArrowLeft size={14} />
                  <span>{locale === 'km' ? 'កែប្រែព័ត៌មាន' : 'Edit details'}</span>
                </button>

                <button
                  type="button"
                  disabled={resendCooldown > 0}
                  onClick={handleResendCode}
                  className="flex items-center gap-1.5 text-indigo-600 dark:text-indigo-400 hover:underline disabled:opacity-50 disabled:no-underline font-medium cursor-pointer"
                >
                  <RefreshCw size={14} className={resendCooldown > 0 ? '' : 'group-hover:rotate-180 transition'} />
                  <span>
                    {resendCooldown > 0
                      ? `${locale === 'km' ? 'ផ្ញើម្តងទៀត (' : 'Resend in ('}${resendCooldown}s)`
                      : (locale === 'km' ? 'ផ្ញើលេខកូដម្តងទៀត' : 'Resend Code')}
                  </span>
                </button>
              </div>
            </form>
          )}

          <div className="mt-6 text-center text-xs text-slate-500 dark:text-slate-400">
            <span>{locale === 'km' ? 'មានគណនីរួចហើយមែនទេ? ' : 'Already have an account? '}</span>
            <a
              href="/login"
              className="font-bold text-indigo-600 dark:text-indigo-400 hover:underline cursor-pointer"
            >
              {locale === 'km' ? 'ចូលប្រើប្រព័ន្ធ' : 'Sign in'}
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}
