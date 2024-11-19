package com.Fern.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String typeName; // E.g., Single, Double, Suite

    @Column(length = 500)
    private String description; // Additional details about the room type
}
