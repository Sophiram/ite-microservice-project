package kh.edu.istad.iam.application.port.out;

import java.util.Optional;

public interface EmailVerificationPort {
    boolean sendVerificationEmail(String recipientEmail, String username);
    boolean verifyCode(String recipientEmail, String codeOrToken);
    Optional<String> verifyCodeAndGetEmail(String recipientEmail, String codeOrToken);
}

