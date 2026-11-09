package kh.edu.istad.iam.application.usecase.identity;

import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.IdentityResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.User;

public final class ListUserIdentitiesUseCase {
        private final UserRepository users;
        private final IdentityRepository identities;

        public ListUserIdentitiesUseCase(UserRepository users, IdentityRepository identities) {
            this.users = users;
            this.identities = identities;
        }

        public List<IdentityResult> execute(UUID userUuid) {
            User user = UseCaseSupport.require(users.findByUuid(userUuid), "User");
            return identities.findByUserId(user.getId()).stream()
                    .map(IdentityResult::from).toList();
        }
    }
