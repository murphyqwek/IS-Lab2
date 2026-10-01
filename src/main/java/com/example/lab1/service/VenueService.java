package com.example.lab1.service;

import com.example.lab1.dto.request.VenueReferenceRequest;
import com.example.lab1.dto.request.VenueRequest;
import com.example.lab1.dto.response.VenueResponse;
import com.example.lab1.entity.Ticket;
import com.example.lab1.entity.Venue;
import com.example.lab1.exception.InvalidReferenceException;
import com.example.lab1.exception.ResourceNotFoundException;
import com.example.lab1.mapper.VenueMapper;
import com.example.lab1.repository.TicketRepository;
import com.example.lab1.repository.VenueRepository;
import com.example.lab1.websocket.ChangeType;
import com.example.lab1.websocket.EntityChangePublisher;
import com.example.lab1.websocket.EntityType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;
    private final TicketRepository ticketRepository;
    private final VenueMapper venueMapper;
    private final EntityChangePublisher changePublisher;

    public VenueService(
            VenueRepository venueRepository,
            TicketRepository ticketRepository,
            VenueMapper venueMapper,
            EntityChangePublisher changePublisher
    ) {
        this.venueRepository = venueRepository;
        this.ticketRepository = ticketRepository;
        this.venueMapper = venueMapper;
        this.changePublisher = changePublisher;
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> getAll() {
        return venueRepository.findAll().stream().map(venueMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VenueResponse getById(Integer id) {
        return venueMapper.toResponse(find(id));
    }

    @Transactional
    public VenueResponse create(VenueRequest request) {
        return venueMapper.toResponse(createEntity(request));
    }

    @Transactional
    public Venue resolve(VenueReferenceRequest request) {
        if (request == null) {
            throw new InvalidReferenceException("Поле 'venue' не может быть null");
        }

        ReferenceRequestValidator.requireExactlyOne(request.id(), request.newObject(), "venue");

        if (request.id() != null) {
            return find(request.id());
        }

        return createEntity(request.newObject());
    }

    @Transactional
    public VenueResponse update(Integer id, VenueRequest request) {
        Venue venue = find(id);

        venue.setName(request.name());
        venue.setCapacity(request.capacity());
        venue.setType(request.venueType());

        changePublisher.publish(EntityType.VENUE, ChangeType.UPDATED, id);

        return venueMapper.toResponse(venue);
    }

    @Transactional
    public void delete(Integer id, Integer replacementId) {
        Venue venue = find(id);
        List<Ticket> tickets = ticketRepository.findAllByVenue_Id(id);

        if (!tickets.isEmpty()) {
            ReferenceRequestValidator.requireReplacement(replacementId, "Venue");
            ReferenceRequestValidator.requireDifferent(id, replacementId, "Venue");

            Venue replacement = find(replacementId);

            for (Ticket ticket : tickets) {
                ticket.setVenue(replacement);
                changePublisher.publish(EntityType.TICKET, ChangeType.UPDATED, ticket.getId());
            }
        }

        venueRepository.delete(venue);
        changePublisher.publish(EntityType.VENUE, ChangeType.DELETED, id);
    }

    private Venue createEntity(VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);
        Venue saved = venueRepository.save(venue);
        changePublisher.publish(EntityType.VENUE, ChangeType.CREATED, saved.getId());
        return saved;
    }

    private Venue find(Integer id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue с id=" + id + " не найден"));
    }
}
