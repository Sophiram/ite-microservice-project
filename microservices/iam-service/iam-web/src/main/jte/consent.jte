@import java.util.Map
@import java.util.List
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param String clientId
@param String state
@param List<String> scopes
<!doctype html>
<html lang="${localeTag}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${messages.get("consent.title")} · ${messages.get("common.brand")}</title>
        <link rel="stylesheet" href="/css/iam.css">
    </head>
    <body>
        <div class="auth-wrapper">
            <div class="auth-card" style="max-width: 480px;">
                <div class="auth-header">
                    <div class="brand-badge" style="background: linear-gradient(135deg, #2563eb, #1d4ed8);">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                        </svg>
                    </div>
                    <h1 class="auth-title">${messages.get("consent.title")}</h1>
                    <p class="auth-subtitle">${messages.get("consent.description")}</p>
                    <div style="margin-top: 0.75rem;">
                        <span class="meta-badge" style="font-size: 0.85rem; padding: 0.35rem 0.85rem;">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 14px; height: 14px;">
                                <rect x="2" y="3" width="20" height="14" rx="2" ry="2"/>
                                <line x1="8" y1="21" x2="16" y2="21"/>
                                <line x1="12" y1="17" x2="12" y2="21"/>
                            </svg>
                            ${clientId}
                        </span>
                    </div>
                </div>

                <form id="consentForm" action="/oauth2/authorize" method="post">
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                    <input type="hidden" name="client_id" value="${clientId}">
                    <input type="hidden" name="state" value="${state}">

                    <h2 class="form-section-title" style="margin-top: 1rem; font-size: 0.95rem;">
                        ${messages.get("consent.scope")}
                    </h2>

                    <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 0.75rem 1rem; margin-bottom: 1.5rem;">
                        @for (String scope : scopes)
                        <label style="display: flex; align-items: center; gap: 0.65rem; padding: 0.5rem 0; cursor: pointer; font-size: 0.9rem; font-weight: 500; color: #334155;">
                            <input type="checkbox" name="scope" value="${scope}" checked style="width: 18px; height: 18px; accent-color: #175cd3; cursor: pointer;">
                            <span>${scope}</span>
                        </label>
                        @endfor
                    </div>

                    <div style="display: flex; gap: 0.75rem;">
                        <button type="submit" class="auth-submit-btn" style="flex: 1; margin-top: 0;">
                            <span>${messages.get("consent.allow")}</span>
                        </button>
                    </div>
                </form>

                <form action="/oauth2/authorize" method="post" style="margin-top: 0.75rem;">
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                    <input type="hidden" name="client_id" value="${clientId}">
                    <input type="hidden" name="state" value="${state}">
                    <button class="btn-secondary-modern" type="submit" style="width: 100%; justify-content: center; height: 44px;">
                        <span>${messages.get("consent.cancel")}</span>
                    </button>
                </form>
            </div>
        </div>
    </body>
</html>
