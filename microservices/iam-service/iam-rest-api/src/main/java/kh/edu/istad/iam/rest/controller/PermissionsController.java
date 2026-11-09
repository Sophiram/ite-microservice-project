package kh.edu.istad.iam.rest.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import kh.edu.istad.iam.application.command.UpdatePermissionCommand;
import kh.edu.istad.iam.application.port.out.PageQuery;
import kh.edu.istad.iam.application.port.out.PageResult;
import kh.edu.istad.iam.application.query.PermissionFilterQuery;
import kh.edu.istad.iam.application.result.PermissionResult;
import kh.edu.istad.iam.application.usecase.permission.ActivatePermissionUseCase;
import kh.edu.istad.iam.application.usecase.permission.CreatePermissionUseCase;
import kh.edu.istad.iam.application.usecase.permission.DeactivatePermissionUseCase;
import kh.edu.istad.iam.application.usecase.permission.DeletePermissionUseCase;
import kh.edu.istad.iam.application.usecase.permission.GetPermissionUseCase;
import kh.edu.istad.iam.application.usecase.permission.ListPermissionsUseCase;
import kh.edu.istad.iam.application.usecase.permission.UpdatePermissionUseCase;
import kh.edu.istad.iam.rest.dto.request.CreatePermissionRequest;
import kh.edu.istad.iam.rest.dto.response.PageResponse;
import kh.edu.istad.iam.rest.dto.response.PermissionResponse;
import kh.edu.istad.iam.rest.dto.request.UpdatePermissionRequest;
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
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionsController {
    private final CreatePermissionUseCase createPermissionUseCase;
    private final GetPermissionUseCase getPermissionUseCase;
    private final ListPermissionsUseCase listPermissionsUseCase;
    private final UpdatePermissionUseCase updatePermissionUseCase;
    private final ActivatePermissionUseCase activatePermissionUseCase;
    private final DeactivatePermissionUseCase deactivatePermissionUseCase;
    private final DeletePermissionUseCase deletePermissionUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ResponseEntity<PermissionResponse> create(
            @Valid @RequestBody CreatePermissionRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PermissionResponse.from(createPermissionUseCase.execute(body.command())));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public PageResponse<PermissionResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String direction) {
        PageQuery query = ApiPageParameters.toQuery(page, size, sort, direction);
        PageResult<PermissionResult> result = listPermissionsUseCase.execute(new PermissionFilterQuery(query));
        return PageResponse.from(result, PermissionResponse::from);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:read')")
    public PermissionResponse get(@PathVariable UUID uuid) {
        return PermissionResponse.from(getPermissionUseCase.execute(uuid));
    }

    @PatchMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:update')")
    public PermissionResponse update(@PathVariable UUID uuid,
            @Valid @RequestBody UpdatePermissionRequest body) {
        return PermissionResponse.from(updatePermissionUseCase.execute(
                new UpdatePermissionCommand(uuid, body.description())));
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        deletePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/activate")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<Void> activate(@PathVariable UUID uuid) {
        activatePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/deactivate")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID uuid) {
        deletePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }
}
