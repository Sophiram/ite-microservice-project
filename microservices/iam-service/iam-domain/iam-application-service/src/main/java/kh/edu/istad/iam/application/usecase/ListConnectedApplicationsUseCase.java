package kh.edu.istad.iam.application.usecase;

import java.util.List;
import kh.edu.istad.iam.application.port.out.AuthorizationConsentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.result.ConnectedApplicationResult;

public final class ListConnectedApplicationsUseCase {
        private final CurrentActorProvider actors;
        private final AuthorizationConsentRepository consents;

        public ListConnectedApplicationsUseCase(CurrentActorProvider actors,
                                                AuthorizationConsentRepository consents) {
            this.actors = actors;
            this.consents = consents;
        }

        public List<ConnectedApplicationResult> execute() {
            return consents.findByPrincipalName(actors.currentActor()).stream()
                    .map(client -> new ConnectedApplicationResult(client.clientId(), client.clientName(),
                            client.scopes())).toList();
        }
    }
