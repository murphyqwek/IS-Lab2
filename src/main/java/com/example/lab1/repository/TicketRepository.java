package com.example.lab1.repository;

import com.example.lab1.entity.Ticket;
import com.example.lab1.entity.Venue;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository
        extends JpaRepository<Ticket, Integer>, JpaSpecificationExecutor<Ticket> {

    @Query(
            value = "SELECT * FROM get_ticket_with_max_type()",
            nativeQuery = true
    )
    Optional<Ticket> findTicketWithMaxType();


    @Query(
            value = """
                    SELECT count_tickets_with_venue_less_than(:venueId)
                    """,
            nativeQuery = true
    )
    long countWithVenueLessThan(
            @Param("venueId") int venueId
    );


    @Query(
            value = "SELECT * FROM get_unique_ticket_venues()",
            nativeQuery = true
    )
    List<Venue> findUniqueVenues();


    @Query(
            value = """
                    SELECT *
                    FROM copy_ticket_as_vip(:ticketId)
                    """,
            nativeQuery = true
    )
    Optional<Ticket> copyAsVip(
            @Param("ticketId") int ticketId
    );


    @Query(
            value = """
                    SELECT *
                    FROM copy_ticket_with_discount(
                        :ticketId,
                        :discount
                    )
                    """,
            nativeQuery = true
    )
    Optional<Ticket> copyWithDiscount(
            @Param("ticketId") int ticketId,
            @Param("discount") int discount
    );

    List<Ticket> findAllByEvent_Id(Integer eventId);

    List<Ticket> findAllByVenue_Id(Integer venueId);

    List<Ticket> findAllByPerson_Id(Long personId);

    List<Ticket> findAllByCoordinates_Id(Long coordinatesId);
}
