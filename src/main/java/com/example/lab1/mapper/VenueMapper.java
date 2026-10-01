package com.example.lab1.mapper;

import com.example.lab1.dto.request.VenueRequest;
import com.example.lab1.dto.response.VenueResponse;
import com.example.lab1.entity.Venue;
import org.springframework.stereotype.Component;

@Component
public class VenueMapper {

    public Venue toEntity(VenueRequest request) {
        Venue venue = new Venue();
        venue.setName(request.name());
        venue.setCapacity(request.capacity());
        venue.setType(request.venueType());
        return venue;
    }

    public VenueResponse toResponse(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getCapacity(),
                venue.getType()
        );
    }
}
