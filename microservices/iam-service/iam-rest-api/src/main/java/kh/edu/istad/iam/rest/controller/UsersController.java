package kh.edu.istad.iam.rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.command.AssignRolesToUserCommand;
import kh.edu.istad.iam.application.command.ChangePasswordCommand;
import kh.edu.istad.iam.application.command.GrantPermissionsToUserCommand;
import kh.edu.istad.iam.application.command.ResetUserPasswordCommand;
import kh.edu.istad.iam.application.command.RevokePermissionFromUserCommand;
import kh.edu.istad.iam.application.command.RevokeRoleFromUserCommand;
import kh.edu.istad.iam.application.command.UpdateUserProfileCommand;
import kh.edu.istad.iam.application.port.out.PageQuery;
import kh.edu.istad.iam.application.port.out.PageResult;
import kh.edu.istad.iam.application.query.UserFilterQuery;
import kh.edu.istad.iam.application.result.UserResult;
import kh.edu.istad.iam.application.usecase.user.AssignRolesToUserUseCase;
import kh.edu.istad.iam.application.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.iam.application.usecase.user.CreateUserUseCase;
import kh.edu.istad.iam.application.usecase.user.DeleteUserUseCase;
import kh.edu.istad.iam.application.usecase.user.DisableUserUseCase;
import kh.edu.istad.iam.application.usecase.user.EnableUserUseCase;
import kh.edu.istad.iam.application.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.iam.application.usecase.user.GetUserUseCase;
import kh.edu.istad.iam.application.usecase.user.GrantPermissionsToUserUseCase;
import kh.edu.istad.iam.application.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.iam.application.usecase.user.ListUsersUseCase;
import kh.edu.istad.iam.application.usecase.user.LockUserUseCase;
import kh.edu.istad.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.iam.application.usecase.user.ResetUserPasswordUseCase;
import kh.edu.istad.iam.application.usecase.user.RevokePermissionFromUserUseCase;
import kh.edu.istad.iam.application.usecase.user.RevokeRoleFromUserUseCase;
import kh.edu.istad.iam.application.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.iam.application.usecase.user.UnlockUserUseCase;
import kh.edu.istad.iam.application.usecase.user.UpdateUserProfileUseCase;
import kh.edu.istad.iam.rest.dto.request.AssignRolesToUserRequest;
import kh.edu.istad.iam.rest.dto.request.ChangePasswordRequest;
import kh.edu.istad.iam.rest.dto.request.CreateUserRequest;
import kh.edu.istad.iam.rest.dto.request.GrantPermissionsToUserRequest;
import kh.edu.istad.iam.rest.dto.response.IdentityResponse;
import kh.edu.istad.iam.rest.dto.response.PageResponse;
import kh.edu.istad.iam.rest.dto.response.PermissionGrantResponse;
import kh.edu.istad.iam.rest.dto.request.RegisterUserRequest;
import kh.edu.istad.iam.rest.dto.request.ResetPasswordRequest;
import kh.edu.istad.iam.rest.dto.response.RoleAssignmentResponse;
import kh.edu.istad.iam.rest.dto.request.UpdateUserProfileRequest;
import kh.edu.istad.iam.rest.dto.response.UserAuthoritiesResponse;
import kh.edu.istad.iam.rest.dto.response.UserResponse;
import kh.edu.istad.iam.rest.support.ApiAuthentication;
import kh.edu.istad.iam.rest.support.ApiPageParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UsersController {
    private final RegisterUserUseCase registerUserUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ResetUserPasswordUseCase resetUserPasswordUseCase;
    private final EnableUserUseCase enableUserUseCase;
    private final DisableUserUseCase disableUserUseCase;
    private final LockUserUseCase lockUserUseCase;
    private final UnlockUserUseCase unlockUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final AssignRolesToUserUseCase assignRolesToUserUseCase;
    private final RevokeRoleFromUserUseCase revokeRoleFromUserUseCase;
    private final GrantPermissionsToUserUseCase grantPermissionsToUserUseCase;
    private final RevokePermissionFromUserUseCase revokePermissionFromUserUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.from(registerUserUseCase.execute(body.command())));
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.from(createUserUseCase.execute(body.command())));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('user:read')")
    public PageResponse<UserResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String direction) {
        PageQuery query = ApiPageParameters.toQuery(page, size, sort, direction);
        PageResult<UserResult> result = listUsersUseCase.execute(new UserFilterQuery(query));
        return PageResponse.from(result, UserResponse::from);
    }

    @GetMapping("/users/{uuid}")
    @PreAuthorize("hasAuthority('user:read')")
    public UserResponse get(@PathVariable UUID uuid) {
        return UserResponse.from(getUserUseCase.execute(uuid));
    }

    @PatchMapping("/users/{uuid}")
    @PreAuthorize("hasAuthority('user:update')")
    public UserResponse update(@PathVariable UUID uuid,
            @Valid @RequestBody UpdateUserProfileRequest body) {
        var command = new UpdateUserProfileCommand(uuid, body.profile(), body.addressDomain());
        return UserResponse.from(updateUserProfileUseCase.execute(command));
    }

    @DeleteMapping("/users/{uuid}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        deleteUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{uuid}/enable")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> enable(@PathVariable UUID uuid) {
        enableUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{uuid}/disable")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> disable(@PathVariable UUID uuid) {
        disableUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{uuid}/lock")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> lock(@PathVariable UUID uuid) {
        lockUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{uuid}/unlock")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> unlock(@PathVariable UUID uuid) {
        unlockUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{uuid}/password")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> resetPassword(@PathVariable UUID uuid,
                                               @Valid @RequestBody ResetPasswordRequest body) {
        resetUserPasswordUseCase.execute(new ResetUserPasswordCommand(uuid, body.newPassword()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{uuid}/authorities")
    @PreAuthorize("hasAuthority('user:read')")
    public UserAuthoritiesResponse authorities(@PathVariable UUID uuid) {
        return UserAuthoritiesResponse.from(getUserAuthoritiesUseCase.execute(uuid));
    }

    @PostMapping("/users/{uuid}/roles")
    @PreAuthorize("hasAuthority('user:assign')")
    public List<RoleAssignmentResponse> assignRoles(@PathVariable UUID uuid,
                                                    @Valid @RequestBody AssignRolesToUserRequest body) {
        return assignRolesToUserUseCase.execute(new AssignRolesToUserCommand(
                        uuid, body.roleUuids(), body.expiresAt(), body.reason())).stream()
                .map(RoleAssignmentResponse::from).toList();
    }

    @DeleteMapping("/users/{uuid}/roles/{roleUuid}")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<Void> revokeRole(@PathVariable UUID uuid, @PathVariable UUID roleUuid,
                                           @RequestParam(required = false) String reason) {
        revokeRoleFromUserUseCase.execute(new RevokeRoleFromUserCommand(uuid, roleUuid, reason));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{uuid}/permissions")
    @PreAuthorize("hasAuthority('user:assign')")
    public List<PermissionGrantResponse> grantPermissions(@PathVariable UUID uuid,
            @Valid @RequestBody GrantPermissionsToUserRequest body) {
        return grantPermissionsToUserUseCase.execute(new GrantPermissionsToUserCommand(
                        uuid, body.permissionUuids(), body.effect(), body.expiresAt(), body.reason())).stream()
                .map(PermissionGrantResponse::from).toList();
    }

    @DeleteMapping("/users/{uuid}/permissions/{permissionUuid}")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<Void> revokePermission(@PathVariable UUID uuid, @PathVariable UUID permissionUuid,
            @RequestParam(defaultValue = "GRANT") kh.edu.istad.iam.domain.entity.Effect effect,
            @RequestParam(required = false) String reason) {
        revokePermissionFromUserUseCase.execute(new RevokePermissionFromUserCommand(uuid, permissionUuid, effect, reason));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{uuid}/identities")
    @PreAuthorize("hasAuthority('user:read')")
    public List<IdentityResponse> identities(@PathVariable UUID uuid) {
        return listUserIdentitiesUseCase.execute(uuid).stream().map(IdentityResponse::from).toList();
    }

    @DeleteMapping("/users/{uuid}/identities/{identityUuid}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> unlinkIdentity(@PathVariable UUID uuid, @PathVariable UUID identityUuid) {
        unlinkIdentityUseCase.execute(uuid, identityUuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserResponse me(Authentication authentication) {
        return UserResponse.from(getUserUseCase.execute(ApiAuthentication.userUuid(authentication)));
    }

    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserResponse updateMe(Authentication authentication,
            @Valid @RequestBody UpdateUserProfileRequest body) {
        UUID uuid = ApiAuthentication.userUuid(authentication);
        return UserResponse.from(updateUserProfileUseCase.execute(
                new UpdateUserProfileCommand(uuid, body.profile(), body.addressDomain())));
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeMyPassword(Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest body) {
        changePasswordUseCase.execute(new ChangePasswordCommand(ApiAuthentication.userUuid(authentication),
                body.currentPassword(), body.newPassword()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/identities")
    @PreAuthorize("isAuthenticated()")
    public List<IdentityResponse> myIdentities(Authentication authentication) {
        return listUserIdentitiesUseCase.execute(ApiAuthentication.userUuid(authentication)).stream()
                .map(IdentityResponse::from).toList();
    }

    @DeleteMapping("/me/identities/{identityUuid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlinkMyIdentity(Authentication authentication, @PathVariable UUID identityUuid) {
        unlinkIdentityUseCase.execute(ApiAuthentication.userUuid(authentication), identityUuid);
        return ResponseEntity.noContent().build();
    }
}
