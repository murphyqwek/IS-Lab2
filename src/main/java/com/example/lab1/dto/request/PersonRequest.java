package com.example.lab1.dto.request;

import com.example.lab1.entity.Color;
import com.example.lab1.entity.Country;
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
