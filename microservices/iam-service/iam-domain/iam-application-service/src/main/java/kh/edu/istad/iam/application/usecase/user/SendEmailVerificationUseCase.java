package kh.edu.istad.iam.application.usecase.user;

import java.util.UUID;
import kh.edu.istad.iam.application.port.out.EmailVerificationPort;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.entity.User;

public final class SendEmailVerificationUseCase {
    private final UserRepository users;
    private final EmailVerificationPort emailPort;

    public SendEmailVerificationUseCase(UserRepository users, EmailVerificationPort emailPort) {
        this.users = users;
        this.emailPort = emailPort;
    }

    public boolean execute(UUID userUuid) {
        String email = "sornsophiram11@gmail.com";
        String username = "admin";
        if (userUuid != null) {
            var userOpt = users.findByUuid(userUuid);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    email = user.getEmail();
                }
                username = user.getUsername();
            }
        }
        return emailPort.sendVerificationEmail(email, username);
    }
}
