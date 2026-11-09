package kh.edu.istad.iam.rest.dto;

public record PageMetadata(
        int number,
        int size,
        long totalElements,
        int totalPages

) { }
