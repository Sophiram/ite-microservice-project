package kh.edu.istad.iam.rest.controller;

import java.util.List;
import java.util.Map;
import kh.edu.istad.iam.persistence.entity.UserSessionEntity;
import kh.edu.istad.iam.persistence.repository.JpaUserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserSessionController {

    private final JpaUserSessionRepository userSessionRepository;

    @GetMapping("/admin/sessions")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasRole('ADMIN') or isAuthenticated()")
    public List<UserSessionEntity> getAllSessions() {
        return userSessionRepository.findAllByOrderByCreatedAtDesc();
    }

    @DeleteMapping("/admin/sessions/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<Map<String, Object>> terminateAdminSession(@PathVariable String id) {
        userSessionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true, "sessionId", id));
    }

    @DeleteMapping("/admin/sessions/user/{username}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasRole('ADMIN') or isAuthenticated()")
    @Transactional
    public ResponseEntity<Map<String, Object>> terminateAllUserSessions(@PathVariable String username) {
        userSessionRepository.deleteByUsername(username);
        return ResponseEntity.ok(Map.of("success", true, "username", username));
    }

    @GetMapping("/me/user-sessions")
    @PreAuthorize("isAuthenticated()")
    public List<UserSessionEntity> getMySessions(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "admin";
        List<UserSessionEntity> list = userSessionRepository.findByUsernameOrderByCreatedAtDesc(username);
        if (list.isEmpty()) {
            return userSessionRepository.findAllByOrderByCreatedAtDesc();
        }
        return list;
    }

    @DeleteMapping("/me/user-sessions/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> terminateMySession(@PathVariable String id) {
        userSessionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true, "sessionId", id));
    }

    @DeleteMapping("/me/user-sessions")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<Map<String, Object>> terminateMyOtherSessions(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "admin";
        List<UserSessionEntity> list = userSessionRepository.findByUsernameOrderByCreatedAtDesc(username);
        for (UserSessionEntity s : list) {
            if (!Boolean.TRUE.equals(s.getCurrent())) {
                userSessionRepository.delete(s);
            }
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
