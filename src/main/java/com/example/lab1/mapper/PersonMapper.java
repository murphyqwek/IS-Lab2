package com.example.lab1.mapper;

import com.example.lab1.dto.response.PersonResponse;
import com.example.lab1.entity.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    private final LocationMapper locationMapper;

    public PersonMapper(LocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    public PersonResponse toResponse(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getEyeColor(),
                person.getHairColor(),
                person.getLocation() == null
                        ? null
                        : locationMapper.toResponse(person.getLocation()),
                person.getWeight(),
                person.getNationality()
        );
    }
}
