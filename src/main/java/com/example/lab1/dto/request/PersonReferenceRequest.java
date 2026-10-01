package com.example.lab1.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public record PersonReferenceRequest(
        @Positive(message = "person id должен быть положительным")
        Long id,

        @Valid
        PersonRequest newObject
) {
}
