package com.example.lab1.mapper;

import com.example.lab1.dto.request.CoordinatesRequest;
import com.example.lab1.dto.response.CoordinatesResponse;
import com.example.lab1.entity.Coordinates;
import org.springframework.stereotype.Component;

@Component
public class CoordinatesMapper {

    public Coordinates toEntity(CoordinatesRequest request) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(request.x());
        coordinates.setY(request.y());
        return coordinates;
    }

    public CoordinatesResponse toResponse(Coordinates coordinates) {
        return new CoordinatesResponse(
                coordinates.getId(),
                coordinates.getX(),
                coordinates.getY()
        );
    }
}
