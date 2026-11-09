package kh.edu.istad.iam.domain.entity;

import org.junit.jupiter.api.Test;

import static kh.edu.istad.iam.domain.TestData.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private User newUser() {
        return User.register("dara", "Dara.Sok@ISTAD.co", "{bcrypt}hash", Profile.of("Dara", "Sok", "km"), NOW);
    }

    @Test
    void emailIsStoredLowerCaseAndNameIsGivenPlusFamily() {
        User u = newUser();
        assertEquals("dara.sok@istad.co", u.getEmail());
        assertEquals("Dara Sok", u.fullName());
        assertTrue(u.canAuthenticate());
    }

    @Test
    void accountLocksAfterTooManyFailures() {
        User u = newUser();
        for (int i = 0; i < User.MAX_FAILED_LOGIN_ATTEMPTS; i++) {
            assertTrue(u.isAccountNonLocked());
            u.recordLoginFailure();
        }
        assertFalse(u.isAccountNonLocked());
        assertFalse(u.canAuthenticate());

        u.unlock();
        assertTrue(u.canAuthenticate());
        assertEquals(0, u.getFailedLoginAttempts());
    }

    @Test
    void successfulLoginResetsCounterAndStampsTime() {
        User u = newUser();
        u.recordLoginFailure();
        u.recordLoginSuccess(NOW);
        assertEquals(0, u.getFailedLoginAttempts());
        assertEquals(NOW, u.getLastLoginAt());
    }

    @Test
    void federatedUserHasNoPassword() {
        User u = User.provisionFederated("dara", "d@gmail.com", true, Profile.of("Dara", "Sok", "en"));
        assertFalse(u.hasPassword());
        assertTrue(u.isEmailVerified());
    }

    @Test
    void deleteIsSoftAndDisablesUser() {
        User u = newUser();
        u.delete("admin", NOW);
        assertTrue(u.isDeleted());
        assertFalse(u.isEnabled());
        assertFalse(u.canAuthenticate());
    }
}
