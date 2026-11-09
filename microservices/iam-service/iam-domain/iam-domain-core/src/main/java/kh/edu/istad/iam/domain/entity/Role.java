package kh.edu.istad.iam.domain.entity;

import java.time.Instant;
import java.util.regex.Pattern;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

public class Role extends AuditableDomain {

    public static final String AUTHORITY_PREFIX = "ROLE_";
    private static final Pattern NAME = Pattern.compile("^[A-Z][A-Z0-9_]{1,49}$");

    private String name;
    private String description;
    private boolean system;
    private boolean active = true;

    public Role() { }

    public static Role create(String name, String description) {
        if (name == null || !NAME.matcher(name).matches()) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR,
                    "Role name must be UPPER_SNAKE_CASE (2-50 chars): " + name);
        }
        Role role = new Role();
        role.name = name;
        role.description = description;
        return role;
    }

    public void updateDescription(String description) {
        assertModifiable();
        this.description = description;
    }

    public void activate() { assertModifiable(); this.active = true; }

    public void deactivate() { assertModifiable(); this.active = false; }

    public void delete(String actor, Instant now) {
        assertModifiable();
        markDeleted(actor, now);
        this.active = false;
    }

    /** System roles cannot be changed or deleted. */
    public void assertModifiable() {
        if (system) {
            throw new DomainException(ErrorCode.SYSTEM_ENTITY_PROTECTED,
                    "System role '" + name + "' cannot be changed or deleted");
        }
        if (isDeleted()) {
            throw new DomainException(ErrorCode.ENTITY_DELETED, "Role '" + name + "' is deleted");
        }
    }

    /** Inactive (or deleted) roles cannot be assigned. */
    public void assertAssignable() {
        if (isDeleted()) {
            throw new DomainException(ErrorCode.ENTITY_DELETED, "Role '" + name + "' is deleted");
        }
        if (!active) {
            throw new DomainException(ErrorCode.ENTITY_INACTIVE, "Role '" + name + "' is inactive");
        }
    }

    public boolean isUsable() { return active && !isDeleted(); }

    /** Spring Security authority, e.g. ROLE_ADMIN. */
    public String authority() { return AUTHORITY_PREFIX + name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isSystem() { return system; }
    public void setSystem(boolean system) { this.system = system; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
