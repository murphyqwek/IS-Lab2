package com.example.is.repository;

import com.example.is.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Integer> {
    boolean existsVenueByName(String name);

    boolean existsVenueByNameAndIdNot(String eventName, Integer id);
}
