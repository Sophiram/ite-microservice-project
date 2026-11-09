package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.DirectPermission;
import kh.edu.istad.iam.application.result.UserAuthoritiesResult;
import kh.edu.istad.iam.application.result.UserRoleAuthority;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserRole;
import kh.edu.istad.iam.domain.service.AuthorityCalculator;

public final class GetUserAuthoritiesUseCase {
        private final UserRepository users;
        private final AssignmentRepository assignments;
        private final Clock clock;

        public GetUserAuthoritiesUseCase(UserRepository users, AssignmentRepository assignments, Clock clock) {
            this.users = users;
            this.assignments = assignments;
            this.clock = clock;
        }

        public UserAuthoritiesResult execute(UUID userUuid) {
            User user = UseCaseSupport.require(users.findByUuid(userUuid), "User");
            var userRoles = assignments.findUserRoles(user.getId());
            List<Long> roleIds = userRoles.stream().map(UserRole::getRole).map(Role::getId).toList();
            var rolePermissions = assignments.findRolePermissions(roleIds);
            var userPermissions = assignments.findUserPermissions(user.getId());
            var effective = AuthorityCalculator.calculate(userRoles, rolePermissions, userPermissions, clock.instant());
            List<UserRoleAuthority> roles = userRoles.stream()
                    .filter(assignment -> assignment.isActive(clock.instant()))
                    .map(assignment -> new UserRoleAuthority(assignment.getRole().getUuid(),
                            assignment.getRole().getName(), assignment.getExpiresAt())).toList();
            List<DirectPermission> direct = userPermissions.stream()
                    .filter(assignment -> assignment.isActive(clock.instant()))
                    .map(assignment -> new DirectPermission(assignment.getPermission().getName(),
                            assignment.getEffect())).toList();
            return new UserAuthoritiesResult(roles, direct,
                    effective.effectivePermissions().stream().sorted().toList());
        }
    }
