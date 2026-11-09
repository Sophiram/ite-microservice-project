package kh.edu.istad.iam.application.result;

import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Effect;

public record PermissionGrantResult(Long id, UUID permissionUuid, String permissionName, Effect effect) { }
