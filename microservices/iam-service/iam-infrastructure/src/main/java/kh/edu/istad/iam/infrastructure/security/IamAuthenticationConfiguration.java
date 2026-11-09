package kh.edu.istad.iam.infrastructure.security;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kh.edu.istad.iam.application.command.FederatedIdentityCommand;
import kh.edu.istad.iam.application.result.AuthUserResult;
import kh.edu.istad.iam.application.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.iam.domain.entity.Provider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration(proxyBeanMethods = false)
public class IamAuthenticationConfiguration {
    @Bean
    UserDetailsService iamUserDetailsService(LoadUserForAuthenticationUseCase loadUser) {
        return login -> {
            AuthUserResult user = loadUser.execute(login);
            return User.withUsername(user.username())
                    .password(user.passwordHash())
                    .authorities(user.authorities().toArray(String[]::new))
                    .disabled(!user.enabled())
                    .accountExpired(!user.accountNonExpired())
                    .accountLocked(!user.accountNonLocked())
                    .credentialsExpired(!user.credentialsNonExpired())
                    .build();
        };
    }

    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> iamOAuth2UserService(
            FindOrProvisionFederatedUserUseCase federatedUsers) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User remote = delegate.loadUser(request);
            String registrationId = request.getClientRegistration().getRegistrationId();
            Provider provider = Provider.fromRegistrationId(registrationId);
            Map<String, Object> attributes = new LinkedHashMap<>(remote.getAttributes());

            if (provider == Provider.GITHUB && (attributes.get("email") == null || attributes.get("email").toString().isBlank())) {
                fetchGitHubEmail(request.getAccessToken().getTokenValue(), attributes);
            }
            if (provider == Provider.FACEBOOK && attributes.get("email") != null && !attributes.get("email").toString().isBlank()) {
                attributes.put("email_verified", true);
            }

            AuthUserResult iamUser = federatedUsers.execute(command(provider, attributes));
            attributes.put("iam_user_uuid", iamUser.uuid().toString());
            attributes.put("iam_username", iamUser.username());
            List<SimpleGrantedAuthority> authorities = authorities(iamUser);
            authorities.add(new SimpleGrantedAuthority("OAUTH2_USER"));
            return new DefaultOAuth2User(authorities, attributes, "iam_user_uuid");
        };
    }

    private static void fetchGitHubEmail(String token, Map<String, Object> attributes) {
        try {
            org.springframework.web.client.RestClient client = org.springframework.web.client.RestClient.builder()
                    .baseUrl("https://api.github.com")
                    .defaultHeader("Authorization", "Bearer " + token)
                    .defaultHeader("Accept", "application/vnd.github+json")
                    .defaultHeader("User-Agent", "istad-iam-service")
                    .build();
            List<Map<String, Object>> emails = client.get()
                    .uri("/user/emails")
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>() {});
            if (emails != null) {
                for (Map<String, Object> entry : emails) {
                    if (Boolean.TRUE.equals(entry.get("primary"))) {
                        attributes.put("email", entry.get("email"));
                        attributes.put("email_verified", Boolean.TRUE.equals(entry.get("verified")));
                        return;
                    }
                }
                if (!emails.isEmpty()) {
                    attributes.put("email", emails.get(0).get("email"));
                    attributes.put("email_verified", Boolean.TRUE.equals(emails.get(0).get("verified")));
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Bean
    OAuth2UserService<OidcUserRequest, OidcUser> iamOidcUserService
            (FindOrProvisionFederatedUserUseCase federatedUsers) {
        OidcUserService delegate = new OidcUserService();
        return request -> {
            OidcUser remote = delegate.loadUser(request);
            Map<String, Object> attributes = new LinkedHashMap<>(remote.getClaims());
            Provider provider = Provider.fromRegistrationId(
                    request.getClientRegistration().getRegistrationId());
            AuthUserResult iamUser = federatedUsers.execute(command(provider, attributes));
            List<SimpleGrantedAuthority> authorities = authorities(iamUser);
            authorities.add(new SimpleGrantedAuthority("OIDC_USER"));
            OidcUserInfo userInfo = new OidcUserInfo(Map.of(
                    "sub", remote.getSubject(),
                    "iam_user_uuid", iamUser.uuid().toString(),
                    "iam_username", iamUser.username()));
            return new DefaultOidcUser(authorities, remote.getIdToken(), userInfo, "iam_user_uuid");
        };
    }

    private static FederatedIdentityCommand command(Provider provider, Map<String, Object> claims) {
        String subject = first(claims, "sub", "id");
        String username = first(claims, "preferred_username", "login", "username", "name");
        String email = first(claims, "email");
        boolean emailVerified = Boolean.TRUE.equals(claims.get("email_verified"));

        String givenName = first(claims, "given_name", "first_name");
        String familyName = first(claims, "family_name", "last_name");
        String displayName = first(claims, "name");
        if (displayName == null) {
            displayName = String.join(" ", List.of(firstNonNull(claims, "given_name", "first_name"),
                    firstNonNull(claims, "family_name", "last_name")).stream().filter(s -> !s.isBlank()).toList());
        }
        if ((givenName == null || givenName.isBlank()) && displayName != null && !displayName.isBlank()) {
            String[] parts = displayName.trim().split("\\s+", 2);
            givenName = parts[0];
            if (parts.length > 1) {
                familyName = parts[1];
            }
        }
        if (displayName == null || displayName.isBlank()) {
            displayName = username != null ? username : subject;
        }

        if (givenName != null && !givenName.isBlank()) {
            claims.put("given_name", givenName);
        }
        if (familyName != null && !familyName.isBlank()) {
            claims.put("family_name", familyName);
        }

        String picture = null;
        Object picObj = claims.get("picture");
        if (picObj instanceof String picStr && !picStr.isBlank()) {
            picture = picStr;
        } else if (picObj instanceof Map<?, ?> pictureMap) {
            Object url = pictureMap.get("url");
            if (url == null && pictureMap.get("data") instanceof Map<?, ?> data) {
                url = data.get("url");
            }
            if (url != null) picture = url.toString();
        }
        if (picture == null) {
            picture = first(claims, "avatar_url", "photo_url");
        }
        if (picture != null) {
            claims.put("picture", picture);
        }

        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Federated provider returned no subject");
        }
        return new FederatedIdentityCommand(provider, subject, username, email, emailVerified,
                displayName, picture, claims);
    }

    private static List<SimpleGrantedAuthority> authorities(AuthUserResult user) {
        return new ArrayList<>(user.authorities().stream().map(SimpleGrantedAuthority::new).toList());
    }

    private static String first(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null && !value.toString().isBlank()) return value.toString();
        }
        return null;
    }

    private static String firstNonNull(Map<String, Object> map, String... keys) {
        String val = first(map, keys);
        return val == null ? "" : val;
    }
}
