package kh.edu.istad.iam.application.usecase.identity;

import java.time.Clock;
import java.util.UUID;
import kh.edu.istad.iam.application.command.FederatedIdentityCommand;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.IdentityResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Identity;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;
import kh.edu.istad.iam.domain.service.IdentityPolicy;

public final class LinkIdentityUseCase {
        private final UserRepository users;
        private final IdentityRepository identities;
        private final CurrentActorProvider actors;
        private final Clock clock;

        public LinkIdentityUseCase(UserRepository users, IdentityRepository identities,
                                   CurrentActorProvider actors, Clock clock) {
            this.users = users;
            this.identities = identities;
            this.actors = actors;
            this.clock = clock;
        }

        public IdentityResult execute(UUID userUuid, FederatedIdentityCommand command) {
            User user = UseCaseSupport.require(users.findByUuid(userUuid), "User");
            assertProviderSubjectAvailable(command);
            IdentityPolicy.assertCanLink(identities.findByUserId(user.getId()), command.provider());
            Identity identity = toIdentity(user.getId(), command);
            identity.setCreatedBy(actors.currentActor());
            return IdentityResult.from(identities.save(identity));
        }

        private void assertProviderSubjectAvailable(FederatedIdentityCommand command) {
            if (identities.findByProviderAndSubject(command.provider(), command.subject()).isPresent()) {
                throw new DomainException(ErrorCode.IDENTITY_ALREADY_LINKED,
                        "This provider account is already linked");
            }
        }

        private Identity toIdentity(Long userId, FederatedIdentityCommand command) {
            return Identity.link(userId, command.provider(), command.subject(), command.email(),
                    command.emailVerified(), command.displayName(), command.username(), command.pictureUrl(),
                    command.attributes(), clock.instant());
        }
    }
