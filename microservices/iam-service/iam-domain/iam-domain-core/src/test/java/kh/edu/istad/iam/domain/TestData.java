package kh.edu.istad.iam.domain;

import java.time.Instant;
import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.domain.entity.Role;

public final class TestData {

    public static final Instant NOW = Instant.parse("2026-11-06T00:00:00Z");

    private TestData() { }

    public static Role role(String name) { return Role.create(name, name + " role"); }

    public static Permission perm(String resource, String action) {
        return Permission.create(resource, action, null);
    }
}
