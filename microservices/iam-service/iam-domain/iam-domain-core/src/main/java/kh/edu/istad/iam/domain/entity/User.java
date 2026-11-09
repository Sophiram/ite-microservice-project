package kh.edu.istad.iam.domain.entity;

import java.time.Instant;
import java.util.Locale;

/**
 * User = OIDC standard claims + Spring Security UserDetails flags.
 * Plain Java: no Spring Security / JPA types here.
 */
public class User extends AuditableDomain {

    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private String username;
    private String email;                 // always stored lower-case
    private boolean emailVerified;
    private String passwordHash;          // null for social-only users
    private Profile profile;
    private Address address = Address.empty();
    private boolean phoneNumberVerified;
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private int failedLoginAttempts;
    private Instant lastLoginAt;
    private Instant passwordChangedAt;

    public User() { }

    /** Self-registration or admin creation with a password. */
    public static User register(String username, String email, String passwordHash,
                                Profile profile, Instant now) {
        User u = new User();
        u.username = username;
        u.email = normalizeEmail(email);
        u.passwordHash = passwordHash;
        u.profile = profile;
        u.passwordChangedAt = now;
        return u;
    }

    /** Created from a social login: no password, email trusted only if the provider verified it. */
    public static User provisionFederated(String username, String email, boolean emailVerified,
                                          Profile profile) {
        User u = new User();
        u.username = username;
        u.email = normalizeEmail(email);
        u.emailVerified = emailVerified;
        u.profile = profile;
        return u;
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    // ---------- behaviour ----------

    public void updateProfile(Profile profile) { this.profile = profile; }

    public void updateAddress(Address address) { this.address = address == null ? Address.empty() : address; }

    public void changePasswordHash(String newHash, Instant now) {
        this.passwordHash = newHash;
        this.passwordChangedAt = now;
        this.credentialsNonExpired = true;
    }

    public void enable() { this.enabled = true; }

    public void disable() { this.enabled = false; }

    public void lock() { this.accountNonLocked = false; }

    public void unlock() {
        this.accountNonLocked = true;
        this.failedLoginAttempts = 0;
    }

    public void recordLoginSuccess(Instant now) {
        this.failedLoginAttempts = 0;
        this.lastLoginAt = now;
    }

    /** Locks the account after too many consecutive failures. */
    public void recordLoginFailure() {
        this.failedLoginAttempts++;
        if (failedLoginAttempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
            this.accountNonLocked = false;
        }
    }

    public void delete(String actor, Instant now) {
        markDeleted(actor, now);
        this.enabled = false;
    }

    public boolean hasPassword() { return passwordHash != null && !passwordHash.isBlank(); }

    public boolean canAuthenticate() {
        return !isDeleted() && enabled && accountNonExpired && accountNonLocked && credentialsNonExpired;
    }

    /** OIDC "name". */
    public String fullName() {
        if (profile == null || profile.fullName().isBlank()) {
            return username;
        }
        return profile.fullName();
    }

    // ---------- accessors ----------

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = normalizeEmail(email); }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public boolean isPhoneNumberVerified() { return phoneNumberVerified; }
    public void setPhoneNumberVerified(boolean v) { this.phoneNumberVerified = v; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isAccountNonExpired() { return accountNonExpired; }
    public void setAccountNonExpired(boolean v) { this.accountNonExpired = v; }
    public boolean isAccountNonLocked() { return accountNonLocked; }
    public void setAccountNonLocked(boolean v) { this.accountNonLocked = v; }
    public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    public void setCredentialsNonExpired(boolean v) { this.credentialsNonExpired = v; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int v) { this.failedLoginAttempts = v; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant v) { this.lastLoginAt = v; }
    public Instant getPasswordChangedAt() { return passwordChangedAt; }
    public void setPasswordChangedAt(Instant v) { this.passwordChangedAt = v; }
}
