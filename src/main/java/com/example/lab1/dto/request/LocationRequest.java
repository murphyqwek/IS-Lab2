package com.example.lab1.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LocationRequest(
        @NotNull(message = "Поле x не может быть пустым")
        Float x,

        @NotNull(message = "Поле y не может быть пустым")
        Long y,

        @NotNull(message = "Поле z не может быть пустым")
        Long z,

        @Size(max = 692, message = "Максимальная длина поля name - 692 символа")
        String name
) {
}
