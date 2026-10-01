package com.example.lab1.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public record EventReferenceRequest(
        @Positive(message = "event id должен быть положительным")
        Integer id,

        @Valid
        EventRequest newObject
) {
}
