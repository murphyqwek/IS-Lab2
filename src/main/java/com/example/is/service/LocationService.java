package com.example.is.service;

import com.example.is.dto.request.LocationReferenceRequest;
import com.example.is.dto.request.LocationRequest;
import com.example.is.dto.response.LocationResponse;
import com.example.is.entity.Location;
import com.example.is.entity.Person;
import com.example.is.exception.InvalidReferenceException;
import com.example.is.exception.ResourceNotFoundException;
import com.example.is.mapper.LocationMapper;
import com.example.is.repository.LocationRepository;
import com.example.is.repository.PersonRepository;
import com.example.is.websocket.ChangeType;
import com.example.is.websocket.EntityChangePublisher;
import com.example.is.websocket.EntityType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final PersonRepository personRepository;
    private final LocationMapper locationMapper;
    private final EntityChangePublisher changePublisher;

    public LocationService(
            LocationRepository locationRepository,
            PersonRepository personRepository,
            LocationMapper locationMapper,
            EntityChangePublisher changePublisher
    ) {
        this.locationRepository = locationRepository;
        this.personRepository = personRepository;
        this.locationMapper = locationMapper;
        this.changePublisher = changePublisher;
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> getAll() {
        return locationRepository.findAll().stream().map(locationMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LocationResponse getById(Long id) {
        return locationMapper.toResponse(find(id));
    }

    @Transactional
    public LocationResponse create(LocationRequest request) {
        return locationMapper.toResponse(createEntity(request));
    }

    @Transactional
    public Location resolve(LocationReferenceRequest request) {
        if (request == null) {
            throw new InvalidReferenceException("Поле 'location' не может быть null");
        }

        ReferenceRequestValidator.requireExactlyOne(request.id(), request.newObject(), "location");

        if (request.id() != null) {
            return find(request.id());
        }

        return createEntity(request.newObject());
    }

    @Transactional
    public LocationResponse update(Long id, LocationRequest request) {
        Location location = find(id);

        location.setX(request.x());
        location.setY(request.y());
        location.setZ(request.z());
        location.setName(request.name());

        changePublisher.publish(EntityType.LOCATION, ChangeType.UPDATED, id);

        return locationMapper.toResponse(location);
    }

    @Transactional
    public void delete(Long id, Long replacementId) {
        Location location = find(id);
        List<Person> persons = personRepository.findAllByLocation_Id(id);

        if (!persons.isEmpty()) {
            ReferenceRequestValidator.requireReplacement(replacementId, "Location");
            ReferenceRequestValidator.requireDifferent(id, replacementId, "Location");

            Location replacement = find(replacementId);

            for (Person person : persons) {
                person.setLocation(replacement);
                changePublisher.publish(EntityType.PERSON, ChangeType.UPDATED, person.getId());
            }
        }

        locationRepository.delete(location);
        changePublisher.publish(EntityType.LOCATION, ChangeType.DELETED, id);
    }

    private Location createEntity(LocationRequest request) {
        Location location = locationMapper.toEntity(request);
        Location saved = locationRepository.save(location);
        changePublisher.publish(EntityType.LOCATION, ChangeType.CREATED, saved.getId());
        return saved;
    }

    private Location find(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location с id=" + id + " не найдена"));
    }
}
