package kh.edu.istad.iam.domain.entity;

import java.time.Instant;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

/**
 * Shared columns of user_roles / role_permissions / user_permissions.
 * Assignments are revoked, never deleted: revokedAt == null means active.
 */
public abstract class AuditedAssignment {

    private Long id;
    private Instant assignedAt;
    private String assignedBy;
    private Instant expiresAt;
    private Instant revokedAt;
    private String revokedBy;
    private String reason;

    protected void initAssignment(String assignedBy, Instant expiresAt, String reason, Instant now) {
        if (expiresAt != null && !expiresAt.isAfter(now)) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "expiresAt must be in the future");
        }
        this.assignedAt = now;
        this.assignedBy = assignedBy;
        this.expiresAt = expiresAt;
        this.reason = reason;
    }

    public void revoke(String actor, String reason, Instant now) {
        if (revokedAt != null) {
            throw new DomainException(ErrorCode.ASSIGNMENT_ALREADY_REVOKED, "Assignment is already revoked");
        }
        this.revokedAt = now;
        this.revokedBy = actor;
        if (reason != null) {
            this.reason = reason;
        }
    }

    public boolean isRevoked() { return revokedAt != null; }

    public boolean isExpired(Instant now) { return expiresAt != null && !expiresAt.isAfter(now); }

    /** Active = not revoked and not expired. */
    public boolean isActive(Instant now) { return !isRevoked() && !isExpired(now); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant v) { this.assignedAt = v; }
    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String v) { this.assignedBy = v; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant v) { this.expiresAt = v; }
    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant v) { this.revokedAt = v; }
    public String getRevokedBy() { return revokedBy; }
    public void setRevokedBy(String v) { this.revokedBy = v; }
    public String getReason() { return reason; }
    public void setReason(String v) { this.reason = v; }
}
