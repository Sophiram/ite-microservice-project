package kh.edu.istad.iam.application.usecase.user;

import java.util.UUID;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.User;

public final class EnableUserUseCase {
        private final UserRepository users;
        public EnableUserUseCase(UserRepository users) { this.users = users; }
        public void execute(UUID userUuid) { updateEnabled(userUuid, true); }
        private void updateEnabled(UUID uuid, boolean enabled) {
            User user = UseCaseSupport.require(users.findByUuid(uuid), "User");
            if (enabled) user.enable(); else user.disable();
            users.save(user);
        }
    }
