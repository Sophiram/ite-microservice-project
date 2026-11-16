package kh.edu.istad.iam.web.dto;

public record EmailVerificationResponse(
        boolean success,
        boolean verified,
        String message
) {
    public static EmailVerificationResponse sent(boolean success) {
        return new EmailVerificationResponse(success, false, success ? "Verification code sent successfully" : "Failed to send verification code");
    }

    public static EmailVerificationResponse verified(boolean ok) {
        return new EmailVerificationResponse(ok, ok, ok ? "Email successfully verified!" : "Invalid or expired verification code.");
    }
}
