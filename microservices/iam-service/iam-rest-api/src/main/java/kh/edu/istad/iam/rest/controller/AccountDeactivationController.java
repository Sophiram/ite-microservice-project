package kh.edu.istad.iam.rest.controller;

import java.time.Instant;
import java.util.Map;
import kh.edu.istad.iam.persistence.entity.AccountDeactivationEntity;
import kh.edu.istad.iam.persistence.entity.AuditLogEntity;
import kh.edu.istad.iam.persistence.repository.JpaAccountDeactivationRepository;
import kh.edu.istad.iam.persistence.repository.JpaAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class AccountDeactivationController {

    private final JpaAccountDeactivationRepository deactivationRepository;
    private final JpaAuditLogRepository auditLogRepository;

    @PostMapping("/deactivate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> requestDeactivation(
            Authentication authentication,
            @RequestBody(required = false) Map<String, String> body) {
        String username = authentication != null ? authentication.getName() : "user";
        String reason = body != null ? body.getOrDefault("reason", "User requested deactivation") : "User requested";

        AccountDeactivationEntity entity = new AccountDeactivationEntity();
        entity.setUsername(username);
        entity.setReason(reason);
        entity.setRequestedAt(Instant.now());
        deactivationRepository.save(entity);

        AuditLogEntity audit = new AuditLogEntity();
        audit.setAction("DEACTIVATION_REQUESTED");
        audit.setActor(username);
        audit.setTarget(username);
        audit.setDetails("Account deactivation requested: " + reason);
        audit.setCreatedAt(Instant.now());
        auditLogRepository.save(audit);

        return ResponseEntity.ok(Map.of(
                "status", "DEACTIVATION_REQUESTED",
                "username", username,
                "requestedAt", entity.getRequestedAt().toString()
        ));
    }
}
