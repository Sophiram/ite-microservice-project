package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import java.util.List;
import kh.edu.istad.iam.application.command.RegisterUserCommand;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PasswordHasher;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.UserResult;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserRole;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

public final class RegisterUserUseCase {
        private final UserRepository users;
        private final PasswordHasher passwords;
        private final RoleRepository roles;
        private final AssignmentRepository assignments;
        private final CurrentActorProvider actors;
        private final Clock clock;

        public RegisterUserUseCase(UserRepository users, PasswordHasher passwords, Clock clock) {
            this(users, passwords, null, null, null, clock);
        }

        public RegisterUserUseCase(UserRepository users, PasswordHasher passwords, RoleRepository roles,
                                   AssignmentRepository assignments, CurrentActorProvider actors, Clock clock) {
            this.users = users;
            this.passwords = passwords;
            this.roles = roles;
            this.assignments = assignments;
            this.actors = actors;
            this.clock = clock;
        }

        public UserResult execute(RegisterUserCommand command) {
            if (command.username() == null || command.username().isBlank()
                    || command.email() == null || command.email().isBlank()
                    || command.password() == null || command.password().isBlank()) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR,
                        "Username, email, and password are required");
            }
            if (users.existsByUsername(command.username().trim())) {
                throw new DomainException(ErrorCode.DUPLICATE_ENTITY, "Username is already in use");
            }
            String email = User.normalizeEmail(command.email());
            if (users.existsByEmail(email)) {
                throw new DomainException(ErrorCode.DUPLICATE_ENTITY, "Email is already in use");
            }
            User saved = users.save(User.register(command.username().trim(), email,
                    passwords.hash(command.password()), command.profile(), clock.instant()));
            List<UserRole> newRoles = assignDefaultUserRole(saved);
            return UserResult.from(saved, newRoles);
        }

        private List<UserRole> assignDefaultUserRole(User user) {
            if (roles == null || assignments == null) return List.of();
            return roles.findByName("USER").map(role -> {
                String actor = actors == null ? "system" : actors.currentActor();
                return List.of(assignments.save(UserRole.assign(user.getId(), role, actor,
                        null, "Default registration role", clock.instant())));
            }).orElseGet(List::of);
        }
    }
