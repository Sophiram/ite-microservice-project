package kh.edu.istad.iam.infrastructure.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.edu.istad.iam.application.command.FederatedIdentityCommand;
import kh.edu.istad.iam.application.result.AuthUserResult;
import kh.edu.istad.iam.application.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.iam.domain.entity.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class TelegramAuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(TelegramAuthenticationService.class);

    private final FindOrProvisionFederatedUserUseCase federatedUsers;
    private final String telegramBotToken;
    private final tools.jackson.databind.ObjectMapper objectMapper;
    private final SecurityContextRepository securityContextRepository;
    private final RequestCache requestCache;

    public TelegramAuthenticationService(
            FindOrProvisionFederatedUserUseCase federatedUsers,
            @Value("${TELEGRAM_BOT_TOKEN:}") String telegramBotToken) {
        this.federatedUsers = federatedUsers;
        this.telegramBotToken = telegramBotToken;
        this.objectMapper = new ObjectMapper();
        this.securityContextRepository = new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository());
        this.requestCache = new HttpSessionRequestCache();
    }

    public String authenticate(String tgAuthResult, HttpServletRequest request, HttpServletResponse response) {
        if (tgAuthResult == null || tgAuthResult.isBlank()) {
            return "redirect:/login?error";
        }
        try {
            byte[] decoded = decodeBase64(tgAuthResult);
            Map<String, Object> data = objectMapper.readValue(decoded, new TypeReference<>() {});

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
            log.warn("Telegram authentication failed: {}", e.getMessage());
            return "redirect:/login?error";
        }
    }

    private static byte[] decodeBase64(String input) {
        String s = input.trim();
        int rem = s.length() % 4;
        if (rem > 0) {
            s = s + "=".repeat(4 - rem);
        }
        try {
            return Base64.getUrlDecoder().decode(s);
        } catch (IllegalArgumentException e) {
            return Base64.getDecoder().decode(s);
        }
    }

    private boolean verifyTelegramAuth(Map<String, Object> data) {
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
}
