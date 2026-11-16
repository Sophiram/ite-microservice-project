package kh.edu.istad.iam.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import kh.edu.istad.iam.application.command.RegisterUserCommand;
import kh.edu.istad.iam.application.port.out.EmailVerificationPort;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.AuthUserResult;
import kh.edu.istad.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.iam.domain.entity.Profile;
import kh.edu.istad.iam.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    private static final Logger log = LoggerFactory.getLogger(TwoFactorAuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationPort emailVerificationPort;
    private final LoadUserForAuthenticationUseCase loadUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    // Cache for pending registrations: email -> PendingRegistration
    private final Map<String, PendingRegistration> pendingRegistrations = new ConcurrentHashMap<>();

    public record PendingRegistration(RegisterUserCommand command, Instant expiresAt) {
        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    public record LoginStep1Request(@NotBlank String username, @NotBlank String password) {}
    public record LoginStep2Request(@NotBlank String username, @NotBlank String code) {}

    public record RegisterStep1Request(
            String givenName,
            String familyName,
            @NotBlank String username,
            @NotBlank String email,
            @NotBlank String password,
            String confirmPassword,
            String locale
    ) {}

    public record RegisterStep2Request(@NotBlank String email, @NotBlank String code) {}
    public record ResendCodeRequest(String email, String username) {}

    /**
     * 1. Login Step 1: Verify username and password, then dispatch 6-digit 2FA email OTP.
     */
    @PostMapping("/login-step1")
    public ResponseEntity<?> loginStep1(@RequestBody LoginStep1Request req) {
        String login = req.username().trim();
        var userOpt = userRepository.findByUsernameOrEmail(login);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }

        User user = userOpt.get();
        if (user.getPasswordHash() == null || !passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }

        if (!user.isAccountNonLocked()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "Account has been locked. Please contact your system administrator.",
                            "reason", "account_locked",
                            "status", "suspended"
                    ));
        }

        if (!user.isEnabled() || user.isDeleted()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "Account has been deactivated by an administrator.",
                            "reason", "account_disabled",
                            "status", "suspended"
                    ));
        }

        if (!user.canAuthenticate()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "Account is disabled or locked.",
                            "reason", "account_disabled",
                            "status", "suspended"
                    ));
        }

        String email = user.getEmail();
        log.info("[2FA] Login Step 1 valid for {}. Sending OTP code to {}", user.getUsername(), email);
        emailVerificationPort.sendVerificationEmail(email, user.getUsername());

        return ResponseEntity.ok(Map.of(
                "step", "2fa_required",
                "username", user.getUsername(),
                "emailMasked", maskEmail(email),
                "message", "2-Step verification code sent to " + maskEmail(email)
        ));
    }

    /**
     * 2. Login Step 2: Verify 6-digit OTP code, then log user in and set session.
     */
    @PostMapping("/verify-login")
    public ResponseEntity<?> verifyLogin(@RequestBody LoginStep2Request req,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        String login = req.username().trim();
        var userOpt = userRepository.findByUsernameOrEmail(login);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        boolean verified = emailVerificationPort.verifyCode(user.getEmail(), req.code());
        if (!verified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid or expired verification code. Please try again."));
        }

        log.info("[2FA] Verification code successful for user {}. Creating authenticated session.", user.getUsername());

        // Authenticate user in Spring Security Context and persist to HTTP Session
        AuthUserResult authUser = loadUserUseCase.execute(user.getUsername());
        List<SimpleGrantedAuthority> authorities = authUser.authorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "username", user.getUsername(),
                "redirectUrl", "/"
        ));
    }

    /**
     * 3. Register Step 1: Validate registration info, store temporarily, and send OTP.
     */
    @PostMapping("/register-step1")
    public ResponseEntity<?> registerStep1(@RequestBody RegisterStep1Request req) {
        if (req.password() == null || req.password().length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 8 characters long."));
        }
        if (req.confirmPassword() != null && !req.password().equals(req.confirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match."));
        }

        String username = req.username().trim();
        String email = req.email().trim().toLowerCase();

        if (userRepository.findByUsernameOrEmail(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists."));
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists."));
        }

        RegisterUserCommand command = new RegisterUserCommand(
                username,
                email,
                req.password(),
                Profile.of(
                        req.givenName() != null ? req.givenName().trim() : "Sophiram",
                        req.familyName() != null ? req.familyName().trim() : "Sorn",
                        req.locale() != null ? req.locale() : "km"
                )
        );

        pendingRegistrations.put(email, new PendingRegistration(command, Instant.now().plusSeconds(900)));

        log.info("[2FA] Registration Step 1 valid for {}. Sending OTP to {}", username, email);
        emailVerificationPort.sendVerificationEmail(email, username);

        return ResponseEntity.ok(Map.of(
                "step", "2fa_required",
                "email", email,
                "emailMasked", maskEmail(email),
                "message", "2-Step verification code sent to " + maskEmail(email)
        ));
    }

    /**
     * 4. Register Step 2: Verify 6-digit OTP code, then execute user registration.
     */
    @PostMapping("/verify-register")
    public ResponseEntity<?> verifyRegister(@RequestBody RegisterStep2Request req,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        String email = req.email().trim().toLowerCase();
        PendingRegistration pending = pendingRegistrations.get(email);

        if (pending == null || pending.isExpired()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Registration session expired. Please sign up again."));
        }

        boolean verified = emailVerificationPort.verifyCode(email, req.code());
        if (!verified) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid or expired verification code. Please try again."));
        }

        pendingRegistrations.remove(email);

        try {
            var userResult = registerUserUseCase.execute(pending.command());
            log.info("[2FA] Successfully registered new user {} with verified email.", userResult.username());

            // Auto-login the newly registered user
            AuthUserResult authUser = loadUserUseCase.execute(userResult.username());
            List<SimpleGrantedAuthority> authorities = authUser.authorities().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            Authentication auth = new UsernamePasswordAuthenticationToken(userResult.username(), null, authorities);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username", userResult.username(),
                    "redirectUrl", "/"
            ));
        } catch (Exception ex) {
            log.error("[2FA] Failed to complete user registration: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * 5. Resend Verification Code
     */
    @PostMapping("/resend")
    public ResponseEntity<?> resendCode(@RequestBody ResendCodeRequest req) {
        String email = req.email() != null ? req.email().trim() : "";
        if (email.isBlank() && req.username() != null) {
            var userOpt = userRepository.findByUsernameOrEmail(req.username().trim());
            if (userOpt.isPresent()) email = userOpt.get().getEmail();
        }

        if (email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Recipient email not found."));
        }

        emailVerificationPort.sendVerificationEmail(email, req.username());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Verification code resent to " + maskEmail(email)
        ));
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        int atIndex = email.indexOf('@');
        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (name.length() <= 2) {
            return name.charAt(0) + "***" + domain;
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + domain;
    }
}
