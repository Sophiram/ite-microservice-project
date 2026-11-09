package kh.edu.istad.iam.application.result;

import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Role;

public record RoleResult(Long id, UUID uuid, String name, String description,
                             boolean system, boolean active) {
        public static RoleResult from(Role role) {
            return new RoleResult(role.getId(), role.getUuid(), role.getName(), role.getDescription(),
                    role.isSystem(), role.isActive());
        }
    }
