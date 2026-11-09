@import java.util.Map
@import java.util.List
@import kh.edu.istad.iam.application.result.IdentityResult
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param List<IdentityResult> identities
<!doctype html>
<html lang="${localeTag}"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("profile.linkedAccounts")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body><header><a href="/">${messages.get("common.home")}</a></header><main><section class="card"><h1>${messages.get("profile.linkedAccounts")}</h1>
@if (identities.isEmpty())<p class="muted">${messages.get("identity.none")}</p>@endif
@for (IdentityResult identity : identities)
<article class="row"><h2>${identity.provider()}</h2><p>${messages.get("identity.email")}: ${identity.email() == null ? "" : identity.email()}</p><p>${messages.get("identity.displayName")}: ${identity.displayName() == null ? "" : identity.displayName()}</p><p>${messages.get("identity.linkedAt")}: ${identity.linkedAt().toString()}</p>
<form action="/profile/accounts/${identity.uuid().toString()}/unlink" method="post"><input type="hidden" name="${csrfParameterName}" value="${csrfToken}"><button class="secondary">${messages.get("identity.unlink")}</button></form></article>
@endfor
</section></main></body></html>
