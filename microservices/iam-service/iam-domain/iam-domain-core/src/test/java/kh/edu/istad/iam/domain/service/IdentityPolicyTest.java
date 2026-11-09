package kh.edu.istad.iam.domain.service;

import java.util.List;
import java.util.Map;
import kh.edu.istad.iam.domain.entity.Identity;
import kh.edu.istad.iam.domain.entity.Profile;
import kh.edu.istad.iam.domain.entity.Provider;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static kh.edu.istad.iam.domain.TestData.NOW;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdentityPolicyTest {

    private Identity google() {
        return Identity.link(1L, Provider.GOOGLE, "g-1", "A@Gmail.com", true, "Dara", null, null, Map.of(), NOW);
    }

    private User user(String hash) {
        return User.register("dara", "dara@istad.co", hash, Profile.of("Dara", "Sok", "km"), NOW);
    }

    @Test
    void cannotLinkSameProviderTwice() {
        DomainException ex = assertThrows(DomainException.class,
                () -> IdentityPolicy.assertCanLink(List.of(google()), Provider.GOOGLE));
        assertEquals(ErrorCode.IDENTITY_ALREADY_LINKED, ex.getCode());
        assertDoesNotThrow(() -> IdentityPolicy.assertCanLink(List.of(google()), Provider.GITHUB));
    }

    @Test
    void cannotUnlinkLastSignInMethod() {
        DomainException ex = assertThrows(DomainException.class,
                () -> IdentityPolicy.assertCanUnlink(user(null), List.of(google())));
        assertEquals(ErrorCode.LAST_SIGN_IN_METHOD, ex.getCode());
    }

    @Test
    void canUnlinkWhenPasswordExists() {
        assertDoesNotThrow(() -> IdentityPolicy.assertCanUnlink(user("{bcrypt}x"), List.of(google())));
    }

    @Test
    void canUnlinkWhenAnotherIdentityExists() {
        Identity github = Identity.link(1L, Provider.GITHUB, "gh-1", null, false, null, "dara", null, null, NOW);
        assertDoesNotThrow(() -> IdentityPolicy.assertCanUnlink(user(null), List.of(google(), github)));
    }
}
