package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import java.util.List;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.AuthUserResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserRole;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;
import kh.edu.istad.iam.domain.service.AuthorityCalculator;

import static kh.edu.istad.iam.application.usecase.user.UserUseCaseSupport.userClaims;

public final class LoadUserForAuthenticationUseCase {
        private final UserRepository users;
        private final AssignmentRepository assignments;
        private final Clock clock;

        public LoadUserForAuthenticationUseCase(UserRepository users, AssignmentRepository assignments, Clock clock) {
            this.users = users;
            this.assignments = assignments;
            this.clock = clock;
        }

        public AuthUserResult execute(String usernameOrEmail) {
            User user = UseCaseSupport.require(users.findByUsernameOrEmail(usernameOrEmail), "User");
            if (!user.canAuthenticate()) {
                throw new DomainException(ErrorCode.ACCOUNT_DISABLED, "User cannot authenticate");
            }
            List<UserRole> userRoles = assignments.findUserRoles(user.getId());
            List<Long> roleIds = userRoles.stream().map(UserRole::getRole).map(Role::getId).toList();
            var authorities = AuthorityCalculator.calculate(userRoles, assignments.findRolePermissions(roleIds),
                    assignments.findUserPermissions(user.getId()), clock.instant());
            return new AuthUserResult(user.getId(), user.getUuid(), user.getUsername(),
                    user.getPasswordHash(), user.isEnabled(), user.isAccountNonExpired(), user.isAccountNonLocked(),
                    user.isCredentialsNonExpired(), authorities.authorities(), userClaims(user));
        }
    }
