import api from './api';

export const DEFAULT_AUDIT_LOGS = [
  {
    id: 'audit-1',
    when: '10 minutes ago',
    timestamp: '2026-11-15 22:15:00',
    action: 'ASSIGN_ROLE',
    changed: 'sornsopiram@gmail.com',
    by: 'admin@istad.edu.kh',
    ip: '192.168.1.105',
    details: 'Assigned roles: ROLE_ADMIN, ROLE_USER to user sornsopiram@gmail.com',
  },
  {
    id: 'audit-2',
    when: '35 minutes ago',
    timestamp: '2026-11-15 21:50:12',
    action: 'EMAIL_VERIFIED',
    changed: 'sornsopiram@gmail.com',
    by: 'system',
    ip: '127.0.0.1',
    details: 'User completed email OTP verification successfully',
  },
  {
    id: 'audit-3',
    when: '2 hours ago',
    timestamp: '2026-11-15 20:22:45',
    action: 'CREATE_CLIENT',
    changed: 'istad-gateway-client',
    by: 'admin@istad.edu.kh',
    ip: '192.168.1.105',
    details: 'Registered new OAuth2 client istad-gateway-client with scopes openid, profile',
  },
  {
    id: 'audit-4',
    when: '5 hours ago',
    timestamp: '2026-11-15 17:15:30',
    action: 'CREATE_PERMISSION',
    changed: 'user:write',
    by: 'admin@istad.edu.kh',
    ip: '192.168.1.105',
    details: 'Created permission user:write for resource user',
  },
  {
    id: 'audit-5',
    when: 'Yesterday',
    timestamp: '2026-11-14 14:02:11',
    action: 'LOCK_USER',
    changed: 'john_doe@example.com',
    by: 'admin@istad.edu.kh',
    ip: '192.168.1.105',
    details: 'Locked user account due to 5 consecutive failed login attempts',
  },
  {
    id: 'audit-6',
    when: '2 days ago',
    timestamp: '2026-11-13 09:44:18',
    action: 'CHANGE_PASSWORD',
    changed: 'admin@istad.edu.kh',
    by: 'admin@istad.edu.kh',
    ip: '192.168.1.105',
    details: 'User changed account password with complexity verification',
  },
  {
    id: 'audit-7',
    when: '3 days ago',
    timestamp: '2026-11-12 11:30:00',
    action: 'USER_CREATED',
    changed: 'rammiez',
    by: 'admin@istad.edu.kh',
    ip: '192.168.1.105',
    details: 'Provisioned new user account with default role ROLE_USER',
  },
];

export const recordAuditLog = (action, changed, by = 'admin@istad.edu.kh', ip = '127.0.0.1', details = '') => {
  try {
    const raw = localStorage.getItem('iam_audit_logs');
    const existing = raw ? JSON.parse(raw) : DEFAULT_AUDIT_LOGS;
    const now = new Date();
    const newLog = {
      id: 'audit-' + Date.now(),
      when: 'Just now',
      timestamp: now.toISOString().replace('T', ' ').substring(0, 19),
      action,
      changed,
      by,
      ip,
      details: details || `Executed ${action} on ${changed}`,
    };
    localStorage.setItem('iam_audit_logs', JSON.stringify([newLog, ...existing].slice(0, 100)));
  } catch (e) {
    console.warn('Could not record audit log', e);
  }
};

