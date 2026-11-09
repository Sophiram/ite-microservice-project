package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import kh.edu.istad.iam.application.command.RevokePermissionFromUserCommand;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Effect;
import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserPermission;

public final class RevokePermissionFromUserUseCase {
        private final UserRepository users;
        private final PermissionRepository permissions;
        private final AssignmentRepository assignments;
        private final CurrentActorProvider actors;
        private final Clock clock;

        public RevokePermissionFromUserUseCase(UserRepository users, PermissionRepository permissions,
                                               AssignmentRepository assignments, CurrentActorProvider actors,
                                               Clock clock) {
            this.users = users;
            this.permissions = permissions;
            this.assignments = assignments;
            this.actors = actors;
            this.clock = clock;
        }

        public void execute(RevokePermissionFromUserCommand command) {
            User user = UseCaseSupport.require(users.findByUuid(command.userUuid()), "User");
            Permission permission = UseCaseSupport.require(
                    permissions.findByUuid(command.permissionUuid()), "Permission");
            Effect effect = command.effect() == null ? Effect.GRANT : command.effect();
            UserPermission assignment = UseCaseSupport.require(assignments.findUserPermission(
                    user.getId(), permission.getId(), effect), "User permission assignment");
            assignment.revoke(actors.currentActor(), command.reason(), clock.instant());
            assignments.save(assignment);
        }
    }
