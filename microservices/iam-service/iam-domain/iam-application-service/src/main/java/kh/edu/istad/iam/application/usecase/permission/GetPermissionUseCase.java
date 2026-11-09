package kh.edu.istad.iam.application.usecase.permission;

import java.util.UUID;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.result.PermissionResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;

public final class GetPermissionUseCase {
        private final PermissionRepository permissions;
        public GetPermissionUseCase(PermissionRepository permissions) { this.permissions = permissions; }
        public PermissionResult execute(UUID permissionUuid) {
            return PermissionResult.from(
                    UseCaseSupport.require(permissions.findByUuid(permissionUuid), "Permission"));
        }
    }
