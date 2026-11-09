package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.PageResult;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.query.UserFilterQuery;
import kh.edu.istad.iam.application.result.UserResult;
import kh.edu.istad.iam.domain.entity.User;

import static kh.edu.istad.iam.application.usecase.user.UserUseCaseSupport.activeUserRoles;

public final class ListUsersUseCase {
        private final UserRepository users;
        private final AssignmentRepository assignments;
        private final Clock clock;

        public ListUsersUseCase(UserRepository users, AssignmentRepository assignments, Clock clock) {
            this.users = users;
            this.assignments = assignments;
            this.clock = clock;
        }

        public PageResult<UserResult> execute(UserFilterQuery query) {
            PageResult<User> page = users.findAll(query.page());
            return new PageResult<>(page.content().stream()
                    .map(user -> UserResult.from(user,
                            activeUserRoles(user.getId(), assignments, clock))).toList(),
                    page.page(), page.size(), page.totalElements());
        }
    }
