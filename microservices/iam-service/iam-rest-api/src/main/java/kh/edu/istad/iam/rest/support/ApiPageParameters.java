package kh.edu.istad.iam.rest.support;

import java.util.Locale;
import kh.edu.istad.iam.application.port.out.PageDirection;
import kh.edu.istad.iam.application.port.out.PageQuery;

public final class ApiPageParameters {
    private ApiPageParameters() { }

    public static PageQuery toQuery(int page, int size, String sort, String direction) {
        String property = sort;
        String order = direction;
        if (property != null && property.contains(",")) {
            String[] parts = property.split(",", 2);
            property = parts[0];
            if (order == null || order.isBlank()) order = parts[1];
        }
        PageDirection parsedDirection = order == null || order.isBlank()
                ? PageDirection.ASC
                : PageDirection.valueOf(order.trim().toUpperCase(Locale.ROOT));
        return new PageQuery(page, size, property, parsedDirection);
    }
}
