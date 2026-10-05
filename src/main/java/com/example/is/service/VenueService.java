package com.example.is.service;

import com.example.is.dto.request.VenueReferenceRequest;
import com.example.is.dto.request.VenueRequest;
import com.example.is.dto.response.VenueResponse;
import com.example.is.entity.Ticket;
import com.example.is.entity.Venue;
import com.example.is.exception.BusinessConstraintsException;
import com.example.is.exception.InvalidReferenceException;
import com.example.is.exception.ResourceNotFoundException;
import com.example.is.mapper.VenueMapper;
import com.example.is.repository.TicketRepository;
import com.example.is.repository.VenueRepository;
import com.example.is.websocket.ChangeType;
import com.example.is.websocket.EntityChangePublisher;
import com.example.is.websocket.EntityType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public VenueResponse create(VenueRequest request) {
        return venueMapper.toResponse(createEntity(request));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public VenueResponse update(Integer id, VenueRequest request) {
        if(isVenueNameTakenByAnotherEvent(request.name(), id)) {
            throw new BusinessConstraintsException("Venue с названием " + request.name() + " уже есть. Выберите другое название");
        }

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
        if(isVenueNameTaken(request.name())) {
            throw new BusinessConstraintsException("Venue с названием " + request.name() + " уже есть. Выберите другое название");
        }

        Venue venue = venueMapper.toEntity(request);
        Venue saved = venueRepository.save(venue);
        changePublisher.publish(EntityType.VENUE, ChangeType.CREATED, saved.getId());
        return saved;
    }

    private Venue find(Integer id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue с id=" + id + " не найден"));
    }

    private boolean isVenueNameTaken(String venueName) {
        return venueRepository.existsVenueByName(venueName);
    }

    private boolean isVenueNameTakenByAnotherEvent(String venueName, Integer id) {
        return venueRepository.existsVenueByNameAndIdNot(venueName, id);
    }
}
