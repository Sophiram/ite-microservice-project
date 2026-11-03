package kh.edu.istad.platform.customer.domain.valueobject;

/**
 * Represents the loyalty segment of a customer.
 * - SILVER: Basic tier (new customers)
 * - GOLD:   Mid tier (regular customers)
 * - PREMIUM: Top tier (high-value customers)
 */
public enum CustomerSegment {
    SILVER,
    GOLD,
    PREMIUM
}
