package com.example.lab1.dto.request;

import jakarta.validation.constraints.NotNull;

public record CoordinatesRequest(
        @NotNull(message = "Поле x не может быть пустым")
        Double x,

        @NotNull(message = "Поле y не может быть пустым")
        Double y
) {
}
