package kh.edu.istad.platform.authserver.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Sample resource endpoints used to prove that access tokens issued by this server work. */
@RestController
@RequestMapping("/api")
public class ApiController {

    /** No token needed. */
    @GetMapping("/public/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok");
    }

    /** Needs a token with scope api.read. */
    @GetMapping("/hello")
    public Map<String, Object> hello(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Hello from the protected API");
        body.put("subject", jwt.getSubject());
        body.put("audience", jwt.getAudience());
        body.put("scope", jwt.getClaimAsStringList("scope"));
        body.put("roles", jwt.getClaimAsStringList("roles"));
        body.put("expiresAt", jwt.getExpiresAt());
        return body;
    }

    /** Needs a token with scope api.write. */
    @GetMapping("/admin")
    public Map<String, String> admin(@AuthenticationPrincipal Jwt jwt) {
        return Map.of("message", "Admin area", "subject", jwt.getSubject());
    }
}
