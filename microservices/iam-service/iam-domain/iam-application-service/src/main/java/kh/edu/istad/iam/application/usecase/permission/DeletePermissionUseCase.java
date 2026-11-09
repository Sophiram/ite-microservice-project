package kh.edu.istad.iam.application.usecase.permission;

import java.time.Clock;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Permission;

public final class DeletePermissionUseCase {
        private final PermissionRepository permissions;
        private final CurrentActorProvider actors;
        private final Clock clock;
        public DeletePermissionUseCase(PermissionRepository permissions, CurrentActorProvider actors, Clock clock) {
            this.permissions = permissions;
            this.actors = actors;
            this.clock = clock;
        }
        public void execute(UUID permissionUuid) {
            Permission permission = UseCaseSupport.require(permissions.findByUuid(permissionUuid), "Permission");
            permission.delete(actors.currentActor(), clock.instant());
            permissions.save(permission);
        }
    }
