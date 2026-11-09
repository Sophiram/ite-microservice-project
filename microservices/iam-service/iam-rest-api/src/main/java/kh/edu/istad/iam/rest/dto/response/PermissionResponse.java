package kh.edu.istad.iam.rest.dto.response;

import java.util.UUID;
import kh.edu.istad.iam.application.result.PermissionResult;

public record PermissionResponse(UUID uuid, String name, String resource, String action,
                                     String description, boolean system, boolean active) {
        public static PermissionResponse from(PermissionResult permission) {
            return new PermissionResponse(permission.uuid(), permission.name(), permission.resource(),
                    permission.action(), permission.description(), permission.system(), permission.active());
        }
    }
