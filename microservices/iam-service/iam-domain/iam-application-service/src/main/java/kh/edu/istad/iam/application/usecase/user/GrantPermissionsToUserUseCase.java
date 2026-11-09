package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.command.GrantPermissionsToUserCommand;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.PermissionGrantResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Effect;
import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserPermission;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

public final class GrantPermissionsToUserUseCase {
        private final UserRepository users;
        private final PermissionRepository permissions;
        private final AssignmentRepository assignments;
        private final CurrentActorProvider actors;
        private final Clock clock;

        public GrantPermissionsToUserUseCase(UserRepository users, PermissionRepository permissions,
                                             AssignmentRepository assignments, CurrentActorProvider actors,
                                             Clock clock) {
            this.users = users;
            this.permissions = permissions;
            this.assignments = assignments;
            this.actors = actors;
            this.clock = clock;
        }

        public List<PermissionGrantResult> execute(GrantPermissionsToUserCommand command) {
            User user = UseCaseSupport.require(users.findByUuid(command.userUuid()), "User");
            Effect effect = command.effect() == null ? Effect.GRANT : command.effect();
            List<PermissionGrantResult> results = new ArrayList<>();
            for (UUID permissionUuid : command.permissionUuids()) {
                Permission permission = UseCaseSupport.require(permissions.findByUuid(permissionUuid), "Permission");
                var existing = assignments.findUserPermission(user.getId(), permission.getId(), effect);
                if (existing.isPresent() && existing.get().isActive(clock.instant())) {
                    throw new DomainException(ErrorCode.DUPLICATE_ENTITY, "Permission is already assigned");
                }
                if (existing.isPresent() && !existing.get().isRevoked()) {
                    existing.get().revoke(actors.currentActor(), "Expired assignment replaced", clock.instant());
                    assignments.save(existing.get());
                }
                UserPermission saved = assignments.save(UserPermission.assign(user.getId(), permission, effect,
                        actors.currentActor(), command.expiresAt(), command.reason(), clock.instant()));
                results.add(new PermissionGrantResult(saved.getId(), permission.getUuid(),
                        permission.getName(), effect));
            }
            return List.copyOf(results);
        }
    }
