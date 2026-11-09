package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.command.UpdateRoleCommand;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.result.RoleResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Role;

public final class UpdateRoleUseCase {
        private final RoleRepository roles;
        public UpdateRoleUseCase(RoleRepository roles) { this.roles = roles; }
        public RoleResult execute(UpdateRoleCommand command) {
            Role role = UseCaseSupport.require(roles.findByUuid(command.roleUuid()), "Role");
            role.updateDescription(command.description());
            return RoleResult.from(roles.save(role));
        }
    }
