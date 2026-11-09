package kh.edu.istad.iam;

import java.time.Clock;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.AuthorizationConsentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.application.port.out.PasswordHasher;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.permission.ActivatePermissionUseCase;
import kh.edu.istad.iam.application.usecase.role.ActivateRoleUseCase;
import kh.edu.istad.iam.application.usecase.role.AssignPermissionsToRoleUseCase;
import kh.edu.istad.iam.application.usecase.user.AssignRolesToUserUseCase;
import kh.edu.istad.iam.application.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.iam.application.usecase.permission.CreatePermissionUseCase;
import kh.edu.istad.iam.application.usecase.role.CreateRoleUseCase;
import kh.edu.istad.iam.application.usecase.user.CreateUserUseCase;
import kh.edu.istad.iam.application.usecase.permission.DeactivatePermissionUseCase;
import kh.edu.istad.iam.application.usecase.role.DeactivateRoleUseCase;
import kh.edu.istad.iam.application.usecase.permission.DeletePermissionUseCase;
import kh.edu.istad.iam.application.usecase.role.DeleteRoleUseCase;
import kh.edu.istad.iam.application.usecase.user.DeleteUserUseCase;
import kh.edu.istad.iam.application.usecase.user.DisableUserUseCase;
import kh.edu.istad.iam.application.usecase.user.EnableUserUseCase;
import kh.edu.istad.iam.application.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.iam.application.usecase.permission.GetPermissionUseCase;
import kh.edu.istad.iam.application.usecase.role.GetRoleUseCase;
import kh.edu.istad.iam.application.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.iam.application.usecase.user.GetUserUseCase;
import kh.edu.istad.iam.application.usecase.user.GrantPermissionsToUserUseCase;
import kh.edu.istad.iam.application.usecase.identity.LinkIdentityUseCase;
import kh.edu.istad.iam.application.usecase.ListConnectedApplicationsUseCase;
import kh.edu.istad.iam.application.usecase.permission.ListPermissionsUseCase;
import kh.edu.istad.iam.application.usecase.role.ListRolesUseCase;
import kh.edu.istad.iam.application.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.iam.application.usecase.user.ListUsersUseCase;
import kh.edu.istad.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.iam.application.usecase.user.LockUserUseCase;
import kh.edu.istad.iam.application.usecase.user.RecordLoginAttemptUseCase;
import kh.edu.istad.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.iam.application.usecase.user.ResetUserPasswordUseCase;
import kh.edu.istad.iam.application.usecase.RevokeConnectedApplicationUseCase;
import kh.edu.istad.iam.application.usecase.role.RevokePermissionFromRoleUseCase;
import kh.edu.istad.iam.application.usecase.user.RevokePermissionFromUserUseCase;
import kh.edu.istad.iam.application.usecase.user.RevokeRoleFromUserUseCase;
import kh.edu.istad.iam.application.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.iam.application.usecase.user.UnlockUserUseCase;
import kh.edu.istad.iam.application.usecase.permission.UpdatePermissionUseCase;
import kh.edu.istad.iam.application.usecase.role.UpdateRoleUseCase;
import kh.edu.istad.iam.application.usecase.user.UpdateUserProfileUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamApplicationConfiguration {
    @Bean
    Clock iamClock() { return Clock.systemUTC(); }

    @Bean
    ListConnectedApplicationsUseCase listConnectedApplicationsUseCase(
            CurrentActorProvider actors, AuthorizationConsentRepository consents) {
        return new ListConnectedApplicationsUseCase(actors, consents);
    }

    @Bean
    RevokeConnectedApplicationUseCase revokeConnectedApplicationUseCase(
            CurrentActorProvider actors, AuthorizationConsentRepository consents) {
        return new RevokeConnectedApplicationUseCase(actors, consents);
    }

    @Bean
    RegisterUserUseCase registerUserUseCase(
            UserRepository users, PasswordHasher passwords, RoleRepository roles,
            AssignmentRepository assignments, CurrentActorProvider actors, Clock iamClock) {
        return new RegisterUserUseCase(users, passwords, roles, assignments, actors, iamClock);
    }

    @Bean
    CreateUserUseCase createUserUseCase(
            UserRepository users, RoleRepository roles, AssignmentRepository assignments,
            PasswordHasher passwords, CurrentActorProvider actors, Clock iamClock) {
        return new CreateUserUseCase(users, roles, assignments, passwords, actors, iamClock);
    }

    @Bean
    GetUserUseCase getUserUseCase(
            UserRepository users, AssignmentRepository assignments, Clock iamClock) {
        return new GetUserUseCase(users, assignments, iamClock);
    }

    @Bean
    ListUsersUseCase listUsersUseCase(
            UserRepository users, AssignmentRepository assignments, Clock iamClock) {
        return new ListUsersUseCase(users, assignments, iamClock);
    }

    @Bean
    UpdateUserProfileUseCase updateUserProfileUseCase(UserRepository users) {
        return new UpdateUserProfileUseCase(users);
    }

    @Bean
    ChangePasswordUseCase changePasswordUseCase(
            UserRepository users, PasswordHasher passwords, Clock iamClock) {
        return new ChangePasswordUseCase(users, passwords, iamClock);
    }

    @Bean
    ResetUserPasswordUseCase resetUserPasswordUseCase(
            UserRepository users, PasswordHasher passwords, Clock iamClock) {
        return new ResetUserPasswordUseCase(users, passwords, iamClock);
    }

    @Bean
    EnableUserUseCase enableUserUseCase(UserRepository users) {
        return new EnableUserUseCase(users);
    }

    @Bean
    DisableUserUseCase disableUserUseCase(UserRepository users) {
        return new DisableUserUseCase(users);
    }

    @Bean
    LockUserUseCase lockUserUseCase(UserRepository users) {
        return new LockUserUseCase(users);
    }

    @Bean
    UnlockUserUseCase unlockUserUseCase(UserRepository users) {
        return new UnlockUserUseCase(users);
    }

    @Bean
    DeleteUserUseCase deleteUserUseCase(
            UserRepository users, CurrentActorProvider actors, Clock iamClock) {
        return new DeleteUserUseCase(users, actors, iamClock);
    }

    @Bean
    AssignRolesToUserUseCase assignRolesToUserUseCase(
            UserRepository users, RoleRepository roles, AssignmentRepository assignments,
            CurrentActorProvider actors, Clock iamClock) {
        return new AssignRolesToUserUseCase(users, roles, assignments, actors, iamClock);
    }

    @Bean
    RevokeRoleFromUserUseCase revokeRoleFromUserUseCase(
            UserRepository users, RoleRepository roles, AssignmentRepository assignments,
            CurrentActorProvider actors, Clock iamClock) {
        return new RevokeRoleFromUserUseCase(users, roles, assignments, actors, iamClock);
    }

    @Bean
    GrantPermissionsToUserUseCase grantPermissionsToUserUseCase(
            UserRepository users, PermissionRepository permissions, AssignmentRepository assignments,
            CurrentActorProvider actors, Clock iamClock) {
        return new GrantPermissionsToUserUseCase(users, permissions, assignments, actors, iamClock);
    }

    @Bean
    RevokePermissionFromUserUseCase revokePermissionFromUserUseCase(
            UserRepository users, PermissionRepository permissions, AssignmentRepository assignments,
            CurrentActorProvider actors, Clock iamClock) {
        return new RevokePermissionFromUserUseCase(
                users, permissions, assignments, actors, iamClock);
    }

    @Bean
    GetUserAuthoritiesUseCase getUserAuthoritiesUseCase(
            UserRepository users, AssignmentRepository assignments, Clock iamClock) {
        return new GetUserAuthoritiesUseCase(users, assignments, iamClock);
    }

    @Bean
    LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase(
            UserRepository users, AssignmentRepository assignments, Clock iamClock) {
        return new LoadUserForAuthenticationUseCase(users, assignments, iamClock);
    }

    @Bean
    RecordLoginAttemptUseCase recordLoginAttemptUseCase(UserRepository users, Clock iamClock) {
        return new RecordLoginAttemptUseCase(users, iamClock);
    }

    @Bean
    CreateRoleUseCase createRoleUseCase(
            RoleRepository roles, AssignPermissionsToRoleUseCase assignPermissions) {
        return new CreateRoleUseCase(roles, assignPermissions);
    }

    @Bean
    GetRoleUseCase getRoleUseCase(RoleRepository roles) {
        return new GetRoleUseCase(roles);
    }

    @Bean
    ListRolesUseCase listRolesUseCase(RoleRepository roles) {
        return new ListRolesUseCase(roles);
    }

    @Bean
    UpdateRoleUseCase updateRoleUseCase(RoleRepository roles) {
        return new UpdateRoleUseCase(roles);
    }

    @Bean
    ActivateRoleUseCase activateRoleUseCase(RoleRepository roles) {
        return new ActivateRoleUseCase(roles);
    }

    @Bean
    DeactivateRoleUseCase deactivateRoleUseCase(RoleRepository roles) {
        return new DeactivateRoleUseCase(roles);
    }

    @Bean
    DeleteRoleUseCase deleteRoleUseCase(
            RoleRepository roles, CurrentActorProvider actors, Clock iamClock) {
        return new DeleteRoleUseCase(roles, actors, iamClock);
    }

    @Bean
    AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase(
            RoleRepository roles, PermissionRepository permissions, AssignmentRepository assignments,
            CurrentActorProvider actors, Clock iamClock) {
        return new AssignPermissionsToRoleUseCase(
                roles, permissions, assignments, actors, iamClock);
    }

    @Bean
    RevokePermissionFromRoleUseCase revokePermissionFromRoleUseCase(
            RoleRepository roles, PermissionRepository permissions, AssignmentRepository assignments,
            CurrentActorProvider actors, Clock iamClock) {
        return new RevokePermissionFromRoleUseCase(
                roles, permissions, assignments, actors, iamClock);
    }

    @Bean
    CreatePermissionUseCase createPermissionUseCase(PermissionRepository permissions) {
        return new CreatePermissionUseCase(permissions);
    }

    @Bean
    GetPermissionUseCase getPermissionUseCase(PermissionRepository permissions) {
        return new GetPermissionUseCase(permissions);
    }

    @Bean
    ListPermissionsUseCase listPermissionsUseCase(PermissionRepository permissions) {
        return new ListPermissionsUseCase(permissions);
    }

    @Bean
    UpdatePermissionUseCase updatePermissionUseCase(PermissionRepository permissions) {
        return new UpdatePermissionUseCase(permissions);
    }

    @Bean
    ActivatePermissionUseCase activatePermissionUseCase(PermissionRepository permissions) {
        return new ActivatePermissionUseCase(permissions);
    }

    @Bean
    DeactivatePermissionUseCase deactivatePermissionUseCase(PermissionRepository permissions) {
        return new DeactivatePermissionUseCase(permissions);
    }

    @Bean
    DeletePermissionUseCase deletePermissionUseCase(
            PermissionRepository permissions, CurrentActorProvider actors, Clock iamClock) {
        return new DeletePermissionUseCase(permissions, actors, iamClock);
    }

    @Bean
    FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase(
            UserRepository users, IdentityRepository identities, CurrentActorProvider actors,
            LoadUserForAuthenticationUseCase loadForAuthentication,
            RoleRepository roles, AssignmentRepository assignments, Clock iamClock) {
        return new FindOrProvisionFederatedUserUseCase(
                users, identities, actors, loadForAuthentication, roles, assignments, iamClock);
    }

    @Bean
    LinkIdentityUseCase linkIdentityUseCase(
            UserRepository users, IdentityRepository identities, CurrentActorProvider actors, Clock iamClock) {
        return new LinkIdentityUseCase(users, identities, actors, iamClock);
    }

    @Bean
    UnlinkIdentityUseCase unlinkIdentityUseCase(
            UserRepository users, IdentityRepository identities, CurrentActorProvider actors, Clock iamClock) {
        return new UnlinkIdentityUseCase(users, identities, actors, iamClock);
    }

    @Bean
    ListUserIdentitiesUseCase listUserIdentitiesUseCase(
            UserRepository users, IdentityRepository identities) {
        return new ListUserIdentitiesUseCase(users, identities);
    }
}
