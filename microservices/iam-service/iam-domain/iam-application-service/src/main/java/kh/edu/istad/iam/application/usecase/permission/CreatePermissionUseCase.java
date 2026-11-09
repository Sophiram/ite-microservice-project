package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.command.CreatePermissionCommand;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.result.PermissionResult;
import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

public final class CreatePermissionUseCase {
        private final PermissionRepository permissions;
        public CreatePermissionUseCase(PermissionRepository permissions) { this.permissions = permissions; }
        public PermissionResult execute(CreatePermissionCommand command) {
            Permission permission = Permission.create(command.resource(), command.action(), command.description());
            if (permissions.existsByName(permission.getName())) {
                throw new DomainException(ErrorCode.DUPLICATE_ENTITY, "Permission name is already in use");
            }
            return PermissionResult.from(permissions.save(permission));
        }
    }
