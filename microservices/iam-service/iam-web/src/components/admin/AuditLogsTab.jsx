import React, { useState, useEffect } from 'react';
import { Activity, Search, Filter, ShieldCheck, UserCheck, Key, Settings } from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function AuditLogsTab() {
  const { locale } = useLanguage();
  const [logs, setLogs] = useState([]);
  const [selectedAction, setSelectedAction] = useState('ALL');
  const [search, setSearch] = useState('');

  useEffect(() => {
    adminService.getAuditLogs({ action: selectedAction }).then(setLogs);
  }, [selectedAction]);

  const getActionBadge = (action) => {
    switch (action) {
      case 'ASSIGN_ROLE':
        return <span className="px-2 py-0.5 rounded text-xs font-semibold bg-purple-100 text-purple-700">ASSIGN_ROLE</span>;
      case 'LOCK_USER':
        return <span className="px-2 py-0.5 rounded text-xs font-semibold bg-rose-100 text-rose-700">LOCK_USER</span>;
      case 'CREATE_CLIENT':
        return <span className="px-2 py-0.5 rounded text-xs font-semibold bg-indigo-100 text-indigo-700">CREATE_CLIENT</span>;
      case 'CHANGE_PASSWORD':
        return <span className="px-2 py-0.5 rounded text-xs font-semibold bg-amber-100 text-amber-700">CHANGE_PASSWORD</span>;
      case 'CREATE_PERMISSION':
        return <span className="px-2 py-0.5 rounded text-xs font-semibold bg-emerald-100 text-emerald-700">CREATE_PERMISSION</span>;
      default:
        return <span className="px-2 py-0.5 rounded text-xs font-semibold bg-slate-100 text-slate-700">{action}</span>;
    }
  };

  const filtered = logs.filter(
    (l) =>
      l.actor.toLowerCase().includes(search.toLowerCase()) ||
      l.target.toLowerCase().includes(search.toLowerCase()) ||
      l.details.toLowerCase().includes(search.toLowerCase()) ||
      l.ip.includes(search)
  );

  return (
    <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-100">
      <div className="flex flex-col md:flex-row justify-between md:items-center gap-4 mb-6">
        <div>
          <h2 className="text-xl font-bold text-slate-900">
            {locale === 'km' ? 'កំណត់ហេតុសវនកម្មប្រព័ន្ធ (Audit Logs)' : 'Compliance & System Audit Logs'}
          </h2>
          <p className="text-sm text-slate-500">
            {locale === 'km'
              ? 'ត្រួតពិនិត្យ និងចម្រោះរាល់សកម្មភាពកែប្រែអ្នកប្រើប្រាស់ តួនាទី សិទ្ធិ និងការចូលប្រព័ន្ធ'
              : 'Immutable record of administrative operations, identity events, and RBAC policy updates.'}
          </p>
        </div>
      </div>

      <div className="flex flex-col sm:flex-row gap-3 mb-6">
        <input
          type="text"
          placeholder="Filter by actor, target, IP, or details..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full sm:w-80 px-3.5 py-2 text-sm border rounded-xl outline-none focus:ring-2 focus:ring-indigo-500"
        />
        <select
          value={selectedAction}
          onChange={(e) => setSelectedAction(e.target.value)}
          className="px-3.5 py-2 text-sm border rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 text-slate-700"
        >
          <option value="ALL">All Event Types</option>
          <option value="ASSIGN_ROLE">ASSIGN_ROLE</option>
          <option value="LOCK_USER">LOCK_USER</option>
          <option value="CREATE_CLIENT">CREATE_CLIENT</option>
          <option value="CHANGE_PASSWORD">CHANGE_PASSWORD</option>
          <option value="CREATE_PERMISSION">CREATE_PERMISSION</option>
        </select>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm text-slate-600">
          <thead className="bg-slate-50 text-slate-500 uppercase text-xs font-semibold">
            <tr>
              <th className="px-4 py-3">Timestamp</th>
              <th className="px-4 py-3">Actor</th>
              <th className="px-4 py-3">Action Event</th>
              <th className="px-4 py-3">Target Subject</th>
              <th className="px-4 py-3">Audit Details</th>
              <th className="px-4 py-3">Origin IP</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {filtered.map((log) => (
              <tr key={log.id} className="hover:bg-slate-50/60">
                <td className="px-4 py-3.5 text-xs font-mono text-slate-700 whitespace-nowrap">{log.timestamp}</td>
                <td className="px-4 py-3.5 font-semibold text-slate-900">@{log.actor}</td>
                <td className="px-4 py-3.5">{getActionBadge(log.action)}</td>
                <td className="px-4 py-3.5 font-mono text-xs text-indigo-700">{log.target}</td>
                <td className="px-4 py-3.5 text-xs text-slate-600 max-w-sm">{log.details}</td>
                <td className="px-4 py-3.5 font-mono text-xs text-slate-400">{log.ip}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
