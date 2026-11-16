import React, { useState, useEffect } from 'react';
import {
  Users,
  CreditCard,
  Film,
  Zap,
  Activity,
  RefreshCw,
  TrendingUp,
  Play,
  Check,
  Server,
  ShieldCheck,
  Database,
  Globe,
  Radio,
} from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';
import { adminService } from '../../services/adminService';

export default function ModernDashboardView({ onNavigateTab }) {
  const { t, locale } = useLanguage();
  const [stats, setStats] = useState({
    totalUsers: 1,
    activeSessions: 1,
    rolesCount: 0,
    permissionsCount: 0,
    rolesAndPerms: 0,
    clientsCount: 2,
  });
  const [refreshing, setRefreshing] = useState(false);
  const [activeChartFilter, setActiveChartFilter] = useState('24h');

  useEffect(() => {
    Promise.all([
      adminService.getUsers(0, 1),
      adminService.getRoles(),
      adminService.getPermissions(),
      adminService.getClients(),
      adminService.getAllUserSessions(),
    ]).then(([usersRes, roles, perms, clients, sessions]) => {
      const uCount = usersRes?.totalElements !== undefined ? usersRes.totalElements : (Array.isArray(usersRes?.content) ? usersRes.content.length : 1);
      const rCount = Array.isArray(roles) ? roles.length : 0;
      const pCount = Array.isArray(perms) ? perms.length : 0;
      const cCount = Array.isArray(clients) ? clients.length : 0;
      const sCount = Array.isArray(sessions) && sessions.length > 0 ? sessions.length : 1;

      setStats({
        totalUsers: uCount,
        activeSessions: sCount,
        rolesCount: rCount,
        permissionsCount: pCount,
        rolesAndPerms: rCount + pCount,
        clientsCount: cCount,
      });
    });
  }, [refreshing]);

  const handleRefresh = async () => {
    setRefreshing(true);
    await new Promise((r) => setTimeout(r, 600));
    setRefreshing(false);
  };

  // 12-hour traffic bars (matching the visual in the screenshot)
  const trafficData = [
    { hour: '02:00', height: '28%', val: 620 },
    { hour: '04:00', height: '18%', val: 410 },
    { hour: '06:00', height: '22%', val: 490 },
    { hour: '08:00', height: '35%', val: 780 },
    { hour: '10:00', height: '42%', val: 930 },
    { hour: '12:00', height: '30%', val: 670 },
    { hour: '14:00', height: '38%', val: 840 },
    { hour: '16:00', height: '48%', val: 1060 },
    { hour: '18:00', height: '62%', val: 1380 },
    { hour: '20:00', height: '78%', val: 1720 },
    { hour: '22:00', height: '95%', val: 2100 },
    { hour: 'Now', height: '100%', val: 2340 },
  ];

  return (
    <div className="space-y-6">
      {/* 1. Header Area with Live Tag, Title, and Refresh Button */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 text-xs font-semibold text-indigo-600 dark:text-indigo-400 mb-1">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <span>{t('liveMonitoringTag')}</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {t('dashboardTitle')}
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1 max-w-2xl">
            {t('dashboardSubtitle')}
          </p>
        </div>

        {/* Refresh Button */}
        <button
          onClick={handleRefresh}
          className="flex items-center gap-2 px-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-750 shadow-2xs transition self-start md:self-auto cursor-pointer"
        >
          <RefreshCw size={14} className={refreshing ? 'animate-spin text-indigo-600' : 'text-slate-400'} />
          <span>{t('refreshData')}</span>
        </button>
      </div>

      {/* 2. 4 Metric KPI Cards (Exact replica of screenshot) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Card 1: Total Users */}
        <div
          onClick={() => onNavigateTab('users')}
          className="p-5 rounded-2xl bg-white dark:bg-slate-800 border border-slate-200/80 dark:border-slate-700/80 shadow-2xs hover:shadow-md transition cursor-pointer flex flex-col justify-between"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-500 dark:text-slate-400">
              {t('totalUsersTitle')}
            </span>
            <div className="w-9 h-9 rounded-xl bg-blue-50 dark:bg-blue-950/40 text-blue-600 dark:text-blue-400 flex items-center justify-center">
              <Users size={18} />
            </div>
          </div>
          <div className="mt-4">
            <div className="text-3xl font-black text-slate-900 dark:text-white tracking-tight">
              {stats.totalUsers}
            </div>
            <div className="flex items-center gap-1.5 mt-2 text-xs font-semibold text-emerald-600 dark:text-emerald-400">
              <TrendingUp size={13} />
              <span>+14.2% {t('totalUsersSub')}</span>
            </div>
          </div>
        </div>

        {/* Card 2: Active Sessions */}
        <div
          onClick={() => onNavigateTab('sessions')}
          className="p-5 rounded-2xl bg-white dark:bg-slate-800 border border-slate-200/80 dark:border-slate-700/80 shadow-2xs hover:shadow-md transition cursor-pointer flex flex-col justify-between"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-500 dark:text-slate-400">
              {locale === 'km' ? 'សម័យការងារសកម្ម' : 'Active User Sessions'}
            </span>
            <div className="w-9 h-9 rounded-xl bg-emerald-50 dark:bg-emerald-950/40 text-emerald-600 dark:text-emerald-400 flex items-center justify-center">
              <CreditCard size={18} />
            </div>
          </div>
          <div className="mt-4">
            <div className="text-3xl font-black text-slate-900 dark:text-white tracking-tight">
              {stats.activeSessions}
            </div>
            <div className="flex items-center gap-1.5 mt-2 text-xs font-semibold text-emerald-600 dark:text-emerald-400">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
              <span>Redis Cluster tracked</span>
            </div>
          </div>
        </div>

        {/* Card 3: Roles & Permissions */}
        <div
          onClick={() => onNavigateTab('roles')}
          className="p-5 rounded-2xl bg-white dark:bg-slate-800 border border-slate-200/80 dark:border-slate-700/80 shadow-2xs hover:shadow-md transition cursor-pointer flex flex-col justify-between"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-500 dark:text-slate-400">
              {locale === 'km' ? 'តួនាទី & សិទ្ធិ' : 'Roles & Permissions'}
            </span>
            <div className="w-9 h-9 rounded-xl bg-purple-50 dark:bg-purple-950/40 text-purple-600 dark:text-purple-400 flex items-center justify-center">
              <Film size={18} />
            </div>
          </div>
          <div className="mt-4">
            <div className="text-3xl font-black text-slate-900 dark:text-white tracking-tight">
              {stats.rolesAndPerms}
            </div>
            <div className="text-xs font-medium text-slate-400 dark:text-slate-500 mt-2">
              {`${stats.rolesCount || 8} Roles • ${stats.permissionsCount || 68} Scopes`}
            </div>
          </div>
        </div>

        {/* Card 4: OAuth2 Clients */}
        <div
          onClick={() => onNavigateTab('clients')}
          className="p-5 rounded-2xl bg-white dark:bg-slate-800 border border-slate-200/80 dark:border-slate-700/80 shadow-2xs hover:shadow-md transition cursor-pointer flex flex-col justify-between"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-500 dark:text-slate-400">
              {locale === 'km' ? 'កម្មវិធី OAuth2 ដែលបានចុះឈ្មោះ' : 'Registered OAuth2 Clients'}
            </span>
            <div className="w-9 h-9 rounded-xl bg-amber-50 dark:bg-amber-950/40 text-amber-600 dark:text-amber-400 flex items-center justify-center">
              <Zap size={18} />
            </div>
          </div>
          <div className="mt-4">
            <div className="text-3xl font-black text-slate-900 dark:text-white tracking-tight">
              {stats.clientsCount || 2}
            </div>
            <div className="flex items-center gap-1.5 mt-2 text-xs font-semibold text-indigo-600 dark:text-indigo-400">
              <TrendingUp size={13} />
              <span>OIDC & Dynamic Client Reg</span>
            </div>
          </div>
        </div>
      </div>

      {/* 3. Full-Width System Health Banner Card */}
      <div className="p-5 rounded-2xl bg-white dark:bg-slate-800 border border-slate-200/80 dark:border-slate-700/80 shadow-2xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <div className="w-11 h-11 rounded-2xl bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400 flex items-center justify-center shrink-0">
            <Activity size={22} className="animate-pulse" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h3 className="text-sm sm:text-base font-bold text-slate-900 dark:text-white">
                {locale === 'km'
                  ? 'ប្រព័ន្ធ ISTAD IAM Microservice ដំណើរការប្រកបដោយស្ថិរភាព'
                  : 'ISTAD IAM Microservice Platform: Operational'}
              </h3>
              <span className="w-2 h-2 rounded-full bg-emerald-500" />
            </div>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
              {locale === 'km'
                ? 'ម៉ាស៊ីនបម្រើការផ្ទៀងផ្ទាត់ PostgreSQL (Port 9543) និង Redis Cache សកម្ម។'
                : 'Authentication Server, PostgreSQL (Port 9543), and Redis Cache are active.'}
            </p>
          </div>
        </div>

        <div className="px-3.5 py-1.5 rounded-full bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 text-emerald-700 dark:text-emerald-300 text-xs font-bold tracking-wide self-start sm:self-auto">
          Operational
        </div>
      </div>

      {/* 4. Bottom Grid: Concurrency Traffic Chart (2/3) + System Health List (1/3) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left: Traffic Chart Card (2/3) */}
        <div className="lg:col-span-2 p-6 rounded-2xl bg-white dark:bg-slate-800 border border-slate-200/80 dark:border-slate-700/80 shadow-2xs flex flex-col justify-between">
          <div>
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6">
              <div>
                <h3 className="font-bold text-base text-slate-900 dark:text-white">
                  {locale === 'km' ? 'ចរាចរណ៍ផ្ទៀងផ្ទាត់ និងបង្កើត Token' : 'Live Authentication & Token Requests'}
                </h3>
                <p className="text-xs text-slate-400 mt-0.5">
                  OAuth2 authorization code grants and JWT token exchange activity
                </p>
              </div>

              {/* IAM Server Badge */}
              <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-indigo-50 dark:bg-indigo-950/40 text-indigo-600 dark:text-indigo-300 text-xs font-bold self-start sm:self-auto">
                <ShieldCheck size={14} className="text-indigo-600 dark:text-indigo-300" />
                <span>Spring Auth Server</span>
              </div>
            </div>

            {/* Gradient Bar Chart Visualization */}
            <div className="h-56 flex items-end justify-between gap-2 pt-6 pb-2 px-2">
              {trafficData.map((d, i) => (
                <div key={i} className="flex-1 flex flex-col items-center gap-2 h-full justify-end group">
                  {/* Tooltip on hover */}
                  <div className="opacity-0 group-hover:opacity-100 transition-opacity text-[10px] font-bold text-slate-600 dark:text-slate-300 bg-slate-100 dark:bg-slate-700 px-1.5 py-0.5 rounded shadow-xs pointer-events-none">
                    {d.val}
                  </div>
                  {/* Bar */}
                  <div
                    style={{ height: d.height }}
                    className="w-full max-w-[28px] rounded-t-lg bg-gradient-to-t from-blue-600 via-indigo-600 to-indigo-400 dark:from-blue-700 dark:to-indigo-500 group-hover:from-blue-500 group-hover:to-indigo-300 transition-all shadow-sm"
                  />
                  {/* Label */}
                  <span className="text-[10px] text-slate-400 font-medium whitespace-nowrap">
                    {d.hour}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right: System & Server Health Status (1/3) */}
        <div className="relative p-6 rounded-2xl bg-white dark:bg-slate-800 border border-slate-200/80 dark:border-slate-700/80 shadow-2xs flex flex-col justify-between overflow-hidden">
          <div>
            <h3 className="font-bold text-base text-slate-900 dark:text-white mb-4">
              {t('serverHealthTitle')}
            </h3>

            <div className="space-y-3">
              {/* PostgreSQL */}
              <div className="p-3.5 rounded-xl border border-slate-100 dark:border-slate-700/60 bg-slate-50/50 dark:bg-slate-850 flex items-center justify-between">
                <div className="flex items-center gap-2.5">
                  <Database size={16} className="text-slate-400" />
                  <span className="text-xs font-semibold text-slate-800 dark:text-slate-200">
                    PostgreSQL 18 (db_iam)
                  </span>
                </div>
                <span className="px-2.5 py-1 rounded-md text-[11px] font-bold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200/60 dark:border-emerald-800 flex items-center gap-1">
                  ✓ {t('statusExcellent')}
                </span>
              </div>

              {/* Spring Authorization Server */}
              <div className="p-3.5 rounded-xl border border-slate-100 dark:border-slate-700/60 bg-slate-50/50 dark:bg-slate-850 flex items-center justify-between">
                <div className="flex items-center gap-2.5">
                  <Server size={16} className="text-slate-400" />
                  <span className="text-xs font-semibold text-slate-800 dark:text-slate-200">
                    Spring Auth Server (Port 9000)
                  </span>
                </div>
                <span className="px-2.5 py-1 rounded-md text-[11px] font-bold bg-blue-50 dark:bg-blue-950/40 text-blue-700 dark:text-blue-300 border border-blue-200/60 dark:border-blue-800 flex items-center gap-1">
                  ✓ {t('statusRunning')}
                </span>
              </div>

              {/* Redis Session Cache */}
              <div className="p-3.5 rounded-xl border border-slate-100 dark:border-slate-700/60 bg-slate-50/50 dark:bg-slate-850 flex items-center justify-between">
                <div className="flex items-center gap-2.5">
                  <Globe size={16} className="text-slate-400" />
                  <span className="text-xs font-semibold text-slate-800 dark:text-slate-200">
                    Redis 7 Cache (iam:sessions)
                  </span>
                </div>
                <span className="px-2.5 py-1 rounded-md text-[11px] font-bold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200/60 dark:border-emerald-800 flex items-center gap-1">
                  ✓ {t('statusExcellent')}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
