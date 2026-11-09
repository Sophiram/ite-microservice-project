package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.command.AssignRolesToUserCommand;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.RoleAssignmentResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserRole;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

public final class AssignRolesToUserUseCase {
        private final UserRepository users;
        private final RoleRepository roles;
        private final AssignmentRepository assignments;
        private final CurrentActorProvider actors;
        private final Clock clock;

        public AssignRolesToUserUseCase(UserRepository users, RoleRepository roles,
                                        AssignmentRepository assignments, CurrentActorProvider actors, Clock clock) {
            this.users = users;
            this.roles = roles;
            this.assignments = assignments;
            this.actors = actors;
            this.clock = clock;
        }

        public List<RoleAssignmentResult> execute(AssignRolesToUserCommand command) {
            User user = UseCaseSupport.require(users.findByUuid(command.userUuid()), "User");
            List<RoleAssignmentResult> results = new ArrayList<>();
            for (UUID roleUuid : command.roleUuids()) {
                Role role = UseCaseSupport.require(roles.findByUuid(roleUuid), "Role");
                var existing = assignments.findUserRole(user.getId(), role.getId());
                if (existing.isPresent() && existing.get().isActive(clock.instant())) {
                    throw new DomainException(ErrorCode.DUPLICATE_ENTITY, "Role is already assigned to the user");
                }
                UserRole assignment = UserRole.assign(user.getId(), role, actors.currentActor(),
                        command.expiresAt(), command.reason(), clock.instant());
                if (existing.isPresent() && !existing.get().isRevoked()) {
                    existing.get().revoke(actors.currentActor(), "Expired assignment replaced", clock.instant());
                    assignments.save(existing.get());
                }
                UserRole saved = assignments.save(assignment);
                results.add(new RoleAssignmentResult(saved.getId(), role.getUuid(), role.getName()));
            }
            return List.copyOf(results);
        }
    }
