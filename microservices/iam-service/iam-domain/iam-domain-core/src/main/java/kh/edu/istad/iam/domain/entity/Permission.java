package kh.edu.istad.iam.domain.entity;

import java.time.Instant;
import java.util.regex.Pattern;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

public class Permission extends AuditableDomain {

    private static final Pattern PART = Pattern.compile("^(?:\\*|[a-z][a-z0-9_-]{0,49})$");

    private String name;      // resource:action
    private String resource;
    private String action;
    private String description;
    private boolean system;
    private boolean active = true;

    public Permission() { }

    public static Permission create(String resource, String action, String description) {
        String r = resource == null ? null : resource.trim().toLowerCase(java.util.Locale.ROOT);
        String a = action == null ? null : action.trim().toLowerCase(java.util.Locale.ROOT);
        if (r == null || !PART.matcher(r).matches() || a == null || !PART.matcher(a).matches()) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR,
                    "Permission resource and action must be lower-case words (e.g. user / read)");
        }
        Permission p = new Permission();
        p.resource = r;
        p.action = a;
        p.name = nameOf(r, a);
        p.description = description;
        return p;
    }

    public static String nameOf(String resource, String action) {
        return resource + ":" + action;
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

    public void assertModifiable() {
        if (system) {
            throw new DomainException(ErrorCode.SYSTEM_ENTITY_PROTECTED,
                    "System permission '" + name + "' cannot be changed or deleted");
        }
        if (isDeleted()) {
            throw new DomainException(ErrorCode.ENTITY_DELETED, "Permission '" + name + "' is deleted");
        }
    }

    public void assertAssignable() {
        if (isDeleted()) {
            throw new DomainException(ErrorCode.ENTITY_DELETED, "Permission '" + name + "' is deleted");
        }
        if (!active) {
            throw new DomainException(ErrorCode.ENTITY_INACTIVE, "Permission '" + name + "' is inactive");
        }
    }

    public boolean isUsable() { return active && !isDeleted(); }

    /** Spring Security authority = the permission name itself, e.g. user:read. */
    public String authority() { return name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isSystem() { return system; }
    public void setSystem(boolean system) { this.system = system; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
