package com.Fern.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // E.g., WiFi, AC, Mini Bar

    @Column(length = 500)
    private String description;
}
