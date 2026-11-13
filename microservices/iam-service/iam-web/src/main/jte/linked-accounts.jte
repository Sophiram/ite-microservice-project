@import java.util.Map
@import java.util.List
@import kh.edu.istad.iam.application.result.IdentityResult
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param List<IdentityResult> identities
<!doctype html>
<html lang="${localeTag}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${messages.get("profile.linkedAccounts")} · ${messages.get("common.brand")}</title>
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
                    <a class="app-nav-link active" href="/profile/accounts">${messages.get("profile.linkedAccounts")}</a>
                    <a class="app-nav-link" href="/profile/apps">${messages.get("profile.connectedApps")}</a>
                </nav>
            </div>
        </header>

        <main class="app-container">
            <div class="page-header">
                <div>
                    <h1 class="page-title">${messages.get("profile.linkedAccounts")}</h1>
                    <p class="page-subtitle">External identity providers linked to your IAM account</p>
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
                @if (identities.isEmpty())
                <div class="empty-state">
                    <svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
                        <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
                    </svg>
                    <p>${messages.get("identity.none")}</p>
                </div>
                @endif

                @for (IdentityResult identity : identities)
                <div class="item-card">
                    <div class="item-info">
                        <div class="item-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                                <circle cx="8.5" cy="7" r="4"/>
                            </svg>
                        </div>
                        <div>
                            <h2 class="item-title">${identity.provider()}</h2>
                            <p class="item-subtitle">
                                ${identity.displayName() != null && !identity.displayName().isBlank() ? identity.displayName() : (identity.email() != null ? identity.email() : "")}
                                · <span class="muted">${messages.get("identity.linkedAt")}: ${identity.linkedAt().toString()}</span>
                            </p>
                        </div>
                    </div>
                    <div>
                        <form action="/profile/accounts/${identity.uuid().toString()}/unlink" method="post" onsubmit="return confirm('Are you sure you want to unlink this account?');" style="margin: 0;">
                            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                            <button class="btn-danger-modern" type="submit">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 14px; height: 14px;">
                                    <polyline points="3 6 5 6 21 6"/>
                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                                </svg>
                                <span>${messages.get("identity.unlink")}</span>
                            </button>
                        </form>
                    </div>
                </div>
                @endfor
            </div>
        </main>
    </body>
</html>
