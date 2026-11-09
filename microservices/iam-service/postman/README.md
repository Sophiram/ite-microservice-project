# IAM Service - Step-by-Step Postman Testing Guide

This comprehensive guide walks you through testing every feature of the IAM Service using the provided Postman collection and local environment. Follow the steps sequentially from setup to cleanup.

---

## 1. Prerequisites and Service Startup

### 1.1 Verify Infrastructure & Configuration
Ensure PostgreSQL is running and environment variables are properly exported in your shell. The IAM Service requires an admin password hash to seed the initial administrator (`admin` / `admin@example.com`).

If you have a `.env` file in `microservices/iam-service/.env`, export its variables in PowerShell:
```powershell
Get-Content microservices/iam-service/.env | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]*)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2], 'Process')
    }
}
```

### 1.2 Start IAM Service
Start the service from the repository root:
```powershell
.\gradlew.bat -p microservices/iam-service :iam-main:bootRun
```

### 1.3 Health & Discovery Verification
Before sending API requests, verify that the service is running and OpenID Connect discovery is responsive:
- Open your browser or terminal and execute:
  ```powershell
  curl http://localhost:9000/.well-known/openid-configuration
  ```
- Expect HTTP status `200 OK` with JSON metadata (issuer `http://localhost:9000`, endpoints for `/oauth2/authorize`, `/oauth2/token`, `/userinfo`, `/oauth2/jwks`, etc.).

> [!IMPORTANT]
> **Database Migration Integrity**: If startup fails due to Liquibase checksum mismatches (e.g., changeset `011-seed-core`), resolve the database migration state before proceeding. Do not modify already-applied changesets in existing databases; add new SQL migrations in new changesets or reset your local disposable test database.

---

## 2. Postman Setup & Environment Variables

### 2.1 Import Collection and Environment
1. In Postman, click **Import** (top left).
2. Select both files located in this directory:
   - [iam-service.postman_collection.json](./iam-service.postman_collection.json)
   - [local.postman_environment.json](./local.postman_environment.json)
3. In the top-right environment selector, choose **IAM Service - local**.

### 2.2 Environment Variables Reference
Ensure the following variables are configured in **IAM Service - local**:

| Variable | Example / Default Value | Type | Description / Handling |
| :--- | :--- | :--- | :--- |
| `baseUrl` | `http://localhost:9000` | default | Target service URL. |
| `browser_client_id` | `iam-browser` | default | Seeded public client for browser/PKCE authorization. |
| `redirect_uri` | `https://oauth.pstmn.io/v1/callback` | default | Callback URL matching `IAM_BROWSER_REDIRECT_URI`. |
| `new_username` | `postman-user-unique` | default | Unique username for self-registration test. Change per rerun. |
| `new_email` | `postman-user-unique@example.com` | default | Unique email for self-registration test. Change per rerun. |
| `new_password` | `LocalP@ssw0rd!` | secret | Password used for registration and managed user (min 8 chars). |
| `rotated_password` | `NewP@ssw0rd999!` | secret | New password used in self-service password change. |
| `managed_username` | `postman-managed-user` | default | Username for admin-created managed user. Change per rerun. |
| `managed_email` | `postman-managed@example.com` | default | Email for admin-created managed user. Change per rerun. |
| `role_name` | `POSTMAN_TEST_ROLE` | default | Unique role name for role management tests. Change per rerun. |
| `permission_resource` | `postman-demo` | default | Resource name for test permission. Change per rerun. |
| `permission_action` | `read` | default | Action name for test permission. |
| `disposable_permission_resource` | `postman-disposable` | default | Resource for disposable permission (deactivation test). |
| `disposable_permission_action` | `read` | default | Action for disposable permission. |
| `dcr_client_id` | `iam-dcr` | default | Seeded DCR client ID (requires `IAM_DCR_CLIENT_SECRET`). |
| `dcr_client_secret` | *(Local DCR Secret)* | secret | Secret matching your server's `IAM_DCR_CLIENT_SECRET`. |
| `dcr_access_token` | *(Auto-populated)* | secret | Captured automatically by DCR token request. |
| `registered_client_id` | *(Auto-populated)* | default | Captured automatically by DCR client registration. |
| `self_service_access_token` | *(Manually generated)* | secret | Access token obtained by logging in as the registered user. |
| `user_uuid` | *(Auto-populated)* | default | Captured automatically after public user registration. |
| `managed_user_uuid` | *(Auto-populated)* | default | Captured automatically after creating managed user. |
| `role_uuid` | *(Auto-populated)* | default | Captured automatically after creating test role. |
| `permission_uuid` | *(Auto-populated)* | default | Captured automatically after creating test permission. |
| `deactivate_permission_uuid` | *(Auto-populated)* | default | Captured automatically after creating disposable permission. |
| `identity_uuid` | *(Blank by default)* | default | UUID of an external linked identity (if testing social login). |

