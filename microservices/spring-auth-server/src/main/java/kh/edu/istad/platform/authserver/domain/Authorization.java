package kh.edu.istad.platform.authserver.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "`authorization`")
@Getter
@Setter
public class Authorization {

    @Id
    @Column
    private String id;
    private String registeredClientId;
    private String principalName;
    private String authorizationGrantType;
    @Column(length = 1000)
    private String authorizedScopes;
    @Column(columnDefinition = "text")
    private String attributes;
    @Column(length = 500)
    private String state;

    @Column(columnDefinition = "text")
    private String authorizationCodeValue;
    private Instant authorizationCodeIssuedAt;
    private Instant authorizationCodeExpiresAt;
    @Column(columnDefinition = "text")
    private String authorizationCodeMetadata;

    @Column(columnDefinition = "text")
    private String accessTokenValue;
    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;
    @Column(columnDefinition = "text")
    private String accessTokenMetadata;
    private String accessTokenType;
    @Column(length = 1000)
    private String accessTokenScopes;

    @Column(columnDefinition = "text")
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;
    @Column(columnDefinition = "text")
    private String refreshTokenMetadata;

    @Column(columnDefinition = "text")
    private String oidcIdTokenValue;
    private Instant oidcIdTokenIssuedAt;
    private Instant oidcIdTokenExpiresAt;
    @Column(columnDefinition = "text")
    private String oidcIdTokenMetadata;
    @Column(columnDefinition = "text")
    private String oidcIdTokenClaims;

    @Column(columnDefinition = "text")
    private String userCodeValue;
    private Instant userCodeIssuedAt;
    private Instant userCodeExpiresAt;
    @Column(columnDefinition = "text")
    private String userCodeMetadata;

    @Column(columnDefinition = "text")
    private String deviceCodeValue;
    private Instant deviceCodeIssuedAt;
    private Instant deviceCodeExpiresAt;
    @Column(columnDefinition = "text")
    private String deviceCodeMetadata;
}
