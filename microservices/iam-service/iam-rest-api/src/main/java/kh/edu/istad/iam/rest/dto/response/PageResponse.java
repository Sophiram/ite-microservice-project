package kh.edu.istad.iam.rest.dto.response;

import java.util.List;
import java.util.function.Function;
import kh.edu.istad.iam.application.port.out.PageResult;
import kh.edu.istad.iam.rest.dto.PageMetadata;

public record PageResponse<T>(
        List<T> content,
        PageMetadata page

) {
    public PageResponse {
        content = List.copyOf(content);
    }

    public static <S, T> PageResponse<T> from(PageResult<S> result, Function<S, T> mapper) {
        int totalPages = result.size() == 0 ? 0
                : (int) Math.ceil((double) result.totalElements() / result.size());
        return new PageResponse<>(result.content().stream().map(mapper).toList(),
                new PageMetadata(result.page(), result.size(), result.totalElements(), totalPages));
    }
}
