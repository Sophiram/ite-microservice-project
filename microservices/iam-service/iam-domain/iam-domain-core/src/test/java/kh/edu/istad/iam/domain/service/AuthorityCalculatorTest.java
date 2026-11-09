package kh.edu.istad.iam.domain.service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import kh.edu.istad.iam.domain.entity.Effect;
import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.domain.entity.RolePermission;
import kh.edu.istad.iam.domain.entity.UserPermission;
import kh.edu.istad.iam.domain.entity.UserRole;
import org.junit.jupiter.api.Test;

import static kh.edu.istad.iam.domain.TestData.NOW;
import static kh.edu.istad.iam.domain.TestData.perm;
import static kh.edu.istad.iam.domain.TestData.role;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorityCalculatorTest {

    private final Role admin = role("ADMIN");
    private final Permission read = perm("user", "read");
    private final Permission update = perm("user", "update");
    private final Permission report = perm("report", "read");

    private UserRole userRole(Role r) { return UserRole.assign(1L, r, "sys", null, null, NOW.minusSeconds(60)); }
    private RolePermission rolePerm(Role r, Permission p) { return RolePermission.assign(r, p, "sys", null, null, NOW.minusSeconds(60)); }
    private UserPermission direct(Permission p, Effect e) { return UserPermission.assign(1L, p, e, "sys", null, null, NOW.minusSeconds(60)); }

    @Test
    void effectiveIsRolePermissionsPlusGrantMinusDeny() {
        var result = AuthorityCalculator.calculate(
                List.of(userRole(admin)),
                List.of(rolePerm(admin, read), rolePerm(admin, update)),
                List.of(direct(report, Effect.GRANT), direct(update, Effect.DENY)),
                NOW);

        assertEquals(Set.of("user:read", "report:read"), result.effectivePermissions());
        assertEquals(List.of("ROLE_ADMIN", "report:read", "user:read"), result.authorities());
    }

    @Test
    void revokedRoleAssignmentGivesNothing() {
        UserRole ur = userRole(admin);
        ur.revoke("sys", "left team", NOW.minusSeconds(10));

        var result = AuthorityCalculator.calculate(List.of(ur), List.of(rolePerm(admin, read)), List.of(), NOW);

        assertTrue(result.roleNames().isEmpty());
        assertTrue(result.effectivePermissions().isEmpty());
    }

    @Test
    void expiredAssignmentIsIgnored() {
        UserRole ur = UserRole.assign(1L, admin, "sys", NOW.plus(Duration.ofHours(1)), null, NOW);
        var later = NOW.plus(Duration.ofHours(2));

        var result = AuthorityCalculator.calculate(List.of(ur), List.of(rolePerm(admin, read)), List.of(), later);

        assertTrue(result.roleNames().isEmpty());
        assertTrue(result.effectivePermissions().isEmpty());
    }

    @Test
    void inactiveRoleAndInactivePermissionAreIgnored() {
        UserRole ur = userRole(admin);
        RolePermission rp = rolePerm(admin, read);
        read.deactivate();

        var withInactivePermission = AuthorityCalculator.calculate(List.of(ur), List.of(rp), List.of(), NOW);
        assertTrue(withInactivePermission.effectivePermissions().isEmpty());
        assertEquals(Set.of("ADMIN"), withInactivePermission.roleNames());

        admin.deactivate();
        var withInactiveRole = AuthorityCalculator.calculate(List.of(ur), List.of(rp), List.of(), NOW);
        assertTrue(withInactiveRole.roleNames().isEmpty());
    }

    @Test
    void denyBeatsGrantFromRole() {
        var result = AuthorityCalculator.calculate(
                List.of(userRole(admin)), List.of(rolePerm(admin, read)),
                List.of(direct(read, Effect.DENY)), NOW);

        assertFalse(result.effectivePermissions().contains("user:read"));
        assertEquals(Set.of("user:read"), result.directDenies());
    }
}