---

## 3. Step 1: Obtain Administrator OAuth 2.0 Token

Most administrative endpoints require bearer authorization with administrative privileges. The collection is pre-configured with top-level OAuth 2.0 settings using Authorization Code with PKCE.

### How to generate the Admin Token:
1. Click on the collection name **IAM Service**.
2. Navigate to the **Authorization** tab.
3. Verify settings:
   - **Type**: `OAuth 2.0`
   - **Grant type**: `Authorization Code (With PKCE)`
   - **Auth URL**: `{{baseUrl}}/oauth2/authorize`
   - **Access Token URL**: `{{baseUrl}}/oauth2/token`
   - **Client ID**: `{{browser_client_id}}`
   - **Client Secret**: *(Leave blank)*
   - **Callback URL**: `{{redirect_uri}}`
   - **Scope**: `openid profile email address phone`
   - **Code challenge method**: `S256`
   - **Client authentication**: `Send client credentials in body`
4. Scroll down and click **Get New Access Token**.
5. Postman opens a browser window:
   - Log in with administrator credentials:
     - **Username**: `admin`
     - **Password**: *(Password set in `IAM_ADMIN_PASSWORD_HASH`)*
   - On the Consent Screen, approve the requested scopes.
6. Postman will receive the token. Click **Use Token**.
7. Validate the token by opening request `05. OAuth 2.0 and OIDC -> Get current user (/api/me) with administrator token` and clicking **Send**. Expect `200 OK` returning admin profile details.

---

## 4. Step 2: Setup and Registration (Folder `01. Setup and registration`)

Execute the requests in folder `01. Setup and registration` in order. This initializes all test entities and saves their UUIDs into your Postman environment.

### 4.1 Register self-service user
- **Request**: `POST {{baseUrl}}/api/auth/register`
- **Auth**: None (`No Auth`)
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "username": "{{new_username}}",
    "email": "{{new_email}}",
    "password": "{{new_password}}",
    "confirmPassword": "{{new_password}}",
    "givenName": "Postman",
    "familyName": "Tester",
    "locale": "en"
  }
  ```
- **Expected Status**: `201 Created`
- **Automated Test Script**: Captures `response.json().uuid` into `user_uuid`.

### 4.2 Create test permission
- **Request**: `POST {{baseUrl}}/api/permissions`
- **Auth**: Inherits Admin OAuth Token
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "resource": "{{permission_resource}}",
    "action": "{{permission_action}}",
    "description": "Postman test permission"
  }
  ```
- **Expected Status**: `201 Created`
- **Automated Test Script**: Captures `response.json().uuid` into `permission_uuid`.

### 4.3 Create disposable permission for deactivate check
- **Request**: `POST {{baseUrl}}/api/permissions`
- **Auth**: Inherits Admin OAuth Token
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "resource": "{{disposable_permission_resource}}",
    "action": "{{disposable_permission_action}}",
    "description": "Disposable Postman deactivation test"
  }
  ```
- **Expected Status**: `201 Created`
- **Automated Test Script**: Captures `response.json().uuid` into `deactivate_permission_uuid`.

### 4.4 Create test role
- **Request**: `POST {{baseUrl}}/api/roles`
- **Auth**: Inherits Admin OAuth Token
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "name": "{{role_name}}",
    "description": "Postman test role",
    "permissionUuids": []
  }
  ```
- **Expected Status**: `201 Created`
- **Automated Test Script**: Captures `response.json().uuid` into `role_uuid`.

