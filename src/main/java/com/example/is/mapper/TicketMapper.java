package com.example.is.mapper;

import com.example.is.dto.response.TicketResponse;
import com.example.is.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    private final CoordinatesMapper coordinatesMapper;
    private final EventMapper eventMapper;
    private final PersonMapper personMapper;
    private final VenueMapper venueMapper;

    public TicketMapper(
            CoordinatesMapper coordinatesMapper,
            EventMapper eventMapper,
            PersonMapper personMapper,
            VenueMapper venueMapper
    ) {
        this.coordinatesMapper = coordinatesMapper;
        this.eventMapper = eventMapper;
        this.personMapper = personMapper;
        this.venueMapper = venueMapper;
    }

    public TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getName(),
                coordinatesMapper.toResponse(ticket.getCoordinates()),
                ticket.getCreationDate(),
                ticket.getPerson() == null
                        ? null
                        : personMapper.toResponse(ticket.getPerson()),
                eventMapper.toResponse(ticket.getEvent()),
                ticket.getPrice(),
                ticket.getType(),
                ticket.getDiscount(),
                ticket.getNumber(),
                ticket.getVenue() == null
                        ? null
                        : venueMapper.toResponse(ticket.getVenue())
        );
    }
}
