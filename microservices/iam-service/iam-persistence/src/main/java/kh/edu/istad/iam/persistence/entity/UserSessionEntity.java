package kh.edu.istad.iam.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
public class UserSessionEntity {
    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "device", length = 100)
    private String device;

    @Column(name = "browser", length = 150)
    private String browser;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "is_current")
    private Boolean current = false;

    @Column(name = "last_active", length = 50)
    private String lastActive = "Active now";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
