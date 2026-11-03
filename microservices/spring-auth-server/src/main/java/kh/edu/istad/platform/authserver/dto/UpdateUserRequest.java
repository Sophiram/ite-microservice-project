package kh.edu.istad.platform.authserver.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String email;
    private String password;
    private Boolean enabled;
    private Set<String> roles;
}
