package kh.edu.istad.iam.application.usecase.role;

import java.util.UUID;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.result.RoleResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;

public final class GetRoleUseCase {
        private final RoleRepository roles;
        public GetRoleUseCase(RoleRepository roles) { this.roles = roles; }
        public RoleResult execute(UUID roleUuid) {
            return RoleResult.from(UseCaseSupport.require(roles.findByUuid(roleUuid), "Role"));
        }
    }
