package kh.edu.istad.iam.infrastructure.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kh.edu.istad.iam.application.result.AuthUserResult;
import kh.edu.istad.iam.application.result.UserClaims;
import kh.edu.istad.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration(proxyBeanMethods = false)
public class IamTokenConfiguration {
    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> iamTokenCustomizer(LoadUserForAuthenticationUseCase loadUser) {
        return context -> {
            Object principal = context.getPrincipal().getPrincipal();
            AuthUserResult user;
            if (principal instanceof UserDetails userDetails) {
                user = loadUser.execute(userDetails.getUsername());
            } else if (principal instanceof OAuth2User oauthUser) {
                String username = oauthUser.getAttribute("iam_username");
                if (username == null || username.isBlank()) return;
                user = loadUser.execute(username);
            } else {
                return;
            }

            context.getClaims().subject(user.uuid().toString());
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                List<String> authorities = user.authorities();
                Set<String> roles = new LinkedHashSet<>();
                Set<String> permissions = new LinkedHashSet<>();
                authorities.forEach(authority -> {
                    if (authority.startsWith("ROLE_")) roles.add(authority.substring("ROLE_".length()));
                    else permissions.add(authority);
                });
                context.getClaims().claim("roles", new ArrayList<>(roles.stream().sorted().toList()));
                context.getClaims().claim("permissions", new ArrayList<>(permissions.stream().sorted().toList()));
                context.getClaims().claim("authorities", new ArrayList<>(authorities.stream().sorted().toList()));
                context.getClaims().claim("preferred_username", user.username());
                return;
            }

            if (!OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) return;
            UserClaims claims = user.claims();
            Set<String> scopes = context.getAuthorizedScopes();
            context.getClaims().claim("preferred_username", user.username());
            if (scopes.contains(OidcScopes.PROFILE)) addProfileClaims(context, claims);
            if (scopes.contains(OidcScopes.EMAIL)) {
                put(context, "email", claims.email());
                context.getClaims().claim("email_verified", claims.emailVerified());
            }
            if (scopes.contains("phone")) {
                put(context, "phone_number", claims.phoneNumber());
                context.getClaims().claim("phone_number_verified", claims.phoneNumberVerified());
            }
            if (scopes.contains(OidcScopes.ADDRESS) && !claims.address().isEmpty()) {
                context.getClaims().claim("address", new LinkedHashMap<>(claims.address()));
            }
        };
    }

    private static void addProfileClaims(JwtEncodingContext context, UserClaims claims) {
        String name = String.join(" ", List.of(nullSafe(claims.givenName()), nullSafe(claims.familyName())))
                .trim();
        put(context, "name", name.isBlank() ? claims.preferredUsername() : name);
        put(context, "given_name", claims.givenName());
        put(context, "family_name", claims.familyName());
        put(context, "middle_name", claims.middleName());
        put(context, "nickname", claims.nickname());
        put(context, "profile", claims.profile());
        put(context, "picture", claims.picture());
        put(context, "website", claims.website());
        put(context, "gender", claims.gender());
        put(context, "birthdate", claims.birthdate());
        put(context, "zoneinfo", claims.zoneinfo());
        put(context, "locale", claims.locale());
        Instant updatedAt = claims.updatedAt();
        if (updatedAt != null) context.getClaims().claim("updated_at", Math.toIntExact(updatedAt.getEpochSecond()));
    }

    private static String nullSafe(String value) { return value == null ? "" : value; }

    private static void put(JwtEncodingContext context, String key, String value) {
        if (value != null && !value.isBlank()) context.getClaims().claim(key, value);
    }
}
