package kh.edu.istad.iam.domain.entity;

import java.time.Instant;

/** role_permissions */
public class RolePermission extends AuditedAssignment {

    private Role role;
    private Permission permission;

    public RolePermission() { }

    /** System roles are protected, so permissions cannot be added to them at runtime. */
    public static RolePermission assign(Role role, Permission permission, String actor,
                                        Instant expiresAt, String reason, Instant now) {
        role.assertModifiable();
        role.assertAssignable();
        permission.assertAssignable();
        RolePermission rp = new RolePermission();
        rp.role = role;
        rp.permission = permission;
        rp.initAssignment(actor, expiresAt, reason, now);
        return rp;
    }

    @Override
    public void revoke(String actor, String reason, Instant now) {
        role.assertModifiable();
        super.revoke(actor, reason, now);
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Permission getPermission() { return permission; }
    public void setPermission(Permission permission) { this.permission = permission; }
}
