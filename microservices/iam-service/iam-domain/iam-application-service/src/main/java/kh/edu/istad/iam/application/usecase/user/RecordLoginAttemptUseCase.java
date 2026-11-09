package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.User;

public final class RecordLoginAttemptUseCase {
        private final UserRepository users;
        private final Clock clock;

        public RecordLoginAttemptUseCase(UserRepository users, Clock clock) {
            this.users = users;
            this.clock = clock;
        }

        public void execute(String usernameOrEmail, boolean success) {
            User user = UseCaseSupport.require(users.findByUsernameOrEmail(usernameOrEmail), "User");
            if (success) user.recordLoginSuccess(clock.instant()); else user.recordLoginFailure();
            users.save(user);
        }
    }
