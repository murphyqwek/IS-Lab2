package com.example.is.dto.response;

import com.example.is.entity.EventType;

public record EventResponse(
        Integer id,
        String name,
        String description,
        EventType eventType
) {
}
