package com.example.is.dto.response;

public record LocationResponse(
        long id,
        float x,
        long y,
        Long z,
        String name
) {
}
