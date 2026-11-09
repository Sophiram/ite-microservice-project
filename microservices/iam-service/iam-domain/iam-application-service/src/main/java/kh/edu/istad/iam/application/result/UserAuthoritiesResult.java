package kh.edu.istad.iam.application.result;

import java.util.List;

public record UserAuthoritiesResult(List<UserRoleAuthority> roles, List<DirectPermission> directPermissions,
                                        List<String> effectivePermissions) {
        public UserAuthoritiesResult {
            roles = List.copyOf(roles);
            directPermissions = List.copyOf(directPermissions);
            effectivePermissions = List.copyOf(effectivePermissions);
        }
    }
