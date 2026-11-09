package kh.edu.istad.iam.application.result;

import java.util.List;

public record ConnectedApplicationResult(String clientId, String clientName, List<String> scopes) {
    public ConnectedApplicationResult {
        scopes = List.copyOf(scopes);
    }
}
