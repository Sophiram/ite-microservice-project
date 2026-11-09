package kh.edu.istad.iam.application.command;

import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Address;
import kh.edu.istad.iam.domain.entity.Profile;

public record UpdateUserProfileCommand(
        UUID userUuid,
        Profile profile,
        Address address) { }
