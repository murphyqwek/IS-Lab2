package com.example.lab1.dto.response;

import com.example.lab1.entity.VenueType;

public record VenueResponse(
        int id,
        String name,
        Integer capacity,
        VenueType venueType
) {
}