### 4.5 Create managed test user
- **Request**: `POST {{baseUrl}}/api/users`
- **Auth**: Inherits Admin OAuth Token
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "username": "{{managed_username}}",
    "email": "{{managed_email}}",
    "password": "{{new_password}}",
    "givenName": "Managed",
    "familyName": "Tester",
    "gender": "other",
    "birthdate": "1998-01-15",
    "phoneNumber": "+85512345678",
    "zoneinfo": "Asia/Phnom_Penh",
    "locale": "en",
    "address": {
      "streetAddress": "12 Test Street",
      "locality": "Phnom Penh",
      "region": "Phnom Penh",
      "postalCode": "12000",
      "country": "KH"
    },
    "roleUuids": []
  }
  ```
- **Expected Status**: `201 Created`
- **Automated Test Script**: Captures `response.json().uuid` into `managed_user_uuid`.

---

## 5. Step 3: User Administration (Folder `02. User administration`)

Run these requests using the **Administrator Token** to test administrative user management features.

### 5.1 List users (Paged & Sorted)
- **Request**: `GET {{baseUrl}}/api/users?page=0&size=20&sort=username&direction=ASC`
- **Authority Required**: `user:read`
- **Expected Status**: `200 OK`
- **Verification**: Body contains paged array in `content` and paging metadata in `page`. Verify `admin`, `managed_username`, and `new_username` exist in the list.

### 5.2 Get managed user
- **Request**: `GET {{baseUrl}}/api/users/{{managed_user_uuid}}`
- **Authority Required**: `user:read`
- **Expected Status**: `200 OK`
- **Verification**: Returns the managed user's details matching the created data.

### 5.3 Update managed user profile
- **Request**: `PATCH {{baseUrl}}/api/users/{{managed_user_uuid}}`
- **Authority Required**: `user:update`
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "givenName": "Updated",
    "familyName": "Tester",
    "middleName": "API",
    "nickname": "postman",
    "gender": "other",
    "birthdate": "1998-01-15",
    "profileUrl": "https://example.com/profile",
    "pictureUrl": "https://example.com/avatar.png",
    "websiteUrl": "https://example.com",
    "zoneinfo": "Asia/Phnom_Penh",
    "locale": "en",
    "phoneNumber": "+85512345678",
    "address": {
      "streetAddress": "12 Test Street",
      "locality": "Phnom Penh",
      "region": "Phnom Penh",
      "postalCode": "12000",
      "country": "KH"
    }
  }
  ```
- **Expected Status**: `200 OK`
- **Verification**: Response returns updated profile fields (e.g. `middleName: "API"`, `nickname: "postman"`).

### 5.4 Get managed user authorities (Initial state)
- **Request**: `GET {{baseUrl}}/api/users/{{managed_user_uuid}}/authorities`
- **Authority Required**: `user:read`
- **Expected Status**: `200 OK`
- **Verification**: Inspect `roles`, `directPermissions`, and `effectivePermissions`. Initially empty.

### 5.5 Assign test role to user
- **Request**: `POST {{baseUrl}}/api/users/{{managed_user_uuid}}/roles`
- **Authority Required**: `user:assign`
- **Body**:
  ```json
  {
    "roleUuids": ["{{role_uuid}}"],
    "expiresAt": "2099-01-01T00:00:00Z",
    "reason": "Postman role-assignment test"
  }
  ```
- **Expected Status**: `200 OK`
- **Verification**: Check user authorities (`GET .../authorities`); the role is now present.

### 5.6 Grant test permission directly to user
- **Request**: `POST {{baseUrl}}/api/users/{{managed_user_uuid}}/permissions`
- **Authority Required**: `user:assign`
- **Body**:
  ```json
  {
    "permissionUuids": ["{{permission_uuid}}"],
    "effect": "GRANT",
    "expiresAt": "2099-01-01T00:00:00Z",
    "reason": "Postman permission test"
  }
  ```
- **Expected Status**: `200 OK`
- **Verification**: Check user authorities; `directPermissions` contains this permission with `GRANT` effect.

### 5.7 Revoke direct permission from user
- **Request**: `DELETE {{baseUrl}}/api/users/{{managed_user_uuid}}/permissions/{{permission_uuid}}?effect=GRANT&reason=Postman%20test`
- **Authority Required**: `user:assign`
- **Expected Status**: `204 No Content`
- **Verification**: Calling authorities endpoint confirms direct permission is revoked.

### 5.8 Revoke test role from user
- **Request**: `DELETE {{baseUrl}}/api/users/{{managed_user_uuid}}/roles/{{role_uuid}}?reason=Postman%20test`
- **Authority Required**: `user:assign`
- **Expected Status**: `204 No Content`
- **Verification**: Calling authorities endpoint confirms role is revoked.

