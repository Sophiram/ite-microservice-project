import React, { useState, useEffect } from 'react';
import { Shield, Eye, EyeOff, User, Lock, AlertCircle, CheckCircle, ArrowRight, ArrowLeft, Sun, Moon, KeyRound, Mail, RefreshCw, Loader2 } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';

export default function LoginPage() {
  const { locale, setLocale, theme, toggleTheme } = useLanguage();
  const [showPassword, setShowPassword] = useState(false);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [statusMessage, setStatusMessage] = useState(null);

  // 2FA state
  const [step, setStep] = useState('credentials'); // 'credentials' | 'otp'
  const [emailMasked, setEmailMasked] = useState('');
  const [otpCode, setOtpCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);

  useEffect(() => {
    let timer;
    if (resendCooldown > 0) {
      timer = setTimeout(() => setResendCooldown(resendCooldown - 1), 1000);
    }
    return () => clearTimeout(timer);
  }, [resendCooldown]);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const errorParam = params.get('error');
    if (errorParam === 'account_locked' || errorParam === 'account_disabled') {
      window.location.href = `/account-suspended?reason=${errorParam}`;
      return;
    }
    if (params.get('error') !== null) {
      setStatusMessage({
        type: 'error',
        text: locale === 'km'
          ? 'ឈ្មោះគណនី ឬពាក្យសម្ងាត់មិនត្រឹមត្រូវ។ សូមព្យាយាមម្តងទៀត។'
          : 'Invalid username or password. Please try again.',
      });
    } else if (params.get('logout') !== null) {
      setStatusMessage({
        type: 'success',
        text: locale === 'km'
          ? 'អ្នកបានចាកចេញពីប្រព័ន្ធដោយជោគជ័យ។'
          : 'You have been successfully logged out.',
      });
    } else if (params.get('registered') !== null) {
      setStatusMessage({
        type: 'success',
        text: locale === 'km'
          ? 'ការចុះឈ្មោះបានជោគជ័យ! សូមចូលប្រើប្រព័ន្ធដោយប្រើគណនីថ្មីរបស់អ្នក។'
          : 'Registration successful! You can now log in with your credentials.',
      });
    }
  }, [locale]);

  const handleCredentialsSubmit = async (e) => {
    e.preventDefault();
    if (!username.trim() || !password) return;

    setLoading(true);
    setStatusMessage(null);

    try {
      const res = await fetch('/api/auth/2fa/login-step1', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: username.trim(), password }),
      });

      const data = await res.json();
      if (!res.ok) {
        if (data.status === 'suspended' || data.reason === 'account_locked' || data.reason === 'account_disabled') {
          window.location.href = `/account-suspended?reason=${data.reason || 'account_disabled'}`;
          return;
        }
        setStatusMessage({
          type: 'error',
          text: data.error || (locale === 'km' ? 'ឈ្មោះគណនី ឬពាក្យសម្ងាត់មិនត្រឹមត្រូវ' : 'Invalid username or password'),
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
            : `A 6-digit verification code has been sent to ${data.emailMasked}`,
        });
      }
    } catch (err) {
      console.error('Login Step 1 error:', err);
      setStatusMessage({
        type: 'error',
        text: locale === 'km' ? 'មានបញ្ហាក្នុងការភ្ជាប់ទៅកាន់ប្រព័ន្ធ' : 'Unable to connect to authentication service',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleOtpSubmit = async (e) => {
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
      const res = await fetch('/api/auth/2fa/verify-login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: username.trim(), code: otpCode.trim() }),
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
          text: locale === 'km' ? 'ផ្ទៀងផ្ទាត់បានជោគជ័យ! កំពុងបញ្ជូន...' : 'Verification successful! Redirecting...',
        });
        setTimeout(() => {
          window.location.href = data.redirectUrl || '/';
        }, 600);
      }
    } catch (err) {
      console.error('Verify 2FA error:', err);
      setStatusMessage({
        type: 'error',
        text: locale === 'km' ? 'មានបញ្ហាក្នុងការផ្ទៀងផ្ទាត់លេខកូដ' : 'Failed to verify OTP code',
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
        body: JSON.stringify({ username: username.trim() }),
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

  const socialProviders = [
    {
      id: 'google',
      name: 'Google',
      url: '/oauth2/authorization/google',
      icon: (
        <svg className="w-5 h-5" viewBox="0 0 24 24">
          <path
            fill="#4285F4"
            d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
          />
          <path
            fill="#34A853"
            d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
          />
          <path
            fill="#FBBC05"
            d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
          />
          <path
            fill="#EA4335"
            d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
          />
        </svg>
      ),
    },
    {
      id: 'github',
      name: 'GitHub',
      url: '/oauth2/authorization/github',
      icon: (
        <svg className="w-5 h-5 fill-current" viewBox="0 0 24 24">
          <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" />
        </svg>
      ),
    },
    {
      id: 'facebook',
      name: 'Facebook',
      url: '/oauth2/authorization/facebook',
      icon: (
        <svg className="w-5 h-5" viewBox="0 0 24 24" fill="#1877F2">
          <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
        </svg>
      ),
    },
    {
      id: 'telegram',
      name: 'Telegram',
      url: '/login/telegram',
      icon: (
        <svg className="w-5 h-5" viewBox="0 0 24 24" fill="#229ED9">
          <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.894 8.221l-1.97 9.28c-.145.658-.537.818-1.084.508l-3-2.21-1.446 1.394c-.14.18-.357.295-.6.295-.002 0-.003 0-.005 0l.213-3.054 5.56-5.022c.24-.213-.054-.334-.373-.121l-6.869 4.326-2.96-.924c-.643-.204-.657-.643.136-.953l11.57-4.461c.537-.196 1.006.128.832.941z" />
        </svg>
      ),
    },
  ];

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
        {/* Brand Logo */}
        <div className="flex justify-center">
          <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-indigo-600 to-indigo-500 flex items-center justify-center text-white shadow-lg shadow-indigo-600/30">
            {step === 'otp' ? <KeyRound size={28} /> : <Shield size={28} />}
          </div>
        </div>
        <h2 className="mt-4 text-center text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
          {step === 'otp'
            ? (locale === 'km' ? 'ការផ្ទៀងផ្ទាត់ ២ ជំហាន (2FA)' : '2-Step Verification')
            : (locale === 'km' ? 'សូមស្វាគមន៍មកកាន់ ISTAD IAM' : 'Welcome to ISTAD IAM')}
        </h2>
        <p className="mt-1 text-center text-xs sm:text-sm text-slate-500 dark:text-slate-400">
          {step === 'otp'
            ? (locale === 'km'
                ? `សូមបញ្ចូលលេខកូដសម្ងាត់ ៦ ខ្ទង់ដែលបានផ្ញើទៅកាន់ ${emailMasked}`
                : `Enter the 6-digit verification code sent to ${emailMasked}`)
            : (locale === 'km'
                ? 'ប្រព័ន្ធគ្រប់គ្រងអត្តសញ្ញាណ និងសិទ្ធិចូលប្រើប្រាស់ប្រកបដោយសុវត្ថិភាព'
                : 'Secure Identity and Access Management System')}
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md px-4 sm:px-0">
        <div className="bg-white dark:bg-slate-900 py-8 px-6 sm:px-10 shadow-xl shadow-slate-200/50 dark:shadow-none rounded-3xl border border-slate-200/80 dark:border-slate-800">
          {/* Status Alert Banner */}
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

          {step === 'credentials' ? (
            /* Step 1: Credentials Form */
            <form onSubmit={handleCredentialsSubmit} className="space-y-4">
              <div>
                <label
                  htmlFor="username"
                  className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1"
                >
                  {locale === 'km' ? 'ឈ្មោះគណនី ឬ អ៊ីមែល' : 'Username or Email'}
                </label>
                <div className="relative rounded-xl">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <User size={16} />
                  </div>
                  <input
                    id="username"
                    name="username"
                    type="text"
                    required
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder={locale === 'km' ? 'admin ឬ sornsophiram11@gmail.com' : 'admin or user@example.com'}
                    className="block w-full pl-10 pr-3 py-2.5 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-700 rounded-xl text-xs sm:text-sm text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition"
                  />
                </div>
              </div>

              <div>
                <label
                  htmlFor="password"
                  className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1"
                >
                  {locale === 'km' ? 'ពាក្យសម្ងាត់' : 'Password'}
                </label>
                <div className="relative rounded-xl">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <Lock size={16} />
                  </div>
                  <input
                    id="password"
                    name="password"
                    type={showPassword ? 'text' : 'password'}
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••••••"
                    className="block w-full pl-10 pr-10 py-2.5 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-700 rounded-xl text-xs sm:text-sm text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 transition cursor-pointer"
                  >
                    {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
              </div>

              <div className="flex items-center justify-between text-xs">
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="checkbox"
                    name="remember-me"
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                    className="w-4 h-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500"
                  />
                  <span className="text-slate-600 dark:text-slate-400">
                    {locale === 'km' ? 'ចងចាំខ្ញុំ' : 'Remember me'}
                  </span>
                </label>
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
            /* Step 2: 2FA OTP Form */
            <form onSubmit={handleOtpSubmit} className="space-y-5">
              <div className="p-4 rounded-2xl bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-100 dark:border-indigo-900/40 flex items-start gap-3">
                <Mail className="w-5 h-5 text-indigo-600 dark:text-indigo-400 mt-0.5 shrink-0" />
                <div className="text-xs text-indigo-950 dark:text-indigo-200">
                  <p className="font-semibold mb-0.5">
                    {locale === 'km' ? 'ផ្ទៀងផ្ទាត់តាមរយៈអ៊ីមែល' : 'Email Security Code'}
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
                    <span>{locale === 'km' ? 'ផ្ទៀងផ្ទាត់ & ចូលប្រព័ន្ធ' : 'Verify & Sign In'}</span>
                    <ArrowRight size={16} />
                  </>
                )}
              </button>

              <div className="flex items-center justify-between text-xs pt-1">
                <button
                  type="button"
                  onClick={() => {
                    setStep('credentials');
                    setStatusMessage(null);
                  }}
                  className="flex items-center gap-1.5 text-slate-500 hover:text-slate-800 dark:hover:text-slate-200 transition font-medium cursor-pointer"
                >
                  <ArrowLeft size={14} />
                  <span>{locale === 'km' ? 'ត្រឡប់ក្រោយ' : 'Back to login'}</span>
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

          {/* Social Login Section (only in credentials step) */}
          {step === 'credentials' && (
            <div className="mt-6">
              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-slate-200 dark:border-slate-700" />
                </div>
                <div className="relative flex justify-center text-xs">
                  <span className="px-3 bg-white dark:bg-slate-900 text-slate-400 uppercase tracking-wider font-medium text-[11px]">
                    {locale === 'km' ? 'ឬចូលតាមរយៈ' : 'Or continue with'}
                  </span>
                </div>
              </div>

              <div className="mt-4 grid grid-cols-2 sm:grid-cols-4 gap-2.5">
                {socialProviders.map((provider) => (
                  <a
                    key={provider.id}
                    href={provider.url}
                    className="flex items-center justify-center gap-2 py-2.5 px-3 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-750 text-xs font-semibold transition shadow-2xs hover:border-slate-300 dark:hover:border-slate-600 cursor-pointer"
                    title={`Sign in with ${provider.name}`}
                  >
                    {provider.icon}
                    <span className="hidden xs:inline">{provider.name}</span>
                  </a>
                ))}
              </div>
            </div>
          )}

          {/* Register Link */}
          <div className="mt-6 text-center text-xs text-slate-500 dark:text-slate-400">
            <span>{locale === 'km' ? 'មិនទាន់មានគណនីមែនទេ? ' : "Don't have an account? "}</span>
            <a
              href="/register"
              className="font-bold text-indigo-600 dark:text-indigo-400 hover:underline cursor-pointer"
            >
              {locale === 'km' ? 'ចុះឈ្មោះឥឡូវនេះ' : 'Sign up now'}
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}
