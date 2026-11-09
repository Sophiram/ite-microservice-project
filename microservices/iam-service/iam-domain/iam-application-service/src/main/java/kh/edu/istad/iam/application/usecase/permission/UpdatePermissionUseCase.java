package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.command.UpdatePermissionCommand;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.result.PermissionResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Permission;

public final class UpdatePermissionUseCase {
        private final PermissionRepository permissions;
        public UpdatePermissionUseCase(PermissionRepository permissions) { this.permissions = permissions; }
        public PermissionResult execute(UpdatePermissionCommand command) {
            Permission permission = UseCaseSupport.require(
                    permissions.findByUuid(command.permissionUuid()), "Permission");
            permission.updateDescription(command.description());
            return PermissionResult.from(permissions.save(permission));
        }
    }
