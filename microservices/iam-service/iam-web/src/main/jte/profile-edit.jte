@import java.util.Map
@import kh.edu.istad.iam.application.result.UserResult
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param UserResult user
@param boolean saved
<!doctype html>
<html lang="${localeTag}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${messages.get("profile.title")} · ${messages.get("common.brand")}</title>
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
                    <a class="app-nav-link active" href="/profile/edit">${messages.get("profile.edit")}</a>
                    <a class="app-nav-link" href="/profile/accounts">${messages.get("profile.linkedAccounts")}</a>
                    <a class="app-nav-link" href="/profile/apps">${messages.get("profile.connectedApps")}</a>
                </nav>
            </div>
        </header>

        <main class="app-container">
            <div class="page-header">
                <div>
                    <h1 class="page-title">${messages.get("profile.title")}</h1>
                    <p class="page-subtitle">Update your personal information and address details</p>
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
                @if (saved)
                <div class="auth-alert auth-alert-success">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                        <polyline points="22 4 12 14.01 9 11.01"/>
                    </svg>
                    <span>${messages.get("common.success")}</span>
                </div>
                @endif

                <form action="/profile/edit" method="post">
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">

                    <h2 class="form-section-title" style="margin-top: 0;">Personal Details</h2>
                    <div class="form-grid-2">
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.givenName")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="givenName" value="${user.profile().givenName() == null ? "" : user.profile().givenName()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.familyName")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="familyName" value="${user.profile().familyName() == null ? "" : user.profile().familyName()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.middleName")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="middleName" value="${user.profile().middleName() == null ? "" : user.profile().middleName()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.nickname")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="nickname" value="${user.profile().nickname() == null ? "" : user.profile().nickname()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.gender")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="gender" value="${user.profile().gender() == null ? "" : user.profile().gender()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.birthdate")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="birthdate" type="date" value="${user.profile().birthdate() == null ? "" : user.profile().birthdate().toString()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.phoneNumber")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="phoneNumber" value="${user.profile().phoneNumber() == null ? "" : user.profile().phoneNumber()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.locale")}</label>
                            <select class="auth-input" style="padding-left: 1rem;" name="locale">
                                <option value="en" selected="${"en".equals(localeTag)}">${messages.get("common.english")}</option>
                                <option value="km" selected="${"km".equals(localeTag)}">${messages.get("common.khmer")}</option>
                            </select>
                        </div>
                    </div>

                    <h2 class="form-section-title">Online Presence & Web</h2>
                    <div class="form-grid-2">
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.profileUrl")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="profileUrl" value="${user.profile().profileUrl() == null ? "" : user.profile().profileUrl()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.pictureUrl")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="pictureUrl" value="${user.profile().pictureUrl() == null ? "" : user.profile().pictureUrl()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.websiteUrl")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="websiteUrl" value="${user.profile().websiteUrl() == null ? "" : user.profile().websiteUrl()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.zoneinfo")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="zoneinfo" value="${user.profile().zoneinfo() == null ? "" : user.profile().zoneinfo()}">
                        </div>
                    </div>

                    <h2 class="form-section-title">Address Details</h2>
                    <div class="auth-form-group">
                        <label class="auth-label">${messages.get("profile.streetAddress")}</label>
                        <input class="auth-input" style="padding-left: 1rem;" name="streetAddress" value="${user.address().streetAddress() == null ? "" : user.address().streetAddress()}">
                    </div>
                    <div class="form-grid-2">
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.locality")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="locality" value="${user.address().locality() == null ? "" : user.address().locality()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.region")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="region" value="${user.address().region() == null ? "" : user.address().region()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.postalCode")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="postalCode" value="${user.address().postalCode() == null ? "" : user.address().postalCode()}">
                        </div>
                        <div class="auth-form-group">
                            <label class="auth-label">${messages.get("profile.country")}</label>
                            <input class="auth-input" style="padding-left: 1rem;" name="country" maxlength="2" value="${user.address().country() == null ? "" : user.address().country()}">
                        </div>
                    </div>

                    <div style="margin-top: 2rem;">
                        <button class="auth-submit-btn" style="width: auto; padding: 0 2rem; margin-top: 0;" type="submit">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 17px; height: 17px;">
                                <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                                <polyline points="17 21 17 13 7 13 7 21"/>
                                <polyline points="7 3 7 8 15 8"/>
                            </svg>
                            <span>${messages.get("profile.save")}</span>
                        </button>
                    </div>
                </form>
            </div>
        </main>
    </body>
</html>
