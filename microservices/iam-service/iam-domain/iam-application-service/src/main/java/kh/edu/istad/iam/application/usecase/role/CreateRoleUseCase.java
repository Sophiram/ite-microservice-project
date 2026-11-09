package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.command.AssignPermissionsToRoleCommand;
import kh.edu.istad.iam.application.command.CreateRoleCommand;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.result.RoleResult;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

public final class CreateRoleUseCase {
        private final RoleRepository roles;
        private final AssignPermissionsToRoleUseCase assignPermissions;
        public CreateRoleUseCase(RoleRepository roles) { this(roles, null); }
        public CreateRoleUseCase(RoleRepository roles, AssignPermissionsToRoleUseCase assignPermissions) {
            this.roles = roles;
            this.assignPermissions = assignPermissions;
        }
        public RoleResult execute(CreateRoleCommand command) {
            if (roles.existsByName(command.name())) {
                throw new DomainException(ErrorCode.DUPLICATE_ENTITY, "Role name is already in use");
            }
            RoleResult result = RoleResult.from(
                    roles.save(Role.create(command.name(), command.description())));
            if (assignPermissions != null && !command.permissionUuids().isEmpty()) {
                assignPermissions.execute(new AssignPermissionsToRoleCommand(
                        result.uuid(), command.permissionUuids(), null, "Assigned during role creation"));
            }
            return result;
        }
    }
