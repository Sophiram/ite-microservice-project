--liquibase formatted sql

--changeset iam:012-seed-admin

UPDATE users
SET password_hash = NULLIF('${iam.admin.password-hash}', ''),
    updated_by = 'system'
WHERE username = 'admin';