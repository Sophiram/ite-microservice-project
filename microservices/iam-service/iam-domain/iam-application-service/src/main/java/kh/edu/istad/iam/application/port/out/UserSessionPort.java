package kh.edu.istad.iam.application.port.out;

import java.util.List;
import kh.edu.istad.iam.application.result.UserSessionResult;

public interface UserSessionPort {
    List<UserSessionResult> findActiveSessions(String clientIp);

    default List<UserSessionResult> findActiveSessions(String clientIp, String userAgent, String sessionId) {
        return findActiveSessions(clientIp);
    }

    void terminateSession(String sessionId);
    void terminateAllOtherSessions();
}
