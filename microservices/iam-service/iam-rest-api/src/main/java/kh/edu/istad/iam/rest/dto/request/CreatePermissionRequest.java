package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kh.edu.istad.iam.application.command.CreatePermissionCommand;

public record CreatePermissionRequest(
        @NotBlank @Size(max = 50)
        String resource,

        @NotBlank @Size(max = 50)
        String action,

        @Size(max = 255)
        String description
) {
        public CreatePermissionCommand command() {
            return new CreatePermissionCommand(resource, action, description);
        }
    }
