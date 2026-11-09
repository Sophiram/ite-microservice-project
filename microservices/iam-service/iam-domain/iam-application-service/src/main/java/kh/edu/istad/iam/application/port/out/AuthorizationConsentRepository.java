package kh.edu.istad.iam.application.port.out;

import java.util.List;

public interface AuthorizationConsentRepository {
    List<ConnectedClient> findByPrincipalName(String principalName);
    boolean revoke(String principalName, String clientId);

    record ConnectedClient(String clientId, String clientName, List<String> scopes) {
        public ConnectedClient { scopes = List.copyOf(scopes); }
    }
}
