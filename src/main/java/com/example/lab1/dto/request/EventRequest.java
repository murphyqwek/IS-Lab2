package com.example.lab1.dto.request;

import com.example.lab1.entity.EventType;
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
