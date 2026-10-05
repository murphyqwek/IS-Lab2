package com.example.is.service;

import com.example.is.dto.file.ImportFileDTO;
import com.example.is.exception.FileImportException;
import com.example.is.file.FileParser;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class FileImportTransactionService {

    private final FileParser fileParser;
    private final CoordinatesService coordinatesService;
    private final EventService eventService;
    private final PersonService personService;
    private final LocationService locationService;
    private final VenueService venueService;
    private final TicketService ticketService;
    private final ImportHistoryService importHistoryService;

    public FileImportTransactionService(
            FileParser fileParser,
            CoordinatesService coordinatesService,
            EventService eventService,
            PersonService personService,
            LocationService locationService,
            VenueService venueService,
            TicketService ticketService,
            ImportHistoryService importHistoryService
    ) {
        this.fileParser = fileParser;
        this.coordinatesService = coordinatesService;
        this.eventService = eventService;
        this.personService = personService;
        this.locationService = locationService;
        this.venueService = venueService;
        this.ticketService = ticketService;
        this.importHistoryService = importHistoryService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void importFile(MultipartFile file, String username) {
        if (file == null || file.isEmpty()) {
            throw new FileImportException("Файл пуст");
        }

        ImportFileDTO objects = parseFile(file);

        uploadDtos(coordinatesService::create, objects.getCoordinates(), "coordinates");
        uploadDtos(eventService::create, objects.getEvents(), "events");
        uploadDtos(personService::create, objects.getPersons(), "persons");
        uploadDtos(venueService::create, objects.getVenues(),"venues");
        uploadDtos(locationService::create, objects.getLocations(),"locations");
        uploadDtos(ticketService::create, objects.getTickets(), "tickets");

        importHistoryService.saveSuccess(username, getImportedObjectsCount(objects));
    }

    private ImportFileDTO parseFile(MultipartFile file) {
        try {
            ImportFileDTO objects = fileParser.parseFile(file);

            if (objects == null) {
                throw new FileImportException("Файл должен содержать JSON-объект импорта");
            }

            return objects;
        } catch (FileImportException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new FileImportException("Не удалось распарсить файл");
        }
    }

    private <T> void uploadDtos(Consumer<T> uploader, List<T> items, String objectType) {
        if (items == null || items.isEmpty()) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            try {
                uploader.accept(items.get(i));
            } catch (ConstraintViolationException exception) {
                int index = i;

                String message = exception.getConstraintViolations()
                        .stream()
                        .map(violation ->
                                objectType + "[" + index + "]."
                                        + violation.getPropertyPath()
                                        + ": "
                                        + violation.getMessage()
                        )
                        .collect(Collectors.joining("; "));

                throw new FileImportException(message);
            }
        }
    }

    private int getImportedObjectsCount(ImportFileDTO objects) {
        return sizeOrZero(objects.getCoordinates())
                + sizeOrZero(objects.getEvents())
                + sizeOrZero(objects.getPersons())
                + sizeOrZero(objects.getVenues())
                + sizeOrZero(objects.getLocations())
                + sizeOrZero(objects.getTickets());
    }

    private int sizeOrZero(List<?> collection) {
        return collection == null ? 0 : collection.size();
    }
}