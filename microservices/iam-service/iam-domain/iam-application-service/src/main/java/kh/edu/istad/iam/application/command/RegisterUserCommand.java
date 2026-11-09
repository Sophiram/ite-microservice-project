package kh.edu.istad.iam.application.command;

import kh.edu.istad.iam.domain.entity.Profile;

public record RegisterUserCommand(
        String username,
        String email,
        String password,
        Profile profile)
{ }
