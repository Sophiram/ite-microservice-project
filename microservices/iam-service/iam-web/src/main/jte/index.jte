@import java.util.Map
@import java.util.List
@import kh.edu.istad.iam.application.result.UserResult
@import kh.edu.istad.iam.application.result.IdentityResult
@import kh.edu.istad.iam.application.result.ConnectedApplicationResult
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param UserResult user
@param List<IdentityResult> identities
@param List<ConnectedApplicationResult> applications
<!doctype html>
<html lang="${localeTag}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${messages.get("index.profile")} · ${messages.get("common.brand")}</title>
        <link rel="stylesheet" href="/css/iam.css">
    </head>
    <body>
        <header class="app-header">
            <div class="app-header-inner">
                <a class="app-brand" href="/">
                    <div class="app-brand-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                        </svg>
                    </div>
                    <span>${messages.get("common.brand")}</span>
                </a>
                <nav class="app-nav">
                    <a class="app-nav-link active" href="/">${messages.get("common.home")}</a>
                    <a class="app-nav-link" href="/profile/edit">${messages.get("profile.edit")}</a>
                    <a class="app-nav-link" href="/profile/accounts">${messages.get("profile.linkedAccounts")}</a>
                    <a class="app-nav-link" href="/profile/apps">${messages.get("profile.connectedApps")}</a>
                    <form action="/logout" method="post" style="margin: 0; display: inline;">
                        <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                        <button class="btn-secondary-modern" style="padding: 0.4rem 0.85rem;" type="submit">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 15px; height: 15px;">
                                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                                <polyline points="16 17 21 12 16 7"/>
                                <line x1="21" y1="12" x2="9" y2="12"/>
                            </svg>
                            <span>${messages.get("common.logout")}</span>
                        </button>
                    </form>
                </nav>
            </div>
        </header>

        <main class="app-container">
            <div class="card-modern">
                <div class="profile-hero">
                    @if (user.profile() != null && user.profile().pictureUrl() != null && !user.profile().pictureUrl().isBlank())
                    <img class="profile-hero-avatar" src="${user.profile().pictureUrl()}" alt="${user.fullName()}">
                    @else
                    <div class="profile-hero-placeholder">${user.fullName() != null && !user.fullName().isBlank() ? user.fullName().substring(0, 1).toUpperCase() : "U"}</div>
                    @endif
                    <div class="profile-hero-meta" style="flex: 1;">
                        <h1>${user.fullName()}</h1>
                        <p class="meta-email">${user.email()}</p>
                        <span class="meta-badge">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 13px; height: 13px;">
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                <circle cx="12" cy="7" r="4"/>
                            </svg>
                            @@${user.username()}
                        </span>
                    </div>
                    <div>
                        <a class="btn-secondary-modern" href="/profile/edit">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 16px; height: 16px;">
                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                            </svg>
                            <span>${messages.get("profile.edit")}</span>
                        </a>
                    </div>
                </div>
            </div>

            <div class="dashboard-grid">
                <div class="stat-card">
                    <div>
                        <div class="stat-card-header">
                            <span class="stat-label">${messages.get("profile.linkedAccounts")}</span>
                            <div class="stat-card-icon blue">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
                                    <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
                                </svg>
                            </div>
                        </div>
                        <div class="stat-count">${identities.size()}</div>
                        <p class="stat-label">Connected federated identities</p>
                    </div>
                    <a class="stat-link" href="/profile/accounts">
                        <span>Manage accounts</span>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 14px; height: 14px;">
                            <line x1="5" y1="12" x2="19" y2="12"/>
                            <polyline points="12 5 19 12 12 19"/>
                        </svg>
                    </a>
                </div>

                <div class="stat-card">
                    <div>
                        <div class="stat-card-header">
                            <span class="stat-label">${messages.get("profile.connectedApps")}</span>
                            <div class="stat-card-icon purple">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="2" y="3" width="20" height="14" rx="2" ry="2"/>
                                    <line x1="8" y1="21" x2="16" y2="21"/>
                                    <line x1="12" y1="17" x2="12" y2="21"/>
                                </svg>
                            </div>
                        </div>
                        <div class="stat-count">${applications.size()}</div>
                        <p class="stat-label">Authorized third-party clients</p>
                    </div>
                    <a class="stat-link" href="/profile/apps">
                        <span>${messages.get("profile.connectedApps")}</span>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 14px; height: 14px;">
                            <line x1="5" y1="12" x2="19" y2="12"/>
                            <polyline points="12 5 19 12 12 19"/>
                        </svg>
                    </a>
                </div>

                <div class="stat-card">
                    <div>
                        <div class="stat-card-header">
                            <span class="stat-label">${messages.get("profile.changePassword")}</span>
                            <div class="stat-card-icon amber">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                                </svg>
                            </div>
                        </div>
                        <div class="stat-count" style="font-size: 1.25rem; font-weight: 600; padding-top: 0.5rem;">Security</div>
                        <p class="stat-label">Manage your login credentials</p>
                    </div>
                    <a class="stat-link" href="/profile/password">
                        <span>${messages.get("profile.changePassword")}</span>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 14px; height: 14px;">
                            <line x1="5" y1="12" x2="19" y2="12"/>
                            <polyline points="12 5 19 12 12 19"/>
                        </svg>
                    </a>
                </div>
            </div>

            <div class="auth-lang-pills" style="margin-top: 3rem;">
                <a href="?lang=en" class="auth-lang-pill ${"en".equals(localeTag) ? "active" : ""}">${messages.get("common.english")}</a>
                <span style="color: #cbd5e1;">·</span>
                <a href="?lang=km" class="auth-lang-pill ${"km".equals(localeTag) ? "active" : ""}">${messages.get("common.khmer")}</a>
            </div>
        </main>
    </body>
</html>
