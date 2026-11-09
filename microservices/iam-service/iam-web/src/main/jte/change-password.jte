@import java.util.Map
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param String error
@param boolean saved
<!doctype html>
<html lang="${localeTag}"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("password.title")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body><header><a href="/">${messages.get("common.home")}</a></header><main class="narrow"><section class="card"><h1>${messages.get("password.title")}</h1>
@if (saved)<p class="notice">${messages.get("common.success")}</p>@endif
@if (!error.isEmpty())<p class="notice error">${messages.get(error.equals("mismatch") ? "password.mismatch" : error.equals("current") ? "password.currentInvalid" : "common.error")}</p>@endif
<form action="/profile/password" method="post"><input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
<label>${messages.get("password.current")}</label><input name="currentPassword" type="password" autocomplete="current-password" required>
<label>${messages.get("password.new")}</label><input name="newPassword" type="password" minlength="8" maxlength="72" autocomplete="new-password" required>
<label>${messages.get("password.confirm")}</label><input name="confirmPassword" type="password" autocomplete="new-password" required>
<p><button>${messages.get("password.save")}</button></p></form></section></main></body></html>
