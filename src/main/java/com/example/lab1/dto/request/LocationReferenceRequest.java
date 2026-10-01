package com.example.lab1.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public record LocationReferenceRequest(
        @Positive(message = "location id должен быть положительным")
        Long id,

        @Valid
        LocationRequest newObject
) {
}
