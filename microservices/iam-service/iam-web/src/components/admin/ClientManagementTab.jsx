import React, { useState, useEffect } from 'react';
import { Layers, Plus, Trash2, Key, Globe, Shield, RefreshCw } from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function ClientManagementTab() {
  const { locale } = useLanguage();
  const [clients, setClients] = useState([]);
  const [createModal, setCreateModal] = useState(false);
  const [newClient, setNewClient] = useState({
    clientId: '',
    clientName: '',
    grantTypes: ['authorization_code', 'refresh_token'],
    redirectUris: '',
    scopes: 'openid, profile, email',
    accessTokenLifetime: 3600,
    refreshTokenLifetime: 86400,
  });

  const loadData = async () => {
    const data = await adminService.getClients();
    setClients(data);
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    const created = await adminService.createClient({
      clientId: newClient.clientId,
      clientName: newClient.clientName,
      clientSecret: `sec-${Math.random().toString(36).substring(2, 12)}••••`,
      grantTypes: newClient.grantTypes,
      redirectUris: newClient.redirectUris.split(',').map((u) => u.trim()).filter(Boolean),
      scopes: newClient.scopes.split(',').map((s) => s.trim()).filter(Boolean),
      accessTokenLifetime: Number(newClient.accessTokenLifetime),
      refreshTokenLifetime: Number(newClient.refreshTokenLifetime),
    });
    setClients([...clients, created]);
    setCreateModal(false);
    setNewClient({
      clientId: '',
      clientName: '',
      grantTypes: ['authorization_code', 'refresh_token'],
      redirectUris: '',
      scopes: 'openid, profile, email',
      accessTokenLifetime: 3600,
      refreshTokenLifetime: 86400,
    });
  };

  const handleDelete = async (id) => {
    if (window.confirm('Delete this OAuth2 client? Connected clients will lose access immediately.')) {
      await adminService.deleteClient(id);
      setClients(clients.filter((c) => c.id !== id));
    }
  };

  const toggleGrant = (grant) => {
    if (newClient.grantTypes.includes(grant)) {
      setNewClient({ ...newClient, grantTypes: newClient.grantTypes.filter((g) => g !== grant) });
    } else {
      setNewClient({ ...newClient, grantTypes: [...newClient.grantTypes, grant] });
    }
  };

  return (
    <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-100">
      <div className="flex flex-col md:flex-row justify-between md:items-center gap-4 mb-6">
        <div>
          <h2 className="text-xl font-bold text-slate-900">
            {locale === 'km' ? 'ការគ្រប់គ្រងអតិថិជន OAuth2 / OIDC' : 'OAuth2 / OpenID Connect Client Administration'}
          </h2>
          <p className="text-sm text-slate-500">
            {locale === 'km'
              ? 'ចុះឈ្មោះ និងកំណត់រចនាសម្ព័ន្ធ Grant Types, Redirect URIs, Scopes, និង Token Lifetimes'
              : 'Register consumer apps, manage secrets, configure callback URIs, and issue scoped tokens.'}
          </p>
        </div>
        <button
          onClick={() => setCreateModal(true)}
          className="flex items-center gap-2 px-4 py-2.5 bg-indigo-600 text-white rounded-xl text-sm font-semibold hover:bg-indigo-700 shadow-sm transition"
        >
          <Plus size={16} /> {locale === 'km' ? 'ចុះឈ្មោះកម្មវិធីថ្មី' : 'Register OAuth2 Client'}
        </button>
      </div>

      <div className="space-y-4">
        {clients.map((c) => (
          <div key={c.id} className="p-5 border rounded-2xl hover:border-slate-300 transition">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-3">
              <div>
                <h3 className="font-bold text-slate-900 text-base">{c.clientName}</h3>
                <div className="flex items-center gap-2 text-xs text-slate-500 font-mono mt-0.5">
                  <span>Client ID: <strong className="text-indigo-600">{c.clientId}</strong></span>
                  <span>•</span>
                  <span>Secret: {c.clientSecret}</span>
                </div>
              </div>
              <button
                onClick={() => handleDelete(c.id)}
                className="text-xs text-rose-600 border border-rose-200 hover:bg-rose-50 px-3 py-1.5 rounded-lg flex items-center gap-1 self-start sm:self-center"
              >
                <Trash2 size={13} /> Delete Client
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-3 pt-3 border-t text-xs">
              <div>
                <span className="font-semibold text-slate-500 block mb-1">Allowed Grants</span>
                <div className="flex flex-wrap gap-1">
                  {c.grantTypes.map((g) => (
                    <span key={g} className="px-2 py-0.5 bg-slate-100 rounded text-slate-700 font-mono">
                      {g}
                    </span>
                  ))}
                </div>
              </div>

              <div>
                <span className="font-semibold text-slate-500 block mb-1">Redirect URIs</span>
                <div className="space-y-0.5 font-mono text-slate-600 truncate">
                  {c.redirectUris.length ? c.redirectUris.join(', ') : 'None (Machine-to-machine)'}
                </div>
              </div>

              <div>
                <span className="font-semibold text-slate-500 block mb-1">Token TTL / Scopes</span>
                <div className="text-slate-600">
                  Access: {c.accessTokenLifetime}s • Scopes: [{c.scopes.join(', ')}]
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Modal */}
      {createModal && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white p-6 rounded-2xl max-w-lg w-full shadow-2xl">
            <h3 className="text-lg font-bold text-slate-900 mb-4">Register New OAuth2 / OIDC Client</h3>
            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="text-xs font-semibold text-slate-600">Client Name *</label>
                <input
                  required
                  type="text"
                  placeholder="e.g. Student Mobile App"
                  value={newClient.clientName}
                  onChange={(e) => setNewClient({ ...newClient, clientName: e.target.value })}
                  className="w-full px-3 py-2 text-sm border rounded-lg outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600">Client ID *</label>
                <input
                  required
                  type="text"
                  placeholder="e.g. student-mobile-app"
                  value={newClient.clientId}
                  onChange={(e) => setNewClient({ ...newClient, clientId: e.target.value })}
                  className="w-full px-3 py-2 text-sm border rounded-lg outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600 block mb-1.5">Grant Types</label>
                <div className="flex flex-wrap gap-2 text-xs">
                  {['authorization_code', 'client_credentials', 'refresh_token'].map((gt) => (
                    <label key={gt} className="flex items-center gap-1.5 p-2 border rounded-lg cursor-pointer">
                      <input
                        type="checkbox"
                        checked={newClient.grantTypes.includes(gt)}
                        onChange={() => toggleGrant(gt)}
                      />
                      <span>{gt}</span>
                    </label>
                  ))}
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600">Redirect URIs (comma-separated)</label>
                <input
                  type="text"
                  placeholder="https://example.com/oauth2/callback, https://dev.example.com/cb"
                  value={newClient.redirectUris}
                  onChange={(e) => setNewClient({ ...newClient, redirectUris: e.target.value })}
                  className="w-full px-3 py-2 text-sm border rounded-lg outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-600">Access Token TTL (sec)</label>
                  <input
                    type="number"
                    value={newClient.accessTokenLifetime}
                    onChange={(e) => setNewClient({ ...newClient, accessTokenLifetime: e.target.value })}
                    className="w-full px-3 py-2 text-sm border rounded-lg outline-none"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-600">Refresh Token TTL (sec)</label>
                  <input
                    type="number"
                    value={newClient.refreshTokenLifetime}
                    onChange={(e) => setNewClient({ ...newClient, refreshTokenLifetime: e.target.value })}
                    className="w-full px-3 py-2 text-sm border rounded-lg outline-none"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setCreateModal(false)}
                  className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-sm bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700"
                >
                  Register Client
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
