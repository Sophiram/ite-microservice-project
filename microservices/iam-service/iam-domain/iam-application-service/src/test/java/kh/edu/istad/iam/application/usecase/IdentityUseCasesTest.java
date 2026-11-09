package kh.edu.istad.iam.application.usecase;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.iam.domain.entity.Identity;
import kh.edu.istad.iam.domain.entity.Provider;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IdentityUseCasesTest {
    private final UserRepository users = mock(UserRepository.class);
    private final IdentityRepository identities = mock(IdentityRepository.class);
    private final CurrentActorProvider actors = mock(CurrentActorProvider.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void cannotUnlinkTheOnlySignInMethod() {
        User user = User.provisionFederated("alex", "alex@example.com", true, null);
        user.setId(4L);
        UUID identityUuid = UUID.randomUUID();
        Identity identity = Identity.link(user.getId(), Provider.GOOGLE, "google-123",
                "alex@example.com", true, "Alex", "alex", null, null, clock.instant());
        identity.setUuid(identityUuid);
        when(users.findByUuid(user.getUuid())).thenReturn(Optional.of(user));
        when(identities.findByUuid(identityUuid)).thenReturn(Optional.of(identity));
        when(identities.findByUserId(user.getId())).thenReturn(List.of(identity));

        DomainException exception = assertThrows(DomainException.class,
                () -> new UnlinkIdentityUseCase(users, identities, actors, clock)
                        .execute(user.getUuid(), identityUuid));

        assertEquals(ErrorCode.LAST_SIGN_IN_METHOD, exception.getCode());
        verify(identities, never()).save(identity);
    }
}
