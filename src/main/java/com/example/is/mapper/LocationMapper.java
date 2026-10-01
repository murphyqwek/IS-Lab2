package com.example.is.mapper;

import com.example.is.dto.request.LocationRequest;
import com.example.is.dto.response.LocationResponse;
import com.example.is.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toEntity(LocationRequest request) {
        Location location = new Location();
        location.setX(request.x());
        location.setY(request.y());
        location.setZ(request.z());
        location.setName(request.name());
        return location;
    }

    public LocationResponse toResponse(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getName()
        );
    }
}
