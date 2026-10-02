package com.example.is.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Integer importedObjectsCount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = Instant.now();
    }
}