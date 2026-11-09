package kh.edu.istad.iam.domain.entity;

import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static kh.edu.istad.iam.domain.TestData.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleAndPermissionTest {

    @Test
    void roleAuthorityHasPrefix() {
        assertEquals("ROLE_ADMIN", Role.create("ADMIN", null).authority());
    }

    @Test
    void roleNameMustBeUpperSnakeCase() {
        assertThrows(DomainException.class, () -> Role.create("admin user", null));
    }

    @Test
    void permissionNameIsResourceColonAction() {
        Permission p = Permission.create("User", "READ", "read users");
        assertEquals("user:read", p.getName());
        assertEquals("user:read", p.authority());
    }

    @Test
    void systemRoleCannotBeChangedOrDeleted() {
        Role r = Role.create("ADMIN", null);
        r.setSystem(true);
        assertEquals(ErrorCode.SYSTEM_ENTITY_PROTECTED,
                assertThrows(DomainException.class, () -> r.updateDescription("x")).getCode());
        assertThrows(DomainException.class, () -> r.delete("me", NOW));
        assertThrows(DomainException.class, r::deactivate);
    }

    @Test
    void inactiveRoleAndPermissionCannotBeAssigned() {
        Role r = Role.create("EDITOR", null);
        r.deactivate();
        assertEquals(ErrorCode.ENTITY_INACTIVE,
                assertThrows(DomainException.class,
                        () -> UserRole.assign(1L, r, "me", null, null, NOW)).getCode());

        Permission p = Permission.create("course", "publish", null);
        p.deactivate();
        assertThrows(DomainException.class,
                () -> UserPermission.assign(1L, p, Effect.GRANT, "me", null, null, NOW));
    }

    @Test
    void softDeleteKeepsRowAndRecordsActor() {
        Role r = Role.create("TEMP", null);
        r.delete("admin-uuid", NOW);
        assertTrue(r.isDeleted());
        assertEquals("admin-uuid", r.getDeletedBy());
        assertEquals(NOW, r.getDeletedAt());
        assertFalse(r.isUsable());
    }

    @Test
    void assignmentCannotBeRevokedTwice() {
        UserRole ur = UserRole.assign(1L, Role.create("USER", null), "me", null, null, NOW);
        ur.revoke("me", "done", NOW.plusSeconds(1));
        assertTrue(ur.isRevoked());
        assertEquals(ErrorCode.ASSIGNMENT_ALREADY_REVOKED,
                assertThrows(DomainException.class, () -> ur.revoke("me", null, NOW.plusSeconds(2))).getCode());
    }

    @Test
    void cannotAddPermissionToSystemRole() {
        Role r = Role.create("ADMIN", null);
        r.setSystem(true);
        Permission p = Permission.create("user", "read", null);
        assertThrows(DomainException.class, () -> RolePermission.assign(r, p, "me", null, null, NOW));
    }
}
