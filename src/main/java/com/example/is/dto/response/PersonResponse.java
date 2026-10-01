package com.example.is.dto.response;

import com.example.is.entity.Color;
import com.example.is.entity.Country;

public record PersonResponse(
        long id,
        Color eyeColor,
        Color hairColor,
        LocationResponse location,
        Float weight,
        Country nationality
) {
}
