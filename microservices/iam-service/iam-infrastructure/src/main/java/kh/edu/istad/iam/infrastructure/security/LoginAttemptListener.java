package kh.edu.istad.iam.infrastructure.security;

import kh.edu.istad.iam.application.usecase.user.RecordLoginAttemptUseCase;
import kh.edu.istad.iam.domain.exception.DomainException;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptListener {
    private final RecordLoginAttemptUseCase recordLoginAttempt;

    public LoginAttemptListener(RecordLoginAttemptUseCase recordLoginAttempt) {
        this.recordLoginAttempt = recordLoginAttempt;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails user) {
            safelyRecord(user.getUsername(), true);
        } else if (principal instanceof OAuth2User user) {
            String username = user.getAttribute("iam_username");
            if (username != null) safelyRecord(username, true);
        }
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        if (!(event.getAuthentication() instanceof UsernamePasswordAuthenticationToken authentication)) return;
        Object principal = authentication.getPrincipal();
        if (principal instanceof String username && !username.isBlank()) safelyRecord(username, false);
        else if (principal instanceof UserDetails user) safelyRecord(user.getUsername(), false);
    }

    private void safelyRecord(String login, boolean success) {
        try {
            recordLoginAttempt.execute(login, success);
        } catch (DomainException ignored) {
            // Unknown usernames are not persisted as accounts.
        }
    }
}
