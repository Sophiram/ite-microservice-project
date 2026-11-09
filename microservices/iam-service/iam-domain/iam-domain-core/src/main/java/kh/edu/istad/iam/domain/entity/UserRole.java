package kh.edu.istad.iam.domain.entity;

import java.time.Instant;

/** user_roles */
public class UserRole extends AuditedAssignment {

    private Long userId;
    private Role role;

    public UserRole() { }

    public static UserRole assign(Long userId, Role role, String actor,
                                  Instant expiresAt, String reason, Instant now) {
        role.assertAssignable();
        UserRole ur = new UserRole();
        ur.userId = userId;
        ur.role = role;
        ur.initAssignment(actor, expiresAt, reason, now);
        return ur;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
