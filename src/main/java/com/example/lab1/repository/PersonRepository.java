package com.example.lab1.repository;

import com.example.lab1.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findAllByLocation_Id(Long locationId);
}
