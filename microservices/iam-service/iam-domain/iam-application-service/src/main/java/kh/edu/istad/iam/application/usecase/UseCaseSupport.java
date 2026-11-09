package kh.edu.istad.iam.application.usecase;

import java.util.Optional;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

public class UseCaseSupport {
    private UseCaseSupport() { }

    public static <T> T require(Optional<T> value, String entity) {
        return value.orElseThrow(() -> new DomainException(ErrorCode.ENTITY_NOT_FOUND,
                entity + " was not found"));
    }
}
