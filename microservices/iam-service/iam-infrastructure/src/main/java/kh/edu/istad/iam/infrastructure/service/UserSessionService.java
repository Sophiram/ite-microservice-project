package kh.edu.istad.iam.infrastructure.service;

import jakarta.servlet.http.HttpServletRequest;
import kh.edu.istad.iam.application.port.out.UserSessionPort;
import kh.edu.istad.iam.application.result.UserSessionResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionService implements UserSessionPort {

    private final Map<String, UserSessionResult> activeSessionStore = new ConcurrentHashMap<>();

    @Override
    public List<UserSessionResult> findActiveSessions(String clientIp) {
        return findActiveSessions(clientIp, null, null);
    }

    @Override
    public List<UserSessionResult> findActiveSessions(String clientIp, String userAgent, String currentSessionId) {
        String ip = (clientIp != null && !clientIp.isBlank()) ? clientIp : "127.0.0.1";
        String id = (currentSessionId != null && !currentSessionId.isBlank())
                ? "sess-" + (currentSessionId.length() > 8 ? currentSessionId.substring(0, 8) : currentSessionId)
                : "sess-current";

        String device = parseDevice(userAgent);
        String browser = parseBrowser(userAgent);
        String location = "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)
                ? "Phnom Penh, Cambodia (Local Network)"
                : "Phnom Penh, Cambodia";

        UserSessionResult currentSession = new UserSessionResult(
                id, device, browser, ip, location, "Just now", true
        );
        activeSessionStore.put(id, currentSession);

        return new java.util.ArrayList<>(activeSessionStore.values());
    }

    @Override
    public void terminateSession(String sessionId) {
        if (sessionId != null) {
            activeSessionStore.remove(sessionId);
        }
    }

    @Override
    public void terminateAllOtherSessions() {
        activeSessionStore.entrySet().removeIf(entry -> !entry.getValue().current());
    }

    private static String parseDevice(String ua) {
        if (ua == null || ua.isBlank()) return "Windows PC";
        if (ua.contains("Windows NT 10.0")) return "Windows 11 / 10 Pro";
        if (ua.contains("Windows")) return "Windows PC";
        if (ua.contains("Macintosh") || ua.contains("Mac OS")) return "macOS Device";
        if (ua.contains("iPhone")) return "iPhone";
        if (ua.contains("iPad")) return "iPad";
        if (ua.contains("Android")) return "Android Device";
        if (ua.contains("Linux")) return "Linux Workstation";
        return "Workstation Device";
    }

    private static String parseBrowser(String ua) {
        if (ua == null || ua.isBlank()) return "Chrome Browser";
        if (ua.contains("Edg/")) return "Microsoft Edge";
        if (ua.contains("Chrome/") && !ua.contains("Edg/")) return "Google Chrome";
        if (ua.contains("Firefox/")) return "Mozilla Firefox";
        if (ua.contains("Safari/") && !ua.contains("Chrome/")) return "Apple Safari";
        return "Web Browser";
    }

    public static String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = request.getRemoteAddr();
        }
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
        if (clientIp == null || clientIp.equals("0:0:0:0:0:0:0:1")) {
            clientIp = "127.0.0.1";
        }
        return clientIp;
    }
}
