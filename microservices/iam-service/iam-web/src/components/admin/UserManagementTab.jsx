import React, { useState, useEffect } from 'react';
import { Users, Plus, Search, Shield, Key, Lock, Unlock, CheckCircle, CheckCircle2, AlertCircle, XCircle, Trash2, Edit2, Check, MoreHorizontal, ChevronLeft, ChevronRight } from 'lucide-react';
import { adminService } from '../../services/adminService';
import { useLanguage } from '../../context/LanguageContext';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '../ui/card';
import { Table, TableHeader, TableBody, TableHead, TableRow, TableCell } from '../ui/table';
import { Badge } from '../ui/badge';
import { Button } from '../ui/button';
import { Input, Label } from '../ui/input';
import { Dialog, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '../ui/dialog';

export default function UserManagementTab() {
  const { t, locale } = useLanguage();
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);
  const [search, setSearch] = useState('');
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 8;
  
  // Modals
  const [createModal, setCreateModal] = useState(false);
  const [editModalUser, setEditModalUser] = useState(null);
  const [roleModalUser, setRoleModalUser] = useState(null);
  const [permModalUser, setPermModalUser] = useState(null);
  const [openActionMenuId, setOpenActionMenuId] = useState(null);
  const [actionMenuState, setActionMenuState] = useState(null);

  // Form states
  const [newUser, setNewUser] = useState({ username: '', email: '', password: '', givenName: '', familyName: '' });
  const [editFormData, setEditFormData] = useState({ givenName: '', familyName: '', email: '', phoneNumber: '', role: 'USER', enabled: true, locked: false });

  const loadData = async () => {
    try {
      const [uData, rData, pData] = await Promise.all([
        adminService.getUsers(),
        adminService.getRoles(),
        adminService.getPermissions(),
      ]);
      const rawUsers = uData?.content || (Array.isArray(uData) ? uData : []);
      const processedUsers = rawUsers.map((u) => {
        const isLocked = u.locked === true || u.accountNonLocked === false;
        return {
          ...u,
          locked: isLocked,
          accountNonLocked: !isLocked,
        };
      });
      setUsers(processedUsers);
      setRoles(rData || []);
      setPermissions(pData || []);
    } catch (e) {
      console.error('Failed to load user management data', e);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreateUser = async (e) => {
    e.preventDefault();
    try {
      await adminService.createUser({
        username: newUser.username,
        email: newUser.email,
        password: newUser.password,
        profile: { givenName: newUser.givenName, familyName: newUser.familyName },
        givenName: newUser.givenName,
        familyName: newUser.familyName,
      });
      adminService.recordAudit('USER_CREATED', newUser.username, 'admin', '127.0.0.1', `Provisioned user account @${newUser.username}`);
      setCreateModal(false);
      setNewUser({ username: '', email: '', password: '', givenName: '', familyName: '' });
      setSuccessMessage('User provisioned successfully.');
      setTimeout(() => setSuccessMessage(''), 3000);
      loadData();
    } catch (err) {
      console.error('Failed to create user', err);
      const msg = err.response?.data?.detail || err.response?.data?.message || err.message || 'Failed to create user';
      setErrorMessage(msg);
      setTimeout(() => setErrorMessage(''), 4000);
    }
  };

  const openEditModal = (u) => {
    setEditModalUser(u);
    const isLocked = u.locked === true || u.accountNonLocked === false;
    setEditFormData({
      givenName: u.givenName || u.name?.split(' ')[0] || '',
      familyName: u.familyName || u.name?.split(' ').slice(1).join(' ') || '',
      email: u.email || '',
      phoneNumber: u.phoneNumber || '',
      role: u.roles?.[0]?.name || (roles[0]?.name || 'USER'),
      enabled: u.enabled !== false,
      locked: isLocked,
    });
  };

  const handleSaveEditUser = async (e) => {
    e.preventDefault();
    if (!editModalUser) return;
    try {
      const updated = await adminService.updateUser(editModalUser.uuid, {
        givenName: editFormData.givenName,
        familyName: editFormData.familyName,
        name: `${editFormData.givenName} ${editFormData.familyName}`.trim(),
        email: editFormData.email,
        phoneNumber: editFormData.phoneNumber,
        enabled: editFormData.enabled,
        locked: editFormData.locked,
      });

      // Synchronize lock state if changed in edit modal
      const origLocked = editModalUser.locked === true || editModalUser.accountNonLocked === false;
      if (editFormData.locked !== origLocked) {
        if (editFormData.locked) {
          await adminService.lockUser(editModalUser.uuid);
          adminService.recordAudit('LOCK_USER', editModalUser.username, 'admin', '127.0.0.1', `Locked user account @${editModalUser.username}`);
        } else {
          await adminService.unlockUser(editModalUser.uuid);
          adminService.recordAudit('UNLOCK_USER', editModalUser.username, 'admin', '127.0.0.1', `Unlocked user account @${editModalUser.username}`);
        }
      }

      // Synchronize enabled state if changed in edit modal
      if (editFormData.enabled !== (editModalUser.enabled !== false)) {
        if (editFormData.enabled) {
          await adminService.enableUser(editModalUser.uuid);
          adminService.recordAudit('ENABLE_USER', editModalUser.username, 'admin', '127.0.0.1', `Enabled user account @${editModalUser.username}`);
        } else {
          await adminService.disableUser(editModalUser.uuid);
          adminService.recordAudit('DISABLE_USER', editModalUser.username, 'admin', '127.0.0.1', `Disabled user account @${editModalUser.username}`);
        }
      }

      // Synchronize primary role selection
      const targetRole = roles.find((r) => r.name === editFormData.role);
      if (targetRole && !editModalUser.roles?.some((r) => r.name === targetRole.name)) {
        await adminService.assignRolesToUser(editModalUser.uuid, [targetRole.uuid]);
        adminService.recordAudit('ASSIGN_ROLE', editModalUser.username, 'admin', '127.0.0.1', `Assigned role ${targetRole.name} to @${editModalUser.username}`);
        // Revoke former different roles
        for (const oldRole of (editModalUser.roles || [])) {
          if (oldRole.uuid && oldRole.name !== targetRole.name) {
            try {
              await adminService.revokeRoleFromUser(editModalUser.uuid, oldRole.uuid);
              adminService.recordAudit('REVOKE_ROLE', editModalUser.username, 'admin', '127.0.0.1', `Revoked role ${oldRole.name} from @${editModalUser.username}`);
            } catch (ignored) {}
          }
        }
      }

      adminService.recordAudit('USER_UPDATED', editModalUser.username, 'admin', '127.0.0.1', `Updated profile attributes for @${editModalUser.username}`);
      setUsers((prev) => prev.map((u) => (u.uuid === editModalUser.uuid ? { ...u, ...updated, locked: editFormData.locked, accountNonLocked: !editFormData.locked } : u)));
      setEditModalUser(null);
      setSuccessMessage('User updated successfully.');
      setTimeout(() => setSuccessMessage(''), 3000);
      loadData();
    } catch (err) {
      console.error('Failed to update user', err);
      const msg = err.response?.data?.detail || err.response?.data?.message || err.message || 'Failed to update user';
      setErrorMessage(msg);
      setTimeout(() => setErrorMessage(''), 4000);
    }
  };

  const handleToggleEnable = async (user) => {
    try {
      if (user.enabled) {
        await adminService.disableUser(user.uuid);
        adminService.recordAudit('DISABLE_USER', user.username, 'admin', '127.0.0.1', `Disabled user account @${user.username}`);
        setUsers(users.map((u) => (u.uuid === user.uuid ? { ...u, enabled: false } : u)));
        setSuccessMessage(`Disabled account @${user.username}`);
      } else {
        await adminService.enableUser(user.uuid);
        adminService.recordAudit('ENABLE_USER', user.username, 'admin', '127.0.0.1', `Enabled user account @${user.username}`);
        setUsers(users.map((u) => (u.uuid === user.uuid ? { ...u, enabled: true } : u)));
        setSuccessMessage(`Enabled account @${user.username}`);
      }
      setTimeout(() => setSuccessMessage(''), 3000);
      loadData();
    } catch (err) {
      console.error('Failed to toggle enable state', err);
      const msg = err.response?.data?.detail || err.response?.data?.message || err.message || 'Failed to toggle enable state';
      setErrorMessage(msg);
      setTimeout(() => setErrorMessage(''), 4000);
    }
  };

  const handleToggleLock = async (user) => {
    try {
      const isLocked = user.locked === true || user.accountNonLocked === false;
      if (isLocked) {
        await adminService.unlockUser(user.uuid);
        adminService.recordAudit('UNLOCK_USER', user.username, 'admin', '127.0.0.1', `Unlocked user account @${user.username}`);
        setUsers(users.map((u) => (u.uuid === user.uuid ? { ...u, locked: false, accountNonLocked: true } : u)));
        setSuccessMessage(`Unlocked account @${user.username}`);
      } else {
        await adminService.lockUser(user.uuid);
        adminService.recordAudit('LOCK_USER', user.username, 'admin', '127.0.0.1', `Locked user account @${user.username}`);
        setUsers(users.map((u) => (u.uuid === user.uuid ? { ...u, locked: true, accountNonLocked: false } : u)));
        setSuccessMessage(`Locked account @${user.username}`);
      }
      setTimeout(() => setSuccessMessage(''), 3000);
      loadData();
    } catch (err) {
      console.error('Failed to toggle lock state', err);
      const msg = err.response?.data?.detail || err.response?.data?.message || err.message || 'Failed to toggle lock state';
      setErrorMessage(msg);
      setTimeout(() => setErrorMessage(''), 4000);
    }
  };

  const handleDelete = async (uuid) => {
    const target = users.find((u) => u.uuid === uuid);
    if (window.confirm(locale === 'km' ? `តើអ្នកប្រាកដជាចង់លុបគណនី @${target?.username || ''} មែនទេ?` : `Are you sure you want to delete user @${target?.username || ''}?`)) {
      try {
        await adminService.deleteUser(uuid);
        adminService.recordAudit('DELETE_USER', target?.username || uuid, 'admin', '127.0.0.1', `Deleted user account @${target?.username || uuid}`);
        setUsers(users.filter((u) => u.uuid !== uuid));
        setSuccessMessage('User deleted successfully.');
        setTimeout(() => setSuccessMessage(''), 3000);
        loadData();
      } catch (err) {
        console.error('Failed to delete user', err);
        const msg = err.response?.data?.detail || err.response?.data?.message || err.message || 'Failed to delete user.';
        setErrorMessage(msg);
        setTimeout(() => setErrorMessage(''), 4000);
      }
    }
  };

  const handleAssignRole = async (userUuid, roleUuid) => {
    try {
      await adminService.assignRolesToUser(userUuid, [roleUuid]);
      setSuccessMessage('Role assigned successfully!');
      setTimeout(() => setSuccessMessage(''), 3000);

      const targetRole = roles.find((r) => r.uuid === roleUuid);
      if (roleModalUser && targetRole) {
        setRoleModalUser((prev) => ({
          ...prev,
          roles: [...(prev.roles || []), { uuid: targetRole.uuid, name: targetRole.name }],
        }));
      }
      loadData();
    } catch (err) {
      console.error('Failed to assign role:', err);
      const msg = err.response?.data?.detail || err.response?.data?.message || err.message || 'Role assignment failed';
      setErrorMessage(msg);
      setTimeout(() => setErrorMessage(''), 4000);
    }
  };

  const handleRevokeRole = async (userUuid, roleUuid) => {
    try {
      await adminService.revokeRoleFromUser(userUuid, roleUuid);
      setSuccessMessage('Role revoked successfully.');
      setTimeout(() => setSuccessMessage(''), 3000);

      if (roleModalUser) {
        setRoleModalUser((prev) => ({
          ...prev,
          roles: (prev.roles || []).filter((r) => r.uuid !== roleUuid),
        }));
      }
      loadData();
    } catch (err) {
      console.error('Failed to revoke role:', err);
      const msg = err.response?.data?.detail || err.response?.data?.message || err.message || 'Failed to revoke role';
      setErrorMessage(msg);
      setTimeout(() => setErrorMessage(''), 4000);
    }
  };

  const handleGrantPermission = async (userUuid, permUuid) => {
    try {
      await adminService.grantPermissionsToUser(userUuid, [permUuid]);
      setSuccessMessage('Permission granted successfully.');
      setTimeout(() => setSuccessMessage(''), 3000);
      setPermModalUser(null);
    } catch (err) {
      console.error('Failed to grant permission:', err);
      const msg = err.response?.data?.detail || err.response?.data?.message || err.message || 'Failed to grant permission';
      setErrorMessage(msg);
      setTimeout(() => setErrorMessage(''), 4000);
    }
  };

  const sortedUsers = [...users].sort((a, b) => {
    const timeA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
    const timeB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
    if (timeA !== timeB) return timeB - timeA;
    return (b.id || 0) - (a.id || 0);
  });

  const filteredUsers = sortedUsers.filter((u) => {
    const matchesSearch =
      u.username?.toLowerCase().includes(search.toLowerCase()) ||
      u.email?.toLowerCase().includes(search.toLowerCase()) ||
      u.name?.toLowerCase().includes(search.toLowerCase());
    const isLocked = !!(u.locked ?? !u.accountNonLocked);
    if (filterStatus === 'ACTIVE') return matchesSearch && u.enabled && !isLocked;
    if (filterStatus === 'LOCKED') return matchesSearch && isLocked;
    if (filterStatus === 'DISABLED') return matchesSearch && !u.enabled;
    return matchesSearch;
  });

  const totalPages = Math.max(1, Math.ceil(filteredUsers.length / pageSize));
  const paginatedUsers = filteredUsers.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  return (
    <div className="space-y-4 w-full">
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

      <Card className="border border-slate-200/80 dark:border-slate-800 bg-white dark:bg-slate-900 shadow-xs">
      <CardHeader className="flex flex-col sm:flex-row items-start sm:items-center justify-between pb-4 gap-4">
        <div>
          <CardTitle className="flex items-center gap-2 text-xl font-extrabold text-slate-900 dark:text-white">
            <Users className="text-indigo-600 dark:text-indigo-400" size={22} />
            <span>{locale === 'km' ? 'គណនីអ្នកប្រើប្រាស់ & អត្តសញ្ញាណ IAM' : 'User Accounts & IAM Identities'}</span>
          </CardTitle>
          <CardDescription className="text-xs text-slate-500 dark:text-slate-400 mt-1">
            {locale === 'km'
              ? 'ស្វែងរក បង្កើត កែប្រែព័ត៌មាន និងសវនកម្មអ្នកប្រើប្រាស់ទាំងអស់។'
              : 'Browse, provision, modify attributes, and audit all identity principals.'}
          </CardDescription>
        </div>
        <Button
          onClick={() => setCreateModal(true)}
          className="bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl shadow-md shadow-indigo-600/20 text-xs font-bold"
        >
          <Plus size={16} /> {locale === 'km' ? 'បន្ថែមអ្នកប្រើប្រាស់' : 'Provision User'}
        </Button>
      </CardHeader>

      <CardContent className="space-y-4">
        {/* Search & Filters */}
        <div className="flex flex-col sm:flex-row items-center gap-3">
          <div className="relative flex-1 w-full">
            <Search className="absolute left-3 top-3 text-slate-400" size={16} />
            <Input
              placeholder={locale === 'km' ? 'ស្វែងរកតាមឈ្មោះ ឈ្មោះគណនី ឬអ៊ីមែល...' : 'Search by name, username, or email...'}
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setCurrentPage(1);
              }}
              className="pl-9 bg-slate-50/70 dark:bg-slate-800/70 border-slate-200 dark:border-slate-700 rounded-xl"
            />
          </div>
          <div className="flex items-center gap-1.5 w-full sm:w-auto">
            {['ALL', 'ACTIVE', 'LOCKED', 'DISABLED'].map((status) => (
              <button
                key={status}
                onClick={() => {
                  setFilterStatus(status);
                  setCurrentPage(1);
                }}
                className={`px-3 py-1.5 text-xs font-semibold rounded-xl transition cursor-pointer border ${
                  filterStatus === status
                    ? 'bg-slate-900 dark:bg-white text-white dark:text-slate-900 border-slate-900 dark:border-white shadow-2xs'
                    : 'bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-300 border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-750'
                }`}
              >
                {status === 'ALL'
                  ? locale === 'km' ? 'ទាំងអស់' : 'ALL'
                  : status === 'ACTIVE'
                  ? locale === 'km' ? 'សកម្ម' : 'ACTIVE'
                  : status === 'LOCKED'
                  ? locale === 'km' ? 'បានចាក់សោ' : 'LOCKED'
                  : locale === 'km' ? 'បានបិទ' : 'DISABLED'}
              </button>
            ))}
          </div>
        </div>

        {/* Users Table */}
        <div className="overflow-x-auto rounded-2xl border border-slate-200/80 dark:border-slate-800">
          <Table>
            <TableHeader>
              <TableRow className="border-b border-slate-200/80 dark:border-slate-800 bg-slate-50/80 dark:bg-slate-800/80">
                <TableHead className="font-bold text-slate-600 dark:text-slate-300">
                  {locale === 'km' ? 'អត្តសញ្ញាណអ្នកប្រើប្រាស់' : 'Identity Principal'}
                </TableHead>
                <TableHead className="font-bold text-slate-600 dark:text-slate-300">
                  {locale === 'km' ? 'តួនាទីដែលបានកំណត់' : 'Assigned Roles'}
                </TableHead>
                <TableHead className="font-bold text-slate-600 dark:text-slate-300 text-right pr-6">
                  {locale === 'km' ? 'ស្ថានភាព' : 'Status'}
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredUsers.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={3} className="text-center py-8 text-xs text-slate-400">
                    {locale === 'km' ? 'រកមិនឃើញអ្នកប្រើប្រាស់ក្នុងទិន្នន័យឡើយ។' : 'No users found in database.'}
                  </TableCell>
                </TableRow>
              ) : (
                paginatedUsers.map((u) => (
                  <TableRow key={u.uuid} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/40 border-b border-slate-100 dark:border-slate-800/60">
                    <TableCell>
                      <div className="font-bold text-slate-900 dark:text-white">{u.name || u.username}</div>
                      <div className="text-xs text-slate-500 dark:text-slate-400 font-mono">@{u.username} • {u.email}</div>
                    </TableCell>
                  <TableCell>
                    <div className="flex flex-wrap gap-1">
                      {u.roles?.length ? (
                        u.roles.map((r, i) => (
                          <Badge key={i} variant="purple" className="text-[11px] font-mono">
                            {r.name || r}
                          </Badge>
                        ))
                      ) : (
                        <span className="text-xs text-slate-400 dark:text-slate-500">{locale === 'km' ? 'គ្មាន' : 'None'}</span>
                      )}
                    </div>
                  </TableCell>
                  <TableCell className="text-right pr-6 relative">
                    <div className="flex items-center justify-end gap-3">
                      {/* Status indicator matching screenshot */}
                      <div className="flex items-center gap-1.5">
                        {(u.locked ?? !u.accountNonLocked) ? (
                          <span className="inline-flex items-center gap-1.5 text-xs font-semibold text-amber-600 dark:text-amber-400">
                            <span className="w-2 h-2 rounded-full bg-amber-500 inline-block" />
                            {locale === 'km' ? 'បានចាក់សោ' : 'Locked'}
                          </span>
                        ) : u.enabled ? (
                          <span className="inline-flex items-center gap-1.5 text-xs font-semibold text-emerald-600 dark:text-emerald-400">
                            <span className="w-2 h-2 rounded-full bg-emerald-500 inline-block" />
                            {locale === 'km' ? 'សកម្ម' : 'Active'}
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-400 dark:text-slate-500">
                            <span className="w-2 h-2 rounded-full bg-slate-400 inline-block" />
                            {locale === 'km' ? 'បានបិទ' : 'Disabled'}
                          </span>
                        )}
                      </div>

                      {/* 3-dots action menu trigger */}
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          if (actionMenuState?.user?.uuid === u.uuid) {
                            setActionMenuState(null);
                          } else {
                            const rect = e.currentTarget.getBoundingClientRect();
                            const spaceBelow = window.innerHeight - rect.bottom;
                            const openUpwards = spaceBelow < 260;
                            setActionMenuState({
                              user: u,
                              coords: {
                                top: openUpwards ? undefined : rect.bottom + 6,
                                bottom: openUpwards ? window.innerHeight - rect.top + 6 : undefined,
                                right: window.innerWidth - rect.right,
                              },
                            });
                          }
                        }}
                        className={`p-1.5 rounded-xl transition cursor-pointer ${
                          actionMenuState?.user?.uuid === u.uuid
                            ? 'bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400'
                            : 'text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800'
                        }`}
                        title="Actions"
                      >
                        <MoreHorizontal size={16} />
                      </button>
                    </div>
                  </TableCell>
                </TableRow>
              ))
              )}
            </TableBody>
          </Table>

          {/* Pagination Controls */}
          {filteredUsers.length > pageSize && (
            <div className="flex flex-col sm:flex-row items-center justify-between gap-3 px-6 py-3.5 border-t border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-900/50">
              <div className="text-xs text-slate-500 dark:text-slate-400">
                {locale === 'km' ? (
                  <>
                    បង្ហាញ <span className="font-semibold text-slate-700 dark:text-slate-200">{(currentPage - 1) * pageSize + 1}</span> ដល់{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{Math.min(currentPage * pageSize, filteredUsers.length)}</span> នៃ{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{filteredUsers.length}</span> នាក់
                  </>
                ) : (
                  <>
                    Showing <span className="font-semibold text-slate-700 dark:text-slate-200">{(currentPage - 1) * pageSize + 1}</span> to{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{Math.min(currentPage * pageSize, filteredUsers.length)}</span> of{' '}
                    <span className="font-semibold text-slate-700 dark:text-slate-200">{filteredUsers.length}</span> users
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
      </CardContent>

      {/* Floating Action Dropdown Menu (Fixed positioning so it never gets clipped by table overflow) */}
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
                const u = actionMenuState.user;
                setActionMenuState(null);
                openEditModal(u);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition cursor-pointer"
            >
              <Edit2 size={14} className="text-indigo-600 dark:text-indigo-400" />
              <span>{locale === 'km' ? 'កែប្រែព័ត៌មានលម្អិត' : 'Edit Details'}</span>
            </button>
            <button
              type="button"
              onClick={() => {
                const u = actionMenuState.user;
                setActionMenuState(null);
                setRoleModalUser(u);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition cursor-pointer"
            >
              <Shield size={14} className="text-purple-600 dark:text-purple-400" />
              <span>{locale === 'km' ? 'កំណត់តួនាទី' : 'Assign Roles'}</span>
            </button>
            <button
              type="button"
              onClick={() => {
                const u = actionMenuState.user;
                setActionMenuState(null);
                setPermModalUser(u);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition cursor-pointer"
            >
              <Key size={14} className="text-amber-600 dark:text-amber-400" />
              <span>{locale === 'km' ? 'គ្រប់គ្រងសិទ្ធិ' : 'Permissions'}</span>
            </button>
            <button
              type="button"
              onClick={() => {
                const u = actionMenuState.user;
                setActionMenuState(null);
                handleToggleLock(u);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition cursor-pointer"
            >
              {(actionMenuState.user.locked ?? !actionMenuState.user.accountNonLocked) ? (
                <>
                  <Unlock size={14} className="text-emerald-600" />
                  <span>{locale === 'km' ? 'ដោះសោគណនី' : 'Unlock Account'}</span>
                </>
              ) : (
                <>
                  <Lock size={14} className="text-amber-600" />
                  <span>{locale === 'km' ? 'ចាក់សោគណនី' : 'Lock Account'}</span>
                </>
              )}
            </button>
            <button
              type="button"
              onClick={() => {
                const u = actionMenuState.user;
                setActionMenuState(null);
                handleToggleEnable(u);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 transition cursor-pointer"
            >
              {actionMenuState.user.enabled ? (
                <>
                  <XCircle size={14} className="text-slate-500" />
                  <span>{locale === 'km' ? 'បិទដំណើរការ' : 'Deactivate User'}</span>
                </>
              ) : (
                <>
                  <CheckCircle size={14} className="text-emerald-600" />
                  <span>{locale === 'km' ? 'បើកដំណើរការ' : 'Activate User'}</span>
                </>
              )}
            </button>
            <div className="h-px bg-slate-100 dark:bg-slate-800 my-1" />
            <button
              type="button"
              onClick={() => {
                const u = actionMenuState.user;
                setActionMenuState(null);
                handleDelete(u.uuid);
              }}
              className="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/30 transition cursor-pointer"
            >
              <Trash2 size={14} />
              <span>{locale === 'km' ? 'លុបអ្នកប្រើប្រាស់' : 'Delete User'}</span>
            </button>
          </div>
        </>
      )}

      {/* Dialog: Create User */}
      <Dialog open={createModal} onOpenChange={setCreateModal}>
        <DialogHeader>
          <DialogTitle>Provision New User</DialogTitle>
          <DialogDescription>Add a new IAM identity account with credentials.</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleCreateUser} className="space-y-4">
          <div>
            <Label>Username</Label>
            <Input
              required
              value={newUser.username}
              onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
              className="mt-1"
            />
          </div>
          <div>
            <Label>Email</Label>
            <Input
              type="email"
              required
              value={newUser.email}
              onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
              className="mt-1"
            />
          </div>
          <div>
            <Label>Initial Password</Label>
            <Input
              type="password"
              required
              value={newUser.password}
              onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
              className="mt-1"
            />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <Label>First Name</Label>
              <Input
                value={newUser.givenName}
                onChange={(e) => setNewUser({ ...newUser, givenName: e.target.value })}
                className="mt-1"
              />
            </div>
            <div>
              <Label>Last Name</Label>
              <Input
                value={newUser.familyName}
                onChange={(e) => setNewUser({ ...newUser, familyName: e.target.value })}
                className="mt-1"
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" type="button" onClick={() => setCreateModal(false)}>
              Cancel
            </Button>
            <Button type="submit" className="bg-indigo-600 hover:bg-indigo-700 text-white">Create User</Button>
          </DialogFooter>
        </form>
      </Dialog>

      {/* Dialog: Edit User */}
      <Dialog open={!!editModalUser} onOpenChange={() => setEditModalUser(null)}>
        <DialogHeader>
          <DialogTitle>Edit User: @{editModalUser?.username}</DialogTitle>
          <DialogDescription>Update user personal info, email, phone, and role.</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSaveEditUser} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <Label>First Name</Label>
              <Input
                value={editFormData.givenName}
                onChange={(e) => setEditFormData({ ...editFormData, givenName: e.target.value })}
                className="mt-1"
              />
            </div>
            <div>
              <Label>Last Name</Label>
              <Input
                value={editFormData.familyName}
                onChange={(e) => setEditFormData({ ...editFormData, familyName: e.target.value })}
                className="mt-1"
              />
            </div>
          </div>
          <div>
            <Label>Email Address</Label>
            <Input
              type="email"
              required
              value={editFormData.email}
              onChange={(e) => setEditFormData({ ...editFormData, email: e.target.value })}
              className="mt-1"
            />
          </div>
          <div>
            <Label>Phone Number</Label>
            <Input
              type="tel"
              value={editFormData.phoneNumber}
              onChange={(e) => setEditFormData({ ...editFormData, phoneNumber: e.target.value })}
              className="mt-1"
            />
          </div>
          <div>
            <Label>Primary Role</Label>
            <select
              value={editFormData.role}
              onChange={(e) => setEditFormData({ ...editFormData, role: e.target.value })}
              className="w-full mt-1 px-3.5 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm outline-none transition focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
            >
              {roles.length > 0 ? (
                roles.map((r) => (
                  <option key={r.uuid} value={r.name}>
                    {r.name} {r.description ? `(${r.description})` : ''}
                  </option>
                ))
              ) : (
                <>
                  <option value="ADMIN">ADMIN</option>
                  <option value="USER">USER</option>
                </>
              )}
            </select>
          </div>
          <div className="flex items-center gap-6 pt-2">
            <label className="flex items-center gap-2 text-xs font-semibold text-slate-700 dark:text-slate-300 cursor-pointer">
              <input
                type="checkbox"
                checked={editFormData.enabled}
                onChange={(e) => setEditFormData({ ...editFormData, enabled: e.target.checked })}
                className="rounded text-indigo-600 w-4 h-4"
              />
              <span>Account Active</span>
            </label>
            <label className="flex items-center gap-2 text-xs font-semibold text-slate-700 dark:text-slate-300 cursor-pointer">
              <input
                type="checkbox"
                checked={editFormData.locked}
                onChange={(e) => setEditFormData({ ...editFormData, locked: e.target.checked })}
                className="rounded text-amber-600 w-4 h-4"
              />
              <span>Account Locked</span>
            </label>
          </div>
          <DialogFooter>
            <Button variant="outline" type="button" onClick={() => setEditModalUser(null)}>
              Cancel
            </Button>
            <Button type="submit" className="bg-indigo-600 hover:bg-indigo-700 text-white">Save Changes</Button>
          </DialogFooter>
        </form>
      </Dialog>

      {/* Dialog: Role Assignment */}
      <Dialog open={!!roleModalUser} onOpenChange={() => setRoleModalUser(null)}>
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Shield className="text-purple-600" size={18} />
            <span>Manage Roles for @{roleModalUser?.username}</span>
          </DialogTitle>
          <DialogDescription>Assign or revoke RBAC roles for this identity principal.</DialogDescription>
        </DialogHeader>

        <div className="space-y-2 mb-4 max-h-72 overflow-y-auto">
          {roles.map((r) => {
            const isAssigned = roleModalUser?.roles?.some(
              (ur) => ur.uuid === r.uuid || ur.name?.toUpperCase() === r.name?.toUpperCase()
            );
            return (
              <div
                key={r.uuid}
                className={`flex items-center justify-between p-3.5 border rounded-2xl transition ${
                  isAssigned
                    ? 'border-purple-200 bg-purple-50/40 dark:border-purple-900/60 dark:bg-purple-950/20'
                    : 'border-slate-200 dark:border-slate-800 hover:bg-slate-50 dark:hover:bg-slate-800/60'
                }`}
              >
                <div>
                  <div className="flex items-center gap-2">
                    <p className="font-bold text-xs text-slate-900 dark:text-white">{r.name}</p>
                    {isAssigned && (
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-purple-100 dark:bg-purple-900/60 text-purple-700 dark:text-purple-300 border border-purple-200 dark:border-purple-800">
                        Assigned
                      </span>
                    )}
                  </div>
                  <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">
                    {r.description || 'System RBAC role'}
                  </p>
                </div>

                {isAssigned ? (
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => handleRevokeRole(roleModalUser.uuid, r.uuid)}
                    className="border-rose-200 text-rose-600 hover:bg-rose-50 hover:text-rose-700 dark:border-rose-800 dark:text-rose-400 dark:hover:bg-rose-950/30 text-xs font-bold cursor-pointer"
                  >
                    Revoke
                  </Button>
                ) : (
                  <Button
                    size="sm"
                    onClick={() => handleAssignRole(roleModalUser.uuid, r.uuid)}
                    className="bg-purple-600 hover:bg-purple-700 text-white text-xs font-bold cursor-pointer"
                  >
                    Assign Role
                  </Button>
                )}
              </div>
            );
          })}
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={() => setRoleModalUser(null)}>Close</Button>
        </DialogFooter>
      </Dialog>

      {/* Dialog: Permission Assignment */}
      <Dialog open={!!permModalUser} onOpenChange={() => setPermModalUser(null)}>
        <DialogHeader>
          <DialogTitle>Grant Permissions to @{permModalUser?.username}</DialogTitle>
          <DialogDescription>Direct resource-based authority grants.</DialogDescription>
        </DialogHeader>
        <div className="space-y-2 mb-4 max-h-60 overflow-y-auto">
          {permissions.map((p) => (
            <div key={p.uuid} className="flex items-center justify-between p-3 border border-slate-200 dark:border-slate-800 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-800">
              <div>
                <Badge variant="indigo" className="font-mono text-[10px]">{p.name}</Badge>
                <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-1">{p.description}</p>
              </div>
              <Button
                size="sm"
                variant="outline"
                onClick={() => handleGrantPermission(permModalUser.uuid, p.uuid)}
              >
                Grant
              </Button>
            </div>
          ))}
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={() => setPermModalUser(null)}>Close</Button>
        </DialogFooter>
      </Dialog>
    </Card>
    </div>
  );
}
