package kh.edu.istad.iam.rest.dto.request;

import java.util.List;

public record UpdateClientRequest(
        String name,
        String clientId,
        String clientSecret,
        String type,
        List<String> grantTypes,
        List<String> redirectUris,
        List<String> signoutUris,
        List<String> scopes,
        Boolean consent
) {}
