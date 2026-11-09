package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import kh.edu.istad.iam.application.command.ChangePasswordCommand;
import kh.edu.istad.iam.application.port.out.PasswordHasher;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

public final class ChangePasswordUseCase {
        private final UserRepository users;
        private final PasswordHasher passwords;
        private final Clock clock;

        public ChangePasswordUseCase(UserRepository users, PasswordHasher passwords, Clock clock) {
            this.users = users;
            this.passwords = passwords;
            this.clock = clock;
        }

        public void execute(ChangePasswordCommand command) {
            User user = UseCaseSupport.require(users.findByUuid(command.userUuid()), "User");
            if (!passwords.matches(command.currentPassword(), user.getPasswordHash())) {
                throw new DomainException(ErrorCode.AUTHENTICATION_FAILED, "Current password is incorrect");
            }
            user.changePasswordHash(passwords.hash(command.newPassword()), clock.instant());
            users.save(user);
        }
    }
