package com.example.is.dto.request;

import com.example.is.entity.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventRequest(
        @NotBlank(message = "Поле name не может быть пустым")
        String name,

        @NotNull(message = "Поле description не может быть пустым")
        String description,

        EventType eventType
) {
}
