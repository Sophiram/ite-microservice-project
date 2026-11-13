@import java.util.Map
@import java.util.List
@import kh.edu.istad.iam.web.form.RegisterForm
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param RegisterForm form
@param List<String> errors
@param boolean passwordMismatch
<!doctype html>
<html lang="${localeTag}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${messages.get("register.title")} · ${messages.get("common.brand")}</title>
        <link rel="stylesheet" href="/css/iam.css">
    </head>
    <body>
        <div class="auth-wrapper">
            <div class="auth-card" style="max-width: 520px;">
                <div class="auth-header">
                    <div class="brand-badge">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                            <circle cx="8.5" cy="7" r="4"/>
                            <line x1="20" y1="8" x2="20" y2="14"/>
                            <line x1="23" y1="11" x2="17" y2="11"/>
                        </svg>
                    </div>
                    <h1 class="auth-title">${messages.get("register.title")}</h1>
                    <p class="auth-subtitle">${messages.get("register.subtitle") != null ? messages.get("register.subtitle") : "Create your ISTAD IAM account in seconds."}</p>
                </div>

                @if (!errors.isEmpty())
                <div class="auth-alert auth-alert-error">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"/>
                        <line x1="12" y1="8" x2="12" y2="12"/>
                        <line x1="12" y1="16" x2="12.01" y2="16"/>
                    </svg>
                    <span>${messages.get(errors.contains("duplicate") ? "common.duplicate" : "common.error")}</span>
                </div>
                @endif

                @if (passwordMismatch)
                <div class="auth-alert auth-alert-error">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"/>
                        <line x1="12" y1="8" x2="12" y2="12"/>
                        <line x1="12" y1="16" x2="12.01" y2="16"/>
                    </svg>
                    <span>${messages.get("register.passwordMismatch")}</span>
                </div>
                @endif

                <form action="/register" method="post">
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">

                    <div class="auth-form-group">
                        <label class="auth-label" for="username">${messages.get("register.username")}</label>
                        <div class="auth-input-wrapper">
                            <span class="auth-input-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                    <circle cx="12" cy="7" r="4"/>
                                </svg>
                            </span>
                            <input class="auth-input" id="username" name="username" value="${form.username() == null ? "" : form.username()}" placeholder="johndoe" required maxlength="50" autofocus>
                        </div>
                    </div>

                    <div class="auth-form-group">
                        <label class="auth-label" for="email">${messages.get("register.email")}</label>
                        <div class="auth-input-wrapper">
                            <span class="auth-input-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                                    <polyline points="22,6 12,13 2,6"/>
                                </svg>
                            </span>
                            <input class="auth-input" id="email" name="email" type="email" value="${form.email() == null ? "" : form.email()}" placeholder="john.doe@example.com" required>
                        </div>
                    </div>

                    <div class="form-grid-2">
                        <div class="auth-form-group">
                            <label class="auth-label" for="givenName">${messages.get("register.givenName")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" id="givenName" name="givenName" value="${form.givenName() == null ? "" : form.givenName()}" placeholder="John" required>
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label" for="familyName">${messages.get("register.familyName")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" id="familyName" name="familyName" value="${form.familyName() == null ? "" : form.familyName()}" placeholder="Doe" required>
                        </div>
                    </div>

                    <div class="form-grid-2">
                        <div class="auth-form-group">
                            <label class="auth-label" for="password">${messages.get("register.password")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" id="password" name="password" type="password" minlength="8" maxlength="72" placeholder="••••••••" required>
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label" for="confirmPassword">${messages.get("register.confirmPassword")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" id="confirmPassword" name="confirmPassword" type="password" placeholder="••••••••" required>
                        </div>
                    </div>

                    <div class="auth-form-group">
                        <label class="auth-label" for="locale">${messages.get("register.locale")}</label>
                        <select class="auth-input" style="padding-left: 1rem;" id="locale" name="locale">
                            <option value="en" selected="${"en".equals(localeTag)}">${messages.get("common.english")}</option>
                            <option value="km" selected="${"km".equals(localeTag)}">${messages.get("common.khmer")}</option>
                        </select>
                    </div>

                    <button type="submit" class="auth-submit-btn">
                        <span>${messages.get("register.submit")}</span>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 18px; height: 18px;">
                            <line x1="5" y1="12" x2="19" y2="12"/>
                            <polyline points="12 5 19 12 12 19"/>
                        </svg>
                    </button>
                </form>

                <div class="auth-footer">
                    <span>${messages.get("register.backToLogin")}?</span>
                    <a href="/login">${messages.get("login.title")}</a>
                </div>

                <div class="auth-lang-pills">
                    <a href="?lang=en" class="auth-lang-pill ${"en".equals(localeTag) ? "active" : ""}">${messages.get("common.english")}</a>
                    <span style="color: #cbd5e1;">·</span>
                    <a href="?lang=km" class="auth-lang-pill ${"km".equals(localeTag) ? "active" : ""}">${messages.get("common.khmer")}</a>
                </div>
            </div>
        </div>
    </body>
</html>
