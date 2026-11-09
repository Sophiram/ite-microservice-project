package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import kh.edu.istad.iam.application.command.ResetUserPasswordCommand;
import kh.edu.istad.iam.application.port.out.PasswordHasher;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.User;

public final class ResetUserPasswordUseCase {
        private final UserRepository users;
        private final PasswordHasher passwords;
        private final Clock clock;

        public ResetUserPasswordUseCase(UserRepository users, PasswordHasher passwords, Clock clock) {
            this.users = users;
            this.passwords = passwords;
            this.clock = clock;
        }

        public void execute(ResetUserPasswordCommand command) {
            User user = UseCaseSupport.require(users.findByUuid(command.userUuid()), "User");
            user.changePasswordHash(passwords.hash(command.newPassword()), clock.instant());
            users.save(user);
        }
    }
