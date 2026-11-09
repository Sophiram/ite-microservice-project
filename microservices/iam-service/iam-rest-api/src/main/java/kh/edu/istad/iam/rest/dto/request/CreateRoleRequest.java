package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.command.CreateRoleCommand;

public record CreateRoleRequest(
        @NotBlank @Size(max = 50)
        String name,


        @Size(max = 255)
        String description,

        List<UUID> permissionUuids
) {
        public CreateRoleCommand command() {
            return new CreateRoleCommand(name, description, permissionUuids);
        }
    }
