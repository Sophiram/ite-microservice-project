package kh.edu.istad.iam.rest.dto.response;

import java.util.UUID;
import kh.edu.istad.iam.application.result.RoleAssignmentResult;

public record RoleAssignmentResponse(Long id, UUID roleUuid, String roleName) {
    public static RoleAssignmentResponse from(RoleAssignmentResult result) {
        return new RoleAssignmentResponse(result.id(), result.roleUuid(), result.roleName());
    }
}
