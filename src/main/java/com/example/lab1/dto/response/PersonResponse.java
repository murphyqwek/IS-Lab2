package com.example.lab1.dto.response;

import com.example.lab1.entity.Color;
import com.example.lab1.entity.Country;

public record PersonResponse(
        long id,
        Color eyeColor,
        Color hairColor,
        LocationResponse location,
        Float weight,
        Country nationality
) {
}
