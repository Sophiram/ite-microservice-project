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
<html lang="${localeTag}"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("index.profile")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body><header><a href="/">${messages.get("common.brand")}</a><nav><a href="/profile/edit">${messages.get("profile.edit")}</a><a href="/profile/accounts">${messages.get("profile.linkedAccounts")}</a><a href="/profile/apps">${messages.get("profile.connectedApps")}</a><form action="/logout" method="post"><input type="hidden" name="${csrfParameterName}" value="${csrfToken}"><button class="secondary">${messages.get("common.logout")}</button></form></nav></header>
<main>
<section class="card profile-header">
    <div class="profile-info-row">
        @if (user.profile() != null && user.profile().pictureUrl() != null && !user.profile().pictureUrl().isBlank())
        <img class="avatar" src="${user.profile().pictureUrl()}" alt="${user.fullName()}">
        @else
        <div class="avatar avatar-placeholder">${user.fullName().substring(0, 1).toUpperCase()}</div>
        @endif
        <div class="profile-meta">
            <h1>${user.fullName()}</h1>
            <p class="user-email">${user.email()}</p>
            <p class="user-username">@@${user.username()}</p>
        </div>
    </div>
</section>
<div class="grid"><section class="card"><h2>${messages.get("profile.linkedAccounts")}</h2><p>${identities.size()}</p><a href="/profile/accounts">${messages.get("common.home")}</a></section><section class="card"><h2>${messages.get("profile.connectedApps")}</h2><p>${applications.size()}</p><a href="/profile/apps">${messages.get("profile.connectedApps")}</a></section><section class="card"><h2>${messages.get("profile.changePassword")}</h2><a href="/profile/password">${messages.get("profile.changePassword")}</a></section></div></main>
</body></html>
