package kh.edu.istad.iam.application.usecase.user;

import java.util.UUID;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.User;

public final class LockUserUseCase {
        private final UserRepository users;
        public LockUserUseCase(UserRepository users) { this.users = users; }
        public void execute(UUID userUuid) {
            User user = UseCaseSupport.require(users.findByUuid(userUuid), "User");
            user.lock();
            users.save(user);
        }
    }
