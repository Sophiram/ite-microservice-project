package kh.edu.istad.iam.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record UserRoleAuthority(UUID uuid, String name, Instant expiresAt) { }
