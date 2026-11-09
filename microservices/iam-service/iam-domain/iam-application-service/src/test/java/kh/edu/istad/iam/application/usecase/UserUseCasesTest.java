package kh.edu.istad.iam.application.usecase;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import kh.edu.istad.iam.application.command.RegisterUserCommand;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.PasswordHasher;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.iam.application.usecase.user.RecordLoginAttemptUseCase;
import kh.edu.istad.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.iam.domain.entity.Effect;
import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.domain.entity.Profile;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.domain.entity.RolePermission;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserPermission;
import kh.edu.istad.iam.domain.entity.UserRole;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserUseCasesTest {
    private final UserRepository users = mock(UserRepository.class);
    private final PasswordHasher passwords = mock(PasswordHasher.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void registrationNormalizesEmailAndStoresHashedPassword() {
        when(users.existsByUsername("alex")).thenReturn(false);
        when(users.existsByEmail("alex@example.com")).thenReturn(false);
        when(passwords.hash("secret")).thenReturn("hashed-secret");
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = new RegisterUserUseCase(users, passwords, clock).execute(
                new RegisterUserCommand("alex", "  Alex@Example.com ", "secret",
                        Profile.of("Alex", "Kim", "en")));

        assertEquals("alex@example.com", result.email());
        verify(passwords).hash("secret");
        verify(users).save(org.mockito.ArgumentMatchers.argThat(user ->
                "hashed-secret".equals(user.getPasswordHash())
                        && Instant.parse("2026-01-01T00:00:00Z").equals(user.getPasswordChangedAt())));
    }

    @Test
    void registrationRejectsDuplicateUsernameBeforeHashingPassword() {
        when(users.existsByUsername("alex")).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class,
                () -> new RegisterUserUseCase(users, passwords, clock).execute(
                        new RegisterUserCommand("alex", "alex@example.com", "secret", null)));

        assertEquals(ErrorCode.DUPLICATE_ENTITY, exception.getCode());
        org.mockito.Mockito.verifyNoInteractions(passwords);
    }

    @Test
    void authenticationReturnsRoleAndEffectivePermissionAuthorities() {
        AssignmentRepository assignments = mock(AssignmentRepository.class);
        User user = User.register("alex", "alex@example.com", "hash",
                Profile.of("Alex", "Kim", "en"), clock.instant());
        user.setId(10L);
        Role role = Role.create("READER", "Read-only access");
        role.setId(20L);
        Permission permission = Permission.create("user", "read", "Read users");
        permission.setId(30L);
        UserRole userRole = UserRole.assign(user.getId(), role, "test", null, null, clock.instant());
        RolePermission rolePermission = RolePermission.assign(role, permission, "test", null,
                null, clock.instant());
        UserPermission deny = UserPermission.assign(user.getId(), permission, Effect.DENY,
                "test", null, null, clock.instant());

        when(users.findByUsernameOrEmail("alex")).thenReturn(java.util.Optional.of(user));
        when(assignments.findUserRoles(user.getId())).thenReturn(java.util.List.of(userRole));
        when(assignments.findRolePermissions(java.util.List.of(role.getId())))
                .thenReturn(java.util.List.of(rolePermission));
        when(assignments.findUserPermissions(user.getId())).thenReturn(java.util.List.of(deny));

        var result = new LoadUserForAuthenticationUseCase(users, assignments, clock).execute("alex");

        assertEquals(java.util.List.of("ROLE_READER"), result.authorities());
    }

    @Test
    void fifthFailedLoginLocksAndPersistsTheAccount() {
        User user = User.register("alex", "alex@example.com", "hash", null, clock.instant());
        user.setFailedLoginAttempts(4);
        when(users.findByUsernameOrEmail("alex")).thenReturn(java.util.Optional.of(user));
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        new RecordLoginAttemptUseCase(users, clock).execute("alex", false);

        assertEquals(5, user.getFailedLoginAttempts());
        org.junit.jupiter.api.Assertions.assertFalse(user.isAccountNonLocked());
        verify(users).save(user);
    }
}
