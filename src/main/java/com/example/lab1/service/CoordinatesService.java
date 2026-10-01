package com.example.lab1.service;

import com.example.lab1.dto.request.CoordinatesReferenceRequest;
import com.example.lab1.dto.request.CoordinatesRequest;
import com.example.lab1.dto.response.CoordinatesResponse;
import com.example.lab1.entity.Coordinates;
import com.example.lab1.entity.Ticket;
import com.example.lab1.exception.InvalidReferenceException;
import com.example.lab1.exception.ResourceNotFoundException;
import com.example.lab1.mapper.CoordinatesMapper;
import com.example.lab1.repository.CoordinatesRepository;
import com.example.lab1.repository.TicketRepository;
import com.example.lab1.websocket.ChangeType;
import com.example.lab1.websocket.EntityChangePublisher;
import com.example.lab1.websocket.EntityType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;
    private final TicketRepository ticketRepository;
    private final CoordinatesMapper coordinatesMapper;
    private final EntityChangePublisher changePublisher;

    public CoordinatesService(
            CoordinatesRepository coordinatesRepository,
            TicketRepository ticketRepository,
            CoordinatesMapper coordinatesMapper,
            EntityChangePublisher changePublisher
    ) {
        this.coordinatesRepository = coordinatesRepository;
        this.ticketRepository = ticketRepository;
        this.coordinatesMapper = coordinatesMapper;
        this.changePublisher = changePublisher;
    }

    @Transactional(readOnly = true)
    public List<CoordinatesResponse> getAll() {
        return coordinatesRepository.findAll().stream().map(coordinatesMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CoordinatesResponse getById(Long id) {
        return coordinatesMapper.toResponse(find(id));
    }

    @Transactional
    public CoordinatesResponse create(CoordinatesRequest request) {
        return coordinatesMapper.toResponse(createEntity(request));
    }

    @Transactional
    public Coordinates resolve(CoordinatesReferenceRequest request) {
        if (request == null) {
            throw new InvalidReferenceException("Поле 'coordinates' не может быть null");
        }

        ReferenceRequestValidator.requireExactlyOne(request.id(), request.newObject(), "coordinates");

        if (request.id() != null) {
            return find(request.id());
        }

        return createEntity(request.newObject());
    }

    @Transactional
    public CoordinatesResponse update(Long id, CoordinatesRequest request) {
        Coordinates coordinates = find(id);

        coordinates.setX(request.x());
        coordinates.setY(request.y());

        changePublisher.publish(EntityType.COORDINATES, ChangeType.UPDATED, id);

        return coordinatesMapper.toResponse(coordinates);
    }

    @Transactional
    public void delete(Long id, Long replacementId) {
        Coordinates coordinates = find(id);
        List<Ticket> tickets = ticketRepository.findAllByCoordinates_Id(id);

        if (!tickets.isEmpty()) {
            ReferenceRequestValidator.requireReplacement(replacementId, "Coordinates");
            ReferenceRequestValidator.requireDifferent(id, replacementId, "Coordinates");

            Coordinates replacement = find(replacementId);

            for (Ticket ticket : tickets) {
                ticket.setCoordinates(replacement);
                changePublisher.publish(EntityType.TICKET, ChangeType.UPDATED, ticket.getId());
            }
        }

        coordinatesRepository.delete(coordinates);
        changePublisher.publish(EntityType.COORDINATES, ChangeType.DELETED, id);
    }

    private Coordinates createEntity(CoordinatesRequest request) {
        Coordinates coordinates = coordinatesMapper.toEntity(request);
        Coordinates saved = coordinatesRepository.save(coordinates);
        changePublisher.publish(EntityType.COORDINATES, ChangeType.CREATED, saved.getId());
        return saved;
    }

    private Coordinates find(Long id) {
        return coordinatesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinates с id=" + id + " не найдены"));
    }
}
