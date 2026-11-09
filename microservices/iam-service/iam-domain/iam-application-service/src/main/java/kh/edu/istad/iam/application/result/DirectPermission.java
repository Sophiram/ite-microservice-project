package kh.edu.istad.iam.application.result;

import kh.edu.istad.iam.domain.entity.Effect;

public record DirectPermission(
        String name,
        Effect effect
) { }
