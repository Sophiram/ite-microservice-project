package kh.edu.istad.iam.domain.service;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import kh.edu.istad.iam.domain.entity.Effect;
import kh.edu.istad.iam.domain.entity.RolePermission;
import kh.edu.istad.iam.domain.entity.UserAuthorities;
import kh.edu.istad.iam.domain.entity.UserPermission;
import kh.edu.istad.iam.domain.entity.UserRole;

/**
 * effective permissions = active role permissions + direct GRANT - direct DENY.
 * Revoked/expired assignments and inactive/deleted roles or permissions are ignored.
 */
public final class AuthorityCalculator {

    private AuthorityCalculator() { }

    public static UserAuthorities calculate(Collection<UserRole> userRoles,
                                            Collection<RolePermission> rolePermissions,
                                            Collection<UserPermission> userPermissions,
                                            Instant now) {
        Set<String> activeRoles = new TreeSet<>();
        for (UserRole ur : userRoles) {
            if (ur.isActive(now) && ur.getRole().isUsable()) {
                activeRoles.add(ur.getRole().getName());
            }
        }

        Set<String> effective = new TreeSet<>();
        for (RolePermission rp : rolePermissions) {
            boolean roleIsGranted = activeRoles.contains(rp.getRole().getName());
            if (roleIsGranted && rp.isActive(now) && rp.getPermission().isUsable()) {
                effective.add(rp.getPermission().authority());
            }
        }

        Set<String> grants = new TreeSet<>();
        Set<String> denies = new TreeSet<>();
        for (UserPermission up : userPermissions) {
            if (!up.isActive(now) || !up.getPermission().isUsable()) {
                continue;
            }
            (up.getEffect() == Effect.DENY ? denies : grants).add(up.getPermission().authority());
        }

        effective.addAll(grants);
        effective.removeAll(denies);   // DENY always wins
        return new UserAuthorities(activeRoles, grants, denies, effective);
    }
}
