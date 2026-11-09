package kh.edu.istad.iam.web;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class PageModelAdvice {
    private static final List<String> KEYS = List.of(
            "login.title", "login.username", "login.password", "login.submit", "login.continueWith",
            "login.register", "login.error", "provider.google", "provider.github", "provider.facebook",
            "provider.telegram", "register.title", "register.username", "register.email",
            "register.password", "register.confirmPassword", "register.givenName", "register.familyName",
            "register.locale", "register.submit", "register.backToLogin", "register.passwordMismatch",
            "consent.title", "consent.description", "consent.allow", "consent.cancel", "consent.scope",
            "profile.title", "profile.givenName", "profile.familyName", "profile.middleName", "profile.nickname",
            "profile.gender", "profile.birthdate", "profile.profileUrl", "profile.pictureUrl",
            "profile.websiteUrl", "profile.zoneinfo", "profile.locale", "profile.phoneNumber",
            "profile.streetAddress", "profile.locality", "profile.region", "profile.postalCode",
            "profile.country", "profile.save", "profile.linkedAccounts", "profile.connectedApps",
            "profile.changePassword", "profile.edit", "profile.email", "profile.username",
            "password.title", "password.current", "password.new", "password.confirm", "password.save",
            "password.mismatch", "password.currentInvalid", "identity.provider", "identity.email",
            "identity.displayName", "identity.linkedAt", "identity.unlink", "identity.none",
            "application.name", "application.scopes", "application.revoke", "application.none",
            "index.welcome", "index.profile", "common.logout", "common.home", "common.brand", "common.back",
            "common.error", "common.success", "common.yes", "common.no", "common.english", "common.khmer",
            "common.registered", "common.duplicate");

    private final MessageSource messages;

    public PageModelAdvice(MessageSource messages) { this.messages = messages; }

    @ModelAttribute
    public void commonPageModel(Model model, Locale locale, HttpServletRequest request) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String key : KEYS) values.put(key, messages.getMessage(key, null, key, locale));
        model.addAttribute("messages", values);
        model.addAttribute("localeTag", locale.getLanguage());
        Object csrfAttribute = request.getAttribute(CsrfToken.class.getName());
        if (csrfAttribute instanceof CsrfToken csrf) {
            model.addAttribute("csrfParameterName", csrf.getParameterName());
            model.addAttribute("csrfToken", csrf.getToken());
        } else {
            model.addAttribute("csrfParameterName", "_csrf");
            model.addAttribute("csrfToken", "");
        }
    }
}
