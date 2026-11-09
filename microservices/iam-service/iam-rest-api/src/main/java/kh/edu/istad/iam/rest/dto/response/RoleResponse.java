package kh.edu.istad.iam.rest.dto.response;

import java.util.UUID;
import kh.edu.istad.iam.application.result.RoleResult;

public record RoleResponse(UUID uuid, String name, String description, boolean system, boolean active) {
        public static RoleResponse from(RoleResult role) {
            return new RoleResponse(role.uuid(), role.name(), role.description(), role.system(), role.active());
        }
    }
