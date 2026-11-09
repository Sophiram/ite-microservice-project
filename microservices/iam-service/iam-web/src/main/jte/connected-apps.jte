@import java.util.Map
@import java.util.List
@import kh.edu.istad.iam.application.result.ConnectedApplicationResult
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param List<ConnectedApplicationResult> applications
<!doctype html>
<html lang="${localeTag}"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("profile.connectedApps")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body><header><a href="/">${messages.get("common.home")}</a></header><main><section class="card"><h1>${messages.get("profile.connectedApps")}</h1>
@if (applications.isEmpty())<p class="muted">${messages.get("application.none")}</p>@endif
@for (ConnectedApplicationResult application : applications)
<article class="row"><h2>${application.clientName()}</h2><p>${messages.get("application.scopes")}: ${String.join(", ", application.scopes())}</p>
<form action="/profile/apps/${application.clientId()}/revoke" method="post"><input type="hidden" name="${csrfParameterName}" value="${csrfToken}"><button class="secondary">${messages.get("application.revoke")}</button></form></article>
@endfor
</section></main></body></html>
