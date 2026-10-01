package com.example.lab1.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public record VenueReferenceRequest(
        @Positive(message = "venue id должен быть положительным")
        Integer id,

        @Valid
        VenueRequest newObject
) {
}
