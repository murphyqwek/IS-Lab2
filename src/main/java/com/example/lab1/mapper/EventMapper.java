package com.example.lab1.mapper;

import com.example.lab1.dto.request.EventRequest;
import com.example.lab1.dto.response.EventResponse;
import com.example.lab1.entity.Event;
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
