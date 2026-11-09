@import java.util.Map
@import java.util.List
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param List<String> providerIds
@param boolean error
@param boolean registered
<!doctype html>
<html lang="${localeTag}">
    <head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("login.title")}</title><link rel="stylesheet" href="/css/iam.css"></head>
    <body>
        <main class="narrow"><section class="card">
            <h1>${messages.get("login.title")}</h1>
            @if (registered)
            <p class="notice">${messages.get("common.registered")}</p>
            @endif
            @if (error)
            <p class="notice error">${messages.get("login.error")}</p>
            @endif
            <form action="/login" method="post">
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
            <label for="username">${messages.get("login.username")}</label><input id="username" name="username" autocomplete="username" required>
            <label for="password">${messages.get("login.password")}</label><input id="password" name="password" type="password" autocomplete="current-password" required>
            <p><button type="submit">${messages.get("login.submit")}</button></p>
            </form>
            <div class="provider-list">
            @for (String provider : providerIds)
            <a href="/oauth2/authorization/${provider}">${messages.get("login.continueWith").replace("{0}", messages.get("provider." + provider))}</a>
            @endfor
            </div>
            <p><a href="/register">${messages.get("login.register")}</a></p>
            <p><a href="?lang=en">${messages.get("common.english")}</a> · <a href="?lang=km">${messages.get("common.khmer")}</a></p>
            </section>
          </main>
        <script>
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
