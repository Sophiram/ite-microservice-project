import React, { useState, useEffect } from 'react';
import { Laptop, Smartphone, LogOut, RefreshCw, ShieldCheck, MapPin, Globe, Clock, Server } from 'lucide-react';
import { accountService } from '../../services/accountService';
import { useLanguage } from '../../context/LanguageContext';

export default function SessionTab() {
  const { locale } = useLanguage();
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);

  const loadSessions = async () => {
    setLoading(true);
    try {
      const data = await accountService.getActiveSessions();
      setSessions(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSessions();
  }, []);

  const handleTerminate = async (id) => {
    await accountService.terminateSession(id);
    setSessions((prev) => prev.filter((s) => s.id !== id));
    setMessage({ type: 'success', text: locale === 'km' ? 'បានបញ្ចប់សម័យការងារជោគជ័យ' : 'Session terminated successfully.' });
    setTimeout(() => setMessage(null), 3000);
  };

  const handleTerminateAll = async () => {
    const confirmMsg = locale === 'km'
      ? 'តើអ្នកប្រាកដជាចង់ចាកចេញពីគ្រប់ឧបករណ៍ផ្សេងទៀតទាំងអស់មែនទេ?'
      : 'Log out from all other active sessions across devices?';
    if (window.confirm(confirmMsg)) {
      await accountService.terminateAllOtherSessions();
      setSessions((prev) => prev.filter((s) => s.current));
      setMessage({ type: 'success', text: locale === 'km' ? 'បានបញ្ចប់គ្រប់សម័យការងារផ្សេងទៀតទាំងអស់' : 'All other sessions have been terminated.' });
      setTimeout(() => setMessage(null), 3000);
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-sm border border-slate-100 dark:border-slate-800 transition-colors">
      {/* Header with Title and Redis Indicator */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
        <div>
          <div className="flex items-center gap-2 mb-1.5">
            <h2 className="text-xl font-bold text-slate-900 dark:text-white">
              {locale === 'km' ? 'សម័យការងារសកម្ម' : 'Active Sessions'}
            </h2>
            {/* Redis Technology Pill */}
            <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-rose-50 dark:bg-rose-950/40 text-rose-600 dark:text-rose-400 border border-rose-200/80 dark:border-rose-900/50">
              <span className="w-2 h-2 rounded-full bg-rose-500 animate-pulse"></span>
              Redis 7 In-Memory Cache
            </span>
          </div>
          <p className="text-sm text-slate-500 dark:text-slate-400">
            {locale === 'km'
              ? 'ត្រួតពិនិត្យ និងគ្រប់គ្រងឧបករណ៍ដែលកំពុងចូលប្រើគណនីរបស់អ្នក ដំណើរការដោយ Redis Cluster (Port 6379)'
              : 'Monitor devices, browsers, IP addresses and locations currently logged in via Redis Session Store.'}
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={loadSessions}
            disabled={loading}
            className="p-2 rounded-xl text-slate-500 hover:text-slate-900 dark:hover:text-white bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 transition cursor-pointer"
            title="Refresh"
          >
            <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
          </button>
          <button
            onClick={handleTerminateAll}
            className="inline-flex items-center gap-2 px-3.5 py-2 bg-rose-50 dark:bg-rose-950/40 text-rose-600 dark:text-rose-400 hover:bg-rose-100 dark:hover:bg-rose-900/50 rounded-xl text-xs font-bold transition cursor-pointer border border-rose-200 dark:border-rose-900/50"
          >
            <LogOut size={14} />
            <span>{locale === 'km' ? 'ចាកចេញពីគ្រប់ឧបករណ៍ផ្សេង' : 'Log Out All Other Devices'}</span>
          </button>
        </div>
      </div>

      {/* Redis Architecture Info Bar */}
      <div className="my-5 p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-800 flex flex-col sm:flex-row items-start sm:items-center justify-between text-xs text-slate-600 dark:text-slate-300 gap-2">
        <div className="flex items-center gap-2">
          <Server size={15} className="text-rose-500" />
          <span className="font-semibold">{locale === 'km' ? 'ប្រព័ន្ធទិន្នន័យសម័យការងារ:' : 'Session Storage:'}</span>
          <code className="px-1.5 py-0.5 rounded bg-slate-200/80 dark:bg-slate-700 font-mono text-[11px] text-slate-800 dark:text-slate-200">
            redis://localhost:6379 (iam:sessions)
          </code>
        </div>
        <div className="flex items-center gap-2 text-slate-400 text-[11px]">
          <span>TTL: 30 minutes</span>
          <span>•</span>
          <span className="text-emerald-500 font-semibold flex items-center gap-1">
            <ShieldCheck size={13} /> Active Cluster
          </span>
        </div>
      </div>

      {message && (
        <div className="mb-4 p-3 rounded-xl bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800 text-xs font-semibold animate-in fade-in-0">
          {message.text}
        </div>
      )}

      {/* Session List */}
      <div className="divide-y divide-slate-100 dark:divide-slate-800">
        {sessions.length === 0 ? (
          <div className="py-12 text-center text-xs text-slate-400">
            {locale === 'km' ? 'មិនមានសម័យការងារសកម្មទេ' : 'No active sessions found.'}
          </div>
        ) : (
          sessions.map((sess) => {
          const isMobile = sess.device.toLowerCase().includes('iphone') || sess.device.toLowerCase().includes('android');
          return (
            <div key={sess.id} className="py-4.5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
              <div className="flex items-start sm:items-center gap-4">
                <div className={`p-3 rounded-2xl ${
                  sess.current
                    ? 'bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400 ring-2 ring-indigo-500/20'
                    : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400'
                }`}>
                  {isMobile ? <Smartphone size={22} /> : <Laptop size={22} />}
                </div>

                <div className="space-y-1">
                  <div className="flex items-center gap-2.5 flex-wrap">
                    <span className="font-bold text-sm text-slate-900 dark:text-white">{sess.device}</span>
                    {sess.current && (
                      <span className="px-2.5 py-0.5 bg-emerald-100 dark:bg-emerald-950/50 text-emerald-700 dark:text-emerald-300 text-[11px] font-bold rounded-full border border-emerald-300 dark:border-emerald-800">
                        {locale === 'km' ? 'ឧបករណ៍បច្ចុប្បន្ន' : 'This Device'}
                      </span>
                    )}
                    <span className="text-[11px] text-slate-400 dark:text-slate-500 font-mono">
                      (ID: {sess.id})
                    </span>
                  </div>

                  <div className="flex items-center gap-3 text-xs text-slate-500 dark:text-slate-400 flex-wrap">
                    <span className="flex items-center gap-1">
                      <Globe size={13} className="text-slate-400" />
                      {sess.browser}
                    </span>
                    <span>•</span>
                    <span className="font-mono text-slate-600 dark:text-slate-300">{sess.ip}</span>
                    <span>•</span>
                    <span className="flex items-center gap-1">
                      <MapPin size={13} className="text-slate-400" />
                      {sess.location}
                    </span>
                  </div>

                  <div className="flex items-center gap-1 text-[11px] text-slate-400 dark:text-slate-500">
                    <Clock size={12} />
                    <span>{locale === 'km' ? 'សកម្មភាពចុងក្រោយ:' : 'Last active:'} {sess.lastActive}</span>
                  </div>
                </div>
              </div>

              {!sess.current && (
                <button
                  onClick={() => handleTerminate(sess.id)}
                  className="self-end sm:self-center text-xs font-semibold text-rose-600 hover:text-rose-700 dark:text-rose-400 dark:hover:text-rose-300 hover:bg-rose-50 dark:hover:bg-rose-950/40 px-3 py-1.5 rounded-xl border border-rose-200 dark:border-rose-900/50 transition cursor-pointer"
                >
                  {locale === 'km' ? 'បញ្ចប់សម័យ' : 'Terminate'}
                </button>
              )}
            </div>
          );
        }))}
      </div>
    </div>
  );
}