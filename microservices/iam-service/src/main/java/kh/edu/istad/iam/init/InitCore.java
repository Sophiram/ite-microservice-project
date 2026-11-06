
package kh.edu.istad.iam.init;

import kh.edu.istad.iam.domain.Permission;
import kh.edu.istad.iam.domain.Role;
import kh.edu.istad.iam.domain.User;
import kh.edu.istad.iam.feature.permission.PermissionJpaRepository;
import kh.edu.istad.iam.feature.role.RoleJpaRepository;
import kh.edu.istad.iam.feature.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class InitCore {

    private final PermissionJpaRepository permissionRepository;
    private final RoleJpaRepository roleRepository;
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner initCoreData() {
        return args -> initialize();
    }

    @Transactional
    public void initialize() {

        if (userRepository.existsByUsername("sophiram")) {
            return;
        }

        Permission permission = Permission.builder()
                .permission("user:read:all")
                .description("Read all users")
                .build();

        permission = permissionRepository.save(permission);

        Role role = Role.builder()
                .role("ADMIN")
                .description("Admin")
                .build();

        role = roleRepository.save(role);

        User user = User.builder()
                .familyName("Sorn")
                .givenName("Sophiram")
                .email("sornsophiram11@gmail.com")
                .gender("Male")
                .emailVerified(true)
                .username("sophiram")
                .password(passwordEncoder.encode("123"))
                .roles(Set.of(role))
                .permissions(Set.of(permission))
                .build();

        userRepository.save(user);
    }
}
