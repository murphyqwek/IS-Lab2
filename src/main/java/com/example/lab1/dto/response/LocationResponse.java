package com.example.lab1.dto.response;

public record LocationResponse(
        long id,
        float x,
        long y,
        Long z,
        String name
) {
}
