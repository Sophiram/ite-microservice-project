package kh.edu.istad.iam.application.port.out;

public record PageQuery(int page, int size, String sort, PageDirection direction) {
    public PageQuery {
        if (page < 0) {
            throw new IllegalArgumentException("page must be zero or greater");
        }
        if (size < 1 || size > 200) {
            throw new IllegalArgumentException("size must be between 1 and 200");
        }
        sort = sort == null || sort.isBlank() ? "id" : sort;
        direction = direction == null ? PageDirection.ASC : direction;
    }
}
