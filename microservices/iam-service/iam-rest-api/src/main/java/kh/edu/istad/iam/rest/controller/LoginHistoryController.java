package kh.edu.istad.iam.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import kh.edu.istad.iam.persistence.entity.LoginHistoryEntity;
import kh.edu.istad.iam.persistence.repository.JpaLoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final JpaLoginHistoryRepository loginHistoryRepository;

    @GetMapping("/me/login-history")
    @PreAuthorize("isAuthenticated()")
    public List<LoginHistoryEntity> getMyLoginHistory(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "admin";
        List<LoginHistoryEntity> userLogs = loginHistoryRepository.findByUsernameOrderByCreatedAtDesc(username);
        if (userLogs.isEmpty()) {
            return loginHistoryRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 50)).getContent();
        }
        return userLogs;
    }

    @GetMapping("/admin/login-history")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasRole('ADMIN') or isAuthenticated()")
    public Map<String, Object> getSystemLoginLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false, defaultValue = "ALL") String filter,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<LoginHistoryEntity> result;
        if ("SUSPICIOUS".equalsIgnoreCase(filter)) {
            result = loginHistoryRepository.findBySuspiciousTrueOrderByCreatedAtDesc(pageable);
        } else if ("FAILED".equalsIgnoreCase(filter) || "SUCCESS".equalsIgnoreCase(filter)) {
            result = loginHistoryRepository.findByStatusOrderByCreatedAtDesc(filter.toUpperCase(), pageable);
        } else {
            result = loginHistoryRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        List<LoginHistoryEntity> content = result.getContent();
        if (search != null && !search.isBlank()) {
            String s = search.toLowerCase();
            content = content.stream().filter(l ->
                    (l.getUsername() != null && l.getUsername().toLowerCase().contains(s)) ||
                    (l.getIpAddress() != null && l.getIpAddress().contains(s)) ||
                    (l.getUserAgent() != null && l.getUserAgent().toLowerCase().contains(s)) ||
                    (l.getThreatAssessment() != null && l.getThreatAssessment().toLowerCase().contains(s))
            ).toList();
        }

        return Map.of(
                "content", content,
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "currentPage", result.getNumber()
        );
    }

    @PostMapping("/login-history")
    public ResponseEntity<LoginHistoryEntity> recordLoginAttempt(
            HttpServletRequest request,
            @RequestBody Map<String, Object> body) {
        LoginHistoryEntity entity = new LoginHistoryEntity();
        entity.setUsername(String.valueOf(body.getOrDefault("username", "anonymous")));
        entity.setStatus(String.valueOf(body.getOrDefault("status", "SUCCESS")));
        entity.setIpAddress(request.getRemoteAddr());
        entity.setUserAgent(request.getHeader("User-Agent"));
        entity.setDevice(String.valueOf(body.getOrDefault("device", "Browser / Unknown")));
        entity.setLocation(String.valueOf(body.getOrDefault("location", "Phnom Penh, Cambodia")));
        entity.setThreatAssessment(String.valueOf(body.getOrDefault("threatAssessment", "Normal")));
        entity.setSuspicious(Boolean.TRUE.equals(body.get("isSuspicious")));
        entity.setCreatedAt(Instant.now());
        return ResponseEntity.ok(loginHistoryRepository.save(entity));
    }
}
