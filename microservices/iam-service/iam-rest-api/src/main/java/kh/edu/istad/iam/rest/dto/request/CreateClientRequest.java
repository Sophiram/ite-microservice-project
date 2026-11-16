package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CreateClientRequest(
        @NotBlank(message = "Application name is required")
        String name,
        @NotBlank(message = "Client ID is required")
        String clientId,
        String clientSecret,
        String type,
        List<String> grantTypes,
        List<String> redirectUris,
        List<String> signoutUris,
        List<String> scopes,
        Boolean consent
) {}
