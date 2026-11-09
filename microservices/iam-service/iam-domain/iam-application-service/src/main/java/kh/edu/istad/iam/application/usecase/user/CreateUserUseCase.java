package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import kh.edu.istad.iam.application.command.AssignRolesToUserCommand;
import kh.edu.istad.iam.application.command.CreateUserCommand;
import kh.edu.istad.iam.application.command.RegisterUserCommand;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PasswordHasher;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.UserResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.User;

import static kh.edu.istad.iam.application.usecase.user.UserUseCaseSupport.activeUserRoles;

public final class CreateUserUseCase {
        private final UserRepository users;
        private final AssignmentRepository assignments;
        private final Clock clock;
        private final RegisterUserUseCase register;
        private final AssignRolesToUserUseCase assignRoles;

        public CreateUserUseCase(UserRepository users, RoleRepository roles, AssignmentRepository assignments,
                                 PasswordHasher passwords, CurrentActorProvider actors, Clock clock) {
            this.users = users;
            this.assignments = assignments;
            this.clock = clock;
            this.register = new RegisterUserUseCase(users, passwords, clock);
            this.assignRoles = new AssignRolesToUserUseCase(users, roles, assignments, actors, clock);
        }

        public UserResult execute(CreateUserCommand command) {
            UserResult created = register.execute(new RegisterUserCommand(
                    command.username(), command.email(), command.password(), command.profile()));
            User user = UseCaseSupport.require(users.findByUuid(created.uuid()), "User");
            if (command.address() != null) user.updateAddress(command.address());
            if (!command.roleUuids().isEmpty()) {
                assignRoles.execute(new AssignRolesToUserCommand(user.getUuid(), command.roleUuids(),
                        null, "Assigned during user creation"));
            }
            user = users.save(user);
            return UserResult.from(user, activeUserRoles(user.getId(), assignments, clock));
        }
    }
