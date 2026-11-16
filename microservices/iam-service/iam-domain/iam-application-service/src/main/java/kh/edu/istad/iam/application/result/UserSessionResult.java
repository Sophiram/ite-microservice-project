package kh.edu.istad.iam.application.result;

public record UserSessionResult(
        String id,
        String device,
        String browser,
        String ip,
        String location,
        String lastActive,
        boolean current
) {}
