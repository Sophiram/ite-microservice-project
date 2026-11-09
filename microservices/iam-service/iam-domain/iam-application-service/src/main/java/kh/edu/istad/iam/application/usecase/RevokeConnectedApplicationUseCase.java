package kh.edu.istad.iam.application.usecase;

import kh.edu.istad.iam.application.port.out.AuthorizationConsentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;

public final class RevokeConnectedApplicationUseCase {
        private final CurrentActorProvider actors;
        private final AuthorizationConsentRepository consents;

        public RevokeConnectedApplicationUseCase(CurrentActorProvider actors,
                                                 AuthorizationConsentRepository consents) {
            this.actors = actors;
            this.consents = consents;
        }

        public boolean execute(String clientId) {
            return consents.revoke(actors.currentActor(), clientId);
        }
    }
