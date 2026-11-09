package kh.edu.istad.iam.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Result of the effective-permission calculation (B.6). */
public record UserAuthorities(
        Set<String> roleNames,
        Set<String> directGrants,
        Set<String> directDenies,
        Set<String> effectivePermissions) {

    /** Authorities for Spring Security: ROLE_x for roles + permission names. */
    public List<String> authorities() {
        List<String> all = new ArrayList<>();
        roleNames.stream().sorted().forEach(r -> all.add(Role.AUTHORITY_PREFIX + r));
        effectivePermissions.stream().sorted().forEach(all::add);
        return all;
    }
}
