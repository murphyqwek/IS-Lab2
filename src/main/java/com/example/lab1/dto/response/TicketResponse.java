package com.example.lab1.dto.response;

import com.example.lab1.entity.TicketType;

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
