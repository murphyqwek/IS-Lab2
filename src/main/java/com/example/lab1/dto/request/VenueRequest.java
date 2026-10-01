package com.example.lab1.dto.request;

import com.example.lab1.entity.VenueType;
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
