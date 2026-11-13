@import java.util.Map
@import java.util.List
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param List<String> providerIds
@param boolean error
@param boolean registered
@param boolean logout = false
<!doctype html>
<html lang="${localeTag}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${messages.get("login.title")} · ${messages.get("common.brand")}</title>
        <link rel="stylesheet" href="/css/iam.css">
    </head>
    <body>
        <div class="auth-wrapper">
            <div class="auth-card">
                <div class="auth-header">
                    <div class="brand-badge">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                        </svg>
                    </div>
                    <h1 class="auth-title">${messages.get("login.title")}</h1>
                    <p class="auth-subtitle">${messages.getOrDefault("login.subtitle", "Welcome back! Please enter your details to continue.")}</p>
                </div>

                @if (registered)
                <div class="auth-alert auth-alert-success">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                        <polyline points="22 4 12 14.01 9 11.01"/>
                    </svg>
                    <span>${messages.get("common.registered")}</span>
                </div>
                @endif

                @if (logout)
                <div class="auth-alert auth-alert-success">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                        <polyline points="16 17 21 12 16 7"/>
                        <line x1="21" y1="12" x2="9" y2="12"/>
                    </svg>
                    <span>${messages.get("common.loggedOut")}</span>
                </div>
                @endif

                @if (error)
                <div class="auth-alert auth-alert-error">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"/>
                        <line x1="12" y1="8" x2="12" y2="12"/>
                        <line x1="12" y1="16" x2="12.01" y2="16"/>
                    </svg>
                    <span>${messages.get("login.error")}</span>
                </div>
                @endif

                <form action="/login" method="post">
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">

                    <div class="auth-form-group">
                        <label class="auth-label" for="username">${messages.get("login.username")}</label>
                        <div class="auth-input-wrapper">
                            <span class="auth-input-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                    <circle cx="12" cy="7" r="4"/>
                                </svg>
                            </span>
                            <input class="auth-input" id="username" name="username" autocomplete="username" placeholder="${messages.get("login.username")}" required autofocus>
                        </div>
                    </div>

                    <div class="auth-form-group">
                        <label class="auth-label" for="password">${messages.get("login.password")}</label>
                        <div class="auth-input-wrapper">
                            <span class="auth-input-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                                </svg>
                            </span>
                            <input class="auth-input" id="password" name="password" type="password" autocomplete="current-password" placeholder="••••••••" required>
                            <button type="button" class="auth-toggle-password" id="togglePasswordBtn" aria-label="Toggle password visibility">
                                <svg id="eyeIcon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                    <circle cx="12" cy="12" r="3"/>
                                </svg>
                            </button>
                        </div>
                    </div>

                    <button type="submit" class="auth-submit-btn">
                        <span>${messages.get("login.submit")}</span>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 18px; height: 18px;">
                            <line x1="5" y1="12" x2="19" y2="12"/>
                            <polyline points="12 5 19 12 12 19"/>
                        </svg>
                    </button>
                </form>

                @if (!providerIds.isEmpty())
                <div class="auth-divider">
                    <span>${messages.getOrDefault("login.orContinueWith", "or continue with")}</span>
                </div>

                <div class="auth-social-grid">
                    @for (String provider : providerIds)
                    <a class="auth-social-btn" href="/oauth2/authorization/${provider}">
                        @if ("google".equalsIgnoreCase(provider))
                        <svg viewBox="0 0 24 24">
                            <path fill="#4285F4" d="M23.745 12.27c0-.7-.06-1.4-.19-2.07H12v4.51h6.6c-.29 1.52-1.14 2.8-2.4 3.66v3.04h3.88c2.27-2.09 3.66-5.17 3.66-9.14z"/>
                            <path fill="#34A853" d="M12 24c3.24 0 5.95-1.08 7.93-2.91l-3.88-3.04c-1.08.72-2.45 1.16-4.05 1.16-3.12 0-5.77-2.1-6.72-4.93H1.27v3.13C3.25 21.32 7.31 24 12 24z"/>
                            <path fill="#FBBC05" d="M5.28 14.28c-.25-.72-.38-1.49-.38-2.28s.13-1.56.38-2.28V6.59H1.27C.46 8.2 0 10.04 0 12s.46 3.8 1.27 5.41l4.01-3.13z"/>
                            <path fill="#EA4335" d="M12 4.75c1.77 0 3.35.61 4.6 1.8l3.42-3.42C17.95 1.19 15.24 0 12 0 7.31 0 3.25 2.68 1.27 6.59l4.01 3.13c.95-2.83 3.6-4.97 6.72-4.97z"/>
                        </svg>
                        @elseif ("github".equalsIgnoreCase(provider))
                        <svg viewBox="0 0 24 24" fill="currentColor">
                            <path fill-rule="evenodd" clip-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/>
                        </svg>
                        @elseif ("facebook".equalsIgnoreCase(provider))
                        <svg viewBox="0 0 24 24" fill="#1877F2">
                            <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                        </svg>
                        @elseif ("telegram".equalsIgnoreCase(provider))
                        <svg viewBox="0 0 24 24" fill="#229ED9">
                            <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.894 8.221l-1.97 9.28c-.145.658-.537.818-1.084.508l-3-2.21-1.446 1.394c-.16.16-.295.295-.605.295l.213-3.053 5.56-5.023c.242-.213-.054-.333-.373-.121l-6.871 4.326-2.962-.924c-.643-.204-.657-.643.136-.953l11.57-4.461c.537-.194 1.006.128.832.932z"/>
                        </svg>
                        @endif
                        <span>${messages.get("provider." + provider)}</span>
                    </a>
                    @endfor
                </div>
                @endif

                <div class="auth-footer">
                    <span>${messages.getOrDefault("login.noAccount", "Don't have an account?")}</span>
                    <a href="/register">${messages.get("login.register")}</a>
                </div>

                <div class="auth-lang-pills">
                    <a href="?lang=en" class="auth-lang-pill ${"en".equals(localeTag) ? "active" : ""}">${messages.get("common.english")}</a>
                    <span style="color: #cbd5e1;">·</span>
                    <a href="?lang=km" class="auth-lang-pill ${"km".equals(localeTag) ? "active" : ""}">${messages.get("common.khmer")}</a>
                </div>

                <div class="auth-security-badge">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                        <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                    </svg>
                    <span>Secured by ISTAD IAM</span>
                </div>
            </div>
        </div>

        <script>
            // Password toggle visibility
            (function() {
                var toggleBtn = document.getElementById('togglePasswordBtn');
                var passwordInput = document.getElementById('password');
                var eyeIcon = document.getElementById('eyeIcon');
                if (toggleBtn && passwordInput && eyeIcon) {
                    toggleBtn.addEventListener('click', function() {
                        var isPassword = passwordInput.getAttribute('type') === 'password';
                        passwordInput.setAttribute('type', isPassword ? 'text' : 'password');
                        if (isPassword) {
                            eyeIcon.innerHTML = '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/>';
                        } else {
                            eyeIcon.innerHTML = '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>';
                        }
                    });
                }
            })();

            // Telegram OAuth callback handler
            (function() {
                if (window.location.hash && window.location.hash.indexOf('tgAuthResult=') !== -1) {
                    var hash = window.location.hash.substring(1);
                    var params = new URLSearchParams(hash);
                    var tgAuthResult = params.get('tgAuthResult');
                    if (tgAuthResult) {
                        window.location.replace('/login/telegram?tgAuthResult=' + encodeURIComponent(tgAuthResult));
                    }
                }
            })();
        </script>
    </body>
</html>
