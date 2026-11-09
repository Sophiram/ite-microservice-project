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
<html lang="${localeTag}"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("register.title")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body><main class="narrow"><section class="card"><h1>${messages.get("register.title")}</h1>
@if (!errors.isEmpty())<p class="notice error">${messages.get(errors.contains("duplicate") ? "common.duplicate" : "common.error")}</p>@endif
@if (passwordMismatch)<p class="notice error">${messages.get("register.passwordMismatch")}</p>@endif
<form action="/register" method="post">
<input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
<label for="username">${messages.get("register.username")}</label><input id="username" name="username" value="${form.username() == null ? "" : form.username()}" required maxlength="50">
<label for="email">${messages.get("register.email")}</label><input id="email" name="email" type="email" value="${form.email() == null ? "" : form.email()}" required>
<label for="givenName">${messages.get("register.givenName")}</label><input id="givenName" name="givenName" value="${form.givenName() == null ? "" : form.givenName()}" required>
<label for="familyName">${messages.get("register.familyName")}</label><input id="familyName" name="familyName" value="${form.familyName() == null ? "" : form.familyName()}" required>
<label for="password">${messages.get("register.password")}</label><input id="password" name="password" type="password" minlength="8" maxlength="72" required>
<label for="confirmPassword">${messages.get("register.confirmPassword")}</label><input id="confirmPassword" name="confirmPassword" type="password" required>
<label for="locale">${messages.get("register.locale")}</label><select id="locale" name="locale"><option value="en">${messages.get("common.english")}</option><option value="km">${messages.get("common.khmer")}</option></select>
<p><button type="submit">${messages.get("register.submit")}</button></p>
</form><a href="/login">${messages.get("register.backToLogin")}</a>
</section></main></body></html>
