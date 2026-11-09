package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.UserResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.User;

import static kh.edu.istad.iam.application.usecase.user.UserUseCaseSupport.activeUserRoles;

public final class GetUserUseCase {
        private final UserRepository users;
        private final AssignmentRepository assignments;
        private final Clock clock;

        public GetUserUseCase(UserRepository users, AssignmentRepository assignments, Clock clock) {
            this.users = users;
            this.assignments = assignments;
            this.clock = clock;
        }

        public UserResult execute(UUID userUuid) {
            User user = UseCaseSupport.require(users.findByUuid(userUuid), "User");
            return UserResult.from(user, activeUserRoles(user.getId(), assignments, clock));
        }
    }
