package com.example.lab1.dto.filter;

public record TicketFilter(
        String name,
        String eventName,
        String eventDescription,
        String venueName
) {
}