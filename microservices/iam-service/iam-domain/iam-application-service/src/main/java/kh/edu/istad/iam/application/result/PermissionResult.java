package kh.edu.istad.iam.application.result;

import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Permission;

public record PermissionResult(Long id, UUID uuid, String name, String resource, String action,
                                   String description, boolean system, boolean active) {
        public static PermissionResult from(Permission permission) {
            return new PermissionResult(permission.getId(), permission.getUuid(), permission.getName(),
                    permission.getResource(), permission.getAction(), permission.getDescription(),
                    permission.isSystem(), permission.isActive());
        }
    }
