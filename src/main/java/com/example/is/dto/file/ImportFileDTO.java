package com.example.is.dto.file;

import com.example.is.dto.request.*;
import com.example.is.entity.Location;

import java.util.List;

public class ImportFileDTO {
    private List<TicketRequest> tickets;
    private List<CoordinatesRequest> coordinates;
    private List<VenueRequest> venue;
    private List<Location> location;
    private List<PersonRequest> persons;
    private List<EventRequest> events;
}
