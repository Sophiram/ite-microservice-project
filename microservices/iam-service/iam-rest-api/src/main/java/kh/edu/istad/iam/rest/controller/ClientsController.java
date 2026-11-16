package kh.edu.istad.iam.rest.controller;

import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.rest.dto.request.CreateClientRequest;
import kh.edu.istad.iam.rest.dto.request.UpdateClientRequest;
import kh.edu.istad.iam.rest.dto.response.ClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('role:*') or isAuthenticated()")
public class ClientsController {

    private final RegisteredClientRepository clients;
    private final JdbcOperations jdbc;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<ClientResponse>> listClients() {
        List<String> ids = jdbc.queryForList(
                "SELECT id FROM oauth2_registered_client ORDER BY client_id_issued_at DESC",
                String.class
        );
        List<ClientResponse> results = new ArrayList<>();
        for (String id : ids) {
            try {
                RegisteredClient client = clients.findById(id);
                if (client != null) {
                    results.add(ClientResponse.from(client));
                }
            } catch (Exception e) {
                log.warn("Failed to load registered client id={}: {}", id, e.getMessage());
            }
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable String id) {
        RegisteredClient client = clients.findById(id);
        if (client == null) {
            client = clients.findByClientId(id);
        }
        if (client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found: " + id);
        }
        return ResponseEntity.ok(ClientResponse.from(client));
    }

    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody CreateClientRequest body) {
        String clientId = body.clientId().trim();
        if (clients.findByClientId(clientId) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client ID already exists: " + clientId);
        }

        boolean isPublic = "Public".equalsIgnoreCase(body.type());
        String rawSecret = null;
        String encodedSecret = null;

        if (!isPublic) {
            if (body.clientSecret() != null && !body.clientSecret().isBlank()) {
                rawSecret = body.clientSecret().trim();
            } else {
                rawSecret = "sec_" + UUID.randomUUID().toString().replace("-", "");
            }
            encodedSecret = passwordEncoder.encode(rawSecret);
        }

        RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientName(body.name().trim())
                .clientIdIssuedAt(Instant.now());

        if (isPublic) {
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(true)
                            .requireAuthorizationConsent(Boolean.TRUE.equals(body.consent()))
                            .build());
        } else {
            builder.clientSecret(encodedSecret)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(false)
                            .requireAuthorizationConsent(Boolean.TRUE.equals(body.consent()))
                            .build());
        }

        boolean hasAuthCode = false;
        boolean hasAnyGrant = false;
        if (body.grantTypes() != null && !body.grantTypes().isEmpty()) {
            for (String gt : body.grantTypes()) {
                AuthorizationGrantType grantType = parseGrantType(gt);
                if (grantType != null) {
                    if (isPublic && AuthorizationGrantType.CLIENT_CREDENTIALS.equals(grantType)) {
                        continue; // Public clients cannot use client_credentials
                    }
                    builder.authorizationGrantType(grantType);
                    hasAnyGrant = true;
                    if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(grantType)) {
                        hasAuthCode = true;
                    }
                }
            }
        }

        if (!hasAnyGrant) {
            builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
            builder.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
            hasAuthCode = true;
        }

        boolean hasRedirectUri = false;
        if (body.redirectUris() != null) {
            for (String uri : body.redirectUris()) {
                if (uri != null && !uri.isBlank()) {
                    String cleanUri = uri.trim();
                    if (!cleanUri.contains("://")) {
                        cleanUri = "http://" + cleanUri;
                    }
                    builder.redirectUri(cleanUri);
                    hasRedirectUri = true;
                }
            }
        }

        // Spring Authorization Server strictly requires at least one redirectUri for authorization_code
        if (hasAuthCode && !hasRedirectUri) {
            builder.redirectUri("http://localhost:3000/callback");
        }

        if (body.signoutUris() != null) {
            for (String uri : body.signoutUris()) {
                if (uri != null && !uri.isBlank()) {
                    String cleanUri = uri.trim();
                    if (!cleanUri.contains("://")) {
                        cleanUri = "http://" + cleanUri;
                    }
                    builder.postLogoutRedirectUri(cleanUri);
                }
            }
        }

        if (body.scopes() != null && !body.scopes().isEmpty()) {
            for (String scope : body.scopes()) {
                if (scope != null && !scope.isBlank()) {
                    builder.scope(scope.trim());
                }
            }
        } else {
            builder.scope(OidcScopes.OPENID).scope(OidcScopes.PROFILE);
        }

        builder.tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMinutes(15))
                .refreshTokenTimeToLive(Duration.ofDays(14))
                .reuseRefreshTokens(false)
                .build());

        RegisteredClient saved;
        try {
            saved = builder.build();
            clients.save(saved);
        } catch (IllegalArgumentException e) {
            log.error("Failed to register client {}: {}", clientId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to save client {}: {}", clientId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to register client: " + e.getMessage());
        }
        log.info("Registered new OAuth2 client: clientId={}, type={}, public={}", clientId, body.type(), isPublic);

        return ResponseEntity.status(HttpStatus.CREATED).body(ClientResponse.from(saved, rawSecret));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable String id,
            @RequestBody UpdateClientRequest body) {
        RegisteredClient existing = clients.findById(id);
        if (existing == null) {
            existing = clients.findByClientId(id);
        }
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found: " + id);
        }

        RegisteredClient.Builder builder = RegisteredClient.from(existing);

        if (body.name() != null && !body.name().isBlank()) {
            builder.clientName(body.name().trim());
        }

        if (body.clientId() != null && !body.clientId().isBlank() && !body.clientId().trim().equals(existing.getClientId())) {
            String newClientId = body.clientId().trim();
            RegisteredClient byNew = clients.findByClientId(newClientId);
            if (byNew != null && !byNew.getId().equals(existing.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Client ID already in use: " + newClientId);
            }
            builder.clientId(newClientId);
        }

        boolean consent = body.consent() != null
                ? body.consent()
                : (existing.getClientSettings() != null && existing.getClientSettings().isRequireAuthorizationConsent());

        if (body.type() != null) {
            boolean isPublic = "Public".equalsIgnoreCase(body.type());
            if (isPublic) {
                builder.clientAuthenticationMethods(methods -> {
                    methods.clear();
                    methods.add(ClientAuthenticationMethod.NONE);
                });
                builder.clientSecret(null);
                builder.clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(consent)
                        .build());
            } else {
                builder.clientAuthenticationMethods(methods -> {
                    methods.clear();
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                });
                if (body.clientSecret() != null && !body.clientSecret().isBlank()) {
                    builder.clientSecret(passwordEncoder.encode(body.clientSecret().trim()));
                }
                builder.clientSettings(ClientSettings.builder()
                        .requireProofKey(false)
                        .requireAuthorizationConsent(consent)
                        .build());
            }
        } else if (body.consent() != null) {
            boolean isPublic = existing.getClientAuthenticationMethods().contains(ClientAuthenticationMethod.NONE);
            builder.clientSettings(ClientSettings.builder()
                    .requireProofKey(isPublic)
                    .requireAuthorizationConsent(consent)
                    .build());
        }

        if (body.grantTypes() != null) {
            builder.authorizationGrantTypes(grants -> {
                grants.clear();
                for (String gt : body.grantTypes()) {
                    AuthorizationGrantType grantType = parseGrantType(gt);
                    if (grantType != null) {
                        grants.add(grantType);
                    }
                }
            });
        }

        if (body.redirectUris() != null) {
            builder.redirectUris(uris -> {
                uris.clear();
                for (String uri : body.redirectUris()) {
                    if (uri != null && !uri.isBlank()) {
                        uris.add(uri.trim());
                    }
                }
            });
        }

        if (body.signoutUris() != null) {
            builder.postLogoutRedirectUris(uris -> {
                uris.clear();
                for (String uri : body.signoutUris()) {
                    if (uri != null && !uri.isBlank()) {
                        uris.add(uri.trim());
                    }
                }
            });
        }

        if (body.scopes() != null) {
            builder.scopes(scopes -> {
                scopes.clear();
                for (String sc : body.scopes()) {
                    if (sc != null && !sc.isBlank()) {
                        scopes.add(sc.trim());
                    }
                }
            });
        }

        RegisteredClient updated;
        try {
            updated = builder.build();
            clients.save(updated);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        log.info("Updated OAuth2 client id={}, clientId={}", updated.getId(), updated.getClientId());

        return ResponseEntity.ok(ClientResponse.from(updated));
    }

    @PostMapping("/{id}/rotate-secret")
    public ResponseEntity<ClientResponse> rotateSecret(@PathVariable String id) {
        RegisteredClient existing = clients.findById(id);
        if (existing == null) {
            existing = clients.findByClientId(id);
        }
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found: " + id);
        }
        String newSecret = "sec_" + UUID.randomUUID().toString().replace("-", "");
        RegisteredClient updated = RegisteredClient.from(existing)
                .clientSecret(passwordEncoder.encode(newSecret))
                .build();
        clients.save(updated);
        log.info("Rotated secret for client id={}, clientId={}", id, existing.getClientId());
        return ResponseEntity.ok(ClientResponse.from(updated, newSecret));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable String id) {
        int rows = jdbc.update("DELETE FROM oauth2_registered_client WHERE id = ? OR client_id = ?", id, id);
        log.info("Deleted OAuth2 client id={}: {} row(s) deleted", id, rows);
        return ResponseEntity.noContent().build();
    }

    private AuthorizationGrantType parseGrantType(String gt) {
        if (gt == null) return null;
        String val = gt.trim().toLowerCase();
        if (val.contains("code") || val.contains("authorization")) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }
        if (val.contains("refresh")) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        if (val.contains("credential")) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        }
        return new AuthorizationGrantType(gt.trim());
    }
}
