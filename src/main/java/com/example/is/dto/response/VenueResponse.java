package com.example.is.dto.response;

import com.example.is.entity.VenueType;

public record VenueResponse(
        int id,
        String name,
        Integer capacity,
        VenueType venueType
) {
}
