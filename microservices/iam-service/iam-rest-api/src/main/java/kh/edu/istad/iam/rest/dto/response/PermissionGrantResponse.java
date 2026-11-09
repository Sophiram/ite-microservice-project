package kh.edu.istad.iam.rest.dto.response;

import java.util.UUID;
import kh.edu.istad.iam.application.result.PermissionGrantResult;
import kh.edu.istad.iam.domain.entity.Effect;

public record PermissionGrantResponse(Long id, UUID permissionUuid, String permissionName, Effect effect) {
    public static PermissionGrantResponse from(PermissionGrantResult result) {
        return new PermissionGrantResponse(result.id(), result.permissionUuid(), result.permissionName(),
                result.effect());
    }
}
