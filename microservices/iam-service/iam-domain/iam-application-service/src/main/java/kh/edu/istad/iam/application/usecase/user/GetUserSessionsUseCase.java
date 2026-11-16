package kh.edu.istad.iam.application.usecase.user;

import java.util.List;
import kh.edu.istad.iam.application.port.out.UserSessionPort;
import kh.edu.istad.iam.application.result.UserSessionResult;

public final class GetUserSessionsUseCase {
    private final UserSessionPort sessionPort;

    public GetUserSessionsUseCase(UserSessionPort sessionPort) {
        this.sessionPort = sessionPort;
    }

    public List<UserSessionResult> execute(String clientIp) {
        return sessionPort.findActiveSessions(clientIp);
    }

    public List<UserSessionResult> execute(String clientIp, String userAgent, String sessionId) {
        return sessionPort.findActiveSessions(clientIp, userAgent, sessionId);
    }
}
