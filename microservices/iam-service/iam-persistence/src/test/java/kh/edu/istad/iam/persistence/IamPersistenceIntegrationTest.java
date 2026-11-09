package kh.edu.istad.iam.persistence;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.entity.Address;
import kh.edu.istad.iam.domain.entity.Identity;
import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.domain.entity.Profile;
import kh.edu.istad.iam.domain.entity.Provider;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.domain.entity.RolePermission;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserPermission;
import kh.edu.istad.iam.domain.entity.UserRole;
import kh.edu.istad.iam.persistence.adapter.AssignmentPersistenceAdapter;
import kh.edu.istad.iam.persistence.adapter.IdentityPersistenceAdapter;
import kh.edu.istad.iam.persistence.adapter.PermissionPersistenceAdapter;
import kh.edu.istad.iam.persistence.adapter.RolePersistenceAdapter;
import kh.edu.istad.iam.persistence.adapter.UserPersistenceAdapter;
import kh.edu.istad.iam.persistence.config.JpaAuditConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml",
        "spring.liquibase.parameters.iam.admin.password-hash="
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(classes = IamPersistenceIntegrationTest.TestApplication.class)
@Import({JpaAuditConfiguration.class, UserPersistenceAdapter.class, RolePersistenceAdapter.class,
        PermissionPersistenceAdapter.class, IdentityPersistenceAdapter.class, AssignmentPersistenceAdapter.class})
class IamPersistenceIntegrationTest {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired UserRepository users;
    @Autowired RoleRepository roles;
    @Autowired PermissionRepository permissions;
    @Autowired IdentityRepository identities;
    @Autowired AssignmentRepository assignments;
    @Autowired EntityManager entityManager;

    @Test
    void persistsDomainRowsAndAuditsAssignmentsWithoutManyToManyMappings() {
        User user = User.register("persistence-test", "persistence@example.com", "hash",
                Profile.of("Persistence", "Tester", "en"), Instant.now());
        user.updateAddress(new Address("No. 1", "Phnom Penh", "Phnom Penh", "12000", "KH"));
        user = users.save(user);
        Role role = roles.save(Role.create("AUDITOR", "Audit access"));
        Permission permission = permissions.save(Permission.create("audit", "read", "Read audit data"));

        UserRole userRole = assignments.save(UserRole.assign(user.getId(), role, "test", null,
                "integration", Instant.now()));
        RolePermission rolePermission = assignments.save(RolePermission.assign(role, permission, "test", null,
                "integration", Instant.now()));
        UserPermission userPermission = assignments.save(UserPermission.assign(user.getId(), permission,
                kh.edu.istad.iam.domain.entity.Effect.GRANT, "test", null, "integration", Instant.now()));
        Identity identity = identities.save(Identity.link(user.getId(), Provider.GITHUB, "external-123",
                "social@example.com", true, "Social User", "social-user", null, java.util.Map.of(), Instant.now()));

        assertNotNull(user.getCreatedAt());
        assertEquals("persistence@example.com", users.findById(user.getId()).orElseThrow().getEmail());
        assertEquals("Persistence", users.findById(user.getId()).orElseThrow().getProfile().givenName());
        assertEquals("KH", users.findById(user.getId()).orElseThrow().getAddress().country());
        assertEquals("AUDITOR", assignments.findUserRoles(user.getId()).getFirst().getRole().getName());
        assertEquals("audit:read", assignments.findRolePermissions(java.util.List.of(role.getId()))
                .getFirst().getPermission().getName());
        assertEquals("GITHUB", identities.findByProviderAndSubject(Provider.GITHUB, "external-123")
                .orElseThrow().getProvider().name());
        assertEquals("GRANT", assignments.findUserPermissions(user.getId()).getFirst().getEffect().name());
        assertNotNull(userRole.getId());
        assertNotNull(rolePermission.getId());
        assertNotNull(userPermission.getId());
        assertNotNull(identity.getId());

        user.delete("test", Instant.now());
        users.save(user);
        entityManager.flush();
        entityManager.clear();
        assertTrue(users.findById(user.getId()).isEmpty());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan("kh.edu.istad.iam.persistence.entity")
    @EnableJpaRepositories("kh.edu.istad.iam.persistence.repository")
    static class TestApplication { }
}