### 5.9 List managed user identities
- **Request**: `GET {{baseUrl}}/api/users/{{managed_user_uuid}}/identities`
- **Authority Required**: `user:read`
- **Expected Status**: `200 OK`
- **Verification**: Returns array of external linked identities (empty if none linked).

### 5.10 Unlink identity from managed user (Optional)
- **Request**: `DELETE {{baseUrl}}/api/users/{{managed_user_uuid}}/identities/{{identity_uuid}}`
- **Authority Required**: `user:update`
- **Expected Status**: `204 No Content`
- > [!NOTE]
  > Skip this request unless `identity_uuid` is set to an active linked identity.

### 5.11 Disable managed user
- **Request**: `POST {{baseUrl}}/api/users/{{managed_user_uuid}}/disable`
- **Authority Required**: `user:update`
- **Expected Status**: `204 No Content`
- **Verification**: Fetching the user shows `enabled: false`.

### 5.12 Enable managed user
- **Request**: `POST {{baseUrl}}/api/users/{{managed_user_uuid}}/enable`
- **Authority Required**: `user:update`
- **Expected Status**: `204 No Content`
- **Verification**: Fetching the user shows `enabled: true`.

### 5.13 Lock managed user
- **Request**: `POST {{baseUrl}}/api/users/{{managed_user_uuid}}/lock`
- **Authority Required**: `user:update`
- **Expected Status**: `204 No Content`
- **Verification**: Fetching the user shows `locked: true`.

### 5.14 Unlock managed user
- **Request**: `POST {{baseUrl}}/api/users/{{managed_user_uuid}}/unlock`
- **Authority Required**: `user:update`
- **Expected Status**: `204 No Content`
- **Verification**: Fetching the user shows `locked: false`.

