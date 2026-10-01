package com.example.is.mapper;

import com.example.is.dto.request.EventRequest;
import com.example.is.dto.response.EventResponse;
import com.example.is.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(EventRequest request) {
        Event event = new Event();
        event.setName(request.name());
        event.setDescription(request.description());
        event.setEventType(request.eventType());
        return event;
    }

    public EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getEventType()
        );
    }
}
