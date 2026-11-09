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
<html lang="${localeTag}"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("consent.title")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body><main class="narrow"><section class="card"><h1>${messages.get("consent.title")}</h1><p>${messages.get("consent.description")}</p><p>${clientId}</p>
<form id="consentForm" action="/oauth2/authorize" method="post">
<input type="hidden" name="${csrfParameterName}" value="${csrfToken}"><input type="hidden" name="client_id" value="${clientId}"><input type="hidden" name="state" value="${state}">
<h2>${messages.get("consent.scope")}</h2>
@for (String scope : scopes)
<label><input type="checkbox" name="scope" value="${scope}" checked> ${scope}</label>
@endfor
<div class="actions"><button type="submit">${messages.get("consent.allow")}</button></div>
</form>
<form action="/oauth2/authorize" method="post"><input type="hidden" name="${csrfParameterName}" value="${csrfToken}"><input type="hidden" name="client_id" value="${clientId}"><input type="hidden" name="state" value="${state}"><button class="secondary" type="submit">${messages.get("consent.cancel")}</button></form>
</section></main></body></html>
