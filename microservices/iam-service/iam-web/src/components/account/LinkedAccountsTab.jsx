import React, { useState, useEffect } from 'react';
import {
  Globe,
  Trash2,
  Link as LinkIcon,
  CheckCircle,
  CheckCircle2,
  X,
  ExternalLink,
  ShieldCheck,
  Send,
} from 'lucide-react';
import { accountService } from '../../services/accountService';
import { useLanguage } from '../../context/LanguageContext';

const GoogleIcon = () => (
  <svg viewBox="0 0 24 24" className="w-6 h-6">
    <path fill="#4285F4" d="M23.745 12.27c0-.7-.06-1.4-.19-2.07H12v4.51h6.6c-.29 1.52-1.14 2.8-2.4 3.66v3.04h3.88c2.27-2.09 3.66-5.17 3.66-9.14z"/>
    <path fill="#34A853" d="M12 24c3.24 0 5.95-1.08 7.93-2.91l-3.88-3.04c-1.08.72-2.45 1.16-4.05 1.16-3.12 0-5.77-2.1-6.72-4.93H1.27v3.13C3.25 21.32 7.31 24 12 24z"/>
    <path fill="#FBBC05" d="M5.28 14.28c-.25-.72-.38-1.49-.38-2.28s.13-1.56.38-2.28V6.59H1.27C.46 8.2 0 10.04 0 12s.46 3.8 1.27 5.41l4.01-3.13z"/>
    <path fill="#EA4335" d="M12 4.75c1.77 0 3.35.61 4.6 1.8l3.42-3.42C17.95 1.19 15.24 0 12 0 7.31 0 3.25 2.68 1.27 6.59l4.01 3.13c.95-2.83 3.6-4.97 6.72-4.97z"/>
  </svg>
);

const GitHubIcon = () => (
  <svg viewBox="0 0 24 24" className="w-6 h-6 fill-current text-slate-900 dark:text-white">
    <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/>
  </svg>
);

const FacebookIcon = () => (
  <svg viewBox="0 0 24 24" className="w-6 h-6 fill-[#1877F2]">
    <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
  </svg>
);

const TelegramIcon = () => (
  <svg viewBox="0 0 24 24" className="w-6 h-6 fill-[#229ED9]">
    <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.894 8.221l-1.97 9.28c-.145.658-.537.818-1.084.508l-3-2.21-1.446 1.394c-.16.16-.295.295-.605.295l.213-3.053 5.56-5.023c.242-.213-.054-.333-.373-.121l-6.871 4.326-2.962-.924c-.643-.204-.657-.643.136-.953l11.57-4.461c.537-.194 1.006.128.832.932z"/>
  </svg>
);

const PROVIDERS = [
  {
    id: 'google',
    name: 'Google',
    icon: GoogleIcon,
    bg: 'bg-white dark:bg-slate-800 border border-slate-200/80 dark:border-slate-700 shadow-2xs',
    authUrl: '/oauth2/authorization/google',
  },
  {
    id: 'github',
    name: 'GitHub',
    icon: GitHubIcon,
    bg: 'bg-slate-100 dark:bg-slate-800 border border-slate-200/80 dark:border-slate-700 shadow-2xs',
    authUrl: '/oauth2/authorization/github',
  },
  {
    id: 'facebook',
    name: 'Facebook',
    icon: FacebookIcon,
    bg: 'bg-blue-50/80 dark:bg-blue-950/40 border border-blue-100 dark:border-blue-900/50 shadow-2xs',
    authUrl: '/oauth2/authorization/facebook',
  },
  {
    id: 'telegram',
    name: 'Telegram',
    icon: TelegramIcon,
    bg: 'bg-sky-50/80 dark:bg-sky-950/40 border border-sky-100 dark:border-sky-900/50 shadow-2xs',
    authUrl: '/login/telegram',
  },
];

