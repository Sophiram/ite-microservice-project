package kh.edu.istad.iam.application.usecase.role;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.command.AssignPermissionsToRoleCommand;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.result.PermissionAssignmentResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.domain.entity.RolePermission;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

public final class AssignPermissionsToRoleUseCase {
        private final RoleRepository roles;
        private final PermissionRepository permissions;
        private final AssignmentRepository assignments;
        private final CurrentActorProvider actors;
        private final Clock clock;

        public AssignPermissionsToRoleUseCase(RoleRepository roles, PermissionRepository permissions,
                                              AssignmentRepository assignments, CurrentActorProvider actors,
                                              Clock clock) {
            this.roles = roles;
            this.permissions = permissions;
            this.assignments = assignments;
            this.actors = actors;
            this.clock = clock;
        }

        public List<PermissionAssignmentResult> execute(AssignPermissionsToRoleCommand command) {
            Role role = UseCaseSupport.require(roles.findByUuid(command.roleUuid()), "Role");
            List<PermissionAssignmentResult> results = new ArrayList<>();
            for (UUID permissionUuid : command.permissionUuids()) {
                Permission permission = UseCaseSupport.require(permissions.findByUuid(permissionUuid), "Permission");
                var existing = assignments.findRolePermission(role.getId(), permission.getId());
                if (existing.isPresent() && existing.get().isActive(clock.instant())) {
                    throw new DomainException(ErrorCode.DUPLICATE_ENTITY,
                            "Permission is already assigned to the role");
                }
                if (existing.isPresent() && !existing.get().isRevoked()) {
                    existing.get().revoke(actors.currentActor(), "Expired assignment replaced", clock.instant());
                    assignments.save(existing.get());
                }
                RolePermission saved = assignments.save(RolePermission.assign(role, permission,
                        actors.currentActor(), command.expiresAt(), command.reason(), clock.instant()));
                results.add(new PermissionAssignmentResult(saved.getId(), permission.getUuid(),
                        permission.getName()));
            }
            return List.copyOf(results);
        }
    }
