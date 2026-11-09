package kh.edu.istad.iam.application.usecase.identity;

import java.time.Clock;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Identity;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;
import kh.edu.istad.iam.domain.service.IdentityPolicy;

public final class UnlinkIdentityUseCase {
        private final UserRepository users;
        private final IdentityRepository identities;
        private final CurrentActorProvider actors;
        private final Clock clock;

        public UnlinkIdentityUseCase(UserRepository users, IdentityRepository identities,
                                     CurrentActorProvider actors, Clock clock) {
            this.users = users;
            this.identities = identities;
            this.actors = actors;
            this.clock = clock;
        }

        public void execute(UUID userUuid, UUID identityUuid) {
            User user = UseCaseSupport.require(users.findByUuid(userUuid), "User");
            Identity identity = UseCaseSupport.require(identities.findByUuid(identityUuid), "Identity");
            if (!user.getId().equals(identity.getUserId())) {
                throw new DomainException(ErrorCode.ENTITY_NOT_FOUND, "Identity was not found");
            }
            List<Identity> activeIdentities = identities.findByUserId(user.getId());
            IdentityPolicy.assertCanUnlink(user, activeIdentities);
            identity.unlink(actors.currentActor(), clock.instant());
            identities.save(identity);
        }
    }
