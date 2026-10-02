package com.example.is.dto.response;
import java.time.Instant;
import java.util.List;

public record CursorPage<T>(
        List<T> content,
        Long nextCursorId,
        Instant nextCursorCreatedAt,
        boolean hasNext) {
}