export default function LinkedAccountsTab() {
  const { t, locale } = useLanguage();
  const [identities, setIdentities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState('');
  const [connectingProvider, setConnectingProvider] = useState(null);
  const [accountHandle, setAccountHandle] = useState('');

  const loadIdentities = async () => {
    try {
      const data = await accountService.getIdentities();
      setIdentities(data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadIdentities();
  }, []);

  const handleOpenConnect = (provider) => {
    setConnectingProvider(provider);
    setAccountHandle('');
  };

  const handleConfirmConnect = async (e) => {
    e.preventDefault();
    if (!connectingProvider) return;

    const emailOrHandle = accountHandle.trim();
    if (!emailOrHandle) return;
    await accountService.linkIdentity({
      provider: connectingProvider.id,
      email: emailOrHandle,
      displayName: emailOrHandle,
    });

    await loadIdentities();

    setToast(`${connectingProvider.name} connected successfully as ${emailOrHandle}.`);
    setConnectingProvider(null);
    setAccountHandle('');
    setTimeout(() => setToast(''), 3500);
  };

  const handleDirectOAuth = () => {
    if (connectingProvider?.authUrl) {
      window.location.href = connectingProvider.authUrl;
    }
  };

  const handleUnlink = async (uuid, providerName) => {
    if (window.confirm(`Are you sure you want to unlink your ${providerName} account?`)) {
      await accountService.unlinkIdentity(uuid);
      setIdentities((prev) => prev.filter((item) => item.uuid !== uuid));
      setToast(`${providerName} account unlinked successfully.`);
      setTimeout(() => setToast(''), 3500);
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-xs border border-slate-200/80 dark:border-slate-800 w-full transition-colors space-y-6">
      {/* Toast Notification */}
      {toast && (
        <div className="p-4 rounded-2xl bg-emerald-50 dark:bg-emerald-950/30 border border-emerald-200/80 dark:border-emerald-800 text-emerald-800 dark:text-emerald-200 text-xs font-semibold flex items-center justify-between shadow-xs">
          <div className="flex items-center gap-2">
            <CheckCircle2 size={16} className="text-emerald-600 dark:text-emerald-400" />
            <span>{toast}</span>
          </div>
          <button
            onClick={() => setToast('')}
            className="text-emerald-500 hover:text-emerald-700"
          >
            ✕
          </button>
        </div>
      )}

      {/* Header */}
      <div>
        <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
          {locale === 'km' ? 'គណនីភ្ជាប់ (Social Accounts)' : 'Linked Social Accounts'}
        </h2>
        <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1 pb-4 border-b border-slate-100 dark:border-slate-800">
          {locale === 'km'
            ? 'ភ្ជាប់គណនីខាងក្រៅដើម្បីចូលប្រព័ន្ធយ៉ាងងាយស្រួលជាមួយ single sign-on (SSO)។'
            : 'Connect your external identity providers to sign in seamlessly with single sign-on (SSO).'}
        </p>
      </div>

      {/* Providers List matching your exact 4 providers */}
      <div className="space-y-3.5">
        {PROVIDERS.map((p) => {
          const linked = identities.find(
            (id) => id.provider?.toLowerCase() === p.id.toLowerCase()
          );

          return (
            <div
              key={p.id}
              className="flex items-center justify-between p-4 sm:p-5 border border-slate-200/80 dark:border-slate-800 rounded-2xl hover:border-slate-300 dark:hover:border-slate-700 bg-white dark:bg-slate-800/40 transition shadow-2xs"
            >
              <div className="flex items-center gap-4">
                <div
                  className={`w-12 h-12 rounded-2xl ${p.bg} flex items-center justify-center shrink-0`}
                >
                  <p.icon />
                </div>
                <div>
                  <h4 className="font-bold text-sm text-slate-900 dark:text-white">{p.name}</h4>
                  {linked ? (
                    <p className="text-xs text-slate-600 dark:text-slate-300 flex items-center gap-1.5 mt-0.5 font-medium">
                      <span className="w-2 h-2 rounded-full bg-emerald-500 inline-block" />
                      <span>
                        {locale === 'km' ? 'បានភ្ជាប់ជា ' : 'Linked as '}
                        <strong className="font-semibold text-slate-900 dark:text-white">
                          {p.id === 'github'
                            ? (linked.username ? `@${linked.username}` : (linked.displayName || linked.email))
                            : p.id === 'telegram'
                            ? (linked.username ? `@${linked.username}` : (linked.displayName || 'Telegram User'))
                            : p.id === 'facebook'
                            ? (linked.displayName || (linked.email && !linked.email.includes('@users.invalid') ? linked.email : 'Facebook Account'))
                            : (linked.email && !linked.email.includes('@users.invalid'))
                            ? linked.email
                            : (linked.displayName || 'Connected Account')}
                        </strong>
                      </span>
                    </p>
                  ) : (
                    <p className="text-xs text-slate-400 dark:text-slate-500 mt-0.5">
                      {locale === 'km' ? 'មិនទាន់បានភ្ជាប់' : 'Not connected'}
                    </p>
                  )}
                </div>
              </div>

              {linked ? (
                <button
                  type="button"
                  onClick={() => handleUnlink(linked.uuid, p.name)}
                  className="flex items-center gap-1.5 px-4 py-2 text-xs font-semibold text-rose-600 hover:text-rose-700 dark:text-rose-400 border border-rose-200/80 dark:border-rose-900/50 hover:bg-rose-50 dark:hover:bg-rose-950/30 rounded-xl transition cursor-pointer"
                >
                  <Trash2 size={14} /> {locale === 'km' ? 'ផ្តាច់ការភ្ជាប់' : 'Unlink'}
                </button>
              ) : (
                <div className="flex items-center gap-2">
                  <button
                    type="button"
                    onClick={() => {
                      window.location.href = p.authUrl;
                    }}
                    className="flex items-center gap-1.5 px-4 py-2 text-xs font-semibold text-white bg-indigo-900 hover:bg-indigo-950 rounded-xl transition cursor-pointer shadow-xs"
                  >
                    <ExternalLink size={13} />
                    <span>{locale === 'km' ? 'ភ្ជាប់គណនី' : 'Connect'}</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => handleOpenConnect(p)}
                    className="flex items-center gap-1.5 px-3 py-2 text-xs font-semibold text-slate-600 dark:text-slate-300 border border-slate-200/80 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 rounded-xl transition cursor-pointer"
                    title={locale === 'km' ? 'ភ្ជាប់ដោយបញ្ចូល ID' : 'Manual ID link'}
                  >
                    <LinkIcon size={13} />
                  </button>
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* Connect Modal */}
      {connectingProvider && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-3xl max-w-md w-full p-6 shadow-2xl border border-slate-200/80 dark:border-slate-800 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800">
              <div className="flex items-center gap-2.5">
                <div
                  className={`w-9 h-9 rounded-xl ${connectingProvider.bg} flex items-center justify-center shrink-0`}
                >
                  <connectingProvider.icon />
                </div>
                <h3 className="font-extrabold text-base text-slate-900 dark:text-white">
                  Connect {connectingProvider.name}
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setConnectingProvider(null)}
                className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
              >
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleConfirmConnect} className="mt-4 space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                  Account Identifier / Handle
                </label>
                <input
                  type="text"
                  required
                  value={accountHandle}
                  onChange={(e) => setAccountHandle(e.target.value)}
                  placeholder="e.g. username or user@example.com"
                  className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200/80 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-indigo-500"
                />
                <span className="text-[11px] text-slate-400 mt-1 block">
                  Connect this profile to authenticate into ISTAD IAM.
                </span>
              </div>

              <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200/60 dark:border-slate-700/60 flex items-start gap-2.5 text-[11px] text-slate-600 dark:text-slate-300">
                <ShieldCheck size={16} className="text-indigo-600 dark:text-indigo-400 shrink-0 mt-0.5" />
                <span>
                  Uses your {connectingProvider.name} credentials configured in your environment for OAuth SSO.
                </span>
              </div>

              <div className="flex flex-col gap-2 pt-2">
                <button
                  type="button"
                  onClick={handleDirectOAuth}
                  className="w-full py-2.5 px-4 rounded-xl border border-indigo-200/80 dark:border-indigo-800 text-indigo-700 dark:text-indigo-300 text-xs font-semibold hover:bg-indigo-50/60 dark:hover:bg-indigo-950/30 flex items-center justify-center gap-2 transition cursor-pointer"
                >
                  <ExternalLink size={14} />
                  <span>Authorize via {connectingProvider.name} OAuth</span>
                </button>

                <div className="flex items-center justify-end gap-2.5 pt-2 border-t border-slate-100 dark:border-slate-800">
                  <button
                    type="button"
                    onClick={() => setConnectingProvider(null)}
                    className="px-4 py-2 rounded-xl border border-slate-200/80 dark:border-slate-700 text-xs font-semibold hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300 transition"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 rounded-xl bg-indigo-900 hover:bg-indigo-950 text-white text-xs font-bold shadow-xs transition cursor-pointer"
                  >
                    Link Account
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
