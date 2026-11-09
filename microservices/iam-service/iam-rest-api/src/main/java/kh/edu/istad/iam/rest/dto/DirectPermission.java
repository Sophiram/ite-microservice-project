package kh.edu.istad.iam.rest.dto;

import kh.edu.istad.iam.domain.entity.Effect;

public record DirectPermission(String name, Effect effect) { }
