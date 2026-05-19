package com.example.miniapp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String note;
    private Instant createdAt;

    public Visit() {}

    public Visit(String note) {
        this.note = note;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getNote() { return note; }
    public Instant getCreatedAt() { return createdAt; }
}
