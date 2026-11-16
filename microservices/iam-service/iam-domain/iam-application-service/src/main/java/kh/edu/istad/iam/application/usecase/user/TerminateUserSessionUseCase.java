package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.port.out.UserSessionPort;

public final class TerminateUserSessionUseCase {
    private final UserSessionPort sessionPort;

    public TerminateUserSessionUseCase(UserSessionPort sessionPort) {
        this.sessionPort = sessionPort;
    }

    public void execute(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            sessionPort.terminateAllOtherSessions();
        } else {
            sessionPort.terminateSession(sessionId);
        }
    }
}
