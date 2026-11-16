package kh.edu.istad.iam.rest.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kh.edu.istad.iam.persistence.entity.SystemConfigEntity;
import kh.edu.istad.iam.persistence.repository.JpaSystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final JpaSystemConfigRepository systemConfigRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasRole('ADMIN') or isAuthenticated()")
    public Map<String, Object> getConfig() {
        List<SystemConfigEntity> list = systemConfigRepository.findAll();
        Map<String, Object> result = new HashMap<>();
        for (SystemConfigEntity entity : list) {
            String k = entity.getKey();
            String v = entity.getValue();
            if ("true".equalsIgnoreCase(v) || "false".equalsIgnoreCase(v)) {
                result.put(k, Boolean.parseBoolean(v));
            } else {
                try {
                    result.put(k, Integer.parseInt(v));
                } catch (NumberFormatException e) {
                    result.put(k, v);
                }
            }
        }
        return result;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveConfig(@RequestBody Map<String, Object> payload) {
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            SystemConfigEntity entity = new SystemConfigEntity(
                    entry.getKey(),
                    String.valueOf(entry.getValue()),
                    Instant.now()
            );
            systemConfigRepository.save(entity);
        }
        return ResponseEntity.ok(getConfig());
    }
}