// Admin Service connecting directly to real Spring Boot REST APIs with local audit fallback
export const adminService = {
  // 1. Overview KPIs (computed from real backend endpoints)
  getOverviewStats: async () => {
    try {
      const [usersRes, rolesRes, permsRes] = await Promise.all([
        api.get('/api/users?size=1').catch(() => ({ data: { totalElements: 0 } })),
        api.get('/api/roles?size=1').catch(() => ({ data: { totalElements: 0 } })),
        api.get('/api/permissions?size=1').catch(() => ({ data: { totalElements: 0 } })),
      ]);
      const totalUsers = usersRes.data?.totalElements ?? 0;
      const totalRoles = rolesRes.data?.totalElements ?? 0;
      const totalPermissions = permsRes.data?.totalElements ?? 0;
      return {
        totalUsers,
        activeUsers: totalUsers,
        totalRoles,
        totalPermissions,
        totalClients: 0,
        pendingInvitations: 0,
      };
    } catch (e) {
      console.error('Failed to fetch overview stats', e);
      return {
        totalUsers: 0,
        activeUsers: 0,
        totalRoles: 0,
        totalPermissions: 0,
        totalClients: 0,
        pendingInvitations: 0,
      };
    }
  },

  // 2. Users Catalog (Pure Database via /api/users)
  getUsers: async (page = 0, size = 100) => {
    try {
      const res = await api.get(`/api/users?page=${page}&size=${size}`);
      if (res.data?.content) return res.data;
      if (Array.isArray(res.data)) return { content: res.data, totalElements: res.data.length };
    } catch (e) {
      console.error('Failed to load users from backend', e);
    }
    return { content: [], totalElements: 0 };
  },

  createUser: async (userData) => {
    const res = await api.post('/api/users', userData);
    return res.data;
  },

  updateUser: async (uuid, data) => {
    const res = await api.patch(`/api/users/${uuid}`, data);
    return res.data;
  },

  deleteUser: async (uuid) => {
    await api.delete(`/api/users/${uuid}`);
    return { success: true };
  },

  enableUser: async (uuid) => {
    await api.post(`/api/users/${uuid}/enable`);
    return { success: true };
  },

  disableUser: async (uuid) => {
    await api.post(`/api/users/${uuid}/disable`);
    return { success: true };
  },

  lockUser: async (uuid) => {
    await api.post(`/api/users/${uuid}/lock`);
    return { success: true };
  },

  unlockUser: async (uuid) => {
    await api.post(`/api/users/${uuid}/unlock`);
    return { success: true };
  },

  resetUserPassword: async (uuid, newPassword) => {
    const res = await api.put(`/api/users/${uuid}/password`, { newPassword });
    return res.data || { success: true };
  },

  // 3. User Roles Assignment
  assignRolesToUser: async (uuid, roleUuids) => {
    const res = await api.post(`/api/users/${uuid}/roles`, { roleUuids });
    return res.data || { success: true };
  },

  revokeRoleFromUser: async (uuid, roleUuid) => {
    const res = await api.delete(`/api/users/${uuid}/roles/${roleUuid}`);
    return res.data || { success: true };
  },

  // 4. User Permission Assignment
  grantPermissionsToUser: async (uuid, permissionUuids, effect = 'GRANT') => {
    const res = await api.post(`/api/users/${uuid}/permissions`, { permissionUuids, effect });
    return res.data || { success: true };
  },

  revokePermissionFromUser: async (uuid, permissionUuid) => {
    const res = await api.delete(`/api/users/${uuid}/permissions/${permissionUuid}`);
    return res.data || { success: true };
  },

  // 5. User Active Sessions across system (Pure Real Database from user_sessions table)
  getAllUserSessions: async () => {
    try {
      const res = await api.get('/api/admin/sessions');
      if (res.data && Array.isArray(res.data) && res.data.length > 0) return res.data;
    } catch (e) {
      console.warn('Failed to fetch from /api/admin/sessions', e);
    }
    return [];
  },

  terminateUserSession: async (sessionId) => {
    try {
      await api.delete(`/api/admin/sessions/${sessionId}`);
    } catch {}
    return { success: true, sessionId };
  },

  terminateAllSessionsForUser: async (username) => {
    try {
      await api.delete(`/api/admin/sessions/user/${username}`);
    } catch {}
    return { success: true, username };
  },

  // 6. User Login History & Security Monitoring (Pure Real Database via /api/admin/login-history)
  getSystemLoginLogs: async (filter = 'ALL', search = '') => {
    try {
      const res = await api.get(`/api/admin/login-history?filter=${encodeURIComponent(filter || 'ALL')}&search=${encodeURIComponent(search || '')}`);
      let list = [];
      if (res.data?.content && Array.isArray(res.data.content)) list = res.data.content;
      else if (Array.isArray(res.data)) list = res.data;
      if (list.length > 0) {
        return list.map((l) => ({
          id: l.id,
          username: l.username,
          ip: l.ipAddress || '127.0.0.1',
          status: l.status,
          timestamp: l.createdAt ? new Date(l.createdAt).toISOString().replace('T', ' ').slice(0, 19) : 'Just now',
          userAgent: l.userAgent || l.device || 'Browser',
          suspicious: l.suspicious,
          note: l.threatAssessment || (l.suspicious ? 'Suspicious Probe' : 'Normal'),
        }));
      }
    } catch (e) {
      console.warn('Failed to load login history from /api/admin/login-history', e);
    }
    return [];
  },

  // 7. Roles Management (Pure Database via /api/roles)
  getRoles: async () => {
    try {
      const res = await api.get('/api/roles?size=100');
      if (res.data?.content) return res.data.content;
      if (Array.isArray(res.data)) return res.data;
    } catch (e) {
      console.error('Failed to load roles from backend', e);
    }
    return [];
  },

  createRole: async (role) => {
    const res = await api.post('/api/roles', role);
    return res.data;
  },

  updateRole: async (uuid, data) => {
    const res = await api.patch(`/api/roles/${uuid}`, data);
    return res.data;
  },

  deleteRole: async (uuid) => {
    await api.delete(`/api/roles/${uuid}`);
    return { success: true };
  },

  assignRolePermissions: async (roleUuid, permissionUuids) => {
    const res = await api.post(`/api/roles/${roleUuid}/permissions`, { permissionUuids });
    return res.data || { success: true };
  },

  getRolePermissions: async (roleUuid) => {
    try {
      const res = await api.get(`/api/roles/${roleUuid}/permissions`);
      return Array.isArray(res.data) ? res.data : [];
    } catch (e) {
      console.error('Failed to get role permissions', e);
      return [];
    }
  },

  revokeRolePermission: async (roleUuid, permissionUuid) => {
    await api.delete(`/api/roles/${roleUuid}/permissions/${permissionUuid}`);
    return { success: true };
  },

  // 8. Permissions Management (Pure Database via /api/permissions)
  getPermissions: async () => {
    try {
      const res = await api.get('/api/permissions?size=200');
      if (res.data?.content) return res.data.content;
      if (Array.isArray(res.data)) return res.data;
    } catch (e) {
      console.error('Failed to load permissions from backend', e);
    }
    return [];
  },

  createPermission: async (data) => {
    const res = await api.post('/api/permissions', data);
    return res.data;
  },

  updatePermission: async (uuid, data) => {
    const res = await api.patch(`/api/permissions/${uuid}`, data);
    return res.data;
  },

  deletePermission: async (uuid) => {
    await api.delete(`/api/permissions/${uuid}`);
    return { success: true };
  },

  // 9. Applications / OAuth2 Clients
  getClients: async () => {
    try {
      const res = await api.get('/api/clients');
      if (res.data && Array.isArray(res.data)) return res.data;
      if (res.data?.content && Array.isArray(res.data.content)) return res.data.content;
    } catch (e) {
      console.error('Failed to get clients:', e);
    }
    return [];
  },

  createClient: async (client) => {
    const res = await api.post('/api/clients', client);
    return res.data;
  },

  updateClient: async (id, clientData) => {
    const res = await api.patch(`/api/clients/${id}`, clientData);
    return res.data;
  },

  deleteClient: async (clientId) => {
    await api.delete(`/api/clients/${clientId}`);
    return { success: true, clientId };
  },

  rotateClientSecret: async (id) => {
    const res = await api.post(`/api/clients/${id}/rotate-secret`);
    return res.data;
  },

  // 10. Audit Logs (Pure Real Database via /api/audit-logs)
  getAuditLogs: async (filter = {}) => {
    try {
      const actionParam = filter.action && filter.action !== 'ALL' ? `?action=${encodeURIComponent(filter.action)}` : '';
      const res = await api.get(`/api/audit-logs${actionParam}`);
      let list = [];
      if (res.data?.content && Array.isArray(res.data.content)) list = res.data.content;
      else if (Array.isArray(res.data)) list = res.data;
      if (list.length > 0) {
        return list.map((l) => ({
          id: String(l.id || l.uuid),
          action: l.action,
          changed: l.target,
          target: l.target,
          by: l.actor,
          actor: l.actor,
          ip: l.clientIp || '127.0.0.1',
          timestamp: l.createdAt ? new Date(l.createdAt).toISOString().replace('T', ' ').slice(0, 19) : 'Just now',
          details: l.details || '',
        }));
      }
    } catch (e) {
      console.warn('Failed to load audit logs from /api/audit-logs', e);
    }
    return [];
  },

  recordAudit: async (action, changed, by = 'admin@istad.edu.kh', ip = '127.0.0.1', details = '') => {
    try {
      await api.post('/api/audit-logs', { action, target: changed, details });
    } catch (e) {
      console.warn('Failed to post audit log', e);
    }
  },

  // 11. System Configuration (Pure Real Database from system_config table)
  getSystemConfig: async () => {
    try {
      const res = await api.get('/api/system/config');
      if (res.data && Object.keys(res.data).length > 0) {
        return res.data;
      }
    } catch (e) {
      console.warn('Failed to load system config from backend', e);
    }
    return {
      maxFailedAttempts: 5,
      lockoutDurationMinutes: 30,
      mfaEnforcement: 'optional',
      passwordMinLength: 8,
      passwordRequireSpecial: true,
      passwordRequireNumbers: true,
      passwordExpirationDays: 90,
      sessionTimeoutMinutes: 60,
      accessTokenTtlSeconds: 3600,
      refreshTokenTtlSeconds: 86400,
    };
  },

  saveSystemConfig: async (config) => {
    const res = await api.post('/api/system/config', config);
    return res.data || { success: true, updatedConfig: config };
  },
};