### 5.15 Reset managed user password by Admin
- **Request**: `PUT {{baseUrl}}/api/users/{{managed_user_uuid}}/password`
- **Authority Required**: `user:update`
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {"newPassword":"{{new_password}}"}
  ```
- **Expected Status**: `204 No Content`

---

## 6. Step 4: Role and Permission Management (Folder `03. Roles and permissions`)

Execute these requests with the **Administrator Token** to test RBAC lifecycles.

### 6.1 List roles
- **Request**: `GET {{baseUrl}}/api/roles?page=0&size=20&sort=name&direction=ASC`
- **Authority Required**: `role:read`
- **Expected Status**: `200 OK` (paged list containing system roles `ADMIN`, `USER`, and your test role).

### 6.2 Get test role
- **Request**: `GET {{baseUrl}}/api/roles/{{role_uuid}}`
- **Authority Required**: `role:read`
- **Expected Status**: `200 OK`

### 6.3 Update test role description
- **Request**: `PATCH {{baseUrl}}/api/roles/{{role_uuid}}`
- **Authority Required**: `role:update`
- **Body**:
  ```json
  {"description":"Updated by Postman"}
  ```
- **Expected Status**: `200 OK`

### 6.4 Assign test permission to role
- **Request**: `POST {{baseUrl}}/api/roles/{{role_uuid}}/permissions`
- **Authority Required**: `role:assign`
- **Body**:
  ```json
  {
    "permissionUuids": ["{{permission_uuid}}"],
    "expiresAt": "2099-01-01T00:00:00Z",
    "reason": "Postman role-permission test"
  }
  ```
- **Expected Status**: `200 OK`

### 6.5 Revoke test permission from role
- **Request**: `DELETE {{baseUrl}}/api/roles/{{role_uuid}}/permissions/{{permission_uuid}}?reason=Postman%20test`
- **Authority Required**: `role:assign`
- **Expected Status**: `204 No Content`

### 6.6 Deactivate test role
- **Request**: `POST {{baseUrl}}/api/roles/{{role_uuid}}/deactivate`
- **Authority Required**: `role:update`
- **Expected Status**: `204 No Content`
- **Verification**: `GET /api/roles/{{role_uuid}}` shows `active: false`.

### 6.7 Activate test role
- **Request**: `POST {{baseUrl}}/api/roles/{{role_uuid}}/activate`
- **Authority Required**: `role:update`
- **Expected Status**: `204 No Content`
- **Verification**: `GET /api/roles/{{role_uuid}}` shows `active: true`.

### 6.8 List permissions
- **Request**: `GET {{baseUrl}}/api/permissions?page=0&size=50&sort=name&direction=ASC`
- **Authority Required**: `permission:read`
- **Expected Status**: `200 OK`

### 6.9 Get test permission
- **Request**: `GET {{baseUrl}}/api/permissions/{{permission_uuid}}`
- **Authority Required**: `permission:read`
- **Expected Status**: `200 OK`

### 6.10 Update test permission description
- **Request**: `PATCH {{baseUrl}}/api/permissions/{{permission_uuid}}`
- **Authority Required**: `permission:update`
- **Body**:
  ```json
  {"description":"Updated by Postman"}
  ```
- **Expected Status**: `200 OK`

### 6.11 Activate test permission
- **Request**: `POST {{baseUrl}}/api/permissions/{{permission_uuid}}/activate`
- **Authority Required**: `permission:update`
- **Expected Status**: `204 No Content`

### 6.12 Deactivate disposable permission
- **Request**: `POST {{baseUrl}}/api/permissions/{{deactivate_permission_uuid}}/deactivate`
- **Authority Required**: `permission:update`
- **Expected Status**: `204 No Content`

> [!WARNING]
> **Deactivation Implementation Behavior**: In the current implementation, `POST /api/permissions/{uuid}/deactivate` routes directly to the delete use case, deleting the record from the database. A subsequent `GET /api/permissions/{{deactivate_permission_uuid}}` may return `404 Not Found`. Always use the disposable permission (`deactivate_permission_uuid`) for this test so your primary `permission_uuid` is preserved.

---

## 7. Step 5: Self-Service User Operations (Folder `04. Self-service (switch token)`)

The requests in Folder 04 authenticate using the environment variable `self_service_access_token`.

### 7.1 Acquire Self-Service User Token
Before running the self-service tests, obtain an OAuth access token for the registered user:
1. Open request `04. Self-service (switch token) -> Get current user`.
2. Go to the request's **Authorization** tab (or the folder's Authorization).
3. Select **OAuth 2.0 -> Get New Access Token**:
   - **Auth URL**: `{{baseUrl}}/oauth2/authorize`
   - **Access Token URL**: `{{baseUrl}}/oauth2/token`
   - **Client ID**: `{{browser_client_id}}`
   - **Scope**: `openid profile email address phone`
   - **Code challenge method**: `S256`
4. Click **Get New Access Token**. In the browser login dialog, sign in as:
   - **Username**: Value of `{{new_username}}`
   - **Password**: Value of `{{new_password}}`
5. Approve consent. When the token is displayed, copy the `access_token` string.
6. Open your Postman Environment (`IAM Service - local`) and paste it into the **`self_service_access_token`** variable. Save the environment.

### 7.2 Get current user profile (`/api/me`)
- **Request**: `GET {{baseUrl}}/api/me`
- **Auth**: Bearer `{{self_service_access_token}}`
- **Expected Status**: `200 OK`
- **Verification**: Returns profile for `new_username`.

### 7.3 Update current user profile
- **Request**: `PATCH {{baseUrl}}/api/me`
- **Auth**: Bearer `{{self_service_access_token}}`
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "givenName": "Postman Updated",
    "familyName": "Tester",
    "locale": "en",
    "address": {
      "streetAddress": "12 Test Street",
      "locality": "Phnom Penh",
      "region": "Phnom Penh",
      "postalCode": "12000",
      "country": "KH"
    }
  }
  ```
- **Expected Status**: `200 OK`
- **Verification**: Profile reflects updated names and address.

