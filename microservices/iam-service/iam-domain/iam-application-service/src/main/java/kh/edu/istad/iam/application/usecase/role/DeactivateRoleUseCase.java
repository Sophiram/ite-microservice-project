package kh.edu.istad.iam.application.usecase.role;

import java.util.UUID;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Role;

public final class DeactivateRoleUseCase {
        private final RoleRepository roles;
        public DeactivateRoleUseCase(RoleRepository roles) { this.roles = roles; }
        public void execute(UUID roleUuid) {
            Role role = UseCaseSupport.require(roles.findByUuid(roleUuid), "Role");
            role.deactivate();
            roles.save(role);
        }
    }
