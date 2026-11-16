package kh.edu.istad.iam.web.dto;

import kh.edu.istad.iam.application.result.UserSessionResult;

public record SessionResponse(
        String id,
        String device,
        String browser,
        String ip,
        String location,
        String lastActive,
        boolean current
) {
    public static SessionResponse from(UserSessionResult result) {
        return new SessionResponse(
                result.id(),
                result.device(),
                result.browser(),
                result.ip(),
                result.location(),
                result.lastActive(),
                result.current()
        );
    }
}
