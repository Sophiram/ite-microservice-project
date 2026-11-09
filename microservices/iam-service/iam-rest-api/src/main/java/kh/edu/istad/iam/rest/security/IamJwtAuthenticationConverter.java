package kh.edu.istad.iam.rest.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class IamJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<String> authorities = new LinkedHashSet<>();
        addAll(authorities, jwt.getClaim("roles"), true);
        addAll(authorities, jwt.getClaim("permissions"), false);
        addAll(authorities, jwt.getClaim("authorities"), false);
        List<SimpleGrantedAuthority> mapped = new ArrayList<>(authorities.size());
        authorities.forEach(authority -> mapped.add(new SimpleGrantedAuthority(authority)));
        return new JwtAuthenticationToken(jwt, mapped, jwt.getSubject());
    }

    private static void addAll(Set<String> authorities, Object claim, boolean roleClaim) {
        if (claim instanceof Collection<?> values) {
            for (Object value : values) add(authorities, value, roleClaim);
        } else if (claim != null) {
            add(authorities, claim, roleClaim);
        }
    }

    private static void add(Set<String> authorities, Object claim, boolean roleClaim) {
        if (claim == null) return;
        String value = claim.toString().trim();
        if (value.isEmpty()) return;
        authorities.add(roleClaim && !value.startsWith("ROLE_") ? "ROLE_" + value : value);
    }
}
