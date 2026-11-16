import React, { useState, useEffect } from 'react';
import {
  Layers,
  Search,
  Plus,
  Shield,
  Trash2,
  Pencil,
  ExternalLink,
  Check,
  X,
  Lock,
  Globe,
  Radio,
  CheckCircle2,
  MoreHorizontal,
  Copy,
  AlertCircle,
  Key,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function ApplicationsView() {
  const { locale } = useLanguage();
  const [clients, setClients] = useState([]);
  const [search, setSearch] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 6;
  const [showModal, setShowModal] = useState(false);
  const [editingClient, setEditingClient] = useState(null);
  const [openActionMenuId, setOpenActionMenuId] = useState(null);
  const [actionMenuState, setActionMenuState] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [saving, setSaving] = useState(false);
  const [newlyCreatedSecret, setNewlyCreatedSecret] = useState(null);
  const [showSecretInput, setShowSecretInput] = useState(false);

  // Form State matching Screenshots with practical defaults
  const initialForm = {
    name: '',
    clientId: '',
    clientSecret: '',
    type: 'Confidential',
    grantTypes: ['Authorization code', 'Refresh token'],
    redirectUris: ['http://localhost:3000/callback'],
    signoutUris: [],
    scopes: ['openid', 'profile', 'email'],
    consent: false,
  };

  const [formData, setFormData] = useState(initialForm);
  const [newRedirect, setNewRedirect] = useState('');
  const [newScope, setNewScope] = useState('');

  const saveCachedSecret = (clientId, secret) => {
    if (!clientId || !secret) return;
    try {
      const raw = localStorage.getItem('iam_cached_client_secrets') || '{}';
      const parsed = JSON.parse(raw);
      parsed[clientId] = secret;
      localStorage.setItem('iam_cached_client_secrets', JSON.stringify(parsed));
    } catch {}
  };

  const getCachedSecret = (clientId) => {
    if (!clientId) return null;
    try {
      const raw = localStorage.getItem('iam_cached_client_secrets') || '{}';
      return JSON.parse(raw)[clientId] || null;
    } catch {
      return null;
    }
  };

  const generateRandomSecret = () => {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-~';
    let sec = 'sec_';
    for (let i = 0; i < 32; i++) {
      sec += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    setFormData((prev) => ({ ...prev, clientSecret: sec }));
    setShowSecretInput(true);
  };

  const loadClients = async () => {
    const data = await adminService.getClients();
    setClients(data);
  };

  useEffect(() => {
    loadClients();
  }, []);

  const openRegisterModal = () => {
    setEditingClient(null);
    setFormData({
      name: '',
      clientId: '',
      clientSecret: '',
      type: 'Confidential',
      grantTypes: ['Authorization code', 'Refresh token'],
      redirectUris: ['http://localhost:3000/callback'],
      signoutUris: [],
      scopes: ['openid', 'profile', 'email'],
      consent: false,
    });
    setNewRedirect('');
    setNewScope('');
    setShowSecretInput(false);
    setErrorMessage('');
    setShowModal(true);
  };

  const openEditModal = (client) => {
    setEditingClient(client);
    setFormData({
      name: client.name || '',
      clientId: client.clientId || '',
      clientSecret: '',
      type: client.type || 'Confidential',
      grantTypes: Array.isArray(client.grantTypes) ? [...client.grantTypes] : ['Authorization code'],
      redirectUris: Array.isArray(client.redirectUris) && client.redirectUris.length > 0 ? [...client.redirectUris] : ['http://localhost:3000/callback'],
      signoutUris: Array.isArray(client.signoutUris) ? [...client.signoutUris] : [],
      scopes: Array.isArray(client.scopes) ? [...client.scopes] : ['openid'],
      consent: Boolean(client.consent),
    });
    setNewRedirect('');
    setNewScope('');
    setShowSecretInput(false);
    setErrorMessage('');
    setShowModal(true);
  };

  const handleToggleGrant = (gt) => {
    setFormData((prev) => {
      const exists = prev.grantTypes.includes(gt);
      return {
        ...prev,
        grantTypes: exists ? prev.grantTypes.filter((g) => g !== gt) : [...prev.grantTypes, gt],
      };
    });
  };

  const handleAddRedirect = (e) => {
    if (e) e.preventDefault();
    if (newRedirect.trim()) {
      if (!formData.redirectUris.includes(newRedirect.trim())) {
        setFormData((prev) => ({ ...prev, redirectUris: [...prev.redirectUris, newRedirect.trim()] }));
      }
      setNewRedirect('');
    }
  };

  const handleRemoveRedirect = (indexToRemove) => {
    setFormData((prev) => ({
      ...prev,
      redirectUris: prev.redirectUris.filter((_, idx) => idx !== indexToRemove),
    }));
  };

  const handleAddScope = (scope) => {
    if (scope && !formData.scopes.includes(scope)) {
      setFormData((prev) => ({ ...prev, scopes: [...prev.scopes, scope] }));
    }
  };

  const handleAddCustomScope = (e) => {
    if (e) e.preventDefault();
    if (newScope.trim()) {
      handleAddScope(newScope.trim().toLowerCase());
      setNewScope('');
    }
  };

  const handleRemoveScope = (scope) => {
    setFormData((prev) => ({ ...prev, scopes: prev.scopes.filter((s) => s !== scope) }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    if (!formData.name.trim() || !formData.clientId.trim()) return;

    setSaving(true);
    setErrorMessage('');
    try {
      const redirectList = (formData.redirectUris || [])
        .map((u) => u.trim())
        .filter(Boolean);
      if (redirectList.length === 0 && formData.grantTypes.some(g => g.toLowerCase().includes('code'))) {
        redirectList.push('http://localhost:3000/callback');
      }

      // Filter grant types: Public clients cannot have client_credentials
      let validGrants = [...formData.grantTypes];
      if (formData.type === 'Public') {
        validGrants = validGrants.filter(g => !g.toLowerCase().includes('credential'));
        if (validGrants.length === 0) validGrants = ['Authorization code', 'Refresh token'];
      }

      if (editingClient) {
        const payload = {
          name: formData.name.trim(),
          clientId: formData.clientId.trim(),
          type: formData.type,
          grantTypes: validGrants,
          redirectUris: redirectList,
          scopes: formData.scopes,
          consent: formData.consent,
        };
        if (formData.type !== 'Public' && formData.clientSecret && formData.clientSecret.trim()) {
          payload.clientSecret = formData.clientSecret.trim();
          saveCachedSecret(editingClient.clientId, payload.clientSecret);
        }
        const updated = await adminService.updateClient(editingClient.id, payload);
        setClients((prev) => prev.map((c) => (c.id === editingClient.id ? { ...c, ...updated } : c)));
        setSuccessMessage('Application updated successfully!');
        setShowModal(false);
      } else {
        const payload = {
          name: formData.name.trim(),
          clientId: formData.clientId.trim(),
          type: formData.type,
          grantTypes: validGrants,
          redirectUris: redirectList,
          scopes: formData.scopes,
          consent: formData.consent,
        };
        if (formData.type !== 'Public' && formData.clientSecret && formData.clientSecret.trim()) {
          payload.clientSecret = formData.clientSecret.trim();
          saveCachedSecret(payload.clientId, payload.clientSecret);
        }
        const created = await adminService.createClient(payload);
        setClients((prev) => [created, ...prev]);
        setShowModal(false);
        if (created.clientSecret) {
          saveCachedSecret(created.clientId, created.clientSecret);
          setNewlyCreatedSecret({
            name: created.name,
            clientId: created.clientId,
            clientSecret: created.clientSecret,
          });
        } else {
          setSuccessMessage('Application registered successfully!');
        }
      }
      setTimeout(() => setSuccessMessage(''), 3500);
    } catch (err) {
      console.error('Failed to save client:', err);
      const msg =
        err.response?.data?.errors?.[0]?.message ||
        err.response?.data?.detail ||
        err.response?.data?.message ||
        err.message ||
        'Failed to save application';
      setErrorMessage(msg);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id, e) => {
    if (e) e.stopPropagation();
    if (window.confirm('Are you sure you want to delete this OAuth2 client application?')) {
      try {
        await adminService.deleteClient(id);
        setClients((prev) => prev.filter((c) => c.id !== id && c.clientId !== id));
        setSuccessMessage('Application deleted successfully.');
        setTimeout(() => setSuccessMessage(''), 3000);
      } catch (err) {
        console.error('Failed to delete client:', err);
        setErrorMessage('Failed to delete application: ' + (err.response?.data?.message || err.message));
        setTimeout(() => setErrorMessage(''), 4000);
      }
    }
  };

  const sortedClients = [...clients].sort((a, b) => {
    const timeA = a.clientIdIssuedAt ? new Date(a.clientIdIssuedAt).getTime() : 0;
    const timeB = b.clientIdIssuedAt ? new Date(b.clientIdIssuedAt).getTime() : 0;
    if (timeA !== timeB) return timeB - timeA;
    return (b.id || '').localeCompare(a.id || '');
  });

  const filteredClients = sortedClients.filter(
    (c) =>
      c.name.toLowerCase().includes(search.toLowerCase()) ||
      c.clientId.toLowerCase().includes(search.toLowerCase())
  );

  const totalPages = Math.max(1, Math.ceil(filteredClients.length / pageSize));
  const paginatedClients = filteredClients.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  return (
    <div className="space-y-6 w-full">
      {/* Toast Notification */}
      {successMessage && (
        <div className="p-4 rounded-2xl bg-emerald-50 dark:bg-emerald-950/30 border border-emerald-200 dark:border-emerald-800 text-emerald-800 dark:text-emerald-200 text-xs font-semibold flex items-center justify-between shadow-xs">
          <div className="flex items-center gap-2">
            <CheckCircle2 size={16} className="text-emerald-600 dark:text-emerald-400" />
            <span>{successMessage}</span>
          </div>
          <button
            onClick={() => setSuccessMessage('')}
            className="text-emerald-500 hover:text-emerald-700 cursor-pointer"
          >
            ✕
          </button>
        </div>
      )}

      {errorMessage && (
        <div className="p-4 rounded-2xl bg-rose-50 dark:bg-rose-950/30 border border-rose-200 dark:border-rose-800 text-rose-800 dark:text-rose-200 text-xs font-semibold flex items-center justify-between shadow-xs">
          <div className="flex items-center gap-2">
            <AlertCircle size={16} className="text-rose-600 dark:text-rose-400" />
            <span>{errorMessage}</span>
          </div>
          <button
            onClick={() => setErrorMessage('')}
            className="text-rose-500 hover:text-rose-700 cursor-pointer"
          >
            ✕
          </button>
        </div>
      )}

      {/* 1. Page Header matching Screenshot 9 */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {locale === 'km' ? 'កម្មវិធីអតិថិជន (OAuth2)' : 'Applications'}
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            {locale === 'km'
              ? 'កម្មវិធីដែលអនុញ្ញាតឱ្យអ្នកប្រើប្រាស់ចូលគណនី ISTAD របស់ពួកគេ (OAuth 2 និង OpenID Connect)។'
              : 'Apps that sign people in with their ISTAD account (OAuth 2 and OpenID Connect clients).'}
          </p>
        </div>

        <button
          onClick={openRegisterModal}
          className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-indigo-900 hover:bg-indigo-950 text-white text-xs font-bold shadow-sm transition cursor-pointer"
        >
          <Plus size={15} />
          <span>{locale === 'km' ? 'ចុះឈ្មោះកម្មវិធីថ្មី' : 'Register application'}</span>
        </button>
      </div>

      {/* 2. Search Bar */}
      <div className="relative w-full sm:w-96">
        <Search size={15} className="absolute left-3.5 top-3 text-slate-400" />
        <input
          type="text"
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setCurrentPage(1);
          }}
          placeholder={locale === 'km' ? 'ស្វែងរកតាមឈ្មោះ ឬ Client ID...' : 'Search by name or client ID...'}
          className="w-full pl-9 pr-4 py-2 rounded-xl border border-slate-200/80 dark:border-slate-800 bg-white dark:bg-slate-900 text-xs outline-none focus:ring-2 focus:ring-indigo-500 text-slate-800 dark:text-slate-100"
        />
      </div>

      {/* 3. Applications Table matching Screenshot 9 */}
      <div className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-200/80 dark:border-slate-800 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-slate-100 dark:border-slate-800 text-slate-400 font-semibold bg-slate-50/50 dark:bg-slate-900/50">
                <th className="py-3 px-6">{locale === 'km' ? 'កម្មវិធី' : 'Application'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'ប្រភេទ' : 'Type'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'ទម្រង់ Grant' : 'Grant types'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'កាលបរិច្ឆេទចុះឈ្មោះ' : 'Registered'}</th>
                <th className="py-3 px-6 text-right">{locale === 'km' ? 'សកម្មភាព' : 'Actions'}</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-slate-600 dark:text-slate-300">
              {filteredClients.length === 0 ? (
                <tr>
                  <td colSpan={5} className="py-8 text-center text-slate-400">
                    No applications found. Click "Register application" to add one.
                  </td>
                </tr>
              ) : (
                paginatedClients.map((client) => (
                  <tr
                    key={client.id}
                    onClick={() => openEditModal(client)}
                    className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition cursor-pointer"
                  >
                    {/* Name & ClientId */}
                    <td className="py-4 px-6">
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-xl bg-indigo-50 dark:bg-indigo-950/40 text-indigo-600 flex items-center justify-center shrink-0">
                          <Layers size={18} />
                        </div>
                        <div>
                          <div className="font-bold text-slate-900 dark:text-white text-sm">
                            {client.name}
                          </div>
                          <div className="font-mono text-[11px] text-slate-400">
                            {client.clientId}
                          </div>
                        </div>
                      </div>
                    </td>

                    {/* Type */}
                    <td className="py-4 px-4">
                      <span className="px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border border-slate-200/80 dark:border-slate-700">
                        {client.type}
                      </span>
                    </td>

                    {/* Grant Types */}
                    <td className="py-4 px-4">
                      <div className="flex items-center gap-1.5 flex-wrap max-w-xs">
                        {client.grantTypes.map((gt, idx) => (
                          <span
                            key={idx}
                            className="px-2 py-0.5 rounded-md text-[10px] font-medium bg-slate-50 dark:bg-slate-800/80 text-slate-600 dark:text-slate-400 border border-slate-200/60 dark:border-slate-700/60"
                          >
                            {gt}
                          </span>
                        ))}
                      </div>
                    </td>

                    {/* Registered Date */}
                    <td className="py-4 px-4 text-slate-400">
                      {client.registered}
                    </td>

                    {/* Actions */}
                    <td className="py-4 px-6 text-right relative">
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          if (actionMenuState?.client?.id === client.id) {
                            setActionMenuState(null);
                          } else {
                            const rect = e.currentTarget.getBoundingClientRect();
                            const spaceBelow = window.innerHeight - rect.bottom;
                            const openUpwards = spaceBelow < 200;
                            setActionMenuState({
                              client: client,
                              coords: {
                                top: openUpwards ? undefined : rect.bottom + 6,
                                bottom: openUpwards ? window.innerHeight - rect.top + 6 : undefined,
                                right: window.innerWidth - rect.right,
                              },
                            });
                          }
                        }}
                        className={`p-1.5 rounded-xl transition cursor-pointer ${
                          actionMenuState?.client?.id === client.id
                            ? 'bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400'
                            : 'text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800'
                        }`}
                        title="Actions"
                      >
                        <MoreHorizontal size={16} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>

          {/* Pagination Controls */}
          {filteredClients.length > pageSize && (
            <div className="flex flex-col sm:flex-row items-center justify-between gap-3 px-6 py-3.5 border-t border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-900/50">
              <div className="text-xs text-slate-500 dark:text-slate-400">
                {locale === 'km' ? (
                  <>
                    បង្ហាញ <span className="font-semibold text-slate-700 dark:text-slate-200">{(currentPage - 1) * pageSize + 1}</span> ដល់{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{Math.min(currentPage * pageSize, filteredClients.length)}</span> នៃ{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{filteredClients.length}</span> កម្មវិធី
                  </>
                ) : (
                  <>
                    Showing <span className="font-semibold text-slate-700 dark:text-slate-200">{(currentPage - 1) * pageSize + 1}</span> to{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{Math.min(currentPage * pageSize, filteredClients.length)}</span> of{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{filteredClients.length}</span> applications
                  </>
                )}
              </div>

              <div className="flex items-center gap-1.5">
                <button
                  type="button"
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                  className="p-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-300 disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-50 dark:hover:bg-slate-700 transition"
                  title="Previous page"
                >
                  <ChevronLeft size={16} />
                </button>

                {Array.from({ length: totalPages }, (_, i) => i + 1).map((pg) => (
                  <button
                    key={pg}
                    type="button"
                    onClick={() => setCurrentPage(pg)}
                    className={`min-w-8 h-8 rounded-xl text-xs font-bold transition ${
                      currentPage === pg
                        ? 'bg-indigo-600 text-white shadow-xs'
                        : 'border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700'
                    }`}
                  >
                    {pg}
                  </button>
                ))}

                <button
                  type="button"
                  disabled={currentPage === totalPages}
                  onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                  className="p-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-300 disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-50 dark:hover:bg-slate-700 transition"
                  title="Next page"
                >
                  <ChevronRight size={16} />
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Floating Action Menu (Fixed positioning so it never gets clipped) */}
      {actionMenuState && (
        <>
          <div
            className="fixed inset-0 z-40 cursor-default"
            onClick={() => setActionMenuState(null)}
          />
          <div
            style={{
              position: 'fixed',
              top: actionMenuState.coords.top,
              bottom: actionMenuState.coords.bottom,
              right: actionMenuState.coords.right,
            }}
            className="w-48 bg-white dark:bg-slate-900 rounded-2xl shadow-2xl border border-slate-200/90 dark:border-slate-800 p-1.5 z-50 space-y-0.5 text-left animate-in fade-in-0 zoom-in-95 select-none"
          >
            <button
              type="button"
              onClick={(e) => {
                const c = actionMenuState.client;
                setActionMenuState(null);
                openEditModal(c);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition cursor-pointer"
            >
              <Pencil size={14} className="text-indigo-600 dark:text-indigo-400" />
              <span>Edit App</span>
            </button>
            <button
              type="button"
              onClick={(e) => {
                const c = actionMenuState.client;
                setActionMenuState(null);
                navigator.clipboard?.writeText(c.clientId);
                setSuccessMessage(`Copied Client ID: ${c.clientId}`);
                setTimeout(() => setSuccessMessage(''), 3000);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition cursor-pointer"
            >
              <Copy size={14} className="text-slate-500" />
              <span>Copy Client ID</span>
            </button>
            {actionMenuState.client.type === 'Confidential' && getCachedSecret(actionMenuState.client.clientId) && (
              <button
                type="button"
                onClick={() => {
                  const c = actionMenuState.client;
                  const sec = getCachedSecret(c.clientId);
                  setActionMenuState(null);
                  navigator.clipboard?.writeText(sec);
                  setSuccessMessage(`Copied saved secret for ${c.name}!`);
                  setTimeout(() => setSuccessMessage(''), 3000);
                }}
                className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-emerald-600 dark:text-emerald-400 hover:bg-emerald-50 dark:hover:bg-emerald-950/30 transition cursor-pointer"
              >
                <Key size={14} />
                <span>Copy Saved Secret</span>
              </button>
            )}
            {actionMenuState.client.type === 'Confidential' && (
              <button
                type="button"
                onClick={async () => {
                  const c = actionMenuState.client;
                  setActionMenuState(null);
                  if (window.confirm(`Generate a new secret for ${c.name}? Existing applications using the old secret will need to update.`)) {
                    try {
                      const updated = await adminService.rotateClientSecret(c.id);
                      if (updated.clientSecret) {
                        saveCachedSecret(updated.clientId, updated.clientSecret);
                      }
                      setNewlyCreatedSecret({
                        name: updated.name,
                        clientId: updated.clientId,
                        clientSecret: updated.clientSecret,
                      });
                    } catch (err) {
                      setErrorMessage('Failed to rotate secret: ' + (err.response?.data?.detail || err.message));
                    }
                  }
                }}
                className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-amber-600 dark:text-amber-400 hover:bg-amber-50 dark:hover:bg-amber-950/30 transition cursor-pointer"
              >
                <Key size={14} />
                <span>Rotate Secret</span>
              </button>
            )}
            <div className="h-px bg-slate-100 dark:bg-slate-800 my-1" />
            <button
              type="button"
              onClick={(e) => {
                const c = actionMenuState.client;
                setActionMenuState(null);
                handleDelete(c.id, e);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/30 transition cursor-pointer"
            >
              <Trash2 size={14} />
              <span>Delete App</span>
            </button>
          </div>
        </>
      )}

      {/* 4. Full Register & Edit Application Modal matching Screenshots */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-3xl max-w-2xl w-full max-h-[90vh] flex flex-col shadow-2xl border border-slate-200/80 dark:border-slate-800 my-auto overflow-hidden animate-in fade-in zoom-in-95 duration-150">
            {/* Sticky Header */}
            <div className="flex items-center justify-between p-6 pb-4 border-b border-slate-100 dark:border-slate-800 shrink-0">
              <div>
                <h3 className="font-extrabold text-xl text-slate-900 dark:text-white">
                  {editingClient ? 'Edit application' : 'Register application'}
                </h3>
                <p className="text-xs text-slate-500 mt-0.5">
                  {editingClient
                    ? 'Update OAuth 2 and OpenID Connect client settings.'
                    : 'Set up an app that signs people in with ISTAD.'}
                </p>
              </div>
              <button
                type="button"
                onClick={() => setShowModal(false)}
                className="p-1.5 rounded-xl text-slate-400 hover:text-slate-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition cursor-pointer"
              >
                <X size={20} />
              </button>
            </div>

            {/* Scrollable Form Body */}
            <form
              id="app-form"
              onSubmit={handleSave}
              className="flex-1 overflow-y-auto p-6 space-y-6 text-sm"
            >
              {/* Application Name & Client ID */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                    Name
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    placeholder="ISTAD LMS"
                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200/80 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs outline-none focus:ring-2 focus:ring-indigo-500 text-slate-900 dark:text-white"
                  />
                  <span className="text-[10px] text-slate-400 mt-1 block">
                    The name people see on the consent page.
                  </span>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                    Client ID
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.clientId}
                    onChange={(e) => setFormData({ ...formData, clientId: e.target.value })}
                    placeholder="istad-lms"
                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200/80 dark:border-slate-700 bg-white dark:bg-slate-800 font-mono text-xs outline-none focus:ring-2 focus:ring-indigo-500 text-slate-900 dark:text-white"
                  />
                  <span className="text-[10px] text-slate-400 mt-1 block">
                    Unique identifier for this OAuth client.
                  </span>
                </div>
              </div>

              {/* Type: Confidential vs Public matching Screenshot 10 */}
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-2">
                  Type
                </label>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {/* Confidential Card */}
                  <div
                    onClick={() => setFormData({ ...formData, type: 'Confidential' })}
                    className={`p-4 rounded-2xl border-2 cursor-pointer transition flex items-start gap-3 ${
                      formData.type === 'Confidential'
                        ? 'border-indigo-600 bg-indigo-50/40 dark:bg-indigo-950/20'
                        : 'border-slate-200/80 dark:border-slate-700 hover:border-slate-300'
                    }`}
                  >
                    <div className="w-9 h-9 rounded-xl bg-indigo-100 text-indigo-700 dark:bg-indigo-900/60 dark:text-indigo-300 flex items-center justify-center shrink-0">
                      <Lock size={18} />
                    </div>
                    <div>
                      <div className="font-bold text-xs text-slate-900 dark:text-white">
                        Confidential
                      </div>
                      <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5 leading-snug">
                        A server-side app that can keep a secret, e.g. a Spring Boot or Next.js backend.
                      </p>
                    </div>
                  </div>

                  {/* Public Card */}
                  <div
                    onClick={() => setFormData({ ...formData, type: 'Public' })}
                    className={`p-4 rounded-2xl border-2 cursor-pointer transition flex items-start gap-3 ${
                      formData.type === 'Public'
                        ? 'border-indigo-600 bg-indigo-50/40 dark:bg-indigo-950/20'
                        : 'border-slate-200/80 dark:border-slate-700 hover:border-slate-300'
                    }`}
                  >
                    <div className="w-9 h-9 rounded-xl bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300 flex items-center justify-center shrink-0">
                      <Globe size={18} />
                    </div>
                    <div>
                      <div className="font-bold text-xs text-slate-900 dark:text-white">
                        Public
                      </div>
                      <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5 leading-snug">
                        A browser or mobile app that cannot keep a secret. It must use PKCE.
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Client Secret (Confidential only) */}
              {formData.type === 'Confidential' && (
                <div>
                  <div className="flex items-center justify-between mb-1.5">
                    <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                      Client Secret {editingClient ? '(Leave blank to keep current)' : ''}
                    </label>
                    <button
                      type="button"
                      onClick={generateRandomSecret}
                      className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-indigo-50 hover:bg-indigo-100 dark:bg-indigo-950/60 dark:hover:bg-indigo-900/60 text-indigo-700 dark:text-indigo-300 text-[11px] font-semibold transition cursor-pointer"
                    >
                      <Key size={12} />
                      <span>{locale === 'km' ? 'បង្កើត Secret ស្វ័យប្រវត្តិ' : 'Auto-Generate Secret'}</span>
                    </button>
                  </div>
                  <div className="relative">
                    <input
                      type={showSecretInput ? 'text' : 'password'}
                      autoComplete="new-password"
                      value={formData.clientSecret || ''}
                      onChange={(e) => setFormData({ ...formData, clientSecret: e.target.value })}
                      placeholder={editingClient ? '••••••••••••••••' : 'Click Auto-Generate or enter your custom secret'}
                      className="w-full pl-3.5 pr-16 py-2.5 rounded-xl border border-slate-200/80 dark:border-slate-700 bg-white dark:bg-slate-800 font-mono text-xs outline-none focus:ring-2 focus:ring-indigo-500 text-slate-900 dark:text-white"
                    />
                    <button
                      type="button"
                      onClick={() => setShowSecretInput(!showSecretInput)}
                      className="absolute right-2.5 top-2.5 px-2 py-0.5 rounded text-[10px] font-medium text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-700 transition"
                    >
                      {showSecretInput ? 'Hide' : 'Show'}
                    </button>
                  </div>
                  <span className="text-[10px] text-slate-400 mt-1 block">
                    {editingClient
                      ? 'Enter a new secret to update it, or leave blank to keep current.'
                      : (locale === 'km'
                          ? 'សម្រាប់កម្មវិធី Confidential៖ Secret នេះប្រើសម្រាប់ផ្ទៀងផ្ទាត់ជាមួយ IAM Server (ចុច Auto-Generate ឬបញ្ចូលខ្លួនឯង)'
                          : 'Used by your backend server to authenticate with IAM. Click Auto-Generate or enter a custom secret.')}
                  </span>

                  {editingClient && getCachedSecret(editingClient.clientId) && (
                    <div className="mt-2.5 p-2.5 rounded-xl bg-slate-50 dark:bg-slate-800/80 border border-slate-200/80 dark:border-slate-700 flex items-center justify-between">
                      <div className="text-[11px] text-slate-500 truncate max-w-[320px]">
                        <span className="font-semibold text-slate-700 dark:text-slate-300">
                          {locale === 'km' ? 'Secret ដែលបានរក្សាទុក៖ ' : 'Saved Secret: '}
                        </span>
                        <span className="font-mono text-indigo-600 dark:text-indigo-400 font-medium">
                          {showSecretInput ? getCachedSecret(editingClient.clientId) : '••••••••••••••••'}
                        </span>
                      </div>
                      <button
                        type="button"
                        onClick={() => {
                          navigator.clipboard?.writeText(getCachedSecret(editingClient.clientId));
                          setSuccessMessage('Copied saved secret to clipboard!');
                          setTimeout(() => setSuccessMessage(''), 3000);
                        }}
                        className="text-xs font-bold text-indigo-600 dark:text-indigo-400 hover:underline flex items-center gap-1 cursor-pointer shrink-0 ml-2"
                      >
                        <Copy size={12} />
                        <span>Copy</span>
                      </button>
                    </div>
                  )}
                </div>
              )}

              {/* Grant Types Checkboxes matching Screenshot 11 */}
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-2">
                  Grant types
                </label>
                <div className="space-y-3">
                  <label className="flex items-start gap-3 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formData.grantTypes.includes('Authorization code')}
                      onChange={() => handleToggleGrant('Authorization code')}
                      className="mt-1 rounded text-indigo-600 focus:ring-indigo-500"
                    />
                    <div>
                      <div className="font-bold text-xs text-slate-800 dark:text-slate-200">
                        Authorization code
                      </div>
                      <div className="text-[11px] text-slate-500 dark:text-slate-400">
                        People sign in on the ISTAD page and come back to the app.
                      </div>
                    </div>
                  </label>

                  <label className="flex items-start gap-3 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formData.grantTypes.includes('Refresh token')}
                      onChange={() => handleToggleGrant('Refresh token')}
                      className="mt-1 rounded text-indigo-600 focus:ring-indigo-500"
                    />
                    <div>
                      <div className="font-bold text-xs text-slate-800 dark:text-slate-200">
                        Refresh token
                      </div>
                      <div className="text-[11px] text-slate-500 dark:text-slate-400">
                        Keeps people signed in without asking again.
                      </div>
                    </div>
                  </label>

                  <label className="flex items-start gap-3 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formData.grantTypes.includes('Client credentials')}
                      onChange={() => handleToggleGrant('Client credentials')}
                      className="mt-1 rounded text-indigo-600 focus:ring-indigo-500"
                    />
                    <div>
                      <div className="font-bold text-xs text-slate-800 dark:text-slate-200">
                        Client credentials
                      </div>
                      <div className="text-[11px] text-slate-500 dark:text-slate-400">
                        The app calls APIs as itself, with no person signed in.
                      </div>
                    </div>
                  </label>
                </div>
              </div>

              {/* Redirect URLs */}
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                  Redirect URLs
                </label>
                <div className="space-y-2">
                  {formData.redirectUris.map((uri, idx) => (
                    <div key={idx} className="flex items-center gap-2">
                      <input
                        type="text"
                        readOnly
                        value={uri}
                        className="flex-1 px-3.5 py-2 rounded-xl border border-slate-200/80 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-800 dark:text-slate-200 font-mono text-xs"
                      />
                      <button
                        type="button"
                        onClick={() => handleRemoveRedirect(idx)}
                        className="p-2 rounded-xl text-slate-400 hover:text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/30 transition cursor-pointer"
                        title="Remove URL"
                      >
                        <X size={15} />
                      </button>
                    </div>
                  ))}
                  <div className="flex items-center gap-2">
                    <input
                      type="url"
                      placeholder="https://app.istad.co/login/oauth2/code/istad"
                      value={newRedirect}
                      onChange={(e) => setNewRedirect(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          handleAddRedirect();
                        }
                      }}
                      className="flex-1 px-3.5 py-2 rounded-xl border border-slate-200/80 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white text-xs outline-none focus:ring-2 focus:ring-indigo-500"
                    />
                    <button
                      type="button"
                      onClick={handleAddRedirect}
                      className="px-3 py-2 rounded-xl border border-slate-200/80 dark:border-slate-700 text-xs font-bold hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300 transition cursor-pointer"
                    >
                      + Add
                    </button>
                  </div>
                </div>
              </div>

              {/* Scopes Tag Cloud matching Screenshot 11 */}
              <div>
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                  Scopes
                </label>
                <div className="p-3 rounded-2xl border border-slate-200/80 dark:border-slate-700 flex items-center gap-2 flex-wrap min-h-12 bg-white dark:bg-slate-800/40">
                  {formData.scopes.map((sc) => (
                    <span
                      key={sc}
                      className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200 font-mono text-xs border border-slate-200/60 dark:border-slate-700"
                    >
                      <span>{sc}</span>
                      <button
                        type="button"
                        onClick={() => handleRemoveScope(sc)}
                        className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 cursor-pointer"
                      >
                        ✕
                      </button>
                    </span>
                  ))}
                </div>

                {/* Add custom scope */}
                <div className="flex items-center gap-2 mt-2">
                  <input
                    type="text"
                    placeholder="Enter custom scope (e.g. read:messages)"
                    value={newScope}
                    onChange={(e) => setNewScope(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                        handleAddCustomScope();
                      }
                    }}
                    className="flex-1 px-3 py-1.5 rounded-xl border border-slate-200/80 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                  <button
                    type="button"
                    onClick={handleAddCustomScope}
                    className="px-3 py-1.5 rounded-xl border border-slate-200/80 dark:border-slate-700 text-xs font-semibold hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300 cursor-pointer"
                  >
                    + Add Scope
                  </button>
                </div>

                <div className="flex items-center gap-2 mt-2 text-[11px] text-slate-400 flex-wrap">
                  <span>Suggestions:</span>
                  <button
                    type="button"
                    onClick={() => handleAddScope('phone')}
                    className="hover:text-indigo-600 font-semibold cursor-pointer"
                  >
                    + phone
                  </button>
                  <button
                    type="button"
                    onClick={() => handleAddScope('address')}
                    className="hover:text-indigo-600 font-semibold cursor-pointer"
                  >
                    + address
                  </button>
                  <button
                    type="button"
                    onClick={() => handleAddScope('offline_access')}
                    className="hover:text-indigo-600 font-semibold cursor-pointer"
                  >
                    + offline_access
                  </button>
                  <button
                    type="button"
                    onClick={() => handleAddScope('roles')}
                    className="hover:text-indigo-600 font-semibold cursor-pointer"
                  >
                    + roles
                  </button>
                </div>
              </div>

              {/* Consent Toggle */}
              <div className="flex items-center justify-between pt-2 border-t border-slate-100 dark:border-slate-800">
                <div>
                  <div className="font-bold text-xs text-slate-800 dark:text-slate-200">
                    Ask people for consent
                  </div>
                  <div className="text-[11px] text-slate-400">
                    Show a page where people approve what the app may access.
                  </div>
                </div>
                <button
                  type="button"
                  onClick={() => setFormData({ ...formData, consent: !formData.consent })}
                  className={`w-11 h-6 rounded-full transition-colors relative cursor-pointer ${
                    formData.consent ? 'bg-indigo-600' : 'bg-slate-300 dark:bg-slate-700'
                  }`}
                >
                  <span
                    className={`block w-4 h-4 rounded-full bg-white transition-transform absolute top-1 ${
                      formData.consent ? 'left-6' : 'left-1'
                    }`}
                  />
                </button>
              </div>
            </form>

            {/* Sticky Actions Footer */}
            <div className="flex items-center justify-end gap-3 p-4 px-6 border-t border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-900/50 shrink-0">
              <button
                type="button"
                onClick={() => setShowModal(false)}
                className="px-4 py-2.5 rounded-xl border border-slate-200/80 dark:border-slate-700 text-xs font-semibold hover:bg-slate-100 dark:hover:bg-slate-800 transition cursor-pointer text-slate-700 dark:text-slate-300"
              >
                Cancel
              </button>
              <button
                type="submit"
                form="app-form"
                disabled={saving}
                className="px-5 py-2.5 rounded-xl bg-indigo-900 hover:bg-indigo-950 text-white text-xs font-bold shadow-sm transition cursor-pointer disabled:opacity-50"
              >
                {saving ? 'Saving...' : editingClient ? 'Save Changes' : 'Register'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 5. Secret Revelation Modal */}
      {newlyCreatedSecret && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-3xl max-w-lg w-full p-6 shadow-2xl border border-slate-200/80 dark:border-slate-800 space-y-5 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-2xl bg-emerald-50 dark:bg-emerald-950/50 text-emerald-600 dark:text-emerald-400 flex items-center justify-center shrink-0">
                <CheckCircle2 size={24} />
              </div>
              <div>
                <h3 className="font-extrabold text-lg text-slate-900 dark:text-white">
                  Application Registered!
                </h3>
                <p className="text-xs text-slate-500">
                  {newlyCreatedSecret.name} ({newlyCreatedSecret.clientId})
                </p>
              </div>
            </div>

            <div className="p-3.5 rounded-2xl bg-amber-50 dark:bg-amber-950/30 border border-amber-200/80 dark:border-amber-800/60 text-amber-800 dark:text-amber-200 text-xs">
              <p className="font-semibold mb-0.5">Save your client secret securely!</p>
              <p className="text-[11px] text-amber-700 dark:text-amber-300">
                This secret will never be shown again in plain text. Store it safely in your backend application or vault.
              </p>
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                Client Secret
              </label>
              <div className="flex items-center gap-2">
                <input
                  type="text"
                  readOnly
                  value={newlyCreatedSecret.clientSecret}
                  className="flex-1 px-3.5 py-2.5 rounded-xl border border-slate-200/80 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 font-mono text-xs select-all text-slate-900 dark:text-white"
                />
                <button
                  type="button"
                  onClick={() => {
                    navigator.clipboard?.writeText(newlyCreatedSecret.clientSecret);
                    setSuccessMessage('Client secret copied to clipboard!');
                    setTimeout(() => setSuccessMessage(''), 3000);
                  }}
                  className="px-3.5 py-2.5 rounded-xl bg-indigo-900 hover:bg-indigo-950 text-white text-xs font-bold transition flex items-center gap-1.5 cursor-pointer shrink-0"
                >
                  <Copy size={14} />
                  <span>Copy</span>
                </button>
              </div>
            </div>

            <div className="pt-2 flex justify-end">
              <button
                type="button"
                onClick={() => setNewlyCreatedSecret(null)}
                className="px-5 py-2.5 rounded-xl bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-white text-xs font-bold transition cursor-pointer"
              >
                Done
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
