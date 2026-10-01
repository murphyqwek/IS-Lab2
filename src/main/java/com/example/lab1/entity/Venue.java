package com.example.lab1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
@Entity
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @NotNull
    @Column(nullable = false,
            check = @CheckConstraint(
                    name = "check_venue_name_not_blank",
                    constraint = "char_length(trim(name)) > 0"))
    private String name;

    @Positive
    @NotNull
    @Column(nullable = false,
            check = @CheckConstraint(
                    name = "check_capacity_positive",
                    constraint = "capacity > 0"))
    private int capacity;


    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private VenueType type;

    public VenueType getType() {
        return type;
    }

    public void setType(VenueType type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
}