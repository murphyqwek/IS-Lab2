package com.example.is.dto.history;

import com.example.is.entity.UserRole;

import java.time.Instant;

public record ImportHistoryDTO(Instant cursorCreatedAt,
                               Long cursorId,
                               int size,
                               String username,
                               UserRole role) {
}
