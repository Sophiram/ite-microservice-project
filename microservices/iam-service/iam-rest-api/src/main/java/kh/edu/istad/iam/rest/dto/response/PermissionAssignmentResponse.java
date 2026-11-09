package kh.edu.istad.iam.rest.dto.response;

import java.util.UUID;
import kh.edu.istad.iam.application.result.PermissionAssignmentResult;

public record PermissionAssignmentResponse(Long id, UUID permissionUuid, String permissionName) {
    public static PermissionAssignmentResponse from(PermissionAssignmentResult result) {
        return new PermissionAssignmentResponse(result.id(), result.permissionUuid(), result.permissionName());
    }
}
