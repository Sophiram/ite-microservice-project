package kh.edu.istad.iam.domain.entity;

import java.time.Instant;
import java.util.Map;

/** A social account (Google, GitHub, Facebook, Telegram) linked to a user. */
public class Identity extends AuditableDomain {

    private Long userId;
    private Provider provider;
    private String providerSubject;
    private String email;
    private boolean emailVerified;
    private String displayName;
    private String username;
    private String pictureUrl;
    private Map<String, Object> attributes = Map.of();
    private Instant linkedAt;
    private Instant lastLoginAt;

    public Identity() { }

    public static Identity link(Long userId, Provider provider, String providerSubject,
                                String email, boolean emailVerified, String displayName,
                                String username, String pictureUrl,
                                Map<String, Object> attributes, Instant now) {
        Identity i = new Identity();
        i.userId = userId;
        i.provider = provider;
        i.providerSubject = providerSubject;
        i.email = User.normalizeEmail(email);
        i.emailVerified = emailVerified;
        i.displayName = displayName;
        i.username = username;
        i.pictureUrl = pictureUrl;
        if (attributes == null || attributes.isEmpty()) {
            i.attributes = Map.of();
        } else {
            java.util.Map<String, Object> safe = new java.util.LinkedHashMap<>();
            attributes.forEach((k, v) -> {
                if (k != null && v != null) {
                    safe.put(k, v);
                }
            });
            i.attributes = java.util.Collections.unmodifiableMap(safe);
        }
        i.linkedAt = now;
        i.lastLoginAt = now;
        return i;
    }

    public void recordLogin(Instant now) { this.lastLoginAt = now; }

    /** Soft-unlink. */
    public void unlink(String actor, Instant now) { markDeleted(actor, now); }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
    public String getProviderSubject() { return providerSubject; }
    public void setProviderSubject(String s) { this.providerSubject = s; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = User.normalizeEmail(email); }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean v) { this.emailVerified = v; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String v) { this.displayName = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String v) { this.pictureUrl = v; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> v) { this.attributes = v == null ? Map.of() : v; }
    public Instant getLinkedAt() { return linkedAt; }
    public void setLinkedAt(Instant v) { this.linkedAt = v; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant v) { this.lastLoginAt = v; }
}
