package com.example.lab1.dto.response;

import com.example.lab1.entity.EventType;

public record EventResponse(
        Integer id,
        String name,
        String description,
        EventType eventType
) {
}