### 7.4 Change current user password
- **Request**: `PUT {{baseUrl}}/api/me/password`
- **Auth**: Bearer `{{self_service_access_token}}`
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "currentPassword": "{{new_password}}",
    "newPassword": "{{rotated_password}}",
    "confirmPassword": "{{rotated_password}}"
  }
  ```
- **Expected Status**: `204 No Content`
- > [!TIP]
  > Once changed, future logins for this user must use `rotated_password`.

### 7.5 List current user identities
- **Request**: `GET {{baseUrl}}/api/me/identities`
- **Auth**: Bearer `{{self_service_access_token}}`
- **Expected Status**: `200 OK`
- **Verification**: Returns array of connected social providers.

### 7.6 Unlink identity from current user (Optional)
- **Request**: `DELETE {{baseUrl}}/api/me/identities/{{identity_uuid}}`
- **Auth**: Bearer `{{self_service_access_token}}`
- **Expected Status**: `204 No Content` (or `409 Conflict` if this is the user's only login method). Skip if no identity is linked.

---

## 8. Step 6: OAuth 2.0 & OpenID Connect Inspection (Folder `05. OAuth 2.0 and OIDC`)

These requests verify the standards compliance of the Authorization Server.

### 8.1 Verify current admin profile
- **Request**: `GET {{baseUrl}}/api/me`
- **Auth**: Inherits Admin OAuth Token
- **Expected Status**: `200 OK` (verifies admin user claims and profile).

### 8.2 OIDC Provider Discovery
- **Request**: `GET {{baseUrl}}/.well-known/openid-configuration`
- **Auth**: None (`No Auth`)
- **Expected Status**: `200 OK`
- **Verification**: Inspect advertised capabilities:
  - `issuer`: `http://localhost:9000`
  - `authorization_endpoint`, `token_endpoint`, `userinfo_endpoint`, `jwks_uri`, `registration_endpoint`
  - Supported scopes: `openid`, `profile`, `email`, `address`, `phone`, `offline_access`.

### 8.3 JSON Web Key Set (JWKS)
- **Request**: `GET {{baseUrl}}/oauth2/jwks`
- **Auth**: None (`No Auth`)
- **Expected Status**: `200 OK`
- **Verification**: Returns public key set with `keys` array containing `kty: "RSA"`, `use: "sig"`, `kid`, `n`, `e`.

### 8.4 UserInfo Endpoint
- **Request**: `GET {{baseUrl}}/userinfo`
- **Auth**: Inherits Admin OAuth Token (or User Token)
- **Expected Status**: `200 OK`
- **Verification**: Returns OIDC standard claims (e.g. `sub`, `preferred_username`, `email`, `name`).

### 8.5 Refresh Token Flow (Optional Verification)
If the authorization code token response includes a `refresh_token`:
- **Method & URL**: `POST {{baseUrl}}/oauth2/token`
- **Headers**: `Content-Type: application/x-www-form-urlencoded`
- **Body**:
  ```
  grant_type=refresh_token
  refresh_token={{refresh_token}}
  client_id={{browser_client_id}}
  ```
- **Expected Status**: `200 OK` with a renewed access token.

---

## 9. Step 7: Dynamic Client Registration (Folder `06. Dynamic Client Registration`)

Dynamic Client Registration (RFC 7591) allows clients to register themselves dynamically when configured with a registration access token or secret.

> [!NOTE]
> This feature requires `IAM_DCR_CLIENT_SECRET` to be configured in your environment before the service starts.

### 9.1 Get DCR Client-Credentials Token
- **Request**: `POST {{baseUrl}}/oauth2/token`
- **Auth**: Basic Auth:
  - **Username**: `{{dcr_client_id}}` (seeded as `iam-dcr`)
  - **Password**: `{{dcr_client_secret}}`
- **Headers**: `Content-Type: application/x-www-form-urlencoded`
- **Body**:
  ```
  grant_type=client_credentials
  scope=client.create
  ```
- **Expected Status**: `200 OK`
- **Automated Test Script**: Captures `response.json().access_token` into `dcr_access_token`.

