package kh.edu.istad.iam.application.command;

import java.util.List;
import java.util.UUID;

import kh.edu.istad.iam.domain.entity.Address;
import kh.edu.istad.iam.domain.entity.Profile;

public record CreateUserCommand(
        String username,
        String email,
        String password,
        Profile profile,
        Address address,
        List<UUID> roleUuids
) {
    public CreateUserCommand {
        roleUuids = roleUuids == null ? List.of() : List.copyOf(roleUuids);
    }
}
