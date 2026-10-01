package com.example.lab1.service;

import com.example.lab1.dto.request.PersonReferenceRequest;
import com.example.lab1.dto.request.PersonRequest;
import com.example.lab1.dto.response.PersonResponse;
import com.example.lab1.entity.Person;
import com.example.lab1.entity.Ticket;
import com.example.lab1.exception.InvalidReferenceException;
import com.example.lab1.exception.ResourceNotFoundException;
import com.example.lab1.mapper.PersonMapper;
import com.example.lab1.repository.PersonRepository;
import com.example.lab1.repository.TicketRepository;
import com.example.lab1.websocket.ChangeType;
import com.example.lab1.websocket.EntityChangePublisher;
import com.example.lab1.websocket.EntityType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final TicketRepository ticketRepository;
    private final LocationService locationService;
    private final PersonMapper personMapper;
    private final EntityChangePublisher changePublisher;

    public PersonService(
            PersonRepository personRepository,
            TicketRepository ticketRepository,
            LocationService locationService,
            PersonMapper personMapper,
            EntityChangePublisher changePublisher
    ) {
        this.personRepository = personRepository;
        this.ticketRepository = ticketRepository;
        this.locationService = locationService;
        this.personMapper = personMapper;
        this.changePublisher = changePublisher;
    }

    @Transactional(readOnly = true)
    public List<PersonResponse> getAll() {
        return personRepository.findAll().stream().map(personMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PersonResponse getById(Long id) {
        return personMapper.toResponse(find(id));
    }

    @Transactional
    public PersonResponse create(PersonRequest request) {
        return personMapper.toResponse(createEntity(request));
    }

    @Transactional
    public Person resolve(PersonReferenceRequest request) {
        if (request == null) {
            throw new InvalidReferenceException("Поле 'person' не может быть null");
        }

        ReferenceRequestValidator.requireExactlyOne(request.id(), request.newObject(), "person");

        if (request.id() != null) {
            return find(request.id());
        }

        return createEntity(request.newObject());
    }

    @Transactional
    public PersonResponse update(Long id, PersonRequest request) {
        Person person = find(id);

        applyRequest(person, request);

        changePublisher.publish(EntityType.PERSON, ChangeType.UPDATED, id);

        return personMapper.toResponse(person);
    }

    @Transactional
    public void delete(Long id, Long replacementId) {
        Person person = find(id);
        List<Ticket> tickets = ticketRepository.findAllByPerson_Id(id);

        if (!tickets.isEmpty()) {
            ReferenceRequestValidator.requireReplacement(replacementId, "Person");
            ReferenceRequestValidator.requireDifferent(id, replacementId, "Person");

            Person replacement = find(replacementId);

            for (Ticket ticket : tickets) {
                ticket.setPerson(replacement);
                changePublisher.publish(EntityType.TICKET, ChangeType.UPDATED, ticket.getId());
            }
        }

        personRepository.delete(person);
        changePublisher.publish(EntityType.PERSON, ChangeType.DELETED, id);
    }

    private Person createEntity(PersonRequest request) {
        Person person = new Person();
        applyRequest(person, request);

        Person saved = personRepository.save(person);
        changePublisher.publish(EntityType.PERSON, ChangeType.CREATED, saved.getId());

        return saved;
    }

    private void applyRequest(Person person, PersonRequest request) {
        person.setEyeColor(request.eyeColor());
        person.setHairColor(request.hairColor());
        person.setLocation(request.location() == null ? null : locationService.resolve(request.location()));
        person.setWeight(request.weight());
        person.setNationality(request.nationality());
    }

    private Person find(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person с id=" + id + " не найден"));
    }
}
