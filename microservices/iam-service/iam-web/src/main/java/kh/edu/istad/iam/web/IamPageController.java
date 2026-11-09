package kh.edu.istad.iam.web;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.command.ChangePasswordCommand;
import kh.edu.istad.iam.application.command.RegisterUserCommand;
import kh.edu.istad.iam.application.command.UpdateUserProfileCommand;
import kh.edu.istad.iam.application.result.UserResult;
import kh.edu.istad.iam.application.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.iam.application.usecase.user.GetUserUseCase;
import kh.edu.istad.iam.application.usecase.ListConnectedApplicationsUseCase;
import kh.edu.istad.iam.application.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.iam.application.usecase.RevokeConnectedApplicationUseCase;
import kh.edu.istad.iam.application.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.iam.application.usecase.user.UpdateUserProfileUseCase;
import kh.edu.istad.iam.domain.entity.Address;
import kh.edu.istad.iam.domain.entity.Profile;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.web.form.PasswordForm;
import kh.edu.istad.iam.web.form.ProfileForm;
import kh.edu.istad.iam.web.form.RegisterForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kh.edu.istad.iam.application.command.FederatedIdentityCommand;
import kh.edu.istad.iam.application.result.AuthUserResult;
import kh.edu.istad.iam.application.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.iam.domain.entity.Provider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
public class IamPageController {
    private final RegisterUserUseCase registerUser;
    private final GetUserUseCase getUser;
    private final LoadUserForAuthenticationUseCase loadUser;
    private final UpdateUserProfileUseCase updateProfile;
    private final ChangePasswordUseCase changePassword;
    private final ListUserIdentitiesUseCase listIdentities;
    private final UnlinkIdentityUseCase unlinkIdentity;
    private final ListConnectedApplicationsUseCase listApps;
    private final RevokeConnectedApplicationUseCase revokeApp;
    private final FindOrProvisionFederatedUserUseCase federatedUsers;

    @Value("${TELEGRAM_BOT_TOKEN:}")
    private String telegramBotToken;

