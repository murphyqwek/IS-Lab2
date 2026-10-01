package com.example.lab1.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public record CoordinatesReferenceRequest(
        @Positive(message = "coordinates id должен быть положительным")
        Long id,

        @Valid
        CoordinatesRequest newObject
) {
}
