package com.example.lab1.specification;

import com.example.lab1.entity.Ticket;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class TicketSpecification {

    private TicketSpecification() {
    }

    public static Specification<Ticket> nameEquals(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("name"),
                        name
                );
    }

    public static Specification<Ticket> eventNameEquals(String eventName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.join("event", JoinType.LEFT)
                                .get("name"),
                        eventName
                );
    }

    public static Specification<Ticket> eventDescriptionEquals(
            String eventDescription
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.join("event", JoinType.LEFT)
                                .get("description"),
                        eventDescription
                );
    }

    public static Specification<Ticket> venueNameEquals(String venueName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.join("venue", JoinType.LEFT)
                                .get("name"),
                        venueName
                );
    }
}