package com.example.is.dto.filter;

public record TicketFilter(
        String name,
        String eventName,
        String eventDescription,
        String venueName
) {
}