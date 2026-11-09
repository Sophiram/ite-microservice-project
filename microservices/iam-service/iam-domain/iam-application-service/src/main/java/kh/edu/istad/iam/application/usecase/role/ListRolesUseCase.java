package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.port.out.PageResult;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.query.RoleFilterQuery;
import kh.edu.istad.iam.application.result.RoleResult;
import kh.edu.istad.iam.domain.entity.Role;

public final class ListRolesUseCase {
        private final RoleRepository roles;
        public ListRolesUseCase(RoleRepository roles) { this.roles = roles; }
        public PageResult<RoleResult> execute(RoleFilterQuery query) {
            PageResult<Role> page = roles.findAll(query.page());
            return new PageResult<>(page.content().stream().map(RoleResult::from).toList(),
                    page.page(), page.size(), page.totalElements());
        }
    }
