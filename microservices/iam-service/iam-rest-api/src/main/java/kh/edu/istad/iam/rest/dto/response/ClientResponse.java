package kh.edu.istad.iam.rest.dto.response;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@Builder
public record ClientResponse(
        String id,
        String clientId,
        String name,
        String type,
        List<String> grantTypes,
        List<String> redirectUris,
        List<String> signoutUris,
        List<String> scopes,
        boolean consent,
        String registered,
        String clientSecret
) {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MMM d, yyyy").withZone(ZoneId.systemDefault());

    public static ClientResponse from(RegisteredClient client, String rawSecret) {
        boolean isPublic = client.getClientAuthenticationMethods().contains(ClientAuthenticationMethod.NONE);
        String type = isPublic ? "Public" : "Confidential";

        List<String> grants = new ArrayList<>();
        if (client.getAuthorizationGrantTypes() != null) {
            for (AuthorizationGrantType gt : client.getAuthorizationGrantTypes()) {
                if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(gt)) {
                    grants.add("Authorization code");
                } else if (AuthorizationGrantType.REFRESH_TOKEN.equals(gt)) {
                    grants.add("Refresh token");
                } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(gt)) {
                    grants.add("Client credentials");
                } else {
                    grants.add(gt.getValue());
                }
            }
        }

        List<String> redirects = client.getRedirectUris() == null ? List.of() : new ArrayList<>(client.getRedirectUris());
        List<String> postLogout = client.getPostLogoutRedirectUris() == null ? List.of() : new ArrayList<>(client.getPostLogoutRedirectUris());
        List<String> scopes = client.getScopes() == null ? List.of() : new ArrayList<>(client.getScopes());
        boolean consent = client.getClientSettings() != null && client.getClientSettings().isRequireAuthorizationConsent();

        String registered = client.getClientIdIssuedAt() != null ? FORMATTER.format(client.getClientIdIssuedAt()) : "Recently";

        return ClientResponse.builder()
                .id(client.getId())
                .clientId(client.getClientId())
                .name(client.getClientName())
                .type(type)
                .grantTypes(grants)
                .redirectUris(redirects)
                .signoutUris(postLogout)
                .scopes(scopes)
                .consent(consent)
                .registered(registered)
                .clientSecret(rawSecret)
                .build();
    }

    public static ClientResponse from(RegisteredClient client) {
        return from(client, null);
    }
}
