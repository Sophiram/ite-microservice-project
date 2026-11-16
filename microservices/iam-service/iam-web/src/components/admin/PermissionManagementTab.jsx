import React, { useState, useEffect } from 'react';
import { Key, Plus, Trash2, Edit2, Search, Filter, X, Check, Shield } from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function PermissionManagementTab() {
  const { locale } = useLanguage();
  const [permissions, setPermissions] = useState([]);
  const [search, setSearch] = useState('');
  const [filterResource, setFilterResource] = useState('ALL');
  const [createModal, setCreateModal] = useState(false);
  const [editModal, setEditModal] = useState(false);
  const [editingPerm, setEditingPerm] = useState(null);
  const [toastMessage, setToastMessage] = useState('');

  const [newPerm, setNewPerm] = useState({ resource: 'user', action: 'read', description: '' });
  const [editForm, setEditForm] = useState({ name: '', resource: 'user', action: 'read', description: '' });

  const loadData = async () => {
    const data = await adminService.getPermissions();
    setPermissions(data || []);
  };

  useEffect(() => {
    loadData();
  }, []);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3000);
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    const permName = newPerm.resource.includes(':')
      ? newPerm.resource
      : `iam:${newPerm.resource}:${newPerm.action}`;
    const created = await adminService.createPermission({
      name: permName,
      resource: newPerm.resource,
      action: newPerm.action,
      description: newPerm.description,
    });
    setPermissions((prev) => [...prev, created]);
    setCreateModal(false);
    setNewPerm({ resource: 'user', action: 'read', description: '' });
    showToast(locale === 'km' ? 'បានបង្កើតសិទ្ធិជោគជ័យ' : 'Permission created successfully');
    loadData();
  };

  const openEditModal = (perm) => {
    setEditingPerm(perm);
    const parts = perm.name.split(':');
    let res = perm.resource || (parts.length >= 2 ? parts[parts.length - 2] : 'user');
    let act = perm.action || (parts.length >= 1 ? parts[parts.length - 1] : 'read');
    setEditForm({
      name: perm.name,
      resource: res,
      action: act,
      description: perm.description || '',
    });
    setEditModal(true);
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    if (!editingPerm) return;

    const updatedData = {
      name: editForm.name.trim() || `iam:${editForm.resource}:${editForm.action}`,
      resource: editForm.resource,
      action: editForm.action,
      description: editForm.description,
    };

    const updated = await adminService.updatePermission(editingPerm.uuid, updatedData);
    setPermissions((prev) =>
      prev.map((p) => (p.uuid === editingPerm.uuid ? { ...p, ...updated } : p))
    );
    setEditModal(false);
    setEditingPerm(null);
    showToast(locale === 'km' ? 'បានកែប្រែសិទ្ធិជោគជ័យ' : 'Permission updated successfully');
    loadData();
  };

  const handleDelete = async (uuid, e) => {
    if (e) e.stopPropagation();
    if (window.confirm(locale === 'km' ? 'តើអ្នកពិតជាចង់លុបសិទ្ធិនេះមែនទេ?' : 'Delete this permission?')) {
      await adminService.deletePermission(uuid);
      setPermissions((prev) => prev.filter((p) => p.uuid !== uuid));
      showToast(locale === 'km' ? 'បានលុបសិទ្ធិ' : 'Permission deleted');
    }
  };

  const resources = Array.from(
    new Set(
      permissions.map((p) => {
        if (p.resource) return p.resource;
        const parts = p.name.split(':');
        return parts.length >= 2 ? parts[parts.length - 2] : parts[0];
      })
    )
  );

  const filtered = permissions.filter((p) => {
    const resName = p.resource || (p.name.includes(':') ? p.name.split(':')[1] : p.name);
    const matchesSearch =
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      (p.description && p.description.toLowerCase().includes(search.toLowerCase()));
    const matchesRes = filterResource === 'ALL' || resName === filterResource || p.name.includes(filterResource);
    return matchesSearch && matchesRes;
  });

  return (
    <div className="bg-white dark:bg-slate-900 p-6 sm:p-8 rounded-3xl shadow-xs border border-slate-200/80 dark:border-slate-800 transition-colors">
      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed top-6 right-6 z-50 flex items-center gap-2 px-4 py-2.5 rounded-xl bg-emerald-600 text-white text-xs font-bold shadow-xl animate-bounce">
          <Check size={16} />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Header matching Screenshot 1 */}
      <div className="flex flex-col md:flex-row justify-between md:items-center gap-4 mb-6">
        <div>
          <h2 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {locale === 'km' ? 'កាតាឡុកសិទ្ធិអនុញ្ញាតផ្អែកលើធនធាន' : 'Resource-Based Permissions Catalog'}
          </h2>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            {locale === 'km'
              ? 'ពង្រឹងគោលការណ៍ least privilege តាមរយៈ resource-action pairs ដ៏ច្បាស់លាស់។'
              : 'Enforce principle of least privilege through granular resource-action pairs.'}
          </p>
        </div>
        <button
          onClick={() => setCreateModal(true)}
          className="inline-flex items-center gap-2 px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold shadow-sm transition cursor-pointer shrink-0"
        >
          <Plus size={16} /> {locale === 'km' ? 'បន្ថែមសិទ្ធិ' : 'Define Permission'}
        </button>
      </div>

      {/* Search and Filters matching Screenshot 1 */}
      <div className="flex flex-col sm:flex-row gap-3 mb-6">
        <div className="relative flex-1 max-w-sm">
          <Search size={15} className="absolute left-3.5 top-3 text-slate-400" />
          <input
            type="text"
            placeholder={locale === 'km' ? 'ស្វែងរកសិទ្ធិ...' : 'Filter permissions...'}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-9 pr-3.5 py-2 text-xs border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
          />
        </div>
        <select
          value={filterResource}
          onChange={(e) => setFilterResource(e.target.value)}
          className="px-3.5 py-2 text-xs border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-300 cursor-pointer"
        >
          <option value="ALL">{locale === 'km' ? 'ធនធានទាំងអស់' : 'All Resources'}</option>
          {resources.map((r) => (
            <option key={r} value={r}>
              Resource: {r}
            </option>
          ))}
        </select>
      </div>

      {/* Permissions Grid matching Screenshot 1 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filtered.map((perm) => (
          <div
            key={perm.uuid}
            onClick={() => openEditModal(perm)}
            className="group p-4 border border-slate-200/80 dark:border-slate-800 rounded-2xl hover:border-indigo-400 dark:hover:border-indigo-600 bg-slate-50/40 dark:bg-slate-800/40 hover:bg-white dark:hover:bg-slate-800 transition flex items-start justify-between cursor-pointer"
          >
            <div className="flex-1 pr-2">
              <div className="flex items-center gap-2">
                <code className="text-xs font-mono font-bold px-2 py-0.5 bg-amber-50 dark:bg-amber-950/40 text-amber-800 dark:text-amber-300 rounded border border-amber-200 dark:border-amber-800/60">
                  {perm.name}
                </code>
              </div>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-2 line-clamp-2">
                {perm.description || (locale === 'km' ? 'គ្មានការពិពណ៌នា' : 'No description')}
              </p>
            </div>
            <div className="flex items-center gap-1 shrink-0 pt-0.5">
              <button
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  openEditModal(perm);
                }}
                className="p-1.5 rounded-lg text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 dark:hover:bg-indigo-950/50 transition cursor-pointer"
                title={locale === 'km' ? 'កែប្រែសិទ្ធិ' : 'Edit Permission'}
              >
                <Edit2 size={15} />
              </button>
              <button
                type="button"
                onClick={(e) => handleDelete(perm.uuid, e)}
                className="p-1.5 rounded-lg text-slate-300 dark:text-slate-600 hover:text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/50 transition cursor-pointer"
                title={locale === 'km' ? 'លុបសិទ្ធិ' : 'Delete Permission'}
              >
                <Trash2 size={15} />
              </button>
            </div>
          </div>
        ))}
        {filtered.length === 0 && (
          <div className="col-span-full py-12 text-center text-slate-400 text-xs">
            {locale === 'km' ? 'រកមិនឃើញសិទ្ធិត្រូវគ្នាទេ' : 'No matching permissions found.'}
          </div>
        )}
      </div>

      {/* Edit Permission Modal */}
      {editModal && editingPerm && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-white dark:bg-slate-900 p-6 rounded-3xl max-w-md w-full shadow-2xl border border-slate-200/80 dark:border-slate-800">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800 mb-4">
              <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                <Edit2 size={17} className="text-indigo-600" />
                {locale === 'km' ? 'កែប្រែសិទ្ធិអនុញ្ញាត' : 'Edit Permission'}
              </h3>
              <button
                onClick={() => setEditModal(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleUpdate} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                  {locale === 'km' ? 'ឈ្មោះសិទ្ធិ (Authority String) *' : 'Authority String Name *'}
                </label>
                <input
                  required
                  type="text"
                  value={editForm.name}
                  onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
                  className="w-full px-3 py-2 border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-900 dark:text-white font-mono"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                    {locale === 'km' ? 'ដែនធនធាន (Resource)' : 'Resource Domain'}
                  </label>
                  <input
                    type="text"
                    value={editForm.resource}
                    onChange={(e) => setEditForm({ ...editForm, resource: e.target.value.toLowerCase().trim() })}
                    className="w-full px-3 py-2 border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                  />
                </div>
                <div>
                  <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                    {locale === 'km' ? 'សកម្មភាព (Action)' : 'Action'}
                  </label>
                  <select
                    value={editForm.action}
                    onChange={(e) => setEditForm({ ...editForm, action: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                  >
                    <option value="*">* (All / Wildcard)</option>
                    <option value="read">read</option>
                    <option value="create">create</option>
                    <option value="update">update</option>
                    <option value="delete">delete</option>
                    <option value="assign">assign</option>
                    <option value="manage">manage</option>
                    <option value="config">config</option>
                    <option value="view">view</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                  {locale === 'km' ? 'ការពិពណ៌នា (Description)' : 'Description'}
                </label>
                <textarea
                  rows={3}
                  value={editForm.description}
                  onChange={(e) => setEditForm({ ...editForm, description: e.target.value })}
                  placeholder="Explains what this grants..."
                  className="w-full px-3 py-2 border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t border-slate-100 dark:border-slate-800">
                <button
                  type="button"
                  onClick={() => setEditModal(false)}
                  className="px-4 py-2 border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 rounded-xl font-medium"
                >
                  {locale === 'km' ? 'បោះបង់' : 'Cancel'}
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-600 text-white font-bold rounded-xl hover:bg-indigo-700 shadow-sm transition cursor-pointer"
                >
                  {locale === 'km' ? 'រក្សាទុកការកែប្រែ' : 'Save Changes'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Create Modal */}
      {createModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-white dark:bg-slate-900 p-6 rounded-3xl max-w-md w-full shadow-2xl border border-slate-200/80 dark:border-slate-800">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800 mb-4">
              <h3 className="text-base font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                <Plus size={17} className="text-indigo-600" />
                {locale === 'km' ? 'កំណត់សិទ្ធិថ្មី' : 'Define New Permission'}
              </h3>
              <button
                onClick={() => setCreateModal(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleCreate} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                  {locale === 'km' ? 'ដែនធនធាន *' : 'Resource Domain *'}
                </label>
                <input
                  required
                  type="text"
                  placeholder="e.g. user, order, payment"
                  value={newPerm.resource}
                  onChange={(e) => setNewPerm({ ...newPerm, resource: e.target.value.toLowerCase().trim() })}
                  className="w-full px-3 py-2 border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                  {locale === 'km' ? 'សកម្មភាព *' : 'Action *'}
                </label>
                <select
                  value={newPerm.action}
                  onChange={(e) => setNewPerm({ ...newPerm, action: e.target.value })}
                  className="w-full px-3 py-2 border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                >
                  <option value="*">* (All operations / Wildcard)</option>
                  <option value="read">read (Inspect / Query)</option>
                  <option value="create">create (Provision / Register)</option>
                  <option value="update">update (Modify)</option>
                  <option value="delete">delete (Remove)</option>
                  <option value="assign">assign (Grant / Bind)</option>
                  <option value="manage">manage (Full Admin)</option>
                  <option value="config">config (Settings)</option>
                  <option value="view">view (Auditing)</option>
                </select>
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                  Preview Authority String
                </label>
                <code className="block p-2 bg-slate-100 dark:bg-slate-800 rounded-xl text-xs font-mono font-bold text-indigo-700 dark:text-indigo-400">
                  iam:{newPerm.resource || 'resource'}:{newPerm.action}
                </code>
              </div>

              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                  {locale === 'km' ? 'ការពិពណ៌នា' : 'Description'}
                </label>
                <input
                  type="text"
                  placeholder="Explains what this grants..."
                  value={newPerm.description}
                  onChange={(e) => setNewPerm({ ...newPerm, description: e.target.value })}
                  className="w-full px-3 py-2 border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t border-slate-100 dark:border-slate-800">
                <button
                  type="button"
                  onClick={() => setCreateModal(false)}
                  className="px-4 py-2 border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 rounded-xl font-medium"
                >
                  {locale === 'km' ? 'បោះបង់' : 'Cancel'}
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-600 text-white font-bold rounded-xl hover:bg-indigo-700 shadow-sm transition cursor-pointer"
                >
                  {locale === 'km' ? 'រក្សាទុកសិទ្ធិ' : 'Save Permission'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
