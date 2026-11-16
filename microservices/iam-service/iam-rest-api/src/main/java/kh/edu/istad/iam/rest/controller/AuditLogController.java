package kh.edu.istad.iam.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import kh.edu.istad.iam.persistence.entity.AuditLogEntity;
import kh.edu.istad.iam.persistence.repository.JpaAuditLogRepository;
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
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final JpaAuditLogRepository auditLogRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasRole('ADMIN') or isAuthenticated()")
    public Map<String, Object> listAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogEntity> logPage;
        if (action != null && !action.isBlank() && !"ALL".equalsIgnoreCase(action)) {
            logPage = auditLogRepository.findByActionOrderByCreatedAtDesc(action, pageable);
        } else {
            logPage = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        List<AuditLogEntity> content = logPage.getContent();
        if (search != null && !search.isBlank()) {
            String s = search.toLowerCase();
            content = content.stream().filter(l ->
                    (l.getActor() != null && l.getActor().toLowerCase().contains(s)) ||
                    (l.getTarget() != null && l.getTarget().toLowerCase().contains(s)) ||
                    (l.getDetails() != null && l.getDetails().toLowerCase().contains(s)) ||
                    (l.getClientIp() != null && l.getClientIp().contains(s))
            ).toList();
        }

        return Map.of(
                "content", content,
                "totalElements", logPage.getTotalElements(),
                "totalPages", logPage.getTotalPages(),
                "currentPage", logPage.getNumber()
        );
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuditLogEntity> recordAudit(
            Authentication authentication,
            HttpServletRequest request,
            @RequestBody Map<String, String> body) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setAction(body.getOrDefault("action", "SYSTEM_EVENT"));
        entity.setActor(authentication != null ? authentication.getName() : "system");
        entity.setTarget(body.getOrDefault("target", "system"));
        entity.setDetails(body.getOrDefault("details", ""));
        entity.setClientIp(request.getRemoteAddr());
        entity.setCreatedAt(Instant.now());
        return ResponseEntity.ok(auditLogRepository.save(entity));
    }
}
