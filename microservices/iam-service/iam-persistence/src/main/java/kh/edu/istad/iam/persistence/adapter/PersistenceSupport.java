package kh.edu.istad.iam.persistence.adapter;

import java.util.Set;
import java.util.function.Function;
import kh.edu.istad.iam.application.port.out.PageDirection;
import kh.edu.istad.iam.application.port.out.PageQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

final class PersistenceSupport {
    private PersistenceSupport() { }

    static PageRequest pageable(PageQuery query, Set<String> allowedSorts) {
        String sort = allowedSorts.contains(query.sort()) ? query.sort() : "id";
        Sort.Direction direction = query.direction() == PageDirection.DESC
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(query.page(), query.size(), Sort.by(direction, sort));
    }

    static <S, T> kh.edu.istad.iam.application.port.out.PageResult<T> page(
            Page<S> source, Function<S, T> mapper) {
        return new kh.edu.istad.iam.application.port.out.PageResult<>(source.getContent().stream()
                .map(mapper).toList(), source.getNumber(), source.getSize(), source.getTotalElements());
    }
}
