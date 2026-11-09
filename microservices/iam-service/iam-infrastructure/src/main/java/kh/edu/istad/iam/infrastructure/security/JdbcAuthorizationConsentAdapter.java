package kh.edu.istad.iam.infrastructure.security;

import java.util.Arrays;
import java.util.List;
import kh.edu.istad.iam.application.port.out.AuthorizationConsentRepository;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class JdbcAuthorizationConsentAdapter implements AuthorizationConsentRepository {
    private final JdbcOperations jdbc;
    private final RegisteredClientRepository clients;
    private final OAuth2AuthorizationConsentService consentService;

    public JdbcAuthorizationConsentAdapter(JdbcOperations jdbc, RegisteredClientRepository clients,
                                           OAuth2AuthorizationConsentService consentService) {
        this.jdbc = jdbc;
        this.clients = clients;
        this.consentService = consentService;
    }

    @Override
    public List<ConnectedClient> findByPrincipalName(String principalName) {
        return jdbc.query("select registered_client_id, authorities from oauth2_authorization_consent "
                        + "where principal_name = ? order by registered_client_id",
                (rs, row) -> {
                    RegisteredClient client = clients.findById(rs.getString("registered_client_id"));
                    if (client == null) return null;
                    List<String> scopes = Arrays.stream(rs.getString("authorities").split(","))
                            .map(String::trim).filter(value -> value.startsWith("SCOPE_"))
                            .map(value -> value.substring("SCOPE_".length())).sorted().toList();
                    String name = client.getClientName() == null || client.getClientName().isBlank()
                            ? client.getClientId() : client.getClientName();
                    return new ConnectedClient(client.getClientId(), name, scopes);
                }, principalName).stream().filter(java.util.Objects::nonNull).toList();
    }

    @Override
    @Transactional
    public boolean revoke(String principalName, String clientId) {
        RegisteredClient client = clients.findByClientId(clientId);
        if (client == null) return false;
        OAuth2AuthorizationConsent consent = consentService.findById(client.getId(), principalName);
        if (consent == null) return false;
        consentService.remove(consent);
        return true;
    }
}
