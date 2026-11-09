package kh.edu.istad.iam.application.usecase.permission;

import java.util.UUID;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Permission;

public final class DeactivatePermissionUseCase {
        private final PermissionRepository permissions;
        public DeactivatePermissionUseCase(PermissionRepository permissions) { this.permissions = permissions; }
        public void execute(UUID permissionUuid) {
            Permission permission = UseCaseSupport.require(permissions.findByUuid(permissionUuid), "Permission");
            permission.deactivate();
            permissions.save(permission);
        }
    }
