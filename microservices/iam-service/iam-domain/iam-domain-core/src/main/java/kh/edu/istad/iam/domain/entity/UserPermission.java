package kh.edu.istad.iam.domain.entity;

import java.time.Instant;

/** user_permissions: a direct GRANT or DENY for one user. */
public class UserPermission extends AuditedAssignment {

    private Long userId;
    private Permission permission;
    private Effect effect = Effect.GRANT;

    public UserPermission() { }

    public static UserPermission assign(Long userId, Permission permission, Effect effect,
                                        String actor, Instant expiresAt, String reason, Instant now) {
        permission.assertAssignable();
        UserPermission up = new UserPermission();
        up.userId = userId;
        up.permission = permission;
        up.effect = effect == null ? Effect.GRANT : effect;
        up.initAssignment(actor, expiresAt, reason, now);
        return up;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Permission getPermission() { return permission; }
    public void setPermission(Permission permission) { this.permission = permission; }
    public Effect getEffect() { return effect; }
    public void setEffect(Effect effect) { this.effect = effect; }
}
