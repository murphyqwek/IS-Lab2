package com.example.is.dto.response;

import com.example.is.entity.TicketType;

import java.time.ZonedDateTime;

public record TicketResponse(
        Integer id,
        String name,
        CoordinatesResponse coordinates,
        ZonedDateTime creationDate,
        PersonResponse person,
        EventResponse event,
        int price,
        TicketType ticketType,
        int discount,
        Float number,
        VenueResponse venue
) {
}
