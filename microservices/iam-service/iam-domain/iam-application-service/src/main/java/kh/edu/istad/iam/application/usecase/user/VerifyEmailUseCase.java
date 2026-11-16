package kh.edu.istad.iam.application.usecase.user;

import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.EmailVerificationPort;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.entity.User;

public final class VerifyEmailUseCase {
    private final UserRepository users;
    private final EmailVerificationPort emailPort;

    public VerifyEmailUseCase(UserRepository users, EmailVerificationPort emailPort) {
        this.users = users;
        this.emailPort = emailPort;
    }

    public boolean execute(UUID userUuid, String codeOrToken) {
        User targetUser = null;
        String email = null;

        if (userUuid != null) {
            var userOpt = users.findByUuid(userUuid);
            if (userOpt.isPresent()) {
                targetUser = userOpt.get();
                email = targetUser.getEmail();
            }
        }

        Optional<String> verifiedEmailOpt = emailPort.verifyCodeAndGetEmail(email, codeOrToken);
        if (verifiedEmailOpt.isEmpty()) {
            return false;
        }

        String verifiedEmail = verifiedEmailOpt.get();
        if (targetUser == null && verifiedEmail != null && !verifiedEmail.isBlank()) {
            var userByEmail = users.findByEmail(verifiedEmail);
            if (userByEmail.isPresent()) {
                targetUser = userByEmail.get();
            } else {
                var userByUsername = users.findByUsernameOrEmail(verifiedEmail);
                if (userByUsername.isPresent()) {
                    targetUser = userByUsername.get();
                }
            }
        }

        if (targetUser != null) {
            targetUser.setEmailVerified(true);
            users.save(targetUser);
            return true;
        }

        return true;
    }
}
