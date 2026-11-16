package kh.edu.istad.iam.web.dto;

public record ActionResponse(
        boolean success,
        String message
) {
    public static ActionResponse ok(String message) {
        return new ActionResponse(true, message);
    }
}
