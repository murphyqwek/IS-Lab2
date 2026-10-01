package com.example.is.dto.request;

import com.example.is.entity.Color;
import com.example.is.entity.Country;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public record PersonRequest(
        Color eyeColor,
        Color hairColor,

        @Valid
        LocationReferenceRequest location,

        @Positive(message = "Поле weight должно быть положительным числом")
        Float weight,

        Country nationality
) {
}
