package kh.edu.istad.platform.authserver.service;

import java.util.List;
import java.util.Set;

import kh.edu.istad.platform.authserver.domain.UserAccount;
import kh.edu.istad.platform.authserver.dto.CreateUserRequest;
import kh.edu.istad.platform.authserver.dto.LoginRequest;
import kh.edu.istad.platform.authserver.dto.LoginResponse;
import kh.edu.istad.platform.authserver.dto.UpdateUserRequest;
import kh.edu.istad.platform.authserver.dto.UserResponse;
import kh.edu.istad.platform.authserver.repository.UserAccountRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty");
        }
        if (userAccountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username '" + request.getUsername() + "' is already taken");
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setRoles(request.getRoles() != null && !request.getRoles().isEmpty()
                ? request.getRoles()
                : Set.of("USER"));

        UserAccount saved = userAccountRepository.save(user);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userAccountRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        UserAccount user = userAccountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        UserAccount user = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
        return mapToResponse(user);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        UserAccount user = userAccountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(request.getRoles());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        UserAccount updated = userAccountRepository.save(user);
        return mapToResponse(updated);
    }

    public void deleteUser(Long id) {
        if (!userAccountRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
        userAccountRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required");
        }

        UserAccount user = userAccountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return LoginResponse.builder()
                .message("Login successful")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }

    private UserResponse mapToResponse(UserAccount user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(user.getRoles())
                .build();
    }
}
