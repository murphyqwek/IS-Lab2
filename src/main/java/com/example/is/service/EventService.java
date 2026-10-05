package com.example.is.service;

import com.example.is.dto.request.EventReferenceRequest;
import com.example.is.dto.request.EventRequest;
import com.example.is.dto.response.EventResponse;
import com.example.is.entity.Event;
import com.example.is.entity.Ticket;
import com.example.is.exception.BusinessConstraintsException;
import com.example.is.exception.InvalidReferenceException;
import com.example.is.exception.ResourceNotFoundException;
import com.example.is.mapper.EventMapper;
import com.example.is.repository.EventRepository;
import com.example.is.repository.TicketRepository;
import com.example.is.websocket.ChangeType;
import com.example.is.websocket.EntityChangePublisher;
import com.example.is.websocket.EntityType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public EventResponse update(Integer id, EventRequest request) {
        if(isEventNameTakenByAnotherEvent(request.name(), id)) {
            throw new BusinessConstraintsException("Event с названием " + request.name() + " уже есть. Выберите другое название");
        }

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
        if(isEventNameTaken(request.name())) {
            throw new BusinessConstraintsException("Event с названием " + request.name() + " уже есть. Выберите другое название");
        }

        Event event = eventMapper.toEntity(request);
        Event saved = eventRepository.save(event);
        changePublisher.publish(EntityType.EVENT, ChangeType.CREATED, saved.getId());
        return saved;
    }

    private boolean isEventNameTaken(String eventName) {
        return eventRepository.existsEventByName(eventName);
    }

    private boolean isEventNameTakenByAnotherEvent(String eventName, Integer id) {
        return eventRepository.existsEventByNameAndIdNot(eventName, id);
    }

    private Event find(Integer id) {
        return eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event с id=" + id + " не найден"));
    }
}
