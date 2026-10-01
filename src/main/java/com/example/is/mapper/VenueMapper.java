package com.example.is.mapper;

import com.example.is.dto.request.VenueRequest;
import com.example.is.dto.response.VenueResponse;
import com.example.is.entity.Venue;
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