### 9.2 Register OAuth Client
- **Request**: `POST {{baseUrl}}/connect/register`
- **Auth**: Bearer `{{dcr_access_token}}`
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "client_name": "Postman registered client",
    "redirect_uris": ["https://oauth.pstmn.io/v1/callback"],
    "grant_types": ["authorization_code"],
    "response_types": ["code"],
    "token_endpoint_auth_method": "none",
    "scope": "openid profile email"
  }
  ```
- **Expected Status**: `201 Created`
- **Verification & Script**: Captures `response.json().client_id` into `registered_client_id`. Verify response contains client metadata and registration client URI.

---

## 10. Step 8: Social Login & External Identities (Browser & API)

There is no direct REST endpoint to create linked identities without provider interaction.

### How to test Social Identity integration:
1. Ensure provider credentials (e.g. Google, GitHub, Facebook, Telegram) and redirect URIs are configured in `.env`.
2. Open a browser and navigate to:
   ```
   http://localhost:9000/oauth2/authorization/google
   ```
   *(Replace `google` with `github`, `facebook`, or `telegram`)*
3. Authenticate with your external provider account.
4. After returning to the application, obtain an access token for this account.
5. Send `GET {{baseUrl}}/api/me/identities` with this token:
   - Expect `200 OK` containing the linked provider name and provider subject.
   - Copy the identity `uuid` into environment variable `identity_uuid`.
6. Test unlinking:
   - Send `DELETE {{baseUrl}}/api/me/identities/{{identity_uuid}}`:
     - If the user has a local password or another linked account: Expect `204 No Content`.
     - If this is the user's sole authentication method: Expect `409 Conflict` (cannot unlink last login method).

---

## 11. Step 9: Negative & Security Validation Tests

Run these targeted negative checks to verify security guards and RFC 9457 Problem Details error formats:

| Test Scenario | Method & Endpoint | Auth | Request Body / Details | Expected Status & Response |
| :--- | :--- | :--- | :--- | :--- |
| **Unauthenticated access** | `GET /api/users` | None | None | `401 Unauthorized` |
| **Insufficient privileges** | `POST /api/users` | Self-service user token | Managed user payload | `403 Forbidden` |
| **Duplicate registration** | `POST /api/auth/register` | None | Duplicate `username` / `email` | `409 Conflict` |
| **Password validation failure** | `POST /api/auth/register` | None | Password shorter than 8 chars | `400 Bad Request` (RFC 9457 `application/problem+json`) |
| **Password mismatch** | `POST /api/auth/register` | None | `password` != `confirmPassword` | `400 Bad Request` |
| **Non-existent entity** | `GET /api/users/00000000-0000-0000-0000-000000000000` | Admin Token | None | `404 Not Found` |
| **Protected entity deletion guard** | `DELETE /api/roles/{admin_role_uuid}` | Admin Token | None | `403 Forbidden` (Cannot delete seeded `ADMIN` role) |
| **Invalid DCR scope** | `POST /connect/register` | Bearer `dcr_access_token` | `"scope": "unsupported.scope"` | `400 Bad Request` (`invalid_scope`) |

---

## 12. Step 10: Cleanup (Folder `07. Cleanup (run after tests)`)

After feature testing is complete, run the requests in folder `07. Cleanup (run after tests)` using the **Administrator Token** to tear down test records.

> [!CAUTION]
> Do not run cleanup before completing tests. Cleanup deletes the test users, role, and permission.

### 12.1 Delete managed test user
- **Request**: `DELETE {{baseUrl}}/api/users/{{managed_user_uuid}}`
- **Expected Status**: `204 No Content`

### 12.2 Delete test role
- **Request**: `DELETE {{baseUrl}}/api/roles/{{role_uuid}}`
- **Expected Status**: `204 No Content`

### 12.3 Delete test permission
- **Request**: `DELETE {{baseUrl}}/api/permissions/{{permission_uuid}}`
- **Expected Status**: `204 No Content`

### 12.4 Delete self-service test user
- **Request**: `DELETE {{baseUrl}}/api/users/{{user_uuid}}`
- **Expected Status**: `204 No Content`

---

## 13. Running with Postman Collection Runner

You can execute the test suite automatically using Postman's Collection Runner:

1. Right-click the **IAM Service** collection -> **Run collection**.
2. **Order of Folders**:
   1. `01. Setup and registration`
   2. `02. User administration`
   3. `03. Roles and permissions`
   4. `04. Self-service (switch token)` *(Only if `self_service_access_token` has been populated)*
   5. `05. OAuth 2.0 and OIDC`
   6. `06. Dynamic Client Registration` *(Only if `dcr_client_secret` is configured)*
   7. `07. Cleanup (run after tests)`
3. **Uncheck Optional Requests**:
   - In `02. User administration`: uncheck `Unlink identity from managed user (optional)`
   - In `04. Self-service (switch token)`: uncheck `Unlink identity from current user (optional)`
4. Ensure the **IAM Service - local** environment is selected.
5. Click **Run IAM Service**.
6. All assertions should pass with green status badges. For reruns against the same database, update `new_username`, `new_email`, `managed_username`, `managed_email`, `role_name`, and permission resources to fresh unique values.
