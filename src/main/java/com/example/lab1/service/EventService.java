package com.example.lab1.service;

import com.example.lab1.dto.request.EventReferenceRequest;
import com.example.lab1.dto.request.EventRequest;
import com.example.lab1.dto.response.EventResponse;
import com.example.lab1.entity.Event;
import com.example.lab1.entity.Ticket;
import com.example.lab1.exception.InvalidReferenceException;
import com.example.lab1.exception.ResourceNotFoundException;
import com.example.lab1.mapper.EventMapper;
import com.example.lab1.repository.EventRepository;
import com.example.lab1.repository.TicketRepository;
import com.example.lab1.websocket.ChangeType;
import com.example.lab1.websocket.EntityChangePublisher;
import com.example.lab1.websocket.EntityType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final EventMapper eventMapper;
    private final EntityChangePublisher changePublisher;

    public EventService(
            EventRepository eventRepository,
            TicketRepository ticketRepository,
            EventMapper eventMapper,
            EntityChangePublisher changePublisher
    ) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.eventMapper = eventMapper;
        this.changePublisher = changePublisher;
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAll() {
        return eventRepository.findAll().stream().map(eventMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EventResponse getById(Integer id) {
        return eventMapper.toResponse(find(id));
    }

    @Transactional
    public EventResponse create(EventRequest request) {
        return eventMapper.toResponse(createEntity(request));
    }

    @Transactional
    public Event resolve(EventReferenceRequest request) {
        if (request == null) {
            throw new InvalidReferenceException("Поле 'event' не может быть null");
        }

        ReferenceRequestValidator.requireExactlyOne(request.id(), request.newObject(), "event");

        if (request.id() != null) {
            return find(request.id());
        }

        return createEntity(request.newObject());
    }

    @Transactional
    public EventResponse update(Integer id, EventRequest request) {
        Event event = find(id);

        event.setName(request.name());
        event.setDescription(request.description());
        event.setEventType(request.eventType());

        changePublisher.publish(EntityType.EVENT, ChangeType.UPDATED, id);

        return eventMapper.toResponse(event);
    }

    @Transactional
    public void delete(Integer id, Integer replacementId) {
        Event event = find(id);
        List<Ticket> tickets = ticketRepository.findAllByEvent_Id(id);

        if (!tickets.isEmpty()) {
            ReferenceRequestValidator.requireReplacement(replacementId, "Event");
            ReferenceRequestValidator.requireDifferent(id, replacementId, "Event");

            Event replacement = find(replacementId);

            for (Ticket ticket : tickets) {
                ticket.setEvent(replacement);
                changePublisher.publish(EntityType.TICKET, ChangeType.UPDATED, ticket.getId());
            }
        }

        eventRepository.delete(event);
        changePublisher.publish(EntityType.EVENT, ChangeType.DELETED, id);
    }

    private Event createEntity(EventRequest request) {
        Event event = eventMapper.toEntity(request);
        Event saved = eventRepository.save(event);
        changePublisher.publish(EntityType.EVENT, ChangeType.CREATED, saved.getId());
        return saved;
    }

    private Event find(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event с id=" + id + " не найден"));
    }
}
