package kh.edu.istad.iam.application.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserRole;

public record UserResult(Long id, UUID uuid, String username, String email, boolean emailVerified,
                             boolean enabled, boolean accountNonLocked, String fullName, String locale,
                             UserProfile profile, AddressResult address, List<RoleSummary> roles,
                             Instant createdAt, Instant updatedAt) {
        public UserResult {
            roles = List.copyOf(roles);
        }

        public static UserResult from(User user) {
            return new UserResult(user.getId(), user.getUuid(), user.getUsername(), user.getEmail(),
                    user.isEmailVerified(), user.isEnabled(), user.isAccountNonLocked(), user.fullName(),
                    user.getProfile() == null || user.getProfile().locale() == null
                            ? "en" : user.getProfile().locale(), UserProfile.from(user), AddressResult.from(user),
                    List.of(), user.getCreatedAt(), user.getUpdatedAt());
        }

        public static UserResult from(User user, List<UserRole> userRoles) {
            List<RoleSummary> roles = userRoles.stream().filter(assignment -> !assignment.isRevoked())
                    .map(UserRole::getRole).filter(role -> role.isActive() && !role.isDeleted())
                    .map(role -> new RoleSummary(role.getUuid(), role.getName())).distinct().toList();
            UserResult basic = from(user);
            return new UserResult(basic.id(), basic.uuid(), basic.username(), basic.email(), basic.emailVerified(),
                    basic.enabled(), basic.accountNonLocked(), basic.fullName(), basic.locale(),
                    basic.profile(), basic.address(), roles,
                    basic.createdAt(), basic.updatedAt());
        }
    }
