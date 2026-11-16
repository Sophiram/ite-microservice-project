package kh.edu.istad.iam.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.result.UserSessionResult;
import kh.edu.istad.iam.application.usecase.user.GetUserSessionsUseCase;
import kh.edu.istad.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.iam.application.usecase.user.SendEmailVerificationUseCase;
import kh.edu.istad.iam.application.usecase.user.TerminateUserSessionUseCase;
import kh.edu.istad.iam.application.usecase.user.VerifyEmailUseCase;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.infrastructure.service.TelegramAuthenticationService;
import kh.edu.istad.iam.web.dto.ActionResponse;
import kh.edu.istad.iam.web.dto.EmailVerificationResponse;
import kh.edu.istad.iam.web.dto.RegisterForm;
import kh.edu.istad.iam.web.dto.SessionResponse;
import kh.edu.istad.iam.web.dto.VerifyEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@SuppressWarnings({"SpringMVCViewInspection", "SpellCheckingInspection"})
public class IamPageController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoadUserForAuthenticationUseCase loadUserUseCase;
    private final GetUserSessionsUseCase getUserSessionsUseCase;
    private final TerminateUserSessionUseCase terminateUserSessionUseCase;
    private final SendEmailVerificationUseCase sendEmailVerificationUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final TelegramAuthenticationService telegramAuthenticationService;

    @GetMapping("/login")
    public String login() {
        return "forward:/index.html";
    }

    @GetMapping("/account-suspended")
    public String accountSuspended() {
        return "forward:/index.html";
    }

    @GetMapping(value = "/login", params = "error")
    public String loginError() {
        return "forward:/index.html";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        for (String cookieName : List.of("JSESSIONID", "SESSION", "remember-me")) {
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(cookieName, "");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return "redirect:/login?logout";
    }

    @GetMapping(value = "/login", params = "logout")
    public String loginLogout() {
        return "forward:/index.html";
    }

    @RequestMapping(value = "/login/telegram", method = {RequestMethod.GET, RequestMethod.POST})
    public String loginTelegram(
            @RequestParam(value = "tgAuthResult", required = false) String tgAuthResult,
            HttpServletRequest request,
            HttpServletResponse response) {
        return telegramAuthenticationService.authenticate(tgAuthResult, request, response);
    }

    @GetMapping("/register")
    public String registerPage() {
        return "forward:/index.html";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form, BindingResult binding,
                           RedirectAttributes redirect) {
        if (binding.hasErrors() || !form.isPasswordMatching()) {
            redirect.addFlashAttribute("error", "invalid");
            return "redirect:/register?error=invalid";
        }
        try {
            registerUserUseCase.execute(form.toCommand());
            return "redirect:/login?registered=true";
        } catch (DomainException exception) {
            return "redirect:/register?error=duplicate";
        }
    }

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/index.html";
    }

    @GetMapping("/account")
    public String account() {
        return "forward:/index.html";
    }

    @GetMapping("/profile/**")
    public String profile() {
        return "forward:/index.html";
    }

    @GetMapping("/oauth2/consent")
    public String consentPage() {
        return "forward:/index.html";
    }

    @GetMapping(value = "/api/me/sessions", produces = "application/json")
    @ResponseBody
    public List<SessionResponse> getActiveSessions(HttpServletRequest request) {
        String clientIp = resolveClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String sessionId = request.getSession(true).getId();
        List<UserSessionResult> results = getUserSessionsUseCase.execute(clientIp, userAgent, sessionId);
        return results.stream().map(SessionResponse::from).toList();
    }

    @DeleteMapping(value = "/api/me/sessions/{sessionId}", produces = "application/json")
    @ResponseBody
    public ActionResponse terminateSession(@PathVariable String sessionId) {
        terminateUserSessionUseCase.execute(sessionId);
        return ActionResponse.ok("Session terminated: " + sessionId);
    }

    @DeleteMapping(value = "/api/me/sessions", produces = "application/json")
    @ResponseBody
    public ActionResponse terminateAllOtherSessions() {
        terminateUserSessionUseCase.execute(null);
        return ActionResponse.ok("All other sessions terminated");
    }

    @PostMapping(value = "/api/me/send-verification-email", produces = "application/json")
    @ResponseBody
    public EmailVerificationResponse sendVerificationEmail(Authentication authentication) {
        UUID uuid = (authentication != null && authentication.isAuthenticated()) ? userUuid(authentication) : null;
        boolean sent = sendEmailVerificationUseCase.execute(uuid);
        return EmailVerificationResponse.sent(sent);
    }

    @PostMapping(value = "/api/me/verify-email", produces = "application/json")
    @ResponseBody
    public EmailVerificationResponse verifyEmail(@RequestBody(required = false) VerifyEmailRequest body,
                                                 Authentication authentication) {
        UUID uuid = (authentication != null && authentication.isAuthenticated()) ? userUuid(authentication) : null;
        String code = body != null ? body.code() : "";
        boolean verified = verifyEmailUseCase.execute(uuid, code);
        return EmailVerificationResponse.verified(verified);
    }

    @GetMapping("/verify-email")
    public String verifyEmailLink(@RequestParam("token") String token, RedirectAttributes redirect) {
        boolean ok = verifyEmailUseCase.execute(null, token);
        redirect.addFlashAttribute("emailVerified", ok);
        return "redirect:/?emailVerified=" + ok;
    }

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "redirect:/";
        }
        return "redirect:/login?error=true";
    }

    private UUID userUuid(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("No authentication principal found");
        }
        Object principal = authentication.getPrincipal();
        if (principal != null) {
            try {
                java.lang.reflect.Method getAttr = principal.getClass().getMethod("getAttribute", String.class);
                Object uuidAttr = getAttr.invoke(principal, "iam_user_uuid");
                if (uuidAttr != null) {
                    return UUID.fromString(uuidAttr.toString());
                }
                Object usernameAttr = getAttr.invoke(principal, "iam_username");
                if (usernameAttr != null) {
                    return loadUserUseCase.execute(usernameAttr.toString()).uuid();
                }
            } catch (Exception ignored) {}
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ignored) {
            return loadUserUseCase.execute(authentication.getName()).uuid();
        }
    }

    private static String resolveClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1";
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) clientIp = request.getHeader("X-Real-IP");
        if (clientIp == null || clientIp.isBlank()) clientIp = request.getRemoteAddr();
        if (clientIp != null && clientIp.contains(",")) clientIp = clientIp.split(",")[0].trim();
        if (clientIp == null || clientIp.equals("0:0:0:0:0:0:0:1")) clientIp = "127.0.0.1";
        return clientIp;
    }

    private static void addDefault(Model model, String name, Object value) {
        if (!model.containsAttribute(name)) {
            model.addAttribute(name, value);
        }
    }
}
