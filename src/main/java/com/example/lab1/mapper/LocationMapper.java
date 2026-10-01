package com.example.lab1.mapper;

import com.example.lab1.dto.request.LocationRequest;
import com.example.lab1.dto.response.LocationResponse;
import com.example.lab1.entity.Location;
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
