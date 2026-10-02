package com.example.is.service;

import com.example.is.dto.file.ImportFileDTO;
import com.example.is.exception.FileImportException;
import com.example.is.file.FileParser;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class FileImportService {
    private final FileParser fileParser;
    private final CoordinatesService coordinatesService;
    private final EventService eventService;
    private final PersonService personService;
    private final LocationService locationService;
    private final VenueService venueService;
    private final TicketService ticketService;

    private final ImportHistoryService importHistoryService;

    public FileImportService(FileParser fileParser,
                             CoordinatesService coordinatesService,
                             EventService eventService,
                             PersonService personService,
                             LocationService locationService,
                             VenueService venueService,
                             TicketService ticketService,
                             ImportHistoryService importHistoryService) {
        this.fileParser = fileParser;
        this.coordinatesService = coordinatesService;
        this.eventService = eventService;
        this.personService = personService;
        this.locationService = locationService;
        this.venueService = venueService;
        this.ticketService = ticketService;

        this.importHistoryService = importHistoryService;
    }

    public <T> void uploadDtos(Consumer<T> uploader, List<T> items, String objectType) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            try {
                uploader.accept(items.get(i));
            } catch (ConstraintViolationException e) {
                int index = i;

                String message = e.getConstraintViolations().stream()
                        .map(v -> objectType + "[" + index + "]." + v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining("; "));

                throw new FileImportException(message);
            }
        }
    }

    private int getImportedObjectsCount(ImportFileDTO objects) {
        return objects.getCoordinates().size()
                + objects.getEvents().size()
                + objects.getPersons().size()
                + objects.getVenues().size()
                + objects.getLocations().size()
                + objects.getTickets().size();
    }

    @Transactional
    public void importFile(MultipartFile file, String username) {
        if (file.isEmpty()) {
            throw new FileImportException("Файл пуст");
        }

        try {
            var objects = fileParser.parseFile(file);

            uploadDtos(coordinatesService::create, objects.getCoordinates(), "coordinates");
            uploadDtos(eventService::create, objects.getEvents(), "events");
            uploadDtos(personService::create, objects.getPersons(), "persons");
            uploadDtos(venueService::create, objects.getVenues(),  "venues");
            uploadDtos(locationService::create, objects.getLocations(),  "locations");
            uploadDtos(ticketService::create, objects.getTickets(),  "tickets");

            int savedCount = getImportedObjectsCount(objects);

            importHistoryService.saveSuccess(username, savedCount);
        } catch (FileImportException e) {
            importHistoryService.saveFailed(username);
            throw e;
        } catch (Exception e) {
            importHistoryService.saveFailed(username);
            throw new FileImportException("Не удалось импортировать данные: " + e.getMessage());
        }
    }
}
