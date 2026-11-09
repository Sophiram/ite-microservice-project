package kh.edu.istad.iam.application.usecase.role;

import java.time.Clock;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Role;

public final class DeleteRoleUseCase {
        private final RoleRepository roles;
        private final CurrentActorProvider actors;
        private final Clock clock;
        public DeleteRoleUseCase(RoleRepository roles, CurrentActorProvider actors, Clock clock) {
            this.roles = roles;
            this.actors = actors;
            this.clock = clock;
        }
        public void execute(UUID roleUuid) {
            Role role = UseCaseSupport.require(roles.findByUuid(roleUuid), "Role");
            role.delete(actors.currentActor(), clock.instant());
            roles.save(role);
        }
    }
