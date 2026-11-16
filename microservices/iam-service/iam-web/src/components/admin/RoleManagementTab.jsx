import React, { useState, useEffect } from 'react';
import {
  Shield,
  Plus,
  Search,
  MoreHorizontal,
  Trash2,
  Key,
  X,
  Pencil,
  Check,
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';

export default function RoleManagementTab() {
  const { locale } = useLanguage();
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [typeFilter, setTypeFilter] = useState('ALL');
  const [createModal, setCreateModal] = useState(false);
  const [editModalRole, setEditModalRole] = useState(null);
  const [editRoleData, setEditRoleData] = useState({ name: '', description: '' });
  const [openActionMenuId, setOpenActionMenuId] = useState(null);
  const [actionMenuState, setActionMenuState] = useState(null);
  const [permModalRole, setPermModalRole] = useState(null);
  const [rolePermissionsMap, setRolePermissionsMap] = useState({});
  const [toastMessage, setToastMessage] = useState('');
  const [newRole, setNewRole] = useState({ name: '', description: '', type: 'Custom' });

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3000);
  };

  const loadData = async () => {
    const [rData, pData] = await Promise.all([
      adminService.getRoles(),
      adminService.getPermissions(),
    ]);
    const rolesList = Array.isArray(rData) ? rData : [];
    setRoles(rolesList);
    setPermissions(Array.isArray(pData) ? pData : []);

    const permMap = {};
    await Promise.all(
      rolesList.map(async (role) => {
        const rolePerms = await adminService.getRolePermissions(role.uuid);
        permMap[role.uuid] = rolePerms.map((rp) => rp.permissionUuid || rp.uuid);
      })
    );
    setRolePermissionsMap(permMap);
  };

  const openPermModal = async (r) => {
    setPermModalRole(r);
    const rolePerms = await adminService.getRolePermissions(r.uuid);
    setRolePermissionsMap((prev) => ({
      ...prev,
      [r.uuid]: rolePerms.map((rp) => rp.permissionUuid || rp.uuid),
    }));
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    await adminService.createRole(newRole);
    setCreateModal(false);
    setNewRole({ name: '', description: '', type: 'Custom' });
    loadData();
  };

  const handleDelete = async (uuid) => {
    if (window.confirm('Delete this role?')) {
      await adminService.deleteRole(uuid);
      setRoles(roles.filter((r) => r.uuid !== uuid));
    }
  };

  const filteredRoles = roles.filter((r) => {
    const matchSearch =
      r.name.toLowerCase().includes(search.toLowerCase()) ||
      r.description.toLowerCase().includes(search.toLowerCase());
    const matchType = typeFilter === 'ALL' || r.type === typeFilter;
    return matchSearch && matchType;
  });

  return (
    <div className="space-y-6 w-full">
      {/* 1. Header matching Screenshot 8 */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {locale === 'km' ? 'តួនាទី (Roles)' : 'Roles'}
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            {locale === 'km'
              ? 'តួនាទីចងក្រងសិទ្ធិអនុញ្ញាត ហើយអ្នកប្រើប្រាស់ទទួលបានសិទ្ធិទាំងអស់នៃតួនាទីរបស់ពួកគេ។'
              : 'Roles group permissions; users get every permission of their roles.'}
          </p>
        </div>

        <button
          onClick={() => setCreateModal(true)}
          className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-indigo-900 hover:bg-indigo-950 text-white text-xs font-bold shadow-sm transition cursor-pointer"
        >
          <Plus size={15} />
          <span>{locale === 'km' ? 'បង្កើតតួនាទី' : 'Create role'}</span>
        </button>
      </div>

      {/* 2. Search & Filters matching Screenshot 8 */}
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
        <div className="relative flex-1 sm:max-w-md">
          <Search size={15} className="absolute left-3.5 top-3 text-slate-400" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder={locale === 'km' ? 'ស្វែងរកតួនាទី...' : 'Search roles...'}
            className="w-full pl-9 pr-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-xs outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <div className="flex items-center gap-2">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-xs text-slate-700 dark:text-slate-300 outline-none"
          >
            <option value="ALL">{locale === 'km' ? 'ស្ថានភាពទាំងអស់' : 'All statuses'}</option>
            <option value="ACTIVE">{locale === 'km' ? 'សកម្ម' : 'Active'}</option>
          </select>

          <select
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
            className="px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-xs text-slate-700 dark:text-slate-300 outline-none"
          >
            <option value="ALL">{locale === 'km' ? 'ប្រភេទទាំងអស់' : 'All types'}</option>
            <option value="Built-in">{locale === 'km' ? 'ជាប់ប្រព័ន្ធ' : 'Built-in'}</option>
            <option value="Custom">{locale === 'km' ? 'ផ្ទាល់ខ្លួន' : 'Custom'}</option>
          </select>
        </div>
      </div>

      {/* 3. Table matching Screenshot 8 */}
      <div className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-200/80 dark:border-slate-800 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-slate-100 dark:border-slate-800 text-slate-400 font-semibold">
                <th className="py-3 px-6">{locale === 'km' ? 'ឈ្មោះតួនាទី' : 'Name'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'ការពិពណ៌នា' : 'Description'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'សិទ្ធិ (Permissions)' : 'Permissions'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'ប្រភេទ' : 'Type'}</th>
                <th className="py-3 px-4">{locale === 'km' ? 'ស្ថានភាព' : 'Status'}</th>
                <th className="py-3 px-6 text-right">{locale === 'km' ? 'សកម្មភាព' : 'Actions'}</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-slate-600 dark:text-slate-300">
              {filteredRoles.map((role) => (
                <tr key={role.uuid} className="hover:bg-slate-50/70 dark:hover:bg-slate-800/40 transition">
                  {/* Name with Shield icon */}
                  <td className="py-4 px-6">
                    <div className="flex items-center gap-2.5">
                      <Shield size={16} className="text-slate-400" />
                      <span className="font-bold font-mono text-slate-900 dark:text-white">
                        {role.name}
                      </span>
                    </div>
                  </td>

                  {/* Description */}
                  <td className="py-4 px-4 text-slate-600 dark:text-slate-300">
                    {role.description}
                  </td>

                  {/* Permissions */}
                  <td className="py-4 px-4">
                    {role.name === 'ADMIN' ? (
                      <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-indigo-50 dark:bg-indigo-950/50 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
                        <Shield size={11} />
                        {locale === 'km' ? 'សិទ្ធិទាំងអស់' : 'All Permissions'} ({rolePermissionsMap[role.uuid]?.length ?? 17})
                      </span>
                    ) : (rolePermissionsMap[role.uuid]?.length || 0) > 0 ? (
                      <div className="flex flex-wrap items-center gap-1 max-w-xs">
                        <span className="px-2 py-0.5 rounded-md text-[10px] font-semibold bg-indigo-50 dark:bg-indigo-950/40 text-indigo-700 dark:text-indigo-300">
                          {rolePermissionsMap[role.uuid].length} {locale === 'km' ? 'សិទ្ធិ' : 'perms'}
                        </span>
                        {permissions
                          .filter((p) => rolePermissionsMap[role.uuid]?.includes(p.uuid))
                          .slice(0, 2)
                          .map((p) => (
                            <code key={p.uuid} className="text-[10px] px-1.5 py-0.5 rounded bg-slate-100 dark:bg-slate-800 font-mono text-slate-700 dark:text-slate-300">
                              {p.name}
                            </code>
                          ))}
                        {(rolePermissionsMap[role.uuid]?.length || 0) > 2 && (
                          <span className="text-[10px] text-slate-400 font-semibold">+{rolePermissionsMap[role.uuid].length - 2}</span>
                        )}
                      </div>
                    ) : (
                      <span className="text-slate-400 text-[11px] italic">
                        {locale === 'km' ? 'គ្មានសិទ្ធិ' : 'No permissions'}
                      </span>
                    )}
                  </td>

                  {/* Type */}
                  <td className="py-4 px-4">
                    <span className="px-2.5 py-0.5 rounded-full text-[10px] font-semibold bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border border-slate-200 dark:border-slate-700">
                      {role.system ? (locale === 'km' ? 'ជាប់ប្រព័ន្ធ' : 'Built-in') : (locale === 'km' ? 'ផ្ទាល់ខ្លួន' : 'Custom')}
                    </span>
                  </td>

                  {/* Status */}
                  <td className="py-4 px-4">
                    <span className="inline-flex items-center gap-1.5 font-semibold text-slate-800 dark:text-slate-200 text-xs">
                      <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                      Active
                    </span>
                  </td>

                  {/* Actions with 3-dots menu for > 2 actions */}
                  {/* Actions with 3-dots menu for > 2 actions */}
                  <td className="py-4 px-6 text-right relative">
                    <button
                      type="button"
                      onClick={(e) => {
                        e.stopPropagation();
                        if (actionMenuState?.role?.uuid === role.uuid) {
                          setActionMenuState(null);
                        } else {
                          const rect = e.currentTarget.getBoundingClientRect();
                          const spaceBelow = window.innerHeight - rect.bottom;
                          const openUpwards = spaceBelow < 200;
                          setActionMenuState({
                            role: role,
                            coords: {
                              top: openUpwards ? undefined : rect.bottom + 6,
                              bottom: openUpwards ? window.innerHeight - rect.top + 6 : undefined,
                              right: window.innerWidth - rect.right,
                            },
                          });
                        }
                      }}
                      className={`p-1.5 rounded-xl transition cursor-pointer ${
                        actionMenuState?.role?.uuid === role.uuid
                          ? 'bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400'
                          : 'text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800'
                      }`}
                      title="Actions"
                    >
                      <MoreHorizontal size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
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
              onClick={() => {
                const r = actionMenuState.role;
                setActionMenuState(null);
                setEditModalRole(r);
                setEditRoleData({ name: r.name, description: r.description || '' });
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition cursor-pointer"
            >
              <Pencil size={14} className="text-slate-400" />
              <span>{locale === 'km' ? 'កែប្រែតួនាទី' : 'Edit role'}</span>
            </button>
            <button
              type="button"
              onClick={() => {
                const r = actionMenuState.role;
                setActionMenuState(null);
                openPermModal(r);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-indigo-600 dark:text-indigo-400 hover:bg-indigo-50 dark:hover:bg-indigo-950/40 transition cursor-pointer"
            >
              <Key size={14} />
              <span>{locale === 'km' ? 'គ្រប់គ្រងសិទ្ធិ' : 'Manage permissions'}</span>
            </button>
            <div className="h-px bg-slate-100 dark:bg-slate-800 my-1" />
            <button
              type="button"
              onClick={() => {
                const r = actionMenuState.role;
                setActionMenuState(null);
                handleDelete(r.uuid);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/40 transition cursor-pointer"
            >
              <Trash2 size={14} />
              <span>{locale === 'km' ? 'លុបតួនាទី' : 'Delete role'}</span>
            </button>
          </div>
        </>
      )}

      {/* Create Role Modal */}
      {createModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-2xl border border-slate-200/80 dark:border-slate-800">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800 mb-4">
              <h3 className="font-extrabold text-base text-slate-900 dark:text-white">Create New Role</h3>
              <button onClick={() => setCreateModal(false)} className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 cursor-pointer">
                <X size={18} />
              </button>
            </div>
            <form onSubmit={handleCreate} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1.5">Role Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. MODERATOR"
                  value={newRole.name}
                  onChange={(e) => setNewRole({ ...newRole, name: e.target.value.toUpperCase() })}
                  className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-xs outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1.5">Description</label>
                <textarea
                  rows={3}
                  required
                  placeholder="Role responsibilities..."
                  value={newRole.description}
                  onChange={(e) => setNewRole({ ...newRole, description: e.target.value })}
                  className="w-full px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-xs outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>
              <div className="flex justify-end gap-2.5 pt-3">
                <button
                  type="button"
                  onClick={() => setCreateModal(false)}
                  className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl transition cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl shadow-sm transition cursor-pointer"
                >
                  Save Role
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Permissions Modal */}
      {permModalRole && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-3xl p-6 sm:p-8 max-w-lg w-full shadow-2xl border border-slate-100 dark:border-slate-800 max-h-[85vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800 mb-4">
              <div>
                <h3 className="font-bold text-base text-slate-900 dark:text-white flex items-center gap-2">
                  <Shield size={18} className="text-indigo-600" />
                  <span>{locale === 'km' ? `សិទ្ធិសម្រាប់ ${permModalRole.name}` : `Permissions for ${permModalRole.name}`}</span>
                </h3>
                <p className="text-xs text-slate-400">
                  {locale === 'km' ? 'គ្រប់គ្រង និងចងភ្ជាប់សិទ្ធិអនុញ្ញាតជាមួយតួនាទី' : 'Bind or revoke scoped permissions'}
                </p>
              </div>
              <button onClick={() => setPermModalRole(null)} className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 cursor-pointer">
                <X size={18} />
              </button>
            </div>

            {permModalRole.system && (
              <div className="mb-4 p-3 bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 rounded-2xl text-amber-800 dark:text-amber-300 text-xs flex items-start gap-2.5">
                <Shield size={16} className="shrink-0 mt-0.5 text-amber-600 dark:text-amber-400" />
                <div>
                  <span className="font-bold">{locale === 'km' ? 'តួនាទីជាប់ប្រព័ន្ធ (System Protected):' : 'System Role Protected:'} </span>
                  {locale === 'km'
                    ? 'តួនាទីនេះត្រូវបានការពារដោយ Core Policies នៃប្រព័ន្ធ។ សិទ្ធិទាំងអស់ត្រូវបានកំណត់ស្រាប់ និងមិនអនុញ្ញាតឱ្យកែប្រែតាម UI ឡើយ។'
                    : 'This role is protected by platform policies. Default assignments are locked and cannot be modified.'}
                </div>
              </div>
            )}

            <div className="space-y-2 text-xs">
              {permissions.map((p) => {
                const assignedIds = rolePermissionsMap[permModalRole.uuid] || [];
                const isAssigned = permModalRole.name === 'ADMIN' || assignedIds.includes(p.uuid);
                return (
                  <div
                    key={p.uuid}
                    className={`p-3 rounded-2xl border transition flex items-center justify-between gap-3 ${
                      isAssigned
                        ? 'border-indigo-100 dark:border-indigo-950/60 bg-indigo-50/40 dark:bg-indigo-950/20'
                        : 'border-slate-100 dark:border-slate-800 hover:bg-slate-50 dark:hover:bg-slate-800/50'
                    }`}
                  >
                    <div>
                      <div className="font-mono font-bold text-slate-900 dark:text-white flex items-center gap-1.5">
                        {p.name}
                        {isAssigned && (
                          <span className="inline-flex items-center text-[10px] font-bold text-emerald-600 dark:text-emerald-400">
                            <Check size={12} className="inline" />
                          </span>
                        )}
                      </div>
                      <div className="text-slate-500 dark:text-slate-400 text-[11px] mt-0.5">{p.description}</div>
                    </div>

                    <div className="shrink-0 flex items-center gap-2">
                      {isAssigned ? (
                        <>
                          <span className="px-2 py-0.5 rounded-lg text-[10px] font-bold bg-emerald-100 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800">
                            {locale === 'km' ? 'បានផ្ដល់សិទ្ធិ' : 'Assigned'}
                          </span>
                          {!permModalRole.system && (
                            <button
                              type="button"
                              onClick={async () => {
                                if (window.confirm(locale === 'km' ? `ដកសិទ្ធិ ${p.name}?` : `Revoke ${p.name}?`)) {
                                  try {
                                    await adminService.revokeRolePermission(permModalRole.uuid, p.uuid);
                                    setRolePermissionsMap((prev) => ({
                                      ...prev,
                                      [permModalRole.uuid]: (prev[permModalRole.uuid] || []).filter((id) => id !== p.uuid),
                                    }));
                                    showToast(locale === 'km' ? `បានដកសិទ្ធិ ${p.name}` : `Revoked ${p.name}`);
                                  } catch (err) {
                                    alert(err?.response?.data?.detail || err?.response?.data?.message || 'Failed to revoke');
                                  }
                                }
                              }}
                              className="px-2.5 py-1 bg-rose-50 hover:bg-rose-100 dark:bg-rose-950/50 text-rose-600 dark:text-rose-400 rounded-xl font-bold text-xs transition cursor-pointer"
                            >
                              {locale === 'km' ? 'ដកចេញ' : 'Revoke'}
                            </button>
                          )}
                        </>
                      ) : (
                        permModalRole.system ? (
                          <span className="text-slate-400 text-[11px] italic">
                            {locale === 'km' ? 'គ្មាន' : 'Not granted'}
                          </span>
                        ) : (
                          <button
                            type="button"
                            onClick={async () => {
                              try {
                                await adminService.assignRolePermissions(permModalRole.uuid, [p.uuid]);
                                setRolePermissionsMap((prev) => ({
                                  ...prev,
                                  [permModalRole.uuid]: [...(prev[permModalRole.uuid] || []), p.uuid],
                                }));
                                showToast(locale === 'km' ? `បានផ្ដល់សិទ្ធិ ${p.name}` : `Assigned ${p.name}`);
                              } catch (err) {
                                alert(err?.response?.data?.detail || err?.response?.data?.message || 'Failed to assign');
                              }
                            }}
                            className="px-3 py-1 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold text-xs shadow-xs transition cursor-pointer"
                          >
                            {locale === 'km' ? 'ផ្ដល់សិទ្ធិ' : 'Assign'}
                          </button>
                        )
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {/* Edit Role Modal */}
      {editModalRole && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-2xl border border-slate-200/80 dark:border-slate-800">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800 mb-4">
              <h3 className="font-extrabold text-base text-slate-900 dark:text-white flex items-center gap-2">
                <Pencil size={16} className="text-indigo-600 dark:text-indigo-400" />
                <span>Edit Role</span>
              </h3>
              <button
                type="button"
                onClick={() => setEditModalRole(null)}
                className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 cursor-pointer"
              >
                <X size={18} />
              </button>
            </div>
            <form
              onSubmit={async (e) => {
                e.preventDefault();
                await adminService.updateRole(editModalRole.uuid, editRoleData);
                setRoles((prev) =>
                  prev.map((r) =>
                    r.uuid === editModalRole.uuid ? { ...r, ...editRoleData } : r
                  )
                );
                setEditModalRole(null);
              }}
              className="space-y-4 text-xs"
            >
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                  Role Name (Uppercase)
                </label>
                <input
                  type="text"
                  required
                  value={editRoleData.name}
                  onChange={(e) =>
                    setEditRoleData({ ...editRoleData, name: e.target.value.toUpperCase() })
                  }
                  className="w-full px-3 py-2 border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-900 dark:text-white font-mono"
                />
              </div>
              <div>
                <label className="block font-bold text-slate-700 dark:text-slate-300 mb-1">
                  Description
                </label>
                <textarea
                  rows={3}
                  value={editRoleData.description}
                  onChange={(e) =>
                    setEditRoleData({ ...editRoleData, description: e.target.value })
                  }
                  className="w-full px-3 py-2 border border-slate-200 dark:border-slate-700 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>
              <div className="flex justify-end gap-2 pt-3 border-t border-slate-100 dark:border-slate-800">
                <button
                  type="button"
                  onClick={() => setEditModalRole(null)}
                  className="px-4 py-2 border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-300 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-800"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-900 hover:bg-indigo-950 text-white font-bold rounded-xl shadow-xs transition cursor-pointer"
                >
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed bottom-6 right-6 z-50 bg-slate-900 dark:bg-slate-100 text-white dark:text-slate-900 px-4 py-2.5 rounded-2xl shadow-2xl border border-slate-700 dark:border-slate-200 text-xs flex items-center gap-2 animate-in fade-in slide-in-from-bottom-2">
          <Check size={14} className="text-emerald-400 dark:text-emerald-600" />
          <span className="font-semibold">{toastMessage}</span>
        </div>
      )}
    </div>
  );
}
