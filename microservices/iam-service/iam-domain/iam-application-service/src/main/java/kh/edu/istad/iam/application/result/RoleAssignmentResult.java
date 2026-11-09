package kh.edu.istad.iam.application.result;

import java.util.UUID;

public record RoleAssignmentResult(Long id, UUID roleUuid, String roleName) { }
