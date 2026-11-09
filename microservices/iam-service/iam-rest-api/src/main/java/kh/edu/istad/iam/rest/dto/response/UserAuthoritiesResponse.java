package kh.edu.istad.iam.rest.dto.response;

import java.util.List;
import kh.edu.istad.iam.application.result.DirectPermission;
import kh.edu.istad.iam.application.result.UserAuthoritiesResult;
import kh.edu.istad.iam.application.result.UserRoleAuthority;

public record UserAuthoritiesResponse(
        List<UserRoleAuthority> roles,
        List<DirectPermission> directPermissions,
        List<String> effectivePermissions
) {
        public static UserAuthoritiesResponse from(UserAuthoritiesResult result) {
            return new UserAuthoritiesResponse(
                    result.roles().stream()
                        .map(role -> new UserRoleAuthority(role.uuid(), role.name(), role.expiresAt())).toList(),
                    result.directPermissions().stream()
                            .map(permission -> new DirectPermission(permission.name(), permission.effect())).toList(),
                    result.effectivePermissions());
        }
    }
