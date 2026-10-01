package com.example.is.dto.filter;

public enum TicketSortField {

    NAME("name"),
    EVENT_NAME("event.name"),
    EVENT_DESCRIPTION("event.description"),
    VENUE_NAME("venue.name");

    private final String property;

    TicketSortField(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}