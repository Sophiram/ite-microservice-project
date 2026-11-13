@import java.util.Map
@import java.util.List
@import kh.edu.istad.iam.application.result.ConnectedApplicationResult
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param List<ConnectedApplicationResult> applications
<!doctype html>
<html lang="${localeTag}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${messages.get("profile.connectedApps")} · ${messages.get("common.brand")}</title>
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
                    <a class="app-nav-link" href="/">${messages.get("common.home")}</a>
                    <a class="app-nav-link" href="/profile/edit">${messages.get("profile.edit")}</a>
                    <a class="app-nav-link" href="/profile/accounts">${messages.get("profile.linkedAccounts")}</a>
                    <a class="app-nav-link active" href="/profile/apps">${messages.get("profile.connectedApps")}</a>
                </nav>
            </div>
        </header>

        <main class="app-container">
            <div class="page-header">
                <div>
                    <h1 class="page-title">${messages.get("profile.connectedApps")}</h1>
                    <p class="page-subtitle">Third-party clients and services authorized to access your account</p>
                </div>
                <a class="btn-secondary-modern" href="/">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 15px; height: 15px;">
                        <line x1="19" y1="12" x2="5" y2="12"/>
                        <polyline points="12 19 5 12 12 5"/>
                    </svg>
                    <span>${messages.get("common.back")}</span>
                </a>
            </div>

            <div class="card-modern">
                @if (applications.isEmpty())
                <div class="empty-state">
                    <svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="2" y="3" width="20" height="14" rx="2" ry="2"/>
                        <line x1="8" y1="21" x2="16" y2="21"/>
                        <line x1="12" y1="17" x2="12" y2="21"/>
                    </svg>
                    <p>${messages.get("application.none")}</p>
                </div>
                @endif

                @for (ConnectedApplicationResult application : applications)
                <div class="item-card">
                    <div class="item-info">
                        <div class="item-icon" style="background: #faf5ff; color: #7e22ce;">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <rect x="2" y="3" width="20" height="14" rx="2" ry="2"/>
                                <line x1="8" y1="21" x2="16" y2="21"/>
                                <line x1="12" y1="17" x2="12" y2="21"/>
                            </svg>
                        </div>
                        <div>
                            <h2 class="item-title">${application.clientName() != null ? application.clientName() : application.clientId()}</h2>
                            <div style="margin-top: 0.35rem;">
                                @for (String scope : application.scopes())
                                <span class="scope-badge">${scope}</span>
                                @endfor
                            </div>
                        </div>
                    </div>
                    <div>
                        <form action="/profile/apps/${application.clientId()}/revoke" method="post" onsubmit="return confirm('Revoke access for this application?');" style="margin: 0;">
                            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                            <button class="btn-danger-modern" type="submit">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 14px; height: 14px;">
                                    <circle cx="12" cy="12" r="10"/>
                                    <line x1="15" y1="9" x2="9" y2="15"/>
                                    <line x1="9" y1="9" x2="15" y2="15"/>
                                </svg>
                                <span>${messages.get("application.revoke")}</span>
                            </button>
                        </form>
                    </div>
                </div>
                @endfor
            </div>
        </main>
    </body>
</html>
