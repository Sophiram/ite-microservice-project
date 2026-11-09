package kh.edu.istad.iam.application.result;

import java.util.UUID;

public record PermissionAssignmentResult(Long id, UUID permissionUuid, String permissionName) { }
