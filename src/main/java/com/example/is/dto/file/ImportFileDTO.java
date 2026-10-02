package com.example.is.dto.file;

import com.example.is.dto.request.*;
import com.example.is.entity.Location;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = false)
public class ImportFileDTO {
    private List<TicketRequest> tickets;
    private List<CoordinatesRequest> coordinates;
    private List<VenueRequest> venues;
    private List<LocationRequest> locations;
    private List<PersonRequest> persons;
    private List<EventRequest> events;

    public List<TicketRequest> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketRequest> tickets) {
        this.tickets = tickets;
    }

    public List<CoordinatesRequest> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<CoordinatesRequest> coordinates) {
        this.coordinates = coordinates;
    }

    public List<VenueRequest> getVenues() {
        return venues;
    }

    public void setVenues(List<VenueRequest> venues) {
        this.venues = venues;
    }

    public List<LocationRequest> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationRequest> locations) {
        this.locations = locations;
    }

    public List<PersonRequest> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonRequest> persons) {
        this.persons = persons;
    }

    public List<EventRequest> getEvents() {
        return events;
    }

    public void setEvents(List<EventRequest> events) {
        this.events = events;
    }
}
