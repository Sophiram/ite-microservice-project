package kh.edu.istad.iam.application.usecase.role;

import java.time.Clock;
import kh.edu.istad.iam.application.command.RevokePermissionFromRoleCommand;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.domain.entity.RolePermission;

public final class RevokePermissionFromRoleUseCase {
        private final RoleRepository roles;
        private final PermissionRepository permissions;
        private final AssignmentRepository assignments;
        private final CurrentActorProvider actors;
        private final Clock clock;

        public RevokePermissionFromRoleUseCase(RoleRepository roles, PermissionRepository permissions,
                                               AssignmentRepository assignments, CurrentActorProvider actors,
                                               Clock clock) {
            this.roles = roles;
            this.permissions = permissions;
            this.assignments = assignments;
            this.actors = actors;
            this.clock = clock;
        }

        public void execute(RevokePermissionFromRoleCommand command) {
            Role role = UseCaseSupport.require(roles.findByUuid(command.roleUuid()), "Role");
            Permission permission = UseCaseSupport.require(
                    permissions.findByUuid(command.permissionUuid()), "Permission");
            RolePermission assignment = UseCaseSupport.require(
                    assignments.findRolePermission(role.getId(), permission.getId()), "Role permission assignment");
            assignment.revoke(actors.currentActor(), command.reason(), clock.instant());
            assignments.save(assignment);
        }
    }
