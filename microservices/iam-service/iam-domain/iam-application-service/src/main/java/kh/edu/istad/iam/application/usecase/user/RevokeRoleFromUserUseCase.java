package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import kh.edu.istad.iam.application.command.RevokeRoleFromUserCommand;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserRole;

public final class RevokeRoleFromUserUseCase {
        private final UserRepository users;
        private final RoleRepository roles;
        private final AssignmentRepository assignments;
        private final CurrentActorProvider actors;
        private final Clock clock;

        public RevokeRoleFromUserUseCase(UserRepository users, RoleRepository roles, AssignmentRepository assignments,
                                         CurrentActorProvider actors, Clock clock) {
            this.users = users;
            this.roles = roles;
            this.assignments = assignments;
            this.actors = actors;
            this.clock = clock;
        }

        public void execute(RevokeRoleFromUserCommand command) {
            User user = UseCaseSupport.require(users.findByUuid(command.userUuid()), "User");
            Role role = UseCaseSupport.require(roles.findByUuid(command.roleUuid()), "Role");
            UserRole assignment = UseCaseSupport.require(
                    assignments.findUserRole(user.getId(), role.getId()), "User role assignment");
            assignment.revoke(actors.currentActor(), command.reason(), clock.instant());
            assignments.save(assignment);
        }
    }