    private final SecurityContextRepository securityContextRepository =
            new DelegatingSecurityContextRepository(
                    new RequestAttributeSecurityContextRepository(),
                    new HttpSessionSecurityContextRepository());
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("providerIds", List.of("google", "github", "facebook", "telegram"));
        model.addAttribute("error", false);
        addDefault(model, "registered", false);
        return "login";
    }

    @GetMapping(value = "/login", params = "error")
    public String loginError(Model model) {
        model.addAttribute("providerIds", List.of("google", "github", "facebook", "telegram"));
        model.addAttribute("error", true);
        addDefault(model, "registered", false);
        return "login";
    }

    @RequestMapping(value = "/login/telegram", method = {RequestMethod.GET, RequestMethod.POST})
    public String loginTelegram(
            @RequestParam(value = "tgAuthResult", required = false) String tgAuthResult,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (tgAuthResult == null || tgAuthResult.isBlank()) {
            return "redirect:/login?error";
        }
        try {
            byte[] decoded = decodeBase64(tgAuthResult);
            ObjectMapper om = new ObjectMapper();
            java.util.Map<String, Object> data = om.readValue(decoded, new TypeReference<java.util.Map<String, Object>>() {});

            if (!verifyTelegramAuth(data)) {
                return "redirect:/login?error";
            }

            Object authDateObj = data.get("auth_date");
            if (authDateObj != null) {
                long authDate = Long.parseLong(authDateObj.toString());
                long now = Instant.now().getEpochSecond();
                if (Math.abs(now - authDate) > 86400) {
                    return "redirect:/login?error";
                }
            }

            String subject = String.valueOf(data.get("id"));
            String username = data.get("username") != null ? data.get("username").toString() : null;
            String firstName = data.get("first_name") != null ? data.get("first_name").toString() : "";
            String lastName = data.get("last_name") != null ? data.get("last_name").toString() : "";
            String displayName = (firstName + " " + lastName).trim();
            if (displayName.isBlank()) {
                displayName = username != null ? username : subject;
            }
            String photoUrl = data.get("photo_url") != null ? data.get("photo_url").toString() : null;

            FederatedIdentityCommand command = new FederatedIdentityCommand(
                    Provider.TELEGRAM, subject, username, null, false, displayName, photoUrl, data);
            AuthUserResult user = federatedUsers.execute(command);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>(
                    user.authorities().stream().map(SimpleGrantedAuthority::new).toList());
            authorities.add(new SimpleGrantedAuthority("OAUTH2_USER"));
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.uuid().toString(), null, authorities);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                return "redirect:" + savedRequest.getRedirectUrl();
            }
            return "redirect:/";
        } catch (Exception e) {
            return "redirect:/login?error";
        }
    }

    private static byte[] decodeBase64(String input) {
        String s = input.trim();
        while (s.length() % 4 != 0) {
            s += "=";
        }
        try {
            return Base64.getUrlDecoder().decode(s);
        } catch (IllegalArgumentException e) {
            return Base64.getDecoder().decode(s);
        }
    }

    private boolean verifyTelegramAuth(java.util.Map<String, Object> data) {
        String receivedHash = (String) data.get("hash");
        if (receivedHash == null || telegramBotToken == null || telegramBotToken.isBlank()) {
            return false;
        }
        try {
            List<String> keys = new ArrayList<>(data.keySet());
            keys.remove("hash");
            Collections.sort(keys);

            StringBuilder dataCheck = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                if (i > 0) {
                    dataCheck.append("\n");
                }
                String k = keys.get(i);
                dataCheck.append(k).append("=").append(data.get(k));
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] secretKey = digest.digest(telegramBotToken.getBytes(StandardCharsets.UTF_8));

            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] hmacBytes = hmac.doFinal(dataCheck.toString().getBytes(StandardCharsets.UTF_8));

            StringBuilder computedHash = new StringBuilder();
            for (byte b : hmacBytes) {
                computedHash.append(String.format("%02x", b));
            }

            return MessageDigest.isEqual(
                    computedHash.toString().getBytes(StandardCharsets.UTF_8),
                    receivedHash.toLowerCase().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterForm(null, null, null, null, null, null, "en"));
        model.addAttribute("errors", List.of());
        model.addAttribute("passwordMismatch", false);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form, BindingResult binding,
                           Model model, RedirectAttributes redirect) {
        boolean passwordMismatch = form.password() != null && form.confirmPassword() != null
                && !form.password().equals(form.confirmPassword());
        if (binding.hasErrors() || passwordMismatch) {
            model.addAttribute("errors", binding.getFieldErrors().stream()
                    .map(error -> error.getField()).distinct().toList());
            model.addAttribute("passwordMismatch", passwordMismatch);
            return "register";
        }
        try {
            registerUser.execute(new RegisterUserCommand(form.username(), form.email(), form.password(),
                    Profile.of(form.givenName(), form.familyName(), supportedLocale(form.locale()))));
            redirect.addFlashAttribute("registered", true);
            return "redirect:/login";
        } catch (DomainException exception) {
            model.addAttribute("errors", List.of("duplicate"));
            model.addAttribute("passwordMismatch", false);
            return "register";
        }
    }

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        UserResult user = getUser.execute(userUuid(authentication));
        model.addAttribute("user", user);
        model.addAttribute("identities", listIdentities.execute(user.uuid()));
        model.addAttribute("applications", listApps.execute());
        return "index";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Authentication authentication, Model model) {
        model.addAttribute("user", getUser.execute(userUuid(authentication)));
        addDefault(model, "saved", false);
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(Authentication authentication, @ModelAttribute ProfileForm form,
                                RedirectAttributes redirect) {
        UUID uuid = userUuid(authentication);
        Profile profile = new Profile(form.givenName(), form.familyName(), form.middleName(), form.nickname(),
                form.gender(), parseDate(form.birthdate()), form.profileUrl(), form.pictureUrl(), form.websiteUrl(),
                form.zoneinfo(), supportedLocale(form.locale()), form.phoneNumber());
        Address address = new Address(form.streetAddress(), form.locality(), form.region(),
                form.postalCode(), form.country());
        updateProfile.execute(new UpdateUserProfileCommand(uuid, profile, address));
        redirect.addFlashAttribute("saved", true);
        return "redirect:/profile/edit";
    }

    @GetMapping("/profile/password")
    public String changePasswordPage(Model model) {
        addDefault(model, "error", "");
        addDefault(model, "saved", false);
        return "change-password";
    }

    @PostMapping("/profile/password")
    public String changePassword(Authentication authentication, @Valid @ModelAttribute PasswordForm form,
                                 BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            model.addAttribute("error", "invalid");
            model.addAttribute("saved", false);
            return "change-password";
        }
        if (!form.newPassword().equals(form.confirmPassword())) {
            model.addAttribute("error", "mismatch");
            model.addAttribute("saved", false);
            return "change-password";
        }
        try {
            changePassword.execute(new ChangePasswordCommand(userUuid(authentication),
                    form.currentPassword(), form.newPassword()));
            redirect.addFlashAttribute("saved", true);
            return "redirect:/profile/password";
        } catch (DomainException exception) {
            model.addAttribute("error", "current");
            model.addAttribute("saved", false);
            return "change-password";
        }
    }

    @GetMapping("/profile/accounts")
    public String linkedAccounts(Authentication authentication, Model model) {
        model.addAttribute("identities", listIdentities.execute(userUuid(authentication)));
        return "linked-accounts";
    }

    @PostMapping("/profile/accounts/{identityUuid}/unlink")
    public String unlinkAccount(Authentication authentication, @PathVariable UUID identityUuid) {
        unlinkIdentity.execute(userUuid(authentication), identityUuid);
        return "redirect:/profile/accounts";
    }

    @GetMapping("/profile/apps")
    public String connectedApps(Model model) {
        model.addAttribute("applications", listApps.execute());
        return "connected-apps";
    }

    @PostMapping("/profile/apps/{clientId}/revoke")
    public String revokeConnectedApp(@PathVariable String clientId) {
        revokeApp.execute(clientId);
        return "redirect:/profile/apps";
    }

    @GetMapping("/oauth2/consent")
    public String consentPage(@RequestParam("client_id") String clientId, @RequestParam String state,
                              @RequestParam(required = false) String scope, Model model) {
        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);
        model.addAttribute("scopes", scope == null || scope.isBlank()
                ? List.of() : List.of(scope.trim().split("\\s+")));
        return "consent";
    }

    private UUID userUuid(Authentication authentication) {
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ignored) {
            return loadUser.execute(authentication.getName()).uuid();
        }
    }

    private static String supportedLocale(String locale) {
        return "km".equalsIgnoreCase(locale) ? "km" : "en";
    }

    private static LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    private static void addDefault(Model model, String name, Object value) {
        if (!model.containsAttribute(name)) {
            model.addAttribute(name, value);
        }
    }

}
