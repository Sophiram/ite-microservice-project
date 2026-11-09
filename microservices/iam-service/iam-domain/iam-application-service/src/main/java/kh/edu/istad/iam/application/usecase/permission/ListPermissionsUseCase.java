package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.port.out.PageResult;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.query.PermissionFilterQuery;
import kh.edu.istad.iam.application.result.PermissionResult;
import kh.edu.istad.iam.domain.entity.Permission;

public final class ListPermissionsUseCase {
        private final PermissionRepository permissions;
        public ListPermissionsUseCase(PermissionRepository permissions) { this.permissions = permissions; }
        public PageResult<PermissionResult> execute(PermissionFilterQuery query) {
            PageResult<Permission> page = permissions.findAll(query.page());
            return new PageResult<>(page.content().stream().map(PermissionResult::from).toList(),
                    page.page(), page.size(), page.totalElements());
        }
    }
