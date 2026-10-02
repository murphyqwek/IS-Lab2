package com.example.is.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank
        @Min(4) @Max(50)
        String username,
        @NotBlank
        @Min(8) @Max(25)
        String password) {
}
