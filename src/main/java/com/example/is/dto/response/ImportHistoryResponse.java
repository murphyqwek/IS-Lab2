package com.example.is.dto.response;

import com.example.is.entity.ImportHistoryStatus;

import java.time.Instant;

public record ImportHistoryResponse(
        Long id,
        String username,
        Integer importedObjectsCount,
        Instant createdAt,
        ImportHistoryStatus status) {
}
