package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;

/**
 * DTO returned from the aggregation pipeline.
 *
 * Represents the count of customers grouped by both status AND segment.
 *
 * Example:
 * { "status": "ACTIVE",   "segment": "SILVER", "total": 8  }
 * { "status": "ACTIVE",   "segment": "GOLD",   "total": 3  }
 * { "status": "INACTIVE", "segment": "SILVER", "total": 2  }
 */
public record CustomerStatistic(
        CustomerStatus status,
        CustomerSegment segment,
        long total
) {
}
