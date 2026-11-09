package kh.edu.istad.iam.application.usecase.permission;

import java.util.UUID;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Permission;

public final class ActivatePermissionUseCase {
        private final PermissionRepository permissions;
        public ActivatePermissionUseCase(PermissionRepository permissions) { this.permissions = permissions; }
        public void execute(UUID permissionUuid) {
            Permission permission = UseCaseSupport.require(permissions.findByUuid(permissionUuid), "Permission");
            permission.activate();
            permissions.save(permission);
        }
    }
