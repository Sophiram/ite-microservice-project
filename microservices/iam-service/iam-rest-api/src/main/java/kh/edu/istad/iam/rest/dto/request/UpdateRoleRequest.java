package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(@Size(max = 255) String description) { }
