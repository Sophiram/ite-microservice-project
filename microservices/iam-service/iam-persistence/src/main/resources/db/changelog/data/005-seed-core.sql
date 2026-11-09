--liquibase formatted sql

--changeset iam:011-seed-core
INSERT INTO roles (name, description, is_system, is_active, created_by, updated_by)
VALUES ('ADMIN', 'System administrator', TRUE, TRUE, 'system', 'system'),
       ('USER', 'Standard user', TRUE, TRUE, 'system', 'system');

INSERT INTO permissions (name, resource, action, description, is_system, is_active, created_by, updated_by)
VALUES ('user:*', 'user', '*', 'All user operations', TRUE, TRUE, 'system', 'system'),
       ('role:*', 'role', '*', 'All role operations', TRUE, TRUE, 'system', 'system'),
       ('permission:*', 'permission', '*', 'All permission operations', TRUE, TRUE, 'system', 'system'),
       ('user:create', 'user', 'create', 'Create users', TRUE, TRUE, 'system', 'system'),
       ('user:read', 'user', 'read', 'Read users', TRUE, TRUE, 'system', 'system'),
       ('user:update', 'user', 'update', 'Update users', TRUE, TRUE, 'system', 'system'),
       ('user:delete', 'user', 'delete', 'Delete users', TRUE, TRUE, 'system', 'system'),
       ('user:assign', 'user', 'assign', 'Assign user roles and permissions', TRUE, TRUE, 'system', 'system'),
       ('role:create', 'role', 'create', 'Create roles', TRUE, TRUE, 'system', 'system'),
       ('role:read', 'role', 'read', 'Read roles', TRUE, TRUE, 'system', 'system'),
       ('role:update', 'role', 'update', 'Update roles', TRUE, TRUE, 'system', 'system'),
       ('role:delete', 'role', 'delete', 'Delete roles', TRUE, TRUE, 'system', 'system'),
       ('role:assign', 'role', 'assign', 'Assign permissions to roles', TRUE, TRUE, 'system', 'system'),
       ('permission:create', 'permission', 'create', 'Create permissions', TRUE, TRUE, 'system', 'system'),
       ('permission:read', 'permission', 'read', 'Read permissions', TRUE, TRUE, 'system', 'system'),
       ('permission:update', 'permission', 'update', 'Update permissions', TRUE, TRUE, 'system', 'system'),
       ('permission:delete', 'permission', 'delete', 'Delete permissions', TRUE, TRUE, 'system', 'system');

INSERT INTO role_permissions (role_id, permission_id, assigned_by, reason)
SELECT roles.id, permissions.id, 'system', 'Default administrator permissions'
FROM roles CROSS JOIN permissions
WHERE roles.name = 'ADMIN';

INSERT INTO role_permissions (role_id, permission_id, assigned_by, reason)
SELECT roles.id, permissions.id, 'system', 'Default user permissions'
FROM roles CROSS JOIN permissions
WHERE roles.name = 'USER' AND permissions.name = 'user:read';

INSERT INTO users
    (uuid, username, email, email_verified, password_hash, given_name, family_name, locale,
     enabled, account_non_expired, account_non_locked, credentials_non_expired, created_by, updated_by)
VALUES
    (gen_random_uuid(), 'admin', 'admin@example.com', TRUE,
     NULLIF('${iam.admin.password-hash}', ''), 'System', 'Administrator', 'en', TRUE, TRUE, TRUE, TRUE,
     'system', 'system');

INSERT INTO user_roles (user_id, role_id, assigned_by, reason, created_by, updated_by)
SELECT users.id, roles.id, 'system', 'Seeded administrator account', 'system', 'system'
FROM users CROSS JOIN roles
WHERE users.username = 'admin' AND roles.name = 'ADMIN';
