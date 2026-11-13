@import java.util.Map
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param String error
@param boolean saved
<!doctype html>
<html lang="${localeTag}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${messages.get("password.title")} · ${messages.get("common.brand")}</title>
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
                    <a class="app-nav-link" href="/profile/apps">${messages.get("profile.connectedApps")}</a>
                </nav>
            </div>
        </header>

        <div class="auth-wrapper" style="min-height: calc(100vh - 70px);">
            <div class="auth-card">
                <div class="auth-header">
                    <div class="brand-badge" style="background: linear-gradient(135deg, #d97706, #b45309);">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                        </svg>
                    </div>
                    <h1 class="auth-title">${messages.get("password.title")}</h1>
                    <p class="auth-subtitle">Choose a strong password to protect your account.</p>
                </div>

                @if (saved)
                <div class="auth-alert auth-alert-success">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                        <polyline points="22 4 12 14.01 9 11.01"/>
                    </svg>
                    <span>${messages.get("common.success")}</span>
                </div>
                @endif

                @if (!error.isEmpty())
                <div class="auth-alert auth-alert-error">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"/>
                        <line x1="12" y1="8" x2="12" y2="12"/>
                        <line x1="12" y1="16" x2="12.01" y2="16"/>
                    </svg>
                    <span>${messages.get(error.equals("mismatch") ? "password.mismatch" : error.equals("current") ? "password.currentInvalid" : "common.error")}</span>
                </div>
                @endif

                <form action="/profile/password" method="post">
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">

                    <div class="auth-form-group">
                        <label class="auth-label">${messages.get("password.current")}</label>
                        <div class="auth-input-wrapper">
                            <span class="auth-input-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                                </svg>
                            </span>
                            <input class="auth-input" name="currentPassword" type="password" autocomplete="current-password" placeholder="••••••••" required autofocus>
                        </div>
                    </div>

                    <div class="auth-form-group">
                        <label class="auth-label">${messages.get("password.new")}</label>
                        <div class="auth-input-wrapper">
                            <span class="auth-input-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M21 2l-2 2m-2-2l2 2M3 12h18M3 6h18M3 18h18"/>
                                </svg>
                            </span>
                            <input class="auth-input" name="newPassword" type="password" minlength="8" maxlength="72" autocomplete="new-password" placeholder="••••••••" required>
                        </div>
                    </div>

                    <div class="auth-form-group">
                        <label class="auth-label">${messages.get("password.confirm")}</label>
                        <div class="auth-input-wrapper">
                            <span class="auth-input-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                                    <polyline points="22 4 12 14.01 9 11.01"/>
                                </svg>
                            </span>
                            <input class="auth-input" name="confirmPassword" type="password" autocomplete="new-password" placeholder="••••••••" required>
                        </div>
                    </div>

                    <button class="auth-submit-btn" type="submit">
                        <span>${messages.get("password.save")}</span>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 18px; height: 18px;">
                            <line x1="5" y1="12" x2="19" y2="12"/>
                            <polyline points="12 5 19 12 12 19"/>
                        </svg>
                    </button>
                </form>

                <div class="auth-footer">
                    <a href="/">← ${messages.get("common.back")}</a>
                </div>
            </div>
        </div>
    </body>
</html>
