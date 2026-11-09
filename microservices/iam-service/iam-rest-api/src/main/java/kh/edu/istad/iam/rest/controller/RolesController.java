package kh.edu.istad.iam.rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.command.AssignPermissionsToRoleCommand;
import kh.edu.istad.iam.application.command.RevokePermissionFromRoleCommand;
import kh.edu.istad.iam.application.command.UpdateRoleCommand;
import kh.edu.istad.iam.application.port.out.PageQuery;
import kh.edu.istad.iam.application.port.out.PageResult;
import kh.edu.istad.iam.application.query.RoleFilterQuery;
import kh.edu.istad.iam.application.result.RoleResult;
import kh.edu.istad.iam.application.usecase.role.ActivateRoleUseCase;
import kh.edu.istad.iam.application.usecase.role.AssignPermissionsToRoleUseCase;
import kh.edu.istad.iam.application.usecase.role.CreateRoleUseCase;
import kh.edu.istad.iam.application.usecase.role.DeactivateRoleUseCase;
import kh.edu.istad.iam.application.usecase.role.DeleteRoleUseCase;
import kh.edu.istad.iam.application.usecase.role.GetRoleUseCase;
import kh.edu.istad.iam.application.usecase.role.ListRolesUseCase;
import kh.edu.istad.iam.application.usecase.role.RevokePermissionFromRoleUseCase;
import kh.edu.istad.iam.application.usecase.role.UpdateRoleUseCase;
import kh.edu.istad.iam.rest.dto.request.AssignRolePermissionsRequest;
import kh.edu.istad.iam.rest.dto.request.CreateRoleRequest;
import kh.edu.istad.iam.rest.dto.response.PageResponse;
import kh.edu.istad.iam.rest.dto.response.PermissionAssignmentResponse;
import kh.edu.istad.iam.rest.dto.response.RoleResponse;
import kh.edu.istad.iam.rest.dto.request.UpdateRoleRequest;
import kh.edu.istad.iam.rest.support.ApiPageParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolesController {
    private final CreateRoleUseCase createRoleUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final ListRolesUseCase listRolesUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final ActivateRoleUseCase activateRoleUseCase;
    private final DeactivateRoleUseCase deactivateRoleUseCase;
    private final AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase;
    private final RevokePermissionFromRoleUseCase revokePermissionFromRoleUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RoleResponse.from(createRoleUseCase.execute(body.command())));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public PageResponse<RoleResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String direction) {
        PageQuery query = ApiPageParameters.toQuery(page, size, sort, direction);
        PageResult<RoleResult> result = listRolesUseCase.execute(new RoleFilterQuery(query));
        return PageResponse.from(result, RoleResponse::from);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:read')")
    public RoleResponse get(@PathVariable UUID uuid) {
        return RoleResponse.from(getRoleUseCase.execute(uuid));
    }

    @PatchMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:update')")
    public RoleResponse update(@PathVariable UUID uuid,
                                             @Valid @RequestBody UpdateRoleRequest body) {
        return RoleResponse.from(updateRoleUseCase.execute(new UpdateRoleCommand(
                uuid, body.description())));
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        deleteRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/activate")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<Void> activate(@PathVariable UUID uuid) {
        activateRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/deactivate")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID uuid) {
        deactivateRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    public List<PermissionAssignmentResponse> assignPermissions(@PathVariable UUID uuid,
            @Valid @RequestBody AssignRolePermissionsRequest body) {
        return assignPermissionsToRoleUseCase.execute(new AssignPermissionsToRoleCommand(
                        uuid, body.permissionUuids(), body.expiresAt(), body.reason())).stream()
                .map(PermissionAssignmentResponse::from).toList();
    }

    @DeleteMapping("/{uuid}/permissions/{permissionUuid}")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<Void> revokePermission(@PathVariable UUID uuid, @PathVariable UUID permissionUuid,
                                                  @RequestParam(required = false) String reason) {
        revokePermissionFromRoleUseCase.execute(new RevokePermissionFromRoleCommand(uuid, permissionUuid, reason));
        return ResponseEntity.noContent().build();
    }
}
