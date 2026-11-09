package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.User;

public final class DeleteUserUseCase {
        private final UserRepository users;
        private final CurrentActorProvider actors;
        private final Clock clock;

        public DeleteUserUseCase(UserRepository users, CurrentActorProvider actors, Clock clock) {
            this.users = users;
            this.actors = actors;
            this.clock = clock;
        }

        public void execute(UUID userUuid) {
            User user = UseCaseSupport.require(users.findByUuid(userUuid), "User");
            user.delete(actors.currentActor(), clock.instant());
            users.save(user);
        }
    }
