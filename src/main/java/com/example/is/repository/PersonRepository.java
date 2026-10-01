package com.example.is.repository;

import com.example.is.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findAllByLocation_Id(Long locationId);
}
