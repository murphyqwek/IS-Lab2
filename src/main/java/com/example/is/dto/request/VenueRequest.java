package com.example.is.dto.request;

import com.example.is.entity.VenueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VenueRequest(
        @NotBlank(message = "Поле name не может быть пустым")
        String name,

        @NotNull(message = "Поле capacity не может быть пустым")
        @Positive(message = "Поле capacity может быть только положительным числом")
        Integer capacity,

        VenueType venueType
) {
}
