package kh.edu.istad.iam.application.usecase.role;

import java.time.Clock;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.result.PermissionAssignmentResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Role;

public final class GetRolePermissionsUseCase {
    private final RoleRepository roles;
    private final AssignmentRepository assignments;
    private final Clock clock;

    public GetRolePermissionsUseCase(RoleRepository roles, AssignmentRepository assignments, Clock clock) {
        this.roles = roles;
        this.assignments = assignments;
        this.clock = clock;
    }

    public List<PermissionAssignmentResult> execute(UUID roleUuid) {
        Role role = UseCaseSupport.require(roles.findByUuid(roleUuid), "Role");
        return assignments.findRolePermissions(List.of(role.getId())).stream()
                .filter(rp -> rp.isActive(clock.instant()))
                .map(rp -> new PermissionAssignmentResult(
                        rp.getId(),
                        rp.getPermission().getUuid(),
                        rp.getPermission().getName()))
                .toList();
    }
}
